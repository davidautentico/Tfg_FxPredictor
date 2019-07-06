package drosa.apuestas;

import drosa.utils.PrintUtils;

public class Bet {

		Match m = null;
		
		int bet = 0;
		int win = 0;
		double odds = 0;
		double profit = 0;
		
		public Match getM() {
			return m;
		}
		public void setM(Match m) {
			this.m = m;
		}
		public int getBet() {
			return bet;
		}
		public void setBet(int bet) {
			this.bet = bet;
		}
		public int getWin() {
			return win;
		}
		public void setWin(int win) {
			this.win = win;
		}
		
		
		
		public double getOdds() {
			return odds;
		}
		public void setOdds(double odds) {
			this.odds = odds;
		}
		public double getProfit() {
			return profit;
		}
		public void setProfit(double profit) {
			this.profit = profit;
		}
		public String toString(){						
			return m.homeTeam+","+m.awayTeam+","+bet+","+PrintUtils.Print2dec(odds, false)+","+PrintUtils.Print2dec(profit, false);
		}
}
