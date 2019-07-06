package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMomentumWeek {
	
	
	public static void doTest(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int href,
			int n,
			int minDistance,
			int tp,
			int sl,
			int offset,
			int nbars
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
		for (int i=n;i<data.size()-1;i++){
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
			
			
			if (true
					&& week!=lastWeek
					&& h==href && min==0
					//y*day>=lastDayTrading+5 && h==href && min==0
					){
				refPoint = q.getOpen5();
				//lastDayTrading = y*week;
				lastWeek = week;
				
				int arange = high-low;
				ranges.add(arange);
				range = MathUtils.average(ranges, ranges.size()-1, ranges.size()-1);
				high = -1;
				low = -1;				
				//minDistance  = (int) (factor*range);
			}
			
			if (true
					&& day!= lastTradeRef
					&& h==href && min==0
					//y*day>=lastDayTrading+5 && h==href && min==0
					){
				refPoint = q.getOpen5();
				//lastDayTrading = y*week;
				lastWeek = week;
				
				int arange = high-low;
				ranges.add(arange);
				range = MathUtils.average(ranges, ranges.size()-1, ranges.size()-1);
				high = -1;
				low = -1;	
				lastTradeRef = day;
				//minDistance  = (int) (factor*range);
			}
			
			if (day!=lastDay
					)
			{
				//refPoint = q.getOpen5();
				//if (isTrade>0) countDays++;
				isTrade=0;
				lastDay = day;
				totalDays++;
				dayLongs = 0;
				dayShorts = 0;
			}
			
			if (high==-1 || q.getHigh5()>=high) high = q.getHigh5();
			if (low==-1 || q.getLow5()<=low) low = q.getLow5();
			
			boolean closeLongs =  false;
			boolean closeShorts = false;
			
			refPoint = data.get(i-0).getOpen5();
			
			int maxMin = maxMins.get(i-1);
			
			int distanceH = q.getOpen5()-refPoint;
			int distanceL = -q.getOpen5()+refPoint;
			if (maxMin>=minDistance
					){
				int diff = q.getOpen5()-data.get(i-n).getOpen5();
				if (true
						//&& diff>=0
						&& h>=h1 && h<=h2
						){
					//abrimos posición larga
					/*PositionShort pos = new PositionShort();
					pos.setEntry(q.getOpen5());
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setPositionType(PositionType.LONG);
					pos.setSl(pos.getEntry()-sl);
					pos.setTp(pos.getEntry()+10000*sl);
					pos.setOpenIndex(i);
					positions.add(pos);*/
					
					
					PositionShort pos = new PositionShort();					
					pos.setEntry(q.getOpen5());
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setPositionType(PositionType.SHORT); 
					pos.setSl(q.getOpen5()+sl);
					pos.setTp(pos.getEntry()-tp);
					pos.setOpenIndex(i);
					pos.setOrder(1);
					
					//System.out.println("[new short] "+pos.toString());
					
					if (dayShorts<=100){
						positions.add(pos);
						dayShorts++;				
						if (day!=lastDayTrading){
							countDays++;
							lastDayTrading = day;
						}
					}
				}
				//closeLongs = true;
			}else if (maxMin<=-minDistance
					){
				int diff = -q.getOpen5()+data.get(i-n).getOpen5();
				if (true
						//&& diff>=0
						&& h>=h1 && h<=h2
						){
					
					PositionShort pos = new PositionShort();
					pos.setEntry(q.getOpen5());
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setPositionType(PositionType.LONG);
					pos.setSl(q.getOpen5()-sl);
					pos.setTp(pos.getEntry()+tp);
					pos.setOpenIndex(i);
					pos.setOrder(1);
					
					if (dayLongs<=100){
						positions.add(pos);
						dayLongs++;
						if (day!=lastDayTrading){
							countDays++;
							lastDayTrading = day;
						}
					}
				}
				//closeShorts= true;
			}
		
			int j = 0;
			while (j<positions.size()){
				boolean closed = false;
				int pips = 0;
				int closeMode = 0;
				PositionShort p = positions.get(j);
				if (i>p.getOpenIndex())
					if (p.getPositionStatus()==PositionStatus.OPEN){
						if (p.getPositionType()==PositionType.LONG){
							if (closeLongs 
									|| q.getLow5()<=p.getSl()
									|| q.getHigh5()>=p.getTp()
									|| i-p.getOpenIndex()>nbars
									){
								pips = (int) (q.getOpen5()-p.getEntry()-comm);
								
								if (q.getHigh5()>=p.getTp()){
									pips = p.getTp()-p.getEntry()-comm;
									
									/*PositionShort pos = new PositionShort();
									pos.setEntry(q.getClose5());
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setPositionType(PositionType.LONG);
									pos.setSl(pos.getEntry()-sl);
									pos.setOpenIndex(i);									
									pos.setOrder(pos.getOrder()+1);									
									int f = pos.getOrder();
									pos.setTp(pos.getEntry()+f*tp);
									positions.add(pos);*/
									closeMode = 1;
								}
								if (q.getLow5()<=p.getSl()){
									pips = p.getSl()-p.getEntry()-comm;	
									
									/*PositionShort pos = new PositionShort();					
									pos.setEntry(q.getClose5());
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setPositionType(PositionType.SHORT); 
									
									int f = p.getOrder();
									pos.setSl(pos.getEntry()+1*sl);
									pos.setOpenIndex(i);	
									pos.setTp(pos.getEntry()-f*tp);
									pos.setOrder(p.getOrder()+1);	
									positions.add(pos);*/
									
									/*PositionShort pos = new PositionShort();					
									pos.setEntry(q.getClose5());
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setPositionType(PositionType.LONG); 									
									int f = p.getOrder();
									pos.setSl(pos.getEntry()-1*sl);
									pos.setOpenIndex(i);	
									pos.setTp(pos.getEntry()+f*tp);
									pos.setOrder(p.getOrder()+1);	
									positions.add(pos);*/
									
									//System.out.println("[new short] "+pos.toString());
									/*PositionShort pos = new PositionShort();
									pos.setEntry(q.getClose5());
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setPositionType(PositionType.LONG);
									pos.setSl(pos.getEntry()-sl);
									pos.setTp(pos.getEntry()+1*tp);
									pos.setOpenIndex(i);									
									pos.setOrder(pos.getOrder()+1);									
									int f = pos.getOrder();
									pos.setTp(pos.getEntry()-f*tp);
									positions.add(pos);*/
									closeMode = -1;
								}
								closed = true;
							}else{
								pips = (int) (q.getOpen5()-p.getEntry()-comm);
								if (pips>=p.getMaxProfit()){
									p.setMaxProfit(pips);
								}else{
									double per = pips*100.0/p.getMaxProfit();
									/*if (p.getMaxProfit()>=sl*0.7
											&& per<=90.0
											){
										closed = true;
									}*/
								}
							}
						}else if (p.getPositionType()==PositionType.SHORT){
							if (closeShorts 
									|| q.getHigh5()>=p.getSl()
									|| q.getLow5()<=p.getTp()
									|| i-p.getOpenIndex()>nbars
									){
								pips = (int) (-q.getOpen5()+p.getEntry()-comm);
								
								if (q.getLow5()<=p.getTp()){
									pips = -p.getTp()+p.getEntry()-comm;
									
									/*PositionShort pos = new PositionShort();					
									pos.setEntry(q.getClose5());
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setPositionType(PositionType.SHORT); 
									pos.setSl(pos.getEntry()+sl);
									pos.setOpenIndex(i);
									pos.setOrder(pos.getOrder()+1);									
									int f = pos.getOrder();
									pos.setTp(pos.getEntry()-f*tp);
									positions.add(pos);*/
									//System.out.println("[closed short] "+p.getEntry()+" "+p.getTp());
									closeMode = 2;
								}
								if (q.getHigh5()>=p.getSl()){
									pips = -p.getSl()+p.getEntry()-comm;
									
									/*PositionShort pos = new PositionShort();
									pos.setEntry(q.getClose5());
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setPositionType(PositionType.LONG);									
									int f = p.getOrder();
									pos.setSl(pos.getEntry()-1*sl);
									pos.setOpenIndex(i);	
									pos.setTp(pos.getEntry()+f*tp);									
									pos.setOrder(p.getOrder()+1);	
									positions.add(pos);*/
									
									/*PositionShort pos = new PositionShort();
									pos.setEntry(q.getClose5());
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setPositionType(PositionType.SHORT);									
									int f = p.getOrder();
									pos.setSl(pos.getEntry()+1*sl);
									pos.setOpenIndex(i);	
									pos.setTp(pos.getEntry()-f*tp);									
									pos.setOrder(p.getOrder()+1);	
									positions.add(pos);*/
									
									/*PositionShort pos = new PositionShort();					
									pos.setEntry(q.getClose5());
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setPositionType(PositionType.SHORT); 
									pos.setSl(pos.getEntry()+sl);
									pos.setOpenIndex(i);
									pos.setOrder(pos.getOrder()+1);									
									int f = pos.getOrder();
									pos.setTp(pos.getEntry()-f*tp);
									positions.add(pos);*/
									closeMode = -2;
									
								}
								closed = true;
							}else{
								pips = (int) (-q.getOpen5()+p.getEntry()-comm);
								if (pips>=p.getMaxProfit()){
									p.setMaxProfit(pips);
								}else{
									double per = pips*100.0/p.getMaxProfit();
									/*if (p.getMaxProfit()>=sl*0.7
											&& per<=90.0
											){
										closed = true;
									}*/
								}
							}
						}
					}
				
				if (closed){
					if (pips>=0){
						winPips += pips;
						wins++;
						
						int yearIdx = y-y1;
						int acc = winPipsYear.get(yearIdx);
						winPipsYear.set(yearIdx, acc+pips);
						
						acc = winYear.get(yearIdx);
						winYear.set(yearIdx, acc+1);
						
						//System.out.println("[closed win] "+closeMode+" || "+pips);
					}else{
						lostPips += -pips;
						losses++;
						
						int yearIdx = y-y1;
						int acc = lostPipsYear.get(yearIdx);
						lostPipsYear.set(yearIdx, acc-pips);
						
						acc = lostYear.get(yearIdx);
						lostYear.set(yearIdx, acc+1);
					}
					positions.remove(j);
				}else{
					j++;
				}
			}
			
		}
		
		int j = 0;
		boolean closeLongs = true;
		boolean closeShorts = true;
		QuoteShort.getCalendar(cal, q);
		
		int y = cal.get(Calendar.YEAR);
		while (j<positions.size()){
			boolean closed = false;
			int pips = 0;
			PositionShort p = positions.get(j);
			if (p.getPositionStatus()==PositionStatus.OPEN){
				if (p.getPositionType()==PositionType.LONG){
					if (closeLongs 
							|| q.getLow5()<=p.getSl()
							|| q.getHigh5()>=p.getTp()
							){
						pips = (int) (q.getOpen5()-p.getEntry()-comm);
						
						//if (q.getHigh5()>=p.getTp()) pips = p.getTp()-p.getEntry()-comm;
						//else if (q.getLow5()<=p.getSl()) pips = p.getSl()-p.getEntry()-comm;
						
						closed = true;
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					if (closeShorts 
							|| q.getHigh5()>=p.getSl()
							|| q.getLow5()<=p.getTp()
							){
						pips = (int) (-q.getOpen5()+p.getEntry()-comm);
						
						//if (q.getLow5()>=p.getTp()) pips = -p.getTp()+p.getEntry()-comm;
						//if (q.getHigh5()>=p.getSl()) pips = -p.getSl()+p.getEntry()-comm;
						closed = true;
					}
				}
			}
			
			if (closed){
				if (pips>=0){
					winPips += pips;
					wins++;
					int yearIdx = y-y1;
					int acc = winPipsYear.get(yearIdx);
					winPipsYear.set(yearIdx, acc+pips);
					
					acc = winYear.get(yearIdx);
					winYear.set(yearIdx, acc+1);
				}else{
					//System.out.println("[LOSS ] "+p.getPositionType()+" "+pips);
					lostPips += -pips;
					losses++;
					int yearIdx = y-y1;
					int acc = lostPipsYear.get(yearIdx);
					lostPipsYear.set(yearIdx, acc-pips);
					
					acc = lostYear.get(yearIdx);
					lostYear.set(yearIdx, acc+1);
				}
				positions.remove(j);
			}else{
				j++;
			}
		}
		
		
		int globalWins = 0;
		int globalLosses = 0;
		int globalWinPips = 0;
		int globalLostPips = 0;
		/*for (int i=0;i<=y2-y1;i++){
			int year = y1+i;
			winPips = winPipsYear.get(i);	
			lostPips = lostPipsYear.get(i);
			wins = winYear.get(i);
			losses = lostYear.get(i);
			int trades = wins+losses;
			double winPer = wins*100.0/trades;
			double pf = winPips*1.0/lostPips;
			double avg = (winPips-lostPips)*0.1/trades;
			
			globalWins += wins;
			globalLosses += losses;
			globalWinPips += winPips;
			globalLostPips += lostPips;
			
			//double pfrev = 1.0/pf;
			//double freq	= countDays*100.0/totalDays;
			
			if (true
					//&& pfrev>=1.30 
					//&& freq>=30.0 
					//&& avg<=-4.0
					)
			System.out.println(
					year+" "+h1+" "+h2+" "+href
					+" "+n
					+" "+minDistance
					+" "+sl
					+" "+offset
					+" "+PrintUtils.Print2dec(factor, false)
					+" || "+PrintUtils.Print2dec(pf, false)+" "+PrintUtils.Print2dec(1.0/pf, false)
					+" || "+(wins+losses)+" "+wins+" "+losses+" "+PrintUtils.Print2dec(winPer, false)				
					+" || "+winPips +" "+lostPips
					
					+" "+PrintUtils.Print2dec(avg, false)
					+" || "+PrintUtils.Print2dec(winPips*0.1/wins, false)+" "+PrintUtils.Print2dec(lostPips*0.1/losses, false)
					//+" || "+countDays+" "+PrintUtils.Print2dec(countDays*100.0/totalDays, false)
					);
		}*/
		
		globalWins += wins;
		globalLosses += losses;
		globalWinPips += winPips;
		globalLostPips += lostPips;
		int trades = globalWins+globalLosses;
		double winPer = globalWins*100.0/trades;
		double pf = globalWinPips*1.0/globalLostPips;
		double avg = (globalWinPips-globalLostPips)*0.1/trades;
		
		System.out.println(
				y1+" "+y2+" "+h1+" "+h2+" "+href
				+" "+n
				+" "+minDistance
				+" "+tp
				+" "+sl
				+" "+offset
				+" "+nbars
				+" || "+trades+" "+globalWins+" "+globalLosses+" "+PrintUtils.Print2dec(winPer, false)				
				+" || "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(1.0/pf, false)
				+" || "+PrintUtils.Print2dec(globalWinPips*0.1/globalWins, false)
				+" "+PrintUtils.Print2dec(globalLostPips*0.1/globalLosses, false)
				+" || "+countDays+" "+PrintUtils.Print2dec(countDays*100.0/totalDays, false)
			);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\GBPUSD_5 Mins_Bid_2004.01.01_2018.08.27.csv";
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
			
			for (int y1=2004;y1<=2004;y1++){
				int y2 = y1+14;
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+9;
					for (int n=1;n<=1;n+=12){
						for (int href=10;href<=10;href++){
							for (int minDistace=0;minDistace<=10000;minDistace+=100){
								for (int tp=2000;tp<=2000;tp+=10){
									for (int sl=20000;sl<=20000;sl+=50)
										for (int offset=5;offset<=5;offset+=1)
											for (int nbars=5000;nbars<=5000;nbars+=100){
												TestMomentumWeek.doTest(data, maxMins, y1, y2, 0,11, h1, h2,href, n,minDistace,tp,sl,offset,nbars);
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
