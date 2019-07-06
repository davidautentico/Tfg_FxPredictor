package drosa.finances;

import java.util.Date;

import drosa.utils.DateUtils;

public class FutureCSIQuote {

	Date date;
	String symbol;
	Float open;
	Float high;
	Float close;
	Float low;
	long volume=-1;
	long openInterest=-1;
	long totalVolume=-1;
	long totalOpenInterest=-1;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
	public long getOpenInterest() {
		return openInterest;
	}
	public void setOpenInterest(long openInterest) {
		this.openInterest = openInterest;
	}
	public long getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(long totalVolume) {
		this.totalVolume = totalVolume;
	}
	public long getTotalOpenInterest() {
		return totalOpenInterest;
	}
	public void setTotalOpenInterest(long totalOpenInterest) {
		this.totalOpenInterest = totalOpenInterest;
	}
	public static FutureCSIQuote decodeFutureCSIData(String linea) {
		// TODO Auto-generated method stub
		Date date = DateUtils.getCSIDate(linea.split(",")[0].trim());
		
		float open = Float.valueOf(linea.split(",")[1].trim());
		float high = Float.valueOf(linea.split(",")[2].trim());
		float low = Float.valueOf(linea.split(",")[3].trim());
		float close = Float.valueOf(linea.split(",")[4].trim());
		long volume= Long.valueOf(linea.split(",")[5].trim());
		long openInterest= Long.valueOf(linea.split(",")[6].trim());
		
		
		FutureCSIQuote fq = new FutureCSIQuote();
		
		fq.setDate(date);
		fq.setOpen(open);
		fq.setClose(close);
		fq.setHigh(high);
		fq.setLow(low);		
		fq.setVolume(volume);
		fq.setOpenInterest(openInterest);
		
		return fq;
	}
	
	
	
	
}
