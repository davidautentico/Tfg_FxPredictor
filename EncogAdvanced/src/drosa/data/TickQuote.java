package drosa.data;

import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class TickQuote {
	
	short year;
	byte month;
	byte day;
	byte hh;
	byte mm;
	byte ss;
	
	int bid;
	int ask;
	double askvolume;
	double bidvolume;
	
	
	public short getYear() {
		return year;
	}
	public void setYear(short year) {
		this.year = year;
	}
	public byte getMonth() {
		return month;
	}
	public void setMonth(byte month) {
		this.month = month;
	}
	public byte getDay() {
		return day;
	}
	public void setDay(byte day) {
		this.day = day;
	}
	public byte getHh() {
		return hh;
	}
	public void setHh(byte hh) {
		this.hh = hh;
	}
	public byte getMm() {
		return mm;
	}
	public void setMm(byte mm) {
		this.mm = mm;
	}
	public byte getSs() {
		return ss;
	}
	public void setSs(byte ss) {
		this.ss = ss;
	}
	public int getBid() {
		return bid;
	}
	public void setBid(int bid) {
		this.bid = bid;
	}
	public int getAsk() {
		return ask;
	}
	public void setAsk(int ask) {
		this.ask = ask;
	}
	
	public double getAskvolume() {
		return askvolume;
	}
	public void setAskvolume(double askvolume) {
		this.askvolume = askvolume;
	}
	public double getBidvolume() {
		return bidvolume;
	}
	public void setBidvolume(double bidvolume) {
		this.bidvolume = bidvolume;
	}
	
	public void setCal(Calendar cal) {
		// TODO Auto-generated method stub
		this.year  = (short) cal.get(Calendar.YEAR);
		this.month = (byte) (cal.get(Calendar.MONTH)+1);
		this.day   = (byte) cal.get(Calendar.DAY_OF_MONTH);
		this.hh    = (byte) cal.get(Calendar.HOUR_OF_DAY);
		this.mm    = (byte) cal.get(Calendar.MINUTE);
		this.ss    = (byte) cal.get(Calendar.SECOND);
	}
	
	public static void getCalendar(Calendar cal, TickQuote q) {
		// TODO Auto-generated method stub
		cal.set(q.getYear(), q.getMonth()-1, q.getDay(), q.getHh(), q.getMm(),q.getSs());
	}
	
	public String toString(){
		String str = DateUtils.datePrint(year, month, day, hh, mm, ss)+" "+bid+" "+ask+" "+PrintUtils.Print2dec(askvolume, false)+" "+PrintUtils.Print2dec(bidvolume, false);
		return str;
	}
	
	
	

}
