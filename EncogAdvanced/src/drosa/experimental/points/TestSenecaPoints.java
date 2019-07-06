package drosa.experimental.points;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.experimental.PositionsStats;
import drosa.experimental.SuperStrategy;
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

public class TestSenecaPoints {
	
	public static void testDailyHLmaeATR(ArrayList<QuoteShort> data,int begin,int end,
			ArrayList<QuoteShort> maxMins,
			String hours,int l,int nATR,double comm){
		
		if (begin<=0) begin =0;
		if (end>data.size()-2) end = data.size()-2;
		
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		int lastDay = -1;
		int actualHigh = -1;
		int actualLow = 999999;
		double atr = 100;
		int totalDays = 0;
		int lastHigh = -1;
		int lastLow = -1;
		double avgDiff = 0;
		int total = 0;
		double totalPips = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (actualHigh-actualLow)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range  "+actualHigh+" "+actualLow+" "+atr+" "+range);
					}
				}
				lastDay = day;
				totalDays++;
				//actualizamos highs/lows
				lastHigh = actualHigh;
				lastLow  = actualLow;
				actualHigh = -1;
				actualLow = 999999999;				
			}
			//logica de las entradas
			
			//if (q.getHigh5()>=lastHigh && maxMins.get(i).getExtra()>=nbars
			int hAllowed = allowedHours.get(h);
			if (lastHigh!=-1 && q.getHigh5()>=lastHigh  && hAllowed==1){
				QuoteShort qMax = TradingUtils.getMaxMinShort(data, i+1, i+l);
				int pipsWin  = q1.getOpen5()-qMax.getLow5();
				int pipsLost = qMax.getHigh5()-q1.getOpen5();
				totalPips += (pipsWin-pipsLost)*0.1-comm;
				double diffAtr = ((pipsWin-pipsLost)*0.1-comm)/atr;
				//System.out.println("diffAtr "+actualHigh+" "+diffAtr);
				avgDiff+=diffAtr;
				total++;
			}
			
			if (lastLow!=-1 && q.getLow5()<=lastLow  && hAllowed==1){
				QuoteShort qMax = TradingUtils.getMaxMinShort(data, i+1, i+l);
				int pipsWin  = qMax.getHigh5()-q1.getOpen5();
				int pipsLost = q1.getOpen5()-qMax.getLow5();
				totalPips += (pipsWin-pipsLost)*0.1-comm;
				double diffAtr = ((pipsWin-pipsLost)*0.1-comm)/atr;
				avgDiff+=diffAtr;
				total++;
			}
						
			//actualizamos daily highs/lows
			if (q.getHigh5()>actualHigh){
				actualHigh = q.getHigh5();
			}
			if (q.getLow5()<actualLow){
				actualLow = q.getLow5();
			}		
		}
		double avg = avgDiff*1.0/total;
		System.out.println(hours+" "+l+" "+total+" || "+PrintUtils.Print2(avg)
				+" "+PrintUtils.Print2(totalPips)+" "+PrintUtils.Print2(totalPips/total)
				);
		//System.out.println(l+" "+PrintUtils.Print2(avg));
	}
	
	public static void testDailyHLmaeATR_l(ArrayList<QuoteShort> data,int begin,int end,
			ArrayList<QuoteShort> maxMins,
			String hours,int l,int nATR,double comm){
		
		if (begin<=0) begin =0;
		if (end>data.size()-2) end = data.size()-2;
		
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		int lastDay = -1;
		int actualHigh = -1;
		int actualLow = 999999;
		double atr = 100;
		int totalDays = 0;
		int lastHigh = -1;
		int lastLow = -1;
		double avgDiff = 0;
		int total = 0;
		double totalPips = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (actualHigh-actualLow)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range  "+actualHigh+" "+actualLow+" "+atr+" "+range);
					}
				}
				lastDay = day;
				totalDays++;
				//actualizamos highs/lows
				lastHigh = actualHigh;
				lastLow  = actualLow;
				actualHigh = -1;
				actualLow = 999999999;				
			}
			//logica de las entradas
			
			//if (q.getHigh5()>=lastHigh && maxMins.get(i).getExtra()>=nbars
			int hAllowed = allowedHours.get(h);
			if (lastHigh!=-1 && q.getHigh5()>=lastHigh  && hAllowed==1){
				QuoteShort qMax = TradingUtils.getMaxMinShort(data, i+1, i+l);
				int pipsWin  = q1.getOpen5()-qMax.getLow5();
				int pipsLost = qMax.getHigh5()-q1.getOpen5();
				totalPips += (pipsWin-pipsLost)*0.1-comm;
				double diffAtr = ((pipsWin-pipsLost)*0.1-comm)/atr;
				//System.out.println("diffAtr "+actualHigh+" "+diffAtr);
				avgDiff+=diffAtr;
				total++;
			}
			
			if (lastLow!=-1 && q.getLow5()<=lastLow  && hAllowed==1){
				QuoteShort qMax = TradingUtils.getMaxMinShort(data, i+1, i+l);
				int pipsWin  = qMax.getHigh5()-q1.getOpen5();
				int pipsLost = q1.getOpen5()-qMax.getLow5();
				totalPips += (pipsWin-pipsLost)*0.1-comm;
				double diffAtr = ((pipsWin-pipsLost)*0.1-comm)/atr;
				avgDiff+=diffAtr;
				total++;
			}
						
			//actualizamos daily highs/lows
			if (q.getHigh5()>actualHigh){
				actualHigh = q.getHigh5();
			}
			if (q.getLow5()<actualLow){
				actualLow = q.getLow5();
			}		
		}
		double avg = avgDiff*1.0/total;
		System.out.println(hours+" "+l+" "+total+" || "+PrintUtils.Print2(avg)
				+" "+PrintUtils.Print2(totalPips)+" "+PrintUtils.Print2(totalPips/total)
				);
		//System.out.println(l+" "+PrintUtils.Print2(avg));
	}
	
	public static void testDailyHLclose(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			String hours,int nClose,
			int orderCheck,double comm,boolean checkOne){
				
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		int lastDay = -1;
		int actualHigh = -1;
		int actualLow = 999999;
		int lastHigh = -1;
		int lastLow = -1;
		int avgDiff = 0;
		int total = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-nClose;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				lastDay = day;
				//actualizamos highs/lows
				lastHigh = actualHigh;
				lastLow  = actualLow;
				actualHigh = -1;
				actualLow = 99999;
			}
			//logica de las entradas
			
			//if (q.getHigh5()>=lastHigh && maxMins.get(i).getExtra()>=nbars
			int hAllowed = allowedHours.get(h);
			if (lastHigh!=-1 && q.getHigh5()>=lastHigh  && hAllowed==1){
				QuoteShort qClose = data.get(i+nClose);
				int pipsWin  = q1.getOpen5()-qClose.getClose5();				
				avgDiff+=(pipsWin);
				total++;
			}
			
			if (lastLow!=-1 && q.getLow5()<=lastLow  && hAllowed==1){
				QuoteShort qClose = data.get(i+nClose);
				int pipsWin  = qClose.getClose5()-q1.getOpen5();				
				avgDiff+=(pipsWin);
				total++;
			}
			
			
			//actualizamos daily highs/lows
			if (q.getHigh5()>actualHigh){
				actualHigh = q.getHigh5();
			}
			if (q.getLow5()<actualLow){
				actualLow = q.getLow5();
			}		
		}
		double avg = avgDiff*1.0/total;
		//System.out.println(hours+" "+l+" "+total+" || "+PrintUtils.Print2(avg)+" "+PrintUtils.Print2(total*avg/10));
		System.out.println(hours+" "+nClose+" || "
				+total+" "+PrintUtils.Print2(avg)
				+" "+PrintUtils.Print2(total*avg/10)
				+" "+PrintUtils.Print2(total*((avg/10)-comm))
				);
	}
	
	public static void testDailyHLmae(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			String hours,int nbars,int l,
			int orderCheck,boolean checkOne){
				
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		int lastDay = -1;
		int actualHigh = -1;
		int actualLow = 999999;
		int lastHigh = -1;
		int lastLow = -1;
		int avgDiff = 0;
		int total = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				lastDay = day;
				//actualizamos highs/lows
				lastHigh = actualHigh;
				lastLow  = actualLow;
				actualHigh = -1;
				actualLow = 99999;
			}
			//logica de las entradas
			
			//if (q.getHigh5()>=lastHigh && maxMins.get(i).getExtra()>=nbars
			int hAllowed = allowedHours.get(h);
			if (lastHigh!=-1 && q.getHigh5()>=lastHigh  && hAllowed==1){
				QuoteShort qMax = TradingUtils.getMaxMinShort(data, i+1, i+l);
				int pipsWin  = q1.getOpen5()-qMax.getLow5();
				int pipsLost = qMax.getHigh5()-q1.getOpen5();
				avgDiff+=(pipsWin-pipsLost);
				total++;
			}
			
			if (lastLow!=-1 && q.getLow5()<=lastLow  && hAllowed==1){
				QuoteShort qMax = TradingUtils.getMaxMinShort(data, i+1, i+l);
				int pipsWin  = qMax.getHigh5()-q1.getOpen5();
				int pipsLost = q1.getOpen5()-qMax.getLow5();
				avgDiff+=(pipsWin-pipsLost);
				total++;
			}
			
			
			//actualizamos daily highs/lows
			if (q.getHigh5()>actualHigh){
				actualHigh = q.getHigh5();
			}
			if (q.getLow5()<actualLow){
				actualLow = q.getLow5();
			}		
		}
		double avg = avgDiff*1.0/total;
		System.out.println(hours+" "+l+" "+total+" || "+PrintUtils.Print2(avg)+" "+PrintUtils.Print2(total*avg/10));
		//System.out.println(l+" "+PrintUtils.Print2(avg));
	}
	
	public static double testDailyHL_l_m_c(ArrayList<QuoteShort> data,
			int begin,int end,
			ArrayList<QuoteShort> maxMins,
			String hours,double tpATR,
			double slATR,int maxAllowed,int nATR,int l,int nbars,
			double balance,double risk,double comm,boolean debug){
		
		double actualBalance = balance;
		double maxBalance = balance;
		double extraNeeded = 0;
		
		if (begin<=0) begin =0;
		if (end>data.size()-2) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		PositionsStats stats = new PositionsStats();
		Calendar calAux = Calendar.getInstance();
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();		
		int wins	= 0;
		int losses	= 0;
		int lastDay = -1;
		double atr = 100;
		int totalDays = 0;
		int actualHigh = -1;
		int actualLow = 99999999;
		int lastHigh = -1;
		int lastLow = -1;
		int actualOrders = 0;
		int totalOpens = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (actualHigh-actualLow)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range  "+actualHigh+" "+actualLow+" "+atr+" "+range);
					}
				}
				lastDay = day;
				totalDays++;
				//actualizamos highs/lows
				lastHigh = actualHigh;
				lastLow  = actualLow;
				actualHigh = -1;
				actualLow = 9999999;
				actualOrders=0;
			}
			//logica de las entradas
			int tp = (int) (atr*tpATR);
			int sl = (int) (atr*slATR);
			//calculo minilots
			double minBalance = TestEAs.getMinBalanceRequiered(actualBalance,risk,maxAllowed,sl);
			if (actualBalance<minBalance){
				extraNeeded += minBalance-actualBalance;
				actualBalance = minBalance;
			}
			
			long microLots = TestEAs.calculateMicroLots(actualBalance,risk,maxAllowed,sl);
			//System.out.println("microLots: "+microLots);			
			int hAllowed = allowedHours.get(h);
			int entry = q1.getOpen5();
			int maxMin = maxMins.get(i).getExtra();
			
			if (lastHigh!=-1 
					//&& q.getHigh5()>=lastHigh
					//&& entry>stats.getMaxShortEntry()
					&& totalOpens<maxAllowed 
					&& maxMin>=nbars
					&& hAllowed==1
					){
					
					int tpValue = entry-tp*10;
					int slValue = entry+sl*10;
					if (tpATR<0.0)
						tpValue = 0;
					if (slATR<0.0)
						slValue = 0;
					PositionShort.addPosition(positions,PositionStatus.PENDING,PositionType.SHORT,microLots,entry,tpValue,slValue,i+l,i);
				//actualOrders++;
			}
			
			
			if (lastLow!=-1 
					//&& q.getLow5()<=lastLow
					//&& entry<stats.getMinLongEntry()
					&& totalOpens<maxAllowed 
					&& maxMin<=-nbars
					&& hAllowed==1
					){
				
					int tpValue = entry+tp*10;
					int slValue = entry-sl*10;
					if (tpATR<0.0)
						tpValue = 0;
					if (slATR<0.0)
						slValue = 0;
					PositionShort.addPosition(positions,PositionStatus.PENDING,PositionType.LONG,microLots,entry,tpValue,slValue,i+l,i);
				//actualOrders++;
			}
			//actualizamos daily highs/lows
			if (q.getHigh5()>actualHigh){
				actualHigh = q.getHigh5();
			}
			if (q.getLow5()<actualLow){
				actualLow = q.getLow5();
			}
			//actualizamos posiciones
			PositionShort.updatePositions(positions,stats,calAux, q1,i+1,actualBalance,comm);	
			totalOpens = stats.getTotalOpens();
			actualBalance = stats.getActualBalance();
			if (actualBalance>maxBalance) maxBalance = actualBalance;
		}
		int totalPips = 0;
		int winPips = 0;
		int lostPips = 0;
		//calculamos balance
		for (int j=0;j<positions.size();j++){
			PositionShort p = positions.get(j);
			if (p.getPositionStatus()==PositionStatus.CLOSE){
				if (p.getWin()==1) wins++;
				if (p.getWin()==-1) losses++;
				if (p.getWinPips()>=0) winPips+=p.getWinPips();
				else lostPips+=(-p.getWinPips());
				totalPips +=p.getWinPips();
				//System.out.println(p.getWinPips()+" "+totalPips);
			}
		}
		int total   = wins+losses;
		double pWin = wins*100.0/total;
		double pLosses = 100.0-pWin;
		double pfaprx = (winPips*1.0)/(lostPips);
		double pfRandom = (pWin*tpATR)/(pLosses*slATR);
		double pfaprx2 = Math.abs((stats.getTotalProfit$())/(stats.getTotalLoss$()));
		//int totalPips = wins*tp-losses*sl;
		//double exp = totalPips*1.0/(wins+losses);
		double avgPips = totalPips*0.1/total;
		/*System.out.println(
				" "+begin
				+" "+end
				+" "+l
				+" "+maxAllowed
				+" "+nbars
				+" "+nATR
				+" "+PrintUtils.Print2(tpATR)
				+" "+PrintUtils.Print2(slATR)
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec2(actualBalance,true)
				+" "+PrintUtils.Print2dec2(balance+extraNeeded,true)
				+" "+PrintUtils.Print2dec2(maxBalance,true)
				+" "+PrintUtils.Print2(totalPips/10)
				+" "+PrintUtils.Print2(avgPips)
				+" "+PrintUtils.Print2(pWin)
				+" "+PrintUtils.Print2(pfaprx2)
				);
		*/
		if (debug)
		System.out.println(
				" "+begin
				+" "+end
				+" "+nbars
				+" "+l
				+" "+maxAllowed
				+" "+nATR
				+" "+PrintUtils.Print2(tpATR)
				+" "+PrintUtils.Print2(slATR)
				+" "+total
				+" "+PrintUtils.Print2(pfRandom,true)
				+" "+nbars
				+" "+PrintUtils.Print2(pWin,true)
				);
		return pfRandom;
	}
	
	public static void testDailyHL_l_m(ArrayList<QuoteShort> data,
			int begin,int end,
			ArrayList<QuoteShort> maxMins,
			String hours,double tpATR,double slATR,int maxAllowed,int nATR,int l){
		
		if (begin<=0) begin =0;
		if (end>data.size()-2) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		PositionsStats stats = new PositionsStats();
		Calendar calAux = Calendar.getInstance();
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();		
		int wins	= 0;
		int losses	= 0;
		int lastDay = -1;
		double atr = 100;
		int totalDays = 0;
		int actualHigh = -1;
		int actualLow = 99999999;
		int lastHigh = -1;
		int lastLow = -1;
		int actualOrders = 0;
		int totalOpens = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (actualHigh-actualLow)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range  "+actualHigh+" "+actualLow+" "+atr+" "+range);
					}
				}
				lastDay = day;
				totalDays++;
				//actualizamos highs/lows
				lastHigh = actualHigh;
				lastLow  = actualLow;
				actualHigh = -1;
				actualLow = 9999999;
				actualOrders=0;
			}
			//logica de las entradas
			int tp = (int) (atr*tpATR);
			int sl = (int) (atr*slATR);
			int hAllowed = allowedHours.get(h);
			int entry = q1.getOpen5();
			if (lastHigh!=-1 
					&& q.getHigh5()>=lastHigh
					&& entry>stats.getMaxShortEntry()
					&& totalOpens<maxAllowed 
					&& hAllowed==1){
					
					int tpValue = entry-tp*10;
					int slValue = entry+sl*10;
					if (tpATR<0.0)
						tpValue = 0;
					if (slATR<0.0)
						slValue = 0;
					PositionShort.addPosition(positions,PositionStatus.PENDING,PositionType.SHORT,0,entry,tpValue,slValue,i+l,i);
				//actualOrders++;
			}
			
			if (lastLow!=-1 
					&& q.getLow5()<=lastLow
					&& entry<stats.getMinLongEntry()
					&& totalOpens<maxAllowed 
					&& hAllowed==1){
				
					int tpValue = entry+tp*10;
					int slValue = entry-sl*10;
					if (tpATR<0.0)
						tpValue = 0;
					if (slATR<0.0)
						slValue = 0;
					PositionShort.addPosition(positions,PositionStatus.PENDING,PositionType.LONG,0,entry,tpValue,slValue,i+l,i);
				//actualOrders++;
			}
			//actualizamos daily highs/lows
			if (q.getHigh5()>actualHigh){
				actualHigh = q.getHigh5();
			}
			if (q.getLow5()<actualLow){
				actualLow = q.getLow5();
			}
			//actualizamos posiciones
			PositionShort.updatePositions(positions,stats,calAux, q1,i+1,0,0);	
			totalOpens = stats.getTotalOpens();
		}
		int totalPips = 0;
		int winPips = 0;
		int lostPips = 0;
		//calculamos balance
		for (int j=0;j<positions.size();j++){
			PositionShort p = positions.get(j);
			if (p.getPositionStatus()==PositionStatus.CLOSE){
				if (p.getWin()==1) wins++;
				if (p.getWin()==-1) losses++;
				if (p.getWinPips()>=0) winPips+=p.getWinPips();
				else lostPips+=(-p.getWinPips());
				totalPips +=p.getWinPips();
				//System.out.println(p.getWinPips()+" "+totalPips);
			}
		}
		int total   = wins+losses;
		double pWin = wins*100.0/total;
		double pLosses = 100.0-pWin;
		double pfaprx = (winPips*1.0)/(lostPips);
		//int totalPips = wins*tp-losses*sl;
		//double exp = totalPips*1.0/(wins+losses);
		double avgPips = totalPips*0.1/total;
		System.out.println(
				" "+begin
				+" "+end
				+" "+l
				+" "+maxAllowed
				+" "+nATR
				+" "+PrintUtils.Print2(tpATR)
				+" "+PrintUtils.Print2(slATR)
				+" || "
				+" "+total
				+" "+PrintUtils.Print2(totalPips/10)
				+" "+PrintUtils.Print2(avgPips)
				+" "+PrintUtils.Print2(pWin)
				+" "+PrintUtils.Print2(pfaprx)
				);
	}

	public static void testDailyHL_l(ArrayList<QuoteShort> data,
			int begin,int end,
			ArrayList<QuoteShort> maxMins,
			String hours,double tpATR,double slATR,int maxAllowed,int nATR,int l){
		
		if (begin<=0) begin =0;
		if (end>data.size()-2) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		PositionsStats stats = new PositionsStats();
		Calendar calAux = Calendar.getInstance();
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();		
		int wins	= 0;
		int losses	= 0;
		int lastDay = -1;
		double atr = 100;
		int totalDays = 0;
		int actualHigh = -1;
		int actualLow = 99999999;
		int lastHigh = -1;
		int lastLow = -1;
		int actualOrders = 0;
		int totalOpens = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (actualHigh-actualLow)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range  "+actualHigh+" "+actualLow+" "+atr+" "+range);
					}
				}
				lastDay = day;
				totalDays++;
				//actualizamos highs/lows
				lastHigh = actualHigh;
				lastLow  = actualLow;
				actualHigh = -1;
				actualLow = 9999999;
				actualOrders=0;
			}
			//logica de las entradas
			int tp = (int) (atr*tpATR);
			int sl = (int) (atr*slATR);
			int hAllowed = allowedHours.get(h);
			if (lastHigh!=-1 && q.getHigh5()>=lastHigh
					&& totalOpens<maxAllowed && hAllowed==1){
					PositionShort.addPosition(positions,PositionStatus.PENDING,PositionType.SHORT,0,q1.getOpen5(),0,0,i+l,i);
				//actualOrders++;
			}
			
			if (lastLow!=-1 && q.getLow5()<=lastLow
					&& totalOpens<maxAllowed && hAllowed==1){
					PositionShort.addPosition(positions,PositionStatus.PENDING,PositionType.LONG,0,q1.getOpen5(),0,0,i+l,i);
				//actualOrders++;
			}
			//actualizamos daily highs/lows
			if (q.getHigh5()>actualHigh){
				actualHigh = q.getHigh5();
			}
			if (q.getLow5()<actualLow){
				actualLow = q.getLow5();
			}
			//actualizamos posiciones
			PositionShort.updatePositions(positions,stats,calAux, q1,i+1,0,0);	
			totalOpens = stats.getTotalOpens();
		}
		int totalPips = 0;
		int winPips = 0;
		int lostPips = 0;
		//calculamos balance
		for (int j=0;j<positions.size();j++){
			PositionShort p = positions.get(j);
			if (p.getPositionStatus()==PositionStatus.CLOSE){
				if (p.getWin()==1) wins++;
				if (p.getWin()==-1) losses++;
				if (p.getWinPips()>=0) winPips+=p.getWinPips();
				else lostPips+=(-p.getWinPips());
				totalPips +=p.getWinPips();
				//System.out.println(p.getWinPips()+" "+totalPips);
			}
		}
		int total   = wins+losses;
		double pWin = wins*100.0/total;
		double pLosses = 100.0-pWin;
		double pfaprx = (winPips*1.0)/(lostPips);
		//int totalPips = wins*tp-losses*sl;
		//double exp = totalPips*1.0/(wins+losses);
		System.out.println(
				" "+begin
				+" "+end
				+" "+l
				+" "+maxAllowed
				+" "+nATR
				+" "+PrintUtils.Print2(tpATR)
				+" "+PrintUtils.Print2(slATR)
				+" || "
				+" "+total
				+" "+PrintUtils.Print2(totalPips/10)
				+" "+PrintUtils.Print2(pWin)
				+" "+PrintUtils.Print2(pfaprx)
				);
	}
	
	public static void testDailyHL(ArrayList<QuoteShort> data,
			int begin,int end,
			ArrayList<QuoteShort> maxMins,
			String hours,double tpATR,double slATR,int maxAllowed,int nATR){
		
		if (begin<=0) begin =0;
		if (end>data.size()-2) end = data.size()-2;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		PositionsStats stats = new PositionsStats();
		Calendar calAux = Calendar.getInstance();
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();		
		int wins	= 0;
		int losses	= 0;
		int lastDay = -1;
		double atr = 100;
		int totalDays = 0;
		int actualHigh = -1;
		int actualLow = 99999999;
		int lastHigh = -1;
		int lastLow = -1;
		int actualOrders = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (actualHigh-actualLow)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range  "+actualHigh+" "+actualLow+" "+atr+" "+range);
					}
				}
				lastDay = day;
				totalDays++;
				//actualizamos highs/lows
				lastHigh = actualHigh;
				lastLow  = actualLow;
				actualHigh = -1;
				actualLow = 9999999;
				actualOrders=0;
			}
			//logica de las entradas
			int tp = (int) (atr*tpATR);
			int sl = (int) (atr*slATR);
			int hAllowed = allowedHours.get(h);
			if (lastHigh!=-1 && q.getHigh5()>=lastHigh
					&& actualOrders<maxAllowed && hAllowed==1){
					PositionShort.addPosition(positions,PositionStatus.PENDING,PositionType.SHORT,0,q1.getOpen5(),q1.getOpen5()-tp*10,q1.getOpen5()+sl*10,0,i);
				actualOrders++;
			}
			
			if (lastLow!=-1 && q.getLow5()<=lastLow
					&& actualOrders<maxAllowed && hAllowed==1){
					PositionShort.addPosition(positions,PositionStatus.PENDING,PositionType.LONG,0,q1.getOpen5(),q1.getOpen5()+tp*10,q1.getOpen5()-sl*10,0,i);
				actualOrders++;
			}
			//actualizamos daily highs/lows
			if (q.getHigh5()>actualHigh){
				actualHigh = q.getHigh5();
			}
			if (q.getLow5()<actualLow){
				actualLow = q.getLow5();
			}
			//actualizamos posiciones
			PositionShort.updatePositions(positions,stats,calAux, q1,i+1,0,0);	
		}
		//calculamos balance
		for (int j=0;j<positions.size();j++){
			PositionShort p = positions.get(j);
			if (p.getPositionStatus()==PositionStatus.CLOSE){
				if (p.getWin()==1) wins++;
				if (p.getWin()==-1) losses++;
			}
		}
		int total   = wins+losses;
		double pWin = wins*100.0/total;
		double pLosses = 100.0-pWin;
		double pfaprx = (pWin*tpATR)/(pLosses*slATR);
		//int totalPips = wins*tp-losses*sl;
		//double exp = totalPips*1.0/(wins+losses);
		System.out.println(
				" "+begin
				+" "+end
				+" "+maxAllowed
				+" "+nATR
				+" "+PrintUtils.Print2(tpATR)
				+" "+PrintUtils.Print2(slATR)
				+" || "
				+" "+total
				+" "+PrintUtils.Print2(pWin)
				+" "+PrintUtils.Print2(pfaprx)
				);
	}

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.31.csv";
		String path5m1   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
	
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 
	  
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			
		ArrayList<QuoteShort> data = null;
		data = data5mS;
		
		ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
		ArrayList<Double> pfs = new ArrayList<Double>();
		ArrayList<Integer> bars = new ArrayList<Integer>();
		ArrayList<String> rows = new ArrayList<String>();
		int begin = 1;
		int end = data.size()-1;
		//begin = 400000;
		end = 400000;
		double comm = 0.0;
		for (int nATR=5;nATR<=5;nATR+=1){
			for (double tpATR=0.11;tpATR<=0.11;tpATR+=0.01){
				for (double slATR=0.32;slATR<=0.32;slATR+=0.01){
					for (int maxAllowed=5;maxAllowed<=5;maxAllowed+=1){
						for (int l=10000;l<=10000;l+=10){
							bars.clear();
							pfs.clear();
							
							for (int nbars=50;nbars<=30000;nbars+=50){
								String row = nbars+" ";
								for (int h=0;h<=23;h++){																	
									//for (int nbars=10;nbars<=10000;nbars+=500){										
										//TestSenecaPoints.testDailyHL(data,maxMins, "0 1 23", tp, sl,maxAllowed,nbars,0,false);
										//TestSenecaPoints.testDailyHLmae(data,maxMins,String.valueOf(h), nbars,l,0,false);
										//TestSenecaPoints.testDailyHLmaeATR(data,maxMins,String.valueOf(h),l,nATR,comm);										
										//TestSenecaPoints.testDailyHLmaeATR(data,begin,end,maxMins,"0 1 2 3 4 5 6 7 8 9",l,nATR,comm);										
										begin = 700000;
										end = data.size()-1;
										begin = 1;
										end   = 900000;
										//TestSenecaPoints.testDailyHLmaeATR(data,begin,end,maxMins,"0 1 2 3 4 5 6 7 8 9",l,nATR,comm);
										//TestSenecaPoints.testDailyHLmae(data,maxMins,"0 1 2 3 4 5 6 7 8 9", nbars,l,0,false);	
										//TestSenecaPoints.testDailyHLclose(data,maxMins,String.valueOf(h),l,0,comm,false);
										//TestSenecaPoints.testDailyHLclose(data,maxMins,"0 1 2 3 4 5 6 7 8 9",l,0,comm,false);	
										//TestSenecaPoints.testDailyHLclose(data,maxMins,"14 15 16 17 18 19 20 21 22 23",l,0,comm,false);	
										//TestSenecaPoints.testDailyHLclose(data,maxMins,"0 1 2 3 4 5 6 7 8 9",l,0,comm,false);	
										//TestSenecaPoints.testDailyHL_l_m(data,begin,end,maxMins,"0 1 2 3 4 5 6 7 8 9",tpATR,slATR,maxAllowed,nATR,l);
										double pf =TestSenecaPoints.testDailyHL_l_m_c(data,begin,end,maxMins,String.valueOf(h),
												tpATR,slATR,maxAllowed,nATR,l,nbars,100000,1.0,comm,false);
										//row+=PrintUtils.Print2dec(pf, true)+" ";
											//TestSenecaPoints.testDailyHL(data,begin,end,maxMins,"0 1 2 3 4 5 6 7 8 9",tpATR,slATR,maxAllowed,nATR);
											//TestSenecaPoints.testDailyHL_l(data,begin,end,maxMins,"0 1 2 3 4 5 6 7 8 9",tpATR,slATR,maxAllowed,nATR,l);
											//TestSenecaPoints.testDailyHL_l_m(data,begin,end,maxMins,"0 1 2 3 4 5 6 7 8 9",tpATR,slATR,maxAllowed,nATR,l);
									//}//nbars									
								}//h
								//System.out.println(row);
								//rows.add(row);								
							}//nbars
							TestSenecaPoints.savepf(rows);
						}
					}
				}
			}
		}
		//test true
		/*for (int maxAllowed=20;maxAllowed<=20;maxAllowed++){
			boolean checkOne = true;
			for (int orderCheck=1;orderCheck<=20;orderCheck++){
				for (int h=10;h<=10;h++){
					TestSenecaPoints.testDailyHL(data, h, tp, sl,maxAllowed,orderCheck,checkOne);
				}
			}
		}*/
	}

	private static void savepf(ArrayList<String> rows) {
		// TODO Auto-generated method stub
		String fileName = "c:\\pfs_h.csv";
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);			 
			  for (int i=0;i<rows.size();i++){				 
				  String row = rows.get(i);
				  out.write(row);
				  out.newLine();
			  }
			  
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}

	private static void savepf(int h, ArrayList<Integer> bars,
			ArrayList<Double> pfs) {
		// TODO Auto-generated method stub
		String fileName = "c:\\pfs_"+h+"h.csv";
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);			 
			  for (int i=0;i<bars.size();i++){				 
				  String row = bars.get(i)+" "+pfs.get(i);
				  out.write(row);
				  out.newLine();
			  }
			  
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}

}
