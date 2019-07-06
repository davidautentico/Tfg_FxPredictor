package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestPeaks {
	
	public static void testPeaks(String header,ArrayList<QuoteShort> data,ArrayList<QuoteShort> peaks,int min){
		

		int lastValue = 2;
		int totalPeaks = 0;
		int totalMins = 0;
		int index1 = 0;
		int index2 = 0;
		int accDiff = 0;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		ArrayList<Integer> accDiffs = new ArrayList<Integer>();
		for (int i=0;i<peaks.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(index1);
			QuoteShort q2 = data.get(index2);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal2, q2);
			int actualValue = peaks.get(i).getExtra();
			if (actualValue!=lastValue && actualValue !=0){//cambio de pico, calculamos diferencias
				//calcular rango entre q1 y q
				QuoteShort maxMin = TradingUtils.getMaxMinShort(data, index1, i);
				int diff = -1;
				int worst = -1;
				if (lastValue==1){
					diff = maxMin.getHigh5()-q1.getHigh5();
					//worst = maxMin.getH
				}else if (lastValue==-1){
					diff = q1.getLow5()-maxMin.getLow5();
				}
				if (diff!=-1){
					//System.out.println("lastPeak : "+lastValue+" "+DateUtils.datePrint(cal1)+" "+diff);
					accDiffs.add(diff);
					totalPeaks++;
					if (diff>=min*10){
						totalMins++;
					}
				}
				index1 = i;
			}
			lastValue = actualValue;
		}
		
		//double avgPips = accDiff*0.1/totalPeaks;
		MathUtils.summary(header
				+" || "+totalPeaks+" "+totalMins, accDiffs);
		/*System.out.println(header
				+" || "+totalPeaks
				+" "+PrintUtils.Print2dec(avgPips, false)
				);*/
	}

	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_30 Mins_Bid_2003.05.04_2015.11.25.csv";
				String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.01.05.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2013_08_29_2016_01_04_GAPS.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2015.11.25.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2015_10_28_2015_01_04.csv";
				
				//String pathEURUSD = "C:\\fxdata\\gbpjpy_UTC_5 Mins_Bid_2008.12.31_2015.10.11.csv";
				
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.12.31_2015.10.06.csv";		
				
				String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.01.05.csv";
				String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.05.csv";
				String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.05.csv";
				String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.05.csv";
				String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.01.05.csv";
				
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(pathEURUSD);paths.add(pathEURJPY);
				paths.add(pathGBPUSD);paths.add(pathGBPJPY);
				paths.add(pathUSDJPY);paths.add(pathAUDUSD);
				//paths.add(pathEURAUD);paths.add(pathNZDUSD);
				
				int total = 0;
				ArrayList<Double> pfs = new ArrayList<Double>();
				int limit = paths.size()-1;
				limit = 0;
				String provider ="";
				Sizeof.runGC ();
				ArrayList<Quote> dataI 		= null;
				ArrayList<Quote> dataS 		= null;
				for (int i = 0;i<=limit;i++){
					String path = paths.get(i);			
					if (path.contains("pepper")){
						dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
						dataS 		= dataI;
						provider="pepper";
					}else if (path.contains("forexdata")){
						dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
						dataS 		= TestLines.calculateCalendarAdjusted(dataI);
						provider="forexdata";
					}else{
						dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
						dataS 		= TestLines.calculateCalendarAdjusted(dataI);
						provider="dukasc";
					}				
				  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
					ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
					//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
					ArrayList<QuoteShort> data = null;
					dataI.clear();
					dataS.clear();
					data5m.clear();
					data = data5mS;
					
					int beginInicial = 1;
					int begin = beginInicial;
					int end = data.size()-1;
					int boxes = 1;
					int boxSize = end/boxes;
					
					double comm = 0.0;
					//System.out.println("total data: "+data.size()+" "+boxSize);
					for (int thr = 100;thr<=3000;thr+=100){
						String header = path.substring(10, 16)+" "+String.valueOf(thr);
						ArrayList<QuoteShort> peaks = TradingUtils.calculatePeaks(data, thr,false);
						TestPeaks.testPeaks(header, data, peaks,5);
						
					}
				}
				
	}

}
