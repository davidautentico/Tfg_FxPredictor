package drosa.experimental;

import drosa.utils.PrintUtils;

public class SimulatedResults {
	
	double balance = 0;
	double maxBalance =0;
	double maxDD = 0;
	
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getMaxDD() {
		return maxDD;
	}
	public void setMaxDD(double maxDD) {
		this.maxDD = maxDD;
	}
	public void setMaxBalance(double maxBalance) {
		// TODO Auto-generated method stub
		this.maxBalance = maxBalance;
	}
	
	public String toString(){			
		return PrintUtils.Print2dec2(balance, true)+" "+PrintUtils.Print2dec2(maxBalance, true)+" "+PrintUtils.Print2dec(maxDD,false, 2);		
	}
	
}
