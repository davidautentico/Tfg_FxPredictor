package drosa.experimental.tooSlow;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TooSlowSystem {
	
	public static void doTradeHOLO(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int dayWeek1,int dayWeek2,
			int h1,int h2,
			int thr,
			int tp,int sl,
			boolean debug
			){
		//
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int highOpenIdx = -1;
		int lowOpenIdx = -1;
		int lastDay = -1;
		int high =-1;
		int low = -1;
		int mode = 0;
		int dayTrades = 0;
		int highIdx = -1;
		int lowIdx = -1;
		int maxMin = -1;
		boolean highUpdated = false;
		boolean lowUpdated = false;
		int actualIdx = 0;
		int totalDays = 0;
		int count = 0;
		int countDiff = 0;
		ArrayList<Integer> oArray = new ArrayList<Integer>();
		int oo = 0;
		int oi = 0;
		int io = 0;
		int lastResult = -1;
		boolean canTrade = true;
		
		double maxFactor = 0;
		int accLoss = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int nonCount = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					lastResult = -1;
					//cuantas estan dentro del highopen-lowopen
					if (q1.getClose5()<data.get(highOpenIdx).getOpen5()
						&& q1.getClose5()>data.get(lowOpenIdx).getOpen5()	
							){
						if (debug)
							//System.out.println("["+DateUtils.datePrint(cal)+"] INSIDE ");
						count++;
						if (lastResult==1){
							oi++;
						}
						lastResult = 0;
					}else{
						int diffH = q1.getClose5()-data.get(highOpenIdx).getOpen5();
						int diffL = data.get(lowOpenIdx).getOpen5()-q1.getClose5();
						
						if (diffH>=0){
							countDiff += diffH;
							if (debug)								
								//System.out.println("["+DateUtils.datePrint(cal)+"] OUTSIDE "+diffH);
							
							if (lastResult==1){
								oo++;
							}
							if (lastResult==0){
								io++;
							}
							
							lastResult = 1;
						}else if  (diffL>=0){
							countDiff += diffL;
							if (debug)
								//System.out.println("["+DateUtils.datePrint(cal)+"] OUTSIDE "+diffL);
							if (lastResult==1){
								oo++;
							}
							if (lastResult==0){
								io++;
							}
							lastResult = 1;
						}
					}
					
					canTrade = true;					
					oArray.add(lastResult);
										
					totalDays++;
				}
				
				
				mode = 0;
				high = -1;
				low = -1;
				highIdx = -1;
				lowIdx = -1;
				highOpenIdx = -1;
				lowOpenIdx = -1;
				lastDay = day;
				dayTrades = 0;
				maxMin = -1;
			}
			
			if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
			
			
			//actualizamos el mayor/menor apertura horaria del dia
			if (min==0){
				if (highOpenIdx==-1 || q.getOpen5()>=data.get(highOpenIdx).getOpen5()){
					highOpenIdx = i;
					//System.out.println("[HIGHER OPEN] "+DateUtils.datePrint(cal)+" || "+q.getOpen5());
					maxMin = maxMins.get(i);
				}
				if (lowOpenIdx==-1 || q.getOpen5()<=data.get(lowOpenIdx).getOpen5()){
					lowOpenIdx = i;
					maxMin = maxMins.get(i);
				}
			}
			
			maxMin = maxMins.get(i-1);
			
			//actualizamos el máximo y mínimo del día
			highUpdated = false;
			lowUpdated = false;
			if (highIdx==-1 || q.getHigh5()>=data.get(highIdx).getHigh5()){
				highIdx = i;
				highUpdated = true;
			}
			if (lowIdx==-1 || q.getLow5()<=data.get(lowIdx).getOpen5()){
				lowIdx = i;
				lowUpdated = true;
			}
			
			if (h>=h1 && h<=h2
					&& i>=actualIdx
					//&& canTrade
					//&& lastResult!=0
					){
				//comprobamos si hay entradas
				int entry = -1;
				int slValue = -1;
				int tpValue = -1;
				int res = 0;
				int pips = 0;
				//entrada para SELL
				if (highOpenIdx>=0){
					if (q.getOpen5()>data.get(highOpenIdx).getOpen5()+0
							&& q.getLow5()<=data.get(highOpenIdx).getOpen5()
							&& maxMin>=thr
							//&& !highUpdated //probamos que no exista actualización del high
							){				
						//testeamos el SELL
						entry	= data.get(highOpenIdx).getOpen5();
						slValue = data.get(highIdx).getHigh5()+tp*10;//dos pips de filtro
						tpValue = entry-10*tp;//dos pips de filtro
						slValue = entry+10*sl;
						//testeamos el tp
						if (q.getLow5()<=tpValue){
							res = 1;
							pips += tp*10;
							if (debug){
								System.out.println("["+DateUtils.datePrint(cal)+"] SELL WIN ");
								
							}
						}else{
							for (int j=i+1;j<data.size()-1;j++){
								QuoteShort qj = data.get(j);
								if (qj.getHigh5()>=slValue){
									res = -1;
									pips = slValue-entry;
									actualIdx = j;
									if (debug){
										System.out.println("["+DateUtils.datePrint(cal)+"] SELL LOSS "+(-pips));
									}
									break;
								}else if (qj.getLow5()<=tpValue){
									res = 1;
									pips = tp*10;
									actualIdx = j;
									if (debug){
										System.out.println("["+DateUtils.datePrint(cal)+"] SELL WIN ");
									}
									break;
								}						
							}
						}
					}
				}
				
				if (lowOpenIdx>=0){
					if (q.getOpen5()<data.get(lowOpenIdx).getOpen5()+0
							&& q.getHigh5()>=data.get(lowOpenIdx).getOpen5()
							&& maxMin<=-thr
							//&& !lowUpdated //probamos que no exista actualización del high
							){
						//testeamos el LONG
						entry	= data.get(lowOpenIdx).getOpen5();
						slValue = data.get(lowIdx).getLow5()-tp*10;//dos pips de filtro
						tpValue = entry+10*tp;//dos pips de filtro
						slValue = entry-10*sl;
						//testeamos el tp
						if (q.getHigh5()>=tpValue){
							res = 1;
							pips += tp*10;
							if (debug){
								System.out.println("["+DateUtils.datePrint(cal)+"] LONG WIN ");
							}
						}else{
							for (int j=i+1;j<data.size()-1;j++){
								QuoteShort qj = data.get(j);
								if (qj.getLow5()<=slValue){
									res = -1;
									pips = entry-slValue;
									actualIdx = j;
									if (debug){
										System.out.println("["+DateUtils.datePrint(cal)+"] LONG LOSS "+(-pips));
									}
									break;
								}else if (qj.getHigh5()>=tpValue){
									res = 1;
									pips = tp*10;
									actualIdx = j;
									if (debug){
										System.out.println("["+DateUtils.datePrint(cal)+"] LONG WIN ");
									}
									break;
								}						
							}
						}
					}
				}
				
				if (res==1){
					if (actualLosses>=4){
						nonCount++;
					}
										
					wins++;
					winPips += pips;
					canTrade = false;
					
					double f = accLoss*1.0/(tp*10);
					if (f>=maxFactor){
						maxFactor = f;
					}
					if (actualLosses>=maxLosses){
						maxLosses = actualLosses;
					}
					
					
					actualLosses = 0;
					accLoss = 0;
				}else if (res==-1){
					if (actualLosses>=4){
						nonCount++;
					}
					
					losses++;
					actualLosses++;
					lostPips += pips;
					accLoss += pips;
				}	
			}
		}
		
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/trades;
		double winInside = count*100.0/totalDays;
		
		int totalOutsides = totalDays-count;
		double probOO = oo*100.0/totalOutsides;
		double probIO = io*100.0/count;
		
		int actualSeq = 0;
		ArrayList<Integer> seqs = new ArrayList<Integer>(); 
		for (int i=0;i<oArray.size();i++){
			
			int res = oArray.get(i);
			
			if (res==1){
				actualSeq++;
			}else{
				if (actualSeq>0){
					seqs.add(actualSeq);
				}
				actualSeq = 0;
			}			
		}
		
		int count2=0;
		int count3=0;
		int count4=0;
		int count5=0;
		for (int i=0;i<seqs.size();i++){
			int res = seqs.get(i);
			if (res>=2){
				count2++;
			}
			if (res>=3){
				count3++;
			}
			if (res>=4){
				count4++;
			}
			if (res>=5){
				count5++;
			}
		}
		
		System.out.println(
				
				h1+" "+h2
				+" "+tp+" "+sl
				+" "+thr
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "
				+" "+PrintUtils.Print2dec(winInside, false)
				+" "+PrintUtils.Print2dec(countDiff*0.1/(totalOutsides), false)
				+" "+PrintUtils.Print2dec(probOO, false)
				+" "+PrintUtils.Print2dec(probIO, false)
				+" ||| "+count2+" "+count3+" "+count4+" "+count5
				+" || "
				+" "+PrintUtils.Print2dec(maxLosses, false) 
				+" || "+nonCount
				);
		
	}
	
	
	public static void doTrade(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int dayWeek1,int dayWeek2,
			int h1,int h2,
			int thr,
			int tp,int sl,
			boolean debug
			){
		//
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int highOpenIdx = -1;
		int lowOpenIdx = -1;
		int lastDay = -1;
		int high =-1;
		int low = -1;
		int mode = 0;
		int dayTrades = 0;
		int highIdx = -1;
		int lowIdx = -1;
		int maxMin = -1;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				mode = 0;
				high = -1;
				low = -1;
				highIdx = -1;
				lowIdx = -1;
				highOpenIdx = -1;
				lowOpenIdx = -1;
				lastDay = day;
				dayTrades = 0;
				maxMin = -1;
			}
			
			if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
		
			if (h>=h1 && h<=h2){
				if (highOpenIdx >=0 && q.getHigh5()>=data.get(highOpenIdx).getOpen5()){
					mode = 1;				
				}
				if (lowOpenIdx >=0 && q.getLow5()<=data.get(lowOpenIdx).getOpen5()){
					mode = -1;
				}
			}
			
			
			if (min==0){
				if (highOpenIdx==-1 || q.getOpen5()>=data.get(highOpenIdx).getOpen5()){
					highOpenIdx = i;
					//System.out.println("[HIGHER OPEN] "+DateUtils.datePrint(cal)+" || "+q.getOpen5());
					maxMin = maxMins.get(i);
				}
				if (lowOpenIdx==-1 || q.getOpen5()<=data.get(lowOpenIdx).getOpen5()){
					lowOpenIdx = i;
					maxMin = maxMins.get(i);
				}
			}
			
			
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
				highIdx = i;
			}
			
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
				lowIdx = i;
			}
						
			int entry = -1;
			int valueSL = -1;
			int valueTP = -1;
			if (dayTrades<100){
				if (mode==1){
					if (q.getOpen5()>data.get(highOpenIdx).getOpen5()+0
							&& q.getLow5()<=data.get(highOpenIdx).getOpen5()
							
							&& i>highIdx+0
							&& maxMin>=thr
							//&& (high>=0 && high-data.get(highOpenIdx).getOpen5()>=200)
							){
						//entrada short
						entry = data.get(highOpenIdx).getOpen5();
						valueSL = high+0;
						if (i==highIdx) valueSL = high+100;
						
						valueTP = entry-10*tp;		
						//System.out.println("[SHORT] "+DateUtils.datePrint(cal)+" || "+q.toString()+" || "+entry+" "+valueSL+" "+valueTP+" || "+high+" "+(high-entry));
					}
				}else if (mode==-1){
					if (q.getOpen5()<data.get(lowOpenIdx).getOpen5()-0
							&& q.getHigh5()>=data.get(lowOpenIdx).getOpen5()
							&& i>lowIdx+0
							&& maxMin<=-thr
							//&& (low>=0 && data.get(lowOpenIdx).getOpen5()-low>=200)
							){
						//entry long
						entry = data.get(lowOpenIdx).getOpen5();
						valueSL = low-0;
						if (i==lowIdx) valueSL = low-100;
						valueTP = entry+10*tp;					
					}
				}
				if (entry!=-1){
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm,i, data.size()-1,entry,valueTP, valueSL, false);
					int pips = 0;
					if (mode==1){//vamos short
						pips = entry-qm.getClose5();
					}else if (mode==-1){
						pips = qm.getClose5()-entry;
					}
					
					if (pips>=0){
						wins++;
						winPips += pips;
						//System.out.println("[WIN]");
					}else{
						losses++;
						lostPips += -pips;
						//System.out.println("[LOSS]");
					}
					dayTrades++;
				}
			}						
		}
		
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/trades;
		
		System.out.println(
				
				h1+" "+h2
				+" "+tp
				+" "+thr
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}
	
	public static void doTrade2(String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int dayWeek1,int dayWeek2,
			int h1,int h2,
			int thr,
			int tp,int sl,
			int maxBars,
			int minDiff,
			boolean debug
			){
		//
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int highOpenIdx = -1;
		int lowOpenIdx = -1;
		int lastDay = -1;
		int high =-1;
		int low = -1;
		int mode = 0;
		int dayTrades = 0;
		int highIdx = -1;
		int lowIdx = -1;
		int maxMin = -1;
		int dayOpen = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				mode = 0;
				high = -1;
				low = -1;
				highIdx = -1;
				lowIdx = -1;
				highOpenIdx = -1;
				lowOpenIdx = -1;
				lastDay = day;
				dayTrades = 0;
				maxMin = -1;
				dayOpen = q.getOpen5();
			}
			
			if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
		
			
			maxMin = maxMins.get(i-1);
			
		
			
			
			
			int entry = -1;
			int valueSL = -1;
			int valueTP = -1;
			if (h>=h1 && h<=h2){
				if (true						 	
							&& maxMin<=thr
							&& q1.getHigh5()-dayOpen>=minDiff*10
							//&& q1.getClose5()<q1.getOpen5()
							//&& (high>=0 && high-data.get(highOpenIdx).getOpen5()>=200)
							){
						//entrada short
						entry = data.get(i).getOpen5();
						valueSL = entry+10*sl;
						valueTP = entry-10*tp;	
						mode = -1;
						//System.out.println("[SHORT] "+DateUtils.datePrint(cal)+" || "+entry+" "+valueSL+" "+valueTP+" || "+high+" "+(high-entry));
				}else if (true
							&& maxMin>=-thr
							&& dayOpen-q1.getLow5()>=minDiff*10
							//&& q1.getClose5()>q1.getOpen5()
							//&& (low>=0 && data.get(lowOpenIdx).getOpen5()-low>=200)
							){
						//entry long
						entry = data.get(i).getOpen5();
						valueSL = entry-10*sl;
						valueTP = entry+10*tp;	
						mode = 1;
				}
				if (entry!=-1){
					TradingUtils.getMaxMinShortTPSL(data, qm, calqm,i, i+maxBars,valueTP, valueSL, false);
					int pips = 0;
					if (mode==-1){//vamos short
						pips = entry-qm.getClose5();
					}else if (mode==1){
						pips = qm.getClose5()-entry;
					}
					
					if (pips>=0){
						wins++;
						winPips += pips;
						//System.out.println("[WIN]");
					}else{
						losses++;
						lostPips += -pips;
						//System.out.println("[LOSS]");
					}
					//dayTrades++;
				}
			}
		}
			
			
		
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/trades;
		
		System.out.println(
				
				h1+" "+h2
				+" "+tp
				+" "+thr
				+" "+maxBars
				+" "+minDiff
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.12.21.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.12.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.12.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.12.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2017.09.12.csv";
		String pathUSDCAD = "C:\\fxdata\\USDCAD_UTC_5 Mins_Bid_2003.05.04_2017.09.12.csv";
		String pathEURGBP = "C:\\fxdata\\EURGBP_UTC_5 Mins_Bid_2003.05.04_2017.09.12.csv";
		
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		paths.add(pathUSDCAD);
		paths.add(pathEURGBP);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = paths.size()-1;
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
			//System.out.println("total data: "+data.size()+" "+maxMins.size());
			for (int y1=2010;y1<=2010;y1+=1){
				int y2 = y1+7;
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+23;
					for (int tp=5;tp<=50;tp+=1){
						for (int sl=1*tp;sl<=1*tp;sl+=tp){
							for (int thr=0;thr<=0;thr+=50){
								for (int dayWeek1 = Calendar.MONDAY;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
									int dayWeek2 = dayWeek1+4;
									TooSlowSystem.doTradeHOLO("", data, maxMins, y1, y2, dayWeek1, dayWeek2, h1, h2,thr, tp, sl,false);
									/*for (int maxBars=75;maxBars<=75;maxBars++){
										for (int minDiff=0;minDiff<=0;minDiff+=5){
											TooSlowSystem.doTrade2("", data, maxMins, y1, y2, Calendar.MONDAY+0, Calendar.MONDAY+4, h1, h2,thr, tp, sl,maxBars,minDiff,false);
										}
									}*/
								}
							}
						}
					}
				}
				
			}
			
			
		}
		
	}

}
