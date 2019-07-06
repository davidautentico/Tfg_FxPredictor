package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class DailyBreak {
	
	/**
	 * Calcula el maximo break con un primer fallo y su max retrace antes de alcanzar ese maximo
	 * @param data
	 * @param y1
	 * @param y2
	 * @param h1
	 * @param h2
	 * @param tp
	 */
	public static void doTrade2Adverse(ArrayList<QuoteShort> data,int y1,int y2,int h1,int h2,int tp){
	
		CoreStats firstBreak = new CoreStats(); //estadisticas del primer break
		CoreStats firstBreak2 = new CoreStats();//estadisticas del primer break si hubo tambien un segundo
		CoreStats secondBreak2 = new CoreStats();//estadisticas del segundo break
		ArrayList<PositionCore> periodPositions = new ArrayList<PositionCore>();
		Calendar cal = Calendar.getInstance();
		int h= -1;
		int lastTime = -1;
		int actualMax = -1;
		int actualMin = -1;
		int lastMax = -1;
		int lastMin = -1;
		int numTrades = 0;
		int max = -1;
		int min = -1;
		int maxAdverse = -1;
		int maxAdverseBP = -1;
		int mode = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			//int day = cal.get(Calendar.DAY_OF_YEAR);
			int minute = cal.get(Calendar.MINUTE);
			int ho = cal.get(Calendar.HOUR_OF_DAY);
			h = ho;//1h breaK
			//if (ho%2==0) h = ho;//2h break
			//if (ho==0 || ho==4 || ho==8 || ho==12 || ho==16 || ho==20) h = ho;//4h break
			//if (ho==0 || ho==8 || ho==16) h = ho;//8h break
			//if (minute==0 || minute==30) h = minute;//30m break
			//if (minute==0 || minute==15 || minute==30 || minute==45) h = minute;//15m break
			
			if (h!=lastTime){
				if (periodPositions.size()>0){
					PositionCore lastPosition = periodPositions.get(periodPositions.size()-1);
					if (lastPosition.getPositionStatus()==PositionStatus.OPEN){
						if (lastPosition.getPositionType()==PositionType.LONG){
							lastPosition.maxProfit = max-lastMax;
							lastPosition.maxAdverseBeforeProfit = lastMax-maxAdverseBP;
							lastPosition.positionStatus = PositionStatus.CLOSE;
						}else if (lastPosition.getPositionType()==PositionType.SHORT){
							lastPosition.maxProfit = lastMin-min;
							lastPosition.maxAdverseBeforeProfit = maxAdverseBP-lastMin;
							lastPosition.positionStatus = PositionStatus.CLOSE;
						}
					}
					if (periodPositions.size()==1){
						firstBreak.addPositionStats(periodPositions.get(0));
					}else if (periodPositions.size()>1){//solo hasta la 2
						firstBreak.addPositionStats(periodPositions.get(0));
						firstBreak2.addPositionStats(periodPositions.get(0));
						secondBreak2.addPositionStats(periodPositions.get(1));
					}
				}
				
				periodPositions.clear();
				lastMax = actualMax;
				lastMin = actualMin;
				actualMax = -1;
				actualMin = -1;
				numTrades = 0;	
				mode = 0;
				max = -1;
				min = -1;
				
				lastTime = h;	
				//System.out.println("[NUEVO PERIODO]");
			}
			
			if (lastMax!=-1 && lastMin!=-1){			
				if (mode==0){
					if (q.getHigh5()>=lastMax){						
						max = q.getHigh5();
						maxAdverse = q.getLow5();
						maxAdverseBP = q.getLow5();
						
						PositionCore pos = new PositionCore();
						pos.setEntry(lastMax);
						pos.setPositionType(PositionType.LONG);
						pos.positionStatus = PositionStatus.OPEN;
						periodPositions.add(pos);
						//System.out.println("[LONG NUEVA] "+max+" "+lastMax+" "+q.getHigh5());
						mode = 1;
					}else if (q.getLow5()<=lastMin){						
						min = q.getLow5();
						maxAdverse = q.getHigh5();
						maxAdverseBP = q.getHigh5();
						
						PositionCore pos = new PositionCore();
						pos.setEntry(lastMin);
						pos.setPositionType(PositionType.SHORT);
						pos.positionStatus = PositionStatus.OPEN;
						periodPositions.add(pos);
						
						mode = -1;
					}
				}else if (mode==1){
					if (q.getLow5()<=maxAdverse){
						maxAdverse = q.getLow5();
					}
					if (q.getHigh5()>=max){
						max = q.getHigh5();
						maxAdverseBP = maxAdverse; //actualizamos el maximo antes de beneficio
						//System.out.println("[LONG UPDATE] "+max+" "+lastMax+" "+q.getHigh5());
					}else if (q.getLow5()<=lastMin){
						//actualizamos anterior posicion
						periodPositions.get(periodPositions.size()-1).maxProfit = max-lastMax;
						periodPositions.get(periodPositions.size()-1).maxAdverseBeforeProfit = lastMax-maxAdverseBP;
						periodPositions.get(periodPositions.size()-1).positionStatus = PositionStatus.CLOSE;
						//System.out.println("[LONG CLOSE] "+max+" "+lastMax+" "+periodPositions.get(periodPositions.size()-1).maxProfit);
						//nueva posicion corta
						min = q.getLow5();
						maxAdverse = q.getHigh5();
						maxAdverseBP =  q.getHigh5();
						PositionCore pos = new PositionCore();
						pos.setEntry(lastMin);
						pos.setPositionType(PositionType.SHORT);
						pos.positionStatus = PositionStatus.OPEN;
						periodPositions.add(pos);
						mode = -1;
					}
				}else if (mode==-1){
					if (q.getHigh5()>=maxAdverse){
							maxAdverse = q.getHigh5();
					}
					if (q.getLow5()<=min){
						min = q.getLow5();
						maxAdverseBP = maxAdverse;
					}else if (q.getHigh5()>=lastMax){	
						//actualizamos anterior posicion
						periodPositions.get(periodPositions.size()-1).maxProfit = lastMin-min;
						periodPositions.get(periodPositions.size()-1).maxAdverseBeforeProfit = maxAdverseBP-lastMin;
						periodPositions.get(periodPositions.size()-1).positionStatus = PositionStatus.CLOSE;
						
						//nueva posicion larga
						max = q.getHigh5();
						maxAdverse = q.getLow5();
						maxAdverseBP = q.getLow5();
						PositionCore pos = new PositionCore();
						pos.setEntry(lastMax);
						pos.setPositionType(PositionType.LONG);
						pos.positionStatus = PositionStatus.OPEN;
						periodPositions.add(pos);
						//System.out.println("[LONG NUEVA] "+max+" "+lastMax+" "+q.getHigh5());
						mode = 1;
					}
				}
			}
			
			if (q.getHigh5()>=actualMax || actualMax==-1) actualMax = q.getHigh5();
			if (q.getLow5()<=actualMin || actualMin==-1) actualMin = q.getLow5();
		}
		
		System.out.println(
				firstBreak.toString()
				+" || "+firstBreak2.toString()
				+" || "+secondBreak2.toString()
				);
		
	}
	
	public static void doTrade2(ArrayList<QuoteShort> data,int y1,int y2,int h1,int h2,int tp){
		
		
		int wins = 0;
		int losses = 0;
		int totaltp0 = 0;
		int totaltp1 = 0;
		int totaltp2 = 0;
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		ArrayList<Integer> totals = new ArrayList<Integer>();
		for (int i=0;i<=1000;i++){
			diffs.add(0);
			totals.add(0);
		}
		int lastHour = -1;
		int lastDay = -1;
		int lastMax = -1;
		int lastMin = -1;
		int actualMax = -1;
		int actualMin = -1;
		int max = -1;
		int min = -1;
		boolean highTriggered = false;
		boolean lowTriggered = false;
		int dayTrades = 0;
		int diff1 = 0;
		int diff2 = 0;
		Calendar cal = Calendar.getInstance();
		int h= -1;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			//int day = cal.get(Calendar.DAY_OF_YEAR);
			int minute = cal.get(Calendar.MINUTE);
			int ho = cal.get(Calendar.HOUR_OF_DAY);
			//h = ho;//1h breaK
			if (ho%2==0) h = ho;//2h break
			//if (ho==0 || ho==4 || ho==8 || ho==12 || ho==16 || ho==20) h = ho;//4h break
			//if (ho==0 || ho==8 || ho==16) h = ho;//8h break
			//if (minute==0 || minute==30) h = minute;//30m break
			//if (minute==0 || minute==15 || minute==30 || minute==45) h = minute;//15m break
			
			if (h!=lastHour){	
				if (dayTrades==1){
					if (lowTriggered){
						diff2 = lastMin-min;						
						min = -1;
						lowTriggered = false;
						dayTrades++;
						//System.out.println("[low triggered] "+diff2);
					}	
					if (highTriggered){
						diff2 = max-lastMax;						
						max = -1;
						highTriggered = false;
						dayTrades++;
						//System.out.println("[high triggered] "+diff2);
					}
					
					if (diff1>=tp*10){
						totaltp0++;
					}
				}
				
				if (dayTrades>=2){
					diffs.set(0, diffs.get(0)+diff1);
					totals.set(0, totals.get(0)+1);
					diffs.set(1, diffs.get(1)+diff2);
					totals.set(1, totals.get(1)+1);
					
					if (diff1>=tp*10){
						totaltp1++;
					}
					if (diff2>=tp*10){
						totaltp2++;
					}
					
					if (diff2>diff1){
						wins++;
					}else{
						losses++;
					}
				}
				
				lastMax = actualMax;
				lastMin = actualMin;				
				actualMax = -1;
				actualMin = -1;
				dayTrades = 0;
				max = -1;
				min = -1;
				diff1 = 0;
				diff2 = 0;
				highTriggered = false;
				lowTriggered = false;
				lastHour= h;
				
				//System.out.println("[nuevo dia] "+DateUtils.datePrint(cal));
			}
			
			if (lastMax!=-1 && lastMin!=-1 && dayTrades<=1){
				if (q.getHigh5()>=lastMax){				
					if (lowTriggered){//el primer golpe
						if (dayTrades==0)
							diff1 = lastMin-min;
						if (dayTrades==1)
							diff2 = lastMin-min;
						min = -1;
						lowTriggered = false;
						dayTrades++;
						//System.out.println("[low triggered] "+diff1+" "+diff2);
					}	
					if (q.getHigh5()>=max || max==-1)
						max = q.getHigh5();										
					highTriggered = true;
				}
				
				if (q.getLow5()<=lastMin){
					if (highTriggered){
						if (dayTrades==0)
							diff1 = max-lastMax;
						if (dayTrades==1)
							diff2 = max-lastMax;
						max = -1;
						highTriggered = false;
						dayTrades++;
						//System.out.println("[high triggered] "+diff1+" "+diff2);
					}
					if (q.getLow5()<=min || min==-1)
						min = q.getLow5();			
					lowTriggered = true;
				}
			}
			
			
			if (q.getHigh5()>=actualMax || actualMax==-1) actualMax = q.getHigh5();
			if (q.getLow5()<=actualMin || actualMin==-1) actualMin = q.getLow5();
		}
		
		String diffStr="";
		for (int i=0;i<=1;i++){
			double value = diffs.get(i)*0.1/totals.get(i);
			diffStr+=" "+PrintUtils.Print2dec(value, false)+" ("+totals.get(i)+")"; 
		}
		
		double tpPer0 = totaltp0*100.0/totals.get(1);
		double tpPer1 = totaltp1*100.0/totals.get(1);
		double tpPer2 = totaltp2*100.0/totals.get(1);
		
		double winPer = wins*100.0/(wins+losses);
		System.out.println(
				tp
				+" || "+diffStr
				+" || "+totals.get(1)
				+" "+PrintUtils.Print2dec(tpPer0,false)
				+" "+PrintUtils.Print2dec(tpPer1,false)
				+" "+PrintUtils.Print2dec(tpPer2,false)
				+" || "+PrintUtils.Print2dec(winPer,false)
				);
		
	}
	
	
	public static void doTrade(ArrayList<QuoteShort> data,int y1,int y2,int h1,int h2,int tp){
		
		
		int wins = 0;
		int losses = 0;
		int totaltp0 = 0;
		int totaltp1 = 0;
		int totaltp2 = 0;
		ArrayList<Integer> diffs = new ArrayList<Integer>();
		ArrayList<Integer> totals = new ArrayList<Integer>();
		for (int i=0;i<=1000;i++){
			diffs.add(0);
			totals.add(0);
		}
		int lastDay = -1;
		int lastMax = -1;
		int lastMin = -1;
		int actualMax = -1;
		int actualMin = -1;
		int max = -1;
		int min = -1;
		boolean highTriggered = false;
		boolean lowTriggered = false;
		int dayTrades = 0;
		int diff1 = 0;
		int diff2 = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){	
				if (dayTrades==1){
					if (lowTriggered){
						diff2 = lastMin-min;						
						min = -1;
						lowTriggered = false;
						dayTrades++;
						//System.out.println("[low triggered] "+diff2);
					}	
					if (highTriggered){
						diff2 = max-lastMax;						
						max = -1;
						highTriggered = false;
						dayTrades++;
						//System.out.println("[high triggered] "+diff2);
					}
					
					if (diff1>=tp*10){
						totaltp0++;
					}
				}
				
				if (dayTrades>=2){
					diffs.set(0, diffs.get(0)+diff1);
					totals.set(0, totals.get(0)+1);
					diffs.set(1, diffs.get(1)+diff2);
					totals.set(1, totals.get(1)+1);
					
					if (diff1>=tp*10){
						totaltp1++;
					}
					if (diff2>=tp*10){
						totaltp2++;
					}
					
					if (diff2>diff1){
						wins++;
					}else{
						losses++;
					}
				}
				
				lastMax = actualMax;
				lastMin = actualMin;				
				actualMax = -1;
				actualMin = -1;
				dayTrades = 0;
				max = -1;
				min = -1;
				diff1 = 0;
				diff2 = 0;
				highTriggered = false;
				lowTriggered = false;
				lastDay = day;
				
				//System.out.println("[nuevo dia] "+DateUtils.datePrint(cal));
			}
			
			if (lastMax!=-1 && lastMin!=-1 && dayTrades<=1){
				if (q.getHigh5()>=lastMax){				
					if (lowTriggered){//el primer golpe
						if (dayTrades==0)
							diff1 = lastMin-min;
						if (dayTrades==1)
							diff2 = lastMin-min;
						min = -1;
						lowTriggered = false;
						dayTrades++;
						//System.out.println("[low triggered] "+diff1+" "+diff2);
					}	
					if (q.getHigh5()>=max || max==-1)
						max = q.getHigh5();										
					highTriggered = true;
				}
				
				if (q.getLow5()<=lastMin){
					if (highTriggered){
						if (dayTrades==0)
							diff1 = max-lastMax;
						if (dayTrades==1)
							diff2 = max-lastMax;
						max = -1;
						highTriggered = false;
						dayTrades++;
						//System.out.println("[high triggered] "+diff1+" "+diff2);
					}
					if (q.getLow5()<=min || min==-1)
						min = q.getLow5();			
					lowTriggered = true;
				}
			}
			
			
			if (q.getHigh5()>=actualMax || actualMax==-1) actualMax = q.getHigh5();
			if (q.getLow5()<=actualMin || actualMin==-1) actualMin = q.getLow5();
		}
		
		String diffStr="";
		for (int i=0;i<=1;i++){
			double value = diffs.get(i)*0.1/totals.get(i);
			diffStr+=" "+PrintUtils.Print2dec(value, false)+" ("+totals.get(i)+")"; 
		}
		
		double tpPer0 = totaltp0*100.0/totals.get(1);
		double tpPer1 = totaltp1*100.0/totals.get(1);
		double tpPer2 = totaltp2*100.0/totals.get(1);
		
		double winPer = wins*100.0/(wins+losses);
		System.out.println(
				tp
				+" || "+diffStr
				+" || "+totals.get(1)
				+" "+PrintUtils.Print2dec(tpPer0,false)
				+" "+PrintUtils.Print2dec(tpPer1,false)
				+" "+PrintUtils.Print2dec(tpPer2,false)
				+" || "+PrintUtils.Print2dec(winPer,false)
				);
		
	}

	public static void main(String[] args) throws Exception {
String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.07.04.csv";
		
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
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			//SimpleContinuation
			for (int y1=2003;y1<=2003;y1+=1){
				int y2 = y1+0;
				for (int h1=0;h1<=0;h1++){
					int h2=h1+23;
					for (int thr=72;thr<=72;thr+=12){
						for (int nbars = 36;nbars<=36;nbars+=12){
							for (int tp=5;tp<=5;tp++){
								//DailyBreak.doTrade(data, y1, y2, h1, h2, tp);
								//DailyBreak.doTrade2(data, y1, y2, h1, h2, tp);
								DailyBreak.doTrade2Adverse(data, y1, y2, h1, h2, tp);
							}
						}
					}
				}
			}
		
		}//limit


	}

}
