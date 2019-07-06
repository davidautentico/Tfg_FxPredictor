package drosa.strategies;

import java.util.Date;

public class PeakInterval {
	Date tradetime;
	String symbol;
	int peakType;
	int pinterval;
	
	public Date getTradetime() {
		return tradetime;
	}
	public void setTradetime(Date tradetime) {
		this.tradetime = tradetime;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getPeakType() {
		return peakType;
	}
	public void setPeakType(int peakType) {
		this.peakType = peakType;
	}
	public int getPinterval() {
		return pinterval;
	}
	public void setPinterval(int pinterval) {
		this.pinterval = pinterval;
	}
	
	
}
