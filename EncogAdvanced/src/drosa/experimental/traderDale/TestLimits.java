package drosa.experimental.traderDale;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestLimits {
	
	public static void test(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int h1,int h2,
			int thr,
			int lookback,
			int minPips,
			int nBars
			){
		
		int accPips = 0;
		int accPipsA = 0;
		int count = 0;
		
		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		for (int i=1;i<data.size()-nBars;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			
			if (day!=lastDay){
				
				lastDay = day;								
			}
			
			int maxMin = maxMins.get(i-1);
			int candleSize = q1.getHigh5()-q1.getLow5();
			
			if (h>=h1 && h<=h2){
				if (maxMin>=thr
						//&& candleSize>=10*20
						){
					TradingUtils.getMaxMinShort(data, qm, cal, i-lookback, i-1);
					
					int diffPips = q.getOpen5()-qm.getLow5();
					if (diffPips>=10*minPips){
						TradingUtils.getMaxMinShort(data, qm, cal, i, i+nBars);
						
						int nPips = q.getOpen5()-qm.getLow5();
						int naPips = qm.getHigh5()-q.getOpen5();
						accPips	+= nPips;
						accPipsA += naPips;
						count++;
					}				
				}else if (maxMin<=-thr
						//&& candleSize>=10*20
						){
					TradingUtils.getMaxMinShort(data, qm, cal, i-lookback, i-1);
					
					int diffPips = qm.getHigh5()-q.getOpen5();
					if (diffPips>=10*minPips){
						TradingUtils.getMaxMinShort(data, qm, cal, i, i+nBars);
						
						int nPips = qm.getHigh5()-q.getOpen5();
						int naPips = q.getOpen5()-qm.getLow5();
						accPips += nPips;
						accPipsA += naPips; 
						count++;
					}		
				}			
			}
		}
		
		double avg = accPips*0.1/count;
		double avgA = accPipsA*0.1/count;
		System.out.println(				
				h1+" "+h2+" "+thr+" "+minPips+" "+lookback+" "+nBars
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(avg, false)	
				+" "+PrintUtils.Print2dec(avgA, false)
				+" || "+PrintUtils.Print2dec(avg/avgA, false)
				);
			
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName   ="5 Mins_Bid_2003.05.04_2017.07.31.csv";
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_"+fileName;
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_"+fileName;
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_"+fileName;
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_"+fileName;
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		
		int limit = paths.size()-1;
		int initial = 0;
		limit       = 0;
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i=initial;i<=limit;i++){
			String path = paths.get(i);	
			String pairName = paths.get(i).split("\\\\")[2].substring(0, 6);
			if (path.contains("UTC")){
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);
			}else{
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);
			}
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  			
			ArrayList<QuoteShort> data = null;
			data = dataS;
			//System.out.println("total data: "+data.size());
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int h1=0;h1<=23;h1++){
				int h2 = h1+7;
				for (int thr=400;thr<=400;thr+=10){
					for (int lookback=100;lookback<=100;lookback++){
						for (int minPips=0;minPips<=0;minPips++){
							for (int nBars=60;nBars<=60;nBars+=12){
								TestLimits.test("", data, maxMins, h1, h2, thr, lookback, minPips, nBars);
							}
						}
					}
				}
			}
			
		
		}
		
		
	}

}
