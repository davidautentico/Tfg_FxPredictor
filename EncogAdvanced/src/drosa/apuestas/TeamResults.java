package drosa.apuestas;

import java.util.ArrayList;

public class TeamResults {

	String team="";
	ArrayList<Integer> results = new ArrayList<Integer>();//siempre resultado del de casa
	
	
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public ArrayList<Integer> getResults() {
		return results;
	}
	public void setResults(ArrayList<Integer> results) {
		this.results = results;
	}
	
	public static ArrayList<TeamResults> calculateTeamResultsNBA(ArrayList<NBAMatch> matches){
		ArrayList<TeamResults> teamResults = new ArrayList<TeamResults>();
		TeamResults tres = null;
		for (int i=0;i<matches.size();i++){
			NBAMatch match  = matches.get(i);
			String homeTeam = match.getHomeTeam();
			String awayTeam = match.getAwayTeam();
			int homeScore   = match.getHomeScore();
			int awayScore   = match.getAwayScore();
			
			int resH = 1;
			int resA = 2;
			if (homeScore>awayScore){
				resH = 1;
				resA = -2;
			}
			if (homeScore<awayScore){
				resH = -1;
				resA = 2;
			}
			int idx = findTeam(teamResults,homeTeam);
			if (idx<0){
				tres = new TeamResults();
				tres.setTeam(homeTeam);
				tres.getResults().add(resH);
				teamResults.add(tres);
			}else{
				teamResults.get(idx).getResults().add(resH);
			}
			
			idx = findTeam(teamResults,awayTeam);
			if (idx<0){
				tres = new TeamResults();
				tres.setTeam(awayTeam);
				tres.getResults().add(resA);
				teamResults.add(tres);
			}else{
				teamResults.get(idx).getResults().add(resA);
			} 
			
		}
		
		return teamResults;
	}
	
	public static ArrayList<TeamResults> calculateTeamResultsOddNBA(ArrayList<OddPortal> matches){
		ArrayList<TeamResults> teamResults = new ArrayList<TeamResults>();
		TeamResults tres = null;
		for (int i=0;i<matches.size();i++){
			OddPortal match  = matches.get(i);
			String homeTeam = match.getHomeTeam();
			String awayTeam = match.getAwayTeam();
			int homeScore   = match.getHomeScore();
			int awayScore   = match.getAwayScore();
			
			int resH = 1;
			int resA = 2;
			if (homeScore>awayScore){
				resH = 1;
				resA = -2;
			}
			if (homeScore<awayScore){
				resH = -1;
				resA = 2;
			}
			int idx = findTeam(teamResults,homeTeam);
			if (idx<0){
				tres = new TeamResults();
				tres.setTeam(homeTeam);
				tres.getResults().add(resH);
				teamResults.add(tres);
			}else{
				teamResults.get(idx).getResults().add(resH);
			}
			
			idx = findTeam(teamResults,awayTeam);
			if (idx<0){
				tres = new TeamResults();
				tres.setTeam(awayTeam);
				tres.getResults().add(resA);
				teamResults.add(tres);
			}else{
				teamResults.get(idx).getResults().add(resA);
			} 
			
		}
		
		return teamResults;
	}

	public static int findTeam(ArrayList<TeamResults> teamResults,String teamToFind){
		
		for (int i=0;i<teamResults.size();i++){
			if (teamResults.get(i).getTeam().equalsIgnoreCase(teamToFind)) return i;
		}
		
		return -1;
	}
	
	public String toString(){
		String res = this.team;
		for (int i=0;i<=results.size()-1;i++){
			res+=" "+String.valueOf(results.get(i));
		}
		
		return res;
	}
	
	public String toString(int lastN){
		String res = "";
		int begin = results.size()-lastN;
		if (begin<0) begin = 0;
		for (int i=begin;i<=results.size()-1;i++){
			res+=" "+String.valueOf(results.get(i));
		}
		
		return res;
	}
	
}
