package drosa.phil;

import java.util.ArrayList;

import drosa.finances.Quote;

public class QuoteArray {
	String id=null;
	ArrayList<Quote> quotes = new ArrayList<Quote>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<Quote> getQuotes() {
		return quotes;
	}
	public void setQuotes(ArrayList<Quote> quotes) {
		this.quotes = quotes;
	}
	
	public void addQuote(Quote q){
		quotes.add(q);
	}
	
	
}
