package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestHighsLows {

	
	public static void testHighLowsPersistence(ArrayList<QuoteShort> data,int h1,int h2,int nbar){
		
		int wins = 0;
		int losses = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=nbar;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			if (h>=h1 && h<=h2){ 
				QuoteShort maxMin = TradingUtils.getMaxMinShort(data,qm,calm, i-nbar, i-1);
				if (q.getHigh5()>=maxMin.getHigh5()){
					if (q1.getHigh5()>=q.getHigh5()){
						wins++;
					}else losses++;
				}
				if (q.getLow5()<=maxMin.getLow5()){
					if (q1.getLow5()>=q.getLow5()){
						wins++;
					}else losses++;
				}
			}
		}
		int total = wins+losses;
		double perWin = wins*100.0/total;
		System.out.println(nbar+" "+total+" "+PrintUtils.Print2(perWin));
	}
	
	public static void testHighLowsPersistence2(ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,int nbar1,int nbar2,int pips){
		
		int wins = 0;
		int losses = 0;
		int avgWin = 0;
		int avgLoss = 0;
		double doValue = -1;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=nbar1;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				doValue = q.getOpen5();
				lastDay = day;
			}
			
			if (h>=h1 && h<=h2){ 
				QuoteShort maxMin = TradingUtils.getMaxMinShort(data,qm,calm, i-nbar1, i-1);
				if (q.getHigh5()>=maxMin.getHigh5() && q.getHigh5()>doValue){
					QuoteShort maxMin1 = TradingUtils.getMaxMinShort(data,qm,calm, i+1, i+nbar2);
					int diffPips  = q.getClose5()-maxMin1.getLow5();
					int diffPipsN = maxMin1.getHigh5()-q.getClose5();
					avgWin+=(diffPips-diffPipsN);
					if (diffPips>=pips*10) wins++;
					else losses++;
				}
				if (q.getLow5()<=maxMin.getLow5() && q.getLow5()<doValue){
					QuoteShort maxMin1 = TradingUtils.getMaxMinShort(data,qm,calm, i+1, i+nbar2);
					int diffPips = maxMin1.getHigh5()-q.getClose5();
					int diffPipsN = q.getClose5()-maxMin1.getLow5();
					avgWin+=(diffPips-diffPipsN);
					if (diffPips>=pips*10) wins++;
					else losses++;
				}
			}
		}
		int total = wins+losses;
		double perWin = wins*100.0/total;
		System.out.println(h1+" "+nbar1+" "+nbar2+" "+pips+" "+total
				+" "+PrintUtils.Print2(perWin)
				+" "+PrintUtils.Print2(avgWin*1.0/total)
				);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.03.06.csv";
		
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		dataI 		= DAO.retrieveDataShort5m(path5m, DataProvider.DUKASCOPY_FOREX3);									
		//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
		TestLines.calculateCalendarAdjustedSinside(dataI);
		//TradingUtils.cleanWeekendDataSinside(dataI); 	
		dataS = TradingUtils.cleanWeekendDataS(dataI);  
		ArrayList<QuoteShort> data = null;
		ArrayList<QuoteShort> dataNoise = null;
		data = dataS;
		
	
		/*for (int nbar=1;nbar<=100000;nbar+=100){
			for (int h1=0;h1<=0;h1++){
				int h2=h1+9;
				TestHighsLows.testHighLowsPersistence(data, h1,h2,nbar);
			}
		}*/
		
		int pips  = 10;
		for (int y1=2009;y1<=2009;y1++){
			int y2 = y1+8;
			for (int nbar1=500;nbar1<=500;nbar1+=1){
				for (int nbar2=50000;nbar2<=50000;nbar2+=1000){
					for (int h1=0;h1<=0;h1++){
						int h2=h1+9;
						TestHighsLows.testHighLowsPersistence2(data,y1,y2, h1,h2,nbar1,nbar2,pips);
					}
				}
			}
		}
		
	}

}
