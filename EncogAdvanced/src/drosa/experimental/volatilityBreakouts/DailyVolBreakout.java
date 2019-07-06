package drosa.experimental.volatilityBreakouts;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.GlobalStats;
import drosa.experimental.PositionShort;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class DailyVolBreakout{
	
	
	public static void testDayATRv3(
			GlobalStats stats,
			ArrayList<QuoteShort> data,int begin,int end,
			int aYear,
			int maxRange,
			int dayDiff,int hOpen,int hClose,
			double volATR,double tpATR,double slATR,int nATR,int nMax,
			double comm,
			boolean debug){
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int closedTPs= 0;
		int closedSLs=0;
		int maxWins = 0;
		int maxLosses = 0;
		int actualWins = 0;
		int actualLosses = 0;
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		dailyRanges.add(100);
		
		int actualMin = -1;
		int actualMax = -1;
		double avgATR = 100;
		int sl = 0;
		int tp = 0;
		int volPips = 0;
		double winPips = 0;
		double lostPips = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		int tradeState = -1;//-1 nada, 0: pending, 1:openB 2:openS 3:finished
		long longIdx = -1;
		long shortIdx = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (aYear!=-1 && year!=aYear) continue;
			
			if (day!=lastDay){
				if (lastDay!=-1){
					int range = (int) ((actualMax-actualMin)*0.1);
					dailyRanges.add(range);
					actualMin=-1;
					actualMax=-1;
				}
				//calculateATR
				avgATR = calculateATR(dailyRanges,nATR);
				//System.out.println(avgATR);
				volPips	= (int) (volATR*avgATR);
				sl		= (int) (slATR*avgATR);
				tp		= (int) (tpATR*avgATR);
				
				lastDay = day;
				if (debug)
				System.out.println("[***NEW DAY***] "+DateUtils.datePrint(cal)+" "+avgATR+" "+volPips
						+" "+sl+" "+tp+" "+PositionShort.countTotal(positions, PositionStatus.PENDING));
				tradeState = -1; //pending sin colocar
			}
			//cierrePorTiempo de las posiciones y cancelacion de pendings
			if (h==hClose){
				PositionShort.closeByTime(stats,positions,cal,q.getOpen5(),i,dayDiff,0.0,debug);
			}
			//colocacion de los nuevos pending
			if (tradeState==-1 && h==hOpen){
				PositionShort.closePendings(positions); //cerramos todos los pendings
				//System.out.println("ANTES DE AÑADIR pendings: "+PositionShort.countTotal(positions, PositionStatus.PENDING));
				longIdx = PositionShort.addPending(positions,PositionType.LONG,cal,q.getOpen5()+volPips*10,q.getOpen5()+volPips*10+tp*10,q.getOpen5()-sl*10,i);
				shortIdx = PositionShort.addPending(positions,PositionType.SHORT,cal,q.getOpen5()-volPips*10,q.getOpen5()-volPips*10-tp*10,q.getOpen5()+sl*10,i);
				tradeState=0;//pendings colocados
			}
			//evaluacion de las posiciones
			PositionShort.evaluatePositions(stats,positions,q,cal,i,false,comm,debug);
			
			if (actualMax==-1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
			if (actualMin==-1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
		}
		
		PositionShort.closeAllPositionsByValue(stats,positions,data.get(data.size()-1).getClose5(),data.size()-1,comm);
		
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*1.0/total;
		double pf = winPips/lostPips;
		
		/*if (debug)
		System.out.println(hOpen+" "+hClose
				+" "+nATR
				+" "+PrintUtils.Print2dec(volATR,false)
				+" "+PrintUtils.Print2dec(tpATR,false)
				+" "+PrintUtils.Print2dec(slATR,false,2)
				+" "+PrintUtils.Print2dec(winPips-lostPips,false,5)
				+" || "
				+total
				+" "+PrintUtils.Print2dec(winPer,false)+" "+PrintUtils.Print2dec(avg,false)
				+" || "+PrintUtils.Print2dec(winPips,false)+" "+PrintUtils.Print2dec(lostPips,false)+" "+PrintUtils.Print2dec(pf,false)
				+" || "+closedTPs+" "+closedSLs
				+" || "+maxWins+" "+maxLosses
				);*/
		//acc stats
		/*if (stats!=null){
			stats.addLosses(losses);
			stats.addWins(wins);
			stats.addWinPips(winPips);
			stats.addLostPips(lostPips);
		}*/
	}
	
public static double testAccumulateFiles(String header0,ArrayList<String> files,int dayDiff,int hOpen,int hClose,
		double volATR,double tpATR,double slATR,int nATR,
		int nMax,
		double comm,
		boolean isDay,
		boolean debug){
	
	GlobalStats stats = new GlobalStats();
	
	for (int i=0;i<files.size();i++){
		String path = files.get(i);
		ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
		ArrayList<QuoteShort> data = null;
		dataI.clear();
		dataS.clear();
		data5m.clear();
		data = data5mS;
		
		
		if (isDay){
			DailyVolBreakout.testDayATRv2(stats,data, 1, data.size()-1,-1,0,dayDiff, hOpen, hClose, volATR, tpATR, slATR,nATR,nMax,comm,false);
			//DailyVolBreakout.testDayATRv3(stats,data, 1, data.size()-1,-1,0,dayDiff, hOpen, hClose, volATR, tpATR, slATR,nATR,nMax,comm,debug);
		}else{
			DailyVolBreakout.testWeekATRv2(stats,data, 1, data.size()-1,-1,0,dayDiff, hOpen, hClose, volATR, tpATR, slATR,nATR,nMax,comm,debug);
		}
		
		
		data.clear();
		//System.out.println("fichero "+i+" procesado");
	}
	String header = header0+" "+hOpen+" "+hClose
			+" "+nATR
			+" "+PrintUtils.Print2Int(dayDiff,4)
			+" "+PrintUtils.Print2dec(volATR,false)
			+" "+PrintUtils.Print2dec(tpATR,false)
			+" "+PrintUtils.Print2dec(slATR,false,2);
			
	double pf= stats.printSummary(header);
	
	int total = stats.getWins()+stats.getLosses();
	double winPer = stats.getWins()*100.0/total;
	double lossPer = 100.0-winPer;
	double avgRiskPips = stats.getTotalRiskPips()*1.0/total;
	double avgPips = (stats.getWinPips()-stats.getLostPips())*1.0/(stats.getWins()+stats.getLosses());
	double expPer = avgPips/avgRiskPips;

	
	//System.out.println(avgPips+" "+stats.getTotalRiskPips());
	return (pf);
}
	
public static void testYear(ArrayList<QuoteShort> data,int begin,int end,int aYear,int hOpen,int h2,int volPips,int tp,int sl){
		
		double winPips = 0;
		double lostPips = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		int lastWeek = -1;
		int lastMonth = -1;
		int lastYear = -1;
		Calendar cal = Calendar.getInstance();
		int entryB = -1;
		int tpValueB = -1;
		int slValueB = -1;
		int entryS = -1;
		int tpValueS = -1;
		int slValueS = -1;
		int tradeState = -1;//-1 nada, 0: pending, 1:openB 2:openS 3:finished
		int openIndex = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (aYear!=-1 && year==aYear) continue;
			
			if (year!=lastYear){
				int pips = -1;
				if (tradeState>=1 && tradeState<=2){
					if (tradeState==1){
						pips = q.getOpen5()-entryB;
						
					}else if (tradeState==2){
						pips = entryS-q.getOpen5();
						
					}
					if (pips>=0){
						wins++;
						winPips+=pips*0.1;
					}else{
						losses++;
						lostPips+=Math.abs(pips*0.1);
					}
				}
				tradeState = -1;
				lastYear = year;
			}
			
			if (tradeState==-1 && h==hOpen){
				entryB = q.getOpen5()+volPips*10;
				tpValueB = entryB+tp*10;
				slValueB = entryB-sl*10;
				entryS = q.getOpen5()-volPips*10;
				tpValueS = entryS-tp*10;
				slValueS = entryS+sl*10;
				tradeState = 0;
			}
			
			if (tradeState==0){
				if (q.getHigh5()>=entryB && q.getOpen5()<=entryB){//open buy
					openIndex = i;
					tradeState=1;
				}else if (q1.getLow5()<=entryS && q1.getOpen5()>=entryS){//open sell
					openIndex = i;
					tradeState=2;
				}
			}
			
			boolean closed = false;
			if (tradeState==1){
				if (q.getHigh5()>=tpValueB){
					winPips += tp;
					wins++;
					closed = true;
				}else if (q.getLow5()<=slValueB
						//&& i>openIndex
						){
					lostPips += sl;
					losses++;
					closed = true;
				}
			}
			if (tradeState==2){
				if (q.getLow5()<=tpValueS){
					winPips += tp;
					wins++;
					closed = true;
				}else if (q.getHigh5()>=slValueS
						//&& i>openIndex
						){
					lostPips += sl;
					losses++;
					closed = true;
				}
			}
			
			if (closed){
				tradeState=3;
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*1.0/total;
		double pf = winPips/lostPips;
		
		System.out.println(hOpen+" "+volPips+" "+tp+" "+sl
				+" || "
				+total+" "+PrintUtils.Print2(winPer)+" "+PrintUtils.Print2(avg)
				+" || "+PrintUtils.Print2(winPips)+" "+PrintUtils.Print2(lostPips)+" "+PrintUtils.Print2(pf));
	}
	
public static void testMonth(ArrayList<QuoteShort> data,int begin,int end,int aYear,int hOpen,int h2,int volPips,int tp,int sl){
		
		double winPips = 0;
		double lostPips = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		int lastWeek = -1;
		int lastMonth = -1;
		Calendar cal = Calendar.getInstance();
		int entryB = -1;
		int tpValueB = -1;
		int slValueB = -1;
		int entryS = -1;
		int tpValueS = -1;
		int slValueS = -1;
		int tradeState = -1;//-1 nada, 0: pending, 1:openB 2:openS 3:finished
		int openIndex = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (aYear!=-1 && year==aYear) continue;
			
			if (month!=lastMonth){
				int pips = -1;
				if (tradeState>=1 && tradeState<=2){
					if (tradeState==1){
						pips = q.getOpen5()-entryB;
						
					}else if (tradeState==2){
						pips = entryS-q.getOpen5();
						
					}
					if (pips>=0){
						wins++;
						winPips+=pips*0.1;
					}else{
						losses++;
						lostPips+=Math.abs(pips*0.1);
					}
				}
				tradeState = -1;
				lastMonth = month;
			}
			
			if (tradeState==-1 && h==hOpen){
				entryB = q.getOpen5()+volPips*10;
				tpValueB = entryB+tp*10;
				slValueB = entryB-sl*10;
				entryS = q.getOpen5()-volPips*10;
				tpValueS = entryS-tp*10;
				slValueS = entryS+sl*10;
				tradeState = 0;
			}
			
			if (tradeState==0){
				if (q.getHigh5()>=entryB && q.getOpen5()<=entryB){//open buy
					openIndex = i;
					tradeState=1;
				}else if (q1.getLow5()<=entryS && q1.getOpen5()>=entryS){//open sell
					openIndex = i;
					tradeState=2;
				}
			}
			
			boolean closed = false;
			if (tradeState==1){
				if (q.getHigh5()>=tpValueB){
					winPips += tp;
					wins++;
					closed = true;
				}else if (q.getLow5()<=slValueB
						//&& i>openIndex
						){
					lostPips += sl;
					losses++;
					closed = true;
				}
			}
			if (tradeState==2){
				if (q.getLow5()<=tpValueS){
					winPips += tp;
					wins++;
					closed = true;
				}else if (q.getHigh5()>=slValueS
						//&& i>openIndex
						){
					lostPips += sl;
					losses++;
					closed = true;
				}
			}
			
			if (closed){
				tradeState=3;
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*1.0/total;
		double pf = winPips/lostPips;
		
		System.out.println(hOpen+" "+volPips+" "+tp+" "+sl
				+" || "
				+total+" "+PrintUtils.Print2(winPer)+" "+PrintUtils.Print2(avg)
				+" || "+PrintUtils.Print2(winPips)+" "+PrintUtils.Print2(lostPips)+" "+PrintUtils.Print2(pf));
	}

public static void testDayATRv2(
		GlobalStats stats,
		ArrayList<QuoteShort> data,int begin,int end,
		int aYear,
		int maxRange,
		int dayDiff,int hOpen,int hClose,
		double volATR,double tpATR,double slATR,int nATR,int nMax,double comm,boolean debug){
	
	ArrayList<Double> rrClosedPositions = new ArrayList<Double>();
	
	int closedTPs= 0;
	int closedSLs=0;
	int maxWins = 0;
	int maxLosses = 0;
	int actualWins = 0;
	int actualLosses = 0;
	ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
	dailyRanges.add(100);
	
	int actualMin = -1;
	int actualMax = -1;
	double avgATR = 100;
	int sl = 0;
	int tp = 0;
	int volPips = 0;
	double winPips = 0;
	double lostPips = 0;
	int wins = 0;
	int losses = 0;
	int lastDay = -1;
	int lastWeek = -1;
	Calendar cal = Calendar.getInstance();
	int entryB = -1;
	int tpValueB = -1;
	int slValueB = -1;
	int entryS = -1;
	int tpValueS = -1;
	int slValueS = -1;
	int tradeState = -1;//-1 nada, 0: pending, 1:openB 2:openS 3:finished
	int openIndex = -1;
	int dayOpen = -1;
	int dayClose = -1;
	for (int i=begin;i<=end;i++){
		QuoteShort q1 = data.get(i-1);
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		int year = cal.get(Calendar.YEAR);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		
		if (aYear!=-1 && year==aYear) continue;
		
		if (day!=lastDay){
			if (lastDay!=-1){
				int range = (int) ((actualMax-actualMin)*0.1);
				dailyRanges.add(range);
				actualMin=-1;
				actualMax=-1;
			}
			
			//calculateATR
			avgATR = calculateATR(dailyRanges,nATR);
			//System.out.println(avgATR);
			volPips	= (int) (volATR*avgATR);
			sl		= (int) (slATR*avgATR);
			tp		= (int) (tpATR*avgATR);
			
			lastDay = day;
			if (debug)
			System.out.println("[***NEW DAY***] "+DateUtils.datePrint(cal)+" "+avgATR+" "+volPips+" "+sl+" "+tp);
			if (tradeState<1 || tradeState>2) tradeState = -1;
		}
		
		//comprobar cierre de distintas semanas si hay operacion
		int actualDayDiff = day-dayOpen;
		//System.out.println("actualDiff h"+actualDayDiff+" "+h);
		if (Math.abs(actualDayDiff)>=dayDiff  && h==hClose 
				//&& dayWeek ==Calendar.FRIDAY
				&& tradeState>=1 && tradeState<=2){//solo se cierra en distintas semanas
			double pips = 0.0;
			double rr = 0.0;
			boolean closed = false;
			if (tradeState==1){
				pips = (q.getOpen5()-entryB)*0.1-comm;
				double totalRisk = (entryB-slValueB)*0.1;
				rr = (pips)/totalRisk;
				rrClosedPositions.add(rr);
				closed = true;
			}else if (tradeState==2){
				pips = (entryS-q.getOpen5())*0.1-comm;
				double totalRisk = (slValueS-entryS)*0.1;
				rr = (pips)/totalRisk;
				rrClosedPositions.add(rr);
				closed = true;
			}
			int win = 0;
			if (closed){
				if (pips>=0){
					wins++;
					winPips+=pips;
					win = 1;
				}else{
					losses++;
					lostPips+=Math.abs(pips);
					win = -1;
				}
			}
			
			if (win==1){
				actualWins++;
				if (actualLosses>=maxLosses) maxLosses = actualLosses;
				actualLosses = 0;
			}else if (win==-1){
				actualLosses++;
				if (actualWins>=maxWins) maxWins = actualWins;
				actualWins = 0;
			}
			if (debug)
			System.out.println("[CLOSED END DAY] "+DateUtils.datePrint(cal)+" "+h+" "+PrintUtils.Print2(pips*0.1));
			
			tradeState = -1;
		}
		
		if (tradeState==-1 && h==hOpen 
				//&& avgATR>=maxRange
				){
			entryB = q.getOpen5()+volPips*10;
			//tpValueB = entryB+tp*10;
			//slValueB = entryB-sl*10;
			tpValueB = entryB+tp*10; //en esta version se coloca el tp y sp tomando la apertura de referencia no el entry
			slValueB = q.getOpen5()-sl*10;
			
			entryS = q.getOpen5()-volPips*10;
			tpValueS = entryS-tp*10; //en esta version se coloca el tp y sp tomando la apertura de referencia no el entry
			slValueS = q.getOpen5()+sl*10;
			//tpValueS = entryS-tp*10;
			//slValueS = entryS+sl*10;
			tradeState = 0;
		}
		
		if (tradeState==0){
			if (q.getHigh5()>=entryB && q.getOpen5()<=entryB){//open buy
				int maxIndex = QuoteShort.getMax(data,i-nMax,i-1);
				if (maxIndex>=0 && debug)
				System.out.println("entryb y max "+entryB+" "+data.get(maxIndex).getHigh5());
				if (maxIndex==-1 || entryB>=data.get(maxIndex).getHigh5()){
					dayOpen = day;
					openIndex = i;
					tradeState=1;
					double riskPips = (entryB-slValueB)*0.1;
					stats.addRiskPips(riskPips);
					if (debug)
					System.out.println("[OPEN BUY] "+DateUtils.datePrint(cal)+" "+entryB+" "+tpValueB+" "+slValueB);
				}
			}else if (q1.getLow5()<=entryS && q1.getOpen5()>=entryS){//open sell
				int minIndex = QuoteShort.getMin(data,i-nMax,i-1);
				if (minIndex>=0 && debug)
				System.out.println("entrys y min "+entryS+" "+data.get(minIndex).getHigh5());
				if (minIndex==-1 ||entryS<=data.get(minIndex).getLow5()){
					dayOpen = day;
					openIndex = i;
					tradeState=2;
					double riskPips = (slValueS-entryS)*0.1;
					stats.addRiskPips(riskPips);
					if (debug)
					System.out.println("[OPEN SELL] "+DateUtils.datePrint(cal)+" "+entryS+" "+tpValueS+" "+slValueS);
				}
			}
		}
		
		boolean closed = false;
		double pips = 0.0;
		int win = 0;
		if (tradeState==1){
			if (q.getHigh5()>=tpValueB){
				pips = tp-comm;
				double totalRisk = (entryB-slValueB)*0.1;
				double rr = (pips)/totalRisk;
				rrClosedPositions.add(rr);
				closed = true;
				if (debug)
				System.out.println("[CLOSED BUY TP] "+DateUtils.datePrint(cal)+" "+pips);
			}else if (q.getLow5()<=slValueB
					//&& i>openIndex //testear bien con 1min data
					){
				int diffSL = (int) ((entryB-slValueB)*0.1); 
				pips = -diffSL-comm;
				rrClosedPositions.add(-1.0);
				closed = true;
				if (debug)
				System.out.println("[CLOSED BUY SL] "+DateUtils.datePrint(cal)+" "+pips);
				closedSLs++;
			}
		}
		if (tradeState==2){
			if (q.getLow5()<=tpValueS){
				pips = tp-comm;
				double totalRisk = (slValueS-entryS)*0.1;
				double rr = (pips)/totalRisk;
				rrClosedPositions.add(rr);
				closed = true;
				if (debug)
				System.out.println("[CLOSED SELL TP] "+DateUtils.datePrint(cal)+" "+pips);
			}else if (q.getHigh5()>=slValueS
					//&& i>openIndex //testear bien con 1min data
					){
				int diffSL =  (int) ((slValueS-entryS)*0.1);
				pips = -diffSL-comm;
				rrClosedPositions.add(-1.0);
				closed = true;
				if (debug)
				System.out.println("[CLOSED SELL SL] "+DateUtils.datePrint(cal)+" "+pips);
				closedSLs++;
			}
		}
		
		if (closed){
			if (pips>=0){
				winPips+=pips;
				wins++;
				actualWins++;
				if (actualLosses>=maxLosses) maxLosses = actualLosses;
				actualLosses = 0;
			}else{
				lostPips+=Math.abs(pips);
				losses++;
				actualLosses++;
				if (actualWins>=maxWins) maxWins = actualWins;
				actualWins = 0;
			}
			tradeState=3;
		}
		
		if (actualMax==-1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
		if (actualMin==-1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
	}
	
	int total = wins+losses;
	double winPer = wins*100.0/total;
	double avg = (winPips-lostPips)*1.0/total;
	double pf = winPips/lostPips;
	
	if (debug)
	System.out.println(hOpen+" "+hClose
			+" "+nATR
			+" "+PrintUtils.Print2dec(volATR,false)
			+" "+PrintUtils.Print2dec(tpATR,false)
			+" "+PrintUtils.Print2dec(slATR,false,2)
			+" "+PrintUtils.Print2dec(winPips-lostPips,false,5)
			+" || "
			+total
			+" "+PrintUtils.Print2dec(winPer,false)+" "+PrintUtils.Print2dec(avg,false)
			+" || "+PrintUtils.Print2dec(winPips,false)+" "+PrintUtils.Print2dec(lostPips,false)+" "+PrintUtils.Print2dec(pf,false)
			+" || "+closedTPs+" "+closedSLs
			+" || "+maxWins+" "+maxLosses
			);
	
	
	//calculate rr
	double avgWinRR = 0;
	double avgLossRR = 0;
	int totalW = 0;
	int totalL = 0;
	for (int i=0;i<rrClosedPositions.size();i++){
		double rr = rrClosedPositions.get(i);
		if (rr>=0){
			avgWinRR+=rr;
			totalW++;
		}
		else{
			avgLossRR+=Math.abs(rr);
			totalL++;
		}
	}
	//acc stats
	if (stats!=null){
		stats.addLosses(losses);
		stats.addWins(wins);
		stats.addWinPips(winPips);
		stats.addLostPips(lostPips);
		stats.setAvgWinRR(avgWinRR/totalW);
		stats.setAvgLossRR(avgLossRR/totalL);
	}
}

public static void testWeekATRv2(GlobalStats stats,ArrayList<QuoteShort> data,int begin,int end,
		int aYear,
		int maxRange,
		int weekDiff,int hOpen,int hClose,
		double volATR,double tpATR,double slATR,int nATR,int nMax,double comm,boolean debug){
	
	int maxWins = 0;
	int maxLosses = 0;
	int actualWins = 0;
	int actualLosses = 0;
	ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
	dailyRanges.add(100);
	
	int actualMin = -1;
	int actualMax = -1;
	double avgATR = 100;
	int sl = 0;
	int tp = 0;
	int volPips = 0;
	double winPips = 0;
	double lostPips = 0;
	int wins = 0;
	int losses = 0;
	int lastDay = -1;
	int lastWeek = -1;
	Calendar cal = Calendar.getInstance();
	int entryB = -1;
	int tpValueB = -1;
	int slValueB = -1;
	int entryS = -1;
	int tpValueS = -1;
	int slValueS = -1;
	int tradeState = -1;//-1 nada, 0: pending, 1:openB 2:openS 3:finished
	int openIndex = -1;
	int weekOpen = -1;
	int weekClose = -1;
	double totalRiskPips = 0;
	ArrayList<Double> rrClosedPositions = new ArrayList<Double>();
	for (int i=begin;i<=end;i++){
		QuoteShort q1 = data.get(i-1);
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		int year = cal.get(Calendar.YEAR);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		
		if (aYear!=-1 && year!=aYear) continue;
		
		if (week!=lastWeek){
			if (lastWeek!=-1){
				int range = (int) ((actualMax-actualMin)*0.1);
				dailyRanges.add(range);
				actualMin=-1;
				actualMax=-1;
			}
			
			//calculateATR
			avgATR = calculateATR(dailyRanges,nATR);
			//System.out.println(avgATR);
			volPips	= (int) (volATR*avgATR);
			sl		= (int) (slATR*avgATR);
			tp		= (int) (tpATR*avgATR);
			
			lastWeek = week;
			if (debug)
			System.out.println("[NEW WEEK] "+DateUtils.datePrint(cal)+" "+avgATR+" "+volPips+" "+sl+" "+tp);
			if (tradeState<1 || tradeState>2) tradeState = -1;
		}
		
		//comprobar cierre de distintas semanas si hay operacion
		int actualWeekDiff = week-weekOpen;
		if (Math.abs(actualWeekDiff)>=weekDiff  && h==hClose 
				//&& dayWeek ==Calendar.FRIDAY
				&& tradeState>=1 && tradeState<=2){//solo se cierra en distintas semanas
			double pips = -1;
			if (tradeState==1){
				pips = (q.getOpen5()-entryB)*0.1-comm;
				double totalRisk = (entryB-slValueB)*0.1;
				double rr = (pips)/totalRisk;
				rrClosedPositions.add(rr);
			}else if (tradeState==2){
				pips = (entryS-q.getOpen5())*0.1-comm;
				double totalRisk = (slValueS-entryS)*0.1;
				double rr = (pips)/totalRisk;
				rrClosedPositions.add(rr);
			}
			
			int win = 0;
			if (pips>=0){
				wins++;
				winPips+=pips;
				win = 1;
			}else{
				losses++;
				lostPips+=Math.abs(pips);
				win = -1;
			}
			
			if (win==1){
				actualWins++;
				if (actualLosses>=maxLosses) maxLosses = actualLosses;
				actualLosses = 0;
			}else if (win==-1){
				actualLosses++;
				if (actualWins>=maxWins) maxWins = actualWins;
				actualWins = 0;
			}
			if (debug)
			System.out.println("[CLOSED END WEEK] "+h+" "+q.getOpen5()+" "+PrintUtils.Print2(pips));
			
			System.out.println(PrintUtils.Print2(pips));
			
			tradeState = -1;
		}
		
		if (tradeState==-1 && h==hOpen && avgATR>=maxRange){
			entryB = q.getOpen5()+volPips*10;
			//tpValueB = entryB+tp*10;
			//slValueB = entryB-sl*10;
			tpValueB = entryB+tp*10; //en esta version se coloca el tp y sp tomando la apertura de referencia no el entry
			slValueB = q.getOpen5()-sl*10;
			if (slValueB>entryB) return;
			
			entryS = q.getOpen5()-volPips*10;
			tpValueS = entryS-tp*10; //en esta version se coloca el tp y sp tomando la apertura de referencia no el entry
			slValueS = q.getOpen5()+sl*10;
			if (slValueS<entryS) return;
			//tpValueS = entryS-tp*10;
			//slValueS = entryS+sl*10;
			tradeState = 0;
			if(debug){
			System.out.println("[PENDING BUY] "+DateUtils.datePrint(cal)+" "+entryB+" "+tpValueB+" "+slValueB);
			System.out.println("[PENDING SELL] "+DateUtils.datePrint(cal)+" "+entryS+" "+tpValueS+" "+slValueS);
			}
		}
		
		if (tradeState==0){
			if (q.getHigh5()>=entryB && q.getOpen5()<=entryB){//open buy
				int maxIndex = QuoteShort.getMax(data,i-nMax,i-1);
				if (maxIndex>=0 && debug)
				System.out.println("entryb y max "+entryB+" "+data.get(maxIndex).getHigh5());
				if (maxIndex==-1 || entryB>=data.get(maxIndex).getHigh5()){
					totalRiskPips += (entryB-slValueB)*0.1;
					weekOpen = week;
					openIndex = i;
					tradeState=1;
					if (debug)
						System.out.println("[OPEN BUY] "+DateUtils.datePrint(cal)+" "+entryB+" "+tpValueB+" "+slValueB);
				}
			}else if (q1.getLow5()<=entryS && q1.getOpen5()>=entryS){//open sell
				int minIndex = QuoteShort.getMin(data,i-nMax,i-1);
				if (minIndex>=0 && debug)
				System.out.println("entrys y min "+entryS+" "+data.get(minIndex).getHigh5());
				if (minIndex==-1 ||entryS<=data.get(minIndex).getLow5()){
					totalRiskPips += (slValueS-entryS)*0.1;
					weekOpen = week;
					openIndex = i;
					tradeState=2;
					if (debug)
					System.out.println("[OPEN SELL] "+DateUtils.datePrint(cal)+" "+entryS+" "+tpValueS+" "+slValueS);
				}
			}
		}
		
		boolean closed = false;
		int win = 0;
		double pips = 0;
		if (tradeState==1){
			if (q.getHigh5()>=tpValueB){
				pips = tp-comm;
				//winPips += tp;
				//wins++;
				closed = true;
				//win = 1;
				double totalRisk = (entryB-slValueB)*0.1;
				double rr = (pips)/totalRisk;
				rrClosedPositions.add(rr);
				if (debug)
				System.out.println("[CLOSED BUY TP] "+DateUtils.datePrint(cal)+" "+pips);
				
			}else if (q.getLow5()<=slValueB
					//&& i>openIndex //testear bien con 1min data
					){
				
				int diffSL = (int) ((entryB-slValueB)*0.1); 
				pips = -diffSL-comm;
				//lostPips += (entryB-slValueB)*0.1;
				//losses++;
				closed = true;
				rrClosedPositions.add(-1.0);
				win = -1;
				if (debug)
				System.out.println("[CLOSED BUY SL] "+DateUtils.datePrint(cal)+" "+-diffSL);
			}
		}
		if (tradeState==2){
			if (q.getLow5()<=tpValueS){
				pips = tp-comm;
				//winPips += tp;
				//wins++;
				closed = true;
				//win = 1;
				double totalRisk = (slValueS-entryS)*0.1;
				double rr = (pips)/totalRisk;
				rrClosedPositions.add(rr);
				if (debug)
				System.out.println("[CLOSED SELL TP] "+DateUtils.datePrint(cal)+" "+tp);
			}else if (q.getHigh5()>=slValueS
					//&& i>openIndex //testear bien con 1min data
					){
				int diffSL =  (int) ((slValueS-entryS)*0.1);
				//lostPips += (slValueS-entryS)*0.1;
				//losses++;
				pips = -diffSL-comm;
				closed = true;
				win = -1;
				rrClosedPositions.add(-1.0);
				if (debug)
				System.out.println("[CLOSED SELL SL] "+DateUtils.datePrint(cal)+" "+-diffSL);
			}
		}
		
		if (closed){
			if (pips>=0){
				winPips+=pips;
				wins++;
				actualWins++;
				if (actualLosses>=maxLosses) maxLosses = actualLosses;
				actualLosses = 0;
			}else{
				lostPips+=Math.abs(pips);
				losses++;
				actualLosses++;
				if (actualWins>=maxWins) maxWins = actualWins;
				actualWins = 0;
			}
			System.out.println(PrintUtils.Print2(pips));
			tradeState=3;
		}
		
		if (actualMax==-1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
		if (actualMin==-1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
	}
	
	int total = wins+losses;
	double winPer = wins*100.0/total;
	double avg = (winPips-lostPips)*1.0/total-comm;//agregamos comision en el avg
	double avgRiskPips = totalRiskPips*1.0/total;
	double pf = winPips/lostPips;
	
	if (debug)
	System.out.println(hOpen+" "+hClose
			+" "+nATR
			+" "+PrintUtils.Print2dec(volATR,false)
			+" "+PrintUtils.Print2dec(tpATR,false)
			+" "+PrintUtils.Print2dec(slATR,false,2)
			+" "+PrintUtils.Print2dec(winPips-lostPips,false,5)
			+" || "
			+total
			+" "+PrintUtils.Print2dec(winPer,false)
			+" "+PrintUtils.Print2dec(avg,false)
			+" "+PrintUtils.Print2dec(avgRiskPips,false)
			+" "+PrintUtils.Print2dec(avg*100.0/avgRiskPips,false)
			+" || "+PrintUtils.Print2dec(winPips,false)
			+" "+PrintUtils.Print2dec(lostPips,false)
			+" "+PrintUtils.Print2dec(pf,false)
			+" || "+maxWins+" "+maxLosses
			);
	
	//calculate rr
		double avgWinRR = 0;
		double avgLossRR = 0;
		int totalW = 0;
		int totalL = 0;
		for (int i=0;i<rrClosedPositions.size();i++){
			double rr = rrClosedPositions.get(i);
			if (rr>=0){
				avgWinRR+=rr;
				totalW++;
			}
			else{
				avgLossRR+=Math.abs(rr);
				totalL++;
			}
		}
		//acc stats
		if (stats!=null){
			stats.addLosses(losses);
			stats.addWins(wins);
			stats.addWinPips(winPips);
			stats.addLostPips(lostPips);
			stats.setTotalRiskPips(totalRiskPips);
			stats.setAvgWinRR(avgWinRR/totalW);
			stats.setAvgLossRR(avgLossRR/totalL);
		}
		
		
		/*double risk = 0.05;
		for (risk=0.02;risk<=0.02;risk+=0.01){
			double balance = 10000.0;
			double minBalance = balance;
			double maxBalance = balance;
			double maxDD = 0;
			for (int i=0;i<rrClosedPositions.size();i++){
				double rr = rrClosedPositions.get(i);
				balance = balance + balance*rr*risk;
				//System.out.println(PrintUtils.Print2(rrClosedPositions.get(i), false)
				//		+" "+PrintUtils.Print2(balance, false));
				if (balance<=minBalance) minBalance = balance;
				if (balance>=maxBalance) maxBalance = balance;
				double actualDD = 100.0-balance*100.0/maxBalance;
				if (actualDD>=maxDD) maxDD = actualDD;
			}
			System.out.println(PrintUtils.Print2(risk, false)+" "+PrintUtils.Print2(balance, false)
					+" "+PrintUtils.Print2(maxBalance, false)
					+" "+PrintUtils.Print2(minBalance, false)
					+" "+PrintUtils.Print2(maxDD, false)
					);
		}*/
}

public static void testWeekATR(ArrayList<QuoteShort> data,int begin,int end,int aYear,int hOpen,int h2,
		double volATR,double tpATR,double slATR,int nATR){
	
	ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
	dailyRanges.add(100);
	
	int actualMin = -1;
	int actualMax = -1;
	double avgATR = 100;
	int sl = 0;
	int tp = 0;
	int volPips = 0;
	double winPips = 0;
	double lostPips = 0;
	int wins = 0;
	int losses = 0;
	int lastDay = -1;
	int lastWeek = -1;
	Calendar cal = Calendar.getInstance();
	int entryB = -1;
	int tpValueB = -1;
	int slValueB = -1;
	int entryS = -1;
	int tpValueS = -1;
	int slValueS = -1;
	int tradeState = -1;//-1 nada, 0: pending, 1:openB 2:openS 3:finished
	int openIndex = -1;
	for (int i=begin;i<=end;i++){
		QuoteShort q1 = data.get(i-1);
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		int year = cal.get(Calendar.YEAR);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		
		if (aYear!=-1 && year==aYear) continue;
		
		if (week!=lastWeek){
			if (lastWeek!=-1){
				int range = (int) ((actualMax-actualMin)*0.1);
				dailyRanges.add(range);
				actualMin=-1;
				actualMax=-1;
			}
			int pips = -1;
			if (tradeState>=1 && tradeState<=2){
				if (tradeState==1){
					pips = q.getOpen5()-entryB;
					
				}else if (tradeState==2){
					pips = entryS-q.getOpen5();
					
				}
				if (pips>=0){
					wins++;
					winPips+=pips*0.1;
				}else{
					losses++;
					lostPips+=Math.abs(pips*0.1);
				}
			}
			
			//calculateATR
			avgATR = calculateATR(dailyRanges,nATR);
			//System.out.println(avgATR);
			volPips	= (int) (volATR*avgATR);
			sl		= (int) (slATR*avgATR);
			tp		= (int) (tpATR*avgATR);
			
			tradeState = -1;
			lastWeek = week;
		}
		
		if (tradeState==-1 && h==hOpen){
			entryB = q.getOpen5()+volPips*10;
			tpValueB = entryB+tp*10;
			slValueB = entryB-sl*10;
			entryS = q.getOpen5()-volPips*10;
			tpValueS = entryS-tp*10;
			slValueS = entryS+sl*10;
			tradeState = 0;
		}
		
		if (tradeState==0){
			if (q.getHigh5()>=entryB && q.getOpen5()<=entryB){//open buy
				openIndex = i;
				tradeState=1;
			}else if (q1.getLow5()<=entryS && q1.getOpen5()>=entryS){//open sell
				openIndex = i;
				tradeState=2;
			}
		}
		
		boolean closed = false;
		if (tradeState==1){
			if (q.getHigh5()>=tpValueB){
				winPips += tp;
				wins++;
				closed = true;
			}else if (q.getLow5()<=slValueB
					&& i>openIndex //testear bien con 1min data
					){
				lostPips += sl;
				losses++;
				closed = true;
			}
		}
		if (tradeState==2){
			if (q.getLow5()<=tpValueS){
				winPips += tp;
				wins++;
				closed = true;
			}else if (q.getHigh5()>=slValueS
					&& i>openIndex //testear bien con 1min data
					){
				lostPips += sl;
				losses++;
				closed = true;
			}
		}
		
		if (closed){
			tradeState=3;
		}
		
		if (actualMax==-1 || q.getHigh5()>=actualMax) actualMax = q.getHigh5();
		if (actualMin==-1 || q.getLow5()<=actualMin) actualMin = q.getLow5();
	}
	
	int total = wins+losses;
	double winPer = wins*100.0/total;
	double avg = (winPips-lostPips)*1.0/total;
	double pf = winPips/lostPips;
	
	System.out.println(hOpen
			+" "+nATR
			+" "+PrintUtils.Print2(volATR)
			+" "+PrintUtils.Print2(tpATR)
			+" "+PrintUtils.Print2(slATR)
			+" || "
			+total+" "+PrintUtils.Print2(winPer)+" "+PrintUtils.Print2(avg)
			+" || "+PrintUtils.Print2(winPips)+" "+PrintUtils.Print2(lostPips)+" "+PrintUtils.Print2(pf));
}
	
private static double calculateATR(ArrayList<Integer> ranges, int nATR) {
	// TODO Auto-generated method stub
	int end = ranges.size()-nATR;
	if (end<=0) end = 0;
	double avg = 0;
	int total=0;
	for (int i=ranges.size()-1;i>=end;i--){
		avg+=ranges.get(i);
		total++;
	}
	return avg/total;
}

public static void testWeek(ArrayList<QuoteShort> data,int begin,int end,int aYear,int hOpen,int h2,int volPips,int tp,int sl){
		
		double winPips = 0;
		double lostPips = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		int lastWeek = -1;
		Calendar cal = Calendar.getInstance();
		int entryB = -1;
		int tpValueB = -1;
		int slValueB = -1;
		int entryS = -1;
		int tpValueS = -1;
		int slValueS = -1;
		int tradeState = -1;//-1 nada, 0: pending, 1:openB 2:openS 3:finished
		int openIndex = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (aYear!=-1 && year==aYear) continue;
			
			if (week!=lastWeek){
				int pips = -1;
				if (tradeState>=1 && tradeState<=2){
					if (tradeState==1){
						pips = q.getOpen5()-entryB;
						
					}else if (tradeState==2){
						pips = entryS-q.getOpen5();
						
					}
					if (pips>=0){
						wins++;
						winPips+=pips*0.1;
					}else{
						losses++;
						lostPips+=Math.abs(pips*0.1);
					}
				}
				tradeState = -1;
				lastWeek = week;
			}
			
			if (tradeState==-1 && h==hOpen){
				entryB = q.getOpen5()+volPips*10;
				tpValueB = entryB+tp*10;
				slValueB = entryB-sl*10;
				entryS = q.getOpen5()-volPips*10;
				tpValueS = entryS-tp*10;
				slValueS = entryS+sl*10;
				tradeState = 0;
			}
			
			if (tradeState==0){
				if (q.getHigh5()>=entryB && q.getOpen5()<=entryB){//open buy
					openIndex = i;
					tradeState=1;
				}else if (q1.getLow5()<=entryS && q1.getOpen5()>=entryS){//open sell
					openIndex = i;
					tradeState=2;
				}
			}
			
			boolean closed = false;
			if (tradeState==1){
				if (q.getHigh5()>=tpValueB){
					winPips += tp;
					wins++;
					closed = true;
				}else if (q.getLow5()<=slValueB
						//&& i>openIndex
						){
					lostPips += sl;
					losses++;
					closed = true;
				}
			}
			if (tradeState==2){
				if (q.getLow5()<=tpValueS){
					winPips += tp;
					wins++;
					closed = true;
				}else if (q.getHigh5()>=slValueS
						//&& i>openIndex
						){
					lostPips += sl;
					losses++;
					closed = true;
				}
			}
			
			if (closed){
				tradeState=3;
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*1.0/total;
		double pf = winPips/lostPips;
		
		System.out.println(hOpen+" "+volPips+" "+tp+" "+sl
				+" || "
				+total+" "+PrintUtils.Print2(winPer)+" "+PrintUtils.Print2(avg)
				+" || "+PrintUtils.Print2(winPips)+" "+PrintUtils.Print2(lostPips)+" "+PrintUtils.Print2(pf));
	}
	
	public static void test(ArrayList<QuoteShort> data,int begin,int end,int aYear,int hOpen,int h2,int volPips,int tp,int sl){
		
		double winPips = 0;
		double lostPips = 0;
		int wins = 0;
		int losses = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		int entryB = -1;
		int tpValueB = -1;
		int slValueB = -1;
		int entryS = -1;
		int tpValueS = -1;
		int slValueS = -1;
		int tradeState = -1;//-1 nada, 0: pending, 1:openB 2:openS 3:finished
		int openIndex = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (aYear!=-1 && year==aYear) continue;
			
			if (day!=lastDay){
				int pips = -1;
				if (tradeState>=1 && tradeState<=2){
					if (tradeState==1){
						pips = q.getOpen5()-entryB;
						
					}else if (tradeState==2){
						pips = entryS-q.getOpen5();
						
					}
					if (pips>=0){
						wins++;
						winPips+=pips*0.1;
					}else{
						losses++;
						lostPips+=Math.abs(pips*0.1);
					}
				}
				tradeState = -1;
				lastDay = day;
			}
			
			if (tradeState==-1 && h==hOpen){
				entryB = q.getOpen5()+volPips*10;
				tpValueB = entryB+tp*10;
				slValueB = entryB-sl*10;
				entryS = q.getOpen5()-volPips*10;
				tpValueS = entryS-tp*10;
				slValueS = entryS+sl*10;
				tradeState = 0;
			}
			
			if (tradeState==0){
				if (q.getHigh5()>=entryB && q.getOpen5()<=entryB){//open buy
					openIndex = i;
					tradeState=1;
				}else if (q.getLow5()<=entryS && q.getOpen5()>=entryS){//open sell
					openIndex = i;
					tradeState=2;
				}
			}
			
			boolean closed = false;
			if (tradeState==1){
				if (q.getHigh5()>=tpValueB){
					winPips += tp;
					wins++;
					closed = true;
				}else if (q.getLow5()<=slValueB
						&& i>openIndex
						){
					lostPips += sl;
					losses++;
					closed = true;
				}
			}
			if (tradeState==2){
				if (q.getLow5()<=tpValueS){
					winPips += tp;
					wins++;
					closed = true;
				}else if (q.getHigh5()>=slValueS
						&& i>openIndex
						){
					lostPips += sl;
					losses++;
					closed = true;
				}
			}
			
			if (closed){
				tradeState=3;
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*1.0/total;
		double pf = winPips*1.0/lostPips;
		System.out.println(hOpen+" "+volPips+" "+tp+" "+sl
				+" || "
				+total+" "+PrintUtils.Print2(winPer)
				+" "+PrintUtils.Print2(avg)
				+" "+PrintUtils.Print2(pf)
				);
	}

	public static void main(String[] args) {
		//String pathEURUSD = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2004.01.01_2008.12.31.csv";
		//String pathEURUSD = "c:\\fxdata\\eurusd_UTC_5 Mins_Bid_2004.01.01_2008.12.31.csv";
		
		//String pathEURUSD = "c:\\fxdata\\eurUSD_UTC_1 Min_Bid_2007.01.01_2011.12.31.csv";
		//String pathEURUSD = "c:\\fxdata\\eurUSD_UTC_1 Min_Bid_2011.01.01_2015.08.18.csv";
		//String pathEURUSD = "c:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.12.31_2015.08.20.csv";
		
		String currency="eurjpy";
		String path1 = "c:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2003.12.31_2007.12.30.csv";
		String path2 = "c:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2007.12.31_2011.12.30.csv";
		String path3 = "c:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2011.12.31_2015.08.21.csv";
		
		ArrayList<String> files = new ArrayList<String>();
		files.add(path1);
		files.add(path2);
		files.add(path3);
		
		ArrayList<String> currencies = new ArrayList<String>();
		currencies.add("eurusd");
		currencies.add("gbpusd");
		currencies.add("audusd");
		currencies.add("usdjpy");
		currencies.add("eurjpy");
		currencies.add("gbpjpy");
		currencies.add("audjpy");
		currencies.add("nzdusd");
		
		double avgPips = 0;
		
		for (double vol=0.30;vol<=0.30;vol+=0.10){
		for (int weekDiff=1;weekDiff<=1;weekDiff+=1){
		for (int h=8;h<=8;h++){
			int total = 0;
			avgPips=0;
			int index=0;
			int limit=currencies.size()-1;
			limit=0;
			for (int c=index;c<=limit;c++){
				currency = currencies.get(c);
				path1 = "c:\\fxdata\\"+currency+"_UTC_5 Mins_Bid_2003.12.31_2015.08.20.csv";
				
				ArrayList<String> filesWeek = new ArrayList<String>();
				filesWeek.add(path1);
			
				int aYear = 2015;
				int hOpen = 0;
				int hClose = 0;
				int h2 = 0;
				int volPips = 20;
				int tp = 100;
				int sl = 50;
				
				//for (int weekDiff=20;weekDiff<=20;weekDiff+=5){
					for (int maxRange=10;maxRange<=10;maxRange+=10){
						for (hOpen=9;hOpen<=9;hOpen++){
							for (hClose=hOpen;hClose<=hOpen;hClose++){
								for (int nATR=1;nATR<=1;nATR++){
									for (double volATR=0.70;volATR<=0.70;volATR+=0.05){
									for (double slATR=0.10;slATR<=0.10;slATR+=0.10){
											//System.out.println(" slATR: "+slATR);
											//for (double tpATR=1000*slATR;tpATR<=1000*slATR;tpATR+=10.0*slATR)
											for (double tpATR=9999;tpATR<=9999;tpATR+=10){
												//for (double volATR=0.80;volATR<=0.80;volATR+=0.10){
													//DailyVolBreakout.testWeekATR(data, begin, end, -1, hOpen, h2, volATR, tpATR, slATR,nATR);
													//DailyVolBreakout.testDayATRv2(null,data, begin, end, -1,maxRange,weekDiff, hOpen, hClose, volATR, tpATR, slATR,nATR,false);
													//DailyVolBreakout.testWeekATRv2(data, begin, end, -1,maxRange,weekDiff, hOpen, hClose, volATR, tpATR, slATR,nATR,false);
													for (int nMax=0;nMax<=0;nMax+=10){
														//avgPips+=DailyVolBreakout.testAccumulateFiles(currency,filesWeek, weekDiff, hOpen, hClose, 
														//		volATR, tpATR, slATR, nATR,nMax,0.0, false,false);
														
														avgPips+=DailyVolBreakout.testAccumulateFiles(currency,filesWeek, weekDiff, h, h, 
																volATR, tpATR, slATR, nATR,nMax,0.0, false,false);
														total++;
													}
											}//tpATR
									}//slATR
									}//natr
								}//hclose
							}//hopen
						}//maxrange
					//}//diff
				}//currencies
			}//h
			System.out.println("avg: "+PrintUtils.Print2dec(avgPips*1.0/total,false));
			}//diff
		}//vol
		}
	}

	
}
