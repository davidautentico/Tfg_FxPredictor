package drosa.apuestas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class FOOTBALL {
	
	//sport = 0: nhl,1: nba
	public static ArrayList<Match> readFromDisk(String folder,int y1,int y2,int type,String prefix){
		
		ArrayList<Match> matches1 = new ArrayList<Match>();
		
		for (int y=y1;y<=y2;y++){
			String fileName = folder+prefix+"_"+y+".csv";
			//System.out.println(fileName);
			File f = new File(fileName);
			if (!f.exists()){				
				continue;

			}
			//System.out.println("a cargar: "+fileName);	
			ArrayList<Match> matches = Match.readFromDisk(fileName,type);
			for (int i=0;i<matches.size();i++) matches1.add(matches.get(i));							
		}
		
		return matches1;
	}
	
	public static void testRachasCasa(ArrayList<Match>  matches){
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>(); 
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			if (hTeam.equalsIgnoreCase("Real Madrid")
					|| hTeam.equalsIgnoreCase("Barcelona")
					|| hTeam.equalsIgnoreCase("Barcelona")
					|| hTeam.equalsIgnoreCase("Celtic")
					|| hTeam.equalsIgnoreCase("Rangers")
					){
				continue;
			}
			
			int hGoals = m.getHomeGoals();
			int aGoals = m.getAwayGoals();
			
			int res = 0;
			if (hGoals>aGoals) res = 1;
			if (aGoals>hGoals) res = -1;
			
			
			
			if (!teams.containsKey(hTeam)){
				ArrayList<Integer> newStreak = new ArrayList<Integer>();
				newStreak.add(0);
				teams.put(hTeam, newStreak);
			}
			if (!teams.containsKey(aTeam)){
				ArrayList<Integer> newStreak = new ArrayList<Integer>();
				newStreak.add(0);
				teams.put(aTeam, newStreak);
			}
			
			ArrayList<Integer> homeStreaks = teams.get(hTeam);
			
			int lastIndex = homeStreaks.size()-1; 
			int lastStreak = homeStreaks.get(lastIndex);
			
			if (res==1){
				if (lastStreak>=0){
					lastStreak++;
					homeStreaks.set(lastIndex,lastStreak);
				}else{
					homeStreaks.add(1);
				}					
			}else if (res==-1){
				if (lastStreak<=0){
					lastStreak--;
					homeStreaks.set(lastIndex,lastStreak);
				}else{
					homeStreaks.add(-1);
				}	
			}	
			lastStreak = homeStreaks.get(homeStreaks.size()-1);
			if (hTeam .equalsIgnoreCase("Alaves")){
				//System.out.println(res+" || "+lastStreak);
			}
		}
		
		ArrayList<Integer> streakAccs = new ArrayList<Integer>();
		for (Entry<String, ArrayList<Integer>> entry :  teams.entrySet()) {
			String team = entry.getKey();
			FOOTBALL.printNegativeInfo(team,entry.getValue(),streakAccs,false);					
		}
		FOOTBALL.printNegativeInfo("todos",streakAccs,null,true);	
	}
	
	public static void testCuotasCasa(ArrayList<Match>  matches){
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>(); 
		
		int total = 0;
		double hOddsEnvAcc = 0.0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			if (hTeam.equalsIgnoreCase("Real Madrid")
					|| hTeam.equalsIgnoreCase("Barcelona")
					){
				continue;
			}
			
			int hGoals = m.getHomeGoals();
			int aGoals = m.getAwayGoals();
			
			int res = 0;
			if (hGoals>aGoals) res = 1;
			if (aGoals>hGoals) res = -1;
			
			double hOddsEnv = m.getHomeOddsEnv();
			double aOddsEnv = m.getAwayOddsEnv();
			
						
			
			if (!teams.containsKey(hTeam)){
				ArrayList<Integer> newStreak = new ArrayList<Integer>();
				newStreak.add(0);
				teams.put(hTeam, newStreak);
			}
			if (!teams.containsKey(aTeam)){
				ArrayList<Integer> newStreak = new ArrayList<Integer>();
				newStreak.add(0);
				teams.put(aTeam, newStreak);
			}
			
			ArrayList<Integer> homeStreaks = teams.get(hTeam);
			
			int lastIndex = homeStreaks.size()-1; 
			int lastStreak = homeStreaks.get(lastIndex);
			
			if (lastStreak==-9){
				hOddsEnvAcc += hOddsEnv;
				total++;
			}
			
			if (res==1){
				if (lastStreak>=0){
					lastStreak++;
					homeStreaks.set(lastIndex,lastStreak);
				}else{
					homeStreaks.add(1);
				}					
			}else if (res==-1){
				if (lastStreak<=0){
					lastStreak--;
					homeStreaks.set(lastIndex,lastStreak);
				}else{
					homeStreaks.add(-1);
				}	
			}	
			lastStreak = homeStreaks.get(homeStreaks.size()-1);
			if (hTeam .equalsIgnoreCase("Alaves")){
				//System.out.println(res+" || "+lastStreak);
			}
		}
		
		System.out.println(total+" || "+PrintUtils.Print2dec(hOddsEnvAcc*1.0/total, false));
		
		ArrayList<Integer> streakAccs = new ArrayList<Integer>();
		for (Entry<String, ArrayList<Integer>> entry :  teams.entrySet()) {
			String team = entry.getKey();
			FOOTBALL.printNegativeInfo(team,entry.getValue(),streakAccs,false);					
		}
		FOOTBALL.printNegativeInfo("todos",streakAccs,null,true);
			
	}
	
 public static void testCuotasCasa2(ArrayList<Match>  matches,int betStreak,double betCuota){
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>(); 
		
		double profit = 0;
		int wins = 0;
		int losses = 0;
		int total = 0;
		double hOddsEnvAcc = 0.0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			if (hTeam.equalsIgnoreCase("Real Madrid")
					|| hTeam.equalsIgnoreCase("Barcelona")
					){
				continue;
			}
			
			int hGoals = m.getHomeGoals();
			int aGoals = m.getAwayGoals();
			
			int res = 0;
			if (hGoals>aGoals) res = 1;
			if (aGoals>hGoals) res = -1;
			
			double hOddsEnv = m.getHomeOddsEnv();
			double aOddsEnv = m.getAwayOddsEnv();
			
						
			
			if (!teams.containsKey(hTeam)){
				ArrayList<Integer> newStreak = new ArrayList<Integer>();
				newStreak.add(0);
				teams.put(hTeam, newStreak);
			}
			if (!teams.containsKey(aTeam)){
				ArrayList<Integer> newStreak = new ArrayList<Integer>();
				newStreak.add(0);
				teams.put(aTeam, newStreak);
			}
			
			ArrayList<Integer> homeStreaks = teams.get(hTeam);
			
			int lastIndex = homeStreaks.size()-1; 
			int lastStreak = homeStreaks.get(lastIndex);
			
			if (lastStreak<=betStreak){
				
				if (hOddsEnv>=betCuota){
					if (res==1){
						hOddsEnvAcc += hOddsEnv;
						profit += (hOddsEnv-1.0);
						//System.out.println("[win] "+(hOddsEnv-1.0)+" "+profit);
						wins++;
					}else if (res==-1){
						//hOddsEnvAcc += hOddsEnv;
						profit +=-1.0;
						//System.out.println("[loaa] "+(-1.0)+" "+profit);
						losses++;
					}
				}
				total++;
			}
			
			if (res==1){
				if (lastStreak>=0){
					lastStreak++;
					homeStreaks.set(lastIndex,lastStreak);
				}else{
					homeStreaks.add(1);
				}					
			}else if (res==-1){
				if (lastStreak<=0){
					lastStreak--;
					homeStreaks.set(lastIndex,lastStreak);
				}else{
					homeStreaks.add(-1);
				}	
			}	
			lastStreak = homeStreaks.get(homeStreaks.size()-1);
			if (hTeam .equalsIgnoreCase("Alaves")){
				//System.out.println(res+" || "+lastStreak);
			}
		}
		
		total = wins+losses;
		System.out.println(betStreak+" || "+total+
				"  "+PrintUtils.Print2dec(hOddsEnvAcc*1.0/wins, false)
				+"  "+PrintUtils.Print2dec(wins*100.0/total, false)
				+" "+PrintUtils.Print2dec(profit, false)+" "+PrintUtils.Print2dec(profit*100.0/total, false));
		
		/*ArrayList<Integer> streakAccs = new ArrayList<Integer>();
		for (Entry<String, ArrayList<Integer>> entry :  teams.entrySet()) {
			String team = entry.getKey();
			FOOTBALL.printNegativeInfo(team,entry.getValue(),streakAccs,false);					
		}
		FOOTBALL.printNegativeInfo("todos",streakAccs,null,true);*/
			
	}

	private static void printNegativeInfo(String team, ArrayList<Integer> streak,ArrayList<Integer> streakAccs,boolean debug) {
		// TODO Auto-generated method stub
		
		int total = 0;
		int acc = 0;
		int total1 = 0;
		int total2 = 0;
		int total3 = 0;
		int total4 = 0;
		int total5 = 0;
		int total6 = 0;
		int total7 = 0;
		ArrayList<Integer> accs = new ArrayList<Integer>();
		for (int i=0;i<streak.size();i++){
			int val = streak.get(i);
			
			if (val<0){
				acc += (-val);
				accs.add(-val);
				
				if (debug){
					//System.out.println(val);
				}
				if (streakAccs!=null)
					streakAccs.add(val);
				
				if (-val==1) total1++;
				if (-val==2) total2++;
				if (-val==3) total3++;
				if (-val==4) total4++;
				if (-val==5) total5++;
				if (-val==6) total6++;
				if (-val==7) total7++;
				total++;
			}
		}
		
		double avg = acc*1.0/total;
		double dt = Math.sqrt(MathUtils.variance(accs));
		//if (team.equalsIgnoreCase("Alaves"))
		double per1 = -1;
		double per2 = -1;
		double per3 = -1;
		double per4 = -1;
		double per5 = -1;
		double per6 = -1;
		double per7 = -1;
		
		if (total1>0) per1 = total1*100.0/total;
		if (total2>0) per2 = total2*100.0/total;
		if (total3>0) per3 = total3*100.0/total;
		if (total4>0) per4 = total4*100.0/total;
		if (total5>0) per5 = total5*100.0/total;
		if (total6>0) per6 = total6*100.0/total;
		if (total7>0) per7 = total7*100.0/total;
		
		if (debug){
			System.out.println(team+" || "+streak.size()+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(dt, false)
				+" || "+PrintUtils.Print2dec(per1, false)
					+" "+PrintUtils.Print2dec(per2, false)
					+" "+PrintUtils.Print2dec(per3, false)
					+" "+PrintUtils.Print2dec(per4, false)
					+" "+PrintUtils.Print2dec(per5, false)
					+" "+PrintUtils.Print2dec(per6, false)	
					+" "+PrintUtils.Print2dec(per7, false)
			);
			
		}
	}
	
  public static void testFiltro(BetStats betStats,ArrayList<Match>  matches,
		  String team,
		  int betStreak,
		  double betCuota1,double betCuota2,
		  int aPoints,
		  int toStreak,
		  boolean betHome,
		  int debug){
		
		HashMap<String,ArrayList<Integer>> teams = new HashMap<String,ArrayList<Integer>>(); 
		HashMap<String,ArrayList<Integer>> teamsStreak= new HashMap<String,ArrayList<Integer>>(); 
		HashMap<String,ArrayList<Integer>> teamResults = new HashMap<String,ArrayList<Integer>>(); 
		
		double profit = 0;
		int totalRisked = 0;
		int wins = 0;
		int losses = 0;
		int total = 0;
		double hOddsEnvAcc = 0.0;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			
			if (!team.isEmpty())
				if (!hTeam.equalsIgnoreCase(team)) continue;
			
			int hGoals = m.getHomeGoals();
			int aGoals = m.getAwayGoals();
			
			int res = 0;
			if (hGoals>aGoals) res = 1;
			if (aGoals>hGoals) res = -1;
			
			
			
			double hOddsEnv = m.getHomeOddsEnv();
			double aOddsEnv = m.getAwayOddsEnv();
			
			
			if (!teamResults.containsKey(hTeam)){				
				teamResults.put(hTeam, new ArrayList<Integer>());
			} 
			
			if (!teams.containsKey(hTeam)){
				ArrayList<Integer> newStreak = new ArrayList<Integer>();
				newStreak.add(0);
				teams.put(hTeam, newStreak);
				teamResults.put(hTeam, new ArrayList<Integer>());
			}
			if (!teams.containsKey(aTeam)){
				ArrayList<Integer> newStreak = new ArrayList<Integer>();
				newStreak.add(0);
				teams.put(aTeam, newStreak);
			}
			
			ArrayList<Integer> homeStreaks = teams.get(hTeam);
			ArrayList<Integer> awayStreaks = teamsStreak.get(hTeam);
			
			int lastIndex = homeStreaks.size()-1; 
			int lastStreak = homeStreaks.get(lastIndex);
			
			if (debug==1)
				System.out.println(hTeam+" - "+aTeam
						+" || "+res+" || "+lastStreak+" "+getAvgPointsTeam(teamResults,hTeam,10));
										
			if (lastStreak<=betStreak){//EL DE CADA
				
				ArrayList<Integer> pointsArray =   getAvgPoints(teamResults,10);
				double avg = MathUtils.average(pointsArray);
				double dt = Math.sqrt(MathUtils.variance(pointsArray));
				double avgPointsHome = getAvgPointsTeam(teamResults,hTeam,10);
				double avgPointsAway = getAvgPointsTeam(teamResults,aTeam,10);
				
				int aStreak=0;
				if (teamsStreak.containsKey(aTeam)){
					aStreak = teamsStreak.get(aTeam).get(teamsStreak.get(aTeam).size()-1);
				}
			
				//System.out.println(avg +" "+dt+" "+avgPointsHome);
				if (true
					//&& !hTeam.equalsIgnoreCase("Granada")
					//&& !hTeam.equalsIgnoreCase("Alaves")
					//&& !hTeam.equalsIgnoreCase("Sporting")
					//&& !hTeam.equalsIgnoreCase("Valencia")
					//&& !hTeam.equalsIgnoreCase("Osasuna")
					//&& !hTeam.equalsIgnoreCase("Sp Gijon")
					//&& !hTeam.equalsIgnoreCase("leganes")
						){
					if (avgPointsHome>=aPoints){
						if (hOddsEnv>=betCuota1-0.00 
								&& hOddsEnv<=betCuota2
								//&& aStreak>0//el de fuera
								){
							
												
							double odds = hOddsEnv;
							int bet = 1;
							int streak=-lastStreak;	
							
				
							if (!betHome){
								bet =-1;
								odds= aOddsEnv;
								streak=-lastStreak;
							}
							
							//streak = 5000;
							
							if (streak>0){
								if (res==bet){
									hOddsEnvAcc += odds;
									profit += (odds-1.0)*streak;
									//System.out.println("[win] "+(hOddsEnv-1.0)+" "+profit);
									wins++;
									totalRisked +=streak;
									betStats.addBet(1,(odds-1.0)*streak,streak);
								
									if (debug==1){
										System.out.println("[win] "+hTeam+" - "+aTeam
												+" || "+lastStreak +" "+aStreak
												+" ||| "+avgPointsHome
												/*+" || "+bet+" || "+res
												+" "+PrintUtils.Print2dec(odds, false)
												+" "+streak
												+" "+PrintUtils.Print2dec(profit, false)
												+" ||| "+m.getHomeOdds()+" "+m.getDrawOdds()+" "+m.getAwayOdds()*/
												);
									}
								
								}else if (res==-bet){
									hOddsEnvAcc += odds;
									profit +=-streak;
									//System.out.println("[loaa] "+(-1.0)+" "+profit);
									losses++;
									totalRisked +=streak;
									betStats.addBet(-1,-streak,streak);
									if (debug==1){
										System.out.println("[loss] "+hTeam+" - "+aTeam
												+" || "+lastStreak +" "+aStreak
												+" ||| "+avgPointsHome
												/*+" || "+bet+" || "+res
												+" "+PrintUtils.Print2dec(odds, false)
												+" "+streak
												+" "+PrintUtils.Print2dec(profit, false)
												+" ||| "+m.getHomeOdds()+" "+m.getDrawOdds()+" "+m.getAwayOdds()*/
												);
									}
								}
							}
						}
						total++;
					}
				}//teams
			}
			
			
			
			if (res==1){
				if (lastStreak>=0){
					lastStreak++;
					homeStreaks.set(lastIndex,lastStreak);
				}else{
					homeStreaks.add(1);
				}	
				//solo resultados de casa
				teamResults.get(hTeam).add(1);
			}else if (res==-1){
				if (lastStreak<=0){
					lastStreak--;
					homeStreaks.set(lastIndex,lastStreak);
				}else{
					homeStreaks.add(-1);
				}	
				//solo resultados de casa
				teamResults.get(hTeam).add(-1);
			}else if (res==0){
				teamResults.get(hTeam).add(0);
			}
			lastStreak = homeStreaks.get(homeStreaks.size()-1);
			
			updateStreak(teamsStreak,hTeam,aTeam,res);
		}
		
		total = wins+losses;
		/*System.out.println(betStreak
				+"  "+PrintUtils.Print2dec(betCuota, false)
				+" || "+total+
				"  "+PrintUtils.Print2dec(hOddsEnvAcc*1.0/total, false)
				+"  "+PrintUtils.Print2dec(wins*100.0/total, false)
				+" "+PrintUtils.Print2dec(profit, false)
				+" "+PrintUtils.Print2dec(profit*100.0/totalRisked, false)
		);*/
			
	}
		

	private static void updateStreak(HashMap<String, ArrayList<Integer>> teams, String hTeam, String aTeam, int res) {
		
			
		if (!teams.containsKey(hTeam)){
			teams.put(hTeam, new ArrayList<Integer>());
			teams.get(hTeam).add(0);
		}
		if (!teams.containsKey(aTeam)){
			teams.put(aTeam, new ArrayList<Integer>());
			teams.get(aTeam).add(0);
		}
		
		ArrayList<Integer> hStreak = teams.get(hTeam);
		ArrayList<Integer> aStreak = teams.get(aTeam);
		
		int lastHstreak = hStreak.get(hStreak.size()-1);
		int lastAstreak = aStreak.get(aStreak.size()-1);
		
		if (res==1){
			if (lastHstreak>=0){
				hStreak.set(hStreak.size()-1, lastHstreak+1);
			}else{//si ya era negativa
				hStreak.add(1);
			}
			
			if (lastAstreak>0){	//era positiva			
				aStreak.add(-1);
			}else{
				aStreak.set(aStreak.size()-1, lastAstreak-1);
			}
		}else if (res==-1){
			if (lastAstreak>=0){
				aStreak.set(aStreak.size()-1, lastAstreak+1);
			}else{
				aStreak.add(1);
			}
			if (lastHstreak>0){				
				hStreak.add(-1);
			}else{
				hStreak.set(hStreak.size()-1, lastHstreak-1);
			}
		}
	
	}

	private static ArrayList<Integer> getAvgPoints(HashMap<String, ArrayList<Integer>> teamResults, int n) {
	

		int begin = 0;
		int avg = 0;
		int total= 0;
		ArrayList<Integer> pointsArray = new ArrayList<Integer>();
		for (Entry<String,ArrayList<Integer>> entry :   teamResults.entrySet()) {
			String team = entry.getKey();
			ArrayList<Integer> results = entry.getValue();
			
			int points = 0;
			begin = results.size()-n;
			if (begin<=0) begin = 0;
			for (int i=begin;i<=results.size()-1;i++){
				points += results.get(i)*3;
			}
			
			pointsArray.add(points);
		}
		
		return pointsArray;
	}
	
	private static double getAvgPointsTeam(HashMap<String, ArrayList<Integer>> teamResults,String team, int n) {
		
		int begin = 0;
		int avg = 0;
		int total= 0;
		if (teamResults.get(team)==null) return 0.0;
		ArrayList<Integer> results = teamResults.get(team);
		
		int points = 0;
		begin = results.size()-n;
		if (begin<=0) begin = 0;
		for (int i=begin;i<=results.size()-1;i++){
			points += results.get(i)*3;
			if (results.get(i)==0){
				points+=1;
			}
		}
		
		avg+=points;
		total++;
		
		return avg*1.0/1;
	}
	
	private static void testLigas(String header,ArrayList<String> ligas, int begin, int end, int y1, int y2) {
		String folder = "C:\\futbol\\";
		ArrayList<Match>  matches = FOOTBALL.readFromDisk(folder, 2013, 2016, 5, ligas.get(begin));
		
		
		System.out.println("**** "+header+" ****");
		for (double cuota1=1.0;cuota1<=1.0;cuota1+=0.05){
			for (double cuota2=9.0;cuota2<=9.0;cuota2+=0.25){
				for (int points=-60;points<=-60;points+=1){
					for (int streak=-1;streak>=-1;streak--){
						BetStats betStats = new BetStats();
						betStats.setCases(0);
						betStats.setWins(0);
						betStats.setProfit(0.0);
						betStats.setRisk(0.0);
						
						FOOTBALL.testFiltro(betStats,matches,"",streak,cuota1,cuota2,points,false);
						
						System.out.println(
								PrintUtils.Print2dec(cuota1, false)		
								+" "+PrintUtils.Print2dec(cuota2, false)	
								+" || "+betStats.cases
								+"  "+PrintUtils.Print2dec(betStats.getWins()*100.0/betStats.cases, false)
								+" "+PrintUtils.Print2dec(betStats.profit, false)
								+" "+PrintUtils.Print2dec(betStats.profit*100.0/betStats.getRisk(), false)
						);
					}
				}
			}//cuota2
		}
		
	}

	public static void main(String[] args) {
		
		String folder = "C:\\futbol\\";
		ArrayList<String> ligas = new ArrayList<String>();
		ligas.add("SP1");ligas.add("SP2")
		;ligas.add("E0");ligas.add("E1");ligas.add("E2");
		ligas.add("D1");ligas.add("D2");ligas.add("P1");ligas.add("G1");ligas.add("T1");
		ligas.add("F1");ligas.add("F2");
		ligas.add("I1");ligas.add("I2");
		ligas.add("SC0");
		ligas.add("N1");ligas.add("B1");
		
		/*int total = ligas.size()-1;
		//total =0;
		for (int i=0;i<=total;i++){
			ArrayList<Match>  matches = FOOTBALL.readFromDisk(folder, 2003, 2016, 5, ligas.get(i));
			FOOTBALL.testRachasCasa(matches);
		}*/
		
		int begin = ligas.size()-1;
		int end = ligas.size()-1;
		begin = 0;
		//end = 11;
		
		
		ArrayList<Match>  matches = FOOTBALL.readFromDisk(folder, 2013, 2016, 5, ligas.get(0));
		HashMap<String,String> teams = NHL.extractTeams(matches);
		
		/*for (Entry<String, String> entry :  teams.entrySet()) {
			String team = entry.getKey();
			BetStats betStats = new BetStats();
			betStats.setCases(0);
			betStats.setWins(0);
			betStats.setProfit(0.0);
			betStats.setRisk(0.0);
			FOOTBALL.testFiltro(betStats,matches,team,-1,1.7,-900);
			System.out.println(
					team
					+" "+PrintUtils.Print2dec(0, false)					
					+" || "+betStats.cases
					+"  "+PrintUtils.Print2dec(betStats.getWins()*100.0/betStats.cases, false)
					+" "+PrintUtils.Print2dec(betStats.profit, false)
					+" "+PrintUtils.Print2dec(betStats.profit*100.0/betStats.getRisk(), false)
			);
		}*/
		/*BetStats betStats = new BetStats();
		betStats.setCases(0);
		betStats.setWins(0);
		betStats.setProfit(0.0);
		betStats.setRisk(0.0);
		FOOTBALL.testFiltro(betStats,matches,"",-1,3.0,-900);
		System.out.println(
				""
				+" "+PrintUtils.Print2dec(0, false)					
				+" || "+betStats.cases
				+"  "+PrintUtils.Print2dec(betStats.getWins()*100.0/betStats.cases, false)
				+" "+PrintUtils.Print2dec(betStats.profit, false)
				+" "+PrintUtils.Print2dec(betStats.profit*100.0/betStats.getRisk(), false)
		);*/
		begin = 0;
		end = ligas.size()-1;
		
		for (double cuota1=1.0;cuota1<=1.0;cuota1+=0.05){
			for (double cuota2=1.0;cuota2<=3.0;cuota2+=0.10){
				for (int points=0;points<=0;points+=1){
					for (int streak=-1;streak>=-1;streak--){
						for (int aStreak=1;aStreak<=1;aStreak++){
							BetStats betStats = new BetStats();
							betStats.setCases(0);
							betStats.setWins(0);
							betStats.setProfit(0.0);
							betStats.setRisk(0.0);
							for (int i=begin;i<=end;i++){
								for (int y=2003;y<=2003;y++){
									int y2 = y+13;
									matches = FOOTBALL.readFromDisk(folder, y, y2, 5, ligas.get(i));
									//System.out.println(matches.size());
									FOOTBALL.testFiltro(betStats,matches,"",streak,cuota1,cuota2,points,aStreak,true,0);								
								}
							}
							
							System.out.println(
									PrintUtils.Print2dec(cuota1, false)		
									+" "+PrintUtils.Print2dec(cuota2, false)
									+" "+points
									+" || "+betStats.cases
									+"  "+PrintUtils.Print2dec(betStats.getWins()*100.0/betStats.cases, false)
									+" "+PrintUtils.Print2dec(betStats.profit, false)
									+" "+PrintUtils.Print2dec(betStats.profit*100.0/betStats.getRisk(), false)
							);
						}
					}
				}
			}//cuota2
		}
		
		/*for (int i=0;i<ligas.size()-1;i++){
			String header = ligas.get(i);
			FOOTBALL.testLigas(header,ligas,i,i,2010,2016);
		}*/
		
	}

	

}
