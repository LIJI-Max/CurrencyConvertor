package com.learning.android.currencyconvertor;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CurrencyAdd2Favorite extends Activity {
	
	private ListView favorList;
	private Activity activity;
	private ArrayList<CurrencyListItem> currFavorList;
	private CurrencyListViewAdapter currListFavViewAdapter;
	
	private CurrencyDBAdapter currDBAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 //Setting up Customized Title Bar
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.choose_favorite_currencies);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_add2fav_curr_list);
		
		setUpAdapter();
	}
	
	private void setUpAdapter()
	{
		currDBAdapter = new CurrencyDBAdapter(this);
        currDBAdapter.open();
        
        Cursor curCursor = currDBAdapter.fetchCurrencyConfig(CurrencyDBAdapter.UNFAVOR);
        currFavorList = new ArrayList<CurrencyListItem>();
        loadCurrFavList(curCursor);
		//fullList= (ListView) this.findViewById(R.id.fulllist);
		favorList = (ListView) this.findViewById(R.id.favoritelist);
		activity = this;
		
		//CurrencyListViewAdapter currListFullViewAdapter = new CurrencyListViewAdapter(curCursor);
		//fullList.setAdapter(currListFullViewAdapter);
		
		currListFavViewAdapter = new CurrencyListViewAdapter(currFavorList);
		favorList.setAdapter(currListFavViewAdapter);
	}

	private void loadCurrFavList(Cursor currCursor) {
		
		for(int i=0;i<currCursor.getCount();i++)
		{
			CurrencyListItem currListItem = new CurrencyListItem();
			currCursor.moveToPosition(i);
			
			currListItem.setKeyId(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_ID)));
			currListItem.setCurrency(currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURRENCY)));
			currListItem.setFlagImage(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FLAG)));
			currListItem.setCurrencyDesc(currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURR_DESC)));
			currFavorList.add(currListItem);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setUpAdapter();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	/**
	 * Listener for Add button on Title bar
	 * @param v
	 */

	public void toFullList(View v)
	{
		//System.out.println("Button Clicked on Add Currency! ");
		//panel.toggle();
		System.out.println("Add again");
		Intent intent = new Intent(this.getBaseContext(),CurrencyFullListActivity.class);
		startActivity(intent);
	}
	
	
	public void backToCurrencyList(View v)
	{
		System.out.println("Remove Currency Button is clicked!");
		this.onBackPressed();
		//Intent intent = new Intent(this.getBaseContext(),CurrencyAdd2Favorite.class);
		//startActivity(intent);
	}
	
	
	class CurrencyListViewAdapter extends BaseAdapter
	{
		//private Cursor currCursor;
		private ArrayList<CurrencyListItem> currFavorList;
		private LayoutInflater currSpinnerInflator;
		
		public CurrencyListViewAdapter(ArrayList<CurrencyListItem> currFavorList)
		{
			//currCursor = currenciesCursor;
			this.currFavorList = currFavorList;
			currSpinnerInflator = activity.getLayoutInflater();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return currFavorList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return currFavorList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			//return null;
			if(convertView == null)
			{
				convertView = currSpinnerInflator.inflate(R.layout.list_item_currency_full, null);
			}
			
			//currCursor.moveToPosition(position);
			//((TextView)convertView.findViewById(R.id.currencynamefull)).setText(currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURRENCY)));
			//((ImageView)convertView.findViewById(R.id.countryflagfull)).setImageResource(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FLAG)));

			((TextView)convertView.findViewById(R.id.currencynamefull)).setText(currFavorList.get(position).getCurrencyDesc());
			((ImageView)convertView.findViewById(R.id.countryflagfull)).setImageResource(currFavorList.get(position).getFlagImage());
			
			Button deleteButton = (Button) convertView.findViewById(R.id.currencydeleted);
			deleteButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//System.out.println("Currency pair at "+ position+" is");
					removeFavorite(position);
					
				}});
			return convertView;
		}
	}
	
	public void removeFavorite(int position)
	{
		//System.out.println("Currency ID "+currFavorList.get(position).getKeyId());
		currDBAdapter.updateCurrencies(currFavorList.get(position).getKeyId(), 0, null, null, 0);
		currFavorList.remove(position);
		currListFavViewAdapter.notifyDataSetChanged();
	}
}
