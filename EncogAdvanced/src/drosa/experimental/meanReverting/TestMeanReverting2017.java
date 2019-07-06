package drosa.experimental.meanReverting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.experimental.zznbrum.TestTrends;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMeanReverting2017 {
	
	
	public static void doTest2017(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int min1,int min2,
			int m1,int m2,
			int dayWeek1,int dayWeek2,
			int thr,
			int minPips,
			double tpFactor,
			double slFactor,
			int comm,
			int debug
			){
	
		int lastDay = -1;
		int lastDayPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		double maFast = -1;
		double maSlow = -1;
		double std0 = -1;
		ArrayList<Integer> days = new ArrayList<Integer>();
		
		int maxLosses = 0;
		int actualLosses = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int high = -1;
		int low = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		ranges.add(60);
		int tp = 150;
		int sl = 450;
		int accTp = 0;
		int accSl = 0;
		for (int i=2;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (min<min1 || min>min2) continue;
			//if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
			
			//days.add(q.getOpen5());
			
			if (day!=lastDay){
				//maFast = MathUtils.average(days, days.size()-14*288, days.size()-1);
				//maSlow = MathUtils.average(days, days.size()-255*288, days.size()-1);
				
				if (high!=-1){
					ranges.add(high-low);
				}
				
				int atr = (int) MathUtils.average(ranges, ranges.size()-5, ranges.size()-1);
				
				//System.out.println(atr+" "+(high-low));
				
				tp = (int) (atr*tpFactor);
				sl = (int) (atr*slFactor);
				
				if (tp<70){
					tp=70;
					sl= (int) (tp*slFactor/tpFactor);
				}
				
				high = -1;
				low = -1;
				lastDay = day;
			}
	
		
			int maxMin = maxMins.get(i-1);
			int maxMin1 = maxMins.get(i-2);
			
			//TradingUtils.getMaxMinShort(data, qm, calqm, i-500, i-1);
			//int hdiff = qm.getHigh5()-q1.getClose5();
			//int ldiff = q1.getClose5()-qm.getLow5();
			
			if (h1<=h && h<=h2
					&& (dayWeek>=dayWeek1  && dayWeek<=dayWeek2)
					&& (h>0 || (h==0 && min>=15))
					){
				
				int mode=0;
				if (maxMin>=thr
						//&& maFast<maSlow
						//&& ldiff>= minPips*10
						//&& maxMin1<thr
						){
					
					accTp += tp;
					accSl += sl;
					
					int valueTP = q.getOpen5()-tp;
					int valueSL = q.getOpen5()+sl;
					
					
					mode = -1;
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, q.getOpen5(), valueTP, valueSL, false);
				}else if (maxMin<=-thr
						//&& maFast>maSlow
						//&& hdiff>=minPips*10
						
						//&& maxMin1>-thr
						){
					
					accTp += tp;
					accSl += sl;
					
					int valueTP = q.getOpen5()+tp;
					int valueSL = q.getOpen5()-sl;
					mode=1;
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, q.getOpen5(), valueTP, valueSL, false);
				}
				
				if (mode!=0){
					int pips = qm.getClose5()-q.getOpen5()-comm;
					if (mode==-1){
						pips = q.getOpen5()-qm.getClose5()-comm;
					}
					
					if (pips>=0){
						wins++;
						winPips += pips;
						
						actualLosses = 0;
					}else{
						losses++;
						lostPips += -pips;
						
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}
				}
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
		
		}//for
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = winPips*1.0/lostPips;
		
		double maxDD = 60.0;
		double avgTp = accTp*0.1/total;
		double avgSl = accSl*0.1/total;
		
		double riskPerTrade = maxDD*1.0/(100*maxLosses);//en tanto por 1
		double profitPerTrade = avg*riskPerTrade/avgSl;//en tanto por 1
		
		int resMult = (int) (1*Math.pow(1+profitPerTrade,total));
		
		//if (pf>=1.5 && avg>=4.0)
		if (resMult>=20)
		System.out.println(
				h1+" "+h2
				+" "+PrintUtils.Print2dec(tpFactor, false) +" "+PrintUtils.Print2dec(slFactor, false)
				+" "+thr+" "+minPips
				+" || "
				+" "+PrintUtils.Print2Int(total, 6)+" "+PrintUtils.Print2Int(wins,6)+" "+PrintUtils.Print2Int(losses,6)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "
				+" "+PrintUtils.Print2dec(accTp*0.1/total, false)
				+" "+PrintUtils.Print2dec(accSl*0.1/total, false)
				+" || "+maxLosses+" || "+resMult
				);
		
	}
	
	public static void doTest2017$$(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int min1,int min2,
			int m1,int m2,
			int dayWeek1,int dayWeek2,
			int thr,
			int minPips,
			double tpFactor,
			double slFactor,
			double balance,
			int maxTrades,
			double maxRisk,
			int comm,
			int debug
			){
	
		
		double actualBalance = balance;
		double equitity = balance;		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int lastDay = -1;
		int lastDayPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		double maFast = -1;
		double maSlow = -1;
		double std0 = -1;
		ArrayList<Integer> days = new ArrayList<Integer>();
		
		int maxLosses = 0;
		int actualLosses = 0;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int high = -1;
		int low = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		ranges.add(60);
		int tp = 150;
		int sl = 450;
		int accTp = 0;
		int accSl = 0;
		for (int i=2;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			if (min<min1 || min>min2) continue;
			//if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
			
			//days.add(q.getOpen5());
			
			if (day!=lastDay){
				//maFast = MathUtils.average(days, days.size()-14*288, days.size()-1);
				//maSlow = MathUtils.average(days, days.size()-255*288, days.size()-1);				
				if (high!=-1){
					ranges.add(high-low);
				}
				
				int atr = (int) MathUtils.average(ranges, ranges.size()-5, ranges.size()-1);				
				//System.out.println(atr+" "+(high-low));				
				tp = (int) (atr*tpFactor);
				sl = (int) (atr*slFactor);				
				if (tp<70){
					tp=70;
					sl= (int) (tp*slFactor/tpFactor);
				}				
				high = -1;
				low = -1;
				lastDay = day;
			}
	
		
			int maxMin = maxMins.get(i-1);			
			int actualTrades = PositionShort.countTotal(positions, PositionStatus.OPEN);
			if (h1<=h && h<=h2
					&& (dayWeek>=dayWeek1  && dayWeek<=dayWeek2)
					&& (h>0 || (h==0 && min>=15))
					&& actualTrades<maxTrades
					){				
				int mode=0;
				double maxRisk$$ = actualBalance*maxRisk/100.0;
				int microLots = (int) (maxRisk$$/(sl*0.1*0.1));
				if (maxMin>=thr
						//&& maFast<maSlow
						//&& ldiff>= minPips*10
						//&& maxMin1<thr
						){
					
					accTp += tp;
					accSl += sl;
					int entry = q.getOpen5();
					int valueTP = q.getOpen5()-tp;
					int valueSL = q.getOpen5()+sl;
					
										
					PositionShort pos = new PositionShort();
					pos.setOpenCal(cal);
					pos.setEntry(entry);
					pos.setTp(valueTP);
					pos.setSl(valueSL);
					pos.setPositionType(PositionType.SHORT);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setMicroLots(microLots);
					positions.add(pos);
				}else if (maxMin<=-thr
						//&& maFast>maSlow
						//&& hdiff>=minPips*10
						
						//&& maxMin1>-thr
						){
					
					accTp += tp;
					accSl += sl;
					int entry = q.getOpen5();
					int valueTP = q.getOpen5()+tp;
					int valueSL = q.getOpen5()-sl;
					
					
					PositionShort pos = new PositionShort();
					pos.setOpenCal(cal);
					pos.setEntry(entry);
					pos.setTp(valueTP);
					pos.setSl(valueSL);
					pos.setPositionType(PositionType.LONG);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setMicroLots(microLots);
					positions.add(pos);
				}				
			}
			
			//evaluacion de posiciones
			int j = 0;
			while (j<positions.size()){
				PositionShort pos = positions.get(j);
				boolean isClosed = false;
				int pips = 0;
				if (pos.getPositionStatus()==PositionStatus.OPEN){					
					if (pos.getPositionType()==PositionType.LONG){
						if (q.getLow5()<=pos.getSl()){
							pips = pos.getSl()-pos.getEntry();
							isClosed = true;
						}else if (q.getHigh5()>=pos.getTp()){
							pips = pos.getTp()-pos.getEntry();
							isClosed = true;
						}else{
							int hOpen = pos.getOpenCal().get(Calendar.HOUR_OF_DAY);
							
							if (h>=10){
								if  (true
										&& maxMin<=-24
										&& q.getOpen5()<=pos.getEntry()){//la cosa va por debajo aun a las 10
									//pips = q.getOpen5()-pos.getEntry();
									//isClosed = true;
								}
							}
						}
					}else if (pos.getPositionType()==PositionType.SHORT){
						if (q.getHigh5()>=pos.getSl()){
							pips = pos.getEntry()-pos.getSl();
							isClosed = true;
						}else if (q.getLow5()<=pos.getTp()){
							pips = pos.getEntry()-pos.getTp();
							isClosed = true;
						}else{
							int hOpen = pos.getOpenCal().get(Calendar.HOUR_OF_DAY);
							
							if (h>=10){
								if  (true
										&& maxMin>=24
										&& q.getOpen5()>=pos.getEntry()){//la cosa va por encima aun a las 10
									//pips = pos.getEntry()-q.getOpen5();
									//isClosed = true;
								}
							}
						}
					}
				}
												
				if (isClosed){					
					pips -= comm;									
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}
					
					
					actualBalance += pos.getMicroLots()*0.1*(pips*0.1); //1fullpip/microlot = 0.1$
					
					if (debug==1							
							){
						System.out.println(
								PrintUtils.Print2dec(actualBalance, false)
								+" || "
								+" "+pos.getMicroLots()
								+" "+pips
								+" "+PrintUtils.Print2dec(pos.getMicroLots()*0.1*(pips*0.1), false)
								);
					}
					
					positions.remove(j);
				}else{
					j++;
				}
			}
			
			
			//high lows
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
		
		}//for
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = winPips*1.0/lostPips;
		
		double maxDD = 60.0;
		double avgTp = accTp*0.1/total;
		double avgSl = accSl*0.1/total;
		
		double riskPerTrade = maxDD*1.0/(100*maxLosses);//en tanto por 1
		double profitPerTrade = avg*riskPerTrade/avgSl;//en tanto por 1
		
		//int resMult = (int) (1*Math.pow(1+profitPerTrade,total));
		double resMult = actualBalance/balance;
		
		
		//if (pf>=1.5 && avg>=4.0)
		if (resMult>=-900)
		System.out.println(
				h1+" "+h2
				+" "+PrintUtils.Print2dec(tpFactor, false) +" "+PrintUtils.Print2dec(slFactor, false)
				+" "+PrintUtils.Print2dec(maxRisk, false)
				+" "+thr+" "+minPips
				+" || "
				+" "+PrintUtils.Print2Int(total, 6)+" "+PrintUtils.Print2Int(wins,6)+" "+PrintUtils.Print2Int(losses,6)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" || "
				+" "+PrintUtils.Print2dec(accTp*0.1/total, false)
				+" "+PrintUtils.Print2dec(accSl*0.1/total, false)
				+" || "+maxLosses
				+" || "+PrintUtils.Print2dec(actualBalance, false)
				+" || "+" "+PrintUtils.Print2dec(resMult, false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		String pathEUR ="C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.04.11.csv";
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEUR);
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
			if (path.contains("UTC")){
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);
			}else{
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);
			}
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		
			double balance = 10000;
			
			for (int h1=0;h1<=23;h1++){
				int h2 = h1+0;
				for (int min1=0;min1<=0;min1+=5){
					int min2 = min1+55;
					for (int thr=0;thr<=500;thr+=500){
						for (double tp=0.05;tp<=0.05;tp+=0.01){
							for (double sl=0.30;sl<=0.30;sl+=tp){
								for (int maxTrades = 1;maxTrades<=1;maxTrades++){
									for (double maxRisk=40.0;maxRisk<=40.0;maxRisk+=5.0){
										for (int minPips=0;minPips<=0;minPips+=10){
											for (int dayWeek1=Calendar.MONDAY;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
												int dayWeek2 = dayWeek1+4;
												for (int y1=2003;y1<=2003;y1++){
													int y2 = y1+14;									
													/*TestMeanReverting2017.doTest2017("", data, maxMins, y1, y2,
															h1, h2, min1, min2,
															0,11,
															dayWeek1, dayWeek2, thr, minPips, tp, sl,20, 0);*/
													TestMeanReverting2017.doTest2017$$("", data, maxMins, y1, y2,
															h1, h2, min1, min2,
															0,11,
															dayWeek1, dayWeek2, thr, minPips, tp, sl,
															balance,maxTrades,maxRisk,20, 0);
												}//y
											}
										}//minpips
									}//maxRisk
								}
							}
						}//tp
					}//thr
				}//min
			}
		}

	}

}
