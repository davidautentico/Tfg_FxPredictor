package drosa.experimental.billyt;

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

public class TestSimplyDailyBreak {
	
	public static void doTest(ArrayList<QuoteShort> data,int y1,int y2,int hDaily,
			int h1,int h2,
			int tp,int sl,
			int maxTrades,int minRange,
			int offset,
			int breakMode,
			int period,
			boolean debug
			){
		
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		
		Calendar cal = Calendar.getInstance();
		int winPips = 0;
		int lostPips = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		int totalTrades = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		int actualH = -1;
		int actualL = -1;
		int lastH = -1;
		int lastL = -1;
		int tradeMode = 0;
		int dayTrades = 0;
		int tpValue = -1;
		int slValue = -1;
		int actualDailyRange = 0;
		int sameBar = 0;
		int avgPrice = -1;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int year = cal.get(Calendar.YEAR);
			
			if (year<y1 || year>y2) continue;
			
			if (day!=lastDay && h==hDaily){

				lastH = actualH;
				lastL = actualL;
				actualH = -1;
				actualL = -1;
				tpValue = -1;
				slValue = -1;
				lastDay = day;
				tradeMode = 0;
				dayTrades = 0;
				actualDailyRange = lastH-lastL;
				
				dailyRanges.add(q.getOpen5());
				int df = dailyRanges.size()-1;
				int di = df-period;				
				avgPrice = (int) MathUtils.average(dailyRanges, di, df);
				
				//System.out.println(di+" "+df+" "+avgPrice+" "+q.getOpen5()+" "+actualDailyRange);				
			}
			tradeMode = 0;
			int entryH = lastH+offset*10;
			int entryL = lastL-offset*10;
			if (dayTrades<=maxTrades 		
					&& h1<=h && h<=h2
					&& actualDailyRange<=minRange*10
					&& lastH!=-1 && lastL!=-1){
				int tpIndex = -1;
				int slIndex = -1;
				int tpOfficial = tp;
				int slOfficial = sl;
				//System.out.println("ENTRADO HIGH "+avgPrice+" "+q.getOpen5()+" "+actualDailyRange);	
				if (lastH!=-1 
						
						&& q.getOpen5()<=avgPrice
						
						&& q.getOpen5()<=entryH && q.getHigh5()>=entryH){
						tradeMode = 1;
						tpValue = entryH+10*tp*breakMode;
						slValue = entryH-10*sl*breakMode;
						
						tpOfficial = (int) Math.abs(((tpValue-entryH)*0.1));
						slOfficial = (int) Math.abs(((entryH-slValue)*0.1));
						//dayTrades++;
						//totalTrades++;
						if (breakMode==1){
							
							tpIndex = TradingUtils.getMaxMinIndex(data, i, data.size()-1, tpValue, true);
							slIndex = TradingUtils.getMaxMinIndex(data, i, data.size()-1, slValue, false);
						}else{
							tpValue = q1.getOpen5()+10*tp*breakMode;
							slValue = q1.getOpen5()-10*sl*breakMode;							
							tpOfficial = (int) Math.abs(((tpValue-q1.getOpen5())*0.1));
							slOfficial = (int) Math.abs(((q1.getOpen5()-slValue)*0.1));
							tpIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, tpValue, false);
							slIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, slValue, true);
						}
						tradeMode = 1;
				}else if (lastL!=-1 
												
						&& q.getOpen5()>=avgPrice		
						
						&& q.getOpen5()>=entryL  && q.getLow5()<=entryL){
						tradeMode = -1;
						tpValue = entryL-10*tp*breakMode;
						slValue = entryL+10*sl*breakMode;
						
						tpOfficial = (int) Math.abs(((entryL-tpValue)*0.1));
						slOfficial = (int) Math.abs(((slValue-entryL)*0.1));
						//dayTrades++;
						//totalTrades++;
						if (breakMode==1){
							tpIndex = TradingUtils.getMaxMinIndex(data, i, data.size()-1, tpValue, false);
							slIndex = TradingUtils.getMaxMinIndex(data, i, data.size()-1, slValue, true);
						}else{
							tpValue = q1.getOpen5()-10*tp*breakMode;
							slValue = q1.getOpen5()+10*sl*breakMode;							
							tpOfficial = (int) Math.abs(((q1.getOpen5()-tpValue)*0.1));
							slOfficial = (int) Math.abs(((slValue-q1.getOpen5())*0.1));
							tpIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, tpValue, true);
							slIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, slValue, false);
						}
						tradeMode = -1;
				}
				
				if (tpIndex!=-1 || slIndex!=-1){
					
					boolean valid = true;
					if (tpIndex==i && breakMode==-1){
						sameBar++;
						valid = false;
						if (tradeMode==1 && q.getClose5()<=tpValue) valid = true;
						if (tradeMode==-1 && q.getClose5()>=tpValue) valid = true;
					}
					
					if (dayTrades>=maxTrades && valid){//solo sumamos estadisticas de los dayTrades concreto
						if (slIndex==-1 || (tpIndex<=slIndex && tpIndex!=-1)){
							wins++;
							winPips+=tpOfficial;
							totalTrades++;
							
						}else if (tpIndex==-1 || (slIndex<=tpIndex && slIndex!=-1)){
							losses++;
							lostPips+=slOfficial;
							totalTrades++;							
						}
					}
					dayTrades++;
					//System.out.println(tpIndex+" "+slIndex+" || "+wins+" "+losses);
				}
			}
			
							
			if (actualH==-1 || q.getHigh5()>=actualH){
				actualH = q.getHigh5();
			}
			if (actualL == -1 || q.getLow5()<=actualL){
				actualL = q.getLow5();
			}
		}
		
		double winPer = wins*100.0/(wins+losses);
		double exp = (winPips-lostPips)*1.0/(wins+losses);
		double pf = winPips*1.0/lostPips;
		System.out.println(
				hDaily
				+" "+h1+" "+h2
				+" "+minRange
				+" "+offset
				+" "+tp+" "+sl
				+" "+period
				+" || "
				+" "+totalTrades
				+" "+wins
				+" "+losses
				+" "+(totalTrades-wins-losses)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(exp, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+sameBar
				+" "+PrintUtils.Print2dec(sameBar*100.0/totalTrades, false)
				+" "+maxLosses
				);
}
	
	public static void doTest(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,int h1,int h2,
			int hDaily,int tp,int sl,
			int thr,
			int maxTrades,int maxRange,int breakingMode,
			boolean debug){
	
			Calendar cal = Calendar.getInstance();
			int winPips = 0;
			int lostPips = 0;
			int actualLosses = 0;
			int maxLosses = 0;
			int totalTrades = 0;
			int wins = 0;
			int losses = 0;
			int lastDay = -1;
			int actualH = -1;
			int actualL = -1;
			int lastH = -1;
			int lastL = -1;
			int tradeMode = 0;
			int dayTrades = 0;
			int tpValue = -1;
			int slValue = -1;
			int equals = 0;
			int lastResult = 0;
			double factor = 1.0;
			for (int i=0;i<data.size()-1;i++){
				QuoteShort q = data.get(i);
				QuoteShort q1 = data.get(i+1);
				QuoteShort.getCalendar(cal, q);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int year = cal.get(Calendar.YEAR);
				
				if (year<y1 || year>y2) continue;
				
				if (day!=lastDay && h==hDaily){

					lastH = actualH;
					lastL = actualL;
					actualH = -1;
					actualL = -1;
					tpValue = -1;
					slValue = -1;
					lastDay = day;
					tradeMode = 0;
					dayTrades = 0;
					factor = 1.0;
				}
				
				int maxMin = maxMins.get(i).getExtra();
				int range = lastH-lastL;
				if (range>=maxRange*10) continue;
				if (dayTrades<maxTrades && lastH!=-1 && lastL!=-1){
					int tpIndex = -1;
					int slIndex = -1;
					if (lastResult==-1) factor = 10.0;
					if (lastH!=-1 
							//&& q.getOpen5()<=lastH && 
							&& maxMin>=thr
							&& q.getHigh5()>=lastH){
							if (h1<=h && h<=h2){
								if (breakingMode == 1){									
									tradeMode = 1;
									
									tpValue = q1.getOpen5()+10*tp;
									slValue = q1.getOpen5()-10*sl;
									tpIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, tpValue, true);
									slIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, slValue, false);
								}else if (breakingMode == -1){
									tradeMode = -1;
									tpValue = (int) (q1.getOpen5()-10*tp*factor);
									slValue = q1.getOpen5()+10*sl;
									tpIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, tpValue, false);
									slIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, slValue, true);
									if (debug)
									System.out.println("[HIGH TOUCHED SELL] "+tpValue+" "+slValue+" || "+tpIndex+" "+slIndex);
								}
								dayTrades++;
							}else{
								dayTrades++;
							}
					}else if (lastL!=-1 
							//&& h1<=h && h<=h2
							//&& q.getOpen5()>=lastL 
							&& maxMin<=-thr
							&& q.getLow5()<=lastL){
							if (h1<=h && h<=h2){
								//dayTrades++;
								//totalTrades++;
								if (breakingMode == 1){
									tradeMode = -1;
									tpValue = q1.getOpen5()-10*tp;
									slValue = q1.getOpen5()+10*sl;
									tpIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, tpValue, false);
									slIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, slValue, true);
								}else if (breakingMode==-1){
									tradeMode = 1;
									tpValue = (int) (q1.getOpen5()+10*tp*factor);
									slValue = q1.getOpen5()-10*sl;
									tpIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, tpValue, true);
									slIndex = TradingUtils.getMaxMinIndex(data, i+1, data.size()-1, slValue, false);
									if (debug)
									System.out.println("[LOW TOUCHED BUY] "+tpValue+" "+slValue+" || "+tpIndex+" "+slIndex);
								}
								dayTrades++;
							}else{
								dayTrades++;
							}
					}
					
					if (tpIndex!=-1 || slIndex!=-1){
						totalTrades++;
						//dayTrades++;
						
						if (slIndex==-1 || (tpIndex<slIndex && tpIndex!=-1)){
							wins++;
							winPips+=Math.abs(tpValue-q1.getOpen5())*0.1;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;							
							actualLosses = 0;
							lastResult = 1;
							if (tpIndex==i) equals++;
						}else if (tpIndex==-1 || (slIndex<=tpIndex && slIndex!=-1)){
							losses++;
							lostPips+=sl;
							lastResult = -1;
							actualLosses++;
							factor += 1.0;
							//if (debug)
							//if (actualLosses>=2)
								//System.out.println(actualLosses+" "+DateUtils.datePrint(cal)+" "+tradeMode+" "+lastL+" "+lastH);
						}
						//System.out.println(tpIndex+" "+slIndex+" || "+wins+" "+losses);
					}
				}
				
								
				if (actualH==-1 || q.getHigh5()>=actualH){
					actualH = q.getHigh5();
				}
				if (actualL == -1 || q.getLow5()<=actualL){
					actualL = q.getLow5();
				}
			}
			
			double winPer = wins*100.0/(wins+losses);
			double exp = (winPips-lostPips)*1.0/(wins+losses);
			double pf = winPips*1.0/lostPips;
			System.out.println(
					hDaily+" "+h1+" "+h2
					+" "+tp+" "+sl+" "+thr
					+" || "
					+" "+totalTrades
					+" "+wins
					+" "+losses
					+" "+equals
					+" "+(totalTrades-wins-losses)
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(exp, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" "+maxLosses
					);
	}
	
	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.05.03.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.17.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_1 Min_Bid_2008.12.31_2016.05.03.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2008.12.31_2016.05.03.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2016.05.03.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_1 Min_Bid_2008.12.31_2016.05.03.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2008.12.31_2016.05.03.csv";
		String pathEURCAD = "C:\\fxdata\\EURCAD_UTC_5 Mins_Bid_2003.05.04_2016.02.28.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.05.04_2016.02.28.csv";
		String pathEURGBP = "C:\\fxdata\\EURGBP_UTC_5 Mins_Bid_2003.05.04_2016.02.28.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.05.04_2016.02.28.csv";
		String pathGBPAUD = "C:\\fxdata\\GBPAUD_UTC_5 Mins_Bid_2003.05.04_2016.02.28.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathGBPUSD);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		paths.add(pathEURJPY);paths.add(pathGBPJPY);
		paths.add(pathEURAUD);paths.add(pathEURCAD);
		paths.add(pathEURGBP);paths.add(pathNZDUSD);
		paths.add(pathGBPAUD);
		
		int limit = paths.size()-1;
		limit = 0;
		for (int i = 0;i<=limit;i++){
			Sizeof.runGC ();
			String path = paths.get(i);	
			ArrayList<Quote> dataI 		= null;
			ArrayList<Quote> dataS 		= null;
			if (path.contains("pepper")){
				dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				//provider="pepper";
			}else if (path.contains("forexdata")){
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				//provider="forexdata";
			}else{
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				//provider="dukasc";
			}										
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	
		  	dataS.clear();
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			data5m.clear();
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			//dataI.clear();
			//dataS.clear();
			//data5m.clear();
			data = data5mS;
			//System.out.println(data.size());
			ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			for (int y1=2003;y1<=2003;y1++){
				int y2 = 2017;
					for (int h=0;h<=0;h++){
						for (int tp=10;tp<=10;tp+=10){
							for (int sl=10;sl<=200;sl+=10){
								for (int minRange=100;minRange<=100;minRange+=10){
									for (int maxTrades = 1;maxTrades<=1;maxTrades++){
										//TestSimplyDailyBreak.doTest(data, y1, y2, h, tp, sl,maxTrades);
										for (int h1=0;h1<=0;h1++){
											int h2 = h1+9;
											for (int offset=0;offset<=0;offset+=1){
												for (int maxRange=500;maxRange<=500;maxRange+=10){
													for (int period =40 ;period<=40;period+=10){
														//TestSimplyDailyBreak.doTest(data, y1, y2, h,h1,h2, tp, sl,maxTrades,minRange,offset,-1,period);
														for (int thr=500;thr<=500;thr+=100){
															TestSimplyDailyBreak.doTest(data,maxMins, y1, y2, h1,h2,h, tp, sl,thr,maxTrades,maxRange,-1,false);
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
				
			}
		}
	}

}
