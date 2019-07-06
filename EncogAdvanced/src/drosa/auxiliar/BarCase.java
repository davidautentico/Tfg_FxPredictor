package drosa.auxiliar;

import drosa.utils.BarCaseType;

public class BarCase {
	
	BarCaseType barCaseType; 
	
	int upBars;
	int downBars;
	
	public BarCaseType getBarCaseType() {
		return barCaseType;
	}
	public void setBarCaseType(BarCaseType barCaseType) {
		this.barCaseType = barCaseType;
	}
	public int getUpBars() {
		return upBars;
	}
	public void setUpBars(int upBars) {
		this.upBars = upBars;
	}
	public int getDownBars() {
		return downBars;
	}
	public void setDownBars(int downBars) {
		this.downBars = downBars;
	}
}
