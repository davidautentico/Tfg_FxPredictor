package drosa.experimental.ticks;

import drosa.utils.PrintUtils;

public class TickStats {
	
	
	int wins = 0;
	int losses = 0;
	int winPips = 0;
	int lostPips = 0;
	
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getLosses() {
		return losses;
	}
	public void setLosses(int losses) {
		this.losses = losses;
	}
	public int getWinPips() {
		return winPips;
	}
	public void setWinPips(int winPips) {
		this.winPips = winPips;
	}
	public int getLostPips() {
		return lostPips;
	}
	public void setLostPips(int lostPips) {
		this.lostPips = lostPips;
	}
	
	public String getReport(String header){
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/trades;
		
		return header+" || "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				;
	}

}
