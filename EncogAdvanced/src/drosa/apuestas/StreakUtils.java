package drosa.apuestas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class StreakUtils {
	
	public static void calculateGroupStreakTeam(ArrayList<Match> matches,HashMap<String,ArrayList<Integer>> teamStats,boolean debug){
		
		int begin = 0;
		for (int i=begin;i<matches.size();i++){
			Match m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
			
			//if (hTeam.contains(team)){	
			ArrayList<Integer> tRes =  null;
			
			String team = hTeam;
			if (!teamStats.containsKey(team)){
				teamStats.put(team, new ArrayList<Integer>());
				tRes =  teamStats.get(team);	
				tRes.add(0);
			}
			tRes =  teamStats.get(team);				
			int lastStreak = tRes.get(tRes.size()-1);
			if (res==1){
				if (lastStreak>=0){
					tRes.set(tRes.size()-1,lastStreak+1);
				}else{
					tRes.add(1);
				}
			}else if (res==-1){
				if (lastStreak<=0){
					tRes.set(tRes.size()-1,lastStreak-1);
				}else{
					tRes.add(-1);
				}
			}
				
			//AWAY
			team = aTeam;
			if (!teamStats.containsKey(team)){
				teamStats.put(team, new ArrayList<Integer>());
				tRes =  teamStats.get(team);	
				tRes.add(0);
			}
			tRes =  teamStats.get(team);				
			lastStreak = tRes.get(tRes.size()-1);
			if (res==-1){
				if (lastStreak>=0){
					tRes.set(tRes.size()-1,lastStreak+1);
				}else{
					tRes.add(1);
				}
			}else if (res==1){
				if (lastStreak<=0){
					tRes.set(tRes.size()-1,lastStreak-1);
				}else{
					tRes.add(-1);
				}
			}
		}
	}
	
	public static void calculateStreaksTeam(ArrayList<Match> matches,HashMap<String,BetStats> teamBetStats,String team,boolean debug){
		
		HashMap<String,ArrayList<Integer>> teamResults = new HashMap<String,ArrayList<Integer>>(); 
		int begin = 0;
		//int begin = matches.size()-400;
		if (begin<=0) begin =0;
		
		if (debug){
			System.out.println("matches: "+matches.size()+" "+begin);
		}
		for (int i=begin;i<matches.size();i++){
			Match m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			if (!hTeam.equalsIgnoreCase(team) && !aTeam.equalsIgnoreCase(team)) continue; 
			
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
			
			if (hTeam.contains(team)){
				team = hTeam;
				if (!teamResults.containsKey(team)){
					teamResults.put(team, new ArrayList<Integer>());
				}
				ArrayList<Integer> tRes = teamResults.get(team);
				tRes.add(res);
			}
			
			if (aTeam.contains(team)){
				team = aTeam;
				if (!teamResults.containsKey(team)){
					teamResults.put(team, new ArrayList<Integer>());
				}
				ArrayList<Integer> tRes = teamResults.get(team);
				tRes.add(-res);
			}		
			
			if (!teamBetStats.containsKey(team)){
				teamBetStats.put(team, new BetStats());
			}
						
			BetStats stats = teamBetStats.get(team);
			ArrayList<Integer> tRes = teamResults.get(team);			
			calculateStreaksR(tRes,stats,debug);
		}
	}
	
	public static void calculateStreaksM(ArrayList<Match> matches,HashMap<String,BetStats> teamBetStats,boolean debug){
		
		HashMap<String,ArrayList<Integer>> teamResults = new HashMap<String,ArrayList<Integer>>(); 
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
			
			if (!teamResults.containsKey(hTeam)){
				teamResults.put(hTeam, new ArrayList<Integer>());
			}
			
			if (!teamResults.containsKey(aTeam)){
				teamResults.put(aTeam, new ArrayList<Integer>());
			}
			
			ArrayList<Integer> hRes = teamResults.get(hTeam);
			ArrayList<Integer> aRes = teamResults.get(aTeam);
			hRes.add(res);
			aRes.add(-res);
			
			if (!teamBetStats.containsKey(hTeam)){
				teamBetStats.put(hTeam, new BetStats());
			}
			
			if (!teamBetStats.containsKey(aTeam)){
				teamBetStats.put(aTeam, new BetStats());
			}
			
			//BetStats hStats = teamBetStats.get(hTeam);
			//BetStats aStats = teamBetStats.get(aTeam);			
		}
		
		for (Entry<String,ArrayList<Integer>> entry : teamResults.entrySet()) {
			String team = entry.getKey();
			
			if (debug)
				System.out.println("team: "+team);
			
			if (team.equalsIgnoreCase("Arizona Coyotes")){
				//debug= true;
			}
			BetStats stats = teamBetStats.get(team);
			calculateStreaksR(teamResults.get(team),stats,debug);

		}

	}
	
	
	public static void calculateStreaksR(ArrayList<Integer> results,BetStats betStats,boolean debug){
		
		ArrayList<Streak> pos = new ArrayList<Streak>();
		ArrayList<Streak> neg = new ArrayList<Streak>();
		
		int actualStreak = 0;
		int totalPos = 0;
		int totalNeg = 0;
		int totalPosRev = 0;
		int totalNegRev = 0;
		int totalGlobalRev = 0;
		int totalGlobal = 0;
		int begin = 1;
		begin = results.size()-20;
		int maxLosses = 0;
		int maxWins = 0;
		if (begin<=1) begin = 1;
		for (int i=begin;i<results.size();i++){
			int res = results.get(i);
			int res1 = results.get(i-1);

			if (res1==1){
				if (actualStreak>=0){
					actualStreak++;
					if (actualStreak>=maxWins) maxWins = actualStreak;					
				}
				else{
					actualStreak = 1;
				}
			}else{
				if (actualStreak<=0){
					actualStreak--;
					if (actualStreak<=maxLosses) maxLosses = actualStreak;					
				}
				else{
					actualStreak = -1;
				}
			}
			
			if (res1!=res){
				totalGlobalRev+=i;
			}
			if (res1==1){
				if (res==-1){
					totalPosRev+=i;
				}
				totalPos+=i;
			}
			if (res1==-1){
				if (res==1) totalNegRev+=i;
				totalNeg+=i;
			}
			
			
			if (res==1){				
				if (actualStreak>=0){
					actualStreak++;					
				}
				else{										
					actualStreak = 1;
				}			
			}else if (res==-1){
				if (actualStreak<=0){
					actualStreak--;					
				}
				else{				
					actualStreak = -1;
				}
			}			
		}
		
		//la ultima
		if (actualStreak>0){			
			betStats.setLastStreak(actualStreak);
		}else if (actualStreak<0){		
			betStats.setLastStreak(actualStreak);	
		}
		
		if (debug){
			System.out.println(totalNegRev*1.0/(totalNeg));
		}
		//System.out.println(totalNegRev);
		betStats.setAvgNeg(totalNegRev*1.0/(totalNeg));
		betStats.setAvgPos(totalPosRev*1.0/(totalPos));
		betStats.setAvgGlobal(totalGlobalRev*1.0/(results.size()-1));
		betStats.setMaxLosses(-maxLosses);
		betStats.setMaxWins(maxWins);
	}

}
