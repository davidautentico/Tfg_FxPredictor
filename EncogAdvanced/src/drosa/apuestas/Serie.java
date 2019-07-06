package drosa.apuestas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import drosa.utils.PrintUtils;

public class Serie {
	
	String team1;
	String team2;
	int initialDay = 0;//año*365+mes*30+dia
	String serieDescr = "NONE";
	
	ArrayList<Integer> results = new ArrayList<Integer>();
	ArrayList<Double> homeOdds = new ArrayList<Double>();
	ArrayList<Double> awayOdds = new ArrayList<Double>();
	ArrayList<String> dates = new ArrayList<String>();

	
	
	
	public String getSerieDescr() {
		return serieDescr;
	}

	public void setSerieDescr(String serieDescr) {
		this.serieDescr = serieDescr;
	}

	public int getInitialDay() {
		return initialDay;
	}

	public void setInitialDay(int initialDay) {
		this.initialDay = initialDay;
	}

	public String getTeam1() {
		return team1;
	}

	public void setTeam1(String team1) {
		this.team1 = team1;
	}

	public String getTeam2() {
		return team2;
	}

	public void setTeam2(String team2) {
		this.team2 = team2;
	}
	
	

	public ArrayList<String> getDates() {
		return dates;
	}

	public void setDates(ArrayList<String> dates) {
		this.dates = dates;
	}

	public ArrayList<Double> getHomeOdds() {
		return homeOdds;
	}

	public void setHomeOdds(ArrayList<Double> homeOdds) {
		this.homeOdds = homeOdds;
	}

	public ArrayList<Double> getAwayOdds() {
		return awayOdds;
	}

	public void setAwayOdds(ArrayList<Double> awayOdds) {
		this.awayOdds = awayOdds;
	}

	public ArrayList<Integer> getResults() {
		return results;
	}

	public void setResults(ArrayList<Integer> results) {
		this.results = results;
	}
	
	public String toString(){
		String resStr = "";
		String homeOddsStr = "";
		String awayOddsStr = "";
		String datesStr = "";
		for (int i=0;i<this.results.size();i++){
			resStr+=results.get(i)+" ";
			homeOddsStr+=PrintUtils.Print2dec(homeOdds.get(i),false)+" ";
			awayOddsStr+=PrintUtils.Print2dec(awayOdds.get(i),false)+" ";
			datesStr+=dates.get(i)+" ";
		}
		return team1+" "+team2+" "+resStr.trim()+" || "+datesStr+" ||"+homeOddsStr.trim()+" || "+awayOddsStr.trim()
		;
		
	}
	
	public String toString2(){
		String resStr = "";
		String homeOddsStr = "";
		String awayOddsStr = "";
		String datesStr = "";
		for (int i=0;i<this.results.size();i++){
			resStr+=results.get(i)+" ";
			homeOddsStr+=PrintUtils.Print2dec(homeOdds.get(i),false)+" ";
			awayOddsStr+=PrintUtils.Print2dec(awayOdds.get(i),false)+" ";
			datesStr+=dates.get(i)+" ";
		}
		return team1+" "+team2+" "+resStr.trim()+" || "+datesStr//+" ||"+homeOddsStr.trim()+" || "+awayOddsStr.trim()
		;
		
	}

	public static ArrayList<Serie> readFromDisk(String fileName) {
		
		ArrayList<Serie> data = new ArrayList<Serie>();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    Match matchItem = null;
	    Serie serieItem = null;
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero

	        int i=0;
	        int posOdds = 0;
	        String lastAwayTeam = "";
	        Serie newSerie = null;
	        while((line=br.readLine())!=null){	        	
	        	if (i>=1){	 	        			
	        		matchItem = decodeMatchLine(line);//queda esto
	        		
	        		if (matchItem!=null){
	        			if (!matchItem.getAwayTeam().equalsIgnoreCase(lastAwayTeam)){ //se cambia de equipo away, es una nueva serie
	        				//crear nueva serie y metemos en datos
	        				newSerie = new Serie();
	        				newSerie.setTeam1(matchItem.getHomeTeam());
	        				newSerie.setTeam2(matchItem.getAwayTeam());
	        				lastAwayTeam = matchItem.getAwayTeam(); 
	        				data.add(newSerie);
	        			}
	        			//añadimos resultado de este partido
	        			int res = 1;
	        			if (matchItem.getWinTeam()==-1) res = -1;
	        			newSerie.getResults().add(res);
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

	//PENDIENTE
	private static Match decodeMatchLine(String line) {
		if (line.split(",").length<6
				|| line.toUpperCase().contains("W/L")
				|| line.toUpperCase().contains("Streak")
				) return null;
		
		String[] values = line.split(",");
		
		Match match = new Match();
		
		match.setHomeTeam(values[4]);
		match.setAwayTeam(values[6]);
		
		String res = values[7];
		
		match.setWinTeam(1);
		if (res.startsWith("L") || res.startsWith("l")){
			match.setWinTeam(-1);
		}
		
		return match;
	}

	private static Serie decodeLine(String line) {
		
		
		
		return null;
	}
	
	

}
