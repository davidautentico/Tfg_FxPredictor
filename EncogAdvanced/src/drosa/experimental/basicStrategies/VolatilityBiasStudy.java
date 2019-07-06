package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.zznbrum.TrendInfo;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class VolatilityBiasStudy {

	public static void doTest(ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,int h3,
			int aBias,
			int debug
			){
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winsFloating=0;
		int lossesFloating=0;
		int others=0;
		int accOthers=0;
		int accref=0;
		int accSize=0;
		int accLosses = 0;
		int accWins = 0;
		boolean canContinue = false;
		boolean isTested = false;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;

		int winsFloatingPips = 0;
		int lostFloatingPips = 0;
		int high = -1;
		int low = -1;
		int totalDays = 0;
		double avgRange = 600.0;
		int tpf = 200;
		int slf = 200;
		int comm = 20;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int hl = 0;
		
		int openH1 = 0;
		int openH2 = 0;
		int bias = 0;
		int rangeH = -1;
		int rangeL = -1;
		int accDiff = 0;
		int accH = 0;
		int accL = 0;
		int accBias = 0;
		int total=0;
		int count10=0;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
						
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay
					//&& min==15
					) {	
				
				if (lastDay!=-1){
					int dayRange = high-low;
					totalDays++;
					ranges.add(dayRange);
					avgRange = MathUtils.average(ranges, ranges.size()-20, ranges.size()-1);

				}
				
				high = -1;
				low = -1;
				lastDay = day;
				isTested = false;
			}//day
			
			if (h==h1 && min==0){
				openH1 = q.getOpen5();
			}
			if (h==h2  && min==0){
				openH2 = q.getOpen5();
				bias = openH2-openH1;
				high = q.getHigh5();
				low = q.getLow5();
				
				rangeH = high-openH2;
				rangeL = openH2-low;
			}
			
			if (h>=h2 && h<h3){
				
				if (high==-1 || q.getHigh5()>=high){
					high = q.getHigh5();
					rangeH = high-openH2;
				}
				
				if (low==-1 || q.getLow5()<=low){
					low = q.getLow5();
					rangeL = openH2-low;
				}
			}
			
			if (h==h3 && min==0){
				if (Math.abs(bias)>=aBias){
					accH +=rangeH;
					accL += rangeL;
					if (bias>=0){
						accDiff+=rangeH;
						if (rangeH>=50) count10++;
					}else{
						accDiff+=rangeL;
						if (rangeL>=50) count10++;
					}
					accBias+= Math.abs(bias);
					total++;
				}
				
				if (debug==1)
				System.out.println(
						DateUtils.datePrint(cal)
						+" "+bias
						+" "+rangeH
						+" "+rangeL
						+" "+count10*100.0/total
						);
			}
			
		}
		
		
		
		System.out.println(
				h1+" "+h2+" "+h3+" "+aBias
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(accBias/total, false)
				+" "+PrintUtils.Print2dec(accDiff*1.0/total, false)
				+" "+PrintUtils.Print2dec(count10*100.0/total, false)
				//+" "+PrintUtils.Print2dec(accL*0.1/total, false)
				//+" || "
				//+" "+PrintUtils.Print2dec(ac, false)
				);
		
	}
	
	public static void doTestADR(ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,int h3,
			int nAtr,
			double aRangeFactor,
			int debug
			){
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winsFloating=0;
		int lossesFloating=0;
		int others=0;
		int accOthers=0;
		int accref=0;
		int accSize=0;
		int accLosses = 0;
		int accWins = 0;
		boolean canContinue = false;
		boolean isTested = false;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;

		int winsFloatingPips = 0;
		int lostFloatingPips = 0;
		int high = -1;
		int low = -1;
		int high3 = -1;
		int low3 = -1;
		int totalDays = 0;
		double avgRange = 600.0;
		int tpf = 200;
		int slf = 200;
		int comm = 20;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int hl = 0;
		
		int openH1 = 0;
		int openH2 = 0;
		int bias = 0;
		int rangeH = -1;
		int rangeL = -1;
		int accDiff = 0;
		int accH = 0;
		int accL = 0;
		int accBias = 0;
		int total=0;
		int count10=0;
		int range12 = 0;
		double accFactor12 = 0.0;
		double accFactor3 = 0.0;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
						
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay
					//&& min==15
					) {	
				
				if (lastDay!=-1){
					int dayRange = high-low;
					totalDays++;
					ranges.add(dayRange);
					avgRange = MathUtils.average(ranges, ranges.size()-nAtr, ranges.size()-1);
				}
				
				high = -1;
				low = -1;
				high3 = -1;
				low3 = -1;
				lastDay = day;
				isTested = false;
			}//day
			
			if (h==h1 && min==0){
				openH1 = q.getOpen5();
			}
			if (h==h2  && min==0){
				openH2 = q.getOpen5();
			}
			
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
			}
			
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
			}
			
			if (h>=h1 && h<h2){						
				range12 = high-low;
			}
			
			if (h>=h2){
				if (high3==-1 || q.getHigh5()>=high3){
					high3 = q.getHigh5();
				}
				if (low3==-1 || q.getLow5()<=low3){
					low3 = q.getLow5();
				}
			}
			
			
			if (h==h3 && min==55){
				int range3 = high-low;
				
				if (range12*1.0/avgRange>=aRangeFactor){
					accFactor12 += range12*1.0/avgRange;
					accFactor3 += range3*1.0/avgRange;
					total++;
					if (debug==1)
					System.out.println(
							DateUtils.datePrint(cal)
							//+" "+bias
							+" "+range12
							+" "+range3
							+" "+count10*100.0/total
							);
				}
			}
			
		}
		
		
		
		System.out.println(
				h1+" "+h2+" "+h3+" "+PrintUtils.Print2dec(aRangeFactor, false)
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(accFactor12/total, false)
				+" "+PrintUtils.Print2dec(accFactor3/total, false)
				+" || "
				+" "+PrintUtils.Print2dec((accFactor3/total)*100.0/(accFactor12/total)-100.0, false)
				+" || "+PrintUtils.Print2dec(accFactor3*1.0/accFactor12, false)
				//+" "+PrintUtils.Print2dec(accL*0.1/total, false)
				//+" || "
				//+" "+PrintUtils.Print2dec(ac, false)
				);
		
	}
	
	public static void doTestZZ(ArrayList<QuoteShort> data,
			ArrayList<TrendInfo> trends,
			ArrayList<TrendInfo> trends40,
			ArrayList<TrendInfo> trends60,
			ArrayList<TrendInfo> trends80,
			ArrayList<TrendInfo> trends100,
			int y1,int y2,
			int h1,int h2,
			int aMaxExt,
			int debug
			){
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winsFloating=0;
		int lossesFloating=0;
		int others=0;
		int accOthers=0;
		int accref=0;
		int accSize=0;
		int accLosses = 0;
		int accWins = 0;
		boolean canContinue = false;
		boolean isTested = false;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;

		int winsFloatingPips = 0;
		int lostFloatingPips = 0;
		int high = -1;
		int low = -1;
		int high3 = -1;
		int low3 = -1;
		int totalDays = 0;
		double avgRange = 600.0;
		int tpf = 200;
		int slf = 200;
		int comm = 20;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int hl = 0;
		
		int openH1 = 0;
		int openH2 = 0;
		int bias = 0;
		int rangeH = -1;
		int rangeL = -1;
		int accDiff = 0;
		int accH = 0;
		int accL = 0;
		int accBias = 0;
		int total=0;
		int count10=0;
		int range12 = 0;
		double accFactor12 = 0.0;
		double accFactor3 = 0.0;
		
		int modeTest=0;
		int modeTestIdx = 0;
		int accBars = 0;
		int modeTestMaxExt = 0;
		int accExtension = 0;
		boolean testEnabled = false;
		
		int count60 = 0;
		int count120 = 0;
		int count180 = 0;
		int count240 = 0;
		for (int i=1;i<data.size();i++) {
			TrendInfo ti = trends.get(i);
			TrendInfo ti1 = trends.get(i-1);
			TrendInfo t40i = trends40.get(i);
			TrendInfo t30i = trends60.get(i);
			TrendInfo t80i = trends80.get(i);
			TrendInfo t100i = trends100.get(i);
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
						
			if (y<y1 || y>y2) continue;
			
			
			if (h==h1 && min==0 
					&& ti.getLeg()!=0
					&& ti.getMaxExtension()>=aMaxExt
					//&& ti.getLeg()!=t40i.getLeg()
					//&& ti.getLeg()!=t4i.getLeg()
					//&& ti.getLeg()!=t5i.getLeg()
					){
				modeTest = ti.getLeg();
				modeTestIdx = i;
				modeTestMaxExt = ti.getMaxExtension();
				testEnabled = true;
			}
			
			if (testEnabled){
				if (modeTest!=ti.getLeg()){
					int diffBars = (i-modeTestIdx);
					accBars+=(i-modeTestIdx);
					accExtension += ti1.getMaxExtension()-modeTestMaxExt;
					testEnabled = false;
					total++;
					
					if (diffBars<=60) count60++;
					if (diffBars<=120) count120++;
					if (diffBars<=180) count180++;
					if (diffBars<=240) count240++;
				}
			}
			
			
			
			if (debug==2){
				int diff = Math.abs(q.getOpen5()-q1.getClose5());
				if (diff>=100){
					System.out.println(DateUtils.datePrint(cal)
							+" || "+trends.get(i).toString()
							+" || "+q1.toString()+" || "+q.toString()
							);
				}
			}
			
			if (debug==4){
				int diff = Math.abs(ti.getMaxExtension()-ti1.getMaxExtension());
				if (h>=9 && h<11){
					System.out.println(DateUtils.datePrint(cal)
							+" || "+ti.toString()
							//+" || "+ti1.toString()
							//+" || "+q1.toString()+" || "+q.toString()
							);
				}
			}
			
			if (debug==3){
				int diff = Math.abs(ti.getMaxExtension()-ti1.getMaxExtension());
				if (diff>=100 && ti.getLeg()==ti1.getLeg()){
					System.out.println(DateUtils.datePrint(cal)
							+" || "+ti.toString()+" || "+ti1.toString()
							+" || "+q1.toString()+" || "+q.toString()
							);
				}
			}
			
			if (debug==1){
				int diff = Math.abs(q.getOpen5()-q1.getClose5());
				if (diff>=100){
					System.out.println(DateUtils.datePrint(cal)
							+" || "+trends.get(i).toString()
							+" || "+q1.toString()+" || "+q.toString()
							);
				}
			}
			
			if (debug==1)
			System.out.println(DateUtils.datePrint(cal)
					+" || "+trends.get(i).toString()
					+" || "+q.toString()
					);
		}
		
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" ||| "
				+" "+total
				+" "+PrintUtils.Print2dec(accBars*1.0/total, false)
				+" "+PrintUtils.Print2dec(accExtension*1.0/total, false)
				+" || "+PrintUtils.Print2dec(count60*100.0/total, false)
				+" "+PrintUtils.Print2dec(count120*100.0/total, false)
				+" "+PrintUtils.Print2dec(count180*100.0/total, false)
				+" "+PrintUtils.Print2dec(count240*100.0/total, false)
				);
		
	}
	
	public static void doTestTrading(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			ArrayList<TrendInfo> trends,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDiff,
			int tp,
			int sl,
			int debug
			){
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();
	
		int trades = 0;
		int wins=0;
		int losses=0;
		int winsFloating=0;
		int lossesFloating=0;
		int others=0;
		int accOthers=0;
		int accref=0;
		int accSize=0;
		int accLosses = 0;
		int accWins = 0;
		boolean canContinue = false;
		boolean isTested = false;
		int actualLosses = 0;
		int maxLosses = 0;
		int winPips = 0;
		int lostPips = 0;

		int winsFloatingPips = 0;
		int lostFloatingPips = 0;
		int high = -1;
		int low = -1;
		int high3 = -1;
		int low3 = -1;
		int totalDays = 0;
		double avgRange = 600.0;
		int tpf = 200;
		int slf = 200;
		int comm = 20;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int hl = 0;
		
		int openH1 = 0;
		int openH2 = 0;
		int bias = 0;
		int rangeH = -1;
		int rangeL = -1;
		int accDiff = 0;
		int accH = 0;
		int accL = 0;
		int accBias = 0;
		int total=0;
		int count10=0;
		int range12 = 0;
		double accFactor12 = 0.0;
		double accFactor3 = 0.0;
		
		int modeTest=0;
		int modeTestIdx = 0;
		int accBars = 0;
		int modeTestMaxExt = 0;
		int accExtension = 0;
		boolean testEnabled = false;
		
		int count60 = 0;
		int count120 = 0;
		int count180 = 0;
		int count240 = 0;
		
		int winPipsYear = 0;
		int lostPipsYear = 0;
		for (int i=1;i<data.size();i++) {
			TrendInfo ti = trends.get(i);
			TrendInfo ti1 = trends.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
						
			if (y<y1 || y>y2) continue;
			
			if (y!=lastYear){
				if (lastYear!=-1){
					double pfy = winPipsYear*1.0/lostPipsYear;
					if (pfy>=1.0) countYears++;
				}
				winPipsYear = 0;
				lostPipsYear = 0;
				lastYear = y;
			}
			
			if (h>=h1 && h<=h2
					&& (h>0 || (h==0 && min>=15))//en h=0 no se cogen posiciones durante los primeros 15 minutos
					//&& min==50
					){
				
				int diffe = ti.getMaxExtension()-ti.getActualExtensionClose();
				int entry = -1;
				int pips = 0;
				
				int maxMin = maxMins.get(i);
				//prueba
				diffe = 150;
				if (true
						//&& diffe>=minDiff
						&& (minDiff==0 || ti.getMaxExtension()>=minDiff)
						){					
					if (true
							&& (minDiff==0 || ti.getLeg()==1)
							&& maxMin>=thr
							){
						entry = q.getClose5();
						int valueTP = entry+10*diffe;
						int valueSL = entry-diffe;
						
						valueSL = q.getHigh5()+sl;
						valueTP = entry-tp;
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i+1, data.size()-1, entry, valueTP, valueSL, false);
						
						pips = entry-qm.getClose5();
						
					}else if (true
							&& (minDiff==0 || ti.getLeg()==-1)
							&& maxMin<=-thr
							){
						entry = q.getClose5();
						int valueTP = entry-5*diffe;
						int valueSL = entry+diffe;
						
						valueSL = q.getLow5()-sl;
						valueTP = entry+tp;
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i+1, data.size()-1, entry, valueTP, valueSL, false);
						
						pips = qm.getClose5()-entry;
					}					
				}
				
				if (entry>=0){
					pips -= comm;
					if (pips>=0){
						wins++;
						winPips += pips;
						winPipsYear += pips;
					}else{
						losses++;
						lostPips += -pips;
						lostPipsYear += -pips;
					}
				}
				
			}
			
			
		}
		
		total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = winPips*1.0/lostPips;
		double avg = (winPips-lostPips)*0.1/total;
		
		if (debug==99 
				&& pf>=1.20
				&& countYears>=8
				)
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+minDiff
				+" "+tp
				+" "+sl
				+" "+thr
				+" ||| "
				+" "+total+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "+countYears
				);
		
	}
	
	public static void main(String[] args) throws Exception {
				String path0 ="C:\\fxdata\\";
				//String pathEURUSD = path0+"eurUSD_UTC_1 Min_Bid_2011.12.31_2018.01.16.csv";
				String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2008.12.31_2018.01.12.csv";
				String pathNews = path0+"News.csv";
				
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
				ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
				//FFNewsClass.readNews(pathNews,news,0);
				for (int i = 0;i<=limit;i++){
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
					
					ArrayList<TrendInfo> trends200 = new ArrayList<TrendInfo>(); 
					ArrayList<TrendInfo> trends300 = new ArrayList<TrendInfo>(); 
					ArrayList<TrendInfo> trends400 = new ArrayList<TrendInfo>(); 
					ArrayList<TrendInfo> trends500 = new ArrayList<TrendInfo>();
					ArrayList<TrendInfo> trends600 = new ArrayList<TrendInfo>();
					ArrayList<TrendInfo> trends800 = new ArrayList<TrendInfo>();
					ArrayList<TrendInfo> trends1000 = new ArrayList<TrendInfo>();
					

					//0 10/50/100(1.49 9) 10/100/200(1.80 8) 15/50/400 (1.70 9) 10/50/500(2.57 9) 20/70/600 (3.76 9)
					//1 30/400/500(7) 5/400/1000(7) 10/400/1000(8) 40/330/1000(8) 10/330/1500(8)
					//2 40/50/1000 20/50/2000 25/40/2000 15/320/2500 20/70/2500
					//3 5/300/3500 (7) 30/20/3500(7) 15/40/4000(8)
					//4 5/330/2500(7) 10/320/2500(7)
					//5 15/15/2000(9)
					//6 30/30/2000(8)
					//7 40/80/1000(8)
					//8 35/35/10000(7)
					//9  30/300/8000(1.56 8) 30/270/10000(2.50 9) 35/350/11000(7.26 9)
					//22 20/50/8000(8) 10/200 (3.54 8)
					//23 20/60/1000(7) 40/60/2000(8) 25/60/900 (1.70 9)
					for (int h1=0;h1<=0;h1++){
						int h2 = h1+0;
						
						//System.out.println(data.size()+" "+trends200.size());
					
						
							System.out.println("testing.."+h1);
						for (int minSize = 200;minSize<=200;minSize+=200){
							trends200.clear();
							TradingUtils.calculateTrendsHL2(data, minSize,trends200);
					
							for (int aMinDiff=minSize; aMinDiff<=minSize; aMinDiff+=10){
								for (int thr=30;thr<=300;thr+=30){
									for (int tp=50;tp<=500;tp+=50){
										for (int sl=1*tp;sl<=10*tp;sl+=1*tp){
											for (int y1=2009;y1<=2009;y1++){
												int y2 = y1+9;			
												VolatilityBiasStudy.doTestTrading(data,
														maxMins,
														trends200,
														y1, y2, h1, h2,
														thr,
														aMinDiff,
														tp,sl,
														99);
											}
										}//sl
									}//tp
									for (int sl=100;sl<=500;sl+=100){
										for (int tp=1*sl;tp<=20*sl;tp+=1*sl){
											for (int y1=2009;y1<=2009;y1++){
												int y2 = y1+9;			
												VolatilityBiasStudy.doTestTrading(data,
														maxMins,
														trends200,
														y1, y2, h1, h2,
														thr,
														aMinDiff,
														tp,sl,
														99);
											}
										}//sl
									}//tp
								}
							}
						}//h1
					}//minSize
					
					/*int h1 = 0;
					int h2 = 9;
					int h3 = 23;
					for (h1=0;h1<=0;h1++){
						for (h2=18;h2<=18;h2++){
							for (int aBias=0;aBias<=0;aBias+=10){
								for (int natr=20;natr<=20;natr+=1){
									for (double aFactor=0.0;aFactor<=2.0;aFactor+=0.10){
										//VolatilityBiasStudy.doTest(data,2012, 2017, h1, h2, h3,aBias,0);
										VolatilityBiasStudy.doTestADR(data,2012, 2017, h1, h2, h3,natr,aFactor,0);
									}
								}
								
							}
						}
					}*/
					
					
				}
	}

}
