package drosa.phil.ranges;

import java.util.Calendar;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class Overlap {
	Calendar cal = Calendar.getInstance();
	double value = 0;
	
	
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	public String toString(){
		return DateUtils.datePrint(cal)+" value= "+PrintUtils.Print2dec(value, false);
	}
}
