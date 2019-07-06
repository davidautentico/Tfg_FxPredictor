package drosa.experimental.ticksStudy;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.GlobalStats;
import drosa.experimental.PositionShort;
import drosa.experimental.edge.Edge;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class ZigZagTicks {
	
	public static ArrayList<QuoteShort> calculate5minBarsFromTicks(ArrayList<Tick> ticks){
		
		ArrayList<QuoteShort> data = new ArrayList<QuoteShort>();
		
		int last5minBar = -1;
		int actualHigh = -1;
		int actualLow = -1;
		Calendar cal = Calendar.getInstance();
		QuoteShort q = null;
		int candleIndex = 0;
		for (int i=0;i<ticks.size();i++){
			Tick t = ticks.get(i);
			Tick.getCalendar(cal, t);
			int min = cal.get(Calendar.MINUTE);
			int actual5minBar = min/5;
			
			if (actual5minBar!=last5minBar){
				if (last5minBar!=-1){
					q.setHigh5(actualHigh);
					q.setLow5(actualLow);
					q.setClose5(t.getBid());
					data.add(q);
					candleIndex++;
				}
				q = new QuoteShort();
				q.setOpen5(t.getBid());
				actualHigh = -1;
				actualLow = -1;
				last5minBar = actual5minBar;
			}
			
			if (actualHigh==-1 || t.getAsk()>actualHigh){
				actualHigh = t.getAsk();
			}
			if (actualLow==-1 || t.getBid()<actualLow){
				actualLow = t.getBid();
			}
			t.setExtra(candleIndex);//indice de 5 min al que pertenece el tick
		}
		
		return data;
	}
	
	
public static ArrayList<Integer> calculateLegs(ArrayList<QuoteShort> data,int period,boolean debug){
		
		ArrayList<Integer> maxMins = new ArrayList<Integer>();
		int totalZeros = 0;
		int total_1 = 0;
		int total1=0;
		Calendar cal = Calendar.getInstance();
		int lastCandle = -1;
		int max = -1;
		int min = 999999;
		QuoteShort qMaxMin = null;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			qMaxMin = TradingUtils.getMaxMinShort(data, i-period, i-1); //maximo y minimo last period bars

			int value = 0;
			if (q.getLow5()<qMaxMin.getLow5()){
				value = -1;
				total_1++;
			}else if (q.getHigh5()>qMaxMin.getHigh5()){
				value = 1;
				total1++;
			}else totalZeros++;
			
			
			maxMins.add(value);
			//System.out.println(value);
		}
		
		double per0 = totalZeros*100.0/data.size();
		double per_1 = total_1*100.0/data.size();
		double per1 = total1*100.0/data.size();
		
		if (debug)
		System.out.println(PrintUtils.Print2dec(per0, false)
				+" "+PrintUtils.Print2dec(per_1, false)
				+" "+PrintUtils.Print2dec(per1, false)
				);
		return maxMins;
	}
	
	/**
	 * 1 = max (nBars)
	 * -1 = min (nBars)
	 * 0 = none
	 * @param ticks
	 * @param nBars
	 * @return
	 */
	public static ArrayList<Integer> calculateLegs(ArrayList<Tick> ticks,ArrayList<QuoteShort> data,int period,boolean debug){
		
		ArrayList<Integer> maxMins = new ArrayList<Integer>();
		int totalZeros = 0;
		int total_1 = 0;
		int total1=0;
		Calendar cal = Calendar.getInstance();
		int lastCandle = -1;
		int max = -1;
		int min = 999999;
		QuoteShort qMaxMin = null;
		for (int i=0;i<ticks.size();i++){
			Tick t = ticks.get(i);
			Tick.getCalendar(cal, t);
			int candleIndex = t.getExtra();
			
			if (candleIndex!=lastCandle){
				min=999999;
				max = -1;
				lastCandle = candleIndex;
				qMaxMin = TradingUtils.getMaxMinShort(data, candleIndex-period, candleIndex-1); //maximo y minimo last period bars
			}
			
			if (min>qMaxMin.getLow5())
				min = qMaxMin.getLow5();
			if (max<qMaxMin.getHigh5())
				max = qMaxMin.getHigh5();
			
			int value = 0;
			if (t.getBid()<min){
				value = -1;
				total_1++;
				min = t.getBid();
				//System.out.println(qMaxMin.toString()+" || "+t.toString()+" || "+t.getBid()+" "+value);
			}else if (t.getAsk()>max){				
				value = 1;
				total1++;
				max = t.getAsk();
				//System.out.println(qMaxMin.toString()+" || "+t.toString()+" || "+t.getAsk()+" "+value);
			}else totalZeros++;
			
			
			maxMins.add(value);
			//System.out.println(value);
		}
		
		double per0 = totalZeros*100.0/ticks.size();
		double per_1 = total_1*100.0/ticks.size();
		double per1 = total1*100.0/ticks.size();
		
		if (debug)
		System.out.println(PrintUtils.Print2dec(per0, false)
				+" "+PrintUtils.Print2dec(per_1, false)
				+" "+PrintUtils.Print2dec(per1, false)
				);
		return maxMins;
	}
	
	
	private static void studyLegs(ArrayList<Tick> ticksClean,
			ArrayList<QuoteShort> data5m, ArrayList<Integer> legs,int h1,int h2) {
		// TODO Auto-generated method stub
		
		Calendar cal = Calendar.getInstance();
		int lastLeg = 0;
		int actualExtension = 0;
		int maxExtension = 0;
		int avgExtension = 0;
		int totalAvgExtensions = 0;
		int actualLegH = -1;
		int lastDay = -1;
		int totalDays = 0;
		int currentBeginIndex = 0;
		int currentEndIndex = 0;
		int totalLength=0;
		for (int i=0;i<legs.size();i++){
			Tick t = ticksClean.get(i);
			Tick.getCalendar(cal, t);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int current = legs.get(i);
			//System.out.println(t.toString()+" || +current last "+current+" "+lastLeg);
			
			if (day!=lastDay){
				totalDays++;
				lastDay = day;
			}
			
			if (current!=0){
				if (current!=lastLeg){				
					if (actualLegH>=h1 && actualLegH<=h2){
						if (actualExtension>maxExtension) maxExtension = actualExtension;
						avgExtension +=actualExtension;
						totalAvgExtensions++;
						double diffPriceDown = ticksClean.get(currentBeginIndex).getAsk()-ticksClean.get(currentEndIndex).getBid(); 
						double diffPriceUp = ticksClean.get(currentEndIndex).getAsk()-ticksClean.get(currentBeginIndex).getBid();
						if (lastLeg==-1){
							totalLength+=diffPriceDown;
						}
						if (lastLeg==1){
							totalLength+=diffPriceUp;
						}
						/*System.out.println("last totalAvgExtensions actual max: "
								+t.toString()
								+" "+lastLeg
								+" "+totalAvgExtensions
								+" "+actualExtension
								+" "+maxExtension);*/
					}					
					//actualLeg
					actualLegH = h;
					actualExtension = 1;
					currentBeginIndex = i;
					currentEndIndex = i;					
					lastLeg = current;					
				}else{
					actualExtension++;
					currentEndIndex = i;
					//System.out.println("inc actualExtension: "+actualExtension);
				}
			}
		}
		
		double avg = avgExtension*1.0/totalAvgExtensions;
		double avgDay = totalAvgExtensions*1.0/totalDays;
		double avgL = totalLength*1.0/totalAvgExtensions;
		System.out.println(
				totalAvgExtensions
				+" "
				+" "+PrintUtils.Print2(avg)
				+" "+PrintUtils.Print2(avgDay)
				+" "+PrintUtils.Print2(avgL)
				);
		
	}
	
	private static void hedgeStudy(GlobalStats stats,ArrayList<Tick> ticksClean,
			ArrayList<Integer> legs, double sl, double tp, int h1, int h2,int numExtension,boolean debug) {
		// TODO Auto-generated method stub
		//pips
		int totalPips = 0;
		int positivePips = 0;
		int negativePips = 0;
		//wins
		int wins = 0;
		int losses = 0;
		
		int extensionsCount = 0;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastLeg = 0;
		Calendar cal = Calendar.getInstance();
		PositionShort pos = null;
		for (int i=0;i<ticksClean.size();i++){
			Tick t = ticksClean.get(i);
			Tick.getCalendar(cal, t);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int currentLeg = legs.get(i);
			
			if (currentLeg!=0){
				if (currentLeg!=lastLeg){
					extensionsCount = 1;
					lastLeg = currentLeg;
				}else{
					extensionsCount++;
				}
				if (extensionsCount==numExtension){	
					int entry = -1;
					int slValue = -1;
					int tpValue = -1;
					PositionType posType = PositionType.NONE;
					if (currentLeg==1){//abrir un SHORT
						entry = t.getBid();
						slValue = (int) (entry+sl*10);
						tpValue = (int) (entry-tp*10);
						posType = PositionType.SHORT;
					}
					if (currentLeg==-1){//abrir un LONG
						entry = t.getAsk();
						slValue = (int) (entry-sl*10);
						tpValue = (int) (entry+tp*10);
						posType = PositionType.LONG;
					}
					if (entry!=-1 && h>=h1 && h<=h2){
						pos = new PositionShort();
						pos.setEntry(entry);
						pos.setSl(slValue);
						pos.setTp(tpValue);
						pos.setPositionType(posType);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setOpenIndex(i);
						positions.add(pos);
					}					
				}
			}//currentLeg
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int earnedPips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					//System.out.println(q.toString());
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (t.getAsk()>=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getSl()-p.getEntry();
						}else if (t.getAsk()<=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getEntry()-p.getTp();
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (t.getBid()<=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getEntry()-p.getSl();
						}else if (t.getBid()>=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getTp()-p.getEntry();
						}
					}	
					
					if (closed){													
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
						totalPips += earnedPips;
						if (earnedPips>0) positivePips += earnedPips;
						else negativePips += Math.abs(earnedPips);
						
						if (earnedPips>=0){
							wins++;
						}else{
							losses++;
						}
						
						positions.remove(j);//borramos y no avanzamos
					}else{
						j++;
					}
				}				
			}//for positions							
		}//for	
		
		stats.addWins(wins);
		stats.addLosses(losses);
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double exp = (wins*tp-losses*sl)*1.0/total;
		double edge = Edge.calculateEdge(tp, sl, winPer);
		double pf = positivePips*1.0/negativePips;
		
		if (debug)
		System.out.println(
				h1+" "+h2+" "+tp+" "+sl+" "+numExtension
				+" || "
				+total
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(exp, false)
				+" "+PrintUtils.Print2(edge, false)
				+" "+PrintUtils.Print2(pf, false)
				);
	}
	
	private static void hedgeStudy2(GlobalStats stats,ArrayList<Tick> ticksClean,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> legs, double sl, double tp, int h1, int h2,int numBars,
			int numExtension,boolean debug) {
		// TODO Auto-generated method stub
		//pips
		int totalPips = 0;
		int positivePips = 0;
		int negativePips = 0;
		//wins
		int wins = 0;
		int losses = 0;
		
		
		int extensionsCount = 0;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastLeg = 0;
		Calendar cal = Calendar.getInstance();
		PositionShort pos = null;
		for (int i=0;i<ticksClean.size();i++){
			Tick t = ticksClean.get(i);
			Tick.getCalendar(cal, t);
			int indexCandle5m = t.getExtra();
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int currentLeg = legs.get(i);
			
			if (currentLeg!=0){
				if (currentLeg!=lastLeg){
					extensionsCount = 1;
					lastLeg = currentLeg;
				}else{
					extensionsCount++;
				}
				if (extensionsCount==numExtension){	
					QuoteShort qMaxMin = TradingUtils.getMaxMinShort(data, indexCandle5m-numBars, indexCandle5m-1);
					
					int entry = -1;
					int slValue = -1;
					int tpValue = -1;
					PositionType posType = PositionType.NONE;
					if (currentLeg==1 && t.getBid()>=qMaxMin.getHigh5()){//abrir un SHORT
						entry = t.getBid();
						slValue = (int) (entry+sl*10);
						tpValue = (int) (entry-tp*10);
						posType = PositionType.SHORT;
					}
					if (currentLeg==-1 && t.getAsk()<=qMaxMin.getLow5()){//abrir un LONG
						entry = t.getAsk();
						slValue = (int) (entry-sl*10);
						tpValue = (int) (entry+tp*10);
						posType = PositionType.LONG;
					}
					if (entry!=-1 && h>=h1 && h<=h2){
						pos = new PositionShort();
						pos.setEntry(entry);
						pos.setSl(slValue);
						pos.setTp(tpValue);
						pos.setPositionType(posType);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setOpenIndex(i);
						positions.add(pos);
					}					
				}
			}//currentLeg
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				int earnedPips = 0;
				if (p.getPositionStatus()==PositionStatus.OPEN){
					//System.out.println(q.toString());
					boolean closed = false;
					int win = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (t.getAsk()>=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getSl()-p.getEntry();
						}else if (t.getAsk()<=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getEntry()-p.getTp();
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (t.getBid()<=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getEntry()-p.getSl();
						}else if (t.getBid()>=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getTp()-p.getEntry();
						}
					}	
					
					if (closed){													
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
						totalPips += earnedPips;
						if (earnedPips>0) positivePips += earnedPips;
						else negativePips += Math.abs(earnedPips);
						
						if (earnedPips>=0){
							wins++;
						}else{
							losses++;
						}
						
						positions.remove(j);//borramos y no avanzamos
					}else{
						j++;
					}
				}				
			}//for positions							
		}//for	
		
		stats.addWins(wins);
		stats.addLosses(losses);
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double exp = (wins*tp-losses*sl)*1.0/total;
		double edge = Edge.calculateEdge(tp, sl, winPer);
		double pf = positivePips*1.0/negativePips;
		
		if (debug)
		System.out.println(
				h1+" "+h2+" "+tp+" "+sl+" "+numExtension
				+" || "
				+total
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(exp, false)
				+" "+PrintUtils.Print2(edge, false)
				+" "+PrintUtils.Print2(pf, false)
				);
	}
	
	private static void hedgeStudy3(GlobalStats stats,ArrayList<Tick> ticksClean,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> legs, double sl, double tp, int h1, int h2,int numBars,
			int numExtension,boolean debug) {
		// TODO Auto-generated method stub
		//pips
		int totalPips = 0;
		int positivePips = 0;
		int negativePips = 0;
		//wins
		int wins = 0;
		int losses = 0;
		
		
		int extensionsCount = 0;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		int lastLeg = 0;
		Calendar cal = Calendar.getInstance();
		PositionShort pos = null;
		for (int i=0;i<ticksClean.size();i++){
			Tick t = ticksClean.get(i);
			Tick.getCalendar(cal, t);
			int indexCandle5m = t.getExtra();
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int currentLeg = legs.get(i);
			
			
			QuoteShort qMaxMin = TradingUtils.getMaxMinShort(data, indexCandle5m-numBars, indexCandle5m-1);
					
			int entry = -1;
			int slValue = -1;
			int tpValue = -1;
			PositionType posType = PositionType.NONE;
			if (t.getAsk()>qMaxMin.getHigh5()){//abrir un SHORT
				//System.out.println("[MAX] "+ t.toString()+" || "+qMaxMin.toString());
				entry = t.getBid();
				slValue = (int) (entry+sl*10);
				tpValue = (int) (entry-tp*10);
				posType = PositionType.SHORT;
			}
			if (t.getBid()<qMaxMin.getLow5()){//abrir un LONG
				//System.out.println("[MIN] "+t.toString()+" || "+qMaxMin.toString());
				entry = t.getAsk();
				slValue = (int) (entry-sl*10);
				tpValue = (int) (entry+tp*10);
				posType = PositionType.LONG;
			}
			if (entry!=-1 && h>=h1 && h<=h2){
				pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(slValue);
				pos.setTp(tpValue);
				pos.setPositionType(posType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.setOpenIndex(i);
				positions.add(pos);
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
						if (t.getAsk()>=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getSl()-p.getEntry();
						}else if (t.getAsk()<=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getEntry()-p.getTp();
						}
					}
					if (p.getPositionType()==PositionType.LONG){
						if (t.getBid()<=p.getSl()){
							win = -1;
							closed = true;
							earnedPips -= p.getEntry()-p.getSl();
						}else if (t.getBid()>=p.getTp()){
							win = 1;
							closed = true;
							earnedPips = p.getTp()-p.getEntry();
						}
					}	
					
					if (closed){													
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWin(win);
						//System.out.println(DateUtils.datePrint(cal)+" "+pos.toString());
						totalPips += earnedPips;
						if (earnedPips>0) positivePips += earnedPips;
						else negativePips += Math.abs(earnedPips);
						
						if (earnedPips>=0){
							wins++;
						}else{
							losses++;
						}
						
						positions.remove(j);//borramos y no avanzamos
					}else{
						j++;
					}
				}				
			}//for positions							
		}//for	
		
		stats.addWins(wins);
		stats.addLosses(losses);
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double exp = (wins*tp-losses*sl)*1.0/total;
		double edge = Edge.calculateEdge(tp, sl, winPer);
		double pf = positivePips*1.0/negativePips;
		
		if (debug)
		System.out.println(
				h1+" "+h2+" "+tp+" "+sl+" "+numExtension
				+" || "
				+total
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(exp, false)
				+" "+PrintUtils.Print2(edge, false)
				+" "+PrintUtils.Print2(pf, false)
				);
	}
	
	
	private static void calculateExtensionsByHour(ArrayList<QuoteShort> data5m,
			ArrayList<Integer> legs,
			int begin,int end,
			int h1,int h2) {
		// TODO Auto-generated method stub
		
		if (end>data5m.size()-1) end = data5m.size()-1;
		
		Calendar cal = Calendar.getInstance();
		int lastLeg = 0;
		int actualExtension = 0;
		int currentBeginIndex = 0;
		int currentEndIndex = 0;
		int currentEndH = 0;
		int totalAvgExtensions = 0;
		int avgExtension = 0;
		int maxExtension = 0;
		int actualLegH = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data5m.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int currentLeg = legs.get(i);
			
			//System.out.println(currentLeg+" "+lastLeg);
			if (currentLeg!=0){
				if (currentLeg!=lastLeg){				
					if (actualLegH>=h1 && actualLegH<=h2){
						if (actualExtension>maxExtension) maxExtension = actualExtension;
						avgExtension +=actualExtension;
						totalAvgExtensions++;
						/*System.out.println("last totalAvgExtensions actual max: "
								+q.toString()
								+" "+lastLeg
								+" "+totalAvgExtensions
								+" "+actualExtension
								+" "+maxExtension);*/
					}					
					//actualLeg
					actualExtension = 1;
					actualLegH = currentEndH;
					currentBeginIndex = currentEndIndex; //es la ultima extension de la otra leg
					currentEndIndex = i;					
					lastLeg = currentLeg;					
				}else{
					actualExtension++;
					currentEndIndex = i;
					currentEndH = h;
					//System.out.println("inc actualExtension: "+actualExtension);
				}
			}			
		}
		
		double avgExt = avgExtension*1.0/totalAvgExtensions;
		System.out.println(h1+" "+h2+" || "+totalAvgExtensions+" "+PrintUtils.Print2(avgExt)+" "+maxExtension);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m        = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.05.csv";
		String ticks2014    = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.01.01_2015.02.15.csv";
		String tickSample0  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2015.01.31_2015.02.23.csv";
		String tickSample1  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.12.31_2015.01.29.csv";
		String tickSample2  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.11.30_2014.12.30.csv";
		String tickSample3  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.10.31_2014.11.29.csv";
		String tickSample4  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.09.30_2014.10.30.csv";
		String tickSample5  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.08.31_2014.09.29.csv";
		String tickSample6  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.07.31_2014.09.01.csv";
		String tickSample7  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.06.30_2014.07.30.csv";
		String tickSample8  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.05.31_2014.06.29.csv";
		String tickSample9  = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.04.30_2014.05.30.csv";
		String tickSample10 = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.03.31_2014.04.29.csv";
		String tickSample11 = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.02.28_2014.04.01.csv";
		String tickSample12 = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2014.01.31_2014.03.01.csv";
		String tickSample13 = "c:\\fxdata\\ticks\\dukas\\EURUSD_UTC_Ticks_Bid_2013.12.31_2014.02.01.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(tickSample0);paths.add(tickSample1);paths.add(tickSample2);paths.add(tickSample3);
		paths.add(tickSample4);paths.add(tickSample5);paths.add(tickSample6);paths.add(tickSample7);
		paths.add(tickSample8);paths.add(tickSample9);paths.add(tickSample10);
		paths.add(tickSample11);paths.add(tickSample12);paths.add(tickSample13);
		
		/*ArrayList<Tick> ticks = Tick.readFromDisk(ticks2014, 2);
		//System.out.println("total ticks: "+ticks.size());
		ArrayList<Tick> ticksAdjusted	= TestLines.calculateCalendarAdjustedT(ticks);
  		ArrayList<Tick> ticksClean		= TradingUtils.cleanWeekendDataT(ticksAdjusted); 
  		System.out.println("total ticks clean: "+ticksClean.size());
  		ArrayList<QuoteShort> data5m = ZigZagTicks.calculate5minBarsFromTicks(ticksClean);*/
		
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);		
		ArrayList<QuoteShort> data = null;
		data = data5mS;
  		System.out.println("data5m: "+data.size());
		
  		//ArrayList<Integer> legs = ZigZagTicks.calculateLegs(ticksClean, data5m, 12,false);
  		
  		int begin = 400000;
  		int end = 900000;
  		
  		for (int period=2;period<=100;period++){
	  		ArrayList<Integer> legs = ZigZagTicks.calculateLegs(data, period,false);
	  		for (int h1=0;h1<=0;h1++){
	  			int h2 = 23;
	  			calculateExtensionsByHour(data,legs,begin,end,h1,h2);
	  		}
  		}
  		
  		
		
  		/*double sl = 20;
  		double tp = 10;
  		int h1 = 0;
  		int h2 = 0;
  		int numBars = 100;
		int total = paths.size();
		//total = 1;
		//for (int i=0;i<paths.size();i++){
		for (numBars=100;numBars<=100;numBars+=100){
			for (int numExtensions = 0;numExtensions<=0;numExtensions+=5){
				GlobalStats stats = new GlobalStats();
				stats.setSl(sl);
				stats.setTp(tp);
				for (int i=0;i<total;i++){
					String tickSample = paths.get(i);
					ArrayList<Tick> ticks = Tick.readFromDisk(tickSample, 2);
					//System.out.println("total ticks: "+ticks.size());
					ArrayList<Tick> ticksAdjusted	= TestLines.calculateCalendarAdjustedT(ticks);
			  		ArrayList<Tick> ticksClean		= TradingUtils.cleanWeekendDataT(ticksAdjusted); 
			  		//System.out.println("total ticks clean: "+ticksClean.size());
			  		ArrayList<QuoteShort> data5m = ZigZagTicks.calculate5minBarsFromTicks(ticksClean);
			  		//System.out.println("data5m: "+data5m.size());
			  		ArrayList<Integer> legs = ZigZagTicks.calculateLegs(ticksClean, data5m, 12,false);
			  		
			  		for (int h1=0;h1<=0;h1++){
			  			int h2 = h1+23;
			  			studyLegs(ticksClean,data5m,legs,h1,h2);
			  		}
			  		//hedgeStudy(stats,ticksClean,legs,sl,tp,h1,h2,numExtensions,false);
			  		//hedgeStudy2(stats,ticksClean,data5m,legs,sl,tp,h1,h2,numBars,numExtensions,false);
			  		hedgeStudy3(stats,ticksClean,data5m,legs,sl,tp,h1,h2,numBars,numExtensions,false);
			  		
				}//total files
				//print stats
				int totalTrades = stats.getWins()+stats.getLosses();
		  		int totalWins = stats.getWins();
		  		int totalLosses = stats.getLosses();
		  		double perWin = totalWins*100.0/totalTrades;
		  		double pf = (totalWins*tp)/(totalLosses*sl);
		  		System.out.println(numExtensions
		  				+" || "
		  				+" "+totalTrades+" "+totalWins+" "+totalLosses
		  				+" "+PrintUtils.Print(perWin)
		  				+" "+PrintUtils.Print(pf)
		  				);
			}//numExtension
		}*/
	}


	

	

	

}
