package drosa.phil.strategy;

import java.util.Calendar;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class StrategyTrade {

	Calendar openCal = Calendar.getInstance();
	Calendar closeCal = Calendar.getInstance();
	double entry = -1;
	double sl = -1;
	double tp = -1;
	boolean win = true;
	TradeType tradeType = TradeType.NONE;
	

	
	public Calendar getOpenCal() {
		return openCal;
	}
	public void setOpenCal(Calendar openCal) {
		this.openCal = openCal;
	}
	public Calendar getCloseCal() {
		return closeCal;
	}
	public void setCloseCal(Calendar closeCal) {
		this.closeCal = closeCal;
	}
	public double getEntry() {
		return entry;
	}
	public void setEntry(double entry) {
		this.entry = entry;
	}
	public double getSl() {
		return sl;
	}
	public void setSl(double sl) {
		this.sl = sl;
	}
	public double getTp() {
		return tp;
	}
	public void setTp(double tp) {
		this.tp = tp;
	}
	public boolean isWin() {
		return win;
	}
	public void setWin(boolean win) {
		this.win = win;
	}
	public TradeType getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}
	
	public String toString(){
		String str = DateUtils.datePrint(this.openCal)
				+" "+DateUtils.datePrint(this.closeCal)
				+" "+this.tradeType.name()
				+" "+PrintUtils.Print4dec(entry)
				+" "+PrintUtils.Print4dec(sl)
				+" "+PrintUtils.Print4dec(tp)
				+" "+this.isWin();
		return str;
	}
	
}
