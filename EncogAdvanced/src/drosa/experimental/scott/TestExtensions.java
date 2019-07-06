package drosa.experimental.scott;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.experimental.ticksStudy.TickVolumeTest;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestExtensions {
	
	
	public static void doDetection(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int h1,int h2,
			int thr,int bars,
			int debug){
		
		int accExt = 0;
		int trades = 0;
		int wins = 0;
		
		ArrayList<Integer> breaks = new ArrayList<Integer>();
		
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size();i++){
			
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			int maxMin = maxMins.get(i);
			if (h>=h1 && h<=h2){
				
				if (maxMin>=thr){					
					TradingUtils.getMaxMinShort(data, qm, cal, i-thr, i-1);
					int high = qm.getHigh5();
					
					int maxExtension = q.getHigh5()-high;
					
					if (debug==1){
						System.out.println("[HIGH BREAK] "+q.toString()+" || "+high+" "+q.getHigh5()+" "+maxExtension);
					}
					
					boolean isReverted = false;
					
					if (q.getClose5()<=high){
						isReverted = true;
					}else{
						for (int j=i+1;j<=i+bars;j++){ //cogemos 120 arbitrariamente
							QuoteShort qj = data.get(j);
							
							int diff = qj.getHigh5()-high;
							if (diff>=maxExtension){
								maxExtension = diff;
							}
							
							if (qj.getLow5()<=high){
								isReverted = true;
								break;
							}
						}
					}
					
					accExt += maxExtension;
					trades++;
					breaks.add(maxExtension);
					if (isReverted){
						wins++;
					}
				}else if (maxMin<=-thr){
					TradingUtils.getMaxMinShort(data, qm, cal, i-thr, i-1);
					int low = qm.getLow5();
					int maxExtension = low-q.getLow5();
					
					if (debug==1){
						System.out.println("[LOW BREAK] "+q.toString()+" || "+low+" "+q.getLow5()+" "+maxExtension);
					}
					
					boolean isReverted = false;
					if (q.getClose5()>=low){
						isReverted = true;
					}else{
						for (int j=i+1;j<=i+bars;j++){ //cogemos 120 arbitrariamente
							QuoteShort qj = data.get(j);
							
							int diff = low-qj.getLow5();
							if (diff>=maxExtension){
								maxExtension = diff;
							}
							
							if (qj.getHigh5()>=low){
								isReverted = true;
								break;
							}
						}
					}
					
					accExt += maxExtension;
					trades++;
					breaks.add(maxExtension);
					if (isReverted){
						wins++;
					}
				}
								
			}
		}
		
		double avg = accExt*0.1/trades;
		double dt = Math.sqrt(MathUtils.variance(breaks))*0.1;
		double factor = dt/avg;
		System.out.println(
				h1+" "+h2
				+" "+thr
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(dt, false)
				+" || "+PrintUtils.Print2dec(factor, false)
				);
		
	}
	
	public static void doTrade(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,int thr2,
			int bars,
			int minPips,
			int tp,int sl,
			int debug){
		
		int accExt = 0;
		int trades = 0;
		int wins = 0;
		int losses = 0;
		
		ArrayList<Integer> breaks = new ArrayList<Integer>();
		
		Calendar calj = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=2;i<data.size()-1;i++){
			
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			int maxMin = maxMins.get(i-1);
			if (y>=y1 && y<=y2 && h>=h1 && h<=h2){
				
				if (maxMin>=thr){//breakout	
					
					int maxMin0 = MathUtils.getMaxMin(maxMins,i-1-bars,i-2,-1);
					if (maxMin0<=-thr2){
						continue;
					}
					
					int entry = q.getOpen5();
					int tpValue = entry - 10*tp;
					int slValue = entry + 10*sl;
					
					for (int j=i;j<data.size();j++){ 
						QuoteShort qj = data.get(j);
						QuoteShort.getCalendar(calj, qj);
						
						if (qj.getLow5()<=tpValue){
							wins++;
							if (debug==1)
							System.out.println("[SHORT TP] "+qj.toString()
									);
							break;
						}else if (qj.getHigh5()>=slValue){
							losses++;
							if (debug==1)
							System.out.println("[SHORT SL] "+qj.toString()
									);
							break;
						}					
					}
														
				}else if (maxMin<=-thr){
					int maxMin0 = MathUtils.getMaxMin(maxMins,i-1-bars,i-2,1);
					if (maxMin0>=thr2){
						continue;
					}
					
					int entry = q.getOpen5();
					int tpValue = entry + 10*tp;
					int slValue = entry - 10*sl;
					
					for (int j=i;j<data.size();j++){ 
						QuoteShort qj = data.get(j);
						QuoteShort.getCalendar(calj, qj);
						
						if (qj.getHigh5()>=tpValue){
							wins++;
							if (debug==1)
							System.out.println("[LONG TP] "+qj.toString()
									);
							break;
						}else if (qj.getLow5()<=slValue){
							losses++;
							if (debug==1)
							System.out.println("[LONG SL] "+qj.toString()
									);
							break;
						}					
					}
										
					
				}
								
			}
		}
		
		trades = wins+losses;
		double avg = (wins*tp-losses*sl)*1.0/trades;
		double pf = (wins*tp)*1.0/(losses*sl);

		System.out.println(
				h1+" "+h2
				+" "+minPips+" "+thr+" "+thr2
				+" "+tp+" "+sl
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		String fileName5min	= "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.05.31.csv";
		
		ArrayList<QuoteShort> data = null;
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(fileName5min);		
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
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			data = TradingUtils.cleanWeekendDataS(dataI);  
		}
		ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		System.out.println("Size vol: "+data.size());
		
		for (int y1=2009;y1<=2009;y1++){
			int y2 = y1+8;
			for (int h1=0;h1<=23;h1++){
				int h2 = h1+0;
				for (int thr = 500;thr<=500;thr+=100){
					for (int thr2 =800;thr2<=800;thr2+=1){
						for (int bars=36;bars<=36;bars++){
							for (int minPips=0;minPips<=0;minPips+=1){
								for (int tp=20;tp<=20;tp++){
									for (int sl=3*tp;sl<=3*tp;sl+=tp){
										//TestExtensions.doDetection("", data, maxMins, h1, h2, thr,bars, 0);
										TestExtensions.doTrade("", data, maxMins,y1,y2, h1, h2, thr,thr2, bars, minPips, tp, sl, 0);
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
