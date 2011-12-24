package com.learning.android.currencyconvertor;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


/**
 * @author Li Ji
 *
 */
public class CurrencyConvertor extends ListActivity {
	
    
	private CurrencyDBAdapter currDBAdapter;
	
	//Adapter for Currencies List View
	private CurrencyListAdapter currencyListAdapter;
	
	//Cursor for all the selected currencies
	private Cursor currencies;
	
	//New Thread to update currencies converter rates
	private HandlerThread handlerThread = new HandlerThread("handler_thread");
	
	//Messenger to receive incoming messages
	private Messenger uIMessenger;
	
	//Message Handler for download currency exchange rates
	private CurrencySyncHandler currSyncHandler;
	
	//Panel to contain Wheel Views
	private CurrencySlidingUpPanel panel=null;
	
	//Remove View Item
	private Menu removeMenu;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Setting up Customized Title Bar
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list_currency_selected);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_currency_list);
        
         //Set Up List Adapter and Load Data into List Activity
        setUpListAdapter();
        
        //Create a new Messenger to Receive message
        uIMessenger = new Messenger(new messageHandler());
        
        //Set Up Currency Wheel for Adding New currency Pair
        setUpCurrencyWheel();
        
       //Start New Thread Handler to Load Currencies
        //handlerThread.start();
        startCurrencyUpdateThread();
        currSyncHandler = new CurrencySyncHandler(handlerThread.getLooper(), this.getBaseContext(),currDBAdapter);
        currSyncHandler.setReadyToUpdate();
        startCurrencySync(CurrencyDBAdapter.SELECTION_MODE[0]);
        showToastMsg();
    }
    
	public void showToastMsg()
	{
		Toast toast = Toast.makeText(this, "Delete Currency Pair: Long Press Center of Currency Pair and Drag to Bottom of Screen! ", Toast.LENGTH_LONG);
		toast.show();
	}

	

//	@Override
//	protected void onNewIntent(Intent intent) {
//		// TODO Auto-generated method stub
//		super.onNewIntent(intent);
//		this.showToastMsg();
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_remove, menu);
	    removeMenu = menu;
	    System.out.println("Menu is created! ");
		return true;
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//handlerThread.stop();
		stopCurrencyUpdateThread();
		currSyncHandler.unSetReadyToUpdate();
		currencyListAdapter.updateDBAsArrayList();
	}
	
	private void stopCurrencyUpdateThread()
	{
		if(handlerThread != null && handlerThread.isAlive() == true )
		{
			handlerThread.interrupt();
		}
	}
	
	private void startCurrencyUpdateThread()
	{
		if(handlerThread != null && handlerThread.isAlive() !=true)
		{
			handlerThread.start();
		}
	}



	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		System.out.println("On Restart");
		setUpListAdapter();
		//handlerThread.start();
		startCurrencyUpdateThread();
		currSyncHandler.setReadyToUpdate();
		this.showToastMsg();
	}




	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("On Resume!");
		//handlerThread.start();
		startCurrencyUpdateThread();
		currSyncHandler.setReadyToUpdate();
	}




	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		currencyListAdapter.updateDBAsArrayList();
		//handlerThread.stop();
		currSyncHandler.unSetReadyToUpdate();
		stopCurrencyUpdateThread();
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		currencyListAdapter.updateDBAsArrayList();
		//handlerThread.stop();
		currSyncHandler.unSetReadyToUpdate();
		stopCurrencyUpdateThread();
	}

	/**
	 * Set Up List Adapter and initial loading data into List View
	 */
	public void setUpListAdapter()
	{
        currDBAdapter = new CurrencyDBAdapter(this);
        currDBAdapter.open();
        //Load selected currencies into list
        currencies = currDBAdapter.getSelectedCurrencies();
        startManagingCursor(currencies);
        
        //Assign value for Currency ListAdapter
        currencyListAdapter = new CurrencyListAdapter(currencies,this,currDBAdapter);
        
        //Set List Adapter 
        setListAdapter(currencyListAdapter);
	}



	private void startCurrencySync(int mode)  {
		// TODO Auto-generated method stub
        //Only Update Selected Only Currencies
         Message currencyUpdatedMessage = currSyncHandler.obtainMessage();
         Bundle msgData = new Bundle();
         msgData.putInt("Selection", mode);
         currencyUpdatedMessage.setData(msgData);
         currencyUpdatedMessage.replyTo = uIMessenger;
         
         //Update Database before sending Message
         currencyUpdatedMessage.sendToTarget();
	}



    
    private void currenceListViewUpdate()
    {
    	System.out.println("--------Before ListView Update -----------------");
        currencyListAdapter.updateDBAsArrayList();
    	currencies = currDBAdapter.getSelectedCurrencies();
    	currencyListAdapter.copyCursor2ArrayList(currencies);
    	currencyListAdapter.notifyDataSetChanged();
    	System.out.println("--------After ListView Update -----------------");
    }
	
	class messageHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what)
			{
			//Only Selected Currencies to Update
			case CurrencySyncHandler.MSG_CURR_SELECT_UPD:
				System.out.println("Selected Currencies has been updated");
				currenceListViewUpdate();
				startCurrencySync(CurrencyDBAdapter.SELECTION_MODE[1]);
				break;
			case CurrencySyncHandler.MSG_CURR_ALL_UPD:
				System.out.println("All currencies has been updated");
				currenceListViewUpdate();
				startCurrencySync(CurrencyDBAdapter.SELECTION_MODE[0]);
				break;
			case CurrencySyncHandler.MSG_CURR_INT_NOT_CONN:
				System.out.println("Internet host is not connected!");
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
	
	public void messageSend () 
	{
	    try{	
		uIMessenger.send(Message.obtain(null,CurrencySyncHandler.MSG_CURR_SELECT_UPD , 0, 0));
	    }
	    catch(Exception e)
	    {
	    	System.out.println("Exception for Sending Message for " + e.toString());
	    }
	}
	
	/**
	 * Listener for Add button on Title bar
	 * @param v
	 */

	public void addCurrencyPair(View v)
	{
		//System.out.println("Button Clicked on Add Currency! ");
		setUpCurrencyWheel();
		panel.toggle();
	}
	
	/**
	 * Set Up Wheel View for Add currency Pair
	 * 
	 */
	public void setUpCurrencyWheel()
	{
		 //Wheel to display currnecy pairs
		System.out.println("setUpCurrencyWheelAdapter Begined");
        panel = (CurrencySlidingUpPanel)findViewById(R.id.wheelPanel);
        panel.setUpWheelView(currDBAdapter, this);
        panel.setOpen(false);
        System.out.println("Wheel Adapter is set up!");
	}
	
	
	/**
	 * Listener for Add currency pair button in Slide panel
	 * @param v
	 */
	public void selectCurrencyPair(View v)
	{
		//panel.addButtonClick();
//		int leftIndex = getFromCurrencyID(panel.getLeftWheelIndex());
//		System.out.println("Left Wheel Index "+leftIndex);
		currencyListAdapter.addNewCurrencyPair(panel.getLeftWheelIndex(), panel.getRightWheelIndex());
		panel.toggle();
	}
	
	
	public void cancelCurrencyPair(View v)
	{
		panel.toggle();
	}
	
	
	private int getFromCurrencyID(int leftWheelIndex) {
		
		currencies.moveToPosition(leftWheelIndex);
		
		return currencies.getInt(currencies.getColumnIndex(CurrencyDBAdapter.KEY_ID));
	}



	/**
	 * Listener for Remove currency pair button in Slide panel
	 * @param v
	 */
	public void addCurrencyPairFavorite(View v)
	{
		System.out.println("Remove Currency Button is clicked!");
		
		Intent intent = new Intent(this.getBaseContext(),CurrencyAdd2Favorite.class);
		startActivity(intent);
	}
	
	public Menu getRemoveMenu()
	{
		return removeMenu;
		
	}
}