package drosa.apuestas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class SportsInsight {
	
	public static ArrayList<Match> reorderAddHomeAwayOdds(ArrayList<Match> matches){
		
		ArrayList<Match> array = new ArrayList<Match>();
		
		for (int i=matches.size()-1;i>=0;i--){
			Match m1 = matches.get(i);
			
			Match m2 = new Match();
			m2.copy(m1);
			
			
			
			/*double homeOddsAsian = (1.0-1.0/m1.getDrawOdds())*m1.getHomeOdds();
			double awayOddsAsian = (1.0-1.0/m1.getDrawOdds())*m1.getAwayOdds();
			
			m2.setHomeOdds(homeOddsAsian);
			m2.setAwayOdds(awayOddsAsian);
			m2.setDrawOdds(0.0);*/
			
			array.add(m2);
		}
		
		
		return array;
	}
	
private static ArrayList<Match> readFileFromDisk(String fileName) {
		
		ArrayList<Match> matches = new ArrayList<Match>();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    Match match = null;
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        //archivo = new File (fileName);
	        //fr = new FileReader (archivo);
	        br = new BufferedReader(fr);
	       // br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-16"));

	        // Lectura del fichero
	        int i=0;
	        int lastMatchLine = -1;
	        int homeGoals = -1;
	        int awayGoals = -1;
	        String homeTeam = "";
	        String awayTeam = "";
	        double homeOdds = 0.0;
	        double drawOdds = 0.0;
	        double awayOdds = 0.0;
	        String dateStr = "";
	        while((line=br.readLine())!=null){
	        	if (i>0){	 
	        			if (!line.contains(";;;;") && !line.contains("Home") 
	        					&& !line.contains("Away")
	        					&& !line.contains("West")
	        					&& !line.contains("Under Goals")
	        					&& !line.contains("Over Goals")
	        					&& !line.contains("Pacific")
	        					&& !line.contains("Atlantic")
	        					){
		        			String[] values0 = line.split(";", -1);
		        			ArrayList<String> values = new ArrayList<String>();
		        			for (int j=0;j<values0.length;j++){
		        				if (values0[j].isEmpty()) continue;
		        				values.add(values0[j]);
		        			}
		        				        				
		        			//System.out.println("dd"+values[2]);
		        			dateStr =  values.get(0);
		        			homeTeam = values.get(3);//.substring(0, 5);
		        			awayTeam = values.get(2);//.substring(0, 5);	        					
		        			homeGoals = Integer.valueOf(values.get(values.size()-1).trim());
		        			awayGoals = Integer.valueOf(values.get(values.size()-2).trim());
		        			
		        			String[] dateArray = dateStr.split("/");
		        			int day = Integer.valueOf(dateArray[1]);
		        			int month = Integer.valueOf(dateArray[0]);
		        			int year = Integer.valueOf(dateArray[2].substring(0, 4));
		        			
		        			
		        			int homeOddsInt =Integer.valueOf(values.get(values.size()-3).trim());
		        			int awayOddsInt =Integer.valueOf(values.get(values.size()-5).trim());
		        			
		        			homeOdds = Double.valueOf((-100.0/homeOddsInt)+1);
		        			awayOdds = Double.valueOf((-100.0/awayOddsInt)+1);
		        			if (homeOddsInt>=0)		        			
		        				homeOdds = Double.valueOf((homeOddsInt/100.0)+1);
		        			if (awayOddsInt>=0)		        			
		        				awayOdds = Double.valueOf((awayOddsInt/100.0)+1);
		        			//awayOdds = Double.valueOf(values.get(values.size()-4).trim());
	    					match = new Match();
	    					match.setYear(year);
	    					match.setDay(day);
	    					match.setMonth(month);
	    					match.setHomeGoals(homeGoals);
	    					match.setAwayGoals(awayGoals);
	    					match.setHomeOdds(Math.abs(homeOdds));
	    					match.setDrawOdds(drawOdds);
	    					match.setAwayOdds(Math.abs(awayOdds));
	    					match.setHomeTeam(homeTeam.trim());
	    					match.setAwayTeam(awayTeam.trim());
	    					
	    					matches.add(match);	
	    					
	    					//System.out.println(match.toString()+" || "+homeOddsInt+" "+awayOddsInt);
	    				//System.out.println(matches.size());
	        			}
    					
	        	}
	        	i++;
	        	//if (i%20000==0)
	        		//System.out.println("i: "+i);
	        }    
	    }catch(Exception e){
	    	e.printStackTrace();
	    	 System.out.println("[error] "+fileName+" || "+line);
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
		
		return matches;
	}

private static ArrayList<Match> readFileFromDiskMLB(String fileName) {
	
	ArrayList<Match> matches = new ArrayList<Match>();
	
	File archivo = null;
    FileReader fr = null;
    BufferedReader br = null;
    String line="";
    Match match = null;
    try {
    	// Apertura del fichero y creacion de BufferedReader para poder
        // hacer una lectura comoda (disponer del metodo readLine()).
        archivo = new File (fileName);
        fr = new FileReader (archivo);
        //archivo = new File (fileName);
        //fr = new FileReader (archivo);
        br = new BufferedReader(fr);
       // br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-16"));

        // Lectura del fichero
        int i=0;
        int lastMatchLine = -1;
        int homeGoals = -1;
        int awayGoals = -1;
        String homeTeam = "";
        String awayTeam = "";
        double homeOdds = 0.0;
        double drawOdds = 0.0;
        double awayOdds = 0.0;
        String dateStr = "";
        while((line=br.readLine())!=null){
        	if (i>=0){	 
        			if (true
        					//&& !line.contains(",,,,") 
        					&& !line.contains("Home") 
        					&& !line.contains("Away")
        					&& !line.contains("West")
        					&& !line.contains("Under Goals")
        					&& !line.contains("Over Goals")
        					&& !line.contains("Pacific")
        					&& !line.contains("Atlantic")
        					){
	        			String[] values0 = line.split(",", -1);
	        			ArrayList<String> values = new ArrayList<String>();
	        			for (int j=0;j<values0.length;j++){
	        				//if (values0[j].isEmpty()) continue;
	        				values.add(values0[j]);
	        			}
	        				        				
	        			//System.out.println("dd"+values[2]);
	        			dateStr =  values.get(0);
	        			homeTeam = values.get(3);//.substring(0, 5);
	        			awayTeam = values.get(2);//.substring(0, 5);	        					
	        			homeGoals = Integer.valueOf(values.get(values.size()-1).trim());
	        			awayGoals = Integer.valueOf(values.get(values.size()-2).trim());
	        			
	        			String[] dateArray = dateStr.split("/");
	        			int day = Integer.valueOf(dateArray[1]);
	        			int month = Integer.valueOf(dateArray[0]);
	        			int year = Integer.valueOf(dateArray[2].substring(0, 4));
	        			
	        			if (values.get(values.size()-3).trim().isEmpty()) continue;
	        			if (values.get(values.size()-5).trim().isEmpty()) continue;
	        			
	        			int homeOddsInt =Integer.valueOf(values.get(values.size()-3).trim());
	        			int awayOddsInt =Integer.valueOf(values.get(values.size()-5).trim());
	        			
	        			homeOdds = Double.valueOf((-100.0/homeOddsInt)+1);
	        			awayOdds = Double.valueOf((-100.0/awayOddsInt)+1);
	        			if (homeOddsInt>=0)		        			
	        				homeOdds = Double.valueOf((homeOddsInt/100.0)+1);
	        			if (awayOddsInt>=0)		        			
	        				awayOdds = Double.valueOf((awayOddsInt/100.0)+1);
	        			//awayOdds = Double.valueOf(values.get(values.size()-4).trim());
    					
	        			if (isMLBValidDate(day,month,year)){	        			
	        				match = new Match();
	    					match.setYear(year);
	    					match.setDay(day);
	    					match.setMonth(month);
	    					match.setHomeGoals(homeGoals);
	    					match.setAwayGoals(awayGoals);
	    					match.setHomeOdds(Math.abs(homeOdds));
	    					match.setDrawOdds(drawOdds);
	    					match.setAwayOdds(Math.abs(awayOdds));
	    					match.setHomeTeam(homeTeam.trim());
	    					match.setAwayTeam(awayTeam.trim());
	    					match.setWinTeam(-1);
	    					if (homeGoals>awayGoals) match.setWinTeam(1);
	    					
	    					matches.add(match);	
	        			}
    					
    					//System.out.println(match.toString()+" || "+homeOddsInt+" "+awayOddsInt);
    				//System.out.println(matches.size());
        			}
					
        	}
        	i++;
        	//if (i%20000==0)
        		//System.out.println("i: "+i);
        }    
    }catch(Exception e){
    	e.printStackTrace();
    	 System.out.println("[error] "+fileName+" || "+line);
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
	
	return matches;
}

private static boolean isMLBValidDate(int day, int month, int year) {
	
	if (year==2016){
		if (month>=5 && month<=9) return true;
		if (month==4 && day>=3) return true;
		if (month==10 && day<=2) return true;
	}
	if (year==2015){
		if (month>=5 && month<=9) return true;
		if (month==4 && day>=5) return true;
		if (month==10 && day<=4) return true;
	}
	if (year==2014){
		if (month>=4 && month<=8) return true;
		if (month==3 && day>=31) return true;
		if (month==9 && day<=28) return true;
	}
	if (year==2013){
		if (month>=4 && month<=8) return true;
		if (month==3 && day>=31) return true;
		if (month==9 && day<=29) return true;
	}
	if (year==2012){
		if (month>=5 && month<=9) return true;
		if (month==4 && day>=5) return true;
		if (month==10 && day<=3) return true;
	}
	if (year==2011){
		if (month>=4 && month<=8) return true;
		if (month==3 && day>=31) return true;
		if (month==9 && day<=28) return true;
	}
	if (year==2010){
		if (month>=5 && month<=9) return true;
		if (month==4 && day>=4) return true;
		if (month==10 && day<=3) return true;
	}
	if (year==2009){
		if (month>=5 && month<=9) return true;
		if (month==4 && day>=5) return true;
		if (month==10 && day<=4) return true;
	}
	if (year==2008){
		if (month>=4 && month<=8) return true;
		if (month==3 && day>=31) return true;
		if (month==9 && day<=28) return true;
	}
	if (year==2007){
		if (month>=5 && month<=8) return true;
		if (month==4 && day>=1) return true;
		if (month==9 && day<=30) return true;
	}
	if (year==2006){
		if (month>=5 && month<=9) return true;
		if (month==4 && day>=3) return true;
		if (month==10 && day<=1) return true;
	}
	if (year==2005){
		if (month>=5 && month<=9) return true;
		if (month==4 && day>=4) return true;
		if (month==10 && day<=2) return true;
	}
	
	return false;
}

public static ArrayList<Match> readFromDisk(String folder,int y1,int y2,int type,int sport){
		
		ArrayList<Match> matches1 = new ArrayList<Match>();
		
		for (int y=y1;y<=y2;y++){
			String fileName = folder+"nhl"+y+".txt";
			if (sport==1){
				//leagues_NBA_1970_games_games.csv
				//fileName = folder+"leagues_NBA_"+y+"_games_games.csv";
			}
			if (sport==2){
				//leagues_NBA_1970_games_games.csv
				fileName = folder+"mlb"+y+".txt";
			}
			File f = new File(fileName);
			if (!f.exists()){
				if (sport==1){
					fileName = folder+"nba"+y+".txt";
					//System.out.println("fileName: "+fileName);
					f = new File(fileName);
					if (!f.exists()){
						continue;
					}else{
						type=4;
					}
				}else{
					//System.out.println("[ERROR] NO EXISTE: "+fileName);
					continue;
				}
			}
			//System.out.println("a cargar: "+fileName);	
			ArrayList<Match> matches = readFileFromDisk(fileName);
			for (int i=0;i<matches.size();i++) matches1.add(matches.get(i));							
		}
		
		return matches1;
	}	

public static ArrayList<Match> readFromDiskMLB(String folder,int y1,int y2,int type,int sport){
	
	ArrayList<Match> matches1 = new ArrayList<Match>();
	
	for (int y=y1;y<=y2;y++){
		String fileName = folder+"nhl"+y+".txt";		
		if (sport==2){
			//leagues_NBA_1970_games_games.csv
			fileName = folder+"mlb"+y+".txt";
		}
		File f = new File(fileName);
		if (!f.exists()){
			if (sport==1){
				fileName = folder+"nba"+y+".txt";
				//System.out.println("fileName: "+fileName);
				f = new File(fileName);
				if (!f.exists()){
					continue;
				}else{
					type=4;
				}
			}else{
				//System.out.println("[ERROR] NO EXISTE: "+fileName);
				continue;
			}
		}
		//System.out.println("a cargar: "+fileName);	
		ArrayList<Match> matches = readFileFromDiskMLB(fileName);
		for (int i=0;i<matches.size();i++) matches1.add(matches.get(i));							
	}
	
	return matches1;
}	
	
public static ArrayList<Match> readAndReorderNHL(String folder,int y1,int y2,int type,int sport){
		
		ArrayList<Match> results  = new ArrayList<Match>();
		
		for (int y=y1;y<=y2;y++){
			ArrayList<Match> matches = SportsInsight.readFromDisk(folder, y, y, 1, sport);
			//System.out.println("total : "+y+" "+matches.size());
			ArrayList<Match> matches2 = SportsInsight.reorderAddHomeAwayOdds(matches);
			for (int i=0;i<matches2.size();i++){
				results.add(matches2.get(i));
			}			
		}
		
		return results;
	}	

public static ArrayList<Match> readAndReorderMLB(String folder,int y1,int y2,int type,int sport){
	
	ArrayList<Match> results  = new ArrayList<Match>();
	
	for (int y=y1;y<=y2;y++){
		ArrayList<Match> matches = SportsInsight.readFromDiskMLB(folder, y, y, 1, sport);
		//System.out.println("total : "+y+" "+matches.size());
		ArrayList<Match> matches2 = SportsInsight.reorderAddHomeAwayOdds(matches);
		for (int i=0;i<matches2.size();i++){
			results.add(matches2.get(i));
		}			
	}
	
	return results;
}	

public static void main(String[] args) {
		
		String folder = "C:\\nhl\\sportsInsight\\";
		
		ArrayList<Match> matches = SportsInsight.readAndReorderNHL(folder, 2005, 2016, 1, 0);
		for (int i=0;i<matches.size();i++){
			//System.out.println(matches.get(i).toString());
		}
		System.out.println("total : "+matches.size());
		
		/*for (int y=2016;y<=2016;y++){
			ArrayList<Match> matches = OddPortal2.readFromDisk(folder, 2010, 2016, 1, 0);
			ArrayList<Match> matches2 = OddPortal2.reorderAddHomeAwayOdds(matches);
			for (int i=0;i<matches2.size();i++){
				//System.out.println(matches2.get(i).toString());
			}
			System.out.println(matches2.size());
		}*/

	}

}
