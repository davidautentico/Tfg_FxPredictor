package drosa.experimental.claudia;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.GlobalStats;
import drosa.experimental.PositionShort;
import drosa.experimental.SimulatedResults;
import drosa.experimental.EAS.TestEAs;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradeLog;
import drosa.utils.TradingUtils;

public class TestScalping {

	//pendiente robot mas avanzado: si hay varias barras consecutivas de 1 minuto que no alcanzan el objetivo, se alargan los tp de las siguientes
	//se podria intentar crear un indicador, marcando las candles que no han conseguido el objetivo
	
	public static void testDirectionScalp(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int begin,int end,int h1,int h2,
			int sl, int tp,int maxPositions,
			int minPips,int bars1,int bars,boolean log){
		
		GlobalStats stats = new GlobalStats();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		if (begin<1) begin = 1;
		if (end>data.size()-2) end = data.size()-2;
		
		int maxOpens = 0;
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q_1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				lastDay = day;
			}
			if (log)
				TradeLog.writeToLog("d:\\tradeLog.txt",q.toString());
			//System.out.println(q.toString());
			
			int allowed = 1;
			int maxMin = maxMins.get(i);
			if (h>=h1 & h<=h2){
				int diff = (int) ((max-min)*0.1);
				if (q.getHigh5()>max 
						&& diff>=minPips
						//&& maxMin>=bars1
						){//nuevo maximo
					longEnabled = true;
					shortEnabled = false;
					if (log)
						TradeLog.writeToLog("d:\\tradeLog.txt","LONG_ENABLED");
					//System.out.println("longEnabled");
				}
				if (q.getLow5()<min 
						&& diff>=minPips
						//&& maxMin<=-bars1
						){//nuevo minimo
					longEnabled = false;
					shortEnabled = true;
					if (log)
						TradeLog.writeToLog("d:\\tradeLog.txt","SHORT_ENABLED");
					//System.out.println("shortEnabled");
				}
			}
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
				//System.out.println("nuevo max: "+max);
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
			
			if (allowed==1){//operacion permitida					
				int entry   = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType positionType = PositionType.NONE;
				
				if (longEnabled 
						//&& maxMin>=bars
						&& maxMin<=-bars
						){
					entry = q_1.getOpen5();
					slValue = entry-sl*10;
					tpValue = entry+tp*10;
					positionType = PositionType.LONG;
				}
				if (shortEnabled 
						//&& maxMin<=-bars
						&& maxMin>=bars
						){
					entry = q_1.getOpen5();
					slValue = entry + sl*10;
					tpValue = entry -tp*10;
					positionType = PositionType.SHORT;
				}
				if ( entry!=-1 && positions.size()<maxPositions){
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(positionType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setOpenIndex(i+1);
					pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
					positions.add(pos);
					if (log)
						TradeLog.writeToLog("d:\\tradeLog.txt",pos.toString2()+" || "+q_1.toString());
					//System.out.println(pos.toString2()+" || "+q_1.toString());
				}
			}
			
			PositionShort.evaluatePositions(positions, stats, q_1, cal1, i+1,true);	
			int totalOpens = positions.size();
			if (totalOpens>maxOpens) maxOpens = totalOpens;
		}
		
		int wins = stats.getWins();
		int losses = stats.getLosses();
		int total = wins+losses;
		double pf = (wins*tp*1.0)/(losses*sl);
		double winPer = wins*100.0/total;
		double exp = (tp*wins-sl*losses)*1.0/total;
		
		System.out.println(h1+" "+h2
				+" "+minPips
				+" "+bars1+" "+bars
				+" "+tp+" "+sl
				+" || "+total+" "+PrintUtils.Print2(winPer)
				+" "+maxOpens
				+" "+PrintUtils.Print2(pf)
				+" "+PrintUtils.Print2(exp)
				);
		
	}
	
	public static void testDirectionScalp$$(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int begin,int end,
			int year1,int year2,
			int h1,int h2,int ha1,int ha2,
			int day1,int day2,
			int sl, int tp,int maxPositions,
			int minPips,int minSeparationPips,
			int barsGroup,int bars1,int bars,
			double initialBalance,double riskPerTrade,double comm,
			boolean closeOppositeTrades,
			boolean modePullBack,
			boolean log,
			double maxDDAllowed){
		
		
		int lastPositions = 0;
		File file = new File("c:\\tradelog.txt");
		if (file.exists()) file.delete();
		
		
		if (riskPerTrade*maxPositions>80)
			riskPerTrade = 80.0/maxPositions;
		
		int factor = -1;
		if (modePullBack) factor = 1;

		double maxDD = 0;
		double maxBalance 		= initialBalance;
		double actualBalance 	= initialBalance;
		double extraNeeded = 0;
		GlobalStats stats = new GlobalStats();
		stats.setBalance(actualBalance);
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		if (begin<1) begin = 1;
		if (end>data.size()-2) end = data.size()-2;
		
		int totalDays = 0;
		int maxOpens = 0;
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		boolean directionSettled = false;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort actualMaxMinPos = new QuoteShort();
		actualMaxMinPos.setHigh5(999999);
		actualMaxMinPos.setLow5(-999999);
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q_1);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK); 
						
			if (year<year1 || year>year2) continue;
			
			if (day!=lastDay){
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				directionSettled = false;
				lastDay = day;
				totalDays++;
			}
			if (log){
				if (positions.size()>0 && positions.size()!=lastPositions){
					//TradeLog.writeToLog("c:\\tradeLog.txt",q.toString()+" || "+positions.size());
					//lastPositions = positions.size();
				}
			}
			lastPositions = positions.size();
				//System.out.println(q.toString());
			
			int dayAllowed = 1;
			if (dayWeek<day1 || dayWeek>day2) dayAllowed=0;
			int allowed = 0;
			int maxMin = getMaxMinGroup(maxMins,i,barsGroup);
			if (h>=ha1 & h<=ha2) allowed = 1; //si es franja correcta de hora para tradear
			
			//int maxMin = getMaxMinGroup2(maxMins,i,barsGroup,bars);
			//int maxMin = maxMins.get(i);
			if (h>=h1 & h<=h2){
				int diff = (int) ((max-min)*0.1);
				if (q.getHigh5()>max 
						&& diff>=minPips
						&& !directionSettled
						//&& maxMin>=bars1
						){//nuevo maximo
						if (modePullBack){
							longEnabled = true;
							shortEnabled = false;
						}else{
							longEnabled = false;
							shortEnabled = true;
						}
						directionSettled = true;
					//longEnabled = false;
					//shortEnabled = true;
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt","LONG_ENABLED");
					}
						//System.out.println("longEnabled");
				}
				if (q.getLow5()<min 
						&& diff>=minPips
						&& !directionSettled
						//&& maxMin<=-bars1
						){//nuevo minimo					
						if (modePullBack){
							longEnabled = false;
							shortEnabled = true;
						}else{
							longEnabled = true;
							shortEnabled = false;
						}
						directionSettled = true;
					//shortEnabled = false;
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt","SHORT_ENABLED");
					}
						//System.out.println("shortEnabled");
				}
			}
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
				//System.out.println("nuevo max: "+max);
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
			
			if (allowed==1 && dayAllowed==1){//operacion permitida					
				int entry   = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType positionType = PositionType.NONE;
				int diffLastLong = actualMaxMinPos.getHigh5()-q_1.getOpen5();
				int diffLastShort = q_1.getOpen5()-actualMaxMinPos.getLow5();
				
				boolean longCondition = false;
				boolean shortCondition = false;
				
				if (modePullBack){
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}else{
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}
				//System.out.println(" diffLastLong lastShort : "+diffLastLong+" "+diffLastShort+" "+maxMin+" "+positions.size());
				if (longEnabled 
						//&& maxMin>=bars
						&& longCondition
						&& (positions.size()==0 || diffLastLong>=minSeparationPips*10)
						){
					//System.out.println("LONG ACTIVATED");
					int diffMax = -bars-maxMin;//debe ser positivo
					double percent = diffMax*100.0/maxMin;
					int mod = 0;
					entry = q_1.getOpen5();
					slValue = entry-sl*10;
					tpValue = entry+tp*10+mod;
					//System.out.println("[LONG] maxMin: "+DateUtils.datePrint(cal1)+" "+maxMin+" "+entry);
					positionType = PositionType.LONG;
					//close all shorts
					if (closeOppositeTrades)
						PositionShort.closeAllPositions(positions, stats,comm, q_1, cal1, i+1,PositionType.SHORT,true);
				}
				if (shortEnabled 
						//&& maxMin<=-bars
						&& shortCondition
						&& (positions.size()==0 || diffLastShort>=minSeparationPips*10)
						){
					
					//experimental
					int diffMax = maxMin-bars;
					double percent = Math.abs(diffMax*100.0/maxMin);
					int mod = 0;
					//if (percent>50.0) mod = 50;
					//experimental
					
					entry = q_1.getOpen5();
					slValue = entry + sl*10;
					tpValue = entry -tp*10-mod;
					//System.out.println("[SHORT] bars maxMin mod sl tp: "+bars+" "+maxMin+" "+mod+" "+slValue+" "+tpValue);
					
					positionType = PositionType.SHORT;
					//close all longs
					if (closeOppositeTrades)
						PositionShort.closeAllPositions(positions, stats,comm, q_1, cal1, i+1,PositionType.LONG,true);
				}
				if ( entry!=-1 && positions.size()<maxPositions){
					//calculo de balance
					//margen requerido
					actualBalance = stats.getBalance();
					double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,riskPerTrade,maxPositions,sl);
					if (actualBalance<minBalance){
						extraNeeded += minBalance-actualBalance;
						actualBalance = minBalance;
					}
					long microLots = TestEAs.calculateMicroLots(actualBalance,400,riskPerTrade,maxPositions,sl);
					//long microLots = TestEAs.calculateMicroLots(actualBalance,400,riskPerTrade,1,sl);
					//microLots = 1;//1$/pip
					stats.setBalance(actualBalance);
					//System.out.println(actualBalance+" "+microLots);
					//
					
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(positionType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setOpenIndex(i+1);
					pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
					pos.setMicroLots(microLots);
					positions.add(pos);
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt",pos.toString2()+" || "+q_1.toString());
						System.out.println(pos.toString2()+" || "+q.toString()+" "+maxMin);
					}
					//System.out.println(pos.toString2()+" || "+q.toString()+" "+maxMin+" "+positions.size());
				}
			}
			
			PositionShort.evaluatePositions(positions,actualMaxMinPos, stats,comm, q_1, cal1, i+1,false);	
			//System.out.println("BALANCE")
			int totalOpens = positions.size();
			if (totalOpens>maxOpens) maxOpens = totalOpens;
			
			if (stats.getBalance()>maxBalance) maxBalance = stats.getBalance();
			else{
				double actualDD = 100.0-stats.getBalance()*100.0/maxBalance;
				if (actualDD>maxDD) maxDD = actualDD;
			}
		}
		
		int wins = stats.getWins();
		int losses = stats.getLosses();
		int total = wins+losses;
		double pf = stats.getWinPips()*1.0/stats.getLostPips();
		double winPer = wins*100.0/total;
		double exp = (stats.getWinPips()-stats.getLostPips())/total;
		
		double f = (maxBalance/(initialBalance+extraNeeded))/maxDD;
		int maxWins = stats.getMaxConsecutiveWins();
		int maxLosses = stats.getMaxConsecutiveLosses();
		double netPips = stats.getWinPips()-stats.getLostPips()-comm*total;
		double profitPerDay = (exp-comm)*total/totalDays;
		
		//double riskPerTrade = totalRisk/maxPositions;
		double riskPerPip = riskPerTrade*1.0/sl;
		double expGained = riskPerPip*(exp-comm);
		double tpTrade = riskPerPip*(tp-comm);
		double profitPer = stats.getBalance()*100.0/initialBalance-100.0;
		double factorDD = profitPer/maxDD;
		double gainPer = stats.getBalance()*100.0/(initialBalance+extraNeeded)-100.0;
		
		SimulatedResults sim = stats.simulateSeq(stats.getWinLossesSeq(), initialBalance, tpTrade, riskPerTrade);
		//System.out.println(PrintUtils.Print2dec(exp,false,2));
		QuoteShort q = data.get(begin);
		QuoteShort q1 = data.get(end);
		
		QuoteShort.getCalendar(cal, q);
		QuoteShort.getCalendar(cal1, q1);
		
		if (maxDD<maxDDAllowed)
		System.out.println(header
				+" "+DateUtils.datePrint(cal)+" "+DateUtils.datePrint(cal1)
				+" "+h1+" "+h2
				+" "+minPips
				+" "+bars1+" "+PrintUtils.Print2Int(bars,3)
				+" "+PrintUtils.Print2Int(tp,3)+" "+PrintUtils.Print2Int(sl,3)
				+" "+PrintUtils.Print2dec(riskPerTrade, false, 2)
				+" || "
				+PrintUtils.Print2Int(total, 7)+" "+PrintUtils.Print2Int(wins, 7)+" "+PrintUtils.Print2Int(losses, 7)
				+" "+PrintUtils.Print2dec(winPer,false,3)
				+" "+PrintUtils.Print2Int(maxOpens,3)
				+" "+PrintUtils.Print2Int(maxWins,4)+" "+PrintUtils.Print2Int(maxLosses,4)
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2dec(exp,false,2)
				//+" "+PrintUtils.Print2dec(profitPerDay,false,2)
				+" "+PrintUtils.Print2dec(stats.getWinPips(),false,2)
				+" "+PrintUtils.Print2dec(stats.getLostPips(),false,2)
				+" "+PrintUtils.Print2dec(netPips,false,2)
				+" || "
				//+" "+sim.toString()
				+PrintUtils.Print2dec2(stats.getBalance(), true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec2(extraNeeded, true)
				+" "+PrintUtils.Print2(gainPer)
				+" "+PrintUtils.Print2(maxDD)
				+" || "+PrintUtils.Print2(factorDD)
				//+" "+PrintUtils.Print2(factorDD)
				//+" || "
				//+" "+PrintUtils.Print2(f)
				//+" || "
				//+riskPerPip+" "+expGained+" "+(initialBalance*(1+expGained/100.0))
				//+" "+sim.toString()
				//+" "+profitCompund
				);		
	}
	
	/**
	 * Abre la posicion desde que se encuntre una barra>=nbars
	 * @param header
	 * @param data
	 * @param maxMins
	 * @param begin
	 * @param end
	 * @param h1
	 * @param h2
	 * @param day1
	 * @param day2
	 * @param sl
	 * @param tp
	 * @param maxPositions
	 * @param minPips
	 * @param minSeparationPips
	 * @param barsGroup
	 * @param bars1
	 * @param bars
	 * @param initialBalance
	 * @param riskPerTrade
	 * @param comm
	 * @param closeOppositeTrades
	 * @param modePullBack
	 * @param log
	 */
	public static void testDirectionScalp$$OpenFrom(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int begin,int end,int h1,int h2,
			int day1,int day2,
			int sl, int tp,int maxPositions,
			int minPips,int minSeparationPips,
			int barsGroup,int bars1,int bars,
			double initialBalance,double riskPerTrade,double comm,
			boolean closeOppositeTrades,
			boolean modePullBack,
			boolean log){
		
		
		int lastPositions = 0;
		File file = new File("c:\\tradelog.txt");
		if (file.exists()) file.delete();
		
		
		if (riskPerTrade*maxPositions>80)
			riskPerTrade = 80.0/maxPositions;
		
		int factor = -1;
		if (modePullBack) factor = 1;

		double maxDD = 0;
		double maxBalance 		= initialBalance;
		double actualBalance 	= initialBalance;
		double extraNeeded = 0;
		GlobalStats stats = new GlobalStats();
		stats.setBalance(actualBalance);
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		if (begin<1) begin = 1;
		if (end>data.size()-2) end = data.size()-2;
		
		int totalDays = 0;
		int maxOpens = 0;
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		boolean directionSettled = false;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort actualMaxMinPos = new QuoteShort();
		actualMaxMinPos.setHigh5(999999);
		actualMaxMinPos.setLow5(-999999);
		int universalTrade = 0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q_1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK); 
						
			if (day!=lastDay){
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				directionSettled = false;
				lastDay = day;
				totalDays++;
				universalTrade = 0;
			}
			if (log){
				if (positions.size()>0 && positions.size()!=lastPositions){
					//TradeLog.writeToLog("c:\\tradeLog.txt",q.toString()+" || "+positions.size());
					//lastPositions = positions.size();
				}
			}
			lastPositions = positions.size();
				//System.out.println(q.toString());
			
			int dayAllowed = 1;
			if (dayWeek<day1 || dayWeek>day2) dayAllowed=0;
			int allowed = 1;
			int maxMin = getMaxMinGroup(maxMins,i,barsGroup);
			//int maxMin = maxMins.get(i);
			if (h>=h1 & h<=h2){
				int diff = (int) ((max-min)*0.1);
				if (q.getHigh5()>max 
						&& diff>=minPips
						&& !directionSettled
						//&& maxMin>=bars1
						){//nuevo maximo
						if (modePullBack){
							longEnabled = true;
							shortEnabled = false;
						}else{
							longEnabled = false;
							shortEnabled = true;
						}
						directionSettled = true;
					//longEnabled = false;
					//shortEnabled = true;
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt","LONG_ENABLED");
					}
						//System.out.println("longEnabled");
				}
				if (q.getLow5()<min 
						&& diff>=minPips
						&& !directionSettled
						//&& maxMin<=-bars1
						){//nuevo minimo					
						if (modePullBack){
							longEnabled = false;
							shortEnabled = true;
						}else{
							longEnabled = true;
							shortEnabled = false;
						}
						directionSettled = true;
					//shortEnabled = false;
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt","SHORT_ENABLED");
					}
						//System.out.println("shortEnabled");
				}
			}
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
				//System.out.println("nuevo max: "+max);
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
			
			if (allowed==1 && dayAllowed==1){//operacion permitida					
				int entry   = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType positionType = PositionType.NONE;
				int diffLastLong = actualMaxMinPos.getHigh5()-q_1.getOpen5();
				int diffLastShort = q_1.getOpen5()-actualMaxMinPos.getLow5();
				
				boolean longCondition = false;
				boolean shortCondition = false;
				
				if (modePullBack){
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}else{
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}
				if (longEnabled 
						//&& maxMin>=bars
						&& longCondition
						&& (positions.size()==0 || diffLastLong>=minSeparationPips*10)){
					universalTrade=1;					
				}
				if (shortEnabled 
						//&& maxMin<=-bars
						&& shortCondition
						&& (positions.size()==0 || diffLastShort>=minSeparationPips*10)
						){
					universalTrade=-1;
				}
				
				//DEFINICION DE OPERACIONES
				if (universalTrade==1){
					//System.out.println("LONG ACTIVATED");
					int diffMax = -bars-maxMin;//debe ser positivo				
					int mod = 0;					
					entry = q_1.getOpen5();
					slValue = entry-sl*10;
					tpValue = entry+tp*10+mod;
					//System.out.println("[LONG] maxMin: "+DateUtils.datePrint(cal1)+" "+maxMin+" "+entry);
					positionType = PositionType.LONG;
					//close all shorts
					if (closeOppositeTrades)
						PositionShort.closeAllPositions(positions, stats,comm, q_1, cal1, i+1,PositionType.SHORT,true);
				}
				if (universalTrade==-1){					
					//experimental
					int diffMax = maxMin-bars;
					int mod = 0;					
					entry = q_1.getOpen5();
					slValue = entry + sl*10;
					tpValue = entry -tp*10-mod;
					//System.out.println("[SHORT] bars maxMin mod sl tp: "+bars+" "+maxMin+" "+mod+" "+slValue+" "+tpValue);					
					positionType = PositionType.SHORT;
					//close all longs
					if (closeOppositeTrades)
						PositionShort.closeAllPositions(positions, stats,comm, q_1, cal1, i+1,PositionType.LONG,true);
				}
				//
				
				if ( entry!=-1 && positions.size()<maxPositions){
					//calculo de balance
					//margen requerido
					actualBalance = stats.getBalance();
					double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,riskPerTrade,maxPositions,sl);
					if (actualBalance<minBalance){
						extraNeeded += minBalance-actualBalance;
						actualBalance = minBalance;
					}
					long microLots = TestEAs.calculateMicroLots(actualBalance,400,riskPerTrade,maxPositions,sl);
					//microLots = 1;//1$/pip
					stats.setBalance(actualBalance);
					//System.out.println(actualBalance+" "+microLots);
					//
					
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(positionType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setOpenIndex(i+1);
					pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
					pos.setMicroLots(microLots);
					positions.add(pos);
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt",pos.toString2()+" || "+q_1.toString());
						System.out.println(pos.toString2()+" || "+q.toString()+" "+maxMin);
					}
					//System.out.println(pos.toString2()+" || "+q.toString()+" "+maxMin+" "+positions.size());
				}
			}
			
			PositionShort.evaluatePositions(positions,actualMaxMinPos, stats,comm, q_1, cal1, i+1,true);	
			//System.out.println("BALANCE")
			int totalOpens = positions.size();
			if (totalOpens>maxOpens) maxOpens = totalOpens;
			
			if (stats.getBalance()>maxBalance) maxBalance = stats.getBalance();
			else{
				double actualDD = 100.0-stats.getBalance()*100.0/maxBalance;
				if (actualDD>maxDD) maxDD = actualDD;
			}
		}
		
		int wins = stats.getWins();
		int losses = stats.getLosses();
		int total = wins+losses;
		double pf = stats.getWinPips()*1.0/stats.getLostPips();
		double winPer = wins*100.0/total;
		double exp = (stats.getWinPips()-stats.getLostPips())/total;
		
		double f = (maxBalance/(initialBalance+extraNeeded))/maxDD;
		int maxWins = stats.getMaxConsecutiveWins();
		int maxLosses = stats.getMaxConsecutiveLosses();
		double netPips = stats.getWinPips()-stats.getLostPips()-comm*total;
		double profitPerDay = (exp-comm)*total/totalDays;
		
		//double riskPerTrade = totalRisk/maxPositions;
		double riskPerPip = riskPerTrade*1.0/sl;
		double expGained = riskPerPip*(exp-comm);
		double tpTrade = riskPerPip*(tp-comm);
		
		//SimulatedResults sim = stats.simulateSeq(stats.getWinLossesSeq(), initialBalance, tpTrade, riskPerTrade);
		//System.out.println(PrintUtils.Print2dec(exp,false,2));
		System.out.println(header+" "+h1+" "+h2
				+" "+minPips
				+" "+bars1+" "+PrintUtils.Print2Int(bars,3)
				+" "+PrintUtils.Print2Int(tp,3)+" "+PrintUtils.Print2Int(sl,3)
				+" "+PrintUtils.Print2dec(riskPerTrade, false, 2)
				+" || "
				+PrintUtils.Print2Int(total, 7)+" "+PrintUtils.Print2Int(wins, 7)+" "+PrintUtils.Print2Int(losses, 7)
				+" "+PrintUtils.Print2dec(winPer,false,3)
				+" "+PrintUtils.Print2Int(maxOpens,3)
				+" "+PrintUtils.Print2Int(maxWins,4)+" "+PrintUtils.Print2Int(maxLosses,4)
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2dec(exp,false,2)
				//+" "+PrintUtils.Print2dec(profitPerDay,false,2)
				+" "+PrintUtils.Print2dec(stats.getWinPips(),false,2)
				+" "+PrintUtils.Print2dec(stats.getLostPips(),false,2)
				+" "+PrintUtils.Print2dec(netPips,false,2)
				+" || "
				+PrintUtils.Print2dec2(stats.getBalance(), true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec2(extraNeeded, true)
				+" "+PrintUtils.Print2(maxDD)
				//+" || "
				//+" "+PrintUtils.Print2(f)
				//+" || "
				//+riskPerPip+" "+expGained+" "+(initialBalance*(1+expGained/100.0))
				//+" "+sim.toString()
				//+" "+profitCompund
				);
		
	}
	
	public static void testDirectionScalp$$ATR(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int begin,int end,int h1,int h2,
			int day1,int day2,int nATR,
			double slATR, double tpATR,int maxPositions,
			int minPips,int minSeparationPips,int bars1,int bars,
			double initialBalance,double riskPerTrade,double comm,
			boolean closeOppositeTrades,
			boolean modePullBack,
			boolean log){

		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		int lastPositions = 0;
		File file = new File("c:\\tradelog.txt");
		if (file.exists()) file.delete();
				
		if (riskPerTrade*maxPositions>80)
			riskPerTrade = 80.0/maxPositions;
		
		int factor = -1;
		if (modePullBack) factor = 1;

		double maxDD = 0;
		double maxBalance 		= initialBalance;
		double actualBalance 	= initialBalance;
		double extraNeeded = 0;
		GlobalStats stats = new GlobalStats();
		stats.setBalance(actualBalance);
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		if (begin<1) begin = 1;
		if (end>data.size()-2) end = data.size()-2;
		
		int totalDays = 0;
		int maxOpens = 0;
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		boolean directionSettled = false;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort actualMaxMinPos = new QuoteShort();
		actualMaxMinPos.setHigh5(999999);
		actualMaxMinPos.setLow5(-999999);
		double atr = 100;
		double factorSlTp = slATR/tpATR;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q_1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK); 
						
			if (day!=lastDay){
				int range = (max-min)/10;
				dailyRanges.add(range);
				if (dailyRanges.size()>0){
					atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
					//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
				}
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				directionSettled = false;
				lastDay = day;
				totalDays++;
			}
			if (log){
				if (positions.size()>0 && positions.size()!=lastPositions){
					TradeLog.writeToLog("c:\\tradeLog.txt",q.toString()+" || "+positions.size());
					//lastPositions = positions.size();
				}
			}
			lastPositions = positions.size();
				//System.out.println(q.toString());
			//SL Y TP
			int sl   = (int) (atr*slATR);
			int tp   = (int) (atr*tpATR);
			if (tp<=5){
				 tp=5;
				 sl = (int) (tp*factorSlTp);
			}
			
			int dayAllowed = 1;
			if (dayWeek<day1 || dayWeek>day2) dayAllowed=0;
			int allowed = 1;
			int maxMin = maxMins.get(i);
			if (h>=h1 & h<=h2){
				int diff = (int) ((max-min)*0.1);
				if (q.getHigh5()>max 
						&& diff>=minPips
						&& !directionSettled
						//&& maxMin>=bars1
						){//nuevo maximo
						if (modePullBack){
							longEnabled = true;
							shortEnabled = false;
						}else{
							longEnabled = false;
							shortEnabled = true;
						}
						directionSettled = true;
					//longEnabled = false;
					//shortEnabled = true;
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt","LONG_ENABLED");
					}
						//System.out.println("longEnabled");
				}
				if (q.getLow5()<min 
						&& diff>=minPips
						&& !directionSettled
						//&& maxMin<=-bars1
						){//nuevo minimo					
						if (modePullBack){
							longEnabled = false;
							shortEnabled = true;
						}else{
							longEnabled = true;
							shortEnabled = false;
						}
						directionSettled = true;
					//shortEnabled = false;
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt","SHORT_ENABLED");
					}
						//System.out.println("shortEnabled");
				}
			}
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
				//System.out.println("nuevo max: "+max);
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
			
			if (allowed==1 && dayAllowed==1){//operacion permitida					
				int entry   = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType positionType = PositionType.NONE;
				int diffLastLong = actualMaxMinPos.getHigh5()-q_1.getOpen5();
				int diffLastShort = q_1.getOpen5()-actualMaxMinPos.getLow5();
				
				boolean longCondition = false;
				boolean shortCondition = false;
				
				if (modePullBack){
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}else{
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}
				//System.out.println(" diffLastLong lastShort : "+diffLastLong+" "+diffLastShort+" "+maxMin+" "+positions.size());
				if (longEnabled 
						//&& maxMin>=bars
						&& longCondition
						&& (positions.size()==0 || diffLastLong>=minSeparationPips*10)
						){
					//System.out.println("LONG ACTIVATED");
					int diffMax = -bars-maxMin;//debe ser positivo
					double percent = diffMax*100.0/maxMin;
					int mod = 0;
					//if (percent>50.0) mod = 50;
					
					entry = q_1.getOpen5();
					slValue = entry-sl*10;
					tpValue = entry+tp*10+mod;
					//System.out.println("[LONG] maxMin: "+DateUtils.datePrint(cal1)+" "+maxMin+" "+entry);
					positionType = PositionType.LONG;
					//close all shorts
					if (closeOppositeTrades)
						PositionShort.closeAllPositions(positions, stats,comm, q_1, cal1, i+1,PositionType.SHORT,true);
				}
				if (shortEnabled 
						//&& maxMin<=-bars
						&& shortCondition
						&& (positions.size()==0 || diffLastShort>=minSeparationPips*10)
						){
					
					//experimental
					int diffMax = maxMin-bars;
					double percent = Math.abs(diffMax*100.0/maxMin);
					int mod = 0;
					//if (percent>50.0) mod = 50;
					//experimental
					
					entry = q_1.getOpen5();
					slValue = entry + sl*10;
					tpValue = entry -tp*10-mod;
					//System.out.println("[SHORT] bars maxMin mod sl tp: "+bars+" "+maxMin+" "+mod+" "+slValue+" "+tpValue);
					
					positionType = PositionType.SHORT;
					//close all longs
					if (closeOppositeTrades)
						PositionShort.closeAllPositions(positions, stats,comm, q_1, cal1, i+1,PositionType.LONG,true);
				}
				if ( entry!=-1 && positions.size()<maxPositions){
					//calculo de balance
					//margen requerido
					actualBalance = stats.getBalance();
					double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,riskPerTrade,maxPositions,sl);
					if (actualBalance<minBalance){
						extraNeeded += minBalance-actualBalance;
						actualBalance = minBalance;
					}
					long microLots = TestEAs.calculateMicroLots(actualBalance,400,riskPerTrade,maxPositions,sl);
					//microLots = 1;//1$/pip
					stats.setBalance(actualBalance);
					//System.out.println(actualBalance+" "+microLots);
					//
					
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(positionType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setOpenIndex(i+1);
					pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
					pos.setMicroLots(microLots);
					positions.add(pos);
					if (log){
						TradeLog.writeToLog("c:\\tradeLog.txt",pos.toString2()+" || "+q_1.toString());
						System.out.println(pos.toString2()+" || "+q.toString()+" "+maxMin);
					}
					//System.out.println(pos.toString2()+" || "+q.toString()+" "+maxMin+" "+positions.size());
				}
			}
			
			PositionShort.evaluatePositions(positions,actualMaxMinPos, stats,comm, q_1, cal1, i+1,true);	
			int totalOpens = positions.size();
			if (totalOpens>maxOpens) maxOpens = totalOpens;
			
			if (stats.getBalance()>maxBalance) maxBalance = stats.getBalance();
			else{
				double actualDD = 100.0-stats.getBalance()*100.0/maxBalance;
				if (actualDD>maxDD) maxDD = actualDD;
			}
		}
		
		int wins = stats.getWins();
		int losses = stats.getLosses();
		int total = wins+losses;
		double pf = stats.getWinPips()*1.0/stats.getLostPips();
		double winPer = wins*100.0/total;
		double exp = (stats.getWinPips()-stats.getLostPips())/total;
		
		double f = (maxBalance/(initialBalance+extraNeeded))/maxDD;
		int maxWins = stats.getMaxConsecutiveWins();
		int maxLosses = stats.getMaxConsecutiveLosses();
		double netPips = stats.getWinPips()-stats.getLostPips()-comm*total;
		double profitPerDay = (exp-comm)*total/totalDays;
		
		
		//SimulatedResults sim = stats.simulateSeq(stats.getWinLossesSeq(), initialBalance, tpTrade, riskPerTrade);
		System.out.println(header+" "+h1+" "+h2
				//+" "+minPips
				//+" "+bars1
				+" "+PrintUtils.Print2Int(bars,3)
				+" "+PrintUtils.Print2Int(nATR,3)+" "+PrintUtils.Print2dec(tpATR, false, 2)+" "+PrintUtils.Print2dec(slATR,false,2)
				+" "+PrintUtils.Print2dec(riskPerTrade, false, 2)
				+" || "
				+PrintUtils.Print2Int(total, 7)+" "+PrintUtils.Print2Int(wins, 7)+" "+PrintUtils.Print2Int(losses, 7)
				+" "+PrintUtils.Print2dec(winPer,false,3)
				+" "+PrintUtils.Print2Int(maxOpens,3)
				+" "+PrintUtils.Print2Int(maxWins,4)+" "+PrintUtils.Print2Int(maxLosses,4)
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2dec(exp,false,2)
				//+" "+PrintUtils.Print2dec(profitPerDay,false,2)
				+" "+PrintUtils.Print2dec(stats.getWinPips(),false,2)
				+" "+PrintUtils.Print2dec(stats.getLostPips(),false,2)
				+" "+PrintUtils.Print2dec(netPips,false,2)
				+" || "
				+PrintUtils.Print2dec2(stats.getBalance(), true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec2(extraNeeded, true)
				+" "+PrintUtils.Print2(maxDD)
				//+" || "
				//+" "+PrintUtils.Print2(f)
				//+" || "
				//+riskPerPip+" "+expGained+" "+(initialBalance*(1+expGained/100.0))
				//+" "+sim.toString()
				//+" "+profitCompund
				);
		
	}
	
	public static void testDirectionScalp$$v2(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int begin,int end,int h1,int h2,
			int sl, int tp,int maxPositions,
			int minPips,int minSeparationPips,int bars1,int bars,
			double initialBalance,double riskPerTrade,double comm,
			boolean modePullBack,
			boolean log){
		
		int lastPositions = 0;
		File file = new File("c:\\tradelog.txt");
		if (file.exists()) file.delete();
		
		
		int factor = -1;
		if (modePullBack) factor = 1;

		double maxDD = 0;
		double maxBalance 		= initialBalance;
		double actualBalance 	= initialBalance;
		double extraNeeded = 0;
		GlobalStats stats = new GlobalStats();
		stats.setBalance(actualBalance);
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		if (begin<1) begin = 1;
		if (end>data.size()-2) end = data.size()-2;
		
		int maxOpens = 0;
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort actualMaxMinPos = new QuoteShort();
		actualMaxMinPos.setHigh5(999999);
		actualMaxMinPos.setLow5(-999999);
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q_1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				lastDay = day;
			}
			if (log){
				if (positions.size()>0 && positions.size()!=lastPositions){
					TradeLog.writeToLog("c:\\tradeLog.txt",q.toString()+" || "+positions.size());
					//lastPositions = positions.size();
				}
			}
			lastPositions = positions.size();
				//System.out.println(q.toString());
			
			int allowed = 1;
			int maxMin = maxMins.get(i);
			if (h>=h1 & h<=h2){
				int diff = (int) ((max-min)*0.1);
				if (q.getHigh5()>max 
						&& diff>=minPips
						//&& maxMin>=bars1
						){//nuevo maximo
					if (modePullBack){
						longEnabled = true;
						shortEnabled = false;
					}else{
						longEnabled = false;
						shortEnabled = true;
					}
					//longEnabled = false;
					//shortEnabled = true;
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt","LONG_ENABLED");
					}
						//System.out.println("longEnabled");
				}
				if (q.getLow5()<min 
						&& diff>=minPips
						//&& maxMin<=-bars1
						){//nuevo minimo
					if (modePullBack){
						longEnabled = false;
						shortEnabled = true;
					}else{
						longEnabled = true;
						shortEnabled = false;
					}
					//shortEnabled = false;
					if (log){
						//TradeLog.writeToLog("c:\\tradeLog.txt","SHORT_ENABLED");
					}
						//System.out.println("shortEnabled");
				}
			}
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
				//System.out.println("nuevo max: "+max);
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
			
			if (allowed==1){//operacion permitida					
				int entry   = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType positionType = PositionType.NONE;
				int diffLastLong = actualMaxMinPos.getHigh5()-q_1.getOpen5();
				int diffLastShort = q_1.getOpen5()-actualMaxMinPos.getLow5();
				
				boolean longCondition = false;
				boolean shortCondition = false;
				
				if (modePullBack){
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}else{
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}
				//System.out.println(" diffLastLong lastShort : "+diffLastLong+" "+diffLastShort+" "+maxMin+" "+positions.size());
				if (longEnabled 
						//&& maxMin>=bars
						&& longCondition
						&& (positions.size()==0 || diffLastLong>=minSeparationPips*10)
						){
					//System.out.println("LONG ACTIVATED");
					int diffMax = -bars-maxMin;//debe ser positivo
					double percent = diffMax*100.0/maxMin;
					int mod = 0;
					//if (percent>50.0) mod = 50;
					
					entry = q_1.getOpen5();
					slValue = entry-sl*10;
					tpValue = entry+tp*10+mod;
					if (positions.size()>0){
						int tpValue1 = PositionShort.getMaxTp(positions,PositionType.LONG);
						if (tpValue1>tpValue && tpValue1>=0) tpValue = tpValue1;
					}
					//System.out.println("[LONG] maxMin: "+DateUtils.datePrint(cal1)+" "+maxMin+" "+entry);
					positionType = PositionType.LONG;
				}
				if (shortEnabled 
						//&& maxMin<=-bars
						&& shortCondition
						&& (positions.size()==0 || diffLastShort>=minSeparationPips*10)
						){
					
					//experimental
					int diffMax = maxMin-bars;
					double percent = Math.abs(diffMax*100.0/maxMin);
					int mod = 0;
					//if (percent>50.0) mod = 50;
					//experimental
					
					entry = q_1.getOpen5();
					slValue = entry + sl*10;
					tpValue = entry -tp*10-mod;
					//System.out.println("[SHORT] bars maxMin mod sl tp: "+bars+" "+maxMin+" "+mod+" "+slValue+" "+tpValue);
					if (positions.size()>0){
						int tpValue1 = PositionShort.getMaxTp(positions,PositionType.SHORT);
						if (tpValue1<tpValue && tpValue1>=0) tpValue = tpValue1;
					}
					positionType = PositionType.SHORT;
				}
				if ( entry!=-1 && positions.size()<maxPositions){
					//calculo de balance
					//margen requerido
					actualBalance = stats.getBalance();
					double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,riskPerTrade,maxPositions,sl);
					if (actualBalance<minBalance){
						extraNeeded += minBalance-actualBalance;
						actualBalance = minBalance;
					}
					long microLots = TestEAs.calculateMicroLots(actualBalance,400,riskPerTrade,maxPositions,sl);
					//microLots = 1;//1$/pip
					stats.setBalance(actualBalance);
					//
					
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(positionType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setOpenIndex(i+1);
					pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
					pos.setMicroLots(microLots);
					positions.add(pos);
					if (log){
						TradeLog.writeToLog("c:\\tradeLog.txt",pos.toString2()+" || "+q_1.toString());
						System.out.println(pos.toString2()+" || "+q.toString()+" "+maxMin);
					}
				}
			}
			
			PositionShort.evaluatePositions(positions,actualMaxMinPos, stats,comm, q_1, cal1, i+1,true);	
			int totalOpens = positions.size();
			if (totalOpens>maxOpens) maxOpens = totalOpens;
			
			if (stats.getBalance()>maxBalance) maxBalance = stats.getBalance();
			else{
				double actualDD = 100.0-stats.getBalance()*100.0/maxBalance;
				if (actualDD>maxDD) maxDD = actualDD;
			}
		}
		
		int wins = stats.getWins();
		int losses = stats.getLosses();
		int total = wins+losses;
		double pf = stats.getWinPips()*1.0/stats.getLostPips();
		double winPer = wins*100.0/total;
		double exp = (stats.getWinPips()-stats.getLostPips())/total;
		
		double f = (maxBalance/(initialBalance+extraNeeded))/maxDD;
		int maxWins = stats.getMaxConsecutiveWins();
		System.out.println(header+" "+h1+" "+h2
				+" "+minPips
				+" "+bars1+" "+bars
				+" "+tp+" "+sl
				+" || "+PrintUtils.Print2Int(total, 5)+" "+PrintUtils.Print2Int(wins, 5)+" "+PrintUtils.Print2Int(losses, 5)
				+" "+PrintUtils.Print2dec(winPer,false,3)
				+" "+PrintUtils.Print2Int(maxOpens,3)
				+" "+maxWins
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2dec(exp,false,2)
				+" "+PrintUtils.Print2dec(stats.getWinPips(),false,2)+" "+stats.getLostPips()
				+" || "+PrintUtils.Print2dec2(stats.getBalance(), true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec2(extraNeeded, true)
				+" "+PrintUtils.Print2(maxDD)
				+" || "
				+" "+PrintUtils.Print2(f)
				);
		
	}
	
	/**
	 * Se van abriendo posiciones hasta que el total este en +5, entonces se cierran todas, probar a abrirlas cada 10 o mas pips en contra
	 * @param header
	 * @param data
	 * @param maxMins
	 * @param begin
	 * @param end
	 * @param h1
	 * @param h2
	 * @param day1
	 * @param day2
	 * @param sl
	 * @param tp
	 * @param maxPositions
	 * @param minPips
	 * @param minSeparationPips
	 * @param bars1
	 * @param bars
	 * @param initialBalance
	 * @param riskPerTrade
	 * @param comm
	 * @param closeOppositeTrades
	 * @param modePullBack
	 * @param log
	 */
	public static void testDirectionScalp$$v3(String header,ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int begin,int end,int h1,int h2,
			int day1,int day2,
			int sl, int tp,int maxPositions,
			int minPips,int minSeparationPips,
			int barsGroup,int bars1,int bars,
			double initialBalance,double riskPerTrade,double comm,
			boolean closeOppositeTrades,
			boolean modePullBack,
			boolean modeAdding,
			boolean log){
		
		QuoteShort actualMaxMin = new QuoteShort();
		int lastPositions = 0;
		File file = new File("c:\\tradelog.txt");
		if (file.exists()) file.delete();				
		int factor = -1;
		if (modePullBack) factor = 1;
		double maxPipsDD = 0;
		double maxPips = 0;
		double maxNegative = 99999;
		double maxDD = 0;
		double maxBalance 		= initialBalance;
		double actualBalance 	= initialBalance;
		double extraNeeded = 0;
		GlobalStats stats = new GlobalStats();
		stats.setBalance(actualBalance);		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		if (begin<1) begin = 1;
		if (end>data.size()-2) end = data.size()-2;
		
		int totalDays = 0;
		int maxOpens = 0;
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		
		boolean longEnabled  = false;
		boolean shortEnabled = false;
		boolean directionSettled = false;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort actualMaxMinPos = new QuoteShort();
		actualMaxMinPos.setHigh5(999999);
		actualMaxMinPos.setLow5(-999999);
		
		int closedTrades = 0;
		double closedPips = 0;
		double floatingPips = 0;
		//BUCLE PRINCIPAL
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q_1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK); 
						
			if (day!=lastDay){
				min = 999999;
				max = -999999;
				longEnabled = false;
				shortEnabled = false;
				directionSettled = false;
				lastDay = day;
				totalDays++;
				String posStr ="";
				if (positions.size()>0) posStr = positions.get(0).toString2();
				floatingPips = PositionShort.getFloatingPips(positions,q.getOpen5());
				/*System.out.println("[NEW DAY] "						
						+" "+DateUtils.datePrint(cal)+" "+positions.size()
						+" "+closedTrades+" "+PrintUtils.Print2dec(closedPips,false,2)+" "+PrintUtils.Print2dec(floatingPips,false,2)
						//+" "+posStr
						);*/
				closedPips = 0;
				closedTrades = 0;
			}
						
			int dayAllowed = 1;
			if (dayWeek<day1 || dayWeek>day2) dayAllowed=0;
			int allowed = 1;
			//int maxMin = maxMins.get(i);
			int maxMin = getMaxMinGroup(maxMins,i,barsGroup);
			if (h>=h1 & h<=h2){
				int diff = (int) ((max-min)*0.1);
				if (q.getHigh5()>max 
						&& diff>=minPips
						&& !directionSettled
						){//nuevo maximo
					if (modePullBack){
						longEnabled = true;
						shortEnabled = false;
						//System.out.println("[ENABLED] LONG");
					}else{
						longEnabled = false;
						shortEnabled = true;
						//System.out.println("[ENABLED] SHORT");
					}
					directionSettled = true;					
				}
				if (q.getLow5()<min 
						&& diff>=minPips
						&& !directionSettled
						){//nuevo minimo					
					if (modePullBack){
						longEnabled = false;
						shortEnabled = true;
						//System.out.println("[ENABLED] SHORT");
					}else{
						longEnabled = true;
						shortEnabled = false;
						//System.out.println("[ENABLED] LONG");
					}
					directionSettled = true;					
				}
			}
			//actualizacion de maximos y minimos
			if (q.getHigh5()>max){
				max = q.getHigh5();
			}
			if (q.getLow5()<min){
				min = q.getLow5();
			}
			
			if (allowed==1 && dayAllowed==1){//operacion permitida					
				int entry   = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType positionType = PositionType.NONE;				
				boolean longCondition = false;
				boolean shortCondition = false;	
				int slAdj = sl;
				int tpAdj = tp;
				boolean addLong = false;
				boolean addShort = false;
				String posHeader = "[NEWPOS]";
				
				PositionShort.getHigherLowerPos(positions, actualMaxMin);
				int diffLong = actualMaxMin.getLow5()-q_1.getOpen5();
				int diffShort = q_1.getOpen5()-actualMaxMin.getHigh5();
				if (modePullBack){
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}else{
					if (maxMin<=-bars) longCondition = true;
					if (maxMin>=bars) shortCondition = true;
				}								
				if (modeAdding && positions.size()>0){ //solo agregamos a su mismo tipo
					slAdj = 999999;
					tpAdj = 999999;
					if (positions.get(0).getPositionType() == PositionType.LONG && diffLong>=minSeparationPips*10 && positions.size()<maxPositions ){
						//System.out.println("[ADDLONG] entry "+q_1.getOpen5()+" "+diffLong+" "+(minSeparationPips*10)+" "+positions.size());
						posHeader = "[ADDLONG]";
						addLong = true;
					}
					if (positions.get(0).getPositionType() == PositionType.SHORT && diffShort>=minSeparationPips*10 && positions.size()<maxPositions ){
						//System.out.println("[ADDSHORT] entry "+q_1.getOpen5()+" "+diffShort+" "+(minSeparationPips*10)+" "+positions.size());
						posHeader = "[ADDSHORT]";
						addShort = true;
					}
				}
								
				if (longEnabled && longCondition){// && (!modeAdding || positions.size()==0)) || (modeAdding && addLong)){
					entry = q_1.getOpen5();
					slValue = entry-slAdj*10;
					tpValue = entry+tpAdj*10;
					positionType = PositionType.LONG;					
				}
				if (shortEnabled && shortCondition){// && (!modeAdding || positions.size()==0)) || (modeAdding && addShort)){					
					entry = q_1.getOpen5();
					slValue = entry +slAdj*10;
					tpValue = entry -tpAdj*10;					
					positionType = PositionType.SHORT;					
				}
				
				//NUEVA POSICION
				if (entry!=-1 && positions.size()<maxPositions){
					//calculo de balance
					//margen requerido
					actualBalance = stats.getBalance();
					double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,riskPerTrade,maxPositions,sl);
					if (actualBalance<minBalance){
						extraNeeded += minBalance-actualBalance;
						actualBalance = minBalance;
					}
					long microLots = TestEAs.calculateMicroLots(actualBalance,400,riskPerTrade,maxPositions,sl);
					//microLots = 1;//1$/pip
					stats.setBalance(actualBalance);
					//
					
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(positionType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setOpenIndex(i+1);
					pos.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
					pos.setMicroLots(microLots);
					positions.add(pos);	
					//System.out.println(posHeader+" "+pos.toString2());
				}
			}
			
			//EVALUACION Q+1: si todas las posiciones en conjunto han hecho +tp, entonces se cierran todas
			int accPips = 0;
			int s = 0;
			while (s<positions.size()){
				PositionShort p = positions.get(s);
				boolean closed = false;
				int win = 0;
				if (p.getPositionType()==PositionType.LONG){
					int pipsC = q_1.getClose5()-p.getEntry();
					if (q_1.getLow5()<=p.getSl()){
						closed = true;
						win = -1;
					}else if (q_1.getHigh5()>=p.getTp()){
						closed = true;
						win = 1;
					}else{
						accPips += pipsC;
					}
				}
				if (p.getPositionType()==PositionType.SHORT){
					int pipsC = p.getEntry()-q_1.getClose5();
					if (q_1.getHigh5()>=p.getSl()){
						closed = true;
						win = -1;
					}else if (q_1.getLow5()<=p.getTp()){
						closed = true;
						win = 1;
					}else{
						accPips += pipsC;
					}
				}
				
				if (closed){
					double pipsEarned = 0;
					if (win==-1){											
						pipsEarned = Math.abs(p.getSl()-p.getEntry())*0.1;
						stats.addLosses(1);
						stats.addLostPips(pipsEarned);
						stats.addWinLossSeq(-1);
					}else if (win==1){
						pipsEarned = Math.abs(p.getTp()-p.getEntry())*0.1;
						
						stats.addWins(1);
						stats.addWinPips(pipsEarned);
						stats.addWinLossSeq(1);
						//System.out.println("pipsearned: "+pipsEarned);
					}
					closedPips +=win*pipsEarned;
					closedTrades++;
					p.setWin(-1);
					positions.remove(s);
				}else{
					s++;
				}
			}
			
			if (modeAdding){
				double totalPips = stats.getWinPips()-stats.getLostPips();			
				double actualPips = totalPips+accPips*0.1;
				if (actualPips>maxPips) maxPips = actualPips;
				double pipsDD = maxPips-actualPips;
				if (pipsDD>maxPipsDD) maxPipsDD = pipsDD;
				if (accPips!=0){
					//System.out.println(DateUtils.datePrint(cal1)+" "+PrintUtils.Print2dec(accPips*0.1, false, 2)+" "+PrintUtils.Print2dec(totalPips, false, 2)+" "+positions.size());
				}
				if (accPips<maxNegative){
					maxNegative = accPips-comm*positions.size();
					//System.out.println(DateUtils.datePrint(cal1)+" "+PrintUtils.Print2dec(accPips*0.1, false, 2)+" "+PrintUtils.Print2dec(totalPips, false, 2)+" "+positions.size());
				}
				if (accPips*0.1>=tp){			
					closedPips+=accPips*0.1;
					int totalOpen = positions.size();
					closedTrades+=totalOpen;
					double longPips = PositionShort.closeAllPositions(positions, stats, comm, q_1, cal1, i+1, PositionType.LONG, false);
					double shortPips = PositionShort.closeAllPositions(positions, stats, comm, q_1, cal1, i+1, PositionType.SHORT, false);
					/*System.out.println("[CLOSED ALL] totalOpen= "+totalOpen
							+" "+PrintUtils.Print2((accPips*0.1))
							+" "+PrintUtils.Print2(longPips)
							+" "+PrintUtils.Print2(shortPips)
							+" || "+PrintUtils.Print2(stats.getWinPips())
							+" "+PrintUtils.Print2(stats.getLostPips())
							+" ("+PrintUtils.Print2(stats.getWinPips()-stats.getLostPips())+") "
							);*/
					//se cierran todas
				}
			}
			//PositionShort.evaluatePositions(positions,actualMaxMinPos, stats,comm, q_1, cal1, i+1,true);
			
			int totalOpens = positions.size();
			if (totalOpens>maxOpens) maxOpens = totalOpens;
			
			if (stats.getBalance()>maxBalance) maxBalance = stats.getBalance();
			else{
				double actualDD = 100.0-stats.getBalance()*100.0/maxBalance;
				if (actualDD>maxDD) maxDD = actualDD;
			}
		}
		
		int wins = stats.getWins();
		int losses = stats.getLosses();
		int total = wins+losses;
		double pf = stats.getWinPips()*1.0/stats.getLostPips();
		double winPer = wins*100.0/total;
		double exp = (stats.getWinPips()-stats.getLostPips())/total;
		
		double f = (maxBalance/(initialBalance+extraNeeded))/maxDD;
		int maxWins = stats.getMaxConsecutiveWins();
		int maxLosses = stats.getMaxConsecutiveLosses();
		double netPips = stats.getWinPips()-stats.getLostPips()-comm*total;
		double profitPerDay = (exp-comm)*total/totalDays;
		double factorProf = netPips/Math.abs(maxPipsDD+comm);
		System.out.println(header+" "+h1+" "+h2
				+" "+minPips
				+" "+bars1+" "+PrintUtils.Print2Int(bars,3)
				+" "+PrintUtils.Print2Int(tp,3)+" "+PrintUtils.Print2Int(sl,3)
				+" "+PrintUtils.Print2dec(riskPerTrade, false, 2)
				+" || "+PrintUtils.Print2Int(total, 6)+" "+PrintUtils.Print2Int(wins, 6)+" "+PrintUtils.Print2Int(losses, 6)
				
				+" "+PrintUtils.Print2dec(winPer,false,3)
				+" "+PrintUtils.Print2Int(maxOpens,3)
				+" "+maxWins+" "+maxLosses
				+" "+PrintUtils.Print2dec(pf,false,2)
				+" "+PrintUtils.Print2dec(exp,false,2)
				//+" "+PrintUtils.Print2dec(profitPerDay,false,2)
				+" "+PrintUtils.Print2dec(stats.getWinPips(),false,2)
				+" "+PrintUtils.Print2dec(stats.getLostPips(),false,2)+" "+PrintUtils.Print2dec(netPips,false,2)
				+" "+PrintUtils.Print2dec(maxNegative*0.1,false,2)
				+" "+PrintUtils.Print2dec(maxPipsDD+comm,false,2)
				+" "+PrintUtils.Print2dec(factorProf,false,2)
				+" || "+PrintUtils.Print2dec2(stats.getBalance(), true)
				+" "+PrintUtils.Print2dec2(maxBalance, true)
				+" "+PrintUtils.Print2dec2(extraNeeded, true)
				+" "+PrintUtils.Print2(maxDD)
				+" || "
				+" "+PrintUtils.Print2(f)
				);
		
	}
	
	private static int getMaxMinGroup(ArrayList<Integer> maxMins, int actual,
			int barsGroup) {
		// TODO Auto-generated method stub
		int sum = 0;
		int begin = actual-barsGroup;
		if (begin<=0) begin = 0;
		for (int i=begin;i<=actual;i++){
			sum+=maxMins.get(i);
		}
				
		return sum;
	}
	
	private static int getMaxMinGroup2(ArrayList<Integer> maxMins, int actual,
			int barsGroup,int nBars) {
		// TODO Auto-generated method stub
		
		int begin = actual-barsGroup;
		if (begin<=0) begin = 0;
		for (int i=begin;i<=actual;i++){
			if (maxMins.get(i)>=nBars && nBars>=0)
				return nBars;
			if (maxMins.get(i)<=-nBars && nBars<0)
				return nBars;
		}
				
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.23.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.23.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.05.04_2015.03.23.csv";
		
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.23.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.08.03_2015.03.23.csv";
		String pathGBPCAD = "C:\\fxdata\\GBPCAD_UTC_5 Mins_Bid_2006.01.02_2015.03.23.csv";
		String pathEURAUD = "C:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2005.10.07_2015.03.23.csv";
		String pathCADJPY= "C:\\fxdata\\CADJPY_UTC_5 Mins_Bid_2004.10.25_2015.03.23.csv";
		String pathNZDUSD= "C:\\fxdata\\NZDUSD_UTC_5 Mins_Bid_2003.08.03_2015.03.23.csv";
		String pathNZDJPY= "C:\\fxdata\\NZDJPY_UTC_5 Mins_Bid_2006.01.02_2015.03.23.csv";*/
		
		/*String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2015.03.23.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2003.05.04_2015.03.23.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2003.08.03_2015.03.23.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2015.03.23.csv";*/
		
		/*String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2014.01.01_2015.03.28.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2014.01.01_2015.03.28.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2014.01.01_2015.03.28.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2014.01.01_2015.03.28.csv";*/
		
		for (int year=2013;year<=2013;year++){
			//int year = 2013;
			
			//String fileName = "1 Min_Bid_"+year+".csv";
			//String fileName = "1 Min_Bid_"+year+".01.01_"+year+".12.31.csv";
			//String fileName ="2004.01.01_2015.03.29.csv";//completo
			//String fileName ="2012.12.31_2015.04.04.csv";
			//String fileName ="2010.12.31_2012.12.30.csv";
			//String fileName ="2013.01.01_2015.04.05.csv";
			//String fileName ="2011.01.01_2015.04.05.csv";
			//String fileName ="2009.01.01_2011.12.31.csv";
			//String fileName ="1 Min_Bid_2015.01.01_2015.04.05.csv";
			
			//String fileName ="5 Mins_Bid_2003.05.04_2015.04.05.csv";
			//String fileName = "5 Mins_Bid_2008.12.31_2015.04.07.csv";
			//String fileName ="5 Mins_Bid_2012.12.31_2015.04.07.csv";
			//String fileName ="5 Mins_Bid_2008.12.31_2012.12.30.csv";
			//String fileName   ="1 Min_Bid_2013.01.01_2015.04.05.csv";
			//String fileName ="1 Min_Bid_2003.05.04_2008.12.30.csv";
			//String fileName ="1 Min_Bid_2008.12.31_2012.12.30.csv";
			//String fileName ="1 Min_Bid_2009.01.01_2015.04.06.csv";
			//String fileName ="1 Min_Bid_2013.12.31_2015.04.08.csv";
			//String fileName ="1 Min_Bid_2003.05.04_2015.04.08.csv";
			//String fileName ="1 Min_Bid_2012.12.31_2015.04.09.csv";
			//String fileName   ="1 Min_Bid_2011.12.31_2015.04.09.csv";
			//String fileName   ="1 Min_Bid_2010.12.31_2015.04.09.csv";
			//String fileName   ="1 Min_Bid_2003.08.03_2015.04.12.csv";
			
			String fileName0   ="1 Min_Bid_2004.12.31_2006.12.30.csv";
			String fileName1   ="1 Min_Bid_2006.12.31_2008.12.30.csv";
			String fileName2   ="1 Min_Bid_2008.12.31_2010.12.30.csv";
			String fileName3   ="1 Min_Bid_2010.12.31_2012.12.30.csv";
			String fileName4   ="1 Min_Bid_2012.12.31_2015.04.13.csv";
			//String fileName   ="1 Min_Bid_2010.12.31_2015.04.13.csv";
			//String fileName   ="1 Min_Bid_2014.12.31_2015.06.05.csv";
			String fileName5   ="1 Min_Bid_2008.12.31_2015.06.05.csv";
			String fileNameP   ="2015_02_17_2015_04_22.csv";
			
			String pathEURUSD0 = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2004.12.31_2006.12.30.csv";
			String pathEURUSD1 = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2006.12.31_2008.12.30.csv";
			String pathEURUSD2 = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2010.12.30.csv";
			String pathEURUSD3 = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2010.12.31_2012.12.30.csv";
			String pathEURUSD4 = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2012.12.31_2015.06.06.csv";
			String pathEURUSD5 = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2015.06.05.csv";
			
			String pathGBPUSD6 = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2004.12.31_2006.12.30.csv";
			String pathGBPUSD7 = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2006.12.31_2008.12.30.csv";
			String pathGBPUSD8 = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2010.12.30.csv";
			String pathGBPUSD9 = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2010.12.31_2012.12.30.csv";
			String pathGBPUSD10 = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2012.12.31_2015.06.06.csv";
			String pathGBPUSD11 = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2015.06.05.csv";
			
			String pathAUDUSD12 = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2004.12.31_2006.12.30.csv";
			String pathAUDUSD13 = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2006.12.31_2008.12.30.csv";
			String pathAUDUSD14 = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2008.12.31_2010.12.30.csv";
			String pathAUDUSD15 = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2010.12.31_2012.12.30.csv";
			String pathAUDUSD16 = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2012.12.31_2015.06.06.csv";
			String pathAUDUSD17 = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2008.12.31_2015.06.05.csv";
			
			//String pathUSDJPY18 = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2004.12.31_2006.12.30.csv";
			//String pathUSDJPY19 = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2006.12.31_2008.12.30.csv";
			String currency = "usdjpy";
			
			ArrayList<String> currencies = new ArrayList<String> ();
			currencies.add("eurusd");
			currencies.add("gbpusd");
			currencies.add("usdjpy");
			currencies.add("eurgbp");
			currencies.add("eurjpy");
			currencies.add("gbpjpy");
			currencies.add("eurgbp");
			
			int limitc = currencies.size()-1;
			limitc=2;
			for (int c=2;c<=limitc;c++){
				currency = currencies.get(c);
				String pathUSDJPY20 = "C:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2003.12.31_2007.12.30.csv";
				String pathUSDJPY21 = "C:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2007.12.31_2010.12.30.csv";
				String pathUSDJPY22 = "C:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2010.12.31_2013.12.30.csv";
				String pathUSDJPY23 = "C:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2005.12.31_2010.12.30.csv";
				String pathUSDJPY18 = "C:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2003.05.04_2016.04.12.csv";
				String pathUSDJPY19 = "C:\\fxdata\\"+currency+"_UTC_1 Min_Bid_2012.12.31_2015.11.07.csv";
				//testing 5min
				//String pathUSDJPY19 = "C:\\fxdata\\"+currency+"_UTC_5 Mins_Bid_2003.12.31_2015.09.14.csv";
				//String pathUSDJPY19   = "c:\\fxdata\\"+currency+"_forexdata_5min_1986_2012.csv";
				
				ArrayList<String> paths = new ArrayList<String>();
				ArrayList<String> names = new ArrayList<String>();
				paths.add(pathEURUSD0);paths.add(pathEURUSD1);paths.add(pathEURUSD2);paths.add(pathEURUSD3);paths.add(pathEURUSD4);paths.add(pathEURUSD5);
				paths.add(pathGBPUSD6);paths.add(pathGBPUSD7);paths.add(pathGBPUSD8);paths.add(pathGBPUSD9);paths.add(pathGBPUSD10);paths.add(pathGBPUSD11);
				paths.add(pathAUDUSD12);paths.add(pathAUDUSD13);paths.add(pathAUDUSD14);paths.add(pathAUDUSD15);paths.add(pathAUDUSD16);paths.add(pathAUDUSD17);
				paths.add(pathUSDJPY18);paths.add(pathUSDJPY19);paths.add(pathUSDJPY20);paths.add(pathUSDJPY21);paths.add(pathUSDJPY22);paths.add(pathUSDJPY23);
				//paths.add(pathUSDCAD);
				//paths.add(pathNZDUSD);
				//paths.add(pathEURJPY);
				//paths.add(pathEURGBP);
				//paths.add(pathEURAUD);
				//paths.add(pathCADJPY);
				//paths.add(pathAUDJPY);
				//paths.add(pathNZDJPY);
				//paths.add(pathGBPJPY);
				//paths.add(pathGBPCAD);
				//paths.add(pathGBPAUD);
				
				int limit = paths.size()-1;
				int initial = 18;
				limit       = 18;
				for (int i=initial;i<=limit;i++){
					String path5m = paths.get(i);
					String pairName = paths.get(i).split("\\\\")[2].substring(0, 6);
					ArrayList<Quote> dataI 		=  null;
					ArrayList<Quote> dataS 		= null;
					if (path5m.contains("pepper")){
						dataI 		= DAO.retrieveData(path5m, DataProvider.PEPPERSTONE_FOREX);
						dataS 		= dataI;
					}else if (path5m.contains("forexdata")){
						dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX2);
						dataS 		= TestLines.calculateCalendarAdjusted(dataI);
					}else{
						dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
						dataS 		= TestLines.calculateCalendarAdjusted(dataI);
					}		
					//ArrayList<Quote> dataI 		= DAO.retrieveData(paths.get(i), DataProvider.DUKASCOPY_FOREX);
					//ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
					ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);				
					ArrayList<QuoteShort> data = null;
					data = data5mS;
					//System.out.println("total data: "+data.size());
					ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
					
					int begin = 4000000;
					int end   = 5000000;
					int tp = 10;
					int sl = 20;
					double comm = 1.4;
					
					for (begin=1;begin<=1;begin+=80000){
						end = begin + 8000000;
						for (int h1=16;h1<=16;h1++){
							//int h2 = h1+0;
							int h2 = 23;
							int ha1 = h1;
							int ha2 = h2;
							for (ha1 = h1+0;ha1<=h1+0;ha1++){
								ha2=ha1+7;
								for (int minPips=1;minPips<=1;minPips+=1){
									for (int bars1=1;bars1<=1;bars1+=1){
										for (int barsGroup =0;barsGroup<=0;barsGroup++){
											for (int bars = 200;bars<=200;bars+=30){
												//for (sl=2;sl<=100;sl+=1){
												for (tp=6;tp<=6;tp+=1){	
													//for (sl=(int) (8.0*tp);sl<=8*tp;sl+=1.0*tp){
														//System.out.println(tp+" "+sl+" "+0.1*tp);
														//if (sl<10) continue;
													//for (tp=(int) (50.0*sl);tp<=50.0*sl;tp+=1.0*sl){
													for (sl=80;sl<=80;sl+=5){
														for (int maxPositions=30;maxPositions<=30;maxPositions+=1){
															String header = currency;
															//TestScalping.testDirectionScalp(data, maxMins, begin, end, h1, h2, sl, tp,maxPositions,minPips, bars1,bars,false);
															for (int minSeparationPips=-9999;minSeparationPips<=-9999;minSeparationPips++){
																double maxRisk = 100.0/maxPositions;
																//maxRisk = 40.0;
																//maxRisk = 0.5;
																double initialRisk = 1.0;
																//double initialRisk = 1.0;
																//maxRisk = 80.0;
																//if (maxRisk>=30.0) maxRisk = 30.0;
																//System.out.println(initialRisk+" "+maxRisk);
																//for (double risk=1.0;risk<=1.0;risk+=0.10){
																for (double risk=initialRisk;risk<=initialRisk ;risk+=0.10){
																	for (comm=2.0;comm<=2.0;comm+=0.5){
																		for (int year1=2003;year1<=2016;year1++){
																			int year2 = year1+0;
																			for (int day1=Calendar.MONDAY+0;day1<=Calendar.MONDAY+0;day1++){
																				int day2 = day1+4;
																				TestScalping.testDirectionScalp$$(header,data, maxMins, begin, end,year1,year2,
																				//TestScalping.testDirectionScalp$$OpenFrom(header,data, maxMins, begin, end,
																					h1, h2,ha1,ha2,day1,day2, sl, tp,maxPositions,minPips,minSeparationPips,barsGroup,
																					bars1,bars,10000,risk,comm,false,true,false,800);
																				//TestScalping.testDirectionScalp$$(header,data, maxMins, begin, end,
																					//h1, h2,day1,day2, sl, tp,maxPositions,minPips,minSeparationPips, bars1,bars,10000,risk,comm,false,true,false);//sequencia suma +1 -1 +1 -1
																				//configuracion 100% al año con DD<40% : 6-80-30-2.5%
																				//configuracion arriesgada DD<60% 6-80-15-5%																
																				//TestScalping.testDirectionScalp$$v3(header,data, maxMins, begin, end,
																						//h1, h2,day1,day2, sl, tp,maxPositions,minPips,minSeparationPips,barsGroup, bars1,bars,500,risk,comm,false,true,false,false);
																			}
																		}
																	}
																	//TestScalping.testDirectionScalp$$v2(header,data, maxMins, begin, end, h1, h2, sl, tp,maxPositions,minPips,minSeparationPips, bars1,bars,5,risk,comm,true,false);
																}
															}
														}
													}					
												}
											}//bars
										}//barsGroup
									}
								}
							}//ha1
							//System.out.println(h1);
						}//h
						
					}
				}//paths
			}//currencies
		}//year
	}

}
