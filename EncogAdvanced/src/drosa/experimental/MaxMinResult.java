package drosa.experimental;

import java.util.Calendar;

import drosa.utils.DateUtils;

public class MaxMinResult {

	Calendar cal = Calendar.getInstance();
	Calendar closeTime = Calendar.getInstance();
	int maxMin = 0;
	double tp = -1;
	double sl = -1;
	int win = 0;
	int index=0;
	
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	
	public Calendar getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Calendar closeTime) {
		this.closeTime = closeTime;
	}
	public int getMaxMin() {
		return maxMin;
	}
	public void setMaxMin(int maxMin) {
		this.maxMin = maxMin;
	}
	public double getTp() {
		return tp;
	}
	public void setTp(double tp) {
		this.tp = tp;
	}
	public double getSl() {
		return sl;
	}
	public void setSl(double sl) {
		this.sl = sl;
	}
	
	
	
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String toString(){
		return DateUtils.datePrint(this.cal)+" "+DateUtils.datePrint(this.closeTime)+" "+maxMin+" "+sl+" "+tp+" "+this.getWin();
	}
	
	
}
