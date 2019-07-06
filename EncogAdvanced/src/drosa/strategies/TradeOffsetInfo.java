package drosa.strategies;

import drosa.utils.PrintUtils;

public class TradeOffsetInfo {
		int interval=0;
		int offset=0;
		int totalTrades=0;
		int totalWins=0;
		int totalLosses=0;
		double pipsGained=0;
		
		
		public int getInterval() {
			return interval;
		}
		public void setInterval(int interval) {
			this.interval = interval;
		}
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}
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
		public double getPipsGained() {
			return pipsGained;
		}
		public void setPipsGained(double pipsGained) {
			this.pipsGained = pipsGained;
		}
		
		public void addTotalTrades(int amount){
			this.totalTrades+=amount;
		}
		
		public void addTotalWins(int amount){
			this.totalWins+=amount;
		}
		
		public void addTotalLosses(int amount){
			this.totalLosses+=amount;
		}
		
		public void addPipsGained(double amount){
			this.pipsGained+=amount;
		}
		
		public double getWinPercentage(){
			return this.totalWins*100.0/this.totalTrades;
		}
		
		public double getLossPercentage(){
			return this.totalLosses*100.0/this.totalTrades;
		}
		
		public void printInfo(){
			System.out.println(offset+" "+totalTrades
					+" "+PrintUtils.Print(getWinPercentage())
					+" "+PrintUtils.Print(getLossPercentage())
					+" "+PrintUtils.Print(this.pipsGained));
		}
}
