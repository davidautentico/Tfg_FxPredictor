package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestContinuation {
	
	public static void test(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,int thr,int maxBars){
		
		
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int accDiffs = 0;
		int count = 0;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i);
			
			if (h>=h1 && h<=h2){
				if (maxMin>=thr){
					TradingUtils.getMaxMinShort(data, qm, calqm, i+1, i+maxBars);
					
					int pipsH = qm.getHigh5()-data.get(i+1).getOpen5();
					int pipsL = data.get(i+1).getOpen5()-qm.getLow5();
					
					accDiffs += (pipsH-pipsL);
					count++;					
				}else if (maxMin<=-thr){
					TradingUtils.getMaxMinShort(data, qm, calqm, i+1, i+maxBars);
					
					int pipsH = qm.getHigh5()-data.get(i+1).getOpen5();
					int pipsL = data.get(i+1).getOpen5()-qm.getLow5();
					
					accDiffs += (pipsL-pipsH);
					count++;
				}
			}
		}
		
		System.out.println(
				h1+" "+h2
				+" "+thr
				+" "+maxBars
				+" || "+count+" "+PrintUtils.Print2dec(accDiffs*0.1/count, false)
				);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.02.28.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2017.02.28.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2017.02.28.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2017.02.28.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathAUDUSD);
		paths.add(pathGBPUSD);
		paths.add(pathUSDJPY);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		limit = 3;
		for (int i = 3;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
				
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		
			for (int y1=2003;y1<=2017;y1++){
				int y2 = y1+0;
				
				for (int h1=16;h1<=16;h1++){
					int h2 = h1+7;
					for (int thr=5000;thr<=5000;thr+=12){
						for (int maxBars= 200;maxBars<=200;maxBars++){
							TestContinuation.test(data, maxMins, y1, y2, h1, h2, thr, maxBars);
							
						}
					}
				}
			}
		}
	}

}
