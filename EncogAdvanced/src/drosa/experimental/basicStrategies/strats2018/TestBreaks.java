package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.basicStrategies.MaxMinConfig;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestBreaks {
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int minPips,
			int ahbreak1,
			int ahbreak2,
			int debug) {
		

		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
	
		int lastDay = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int high = -1;
		int low = -1;
		int touched = 0;
		int trades = 0;
		int wins = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int accLossesClosed = 0;
		int hbreak = -1;
		for (int i=0;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (day!=lastDay){
				
				int touchedabs = Math.abs(touched);
				boolean reset = true;
				if (touchedabs>=1){
					if (hbreak>=ahbreak1 && hbreak<=ahbreak2
						){
						trades++;
						if (touchedabs>=2){
							wins++;
							actualLosses = 0;
						}else{
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							
							if (touched>0){
								accLossesClosed += lastHigh-q.getOpen5();
							}else if (touched<0){
								accLossesClosed += q.getOpen5()-lastLow;
							}
						}
					}
					reset = true;
				}
				
				if (reset){
					touched = 0;
					lastHigh = high;
					lastLow = low;
				}
				high = -1;
				low = -1;
				hbreak=-1;
				lastDay = day;
			}
			
			if (lastHigh!=-1 && touched==0){
				
				if (q.getHigh5()>=lastHigh){
					touched=1;
					hbreak = h;
				}else if (q.getLow5()<=lastLow){
					touched=-1;
					hbreak = h;
				}
			}
			
			if (touched==1){
				if (q.getHigh5()>=lastHigh+10*minPips){
					touched=2;
				}
			}else if (touched==-1){
				if (q.getLow5()<=lastLow-10*minPips){
					touched = -2;
				}
			}
			
			if (q.getHigh5()>=high || high==-1){
				high = q.getHigh5();
			}
			if (q.getLow5()<=low || low==-1){
				low = q.getLow5();
			}
		}
		
		
		double winPer = wins*100.0/trades;
		int losses = trades-wins;
		double avgClosed = accLossesClosed*0.1/losses;
		double pf = wins*minPips*1.0/(avgClosed*losses);
		
		double avg = (wins*minPips-avgClosed*losses)*1.0/trades;
		System.out.println(
				minPips
				+" "+ahbreak1+" "+ahbreak2
				+" || "
				+" "+trades
				+" "+wins
				+" "+(trades-wins)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" || "+maxLosses
				+" "+PrintUtils.Print2dec(accLossesClosed*0.1/losses, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				);
	}

	
	public static void doTest2(
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int minPips,
			int slPips,
			int ahbreak1,
			int ahbreak2,
			int debug) {
		

		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
	
		int lastDay = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int high = -1;
		int low = -1;
		int touched = 0;
		int trades = 0;
		int wins = 0;
		int losses = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int accLossesClosed = 0;
		int hbreak = -1;
		int totalDays = 0;
		ArrayList<Integer> lossesArr = new ArrayList<Integer>();
		ArrayList<Integer> tradesArr = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (day!=lastDay){
				
				int touchedabs = Math.abs(touched);
				boolean reset = true;
				if (touchedabs>=1){
					reset = true;
					if (hbreak>=ahbreak1 && hbreak<=ahbreak2
						){
						reset = false;//ha habido break, nos esperamos a saber...
						trades++;
						if (touchedabs==2){
							wins++;
							//if (actualLosses>0)
								//lossesArr.add(actualLosses);
							actualLosses = 0;
							reset = true;//reset por win
							tradesArr.add(1);
						}else if (touchedabs==3){
							actualLosses++;
							tradesArr.add(-1);
							if (actualLosses>=maxLosses) maxLosses = actualLosses;							
							if (touched>0){
								accLossesClosed += slPips;
								losses++;								
							}else if (touched<0){
								accLossesClosed += slPips;
								losses++;
							}
							reset = true;//reset por loss
						}
					}
				}
				
				if (reset){
					touched = 0;
					lastHigh = high;
					lastLow = low;

					hbreak=-1;
				}
				totalDays++;
				high = -1;
				low = -1;
				lastDay = day;
			}
			
			if (lastHigh!=-1 && touched==0){
				
				if (q.getHigh5()>=lastHigh){
					touched=1;
					hbreak = h;
				}else if (q.getLow5()<=lastLow){
					touched=-1;
					hbreak = h;
				}
			}
			
			if (touched==1){
				if (q.getHigh5()>=lastHigh+10*minPips){
					touched=2;
				}else if (q.getLow5()<=lastHigh-10*slPips){
					touched = 3;
				}				
			}else if (touched==-1){
				if (q.getLow5()<=lastLow-10*minPips){
					touched = -2;
				}else if (q.getHigh5()>=lastLow+10*slPips){
					touched = -3;
				}	
			}
			
			if (q.getHigh5()>=high || high==-1){
				high = q.getHigh5();
			}
			if (q.getLow5()<=low || low==-1){
				low = q.getLow5();
			}
		}
		
		trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avgClosed = accLossesClosed*1.0/losses;
		double pf = wins*minPips*1.0/(avgClosed*losses);
		
		double avg = (wins*minPips-avgClosed*losses)*1.0/trades;
		
		double avgTrends = MathUtils.average(tradesArr);
		
		int count3=0;
		int rachaSize = maxLosses;
		int actualRacha=0;
		for (int i=0;i<tradesArr.size();i++){
			int trade = tradesArr.get(i);
			
			if (trade==-1) actualRacha++;
			else actualRacha = 0;
			
			if (actualRacha>=rachaSize){
				actualRacha = 0;
				count3++;
			}
		}
		System.out.println(
				minPips+" "+slPips
				+" "+ahbreak1+" "+ahbreak2
				+" || "
				+" "+totalDays
				+" "+trades
				+" "+wins
				+" "+(trades-wins)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" || "+maxLosses
				+" "+PrintUtils.Print2dec(accLossesClosed*1.0/losses, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgTrends, false)
				+" || c3= "+count3
				);
	}
	
	
	public static void doTest3(
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			ArrayList<Integer> maxMins,
			int thr,
			int minPips,
			int slPips,
			int ahbreak1,
			int ahbreak2,
			boolean isReverse,
			int debug) {
		

		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
	
		int lastDay = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int high = -1;
		int low = -1;
		int touched = 0;
		int trades = 0;
		int wins = 0;
		int losses = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int accLossesClosed = 0;
		int hbreak = -1;
		int totalDays = 0;
		ArrayList<Integer> lossesArr = new ArrayList<Integer>();
		ArrayList<Integer> tradesArr = new ArrayList<Integer>();
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (day!=lastDay){
				
				int touchedabs = Math.abs(touched);
				boolean reset = true;
				if (touchedabs>=1){
					reset = true;
					if (hbreak>=ahbreak1 && hbreak<=ahbreak2
						){
						reset = false;//ha habido break, nos esperamos a saber...
						trades++;
						if (touchedabs==2){
							wins++;
							//if (actualLosses>0)
								//lossesArr.add(actualLosses);
							actualLosses = 0;
							reset = true;//reset por win
							tradesArr.add(1);
						}else if (touchedabs==3){
							actualLosses++;
							tradesArr.add(-1);
							if (actualLosses>=maxLosses) maxLosses = actualLosses;							
							if (touched>0){
								accLossesClosed += slPips;
								losses++;								
							}else if (touched<0){
								accLossesClosed += slPips;
								losses++;
							}
							reset = true;//reset por loss
						}
					}
				}
				
				if (reset){
					touched = 0;
					lastHigh = high;
					lastLow = low;

					hbreak=-1;
				}
				totalDays++;
				high = -1;
				low = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
			
			if (lastHigh!=-1 && touched==0 
					&& h>=ahbreak1 && h<=ahbreak2
					){
				
				if (maxMin>=thr){
					touched=1;
					hbreak = h;
					lastHigh = q.getOpen5();
				}else if (maxMin<=-thr){
					touched=-1;
					hbreak = h;
					lastLow = q.getOpen5();
				}
			}
			
			if (touched==1){
				if (!isReverse){
					if (q.getHigh5()>=lastHigh+10*minPips){
						touched=2;
					}else if (q.getLow5()<=lastHigh-10*slPips){
						touched = 3;
					}		
				}else{
					if (q.getLow5()<=lastHigh-10*minPips){
						touched=2;
					}else if (q.getHigh5()>=lastHigh+10*slPips){
						touched = 3;
					}	
				}
			}else if (touched==-1){
				if (!isReverse){
					if (q.getLow5()<=lastLow-10*minPips){
						touched = -2;
					}else if (q.getHigh5()>=lastLow+10*slPips){
						touched = -3;
					}
				}else{
					if (q.getHigh5()>=lastLow+10*minPips){
						touched=2;
					}else if (q.getLow5()<=lastLow-10*slPips){
						touched = 3;
					}	
				}
			}
			
			if (q.getHigh5()>=high || high==-1){
				high = q.getHigh5();
			}
			if (q.getLow5()<=low || low==-1){
				low = q.getLow5();
			}
		}
		
		trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avgClosed = accLossesClosed*1.0/losses;
		double pf = wins*minPips*1.0/(avgClosed*losses);
		
		double avg = (wins*minPips-avgClosed*losses)*1.0/trades;
		
		double avgTrends = MathUtils.average(tradesArr);
		
		int count3=0;
		int rachaSize = maxLosses;
		int actualRacha=0;
		for (int i=0;i<tradesArr.size();i++){
			int trade = tradesArr.get(i);
			
			if (trade==-1) actualRacha++;
			else actualRacha = 0;
			
			if (actualRacha>=rachaSize){
				actualRacha = 0;
				count3++;
			}
		}
		System.out.println(
				thr+" "
				+" "+minPips+" "+slPips
				+" "+ahbreak1+" "+ahbreak2
				+" || "
				+" "+totalDays
				+" || "+trades
				+" "+wins
				+" "+(trades-wins)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" || "+maxLosses
				+" "+PrintUtils.Print2dec(accLossesClosed*1.0/losses, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgTrends, false)
				+" || c3= "+count3
				);
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path0 ="C:\\fxdata\\";
		//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
		
		String pathEURUSD = path0+"eurusd_UTC_15 Mins_Bid_2003.12.31_2018.02.21.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_15 Secs_Bid_2010.12.31_2018.01.26.csv";
		
		String pathNews = path0+"News.csv";
		
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
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		//FFNewsClass.readNews(pathNews,news,0);
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int y1=2009;y1<=2009;y1++){
				int y2 = y1+9;
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+23;
					for (int minPips=100;minPips<=100;minPips+=5){
						for (int slPips=100;slPips<=3000;slPips+=100){
					//for (int slPips=5;slPips<=100;slPips+=5){
						//for (int minPips=1*slPips;minPips<=1*slPips;minPips+=1*slPips){
							//TestBreaks.doTest2("", data, y1, y2, minPips,slPips,h1,h2, 0);
							for (int thr=0;thr<=0;thr+=100){
								TestBreaks.doTest3("", data, y1, y2,maxMins,thr, minPips,slPips,h1,h2,true, 0);
							}
						}
					}
				}
			}
		}

	}

}
