package drosa.experimental;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class SuperStrategy3 {

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
  		ArrayList<MaxMinResult> mmArray = new ArrayList<MaxMinResult>();
  		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(1);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(1);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=5;i++) allowedDays.add(1);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedMonths.add(1);
  		
		int maxRetrace = 200000;
		double initialCapital = 200;
  		ArrayList<MaxMinItem> arrayMaxMin = MaxMinItem.calculateMaxMin(data, maxRetrace);
  		System.out.println("Calculados maximos y minimos: "+arrayMaxMin.size()); 

  		/*String f1 = path+"trades_EURUSD_5_10.csv";
  		String f2 = path+"trades_EURUSD_5_15.csv";
  		ArrayList<TradeResultSimple> t1 = TradeResultSimple.readFromDisk(f1);
		ArrayList<TradeResultSimple> t2 = TradeResultSimple.readFromDisk(f2);
		if (!TradingUtils.checkTradeFileConsistency(t1,t2)){
			System.out.println("ERROR!!");
		}*/
  		
  		  		
  		for (int h=0;h<=0;h++){
  			//SuperStrategy.convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
  			//SuperStrategy.convertAllowedTime(allowedHours,"0 1 2 3 4 5 6 7 8 9 23",0,23,0);
	  		for (double tp=8;tp<=8;tp++){
				//for (double sl=tp;sl<=tp*1;sl+=tp){
	  			for (double sl=24;sl<=24;sl+=1){
					String fileName = path+"trades_EURUSD_"+String.valueOf((int)tp)+"_"+String.valueOf((int)sl)+".csv";				
					File f = new File(fileName);
					if (!f.exists()){
						//System.out.println("NO EXISTE!");
						continue;
					}
					//System.out.println(fileName);
					ArrayList<TradeResultSimple> tradeResults = TradeResultSimple.readFromDisk(fileName);
					//System.out.println(fileName+" "+tradeResults.size());
					int end = tradeResults.size()-1; 
					//int end = 300000; 
					for (int period=12;period<=50000;period+=12){//para cada periodo
						String line = String.valueOf(period)+" ";						
			  			for (h=0;h<=23;h++){
			  				SuperStrategy.convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
			  				int totalTrades = 0;
				  			int buyWins = 0;
				  			mmArray.clear();
							for (int i=400000;i<=end;i++){						
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
					  		}//i begin
							double comm = 1.4;
					  		double winPer = buyWins*100.0/totalTrades;
					  		double exp = (winPer*tp/100.0)-((100.0-winPer)/100.0)*sl-comm;
					  		double kelly = exp*100.0/tp;
					  		int maxConcurrent = SuperStrategy.calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
							double maxRisk = 100.0/maxConcurrent;
							if (kelly<maxRisk) maxRisk = kelly;
							double profitPerTrade = exp*(maxRisk/sl);
							if (exp<=0) profitPerTrade = 0;
							double finalCapital = initialCapital*Math.pow(1.0+(profitPerTrade/100.0),totalTrades);
							if (profitPerTrade<=0) finalCapital = 0;
							line+=" "+PrintUtils.Print2dec(profitPerTrade,false,1);
							/*System.out.println(period
									+" "+totalTrades
									+" "+h
									+" "+PrintUtils.PrintInt2((int)tp)
									+" "+PrintUtils.PrintInt2((int)sl)
									//+" "+maxConcurrent
									//+" "+PrintUtils.Print2(1.0+(profitPerTrade/100.0))
									+" "+PrintUtils.Print2(profitPerTrade)
									//+" "+PrintUtils.Print2(exp)
									//+" "+PrintUtils.Print2dec2(finalCapital, true)
									);*/
			  			}//h
			  			System.out.println(line);
					}//period
				}//sl
			}//tp
  		}//h

	}

}
