package drosa.apuestas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class NHLOdds2 {
	
	
	
	public static HashMap<String,MatchProbStats> computeProbabilities(ArrayList<Match> matches,int lookBack){
		
		HashMap<String,MatchProbStats>  resultStats = new HashMap<String,MatchProbStats>(); 
		
		HashMap<String,BetStats2> teamStats = new HashMap<String,BetStats2>();
		
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
			
			if (!teamStats.containsKey(hTeam)){
				teamStats.put(hTeam,new BetStats2());
			}
			if (!teamStats.containsKey(aTeam)){
				teamStats.put(aTeam,new BetStats2());
			}
			
			BetStats2 hStats = teamStats.get(hTeam);
			BetStats2 aStats = teamStats.get(aTeam);
		
			if (hStats.getResults().size()>=lookBack && aStats.getResults().size()>=lookBack){
				//int hStreak = hStats.getCurrentStreak();
				//int aStreak = aStats.getCurrentStreak();
				//obtengo la probabilidad de ganar despues de perder hStreak y aStreak
				int hProb = 5* (int) (hStats.getWinProb(lookBack)/5);
				int aProb = 5*(int) (aStats.getWinProb(lookBack)/5);
			
				//guardamos resultado
				String key = hProb+"_"+aProb;
				if (!resultStats.containsKey(key)){
					resultStats.put(key, new MatchProbStats());
				}
				MatchProbStats table = resultStats.get(key);
				table.cases++;
				if (res==1)
					table.wins++;
			}
			
						
			//añadimos estadisticas a cada equipo
			hStats.getResults().add(res);
			aStats.getResults().add(-res);
		}
		
		
		return resultStats;
	}
	
	public static void analizeTeam(ArrayList<Match> matches,String team,int lookBack){
		
		ArrayList<Integer> results = new ArrayList<Integer>();
		int wins = 0;
		int cases = 0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
			if (!hTeam.equalsIgnoreCase(team) && !aTeam.equalsIgnoreCase(team)) continue;
			
			if (hTeam.equalsIgnoreCase(team)){
				results.add(res);
			}
			if (aTeam.equalsIgnoreCase(team)){
				results.add(-res);
			}
			
			cases++;
		}
		
		//analisis de rachas
		for (int i=0;i<results.size();i++){
			
			if (i>lookBack){
				int w=0;
				int total = 0;
				for (int j=i-lookBack;j<=i-1;j++){
					int r = results.get(j);
					if (r==1) w++;
					total++;
				}
				System.out.println(w+" "+total+" "+PrintUtils.Print2dec(w*100.0/total, false));
			}
		}
	}
	
	public static HashMap<String,ArrayList<Integer>> getRachas(ArrayList<Match> matches){
		
		HashMap<String,ArrayList<Integer>> rachas = new  HashMap<String,ArrayList<Integer>>();
		
		double avg = 0;
		int cases = 0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			if (!rachas.containsKey(hTeam)){
				rachas.put(hTeam,new ArrayList<Integer>());
				rachas.get(hTeam).add(0);
			}
			if (!rachas.containsKey(aTeam)){
				rachas.put(aTeam,new ArrayList<Integer>());
				rachas.get(aTeam).add(0);
			}
			
			ArrayList<Integer> hRachas = rachas.get(hTeam);
			ArrayList<Integer> aRachas = rachas.get(aTeam);
			
			int hLastStreak = hRachas.get(hRachas.size()-1);
			int aLastStreak = aRachas.get(aRachas.size()-1);
			
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
			
			if (res==1){	
				//home = 1
				if (hLastStreak>=0){
					hRachas.set(hRachas.size()-1, hRachas.get(hRachas.size()-1)+1);
				}else{
					hRachas.add(1);
				}
				//away = -1
				if (aLastStreak<=0){
					aRachas.set(aRachas.size()-1, aRachas.get(aRachas.size()-1)-1);
				}else{
					aRachas.add(-1);
				}
			}else if (res==-1){
				//home = -1
				if (hLastStreak<=0){
					hRachas.set(hRachas.size()-1, hRachas.get(hRachas.size()-1)-1);
				}else{
					hRachas.add(-1);
				}
				//away = 1
				if (aLastStreak>=0){
					aRachas.set(aRachas.size()-1, aRachas.get(aRachas.size()-1)+1);
				}else{
					aRachas.add( 1);
				}
			}
		}
		
		return rachas;
	}

	
	public static HashMap<String,ArrayList<Integer>> getTeamMatches(ArrayList<Match> matches){
		
		 HashMap<String,ArrayList<Integer>> stats= new  HashMap<String,ArrayList<Integer>>();
		 
		 
		 for (int i=0;i<matches.size();i++){
			 
			 Match m = matches.get(i);
			 String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			if (!stats.containsKey(hTeam)){
				stats.put(hTeam,new ArrayList<Integer>());
			}
			if (!stats.containsKey(aTeam)){
				stats.put(aTeam,new ArrayList<Integer>());
			}
						
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
			stats.get(hTeam).add(res);
			stats.get(aTeam).add(-res);						
		 }
		 		 				 
		return stats;
	}
	
	
	public static double computeProbWin(HashMap<String,ArrayList<Integer>> stats,int streak){
		
		int count = 0;
		int wins = 0;
		int sumStreaks = 0;
		int totalStreaks = 0;
		ArrayList<Integer> streaks = new ArrayList<Integer>();
		for (Entry<String, ArrayList<Integer>> entry : stats.entrySet()) {
			String team = entry.getKey();
			
			ArrayList<Integer> matches = entry.getValue();
			
			int actualStreak = 0;
			for (int i=0;i<matches.size()-1;i++){
				
				int res = matches.get(i);
				
				if (res==1){
					if (actualStreak>=0) actualStreak++;
					else{
						sumStreaks += -(actualStreak);
						totalStreaks++;
						actualStreak = 1;
					}
				}
				else if (res==-1){
					if (actualStreak<=0) actualStreak--;
					else actualStreak = -1;
				}
				
				if (actualStreak==-streak){
					count++;
					if (matches.get(i+1)==1){
						wins++;
					}
				}
			}
		}
		
		System.out.println(count+" "+PrintUtils.Print2dec(wins*100.0/count, false)
			+" || "+totalStreaks+" "+PrintUtils.Print2dec(sumStreaks*1.0/totalStreaks, false)
				);
		
		if (count==0) return 0;
		else return wins*100.0/count;
	}
	
	public static void getRachasInfo(HashMap<String,ArrayList<Integer>> rachas,String ateam,boolean isNeg){
	
		double avg = 0.0;
		int cases = 0;
		ArrayList<Double> values = new ArrayList<Double>();
		for (Entry<String, ArrayList<Integer>> entry : rachas.entrySet()) {
			String team = entry.getKey();
			
			if (team.isEmpty() || !team.equalsIgnoreCase(ateam)) continue;
			
			ArrayList<Integer> stats = entry.getValue();
			
			int total = 0;
			int count = 0;
			//values.clear();
			for (int i=0;i<stats.size();i++){
				if (stats.get(i)<0 && isNeg){
					total++;
					count += -stats.get(i);
					values.add((double) -stats.get(i));					
				}else if (stats.get(i)>0 && !isNeg){
					total++;
					count += stats.get(i);
					values.add((double) stats.get(i));					
				}
			}
			
			avg += count*1.0/total;
			cases++;
			//values.add(count*1.0/total);
			
			//System.out.println(team+" "+PrintUtils.Print2dec(count*1.0/total, false));
		}
		double avgMedia = avg*1.0/cases;
		double dt = Math.sqrt(MathUtils.variance(values));
		String header = String.format("%22s",ateam)+" "+PrintUtils.Print2dec(avgMedia, false)+" "+PrintUtils.Print2dec(dt, false);
		/*System.out.println(PrintUtils.Print2dec(avg*1.0/cases, false)
				+" "+PrintUtils.Print2dec(dt, false)
				);*/
		MathUtils.summary_complete(header, values);
	}

	public static void main(String[] args) {
		
		String folder = "C:\\nhl\\";
		
		/*for (int y=2000;y<=2000;y++){
			ArrayList<Match> matches = NHL.readFromDisk(folder, y, y+16,3,0);
			HashMap<String,ArrayList<Integer>> teamMatches = NHLOdds2.getTeamMatches(matches);
			for (int streak=1;streak<=10;streak++){
				double prob = NHLOdds2.computeProbWin(teamMatches,streak);
				//System.out.println(PrintUtils.Print2dec(prob, false));
			}
		}*/
		ArrayList<Match> matches = NHL.readFromDisk(folder, 2016, 2016,3,0);
		HashMap<String,String> teams = NHL.extractTeams(matches);
		for (Entry<String, String> entry : teams.entrySet()) {
			String team = entry.getKey();
			for (int y=2016;y<=2016;y++){
				ArrayList<Match> matches1 = NHL.readFromDisk(folder, y, y+2,3,0);				
			
				HashMap<String,ArrayList<Integer>> rachas = NHLOdds2.getRachas(matches1);
				//String team = "Los Angeles Kings";
				//team = "";
				NHLOdds2.getRachasInfo(rachas,team,false);
			}
		}
		
		
		//NHLOdds2.analizeTeam(matches, team, 50);
		
		/*ArrayList<Match> matches = NHL.readFromDisk(folder, 2016, 2016,3,0);
		HashMap<String,String> teams = NHL.extractTeams(matches);
		//NHL.printMap(teams);
		
		HashMap<String,MatchProbStats> stats = NHLOdds2.computeProbabilities(matches, 60);
		
		for (int s1=10;s1<=70;s1++){
			for (int s2=30;s2<=30;s2++){
				String key = s1+"_"+s2;
				
				MatchProbStats mStats = stats.get(key);
				
				if (mStats==null) continue;
				System.out.println(s1+" "+s2+" || "+mStats.getCases()+" "+PrintUtils.Print2dec(mStats.getWinPer(), false));
			}
		}*/
		
		/*ArrayList<Match> matches1 = NHL.readFromDisk(folder, 2000, 2013,3,0);
		ArrayList<Match> matches2 = NHL.readFromDisk(folder, 2014, 2016,3,0);
		HashMap<String,MatchProbStats> stats1 = NHLOdds2.computeProbabilities(matches1, 30);
		HashMap<String,MatchProbStats> stats2 = NHLOdds2.computeProbabilities(matches2, 30);
		
		int s1 = 50;
		int s2 = 50;
		String key = s1+"_"+s2;
		MatchProbStats mStats1 = stats1.get(key);
		MatchProbStats mStats2 = stats2.get(key);
		System.out.println(s1+" "+s2+" || "+mStats1.getCases()+" "+PrintUtils.Print2dec(mStats1.getWinPer(), false));
		System.out.println(s1+" "+s2+" || "+mStats2.getCases()+" "+PrintUtils.Print2dec(mStats2.getWinPer(), false));*/
		
		System.out.println("FINALIZADO");
	}
}
