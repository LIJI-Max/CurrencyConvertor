package com.learning.android.currencyconvertor;



import kankan.wheel.widget.WheelView;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class CurrencySlidingUpPanel extends LinearLayout{

	private int speed=300;
	private boolean isOpen=false;
	private WheelView fromCurrencyWheel;
	private WheelView toCurrencyWheel;
	private Cursor currFavor;
	private Animation.AnimationListener openListener;
	
	public CurrencySlidingUpPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a= context.obtainStyledAttributes(attrs,R.styleable.SlidingPanel,0, 0);
		speed=a.getInt(R.styleable.SlidingPanel_speed, 300);
		a.recycle();
		
	}
	public void setUpOpenListener(Animation.AnimationListener listener)
	{
		openListener = listener;
	}
	
	public void toggle() {
		TranslateAnimation anim=null;
		
		isOpen=!isOpen;
		
		if (isOpen) {
			setVisibility(View.VISIBLE);
			anim=new TranslateAnimation(0.0f, 0.0f,getHeight(),0.0f);
			if(openListener!=null)
			anim.setAnimationListener(openListener);
		}
		else {
			anim=new TranslateAnimation(0.0f, 0.0f, 0.0f,getHeight());
			anim.setAnimationListener(collapseListener);
		}
		
		anim.setDuration(speed);
		anim.setInterpolator(new AccelerateInterpolator(1.0f));
		//v.startAnimation(anim);
		startAnimation(anim);
	}
	
	Animation.AnimationListener collapseListener=new Animation.AnimationListener() {
		public void onAnimationEnd(Animation animation) {
			setVisibility(View.GONE);
		}
		
		public void onAnimationRepeat(Animation animation) {
			// not needed
		}
		
		public void onAnimationStart(Animation animation) {
			// not needed
		}
	};
	
	public void setUpWheelView(CurrencyDBAdapter currDBAdapter,Context ctx)
	{
		currFavor = currDBAdapter.fetchCurrencyConfig(CurrencyDBAdapter.UNFAVOR);
		
		//Set Wheel Text
	    fromCurrencyWheel = (WheelView)findViewById(R.id.from_currency_wheel);
	    toCurrencyWheel = (WheelView)findViewById(R.id.to_currency_wheel);
	    fromCurrencyWheel.setViewAdapter(new CurrencyAddWheelTextAdapter(currFavor,ctx));
	    toCurrencyWheel.setViewAdapter(new CurrencyAddWheelTextAdapter(currFavor,ctx));

	}
	
	public void addButtonClick()
	{
		System.out.println("New Currency Pair Selected!");
		
	}
	
	public int getLeftWheelIndex()
	{
		currFavor.moveToPosition(fromCurrencyWheel.getCurrentItem());
		System.out.println("From Currency ID "+ currFavor.getInt(currFavor.getColumnIndex(CurrencyDBAdapter.KEY_ID)));
		return currFavor.getInt(currFavor.getColumnIndex(CurrencyDBAdapter.KEY_ID));
	}
	
	public int getRightWheelIndex()
	{
		currFavor.moveToPosition(toCurrencyWheel.getCurrentItem());
		System.out.println("To Currency ID "+ currFavor.getInt(currFavor.getColumnIndex(CurrencyDBAdapter.KEY_ID)));
		return currFavor.getInt(currFavor.getColumnIndex(CurrencyDBAdapter.KEY_ID));

	}
	
	public void setOpen(boolean open) {
		// TODO Auto-generated method stub
		this.isOpen = open;
	}
	
	public boolean getOpen()
	{
		return this.isOpen;
	}
	
}
