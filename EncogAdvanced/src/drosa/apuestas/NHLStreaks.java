package drosa.apuestas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import drosa.utils.PrintUtils;

public class NHLStreaks {

	
	public static void test(boolean isSportInsight,int y1,int y2,double streakTest1,double streakTest2,int test1,int test2){
		String folder = "C:\\nhl\\";
		//String folder = "C:\\nhl\\oddsportal\\";		
		String folder2 = "C:\\nhl\\sportsInsight\\";
		
		ArrayList<Match> testMatches = null;
		if (!isSportInsight){
			testMatches = NHL.readFromDisk(folder, y1, y2, 3, 0);
		}else{
			testMatches = SportsInsight.readAndReorderNHL(folder2, y1, y2, 1, 0);
		}
		HashMap<String,ArrayList<Integer>> teamStats = new HashMap<String,ArrayList<Integer>>(); 
		StreakUtils.calculateGroupStreakTeam(testMatches, teamStats,false);
		
		
		int totalStreaks = 0;
		int totalStreaksAcum = 0;
		 
		int count3 = 0;
		int acc = 0;
		int count2p1 =0;
		int count2p2 =0;
		for (Entry<String, ArrayList<Integer>> entry : teamStats.entrySet()) {			
			String team = entry.getKey();
			ArrayList<Integer> streaks =  entry.getValue();
			String streakStr ="";
			int lastNegStreak = 0;
			int count3t = 0;
			int acct = 0;
			ArrayList<Integer> streaksN =  new ArrayList<Integer>();
			for (int i=0;i<streaks.size();i++){
				int streak = streaks.get(i);
				if (streak>=0) continue;
				streaksN.add(streak);
				 totalStreaksAcum += -streak;
				 totalStreaks++;
			}
			
						
			for (int i=10;i<streaksN.size();i++){
				int last1 = streaksN.get(i-1);
				int last2 = streaksN.get(i-2);
				int last3 = streaksN.get(i-3);
				int last4 = streaksN.get(i-4);
				int last5 = streaksN.get(i-5);
				int last6 = streaksN.get(i-6);
				int last7 = streaksN.get(i-7);
				int last8 = streaksN.get(i-8);
				int last9 = streaksN.get(i-9);
				int last10 = streaksN.get(i-10);
				int streak = streaksN.get(i);
				streakStr+=streaksN.get(i)+" ";	
				int acum = 1+2+3+4+5+6+7+8+9+10;
				double sum = Math.abs((last1*1+last2*2+last3*3+last4*4+last5*5+last6*6+last7*7+last8*8+last9*9+last10*10)*1.0/acum);
				
				int n = 10;
				acum = 0;
				sum = 0;
				int peso = n;
				for (int j=1;j<=n;j++){
					acum +=peso;
					sum += Math.abs(streaksN.get(i-j)*peso);
					peso--;
				}
				sum = sum*1.0/acum;
				if (sum>=streakTest1 && sum<=streakTest2){
					acc+=streak;
					count3++;
					acct+=streak;
					count3t++;
					if (streak<=-test1){
						count2p1++;
					}
					if (streak<=-test2){
						count2p2++;
					}
				}
					
				lastNegStreak = streak;
			}
			//System.out.println(team+" "+PrintUtils.Print2dec(acct*1.0/count3t, false)+" "+streakStr.trim());
		}
		/*System.out.println(
				y1+" "+y2
				+" "+PrintUtils.Print2dec(streakTest, false)
				+" || "+count3
				+" "+PrintUtils.Print2dec(acc*1.0/count3, false)
				+" "+count2p1
				+" "+PrintUtils.Print2dec(count2p2*100.0/count2p1, false)
			);*/
		
		double prob = count2p2*100.0/count2p1;
		System.out.println(
				y1+" "+y2
				+" || "+PrintUtils.Print2dec(totalStreaksAcum*1.0/totalStreaks, false)
				+" "+count2p1+" "+PrintUtils.Print2dec(prob, false)
				);
	}
	
	public static void main(String[] args) {
		
		/*for (double streakTest=1.00;streakTest<=1.0;streakTest+=0.25){
			NHLStreaks.test(false, 1970, 2016,streakTest);
		}*/
		
		for (int y=2009;y<=2009;y+=1){
			for (int nump=1;nump<=15;nump++)
				NHLStreaks.test(false, y, y+40,2.0,3.0,nump,nump+1);
		}
	}

}
