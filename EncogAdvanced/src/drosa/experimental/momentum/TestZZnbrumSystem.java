package drosa.experimental.momentum;

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
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestZZnbrumSystem {

	/**
	 * Se llevan las cuentas de trends
	 * @param data
	 */
	public static void doTrade(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMinsExt,
			int begin,int end,
			int y1,int y2,
			int month1,int month2,
			int dayWeek1,int dayWeek2,
			int minSize,
			String hours,int hClose,
			int minAttempts,int maxAttemps,
			int actualSizeThr1,int actualSizeThr2,
			double tpAtr,double slAtr,
			int minTp,
			int maxTime,
			int COdiffThr,
			int diffValues,
			double factorPending,
			double perBE,
			int maxTrades,
			int nAtr,
			double balance,
			double risk,
			double comm,
			boolean debug,boolean debugTrading){
		
		int cases00 = 0;
		int fails = 0;
		
		ArrayList<Integer> countedDays = new ArrayList<Integer>();
		ArrayList<Integer> countedTrends = new ArrayList<Integer>();
		for (int i=0;i<=287;i++){
			countedDays.add(0);
			countedTrends.add(0);
		}
		
		
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		ArrayList<Integer> hoursThr = TradingUtils.decodeHours(hours);
		
		//parametros trading
		GlobalStats stats = new GlobalStats();
		stats.setBalance(balance);
		stats.setMaxBalance(balance);
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		double actualBalance = balance;
		double extraNeeded = 0;
		int totalTrades = 0;
		int wins = 0 ;
		double totalPips = 0;
		double winPips = 0;
		double lostPips = 0;
		
		ArrayList<Integer> total8 = new ArrayList<Integer>();
		ArrayList<Integer> totalReached9 = new ArrayList<Integer>();
		for (int i=0;i<100;i++){
			total8.add(0);
			totalReached9.add(0);
		}
		Calendar cal = Calendar.getInstance();
		Calendar cal_1 = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		Calendar calLeg = Calendar.getInstance();
		QuoteShort qmaxmin = new QuoteShort();
		int actualLeg = 0;
		int index1=-1;
		int index2=-1;
		int index0=-1;
		int indexHigh = -1;
		int indexLow = -1;
		int actualHigh = -1;
		int actualLow = -1;
		int lastDay = -1;
		QuoteShort q1 = null;
		QuoteShort q2 = null;		
		int actualDayTrends = 0;
		int totalReached8 = 0;
		int totalReached = 0;
		int totalDays = 0;
		boolean reached = false;
		int totalDayTrades = 0;
		int totalRiskedPips = 0;
		int consecWins = 0;
		int actualWins = 0;
		PositionType positionType = PositionType.NONE;
		PositionStatus positionStatus = PositionStatus.NONE;
		int entryValue = -1;
		int slValue = -1;
		int tpValue = -1;
		int tp = (int) (100*tpAtr);
		int sl = (int) (100*slAtr);
		int pendingIndex = -1;
		if (begin<1) begin = 1;
		int doValue = 0;
		//BUCLE PRINCIPAL
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal_1, q_1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int minute = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
			if (month<month1 || month>month2) continue;
			if (year<y1 || year>y2) continue;
			
			//nuevo dia
			if (day!=lastDay){	
				//cerramos todos los trades
				//PositionShort.closeAllPositionsByValue(stats, positions, q.getOpen5(), i, comm);
				
				double slFactor = slAtr/tpAtr;
				int dailyRange = (int) ((actualHigh-actualLow)*0.1);
				dailyRanges.add(dailyRange);
				double range = MathUtils.average(dailyRanges,totalDays-nAtr,totalDays);
				//double range10 = MathUtils.average(dailyRanges,i-10,i);
				//double range20 = MathUtils.average(dailyRanges,i-20,i);
				//System.out.println(range+" "+range10+" "+range20);
				tp = (int) (range*tpAtr);
				sl = (int) (range*slAtr);
				if (tp<=minTp){
					tp=minTp;
					sl=(int) (tp*slFactor);
				}
				
				//tp = 5;
				//sl = 40;
				
				//System.out.println(dailyRange+" "+range+" "+tp+" "+sl);
				doValue = q.getOpen5();
				index0=i;
				index1=i;
				index2=i;
				actualHigh = -1;
				actualLow = -1;
				actualLeg = 0;
				actualDayTrends = 0;
				
												
				totalReached8=0;
				reached = false;				
				totalDays++;
				lastDay = day;
				//parametros trading
				totalDayTrades=0;
			}
			
			if (index1==-1){
				index0 = 0;
				index1 = i;
				index2 = i;
			}
			//actualizacion de legs
			q1 = data.get(index1);
			q2 = data.get(index2);	
			int maxMin = maxMinsExt.get(i-1).getExtra();//se mira que hizo el anterior..
			int totalOpenTrades = PositionShort.countTotal(positions, PositionStatus.OPEN);
			int COdiff = q_1.getClose5()-q_1.getOpen5();
			int HCdiff = q_1.getHigh5()-q_1.getClose5();
			int LCdiff = q_1.getClose5()-q_1.getLow5();
			positionType=PositionType.NONE;
			entryValue = -1;
			//actualizacion de operaciones
			int maxMinThr = hoursThr.get(h);
			if (maxMinThr!=-1
					&& totalOpenTrades<maxTrades
					//&& actualDayTrends<=minAttempts
					){
				positionType = PositionType.NONE;
				
				boolean maxPassed = true;
				if (maxMin<maxMinThr) maxPassed = false;
				if (maxPassed){					
					int actualSize = (int) ((q.getOpen5()-q1.getLow5())*0.1);
					if (actualLeg==1
							&& actualSize>=actualSizeThr1 && actualSize<=actualSizeThr2
							//&& COdiff<COdiffThr*10
							//&& COdiff<-COdiffThr
							//&& HCdiff<-COdiffThr
							){						
						positionStatus = PositionStatus.OPEN;
						entryValue = q.getOpen5();
						//if (debugTrading)
							//System.out.println("[OPEN MARKET SHORT] "+entryValue+" || "+q.toString()+" || "+actualSize);
						positionType=PositionType.SHORT;												
					}										
					slValue = entryValue+10*sl;
					tpValue = entryValue-10*tp;
				}else{
					maxPassed = true;
					if (maxMin>-maxMinThr) maxPassed = false;
					if (maxPassed){
						int actualSize = (int) ((q1.getHigh5()-q.getOpen5())*0.1);
						if (actualLeg==-1 
								&& actualSize>=actualSizeThr1 && actualSize<=actualSizeThr2
								//&& COdiff>-COdiffThr*10
								//&& COdiff>COdiffThr
								//&& LCdiff>COdiffThr
								){
							positionStatus = PositionStatus.OPEN;
							entryValue = q.getOpen5();
							//if (debugTrading)
								//System.out.println("[OPEN MARKET LONG] "+entryValue+" || "+q.toString()+" || "+actualSize);
							positionType=PositionType.LONG;							
						}					
					
						slValue = entryValue-10*sl;
						tpValue = entryValue+10*tp;					
					}
				}
				
				if (entryValue!=-1){
					actualBalance = stats.getBalance();
					//margen requerido
					double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,risk,maxTrades,sl);
					if (actualBalance<minBalance){
						extraNeeded += minBalance-actualBalance;
						actualBalance = minBalance;
					}
					
					long microLots = TestEAs.calculateMicroLots(actualBalance,400,risk,maxTrades,sl);				
					
					PositionShort pos = new PositionShort();
					pos.setPositionType(positionType);
					pos.setPositionStatus(positionStatus);
					pos.setMicroLots(microLots);
					pos.setEntry(entryValue);
					pos.setTp(tpValue);
					pos.setSl(slValue);
					pos.setOpenIndex(i);
					pos.setPendingIndex(pendingIndex);
					pos.setId(0);
					
					//test 00 factor
					int doValue3 = doValue / 1000;
					int entryValue3 = entryValue/1000;		
					int diff = Math.abs(doValue-entryValue);
					if (//doValue3!=entryValue3 
							//&& 
							diff>=diffValues
							){
						//System.out.println(diff+" "+diffValues);
						pos.setId(1);
					}
					//
					
					if (positionStatus ==PositionStatus.OPEN){
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					}
					positions.add(pos);
					if (debugTrading){
						//System.out.println("[OPEN]: "+pos.toString()+" || "+q.toString());	
					}
				}
			}
			
			//evalualicion del trading
			PositionShort.evaluatePositions(positions, qmaxmin, stats, comm, q, cal2, i, debugTrading);
			
			//if (h>=hClose){
				//PositionShort.closeAllPositionsByValue(stats, positions, q.getOpen5(), i, comm);
			//}
			
						
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
				indexHigh = i;
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow = q.getLow5();
				indexLow = i;
			}
			
			int actualPoint = h*12+minute/5;
			
			if (actualLeg==0){
				double longDiff0 = (q.getHigh5()-data.get(indexLow).getLow5())*0.1;
				double shortDiff0 = (data.get(indexHigh).getHigh5()-q.getLow5())*0.1;
				//System.out.println(longDiff0+" "+shortDiff0+" "+i+" "+index1);
				if (longDiff0>=minSize 
						&& indexLow!=i
						){
					index1 = indexLow;
					actualLeg = 1;
					index2 = i;
					actualDayTrends++;
					if (debug)
					System.out.println("[NUEVA LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" || "+q.getClose5());
					
					
					QuoteShort.getCalendar(calLeg, data.get(index1));
					int hIndex1   = calLeg.get(Calendar.HOUR_OF_DAY);
					int minIndex1 = calLeg.get(Calendar.MINUTE);
					int actualPoint1 = hIndex1*12+minIndex1/5;
					int cd = countedDays.get(actualPoint1);
					countedDays.set(actualPoint1,cd+1);

					int ct = countedTrends.get(actualPoint);
					countedTrends.set(actualPoint, ct+1);
				}else if (shortDiff0>=minSize  
						&& indexHigh!=i
						){
					index1 = indexHigh;
					actualLeg = -1;
					index2 = i;
					actualDayTrends++;
					if (debug)
					System.out.println("[NUEVA LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" || "+q.getClose5());
				
					
					QuoteShort.getCalendar(calLeg, data.get(index1));
					int hIndex1   = calLeg.get(Calendar.HOUR_OF_DAY);
					int minIndex1 = calLeg.get(Calendar.MINUTE);
					int actualPoint1 = hIndex1*12+minIndex1/5;
					int cd = countedDays.get(actualPoint1);
					countedDays.set(actualPoint1,cd+1);

					int ct = countedTrends.get(actualPoint);
					countedTrends.set(actualPoint, ct+1);
				}
			}else if (actualLeg==1){
				int lowDiff = (int) ((q2.getHigh5()-q.getLow5())*0.1);
				//System.out.println("LONG "+shortDiff);
				if (q.getHigh5()>=q2.getHigh5()){
					int actualSize = (int) ((q.getHigh5()-q1.getLow5())*0.1);
					if (index0<=0 && actualSize>=minSize) index0 = i;
					index2 = i;
					//se hace un nuevo maximo valdria como turning point
					int actualPoint1 = h*12+minute/5;
					int cd = countedDays.get(actualPoint1);
					countedDays.set(actualPoint1,cd+1);
					
					if (h==9){
						//System.out.println("[AMPLIACION LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" "+actualSize+" "+(cd+1));
					}
					
					//if (h==9 || h==10)
					//System.out.println("[AMPLIACION LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" "+actualSize);
				}else if (lowDiff>=minSize){
					//Trend t = new Trend(q1,q2,index0,index1,index2,1);
					//System.out.println("[AÑADIENDO TRNE: "+t.toString());
					actualLeg = -1;
					index0 = -1;
					index1 = index2;
					index2 = i;
					actualDayTrends++;	
					QuoteShort.getCalendar(calLeg, data.get(index1));
					int hIndex1   = calLeg.get(Calendar.HOUR_OF_DAY);
					int minIndex1 = calLeg.get(Calendar.MINUTE);
					if (debug)
					//if (hIndex1==9)
						System.out.println("[NUEVA LEG] "+DateUtils.datePrint(calLeg)+" "+actualLeg
								+" "+q2.getHigh5()+" "+q.getLow5()
								+" || "+actualDayTrends
								+" || "+q.getClose5()
								);
					
					//aqui hay que señalidar la trend de index1, donde se inicio
					
					
					int actualPoint1 = hIndex1*12+minIndex1/5;
					int ct = countedTrends.get(actualPoint1);
					countedTrends.set(actualPoint1,ct+1);
					
					//es un turning point asi que se pone
					actualPoint1 = h*12+minute/5;
					int cd = countedDays.get(actualPoint1);
					countedDays.set(actualPoint1,cd+1);
				}
			}else if (actualLeg==-1){//BEAR
				int highDiff = (int) ((q.getHigh5() -q2.getLow5())*0.1);
				if (q.getLow5()<=q2.getLow5()){
					int actualSize = (int) ((q1.getHigh5()-q.getLow5())*0.1);
					if (index0<=0 && actualSize>=minSize) index0 = i;
					index2 = i;
					//se hace nuevo minimo y cualifica como posible turning point
					int cd = countedDays.get(actualPoint);
					countedDays.set(actualPoint,cd+1);
					
					if (h==9){
						//System.out.println("[AMPLIACION LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" "+actualSize+" "+(cd+1));
					}
					//System.out.println("[AMPLIACION LEG] "+DateUtils.datePrint(cal)+" "+actualLeg+" "+actualSize);
				}else if (highDiff>=minSize){
					actualLeg = 1;
					index0 = -1;
					index1 = index2;
					index2 = i;
					actualDayTrends++;	
					QuoteShort.getCalendar(calLeg, data.get(index1));
					int hIndex1   = calLeg.get(Calendar.HOUR_OF_DAY);
					int minIndex1 = calLeg.get(Calendar.MINUTE);
					if (debug)
					//if (hIndex1==9)
						System.out.println("[NUEVA LEG] "+DateUtils.datePrint(calLeg)+" "+actualLeg
								+" "+q2.getLow5()+" "+q.getHigh5()
								+" || "+actualDayTrends
								+" || "+q.getClose5()
								);
					
					//aqui hay que señalidar la trend de index1, donde se inicio
					QuoteShort.getCalendar(calLeg, data.get(index1));
					int actualPoint1 = hIndex1*12+minIndex1/5;
					int ct = countedTrends.get(actualPoint1);
					countedTrends.set(actualPoint1,ct+1);
					
					//es un turning point asi que se pone
					actualPoint1 = h*12+minute/5;
					int cd = countedDays.get(actualPoint1);
					countedDays.set(actualPoint1,cd+1);
				}
			}						
		}
		
		//trading
		wins = stats.getWins();
		totalTrades = stats.getWins()+stats.getLosses();
		int losses = totalTrades-wins;
		totalPips = stats.getWinPips()-stats.getLostPips();
		double perWin = wins*100.0/totalTrades;
		double avgPips = totalPips*1.0/totalTrades;
		double avgRiskPips = stats.getTotalRiskPips()*0.1/totalTrades;
		double pf = stats.getWinPips()*1.0/stats.getLostPips();
		double deposit = balance+extraNeeded;
		double perBal = stats.getBalance()*100.0/deposit-100.0;
		double winPerSpecial = stats.getSpecialWinsTrades()*100.0/stats.getSpecialTotalTrades();
		double avgSpecialPips = (stats.getSpecialWinsPips()-stats.getSpecialLostPips())*1.0/stats.getSpecialTotalTrades();
		double pfSpecial = stats.getSpecialWinsPips()/stats.getSpecialLostPips();
		
		double yield = avgPips*100.0/(avgRiskPips);
		
		
		System.out.println(
				hours
				+" "+PrintUtils.Print2dec(comm, false)
				+" "+PrintUtils.Print2dec(risk, false)
				+" "+minSize
				+" "+actualSizeThr1+" "+actualSizeThr2				
				+" "+PrintUtils.Print2dec(tpAtr, false)+" "+PrintUtils.Print2dec(slAtr, false)+" "+minTp
				+" "+PrintUtils.Print2Int(maxTime, 3)
				+" || "+PrintUtils.Print2Int(totalTrades, 6)
				+" "+PrintUtils.Print2dec(perWin, false)
				+" "+stats.getSpecialTotalTrades()
				+" "+PrintUtils.Print2dec(winPerSpecial, false)
				+" "+PrintUtils.Print2dec(avgPips, false)
				+" "+PrintUtils.Print2dec(avgSpecialPips, false)
				+" "+PrintUtils.Print2dec(stats.getWinPips()/wins, false)
				+" "+PrintUtils.Print2dec(stats.getLostPips()/losses, false)
				+" "+PrintUtils.Print2dec(stats.getWinPips(),false,10)
				+" "+PrintUtils.Print2dec(stats.getLostPips(),false, 3)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(pfSpecial, false)
				+" "+stats.getTotalRiskPips()
				+" "+PrintUtils.Print2dec(yield, false)
				+" || "				
				+PrintUtils.Print2dec2(deposit,true)
				+" "+PrintUtils.Print2dec2(stats.getBalance(),true)
				+" "+PrintUtils.Print2dec2(stats.getMaxBalance(),true)
				+" "+PrintUtils.Print2dec(perBal,false)
				+" "+PrintUtils.Print2dec(stats.getMaxDD(),false)
				+" "+PrintUtils.Print2dec(perBal/stats.getMaxDD(),false)
				+" "+stats.getMaxConsecutiveWins()+" "+stats.getMaxConsecutiveLosses()
				);
		
		//numero de trends
		/*for (int i=0;i<=287;i++){
			int cd = countedDays.get(i);
			int ct = countedTrends.get(i);
			int h = i/12;
			int min = (i-h*12)*5;
			double per = ct*100.0/cd;
			System.out.println(
					PrintUtils.Print2Int(h,2)
					+" "+PrintUtils.Print2Int(min,2)
					+" "+totalDays
					+" "+PrintUtils.Print2Int(cd,3)
					+" "+PrintUtils.Print2Int(ct,3)
					+" "+PrintUtils.Print2dec(per, false, 2)
					);
		}*/
	}
	
	public static void main(String[] args) throws Exception {
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_30 Mins_Bid_2003.05.04_2015.11.25.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2016.03.07.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.05.03.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2013_08_29_2016_01_04_GAPS.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2015.11.25.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2015_10_28_2015_01_04.csv";
		
		//String pathEURUSD = "C:\\fxdata\\gbpjpy_UTC_5 Mins_Bid_2008.12.31_2015.10.11.csv";
		
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.12.31_2015.10.06.csv";		
		
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.05.04_2016.03.07.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		String pathNZDUSD = "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.17.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathEURJPY);
		paths.add(pathGBPUSD);paths.add(pathGBPJPY);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
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
			
			double comm = 0.0;
			System.out.println("total data: "+data.size()+" "+boxSize);
			ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			for (int h1=0;h1<=0;h1++){
				for (int h2=h1+0;h2<=h1+0;h2++){
					for (int h3=0;h3<=0;h3++){
						for (int h4=h3+0;h4<=h3+0;h4++){
							for (int hClose=0;hClose<=0;hClose++){
								for (int att=0;att<=0;att++){
									for (int actualSize1=12;actualSize1<=12;actualSize1+=1){//defecto 12
										//for (int actualSize2=(int) (2.5*actualSize1);actualSize2<=2.5*actualSize1;actualSize2+=1){ //defecto 48
										for (int actualSize2=actualSize1+36;actualSize2<=actualSize1+36;actualSize2+=1){
											for (int legSize=20;legSize<=20;legSize+=1){ //defecto 20
												for (double tp=0.08;tp<=0.08;tp+=0.01){//defecto 0.08
													for (double sl=2.0*tp;sl<=2.0*tp;sl+=0.5*tp){//defecto 0.16
													//for (double sl=1.0;sl<=1.0;sl+=1){//
													 //for (double tp=0.5*sl;tp<=100*sl;tp+=0.5*sl){//
														for (int nAtr=14;nAtr<=14;nAtr+=1){
															for (int y1=2003;y1<=2016;y1+=1){
																int y2 = y1+0;
																for (int COdiffThr=0;COdiffThr<=0;COdiffThr+=1){
																	for (double perBE=2.0;perBE<=2.0;perBE+=0.1){
																		for (double factorPending=0.0;factorPending<=0.0;factorPending+=0.1){
																			for (int maxTrades=7;maxTrades<=7;maxTrades++){
																				for (double risk=5.0;risk<=5.0;risk+=0.1){
																					//String hours ="125 550 450 450 475 475 475 400 575 500";
																					String ha0 ="95";String ha1 ="550";String ha2 ="450";String ha3 ="450";String ha4 ="475";
																					String ha5 ="475";String ha6 ="475";String ha7 ="400";String ha8 ="575";String ha9 ="500";
																					String ha10 ="-1";String ha11 ="-1";String ha12 ="-1";String ha13 ="-1";String ha14 ="-1";
																					String ha15 ="-1";String ha16 ="-1";String ha17 ="-1";String ha18 ="-1";String ha19 ="-1";
																					String ha20 ="-1";String ha21 ="-1";String ha22 ="-1";String ha23 ="-1";
																					String hours =ha0+" "+ha1+" "+ha2+" "+ha3+" "+ha4+" "+ha5+" "+ha6+" "+ha7+" "+ha8
																							+" "+ha9+" "+ha10+" "+ha11+" "+ha12+" "+ha13+" "+ha14+" "+ha15+" "+ha16
																							+" "+ha17+" "+ha18+" "+ha19+" "+ha20+" "+ha21+" "+ha22+" "+ha23;
																					for (int mm=500;mm<=500;mm+=10){
																						//hours = fillHours(h1,h1+0,mm,-1);
																						//hours = fillHours(0,9,mm,-1);
																						for (comm=2.0;comm<=2.0;comm+=0.01){
																							for (int diffValues=0;diffValues<=0;diffValues+=100){
																								for (int month1 = Calendar.JANUARY;month1<=Calendar.JANUARY+6;month1+=6){
																									int month2 = month1 + 5;
																									for (int dayWeek1 = Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
																										int dayWeek2 = dayWeek1+4;
																										for (int minTp=18;minTp<=18;minTp++){																									
																											TestZZnbrumSystem.doTrade(data,maxMinsExt, begin, end,
																												y1,y2, 
																												month1,month2,
																												dayWeek1,dayWeek2,
																												legSize,hours,hClose,att,att,
																												actualSize1,actualSize2,tp,sl,minTp,100000,
																												COdiffThr,
																												diffValues,
																												factorPending,perBE,
																												maxTrades,nAtr,10000,risk,comm,false,false);
																										}
																									}
																								}
																							}
																						}
																					}
																				}
																				//TestZZnbrumSystem.doTrade(data,maxMinsExt, begin, end,y1,y1, tp,h1,h2,h3,h4,att,att,actualSize+99999,maxMinThr,tp,sl,false,false);
																			}
																		}//factorPending
																	}
																}//COdiffThr
															}
														}//natr
													}													
												}
											}//legSize
										}
									}
								}
							}//hClose
						}
					}
				}
			}			
			//TestZZnbrumSystem.doTrade(data, begin, end, 20,0,15,16,16,15);			
		}
	}

	private static String fillHours(int h1, int h2, int mm, int def) {
		// TODO Auto-generated method stub
		ArrayList<Integer> hours = new ArrayList<Integer>();
		for (int h=0;h<=23;h++){
			hours.add(def);
		}
		for (int h=h1;h<=h2;h++){
			hours.set(h, mm);
		}
		String hoursStr ="";
		for (int h=0;h<=23;h++){
			hoursStr+=String.valueOf(hours.get(h))+" ";
		}
		return hoursStr.trim();
	}
}
