package drosa.neuralNetwork;

import java.util.Calendar;

public class NNElement {
	Calendar cal = Calendar.getInstance();
	
	int isHigh = 0;
	int maxMinPeriod = 0; //0-2000
	int hourOfDay = 0; // 0-23
	int month = 0; //0-11
	int dayOfWeek = 0; //0-5
	//0: loss 1:win
	double outResult = 0; //0-1
	
	
	
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	public int getIsHigh() {
		return isHigh;
	}
	public void setIsHigh(int isHigh) {
		this.isHigh = isHigh;
	}
	public int getMaxMinPeriod() {
		return maxMinPeriod;
	}
	public void setMaxMinPeriod(int maxMinPeriod) {
		this.maxMinPeriod = maxMinPeriod;
	}
	public int getHourOfDay() {
		return hourOfDay;
	}
	public void setHourOfDay(int hourOfDay) {
		this.hourOfDay = hourOfDay;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public double getOutResult() {
		return outResult;
	}
	public void setOutResult(double outResult) {
		this.outResult = outResult;
	}
		
}
