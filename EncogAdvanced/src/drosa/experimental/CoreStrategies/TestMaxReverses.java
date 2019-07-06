package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMaxReverses {

	public static void test(ArrayList<QuoteShort> data,ArrayList<Integer> maxMins,
			int h3,int thr,
			int nBars,int nBars2,
			int advserPips){
		
		ArrayList<Double> diffs = new ArrayList<Double>();
		ArrayList<Double> diffs2 = new ArrayList<Double>();
		
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int dayTrades = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				
				dayTrades=0;
				lastDay = day;
			}
			
			
			
			if (h==h3 && dayTrades==0){				
				int maxMin = maxMins.get(i-1);
				int begin = i;
				int end = i+nBars;
				if (end>=data.size()-1) end = data.size()-1;
				int openValue = data.get(i).getOpen5();
				int closeValue = data.get(end).getOpen5();
				int diff = 0;
				int diff2 = 0;
				boolean isTrade = false;
				if (maxMin>=thr){
					TradingUtils.getMaxMinShort(data, qm, calqm, end, end+nBars2);
					
					diff = closeValue-openValue;
					diff2 = qm.getHigh5()-closeValue;
					isTrade = true;
				}else if (maxMin<=-thr){
					TradingUtils.getMaxMinShort(data, qm, calqm, end, end+nBars2);
					
					diff = openValue-closeValue;
					diff2 = closeValue-qm.getLow5();
					isTrade = true;
				}
				
				if (isTrade){
					diffs.add(diff*0.1);
					diffs2.add(diff2*0.1);
					dayTrades++;
				}
			}
		}
		
		//MathUtils.summary_complete(h3+" "+thr+" "+nBars, diffs);
		
		int count50 = 0;
		int count15 = 0;
		for (int i=0;i<=diffs.size()-1;i++){
			double diff = diffs.get(i);
			double diff2 = diffs2.get(i);
			if (diff<=-advserPips){
				count50++;
				if (diff2<15) count15++;
			}
		}
		
		double per50 = count50*100.0/diffs.size();
		double per15 = count15*100.0/count50;
		System.out.println(
				h3+" "+thr+" "+nBars
				+" || "
				+" "+diffs.size()+" "+count50+" "+count15		
				+" "+PrintUtils.Print2dec(per50, false)
				+" "+PrintUtils.Print2dec(per15, false)
				);
	}
	
	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.10.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
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
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
						
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int h3=16;h3<=16;h3++){
				for (int thr=12;thr<=12;thr+=12){
					for (int nBars=96;nBars<=96;nBars+=12){
						for (int nBars2=12*72;nBars2<=12*72;nBars2+=12){
							for (int adversePips=0;adversePips<=200;adversePips+=10)
								TestMaxReverses.test(data, maxMins, h3, thr, nBars,nBars2, adversePips);
						}
					}
				}
			}
			
		}

	}

}
