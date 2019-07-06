package drosa.simulator;

import java.util.ArrayList;

public class SimulatorResults {

	double initialBalance=0;
	double availableBalance=0;
	int totalTrades;
	double points=0;
	double profit=0;
	int totalWins=0;
	int totalLosses=0;
	double totalProfit=0;
	
	
	
	public double getInitialBalance() {
		return initialBalance;
	}


	public void setInitialBalance(double initialBalance) {
		this.initialBalance = initialBalance;
		this.availableBalance = initialBalance;
	}

	
	public double getAvailableBalance() {
		return availableBalance;
	}


	public void setAvailableBalance(double availableBalance) {
		this.availableBalance = availableBalance;
	}


	public int getTotalTrades() {
		return totalTrades;
	}


	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}


	public double getPoints() {
		return points;
	}


	public void setPoints(double points) {
		this.points = points;
	}




	public double getProfit() {
		return profit;
	}




	public void setProfit(double profit) {
		this.profit = profit;
	}

	public void openTrade(ArrayList<Trade> trades,double openValue, double spread, double limit){
		
	}

	public void closeTrade(ArrayList<Trade> trades,double closeValue){
		
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


	public double getTotalProfit() {
		return totalProfit;
	}


	public void setTotalProfit(double totalProfit) {
		this.totalProfit = totalProfit;
	}


	public void addTrade(Trade trade) {
		// TODO Auto-generated method stub
		double profit = trade.getProfit();
		if (profit>=0){
			totalWins++;
		}else{
			totalLosses++;
		}
		
		totalProfit+=profit;
		totalTrades++;
	}

}
