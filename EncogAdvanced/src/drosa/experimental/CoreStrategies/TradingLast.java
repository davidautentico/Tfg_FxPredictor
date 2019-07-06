package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TradingLast {
	
	public static void test(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			HashMap<Integer,StrategyConfig> config,
			int h3,
			int maxPips,
			int mode,
			double comm,boolean debug){
		
		int maxLosses = 0;
		int actualLosses = 0;
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		boolean isFailedDay = false;
		boolean isTradedDay = true;
		int totalFailedDays = 0;
		int totalWinDays = 0;
		int dayTrades = 0;
		int lastHigh = -1;
		int lastLow = -1;
		int actualMax = -1;
		int actualMin = -1;
		for (int i=1; i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				if (isTradedDay){
					if (isFailedDay){
						totalFailedDays++;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}else{
						totalWinDays++;
						actualLosses = 0;
					}
				}
				dayTrades = 0;
				isFailedDay = false;
				isTradedDay = false;
				lastDay = day;
				
				lastHigh = actualMax;
				lastLow = actualMin;
				actualMax = -1;
				actualMin = -1;
			}
			
	
			
			if (config.containsKey(h)
					&& config.get(h).isEnabled()
					//&& dayTrades<1
					){			
				int maxMin = maxMins.get(i-1);	
				int diffH = actualMax-q.getOpen5();
				int diffL = q.getOpen5()-actualMin;
				//int diffH = lastHigh-q.getOpen5();
				//int diffL = q.getOpen5()-lastLow;
				//evaluar entrada de nuevas operaciones
				StrategyConfig sc = config.get(h);
				int thr = sc.getThr();
				int tp = sc.getTp();
				int sl = sc.getSl();
				PositionCore pos = null;
				if (maxMin>=thr
						//&& diffH<=maxPips*10
						&& q1.getClose5()>=q1.getOpen5()+maxPips*10
						){
					pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setTp(q.getOpen5()-10*tp);
					pos.setSl(q.getOpen5()+10*sl);
					pos.setPositionType(PositionType.SHORT);
					positions.add(pos);
					dayTrades++;
				}else if (maxMin<=-thr 
						//&& diffL<=maxPips*10
						&& q1.getClose5()<=q1.getOpen5()-maxPips*10
						){
					pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setTp(q.getOpen5()+10*tp);
					pos.setSl(q.getOpen5()-10*sl);
					pos.setPositionType(PositionType.LONG);
					positions.add(pos);
					dayTrades++;
				}
			}
			
			
			//evalucion entradas actuales
			int j = 0;
			while (j<positions.size()){
				PositionCore p = positions.get(j);
				boolean closed = false;
				int diff = 0;
				
				if (p.getPositionType()==PositionType.LONG){
					if (q.getLow5()<=p.getSl()){
						diff = p.getSl()-p.getEntry();
						closed = true;
					}else if (q.getHigh5()>=p.getTp()){
						diff = p.getTp()-p.getEntry();
						closed = true;
					}else if (h>=h3){
						diff = q.getClose5()-p.getEntry();
						closed = true;
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					if (q.getHigh5()>=p.getSl()){
						diff = p.getEntry()-p.getSl();
						closed = true;
					}else if (q.getLow5()<=p.getTp()){
						diff = p.getEntry()-p.getTp();
						closed = true;
					}else if (h>=h3){
						diff = p.getEntry()-q.getClose5();
						closed = true;
					}
				}
				
				
				if (closed){
					diff -= comm*10;
					isTradedDay = true;
					//stats
					if (diff>=0){
						wins++;
						winPips += diff;
						if (debug)
							System.out.println("[win] "+p.getPositionType()+" "+p.getEntry()+" "+p.getSl()+" "+p.getTp()+" || "+diff+" || "+q.toString());
					}else{
						isFailedDay = true;
						losses++;
						lostPips += -diff;
						if (debug)
							System.out.println("[loss] "+p.getPositionType()+" "+p.getEntry()+" "+p.getSl()+" "+p.getTp()+" || "+diff+" || "+q.toString());
					}
					positions.remove(j);
				}else{
					j++;
				}
			}	
			
			if (actualMax== -1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
			if (actualMin== -1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
		}
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		int totalTradedDays = totalWinDays + totalFailedDays;
		double winDays = totalWinDays*100.0/(totalTradedDays);
		System.out.println(
				header
				+" "+y1+" "+y2
				+" "+maxPips
				+" || "
				+" "+PrintUtils.Print2Int(trades, 5)+" "+PrintUtils.Print2Int(wins, 5)+" "+PrintUtils.Print2Int(losses, 5)
				+" "+PrintUtils.Print2dec(winPer, false)						
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(winDays, false)+" ("+PrintUtils.Print2Int(totalFailedDays,4)+"/"+PrintUtils.Print2Int(totalTradedDays,4)+") "	
				+" "+maxLosses
				);
		
	}

	public static void main(String[] args) throws Exception {
String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.08.03.csv";
		
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
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			HashMap<Integer,StrategyConfig> config = new HashMap<Integer,StrategyConfig>();
			
			for (int s=0;s<=23;s++){
				StrategyConfig sc = new StrategyConfig();
				sc.setHour(s);
				sc.setEnabled(false);
				sc.setThr(500);
				sc.setTp(12);
				sc.setSl(36);
				config.put(s, sc);
			}
			//0: 60,10,50
			//1: 130,10,50
			//2: 420,14,28
			//3: 450,15,45
			//4: 700,15,30
			//5: 450,15,45
			//6: 200,15,45
			//7: 300,15,30
			//8: 550,15,45
			//9: 500,12,36
			//22:60,17,119
			//23: 80,10,60
			StrategyConfig sc = null;
			for (int y1=2009;y1<=2009;y1+=1){
				int y2 = y1+7;
				for (int h1=0;h1<=0;h1++){
					sc = config.get(h1);
					sc.setEnabled(true);
					for (int h3=99;h3<=99;h3++){
						for (int thr= 30;thr<= 30;thr+= 10){
							for (int nbars = 36;nbars<= 36;nbars+=1){
								for (int tp=8;tp<=8;tp++){
									for (int sl=(int) (1*tp);sl<=1*tp;sl+=tp){	
										//sc.setParams(h1, thr, tp, sl, true);	
										sc = config.get(0);sc.setParams(0,60,10,50, true);	
										sc = config.get(1);sc.setParams(1,130,10,50, true);	
										sc = config.get(2);sc.setParams(2,420,14,28, true);	
										sc = config.get(3);sc.setParams(3,450,15,45, true);	
										sc = config.get(4);sc.setParams(4,700,15,30, true);	
										sc = config.get(5);sc.setParams(5,450,15,45, true);	
										sc = config.get(6);sc.setParams(6,200,15,45, true);	
										sc = config.get(7);sc.setParams(7,300,15,30, true);	
										sc = config.get(8);sc.setParams(8,550,15,45, true);	
										sc = config.get(9);sc.setParams(9,500,12,36, true);	
										sc = config.get(22);sc.setParams(22,60,17,119, true);	
										sc = config.get(23);sc.setParams(23,80,10,60, true);	
										String header = "[config] "+PrintUtils.Print2Int(thr, 4)+" "+PrintUtils.Print2Int(tp, 3)+" "+PrintUtils.Print2Int(sl, 3);
										for (int maxPips=0;maxPips<=0;maxPips+=1){
											TradingLast.test(header,data, maxMins,y1,y2, config,h3,maxPips,-1,2.0,false);
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
