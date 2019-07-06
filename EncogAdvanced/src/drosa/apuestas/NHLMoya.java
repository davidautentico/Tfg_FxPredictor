package drosa.apuestas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import drosa.utils.PrintUtils;

public class NHLMoya {

	public static ArrayList<Match> readFromDisk(String fileName,String separator){
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
	        while((line=br.readLine())!=null){
	        	if (i>=1){	 
	        		String[] values = line.split(separator);
	        		String homeTeam = values[0].trim();
	        		String awayTeam = values[1].trim();
	        		String date = values[2].trim();
	        		String selectedTeam = values[3].trim();
	        		String cuota = values[4].trim();
	        		String stake = values[5].trim();
	        		Match m = new Match();
	        		m.setHomeTeam(homeTeam);
	        		m.setAwayTeam(awayTeam);
	        		if (date.contains("Dec")){
	        			m.setYear(2016);
	        			m.setMonth(12);
	        			m.setDay(Integer.valueOf(date.substring(0, 2)));
	        		}else{
	        			m.setYear(2017);
	        			m.setMonth(1);
	        			m.setDay(Integer.valueOf(date.substring(0, 2)));
	        		}
	        		int bet = 1;
	        		if (awayTeam.equalsIgnoreCase(selectedTeam)) bet = -1;
	        		m.setBetTeam(bet);
	        		data.add(m);
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
	
	public static void test(String fileName,double factor){
		
		String fileMoya = "c:\\nhl\\moya.csv";
		ArrayList<Match> data = NHLMoya.readFromDisk(fileMoya, ";");
		//reverse
		ArrayList<Match> dataR = new ArrayList<Match>();
		for (int i=data.size()-1;i>=0;i--){
			dataR.add(data.get(i));
		}
		
		
		for (int i=0;i<dataR.size();i++){
			//System.out.println(dataR.get(i).toString());
		}
		
		String folder = "C:\\nhl\\";
		
		ArrayList<Match> testMatches = null;
		ArrayList<Match> historicalMatches = new ArrayList<Match>();
		testMatches = NHL.readFromDisk(folder, 2016, 2016, 3, 0);
		
		int totalBets = 0;
		int totalMoya = 0;
		int winsMoya = 0;
		int wins= 0;
		double cuotaBet = 0;
		double cuotaMoya = 0;
		HashMap<String,Integer> teamResults = new HashMap<String,Integer>();
		//for (int j=0;j<dataR.size();j++){
			//Match mo = dataR.get(j);
			historicalMatches.clear();
			teamResults.clear();
			HashMap<String,BetStats> teamBetStats = new HashMap<String,BetStats>();		
			for (int i=0;i<testMatches.size();i++){
				Match m = testMatches.get(i);
				String hTeam = m.getHomeTeam();
				String aTeam = m.getAwayTeam();
				int res = m.getHomeGoals()>m.getAwayGoals()?1:-1;
				
				int hStreak = 0;
				if (!teamResults.containsKey(hTeam)){
					teamResults.put(hTeam,0);
				}
				int aStreak = 0;
				if (!teamResults.containsKey(aTeam)){
					teamResults.put(aTeam,0);
				}
				
				hStreak = teamResults.get(hTeam);
				aStreak = teamResults.get(aTeam);
				BetStats hStats = teamBetStats.get(hTeam);
				BetStats aStats = teamBetStats.get(aTeam);
				
				if ((m.getMonth()==12 && m.getDay()>=13) || m.getYear()==2017){
					boolean found = false;
					boolean isBet = false;
					for (int j=0;j<dataR.size();j++){
						Match mo = dataR.get(j);
						
						//System.out.println(mo.toString()+" VERSUS "+m.toString());
						if (isSameMatch(mo,m)
								
								){
							if (true
									&& (hStreak<=-1 && aStreak>=1) || (hStreak>=1 && aStreak<=-1)){
								//System.out.println(mo.toString()+" ||| "+m.toString()+" |||| "+hStreak+" "+aStreak+" || "+hStats.printStats()+" vs "+aStats.printStats()
									
										//);
								if (hStreak<=-1 && res==1) winsMoya++;
								if (aStreak<=-1 && res==-1) winsMoya++;
								totalMoya++;
								
								if (hStreak<=-1 && hStats.getAvgNeg()>factor){
									//totalBets++;
									isBet = true;
								}
								if (aStreak<=-1 && aStats.getAvgNeg()>factor){
									//totalBets++;
									isBet = true;
								}
							}
							found = true;
							break;
						}										
					}
					
					if (!found
							&& (hStreak<0 || aStreak<0)
							&& ((hStreak<=-1 && aStreak>=1 && hStats.getAvgNeg()>factor)|| (hStreak>=1 && aStreak<=-1 && aStats.getAvgNeg()>factor))
							){						
						//System.out.println("[NO BET] "+m.toString()+" |||| "+hStreak+" "+aStreak+" || "+hStats.printStats()+" vs "+aStats.printStats());
						isBet = true;
					}
					
					if (isBet){
						if (hStreak<=-1){
							//
						}
						if (hStreak<=-1 && res==1) wins++;
						if (aStreak<=-1 && res==-1) wins++;
						totalBets++;
					}
					
				}
				//actualizamos streak
				if (res==1){
					if (hStreak>=0){
						teamResults.put(hTeam, teamResults.get(hTeam)+1);
					}else{
						teamResults.put(hTeam, 1);
					}
					if (aStreak<=0){
						teamResults.put(aTeam, teamResults.get(aTeam)-1);
					}else{
						teamResults.put(aTeam, -1);
					}
				}else if (res==-1){
					if (hStreak<=0){
						teamResults.put(hTeam, teamResults.get(hTeam)-1);
					}else{
						teamResults.put(hTeam, -1);
					}
					if (aStreak>=0){
						teamResults.put(aTeam, teamResults.get(aTeam)+1);
					}else{
						teamResults.put(aTeam, 1);
					}
				}
				
				historicalMatches.add(m);
				StreakUtils.calculateStreaksTeam(historicalMatches, teamBetStats,hTeam,false);
				StreakUtils.calculateStreaksTeam(historicalMatches, teamBetStats,aTeam,false);
			}
			
			System.out.println(totalBets+" "+PrintUtils.Print2dec(wins*100.0/totalBets, false)
					+" "+totalMoya+" "+PrintUtils.Print2dec(winsMoya*100.0/totalMoya, false)
					);
	}
	private static boolean isSameMatch(Match mo, Match m) {
		// TODO Auto-generated method stub
		if (!mo.getHomeTeam().equalsIgnoreCase(m.getHomeTeam())) return false;
		if (!mo.getAwayTeam().equalsIgnoreCase(m.getAwayTeam())) return false;
		if (mo.getYear()!=m.getYear()) return false;
		
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (double factor =0.10;factor<=0.90;factor+=0.01){
			NHLMoya.test("",factor);
		}
	}

}
