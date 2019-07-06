package drosa.experimental.newResearches;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.GlobalStats;
import drosa.experimental.PositionShort;
import drosa.experimental.EAS.TestEAs;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

/**
 * Despues de ahcer un maximo o un minimo de n pips se coloca un stop debajo del ultimo minimo o maximo
 * @author PC01
 *
 */
public class SwingStop {
	
	public static void doTrade(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,			
			int maxMinThr,
			double perThr1,
			double perThr2,
			double factorBounce,double factorTP,
			int stopOffset,int nPeriod,
			int maxTrades,
			int minTP,
			int minStop,
			int maxStop,
			double balance,
			double risk,
			double comm,
			boolean debug,
			int lookup
			){
		
		GlobalStats stats = new GlobalStats();
		stats.setBalance(balance);
		double extraNeeded = 0;
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int totalOpen = 0;
		boolean longEnabled = false;
		boolean shortEnabled = false;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int actualLow = -1;
		int actualHigh = -1;
		int avgRange = 100;
		int minPipsBounce = (int) (avgRange*factorBounce);
		int minPipsTp = (int) (avgRange*factorTP);
		QuoteShort qmaxmin = new QuoteShort();
		int dayTrades = 0;
		int longEnabledValue = -1;
		int shortEnabledValue = -1;
		int maxRangeFromHigh = -1;
		int maxRangeFromLow = -1;
		for (int i=1;i<data.size();i++){
			int last = lookup;
			if (i<lookup) last = i;
			QuoteShort q1000 = data.get(i-last);
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort maxMin1 = maxMins.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){				
				if (actualLow!=-1 && actualHigh!=-1){
					int actualRange = actualHigh-actualLow;
					dailyRanges.add(actualRange);
					avgRange		= (int) MathUtils.average(dailyRanges, dailyRanges.size()-nPeriod, dailyRanges.size()-1);
					minPipsTp 		= (int) (avgRange*factorTP); 
					minPipsBounce	 = (int) (avgRange*factorBounce);
				}
				shortEnabled = false;
				longEnabled = false;
				actualLow = -1;
				actualHigh = -1;
				lastDay = day;
				dayTrades = 0;
				longEnabledValue = -1;
				shortEnabledValue = -1;
				maxRangeFromHigh = -1;
				maxRangeFromLow = -1;
			}
			
			//EVALUACION EN TIEMPO Q1
			//1) rango
			int actualRangeFromLow = q1.getHigh5()-actualLow;
			int actualRangeFromHigh = actualHigh-q1.getLow5();
			
			if (actualLow!=-1 && actualHigh!=-1){
				if (maxRangeFromLow==-1 || actualRangeFromLow>=maxRangeFromLow){
					maxRangeFromLow = actualRangeFromLow;
					//System.out.println(maxRangeFromLow);
				}
				if (maxRangeFromHigh==-1 ||actualRangeFromHigh>=maxRangeFromHigh){
					maxRangeFromHigh = actualRangeFromHigh;
				}
			}
			//2)posible activacion
			if (h>=h1 && h<=h2){
				if (maxMin1.getExtra()>=maxMinThr){ //posibilidad de reversal short
					shortEnabled = true;					
				}else if (maxMin1.getExtra()<=-maxMinThr){ //posibilidad de reversal long
					longEnabled = true;					
				}											
			}else{
				shortEnabled = false;
				longEnabled = false;
			}
						
			
			//3)evaluacion posiciones existentes (pendiente) y actualizacion estadisticas
			PositionShort.evaluatePositions(positions, qmaxmin, stats, comm, q, cal, i, debug);
			//EVALUACION EN TIEMPO Q
			totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
			//1) apertura nuevas posiciones
			if (totalOpen<maxTrades
					//&& dayTrades<2
					&& year>=y1 && year<=y2
					
					){
				int entry = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType posType = PositionType.NONE;
			
				if (longEnabled){	
					int diffValue = q.getOpen5()-actualLow;
					double perDiff = diffValue*1.0/maxRangeFromLow;
					//System.out.println(diffValue+" "+maxRangeFromLow+" "+perDiff);
					if (actualLow!=-1 
							//&& maxMin1.getExtra()>=maxMinThr
							//&& q1.getClose5()>q1.getOpen5()
							&& q1.getClose5()>q1000.getOpen5()
							//&& perDiff>=perThr1 && perDiff<=perThr2
							&& actualRangeFromLow>=minPipsBounce){
						//se abre trade largo en la apertura de q con stop por debajo de actualLow
						entry = q.getOpen5();
						slValue = actualLow;
						int diffSL = (int) (Math.abs(slValue-entry)*0.1);						
						if (diffSL>=maxStop){
							diffSL = maxStop;
						}else if (diffSL<=minStop){
							diffSL = minStop;
						}
						int diffTP = (int) (diffSL*factorTP);
						if (diffTP<=minTP){
							diffTP = minTP;
						}						
						slValue = q.getOpen5()-diffSL*10;
						tpValue = q.getOpen5()+diffTP*10;		
						posType = PositionType.LONG;
					}
				}else if (shortEnabled){
					int diffValue = actualHigh-q.getOpen5();
					double perDiff = diffValue*1.0/maxRangeFromHigh;
					//System.out.println( diffValue+" "+maxRangeFromHigh+" "+perDiff);
					if (actualHigh!=-1 
							//&& maxMin1.getExtra()<=maxMinThr
							//&& q1.getClose5()<q1.getOpen5()
							&& q1.getClose5()<q1000.getOpen5()
							//&& perDiff>=perThr1 && perDiff<=perThr2
							&& actualRangeFromHigh>=minPipsBounce){
						//se abre trade corto en la apertura de q con stop por encima de actualHigh
						entry = q.getOpen5();	
						slValue = actualHigh;
						int diffSL = (int) (Math.abs(slValue-entry)*0.1);						
						if (diffSL>=maxStop){
							diffSL = maxStop;
						}else if (diffSL<=minStop){
							diffSL = minStop;
						}
						int diffTP = (int) (diffSL*factorTP);
						if (diffTP<=minTP){
							diffTP = minTP;
						}
						
						slValue = q.getOpen5()+diffSL*10;
						tpValue = q.getOpen5()-diffTP*10;						
						posType = PositionType.SHORT;
					}					
				}
				if (entry!=-1){
					int sl = (int) (Math.abs(slValue-entry)*0.1);
					double minBalance = TestEAs.getMinBalanceRequiered(stats.getBalance(),risk,maxTrades,sl);
					if (stats.getBalance()<minBalance){
						extraNeeded += minBalance-stats.getBalance();
						stats.setBalance(minBalance);
					}
					
					
					long microLots = TestEAs.calculateMicroLots(stats.getBalance(),400,risk,maxTrades,sl);
					
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(posType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					pos.setMicroLots(microLots);
					positions.add(pos);
					//dayTrades++;
				}
			}
			
			//3)evaluacion posiciones existentes (pendiente) y actualizacion estadisticas
			PositionShort.evaluatePositions(positions, qmaxmin, stats, comm, q, cal, i, debug);
			
			//2) actualizamos low y high
			if (actualLow==-1 || q.getLow5()<=actualLow) actualLow = q.getLow5();
			if (actualHigh==-1 || q.getHigh5()>=actualHigh) actualHigh = q.getHigh5();
		}
		
		int totalTrades = stats.getWins()+stats.getLosses();
		double winPips	= stats.getWinPips();
		double lostPips = stats.getLostPips();
		double avgPips = (winPips-lostPips)/totalTrades;
		double pfPips = winPips/lostPips;
		double winPer = stats.getWins()*100.0/totalTrades;
		double avgPipsRisked = stats.getTotalRiskPips()*0.1/totalTrades;
		double yield = avgPips*100.0/avgPipsRisked;
		
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2			
				+" "+maxMinThr
				+" "+PrintUtils.Print2dec(factorBounce, false)
				+" "+PrintUtils.Print2dec(factorTP, false)
				+" "+PrintUtils.Print2dec(perThr1, false)+" "+PrintUtils.Print2dec(perThr2, false)
				+" "+stopOffset+" "+nPeriod
				+" "+minTP
				+" "+minStop
				+" "+maxStop
				+" "+maxTrades
				+" || "
				+" "+totalTrades
				+" "
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avgPips,false)
				+" "+PrintUtils.Print2dec(avgPipsRisked,false)
				+" "+PrintUtils.Print2dec(pfPips,false)
				+" "+PrintUtils.Print2dec(yield,false)
				+" "+PrintUtils.Print2dec2(stats.getBalance(),true)
				+" "+PrintUtils.Print2dec(balance+extraNeeded,false)
				+" "+PrintUtils.Print2dec(stats.getMaxDD(),true)
				);
	}

	public static void main(String[] args) throws Exception {
	
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathEURNZD = "C:\\fxdata\\EURNZD_UTC_5 Mins_Bid_2003.05.04_2016.03.30.csv";
		String pathUSDCAD = "C:\\fxdata\\USDCAD_UTC_5 Mins_Bid_2003.05.04_2016.03.29.csv";
		String pathAUDJPY = "C:\\fxdata\\AUDJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.29.csv";
		String pathEURGBP = "C:\\fxdata\\EURGBP_UTC_5 Mins_Bid_2003.05.04_2016.03.29.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.05.04_2016.03.29.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		paths.add(pathGBPJPY);
		paths.add(pathEURJPY);
		paths.add(pathNZDUSD);
		paths.add(pathEURNZD);
		paths.add(pathUSDCAD);
		paths.add(pathAUDJPY);
		paths.add(pathEURGBP);
		paths.add(pathEURAUD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 5;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<Quote> dataI 		= null;
		ArrayList<Quote> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			if (path.contains("pepper")){
				dataI 		= DAO.retrieveData(path, DataProvider.PEPPERSTONE_FOREX);
				dataS 		= dataI;
				provider="pepper";
			}else if (path.contains("forexdata")){
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX2);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="forexdata";
			}else{
				dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				dataS 		= TestLines.calculateCalendarAdjusted(dataI);
				provider="dukasc";
			}				
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			
			int beginInicial = 1;
			int begin = beginInicial;
			int end = data.size()-1;
			int boxes = 1;
			int boxSize = end/boxes;
			
			double comm = 2.0;
			System.out.println("total data: "+data.size()+" "+boxSize);
			ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			
			for (int nPeriod=40;nPeriod<=40;nPeriod++){
				for (int stopOffset = 0; stopOffset<=0;stopOffset++){
					for (double factorTP= 0.10;factorTP<=0.10;factorTP+=0.01){
						for (double factorBounce = 0.45;factorBounce<=0.45;factorBounce+=0.01){
							for (int y1=2016;y1<=2016;y1++){
								int y2 = y1+3;
								for (int h1=0;h1<=0;h1++){
									int h2 = h1+23;
									for (int maxTrades = 1;maxTrades<=1;maxTrades++){
										for (int minTP = 20 ; minTP<=20;minTP+=5){
											for (int minStop = 20 ; minStop<=20;minStop+=1){
												for (int maxStop = 70;maxStop<=70;maxStop+=10){
													for (double perThr1=0;perThr1<=0;perThr1+=0.05){
														double perThr2 = 9999;
														for (int maxMinThr=70;maxMinThr<=70;maxMinThr+=500){
															for (double risk=1.0;risk<=10.0;risk+=1.0){
																for (int lookup=1;lookup<=1;lookup+=1000){
																	SwingStop.doTrade(data, maxMinsExt,y1,y2, h1, h2, 
																			maxMinThr,perThr1,perThr2, factorBounce, factorTP, stopOffset, nPeriod, 
																			maxTrades,minTP,minStop,maxStop, 2000,risk, comm,false,
																			lookup);
																	
																}
															}
														}//maxMinThr
													}//perthr1
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

	}

}
