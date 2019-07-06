package drosa.apuestas;

import java.util.ArrayList;

import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class BetStats {
	
	ArrayList<Integer> streaks = new ArrayList<Integer>();
	
	int cases = 0;
	int wins = 0;
	int losses = 0;
	int maxWins = 0;
	int maxLosses = 0;
	int bestStreak = 0;
	int worseStreak = 0;
	int lastStreak = 0;
	double profit = 0;
	double risk = 0;
	
	double avgPos = 0;
	double avgNeg = 0;
	double desvP = 0;
	double desvN = 0;
	double avgGlobal = 0;
	
	
	
	public double getProfit() {
		return profit;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	public double getRisk() {
		return risk;
	}
	public void setRisk(double risk) {
		this.risk = risk;
	}
	public double getAvgGlobal() {
		return avgGlobal;
	}
	public void setAvgGlobal(double avgGlobal) {
		this.avgGlobal = avgGlobal;
	}
	public int getLastStreak() {
		return lastStreak;
	}
	public void setLastStreak(int lastStreak) {
		this.lastStreak = lastStreak;
	}
	public int getLosses() {
		return losses;
	}
	public void setLosses(int losses) {
		this.losses = losses;
	}
	public int getBestStreak() {
		return bestStreak;
	}
	public void setBestStreak(int bestStreak) {
		this.bestStreak = bestStreak;
	}
	public int getWorseStreak() {
		return worseStreak;
	}
	public void setWorseStreak(int worseStreak) {
		this.worseStreak = worseStreak;
	}
	public int getCases() {
		return cases;
	}
	public void setCases(int cases) {
		this.cases = cases;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getMaxWins() {
		return maxWins;
	}
	public void setMaxWins(int maxWins) {
		this.maxWins = maxWins;
	}
	public int getMaxLosses() {
		return maxLosses;
	}
	public void setMaxLosses(int maxLosses) {
		this.maxLosses = maxLosses;
	}
	public ArrayList<Integer> getStreaks() {
		return streaks;
	}
	public void setStreaks(ArrayList<Integer> streaks) {
		this.streaks = streaks;
	}		
	public double getAvgPos() {
		return avgPos;
	}
	public void setAvgPos(double avgPos) {
		this.avgPos = avgPos;
	}
	public double getAvgNeg() {
		return avgNeg;
	}
	public void setAvgNeg(double avgNeg) {
		this.avgNeg = avgNeg;
	}
	public double getDesvP() {
		return desvP;
	}
	public void setDesvP(double desvP) {
		this.desvP = desvP;
	}
	public double getDesvN() {
		return desvN;
	}
	public void setDesvN(double desvN) {
		this.desvN = desvN;
	}
	
	public void recalculateStreak(){
		
		//ArrayList<Integer> streak = betStats.getStreaks();
		ArrayList<Integer> streakPos = new ArrayList<Integer> ();
		ArrayList<Integer> streakNeg = new ArrayList<Integer> ();
		
		int begin = 0;
		/*if (streaks.size()>=15){
			begin = streaks.size()-15; 
		}*/
		for (int i=0;i<streaks.size();i++){
			if (streaks.get(i)>=1){
			streakPos.add(streaks.get(i));
			}else if (streaks.get(i)<=-1){
				streakNeg.add(-streaks.get(i));
			}
		}
		
		avgPos = MathUtils.average(streakPos);
		avgNeg = MathUtils.average(streakNeg);		
		desvP = Math.sqrt(MathUtils.variance(streakPos));
		desvN = Math.sqrt(MathUtils.variance(streakNeg));
	}
	
	public String getOddsStr(){
		return PrintUtils.Print2dec(avgPos,false)+" "+PrintUtils.Print2dec(desvP,false)
				+" / "+PrintUtils.Print2dec(avgNeg,false)+" "+PrintUtils.Print2dec(desvN,false);
	}
	public String streakToString() {
		// TODO Auto-generated method stub
		String str="";
		for (int i=0;i<streaks.size();i++){
			str+=" "+streaks.get(i);
		}
		return str;
	}
	public String printStats() {
		// TODO Auto-generated method stub
		return PrintUtils.Print2dec(avgNeg,false)+" / "+PrintUtils.Print2dec(avgPos,false);
	}
	public void addBet(int win, double profit,double risk) {
		if (win==1) this.wins++;
		else this.losses++;
		//this.wins += win;
		
		this.profit += profit;
		this.risk += risk;
		this.cases++;
	}

}
