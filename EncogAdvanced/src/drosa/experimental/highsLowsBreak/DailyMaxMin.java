package drosa.experimental.highsLowsBreak;

public class DailyMaxMin {
	
	int index = 0;
	int value = 0;
	boolean max = true;
	
	public DailyMaxMin(){
	}
	
	public DailyMaxMin(int index,int value,boolean isMax){
		this.index = index;
		this.value = value;
		this.max = isMax;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean isMax() {
		return max;
	}
	public void setMax(boolean max) {
		this.max = max;
	}
	
	
}
