package com.learning.android.currencyconvertor;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;



public class CurrencySelectItem {
	
	public Spinner fromCurrSpinner;
	public Spinner toCurrSpinner;
	
	//public ArrayAdapter<CharSequence> currListAdapter;
	//
	
	
	public EditText fromCurrRate;
	public EditText toCurrRate;
	
	public TextView versus;
	
	public Button currencySwapBtn;
	
	//TextChanged Watcher for from/to currency exchange rates
	public TextWatcher fromCurrTextWatcher;
	public TextWatcher toCurrTextWatcher;
	
	//Semaphor for from & to currency Edit Text Changes
	private boolean flagTextChangeFromUser = false;
	
	//Decimal Point Accuracy
	private DecimalFormat df = new DecimalFormat("#.####");
	
	private ArrayList<CurrencyListItem> currList;
	
//	private CurrencyConvertor parentListActivity;

	/**
	 * Set Up Adapter for Spinner
	 * @param parentListActivity
	 * @param currencyDBAdapter
	 * @param position 
	 */
	
	public int getPosition(int currencyID)
	{
		
		for(int i=0;i<currList.size();i++)
		{
			//currCursor.moveToPosition(i);
			if(currencyID == currList.get(i).getKeyId())
			{
				return i;
			}
		}
		return 0;
	}
	
	public void setUpAdapterForSpinner(CurrencyConvertor  parentListActivity, CurrencyDBAdapter currencyDBAdapter)
	{
		
		Cursor cursor = currencyDBAdapter.fetchCurrencyConfig(CurrencyDBAdapter.UNFAVOR);
		CurrencySpinnerArrayAdaper currSpinnerAdapter = new CurrencySpinnerArrayAdaper(cursor,parentListActivity);
		
		toCurrSpinner.setAdapter(currSpinnerAdapter);
	    fromCurrSpinner.setAdapter(currSpinnerAdapter);
	 }
	
	
	/**
	 * Set Up OnClickListener for currencySwapBtn
	 * @param parentListView
	 * @param currArrayList
	 * @param currListAdapter
	 */
	public void setUpSwapBtnOnClickListner(ListView parentListView, ArrayList<CurrencyXchgRatePair> currArrayList, CurrencyListAdapter currListAdapter)
	{
		currencySwapBtn.setOnClickListener(new SwapBtnOnClickListner(parentListView,currArrayList,currListAdapter));
	}
	
	
	/**
	 * Customised OnClickListener for currencySwapBtn
	 */
	private class SwapBtnOnClickListner implements OnClickListener
	{
		private int pos;
		private ListView parListView;
		private ArrayList<CurrencyXchgRatePair> currencyArrayList;
		private CurrencyListAdapter currencyListAdapter;
		SwapBtnOnClickListner(ListView parentListView, ArrayList<CurrencyXchgRatePair> currArrayList, CurrencyListAdapter currListAdapter)
		{
			parListView = parentListView;
			currencyArrayList = currArrayList;
			currencyListAdapter = currListAdapter;
		}
		@Override
		public void onClick(View v) {
			pos = parListView.getPositionForView(v);
			currencyArrayList.get(pos).setDirection(!currencyArrayList.get(pos).isDirection());
			System.out.println("Click Position at "+ pos+ " direction " + currencyArrayList.get(pos).isDirection());
			Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.convertor_rotate_action);
			try{
			anim.setAnimationListener(new AnimationListener()
				{

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						currencyListAdapter.notifyDataSetChanged();
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}
				}
			);
			} catch(Exception e)
			{
				System.out.println("OnClick Exception "+e.toString());
			}
			v.startAnimation(anim);
		}
		
	}
	
	/**
	 * Set Up OnItemSelectedListener for fromCurrSpinner & toCurrSpinner
	 * @param parentListView
	 * @param currArrayList
	 * @param currListAdapter
	 */
	public void setUpCurrSpinnerOnItemSelectedListener(ListView parentListView, ArrayList<CurrencyXchgRatePair> currArrayList,CurrencyDBAdapter currDBAdapter, CurrencyListAdapter currListAdapter)
	{
		//currencySwapBtn.setOnClickListener(new SwapBtnOnClickListner(parentListView,currArrayList,currListAdapter));
		CurrSpinnerOnItemSelectedListener currSpinnerOnItemSelectedListener = new CurrSpinnerOnItemSelectedListener(parentListView,currArrayList,currDBAdapter,currListAdapter);
		fromCurrSpinner.setOnItemSelectedListener(currSpinnerOnItemSelectedListener);
		toCurrSpinner.setOnItemSelectedListener(currSpinnerOnItemSelectedListener);
	}
	
	
	/**
	 * Customised OnItemSelectedListener for fromCurrSpinner & toCurrSpinner
	 */
	private class CurrSpinnerOnItemSelectedListener implements OnItemSelectedListener
	{
		private int pos;
		private ListView parListView;
		private ArrayList<CurrencyXchgRatePair> currencyArrayList;
		private CurrencyDBAdapter currencyDBAdapter;
		private CurrencyListAdapter currencyListDapter;
		
		CurrSpinnerOnItemSelectedListener(ListView parentListView, ArrayList<CurrencyXchgRatePair> currArrayList, CurrencyDBAdapter currDBAdapter,CurrencyListAdapter currListAdapter)
		{
			parListView = parentListView;
			currencyArrayList = currArrayList;
			currencyDBAdapter = currDBAdapter;
			currencyListDapter = currListAdapter;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			try
			{ 
				pos = parListView.getPositionForView(parent);
			} catch(Exception e)
			{   
				pos = -1;
				System.out.println("Item Selected Exception! "+e.toString());
			}
			
			if(view!=null && pos >=0 )
			{
				int previous;
				int current = currList.get(position).getKeyId();
				CurrencyXchgRatePair currencyXchgRatePair = currencyArrayList.get(pos);
				//System.out.println("Item Selected Listener: "+parent + " - "+ view+" - "+ pos+ " - "+id );
				switch(parent.getId())
			    {
				case R.id.fromCurrency:
					previous = currencyXchgRatePair.isDirection()?currencyXchgRatePair.getToCurrID():currencyXchgRatePair.getFromCurrID();
					
					if(previous != current)
					{
						currencyXchgRatePair.updateFromSpinner(current);
						//currencyXchgRatePair.setFromCurrID(current);
						
						if(currencyDBAdapter.getNewCurrencyPair(currencyXchgRatePair, CurrencyDBAdapter.DATA_UPDATE)!=null)
						{
							System.out.println("Currency Pair -- From Currency "+currencyArrayList.get(pos).getFromCurrID()+" Selection "+currencyXchgRatePair.getSelection()+" Exchange Rate "+ currencyXchgRatePair.getRateADV());
						}
						currencyListDapter.notifyDataSetChanged();
					}
				    
				break;
				case R.id.toCurrency:
					previous = (!currencyXchgRatePair.isDirection())?currencyXchgRatePair.getToCurrID():currencyXchgRatePair.getFromCurrID();
					if( previous != current)
					{
						currencyXchgRatePair.updateToSpinner(current);
						//currencyXchgRatePair.setToCurrID(current);
						
						if(currencyDBAdapter.getNewCurrencyPair(currencyXchgRatePair, CurrencyDBAdapter.DATA_UPDATE)!=null)
						{
							System.out.println("Currency Pair -- To Currency "+currencyArrayList.get(pos).getToCurrID()+" Selection "+currencyXchgRatePair.getSelection());
						}
						
						currencyListDapter.notifyDataSetChanged();
					}
					
				default:
			    break;
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * Set up Edit Text Watcher
	 */
	
	public void setUpcurrTextWatcher(ListView parentListView,ArrayList<CurrencyXchgRatePair> currArrayList)
	{
		 fromCurrTextWatcher = new FromCurrTextWatcher(parentListView,currArrayList);
		 toCurrTextWatcher = new ToCurrTextWatcher(parentListView,currArrayList);
		 fromCurrRate.addTextChangedListener(fromCurrTextWatcher);
		 toCurrRate.addTextChangedListener(toCurrTextWatcher);
	}
	
	public void removeCurrTextWatcher()
	{
		fromCurrRate.removeTextChangedListener(fromCurrTextWatcher);
		toCurrRate.removeTextChangedListener(toCurrTextWatcher);
	}
	

/**
 * Customised OnTextWatcher for fromCurrRate Edit Text
 */
private class FromCurrTextWatcher implements TextWatcher{
		
		private int pos;
		private ListView parListView;
		private ArrayList<CurrencyXchgRatePair> currencyArrayList;
		
		public FromCurrTextWatcher(ListView parentListView,ArrayList<CurrencyXchgRatePair> currArrayList)
		{
			parListView = parentListView;
			currencyArrayList = currArrayList;
		}

		@Override
		public void afterTextChanged(Editable s) {
			}


		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			//System.out.println("Triggere Here!");
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			if(!flagTextChangeFromUser)
			{
				//System.out.println("From Currency Edit Text Changed ");
				flagTextChangeFromUser = true;
			 try{
				 
				 //System.out.println("From Curr Position at " + parListView.getPositionForView(toCurrRate) + " " + flagTextChangeFromUser );
				pos = parListView.getPositionForView(fromCurrRate);
				
				if(fromCurrRate.getText().toString()!=null)
					currencyArrayList.get(pos).setFromCurrAMT(Float.valueOf((fromCurrRate.getText().toString())));
				
				if(currencyArrayList.get(pos).getToCurrAMT()!=1)
					currencyArrayList.get(pos).setToCurrAMT(1);
				
				toCurrRate.setText(df.format(
						Float.valueOf(fromCurrRate.getText().toString())*
						 (currencyArrayList.get(pos).isDirection()? currencyArrayList.get(pos).getRateREV():currencyArrayList.get(pos).getRateADV())));
				
				//notifyDataSetChanged();		
			    } catch(Exception e)
				{
					System.out.println(" EditText Exception happened "+e.toString());
				}
			    flagTextChangeFromUser = false;
			}
		}
	}
	

/**
 * Customised OnTextWatcher for toCurrRate Edit Text
 */
private class ToCurrTextWatcher implements TextWatcher{
	
	private int pos;
	private ListView parListView;
	private ArrayList<CurrencyXchgRatePair> currencyArrayList;
	
	
	public ToCurrTextWatcher(ListView parentListView,ArrayList<CurrencyXchgRatePair> currArrayList)
	{
		parListView = parentListView;
		currencyArrayList = currArrayList;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count) {
		
		
		// TODO Auto-generated method stub
    if(!flagTextChangeFromUser)
    {
    	//System.out.println("To Currency Rate EditText Changed! ");
    	flagTextChangeFromUser = true;
		 try{ 
			 //System.out.println("To Curr Position at " + parListView.getPositionForView(toCurrRate) + " " + flagTextChangeFromUser );
			 pos = parListView.getPositionForView(toCurrRate);
			 
			 if(toCurrRate.getText().toString()!=null)
				 currencyArrayList.get(pos).setToCurrAMT(Float.valueOf(toCurrRate.getText().toString()));
			 
			 
			 if(currencyArrayList.get(pos).getFromCurrAMT()!=1)
				 currencyArrayList.get(pos).setFromCurrAMT(1);
			
			
			 fromCurrRate.setText(df.format(
						Float.valueOf(toCurrRate.getText().toString())*
						 (currencyArrayList.get(pos).isDirection()? 
								 currencyArrayList.get(pos).getRateADV():
									 currencyArrayList.get(pos).getRateREV())));
			   
			} catch(Exception e)
			{
				System.out.println(" EditText Exception happened "+e.toString());
			}
			flagTextChangeFromUser = false;
        }
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		//System.out.println("Before Change To Currency Exchange ");
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}
}

	/**
	 * Customised Spinner Adapter for fromCurrSpinner & toCurrSpinner
	 */
	public  class CurrencySpinnerArrayAdaper extends BaseAdapter 
	{
		private Cursor currCursor;
		private LayoutInflater currSpinnerInflator;
		
		
		public CurrencySpinnerArrayAdaper(Cursor currenciesCursor,CurrencyConvertor parentListActivity)
		{
			currCursor = currenciesCursor;
			currList = new ArrayList<CurrencyListItem>();
			loadArrayList();
			currSpinnerInflator = parentListActivity.getLayoutInflater();
		}
		
		private void loadArrayList() {
			
			currList.clear();
			for (int i=0;i<currCursor.getCount();i++)
			{
				currCursor.moveToPosition(i);
				
				CurrencyListItem currencyListItem = new CurrencyListItem();
				currencyListItem.setKeyId(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_ID)));
				currencyListItem.setCurrency(currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURRENCY)));
				currencyListItem.setCurrencyDesc(currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURR_DESC)));
				currencyListItem.setFavoriate(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FAVOURITE))==1?true:false);
				currencyListItem.setFlagImage(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FLAG)));
				
				currList.add(currencyListItem);
			}
			
			//System.out.println("LoadArrayList "+currList.size());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			//return currCursor.getCount();
			return currList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			//return currCursor.moveToPosition(position);
			return currList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null)
			{
				convertView = currSpinnerInflator.inflate(R.layout.spinner_item_currency_selected, null);
			}
			
			//currCursor.moveToPosition(position);
			//((TextView)convertView.findViewById(R.id.currencyName)).setText(currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURRENCY)));
			//((ImageView)convertView.findViewById(R.id.countryFlag)).setImageResource(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FLAG)));
			
			((TextView)convertView.findViewById(R.id.currencyName)).setText(currList.get(position).getCurrency());
			((ImageView)convertView.findViewById(R.id.countryFlag)).setImageResource(currList.get(position).getFlagImage());
			return convertView;
		}
	}

	public void updateCurrSpinnerAdapter(Spinner spinner, int currencyID, CurrencyDBAdapter currDBAdapter, CurrencyConvertor parentActivity) {
		// TODO Auto-generated method stub
		spinner.setAdapter(null);
		Cursor cursor = currDBAdapter.fetchSpinnerCurrency(0, currencyID);
		CurrencySpinnerArrayAdaper currencySpinnerArrayAdapter = new CurrencySpinnerArrayAdaper(cursor, parentActivity);
		spinner.setAdapter(currencySpinnerArrayAdapter);
	}
}
