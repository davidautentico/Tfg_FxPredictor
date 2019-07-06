package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestCorrelationUpDown {
	
	public static void doTest(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int nRange,
			int lookBack,
			int lookForward,
			double f1Test
			){
		
		QuoteShort q1 = null;
		QuoteShort q = null;
		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double accF = 0;
		int totalTests = 0;
		int wins=0;
		int totalDays = 0;
		int totalDaysTrading = 0;
		boolean isNewDay = true;
		int lastDay = -1;
		int winPips = 0;
		int lostPips = 0;
		for (int i=nRange;i<data.size()-lookForward;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int month = cal.get(Calendar.MONTH);
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				totalDays++; 
				isNewDay = true;
				lastDay = day ;
			}
			
			if (h>=h1 && h<=h2){
				int maxMin = maxMins.get(i-1);
				TradingUtils.getMaxMinShort(data, qm, cal, i-nRange, i);
				//int range = qm.getHigh5()-qm.getLow5();
				int beginPoint = i-lookBack;
				int endPoint = i+lookForward;
				
				TradingUtils.getMaxMinShort(data, qm, cal, beginPoint, i);
				int diff1 = qm.getHigh5()-data.get(beginPoint).getOpen5()-(data.get(beginPoint).getOpen5()-qm.getLow5());
				//double factor1 = diff1/range;
				
				TradingUtils.getMaxMinShort(data, qm, cal,i,  endPoint);
				int diff2 = qm.getHigh5()-data.get(i).getOpen5()-(data.get(i).getOpen5()-qm.getLow5());
				//double factor2 = diff2/range;
				
				//System.out.println(diff1+" "+diff2);
				if (diff1>=f1Test
						//&& maxMin>=1000
						){
					accF += diff2;
					totalTests++;
					if (diff2>=0) wins++;
					
					if (isNewDay){
						totalDaysTrading++;
						isNewDay = false;
					}
					
					int pips = data.get(endPoint).getClose5()-data.get(i).getOpen5();
					if (pips>=0){
						winPips+=pips;
					}else{
						lostPips += -pips;
					}
				}else if (diff1<=-f1Test
						//&& maxMin<=-1000
						){
					accF += -diff2;
					totalTests++;
					if (-diff2>=0) wins++;
					
					if (isNewDay){
						totalDaysTrading++;
						isNewDay = false;
					}
					
					int pips = -data.get(endPoint).getClose5()+data.get(i).getOpen5();
					if (pips>=0){
						winPips+=pips;
					}else{
						lostPips += -pips;
					}
				}
				
				
			}
		}
		
		double pf = winPips*1.0/lostPips;
		double avg = accF*0.1/totalTests;
		double winPer = wins*100.0/totalTests;
		double daysPer = totalDaysTrading*100.0/totalDays;
		System.out.println(
				h1+" "+nRange+" "+lookBack+" "+lookForward+" "+PrintUtils.Print2dec(f1Test, false)
				+" || "+totalTests
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(daysPer,false)
				+" | "+PrintUtils.Print2dec(pf,false)+" "+PrintUtils.Print2dec(1.0/pf,false)
				);
	}

	public static void main(String[] args) {
String path0 ="C:\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.02.01.csv";
		
		//String pathEURUSD = path0+"EURUSD_15 Mins_Bid_2009.01.01_2018.12.12.csv";
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2018.12.12.csv";
		
		String pathEURUSD_bricks5 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_50_bricks.csv";
		String pathEURUSD_bricks10 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_100_bricks.csv";
		String pathEURUSD_bricks20 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_200_bricks.csv";
		String pathEURUSD_bricks40 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_400_bricks.csv";
		String pathEURUSD_bricks60 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_600_bricks.csv";
		String pathEURUSD_bricks80 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_800_bricks.csv";
		String pathEURUSD_bricks100 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_1000_bricks.csv";
		String pathEURUSD_bricks150 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_1500_bricks.csv";
		//String pathEURUSD = path0+"EURUSD_Ticks_2018.10.04_2018.10.04.csv_50_bricks.csv";
		
		String pathNews = path0+"News.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		try {
			Sizeof.runGC ();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		//FFNewsClass.readNews(pathNews,news,0);
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);	
			
			dataI 		= new ArrayList<QuoteShort>();
			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+0;
				for (int nBars=288;nBars<=288;nBars++){
					for (int lookBack=12;lookBack<=288*5;lookBack+=12){
						for (int lookForward=288*1;lookForward<=288*1;lookForward+=12){
							for (double fTest=100;fTest<=100;fTest+=100){						
								TestCorrelationUpDown.doTest(data, maxMins, 2009, 2019, h1,h2, nBars, lookBack, lookForward, fTest);
							}
						}
					}
				}
			}
			
		}

	}

}
