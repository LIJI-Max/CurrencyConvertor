package com.learning.android.currencyconvertor;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

public class CurrencySyncHandler extends Handler{
	
	//Message Update
	static final int MSG_CURR_SELECT_UPD = 1;
	static final int MSG_CURR_ALL_UPD = 2;
	static final int MSG_CURR_INT_NOT_CONN = 3;
	
	private boolean readyToUpdate = true;
	
	public static final String CURR_URL = "http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate";
	private CurrencyDBAdapter currencyDBAdatper;
	private Cursor currCursor;
	private Context ctx;
	
	public CurrencySyncHandler(Looper looper, Context ctx, CurrencyDBAdapter currDBAdapter)
	{
		super(looper);
		currencyDBAdatper = currDBAdapter;
		this.ctx = ctx;
		this.readyToUpdate = true;
	}
	
	public void setReadyToUpdate()
	{
		this.readyToUpdate = true;
	}
	
	public void unSetReadyToUpdate()
	{
		this.readyToUpdate = false;
	}

	@Override
	public void handleMessage(Message msg) {
		
		//System.out.println("Updating Currency Exchange Rate ");
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		if(checkInternetConnection())
		{
		      updateXchgRates(msg);
			if(msg.getData().getInt("Selection") == CurrencyDBAdapter.SELECTION_MODE[0])
				try {
					msg.replyTo.send(Message.obtain(null,MSG_CURR_SELECT_UPD , 0, 0));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				try {
					msg.replyTo.send(Message.obtain(null,MSG_CURR_ALL_UPD , 0, 0));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    }
		else
		{
			try {
				msg.replyTo.send(Message.obtain(null,MSG_CURR_INT_NOT_CONN , 0, 0));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//Function Update Currency Exchange Rate in the Database Table
	private void updateXchgRates(Message msg) {
		System.out.println("Updating Currency Exchange Rate !");
		// TODO Auto-generated method stub
		if(msg.getData().getInt("Selection")==CurrencyDBAdapter.SELECTION_MODE[0])
			currCursor = currencyDBAdatper.getSelectedCurrencies();
		else if(msg.getData().getInt("Selection")==CurrencyDBAdapter.SELECTION_MODE[1])
		    //currCursor = currencyDBAdatper.getAllCurrencies();
			currCursor = currencyDBAdatper.getFavCurrencies();
		else
			return;
		
		
		float rateAdv;
		float rateRev;
		String fromCurr;
		String toCurr;
		
		//Update Currency List inside the Database
		System.out.println("Update Currency Cursor Count " + currCursor.getCount());
		while(currCursor.moveToNext())
		{
			fromCurr = currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_FROM_CURR));
			toCurr = currCursor.getString(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_TO_CURR));
			rateAdv = httpGetCurr(fromCurr,toCurr);
			rateRev = httpGetCurr(toCurr,fromCurr);
			//System.out.println("Cursor Position"+currCursor.getPosition()+" FromCurrency "+fromCurr+" ToCurrency "+toCurr+" "+rateAdv+" "+rateRev);
		    currencyDBAdatper.updateXchgRate(currCursor.getInt(currCursor.getColumnIndex(CurrencyDBAdapter.KEY_ID)), rateAdv, rateRev);
		}
		
	    currCursor.close();
	}

	/*
	 * Fetch Currency Exchange Rate from website www.webservicex.net
	 * @param fromCurr from currency toCurr to currency
	 * @return exchange rate
	 */
	private float httpGetCurr(String fromCurr, String toCurr) {
		
		// Download Currencies Rate Using HttpGet
		HttpClient httpClient = new DefaultHttpClient();
		String url = CURR_URL + "?FromCurrency="+fromCurr+"&ToCurrency="+toCurr;
		float result = 0;
		
		try{
			HttpGet httpGet = new HttpGet(url);
			try{
				HttpResponse response = httpClient.execute(httpGet);
				
				if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
				{
					byte[] buffer = new byte[1024];
					int numBytes = 0;
					InputStream is = response.getEntity().getContent();
					
					try{
						numBytes = is.read(buffer);
						is.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
					
					result =  XMLParse(new String(buffer,0,numBytes),"double");
				}
			}
			catch(ClientProtocolException e)
			{
				System.out.println("Protocol: " + e.toString());
			}
			catch(IOException e)
			{
				System.out.println("IO: " + e.toString());
			}
			
		}catch(IllegalArgumentException e)
		{
			System.out.println("Illegal Arguments: "+e.toString());
		}
		return result;
		
	}

	//Parse XML file
	private float XMLParse(String content, String tag) {
		
		String strReturn = "";
		int flag = 0;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(new StringReader(content));
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (xpp.getName().toString().equals(tag)) {
						flag = 1;
					}
				} else if (eventType == XmlPullParser.TEXT) {
					if (flag == 1) {
						strReturn = xpp.getText();
					} else {
						strReturn = "0";
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			System.out.println(e.toString());
			return 0;
		} catch (IOException e) {
			System.out.println(e.toString());
			return 0;
		}
		
		return new Float(strReturn);
		
	}
	
	
	/**
	 * Convert Host Name to integer IP address
	 * @param hostname host address
	 * @return
	 */
	public static int lookupHost(String hostname) {
	    InetAddress inetAddress;
	    try {
	        inetAddress = InetAddress.getByName(hostname);
	    } catch (Exception e) {
	        return -1;
	    }
	    byte[] addrBytes;
	    int addr;
	    addrBytes = inetAddress.getAddress();
	    addr = ((addrBytes[3] & 0xff) << 24)
	            | ((addrBytes[2] & 0xff) << 16)
	            | ((addrBytes[1] & 0xff) << 8)
	            |  (addrBytes[0] & 0xff);
//	    addr = ipToInt(inetAddress.getHostAddress());
	    return addr;
	}
	
	/**
	 * @return true connected to Internet
	 *         false not connected Internet
	 */
	private boolean checkInternetConnection() {
	    ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    // test for connection
	    if (cm.getActiveNetworkInfo() != null
	            && cm.getActiveNetworkInfo().isAvailable()
	            && cm.getActiveNetworkInfo().isConnected()
	            && cm.getBackgroundDataSetting()
//	            && cm.requestRouteToHost(cm.getActiveNetworkInfo().getType(), lookupHost("www.webservicex.net"))
	            && this.readyToUpdate == true
	            ) {
	    	
	        return true;
	    } else {
	        return false;
	    }
	}
}
