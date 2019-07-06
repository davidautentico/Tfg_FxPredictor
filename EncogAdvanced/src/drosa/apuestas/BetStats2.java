package drosa.apuestas;

import java.util.ArrayList;

public class BetStats2 {
	

	ArrayList<Integer> results = new ArrayList<Integer>();

	
	
	
	public ArrayList<Integer> getResults() {
		return results;
	}

	public void setResults(ArrayList<Integer> results) {
		this.results = results;
	}



	public double getWinProb(int lookBack) {
		
		int begin = results.size()-lookBack;
		
		if (begin<0) begin = 0;
		
		int wins = 0;
		int actualStreak = 0;
		for (int i=begin;i<results.size()-1;i++){
			
			int res = results.get(i);
			
			if (res==1) wins++;			
		}
		
		return wins*100.0/lookBack;
	}

}
