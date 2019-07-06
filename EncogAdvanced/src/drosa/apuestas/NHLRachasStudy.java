package drosa.apuestas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class NHLRachasStudy {
	
	
	public static void test(int y1,int y2,boolean isSportInsight){
		String folder = "C:\\nhl\\";
		//String folder = "C:\\nhl\\oddsportal\\";		
		String folder2 = "C:\\nhl\\sportsInsight\\";
		
		ArrayList<Match> testMatches = null;
		if (!isSportInsight){
			testMatches = NHL.readFromDisk(folder, y1, y2, 3, 0);
		}else{
			testMatches = SportsInsight.readAndReorderNHL(folder2, y1, y2, 1, 0);
		}
		
		
		int lastDay = -1;
		int winPoints = 0;
		int lostPoints = 0;
		int gwinPoints = 0;
		int glostPoints = 0;
		HashMap<String,Integer> teamStreaks = new HashMap<String,Integer>();
		for (int i=0;i<testMatches.size();i++){
			Match m = testMatches.get(i);
			
			int day = m.getDay();
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					gwinPoints += winPoints;
					glostPoints += lostPoints;
					System.out.println("NUEVO DIA: "+winPoints+" "+lostPoints+" || "+gwinPoints+" "+glostPoints);
					winPoints = 0;
					lostPoints = 0;
				}
				lastDay = day;
			}
			
			
			String hTeam = m.getHomeTeam();
			String aTeam = m.getAwayTeam();
			int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
			
			if (!teamStreaks.containsKey(hTeam)){
				teamStreaks.put(hTeam, 0);
			}
			if (!teamStreaks.containsKey(aTeam)){
				teamStreaks.put(aTeam, 0);
			}

			
			
			int hStreak = teamStreaks.get(hTeam);
			int aStreak = teamStreaks.get(aTeam);
			
			System.out.println(m.toString()+" || "+hStreak+" "+aStreak);
			
			//calculo de puntos
			// en caso de negativos ambos voy con los dos
			
			if (hStreak<0 
					
					&& aStreak>=0){
				if (res==1){
					winPoints += -hStreak;
				}else{
					lostPoints += -hStreak;
				}
			}else if (hStreak>=0 
					
					&& aStreak<0){
				if (res==-1){
					winPoints += -aStreak;
				}else{
					lostPoints += -aStreak;
				}
			}else if (hStreak<0 
					
					&& aStreak<0){
				if (hStreak<=aStreak){
					if (res==1){
						winPoints += -hStreak;
					}else{
						lostPoints += -hStreak;
					}
				}else if (aStreak<=hStreak){
					if (res==-1){
						winPoints += -aStreak;
					}else{
						lostPoints += -aStreak;
					}
				}
			}
			
			
			if (res==1){
				if (hStreak>=0) hStreak++;
				else hStreak = 1;
				
				if (aStreak<0){
					hStreak--;
				}
				else aStreak = -1;
			}else if (res==-1){
				if (aStreak>=0) aStreak++;
				else aStreak = 1;
				
				if (hStreak<0) hStreak--;
				else hStreak = -1;
			}
			
			teamStreaks.put(hTeam, hStreak);
			teamStreaks.put(aTeam, aStreak);
						
		}
		
	}
	
	public static void test2(int y1,int y2,boolean isSportInsight){
		String folder = "C:\\nhl\\";
		//String folder = "C:\\nhl\\oddsportal\\";		
		String folder2 = "C:\\nhl\\sportsInsight\\";
		
		ArrayList<Match> testMatches = null;
		if (!isSportInsight){
			testMatches = NHL.readFromDisk(folder, y1, y2, 3, 0);
		}else{
			testMatches = SportsInsight.readAndReorderNHL(folder2, y1, y2, 1, 0);
		}
		
		HashMap<String, String> teams = NHL.extractTeams(testMatches);
		int profit = 0;
		for (Entry<String,String> entry : teams.entrySet()){
			
			String team = entry.getKey();
			
			int totalProfit = 0;
			int totalLosses = 0;
			int lastDay = -1;
			int winPoints = 0;
			int lostPoints = 0;
			int gwinPoints = 0;
			int glostPoints = 0;
			HashMap<String,Integer> teamStreaks = new HashMap<String,Integer>();
			for (int i=0;i<testMatches.size();i++){
				
				
				Match m = testMatches.get(i);
				
				int day = m.getDay();
				
				if (day!=lastDay){
					
					if (lastDay!=-1){
						gwinPoints += winPoints;
						glostPoints += lostPoints;
						//System.out.println("NUEVO DIA: "+winPoints+" "+lostPoints+" || "+gwinPoints+" "+glostPoints+" ||| "+totalProfit+" "+totalLosses);
						winPoints = 0;
						lostPoints = 0;
					}
					lastDay = day;
				}
				
				
				
				String hTeam = m.getHomeTeam();
				String aTeam = m.getAwayTeam();
				int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
				
				
				
				if (!teamStreaks.containsKey(hTeam)){
					teamStreaks.put(hTeam, 0);
				}
				if (!teamStreaks.containsKey(aTeam)){
					teamStreaks.put(aTeam, 0);
				}
	
				
				
				int hStreak = teamStreaks.get(hTeam);
				int aStreak = teamStreaks.get(aTeam);
				
				boolean isBet = false;
				//String team = "Pittsburgh Penguins";
				
				int betInt = 0;
				if (hTeam.equalsIgnoreCase(team)) betInt=1;
				if (aTeam.equalsIgnoreCase(team)) betInt=-1;
				
				if (betInt!=0){
					//System.out.println(m.toString()+" || "+hStreak+" "+aStreak);
					isBet = true;
				}
				
				
				//if (aStreak<=-7) aStreak = 0;
				//if (hStreak<=-7) hStreak = 0;
				
				//calculo de puntos
				// en caso de negativos ambos voy con los dos
				
				
				if (hStreak<0 					
						&& aStreak>=0){
					if (res==1){
						if (betInt==1){
							winPoints += -hStreak;	
							totalProfit +=1;
						}
						hStreak = 1;										
					}else{
						if (betInt==1){
							lostPoints += -hStreak;
							if (hStreak==-5){
								totalLosses +=1;
								hStreak = -1;
								break;
							}else{
								hStreak--;
							}
						}else{
							hStreak--;
						}
					}
									
				}else if (hStreak>=0 					
						&& aStreak<0){
					if (res==-1){
						if (betInt==-1){
							winPoints += -aStreak;
							totalProfit +=1;
						}
						aStreak = 1;					
					}else{
						if (betInt==-1){
							lostPoints += -aStreak;
							if (aStreak==-5){
								totalLosses +=1;
								aStreak = -1;
								break;
							}else{
								aStreak--;
							}
						}else{
							aStreak--;
						}									
					}
				}else if (hStreak<0 					
						&& aStreak<0){
					if (hStreak<=aStreak){
						if (res==1){
							if (betInt==1){
								winPoints += -hStreak;	
								totalProfit +=1;
							}
							hStreak = 1;		
						}else{
							if (betInt==1){
								lostPoints += -hStreak;
								if (hStreak==-5){
									totalLosses +=1;
									hStreak = -1;
									break;
								}else{
									hStreak--;
								}
							} else{
								hStreak--;
							}
						}
					}else if (aStreak<=hStreak){
						if (res==-1){
							if (betInt==-1){
								winPoints += -aStreak;
								totalProfit +=1;
							}
							aStreak = 1;	
						}else{
							if (betInt==-1){
								lostPoints += -aStreak;
								if (aStreak==-5){
									totalLosses +=1;
									aStreak = -1;
									break;
								}else{
									aStreak--;	
								}
							}else{
								aStreak--;
							}
						}
					}
				}else if (hStreak>=0 && aStreak>=0){
					if (res==1){
						if (hStreak>=0) hStreak++;
						else hStreak = 1;
						
						if (aStreak<0){
							hStreak--;
						}
						else aStreak = -1;
					}else if (res==-1){
						if (aStreak>=0) aStreak++;
						else aStreak = 1;
						
						if (hStreak<0) hStreak--;
						else hStreak = -1;
					}
				}
				
												
				teamStreaks.put(hTeam, hStreak);
				teamStreaks.put(aTeam, aStreak);
							
			}
			//System.out.println(team+" "+winPoints+" "+lostPoints+" || "+gwinPoints+" "+glostPoints+" ||| "+totalProfit+" "+totalLosses+" |||| "+(totalProfit-totalLosses*15));
			profit += (totalProfit-totalLosses*30);
		}
		System.out.println(profit);
	}

	public static void main(String[] args) {
		
		for (int y=2005;y<=2016;y++)
		NHLRachasStudy.test2(y, y, true);
	}

}
