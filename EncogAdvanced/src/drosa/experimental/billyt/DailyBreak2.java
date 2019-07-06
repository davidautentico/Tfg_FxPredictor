package drosa.experimental.billyt;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class DailyBreak2 {
	
	public static void testBasic(ArrayList<QuoteShort> days,int tp,double factor,int minRange,int debug){
		
		
		int fails = 0;
		int count = 0;
		ArrayList<Integer> opens = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		int maxLosses = 0;
		int actualLosses = 0;
		int profit = 0;
		for (int i=1;i<days.size();i++){
			QuoteShort q1 = days.get(i-1);
			QuoteShort q = days.get(i);
			QuoteShort.getCalendar(cal, q);
			opens.add(q1.getOpen5());
			
			double ma = MathUtils.average(opens, opens.size()-14, opens.size()-1);
			double std = Math.sqrt(MathUtils.variance(opens, opens.size()-14, opens.size()-1));
			int range1 = q1.getHigh5()-q.getLow5();
			
			int dayWins=0;
			boolean tested = false;
			
			if (range1<=minRange*10) continue;
			/*System.out.println(q.toString()
					+" || "+q1.toString()
					+" "+(q.getHigh5()-q1.getHigh5())
					+" "+(q1.getLow5()-q.getLow5())
					);*/
			if (q.getHigh5()>=q1.getHigh5()
					//&& (q1.getHigh5()<ma-factor*std)
					){
				tested = true;
				if (q.getHigh5()>=q1.getHigh5()+tp*10){
					dayWins++;
					
					//System.out.println("High ok"
							//);
				}
			}
			if (q.getLow5()<=q1.getLow5()
					//&& (q1.getLow5()>ma+factor*std)
					){
				tested = true;
				if (q.getLow5()<=q1.getLow5()-tp*10){
					dayWins++;
					//System.out.println("low ok"
							//);
				}
			}
			
			if (tested){
				count++;
				String resStr = "[WIN]";
				if (dayWins==0){
					fails++;
					resStr = "[FAIL] "+" "+PrintUtils.Print2dec(range1*0.1, false);
					actualLosses++;
					if (actualLosses>=maxLosses) maxLosses = actualLosses;
					profit -= range1;
				}else{
					actualLosses = 0;
					profit += 100;
				}
				if (debug==1)
				System.out.println(resStr+" "+profit
						+" || "+DateUtils.datePrint(cal)
				);
			}
		}
		
		double failPer = fails*100.0/count;
		
		System.out.println(
				tp+" "+PrintUtils.Print2dec(factor, false)+" "+minRange
				+" || "+count+" "+fails+" "+PrintUtils.Print2dec(failPer, false)+" "+maxLosses
				);
	}

	public static void main(String[] args) throws Exception {

		
		String pathEURUSD = "C:\\fxdata\\gbpusd_UTC_5 Mins_Bid_2003.05.04_2017.04.07.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);		
			if (path.contains("UTC")){
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);
			}else{
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);
			}
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			
			ArrayList<QuoteShort> days = ConvertLib.createDailyDataShort(data);
			
			System.out.println(data.size()+" "+days.size());
			
			for (int tp=10;tp<=10;tp++){
				for (double factor =0.0;factor<=0.0;factor+=1.0){
					for (int minRange = 0;minRange<=0;minRange+=10){
						DailyBreak2.testBasic(days, tp,factor,minRange,1);
					}
				}
			}
			
		}

	}

}
