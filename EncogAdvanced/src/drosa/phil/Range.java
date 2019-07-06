package drosa.phil;

import java.util.Calendar;

import drosa.utils.DateUtils;

public class Range {
	Calendar date;
	int range;
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	
	public String toString(){
		
		return DateUtils.datePrint(this.date)+" "+this.range;
	}
}
