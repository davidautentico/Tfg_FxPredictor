package drosa.strategies;

public class DIBSObject {

	int rr;
	String symbol;
	int day=-1;
	int hourL;
	int hourH;
	double umbralL;
	double umbralH;
	int totalTrades;
	int numTrades;
	double percentage;
	double result;
	public int getRr() {
		return rr;
	}
	public void setRr(int rr) {
		this.rr = rr;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHourL() {
		return hourL;
	}
	public void setHourL(int hourL) {
		this.hourL = hourL;
	}
	public int getHourH() {
		return hourH;
	}
	public void setHourH(int hourH) {
		this.hourH = hourH;
	}
	public double getUmbralL() {
		return umbralL;
	}
	public void setUmbralL(double umbralL) {
		this.umbralL = umbralL;
	}
	public double getUmbralH() {
		return umbralH;
	}
	public void setUmbralH(double umbralH) {
		this.umbralH = umbralH;
	}
	public int getTotalTrades() {
		return totalTrades;
	}
	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}
	public int getNumTrades() {
		return numTrades;
	}
	public void setNumTrades(int numTrades) {
		this.numTrades = numTrades;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
	
	
}
