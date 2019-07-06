package drosa.apuestas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class OddPortal2 {
	
	private static Match decodeLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static ArrayList<Match> readFileFromDisk(String fileName, int type) {
		
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
	        br = new BufferedReader(fr);

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
	        while((line=br.readLine())!=null){
	        	if (i>=0){	 
	        			if (line.contains(":")){	        				
	        				int index = line.lastIndexOf(":");
	        				if (index>=5){
	        					homeTeam = line.substring(5, index-3).split("-")[0].trim().substring(0, 4);
	        					awayTeam = line.substring(5, index-3).split("-")[1].trim().substring(0, 4);
	        					
	        					homeGoals = Integer.valueOf(line.substring(index-2, index).trim());
	        					awayGoals = Integer.valueOf(line.substring(index+1, index+3).trim());
	        					lastMatchLine = i;
	        					/*if (awayTeam.trim().equalsIgnoreCase("Washington Capital")){
	        						System.out.println(line+" || "+awayTeam);
	        					}*/
	        				}
	        			}
	        			if (lastMatchLine>=0){
	        				if (i==lastMatchLine+1) homeOdds = Double.valueOf(line.trim());
	        				if (i==lastMatchLine+2) drawOdds = Double.valueOf(line.trim());
	        				if (i==lastMatchLine+3){
	        					awayOdds = Double.valueOf(line.trim());
	        					match = new Match();
	        					match.setHomeGoals(homeGoals);
	        					match.setAwayGoals(awayGoals);
	        					match.setHomeOdds(homeOdds);
	        					match.setDrawOdds(drawOdds);
	        					match.setAwayOdds(awayOdds);
	        					match.setHomeTeam(homeTeam.trim());
	        					match.setAwayTeam(awayTeam.trim());
	        					matches.add(match);
	        					
	        					//System.out.println(match.toString()+" || "+((1.0/homeOdds)+(1.0/drawOdds)+(1.0/awayOdds)));
	        				}
	        			}
	        		
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
		
		return matches;
	}
	
	
	


	public static ArrayList<Match> readFromDisk(String folder,int y1,int y2,int type,int sport){
		
		ArrayList<Match> matches1 = new ArrayList<Match>();
		
		for (int y=y1;y<=y2;y++){
			String fileName = folder+"nhl"+y+".txt";
			if (sport==1){
				//leagues_NBA_1970_games_games.csv
				//fileName = folder+"leagues_NBA_"+y+"_games_games.csv";
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
			ArrayList<Match> matches = readFileFromDisk(fileName,type);
			for (int i=0;i<matches.size();i++) matches1.add(matches.get(i));							
		}
		
		return matches1;
	}

	public static ArrayList<Match> reorderAddHomeAwayOdds(ArrayList<Match> matches){
	
		ArrayList<Match> array = new ArrayList<Match>();
		
		for (int i=matches.size()-1;i>=0;i--){
			Match m1 = matches.get(i);
			
			Match m2 = new Match();
			m2.copy(m1);
			
			double homeOddsAsian = (1.0-1.0/m1.getDrawOdds())*m1.getHomeOdds();
			double awayOddsAsian = (1.0-1.0/m1.getDrawOdds())*m1.getAwayOdds();
			
			m2.setHomeOdds(homeOddsAsian);
			m2.setAwayOdds(awayOddsAsian);
			m2.setDrawOdds(0.0);
			array.add(m2);
		}
		
		
		return array;
	}
	
	public static ArrayList<Match> readAndReorderNHL(String folder,int y1,int y2,int type,int sport){
		
		ArrayList<Match> results  = new ArrayList<Match>();
		
		for (int y=y1;y<=y2;y++){
			ArrayList<Match> matches = OddPortal2.readFromDisk(folder, y, y, 1, 0);
			ArrayList<Match> matches2 = OddPortal2.reorderAddHomeAwayOdds(matches);
			for (int i=0;i<matches2.size();i++){
				results.add(matches2.get(i));
			}			
		}
		
		return results;
	}

	public static void main(String[] args) {
		
		String folder = "C:\\nhl\\oddsportal\\";
		
		ArrayList<Match> matches = OddPortal2.readAndReorderNHL(folder, 2010, 2016, 1, 0);
		for (int i=0;i<matches.size();i++){
			//System.out.println(matches.get(i).toString());
		}
		System.out.println(matches.size());
		
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
