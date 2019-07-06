package drosa.simulator;

public class SimAction {

	SimActionType simActionType;
	int shares;
	double stopLoss=0;
	double takeProfit=0;
	

	public SimActionType getSimActionType() {
		return simActionType;
	}



	public void setSimActionType(SimActionType simActionType) {
		this.simActionType = simActionType;
	}



	public int getShares() {
		return shares;
	}



	public void setShares(int shares) {
		this.shares = shares;
	}


	

	public double getStopLoss() {
		return stopLoss;
	}



	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}



	public double getTakeProfit() {
		return takeProfit;
	}



	public void setTakeProfit(double takeProfit) {
		this.takeProfit = takeProfit;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
