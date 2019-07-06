package drosa.apuestas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class NHLOdds {
	
	public static ArrayList<String> readFromDisk(String fileName){
		ArrayList<String> lines = new ArrayList<String>();
		
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
	        while((line=br.readLine())!=null){
	        	if (i>=1){	 
	        			item = null;
	        			
	        		if (line.contains(",")) lines.add(line);
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
		
		return lines;
	}
	
	public static void readQuotes(String fileName,HashMap<String,ArrayList<Double>> quotesHome,HashMap<String,ArrayList<Double>> quotesAway){
		
		
		
		ArrayList<String> lines = NHLOdds.readFromDisk(fileName);
		
		//System.out.println("lines: "+lines.size());
		for (int i=0;i<lines.size();i++){
			String line = lines.get(i);
			if (!line.contains(",")) continue;
			
			try{
				String homeTeam = line.split(",")[0].split("-")[0].trim();
				String awayTeam = line.split(",")[0].split("-")[1].trim();
				String homeOdds = line.split(",")[1];
				String awayOdds = "0.0";
				double ho = 0.0;
				double ao = 0.0;
				boolean isNeed = true;
				if (line.split(",").length>2){
					awayOdds = line.split(",")[2];
					isNeed = false;
					ao = Double.valueOf(awayOdds);
				}
				
				if (isNeed){
					ho = Double.valueOf(homeOdds);
					ao = 1/(1.02-(1.0/ho));
					//System.out.println(PrintUtils.Print2dec(ho, false)+" "+PrintUtils.Print2dec(ao, false));
				}else if (homeOdds.trim().isEmpty()){					
					ho = 1/(1.02-(1.0/ao));
				}else{
					ho = Double.valueOf(homeOdds);
				}
				
				//System.out.println(homeTeam+" "+awayTeam+" || "+PrintUtils.Print2dec(ho, false)+" "+PrintUtils.Print2dec(ao, false));
			
				if (!quotesHome.containsKey(homeTeam)){
					quotesHome.put(homeTeam, new ArrayList<Double>());
					quotesHome.get(homeTeam).add(ho);
				}
				if (!quotesAway.containsKey(awayTeam)){
					quotesAway.put(awayTeam, new ArrayList<Double>());
					quotesAway.get(awayTeam).add(ao);
				}
			
			}catch(Exception e){
				System.out.println("[ERROR] "+line);
			}
		}
	}

	public static void main(String[] args) {
		//String folder = "C:\\nhl\\";
		//String folder = "C:\\nhl\\oddsportal\\";
		String folder = "C:\\nhl\\sportsInsight\\";
		/*HashMap<String,ArrayList<Double>> qh = new HashMap<String,ArrayList<Double>>();
		HashMap<String,ArrayList<Double>> qa = new HashMap<String,ArrayList<Double>>();
	
		NHLOdds.readQuotes(folder+"CUOTASNHL.txt",qh,qa);
		
		for (Entry<String, ArrayList<Double>> entry : qh.entrySet()) {
			String team = entry.getKey();
			
			ArrayList<Double>  array = entry.getValue();
			ArrayList<Double>  arrayA =  qa.get(team);
			double avgA = 0.00;
			if (arrayA!=null && arrayA.size()>0)
				avgA = MathUtils.averageD(arrayA, 0, 99999);
			
			double avg = MathUtils.averageD(array, 0, 99999);
			System.out.println("[HOME]" +team+" "+PrintUtils.Print2dec(avg, false)+" "+PrintUtils.Print2dec(avgA, false));
		}*/
		
		
		//Equipos2016
		int y0 = 2016;
		ArrayList<Match> testMatches = SportsInsight.readAndReorderNHL(folder, y0, y0, 1, 0);
		HashMap<String,String> teams = NHL.extractTeams(testMatches);
		//printMap(teams);
		int y2 = 2016;
		int y1 = 2016;
		ArrayList<Match> matches = SportsInsight.readAndReorderNHL(folder, y1, y2, 1, 0);
		
		for (Entry<String, String> entry :  teams.entrySet()) {
			String team = entry.getKey();
		//String team = "Calgary Flames";
			for (int y=2011;y<=2016;y++){
				String header = y+" "+team;
				matches = SportsInsight.readAndReorderNHL(folder, y, y, 1, 0);
				BetStats betStats = new BetStats();
				NHL.getAvgStreakByTeam(matches, team,betStats);
				NHL.setStreakInfo(header, betStats);
				System.out.println(y+" "+team+" || "+PrintUtils.Print2dec(betStats.getAvgNeg(), false)
						+" "+betStats.getWorseStreak()
						+" || "+PrintUtils.Print2dec((betStats.getWins()*100.0/betStats.getCases()), false)
						);
			}
		}

	}

}
