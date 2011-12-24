package com.learning.android.currencyconvertor;


/**
 * @author Li Ji
 *
 */
public class CurrencyXchgRatePair {
	
	private int cursorRowID;
	
	private int dbRowID;
	
	private float fromCurrAMT;
	
	private float toCurrAMT;
	
	private double rateADV;
	
	private double rateREV;
	
	private int fromCurrID;
	
	private int toCurrID;
	
	private boolean direction;
	
	private int selection;

	public int getCursorRowID() {
		return cursorRowID;
	}

	public void setCursorRowID(int cursorRowID) {
		this.cursorRowID = cursorRowID;
	}

	public int getDbRowID() {
		return dbRowID;
	}
	
	public CurrencyXchgRatePair()
	{
		if(this.getFromCurrID()==this.getToCurrID())
		{
			this.setRateADV(1);
			this.setRateREV(1);
		}
	}

	public void setDbRowID(int dbRowID) {
		this.dbRowID = dbRowID;
	}


	public float getFromCurrAMT() {
		return fromCurrAMT;
	}

	public void setFromCurrAMT(float fromCurrAMT) {
		this.fromCurrAMT = fromCurrAMT;
	}

	public float getToCurrAMT() {
		return toCurrAMT;
	}

	public void setToCurrAMT(float toCurrAMT) {
		this.toCurrAMT = toCurrAMT;
	}

	public double getRateADV() {
		return rateADV;
	}

	public void setRateADV(double rateADV) {
		this.rateADV = rateADV;
	}

	public double getRateREV() {
		return rateREV;
	}

	public void setRateREV(double rateREV) {
		this.rateREV = rateREV;
	}

	public int getFromCurrID() {
		return fromCurrID;
	}

	public void setFromCurrID(int fromCurrID) {
		this.fromCurrID = fromCurrID;
		checkFromAndToCurrencySame();
	}

	public int getToCurrID() {
		return toCurrID;
	}

	public void setToCurrID(int toCurrID) {
		this.toCurrID = toCurrID;
		checkFromAndToCurrencySame();
	}
	
	public void checkFromAndToCurrencySame()
	{
		if (this.fromCurrID==this.toCurrID)
		{
			this.setRateADV(1);
			this.setRateREV(1);
		}
	}
	
	public boolean isDirection() {
		return direction;
	}

	public void setDirection(boolean direction) {
		this.direction = direction;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}
	
	
	/**
	 * Triggered when from spinner gets updated
	 * @param position -- Position of Selection in from Spinner
	 */
	public void updateFromSpinner(int currencyId)
	{
		if(!isDirection())
		{
		    setFromCurrID(currencyId);
		}
		else
		{
			setToCurrID(currencyId);
		}
	}
	
	/**
	 * Triggered when to spinner gets updated
	 * @param position -- Position of Selection in to Spinner
	 */
	public void updateToSpinner(int currencyId)
	{
		if(!isDirection())
		{
		    setToCurrID(currencyId);
		}
		else
		{
			setFromCurrID(currencyId);
		}
	}
	
	
	
}
