package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TrendStudy2 {
	
	
	public static void studyTrends3(ArrayList<Trend> trends,int minSize1,int minSize2,int h11,int h12,int h2,int h3){
		
		int count = 0;
		int positives = 0;
		double avgDiff = 0;
		double avgDiff1 = 0;
		double avgSize=0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<trends.size()-1;i++){
			Trend t0 = trends.get(i);
			Trend t1 = trends.get(i+1);
			int ht1 =  t0.getHour1(cal);
			int hIndex0 = t0.getHour0(cal);//hora de determinacion de la trend debe sertar entre h11 y h12
			int ht2 = t0.getHour2(cal);//hora de finalizacion
			if (t0.getTokenSize()>=minSize1 && t0.getTokenSize()<=minSize2 && hIndex0>=h11 && hIndex0<=h12 && ht2>=h2){
				//System.out.println(t0.toString()+" || "+t1.toString());
				if (ht2<=h3){
					positives++;
				}
				avgDiff+=t0.getSize()-t0.getTokenSize();
				avgSize+=t0.getSize();
				avgDiff1+=t1.getSize();
				count++;
			}
		}		
		double winPer = positives*100.0/count;
		double perDiff = (avgDiff/count)*100.0/minSize1-100.0; 
		System.out.println(
				minSize1+" "+h11+" "+h12+" "+h2+" "+h3
				+" "+count
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avgDiff/count, false)
				+" || "
				+PrintUtils.Print2dec(avgSize/count, false)
				+" ("+PrintUtils.Print2dec(avgDiff/count, false)+" "+PrintUtils.Print2dec(perDiff, false)+") "
				+" "+PrintUtils.Print2dec(avgDiff1/count, false)
				
				);
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.12.31_2015.10.06.csv";
		
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathEURJPY);
		paths.add(pathGBPUSD);paths.add(pathGBPJPY);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
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
			System.out.println("total data: "+data.size()+" "+boxSize);
			
			/*int period = 1;
			ArrayList<QuoteShort> boxesData = TradingUtils.calculateBoxesH(data, begin, end, period);
			for (int b=0;b<boxesData.size();b++){
				System.out.println(boxesData.get(b).toString());
			}
			studyboxes(boxesData,period);*/
			Calendar cal = Calendar.getInstance();
			for (int box=1;box<=boxes;box++){
				begin = (box-1)*boxSize;
				if (begin<=beginInicial) begin = beginInicial;
				end = begin+boxSize;
				for (int minSize=10;minSize<=50;minSize+=5){
					for (int thr=minSize;thr<=minSize;thr+=5){
						for (int h1=0;h1<=0;h1++){
							for (int h2=h1+8;h2<=h1+8;h2++){
								int tokenH = h2+1;
								ArrayList<Trend> trends = Trend.calculateTrends(data, begin, end, minSize,thr,tokenH,true);
								for (int tokenSize1=thr;tokenSize1<=thr;tokenSize1+=5){
									int tokenSize2=tokenSize1+9999;
									TrendStudy2.studyTrends3(trends, tokenSize1,tokenSize2, h1, h2, tokenH,tokenH+1);
								}
							}
						}
												
						/*for (int h1=0;h1<=23;h1++){
							int h2 = h1+1;
							Trend.printSummaryByHour(h1+" "+h2, trends, 2000, 2016, h1, h2);
						}*/
					}//thr
				}//minsize
			}//boxes
		}//limit
	}

	private static void studyboxes(ArrayList<QuoteShort> boxesData, int period) {
		
		int boxes = 24/period; //numero de cajas por dia
		ArrayList<Double> acc = new ArrayList<Double>();
		ArrayList<Double> ranges = new ArrayList<Double>();
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		ArrayList<Integer> totals = new ArrayList<Integer>();
		for (int i=0;i<boxes;i++){
			ranges.add(0.0);
			acc.add(0.0);
			totals.add(0);
			diffs.add(0);
		}
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<boxesData.size();i++){
			QuoteShort q = boxesData.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int box = h/period;
			//calculo rango y diferencia maximo H/L to close
			int range = q.getHigh5()-q.getLow5();
			if (range<=0) continue;
			int diffHO = q.getHigh5()-q.getOpen5();
			int diffOL = q.getOpen5()-q.getLow5();
			int diffHC = q.getHigh5()-q.getClose5();
			int diffCL = q.getClose5()-q.getLow5();
			
			//cogemos la maxima apertura
			int max = diffHC;
			if (diffCL>=diffHC) max = diffCL;
			//int max = (diffHC+diffCL)/2;
			
			double perDiff = max*100.0/range;
			ranges.set(box, ranges.get(box)+range);
			diffs.set(box, diffs.get(box)+max);
			acc.set(box, acc.get(box)+perDiff);
			totals.set(box, totals.get(box)+1);
		}
		
		for (int i=0;i<boxes;i++){
			double avgPer = acc.get(i)/totals.get(i);
			double avgRange = ranges.get(i)*1.0/totals.get(i);
			double avgDiffs = diffs.get(i)*1.0/totals.get(i);
			double avgPer2 = avgDiffs*100.0/avgRange ;
			System.out.println("box "+i+" : "
					+PrintUtils.Print2dec(avgPer, false)
					+" "+PrintUtils.Print2dec(avgRange*0.1, false)
					+" "+PrintUtils.Print2dec(avgDiffs*0.1, false)
					+" || "+PrintUtils.Print2dec(avgPer2, false)
					);
		}
	}

}
