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
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class PullBackFeb18 {
	
	public static int doTestTrading5(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr1,int thr2,
			int tp,int sl,
			boolean isReverse,
			int debug) {
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winPips = 0;
		int lostPips = 0;
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int lastMonth = -1;
		int totalMonths = 0;
		int winMonths = 0;
		int winsM = 0;
		int lossM = 0;
		int countMonths = 0;
		
		int mode = 0;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (y<y1 || y>y2) continue;
			
			int maxMin = maxMins.get(i-1);
			

			if (maxMin>=thr1){
				mode = 1;//MODO LONG
			}else if (maxMin<=-thr1){
				mode = -1;//MODO SHORT
			}
			
			int entry = -1;
			int tpValue = -1;
			int slValue = -1;
			if (mode==1){				
				if (maxMin<=-thr2
						&& h>=h1 && h<=h2
						){
					//trade
					entry = q.getOpen5();
					tpValue = entry+10*tp;
					slValue = entry-10*sl;
					if (isReverse){
						tpValue = entry-10*tp;
						slValue = entry+10*sl;
					}
					
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, entry, tpValue, slValue, false);
					
					int pips = qm.getClose5()-entry;
					if (isReverse){
						pips = entry-qm.getClose5();
					}
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}					
				}				
			}else if (mode==-1){
				if (maxMin>=thr2
						&& h>=h1 && h<=h2
						){
					//trade
					entry = q.getOpen5();
					tpValue = entry-10*tp;
					slValue = entry+10*sl;
					if (isReverse){
						tpValue = entry+10*tp;
						slValue = entry-10*sl;
					}
					
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, entry, tpValue, slValue, false);
					
					int pips = entry-qm.getClose5();
					if (isReverse){
						pips = qm.getClose5()-entry;
					}
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}		
				}
			}	
		}//for
		
		int totalTrades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/totalTrades;
		double avg = (winPips-lostPips)*0.1/totalTrades;
		
		System.out.println(
				thr1+" "+thr2
				+" "+tp+" "+sl
				+" || "
				+" "+totalTrades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
		return -1;		
	}

	
	public static int doTestTrading5(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr1,int thr2,
			int tp,int sl,
			boolean isReverse,
			int debug) {
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		QuoteShort qm = new QuoteShort();		
		int trades = 0;
		int wins=0;
		int losses=0;
		int winPips = 0;
		int lostPips = 0;
		int winsYear = 0;
		int lossYear = 0;
		int tradesYear = 0;
		int countYears = 0;
		int lastYear = -1;
		int lastMonth = -1;
		int totalMonths = 0;
		int winMonths = 0;
		int winsM = 0;
		int lossM = 0;
		int countMonths = 0;
		
		int mode = 0;
		for (int i=1;i<data.size();i++) {
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min= cal.get(Calendar.MINUTE);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int month = cal.get(Calendar.MONTH);
			
			if (y<y1 || y>y2) continue;
			
			int maxMin = maxMins.get(i-1);
			

			if (maxMin>=thr1){
				mode = 1;//MODO LONG
			}else if (maxMin<=-thr1){
				mode = -1;//MODO SHORT
			}
			
			int entry = -1;
			int tpValue = -1;
			int slValue = -1;
			if (mode==1){				
				if (maxMin<=-thr2
						&& h>=h1 && h<=h2
						){
					//trade
					entry = q.getOpen5();
					tpValue = entry+10*tp;
					slValue = entry-10*sl;
					if (isReverse){
						tpValue = entry-10*tp;
						slValue = entry+10*sl;
					}
					
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, entry, tpValue, slValue, false);
					
					int pips = qm.getClose5()-entry;
					if (isReverse){
						pips = entry-qm.getClose5();
					}
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}					
				}				
			}else if (mode==-1){
				if (maxMin>=thr2
						&& h>=h1 && h<=h2
						){
					//trade
					entry = q.getOpen5();
					tpValue = entry-10*tp;
					slValue = entry+10*sl;
					if (isReverse){
						tpValue = entry+10*tp;
						slValue = entry-10*sl;
					}
					
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, cal, i, data.size()-1, entry, tpValue, slValue, false);
					
					int pips = entry-qm.getClose5();
					if (isReverse){
						pips = qm.getClose5()-entry;
					}
					if (pips>=0){
						wins++;
						winPips += pips;
					}else{
						losses++;
						lostPips += -pips;
					}		
				}
			}	
		}//for
		
		int totalTrades = wins+losses;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/totalTrades;
		double avg = (winPips-lostPips)*0.1/totalTrades;
		
		System.out.println(
				thr1+" "+thr2
				+" "+tp+" "+sl
				+" || "
				+" "+totalTrades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
		return -1;		
	}

	public static void main(String[] args) throws Exception {
		String path0 ="C:\\fxdata\\";
		//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
		
		String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.12.31_2018.04.10.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_30 Mins_Bid_2003.12.31_2018.02.22.csv";
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
			
			System.out.println(data.size()+" "+maxMins.size());
			for (int y1=2004;y1<=2018;y1++) {
				int y2 = y1+0;
				for (int thr1=1000;thr1<=1000;thr1+=1000){	
					for (int thr2=500;thr2<=500;thr2+=50){		
						for (int tp=9;tp<=9;tp+=1){	
							for (int sl=36;sl<=36;sl+=10){		
								for (int h1=0;h1<=0;h1++){
									int h2 = h1+9;
									PullBackFeb18.doTestTrading5("", data, maxMins, y1, y2, h1, h2, thr1, thr2, tp, sl,false, 0);
								}
							}
						}
					}
				}
			}
			
		}

	}

}
