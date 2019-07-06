package drosa.experimental.newResearches;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MomentumDetector {
	
	public static  void doStudy(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int year1,int year2,
			int h1,int h2,
			int thr,int predictionSize,
			int pullPips,
			int limitSL,
			int limitThr
			){
		
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int accPips = 0;
		int count = 0;
		int count5 = 0;
		int lastDayLoss = -1;
		int lastDay = -1;
		int dayLosses = 0;
		int losses5 = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-2;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (y<year1 || y>year2) continue;
			if (h<h1 || h>h2) continue;
			QuoteShort maxMin = maxMins.get(i);
			
			if (day!=lastDay){				
				lastDay = day;
			}
						
			if (maxMin.getExtra()>=thr){
				for (int j=i+1;j<data.size()-2;j++){
					QuoteShort qj = data.get(j);
					//if (qj.getOpen5()-q.getHigh5()>=pullPips*10){
					if (pullPips==-1 || q.getHigh5()-qj.getOpen5()>=pullPips*10){
						//QuoteShort mm = TradingUtils.getMaxMinShortLimitSL(data, j, j+predictionSize,limitSL,true);
						QuoteShort mm = TradingUtils.getMaxMinShortLimitSLReversal(data,maxMins, j, j+predictionSize,limitSL,limitThr,true);
						//QuoteShort mm = TradingUtils.getMaxMinShort(data, j, j+predictionSize);
						int high = mm.getHigh5()-qj.getOpen5();
						int low = qj.getOpen5()-mm.getLow5();
						int close = mm.getClose5()-qj.getOpen5();
						//accPips += (high-low);						
						accPips += close;
						if (close>=0){
							winPips += close;
							wins++;
						}
						else{
							lostPips += -close;
							losses++;
						}
						if (high>=50) count5++;
						else{
							if (day!=lastDayLoss){
								dayLosses++;
								lastDayLoss = day;
							}
							losses5 += -close*0.1;
						}
						count++;
						break;
					}
				}
			}else if (maxMin.getExtra()<=-thr){
				for (int j=i+1;j<data.size()-2;j++){
					QuoteShort qj = data.get(j);
					//if (q.getLow5()-qj.getOpen5()>=pullPips*10){
					//System.out.println("'entrado1");
					if (pullPips==-1 || qj.getOpen5()-q.getLow5()>=pullPips*10){
						//QuoteShort mm = TradingUtils.getMaxMinShortLimitSL(data, j, j+predictionSize,limitSL,false);
						QuoteShort mm = TradingUtils.getMaxMinShortLimitSLReversal(data,maxMins, j, j+predictionSize,limitSL,limitThr,false);
						//QuoteShort mm = TradingUtils.getMaxMinShort(data, j, j+predictionSize);
						int high = mm.getHigh5()-qj.getOpen5();
						int low = qj.getOpen5()-mm.getLow5();
						//accPips += (low-high);
						int close = qj.getOpen5()-mm.getClose5();
						accPips += close;
						if (close>=0){
							winPips += close;
							wins++;
						}
						else{
							lostPips += -close;
							losses++;
						}
						if (low>=50) count5++;
						else{
							if (day!=lastDayLoss){
								dayLosses++;
								lastDayLoss = day;
							}
							losses5 += -close*0.1;
						}
						count++;
						//System.out.println("'entrado2");
						break;
					}
				}
			}
		}
		
		double avg = accPips*0.1/count;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		double winPer = wins*100.0/(wins+losses);
		double pf = winPips*1.0/lostPips;
		double win5 = count5*100.0/(wins+losses);
		double pf5 = 5.0*count5/losses5;
		System.out.println(
				thr
				+" "+predictionSize
				+" "+pullPips
				+" "+limitSL
				+" "+limitThr
				+" "+h1+" "+h2
				+" || "
				+count
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgLoss, false)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(win5, false)
				+" "+count5+" "+(count-count5)
				+" "+dayLosses
				+" "+PrintUtils.Print2dec(pf5, false)
				);
	}
	
	public static  void doStudyPrediction(ArrayList<QuoteShort> data,int boxSize,int predictionSize,int targetDiff,boolean isHigh){
			
		int total = 0;
		int avg = 0;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			
			int highs = TradingUtils.countHighsLows(data,i-boxSize,i,true);
			int lows = TradingUtils.countHighsLows(data,i-boxSize,i,false);
			
			if ((isHigh && highs-lows>=targetDiff)
					|| (!isHigh && lows-highs>=targetDiff)){
				QuoteShort qmm = TradingUtils.getMaxMinShort(data, i+1, i+predictionSize);
				
				int diffH = qmm.getHigh5()-q1.getOpen5();
				int diffL = q1.getOpen5()-qmm.getLow5();
				
				int diff = diffH-diffL;
				System.out.println(diff+" "+diffH+" "+diffL+" || "+highs+" "+lows+" "+q1.getOpen5()+" "+qmm.getHigh5()+" "+qmm.getLow5());
				avg += diff;
				total++;
			}
		}
		
		double avgDiff = avg*0.1/total;
		System.out.println(
				boxSize
				+" "+predictionSize
				+" "+targetDiff
				+" || "+total+" "+PrintUtils.Print2(avgDiff, false)
				);
		
	}
	
	public static void main(String[] args) throws Exception {
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.04.04.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.04.04.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.04.04.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2016.04.04.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.04.04.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.04.04.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathEURNZD = "C:\\fxdata\\EURNZD_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathUSDCAD = "C:\\fxdata\\USDCAD_UTC_5 Mins_Bid_2003.05.04_2016.03.29.csv";
		String pathAUDJPY = "C:\\fxdata\\AUDJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.29.csv";
		String pathEURGBP = "C:\\fxdata\\EURGBP_UTC_5 Mins_Bid_2003.05.04_2016.03.29.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.05.04_2016.03.29.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		paths.add(pathGBPJPY);
		paths.add(pathEURJPY);
		paths.add(pathNZDUSD);
		paths.add(pathEURNZD);
		paths.add(pathUSDCAD);
		paths.add(pathAUDJPY);
		paths.add(pathEURGBP);
		paths.add(pathEURAUD);
		
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
			

			

			//System.out.println("total data: "+data.size());
			
			/*for (int boxSize = 400;boxSize<=400;boxSize+=100){
				for (int predictionSize = 400;predictionSize<=400;predictionSize+=100){
					for (int targetDiff = 50;targetDiff<=50;targetDiff+=10){
						MomentumDetector.doStudyPrediction(data, boxSize, predictionSize, targetDiff, true);
					}
				}
			}*/
			
			ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			for (int predictionSize = 2000;predictionSize<=2000;predictionSize+=10){
				for (int thr =1000;thr<=20000;thr+=1000){
					for (int limitThr = -1;limitThr<=-1;limitThr+=100){
						for (int pullPips = -1;pullPips<=-1;pullPips+=101){
							for (int limitSL = 999999;limitSL<=999999;limitSL+=10){
								for (int year=2003;year<=2003;year++){
									int year2 = year+13;
									for (int h=0;h<=0;h++){
										MomentumDetector.doStudy(data5mS,maxMinsExt,year,year2,h,23, thr, predictionSize,pullPips,limitSL,limitThr);
									}
								}
							}
						}
					}
				}
			}
			
						
		}

	}
	
	
	
}
	
	
