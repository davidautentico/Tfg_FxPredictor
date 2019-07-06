package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestVolatility {
	
	
	public static void doTest(
			String header,
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			double per,
			int bbars,
			double ftp,
			double fsl,
			int debug,
			boolean isMomentum,
			boolean printAlways
			){
		

		Calendar cal = Calendar.getInstance();
		
		int comm = 20;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		ArrayList<Long> yearWinPips = new ArrayList<Long>();
		ArrayList<Long> yearLostPips = new ArrayList<Long>();
		int lastYear = -1;
		for (int i=0;i<=(y2-y1)+1;i++){
			yearWinPips.add(0L);
			yearLostPips.add(0L);
		}
		ArrayList<Long> mWinPips = new ArrayList<Long>();
		ArrayList<Long> mLostPips = new ArrayList<Long>();
		ArrayList<Long> mWinPipsO = new ArrayList<Long>();
		ArrayList<Long> mLostPipsO = new ArrayList<Long>();
		int mYear = -1;
		for (int i=0;i<=(y2-y1)*12+11;i++){
			mWinPips.add(0L);
			mLostPips.add(0L);
			mWinPipsO.add(0L);
			mLostPipsO.add(0L);
		}
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastDay = -1;
		int doValue = -1;
		int mode = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int range = 800;
		ArrayList<Integer> closeArr = new ArrayList<Integer>();
		for (int i=0;i<data.size()-1;i++){
			closeArr.add(data.get(i).getClose5());
		}
		int y = y1;
		ArrayList<Integer> rangeArr = new ArrayList<Integer>();
		ArrayList<Integer> adr = new ArrayList<Integer>();
		int totalDays = 0;
		int totalTradeDays = 0;
		int lastTradeDay = 0;
		QuoteShort q = null;
		QuoteShort q1 = null;
		QuoteShort qLast = null;
		int month = 0;
		int lastCloseMonth = -1;
		double actualOpenRisk = 0;
		double accPositions = 0.0;
		double actualFloatingPips = 0;
		boolean ishOk = false;
		ArrayList<Long> closedTimes = new ArrayList<Long>();
		ArrayList<Integer> closedPips = new ArrayList<Integer>();
		ArrayList<Double> perArray = new ArrayList<Double>(); 
		
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		ArrayList<Integer> results = new ArrayList<Integer>();
		HashMap<Integer,Integer> yWinPips = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> yLostPips = new HashMap<Integer,Integer>();
		for (int i=bbars;i<data.size()-2;i++){
			q1 = data.get(i-1);
			q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			 y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			 month = cal.get(Calendar.MONTH);
			if (y>y2) break;
			
			if (y<y1 || y>y2) continue;
			
			if (y==y1 && m<m1) continue;
			if (y==y2 && m>m2) continue;
			qLast = q;
			
			comm = 20;
			//if (h==0) comm=30;
			
			if (day!=lastDay){				
				if (high!=-1){
					range = high-low;
					rangeArr.add(range);
					range = (int) MathUtils.average(rangeArr, rangeArr.size()-20,rangeArr.size()-1);					
				}				
				high = -1;
				low = -1;
				doValue = q.getOpen5();
				lastDay = day;
				mode = 0;
				totalDays++;
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();	
			
			//double avg = MathUtils.average(closeArr, i-n, i-1);
			double avg = data.get(i-bbars).getOpen5();//n ultimas vars			
			
			int diffUp = (int) (q.getOpen5()-avg);
			int diffDown = (int) (avg-q.getOpen5());
			double per1 = diffUp*100.0/range;
			int tp = (int) (range*ftp);
			int sl = (int) (range*fsl);
			
			if (tp<=40) tp=40;
			
			if (h>=h1 && h<=h2){
				if (per1>=per){
					int entry = q.getOpen5();
					
					if (isMomentum){
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
					}else{
						int tpvalue = entry-tp;
						int slvalue = entry+sl;
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
					}
					
					int tpvalue = entry+tp;
					int slvalue = entry-sl;
					if (!isMomentum){
						tpvalue = entry-tp;
						slvalue = entry+sl;
					}
					//positions.add(p);
					
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, entry, tpvalue, slvalue, false);
					
					
					int pips = qm.getClose5()-entry;
					if (!isMomentum){
						pips = entry-qm.getClose5();
					}
					
					
					pips-=comm;
					
					if (pips>=0){
						results.add(pips);
						
						if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
						int ya = yWinPips.get(y);
						yWinPips.put(y, ya+pips);
					}else{
						results.add(pips);
						
						if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
						int ya = yLostPips.get(y);
						yLostPips.put(y, ya-pips);
					}				
				}else if (per1<=-per){
					int entry = q.getOpen5();
					
					if (isMomentum){
						int tpvalue = entry-tp;
						int slvalue = entry+sl;					
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.SHORT);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
					}else{
						int tpvalue = entry+tp;
						int slvalue = entry-sl;
						PositionShort p = new PositionShort();
						p.setEntry(entry);
						p.setTp(tpvalue);
						p.setSl(slvalue);
						p.setMaxProfit(entry);
						p.setPositionType(PositionType.LONG);
						p.setPositionStatus(PositionStatus.OPEN);
						p.setOpenIndex(i);
					}
					
					int tpvalue = entry-tp;
					int slvalue = entry+sl;
					if (!isMomentum){
						tpvalue = entry+tp;
						slvalue = entry-sl;
					}
					TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, entry, tpvalue, slvalue, false);
					
					//positions.add(p);
					int pips = entry-qm.getClose5();
					if (!isMomentum){
						pips = qm.getClose5()-entry;
					}
					
					pips-=comm;
					
					if (pips>=0){
						results.add(pips);
						
						if (!yWinPips.containsKey(y)) yWinPips.put(y,0);
						int ya = yWinPips.get(y);
						yWinPips.put(y, ya+pips);
					}else{
						results.add(pips);
						
						if (!yLostPips.containsKey(y)) yLostPips.put(y,0);
						int ya = yLostPips.get(y);
						yLostPips.put(y, ya-pips);
					}				
				}
			}//H
		}
		
		
		winPips = 0;
		lostPips = 0;
		for (int i=0;i<results.size();i++){
			int pips = results.get(i);
			if (pips>=0){ 
				winPips += pips;
				wins++;
			}
			else{
				lostPips += -pips;
				losses++;
			}
		}
		
		//estudio de years
		int posYears = 0;
		Iterator it = yWinPips.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer,Integer> pair = (Map.Entry)it.next();
	        int year = pair.getKey();
	        int wPips = pair.getValue();
	        int lPips = 0;
	        if (yLostPips.containsKey(year))
	        	lPips = yLostPips.get(year);
	        int netPips = wPips-lPips;
	        if (netPips>=0) posYears++;
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
		double pf = winPips*1.0/lostPips;
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (winPips-lostPips)*0.1/trades;
		//if (pf>=1.5)
		if (printAlways 
				|| (trades>=200 && Math.abs(avg)>=2.0) && posYears>=12
				)
		System.out.println(
				h1+" "+h2
				+" "+PrintUtils.Print2dec(ftp, false)
				+" "+PrintUtils.Print2dec(fsl, false)
				+" "+bbars
				+" "+PrintUtils.Print2dec(per, false)
				+" || "
				+" "+trades
				+" "+posYears
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(1.0/pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
	}

	public static void main(String[] args) {
String path0 ="C:\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.03.08.csv";
		
		String pathNews = path0+"News.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		try {
			Sizeof.runGC ();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		//FFNewsClass.readNews(pathNews,news,0);
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);				
			dataI 		= new ArrayList<QuoteShort>();			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			Calendar cal = Calendar.getInstance();
			System.out.println("path: "+path+" "+data.size());
			double aMaxFactorGlobal = -9999;
			
			//7-9:0.10 0.70 850 200.00 ||  9898 0 7.67 0.54 1.85 -4.85 
			for (int h1=3;h1<=3;h1++){
				int h2 = h1+0;
				//System.out.println(h1);
				for (double fsl=0.80;fsl<=0.8;fsl+=0.10){
					//for (double ftp=4*fsl;ftp<=4.0*fsl;ftp+=0.5*fsl){
					for (double ftp=0.10;ftp<=0.10;ftp+=0.05){
						for (int bbars=1;bbars<=360;bbars+=1){
							for (double per = 70;per<=70.0;per+=50){
								for (int y1=2004;y1<=2004;y1++){
									int y2 = y1+14;
									TestVolatility.doTest("", data, y1, y2, 0, 11, h1, h2, per, bbars, ftp, fsl, 0,false, true);
								}
								
							}
						}
					}
				}
			}
		}

	}

}
