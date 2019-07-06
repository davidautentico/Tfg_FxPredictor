package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.GlobalStats;
import drosa.experimental.PositionShort;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.FileUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMomentumDetail {
	
	public static ArrayList<Double> test(String header,GlobalStats stats,ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,int begin,int end,
			int h1,int h2,
			int maxMinOpen,int maxMinClose,
			double sl,
			int maxOpens,
			int atrPeriod,
			double comm,
			boolean debug){
		
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		ArrayList<Double> dailyEquitity = new ArrayList<Double>();
		int totalDays = 0;
		int lastDay = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		Calendar cal = Calendar.getInstance();
		boolean canAdd =true;
		int actualMax = -1;
		int actualMin = -1;
		int max = -1;
		int min = -1;
		double avgRange = 100;
		double avgRangeFactor = avgRange*sl;
		double totalPipsRisked = 0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay!=-1){
					double equitityOpens = PositionShort.calculatePips(positions,q.getOpen5());
					double actualEquitity = stats.getWinPips()-stats.getLostPips()+equitityOpens;
					dailyEquitity.add(actualEquitity);
					dailyRanges.add((int) ((actualMax-actualMin)*0.1));
					//int opens = PositionShort.
					/*System.out.println(
							PrintUtils.Print2dec(stats.getWinPips()-stats.getLostPips(),false)
							+" "+PrintUtils.Print2dec(equitityOpens,false)
							+" "+PrintUtils.Print2dec(actualEquitity,false)
							);*/
				}
				double atr = MathUtils.average(dailyRanges,totalDays-atrPeriod,totalDays-1);
				avgRangeFactor = atr*sl;
				max = -1;
				min = -1;
				actualMax = -1;
				actualMin = -1;
				canAdd=true;
				lastDay = day;
				totalDays++;
			}
			
			//si se cumple la señal (high/low anterior)
			int maxMin = maxMins.get(i-1).getExtra();
			//open signal
			int entry = -1;
			int slValue = -1;
			int tpValue = -1;
			PositionType positionType = PositionType.NONE;
			if (maxMin>=maxMinOpen
					
					){
				entry = q.getOpen5();
				slValue = (int) (entry - 10*avgRangeFactor);
				//totalPipsRisked += avgRangeFactor;
				/*if (actualMin!=-1){
					double pips = (q.getOpen5()-actualMin)*sl;
					slValue = (int) (entry-pips);
				}*/
				tpValue = 99999999;
				positionType = PositionType.LONG;
			}else if (maxMin<=-maxMinOpen){
				entry = q.getOpen5();
				slValue = (int) (entry + 10*avgRangeFactor);
				//totalPipsRisked += avgRangeFactor;
				/*if (actualMax!=-1){
					double pips = (actualMax-q.getOpen5())*sl;
					slValue = (int) (entry+pips);
				}*/
				tpValue = 0;
				positionType = PositionType.SHORT;
			}
			
			if (entry!=-1
					&& h1<=h && h<=h2
					){
				int actualOpens = PositionShort.countTotal(positions, PositionStatus.OPEN);
				if (actualOpens<maxOpens && canAdd){
					//if (positions.size()==0
							//|| (positions.size()>0 
							//		&& positions.get(0).getPositionType()==positionType)
							//){
						PositionShort.moveToBE(positions,q.getOpen5(),(int)avgRangeFactor,true);
						
						PositionShort pos = new PositionShort();
						pos.setEntry(entry);
						pos.setSl(slValue);
						pos.setTp(tpValue);
						pos.setPositionType(positionType);
						pos.setPositionStatus(PositionStatus.OPEN);
						positions.add(pos);
						//canAdd = false;
						totalPipsRisked += avgRangeFactor;
					//}
				}
			}
			//close signal
			if (maxMin>=maxMinClose){//cerramos cortos
				PositionShort.closeAllPositionsByValue(stats, positions,PositionType.SHORT, q.getOpen5(), i, comm);
			}else if (maxMin<=-maxMinClose){//cerramosLargos
				PositionShort.closeAllPositionsByValue(stats, positions,PositionType.LONG, q.getOpen5(), i, comm);
			}
			//ver si ha saltado el SL
			PositionShort.evaluatePositions(stats, positions, q, cal, i, false, comm, debug);
			
			if (actualMax==-1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
			if (actualMin==-1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
		}//for
		
		PositionShort.closeAllPositionsByValue(stats, positions,PositionType.SHORT,data.get(data.size()-1).getClose5(), data.size()-1, comm);
		PositionShort.closeAllPositionsByValue(stats, positions,PositionType.LONG,data.get(data.size()-1).getClose5(), data.size()-1, comm);
		
		int total = stats.getWins()+stats.getLosses();
		double winPer = stats.getWins()*100.0/total;
		double pf = stats.getWinPips()/stats.getLostPips();
		double avgPips = (stats.getWinPips()-stats.getLostPips())*1.0/total;
		double avgPipsRisked= totalPipsRisked*1.0/total;
		
		if (debug)
		System.out.println(
				header+" || "
				+maxMinOpen
				+" "+maxMinClose
				+" "+PrintUtils.Print2dec(sl,false)
				+" || "
				+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(stats.getWinPips()*1.0/stats.getWins(), false)
				+" "+PrintUtils.Print2dec(stats.getLostPips()*1.0/stats.getLosses(), false)
				+" "+PrintUtils.Print2dec(avgPipsRisked, false)
				+" "+PrintUtils.Print2dec(avgPips/avgPipsRisked, false)
				);
		
		return dailyEquitity;
	}
	
	public static void testFiles(ArrayList<String> files,
			boolean debug
			) throws Exception{
		
		ArrayList<ArrayList<QuoteShort>> datas = new ArrayList<ArrayList<QuoteShort>>();
		ArrayList<ArrayList<QuoteShort>> maxMinss = new ArrayList<ArrayList<QuoteShort>>();
				
		//calculo de los arrays de datos
		for (int i=0;i<files.size();i++){
			Sizeof.runGC ();
			String fileName = files.get(i);
			ArrayList<QuoteShort> data = FileUtils.extractData(fileName);
			ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			datas.add(data);
			maxMinss.add(maxMins);
			System.out.println("añadido: "+fileName);
		}
		
		System.out.println("todos calculados");
		for (int atr=20;atr<=20;atr++){
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+23;
				for (double sl=1.0;sl<=1.0;sl+=0.1){
					double avgPF = 0;
					int total = 0;
					for (int maxMinOpen=1000;maxMinOpen<=40000;maxMinOpen+=500){
						//for (int maxMinClose=(int) (maxMinOpen*1.0);maxMinClose<=maxMinOpen*1.0;maxMinClose+=maxMinOpen*0.1){
						for (int maxMinClose=10000;maxMinClose<=10000;maxMinClose+=1000){
							for (int maxOpens=1000;maxOpens<=1000;maxOpens++){
								TestMomentumDetail.testWithStats(datas,maxMinss,h1, h2, maxMinOpen, maxMinClose, sl, maxOpens, atr, 0.0, false);
							}
						}
					}
				}
			}
		}
	}

	private static void testWithStats(ArrayList<ArrayList<QuoteShort>> datas,
			ArrayList<ArrayList<QuoteShort>> maxMinss,
			int h1,int h2,
			int maxMinOpen, int maxMinClose,double sl, int maxOpens,
			int atrPeriod,double comm,
			boolean debug
			) throws Exception {
		
		double totalWinPips = 0;
		double totalLostPips = 0;
		int totalTrades = 0;
		for (int i=0;i<datas.size();i++){
			//String fileName = files.get(i);
			//ArrayList<QuoteShort> data = FileUtils.extractData(fileName);
			//ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			ArrayList<QuoteShort> data = datas.get(i);
			ArrayList<QuoteShort> maxMins = maxMinss.get(i);
			int begin = 1;
			int end = data.size()-1;
			GlobalStats stats = new GlobalStats();
			TestMomentumDetail.test("Global", stats, data, maxMins, begin, end, h1, h2, maxMinOpen, maxMinClose, sl, maxOpens, atrPeriod, comm, debug);
			totalWinPips += stats.getWinPips();
			totalLostPips += stats.getLostPips();
			totalTrades += stats.getWins()+stats.getLosses();
			Sizeof.runGC ();
		}
		
		double pf = totalWinPips/totalLostPips;
		double avg = (totalWinPips-totalLostPips)*1.0/totalTrades;
		System.out.println(
				h1+" "+h2
				+" "+maxMinOpen+" "+maxMinClose
				+" "+PrintUtils.Print2dec(sl, false)
				+" "+maxOpens
				+" "+atrPeriod
				+" || "
				+totalTrades
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}

	public static void main(String[] args) throws Exception {
		ArrayList<String> currencies = new ArrayList<String>();
		currencies.add("eurusd");
		currencies.add("gbpusd");
		currencies.add("audusd");
		currencies.add("usdjpy");
		currencies.add("eurjpy");
		currencies.add("gbpjpy");
		currencies.add("audjpy");
		//currencies.add("nzdusd");
		int index=0;
		int limit=currencies.size()-1;
		//limit=3;
		ArrayList<String> files = new ArrayList<String>();
		for (int c=index;c<=limit;c++){
			String currency = currencies.get(c);
			//String path1 = "c:\\fxdata\\"+currency+"_forexdata_5min_1986_2012.csv";
			String	path1 = "c:\\fxdata\\"+currency+"_UTC_5 Mins_Bid_2003.12.31_2015.09.05.csv";
			String header = currency;
			files.add(path1);
		}
		
		TestMomentumDetail.testFiles(files,false);
		
		
		/*int index=3;
		int limit=currencies.size()-1;
		limit=3;
		for (int c=index;c<=limit;c++){
			String currency = currencies.get(c);
			String path1 = "c:\\fxdata\\"+currency+"_forexdata_5min_1986_2012.csv";
			//String	path1 = "c:\\fxdata\\"+currency+"_UTC_5 Mins_Bid_2003.12.31_2015.09.05.csv";
			String header = currency;
			ArrayList<String> files = new ArrayList<String>();
			files.add(path1);
			
			for (int i=0;i<files.size();i++){
				String path5m = files.get(i);
				//System.out.println(path5m);
				ArrayList<Quote> dataI 		= null;
				ArrayList<Quote> dataS     = null;
				if (path5m.contains("pepper")){
					dataI 		= DAO.retrieveData(path5m, DataProvider.PEPPERSTONE_FOREX);
					dataS 		= dataI;
					//provider="pepper";
				}else if (path5m.contains("forexdata")){
					dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX2);
					dataS 		= TestLines.calculateCalendarAdjusted(dataI);
					//provider="forexdata";
				}else{
					dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
					dataS 		= TestLines.calculateCalendarAdjusted(dataI);
					//provider="dukasc";
				}					
				//ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
				//ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
				ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
				//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
				ArrayList<QuoteShort> data = null;
				dataI.clear();
				dataS.clear();
				data5m.clear();
				data = data5mS;
				
				//System.out.println("size: "+data.size());
				int begin = 1;
				int end = data.size()-1;
				//end = 9900000;
				
				ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
				
				for (int atr=20;atr<=20;atr++){
					for (int h1=0;h1<=0;h1++){
						int h2 = h1+23;
						for (double sl=1.30;sl<=1.30;sl+=0.1){
							double avgPF = 0;
							int total = 0;
							for (int maxMinOpen=20000;maxMinOpen<=20000;maxMinOpen+=500){
								//for (int maxMinClose=(int) (maxMinOpen*1.0);maxMinClose<=maxMinOpen*1.0;maxMinClose+=maxMinOpen*0.1){
								for (int maxMinClose=10000;maxMinClose<=10000;maxMinClose+=1000){
									for (int maxOpens=1000;maxOpens<=1000;maxOpens++){
										for (int begin1=begin;begin1<=begin;begin1+=80000){
											int end1 = end;
											GlobalStats stats = new GlobalStats();
											ArrayList<Double> dailyEquitity = TestMomentumDetail.test(currency,stats, data, maxMins, begin1, end1, h1,h2,
													maxMinOpen, maxMinClose, sl,maxOpens,atr, 0.0, false);
											PrintUtils.print(dailyEquitity);
											//studyResults(dailyEquitity,10);
											total++;
											//avgPF +=pf;
										}
									}
								}
							}
							//System.out.println(PrintUtils.Print2dec(avgPF*1.0/total, false));
						}
					}
				}
			}//files
		}//c*/

	}

	private static void studyResults(ArrayList<Double> values, int boxes) {
		// TODO Auto-generated method stub
		int size = values.size();
		int boxSize = size/boxes;
		
		for (int i=0;i<boxes;i++){
			int begin = i*boxSize;
			int end = begin+boxSize;
			if (i==boxes-1 || end > values.size()-1) end = values.size()-1;
			
			double valueB = values.get(begin);
			double valueE = values.get(end);
			System.out.println(PrintUtils.Print2dec(valueE-valueB, false));
		}
	}

}
