package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyHalfMoves {
	
	public static void doTest(		
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int blockSize,
			int maxf,
			int debug 
			){
		//
		
		
		int lastDay = -1;
		int lastDayPips = 0;
		int dayPips = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalDays = 0;
		int totalL = 0;
		int totalLL = 0;
		int totalW = 0;
		int totalWL = 0;
		int totalRiskedPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		double ma0 = -1;
		double std0 = -1;
		ArrayList<Integer> days = new ArrayList<Integer>();
		int isTrade=0;
		int lastDayTrade = -1;
		int countDays = 0;
		int high = -1;
		int low = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		ArrayList<Double> speeds = new ArrayList<Double>();
		
		double range = 1000.0;
		int point = -1;
		int barTries = 0;
		int dayIndex = 0;
		int mode = 0;
		int doValue = -1;
		int acc10 = 0;
		int total10=0;
		int comm = 10;
		int ref = 1;
		int ref0 = 0;
		
		boolean canTest = false;
		for (int i=1;i<data.size()-1;i++){
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
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			
			if (day!=lastDay){
				ref0 = q1.getClose5();
				mode = 0;
				canTest = true;
				lastDay = day;
			}
			
			if (
				h==h1 && ((h1==0 && min==15) || (h1>0 && min==0))
				){
				ref = q.getOpen5();
			}
			
			if (h1==0){
				ref = ref0;
			}
			
			int diffH = q.getHigh5()-ref;
			int diffL = ref-q.getLow5();
			
			if (canTest){
				if (diffH>=blockSize){
					canTest = false;
					int target = ref+(q.getHigh5()-ref)/2-00;
					int higha = q.getHigh5();
					if (debug==1){
						System.out.println("[HIGH TEST] "+ref+" "+higha+" "+target+" | "+q.toString());
					}
					for (int j=i+1;j<data.size();j++){
						QuoteShort qj = data.get(j);
						QuoteShort.getCalendar(calqm, qj);
						int hj = calqm.get(Calendar.HOUR_OF_DAY);
						int minj = calqm.get(Calendar.MINUTE);
						if (hj==0 && minj<15) continue;
						if (qj.getLow5()<=target){
							if (debug==1){
								System.out.println("[HIGH WIN] "+ref+" "+higha+" | "+qj.toString());
							}
							diffs.add(higha-ref);
							break;
						}else if (qj.getHigh5()>higha){
							target = ref+(qj.getHigh5()-ref)/2-00;
							higha = qj.getHigh5();
							if ((higha-ref)/blockSize>=maxf){
								diffs.add(higha-ref);
								if (debug==1){
									System.out.println("[HIGH FAIL TEST] "+ref+" "+higha+" "+target+" | "+q.toString());
								}
								break;
							}
						}
					}
				}else if (diffL>=blockSize){
					canTest = false;
					int target = ref-(ref-q.getLow5())/2+0;
					int lowa = q.getLow5();
					if (debug==2){
						//System.out.println("[LOW TEST] "+ref+" "+lowa+" | "+q.toString());
					}
					for (int j=i+1;j<data.size();j++){
						QuoteShort qj = data.get(j);
						QuoteShort.getCalendar(calqm, qj);
						int hj = calqm.get(Calendar.HOUR_OF_DAY);
						int minj = calqm.get(Calendar.MINUTE);
						if (hj==0 && minj<15) continue;
						if (qj.getHigh5()>=target){
							if (debug==2){
								//System.out.println("[LOW WIN] "+ref+" "+lowa+" | "+qj.toString());
							}
							diffs.add(ref-lowa);
							break;
						}else if (qj.getLow5()<lowa){
							target = ref-(ref-qj.getLow5())/2+00;
							lowa = qj.getLow5();
							if ((-lowa+ref)/blockSize>=maxf){
								diffs.add(-lowa+ref);
								break;
							}
						}
					}
				}
			}
		}
		
		
		int avg = 0;
		int count1 = 0;
		ArrayList<Integer> factors = new ArrayList<Integer>();
		for (int i=0;i<=maxf;i++) factors.add(0);
		for (int i=0;i<diffs.size();i++){
			avg += diffs.get(i);
			int f = diffs.get(i)/blockSize;
			int c = 0;
			if (f>=maxf){
				c = factors.get(maxf); 
				factors.set(maxf, c+1);
				count1++;
			}
			else{
				c = factors.get(f);
				factors.set(f, c+1);
				count1++;
			}						
		}
		
		String str="";
		for (int i=1;i<=maxf;i++){
			int c = factors.get(i);
			double per = c*100.0/count1;
			str+=PrintUtils.Print2dec(per, false)+" ";
		}
		
		double avgd = avg*1.0/diffs.size();
		System.out.println(
				h1
				+" "+blockSize
				+" || "
				+" "+diffs.size()
				+" "+PrintUtils.Print2dec(avgd/blockSize, false)
				+" | "+str
				);
	}

	public static void main(String[] args) {

		String path0 ="C:\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2009.01.01_2018.12.19.csv";
		
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
			
			System.out.println("path: "+path);
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			Calendar cal = Calendar.getInstance();
			
			for (int h1=0;h1<=23;h1++){
				int h2 = h1;
				for (int blockSize=300;blockSize<=300;blockSize+=100){
					StudyHalfMoves.doTest("", data, maxMins, 2009, 2018, 0, 11, h1, h2, blockSize,5,0);
				}
			}
		}

	}

}
