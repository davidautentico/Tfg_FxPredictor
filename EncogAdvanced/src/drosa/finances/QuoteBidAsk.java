package drosa.finances;

import java.util.Calendar;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class QuoteBidAsk {

	Calendar cal = Calendar.getInstance();
	String symbol;
	double openBid = 0;
	double openAsk = 0;
	double highBid = 0;
	double highAsk = 0;
	double lowBid = 0;
	double lowAsk = 0;
	double closeAsk = 0;
	double closeBid = 0;
	
	double bid = 0;
	double ask = 0;
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public double getOpenBid() {
		return openBid;
	}
	public void setOpenBid(double openBid) {
		this.openBid = openBid;
	}
	public double getOpenAsk() {
		return openAsk;
	}
	public void setOpenAsk(double openAsk) {
		this.openAsk = openAsk;
	}
	public double getHighBid() {
		return highBid;
	}
	public void setHighBid(double highBid) {
		this.highBid = highBid;
	}
	public double getHighAsk() {
		return highAsk;
	}
	public void setHighAsk(double highAsk) {
		this.highAsk = highAsk;
	}
	public double getLowBid() {
		return lowBid;
	}
	public void setLowBid(double lowBid) {
		this.lowBid = lowBid;
	}
	public double getLowAsk() {
		return lowAsk;
	}
	public void setLowAsk(double lowAsk) {
		this.lowAsk = lowAsk;
	}
	public double getCloseAsk() {
		return closeAsk;
	}
	public void setCloseAsk(double closeAsk) {
		this.closeAsk = closeAsk;
	}
	public double getCloseBid() {
		return closeBid;
	}
	public void setCloseBid(double closeBid) {
		this.closeBid = closeBid;
	}
	public double getBid() {
		return bid;
	}
	public void setBid(double bid) {
		this.bid = bid;
	}
	public double getAsk() {
		return ask;
	}
	public void setAsk(double ask) {
		this.ask = ask;
	}
	
	public String toString(){
		return DateUtils.datePrint(cal)
				+" "+PrintUtils.Print5dec(openBid)+"/"+PrintUtils.Print5dec(openAsk)
				+" "+PrintUtils.Print5dec(highBid)+"/"+PrintUtils.Print5dec(highAsk)
				+" "+PrintUtils.Print5dec(lowBid)+"/"+PrintUtils.Print5dec(lowAsk)
				+" "+PrintUtils.Print5dec(closeBid)+"/"+PrintUtils.Print5dec(closeAsk)
				;
	}
	public String toStringBid(){
		return DateUtils.datePrint(cal)
				+" "+PrintUtils.Print5dec(openBid)
				+" "+PrintUtils.Print5dec(highBid)
				+" "+PrintUtils.Print5dec(lowBid)
				+" "+PrintUtils.Print5dec(closeBid);
	}
	public String toStringAsk(){
		return DateUtils.datePrint(cal)
				+" "+PrintUtils.Print5dec(openAsk)
				+" "+PrintUtils.Print5dec(highAsk)
				+" "+PrintUtils.Print5dec(lowAsk)
				+" "+PrintUtils.Print5dec(closeAsk);
	}
	
	public void copy(QuoteBidAsk q) {
		// TODO Auto-generated method stub
		
		this.cal.setTimeInMillis(q.getCal().getTimeInMillis());
		this.openBid = q.getOpenBid();
		this.openAsk = q.getOpenAsk();
		this.highBid = q.getHighBid();
		this.highAsk = q.getHighAsk();
		this.lowBid = q.getLowBid();
		this.lowAsk = q.getLowAsk();
		this.closeBid = q.getCloseBid();
		this.closeAsk = q.getCloseAsk();
		this.bid = q.getBid();
		this.ask = q.getAsk();
	}
	

}
