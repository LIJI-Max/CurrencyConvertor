package com.learning.android.currencyconvertor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class CurrencyListItemDragDropController {
	
	private CurrencyConvertor parentListActivity;
	private ListView parentListView;
	private boolean isDraging = false;
	private int draggedItem;
	private ImageView currItemShadowImageView;
	private WindowManager.LayoutParams lp;
	private RelativeLayout rl;
	private WindowManager currWindowManager;
	private CurrencySlidingUpPanel removeItemPanel;
	private int removeItemPanelTop;
	private int currItemHeight;
	private CurrencyListAdapter currListAdapter;
	
	public CurrencyListItemDragDropController(CurrencyConvertor parListAcvity, ListView parListView, CurrencyListAdapter currencyListAdapter)
	{
		parentListActivity = parListAcvity;
		parentListView = parListView;
		currListAdapter = currencyListAdapter;
		currItemShadowImageView = new ImageView(parentListActivity);
		rl = ((RelativeLayout)(parentListView.getParent().getParent()));
		currWindowManager =  (WindowManager)parentListActivity.getSystemService(Context.WINDOW_SERVICE);
		setUpLayOutParams(0,0);
		parentListView.requestDisallowInterceptTouchEvent(true);
		removeItemPanel = (CurrencySlidingUpPanel) parListAcvity.findViewById(R.id.removeItemPanel);
		
		removeItemPanel.setUpOpenListener(openListener);
	}
	
	Animation.AnimationListener openListener=new Animation.AnimationListener() {
		public void onAnimationEnd(Animation animation) {
			removeItemPanelTop = removeItemPanel.getTop();
		}
		
		public void onAnimationRepeat(Animation animation) {
			// not needed
		}
		
		public void onAnimationStart(Animation animation) {
			// not needed
		}
	};
	
	
	public void setUpLayOutParams(int x, int y)
	{
		lp = new WindowManager.LayoutParams();
		lp.gravity = Gravity.TOP;
		lp.x = x;
		lp.y = y;
		lp.height =  WindowManager.LayoutParams.WRAP_CONTENT;
		lp.width =  WindowManager.LayoutParams.WRAP_CONTENT;
		lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		
		lp.format = PixelFormat.TRANSLUCENT;
		lp.windowAnimations = 0;
	}
	
	public void setUpOnClickListener(View v)
	{
		v.setLongClickable(true);
		v.setOnLongClickListener(currListItemOnLongClickListener);
		v.setOnTouchListener(currListItemOnTouchListener);
	}
	
	private OnLongClickListener currListItemOnLongClickListener = new OnLongClickListener()
	{
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			//v.getParent().requestDisallowInterceptTouchEvent(false);
			draggedItem = parentListView.getPositionForView(v);
			System.out.println("Long Click Item: "+ draggedItem+ " Left "+ v.getLeft()+" Top "+v.getTop());
			isDraging = true;
			v.setDrawingCacheEnabled(true);
			Bitmap bitmap =  Bitmap.createBitmap(v.getDrawingCache());
			currItemShadowImageView.setImageBitmap(bitmap);
			currItemHeight = v.getHeight();
			lp.y = v.getTop()+ currItemHeight/2;
			currItemShadowImageView.setBackgroundColor(R.color.royal_blue);
			currItemShadowImageView.setFadingEdgeLength(10);
			currWindowManager.addView(currItemShadowImageView,lp);
			//popUpRecycleView();
			removeItemPanel.toggle();
			//System.out.println("Remove Panel Top "+removeItemPanel.getBottom());
			removeItemPanelTop = removeItemPanel.getTop();
			return true;
		}
	};
	
	
	public void setUpOnTouchListner(View v)
	{
		v.setOnTouchListener(currListOnTouchListener);
	}
	
	private void onEnterRemoveItemPanel() {
		// TODO Auto-generated method stub
		removeItemPanel.setBackgroundColor(parentListActivity.getResources().getColor(R.color.orange_red));
	}
	
	private void onExitRemoveItemPanel()
	{
		removeItemPanel.setBackgroundColor(parentListActivity.getResources().getColor(R.color.white));
	}
	
	private OnTouchListener currListOnTouchListener = new OnTouchListener(){
		
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			System.out.println("List Listener On Touch!! "+event.getY()+" Action "+event.getAction());
			
			if(isDraging)
			{
				switch(event.getAction())
				{
				  case MotionEvent.ACTION_DOWN:
					   System.out.println("Action Down On Touch!! "+event.getX()+" "+event.getY()+" Action "+event.getAction());
					return true;
				   case MotionEvent.ACTION_MOVE:
					  System.out.println("Action Move On Touch!! "+event.getY()+" Action "+event.getAction());
					   //lp.y = lp.y+10;
					   ActionMotionMove(event);
					 return true;
				   case MotionEvent.ACTION_UP:
					   System.out.println("Touch Event is released!! ");
					   ActionMotionUp(event);
					   return true;
				   case MotionEvent.ACTION_CANCEL:
					   System.out.println("Action is cancelled!");
					   return true;
					default:
						System.out.println("Touch Event : " + event.getAction());
						return true;
				}
			}
			return false;
		}

	};
	
	private void ActionMotionMove(MotionEvent event)
	{
		lp.y = (int) event.getY();
		 currWindowManager.updateViewLayout(currItemShadowImageView, lp);
		 if((int) event.getY()+ currItemHeight/2>removeItemPanelTop)
			 onEnterRemoveItemPanel();
		 else onExitRemoveItemPanel();
	}
	
	private void ActionMotionItemMove(MotionEvent event,int height)
	{
		lp.y = (int) event.getY()+height;
		 currWindowManager.updateViewLayout(currItemShadowImageView, lp);
		 System.out.println("Panel Top "+removeItemPanelTop);
		 if(lp.y+ currItemHeight/2>removeItemPanelTop)
			 onEnterRemoveItemPanel();
		 else onExitRemoveItemPanel();
	}
	
	private void ActionMotionUp(MotionEvent event)
	{
		if(currItemShadowImageView.isShown()==true)
		   {
			   removeItemPanelToggle();
		   }
		   if((int) event.getY()+ currItemHeight/2>removeItemPanelTop)
			   currListAdapter.removeItem(draggedItem);
	}
	
	
	private void ActionMotionItemUp(MotionEvent event,int height)
	{
		if(currItemShadowImageView.isShown()==true)
		   {
			   removeItemPanelToggle();
		   }
		   if((int) event.getY()+height+ currItemHeight/2>removeItemPanelTop)
			   currListAdapter.removeItem(draggedItem);
	}
		
		private OnTouchListener currListItemOnTouchListener = new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				System.out.println("List Item Listener On Touch!! "+event.getY()+" Action "+event.getAction());
				
				if(isDraging)
				{
					switch(event.getAction())
					{
					case MotionEvent.ACTION_UP:
//						System.out.println("Item On Touch Up!!");
//						 if(currItemShadowImageView.isShown()==true)
//						 {
//							 removeItemPanelToggle();
//						 }
						//System.out.println("Touch Event is released!! ");
						ActionMotionItemUp(event,(parentListView.getPositionForView(v))*v.getHeight());
						return false;
					//break;
					case MotionEvent.ACTION_MOVE:
						//System.out.println("On Touch Listener Action Move : "+lp.y);
						 ActionMotionItemMove(event,(parentListView.getPositionForView(v))*v.getHeight());
						 return false;
						//break;
					}
				}
				return false;
			}
			
		};
		
		private void removeItemPanelToggle()
		{
			 removeItemPanel.toggle();
			 currWindowManager.removeView(currItemShadowImageView);
		     isDraging = false;
		     removeItemPanel.setBackgroundColor(parentListActivity.getResources().getColor(R.color.white));
		}
}
