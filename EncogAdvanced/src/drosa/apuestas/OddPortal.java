package drosa.apuestas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class OddPortal {
	
	Sport sport;
	SeasonType seasonType;
	int year  = 0;
	int month = 0;
	int day   = 0;
	
	String homeTeam = "";
	String awayTeam = "";
	int homeScore = 0;
	int awayScore = 0;
	
	double homeOdds = 0.0;
	double awayOdds = 0.0;
	double drawOdds = 0.0;
	
	
	public Sport getSport() {
		return sport;
	}
	public void setSport(Sport sport) {
		this.sport = sport;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
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
	public double getHomeOdds() {
		return homeOdds;
	}
	public void setHomeOdds(double homeOdds) {
		this.homeOdds = homeOdds;
	}
	public double getAwayOdds() {
		return awayOdds;
	}
	public void setAwayOdds(double awayOdds) {
		this.awayOdds = awayOdds;
	}
	public double getDrawOdds() {
		return drawOdds;
	}
	public void setDrawOdds(double drawOdds) {
		this.drawOdds = drawOdds;
	}			
	public SeasonType getSeasonType() {
		return seasonType;
	}
	public void setSeasonType(SeasonType seasonType) {
		this.seasonType = seasonType;
	}
	
	public String toString(){
		return DateUtils.datePrint(year, month, day)+","+sport.name()+","+seasonType.name()+","+homeTeam+","+awayTeam+","+homeScore+","+awayScore
				+","+homeOdds+","+drawOdds+","+awayOdds;
	}
	
	public static void saveToDisk(String fileName,ArrayList<OddPortal> matches){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);

			  for (int i=0;i<matches.size();i++){
				  out.write(matches.get(i).toString());
				  out.newLine();
			  }
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static ArrayList<OddPortal> readFromDiskNBA(String fileName,Sport sport,int year){
		
		ArrayList<OddPortal> data = new ArrayList<OddPortal>();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    OddPortal item = null;
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);
	        // Lectura del fichero
	        int i=0;
	        SeasonType seasonType = SeasonType.REGULAR;
	        int month = 0;
	        int day = 0;
	        int hour = 0;
	        int minute = 0;
	        String homeTeam = "";
	        String awayTeam = "";
	        int homeScore = 0;
	        int awayScore = 0;
	        double homeOdds = 0.0;
	        double awayOdds = 0.0;
	        int nextLine = 0;
	        while((line=br.readLine())!=null){
	        	if (i>=0){	 	        			
	        			item = null;	
	        			if (nextLine == 1){
	        				if (!line.contains("-")){
	        					homeOdds = Double.valueOf(line.trim());
	        					nextLine = 2;
	        				}else nextLine =0;
	        			}else if (nextLine == 2){
	        				nextLine = 0;
	        				if (!line.contains("-")){
		        				awayOdds = Double.valueOf(line.trim());	        				
		        				//insertamos
		        				item = new OddPortal();
		        				item.setDay(day);
		        				item.setMonth(month);
		        				item.setYear(year);
		        				item.setSport(sport);
		        				item.setSeasonType(seasonType);
		        				item.setHomeTeam(homeTeam);
		        				item.setAwayTeam(awayTeam);
		        				item.setHomeScore(homeScore);
		        				item.setAwayScore(awayScore);
		        				item.setHomeOdds(homeOdds);
		        				item.setAwayOdds(awayOdds);
		        				data.add(item);
	        				}
	        				//System.out.println(item.toString());
	        			}else{
		        			if (line.length()>6 && isDateLine(line)){//guardo fecha
		        				day = Integer.valueOf(line.substring(0, 2));
		        				month = decodeMonth(line.substring(3, 6));
		        				year =  Integer.valueOf(line.substring(7, 11));
		        				seasonType = SeasonType.REGULAR;
			        			if (line.contains("Pre-season")){
			        				seasonType = SeasonType.PRE;
			        			}else if (line.contains("Play Offs")){
			        				seasonType = SeasonType.PLAYOFF;
			        			}
		        			}else if (line.length()>6 && isMatch(line) && !line.contains("canc.")){ //si es partido,leo el resultado y las dos siguientes
		        				hour   = Integer.valueOf(line.substring(0,2));
		        				minute = Integer.valueOf(line.substring(3,5));
		        				String teams = line.split("\\t")[1];
		        				String results = line.split("\\t")[2];
		        				homeTeam  = teams.split("-")[0].trim();
		        				awayTeam  = teams.split("-")[1].trim();
		        				homeScore = Integer.valueOf(results.split(":")[0].trim());
		        				awayScore = Integer.valueOf(results.split(":")[1].trim().split(" ")[0]);	
		        				nextLine = 1;
		        			}	        	
	        			}
	        	}
	        	i++;
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
	private static int decodeMonth(String month) {
		// TODO Auto-generated method stub
		if (month.equalsIgnoreCase("Jan")) return 0;
		if (month.equalsIgnoreCase("Feb")) return 1;
		if (month.equalsIgnoreCase("Mar")) return 2;
		if (month.equalsIgnoreCase("Apr")) return 3;
		if (month.equalsIgnoreCase("May")) return 4;
		if (month.equalsIgnoreCase("Jun")) return 5;
		if (month.equalsIgnoreCase("Jul")) return 6;
		if (month.equalsIgnoreCase("Aug")) return 7;
		if (month.equalsIgnoreCase("Sep")) return 8;
		if (month.equalsIgnoreCase("Oct")) return 9;
		if (month.equalsIgnoreCase("Nov")) return 10;
		if (month.equalsIgnoreCase("Dec")) return 11;

		return 0;
	}
	private static boolean isMatch(String line) {
		// TODO Auto-generated method stub
		String dots= line.substring(2, 3);
		if (dots.contains(":")) return true;
		return false;
	}
	private static boolean isDateLine(String line) {
		// TODO Auto-generated method stub
		String month = line.substring(3, 6);
		if (month.equalsIgnoreCase("Dec") 
				|| month.equalsIgnoreCase("Nov")
				|| month.equalsIgnoreCase("Oct")
				|| month.equalsIgnoreCase("Jan")
				|| month.equalsIgnoreCase("Feb")
				|| month.equalsIgnoreCase("Mar")
				|| month.equalsIgnoreCase("Apr")
				|| month.equalsIgnoreCase("May")
				|| month.equalsIgnoreCase("Jun")
				|| month.equalsIgnoreCase("Jul")
				|| month.equalsIgnoreCase("Aug")
				|| month.equalsIgnoreCase("Sep")				
				){
			return true;
		}
		return false;
	}
	
	public static void analysis(ArrayList<OddPortal> matches){
		int homeWins = 0;
		int awayWins = 0;
		for (int i = 0;i<matches.size();i++){
			OddPortal odds = matches.get(i);
			if (odds.getHomeScore()>odds.getAwayScore())
				homeWins++;
			else awayWins++;
		}
		int total = homeWins+awayWins;
		double homePer = homeWins*100.0/total;
		System.out.println(PrintUtils.Print2(homePer)+" "+PrintUtils.Print2(100.0-homePer));
	}
	
	public static void analysisOdds(ArrayList<OddPortal> matches,double odd1,double odd2,SeasonType seasonType,boolean isHome){
		int cases = 0;
		int wins = 0;
		double winPoints = 0;
		double lostPoints = 0;
		double avgOdds=0;
		for (int i = 0;i<matches.size();i++){
			OddPortal odds = matches.get(i);
			SeasonType season = odds.getSeasonType();
			double homeOdds = odds.getHomeOdds();
			double awayOdds = odds.getAwayOdds();
			
			if (isHome && homeOdds>=odd1 && homeOdds<=odd2 && (seasonType==SeasonType.ALL || season==seasonType)){
				cases++;
				avgOdds+=homeOdds;
				if (odds.getHomeScore()>odds.getAwayScore()){
					wins++;
					winPoints+=odds.getHomeOdds();
				}else{
					lostPoints+=odds.getAwayOdds();
				}
			}
			
			if (!isHome && awayOdds>=odd1 && awayOdds<=odd2 && (seasonType==SeasonType.ALL || season==seasonType)){
				cases++;
				avgOdds+=awayOdds;
				if (odds.getHomeScore()<odds.getAwayScore()){
					wins++;
					winPoints+=odds.getAwayOdds();
				}else{
					lostPoints+=odds.getHomeOdds();
				}
			}
			
		}

		double winPer = wins*100.0/cases;
		double the = 100.0*cases/avgOdds;
		double perProfit = winPoints/lostPoints;
		System.out.println(seasonType+" "+cases+" "+PrintUtils.Print2(odd1)+" "+PrintUtils.Print2(odd2)
				+" ["+" "+PrintUtils.Print2(avgOdds/cases)+" "+PrintUtils.Print2(the)+"]"
				+" "+PrintUtils.Print2(winPer)
				+" "+PrintUtils.Print2(the-winPer)
				+" ["+PrintUtils.Print2(winPoints)+"-"+PrintUtils.Print2(lostPoints)+"||"+PrintUtils.Print2(perProfit)+"]"
				);
	}
	
	/**
	 * 1->6 2->4
	 * @param matches
	 * @return
	 */
	public static ArrayList<ScoreResults> computeTeamResults(ArrayList<OddPortal> matches,int n){
		int maxDiff = 2*6*n;
		ArrayList<ScoreResults> scoreArray = new ArrayList<ScoreResults>();
		for (int i=0;i<=2*maxDiff;i++){
			ScoreResults scoreRes = new ScoreResults();
			scoreRes.setScore(i);
			scoreArray.add(scoreRes);
		}
		ArrayList<String> patterns = new ArrayList<String>();
		for (int i=0;i<matches.size();i++){
			OddPortal odds = matches.get(i);
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
	
	/**
	 * home: +4 away: +6
	 * @param matches
	 * @param begin
	 * @param end
	 * @param team
	 * @param n
	 * @return
	 */
	private static int getScore(ArrayList<OddPortal> matches, int begin,
			int end, String team, int n) {
		// TODO Auto-generated method stub
		//System.out.println("***"+team+"***");
		int score = 0;
		int total = 0;
		for (int i=begin;i<=end;i++){
			OddPortal odds = matches.get(i);
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
	public static void main(String[] args) {
		
		String folder1 = "C:\\NBA\\oddsportal\\";
		
		ArrayList<OddPortal> matches = null;
		ArrayList<OddPortal> allMatches = new ArrayList<OddPortal>();
		for (int year = 2014;year>=2010;year--){
			String fileName = folder1+year+".csv";
			matches = OddPortal.readFromDiskNBA(fileName, Sport.NBA, year);
			System.out.println("total matches: "+matches.size());
			for (int i=0;i<matches.size();i++){
				allMatches.add(matches.get(i));
			}
			//analysisOdds(matches,1.0,4.0,SeasonType.REGULAR,true);
			//analysisOdds(matches,1.0,2.5,SeasonType.REGULAR,false);
			/*analysis(matches);
			for (double odd1=1.00;odd1<=4.0;odd1+=0.1){
				double odd2 = odd1+0.5;
				analysisOdds(matches,odd1,odd2);
			}*/
		}
		/*OddPortal.saveToDisk("c:\\NBA_2010_2014.csv", allMatches);
		analysis(allMatches);
		for (double odd2=1.00;odd2<=10.0;odd2+=0.5){
			double odd1 = 1.00;
			analysisOdds(allMatches,odd1,odd2,SeasonType.REGULAR,false);
			//analysisOdds(allMatches,odd1,odd2,SeasonType.PLAYOFF,false);
			//analysisOdds(allMatches,odd1,odd2,SeasonType.PRE,false);
		}*/
		
		int n = 2;
		ArrayList<ScoreResults> scoreArray = OddPortal.computeTeamResults(allMatches,n);
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
		int score1 = calculateStreakScore(FirstTry.decodePattern("-1 1 -2 1 -1 "));
		int score2 = calculateStreakScore(FirstTry.decodePattern("-1 -2 -2 2 -1"));
		System.out.println("score: "+score1+" "+score2+" "+(score1-score2));
	}
	static int calculateStreakScore(ArrayList<Integer> arrayList) {
		// TODO Auto-generated method stub
		int score = 0;
		for (int i=0;i<arrayList.size();i++){
			if (arrayList.get(i)==1) score+=6;
			if (arrayList.get(i)==-1) score+=-6;
			if (arrayList.get(i)==2) score+=4;
			if (arrayList.get(i)==-2) score+=-4;
		}
		return score;
	}
	
}
