package drosa.experimental;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class SuperStrategyReal {
	
	
	public static void testBasedonDistance(ArrayList<Quote> data,ArrayList<Integer> maxMin,Calendar from,Calendar to,
			ArrayList<Double> risks,int maxOpenOrders){
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			
		}
		
	}
	
	public static void testBasedonHourOfDay(){
		
	}
	
	public static void testBasedonDistanceHourOfDay(){
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "c:\\fxdata\\TRADES EURUSD 30_09_2014\\";
		String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		//String path5m = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		System.out.println("datas dataI: "+dataI.size()+" "+dataS.size());
  		//ArrayList<Quote> dataGMT = TradingUtils.cleanWeekendData(dataI);
  		
  		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(1);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(1);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=5;i++) allowedDays.add(1);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedMonths.add(1);
  		
  		String symbol = "EURUSD";
  		String suffix = "c:\\trades_";
  		ArrayList<TradeResultSimple> tradeResults = null;
  		File[] files = new File[2];
  		for (double tp=12;tp<=12;tp+=1){
  			for (double sl=tp;sl<=tp;sl+=1){
  				System.out.println("****"+tp+"-"+sl+"****");
  				String fileName1 = TradingUtils.generateFileName(suffix, symbol, (int)tp,(int)sl,1,400000);
  				String fileName2 = TradingUtils.generateFileName(suffix, symbol, (int)tp,(int)sl,400000,900000);
  				String fileName3 = TradingUtils.generateFileName(suffix, symbol, (int)tp,(int)sl,-1,-1);
		  		tradeResults = TradingUtils.calculateTradeResults(data,fileName1,1,400000,tp, sl);
		  		tradeResults = TradingUtils.calculateTradeResults(data,fileName2,400000,900000,tp, sl);		  		
				files[0] = new File(fileName1);
				files[1] = new File(fileName2);		 
				File mergedFile = new File(fileName3);		 
				TradingUtils.mergeFiles(files, mergedFile);
		  		System.out.println("Calculados trades y mezclados: "+tradeResults.size());
  			}
  		}
  		
  		
  		int buyWins = 0;
  		int sellWins = 0;
  		int totalTrades = 0;
  		double initialCapital = 200;
  		ArrayList<MaxMinResult> mmArray = new ArrayList<MaxMinResult>();
  		//SuperStrategy.convertAllowedTime(allowedHours,"9",0,23,0);
  		int begin = 1;
  		//int inc = 454000;
  		int inc = 104000;
  		int h1 = 0;
  		int h2 = 0;
  		double tp = 8;
  		double sl = 24;
  		String fileName = path+"trades_EURUSD_"+String.valueOf((int)tp)+"_"+String.valueOf((int)sl)+".csv";
  		int maxRetrace = 200000;
  		int total = 0;
  		//String symbol="EURUSD";
  		ArrayList<MaxMinItem> arrayMaxMin = MaxMinItem.calculateMaxMin(data, maxRetrace);
  		System.out.println("Calculados maximos y minimos: "+arrayMaxMin.size());  		
  		//ArrayList<TradeResultSimple> tradeResults = TradeResultSimple.readFromDisk(fileName);
  		
  		ArrayList<ExpectancyItem> exps = ExpectancyItem.calculateExp(arrayMaxMin, tradeResults,3000,250000, tradeResults.size()-1, tp, sl);
  		System.out.println("calculadas las exps: "+exps.size());
  		for (int i=0;i<exps.size();i++){
  			if (exps.get(i)!=null)
  				System.out.println(exps.get(i).toString());
  		}
  		for (begin=300000;begin<=300000;begin+=inc){
  			System.out.println("fecha inicio: "+DateUtils.datePrint(tradeResults.get(begin).getOpenCal()));
  			//int end = begin+inc;
  			int end = tradeResults.size()-1; 
  			if (end>tradeResults.size()-1) end = tradeResults.size()-1; 
	  		for (int period=12;period<=2000;period+=12){
	  			String line="";
	  			double avg = 0;
	  			double periodTrades = 0;
		  		for (int h=h1;h<=h2;h++){
		  			//SuperStrategy.convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
		  			SuperStrategy.convertAllowedTime(allowedHours,"0 1 2 3 4 5 6 7 8 9",0,23,0);
		  			totalTrades = 0;
		  			buyWins = 0;
		  			mmArray.clear();
			  		for (int i=begin;i<=end;i++){
			  			TradeResultSimple res = tradeResults.get(i);
			  			if (i-1>arrayMaxMin.size()-1) break;
			  			MaxMinItem maxMin = arrayMaxMin.get(i-1); 
			  			int allowedH = allowedHours.get(res.getOpenCal().get(Calendar.HOUR_OF_DAY));
			  			if (allowedH==0){ //si la hora no está permitida				
							continue;
						}			
		  				if (maxMin.getMax()>=period){
		  					MaxMinResult mm = new MaxMinResult();
			  				mm.getCal().setTimeInMillis(res.getOpenCal().getTimeInMillis());
			  				mm.setSl(sl);
			  				mm.setTp(tp);
		  					if (res.getSellResult()==1){
		  						buyWins++;
		  						mm.setWin(1);
		  					}else{
		  						mm.setWin(-1);
		  					}
		  					mm.getCloseTime().setTimeInMillis(res.getBuyCloseCal().getTimeInMillis());
		  					totalTrades++;
		  					mmArray.add(mm);
		  				}else if (Math.abs(maxMin.getMin())>=period){
		  					MaxMinResult mm = new MaxMinResult();
			  				mm.getCal().setTimeInMillis(res.getOpenCal().getTimeInMillis());
			  				mm.setSl(sl);
			  				mm.setTp(tp);
		  					if (res.getBuyResult()==1){
		  						buyWins++;
		  						mm.setWin(1);
		  					}else{
		  						mm.setWin(-1);
		  					}
		  					mm.getCloseTime().setTimeInMillis(res.getSellCloseCal().getTimeInMillis());
		  					totalTrades++;
		  					mmArray.add(mm);
		  				}		  				
			  		}//begin
			  		//int maxConcurrent = SuperStrategy.calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					//double maxRisk = 100.0/maxConcurrent;
					double comm = 1.4;
			  		double winPer = buyWins*100.0/totalTrades;
			  		double exp = (winPer*tp/100.0)-((100.0-winPer)/100.0)*sl-comm;
					avg+=exp;
			  		//line+=totalTrades+" "+PrintUtils.Print2dec(exp,false,1)+" ";
			  		line+=PrintUtils.Print2dec(exp,false,1)+" ";
			  		periodTrades += totalTrades;
			  		//System.out.println(period+" "+line);
			  		double kelly = exp*100.0/tp;
			  		int maxConcurrent = SuperStrategy.calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					double maxRisk = 100.0/maxConcurrent;
					//StrategyResultEx res0 = calculateStatsMin(mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true);
					StrategyResultEx res1 = SuperStrategy.calculateStatsReal(mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true,false,false);
			  				System.out.println("h= "+h
			  				+" period= "+period
			  				+" totalTrades= "+totalTrades
			  				+" wins%: "+PrintUtils.Print2(winPer)
			  				+" maxConcurrent: "+maxConcurrent
			  				+" exp= "+PrintUtils.Print2(exp)
			  				+" kelly= "+PrintUtils.Print2(kelly)
			  				+" capital= "+PrintUtils.Print2dec2(res1.getFinalCapital(), true)
			  				);
		  		}//h
		  		//System.out.println(period+" "+periodTrades+" "+line+PrintUtils.Print2dec(avg*1.0/(h2-h1+1),false,1));
	  		}//period
  		}  	
	}
}
