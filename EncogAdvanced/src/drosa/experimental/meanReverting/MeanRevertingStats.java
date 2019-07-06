package drosa.experimental.meanReverting;

public class MeanRevertingStats {
	double pf = 0.0;
	double avgPips = 0.0;
	int totalTrades = 0;
	int wins = 0;
	int losses = 0;
	public double getPf() {
		return pf;
	}
	public void setPf(double pf) {
		this.pf = pf;
	}
	public double getAvgPips() {
		return avgPips;
	}
	public void setAvgPips(double avgPips) {
		this.avgPips = avgPips;
	}
	public int getTotalTrades() {
		return totalTrades;
	}
	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getLosses() {
		return losses;
	}
	public void setLosses(int losses) {
		this.losses = losses;
	}
	
	
	
	
}
