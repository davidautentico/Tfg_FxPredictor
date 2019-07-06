package drosa.experimental.CoreStrategies;

import drosa.utils.PrintUtils;

public class CoreStats {

	int totalTrades = 0;
	int totalWins = 0;
	int totalLosses = 0;
	double avg = 0.0;
	int totalWinPips  = 0;
	int totalLostPips = 0;
	int totalMaxProfit = 0;
	int totalMaxAdverseBeforeProfit = 0;
	double pf = 0.0;
	
	public int getTotalTrades() {
		return totalTrades;
	}
	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}
	public int getTotalWins() {
		return totalWins;
	}
	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}
	public int getTotalLosses() {
		return totalLosses;
	}
	public void setTotalLosses(int totalLosses) {
		this.totalLosses = totalLosses;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	public int getTotalWinPips() {
		return totalWinPips;
	}
	public void setTotalWinPips(int totalWinPips) {
		this.totalWinPips = totalWinPips;
	}
	public int getTotalLostPips() {
		return totalLostPips;
	}
	public void setTotalLostPips(int totalLostPips) {
		this.totalLostPips = totalLostPips;
	}
	public double getPf() {
		return pf;
	}
	public void setPf(double pf) {
		this.pf = pf;
	}
	
	public int getTotalMaxProfit() {
		return totalMaxProfit;
	}
	public void setTotalMaxProfit(int totalMaxProfit) {
		this.totalMaxProfit = totalMaxProfit;
	}
	public int getTotalMaxAdverseBeforeProfit() {
		return totalMaxAdverseBeforeProfit;
	}
	public void setTotalMaxAdverseBeforeProfit(int totalMaxAdverseBeforeProfit) {
		this.totalMaxAdverseBeforeProfit = totalMaxAdverseBeforeProfit;
	}
	
	public String toString(){
		return totalTrades
				+" "+PrintUtils.Print2dec(totalMaxProfit*0.1/totalTrades, false)
				+" "+PrintUtils.Print2dec(this.totalMaxAdverseBeforeProfit*0.1/totalTrades, false);
		
	}
	
	public void addPositionStats(PositionCore positionCore) {
		this.totalTrades++;
		this.totalMaxProfit += positionCore.maxProfit;
		this.totalMaxAdverseBeforeProfit += positionCore.maxAdverseBeforeProfit;		
	}
	public void reset() {
		this.totalTrades = 0;
		this.pf = 0;
		this.totalWins = 0;
		this.totalLosses = 0;
		this.totalWinPips = 0;
		this.totalLostPips = 0;
	}
	
	
}
