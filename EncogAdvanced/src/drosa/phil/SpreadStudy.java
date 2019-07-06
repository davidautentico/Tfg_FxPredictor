package drosa.phil;

import java.io.File;
import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.Tick;
import drosa.utils.TradingUtils;

public class SpreadStudy {

	public static void study(String path,DataProvider dataProvider,String symbol){
		ArrayList<Integer> spreads = new ArrayList<Integer>(); 
		
		File folder = new File(path);
	    File[] listOfFiles = folder.listFiles();

	    int maxSpread = -999;
	    for (File file : listOfFiles) {
	    	if (file.getName().contains(symbol)
	    			&& file.getName().contains("2013")
	    			){                            
	    		//System.out.println(file.getAbsolutePath()+" "+file.getName());
	    		String fileName = file.getAbsolutePath();
	    		ArrayList<Tick> ticks	= DAO.retrieveTicks(fileName,dataProvider,100);
	    		//System.out.println("total ticks: "+ticks.size());
	    		for (int i=0;i<ticks.size();i++){
	    			Tick tick = ticks.get(i);
	    			int spread = TradingUtils.getPipsDiff5(tick.getAsk(), tick.getBid());
	    			//System.out.println(tick.toString()+" "+spread);
	    			spreads.add(spread);
	    			if (spread>1.0){
	    				System.out.println("New High spread: "+tick.toString()+" "+spread);
	    				maxSpread = spread;
	    			}
	    		}
	    	}
	    }
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//SpreadStudy.study("C:\\fxdata\\tickdata", "EURUSD");
		//SpreadStudy.study("C:\\fxdata\\tickdata\\pepperstone",DataProvider.PEPPERSTONE_FOREX, "EURUSD");
		SpreadStudy.study("C:\\fxdata\\tickdata\\dukascopy",DataProvider.DUKASCOPY_FOREX, "EURUSD");
	}

}
