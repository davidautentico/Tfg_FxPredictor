package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestInfluxIndicator {
	
	public static void testInflux(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int nAtr,
			int tp,
			int sl,
			int thr1,
			double thr,
			int maxBars,
			int diffClose,
			boolean isReverse,
			int debug
			) {
		
		double balanceInicial =10000;
		double balance =10000;
		double maxbalance = 10000;
		double maxdd = 0;
		double balanceNeed=0;
		double win$$=0;
		double lost$$=0;
		
		
		Calendar cal = Calendar.getInstance();
	
		int lastDay = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int thrValue = 0;
		int thrMode = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		int n= 200;
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		QuoteShort qm = new QuoteShort();
		int acc = 0;
		int count = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		for (int i=n;i<data.size();i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
															
				}						
				thrValue = 0;
				thrMode = 0;
				lastDay = day;				
			}
			
			
			int  maxMin = maxMins.get(i-1);
			
			int currentDiff = q.getOpen5()-q1.getOpen5();
			diffs.add(currentDiff);
			
			double influx = MathUtils.average(diffs,diffs.size()-nAtr, diffs.size()-1);
			
			if (h>=h1 && h<=h2){			
				if (influx>=thr
						&& maxMin>=thr1
						){
					
					TradingUtils.getMaxMinShort(data, qm, cal, i-nAtr, i-1);
					
					if (qm.getHigh5()-q1.getClose5()>=diffClose){
						
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, q.getOpen5(), q.getOpen5()+tp, q.getOpen5()-sl, false);
						
						int res = (qm.getClose5()-q.getOpen5());
						if (res>=0){
							wins++;
							winPips += res;
						}else{
							losses++;
							lostPips += -res;
						}
						acc+= res;
						
						count++;
					}
				}else if (influx<=-thr
						&& maxMin<=-thr1
						){
					
					TradingUtils.getMaxMinShort(data, qm, cal, i-nAtr, i-1);
					
					if (q1.getClose5()-qm.getLow5()>=diffClose){
					
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, q.getOpen5(), q.getOpen5()-tp, q.getOpen5()+sl, false);
						
						int res = (q.getOpen5()-qm.getClose5());
						if (res>=0){
							wins++;
							winPips += res;
						}else{
							losses++;
							lostPips += -res;
						}
						acc+= res;
						
						count++;
					}
				}
			}

		}
		//System.out.println(header);
		double per = wins*100.0/(wins+losses);
		double pf = winPips*1.0/lostPips;
		if (count>0)
		System.out.println(header+" "+
				nAtr+" "+PrintUtils.Print2dec(thr, false)+" "+maxBars+" "+diffClose
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(per, false)
				+" "+PrintUtils.Print2dec(acc*0.1/count, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	
	public static void testInflux2(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int nAtr,
			int tp,
			int sl,
			int thr1,
			double thr,
			int maxBars,
			int diffClose,
			boolean isReverse,
			int debug
			) {
		
		double balanceInicial =10000;
		double balance =10000;
		double maxbalance = 10000;
		double maxdd = 0;
		double balanceNeed=0;
		double win$$=0;
		double lost$$=0;
		
		
		Calendar cal = Calendar.getInstance();
	
		int lastDay = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int thrValue = 0;
		int thrMode = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		int n= 200;
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		QuoteShort qm = new QuoteShort();
		int acc = 0;
		int count = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		for (int i=n;i<data.size();i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
															
				}						
				thrValue = 0;
				thrMode = 0;
				lastDay = day;				
			}
			
			
			int  maxMin = maxMins.get(i-1);
			
			int currentDiff = q.getOpen5()-q1.getOpen5();
			diffs.add(currentDiff);
			
			double influx = MathUtils.average(diffs,diffs.size()-nAtr, diffs.size()-1);
			
			if (h>=h1 && h<=h2){			
				if (influx>=thr
						&& maxMin>=thr1
						&& q1.getHigh5()-q1.getClose5()>=diffClose
						){
					
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, q.getOpen5(), q.getOpen5()+tp, q.getOpen5()-sl, false);
					
					int res = (qm.getClose5()-q.getOpen5());
					if (res>=0){
						wins++;
						winPips += res;
					}else{
						losses++;
						lostPips += -res;
					}
					acc+= res;
					
					count++;
					
					
				}else if (influx<=-thr
						&& maxMin<=-thr1
						&& q1.getClose5()-q1.getLow5()>=diffClose
						){
					
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, q.getOpen5(), q.getOpen5()-tp, q.getOpen5()+sl, false);
					
					int res = (q.getOpen5()-qm.getClose5());
					if (res>=0){
						wins++;
						winPips += res;
					}else{
						losses++;
						lostPips += -res;
					}
					acc+= res;
					
					count++;
				}
			}

		}
		//System.out.println(header);
		double per = wins*100.0/(wins+losses);
		double pf = winPips*1.0/lostPips;
		if (count>0)
		System.out.println(header+" "+
				nAtr+" "+PrintUtils.Print2dec(thr, false)+" "+maxBars+" "+diffClose
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(per, false)
				+" "+PrintUtils.Print2dec(acc*0.1/count, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static double testInflux3(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int nAtr,
			int tp,
			int sl,
			int thr1,
			double thr,
			int maxBars,
			int diffClose,
			boolean isReverse,
			int debug
			) {
		
		double balanceInicial =10000;
		double balance =10000;
		double maxbalance = 10000;
		double maxdd = 0;
		double balanceNeed=0;
		double win$$=0;
		double lost$$=0;
		
		
		Calendar cal = Calendar.getInstance();
	
		int lastDay = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int thrValue = 0;
		int thrMode = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		int n= 200;
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		QuoteShort qm = new QuoteShort();
		int acc = 0;
		int count = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		for (int i=n;i<data.size();i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
															
				}						
				thrValue = 0;
				thrMode = 0;
				lastDay = day;				
			}
			
			
			int  maxMin = maxMins.get(i-1);
			
			int currentDiff = q.getOpen5()-q1.getOpen5();
			diffs.add(currentDiff);
			
			double influx = MathUtils.average(diffs,diffs.size()-nAtr, diffs.size()-1);
			int comm = 20;
			if (h>=h1 && h<=h2){			
				if (influx>=thr
						//&& maxMin>=thr1
						&& q1.getHigh5()-q1.getClose5()>=diffClose
						){
					
					int pipsSL = q.getOpen5()-data.get(i-nAtr).getOpen5();
					if (pipsSL<=200) pipsSL = 200;
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, q.getOpen5(), q.getOpen5()+1*pipsSL, q.getOpen5()-1*pipsSL, false);
					
					int res =qm.getOpen5();
					
					
					if (res!=0){
						if (res>0){
							wins++;
							winPips += pipsSL-comm;
							actualLosses = 0;
						}else if (res<0){
							losses++;
							lostPips += pipsSL+comm;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
						}
						acc+= res;
						
						count++;
					}
					
					
				}else if (influx<=-thr
						//&& maxMin<=-thr1
						&& q1.getClose5()-q1.getLow5()>=diffClose
						){
					
					int pipsSL = data.get(i-nAtr).getOpen5()-q.getOpen5();
					if (pipsSL<=200){
						pipsSL = 200;
					}
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, q.getOpen5(), q.getOpen5()-1*pipsSL, q.getOpen5()+1*pipsSL, false);
					
					int res =qm.getOpen5();
					if (res!=0){
						if (res>0){
							wins++;
							winPips += pipsSL-comm;
							actualLosses = 0;
						}else if (res<0){
							losses++;
							lostPips += pipsSL+comm;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
						}
						acc+= res;
						
						count++;
					}
				}
			}

		}
		//System.out.println(header);
		double per = wins*100.0/(wins+losses);
		double pf = winPips*1.0/lostPips;
		if (count>0 && debug==1)
		System.out.println(header+" "+
				nAtr+" "+PrintUtils.Print2dec(thr, false)+" "+maxBars+" "+diffClose
				+" || "
				+" "+count+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(per, false)
				+" "+PrintUtils.Print2dec((winPips-lostPips)*0.1/count, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+maxLosses
				);
		return pf;
	}
	
	public static void testInflux4(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int nAtr,
			int tp,
			int sl,
			int thr1,
			double thr,
			int maxBars,
			int diffClose,
			boolean isReverse,
			int debug
			) {
		
		double balanceInicial =10000;
		double balance =10000;
		double maxbalance = 10000;
		double maxdd = 0;
		double balanceNeed=0;
		double win$$=0;
		double lost$$=0;
		
		
		Calendar cal = Calendar.getInstance();
	
		int lastDay = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int thrValue = 0;
		int thrMode = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		int n= 200;
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		QuoteShort qm = new QuoteShort();
		int acc = 0;
		int count = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		for (int i=n;i<data.size();i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
															
				}						
				thrValue = 0;
				thrMode = 0;
				lastDay = day;				
			}
			
			
			int  maxMin = maxMins.get(i-1);
			
			int currentDiff = q.getOpen5()-q1.getOpen5();
			diffs.add(currentDiff);
			
			double influx = MathUtils.average(diffs,diffs.size()-nAtr, diffs.size()-1);
			
			if (h>=h1 && h<=h2){			
				if (influx>=thr
						&& maxMin>=thr1
						&& q1.getHigh5()-q1.getClose5()>=diffClose
						){
					
					int pipsSL = q.getOpen5()-data.get(i-nAtr).getOpen5();
					
					TradingUtils.getMaxMinShortEntryTPSLClose(data, qm, cal, i, data.size()-1, q.getOpen5(), q.getOpen5()+tp, data.get(i-nAtr).getOpen5(), false);
					
					int res = (qm.getClose5()-q.getOpen5());
					if (res>=0){
						wins++;
						winPips += res;
					}else{
						losses++;
						lostPips += -res;
					}
					acc+= res;
					
					count++;
					
					
				}else if (influx<=-thr
						&& maxMin<=-thr1
						&& q1.getClose5()-q1.getLow5()>=diffClose
						){
					
					int pipsSL = data.get(i-nAtr).getOpen5()-q.getOpen5();
					TradingUtils.getMaxMinShortEntryTPSLClose(data, qm, cal, i, data.size()-1, q.getOpen5(), q.getOpen5()-tp, data.get(i-nAtr).getOpen5(), false);
					
					int res = (q.getOpen5()-qm.getClose5());
					if (res>=0){
						wins++;
						winPips += res;
					}else{
						losses++;
						lostPips += -res;
					}
					acc+= res;
					
					count++;
				}
			}

		}
		//System.out.println(header);
		double per = wins*100.0/(wins+losses);
		double pf = winPips*1.0/lostPips;
		if (count>0)
		System.out.println(header+" "+
				nAtr+" "+PrintUtils.Print2dec(thr, false)+" "+maxBars+" "+diffClose
				+" || "
				+" "+count
				+" "+PrintUtils.Print2dec(per, false)
				+" "+PrintUtils.Print2dec(acc*0.1/count, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	
	private static double testZZ(String string, 
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins,
			ArrayList<Integer> zzs, 
			int y1, int y2, 
			int h1, int h2, 
			int tp, int sl, 
			int thr1,
			int filter,
			double aRange,
			int debug) {
		
		
		Calendar cal = Calendar.getInstance();
		Calendar cal4 = Calendar.getInstance();
		int lastDay = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int thrValue = 0;
		int thrMode = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		int n= 200;
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		QuoteShort qm = new QuoteShort();
		int acc = 0;
		int count = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int lastzz = -1;
		int actualzz = -1;
		int slValue = -1;
		int tpValue = -1;
		int entry = -1;
		
		int zzIdx4 = -1;
		int zzIdx3 = -1;
		int zzIdx2 = -1;
		int zzIdx1 = -1;
		int zzValue4 = -1;
		int zzValue3 = -1;
		int zzValue2 = -1;
		int zzValue1 = -1;
		int thr4 = -1;
		int h4 = -1;
		int pivotUp = -1;
		int pivotDown = -1;
		boolean isNew = true;
		int totalUpdate=0;
		int dayOpen = 0;
		int totalUpdates = 0;
		int lastEntryS = -1;
		int lastEntryL = -1;
		int minl = -1;
		int maxh = -1;
		ArrayList<Integer> zzsRealTimeIdx = new ArrayList<Integer>();
		
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		double avgRange = 600;
		int natr = 20;
		double factorR = 0.0;
		int totalDays = 0;
		int totalGreat = 0;
		int actualRange=0;
		int isDefined = 0;
		int definedValue = -1;
		int testCases=0;
		int accDiff = 0;
		int accDiffClose = 0;
		int wins2 = 0;
		int losses2 = 0;
		int accDiffWins = 0;
		int accDiffLosses = 0;
		int expectedValue = 0;
		for (int i=12;i<data.size()-4;i++) {
			QuoteShort q2 = data.get(i-2);
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {
				
				if (lastDay!=-1) {
					int range = maxh-minl;
					ranges.add(range);
					
					totalDays++;
					actualRange = maxh-minl;
					factorR = actualRange*100.0/avgRange;
					if (factorR<=80.0){
						totalGreat++;
					}
					
					if (isDefined!=0){
						if (isDefined==1){
							accDiff += maxh-definedValue;
							accDiffClose += q1.getClose5()-definedValue;	
							
							if (maxh>=expectedValue){//si llega al tp..
								accDiffWins += expectedValue-definedValue;
								wins2++;
							}else{ //si no llega al 
								if (q1.getClose5()-definedValue>=0){
									accDiffWins += q1.getClose5()-definedValue;
									wins2++;
								}else{
									accDiffLosses += -(q1.getClose5()-definedValue);
									losses2++;
								}																
							}
						}else if (isDefined==-1){
							accDiff += definedValue-minl;
							accDiffClose += definedValue-q1.getClose5();
							if (minl<=expectedValue){
								accDiffWins += definedValue-expectedValue;
								wins2++;
							}else{
								if (definedValue-q1.getClose5()>=0){
									accDiffWins += definedValue-q1.getClose5();
									wins2++;
								}else{
									accDiffLosses += -(definedValue-q1.getClose5());
									losses2++;
								}
							}
						}
						testCases++;
					}
										
					avgRange = MathUtils.average(ranges, ranges.size()-natr, ranges.size()-1);									
				}			
				dayOpen = q.getOpen5();
				pivotUp = -1;
				pivotDown = -1;
				thrValue = 0;
				thrMode = 0;
				totalUpdate=0;
				minl = -1;
				maxh = -1;
				lastDay = day;	
				isDefined=0;
				
				if (debug==2)
				System.out.println("*****[NEW DAY]***** "+DateUtils.datePrint(cal));
			}
			
			int  maxMin = maxMins.get(i);
			
			actualRange = maxh-minl;
		    factorR = actualRange*100.0/avgRange;
		    
		    
				if (maxh==-1 || q.getHigh5()>maxh) {
					maxh = q.getHigh5();
					actualRange = maxh-minl;
					factorR = actualRange*100.0/avgRange;
					if (debug==2)
					System.out.println("[HIGH] "+maxh+" "+minl
							+" ||"+" "+PrintUtils.Print2dec(factorR, false)
							+" ||| "+(maxh-minl)+" "+PrintUtils.Print2dec(avgRange, false)
							+" || "+q.toString());
					
					if (h>=h1 && h<=h2
							&& isDefined==0
							//&& factorR>=20.0
							&& maxMin>=thr1
							&& factorR>=aRange
							&& minl!=-1 && maxh!=-1
							){
						//definedValue = q.getHigh5();
						definedValue = q.getClose5();
						expectedValue = q.getHigh5()+filter;
						isDefined = 1;
					}
				}
				
				if (minl==-1 || q.getLow5()<minl) {
					minl = q.getLow5();
					
					actualRange = maxh-minl;
					factorR = actualRange*100.0/avgRange;
					if (debug==2)
					System.out.println("[LOW] "+maxh+" "+minl
							+" ||"+" "+PrintUtils.Print2dec(factorR, false)
							+" ||| "+(maxh-minl)+" "+PrintUtils.Print2dec(avgRange, false)
							+" || "+q.toString());
					
					if (h>=h1 && h<=h2
							&& isDefined==0
							//&& factorR>=20.0
							&& maxMin<=-thr1
							&& factorR>=aRange
							&& minl!=-1 && maxh!=-1
							){
						//definedValue = q.getLow5();
						definedValue = q.getClose5();
						expectedValue = q.getLow5()-filter;
						isDefined = -1;
					}
				}

			
			
			
			int range = maxh-minl;
			//se actualiza
			
			actualzz = zzs.get(i);
			if (actualzz!=lastzz 
					//&& (actualzz/lastzz)<0
					){	
				if (zzsRealTimeIdx.size()==0){
					zzsRealTimeIdx.add(actualzz);
				}else{
					if ((actualzz*1.0/lastzz)>=0){
						zzsRealTimeIdx.set(zzsRealTimeIdx.size()-1, i);//se actualiza
						//System.out.println(DateUtils.datePrint(cal)+" ACTUALIZA: "+actualzz);
						isNew = false;
						if (actualzz>=0)
							totalUpdate += actualzz-lastzz;
						else
							totalUpdate += Math.abs(lastzz)-Math.abs(actualzz);
						totalUpdates++;
					}else{//cambio de signo, es nuevo
						zzsRealTimeIdx.add(i);
						isNew = true;
						totalUpdate = 0;
						totalUpdates=0;
						lastEntryS = -1;
						lastEntryL = -1;
						//System.out.println(DateUtils.datePrint(cal)+" NUEVO: "+actualzz);
					}
				}
				//System.out.println(DateUtils.datePrint(cal)+" actualzz: "+actualzz);
				lastzz = actualzz;
			}
			
			//se toman las decisiones el close
			
			//miramos los ultimos 4 semaforos
			if (zzsRealTimeIdx.size()>=10){
				zzIdx4 = zzsRealTimeIdx.get(zzsRealTimeIdx.size()-4);
				zzIdx3 = zzsRealTimeIdx.get(zzsRealTimeIdx.size()-3);
				zzIdx2 = zzsRealTimeIdx.get(zzsRealTimeIdx.size()-2);
				zzIdx1 = zzsRealTimeIdx.get(zzsRealTimeIdx.size()-1);
				
				//System.out.println(zzIdx4);
				zzValue4 = zzs.get(zzIdx4);
				zzValue3 = zzs.get(zzIdx3);
				zzValue2 = zzs.get(zzIdx2);
				zzValue1 = zzs.get(zzIdx1);
				
				
				QuoteShort.getCalendar(cal4, data.get(zzIdx4));
				h4 = cal4.get(Calendar.HOUR_OF_DAY);
				
				thr4 = maxMins.get(zzIdx4);
				
	
				if (h>=h1 && h<=h2
						&& range<=filter
						){
					if (true
							&& maxMins.get(zzIdx1)>=thr1 
							//q.getClose5()>=pivotUp && pivotUp!=-1
							//&& thr4>=thr1
							//&& zzValue1>=pivotUp
							){
						if (zzValue3>0){
							if (zzValue2<0){
								if (zzValue1>zzValue3
										//&& zzValue1-q.getClose5()>=100
										//&& zzValue1-q.getClose5()>=150
										//&& q.getClose5()<=q.getOpen5()
										//&& (q.getClose5()>=lastEntryL || lastEntryL==-1)
											//&& q.getClose5()<=pivotUp
											//&& q.getLow5()<=pivotUp
											//&& q1.getLow5()<=pivotUp
										&& (isNew)
										//&& zzIdx1==i
											){
										if (debug==1)
										System.out.println("[CANDIDATO PARA LONG]" 
											+" "+maxMins.get(zzIdx1)+" || "+zzValue3+" "+zzValue2+" "+zzValue1
											+" || "+q.toString()
											);
										
										entry = q.getClose5();
										int valueTP = entry+tp;
										int valueSL = entry -sl;
										lastEntryL = entry;
										TradingUtils.getMaxMinShortEntryTPSLClose(data, qm, cal4, i+1, data.size()-1,
													entry, valueTP, valueSL, false);
										
										int pips = qm.getClose5()-q.getClose5();
										
										if (pips>=0) {
											wins++;
											winPips += pips;
										}else{
											losses++;
											lostPips +=-pips;
										}
										
										acc += qm.getClose5()-q.getClose5();
										count++;
									
								}
							}
						}									
					}else if (true
							&& maxMins.get(zzIdx1)<=-thr1 
							//&& -zzValue1<=pivotDown 
							//&& q.getClose5()<=pivotDown
							//&& thr4<=-thr1
							//&& pivotUp!=-1
							
							){
						if (zzValue3<0){
							if (zzValue2>0){
								if (-zzValue1<-zzValue3
										//&& q.getClose5()-(-zzValue1)>=100
										//&& (isNew)
										//&& (q.getClose5()<=lastEntryS || lastEntryS==-1)
										//&& zzIdx1==i
										//&& q.getClose5()>=q.getOpen5()
										&& isNew
											//&& q.getClose5()>=pivotDown
											//&& q.getHigh5()>=pivotDown
											//&& q1.getClose5()>=pivotDown
											//&& q.getClose5()<=thrvalue
										//&& q.getClose5()>=q.getOpen5()
											){
										if (debug==1)
										System.out.println("[CANDIDATO PARA SHORT]" 
												+" || "+zzValue3+" "+zzValue2+" "+zzValue1
											+" || "+q.toString()
											);
										entry = q.getClose5();
										int valueTP = entry-tp;
										int valueSL = entry+sl;
										lastEntryS = entry;
										TradingUtils.getMaxMinShortEntryTPSLClose(data, qm, cal4, i+1, data.size()-1,
													entry, valueTP, valueSL, false);
										
										int pips = q.getClose5()-qm.getClose5();
										
										if (pips>=0) {
											wins++;
											winPips += pips;
										}else{
											losses++;
											lostPips +=-pips;
										}
										
										acc += q.getClose5()-qm.getClose5();
										count++;
								}
							}
						}//zzvalue3		*/
					}
				}
			}
		}
		
		int total = wins+losses;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/count;
		double perGreat = totalGreat*100.0/totalDays;
		
		double avgLoss2 = accDiffLosses*0.1/losses2; 
		pf = accDiffWins*1.0/accDiffLosses;
		System.out.println(thr1+" "+sl+" "+filter+" "+h1+" "+h2
				+" || "
				//+count
				//+" "+PrintUtils.Print2dec(winPer, false)
				//+" "+PrintUtils.Print2dec(pf, false)
				//+" "+PrintUtils.Print2dec(avg, false)
				+" || "+totalDays
				+" "+PrintUtils.Print2dec(perGreat, false)
				+" || "+testCases
				+" "+PrintUtils.Print2dec(accDiff*0.1/testCases, false)
				+" "+PrintUtils.Print2dec(accDiffClose*0.1/testCases, false)
				+" || "
				+" "+PrintUtils.Print2dec(wins2*100.0/testCases, false)
				+" "+PrintUtils.Print2dec(accDiffWins*0.1/wins2, false)
				+" || "
				+" "+PrintUtils.Print2dec(losses2*100.0/testCases, false)
				+" "+PrintUtils.Print2dec(accDiffLosses*0.1/losses2, false)
				+" || "
				+" "+testCases+" "+PrintUtils.Print2dec(pf, false)
			);
		
		return 0;
	}
	
	private static void testZZ2(String string, 
			ArrayList<QuoteShort> data, 
			ArrayList<Integer> maxMins,
			ArrayList<Integer> zzs, 
			int y1, int y2, 
			int h1, int h2, 
			int minDiff,
			int maxBars,
			int debug) {
		
		
		Calendar cal = Calendar.getInstance();
		Calendar cal4 = Calendar.getInstance();
		int lastDay = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int thrValue = 0;
		int thrMode = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		int n= 200;
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		QuoteShort qm = new QuoteShort();
		int acc = 0;
		int count = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int lastzz = -1;
		int actualzz = -1;
		int slValue = -1;
		int tpValue = -1;
		int entry = -1;
		
		int zzIdx4 = -1;
		int zzIdx3 = -1;
		int zzIdx2 = -1;
		int zzIdx1 = -1;
		int zzValue4 = -1;
		int zzValue3 = -1;
		int zzValue2 = -1;
		int zzValue1 = -1;
		int thr4 = -1;
		int h4 = -1;
		int pivotUp = -1;
		int pivotDown = -1;
		boolean isNew = true;
		int totalUpdate=0;
		int dayOpen = 0;
		int totalUpdates = 0;
		int lastEntryS = -1;
		int lastEntryL = -1;
		int minl = -1;
		int maxh = -1;
		ArrayList<Integer> zzsRealTimeIdx = new ArrayList<Integer>();
		
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		double avgRange = 600;
		int natr = 20;
		double factorR = 0.0;
		int totalDays = 0;
		int totalGreat = 0;
		int actualRange=0;
		int isDefined = 0;
		int definedValue = -1;
		int testCases=0;
		int accDiff = 0;
		int accDiffClose = 0;
		int wins2 = 0;
		int losses2 = 0;
		int accDiffWins = 0;
		int accDiffLosses = 0;
		int expectedValue = 0;
		int value10 = 0;
		int value9=0;
		int mode = 0;
		for (int i=60;i<data.size()-maxBars;i++) {
			QuoteShort q2 = data.get(i-2);
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay) {			
				/*if (mode!=0){					
					if (mode==1){
						int d= q.getOpen5()-value10;
						if (d>=0){
							accDiffWins+=d;
							wins2++;
						}else{
							accDiffLosses+=-d;
							losses2++;
						}
					}else if (mode==-1){
						int d= value10-q.getOpen5();
						if (d>=0){
							accDiffWins+=d;
							wins2++;
						}else{
							accDiffLosses+=-d;
							losses2++;
						}
					}					
					testCases++;
				}	*/			
				mode = 0;
				lastDay = day;
			}
			
			int diff12 = data.get(i).getOpen5()-data.get(i-12).getOpen5();
			int diff24 = data.get(i).getOpen5()-data.get(i-24).getOpen5();
			int diff36 = data.get(i).getOpen5()-data.get(i-36).getOpen5();
			int diff48 = data.get(i).getOpen5()-data.get(i-48).getOpen5();
			
			int sum = diff12+diff24+diff36+diff48;
			
			if (h==5 && min==0){
				value9 = q.getOpen5();
			}
			
			if (h>=h1 && h<=h2 
					//&& min==0
					){
				value10 = q.getOpen5();
				
				int diffLondon = value10-value9;
				
				if (sum>=minDiff){
					mode=1;
					
					int d= data.get(i).getOpen5()-data.get(i+maxBars).getOpen5();
					if (d>=0){
						accDiffWins+= d;
						wins2++;
						//System.out.println(d);
					}else{
						accDiffLosses+= -d;
						losses2++;
					}
					testCases++;
				}else if (sum<=-minDiff){
					mode=-1;
					
					int d= data.get(i+maxBars).getOpen5()-data.get(i).getOpen5();
					if (d>=0){
						accDiffWins+= d;
						wins2++;
						//System.out.println(d+" || "+data.get(i+maxBars).toString()+" || "+);
					}else{
						accDiffLosses+= -d;
						losses2++;
					}
					testCases++;
				}
			}
			
			
		}
		
		
		

		double pf = accDiffWins*1.0/accDiffLosses;
		//if (testCases>=0 && pf>=1.3)
		System.out.println(
				minDiff+" "+maxBars
				+" || "
				+" "+testCases
				+" "+PrintUtils.Print2dec(wins2*100.0/testCases, false)
				+" "+PrintUtils.Print2dec(accDiffWins*1.0/accDiffLosses, false)
				+" "+PrintUtils.Print2dec((accDiffWins-accDiffLosses)*0.1/testCases, false)
				);
		
	}
	
	public static void main(String[] args) throws Exception {
		
		//String path0 ="C:\\Users\\David\\Documents\\fxdata\\";
				String path0 ="C:\\fxdata\\";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
				
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
				//String pathEURUSD = path0+"eurusd_UTC_15 Mins_Bid_2003.12.31_2017.11.25.csv";
				String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.12.04.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2003.05.04_2017.11.24.csv";
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
				//String pathEURUSD = path0+"eurusd_UTC_1 Min_Bid_2013.12.31_2017.11.22.csv";
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
				
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.12.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
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
				FFNewsClass.readNews(pathNews,news,0);
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
					
					ArrayList<Integer> zzs = new ArrayList<Integer>(); 
					TradingUtils.calculateTrendsHLDay(data, 200, zzs,0);
					
					System.out.println(data.size()+" "+zzs.size());
				
					
						for (int h1=0;h1<=0;h1++){
							int h2 = h1+9;
							for (int nbars=1;nbars<=1;nbars+=1){
								for (double thrInflux=10;thrInflux<=10;thrInflux+=10){
									for (int maxBars=48;maxBars<=48;maxBars+=12
											){
										for (int thr1=0;thr1<=0;thr1+=100){
											for (int tp=400;tp<=400;tp+=10){
												for (int sl=200;sl<=200;sl+=50){
													for (int diffClose=0;diffClose<=0;diffClose+=10){
														int count = 0;
														for (double aRange=30.0;aRange<=30.0;aRange+=10.0){
															for (int filter =2900;filter<=2900;filter+=100) {
																for (int y1=2004;y1<=2017;y1++){
																	int y2 = y1+0;
																	
																	//double pf =TestInfluxIndicator.testInflux3(""+h1+" "+h2, data, maxMins, y1,y2, h1, h2, nbars, tp, sl,
																			//thr1, thrInflux, maxBars,diffClose, false, 0);
																	
																	/*double pf =TestInfluxIndicator.testZZ(""+h1+" "+h2, 
																			data, maxMins,zzs, y1,y2, h1, h2, tp, sl,
																			thr1,filter,aRange,
																			0);*/
																	
																	TestInfluxIndicator.testZZ2(""+h1+" "+h2, 
																			data, maxMins,zzs, y1,y2, h1, h2,
																			filter,maxBars,
																			0);
																	
																	//if (pf>=1.0){
																		//count++;
																	//}
																}
																/*if (count>=12){
																	String header = nbars
																			+" "+PrintUtils.Print2dec(thrInflux, false)
																			+" "+PrintUtils.Print2dec(diffClose, false)
																			+" ||| "+count;
																			TestInfluxIndicator.testInflux3(header, data, maxMins, 2004,2017, h1, h2, nbars, tp, sl,
																					thr1, thrInflux, maxBars,diffClose, false, 1);
																}*/
															}//filter
														}
													}//difclose
												}//sl
											}//tp
										}//thr1
									}//maxbars															
								}							
							}//nbars
						}	
														
				}

	}


	

}
