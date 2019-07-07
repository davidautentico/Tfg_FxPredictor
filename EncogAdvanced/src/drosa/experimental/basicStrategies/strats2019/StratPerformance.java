package drosa.experimental.basicStrategies.strats2019;

public class StratPerformance {
	
	double pf = 0.0;
	double pfYears = 0.0;
	int years = 0;
	double avgPips  = 0.0;
	double maxDD = 0;
	double winPips = 0;
	double lostPips = 0;
	double trades = 0;
	
	public double getPf() {
		return pf;
	}
	public void setPf(double pf) {
		this.pf = pf;
	}
	public int getYears() {
		return years;
	}
	public void setYears(int years) {
		this.years = years;
	}
	public double getAvgPips() {
		return avgPips;
	}
	public void setAvgPips(double avgPips) {
		this.avgPips = avgPips;
	}
	public double getPfYears() {
		return pfYears;
	}
	public void setPfYears(double pfYears) {
		this.pfYears = pfYears;
	}
	public double getMaxDD() {
		return maxDD;
	}
	public void setMaxDD(double maxDD) {
		this.maxDD = maxDD;
	}
	public void reset() {
		pf = 0.0;
		pfYears = 0.0;
		years = 0;
		avgPips  = 0.0;
		 maxDD = 0;		
		winPips = 0;
		lostPips = 0;
		trades = 0;
	}
	public double getWinPips() {
		return winPips;
	}
	public void setWinPips(double winPips) {
		this.winPips = winPips;
	}
	public double getLostPips() {
		return lostPips;
	}
	public void setLostPips(double lostPips) {
		this.lostPips = lostPips;
	}
	public double getTrades() {
		return trades;
	}
	public void setTrades(double trades) {
		this.trades = trades;
	}
	
	
	

}
