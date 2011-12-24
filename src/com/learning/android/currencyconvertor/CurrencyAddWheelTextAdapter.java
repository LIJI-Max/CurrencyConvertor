package com.learning.android.currencyconvertor;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class CurrencyAddWheelTextAdapter extends AbstractWheelTextAdapter {
	Cursor currCursor;

	public CurrencyAddWheelTextAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public CurrencyAddWheelTextAdapter(Cursor currenciesCursor, Context context)
	{
		 super(context, R.layout.spinner_item_currency_selected, NO_RESOURCE);
		 setItemTextResource(R.id.currencyName);
		 currCursor = currenciesCursor;
	}
	
	

	@Override
	public View getItem(int index, View convertView, ViewGroup parent) {
		 View view = super.getItem(index, convertView, parent);
		 ImageView img = (ImageView)view.findViewById(R.id.countryFlag);
		 currCursor.moveToPosition(index);
		 img.setImageResource(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FLAG)));
		 return view;
	}

	@Override
	public CharSequence getItemText(int index) {
		// TODO Auto-generated method stub
		currCursor.moveToPosition(index);
		return (CharSequence)currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_CURRENCY));
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return currCursor.getCount();
	}
}
