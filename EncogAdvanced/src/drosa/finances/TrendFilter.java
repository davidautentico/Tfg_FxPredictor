package drosa.finances;

import drosa.utils.TrendType;

public class TrendFilter {
	
	int lowBar;
	int highBar;
	TrendType type;
	
	public int getLowBar() {
		return lowBar;
	}
	public void setLowBar(int lowBar) {
		this.lowBar = lowBar;
	}
	public int getHighBar() {
		return highBar;
	}
	public void setHighBar(int highBar) {
		this.highBar = highBar;
	}
	public TrendType getType() {
		return type;
	}
	public void setType(TrendType type) {
		this.type = type;
	}
	
	
}
