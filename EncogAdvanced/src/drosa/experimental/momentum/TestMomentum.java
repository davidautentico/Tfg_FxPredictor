package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMomentum {
	
	public static void testWithProfit(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,int begin,int end,
			int h1,int h2,
			int maxMinThr,int maxTime,int offset,
			int maxTradesDay,
			double stopSize,
			boolean debug
			){
		
		if (end>=data.size()-1)
			end = (data.size()-1)-maxTime-1-offset;
        
		double winPips = 0;
		double lostPips = 0;
		double avg=0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int actualTrades = 0;
		boolean canTrade = true;
		int actualMax = -1;
		int actualMin = -1;
		for (int i=begin;i<=end ;i++){
			int iOpen = i+1;
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(iOpen);
			QuoteShort qEnd = data.get(i+1+maxTime-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (h<h1 || h>h2) continue;
			
			if (day!=lastDay){
				actualTrades = 0;
				canTrade = true;
				actualMax=-1;
				actualMin=-1;
				lastDay = day;
			}
			
			if (actualMax==-1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
			if (actualMin==-1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
			
			int maxMin = maxMins.get(i).getExtra();
			
			boolean closed = false;
			double diff = 0;
			if (canTrade){
				if (maxMin>=maxMinThr && q1.getOpen5()<q.getHigh5()){
					double rangeProfit = (q1.getOpen5()-actualMin)*stopSize;
					double profitValue = q1.getOpen5()+rangeProfit;
					QuoteShort highLow = TradingUtils.getMaxMinShort(data, iOpen, iOpen+maxTime+1);
					if (highLow.getHigh5()>=profitValue){
						diff = (profitValue-q1.getOpen5());//profit
					}else{
						diff = qEnd.getClose5()-q1.getOpen5();
					}
					avg+=diff;
					closed = true;
					actualTrades++;
				}else if (maxMin<=-maxMinThr && q1.getOpen5()>q.getLow5()){
					double rangeStop = (actualMax-q1.getOpen5())*stopSize;
					double profitValue = q1.getOpen5()-rangeStop;
					QuoteShort highLow = TradingUtils.getMaxMinShort(data, iOpen, iOpen+maxTime+1);
					if (highLow.getLow5()<=profitValue){
						diff = (q1.getOpen5()-profitValue);//stop
					}else{
						diff = q1.getOpen5()-qEnd.getClose5();
					}
					avg+=diff;
					closed = true;
					actualTrades++;
				}
				if (closed){
					//canTrade = false;
					if (diff>=0){
						wins++;
						winPips+=diff*0.1;
						if (debug){
							System.out.println("[CLOSE WIN] "+DateUtils.datePrint(cal1)
									+" "+q1.getOpen5()
									+" "+PrintUtils.Print2dec(diff*0.1,false));
						}
					}
					else{
						lostPips+=-diff*0.1;
						losses++;
						if (debug){
							System.out.println("[CLOSE LOSE] "+DateUtils.datePrint(cal1)
									+" "+q1.getOpen5()
									+" "+PrintUtils.Print2dec(diff*0.1,false));
						}
					}
					if (actualTrades>=maxTradesDay){
						canTrade = false;
					}
				}
			}
		}
		
		QuoteShort q0 = data.get(begin);
		QuoteShort.getCalendar(cal, q0);
		String dateBegin = DateUtils.datePrint(cal);
		QuoteShort qn = data.get(end);
		QuoteShort.getCalendar(cal, qn);
		String dateEnd = DateUtils.datePrint(cal);
		
		int total = wins+losses;
		double perWin = wins*100.0/total;
		double avgPips = avg*0.1/total;
		System.out.println(
				header
				+" "+dateBegin+" "+dateEnd
				+" "+h1+" "+h2
				+" "+maxMinThr
				+" "+maxTime
				+" "+offset
				+" "+PrintUtils.Print2dec(stopSize, false)
				+" "+maxTradesDay
				+" || "+total
				+" "+PrintUtils.Print2dec(perWin, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(winPips/lostPips, false)
				);
	}
	
	public static void testWithStop(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,int begin,int end,
			int h1,int h2,
			int maxMinThr,int maxTime,int offset,
			int maxTradesDay,
			double stopSize,
			boolean debug
			){
		
		if (end>=data.size()-2)
			end = data.size()-2;
        double totalPips = 0;
		double totalPipRisk = 0;
		double winPips = 0;
		double lostPips = 0;
		double avg=0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int actualTrades = 0;
		boolean canTrade = true;
		int actualMax = -1;
		int actualMin = -1;
		for (int i=begin;i<=end ;i++){
			int iOpen = i+1;
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(iOpen);
			int iEnd = i+1+maxTime-1;
			if (iEnd>=data.size()-1) iEnd = data.size()-1;
			QuoteShort qEnd = data.get(iEnd);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (h<h1 || h>h2) continue;
			
			if (day!=lastDay){
				actualTrades = 0;
				canTrade = true;
				actualMax=-1;
				actualMin=-1;
				lastDay = day;
			}
			
			if (actualMax==-1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
			if (actualMin==-1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
			
			int maxMin = maxMins.get(i).getExtra();
			
			boolean closed = false;
			double diff = 0;
			if (canTrade){
				if (maxMin>=maxMinThr && q1.getOpen5()<q.getHigh5()){
					double rangeStop = (q1.getOpen5()-actualMin)*stopSize;
					double stopValue = q1.getOpen5()-rangeStop;
					QuoteShort highLow = TradingUtils.getMaxMinShort(data, iOpen, iOpen+maxTime+1);
					totalPipRisk+=rangeStop;
					if (highLow.getLow5()<=stopValue){
						diff = -(q1.getOpen5()-stopValue);//stop
					}else{
						diff = qEnd.getClose5()-q1.getOpen5();
					}
					avg+=diff;
					closed = true;
					actualTrades++;
				}else if (maxMin<=-maxMinThr && q1.getOpen5()>q.getLow5()){
					double rangeStop = (actualMax-q1.getOpen5())*stopSize;
					double stopValue = q1.getOpen5()+rangeStop;
					QuoteShort highLow = TradingUtils.getMaxMinShort(data, iOpen, iOpen+maxTime+1);
					totalPipRisk+=rangeStop;
					if (highLow.getHigh5()>=stopValue){
						diff = -(stopValue-q1.getOpen5());//stop
					}else{
						diff = q1.getOpen5()-qEnd.getClose5();
					}
					avg+=diff;
					closed = true;
					actualTrades++;
				}
				if (closed){
					//canTrade = false;
					if (diff>=0){
						totalPips+=diff*0.1;
						wins++;
						winPips+=diff*0.1;
						if (debug){
							System.out.println("[CLOSE WIN] "+DateUtils.datePrint(cal1)
									+" "+q1.getOpen5()
									+" "+PrintUtils.Print2dec(diff*0.1,false)
									+" || "+PrintUtils.Print2dec(totalPips,false)
									);
						}
					}
					else{
						totalPips+=diff*0.1;
						lostPips+=-diff*0.1;
						losses++;
						if (debug){
							System.out.println("[CLOSE LOSE] "+DateUtils.datePrint(cal1)
									+" "+q1.getOpen5()
									+" "+PrintUtils.Print2dec(diff*0.1,false)
									+" || "+PrintUtils.Print2dec(totalPips,false)
									);
						}
					}
					
					//System.out.println(PrintUtils.Print2(totalPips, false));
					if (actualTrades>=maxTradesDay){
						canTrade = false;
					}
				}
			}
		}
		
		QuoteShort q0 = data.get(begin);
		QuoteShort.getCalendar(cal, q0);
		String dateBegin = DateUtils.datePrint(cal);
		QuoteShort qn = data.get(end);
		QuoteShort.getCalendar(cal, qn);
		String dateEnd = DateUtils.datePrint(cal);
		
		int total = wins+losses;
		double perWin = wins*100.0/total;
		double avgPips = avg*0.1/total;
		double avgPipsRisk = totalPipRisk*0.1/total;
		System.out.println(
				header
				+" "+dateBegin+" "+dateEnd
				+" "+h1+" "+h2
				+" "+maxMinThr
				+" "+maxTime
				+" "+offset
				+" "+PrintUtils.Print2dec(stopSize, false)
				+" "+maxTradesDay
				+" || "+total
				+" "+PrintUtils.Print2dec(perWin, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(winPips/lostPips, false)
				+" || "+PrintUtils.Print2dec(avgPipsRisk, false)
				+" "+PrintUtils.Print2dec(avgPips/avgPipsRisk, false)
				);
	}
	
	public static void test(String header,ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,int begin,int end,
			int h1,int h2,
			int maxMinThr,int maxTime,int offset,
			int maxTradesDay,
			boolean debug
			){
		
		if (end>=data.size()-1)
			end = (data.size()-1)-maxTime-1-offset;
        
		double totalPips = 0;
		double winPips = 0;
		double lostPips = 0;
		double avg=0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int actualTrades = 0;
		boolean canTrade = true;
		for (int i=begin;i<=end ;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+offset);
			QuoteShort qEnd = data.get(i+1+maxTime-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (h<h1 || h>h2) continue;
			
			if (day!=lastDay){
				actualTrades = 0;
				canTrade = true;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i).getExtra();
			int iOpen = i+1;
			boolean closed = false;
			double diff = 0;
			if (canTrade){
				if (maxMin>=maxMinThr && q1.getOpen5()<q.getHigh5()){
					//QuoteShort highLow = TradingUtils.getMaxMinShort(data, iOpen, iOpen+maxTime+1);
					diff = qEnd.getClose5()-q1.getOpen5();
					avg+=diff;
					closed = true;
					actualTrades++;
				}else if (maxMin<=-maxMinThr && q1.getOpen5()>q.getLow5()){
					//QuoteShort highLow = TradingUtils.getMaxMinShort(data, iOpen, iOpen+maxTime+1);
					diff = q1.getOpen5()-qEnd.getClose5();
					avg+=diff;
					closed = true;
					actualTrades++;
				}
				if (closed){
					//canTrade = false;
					if (diff>=0){
						totalPips+=diff*0.1;
						wins++;
						winPips+=diff*0.1;
						if (debug){
							System.out.println("[CLOSE WIN] "+DateUtils.datePrint(cal1)
									+" "+q1.getOpen5()
									+" "+PrintUtils.Print2dec(diff*0.1,false)
									+" || "+totalPips
									);
						}
					}
					else{
						totalPips+=diff*0.1;
						lostPips+=-diff*0.1;
						losses++;
						if (debug){
							System.out.println("[CLOSE LOSE] "+DateUtils.datePrint(cal1)
									+" "+q1.getOpen5()
									+" "+PrintUtils.Print2dec(diff*0.1,false)
									+" || "+totalPips
									);
						}
					}
					if (actualTrades>=maxTradesDay){
						canTrade = false;
					}
				}
			}
		}
		
		QuoteShort q0 = data.get(begin);
		QuoteShort.getCalendar(cal, q0);
		String dateBegin = DateUtils.datePrint(cal);
		QuoteShort qn = data.get(end);
		QuoteShort.getCalendar(cal, qn);
		String dateEnd = DateUtils.datePrint(cal);
		
		int total = wins+losses;
		double perWin = wins*100.0/total;
		double avgPips = avg*0.1/total;
		System.out.println(
				header
				+" "+dateBegin+" "+dateEnd
				+" "+h1+" "+h2
				+" "+maxMinThr
				+" "+maxTime
				+" "+offset
				+" "+maxTradesDay
				+" || "+total
				+" "+PrintUtils.Print2dec(perWin, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(winPips/lostPips, false)
				);
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> currencies = new ArrayList<String>();
		currencies.add("eurusd");
		currencies.add("gbpusd");
		currencies.add("audusd");
		currencies.add("usdjpy");
		//currencies.add("eurjpy");
		//currencies.add("gbpjpy");
		//currencies.add("audjpy");
		//currencies.add("nzdusd");
		
		int index=0;
		int limit=currencies.size()-1;
		limit=0;
		for (int c=index;c<=limit;c++){
			String currency = currencies.get(c);
			String	path1 = "c:\\fxdata\\"+currency+"_UTC_5 Mins_Bid_2003.12.31_2015.08.20.csv";
			String header = currency;
			ArrayList<String> files = new ArrayList<String>();
			files.add(path1);
			
			for (int i=0;i<files.size();i++){
				String path = files.get(i);
				ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
				ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
				//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
				ArrayList<QuoteShort> data = null;
				dataI.clear();
				dataS.clear();
				data5m.clear();
				data = data5mS;
				//System.out.println("total data: "+data.size());
				int begin1=300000;
				int end = data.size()-1;
				ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
				for (int begin=begin1;begin<=data.size()-1;begin+=9990000){
					end = begin + 8990000;
					for (int h1=0;h1<=0;h1++){
						int h2=h1+23;
						for (int maxMinThr=20000;maxMinThr<=20000;maxMinThr+=1000){
							for (int maxTime=40000;maxTime<=40000;maxTime+=1000){
								for (int offset=1;offset<=1;offset+=1){
									for (double stopSize=1.0;stopSize<=1.0;stopSize+=1.0){
										for (int maxTradesDay=1;maxTradesDay<=1;maxTradesDay+=1){
											TestMomentum.test(currency,data5mS,  maxMinsExt, begin, end,h1,h2, maxMinThr, maxTime,offset,maxTradesDay,false);
											//TestMomentum.testWithStop(header,data5mS,  maxMinsExt, begin, end,h1,h2, maxMinThr, maxTime,offset,maxTradesDay,
													//stopSize,false);
											//TestMomentum.testWithProfit(header,data5mS,  maxMinsExt, begin, end,h1,h2, maxMinThr, maxTime,offset,maxTradesDay,
													//stopSize,false);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
	}

}
