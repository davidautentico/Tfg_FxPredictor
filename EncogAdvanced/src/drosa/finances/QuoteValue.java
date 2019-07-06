package drosa.finances;

import java.util.Date;

import drosa.utils.QuoteType;

public class QuoteValue {
	QuoteType quoteType;
	float value;
	Date date;
	
	
	public QuoteType getQuoteType() {
		return quoteType;
	}
	public void setQuoteType(QuoteType quoteType) {
		this.quoteType = quoteType;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
