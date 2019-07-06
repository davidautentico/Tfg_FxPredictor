package drosa.experimental;

import java.util.ArrayList;

import drosa.strategies.auxiliar.Position;

public class StrategyResultEx {

	int totalTrades = 0;
	int totalWins = 0;
	int totalDontKnow = 0;
	int totalLosses = 0;
	double percentWin = 0;
	double winPips = 0;
	double lossPips = 0;
	double profitFactor = 0;
	int bestTrack = 0;
	int worstTrack = 0;
	int maxOpenPosition = 0;
	double finalCapital = 0;
	String sequenceWinLoss = "";
	ArrayList<Double> sequencePips = new ArrayList<Double>();
	ArrayList<Position> positions = new ArrayList<Position>();
	double expectancy = 0;
	double kelly = 0;
	double testRisk=0;
	double tradeExpectancyTest = 0;
	
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
	
	public int getTotalDontKnow() {
		return totalDontKnow;
	}
	public void setTotalDontKnow(int totalDontKnow) {
		this.totalDontKnow = totalDontKnow;
	}
	public double getPercentWin() {
		return percentWin;
	}
	public void setPercentWin(double percentWin) {
		this.percentWin = percentWin;
	}
	public double getWinPips() {
		return winPips;
	}
	public void setWinPips(double winPips) {
		this.winPips = winPips;
	}
	public double getLossPips() {
		return lossPips;
	}
	public void setLossPips(double lossPips) {
		this.lossPips = lossPips;
	}
	public double getProfitFactor() {
		return profitFactor;
	}
	public void setProfitFactor(double profitFactor) {
		this.profitFactor = profitFactor;
	}
	public int getBestTrack() {
		return bestTrack;
	}
	public void setBestTrack(int bestTrack) {
		this.bestTrack = bestTrack;
	}
	public int getWorstTrack() {
		return worstTrack;
	}
	public void setWorstTrack(int worstTrack) {
		this.worstTrack = worstTrack;
	}
	public int getMaxOpenPosition() {
		return maxOpenPosition;
	}
	public void setMaxOpenPosition(int maxOpenPosition) {
		this.maxOpenPosition = maxOpenPosition;
	}
	public double getFinalCapital() {
		return finalCapital;
	}
	public void setFinalCapital(double finalCapital) {
		this.finalCapital = finalCapital;
	}
	public String getSequenceWinLoss() {
		return sequenceWinLoss;
	}
	public void setSequenceWinLoss(String sequenceWinLoss) {
		this.sequenceWinLoss = sequenceWinLoss;
	}
	public ArrayList<Double> getSequencePips() {
		return sequencePips;
	}
	public void setSequencePips(ArrayList<Double> sequencePips) {
		this.sequencePips = sequencePips;
	}
	public ArrayList<Position> getPositions() {
		return positions;
	}
	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
	}
	public double getExpectancy() {
		return expectancy;
	}
	public void setExpectancy(double expectancy) {
		this.expectancy = expectancy;
	}
	public double getKelly() {
		return kelly;
	}
	public void setKelly(double kelly) {
		this.kelly = kelly;
	}
	public double getTestRisk() {
		return testRisk;
	}
	public void setTestRisk(double testRisk) {
		this.testRisk = testRisk;
	}
	public double getTradeExpectancyTest() {
		return tradeExpectancyTest;
	}
	public void setTradeExpectancyTest(double tradeExpectancyTest) {
		this.tradeExpectancyTest = tradeExpectancyTest;
	}
	
	
	
}
