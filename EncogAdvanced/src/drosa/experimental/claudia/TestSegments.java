package drosa.experimental.claudia;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestSegments {
	
	
	public static void calculateSegments(ArrayList<QuoteShort> data,int begin,int end,int h1,int h2,int minPips,double percent){
		
		ArrayList<Segment> segments = new ArrayList<Segment>();
		if (begin<1) begin = 1;
		if (end>=data.size()) end = data.size()-1;
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		boolean settlement = false;
		int retracement = -1;
		int totalDays = 0;
		int totalretracement = 0;
		int settlementMax = -1;
		int settlementMin = -1;
		boolean isFirst = true;
		int firstIndex = begin;
		int lastIndex = begin;
		int actualTrend = 0;
		int maxMin=0;
		double avg = 0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				totalDays++;	
				max = -999999;
				min = 999999;
				lastDay = day;	
				maxMin=0;
			}
			QuoteShort firstQuote	= data.get(firstIndex);
			QuoteShort lastQuote 	= data.get(lastIndex);
			if (i>firstIndex){
				int ascDiff = q.getHigh5()-firstQuote.getLow5();
				int descDiff = firstQuote.getHigh5()-q.getLow5();
				if (actualTrend==1){
					descDiff = lastQuote.getHigh5()-q.getLow5();
				}else if (actualTrend==-1){
					ascDiff = q.getHigh5()-lastQuote.getLow5();
				}
				if (ascDiff>=minPips && q.getHigh5()>=lastQuote.getHigh5()){
					//lastIndex = i;
					if (actualTrend == -1){
						int diff = data.get(firstIndex).getHigh5()-data.get(lastIndex).getLow5();
						Segment seg = new Segment();
						seg.setFirstIndex(firstIndex);
						seg.setLastIndex(lastIndex);
						seg.setTrend(-1);
						seg.setDiff(diff);
						seg.setMaxMin(maxMin);
						segments.add(seg);
						//System.out.println(seg.toString()+" "+data.get(firstIndex).getHigh5()+" "+data.get(lastIndex).getLow5());
						firstIndex = lastIndex;
					}
					if (q.getHigh5()>=max) maxMin=1;
					lastIndex = i;
					actualTrend = 1;
					//System.out.println("asc "+i +" "+firstIndex+" "+ascDiff+" "+descDiff);
				}else if (descDiff>=minPips && q.getLow5()<=lastQuote.getLow5()){
					//lastIndex = i;
					if (actualTrend == 1){
						int diff = data.get(lastIndex).getHigh5()-data.get(firstIndex).getLow5();
						Segment seg = new Segment();
						seg.setFirstIndex(firstIndex);
						seg.setLastIndex(lastIndex);
						seg.setTrend(1);
						seg.setDiff(diff);		
						seg.setMaxMin(maxMin);
						segments.add(seg);
						//System.out.println(seg.toString()+" "+data.get(firstIndex).getLow5()+" "+data.get(lastIndex).getHigh5());
						firstIndex = lastIndex;
					}
					if (q.getLow5()<=max) maxMin=-1;
					lastIndex = i;
					actualTrend = -1;
					//System.out.println("des "+i +" "+firstIndex+" "+ascDiff+" "+descDiff);
				}
			}	
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
		}
		
		int total = 0;
		int totalLosses = 0;
		int populationSize = 0;
		double avgFactor = 0;
		for (int i=0;i<=segments.size()-2;i++){
			Segment s = segments.get(i);
			Segment s1 = segments.get(i+1);
			
			double per = s1.getDiff()*100.0/s.getDiff();
			QuoteShort.getCalendar(cal, data.get(s.getFirstIndex()));
			QuoteShort.getCalendar(cal1, data.get(s.getLastIndex()));
			int hs = cal.get(Calendar.HOUR_OF_DAY);
			if (hs>=h1 && hs<=h2 && s.getMaxMin()!=0){
				if (per>=percent){
					total++;
				}else{
					//System.out.println(DateUtils.datePrint(cal)+" "+DateUtils.datePrint(cal1)+" "+s.toString());
					//avgFactor += s.getDiff()*1.0/minPips;
					//totalLosses++;
				}
				avgFactor += s.getDiff()*1.0/minPips;
				totalLosses++;
				populationSize++;
			}			
		}
		double p = total*100.0/(populationSize);
		double avg2 = avgFactor/totalLosses;
		System.out.println(minPips+" "+h1+" "+h2+" "+PrintUtils.Print2(percent)+" || "+populationSize+" "+PrintUtils.Print2(p)+" "+PrintUtils.Print2(avg2));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName   ="1 Min_Bid_2008.12.31_2015.04.20.csv";
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_"+fileName;
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_"+fileName;
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_"+fileName;
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_"+fileName;
		String pathUSDCAD = "C:\\fxdata\\USDCAD_UTC_"+fileName;
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_"+fileName;
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_"+fileName;
		String pathEURGBP = "C:\\fxdata\\EURGBP_UTC_"+fileName;
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_"+fileName;
		String pathCADJPY = "C:\\fxdata\\CADJPY_UTC_"+fileName;
		String pathAUDJPY = "C:\\fxdata\\AUDJPY_UTC_"+fileName;
		String pathNZDJPY = "C:\\fxdata\\NZDJPY_UTC_"+fileName;
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_"+fileName;
		String pathGBPCAD = "C:\\fxdata\\GBPCAD_UTC_"+fileName;
		String pathGBPAUD = "C:\\fxdata\\GBPAUD_UTC_"+fileName;
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		paths.add(pathEURJPY);
		paths.add(pathEURAUD);
		paths.add(pathGBPJPY);
		paths.add(pathGBPAUD);
		
		int limit = paths.size()-1;
		int initial = 1;
		limit       = 1;
		for (int i=initial;i<=limit;i++){
			String pairName = paths.get(i).split("\\\\")[2].substring(0, 6);
			ArrayList<Quote> dataI 		= DAO.retrieveData(paths.get(i), DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
			ArrayList<QuoteShort> data = null;
			data = data5mS;
			//System.out.println("total data: "+data.size());
			//ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			int begin = 4000000;
			int end   = 5000000;
						
			for (begin=1;begin<=1;begin+=100000){
				end = begin + 7000000;
				for (int h1=16;h1<=16;h1++){
					int h2 = h1+6;
					for (int minPips=100;minPips<=5000;minPips+=50){
						//TestRetraces.testMaxRetracing(data, begin, end, h1, h2);
						for (int per=50;per<=50;per+=10){
							TestSegments.calculateSegments(data, begin, end, h1, h2, minPips,per);
						}
					}
				}
			}
		}
	}
}
