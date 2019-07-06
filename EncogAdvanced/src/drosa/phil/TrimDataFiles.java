package drosa.phil;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TrimDataFiles {


	private static boolean equalQuotes(ArrayList<Quote> data, int pos) {
		// TODO Auto-generated method stub
		Quote actual = data.get(pos);
		Quote last   = data.get(pos-1);
		
		int openDiff  = TradingUtils.getPipsDiff(actual.getOpen(), last.getOpen());
		int closeDiff = TradingUtils.getPipsDiff(actual.getClose(), last.getClose());
		int highDiff  = TradingUtils.getPipsDiff(actual.getHigh(), last.getHigh());
		int lowDiff   = TradingUtils.getPipsDiff(actual.getLow(), last.getLow());
		
		if (openDiff!=0) return false;
		if (closeDiff!=0) return false;
		if (highDiff!=0) return false;
		if (lowDiff!=0) return false;
		
		return true;
	}
	
	public static void trimFile(String fileSource,String fileTarget){
		try{
			ArrayList<Quote>   data  = new ArrayList<Quote>();
			DAO.retrieveData(data,fileSource, DataProvider.DUKASCOPY_FOREX,0);
			
			PrintWriter writer;		
			writer = new PrintWriter(fileTarget, "UTF-8");
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				if (i==0 || !equalQuotes(data,i)){
					Quote q = data.get(i);
					cal.setTime(q.getDate());

					String dateStr = DateUtils.getDukasFormat(cal);
					String OHLC = PrintUtils.getOHLC(q);					
					writer.println(dateStr + " "+OHLC);	
				}
			}
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "C:\\fxdata";
		
		File folder = new File(path);
	    File[] listOfFiles = folder.listFiles();

	    for (File file : listOfFiles) {
	    	if (file.isFile() && file.getName().contains("_1s_data")){                            
	    		//System.out.println(file.getAbsolutePath()+" "+file.getName());
	    		String fileSource = file.getAbsolutePath();
	    		String fileTarget = fileSource.substring(0, fileSource.length()-4)+"_trim.csv";
	    		System.out.println("filesource filetarget: "+fileSource+" "+fileTarget);
	    		TrimDataFiles.trimFile(fileSource, fileTarget);
	    		
	      }
	    }

	}

}
