package drosa.finances;

public class Volume {
	String symbol;
	long value;
	int bars;
	
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public int getBars() {
		return bars;
	}
	public void setBars(int bars) {
		this.bars = bars;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	
	
}
