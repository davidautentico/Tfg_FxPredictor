package drosa.strategies;

import drosa.utils.PrintUtils;

public class PipRiskStat {

	int interval=0;;
	int total=0;
	double pipsGained=0;
	
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public double getPipsGained() {
		return pipsGained;
	}
	public void setPisGained(double pipsGained) {
		this.pipsGained = pipsGained;
	}
	public void addTotal(int amount){
		this.total+=amount;
	}
	public void addPips(double amount){
		this.pipsGained+=amount;
	}
	public void printInfo() {
		// TODO Auto-generated method stub
		System.out.println("interval total pipsgained pips/risk "+
				total+" "+PrintUtils.Print(this.pipsGained/this.total,2));
	}
	
	
}
