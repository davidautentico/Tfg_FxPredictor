package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.TradingUtils;

public class DataAnalysis {

	
	
	public static void calculateHLtime(ArrayList<Quote> data){
		
		ArrayList<Integer> mins = new ArrayList<Integer>();
		ArrayList<Integer> maxs = new ArrayList<Integer>();
		
		for (int i=0;i<=23;i++){
			mins.add(0);
			maxs.add(0);
		}
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		double min =  999999;
		double max = -999999;
		int hmin=-1;
		int hmax=-1;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			
			cal.setTime(q.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (actualDay!=lastDay){
				if (hmax>=0){
					int minAcc = mins.get(hmin);
					int maxAcc = maxs.get(hmax);
					mins.set(hmin, minAcc+1);
					maxs.set(hmax, maxAcc+1);
				}
				min = 99999;
				max = -99999;
				hmin=-1;
				hmax=-1;
				
				lastDay = actualDay;
			}
			
			if (q.getHigh()>max){
				hmax = h;
				max = q.getHigh();
			}
			if (q.getLow()<min){
				hmin = h;
				min = q.getLow();
			}
		}
		for (int i=0;i<=23;i++){
			System.out.println("h min max "+i+" "+mins.get(i)+" "+maxs.get(i));
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileHour = "c:\\fxdata\\EURUSD_Hourly_Bid_2010.01.01_2013.09.05.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(fileHour, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		
  		DataAnalysis.calculateHLtime(data);
	}

}
