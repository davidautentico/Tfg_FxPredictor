package drosa.experimental.spikeStudy;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.CurrencyType;
import drosa.experimental.PositionShort;
import drosa.experimental.StatsDebugOptions;
import drosa.experimental.SuperStrategy;
import drosa.experimental.SystemStats;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Spikes {
	
	public static double candleRetracement2(ArrayList<QuoteShort> data, 
			int begin, int end,
			String hours,int bars,int diff,int n,int tp ,int sl,double riskPerTrade,boolean debug){
		
		SystemStats systemStats = new SystemStats(200.0,400,99999999,
				5,1.3,CurrencyType.USD_BASED,tp,sl,true);
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		PositionShort pos = null;
		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		int total5 = 0;
		double avgPipsCT = 0;
		int total= 0;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int DOvalue=0;
		int end2 = end;
		if (end>data.size()-1) end2 = data.size()-1;
		for (int i=begin;i<end2;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			if (allowedHours.get(hour)==0) continue;
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				DOvalue = q.getOpen5();
				lastDay = day;
			}
			
			int allowed = allowedHours.get(hour);
			if (allowed==1 
					&& (hour!=0 || min>=10) ){ 
			
				QuoteShort qR = null;
				QuoteShort qL = null;
				int diffH = 0;
				int diffL = 0;
				if (bars>=0){
					qR = TradingUtils.getMaxMinShort(data, i-1-bars, i-1);
					diffH = q.getClose5()-qR.getHigh5();
					diffL = qR.getLow5()-q.getClose5();
				}
				
				qL = TradingUtils.getMaxMinShort(data, i+1, i+1+n);
				
				if (q.getHigh5()>=DOvalue && (q.getClose5()-q.getOpen5())>=0){	
					if ((bars>0 && diffH>=diff*10) || (bars==0 && diffH<=-diff*10)){
						String res = "LOSE";
						int diffq1 = q1.getOpen5()-qL.getLow5();
						avgPipsCT+= q1.getOpen5()-qL.getLow5();
						total++;
						if (diffq1>=50){
							res = "WIN";
							total5++;
						}
						
						//System.out.println(q.toString()+" "+res);
						pos = new PositionShort();
						pos.setEntry(q1.getOpen5());
						pos.setTp(q1.getOpen5()-tp*10);
						pos.setSl(q1.getOpen5()+sl*10);
						pos.setPositionStatus(PositionStatus.PENDING);
						pos.setPendingIndex(i+1);
						pos.setPositionType(PositionType.SHORT);
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						pos.setRisk(riskPerTrade);
						positions.add(pos);
					}
				}else if (q.getLow5()<DOvalue && (q.getClose5()-q.getOpen5())<=0){
					if ((bars>0 && diffL>=diff*10) || (bars==0 && diffL<=-diff*10)){
						String res = "LOSE";
						int diffq1 = qL.getHigh5()-q1.getOpen5();
						avgPipsCT+= qL.getHigh5()-q1.getOpen5();
						total++;
						if (diffq1>=50){
							total5++;
							res = "WIN";
						}
						//System.out.println(q.toString()+" "+res);
						
						pos = new PositionShort();
						pos.setEntry(q1.getOpen5());
						pos.setTp(q1.getOpen5()+tp*10);
						pos.setSl(q1.getOpen5()-sl*10);
						//pos.setPositionStatus(PositionStatus.OPEN);
						pos.setPositionStatus(PositionStatus.PENDING);
						pos.setPendingIndex(i+1);
						pos.setPositionType(PositionType.LONG);
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						pos.setRisk(riskPerTrade);
						positions.add(pos);
					}
				}	
			}
			//systemStats.update(positions, q1, i+1,StatsDebugOptions.NONE);
		}//for
		
		double avgCT = avgPipsCT / (total*10);
		
		if (debug)
		System.out.println(total+" "+bars+" "+diff+" "+n
				+" "+PrintUtils.Print2(avgCT)
				+" "+PrintUtils.Print2(total5*100.0/total)
				);
		
		return total5*100.0/total;
		/*systemStats.printSummary(total+" "+bars+" "+diff+" "+n
				+" "+PrintUtils.Print2(avgCT)
				+" "+PrintUtils.Print2(total5*100.0/total));*/
	}
	
	public static void candleRetracement(ArrayList<QuoteShort> data, 
			int begin, int end,
			String hours,int tp,int sl,int wick1,int wick2,int diffCloseOpen){
		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		
		ArrayList<Integer> totalPer = new ArrayList<Integer>();
		for (int i =0;i<=100;i++) totalPer.add(0);
		double avgPips = 0;
		double avgPipsCT = 0;
		int total= 0;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int DOvalue;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				DOvalue = q.getOpen5();
				lastDay = day;
			}
			
			int wickH = q.getHigh5()-q.getClose5();
			int wickL = q.getClose5()-q.getLow5();
			int diffClose = q.getClose5()-q.getOpen5();
			if (diffClose>=diffCloseOpen*10){
				if (wickH>=wick1*10 && wickH<=wick2*10){
					avgPips   += q1.getHigh5()-q.getClose5();
					avgPipsCT += q.getClose5()-q1.getLow5();
					total++;

					int diffPips = q1.getHigh5()-q.getClose5();
					double per = diffPips*10.0/wick2;
					//System.out.println(diffPips+" "+per);
					int end2 = (int) per;
					if (per>=100) end2 = 100;
					//System.out.println(end2);
					for (int p = 0 ;p<=end2;p++){
						int count = totalPer.get(p);
						totalPer.set(p, count+1);
					}
				}
			}else if (diffClose<=-diffCloseOpen){
				if (wickL>=wick1*10 && wickL<=wick2*10){
					avgPips   += q.getClose5()-q1.getLow5();
					avgPipsCT += q1.getHigh5()-q.getClose5(); 
					total++;

					int diffPips = q.getClose5()-q1.getLow5();
					double per = diffPips*10.0/wick2;
					int end2 = (int) per;
					if (per>=100) end2 = 100;
					for (int p = 0 ;p<=end2;p++){
						int count = totalPer.get(p);
						totalPer.set(p, count+1);
					}
				}
			}
			
		}
		double avg = avgPips / (total*10);
		double avgCT = avgPipsCT / (total*10);
		int total50 = totalPer.get(50);
		int total60 = totalPer.get(60);
		int total70 = totalPer.get(70);
		int total80 = totalPer.get(80);
		int total90 = totalPer.get(90);
		int total100 = totalPer.get(100);
		System.out.println(wick1 +" "+wick2+" "+total
				+" "+PrintUtils.Print2(avg)
				+" "+PrintUtils.Print2(avgCT)
				+" "+PrintUtils.Print2(avg/avgCT)
				+" "+PrintUtils.Print2(avg*100.0/wick2)
				+" "+PrintUtils.Print2(total50*100.0/total)
				+" "+PrintUtils.Print2(total60*100.0/total)
				+" "+PrintUtils.Print2(total70*100.0/total)
				+" "+PrintUtils.Print2(total80*100.0/total)
				+" "+PrintUtils.Print2(total90*100.0/total)
				+" "+PrintUtils.Print2(total100*100.0/total)
				);
		
	}
	
	public static void studyCandles(ArrayList<QuoteShort> data, 
			int begin, int end,
			String hours,
			int barsAfter,int tp,int sl,double riskPerTrade,double comm,int diff1,int diff2,int doDiff,int diffhl){
		
		//stats
		SystemStats systemStats = new SystemStats(200.0,400,5,
						5,comm,CurrencyType.USD_BASED,tp,sl,true);
		
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		PositionShort pos = null;
		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		
		int totalH = 0;
		int totalL = 0;
		int totaltpH2 = 0;
		int totaltpL2 = 0;
		int totalWinsH = 0;
		int totalWinsL = 0;
		int avgMaxH = 0;
		int avgMaxL = 0;
		int begin2 = begin;
		if (begin<0) begin2 = doDiff+2;
		int end2 = end;		
		if (end2>=data.size()-1) end2 = data.size()-2;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		//bucle principal
		int lastDay = -1;
		int DOvalue = 0;
		for (int i=begin2;i<=end2-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort q_1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			
	
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				DOvalue = q.getOpen5();
				lastDay = day;
			}
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int allowed = allowedHours.get(hour);
			if (allowed==1 
					&& (hour!=0 || min>=10) ){ 
				QuoteShort qR = null;
				if (doDiff>0){
					qR = TradingUtils.getMaxMinShort(data, i-1-1-doDiff, i-1-1);
				}
				
				int diffH = qR.getHigh5()-q.getOpen5();
				int diffL = q.getOpen5()-qR.getLow5();
				int diffLq = q.getOpen5()-q.getLow5();
				int diffHq = q.getHigh5()-q.getOpen5();
				
				int diffO1 = q.getOpen5()-q_1.getOpen5();
				int diffDO = q.getOpen5()-DOvalue;
				int diffBarsH = q_1.getHigh5()-qR.getHigh5();
				int diffBarsL = qR.getLow5()-q_1.getLow5();
				
				//if (diffO1>=diff*10){
				if (diffO1>=diff1*10 && diffO1<=diff2*10 && diffBarsH>=diffhl*10){
					int win = 0;
					totalH++;
					pos = new PositionShort();
					int df = (int) ((q_1.getHigh5()-q.getOpen5())*0.7);
					if (df>=20)
						pos.setEntry(q.getOpen5()+df);
					else pos.setEntry(q.getOpen5());
					
					//pos.setEntry(q.getOpen5());
					pos.setTp(pos.getEntry()-tp*10);
					pos.setSl(pos.getEntry()+sl*10);
					//pos.setPositionStatus(PositionStatus.OPEN);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPendingIndex(i);
					pos.setPositionType(PositionType.SHORT);
					pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					pos.setRisk(riskPerTrade);
					positions.add(pos);
					if (diffL>=tp*10 || diffLq>=tp*10){
						win = 1;
						totalWinsH++;
						avgMaxH +=diffH;
						if (diffH>=2*tp*10){
							totaltpH2++;
						}
					}
					//System.out.println(win+" "+q.toString()+" "+diffHq);
				}
				//if (diffO1<=-diff*10){ 
				if (diffO1<=-diff1*10 && diffO1>=-diff2*10 && diffBarsL>=diffhl*10){ 
					int win = 0;
					totalL++;
					pos = new PositionShort();
					int df = (int) ((q.getOpen5()-q_1.getLow5())*0.7);
					if (df>=20)
						pos.setEntry(q.getOpen5()-df);
					else pos.setEntry(q.getOpen5());
					
					//pos.setEntry(q.getOpen5());
					pos.setTp(pos.getEntry()+tp*10);
					pos.setSl(pos.getEntry()-sl*10);
					//pos.setPositionStatus(PositionStatus.OPEN);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPendingIndex(i);
					pos.setPositionType(PositionType.LONG);
					pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					pos.setRisk(riskPerTrade);
					positions.add(pos);
					if (diffH>=tp*10 || diffHq>=tp*10){
						win = 1;
						totalWinsL++;
						avgMaxL +=diffL;
						if (diffL>=2*tp*10){
							totaltpL2++;
						}
					}
					//System.out.println(win+" "+q.toString()+" "+diffLq);
				}
			}
			//PositionShort.updatePositions(positions,q);
			systemStats.update(positions, q, i,StatsDebugOptions.NONE);
		}
		int totalClosed = PositionShort.countTotal(positions,PositionStatus.CLOSE);
		int totalWins = PositionShort.countTotalResult(positions,1);	
		
		double perWinClosed = totalWins*100.0/totalClosed;
		double pf = (perWinClosed*tp)/((100.0-perWinClosed)*sl);
		double pfComm = (perWinClosed*(tp-comm))/((100.0-perWinClosed)*(sl+comm));
		double ex = (perWinClosed*tp)-((100.0-perWinClosed)*sl);
		double perWinH = totalWinsH*100.0/totalH;
		double perWinL = totalWinsL*100.0/totalL;
		double perWinT = (totalWinsH+totalWinsL)*100.0/(totalH+totalL);
		/*System.out.println(tp+" "+sl+" "+diff1+" "+diff2+" "+doDiff
				+" || "
				+" "+totalClosed+" "+PrintUtils.Print2(perWinClosed)+" "+PrintUtils.Print2(pf)
				+" ("+PrintUtils.Print2(pfComm)+")"
				+" "+PrintUtils.Print2(ex/100.0)
				);*/
		systemStats.printSummary(doDiff+" "+tp+" "+sl);
	}

	
	public static void spikeStudy(ArrayList<QuoteShort> data, 
			int begin, int end,
			int h1,int h2,
			int barsBreak,int barsAfter,
			int breachSize1,int breachSize2){
		
		int totalBreaches = 0;
		int totalWins = 0;
		int begin2 = begin-barsBreak;
		if (begin2<0) begin2 = barsBreak;
		int end2 = end;		
		if (end2>=data.size()-1) end2 = data.size()-2;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		//bucle principal
		for (int i=begin2;i<=end2-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			if (hour<h1 || hour>h2) continue;
			if (hour==0 && min<10) continue;
			QuoteShort qL = null;
			QuoteShort qR = null;
			int difflH=-9999999;
			int difflL=-9999999;
			int diffrH=-9999999;
			int diffrL=-9999999;
			if (barsBreak>0){
				qL = TradingUtils.getMaxMinShort(data, i-1-barsBreak-1, i-1);
				difflH = q.getHigh5()-qL.getHigh5();
				difflL = qL.getLow5()-q.getLow5();
				//System.out.println(q.toString()+" || "+qL.toString()+" "+difflH+" "+difflL+" || "+(i-1-barsBreak-1));
			}
			
			if (barsAfter>0){
				qR = TradingUtils.getMaxMinShort(data, i+1, i+1+barsAfter-1);
				diffrH = qL.getHigh5()-qR.getLow5();
				diffrL = qR.getHigh5()-qL.getLow5();
			}
			
			//int breachHigh = qL.getHigh5()+breachSize*10;
			//int breachLow = qL.getLow5()-breachSize*10;
			
			if (difflH>=breachSize1*10 && difflH<=breachSize2*10){
				int win = 0;
				if (q.getClose5()<=qL.getHigh5() || diffrH>=0){
					totalWins++;
					win = 1;
				}
				totalBreaches++;
				
				/*System.out.println("HIGH "+win
						+" "+q.toString()+" "+q1.toString()
						+" || "+qL.toString()+" "+difflH+" "+difflL
						+" || "+qR.toString()+" "+diffrH+" "+diffrL);*/
			}
			if (difflL>=breachSize1*10 && difflL<=breachSize2*10){
				int win = 0;
				if (q.getClose5()>=qL.getLow5() || diffrL>=0){
					totalWins++;
					win = 1;
				}
				totalBreaches++;
				
				/*System.out.println("LOW "+win
						+" || "+q.toString()+" "+q1.toString()
						+" || "+qL.toString()+" "+difflH+" "+difflL
						+" || "+qR.toString()+" "+diffrH+" "+diffrL);*/
			}
		}
		double perWin = totalWins*100.0/totalBreaches;
		System.out.println(h1+" "+h2+" "+barsBreak+" "+barsAfter+" "+breachSize1+" || "
				+totalBreaches+" "+PrintUtils.Print2(perWin));
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String path5m0     = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		String path5m1     = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		String path5m2     = "c:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2005.10.07_2014.11.27.csv";
		String path5m3     = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		String path5m4     = "c:\\fxdata\\USDCAD_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		String path5m5     = "c:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		String path5m6     = "c:\\fxdata\\EURGBP_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		String path5m7     = "c:\\fxdata\\AUDNZD_UTC_5 Mins_Bid_2006.12.12_2014.11.27.csv";	
		String path5m8     = "c:\\fxdata\\GBPAUD_UTC_5 Mins_Bid_2006.03.22_2014.11.27.csv";
		String path5m9     = "c:\\fxdata\\GBPCAD_UTC_5 Mins_Bid_2006.01.02_2014.11.27.csv";
		String path5m10    = "c:\\fxdata\\GBPNZD_UTC_5 Mins_Bid_2006.01.02_2014.11.27.csv";
		String path5m11    = "c:\\fxdata\\EURNZD_UTC_5 Mins_Bid_2006.01.02_2014.11.27.csv";
		//String path5m10    = "c:\\fxdata\\USDPLN_UTC_5 Mins_Bid_2007.03.13_2014.11.27.csv";
		
		ArrayList<String> files = new ArrayList<String>();
		files.add(path5m0);
		files.add(path5m1);files.add(path5m2);files.add(path5m3);files.add(path5m4);files.add(path5m5);
		files.add(path5m6);files.add(path5m7);files.add(path5m8);files.add(path5m9);files.add(path5m10);
		files.add(path5m11);
		double comm = 1.4;
		for (int p=7;p<=7;p++){
			String path5m = files.get(p);
			File f = new File(path5m);
			if (!f.exists()) continue;
			
			if (p==0){
				comm = 1.4;
			}
			if (p==2){
				comm = 2.0;
			}
			if (p==3){
				comm = 1.4;
			}
			if (p==7){
				comm = 1.9;
			}
			if (p==8){
				comm = 2.3;
			}
			if (p==9){
				comm = 2.3;
			}
			if (p==10){
				comm = 3.0;
			}	
			if (p==11){
				comm = 3.0;
			}
			Sizeof.runGC ();
						
			ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 
	  		//ArrayList<Quote> data10m  = ConvertLib.convert(data5m,2);
	  		//ArrayList<Quote> data15m  = ConvertLib.convert(data5m,3);
	  		//ArrayList<Quote> data20m  = ConvertLib.convert(data5m,4);
	  		//ArrayList<Quote> data1h = ConvertLib.convert(data5m,12);
	  		
			ArrayList<QuoteShort> data5mS  = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			//ArrayList<QuoteShort> data10mS  = QuoteShort.convertQuoteArraytoQuoteShort(data10m);
			//ArrayList<QuoteShort> data20mS  = QuoteShort.convertQuoteArraytoQuoteShort(data20m);
			//ArrayList<QuoteShort> data15mS  = QuoteShort.convertQuoteArraytoQuoteShort(data15m);
			//ArrayList<QuoteShort> data1hS  = QuoteShort.convertQuoteArraytoQuoteShort(data1h);
			
			//QuoteShort.saveToDisk(data5mS,"c:\\data5digits.csv");
			ArrayList<QuoteShort> data = null;
			data = data5mS;
			//data = data15mS;
			//data = data1hS;
			System.out.println("TEST file data "+path5m+" "+data.size());
			
			//int begin = data.size()/2;
			int begin = data.size()-800000;
			int end   = 900000;
			int h1 = 0;
			int h2 = 23;
			int barsAfter = 0;
			int breachSize1 = 5;
			int breachSize2 = 10;
			int tp = 4;
			int sl = 4;
			double riskPerTrade = 8.0;
			
			/*
			for (int h=0;h<=0;h++){
				for (int bars=100;bars<=100;bars+=1){
					for (int n=6;n<=6;n+=12){
						for (int diff=1;diff<=10;diff++){
							String prob="";	
							double avg = 0;
							for (int hour=0;hour<=23;hour++){
								//Spikes.candleRetracement2(data, begin, end,"0",bars,diff,n);
								//Spikes.candleRetracement2(data, begin, end,"0 1 2 3 4",bars,diff,n,tp,sl,riskPerTrade);
								//Spikes.candleRetracement2(data, begin, end,"5 6 7 8 9",bars,diff,n,tp,sl,riskPerTrade);
								//double per = Spikes.candleRetracement2(data, begin, end,"15",bars,diff,n,tp,sl,riskPerTrade,false);
		
								//Spikes.candleRetracement2(data, begin, end,"0 1 2 3 4 5 7 8 9",bars,diff,n,tp,sl,riskPerTrade);
								//Spikes.candleRetracement2(data, begin, end,"5 6 7 8 9",bars,diff,n,tp,sl,riskPerTrade);
								//Spikes.candleRetracement2(data, begin, end,"10 11 12 13 14",bars,diff,n,tp,sl,riskPerTrade);
								//Spikes.candleRetracement2(data, begin, end,"15 16",bars,diff,n,tp,sl,riskPerTrade);
								//Spikes.candleRetracement2(data, begin, end,"15 16 17 18 19 20 21 22 23",bars,diff,n,tp,sl,riskPerTrade);
								
								//Spikes.candleRetracement2(data, begin, end,"0 1 2 3 4 5 6 7 8 9",bars,diff,n,tp,sl,riskPerTrade);
								//Spikes.candleRetracement2(data, begin, end,"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23",bars,diff,n,tp,sl,riskPerTrade);
								double per = Spikes.candleRetracement2(data, begin, end,String.valueOf(hour),bars,diff,n,tp,sl,riskPerTrade,false);
								prob+= PrintUtils.Print2dec(per,false,2)+" ";
								avg += per;
							}
							System.out.println(bars+" "+n+" "+diff+" || " +prob+" || "+PrintUtils.Print2(avg/24));
						}
					}
				}
			}*/
			
			
			for (h1=0;h1<=23;h1++){
				h2=h1+0;
				System.out.println(h1);
				for (int barsBreak=1;barsBreak<=1;barsBreak+=1){
					for (barsAfter=12;barsAfter<=12;barsAfter+=6){
						//Spikes.spikeStudy(data, begin, end, h1, h2, barsBreak, barsAfter, breachSize1,breachSize2);
						for (int diff1=1;diff1<=1;diff1++){
							int diff2=13;
							for (int diffhl=0;diffhl<=0;diffhl++){
								for (int doDiff=100;doDiff<=500;doDiff+=50){
									for (tp=12;tp<=12;tp++){
										for (sl=(int) (tp*2.0);sl<=tp*2.0;sl+=tp*1.0){
										//for (sl=5;sl<=5;sl+=tp*0.5){
											//Spikes.studyCandles(data, begin, end, String.valueOf(h1), barsAfter, tp,sl,riskPerTrade,diff1,diff2,doDiff);
											if (p==0){
												//Spikes.studyCandles(data, begin, end, "0 1 2 3 4 5 6 7 8 9", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);//EURUSD
												//Spikes.studyCandles(data, begin, end, String.valueOf(h1), barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);
											}
											if (p==1){
												//Spikes.studyCandles(data, begin, end, "0 1 2 3 4 5 6 7 8 9", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);//EURUSD
												Spikes.studyCandles(data, begin, end, String.valueOf(h1), barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);
											}
											if (p==2){
												//Spikes.studyCandles(data, begin, end, "0 1 7 8 19 20 21 22 23", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);//EURAUD
												Spikes.studyCandles(data, begin, end, String.valueOf(h1), barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);
											}
											if (p==3){
												//Spikes.studyCandles(data, begin, end, "0 1 2 8 9 10 23", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff);//EURAUD
												Spikes.studyCandles(data, begin, end, String.valueOf(h1), barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);
											}
											if (p==7){
												//Spikes.studyCandles(data, begin, end, "8 9 10 17 18 19 20 21 22 23", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);//AUDNZD
												Spikes.studyCandles(data, begin, end, String.valueOf(h1), barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);
											}if (p==8)
											Spikes.studyCandles(data, begin, end, "0 1 7 8 19 20 21 22 23", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);//GBPAUD
											if (p==9)
											Spikes.studyCandles(data, begin, end, "0 1 19 20 21 22 23", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);//GBPCAD
											if (p==10)
											Spikes.studyCandles(data, begin, end, "0 1 19 20 21 22 23", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);//GBPNZD
											if (p==11)
											Spikes.studyCandles(data, begin, end, "0 1 7 8 9 19 20 21 22 23", barsAfter, tp,sl,riskPerTrade,comm,diff1,diff2,doDiff,diffhl);//EURNZD
										}
									}
								}
							}//diffhl
						}//diff
					}
				}	
			}//h1
			
			
		}//files
	}
}
