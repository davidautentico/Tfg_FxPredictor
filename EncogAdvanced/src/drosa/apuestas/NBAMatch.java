package drosa.apuestas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import drosa.utils.PrintUtils;

public class NBAMatch {

	// TODO Auto-generated method stub
	String homeTeam="";
	String awayTeam="";
	int homeScore=0;
	int awayScore=0;
	
	
	
	public String getHomeTeam() {
		return homeTeam;
	}



	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}



	public String getAwayTeam() {
		return awayTeam;
	}



	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}



	public int getHomeScore() {
		return homeScore;
	}



	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}



	public int getAwayScore() {
		return awayScore;
	}



	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}
	
	public String toString(){
		return homeTeam+" "+homeScore+" - "+awayScore+" "+awayTeam;
	}
	
	public static void openPages(){
		for (int year=1970;year<=1979;year++){
			String url = "http://www.basketball-reference.com/leagues/NBA_"+year+"_games.html#games::none";
			try
			{
			    java.net.URI uri = new URI(url);
			    int iBrowserWindows = 1;
			    for (int i = 0; i < iBrowserWindows; ++i)
			    {
			        java.awt.Desktop.getDesktop().browse(uri);
			        Thread.sleep(2000);
			    }
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private static NBAMatch decodeLine(String line) {
		// TODO Auto-generated method stub
		NBAMatch m = null;
		if (!line.contains("Box Score")) return null;
		if (line.contains("Visitor")) return null;
		
		
		String homeTeam="";
		String awayTeam="";
		int homeScore = 0;
		int awayScore = 0;
		
		try{
			homeTeam   = line.split(",")[4].trim();
			homeScore = Integer.valueOf(line.split(",")[5].trim());
			awayTeam   = line.split(",")[2].trim();
			awayScore = Integer.valueOf(line.split(",")[3].trim());
		
	        m  = new NBAMatch();
			m.setAwayScore(Integer.valueOf(awayScore));
			m.setHomeScore(Integer.valueOf(homeScore));
			m.setAwayTeam(awayTeam);
			m.setHomeTeam(homeTeam);
		}catch(Exception e){
			System.out.println("[ERROR] "+e.getMessage()+" || "+homeTeam+" || "+awayTeam+" || "+homeScore+" || "+awayScore
					);
			return null;
		}
		
		return m;
	}

	
	public static ArrayList<NBAMatch> readFromDisk(String fileName){
		ArrayList<NBAMatch> data = new ArrayList<NBAMatch>();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    NBAMatch item = null;
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero

	        int i=0;
	        while((line=br.readLine())!=null){
	        	if (i>=1){
	        			//System.out.println(i+" line= "+line);
	        			item = null;
	        			item = decodeLine(line);
	        			if (item!=null){
	        				data.add(item);
	        				//System.out.println(item.toString());
	        			}
	        	}
	        	i++;
	        	//if (i%20000==0)
	        		//System.out.println("i: "+i);
	        }    
	    }catch(Exception e){
	    	e.printStackTrace();
	    	 System.out.println("[error] "+line);
	    }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{            
	        	
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	   }	
		
		return data;
	}
	
	
	/**
	 * home: +4 away: +6
	 * @param matches
	 * @param begin
	 * @param end
	 * @param team
	 * @param n
	 * @return
	 */
	private static int getScore(ArrayList<NBAMatch> matches, int begin,
			int end, String team, int n) {
		// TODO Auto-generated method stub
		//System.out.println("***"+team+"***");
		int score = 0;
		int total = 0;
		for (int i=begin;i<=end;i++){
			NBAMatch odds = matches.get(i);
			int res = odds.getHomeScore()>odds.getAwayScore()?1:2;
			if (odds.getHomeTeam().equalsIgnoreCase(team)){
				if (res==1) score+=4;
				else score-=6;
				//System.out.println(odds.toString()+" "+score);
				total++;
			}else if (odds.getAwayTeam().equalsIgnoreCase(team)){
				if (res==2) score+=6;
				else score-=4;
				//System.out.println(odds.toString()+" "+score);
				total++;
			}
			if (total==n) return score;
		}
		
		return score;
	}
	
	/**
	 * 1->6 2->4
	 * @param matches
	 * @return
	 */
	public static ArrayList<ScoreResults> computeTeamResults(ArrayList<NBAMatch> matches,int n){
		int maxDiff = 2*6*n;
		ArrayList<ScoreResults> scoreArray = new ArrayList<ScoreResults>();
		for (int i=0;i<=2*maxDiff;i++){
			ScoreResults scoreRes = new ScoreResults();
			scoreRes.setScore(i);
			scoreArray.add(scoreRes);
		}
		ArrayList<String> patterns = new ArrayList<String>();
		for (int i=0;i<matches.size();i++){
			NBAMatch odds = matches.get(i);
			String homeTeam = odds.getHomeTeam();
			String awayTeam = odds.getAwayTeam();
			int homeScore = getScore(matches,i+1,matches.size()-1,homeTeam,n);
			int awayScore = getScore(matches,i+1,matches.size()-1,awayTeam,n);
			int res = odds.getHomeScore()>odds.getAwayScore()?1:2;
			String pattern = homeScore+"/"+awayScore+"/"+res;
			patterns.add(pattern);
			//System.out.println(pattern);
			int scoreDiff = homeScore-awayScore;
			int idx = scoreDiff+maxDiff;
			
			scoreArray.get(idx).getResults().add(res);
			//System.out.println("añadido para score idx: "+scoreDiff+" "+idx+" "+res+" || "+scoreArray.get(idx).getResults().size());
		}
		//System.out.println("Total patterns found: "+patterns.size());
		return scoreArray;
	}
	
	public static ArrayList<NBAMatch> loadFolder(String folder,int y1,int y2){
		ArrayList<NBAMatch> matches = new ArrayList<NBAMatch>();
		
		for (int y=y1;y<=y2;y++){
			String fileName = folder+"leagues_NBA_"+y+"_games_games.csv";
			File f = new File(fileName);
			if (!f.exists()){
				System.out.println("[ERROR] NO EXISTE: "+fileName);
				continue;
			}else{
				ArrayList<NBAMatch> matches1 = NBAMatch.readFromDisk(fileName);
				for (int i=0;i<matches1.size();i++) matches.add(matches1.get(i));
				
			}
		}
		
		return matches;
	}
	
	public static String getProbabilities(ArrayList<NBAMatch> matches,
			ArrayList<Integer> streak1,
			ArrayList<Integer> streak2,
			int streakLen,
			int handicap){
		
		
		int homeWins = 0;
		int awayWins = 0;
			
		HashMap<String,ArrayList<Integer>> teams= new HashMap<String,ArrayList<Integer>>();
			
		int ww = 0;
		int lw = 0;
		int total = 0;
		for (int i=0;i<=matches.size()-1;i++){//del ultimo al primero
			NBAMatch match = matches.get(i);
			//System.out.println(match.toString());
			String team1 = match.getHomeTeam();
			String team2 = match.getAwayTeam();
						
			if (!teams.containsKey(team1)){
				teams.put(team1, new ArrayList<Integer>());
			}
			if (!teams.containsKey(team2)){
				teams.put(team2, new ArrayList<Integer>());
			}
																
			if (teams.containsKey(team1)){
				ArrayList<Integer> r1 = teams.get(team1);
				ArrayList<Integer> r2 = teams.get(team2);
				if (r1.size()>=streakLen && r2.size()>=streakLen){
					int r11 = r1.get(r1.size()-1);
					int r12 = r1.get(r1.size()-2);
					int r13 = r1.get(r1.size()-3);
					//int r14 = r1.get(r1.size()-4);
					//int r15 = r1.get(r1.size()-5);
					//int r16 = r1.get(r1.size()-6);
					//int r17 = r1.get(r1.size()-7);
					int r21 = r2.get(r2.size()-1);
					int r22 = r2.get(r2.size()-2);
					int r23 = r2.get(r2.size()-3);
					//int r24 = r2.get(r2.size()-4);
					//int r25 = r2.get(r2.size()-5);
					//int r26 = r2.get(r2.size()-6);
					//int r27 = r2.get(r2.size()-7);*/
					//int r2Result = r2.get(r2.size()-1);
					
					boolean isOK = true;
					int s = 0;
					//System.out.println("steak1: "+streak1.get(0)+" "+streak1.get(1)+" "+streak1.get(2)
						//	+" | "+"steak2: "+streak2.get(0)+" "+streak2.get(1)+" "+streak2.get(2)
						//	+" ||| "+r11+" "+r12+" "+r13+" | "+r21+" "+r22+" "+r23);
					for (int j=1;j<=streakLen;j++){
						if (r1.get(r1.size()-j)!= streak1.get(j-1)){
							//System.out.println("diff1: "+r1.get(r1.size()-j)+" || "+streak1.get(j-1));
							isOK = false;
							break;
						}
						if (r2.get(r2.size()-j)!= streak2.get(j-1)){
							//System.out.println("diff2: "+r2.get(r2.size()-j)+" || "+streak2.get(j-1));
							isOK = false;
							break;
						}						
						s++;
					}
					if (isOK						
							){
						if (match.getHomeScore()+handicap>match.getAwayScore()){
							homeWins++;
						}else{
							awayWins++;
						}
						//System.out.println("YES!");
					}
				}
				
				int res = 1;
				if (match.getHomeScore()>match.getAwayScore()){
					//homeWins++;
					res = 1;
				}else{
					//awayWins++;
					res = -1;
				}
				
				teams.get(team1).add(res);
				teams.get(team2).add(res*-1);				
			}
				
		}		
			
		int totalMatches = homeWins+awayWins;
		double winPer = homeWins*100.0/totalMatches;
		double odds = 100/winPer;
		double odds2 = 100/(100.0-winPer);
		
		return totalMatches
				+" "+PrintUtils.Print2dec(homeWins*100.0/totalMatches, false)
				+" "+PrintUtils.Print2dec(odds, false)
				+" "+PrintUtils.Print2dec(odds2, false);
	}
	
	public static void printProbabilities(ArrayList<NBAMatch> matches,ArrayList<Integer> streak1,ArrayList<Integer> streak2,int streakLen){
					
		for (int handicap=-20;handicap<=20;handicap++){
			String oddsStr = getProbabilities(matches,streak1,streak2,streakLen,handicap);
			System.out.println(handicap+" || "+oddsStr);
		}
	}

	public static void main(String[] args) {
	
		String folder1 = "c:\\NBA\\";
		
		ArrayList<Integer> streak1 = new ArrayList<Integer>();
		ArrayList<Integer> streak2 = new ArrayList<Integer>();
		
		ArrayList<NBAMatch> matches = NBAMatch.loadFolder(folder1, 1970,2015);
		
		for (int i=0;i<=2;i++){
			streak1.add(0);
			streak2.add(0);
		}
		streak1.set(0,1);streak1.set(1,-1);streak1.set(2,1);
		streak2.set(0,-1);streak2.set(1,-1);streak2.set(2,1);
		printProbabilities(matches,streak1,streak2,3);
		
		//openPages();
		
		/*int handicap = 0;
		//ArrayList<NBAMatch> matches = NBAMatch.readFromDisk(folder1);
		for (int y=1980;y<=1980;y++){
			int y2 = 2015;
			ArrayList<NBAMatch> matches = NBAMatch.loadFolder(folder1, y,y2);
			
			//System.out.println("total matches: "+matches.size());
			
			int homeWins = 0;
			int awayWins = 0;
			
			HashMap<String,ArrayList<Integer>> teams= new HashMap<String,ArrayList<Integer>>();
			
			int ww = 0;
			int lw = 0;
			int total = 0;
			for (int i=0;i<=matches.size()-1;i++){//del ultimo al primero
				NBAMatch match = matches.get(i);
				//System.out.println(match.toString());
				String team1 = match.getHomeTeam();
				String team2 = match.getAwayTeam();
				
		
				if (!teams.containsKey(team1)){
					teams.put(team1, new ArrayList<Integer>());
				}
				if (!teams.containsKey(team2)){
					teams.put(team2, new ArrayList<Integer>());
				}
				
				
				
				
				if (teams.containsKey(team1)){
					ArrayList<Integer> r1 = teams.get(team1);
					ArrayList<Integer> r2 = teams.get(team2);
					if (r1.size()>=10 && r2.size()>=10){
						int r11 = r1.get(r1.size()-1);
						int r12 = r1.get(r1.size()-2);
						int r13 = r1.get(r1.size()-3);
						int r14 = r1.get(r1.size()-4);
						int r15 = r1.get(r1.size()-5);
						int r16 = r1.get(r1.size()-6);
						int r17 = r1.get(r1.size()-7);
						int r21 = r2.get(r2.size()-1);
						int r22 = r2.get(r2.size()-2);
						int r23 = r2.get(r2.size()-3);
						int r24 = r2.get(r2.size()-4);
						int r25 = r2.get(r2.size()-5);
						int r26 = r2.get(r2.size()-6);
						int r27 = r2.get(r2.size()-7);
						//int r2Result = r2.get(r2.size()-1);
						
						if (true
								&& r11==1 
								&& r12==1
								&& r13==1
								//&& r14==-1
								//&& r15==-1
								//&& r16==-1
								//&& r17==-1
								
								&& r21==-1
								&& r22==-1
								&& r23==-1
								//&& r24==-1
								//&& r25==-1
								//&& r26==-1
								//&& r27==-1
								){
							if (match.getHomeScore()+handicap>match.getAwayScore()){
								homeWins++;
							}else{
								awayWins++;
							}
						}
					}
					
					int res = 1;
					if (match.getHomeScore()>match.getAwayScore()){
						//homeWins++;
						res = 1;
					}else{
						//awayWins++;
						res = -1;
					}
					
					teams.get(team1).add(res);
					teams.get(team2).add(res*-1);
					
				}
				
			}
			
			//System.out.println(total+" "+PrintUtils.Print2dec(ww*100.0/total, false));
			
			int totalMatches = homeWins+awayWins;
			double winPer = homeWins*100.0/totalMatches;
			double odds = 100/winPer;
			double odds2 = 100/(100.0-winPer);
			System.out.println(
					y
					+" "+totalMatches
					+" "+PrintUtils.Print2dec(homeWins*100.0/totalMatches, false)
					+" "+PrintUtils.Print2dec(odds, false)
					+" "+PrintUtils.Print2dec(odds2, false)
					);
		}*/
		
		
		
		/*for (int i=matches.size()-1;i>=0;i--){//del ultimo al primero
			NBAMatch match = matches.get(i);
			//System.out.println(match.toString());
			String team1 = match.getHomeTeam();
			String team2 = match.getAwayTeam();
			System.out.println(match.toString()
					+" || "+NBAMatch.printLastN(teams,team1,5)+" || "+NBAMatch.printLastN(teams,team2,5));
		}*/
		
		/*int n = 5;
		ArrayList<ScoreResults> scoreArray = NBAMatch.computeTeamResults(matches,n);
		int maxDiff = 2*6*n;
		for (int i=0;i<scoreArray.size();i+=2){
			ScoreResults results = scoreArray.get(i);
			int wins  = 0;
			int total = 0;
			for (int j=0;j<results.getResults().size();j++){
				int value = results.getResults().get(j);
				if (value==1) wins++;
				total++;
			}
			double perWin = wins*100.0/total;
			double odds = 100.0/perWin;
			System.out.println(results.getScore()-maxDiff+" || "+results.getResults().size()
					+" "+PrintUtils.Print2(perWin)+" %"+" ("+PrintUtils.Print2(odds)+")"
					);
		}
		
		int score1 = OddPortal.calculateStreakScore(FirstTry.decodePattern("-1 1 -2 -1 -1"));
		int score2 = OddPortal.calculateStreakScore(FirstTry.decodePattern("2 1 1 2 1"));
		
		System.out.println("score: "+score1+" "+score2+" "+(score1-score2));
	
		*/
	
		/*ArrayList<TeamResults> results = TeamResults.calculateTeamResultsNBA(matches);
		
		TeamBBDD b1  = new TeamBBDD(results);
		b1.printInfo();
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern("2 1 2 1 2"), 1);
		String streak = b1.printTeamInfo("Boston Celtics",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Philadelphia 76ers",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Charlotte Hornets",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Cleveland Cavaliers",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Los Angeles Lakers",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Indiana Pacers",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Chicago Bulls",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Atlanta Hawks",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Orlando Magic",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Toronto Raptors",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Milwaukee Bucks",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Phoenix Suns",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("San Antonio Spurs",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Portland Trail Blazers",4);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Detroit Pistons",3);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);
		
		streak = b1.printTeamInfo("Los Angeles Clippers",3);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 1);
		FirstTry.printBBDDProbs("b1",b1, FirstTry.decodePattern(streak), 2);*/
		//openPages();
	}



	private static String printLastN(HashMap<String, ArrayList<Integer>> teams, String team,int n) {
		// TODO Auto-generated method stub
		if (!teams.containsKey(team)) return "";
		ArrayList<Integer> results = teams.get(team);
		
		int begin = results.size()-n;
		if (begin<0) begin = 0;
		
		String r = "";
		for (int i=results.size()-1;i>=begin;i--){
			r+=results.get(i)+" ";
		}
		
		return r.trim();
	}
}
