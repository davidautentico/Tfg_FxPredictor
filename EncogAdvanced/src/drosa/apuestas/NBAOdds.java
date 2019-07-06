package drosa.apuestas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class NBAOdds {

	public static void main(String[] args) {
		String folder = "C:\\nba\\";
		//Equipos2016
		int y0 = 2016;
		ArrayList<Match> testMatches = NHL.readFromDisk(folder, y0, y0,1,1);
		HashMap<String,String> teams = NHL.extractTeams(testMatches);
		//printMap(teams);
		int y2 = 2015;
		int y1 = 2014;
		ArrayList<Match> matches = NHL.readFromDisk(folder, y1, y2,1,1);
		
		for (Entry<String, String> entry :  teams.entrySet()) {
			String team = entry.getKey();
		//String team = "Calgary Flames";
			for (int y=2016;y<=2016;y++){
				String header = y+" "+team;
				matches = NHL.readFromDisk(folder, y-3, y,1,1);
				BetStats betStats = new BetStats();
				NHL.getAvgStreakByTeam(matches, team,betStats);
				NHL.printStreakInfo(header, betStats);
			}
		}
	}

}
