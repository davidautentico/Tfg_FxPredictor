package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestClaudia2 {

	public static void doTest2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int backBars,
			int maxBars,
			int minDistance1,
			int minDistance2,
			double comm,
			int debug,
			boolean printSummary
			){
		
	
		
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
	
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		QuoteShort qmi = new QuoteShort();
		
		int lastDay = -1;
		int avgBars = 0;
		int cases = 0;
		int cases2 = 0;
		int totalDays = 0;
		int avgPips = 0;
		for (int i=backBars;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR); 
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			
			if (day!=lastDay){	
				totalDays++;
				
				int distanceH = 0;
				int distanceL = 0;
				boolean isH = false;
				boolean isL = false;
				for (int j=i-backBars;j<=i-1;j++){
					if (!isH) distanceH = data.get(j).getOpen5()-q.getOpen5();//fue un high
					if (!isL) distanceL = q.getOpen5()-data.get(j).getOpen5();//fue un high
					
					if (distanceH>=minDistance1*10) isH = true;
					if (distanceL>=minDistance1*10) isL = true;
						
					if (isH && isL){
						break;
					}
				}
							
				
				if (distanceH>=minDistance1*10){
					for (int j=i;j<=data.size()-1;j++){
						int index = TradingUtils.getMaxMinIndex(data, j, j+maxBars, data.get(j).getOpen5()+minDistance2*10, true);
						if (index>=0){
							avgPips += q.getOpen5()-data.get(j).getOpen5();
							cases2++;
							avgBars += (index-j);
							break;
						}						
					}
					cases++;
				}else if (distanceL>=minDistance1*10){
					for (int j=i;j<=data.size()-1;j++){
						int index = TradingUtils.getMaxMinIndex(data, j, j+maxBars, data.get(j).getOpen5()-minDistance2*10, false);
						if (index>=0){
							avgPips += data.get(j).getOpen5()-q.getOpen5();
							cases2++;
							avgBars += (index-j);
							break;
						}		
					}
					cases++;
				}					
				lastDay = day;
			}						
		}
	
		double casesPercent = cases*100.0/totalDays;
		System.out.println(
				
				backBars+" "+maxBars+" "+minDistance1+" "+minDistance2
				+" || "
				+totalDays+" "+cases+" "+PrintUtils.Print2dec(casesPercent, false)
				+" || "+cases2+" "+PrintUtils.Print2dec(avgBars*1.0/cases2, false)+" "+PrintUtils.Print2dec(avgPips*0.1/cases2, false)
				);
	}
	
	
	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.11.02.csv";
		String pathGBPUSD = "C:\\fxdata\\gbpUSD_UTC_1 Min_Bid_2010.01.01_2016.11.13.csv";
		//String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.09.20.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathGBPUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 1;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i = 1;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			
			for (int y1=2010;y1<=2010;y1++){
				int y2 = y1+6;
				
				
				for (int thr1=300;thr1<=300;thr1++){
					for (int backBars=600;backBars<=600;backBars+=60){
						for (int maxBars=1440;maxBars<=1440;maxBars++){
							for (int minDistance1=20;minDistance1<=200;minDistance1+=10){
								for (int minDistance2=40;minDistance2<=40;minDistance2+=10){
									TestClaudia2.doTest2("", data, maxMins, y1, y2,backBars,maxBars, minDistance1,minDistance2, 0.0, 0, true);
								}
							}		
						}
					}
				}
			}
		}

	}

}
