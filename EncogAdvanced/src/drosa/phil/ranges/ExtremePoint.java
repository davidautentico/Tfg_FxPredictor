package drosa.phil.ranges;

import java.util.Calendar;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class ExtremePoint {

	Calendar cal	= Calendar.getInstance();
	Calendar calMax	= Calendar.getInstance();
	double value	= 0;
	boolean maximum = true; 
	boolean first	= true;
	int indexData   = 0;
	int indexDay    = 0;
	
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	
	public Calendar getCalMax() {
		return calMax;
	}
	public void setCalMax(Calendar calMax) {
		this.calMax = calMax;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public boolean isMaximum() {
		return maximum;
	}
	public void setMaximum(boolean maximum) {
		this.maximum = maximum;
	}
	public boolean isFirst() {
		return first;
	}
	public void setFirst(boolean first) {
		this.first = first;
	}
	public int getIndexData() {
		return indexData;
	}
	public void setIndexData(int indexData) {
		this.indexData = indexData;
	}
	public int getIndexDay() {
		return indexDay;
	}
	public void setIndexDay(int indexDay) {
		this.indexDay = indexDay;
	}
	
	public String toString(){
		String res = DateUtils.datePrint(this.cal);
		res+= " "+this.first+" "+this.maximum+" "+this.indexData+" "+this.indexDay+" "+PrintUtils.Print4dec(this.value);
		return res;
		
	}
	
	
}
