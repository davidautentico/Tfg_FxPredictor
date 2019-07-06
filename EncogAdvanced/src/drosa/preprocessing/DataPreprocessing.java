package drosa.preprocessing;

import java.util.ArrayList;

import drosa.finances.Quote;

public class DataPreprocessing {

	public static ArrayList<Quote> calculateDifferences(ArrayList<Quote> data){
			ArrayList<Quote> difs = new ArrayList<Quote>();
			System.out.println("total data before prepoc: "+data.size());
			for (int i= 0;i<data.size()-1;i++){
				Quote q2 = data.get(i+1);
				Quote q1 = data.get(i);
				
				double open  = q2.getOpen()-q1.getOpen();
				double close = q2.getClose()-q1.getClose();
				double low =   q2.getLow()-q1.getLow();
				double high =  q2.getHigh()-q1.getHigh(); 
				long volume =   q2.getVolume()-q1.getVolume();
				double adjclose = q2.getAdjClose()-q1.getAdjClose();
				
				Quote q3 = new Quote();
				q3.setAdjClose(adjclose);
				q3.setClose(close);
				q3.setHigh(high);
				q3.setLow(low);
				q3.setOpen(open);
				q3.setVolume(volume);
				
				difs.add(q3);
			}
			System.out.println("total dataafterprepoc: "+difs.size());
		return difs;
	}
	
	public static ArrayList<Quote> calculateLogDifferences(ArrayList<Quote> data){
		ArrayList<Quote> difs = new ArrayList<Quote>();
		//
		return difs;
	}
	
	
}
