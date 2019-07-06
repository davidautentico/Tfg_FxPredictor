package drosa.experimental.martingale;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.billyt.TestDailyBreakout;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMartingale {
	
	public static void tradingDO(ArrayList<QuoteShort> data,
			int begin,int end,
			int aYear,
			int minPips,int minDistance,int sl,boolean debug){
	
		int totalWins = 0;
		int totalLosses = 0;
		int totalWinPips = 0;
		int totalLostPips = 0;
		int lastResult=-1;
		int actualWinsStreak=0;
		int actualLossesStreak=0;
		int maxLossesStreak=0;
		int maxWinsStreak=0;
		
		int lastDay = -1;
		int actualMax= -1;
		int actualMin = -1;
		int DOValue = -1;
		boolean isTradeOpen = false;
		Calendar cal = Calendar.getInstance();
		int entry = -1;
		int slValue = -1;
		int tpValue = -1;
		PositionType positionType = PositionType.NONE;
		for (int i=begin;i<end;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int year = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//if (year!=aYear) continue;
			
			//determinar si estamos en nuevo dia
			if (day!=lastDay){
				if (debug)
					System.out.println("****[NEW DAY] "+DateUtils.datePrint(cal)+" "+q.getOpen5()+" || "+actualMax+" "+actualMin);
				DOValue = q.getOpen5();
				actualMin = -1;
				actualMax = -1;
				lastDay = day;
			}
			
			int diffDOH = actualMax-DOValue;
			int diffDOL = DOValue- actualMin;
			int diffActual = Math.abs(q.getOpen5()-DOValue);
			//System.out.println(actualMax+" "+actualMin+" || "+diffDOH+" "+diffDOL+" "+diffActual);
			if (!isTradeOpen
					&& h>=0 && h<=9
					&& DOValue!=-1
					&& diffActual>=minDistance*10
					){
				entry = -1;
				positionType = PositionType.NONE;
				if (q.getOpen5()<DOValue && diffDOH<minPips*10){//entronces long
					entry = q.getOpen5();
					slValue = entry-sl*10;
					tpValue = DOValue+minPips*10;
					positionType = PositionType.LONG;
				}else if (q.getOpen5()>DOValue && diffDOL<minPips*10){ //entonces short
					entry = q.getOpen5();
					slValue = entry+sl*10;
					tpValue = DOValue-minPips*10;
					positionType = PositionType.SHORT;
				}
				if (entry!=-1){
					isTradeOpen = true;
					if (debug)
						System.out.println("[OPEN] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+i);
				}
			}
			
			if (isTradeOpen){
				boolean closed = false;
				boolean isSL = false;
				int earnedPips = 0;
				if (positionType==PositionType.LONG){
					if (q.getLow5()<=slValue){
						isSL = true;
						closed = true;
						isTradeOpen = false;
						earnedPips = (int) -((entry-slValue)*0.1);
						if (debug)
							System.out.println("[CLOSE SL] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+earnedPips+" || "+i);
					}else if (q.getHigh5()>=tpValue){
						isSL = false;
						closed = true;
						isTradeOpen = false;
						earnedPips = (int) ((tpValue-entry)*0.1);
						if (debug)
							System.out.println("[CLOSE TP] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+earnedPips+" || "+i);
					}
				}else if (positionType==PositionType.SHORT){
					if (q.getHigh5()>=slValue){
						isSL = true;
						closed = true;
						isTradeOpen = false;
						earnedPips = (int) -((slValue-entry)*0.1);
						if (debug)
							System.out.println("[CLOSE SL] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+earnedPips+" || "+i);
					}else if (q.getLow5()<=tpValue){
						isSL = false;
						closed = true;
						isTradeOpen = false;
						earnedPips = (int) ((entry-tpValue)*0.1);
						if (debug)
							System.out.println("[CLOSE TP] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+earnedPips+" || "+i);
					}
				}
				
				if (closed){
					if (isSL){
						//System.out.println("0");
						//results+=" 0";
						if (lastResult==0){
							actualLossesStreak++;
						}else if (lastResult==1){
							actualLossesStreak = 1;
							if (actualWinsStreak>=maxWinsStreak){
								maxWinsStreak = actualWinsStreak;
							}
						}
						lastResult = 0;
						totalLosses++;
						totalLostPips+=Math.abs(earnedPips);
					}
					else{
						if (lastResult==1){
							actualWinsStreak++;
						}else if (lastResult==0){
							actualWinsStreak = 1;
							if (actualLossesStreak>=maxLossesStreak){
								maxLossesStreak = actualLossesStreak;
							}
						}
						lastResult = 1;
						totalWins++;
						totalWinPips+=Math.abs(earnedPips);
					}
					isTradeOpen = false;
				}//closed
			}//tradeOpen
			
			if (actualMax==-1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
			if (actualMin==-1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
		}
		
		int total = totalWins+totalLosses;
		double avg = (totalWinPips-totalLostPips)*0.1/total;
		
		System.out.println(total+" || "+PrintUtils.Print2(avg));
	}
	
	public static void alwaysInMarketNoFilters(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMinsExt,
			int begin,int end,
			int aYear, 
			int h1,int h2,
			int tp,int sl,
			int thr,
			boolean debug){
		
		int totalWins = 0;
		int totalLosses = 0;
		int lastResult=-1;
		int actualWinsStreak=0;
		int actualLossesStreak=0;
		int maxLossesStreak=0;
		int maxWinsStreak=0;
		
		
		String results = "";
		boolean isPositionOpen = false;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		int entry = -1;
		int slValue = -1;
		int tpValue = -1;
		PositionType positionType = PositionType.NONE;
		for (int i=begin;i<end;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int year = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//if (year!=aYear) continue;
			
			//determinar si estamos en nuevo dia
			if (day!=lastDay){
				lastDay = day;
				if (debug)
					System.out.println("****[NEW DAY] "+DateUtils.datePrint(cal));
			}
			
			int maxMin = maxMinsExt.get(i-1).getExtra();//el anterior
			if (!isPositionOpen && h>=h1 && h<=h2){
				entry = -1;
				if (maxMin>=thr){
					entry = q.getOpen5();
					tpValue = entry+tp*10;
					slValue = entry-sl*10;
					positionType=PositionType.LONG;
				}else if (maxMin<=-thr){
					entry = q.getOpen5();
					tpValue = entry-tp*10;
					slValue = entry+sl*10;
					positionType=PositionType.SHORT;
				}
				if (entry!=-1){
					isPositionOpen = true;
					if (debug)
						System.out.println("[OPEN] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+i);
				}
			}
			
			if (isPositionOpen){
				boolean closed = false;
				boolean isSL = false;
				if (positionType==PositionType.LONG){
					if (q.getLow5()<=slValue){
						isSL = true;
						closed = true;
						isPositionOpen = false;
						if (debug)
							System.out.println("[CLOSE SL] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+i);
					}else if (q.getHigh5()>=tpValue){
						isSL = false;
						closed = true;
						isPositionOpen = false;
						if (debug)
							System.out.println("[CLOSE TP] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+i);
					}
				}else if (positionType==PositionType.SHORT){
					if (q.getHigh5()>=slValue){
						isSL = true;
						closed = true;
						isPositionOpen = false;
						if (debug)
							System.out.println("[CLOSE SL] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+i);
					}else if (q.getLow5()<=tpValue){
						isSL = false;
						closed = true;
						isPositionOpen = false;
						if (debug)
							System.out.println("[CLOSE TP] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+i);
					}
				}
				
				if (closed){
					if (isSL){
						//System.out.println("0");
						//results+=" 0";
						if (lastResult==0){
							actualLossesStreak++;
						}else if (lastResult==1){
							actualLossesStreak = 1;
							if (actualWinsStreak>=maxWinsStreak){
								maxWinsStreak = actualWinsStreak;
							}
						}
						lastResult = 0;
						totalLosses++;
						if (positionType==PositionType.LONG) positionType=PositionType.SHORT;
						else positionType=PositionType.LONG;
					}
					else{
						
						if (lastResult==1){
							actualWinsStreak++;
						}else if (lastResult==0){
							actualWinsStreak = 1;
							if (actualLossesStreak>=maxLossesStreak){
								maxLossesStreak = actualLossesStreak;
							}
						}
						lastResult = 1;
						
						//if (positionType==PositionType.LONG) positionType=PositionType.SHORT;
						//else positionType=PositionType.LONG;
						
						totalWins++;
					}
				}
			}
		}//data
		int total = totalWins+totalLosses;
		double winPer = totalWins*100.0/total;
		double avgPips = (totalWins*tp-totalLosses*sl)*1.0/total;
		System.out.println(tp+" "+sl+" || "+total+" "+maxWinsStreak+" "+maxLossesStreak
				+" "+PrintUtils.Print2(winPer)
				+" "+PrintUtils.Print2(avgPips)
				);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2004.01.01_2015.08.15.csv";
		
		String path = pathEURUSD;
		ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
		ArrayList<QuoteShort> data = null;
		dataI.clear();
		dataS.clear();
		data5m.clear();
		data = data5mS;
		//System.out.println("data: "+data.size());
		int begin = 1;
		int end = data.size();
		ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
		
		int total = 0;
		int wins= 0;
		for (int i=0;i<dailyData.size();i++){
			QuoteShort q = dailyData.get(i);
			int hdiff = q.getHigh5()-q.getOpen5();
			int ldiff = q.getOpen5()-q.getLow5();
			if (hdiff>=5*10 && ldiff>=5*10) wins++;
			total++;
		}
		double winPer = wins*100.0/total;
		System.out.println(total+" "+PrintUtils.Print2dec(winPer, false));
		
		for (int minDistance=10;minDistance<=300;minDistance+=5)
			TestMartingale.tradingDO(data, begin, end, 2015, 5, minDistance, minDistance, false);
		
		/*int tp = 20;
		int sl = 20;
		for (tp=5;tp<=5;tp+=100){
			//for (sl=1*tp;sl<=1*tp;sl++){
			for (sl=200;sl<=200;sl++){
				for (int year=2015;year<=2015;year++)
					for (int thr=0;thr<=0;thr+=10)
						for (int h1=0;h1<=23;h1++){
							int h2 = h1;
							TestMartingale.alwaysInMarketNoFilters(data,maxMinsExt, begin, end,year,h1,h2,tp,sl,thr,false);
						}
			}
		}*/
		
		
	}

}
