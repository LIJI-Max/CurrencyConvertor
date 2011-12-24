package com.learning.android.currencyconvertor;

import java.util.ArrayList;

import com.learning.android.currencyconvertor.CurrencyAdd2Favorite.CurrencyListViewAdapter;

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


public class CurrencyFullListActivity extends Activity{

	private ListView fullList;
	private Activity activity;
	private CurrencyDBAdapter currDBAdapter;
	private ArrayList<CurrencyListItem> currFullList;
	private CurrencyListViewAdapter currListFullViewAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 //Setting up Customized Title Bar
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.full_currency_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_full_curr_list);
		
		currDBAdapter = new CurrencyDBAdapter(this);
		currDBAdapter.open();
		
		Cursor currCursor = currDBAdapter.fetchCurrencyConfig(CurrencyDBAdapter.ALL);
		currFullList = new ArrayList<CurrencyListItem>();
		loadCurrFullList(currCursor);
		
		fullList = (ListView) this.findViewById(R.id.fulllist);
		activity = this;
		currListFullViewAdapter = new CurrencyListViewAdapter(currFullList);
		
		fullList.setAdapter(currListFullViewAdapter);
	}
	
	private void loadCurrFullList(Cursor currCursor) {
		currFullList.clear();
		for(int i=0;i<currCursor.getCount();i++)
		{
			CurrencyListItem currListItem = new CurrencyListItem();
			currCursor.moveToPosition(i);
			
			currListItem.setKeyId(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_ID)));
			currListItem.setCurrency(currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURRENCY)));
			currListItem.setFavoriate(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FAVOURITE))==1?true:false);
			currListItem.setFlagImage(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FLAG)));
			currListItem.setCurrencyDesc(currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURR_DESC)));
			currFullList.add(currListItem);
		}
	}

	public void backToCurrencyList(View v)
	{
		//System.out.println("Back to Select View is triggered!");
		this.onBackPressed();
	}
	
	public void addToFav(View v)
	{
		//System.out.println("Back to Home ~~");
		Intent intent = new Intent(this, CurrencyConvertor.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		this.startActivity(intent);
		
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
	
	
	class CurrencyListViewAdapter extends BaseAdapter
	{
		//private Cursor currCursor;
		private ArrayList<CurrencyListItem> currFullList;
		private LayoutInflater currSpinnerInflator;
		
		public CurrencyListViewAdapter(ArrayList<CurrencyListItem> currFullList)
		{
			//currCursor = currenciesCursor;
			this.currFullList = currFullList;
			currSpinnerInflator = activity.getLayoutInflater();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return currFullList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return currFullList.get(position);
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

			((TextView)convertView.findViewById(R.id.currencynamefull)).setText(currFullList.get(position).getCurrencyDesc());
			((ImageView)convertView.findViewById(R.id.countryflagfull)).setImageResource(currFullList.get(position).getFlagImage());
			
			Button deleteButton = (Button) convertView.findViewById(R.id.currencydeleted);
			
			if(currFullList.get(position).isFavoriate())
			{
				deleteButton.setBackgroundResource(R.drawable.x_currency_fav);
			}
			else if (!currFullList.get(position).isFavoriate())
			{
				deleteButton.setBackgroundResource(R.drawable.x_currency_unfav);
			}
				
			deleteButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//System.out.println("Currency pair at "+ position+" is");
					//removeFavorite(position);
					currFullList.get(position).setFavoriate(!currFullList.get(position).isFavoriate());
					currDBAdapter.updateCurrencyFavour(currFullList.get(position).getKeyId(), currFullList.get(position).isFavoriate());
					currListFullViewAdapter.notifyDataSetChanged();
				}});
			return convertView;
		}
	}
	
}
