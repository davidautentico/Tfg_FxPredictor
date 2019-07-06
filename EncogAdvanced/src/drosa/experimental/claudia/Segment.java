package drosa.experimental.claudia;

public class Segment {
	int firstIndex = -1;
	int lastIndex = -1;
	int diff = 0;
	int trend = 0;
	int maxMin = 0;
	
	public int getFirstIndex() {
		return firstIndex;
	}
	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}
	public int getLastIndex() {
		return lastIndex;
	}
	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}
	public int getTrend() {
		return trend;
	}
	public void setTrend(int trend) {
		this.trend = trend;
	}
	public int getDiff() {
		return diff;
	}
	public void setDiff(int diff) {
		this.diff = diff;
	}
	
	public int getMaxMin() {
		return maxMin;
	}
	public void setMaxMin(int maxMin) {
		this.maxMin = maxMin;
	}
	@Override
	public String toString(){
		return firstIndex+" "+lastIndex+" "+trend+" "+diff+" "+maxMin;
	}
	
}
