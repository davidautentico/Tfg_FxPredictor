package drosa.experimental.fibs;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.SuperStrategy;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestBasicBars {

	public static void testBasicBars(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int begin,int end,String hours,
			int bar,int tp,int sl,
			int moveBE,int badBE,
			boolean ctMode){
		
		ArrayList<Integer> minutesWin = new ArrayList<Integer>();
		ArrayList<Integer> totalsMinutes = new ArrayList<Integer>();
		for (int i=0;i<=12*23+12;i++){
			minutesWin.add(0);
			totalsMinutes.add(0);
		}
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		
		int totalPips = 0;
		int maxOpens = 0;
		int wins = 0;
		int losses = 0;
		int bes = 0;
		int max = -999999;
		int min = 999999;
		int positivePips = 0;
		int negativePips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		PositionShort pos = null;
		QuoteShort higherLower = new QuoteShort();
		higherLower.setHigh5(-1);
		higherLower.setLow5(-1);
		int opens=0;
		for (int i=begin;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		
			int tpPips = tp;
			int slPips = sl;
			
			int maxMin = maxMins.get(i).getExtra();
			int entry = -1;
			int stopLoss = -1;
			int takeProfit = -1;
			
			PositionType posType = PositionType.NONE;
			int allowed = allowedHours.get(h);
			
			if (i<end){//solo abrimos posiciones para ese periodo
				if (maxMin>=bar 
						//&& q.getClose5()<q.getOpen5()
						&& allowed==1){
					entry      = q1.getOpen5();
					if (ctMode){
						stopLoss   = entry+slPips*10;
						takeProfit = entry-tpPips*10;
						posType = PositionType.SHORT;
					}else{
						stopLoss   = entry-slPips*10;
						takeProfit = entry+tpPips*10;
						posType = PositionType.LONG;
					}
				}

				//solo abrimos posiciones para ese periodo
				if (maxMin<=-bar
						//&& q.getClose5()>q.getOpen5()
						&& allowed==1){
					entry      = q1.getOpen5();
					if (ctMode){
						stopLoss   = entry-slPips*10;
						takeProfit = entry+tpPips*10;
						posType = PositionType.LONG;
					}else{
						stopLoss   = entry+slPips*10;
						takeProfit = entry-tpPips*10;
						posType = PositionType.SHORT;
					}
				}
			}
			
			//opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
			if (entry!=-1 //&& opens<maxAllowed			
					){
				pos = new PositionShort();
				pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
				pos.setEntry(entry);
				pos.setSl(stopLoss);
				pos.setTp(takeProfit);		
				pos.setPositionType(posType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenDiff(i);
				positions.add(pos);
				//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
			}
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int earnedPips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					//System.out.println(q.toString());
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						int diffClose = p.getEntry()-q1.getClose5();
						if (q1.getHigh5()>=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getSl()-p.getEntry();
						}else if (q1.getLow5()<=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getEntry()-p.getTp();
						}else if (diffClose*0.1>=moveBE){
							p.setSl(p.getEntry()-10);
						}else if (diffClose*0.1<=-badBE){
							p.setTp(p.getEntry()-10);
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						int diffClose = q1.getClose5()-p.getEntry();
						if (q1.getLow5()<=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getEntry()-p.getSl();
						}else if (q1.getHigh5()>=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getTp()-p.getEntry();
						}else if (diffClose*0.1>=moveBE){
							p.setSl(p.getEntry()+10);
						}else if (diffClose*0.1<=-badBE){
							p.setTp(p.getEntry()+10);
						}
					}	
					
					if (closed){	
						int hopen = p.getOpenCal().get(Calendar.HOUR_OF_DAY);
						int mopen = p.getOpenCal().get(Calendar.MINUTE);
						int tradePosition = hopen*12+(int)mopen/5;
						//System.out.println("position: "+tradePosition);
						int totalWin = minutesWin.get(tradePosition);
						int total    = totalsMinutes.get(tradePosition);
						totalsMinutes.set(tradePosition, total+1);
						if (win==1)
							minutesWin.set(tradePosition, totalWin+1);
						
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
						totalPips += earnedPips;
						if (earnedPips>0) positivePips += earnedPips;
						else negativePips += Math.abs(earnedPips);
						
						if (earnedPips>=0){
							if (Math.abs(earnedPips)>10) wins++;
							else {
								if (Math.abs(earnedPips)<=10) bes++;
							}
						}else{
							losses++;
						}
						
						positions.remove(j);//borramos y no avanzamos
					}else{
						j++;
					}
				}
				opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
				if (opens>maxOpens) maxOpens = opens;
			}//for positions	
			
			PositionShort.getHigherLowerPos(positions,higherLower);
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}//for data
		
		int j = 0;
		QuoteShort q1 = data.get(end-1);
		while (j<positions.size()){
			PositionShort p = positions.get(j);
			int earnedPips = 0;
			if (p.getPositionStatus()==PositionStatus.OPEN){
				//System.out.println(q.toString());
				boolean closed = false;
				int win = 0;
				if (p.getPositionType()==PositionType.SHORT){
					earnedPips =  p.getEntry()-q1.getClose5();
					closed = true;
					if (earnedPips>=0) wins++;
					else losses ++;					
				}
				if (p.getPositionType()==PositionType.LONG){
					earnedPips =  q1.getClose5()-p.getEntry();
					closed = true;
					if (earnedPips>=0) wins++;
					else losses ++;	
				}	
				
				if (closed){	
					int hopen = p.getOpenCal().get(Calendar.HOUR_OF_DAY);
					int mopen = p.getOpenCal().get(Calendar.MINUTE);
					int tradePosition = hopen*12+(int)mopen/5;
					//System.out.println("position: "+tradePosition);
					int totalWin = minutesWin.get(tradePosition);
					int total    = totalsMinutes.get(tradePosition);
					totalsMinutes.set(tradePosition, total+1);
					if (win==1)
						minutesWin.set(tradePosition, totalWin+1);
					
					p.setPositionStatus(PositionStatus.CLOSE);
					p.setWin(win);
					//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
					totalPips += earnedPips;
					if (earnedPips>0) positivePips += earnedPips;
					else negativePips += Math.abs(earnedPips);
					
					if (earnedPips>=0){
						if (Math.abs(earnedPips)>10) wins++;
						else {
							if (Math.abs(earnedPips)<=10) bes++;
						}
					}else{
						losses++;
					}
					positions.remove(j);//borramos y no avanzamos
				}else{
					j++;
				}
			}
			opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
			if (opens>maxOpens) maxOpens = opens;
		}//for positions
		
		int totals = wins+losses+bes;
		double perWin = wins*100.0/totals;
		double perBe  = bes*100.0/totals;
		double perLoss = 100.0-perWin;
		double pf = (positivePips)*1.0/(negativePips);
		double exp = (perWin*tp*1.0-perLoss*sl)/100.0;
		double avgPips = totalPips*0.1/totals;
		
		//System.out.println(totals+" "+PrintUtils.Print2(pf));
		System.out.println(
				hours
				//+" "+bar
				//+" "
				+" "+begin+" "+end
				+" "+PrintUtils.Print2dec(tp,false,2)
				+" "+PrintUtils.Print2dec(sl,false,2)
				//+" "+moveBE
				//+" "+badBE
				+" "+bar
				+" || "
				+" "+totals
				+" "+wins
				+" "+PrintUtils.Print2(perWin)
				+" "+PrintUtils.Print2(perBe)
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" || "
				+" "+PrintUtils.Print2(avgPips)
				+" "+totalPips/10
				+" "+positivePips/10
				+" "+negativePips/10
				);	
	}
	
	public static void testBars(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int begin,int end,String hours,
			int bar,int nATR,double tpFactor,double slFactor,
			int maxAllowed,int maxDOCrosses,int minPips,
			double comm,boolean minutesDebug){
		
		ArrayList<Integer> minutesWin = new ArrayList<Integer>();
		ArrayList<Integer> totalsMinutes = new ArrayList<Integer>();
		for (int i=0;i<=12*23+12;i++){
			minutesWin.add(0);
			totalsMinutes.add(0);
		}
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		
		int totalPips = 0;
		int maxWins = 0;
		int maxLosses = 0;
		int actualWins = 0;
		int actualLosses = 0;
		int avgWins=0;
		int avgLosses=0;
		int totalAvgWins=0;
		int totalAvgLosses=0;
		int maxOpens = 0;
		int wins = 0;
		int losses = 0;
		int totalDays = 0;
		int lastDay = -1;
		int max = -999999;
		int min = 999999;
		double atr = 100.0;
		int DO = 0;
		int DOcrosses = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		PositionShort pos = null;
		QuoteShort higherLower = new QuoteShort();
		higherLower.setHigh5(-1);
		higherLower.setLow5(-1);
		int opens=0;
		for (int i=begin;i<end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//para al calculo de rangos
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
					}
				}
				DO = q.getOpen5();
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
				DOcrosses=0;
			}
			
			if (q.getLow5()<=DO && DO<=q.getHigh5())
				DOcrosses++;
			//int atrPips = (int) (atr*percent*0.01);
			//int highEntry = DO + atrPips*10;
			//int lowEntry  = DO - atrPips*10;
			double slTpFactor = slFactor/tpFactor;
			int tpPips = (int) (atr*tpFactor);
			int slPips = (int) (atr*slFactor);
			if (tpPips<=5){
				tpPips = 5;
				slPips = (int) (tpPips*slTpFactor);
			}
			//System.out.println(atr+" "+atrPips+" "+DO+" "+highEntry+" "+lowEntry);
			int maxMin = maxMins.get(i).getExtra();
			int entry = -1;
			int stopLoss = -1;
			int takeProfit = -1;
			int shortDiff = q1.getOpen5()-higherLower.getHigh5();
			int longDiff = higherLower.getLow5()-q1.getOpen5();
			/*if (opens>0)
				System.out.println(shortDiff+" "+longDiff
						+" "+PrintUtils.Print2(higherLower.getHigh5())
						+" "+PrintUtils.Print2(higherLower.getLow5())
				);*/
			
			PositionType posType = PositionType.NONE;
			int allowed = allowedHours.get(h);
			if (maxMin>=bar 
					&& q.getHigh5()>=DO
					&& DOcrosses<=maxDOCrosses
					&& (shortDiff>=minPips*10 || higherLower.getHigh5()==-1)
					&& allowed==1){
				entry      = q1.getOpen5();
				stopLoss   = entry+slPips*10;
				takeProfit = entry-tpPips*10;
				posType = PositionType.SHORT;
			}
			
			if (maxMin<=-bar
					&& q.getLow5()<=DO
					&& DOcrosses<=maxDOCrosses
							&& (longDiff>=minPips*10 || higherLower.getLow5()==-1)
					&& allowed==1){
				entry      = q1.getOpen5();
				stopLoss   = entry-slPips*10;
				takeProfit = entry+tpPips*10;
				posType = PositionType.LONG;
			}
			
			opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
			if (entry!=-1 && opens<maxAllowed			
					){
				pos = new PositionShort();
				pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
				pos.setEntry(entry);
				pos.setSl(stopLoss);
				pos.setTp(takeProfit);		
				pos.setPositionType(posType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenDiff(i);
				positions.add(pos);
				//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
			}
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int earnedPips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					//System.out.println(q.toString());
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (q1.getHigh5()>=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getSl()-p.getEntry();
						}else if (q1.getLow5()<=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getEntry()-p.getTp();
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (q1.getLow5()<=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getEntry()-p.getSl();
						}else if (q1.getHigh5()>=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getTp()-p.getEntry();
						}
					}	
					
					if (closed){	
						int hopen = p.getOpenCal().get(Calendar.HOUR_OF_DAY);
						int mopen = p.getOpenCal().get(Calendar.MINUTE);
						int tradePosition = hopen*12+(int)mopen/5;
						//System.out.println("position: "+tradePosition);
						int totalWin = minutesWin.get(tradePosition);
						int total    = totalsMinutes.get(tradePosition);
						totalsMinutes.set(tradePosition, total+1);
						if (win==1)
							minutesWin.set(tradePosition, totalWin+1);
						
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
						totalPips += earnedPips;
						if (win==1){
							wins++;
							if (actualLosses>maxLosses) maxLosses = actualLosses;							
							avgLosses+=actualLosses;
							if (actualLosses>0){
								avgLosses+=actualLosses;
								totalAvgLosses++;
							}
							actualLosses = 0;
							actualWins++;
						}
						if (win==-1){
							losses++;
							if (actualWins>maxWins) maxWins = actualWins;
							if (actualWins>0){
								avgWins+=actualWins;
								totalAvgWins++;
							}
							actualWins = 0;
							actualLosses++;
						}
						positions.remove(j);//borramos y no avanzamos
					}else{
						j++;
					}
				}
				opens = PositionShort.countTotal(positions, PositionStatus.OPEN);
				if (opens>maxOpens) maxOpens = opens;
			}//for positions	
			
			PositionShort.getHigherLowerPos(positions,higherLower);
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}//for data
		
		int totals = wins+losses;
		double perWin = wins*100.0/totals;
		double perLoss = 100.0-perWin;
		double pf = (perWin*tpFactor*1.0)/(perLoss*slFactor);
		//double exp = (perWin*tpFactor*1.0-perLoss*slFactor)/100.0;
		double avgPips = totalPips*0.1/totals;
		System.out.println(hours
				+" "+bar
				+" "+PrintUtils.Print2dec(tpFactor,false,2)
				+" "+PrintUtils.Print2dec(slFactor,false,2)
				+" "+maxAllowed
				+" "+maxDOCrosses
				+" || "
				+" "+totals
				+" "+PrintUtils.Print2(perWin)
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2(avgPips)
				);	
		if (minutesDebug){
			int maxMinute = 23*12+12;
			for (int i=0;i<=maxMinute;i++){
				int mins = minutesWin.get(i);
				int total = totalsMinutes.get(i);
				double perMWin = mins*100.0/total;
				int h =  i/12;
				int m = i%12;
				System.out.println(h+":"+(m*5)+" "+total+" "+PrintUtils.Print2(perMWin));
			}
		}
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pathEURUSD = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.04.04.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2015.03.12.csv";
		/*String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2015.03.12.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_1 Min_Bid_2003.08.03_2015.03.12.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_1 Min_Bid_2003.08.03_2015.03.12.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2003.05.04_2015.03.12.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2015.03.12.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2003.08.03_2015.03.12.csv";*/
		
		ArrayList<Quote> dataI 		= DAO.retrieveData(pathEURUSD, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		System.out.println("total data: "+data.size());
		
		int begin = 1;
		int end = 999999;
		end = 400000;
		ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
		int tp = 11;
		int sl = 35;
		for (begin=1;begin<=data.size()-70000;begin+=17000){
			//begin = 1;
			end = begin+70000;
			for (tp=12;tp<=12;tp+=50){
			//for (sl=(int) (0.01*tp);sl<=500;sl+=0.01*tp){
					//System.out.println("sl");
				for (sl=30;sl<=30;sl+=1){
					for (int moveBE=9999;moveBE<=9999;moveBE+=1){
						for (int badBE=9999;badBE<=9999;badBE+=1){
							for (int bar=500;bar<=500;bar+=1){
								//System.out.println("bar");
								for (int h=0;h<=0;h++){
									TestBasicBars.testBasicBars(data, maxMins, begin, end,
										//String.valueOf(h), bar, tp, sl,moveBE,true);
										"0 1 2 3 4 5 6 7 8 9", bar, tp, sl,moveBE,badBE,true);
										//"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23", bar, tp, sl,moveBE,badBE,true);
										//"0 1 2 3", bar, tp, sl,moveBE,true);
										//"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23", bar, tp, sl,false);
									    //"10 11 12 13 14 15 16 17", bar, tp, sl,false);
									//TestBasicBars.testBasicBars(data, maxMins, begin, end,
											//String.valueOf(h), bar, tp, sl,false);
											//"0 23", bar, tp, sl,true);
								}
							}//bar
						}//barBE
					}//moveBE
				}
			}
		}
		
	}

}
