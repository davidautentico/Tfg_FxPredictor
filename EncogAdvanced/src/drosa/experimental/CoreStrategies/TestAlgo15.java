package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestAlgo15 {
	
	public static void doTrade(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int year1,int year2,
			ArrayList<StrategyConfig> configs,
			double comm,
			int debug
			){
		
		ArrayList<Double> lostTrades = new ArrayList<Double> ();
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<year1 || y>year2) continue;
			
			if (day!=lastDay){
				
				lastDay = day;
			}
			
			StrategyConfig strat = configs.get(h); 
			
			if (strat == null || !strat.isEnabled()) continue;

			int maxMin = maxMins.get(i-1);//buscamos la anterior para abriur en la siguiente apertura
			int open = q.getOpen5();
			int maxBars = strat.getMaxBars();
			int pips = 0;
			boolean isTrade = false;
			if (maxMin>=strat.getThr()){				
				int valueTP = (int) (open-10*(strat.getTp()+comm));
				int valueSL= (int) (open+10*(strat.getSl()-comm));
				TradingUtils.getMaxMinShortTPSLMaxBars(data, qm, calqm, i,i+maxBars, valueTP,valueSL, maxBars, false);				
				pips = open-qm.getClose5();
				isTrade = true;
			}else if (maxMin<=-strat.getThr()){
				int valueTP = (int) (open+(10*strat.getTp()+comm));
				int valueSL= (int) (open-10*(strat.getSl()-comm));
				TradingUtils.getMaxMinShortTPSLMaxBars(data, qm, calqm, i,i+maxBars, valueTP,valueSL, maxBars, false);
				pips = qm.getClose5()-open;
				isTrade = true;
			}	
			
			if (isTrade){
				pips-=comm*10;
				if (pips>=0){
					wins++;
					winPips += pips;
				}else{
					losses++;
					lostPips += -pips;
					lostTrades.add(-pips*0.1);
				}
			}			
		}//data	
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		double winPer = wins*100.0/trades;
		
		double winAvg	= winPips*0.1/wins;
		double lossAvg 	= lostPips*0.1/losses;
		
		//MathUtils.summary(header, lostTrades);
		if (debug>=2)
			MathUtils.summary_complete(header, lostTrades);
		
		if (debug>=1)
		System.out.println(
				header
				+" "+year1+" "+year2
				+" || "
				+" "+PrintUtils.Print2Int(trades, 5)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)+" ( "+PrintUtils.Print2dec(winAvg, false)+" "+PrintUtils.Print2dec(lossAvg, false)+" )"
				+" "+PrintUtils.Print2dec(pf, false)				
				);				
	}

	public static void main(String[] args) throws Exception {
		
		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.10.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
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
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			ArrayList<StrategyConfig> configs = new ArrayList<StrategyConfig>();
			for (int h=0;h<=23;h++) configs.add(null);
			/*StrategyConfig sc = new StrategyConfig();sc.setParams(0, 50, 10, 0, 198, true);configs.set(0, sc);
			StrategyConfig sc1 = new StrategyConfig();sc1.setParams(1, 110, 10, 0, 162, true);configs.set(1, sc1);
			StrategyConfig sc2 = new StrategyConfig();sc2.setParams(2, 190,10, 0, 96, true);configs.set(2, sc2);
			StrategyConfig sc3 = new StrategyConfig();sc3.setParams(3, 720, 10, 0, 156, false);configs.set(3, sc3);
			StrategyConfig sc4 = new StrategyConfig();sc4.setParams(4, 460, 10, 0, 66, false);configs.set(4, sc4);
			StrategyConfig sc5 = new StrategyConfig();sc5.setParams(5, 250, 10, 0, 54, true);configs.set(5, sc5);
			StrategyConfig sc6 = new StrategyConfig();sc6.setParams(6, 150, 10, 0, 24, true);configs.set(6, sc6);
			StrategyConfig sc7 = new StrategyConfig();sc7.setParams(7, 100, 10, 0, 66, true);configs.set(7, sc7);
			StrategyConfig sc8 = new StrategyConfig();sc8.setParams(8, 500, 10, 0, 12, true);configs.set(8, sc8);
			StrategyConfig sc9 = new StrategyConfig();sc9.setParams(9, 300, 10, 0, 12, true);configs.set(9, sc9);
			StrategyConfig sc14 = new StrategyConfig();sc14.setParams(14, 1800, 10, 0, 36, true);configs.set(14, sc14);
			StrategyConfig sc18 = new StrategyConfig();sc18.setParams(18, 1000, 10, 0, 132, true);configs.set(18, sc18);
			StrategyConfig sc19 = new StrategyConfig();sc19.setParams(19, 220, 10, 0, 192, true);configs.set(19, sc19);
			StrategyConfig sc23 = new StrategyConfig();sc23.setParams(23, 110, 10, 0, 144, true);configs.set(23, sc23);*/
			
			StrategyConfig sc = new StrategyConfig();sc.setParams(0, 200, 10, 0, 120, true);configs.set(0, sc);
			StrategyConfig sc1 = new StrategyConfig();sc1.setParams(1, 275, 10, 0, 120, true);configs.set(1, sc1);
			StrategyConfig sc2 = new StrategyConfig();sc2.setParams(2, 425,10, 0, 120, true);configs.set(2, sc2);
			StrategyConfig sc3 = new StrategyConfig();sc3.setParams(3, 720, 10, 0, 156, false);configs.set(3, sc3);
			StrategyConfig sc4 = new StrategyConfig();sc4.setParams(4, 460, 10, 0, 66, false);configs.set(4, sc4);
			StrategyConfig sc5 = new StrategyConfig();sc5.setParams(5, 450, 10, 0, 90, true);configs.set(5, sc5);
			StrategyConfig sc6 = new StrategyConfig();sc6.setParams(6, 200, 10, 0, 66, true);configs.set(6, sc6);
			StrategyConfig sc7 = new StrategyConfig();sc7.setParams(7, 250, 10, 0, 120, true);configs.set(7, sc7);
			StrategyConfig sc8 = new StrategyConfig();sc8.setParams(8, 725, 10, 0, 36, true);configs.set(8, sc8);
			StrategyConfig sc9 = new StrategyConfig();sc9.setParams(9, 625, 10, 0, 36, true);configs.set(9, sc9);
			StrategyConfig sc14 = new StrategyConfig();sc14.setParams(14, 1950, 10, 0, 120, true);configs.set(14, sc14);
			StrategyConfig sc18 = new StrategyConfig();sc18.setParams(18, 4175, 10, 0, 120, true);configs.set(18, sc18);
			StrategyConfig sc19 = new StrategyConfig();sc19.setParams(19, 450, 10, 0, 120, true);configs.set(19, sc19);
			StrategyConfig sc23 = new StrategyConfig();sc23.setParams(23, 1325, 10, 0, 120, true);configs.set(23, sc23);
			
			double comm = 2.0;
			/*for (int year1=2009;year1<=2009;year1++){
				int year2 = year1 + 7;
				TestAlgo15.doTrade("", data, maxMins, year1, year2, configs, comm);
			}*/
			//2003-2008: 0,28/200/50 || 
			for (int year1=2003;year1<=2003;year1++){
				int year2 = year1 + 5;
				String header="";
				for (int thr=50;thr<=500;thr+=10){
					for (int tp=10;tp<=20;tp++){
						for (int sl=50;sl<=400;sl+=50){
							for (int h=0;h<=23;h++){
								if (h!=2){
									if (configs.get(h)!=null){														
										configs.get(h).setEnabled(false);	
									}
								}
							}
							for (int h=2;h<=2;h++){
								if (configs.get(h)!=null && configs.get(h).isEnabled()){														
									configs.get(h).setTp(tp);
									configs.get(h).setSl(sl);	
								}
							}
							header=tp+" "+sl+" "+thr;
							TestAlgo15.doTrade(header, data, maxMins, year1, year2, configs, comm,1);
						}										
					}
				}
			}
			
			/*for (int year1=2009;year1<=2009;year1++){
				int year2 = year1 + 7;
				String header="";
				
				
				for (int h=2;h<=2;h++){					
					if (configs.get(h)!=null) configs.get(h).setEnabled(true);
					for (int thr=25;thr<=5000;thr+=25){												
						for (int tp=10;tp<=10;tp++){
							for (int nbars=6;nbars<=120;nbars+=6){
								configs.get(h).setParams(h, thr, tp, 0, nbars, true);
								configs.set(h, sc);								
								header=h+" "+thr+" "+tp+" "+nbars;
								TestAlgo15.doTrade(header, data, maxMins, year1, year2, configs, comm);
							}
						}
						
					}
				}				
			}*/
			
		}

	}

}
