package drosa.phil.strategy;

import java.util.ArrayList;

public class StrategyResult {

	int tp = 0;
	int sl = 0;
	int totalTrades = 0;
	double winsPer = 0;
	int bestTrack = 0;
	ArrayList<StrategyTrade> trades = new ArrayList<StrategyTrade>();
	
	
	
	public int getTp() {
		return tp;
	}



	public void setTp(int tp) {
		this.tp = tp;
	}



	public int getSl() {
		return sl;
	}



	public void setSl(int sl) {
		this.sl = sl;
	}



	public int getTotalTrades() {
		return totalTrades;
	}



	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}



	public double getWinsPer() {
		return winsPer;
	}



	public void setWinsPer(double winsPer) {
		this.winsPer = winsPer;
	}



	public int getBestTrack() {
		return bestTrack;
	}



	public void setBestTrack(int bestTrack) {
		this.bestTrack = bestTrack;
	}



	public ArrayList<StrategyTrade> getTrades() {
		return trades;
	}



	public void setTrades(ArrayList<StrategyTrade> trades) {
		this.trades = trades;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
