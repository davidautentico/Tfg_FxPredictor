package drosa.finances;

import java.util.Date;

import drosa.utils.DateUtils;
import drosa.utils.TimeFrame;

public class FutureQuote {

	Date dateTime;
	String symbol;
	Float open;
	Float high;
	Float close;
	Float low;
	long volume=-1;
	int down=-1;
	int up=-1;
	int openInterest=-1;
	
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
	public int getDown() {
		return down;
	}
	public void setDown(int down) {
		this.down = down;
	}
	public int getUp() {
		return up;
	}
	public void setUp(int up) {
		this.up = up;
	}
	public int getOpenInterest() {
		return openInterest;
	}
	public void setOpenInterest(int openInterest) {
		this.openInterest = openInterest;
	}
	
	public static FutureQuote decodeFutureData(String linea,TimeFrame tf) {
		// TODO Auto-generated method stub
		FutureQuote future = new FutureQuote();
		
		Date date = DateUtils.getDateTime2(linea.split(",")[0].trim(),linea.split(",")[1].trim());
		
		float open = Float.valueOf(linea.split(",")[2].trim());
		float high = Float.valueOf(linea.split(",")[3].trim());
		float low = Float.valueOf(linea.split(",")[4].trim());
		float close = Float.valueOf(linea.split(",")[5].trim());
		
		future.setDateTime(date);
		future.setOpen(open);
		future.setClose(close);
		future.setHigh(high);
		future.setLow(low);		
		
		long volume=-1;
		int openInterest=-1;
		int down=-1;
		int up=-1;
		
		if (tf == TimeFrame.DAILY){
			volume = Long.valueOf(linea.split(",")[6].trim());
			openInterest = Integer.valueOf(linea.split(",")[7].trim());
			
			future.setVolume(volume);
			future.setOpenInterest(openInterest);
		}else{
			up = Integer.valueOf(linea.split(",")[6].trim());
			down = Integer.valueOf(linea.split(",")[7].trim());
			
			future.setUp(up);
			future.setDown(down);
		}
		
		return future;
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
