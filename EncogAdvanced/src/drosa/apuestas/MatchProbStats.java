package drosa.apuestas;

public class MatchProbStats {
	
	int prob1 = 0;
	int prob2 = 0;
	
	int cases = 0;
	int wins = 0;//1=win
	
	public int getProb1() {
		return prob1;
	}
	public void setProb1(int prob1) {
		this.prob1 = prob1;
	}
	public int getProb2() {
		return prob2;
	}
	public void setProb2(int prob2) {
		this.prob2 = prob2;
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
	
	
	public double getWinPer(){
		return wins*100.0/cases;
	}
	

}
