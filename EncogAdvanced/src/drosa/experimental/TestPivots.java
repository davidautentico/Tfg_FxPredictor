package drosa.experimental;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.PriceTestResult;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestPivots {
	
	public static double testYesterdayHL0(ArrayList<Quote> data,
			ArrayList<TradeResultSimple> tradeResults,ArrayList<Quote> dailyData,
			int begin,int end,int day1,int day2,int h1,int h2,double tp,double sl,
			boolean onlyFirst,boolean breakout,boolean debug,boolean printStats){
		
		ArrayList<MaxMinResult> mmArray = new ArrayList<MaxMinResult>();
		int totalTrades = 0;
		int totalWins = 0;
		int end2 = end;
		int actualIndex = 0;
		if (end>tradeResults.size()-1) end2 = tradeResults.size()-1;
		int lastDay = -1;
		int index = -1;
		Calendar cal = null;
		boolean isMax = false;
		boolean isMin = false;
		double min = 99999;
		double max = -999;
	
		for (int i=begin;i<=end2;i++){
			TradeResultSimple res = tradeResults.get(i);
			TradeResultSimple res1 = tradeResults.get(i-1);
			int d = res.getOpenCal().get(Calendar.DAY_OF_YEAR);
			int h = res1.getOpenCal().get(Calendar.HOUR_OF_DAY);	
			int dw = res.getOpenCal().get(Calendar.DAY_OF_WEEK); 
			
			if (dw<day1 || dw>day2) continue;
			if (d!=lastDay){ //sólo busco cuando se cambia de dia, para ahorrarnos búsquedas
				lastDay = d;
				cal = TradeResultSimple.getCalendarDay(tradeResults,i,-1);
				index = TradingUtils.findQuote(dailyData,cal,actualIndex);
				min = 9.9999;
				max = 0.0001;
			}else if ((isMin || isMax) && onlyFirst){
				continue;
			}
									
			if (index>=actualIndex) actualIndex = index;			
			
			//System.out.println(index+" "+h+" "+h1+" "+h2);
			isMax = false;
			isMin = false;
			if (index>=0){
				Quote yesterday = dailyData.get(index);
				int pipsHigh = TradingUtils.getPipsDiff(data.get(i+1).getHigh(),yesterday.getHigh());
				int pipsMax = TradingUtils.getPipsDiff(data.get(i+1).getHigh(),max);
				int pipsLow = TradingUtils.getPipsDiff(yesterday.getLow(),data.get(i+1).getLow());
				int pipsMin = TradingUtils.getPipsDiff(min,data.get(i+1).getLow());
				if (pipsHigh>=1 &&  pipsMax>=1){
					isMax= true;					
					max = data.get(i+1).getHigh();					
				}else if (pipsLow>=1 && pipsMin>=1){
					isMin = true;
					min = data.get(i+1).getLow();
				}
				
				if (isMax || isMin){
					if (h>=h1 && h<=h2){ //solo se contabiliza si esta en rango
						MaxMinResult mm = new MaxMinResult();
						if (isMax){		
							
			  				mm.getCal().setTimeInMillis(res.getOpenCal().getTimeInMillis());
			  				mm.setSl(sl);
			  				mm.setTp(tp);
			  				int win = res.getBuyResult();
			  				mm.getCloseTime().setTimeInMillis(res.getBuyCloseCal().getTimeInMillis());
							if (!breakout){
								win = res.getSellResult();
								mm.getCloseTime().setTimeInMillis(res.getSellCloseCal().getTimeInMillis());
							}
							mm.setWin(win);
							if (win==1) totalWins++;
							if (debug)
								System.out.println("dia maximo: "+totalTrades+" "+d
										+" "+DateUtils.datePrint(yesterday.getDate())
										+" "+PrintUtils.Print4dec(yesterday.getHigh())
										+" "+PrintUtils.Print4dec(max)
										+" "+PrintUtils.Print(data.get(i+2))
										+" "+win);
						}else if (isMin){
							
			  				mm.getCal().setTimeInMillis(res.getOpenCal().getTimeInMillis());
			  				mm.setSl(sl);
			  				mm.setTp(tp);
			  				int win = res.getSellResult();
			  				mm.getCloseTime().setTimeInMillis(res.getSellCloseCal().getTimeInMillis());
							if (!breakout){
								win = res.getBuyResult();
								mm.getCloseTime().setTimeInMillis(res.getBuyCloseCal().getTimeInMillis());
							}
							mm.setWin(win);
							if (win==1) totalWins++;
							if (debug)
								System.out.println("dia minimo: "+totalTrades+" "+d
										+" "+DateUtils.datePrint(yesterday.getDate())
										+" "+PrintUtils.Print4dec(yesterday.getLow())
										+" "+PrintUtils.Print4dec(min)
										+" "+PrintUtils.Print(data.get(i+2))
										+" "+win
										);
						}
						
	  					totalTrades++;
	  					mmArray.add(mm);
					}
				}
				//System.out.println(totalWins*100.0/totalTrades);
			}			
		}
		double comm = 1.4;
		double winPer = totalWins*100.0/totalTrades;
  		double exp = (winPer*tp/100.0)-((100.0-winPer)/100.0)*sl-comm;
  		double kelly = exp*100.0/tp;
  		int maxConcurrent = SuperStrategy.calculateConcurrent(mmArray,null,null,null,tp,sl);
		double maxRisk = 100.0/maxConcurrent;
		if (kelly<maxRisk) maxRisk = kelly;
		double profitPerTrade = exp*(maxRisk/sl);
		if (exp<=0) profitPerTrade = 0;
		double finalCapital = 200*Math.pow(1.0+(profitPerTrade/100.0),totalTrades);
		if (printStats)
		System.out.println(""
				+""+DateUtils.datePrint(tradeResults.get(begin).getOpenCal())
				+" tp= "+tp
				+" sl= "+sl
				+" h1= "+h1
				+" h2= "+h2
				+" tt= "+totalTrades
				+" "+maxConcurrent
				+" "+PrintUtils.Print2dec(winPer, false,3) 
				+" "+PrintUtils.Print2dec(exp, false,2)
				+" "+PrintUtils.Print2dec(profitPerTrade, false,2)
				+" "+PrintUtils.Print2dec2(finalCapital, true)
				);
		
		return profitPerTrade;
	}
	
	public static void testYesterdayHL(ArrayList<Quote> data,ArrayList<Quote> dailyData,
			Calendar from,Calendar to,int day1,int day2,int hTest1,int hTest2,double tp,double sl,boolean onlyFirst){
	
		PriceTestResult resBuy = new PriceTestResult();
		PriceTestResult resSell = new PriceTestResult();
		int totalTrades = 0;
		int totalWins = 0;
		int index = 0;
		double min = 99999;
		double max = -999;
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<dailyData.size();i++){
			Quote q = dailyData.get(i);
			Quote y = dailyData.get(i-1);
			
			cal.setTimeInMillis(q.getDate().getTime());
			//System.out.println(DateUtils.datePrintYMD(cal));
			int day = cal.get(Calendar.DAY_OF_WEEK);		
			//test tiempo
			if (cal.getTimeInMillis()<from.getTimeInMillis() || cal.getTimeInMillis()>to.getTimeInMillis()){
				continue;
			}

			if (day<day1 || day>day2){
				continue;
			}
			//intraday data today
			ArrayList<Quote> dayData = TradingUtils.getDayData(data, cal);
			
			Calendar calj = Calendar.getInstance();
			boolean isMax = false;
			boolean isMin = false;
			min = 99999;
			max = -9999;
			for (int j=0;j<dayData.size()-1;j++){
				Quote qj = dayData.get(j);
				Quote qj1 = dayData.get(j+1);
				//maximos y minimos
				if (qj.getHigh()>=y.getHigh() && qj.getHigh()>=max){ //new high
					//hacemos tests
					isMax = true;
					max = qj.getHigh();
				}else if (qj.getLow()<=y.getLow() && qj.getLow()<=min){
					//hacemos test
					isMin = true;
					min = qj.getLow();
				}
				calj.setTimeInMillis(qj.getDate().getTime());
				int hj = calj.get(Calendar.HOUR_OF_DAY);
				//System.out.println("h: "+hj);
								
				if (isMin || isMax){					
					if (hj>=hTest1 && hj<=hTest2){ //minimo o maximo
						/*System.out.println("minMax tocado "
								+" yHL "+PrintUtils.Print4dec(y.getHigh())+" "+PrintUtils.Print4dec(y.getLow())
								+" "+DateUtils.datePrint(calj)
								+" "+PrintUtils.getOHLC(qj));*/
						index = TradingUtils.getDayIndex(data, cal,index);//indice absoluto
						int end = data.size()-1;
						int begin = index+j+1;
						//System.out.println("begin: "+DateUtils.datePrint(data.get(begin).getDate()));
						double entryValue = data.get(begin).getOpen();
						//buy
						if (isMin){
							
							double slValue = entryValue-0.0001*sl;
							double tpValue = entryValue+0.0001*tp;							
							TradingUtils.testPriceMovement(resBuy,data, begin, end, entryValue,slValue,tpValue,1,true);
							int win = resBuy.getWin(); 
							if (resBuy.getWin()==1) totalWins++;
							System.out.println("minimo yesterday: "
									+" "+totalTrades
									+" "+DateUtils.datePrint(y.getDate())
									+" "+PrintUtils.Print4dec(y.getLow())
									+" "+DateUtils.datePrint(calj)
									+" "+PrintUtils.Print4dec(min)
									+" "+win
									);
						}else if (isMax){
							
							//sell
							double slValue = entryValue+0.0001*sl;
							double tpValue = entryValue-0.0001*tp;
							TradingUtils.testPriceMovement(resSell,data, begin, end, entryValue,slValue,tpValue,0,true);
							int win = resSell.getWin();
							if (resSell.getWin()==1) totalWins++;
							System.out.println("maximo yesterday: "
									+" "+totalTrades
									+" "+DateUtils.datePrint(y.getDate())
									+" "+PrintUtils.Print4dec(y.getHigh())
									+" "+DateUtils.datePrint(calj)
									+" "+PrintUtils.Print4dec(max)
									+" "+win
									);
						}
						totalTrades++;
						//break; //rompemos seguro para este dia
					}
					if (onlyFirst) break;
				}
				isMax=false;
				isMin=false;
			}//daydata
		}//dailyData
		double comm = 1.4;
		double winPer = totalWins*100.0/totalTrades;
  		double exp = (winPer*tp/100.0)-((100.0-winPer)/100.0)*sl-comm;
		System.out.println(
				" from= "+DateUtils.datePrintYMD(from)
				+" to= "+DateUtils.datePrintYMD(to)
				+" d1= "+day1
				+" d2= "+day2
				+" h1= "+hTest1
				+" h2= "+hTest2
				+" tt= "+totalTrades
				+" w%= "+PrintUtils.Print2dec(winPer, false,3) 
				+" ex= "+PrintUtils.Print2dec(exp, false,2)
				);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "c:\\fxdata\\TRADES EURUSD 30_09_2014\\";
		String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		//String path5m = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 		= TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData	= ConvertLib.createDailyData(data);
  		
  		Calendar from = Calendar.getInstance();
  		Calendar to = Calendar.getInstance();
  		from.set(2014, 4, 26);
		to.set(2014,  11, 31);
  		int day1 = Calendar.MONDAY+4;
  		int day2 = Calendar.MONDAY+4;
  		int h1 = 0;
  		int h2 = 9;
  		double tp = 10;
  		double sl = 30;
  		boolean onlyFirst = false;
  		
  		for (tp=5;tp<=30;tp++){
  			for (sl=5;sl<=60;sl++){
		  		double avg = 0;
		  		int total = 0;
		  		for (int begin=450000;begin<=450000;begin+=80000){
		  			int end = begin+50000;
		  			for (day1=Calendar.MONDAY+0;day1<=Calendar.MONDAY+0;day1++){
		  				day2=day1+4;
				  		//for (tp=15;tp<=15;tp++){
				  			//for (sl=15;sl<=15;sl++){
				  				String fileName = path+"trades_EURUSD_"+String.valueOf((int)tp)+"_"+String.valueOf((int)sl)+".csv";				
				  				File f = new File(fileName);
				  				if (!f.exists()){
				  					continue;
				  				}
				  				ArrayList<TradeResultSimple> tradeResults = TradeResultSimple.readFromDisk(fileName);
				  				end = tradeResults.size()-1;
				  				//System.out.println("sizes "+data.size()+" "+tradeResults.size());
				  				//System.out.println("leido results");
				  		  		for (h1=9;h1<=9;h1++){
				  		  			h2 = h1+9;
				  		  			//TestPivots.testYesterdayHL(data, dailyData, from, to, day1, day2, h1, h2, tp, sl, onlyFirst);
				  		  			double profitPerTrade = TestPivots.testYesterdayHL0(data, tradeResults, dailyData, begin, end, day1, day2, h1, h2, tp, sl, onlyFirst,false,false,true);
				  		  			//TestPivots.testYesterdayHL0(data, tradeResults, dailyData, 750000, 850000, day1, day2, h1, h2, tp, sl, onlyFirst,false,false);
				  		  			total++;
				  		  			avg+=profitPerTrade;
				  		  		}
				  			//}
				  		//}
		  			}//day
		  		}
		  		//if (total>0) System.out.println("avg: "+(int)tp+" "+(int)sl+" "+PrintUtils.Print2dec(avg*1.0/total, false,2));
  			}
  		}
  		
	}

}
