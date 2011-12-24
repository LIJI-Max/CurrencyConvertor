package com.learning.android.currencyconvertor;

public class CurrencyListItem {
	
	private int flagImage;
	private String currency;
	private boolean favoriate;
	private String currencyDesc;
	private int keyId; 
	
	public int getKeyId() {
		return keyId;
	}
	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}
	public int getFlagImage() {
		return flagImage;
	}
	public void setFlagImage(int flagImage) {
		this.flagImage = flagImage;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public boolean isFavoriate() {
		return favoriate;
	}
	public void setFavoriate(boolean favoriate) {
		this.favoriate = favoriate;
	}
	public String getCurrencyDesc() {
		return currencyDesc;
	}
	public void setCurrencyDesc(String currencyDesc) {
		this.currencyDesc = currencyDesc;
	}
	
}
