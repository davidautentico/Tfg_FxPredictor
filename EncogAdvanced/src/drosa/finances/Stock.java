package drosa.finances;

import java.util.ArrayList;
import java.util.List;

import drosa.utils.TimeFrame;

public class Stock {
	
	String Symbol;
	TimeFrame tf;
	List<Quote> quotes = new ArrayList<Quote>();
	
	public String getSymbol() {
		return Symbol;
	}
	public void setSymbol(String symbol) {
		Symbol = symbol;
	}
	public TimeFrame getTf() {
		return tf;
	}
	public void setTf(TimeFrame tf) {
		this.tf = tf;
	}
	public List<Quote> getQuotes() {
		return quotes;
	}
	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}
	
}
