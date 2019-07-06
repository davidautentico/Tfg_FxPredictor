package drosa.finances;

import java.util.Date;

import drosa.utils.DateUtils;

public class ForexQuote {

	Date dateTime;
	String symbol;
	Float open;
	Float high;
	Float close;
	Float low;
	long volume;
	
	
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Float getOpen() {
		return open;
	}
	public void setOpen(Float open) {
		this.open = open;
	}
	public Float getHigh() {
		return high;
	}
	public void setHigh(Float high) {
		this.high = high;
	}
	public Float getClose() {
		return close;
	}
	public void setClose(Float close) {
		this.close = close;
	}
	public Float getLow() {
		return low;
	}
	public void setLow(Float low) {
		this.low = low;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public static ForexQuote decodeForexData(String linea) {
		// TODO Auto-generated method stub
		Date date = DateUtils.getDateTime(linea.split(",")[0].trim(),linea.split(",")[1].trim());
		
		float open = Float.valueOf(linea.split(",")[2].trim());
		float high = Float.valueOf(linea.split(",")[3].trim());
		float low = Float.valueOf(linea.split(",")[4].trim());
		float close = Float.valueOf(linea.split(",")[5].trim());
		long volume= Long.valueOf(linea.split(",")[6].trim());
		
		
		ForexQuote fq = new ForexQuote();
		
		fq.setDateTime(date);
		fq.setOpen(open);
		fq.setClose(close);
		fq.setHigh(high);
		fq.setLow(low);		
		fq.setVolume(volume);
		
		
		//System.out.println(DateUtils.datePrint(date)+','+open+','+high+','+low+
		//		','+close+','+volume);
				
		return fq;
	}
	public void copy(ForexQuote f) {
		// TODO Auto-generated method stub
		dateTime = f.getDateTime();
		symbol = f.getSymbol();
		open = f.getOpen();
		high = f.getHigh();
		close = f.getClose();
		low = f.getLow();
		volume = f.getVolume();
	}
	
	
}
