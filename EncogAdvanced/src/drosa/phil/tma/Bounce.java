package drosa.phil.tma;

import java.util.Calendar;

public class Bounce {
	
	Calendar cal = Calendar.getInstance();
	int maxPips=0;
	boolean upBounce=true;
	
	public Calendar getCal() {
		return cal;
	}
	public void setCal(Calendar cal) {
		this.cal = cal;
	}
	public int getMaxPips() {
		return maxPips;
	}
	public void setMaxPips(int maxPips) {
		this.maxPips = maxPips;
	}
	public boolean isUpBounce() {
		return upBounce;
	}
	public void setUpBounce(boolean upBounce) {
		this.upBounce = upBounce;
	}			
}
