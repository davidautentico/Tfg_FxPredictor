package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class MicroStructure {
	
	public static void doTest(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int href1,int href2,
			int thr,int aTest,int aTail,int offset,
			int diffHours
			){
		
		
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
		int comm=00;
		double balanceInicial = 50000;
		double balance = balanceInicial;
		double maxBalance = balance;
		double maxDD = 0;
		double risk = 1.0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		double probU = 50.0;
		double probD = 50.0;
		
		int lastWeek = -1;
		int refPoint = -1;
		int lastDayTrading = -1;
		QuoteShort q = null;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		double range = 4000;
		int high = -1;
		int low = -1;
		double frange = 4000;
		int lastTradeRef = -1;
		int dayLongs = 0;
		int dayShorts = 0;
		
		ArrayList<Integer> winPipsYear = new ArrayList<Integer>();
		ArrayList<Integer> lostPipsYear = new ArrayList<Integer>();
		ArrayList<Integer> winYear = new ArrayList<Integer>();
		ArrayList<Integer> lostYear = new ArrayList<Integer>();
		for (int i=0;i<=50;i++){
			winPipsYear.add(0);
			lostPipsYear.add(0);
			winYear.add(0);
			lostYear.add(0);
		}
		int cases = 0;
		int accDistance = 0;
		int accBars = 0;
		ArrayList<Integer> distancesArr = new ArrayList<Integer>();
		ArrayList<Integer> barsArr = new ArrayList<Integer>();
		ArrayList<Integer> distancesHCArr = new ArrayList<Integer>();
		for (int i=400;i<data.size()-1;i++){
			QuoteShort q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
						
			q = data.get(i);			
			days.add(q.getOpen5());
						
			if (day!=lastDay){
				isTrade=0;
				lastDay = day;
				totalDays++;
				dayLongs = 0;
				dayShorts = 0;
			}
						
			int maxMin = maxMins.get(i-offset);
			if (h>=href1 && h<=href2){
				int diff = q.getClose5()-data.get(i-288).getClose5();
				if (maxMin>=thr
						//&& diff>=diffHours
						){
					high = q.getHigh5()+aTail;
					int maxDistance = 0;
					int bars = 0;
					for (int j=i+1;j<data.size()-1;j++){
						QuoteShort qj = data.get(j);
						int distance = q.getClose5()-qj.getLow5();
						bars++;
						if (distance>=maxDistance){
							maxDistance = distance;
						}else if (qj.getHigh5()>=high
								|| maxDistance>=aTest+comm
								){	
							break;
						}
					}
					cases++;
					distancesArr.add(maxDistance);
					barsArr.add(bars);

					distancesHCArr.add(high-q.getClose5());
				}else if (maxMin<=-thr
						//&& diff<=-diffHours
						){
					low = q.getLow5()-aTail;
					int maxDistance = 0;
					int bars = 0;
					for (int j=i+1;j<data.size()-1;j++){
						QuoteShort qj = data.get(j);
						int distance = qj.getHigh5()-q.getClose5();						
						bars++;
						if (distance>=maxDistance){
							maxDistance = distance;
						}else if (qj.getLow5()<=low
								|| maxDistance>=aTest+comm
								){	
							
							break;
						}
					}
					cases++;
					distancesArr.add(maxDistance);
					barsArr.add(bars);
					distancesHCArr.add(q.getClose5()-low);
				}
			}
		}
		
		double avgD = MathUtils.average(distancesArr);
		double avgB = MathUtils.average(barsArr);
		
	
		int acc5=0;
		int acchc = 0;
		for (int i=0;i<distancesArr.size();i++){
			int d = distancesArr.get(i);
			if (d>=aTest+comm) acc5++;
			acchc  += distancesHCArr.get(i);
		}
		
		double per5 = acc5*100.0/distancesArr.size();
		double per5c = 100.0-per5;
		double avgStop = (acchc+comm)*1.0/distancesHCArr.size();
		double pf = aTest*per5/((100.0-per5)*avgStop);
		double pfr = 1.0/pf;
		
		double avg = ((per5*aTest)-(per5c*avgStop))*0.1/100.0; 
		//if (pf>=1.3 && avg>=3.8)
		System.out.println(
				href1+" "+href2+" "+thr+" "+aTest+" "+aTail
				+" || "
				+" "+cases
				+" "+PrintUtils.Print2dec(avgD, false)
				+" "+PrintUtils.Print2dec(avgB, false)
				+" || "+PrintUtils.Print2dec(per5, false)
				+" || "+PrintUtils.Print2dec(avgStop, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(pfr, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\EURUSD_5 Mins_Bid_2004.01.01_2018.08.27.csv";
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
			
			for (int y1=2004;y1<=2004;y1+=1){
				int y2 = y1+14;
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+0;
					for (int href=0;href<=0;href++){
						int href2 = href+9;
						for (int thr=400;thr<=400;thr+=10){
							for (int aTest=100;aTest<=4000;aTest+=100){
								for (int aTail=200;aTail<=200;aTail+=100)
									for (int offset=0;offset<=0;offset+=1){
										for (int diffHours=0;diffHours<=0;diffHours+=100){
											MicroStructure.doTest(data, maxMins, y1, y2, 0,11, 
													h1, h2,href,href2, thr,aTest,
													aTail,offset,diffHours
											);
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
