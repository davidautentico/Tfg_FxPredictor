package drosa.apuestas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import drosa.experimental.ticksStudy.Tick;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class Match {
	String league = "";
	int day = 0;
	int month = 0;
	int year = 0;
	String homeTeam = "";
	String awayTeam = "";
	int homeGoals = 0;
	int awayGoals = 0;
	double homeOdds = 1.7;
	double awayOdds = 2.3;
	double drawOdds = 0.0;
	double homeOddsEnv = 1.7;
	double awayOddsEnv = 2.3;
	int betTeam = 0;
	int winTeam = 0;
	
	
	
	public int getWinTeam() {
		return winTeam;
	}
	public void setWinTeam(int winTeam) {
		this.winTeam = winTeam;
	}
	public double getHomeOddsEnv() {
		return homeOddsEnv;
	}
	public void setHomeOddsEnv(double homeOddsEnv) {
		this.homeOddsEnv = homeOddsEnv;
	}
	public double getAwayOddsEnv() {
		return awayOddsEnv;
	}
	public void setAwayOddsEnv(double awayOddsEnv) {
		this.awayOddsEnv = awayOddsEnv;
	}
	public int getBetTeam() {
		return betTeam;
	}
	public void setBetTeam(int betTeam) {
		this.betTeam = betTeam;
	}
	public String getLeague() {
		return league;
	}
	public void setLeague(String league) {
		this.league = league;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
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
	public int getHomeGoals() {
		return homeGoals;
	}
	public void setHomeGoals(int homeGoals) {
		this.homeGoals = homeGoals;
	}
	public int getAwayGoals() {
		return awayGoals;
	}
	public void setAwayGoals(int awayGoals) {
		this.awayGoals = awayGoals;
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
	public String toString(){
		String str = DateUtils.datePrint(year, month, day, 0, 0, 0)+" "+homeTeam+" - "+awayTeam+" "+homeGoals+" "+awayGoals
				+" || "+PrintUtils.Print2dec(homeOdds, false)
				+" "+PrintUtils.Print2dec(drawOdds, false)
				+" "+PrintUtils.Print2dec(awayOdds, false)
				;
		return str;
	}
	
	public static ArrayList<Match> readFromDisk(String fileName,int type){
		ArrayList<Match> data = new ArrayList<Match>();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    Match item = null;
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero

	        int i=0;
	        int posOdds = 0;
	        while((line=br.readLine())!=null){
	        	if (i==0){
	        		posOdds = Match.detectOddsPos(line);
	        		//System.out.println(posOdds);
	        	}
	        	if (i>=1){	 
	        			item = null;
	        			if (type==1)
	        				item = decodeLine(line);
	        			if (type==2)
	        				item = decodeLine2(line);
	        			if (type==3)
	        				item = decodeLine3(line);
	        			if (type==4)
	        				item = decodeLine4(line);
	        			if (type==5)
	        				item = decodeLine5(line,posOdds);
	        		if (item!=null) data.add(item);
	        		//System.out.println(item.toString());
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
	
	private static int detectOddsPos(String line) {
		String[] values = line.split(",");
		
		for (int i=0;i<values.length;i++){
			String val = values[i];
			
			if (val.equalsIgnoreCase("B365H")) return i;
			if (val.equalsIgnoreCase("BSH")) return i;
			if (val.equalsIgnoreCase("BWH")) return i;
			if (val.equalsIgnoreCase("GBH")) return i;
			if (val.equalsIgnoreCase("IWH")) return i;
			if (val.equalsIgnoreCase("LBH")) return i;
			if (val.equalsIgnoreCase("PSH")) return i;
			if (val.equalsIgnoreCase("SOH")) return i;
			if (val.equalsIgnoreCase("SBH")) return i;
			if (val.equalsIgnoreCase("SJH")) return i;
			if (val.equalsIgnoreCase("SYH")) return i;
			if (val.equalsIgnoreCase("VCH")) return i;
			if (val.equalsIgnoreCase("WHH")) return i;
		}
					
		return -1;
	}
	private static Match decodeLine2(String line) {
		// TODO Auto-generated method stub		
		if (line.split(",").length<6) return null;
		
		String league  = line.split(",")[1].trim();
		String dateStr = line.split(",")[0].trim();
		
		String day   = dateStr.substring(8,10);
        String month = dateStr.substring(5,7); 
        String year  = dateStr.substring(0,4);
     
        String homeTeam = line.split(",")[2].trim();
        String awayTeam = line.split(",")[3].trim();
        String goals    = line.split(",")[4].trim();
        		
        if (goals.trim().equalsIgnoreCase("")) return null;
        
        String homeGoals = goals.split(" - ")[0].trim();
        String awayGoals = goals.split(" - ")[1].trim();
        
        if (day.trim()=="" || month.trim()=="" || year.trim()=="") return null;
        
        Match m  = new Match();
        m.setLeague(league);       	
		m.setYear(Integer.valueOf(year));
		m.setMonth(Integer.valueOf(month));
		m.setDay(Integer.valueOf(day));
		m.setAwayGoals(Integer.valueOf(awayGoals));
		m.setHomeGoals(Integer.valueOf(homeGoals));
		m.setAwayTeam(awayTeam);
		m.setHomeTeam(homeTeam);
		
		return m;
	}
	
	private static Match decodeLine(String line) throws ParseException {
		
		
		if (line.split(",").length<6
				|| line.toUpperCase().contains("POSTPONED")
				|| line.toUpperCase().contains("RESCHEDULED")
				|| line.toUpperCase().contains("DATE")
				|| line.toUpperCase().contains("NOTES")
				) return null;
		
		//System.out.println(line.split(",").length);
		String league  = line.split(",")[0].trim();
		String dateStr = line.split(",")[0].trim();
		
		DateFormat readFormat = new SimpleDateFormat( "EEE MMM dd yyyy",Locale.US);
		Date date = null;
	  
	   date = readFormat.parse( dateStr );
	   
	   		
		String day   = dateStr.substring(0,2);
        String month = dateStr.substring(3,5); 
        String year  = dateStr.substring(6,8);
     
        String homeTeam = line.split(",")[2].trim();
        String awayTeam = line.split(",")[4].trim();
        String homeGoals = line.split(",")[3].trim();
        if (homeGoals.trim().isEmpty()) return null;
        String awayGoals = line.split(",")[5].trim();
        
        if (homeGoals.trim().isEmpty()) return null;
        if (awayGoals.trim().isEmpty()) return null;
        
        if (day.trim()=="" || month.trim()=="" || year.trim()=="") return null;
        
        Match m  = new Match();
        m.setLeague(league);       	
		m.setYear(date.getYear());
		m.setMonth(date.getMonth());
		m.setDay(date.getDay());
		m.setAwayGoals(Integer.valueOf(awayGoals));
		m.setHomeGoals(Integer.valueOf(homeGoals));
		m.setAwayTeam(awayTeam);
		m.setHomeTeam(homeTeam);
		
		return m;
	}
	
	private static Match decodeLine5(String line,int posOdds) throws ParseException {
		
		
		if (line.split(",").length<6
				|| line.toUpperCase().contains("HOMETEAM")
				|| line.toUpperCase().contains("RESCHEDULED")
				|| line.toUpperCase().contains("DATE")
				|| line.toUpperCase().contains("NOTES")
				|| line.toUpperCase().contains("#REF")
				) return null;
		
		//System.out.println(line.split(",").length);
		String league  = line.split(",")[0].trim();
		String dateStr = line.split(",")[1].trim();
		
		//DateFormat readFormat = new SimpleDateFormat( "EEE MMM dd yyyy",Locale.US);
		//Date date = null;
	  
	   //date = readFormat.parse( dateStr );
	   
	   		
		String day   = dateStr.substring(0,2);
        String month = dateStr.substring(3,5); 
        String year  = dateStr.substring(6,8);
     
        String homeTeam = line.split(",")[2].trim();
        String awayTeam = line.split(",")[3].trim();
        String homeGoals = line.split(",")[4].trim();
        if (homeGoals.trim().isEmpty()) return null;
        String awayGoals = line.split(",")[5].trim();
    
        
        if (homeGoals.trim().isEmpty()) return null;
        if (awayGoals.trim().isEmpty()) return null;
        
        if (day.trim()=="" || month.trim()=="" || year.trim()=="") return null;
        
        
        String[] values = line.split(",");
        
        int index = posOdds;
        
        
        double hOdds = 0.0;
        double dOdds = 0.0;
        double aOdds = 0.0;
        try{
	        hOdds = Double.valueOf(line.split(",")[index].trim());
	         dOdds = Double.valueOf(line.split(",")[index+1].trim());
	         aOdds = Double.valueOf(line.split(",")[index+2].trim());
        }catch( Exception e){
        	return null;
        }
        double hOddsEnv = hOdds*(dOdds-1)/dOdds;
        double aOddsEnv = aOdds*(dOdds-1)/dOdds;
        
        
        Match m  = new Match();
        m.setLeague(league);       	
		m.setYear(Integer.valueOf(year));
		m.setMonth(Integer.valueOf(month));
		m.setDay(Integer.valueOf(day));
		m.setAwayGoals(Integer.valueOf(awayGoals));
		m.setHomeGoals(Integer.valueOf(homeGoals));
		m.setAwayTeam(awayTeam);
		m.setHomeTeam(homeTeam);
		m.setHomeOdds(hOdds);
		m.setDrawOdds(dOdds);
		m.setAwayOdds(aOdds);
		m.setHomeOddsEnv(hOddsEnv);
		m.setAwayOddsEnv(aOddsEnv);
		
		return m;
	}
	
	private static Match decodeLine4(String line) throws ParseException {
		
		
		if (line.split(",").length<6
				|| line.toUpperCase().contains("POSTPONED")
				|| line.toUpperCase().contains("RESCHEDULED")
				|| line.toUpperCase().contains("DATE")
				|| line.toUpperCase().contains("NOTES")
				) return null;
		
		//System.out.println(line.split(",").length);
		String league  = line.split(",")[0].trim();
		String dateStr = line.split(",")[0].trim();
		
		DateFormat readFormat = new SimpleDateFormat( "EEE MMM dd yyyy",Locale.US);
		Date date = null;
	  
	   date = readFormat.parse( dateStr );
	   
	   		
		String day   = dateStr.substring(0,2);
        String month = dateStr.substring(3,5); 
        String year  = dateStr.substring(6,8);
     
        String homeTeam = line.split(",")[2].trim();
        String awayTeam = line.split(",")[4].trim();
        String homeGoals = line.split(",")[3].trim();
        if (homeGoals.trim().isEmpty()) return null;
        String awayGoals = line.split(",")[5].trim();
        
        if (homeGoals.trim().isEmpty()) return null;
        if (awayGoals.trim().isEmpty()) return null;
        
        if (day.trim()=="" || month.trim()=="" || year.trim()=="") return null;
        
        Match m  = new Match();
        m.setLeague(league);       	
		m.setYear(date.getYear());
		m.setMonth(date.getMonth());
		m.setDay(date.getDay());
		m.setAwayGoals(Integer.valueOf(awayGoals));
		m.setHomeGoals(Integer.valueOf(homeGoals));
		m.setAwayTeam(awayTeam);
		m.setHomeTeam(homeTeam);
		
		return m;
	}
	
	private static Match decodeLine3(String line) {
		// TODO Auto-generated method stub		
		if (line.split(",").length<5
				|| line.toUpperCase().contains("POSTPONED")
				|| line.toUpperCase().contains("RESCHEDULED")
				) return null;
		
		//String league  = line.split(",")[1].trim();
		String dateStr = line.split(",")[0].trim();
		
		String day   = dateStr.substring(8,10);
        String month = dateStr.substring(5,7); 
        String year  = dateStr.substring(0,4);
     
        String homeTeam = line.split(",")[3].trim();
        String awayTeam = line.split(",")[1].trim();        
        String homeGoals = line.split(",")[4].trim(); 
        String awayGoals = line.split(",")[2].trim(); 
        
        if (homeGoals.trim()=="") return null;
        if (awayGoals.trim()=="") return null;
              
        if (day.trim()=="" || month.trim()=="" || year.trim()=="") return null;
        
        Match m  = new Match();
       // m.setLeague(league);       	
		m.setYear(Integer.valueOf(year));
		m.setMonth(Integer.valueOf(month));
		m.setDay(Integer.valueOf(day));
		m.setAwayGoals(Integer.valueOf(awayGoals));
		m.setHomeGoals(Integer.valueOf(homeGoals));
		m.setAwayTeam(awayTeam);
		m.setHomeTeam(homeTeam);
		
		return m;
	}
	public void copy(Match m) {
		this.year = m.getYear();
		this.month = m.getMonth();
		this.day = m.getDay();
		this.homeTeam	= m.getHomeTeam();
		this.awayTeam 	= m.getAwayTeam();
		this.homeGoals 	= m.getHomeGoals();
		this.awayGoals 	= m.getAwayGoals();
		this.homeOdds 	= m.getHomeOdds();
		this.drawOdds 	= m.getDrawOdds();
		this.awayOdds 	= m.getAwayOdds();
		this.winTeam	= m.getWinTeam();
		
	}
	
}
