package drosa.strategies;

public class MonteCarloResult {
	
	double avgDDHigher95per=0.0; //average of DD values higher than 95% of cases
	double avgMaxDD=0.0;         //average Maximum DD
	double avgBalance=0.0;       //average final balance (% change)
	double worstDD=0.0;          //worst case DD scenario
	int    worstConLosses=0;	 //worst case consecutive losses
	int    worstConWins=0;       //worst case consecutive wins
	int 	bestConWins=0;
	double avgCompYearProfit=0;  //average compounded yearly profit
	double AARtoWorstCase=0;     //AAR to Worst Case Ratio
	double AARtoDD95=0;          //AAR to DD95% Avg Ratio
	
	public double getAvgDDHigher95per() {
		return avgDDHigher95per;
	}

	public void setAvgDDHigher95per(double avgDDHigher95per) {
		this.avgDDHigher95per = avgDDHigher95per;
	}

	public double getAvgMaxDD() {
		return avgMaxDD;
	}

	public void setAvgMaxDD(double avgMaxDD) {
		this.avgMaxDD = avgMaxDD;
	}

	public double getAvgBalance() {
		return avgBalance;
	}

	public void setAvgBalance(double avgBalance) {
		this.avgBalance = avgBalance;
	}

	public double getWorstDD() {
		return worstDD;
	}

	public void setWorstDD(double worstDD) {
		this.worstDD = worstDD;
	}

	public int getWorstConLosses() {
		return worstConLosses;
	}

	public void setWorstConLosses(int worstConLosses) {
		this.worstConLosses = worstConLosses;
	}

	public int getWorstConWins() {
		return worstConWins;
	}

	public void setWorstConWins(int worstConWins) {
		this.worstConWins = worstConWins;
	}

	public double getAvgCompYearProfit() {
		return avgCompYearProfit;
	}

	public void setAvgCompYearProfit(double avgCompYearProfit) {
		this.avgCompYearProfit = avgCompYearProfit;
	}

	public double getAARtoWorstCase() {
		return AARtoWorstCase;
	}

	public void setAARtoWorstCase(double aARtoWorstCase) {
		AARtoWorstCase = aARtoWorstCase;
	}

	public double getAARtoDD95() {
		return AARtoDD95;
	}

	public void setAARtoDD95(double aARtoDD95) {
		AARtoDD95 = aARtoDD95;
	}

	
	public int getBestConWins() {
		// TODO Auto-generated method stub
		return bestConWins;
	}

	public void setBestConWins(int bestConWins) {
		this.bestConWins = bestConWins;
	}
	
	

}
