package com.learning.android.currencyconvertor;



import java.text.DecimalFormat;
import java.util.ArrayList;

import com.learning.android.currencyconvertor.CurrencySelectItem.CurrencySpinnerArrayAdaper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;


public class CurrencyListAdapter extends BaseAdapter {
	
	
	private CurrencyConvertor parentListActivity;
	private ListView parentListView;
	private ArrayList<CurrencyXchgRatePair> currArrayList;
	private LayoutInflater currInflator;
	private Cursor currList;
	private CurrencySelectItem  currencySelectItem;
	private CurrencyDBAdapter currDBAdapter;
	private CurrencyListItemDragDropController currListItemDDController;
	private DecimalFormat df = new DecimalFormat("#.####");
	
	
	public CurrencyListAdapter (Cursor currencyList,CurrencyConvertor currListActivity, CurrencyDBAdapter currencyDBAdapter)
	{
		//Debug
		System.out.println("Initializing List Adapter...");
		
		currList = currencyList;
		currInflator = (LayoutInflater)currListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentListActivity =  currListActivity;
		parentListView = parentListActivity.getListView();
		currDBAdapter = currencyDBAdapter;
		currArrayList = new ArrayList<CurrencyXchgRatePair>();
		//Initialise ArrayList
		copyCursor2ArrayList(currList);
		//parentListView.setOnTouchListener(currListOnTouchListener);
		currListItemDDController = new CurrencyListItemDragDropController(parentListActivity,parentListView,this);
		currListItemDDController.setUpOnTouchListner(parentListView);
		//System.out.println("List Adapter Constructor End !");
	}
	
	public int getPosition(int currId)
	{
		Cursor currFavCursor = currDBAdapter.fetchCurrencyConfig(CurrencyDBAdapter.UNFAVOR);
		for(int i=0;i < currFavCursor.getCount();i++)
		{
			currFavCursor.moveToPosition(i);
			
			if(currId == currFavCursor.getInt(currFavCursor.getColumnIndex(CurrencyDBAdapter.KEY_ID)))
				return i;
		}
		return 0;
	}
	/**
	 * Copy All the Values from Cursor to Array List
	 * @param currList Cursor have data from database
	 * @param currArrayList
	 */
	public void copyCursor2ArrayList(Cursor currList) {
		
		// TODO Auto-generated method stub
		//currArrayList = new ArrayList<CurrencyXchgRatePair>();
		currArrayList.clear();
		addCursor2ArrayList(currList);
	}
	
	public void addCursor2ArrayList(Cursor currList)
	{
		for(int i =0 ; i < currList.getCount();i++)
		{
			CurrencyXchgRatePair currencyXchgRatePair = new CurrencyXchgRatePair();
			//Initialize Currencies Xchg Rate Array List
			currList.moveToPosition(i);
			currencyXchgRatePair.setCursorRowID(i);
			currencyXchgRatePair.setDbRowID(currList.getInt(currList.getColumnIndex(CurrencyDBAdapter.KEY_ID)));
			currencyXchgRatePair.setFromCurrAMT(currList.getFloat(currList.getColumnIndex(CurrencyDBAdapter.KEY_FROM_CURR_AMT)));
			currencyXchgRatePair.setToCurrAMT(currList.getFloat(currList.getColumnIndex(CurrencyDBAdapter.KEY_TO_CURR_AMT)));
			currencyXchgRatePair.setFromCurrID(currList.getInt(currList.getColumnIndex(CurrencyDBAdapter.KEY_FROM_CURR_ID)));
			currencyXchgRatePair.setToCurrID(currList.getInt(currList.getColumnIndex(CurrencyDBAdapter.KEY_TO_CURR_ID)));
			currencyXchgRatePair.setRateADV(currList.getFloat(currList.getColumnIndex(CurrencyDBAdapter.KEY_RATE_ADV)));
			currencyXchgRatePair.setRateREV(currList.getFloat(currList.getColumnIndex(CurrencyDBAdapter.KEY_RATE_REV)));
			currencyXchgRatePair.setSelection(currList.getInt(currList.getColumnIndex(CurrencyDBAdapter.KEY_SELECTION)));
			currencyXchgRatePair.setDirection(currList.getInt(currList.getColumnIndex(CurrencyDBAdapter.KEY_DIRECTION))>0);
			
			currArrayList.add(currencyXchgRatePair);
		}
	}
	
	
	public void cleanUpArrayList()
	{
		currArrayList.clear();
	}
	
	/**
	 * Update Data in Database According to ArrayList
	 * 
	 */
	public void updateDBAsArrayList()
	{
		for(int i =0;i<currArrayList.size();i++)
		{
//			currDBAdapter.updateXchgRate(currArrayList.get(i).getDbRowID(),
//					                     0, //Currency Exchange Rate will not be updated in database
//					                     0, //Currency Exchange Rate will not be updated in database
//					                     i+1,
//					                     currArrayList.get(i).isDirection()==false?0:1,
//					                     currArrayList.get(i).getFromCurrAMT(),
//					                     currArrayList.get(i).getToCurrAMT());
			currDBAdapter.updateCurrPairSelected(currArrayList.get(i).getSelection(), currArrayList.get(i).getDbRowID(), currArrayList.get(i).isDirection()==false?0:1, currArrayList.get(i).getFromCurrAMT(), currArrayList.get(i).getToCurrAMT());
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return currList.getCount();
		return currArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		//return currList.moveToPosition(position);
		return currArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		//New Created Convert View
		if(convertView == null)
		{
			convertView = currInflator.inflate(R.layout.item_currency_selected, null);
			currencySelectItem = new CurrencySelectItem();
			
			//Get View in CurrencySelectItem.xml by ID
			currencySelectItem.fromCurrSpinner = (Spinner)convertView.findViewById(R.id.fromCurrency);
			currencySelectItem.toCurrSpinner = (Spinner)convertView.findViewById(R.id.toCurrency);
			currencySelectItem.fromCurrRate = (EditText)convertView.findViewById(R.id.fromCurrRate);
			currencySelectItem.toCurrRate = (EditText)convertView.findViewById(R.id.toCurrRate);
			currencySelectItem.currencySwapBtn = (Button)convertView.findViewById(R.id.swap);
			//currencySelectItem.versus = (TextView)convertView.findViewById(R.id.versus);
			
						
			//Set up Adapter for Spinner
			currencySelectItem.setUpAdapterForSpinner(parentListActivity,currDBAdapter);
			//System.out.println("Current Position is "+position);
			
			//Set Up Event Handler for Swap Button , Spinner
			currencySelectItem.setUpSwapBtnOnClickListner(parentListView, currArrayList, this);
			currencySelectItem.setUpCurrSpinnerOnItemSelectedListener(parentListView, currArrayList,  currDBAdapter,this);
			currListItemDDController.setUpOnClickListener(convertView);
			
			//Populate the currency xchgrates for each item
			fillView(currencySelectItem,position); 
			
			//Attached a tag to it for easy reference later
			convertView.setTag(currencySelectItem);
		}
		else 
		{
			//Recycled Convert View
			currencySelectItem = (CurrencySelectItem)convertView.getTag();
			
			//Remove the Text Change Listener
			currencySelectItem.removeCurrTextWatcher();
			
			fillView(currencySelectItem,position);
		}
			
		return convertView;
	}

	//Binding Cursor to Spinner and EditText in CurrencySelectItem.XML
	private void fillView(CurrencySelectItem currencySelectItem,int position) {
		
		int fromCurrId = currencySelectItem.getPosition(currArrayList.get(position).getFromCurrID());
		int toCurrId = currencySelectItem.getPosition(currArrayList.get(position).getToCurrID());
		
		
    try{
	    //System.out.println("System Select Item Value "+ (currencySelectItem==null)+" Position "+position);
	    if(currArrayList.get(position).isDirection()== false)
	    {
	    //currencySelectItem.toCurrSpinner.setSelection(currArrayList.get(position).getToCurrID()-1);
	    currencySelectItem.toCurrSpinner.setSelection(toCurrId);
	    //System.out.println("Currency Array Adapter "+((CurrencySpinnerArrayAdaper)currencySelectItem.toCurrSpinner.getAdapter()).getPosition(currArrayList.get(position).getToCurrID()));
	    //currencySelectItem.fromCurrSpinner.setSelection(currArrayList.get(position).getFromCurrID()-1);
	    currencySelectItem.fromCurrSpinner.setSelection(fromCurrId);
	    
	    
		    if(currArrayList.get(position).getToCurrAMT()==1)
		    {
			    currencySelectItem.fromCurrRate.setText(df.format(currArrayList.get(position).getFromCurrAMT()));
			    currencySelectItem.toCurrRate.setText(df.format(currArrayList.get(position).getRateADV()*currArrayList.get(position).getFromCurrAMT()));
	        }
		    else
		    {
		    	currencySelectItem.fromCurrRate.setText(df.format(currArrayList.get(position).getRateREV()*currArrayList.get(position).getToCurrAMT()));
		    	currencySelectItem.toCurrRate.setText(df.format(currArrayList.get(position).getToCurrAMT()));
		    }
	    }
	    else
	    {
	    
    	//currencySelectItem.fromCurrSpinner.setSelection(currArrayList.get(position).getToCurrID()-1);
	    currencySelectItem.fromCurrSpinner.setSelection(toCurrId);
    	//currencySelectItem.updateCurrSpinnerAdapter(currencySelectItem.toCurrSpinner,currArrayList.get(position).getFromCurrID(),currDBAdapter,parentListActivity);
  	    //currencySelectItem.toCurrSpinner.setSelection(currArrayList.get(position).getFromCurrID()-1);
	    currencySelectItem.toCurrSpinner.setSelection(fromCurrId);
  	    //currencySelectItem.updateCurrSpinnerAdapter(currencySelectItem.fromCurrSpinner,currArrayList.get(position).getToCurrID(),currDBAdapter,parentListActivity);
  	    
	  	    if(currArrayList.get(position).getToCurrAMT()==1)
	  	    {
		  	    currencySelectItem.fromCurrRate.setText(df.format(currArrayList.get(position).getFromCurrAMT()));
		  	    currencySelectItem.toCurrRate.setText(df.format(currArrayList.get(position).getRateREV()*currArrayList.get(position).getFromCurrAMT()));
		  	}
	  	    else
	  	    {
	  	    	currencySelectItem.fromCurrRate.setText(df.format(currArrayList.get(position).getRateADV()*currArrayList.get(position).getToCurrAMT()));
		  	    currencySelectItem.toCurrRate.setText(df.format(currArrayList.get(position).getToCurrAMT()));
	  	    }

	    }
	    //currencySelectItem.currListAdapter.notifyDataSetChanged();

	    //Set TextChange Listener for from currency/to exchange rate
	    currencySelectItem.setUpcurrTextWatcher(parentListView, currArrayList);
	    
		} catch(Exception e)
		{
			System.out.println(" Fill View Exception "+ e.toString());
		}
	}
	
	
	public void echoArrayList()
	{
		for(int i = 0;i<currArrayList.size();i++)
		{
			System.out.println(i+" "+currArrayList.get(i).getCursorRowID()+" "+currArrayList.get(i).getDbRowID()+" "+currArrayList.get(i).getFromCurrID()+" "+currArrayList.get(i).getToCurrID()+" "+currArrayList.get(i).getSelection());
		}
	}
	
	//Add New Currency Pair into List
	public void addNewCurrencyPair(int fromCurrencyID, int toCurrencyID)
	{
		CurrencyXchgRatePair currXchgRatePair = new  CurrencyXchgRatePair();
		currXchgRatePair.setFromCurrAMT(1);
		currXchgRatePair.setToCurrAMT(1);
		currXchgRatePair.setFromCurrID(fromCurrencyID);
		currXchgRatePair.setToCurrID(toCurrencyID);
		//currXchgRatePair.setSelection(currArrayList.size()+1);
		currArrayList.add((currDBAdapter.setNewCurrencyPair(currXchgRatePair, CurrencyDBAdapter.DATA_INSERT)));
		this.notifyDataSetChanged();
	}

	public void removeItem(int draggedItem) {
		// TODO Auto-generated method stub
		//currDBAdapter.updateXchgRate(currArrayList.get(draggedItem).getDbRowID(), 0, 0, 0, 0, 1, 1);
		currDBAdapter.deleteCurrPairSelected(currArrayList.get(draggedItem).getSelection());
		currArrayList.remove(draggedItem);
		this.notifyDataSetChanged();
	}
	
	//Change the direction of for Position
	
}
