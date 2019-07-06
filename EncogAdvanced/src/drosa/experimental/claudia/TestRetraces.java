package drosa.experimental.claudia;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradeLog;
import drosa.utils.TradingUtils;

public class TestRetraces {

	
	
	public static void testMaxRetracing(ArrayList<QuoteShort> data,int begin,int end,int h1,int h2){
		
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
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (retracement>=0){
					//System.out.println(DateUtils.datePrint(cal1)+" "+PrintUtils.Print2(retracement*0.1));
					totalretracement += retracement;
					totalDays++;
				}
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				settlement=false;
				lastDay = day;	
				settlementMax = -1;
				settlementMin = -1;
				retracement=-1;
			}
			//deteccion de maximos o minimos
			if (h>=h1 & h<=h2 && !settlement){
				if (q.getHigh5()>max){
					longEnabled = true;
					shortEnabled = false;
					settlement = true;
					settlementMax = q.getHigh5();
					//System.out.println("MAX: "+q.toString()+" "+settlementMax);
				}
				if (q.getLow5()<min && !settlement){
					longEnabled = false;
					shortEnabled = true;
					settlement = true;
					settlementMin = q.getLow5();
				}
			}
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
			//calculo de retracement;
			if (longEnabled){
				int diff = settlementMax-q.getLow5();
				if (diff>=retracement || retracement==-1){
					retracement = diff;
					//System.out.println(settlementMax+" "+q.getLow5());
				}
			}
			if (shortEnabled){
				int diff = q.getHigh5()-settlementMin;
				if (diff>=retracement || retracement==-1) retracement = diff;
			}
		}
		double avgRetrace = totalretracement*0.1/totalDays;
		System.out.println(totalDays+" "+h1+" "+h2+" || "+PrintUtils.Print2(avgRetrace));
	}
	
public static void testMaxRetracingBars(
		ArrayList<QuoteShort> data,
		ArrayList<Integer> maxMins,
		int begin,int end,int h1,int h2,
		int thr,
		int nBars){
		
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
		QuoteShort qm = new QuoteShort();
		Calendar cal2 = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (retracement>=0){
					//System.out.println(DateUtils.datePrint(cal1)+" "+PrintUtils.Print2(retracement*0.1));
					totalretracement += retracement;
					totalDays++;
				}
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				settlement=false;
				lastDay = day;	
				settlementMax = -1;
				settlementMin = -1;
				retracement=-1;
			}
			
			int maxMin = maxMins.get(i-1);
			//deteccion de maximos o minimos
			if (h>=h1 & h<=h2 && !settlement){
				if (q.getHigh5()>max
						&& maxMin>=thr
						){
					longEnabled = true;
					shortEnabled = false;
					settlement = true;
					settlementMax = q.getHigh5();
					//System.out.println("MAX: "+q.toString()+" "+settlementMax);
				}
				if (q.getLow5()<min 
						&& maxMin<=-thr
						
						&& !settlement){
					longEnabled = false;
					shortEnabled = true;
					settlement = true;
					settlementMin = q.getLow5();
				}
			}
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
			//calculo de retracement;
			if (longEnabled){
				TradingUtils.getMaxMinShort(data,qm,cal2, i, i+nBars);
				int diff = settlementMax-qm.getLow5();
				if (diff>=retracement || retracement==-1){
					retracement = diff;
					//System.out.println(settlementMax+" "+q.getLow5());
				}
			}
			if (shortEnabled){
				TradingUtils.getMaxMinShort(data,qm,cal2, i, i+nBars);
				int diff = qm.getHigh5()-settlementMin;
				if (diff>=retracement || retracement==-1) retracement = diff;
			}
		}
		double avgRetrace = totalretracement*0.1/totalDays;
		System.out.println(totalDays+" "+h1+" "+h2+" || "+PrintUtils.Print2(avgRetrace));
	}

public static void testRetraceFailsInARow(
		ArrayList<QuoteShort> data,
		ArrayList<Integer> maxMins,
		int begin,int end,int h1,int h2,
		int thr,
		int nBars,
		int minPips,
		int tp,
		int tries
		){
		
		if (begin<1) begin = 1;
		if (end>=data.size()) end = data.size()-1;
		
		int count = 0;
		int totalLosses =0;
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
		QuoteShort qm = new QuoteShort();
		Calendar cal2 = Calendar.getInstance();
		boolean dayTried = false;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (retracement>=0){
					//System.out.println(DateUtils.datePrint(cal1)+" "+PrintUtils.Print2(retracement*0.1));
					totalretracement += retracement;
					totalDays++;
				}
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				settlement=false;
				lastDay = day;	
				settlementMax = -1;
				settlementMin = -1;
				retracement=-1;
				
				dayTried = false;
			}
			
			
			if (h>=h1 && h<=h2
					&& !dayTried
					){
				int maxMin = maxMins.get(i-1);
				
				
				if (maxMin>=thr){
					//a partir de aqui, tenemos que probar 'tries' veces y ver si consigo tp 					
					int wins = 0;
					int actualTries = 0;
					int referencePrice = q.getOpen5();//precio de referencia
					int endj = i + nBars;
					if (endj>=end) endj=end;
					for (int j=i;j<=endj;j++){
						QuoteShort qj = data.get(j);
						int diff = referencePrice-qj.getOpen5();
						if (diff>=10*minPips){
							TradingUtils.getMaxMinShort(data, qm, cal2, j, endj);
							int profit = qm.getHigh5()-qj.getOpen5();
							if (profit>=10*tp){
								wins++;
							}
							referencePrice = qj.getOpen5();
							actualTries++;
						}
						
						if (actualTries>=tries) break;
					}
					
					if (actualTries>0){
						count++;
						if (wins==0){
							totalLosses++;
						}
						dayTried = true;
					}
					
				}else if (maxMin<=-thr){
					//a partir de aqui, tenemos que probar 'tries' veces y ver si consigo tp 					
					int wins = 0;
					int actualTries = 0;
					int referencePrice = q.getOpen5();//precio de referencia
					int endj = i + nBars;
					if (endj>=end) endj=end;
					for (int j=i;j<=endj;j++){
						QuoteShort qj = data.get(j);
						int diff = qj.getOpen5()-referencePrice;
						if (diff>=10*minPips){
							TradingUtils.getMaxMinShort(data, qm, cal2, j, endj);
							int profit = qj.getOpen5()-qm.getLow5();
							if (profit>=10*tp){
								wins++;
							}
							referencePrice = qj.getOpen5();
							actualTries++;
						}
						
						if (actualTries>=tries) break;
					}
					
					if (actualTries>0){
						count++;
						if (wins==0){
							totalLosses++;
						}
						dayTried = true;
					}
				}
			}
			
		}
		
		
		double lossPer = totalLosses*100.0/count;
		System.out.println(
				thr
				+" "+nBars
				+" "+minPips
				+" "+tp
				+" "+tries
				
				+" || "
				+" "+count
				+" "+totalLosses
				+" "+PrintUtils.Print2dec(lossPer, false)
				);
	}

public static void testRetracev2(
		ArrayList<QuoteShort> data,
		ArrayList<Integer> maxMins,
		int begin,int end,int h1,int h2,
		int thr,
		int nBars,
		int minPips,
		int tp,
		int sl,
		int tries
		){
		
		if (begin<1) begin = 1;
		if (end>=data.size()) end = data.size()-1;
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int totalPips = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalLosses =0;
		int totalDayLosses = 0;
		int totalDays = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		boolean settlement = false;
		int retracement = -1;
		int totalretracement = 0;
		int settlementMax = -1;
		int settlementMin = -1;
		QuoteShort qm = new QuoteShort();
		Calendar cal2 = Calendar.getInstance();
		boolean dayTried = false;
		boolean dayLost = false;
		int lastprice = 0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				
				if (dayTried){
					if (dayLost){
						totalDayLosses++;
					}
					totalDays++;
				}
				
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				settlement=false;
				lastDay = day;	
				settlementMax = -1;
				settlementMin = -1;
				retracement=-1;
				
				dayTried = false;
				dayLost = false;
			}
			
			
			if (h>=h1 && h<=h2
					&& !dayTried
					){
				int maxMin = maxMins.get(i-1);
				
				
				if (maxMin>=thr){
					//a partir de aqui, tenemos que probar 'tries' veces y ver si consigo tp 					
					int endj=i+nBars;
					if (endj>=data.size()-1) endj = data.size()-1; 
					boolean tried = false;
					int dayLosses = 0;
					int lastPriceH = 999999;
					for (int j=i;j<=endj;j++){
						
						int entry = data.get(j).getOpen5();
						int tpvalue = entry+10*tp;
						int slValue = entry-10*sl;
						
						if (entry<=lastPriceH-10*minPips){
												
							TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal2, j, data.size()-1, entry, tpvalue, slValue, false);
							
							int pips = qm.getClose5()-entry;
							
							lastPriceH = entry;
							tried = true;
							if (pips>=0){
								wins++;
								totalPips += pips;
								winPips += pips;
								
								//System.out.println("[WIN] "+DateUtils.datePrint(cal)+" || "+entry);
							}else{
								losses++;
								totalPips+=pips;
								lostPips += -pips;
								dayLosses++;
								//System.out.println("[LOSS] "+DateUtils.datePrint(cal)+" || "+entry);
							}
						}
					}
					
					if (tried){
						if (dayLosses>=tries){
							dayLost = true;
							//System.out.println("[DAYLOST] "+DateUtils.datePrint(cal));
						}
						dayTried = true;
					}
										
				}else if (maxMin<=-thr){
					
					//a partir de aqui, tenemos que probar 'tries' veces y ver si consigo tp 					
					int endj=i+nBars;
					if (endj>=data.size()-1) endj = data.size()-1; 
					boolean tried = false;
					int dayLosses = 0;
					int lastPriceL = -999999;
					for (int j=i;j<=endj;j++){
						
						int entry = data.get(j).getOpen5();
						int tpvalue = entry-10*tp;
						int slValue = entry+10*sl;
						
						if (entry>=lastPriceL+10*minPips){
							TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal2, j, data.size()-1, entry, tpvalue, slValue, false);
							
							int pips = entry-qm.getClose5();
							
							lastPriceL = entry;
							tried = true;
							if (pips>=0){
								wins++;
								totalPips += pips;
								winPips += pips;
							}else{
								losses++;
								totalPips+=pips;
								lostPips += -pips;
								dayLosses++;
							}
						}
					}
					
					if (tried){
						if (dayLosses>=tries){
							dayLost = true;
						}
						dayTried = true;
					}
				}
			}
			
		}
		
		count = wins+losses;
		
		double lossPer = losses*100.0/count;
		double dayLossPer = totalDayLosses*100.0/totalDays;
		System.out.println(
				thr
				+" "+nBars
				+" "+minPips
				+" "+tp
				+" "+sl		
				+" "+tries
				+" || "
				+" "+count
				+" "+losses
				+" "+PrintUtils.Print2dec(lossPer, false)
				+" || "+totalDays+" "+totalDayLosses+" "+PrintUtils.Print2dec(dayLossPer, false)
				);
	}

public static void testRetracev3(
		ArrayList<QuoteShort> data,
		ArrayList<Integer> maxMins,
		int begin,int end,int h1,int h2,
		int thr,
		int nBars,
		int minBars,
		int tp,
		int sl,
		int tries
		){
		
		if (begin<1) begin = 1;
		if (end>=data.size()) end = data.size()-1;
		
		int count = 0;
		int wins = 0;
		int losses = 0;
		int totalPips = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalLosses =0;
		int totalDayLosses = 0;
		int totalDays = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		boolean settlement = false;
		int retracement = -1;
		int totalretracement = 0;
		int settlementMax = -1;
		int settlementMin = -1;
		QuoteShort qm = new QuoteShort();
		Calendar cal2 = Calendar.getInstance();
		boolean dayTried = false;
		boolean dayLost = false;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				
				if (dayTried){
					if (dayLost){
						totalDayLosses++;
					}
					totalDays++;
				}
				
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				settlement=false;
				lastDay = day;	
				settlementMax = -1;
				settlementMin = -1;
				retracement=-1;
				
				dayTried = false;
				dayLost = false;
			}
			
			
			if (h>=h1 && h<=h2
					&& !dayTried
					){
				int maxMin = maxMins.get(i-1);
				
				
				if (maxMin>=thr){
					//a partir de aqui, tenemos que probar 'tries' veces y ver si consigo tp 					
					int endj=i+nBars;
					if (endj>=data.size()-1) endj = data.size()-1; 
					boolean tried = false;
					int dayLosses = 0;
					for (int j=i;j<=endj;j++){
						
						int entry = data.get(j).getOpen5();
						int tpvalue = entry+10*tp;
						int slValue = entry-10*sl;
												
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal2, j, data.size()-1, entry, tpvalue, slValue, false);
						
						int pips = qm.getClose5()-entry;
						
						tried = true;
						if (pips>=0){
							wins++;
							totalPips += pips;
							winPips += pips;
						}else{
							losses++;
							totalPips+=pips;
							lostPips += -pips;
							dayLosses++;
						}
					}
					
					if (tried){
						if (dayLosses>0){
							dayLost = true;
						}
						dayTried = true;
					}
										
				}else if (maxMin<=-thr){
					
					//a partir de aqui, tenemos que probar 'tries' veces y ver si consigo tp 					
					int endj=i+nBars;
					if (endj>=data.size()-1) endj = data.size()-1; 
					boolean tried = false;
					int dayLosses = 0;
					for (int j=i;j<=endj;j++){
						
						int entry = data.get(j).getOpen5();
						int tpvalue = entry-10*tp;
						int slValue = entry+10*sl;
						
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal2, j, data.size()-1, entry, tpvalue, slValue, false);
						
						int pips = entry-qm.getClose5();
						
						if (pips>=0){
							wins++;
							totalPips += pips;
							winPips += pips;
						}else{
							losses++;
							totalPips+=pips;
							lostPips += -pips;
							dayLosses++;
						}
					}
					
					if (tried){
						if (dayLosses>0){
							dayLost = true;
						}
						dayTried = true;
					}
				}
			}
			
		}
		
		count = wins+losses;
		
		double lossPer = losses*100.0/count;
		double dayLossPer = totalDayLosses*100.0/totalDays;
		System.out.println(
				thr
				+" "+nBars
				//+" "+minPips
				+" "+tp
				+" "+sl				
				+" || "
				+" "+count
				+" "+losses
				+" "+PrintUtils.Print2dec(lossPer, false)
				+" || "+totalDays+" "+totalDayLosses+" "+PrintUtils.Print2dec(dayLossPer, false)
				);
	}

	public static void testRetracev4(
		ArrayList<QuoteShort> data,
		ArrayList<Integer> maxMins,
		int begin,int end,int h1,int h2,
		int thr,
		int tp,
		int sl,
		int diff
		){
		
		
		int accDiffs = 0;
		int totalTrades = 0;
		int losses = 0;
		
		Calendar cal	= Calendar.getInstance();
		Calendar cal1	= Calendar.getInstance();
		
		ArrayList<Integer> dailyCloses = new ArrayList<Integer>();
		int max = -1;
		int min = -1;
		int lastDay = -1;
		double avgClose = -1;
		for (int i=1;i<=data.size()-1;i++){
			
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				
				if (max!=-1){
					int range = max-min;
					dailyCloses.add(q1.getClose5());
					avgClose = MathUtils.average(dailyCloses , dailyCloses.size()-20, dailyCloses.size()-1); 
				}
				
				
				max = -1;
				min = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (h>=h1 && h<=h2){			
				if (maxMin>=thr 
						&& (avgClose==-1 || q.getOpen5()<=avgClose-10*diff) 
						
						){ //sell
									
					int entry = q.getOpen5();
					int tpValue = entry - 10 *tp;
					
					int maxDiff = entry;
					
					int win = 0;
					for (int j=i;j<=data.size()-1;j++){
						QuoteShort qj = data.get(j);
						if (qj.getLow5()<=tpValue){
							win = 1;
							break;
						}
						
						if (qj.getHigh5()>=maxDiff){
							maxDiff = qj.getHigh5();
						}
						
						int diffPips = maxDiff-entry;
						if (diffPips>=sl*10){
							win = -1;
							break;
						}
					}
					
					if (win==-1){
						accDiffs += sl*10;
						totalTrades++;
						losses++;
					}else{
						accDiffs += maxDiff-entry;
						totalTrades++;
					}								
				}else if (maxMin<=-thr
						&& (avgClose==-1 || q.getOpen5()>=avgClose+10*diff) 
						){ // buy
					
					int entry = q.getOpen5();
					int tpValue = entry - 10 *tp;
					
					int maxDiff = entry;
					
					int win = 0;
					for (int j=i;j<=data.size()-1;j++){
						QuoteShort qj = data.get(j);
						if (qj.getHigh5()>=tpValue){
							win = 1;
							break;
						}
						
						if (qj.getLow5()<=maxDiff){
							maxDiff = qj.getLow5();
						}
						
						int diffPips = entry-maxDiff;
						if (diffPips>=sl*10){
							win = -1;
							break;
						}
					}
					
					if (win==-1){
						accDiffs += sl*10;
						totalTrades++;
						losses++;
					}else{
						accDiffs += entry-maxDiff;
						totalTrades++;
					}									
				}//thr
			}//h
			
			if (max==-1 || q.getHigh5()>=max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<=min) min = q.getLow5();
			
		}//for
		
		
		double avg = accDiffs*0.1/totalTrades;
		double lossPer = losses*100.0/totalTrades;
		double factor = avg/tp;
		System.out.println(
				
				tp+" "+sl+" "+thr+" "+diff+" "+h1+" "+h2
				+" || "
				+" "+totalTrades
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(lossPer, false)
				+" || "+PrintUtils.Print2dec(factor, false)
		);
	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName   ="5 Mins_Bid_2003.05.04_2017.07.31.csv";
						
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
		int initial = 0;
		limit       = 0;
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i=initial;i<=limit;i++){
			String path = paths.get(i);	
			String pairName = paths.get(i).split("\\\\")[2].substring(0, 6);
			if (path.contains("UTC")){
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);
			}else{
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);
			}
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  			
			ArrayList<QuoteShort> data = null;
			data = dataS;
			//System.out.println("total data: "+data.size());
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			int begin = 4000000;
			int end   = 5000000;
			int tp = 10;
			int sl = 20;
			double comm = 1.4;
			
			for (int h1=0;h1<=0;h1+=1){
				int h2 = h1+9;
				for (int thr=0;thr<=1000;thr+=100){
					for (int diff=20;diff<=20;diff+=10){
						for (tp=10;tp<=10;tp++){
							for (sl=40000;sl<=40000;sl+=50){
								TestRetraces.testRetracev4(data, maxMins, begin, end, h1, h2, thr, tp,sl,diff);
							}
						}
					}
				}
			}
			
			
			/*for (begin=1;begin<=1;begin+=100000){
				end = begin + 7000000;
				for (int nBars=1000;nBars<=1000;nBars+=12){
					for (int h1=16;h1<=16;h1+=1){
						int h2 = h1+7;
					//for (int nBars=120;nBars<=120;nBars++){
						//TestRetraces.testMaxRetracing(data, begin, end, h1, h2);
						for (int thr=2000;thr<=2000;thr+=100){
							//TestRetraces.testMaxRetracingBars(data,maxMins, begin, end, h1, h2,thr,nBars);
							for (int minPips=10;minPips<=10;minPips++){
								for (int tries=10;tries<=10;tries++){
									for (tp=10;tp<=10;tp++){
										//TestRetraces.testRetraceFailsInARow(data, maxMins, begin, end, h1, h2, thr, nBars, minPips, tp, tries);
										for (sl=100;sl<=100;sl+=50){
											for (int minDayLost=1;minDayLost<=10;minDayLost++){
												TestRetraces.testRetracev2(data, maxMins, begin, end, h1, h2, thr, nBars, minPips, tp,sl, minDayLost);
											}
										}
									}
								}
							}
						}
					}
				}
			}*/
			
		}
	}

}
