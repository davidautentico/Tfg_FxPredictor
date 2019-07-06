package drosa.experimental;

public class PositionsStats {
	
	int totalOpens = 0;
	int maxShortEntry = 0;
	int minLongEntry = 999999;
	double actualBalance = 0;
	double totalProfit$ = 0;
	double totalLoss$ = 0;
	
	public int getTotalOpens() {
		return totalOpens;
	}
	public void setTotalOpens(int totalOpens) {
		this.totalOpens = totalOpens;
	}
	public int getMaxShortEntry() {
		return maxShortEntry;
	}
	public void setMaxShortEntry(int maxShortEntry) {
		this.maxShortEntry = maxShortEntry;
	}
	public int getMinLongEntry() {
		return minLongEntry;
	}
	public void setMinLongEntry(int minLongEntry) {
		this.minLongEntry = minLongEntry;
	}
		
	public double getActualBalance() {
		return actualBalance;
	}
	public void setActualBalance(double actualBalance) {
		this.actualBalance = actualBalance;
	}
	
	public double getTotalProfit$() {
		return totalProfit$;
	}
	public void setTotalProfit$(double totalProfit$) {
		this.totalProfit$ = totalProfit$;
	}
	public double getTotalLoss$() {
		return totalLoss$;
	}
	public void setTotalLoss$(double totalLoss$) {
		this.totalLoss$ = totalLoss$;
	}
	public void reset() {
		// TODO Auto-generated method stub
		totalOpens = 0;
		maxShortEntry = 0;
		minLongEntry = 999999;
	}
	
	
	
	
	
}
