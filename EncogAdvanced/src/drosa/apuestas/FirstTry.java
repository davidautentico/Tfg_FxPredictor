package drosa.apuestas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import drosa.utils.PrintUtils;

public class FirstTry {

	public static String getLastMatches(String folder,ArrayList<String> leagues,String team,int year,int n,int type) {
		for (int i=0;i<leagues.size();i++){
			String prefix = leagues.get(i);
			String fileName = folder+prefix+"_"+year+".csv";
			File f = new File(fileName);
			if (!f.exists()){
				//System.out.println("no existe: "+fileName);
				continue;
			}
			String matches="";
			ArrayList<TeamResults> teamResults = calculateTeamResults(fileName,type);
			//System.out.println(teamResults.size());
			for (int l=0;l<teamResults.size();l++){
				TeamResults tr = teamResults.get(l);
				if (tr.getTeam().equalsIgnoreCase(team)){
					for (int j = tr.getResults().size()-n;j<=tr.getResults().size()-1;j++){
						matches+=(String.valueOf(tr.getResults().get(j)))+" ";
					}
					return matches;
				}
			}
		}
		return "";
		
	}
	
	public static ArrayList<Integer> patternMatch(ArrayList<Integer> rs,ArrayList<Integer> find){
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		String seq = "";
		int len = find.size();
		for (int i=0;i<=rs.size()-2;i++){
			int end = i+len-1;
			seq = "";
			if (end<=rs.size()-2){
				boolean ok = true;
				for (int j=i,f=0;j<=end && ok;j++){
					seq += rs.get(j)+" ";
					if (rs.get(j)!=find.get(f)){
						ok = false;
					}
					f++;
				}
				if (ok){
					//System.out.println(seq+" "+rs.get(i+len));
					indexes.add(i+len);
				}
			}
		}
		
		return indexes;
	}
	
	/**
	 * Los resultados son siempre desde el punto de vista del equipo de casa
	 * @param fileName
	 * @param pattern
	 * @param expected
	 * @param type
	 * @return
	 */
	public static String calculateProbs(String fileName,ArrayList<Integer> pattern,int expected,int type){
		//primero agrupo resultados por equipo
		ArrayList<TeamResults> teamResults = calculateTeamResults(fileName,type);
		//System.out.println("teamresults: "+teamResults.size());
		int cases = 0;
		int wins = 0;
		for (int i=0;i<teamResults.size();i++){
			TeamResults r  = teamResults.get(i);
			ArrayList<Integer> rs = r.getResults();
			//calculo matches del patron
			ArrayList<Integer> indexes = patternMatch(rs,pattern);//indices= valor siguiente
			//cases+=indexes.size();
			//if (indexes.size()>0)
				//System.out.println(indexes.size());//+" "+r.toString());
			for (int x=0;x<indexes.size();x++){
				int idx = indexes.get(x);
				if (
						(isMatchAtHomeAway(expected,1) && isMatchAtHomeAway(rs.get(idx),1))//juega en casa
						|| (isMatchAtHomeAway(expected,2) && isMatchAtHomeAway(rs.get(idx),2))
						){//juega fuera
					cases++;
					//System.out.println("new case");
					//1-3
					if (expected==5){
						if (rs.get(idx)==1 || rs.get(idx)==3) wins++; //1X
					}
					if (expected==6){
						if (rs.get(idx)==2 || rs.get(idx)==4) wins++; //2X
					}
					
					if (expected<=4){
						if (expected==rs.get(idx)) wins++;
					}
				}
				
			}
		}
		double perWin = wins*100.0/cases;
		//System.out.println(cases+" "+PrintUtils.Print2(perWin));
		
		return cases+"-"+wins;
	}
	
	private static boolean isMatchAtHomeAway(int value, int home) {
		// TODO Auto-generated method stub
		if (home==1){
			if (value==1 || value==-1 || value==3 || value==5) return true;
			else return false;
		}else{
			if (value==2 || value==-2 || value==4 || value==6) return true;
			else return false;
		}
	}

	public static void printBBDDProbs(String header,TeamBBDD bbdd,ArrayList<Integer> pattern,int targetValue){
		//primero agrupo resultados por equipo
		ArrayList<TeamResults> teamResults = bbdd.getTeams();
		int cases = 0;
		int wins = 0;
		for (int i=0;i<teamResults.size();i++){
			TeamResults r  = teamResults.get(i);
			ArrayList<Integer> rs = r.getResults();
			//calculo matches del patron
			ArrayList<Integer> indexes = patternMatch(rs,pattern);
			//cases+=indexes.size();
			//System.out.println(indexes.size()+" "+r.toString());
			for (int x=0;x<indexes.size();x++){
				int idx = indexes.get(x);
				if (targetValue==3){
					cases++;
					if (rs.get(idx)>=0){
						wins++;
					}
				}else if (targetValue==-3){
					cases++;
					if (rs.get(idx)<=0){
						wins++;
					}
				}else{
					/*if (rs.get(idx)==0){
						cases++;
						wins++;
					}else*/ if (Math.abs(targetValue)==Math.abs(rs.get(idx))){
						cases++;
						if (rs.get(idx)==targetValue){												
							wins++;
						}
					}
					//System.out.println(cases+" "+wins);
				}
			}
		}
		double odds = 1.0/(wins*1.0/cases);
		System.out.println(header+" "+pattern+" "+targetValue+" "+cases+" "+wins
				+" "+PrintUtils.Print2(wins*100.0/cases)+" "+PrintUtils.Print2(odds));
	}

	
	public static ArrayList<TeamResults> calculateTeamResults(String fileName,int type){
		
		ArrayList<Match> matches = Match.readFromDisk(fileName,type);
		ArrayList<TeamResults> teamResults = new ArrayList<TeamResults>();
		TeamResults tres = null;
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			int indexH = TeamResults.findTeam(teamResults, m.getHomeTeam());
			int indexA = TeamResults.findTeam(teamResults, m.getAwayTeam());
			int resH = 0;
			int resA = 0;
			
			if (m.getHomeGoals()>m.getAwayGoals()){
				resH = 1;//casa siempre 1
				resA = -2;//fuera siempre 2
			}
			if (m.getHomeGoals()<m.getAwayGoals()){
				resH = -1;
				resA = 2;
			}
			
			if (m.getHomeGoals()==m.getAwayGoals()){
				resH = 3;//empate en casa
				resA = 4;//empate fuera
			}
			//HOME
			if (indexH==-1){
				tres = new TeamResults();
				tres.setTeam(m.homeTeam);
				tres.getResults().add(resH);
				teamResults.add(tres);
			}else{
				tres = teamResults.get(indexH);
				tres.getResults().add(resH);
			}
			
			//AWAY
			if (indexA==-1){
				tres = new TeamResults();
				tres.setTeam(m.awayTeam);
				tres.getResults().add(resA);
				teamResults.add(tres);
			}else{
				tres = teamResults.get(indexA);
				tres.getResults().add(resA);
			}
		}
		return teamResults;
	}
	
	public static void checkBBDDErrors(String folder,ArrayList<String> leagues,int type){
		for (int i=0;i<leagues.size();i++){
			String prefix = leagues.get(i);
			for (int year = 1980;year<=2014;year++){
				String fileName = folder+prefix+"_"+year+".csv";
				File f = new File(fileName);
				if (!f.exists()){
					//System.out.println("no existe: "+fileName);
					continue;
				}
				//System.out.println("file: "+fileName);
				if (checkFileError(fileName,prefix,type)){
					System.out.println("[ERROR] fileName: "+fileName);
				}
			}
		}
	}
	public static boolean checkFileError(String fileName,String league,int type){
		
		boolean error = false;
		
		ArrayList<Match> matches = Match.readFromDisk(fileName,type);
		
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			if (!m.getLeague().equalsIgnoreCase(league))
			return true;
		}
		
		return error;
	}
	

	static ArrayList<Integer> decodePattern(String pattern) {
		// TODO Auto-generated method stub
		ArrayList<Integer> pat = new ArrayList<Integer>();
		String str[] =pattern.trim().split(" ");
		for (int i=0;i<str.length;i++)
			pat.add(Integer.valueOf(str[i]));
		
		return pat;
	}
	
	public static void printLeagueProbabilities(String folder,String pattern,ArrayList<String> leagues,int targetValue,int type){
		int cases = 0;
		int wins = 0;
		ArrayList<Integer> patternInt = decodePattern(pattern);
		for (int i=0;i<leagues.size();i++){
			String prefix = leagues.get(i);
			for (int year = 1992;year<=2014;year++){
				String fileName = folder+prefix+"_"+year+".csv";
				File f = new File(fileName);
				if (!f.exists()){
					//System.out.println("no existe: "+fileName);
					continue;
				}
				
				String probs = calculateProbs(fileName,patternInt,targetValue,type);
				cases += Integer.valueOf(probs.split("-")[0]);
				wins  += Integer.valueOf(probs.split("-")[1]);
			}
		}
		double odds = 1.0/(wins*1.0/cases);
		System.out.println(pattern+" "+targetValue+" "+cases+" "+wins
				+" "+PrintUtils.Print2(wins*100.0/cases)+" "+PrintUtils.Print2(odds));
	}
	
	public static void printProbabilities(String folder,String pattern,ArrayList<String> leagues,int targetValue,int type){
		int cases = 0;
		int wins = 0;
		ArrayList<Integer> patternInt = decodePattern(pattern);
		for (int i=0;i<leagues.size();i++){
			String prefix = leagues.get(i);
			for (int year = 1992;year<=2014;year++){
				String fileName = folder+prefix+"_"+year+".csv";
				File f = new File(fileName);
				if (!f.exists()){
					//System.out.println("no existe: "+fileName);
					continue;
				}
				
				String probs = calculateProbs(fileName,patternInt,targetValue,type);
				cases += Integer.valueOf(probs.split("-")[0]);
				wins  += Integer.valueOf(probs.split("-")[1]);
			}
		}
		double odds = 1.0/(wins*1.0/cases);
		System.out.println(pattern+" "+targetValue+" || "+cases+" "+wins
				+" "+PrintUtils.Print2(wins*100.0/cases)+" "+PrintUtils.Print2(odds));
	}
	
	public static void printProbabilitiesLeague(String folder,ArrayList<String> leagues,String league,int year,
			int numMatches,int type){
		
		ArrayList<String> teams = getTeamNames(folder,league,year,type);
		System.out.println(teams.size());
		for (int i=0;i<teams.size();i++){
			String team = teams.get(i);
			String pattern = getLastMatches(folder,leagues,team,year,numMatches,type);
			System.out.println("****"+team+"****");
			printProbabilities(folder,pattern,leagues,1,type);
			printProbabilities(folder,pattern,leagues,2,type);
		}
	}
	
	public static void printProbabilitiesLeagueBBDD(TeamBBDD bbdd,String folder,
			ArrayList<String> leagues,String league,int year,
			int numMatches,int type){
		
		ArrayList<String> teams = getTeamNames(folder,league,year,type);
		//System.out.println(teams.size());
		for (int i=0;i<teams.size();i++){
			String team = teams.get(i);
			String pattern = getLastMatches(folder,leagues,team,year,numMatches,type);
			FirstTry.printBBDDProbs(team,bbdd, FirstTry.decodePattern(pattern), 1);
			FirstTry.printBBDDProbs(team,bbdd, FirstTry.decodePattern(pattern), 2);
		}
	}
	
	private static ArrayList<String> getTeamNames(String folder, String prefix,
			int year,int type) {
		
		ArrayList<String> teamNames = new ArrayList<String>();
		String fileName = folder+prefix+"_"+year+".csv";
		File f = new File(fileName);
		if (!f.exists()){
			return null;
		}
		ArrayList<Match> matches = Match.readFromDisk(fileName,type);
		for (int i=0;i<matches.size();i++){
			Match m = matches.get(i);
			teamNames.add(m.getHomeTeam());
			teamNames.add(m.getAwayTeam());
		}
		
		Set<String> set = new HashSet<String>();
		set.addAll(teamNames);
		teamNames.clear();
		teamNames.addAll(set);
		
		return teamNames;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folder = "D:\\apuestas\\futbol\\";
		
		
		int cases = 0;
		int wins = 0;
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
		String pattern1 = "-1 -1 2 3";
		String pattern2 = "-1 -2 -1 -2";
		//checkBBDDErrors(folder,leagues,1);
		//printProbabilitiesLeague(folder,leagues,"T1",2014,2);
		
		//String pattern = getLastMatches(folder,leagues,"Roma",2014,4);
		//printProbabilities(folder,pattern,leagues,1,1);
		printProbabilities(folder,pattern2,leagues,5,1);
	}

	


}
