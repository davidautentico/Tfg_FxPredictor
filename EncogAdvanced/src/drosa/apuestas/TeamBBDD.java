package drosa.apuestas;

import java.io.File;
import java.util.ArrayList;

public class TeamBBDD {

ArrayList<TeamResults> teams = new ArrayList<TeamResults>();
	
    
	public TeamBBDD(){
	
	}
	public TeamBBDD(ArrayList<TeamResults> teams){
		this.teams = teams;
	}
	
	public ArrayList<TeamResults> getTeams() {
		return teams;
	}

	public void setTeams(ArrayList<TeamResults> teams) {
		this.teams = teams;
	}
	
	public int findTeam(String teamName){
		for (int i=0;i<teams.size();i++){
			if (teams.get(i).getTeam().equalsIgnoreCase(teamName))
				return i;
		}
		return -1;
	}

	public void addTeamResults(ArrayList<TeamResults> teamResults){
		boolean found = false;
		for (int i=0;i<teamResults.size();i++){//para cada equipo
			TeamResults tr = teamResults.get(i);
			String teamName = tr.getTeam();
			ArrayList<Integer> results = tr.getResults();
			//lo busco
			int idx = findTeam(teamName);
			if (idx>=0){//existe añado los resultados
				for (int j=0;j<results.size();j++){					
					teams.get(idx).getResults().add(results.get(j));
					/*if (teams.get(idx).getTeam().equalsIgnoreCase("Real Madrid")){
						System.out.println(results.get(j));
					}*/
				}
			}
			else{//no existe añado todo
				teams.add(tr);
			}
		}
		
	}
	
	public static void printBBDD(TeamBBDD bbdd){
		for (int i=0;i<bbdd.teams.size();i++){
			TeamResults tr = bbdd.getTeams().get(i);
			System.out.println(tr.toString());
		}
	}
	public static void fillTeamBBDD1(TeamBBDD bbdd,String folder,ArrayList<String> leagues){
		
		for (int i=0;i<leagues.size();i++){
			String prefix = leagues.get(i);
			for (int year = 1992;year<=2014;year++){
				String fileName = folder+prefix+"_"+year+".csv";
				File f = new File(fileName);
				if (!f.exists()){
					//System.out.println("no existe: "+fileName);
					continue;
				}
				//System.out.println("file: "+fileName);
				//leer partidos
				ArrayList<TeamResults> teamResults = FirstTry.calculateTeamResults(fileName,1);
	    		//insertarlos
				bbdd.addTeamResults(teamResults);
			}
		}
	}
	
	public static void fillTeamBBDD2(TeamBBDD bbdd,String path){
		File folder = new File(path);
	    File[] listOfFiles = folder.listFiles();

	    for (File file : listOfFiles) {
	    	if (file.isFile() && file.getName().contains(".csv")){                            
	    		System.out.println(file.getAbsolutePath()+" "+file.getName());
	    		String fileName = file.getAbsolutePath();	   
				//leer partidos
				ArrayList<TeamResults> teamResults = FirstTry.calculateTeamResults(fileName,2);
	    		//insertarlos
				bbdd.addTeamResults(teamResults);
			}
		}


	}
	
	public static void merge(TeamBBDD b1,TeamBBDD b2){
		for (int i = 0;i<b2.getTeams().size();i++){
			b1.getTeams().add(b2.getTeams().get(i));
		}
	}
	

	void printInfo() {
		// TODO Auto-generated method stub
		System.out.println("Teams: "+teams.size());
	}
	
	public String printTeamInfo(String team,int lastN) {
		// TODO Auto-generated method stub
		String res = "";
		int idx = findTeam(team);
		if (idx>=0){
			TeamResults teamResults = teams.get(idx);
			res = teamResults.toString(lastN);
			System.out.println(team+" "+teamResults.toString(lastN));
		}
		return res;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folder1 = "c:\\futbol\\";
		String folder2 = "C:\\futbolfdr\\";
		
		ArrayList<String> leagues = new ArrayList<String>();
		leagues.add("SP1");
		leagues.add("SP2");
		leagues.add("SC0");
		leagues.add("SC1");
		leagues.add("SC2");
		leagues.add("SC3");
		leagues.add("E0");
		leagues.add("E1");
		leagues.add("E2");
		leagues.add("E3");
		leagues.add("EC");
		leagues.add("D1");
		leagues.add("D2");
		leagues.add("I1");
		leagues.add("I2");
		leagues.add("F1");
		leagues.add("F2");
		leagues.add("N1");
		leagues.add("B1");
		leagues.add("P1");
		leagues.add("T1");
		leagues.add("G1");
		
		TeamBBDD bbdd1 = new TeamBBDD();
		TeamBBDD bbdd2 = new TeamBBDD();
		//bbdd tipo 1 football-data
		TeamBBDD.fillTeamBBDD1(bbdd1, folder1, leagues);
		TeamBBDD.fillTeamBBDD2(bbdd2, folder2);
		bbdd1.printInfo();
		bbdd2.printInfo();
		//TeamBBDD.printBBDD(bbdd);
		/*String team = "Barcelona";
		String header = team;
		String pattern = FirstTry.getLastMatches(folder1,leagues,team,2014,4);
		
		FirstTry.printBBDDProbs(header,bbdd, FirstTry.decodePattern(pattern), 1);
		FirstTry.printBBDDProbs(header,bbdd, FirstTry.decodePattern(pattern), 2);*/
		
		//FirstTry.printProbabilitiesLeagueBBDD(bbdd1,folder1,leagues,"SP1",2014,4,1);
		//FirstTry.printProbabilitiesLeagueBBDD(bbdd2,folder2,leagues,"PL1",2014,4,1);
		//FirstTry.printBBDDProbs("b1",bbdd1, FirstTry.decodePattern("1 2 1 2"), 1);
		//FirstTry.printBBDDProbs("b2",bbdd2, FirstTry.decodePattern("1 2 1 2"), 1);
		
		TeamBBDD.merge(bbdd1, bbdd2);
		FirstTry.printBBDDProbs("b1",bbdd1, FirstTry.decodePattern("1 2"), 1);
	}
	

	

}
