package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudySpeed {

	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			double minSpeed1,double minSpeed2,
			int lbars,int nbars,
			int tp,int sl,
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
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		for (int i=lbars+100;i<data.size()-1;i++){
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
			
			days.add(q.getOpen5());
			
			if (day!=lastDay){
				if (isTrade>0) countDays++;
				isTrade=0;
				lastDay = day;
				totalDays++;
			}
			
			int accDiff = 0;
			int totalDiffs = 0;
			int ups = 0;
			int downs=0;
			for (int j=i-lbars;j<=i-1;j++){
				QuoteShort qj = data.get(j);
				QuoteShort qj1 = data.get(j-1);
				QuoteShort qj2 = data.get(j-12);
				
				int diff =qj1.getClose5()-qj.getClose5();
				//int diff =(qj1.getHigh5()-0)-(qj.getHigh5()-0);
				
				accDiff+=diff;
				
				if (diff>=0) downs+=diff;
				else ups += -diff;
				//if (diff>=0) downs++;
				//else ups ++;
				
				totalDiffs++;
			}
			
			double speed =accDiff*0.1/totalDiffs;
			double probU =ups*100.0/(ups+downs);
			double probD =downs*100.0/(ups+downs);
			int maxMin = maxMins.get(i-1);
			if (h>=h1 && h<=h2
					//&& (h>9 || maxMin>400)
					//&& q1.getOpen5()>q.getOpen5()
					&& isTrade <=100
					){
				if (true
						
						&& probD>=minSpeed1 && probD<=minSpeed2
						//&& speed>=minSpeed
						){
					int entry = q.getOpen5();
					int valueTP = entry-tp;
					int valueSL = entry+sl;
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, 
							i, i+nbars, entry, valueTP, valueSL,false);
					
					//int pips = qm.getHigh5()-entry-(entry-qm.getLow5());
					
					
					
					int pips = entry-qm.getClose5();
					if (pips>=0){
						wins++;
						winPips += pips;
						
						if (debug==1){
							System.out.println("[SHORT WIN] "+PrintUtils.Print2dec(probD, false)
									+" "+entry+" "+valueTP+" "+valueSL
									+" || "+q.toString()+" || "+qm.toString()
									);
						}
					}else{
						losses++;
						lostPips += -pips;
						if (debug==1){
							System.out.println("[SHORT LOSS] "+PrintUtils.Print2dec(probD, false)
									+" "+entry+" "+valueTP+" "+valueSL
									+" || "+q.toString()+" || "+qm.toString()
									);
						}
					}
					
					isTrade ++;
				}else if (true						
						&& probU>=minSpeed1 && probU<=minSpeed2
						//&& speed<=-minSpeed
						){
					int entry = q.getOpen5();
					int valueTP = entry+tp;
					int valueSL = entry-sl;
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, 
							i, i+nbars, entry, valueTP, valueSL,false);
					
					//int pips = qm.getHigh5()-entry-(entry-qm.getLow5());
					//int pips = entry-qm.getClose5();
					int pips = qm.getClose5()-entry;
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}
					
					isTrade++;
				}
			}
		}
		
		double avg = (winPips-lostPips)*0.1/(wins+losses);
		double winPer = wins*100.0/(wins+losses);
		double pf = winPips*1.0/lostPips;
		double pfr = lostPips*1.0/winPips;
		double avgDays = countDays*100.0/totalDays;
		
		//if (pf>=1.20 && avgDays>=25.0)
		System.out.println(
				h1+" "+h2
				+" "+lbars
				+" "+nbars
				+" "+tp+" "+sl
				+" "+PrintUtils.Print2dec(minSpeed1, false)
				+" || "+(wins+losses)+" "+wins+" "+losses+" "+PrintUtils.Print2dec(winPer, false)
				
				+" || "+PrintUtils.Print2dec(pf, false)+" "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(pfr, false)
				+" || "+countDays+" / "+totalDays
				+" || "+PrintUtils.Print2dec(avgDays, false)
				);
	}
	
	public static void doTest2(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			double minSpeed1,double minSpeed2,
			int lbars,int nbars,
			int tp,int sl,
			int maxPositions,
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
		double comm = 2.0;
		double balanceInicial = 50000;
		double balance = balanceInicial;
		double maxBalance = balance;
		double maxDD = 0;
		double risk = 1.0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		double probU = 50.0;
		double probD = 50.0;
		
		for (int i=lbars+100;i<data.size()-1;i++){
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
			
			days.add(q.getOpen5());
			
			if (day!=lastDay){
				if (isTrade>0) countDays++;
				isTrade=0;
				lastDay = day;
				totalDays++;
			}
			
			int ups = 0;
			int downs=0;
			for (int j=i-lbars;j<=i-1;j++){
				QuoteShort qj = data.get(j);
				QuoteShort qj1 = data.get(j-1);				
				int diff =qj1.getClose5()-qj.getClose5();								
				if (diff>=0) downs+=diff;
				else ups += -diff;	
			}
			
			probU =ups*100.0/(ups+downs);
			probD =downs*100.0/(ups+downs);
			
			boolean closeLongs = false;
			boolean closeShorts = false;
			//ENTRADAS
			if (h>=h1 && h<=h2
					&& positions.size()<maxPositions
					){
				if (true						
						&& probD>=minSpeed1 && probD<=minSpeed2
						){
					int entry = q.getOpen5();
					int valueTP = entry-tp;
					int valueSL = entry+sl;
					double pipValue = 0.1*(balance*(risk/100.0)/sl);//el miniPip
					
					//pipValue = 0.1;
					
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setSl(valueSL);
					p.setTp(valueTP);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setPositionType(PositionType.SHORT);
					p.setPip$$(pipValue);
					positions.add(p);					
					isTrade ++;
					
					//closeLongs = true;
				}else if (true						
						&& probU>=minSpeed1 && probU<=minSpeed2
						//&& speed<=-minSpeed
						){
					int entry = q.getOpen5();
					int valueTP = entry+tp;
					int valueSL = entry-sl;
					double pipValue = 0.1*(balance*(risk/100.0)/sl);//el miniPip
					
					//pipValue = 0.1;
					
					PositionShort p = new PositionShort();
					p.setEntry(entry);
					p.setSl(valueSL);
					p.setTp(valueTP);
					p.setPositionStatus(PositionStatus.OPEN);
					p.setPositionType(PositionType.LONG);
					p.setPip$$(pipValue);
					positions.add(p);		
					isTrade++;
					
					//closeShorts = true;
				}
			}
			
			//SALIDAS
			int j = 0;
			while (j<positions.size()){
				boolean canClose = false;
				PositionShort p = positions.get(j);
				int pips = 0;
				if (p.getPositionStatus() == PositionStatus.OPEN){
					
					if (p.getPositionType() == PositionType.LONG){
						if (q.getLow5()<=p.getSl()){
							canClose = true;
							pips = -sl;
						}else if (q.getHigh5()>=p.getTp()){
							canClose = true;
							pips = tp;
						}else if (closeLongs){
							canClose = true;
							pips = q.getClose5() - p.getEntry();
						}
					}else if (p.getPositionType() == PositionType.SHORT){
						if (q.getHigh5()>=p.getSl()){
							canClose = true;
							pips = -sl;
						}else if (q.getLow5()<=p.getTp()){
							canClose = true;
							pips = tp;
						}else if (closeShorts){
							canClose = true;
							pips = -q.getClose5() + p.getEntry();
						}
					}					
				}
				
				
				if (canClose){
					pips -= comm;
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}
					
					balance = balance+pips*p.getPip$$();
					if (balance>=maxBalance){
						maxBalance = balance;
					}else{
						double dd = 100.0-balance*100.0/maxBalance;
						if (dd>=maxDD) maxDD = dd;
					}
					
					//System.out.println("[balance] "+pips+" "+PrintUtils.Print2dec2(balance, true));
					
					positions.remove(j);
				}else{
					j++;
				}
			}
			
		}//for
		
		int j = 0;
		QuoteShort q = data.get(data.size()-1);
		while (j<positions.size()){
			boolean canClose = false;
			PositionShort p = positions.get(j);
			int pips = 0;
			if (p.getPositionStatus() == PositionStatus.OPEN){
				
				if (p.getPositionType() == PositionType.LONG){
					pips = q.getClose5() - p.getEntry();
				}else if (p.getPositionType() == PositionType.SHORT){
					pips = -q.getClose5() + p.getEntry();
				}					
			
				if (pips>=0){
					wins++;
					winPips += pips;
				}else{
					losses++;
					lostPips += -pips;
				}
				
				balance = balance+pips*p.getPip$$();
				if (balance>=maxBalance){
					maxBalance = balance;
				}else{
					double dd = 100.0-balance*100.0/maxBalance;
					if (dd>=maxDD) maxDD = dd;
				}
			}
			j++;
		}
		
		double avg = (winPips-lostPips)*0.1/(wins+losses);
		double winPer = wins*100.0/(wins+losses);
		double pf = winPips*1.0/lostPips;
		double pfr = lostPips*1.0/winPips;
		double avgDays = countDays*100.0/totalDays;
		
		double maxp = maxBalance*100.0/balanceInicial-100.0;
		
		double ff = maxp/maxDD;
		//if (pf>=1.20 
				//&& avgDays>=30.0
				//)
		System.out.println(
				h1+" "+h2
				+" "+lbars
				+" "+nbars
				+" "+tp+" "+sl
				+" "+PrintUtils.Print2dec(minSpeed1, false)
				+" || "+(wins+losses)+" "+wins+" "+losses+" "+PrintUtils.Print2dec(winPer, false)
				
				+" || "+PrintUtils.Print2dec(pf, false)+" "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(pfr, false)
				+" || "+countDays+" / "+totalDays
				+" || "+PrintUtils.Print2dec(avgDays, false)
				+" || "+PrintUtils.Print2dec2(balance, true)
				+" || "+PrintUtils.Print2dec2(maxBalance, true)
				+" || "+PrintUtils.Print2dec(maxDD, true)
				+" || "+PrintUtils.Print2dec(maxp, true)
				+" || "+PrintUtils.Print2dec(ff, true)
				);
	}
	
	
	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2017.07.31.csv";
				String pathEURUSD = "C:\\fxdata\\eurusd_5 Mins_Bid_2004.01.01_2018.08.27.csv";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_1 Min_Bid_2009.01.01_2018.09.18.csv";
				
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
					dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
					//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
					TestLines.calculateCalendarAdjustedSinside(dataI);
					//TradingUtils.cleanWeekendDataSinside(dataI); 	
					dataS = TradingUtils.cleanWeekendDataS(dataI);  
					ArrayList<QuoteShort> data = null;
					ArrayList<QuoteShort> dataNoise = null;
					data = dataS;
					ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
					
					for (double prob1=50.0;prob1<=70.0;prob1+=1.0){
						double prob2 = prob1+9990.49;
						for (int y1=2009;y1<=2009;y1++){
							int y2 = y1+9;
							for (int h1=0;h1<=0;h1++){
								int h2 = h1+9;
								for (int lbars=240;lbars<=240 ;lbars+=10){
									for (int nbars=300*240;nbars<=300*240;nbars+=1*240){
										/*for (int tp=300; tp<=400 ;tp+=100){
											for (int sl=2*tp;sl<=5*tp;sl+=1*tp){												
												StudySpeed.doTest("", data, maxMins, y1, y2, 0, 11, h1, h2, 
														prob1,prob2, lbars, nbars,tp,sl,0);
											}
										}*/
										for (int sl=400; sl<=400 ;sl+=100){
											for (int tp=7*sl;tp<=7*sl;tp+=1*sl){												
												//StudySpeed.doTest("", data, maxMins, y1, y2, 0, 11, h1, h2, 
														//prob1,prob2, lbars, nbars,tp,sl,0);
												
												StudySpeed.doTest2("", data, maxMins, y1, y2, 0, 11, h1, h2, 
														prob1,prob2, lbars, nbars,tp,sl,10,0);
											}
										}
										/*for (int tp=400; tp<=400 ;tp+=10){
											for (int sl=3*tp;sl<=3*tp;sl+=1*tp){												
												StudySpeed.doTest2("", data, maxMins, y1, y2, 0, 11, h1, h2, 
														prob1,prob2, lbars, nbars,tp,sl,10,0);
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
