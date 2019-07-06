package drosa.apuestas;

import java.util.ArrayList;

public class TestStats {
	int totalBets = 0;
	int totalAmount = 0;
	int totalWins = 0;
	double totalProfit=0;
	double totalAvgCuotas = 0;
	double totalWinsU = 0.0;
	double totalLossesU = 0.0;
	double totalAvgNeg = 0.0;
	
	ArrayList<Bet> bets = new ArrayList<Bet>();
	public int getTotalBets() {
		return totalBets;
	}
	public void setTotalBets(int totalBets) {
		this.totalBets = totalBets;
	}
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getTotalProfit() {
		return totalProfit;
	}
	public void setTotalProfit(double totalProfit) {
		this.totalProfit = totalProfit;
	}
	public int getTotalWins() {
		return totalWins;
	}
	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}
	public double getTotalAvgCuotas() {
		return totalAvgCuotas;
	}
	public void setTotalAvgCuotas(double totalAvgCuotas) {
		this.totalAvgCuotas = totalAvgCuotas;
	}
	public ArrayList<Bet> getBets() {
		return bets;
	}
	public void setBets(ArrayList<Bet> bets) {
		this.bets = bets;
	}
	public double getTotalWinsU() {
		return totalWinsU;
	}
	public void setTotalWinsU(double totalWinsU) {
		this.totalWinsU = totalWinsU;
	}
	public double getTotalLossesU() {
		return totalLossesU;
	}
	public void setTotalLossesU(double totalLossesU) {
		this.totalLossesU = totalLossesU;
	}
	public double getTotalAvgNeg() {
		return totalAvgNeg;
	}
	public void setTotalAvgNeg(double totalAvgNeg) {
		this.totalAvgNeg = totalAvgNeg;
	}
	
	
	
}
