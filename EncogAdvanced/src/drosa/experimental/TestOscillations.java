package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;
import drosa.utils.MathUtils;

public class TestOscillations {
	
	public static double getMinBalanceRequiered(double balance,double risk,int maxConcurrent,int sl){
		
		double totalRisk = maxConcurrent*risk; //total riesgo
		double risk1lot   = 1*0.1*sl; //riesgo en $ de 1 microlot
		double minBalance = (risk1lot*totalRisk/risk)*100/totalRisk;
		
		return minBalance;
	}
	
	public static long calculateMicroLots(double balance,double risk,int maxConcurrent,int sl){
		double totalRisk   = maxConcurrent*risk; //total riesgo
		long microSLPerPos = (long) ((balance*totalRisk/100.0) / (maxConcurrent*sl*0.1));
		
		return microSLPerPos;
	}
	
	public static void tradingHighLowOscillationsBarsV2(String header,ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int begin,int end,
			int h1,int h2,
			int dayWeek1,int dayWeek2,double factor,
			int nBars,int sl,int tp,int pips,int expiration,double comm){
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		if (begin<0) begin = 0;
		if (end>data.size()-2) end = data.size()-2;
		//if (end-begin<binSize) return;
		//System.out.println("size end: "+data.size()+" "+end);
		int wins   = 0;
		int losses = 0;
		int maxOpens = 0;
		PositionShort pos = null;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//QuoteShort maxMin0 = TradingUtils.getMaxMinShort(data, i-nBars, i-1);
			//QuoteShort maxMin1 = TradingUtils.getMaxMinShort(data, i, i+nBars);
			QuoteShort maxMin0 = maxMins.get(i);
			
			if (h>=h1 && h<=h2){
				if (maxMin0.getExtra()==-1){
					PositionShort.removePositions(positions, PositionStatus.PENDING);
					int entryValue = q.getLow5()+pips*10;
					//if (entryValue>=)
					int slValue    = entryValue+sl*10;
					int tpValue    = entryValue-tp*10;
					pos = new PositionShort();
					pos.setEntry(entryValue);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPositionType(PositionType.SHORT);
					pos.setPendingIndex(i);
					positions.add(pos);
				}else if (maxMin0.getExtra()==1){
					PositionShort.removePositions(positions, PositionStatus.PENDING);
					int entryValue = q.getHigh5()-pips*10;
					int slValue    = entryValue-sl*10;
					int tpValue    = entryValue+tp*10;
					pos = new PositionShort();
					pos.setEntry(entryValue);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPositionType(PositionType.LONG);
					pos.setPendingIndex(i);
					positions.add(pos);
				}
			}
			
			//positions update
			int opens = 0;
			for (int s=0;s<positions.size();s++){
				PositionShort p = positions.get(s);
				if (p.getPositionStatus()==PositionStatus.PENDING){
					long diffExpiration = i-p.getPendingIndex();
					if (q1.getLow5()<=p.getEntry() && p.getEntry()<=q1.getHigh5() && diffExpiration<expiration){
						p.setPositionStatus(PositionStatus.OPEN);
						p.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
						p.setOpenIndex(i+1);
						//System.out.println("[OPEN] "+p.toString2());
					}
				}
				if (p.getPositionStatus()==PositionStatus.OPEN){
					opens++;
					boolean closed = false;
					if (p.getPositionType()==PositionType.LONG){
						if (q1.getLow5()<=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed = true;
						}else if (q1.getHigh5()>=p.getTp() && (i+1)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed = true;
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						if (q1.getHigh5()>=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed = true;
						}else if (q1.getLow5()<=p.getTp() && (i+1)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed = true;
						}
					}
					if (closed){
						p.getCloseCal().setTimeInMillis(cal1.getTimeInMillis());
						//System.out.println("[CLOSE] "+p.toString2()+" || "+q1.toString());
					}
				}
			}//positions
			if (opens>maxOpens) maxOpens = opens;
			
		}
		int cases = losses+wins;
		double per = wins*100.0/cases;
		double lossPer = 100.0-per;
		double exp = (per*tp-(100.0-per)*sl);
		double pf = (wins*tp*1.0)/(losses*sl);
		double totalPips = cases*((exp/100.0)-comm);
		System.out.println(header+" "+h1+" "+begin+" "+end+" "+nBars+" "+tp+" "+sl+" "+pips+" "+expiration
				+" || "
				+PrintUtils.Print2Int(losses+wins,4)
				+" "+losses+" "+wins
				+" "+PrintUtils.Print2(per)
				+" "+maxOpens
				+" "+PrintUtils.Print2(exp/100.0)
				+" "+PrintUtils.Print2(pf)
				+" "+PrintUtils.Print2(totalPips)
				);
	}
	
	public static double tradingHighLowOscillationsBarsATRv3(String header,
			ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMinsExt,
			int begin,int end,
			String hours,
			int dayWeek1,int dayWeek2,double factor,
			int nBars,int nATR,double slATR,double tpATR,double pipsATR,
			double factorTPPips,
			int expiration,
			int maxAllowed,
			double balance,
			ArrayList<MaxMinRisk> maxMinRisks,
			double comm){
		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		int count5000=0;
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		double actualBalance = balance;
		double extraNeeded   = 0;
		double profit$        = 0;
		double losses$        = 0;
		double maxBalance = balance;
		double maxDD      = 0.0;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		if (begin<0) begin = 0;
		if (end>data.size()-2) end = data.size()-2;
		//if (end-begin<binSize) return;
		//System.out.println("size end: "+data.size()+" "+end);
		int wins   = 0;
		int losses = 0;
		int maxOpens = 0;
		PositionShort pos = null;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int max = -999999;
		int min = 999999;
		double atr = 100;
		int totalDays = 0;
		long totalPips = 0;
		int pipsAvg = 0;
		int slAvg = 0;
		int tpAvg = 0;
		int totalQuotes=0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr+" "+range);
					}
				}
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
			}
			
			int pips = (int) (atr*pipsATR);
			int sl   = (int) (atr*slATR);
			int tp   = (int) (atr*tpATR);
			pipsAvg+=pips;
			slAvg+=sl;
			tpAvg+=tp;
			totalQuotes++;
			QuoteShort maxMin0 = maxMinsExt.get(i);
			double risk = MaxMinRisk.calculateRisk(maxMinRisks,maxMin0);
			//if (maxMin0.getExtra()!=0)
				//System.out.println(maxMin0.getExtra()+" "+PrintUtils.Print2(risk));
			int allowed = allowedHours.get(h);
			if (allowed==1){				
				if (maxMin0.getExtra()<0 && risk>0.0){
					count5000++;
					//System.out.println("minimo "+count5000);
					//System.out.println(maxMin0.getExtra()+" "+PrintUtils.Print2(risk));
					int entryValue = q.getLow5()+pips*10;
					//int entryValue = q.getClose5()+pips*10;
					if (q.getClose5()<entryValue-20){
						//if (entryValue>=)
						int slValue    = entryValue+sl*10;
						int tpValue    = entryValue-tp*10;
						pos = new PositionShort();
						pos.setEntry(entryValue);
						pos.setSl(slValue);
						pos.setTp(tpValue);
						pos.setPositionStatus(PositionStatus.PENDING);
						pos.setPositionType(PositionType.SHORT);
						pos.setPendingIndex(i);
						pos.setRisk(risk);
						positions.add(pos);
					}
				}else if (maxMin0.getExtra()>0 && risk>0.0){
					count5000++;
					//System.out.println("minimo "+count5000);
					
					int entryValue = q.getHigh5()-pips*10;
					if (q.getClose5()>entryValue+20){
						//int entryValue = q.getClose5()-pips*10;
						int slValue    = entryValue-sl*10;
						int tpValue    = entryValue+tp*10;
						pos = new PositionShort();
						pos.setEntry(entryValue);
						pos.setSl(slValue);
						pos.setTp(tpValue);
						pos.setPositionStatus(PositionStatus.PENDING);
						pos.setPositionType(PositionType.LONG);
						pos.setPendingIndex(i);
						pos.setRisk(risk);
						positions.add(pos);
					}
				}
			}
			
			//positions update
			int opens = 0;
			int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
			//System.out.println("totalOpen maxAllowd: "+totalOpen+" "+maxAllowed);
			for (int s=0;s<positions.size();s++){
				PositionShort p = positions.get(s);
				double posRisk = p.getRisk();
				if (p.getPositionStatus()==PositionStatus.PENDING){
					long diffExpiration = i-p.getPendingIndex();
					if (q1.getLow5()<=p.getEntry() && p.getEntry()<=q1.getHigh5() 
							&& diffExpiration<expiration
							&& totalOpen<maxAllowed){
						//margen requerido
						double minBalance = getMinBalanceRequiered(actualBalance,posRisk,maxAllowed,sl);
						if (actualBalance<minBalance){
							extraNeeded += minBalance-actualBalance;
							actualBalance = minBalance;
						}
						
						long microLots = calculateMicroLots(actualBalance,posRisk,maxAllowed,sl);
						
						p.setPositionStatus(PositionStatus.OPEN);
						p.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
						p.setOpenIndex(i+1);
						p.setMicroLots(microLots);
						totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
						//System.out.println("OPEN "+microLots);
						if (totalOpen>maxOpens) maxOpens = totalOpen;
					}
					
				}
				if (p.getPositionStatus()==PositionStatus.OPEN){
					opens++;
					boolean closed = false;
					double posPips = 0;
					if (p.getPositionType()==PositionType.LONG){
						int posSL = p.getEntry()-p.getSl();
						int movedTPPips = (int) (posSL*factorTPPips);
						int tpMoved = p.getEntry()-movedTPPips;
						if (q1.getLow5()<=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed  = true;
							posPips = p.getSl()-p.getEntry();
						}else if (q1.getHigh5()>=p.getTp() && (i)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed  = true;
							posPips = p.getTp()-p.getEntry();
						}else if (q.getLow5()<=tpMoved && q.getClose5()<=p.getEntry() && movedTPPips>=20){
							p.setTp(p.getEntry()+20);
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						int posSL = p.getSl()-p.getEntry();
						int movedTPPips = (int) (posSL*factorTPPips);
						int tpMoved = p.getEntry()+movedTPPips;
						if (q1.getHigh5()>=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed  = true;
							posPips = p.getEntry()-p.getSl();
						}else if (q1.getLow5()<=p.getTp() && (i)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed  = true;
							posPips = p.getEntry()-p.getTp();
						}else if (q.getHigh5()>=tpMoved && q.getClose5()>=p.getEntry() && movedTPPips>=2){
							p.setTp(p.getEntry()-20);
						}
					}
					if (closed){
						double pipsEarned = (posPips-comm*10)/10;
						
						if (pipsEarned>=0){
							profit$+=pipsEarned*p.getMicroLots()*0.1;
							//System.out.println(pipsEarned+" "+p.getMicroLots());
						}else{
							losses$+=Math.abs(pipsEarned*p.getMicroLots()*0.1);
						}
						
						actualBalance +=pipsEarned*p.getMicroLots()*0.1;
						
						p.getCloseCal().setTimeInMillis(cal1.getTimeInMillis());
						//System.out.println("[CLOSE] "+p.toString2()+" || "+q1.toString());
						if (actualBalance>maxBalance) maxBalance = actualBalance;
						double dd = 100.0-actualBalance*100.0/maxBalance;
						if (dd>maxDD) maxDD = dd;
					}
				}
			}//positions
			
			if (totalOpen>=maxAllowed) PositionShort.removePositions(positions, PositionStatus.PENDING);
			
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}
		int cases = losses+wins;
		double per = wins*100.0/cases;
		double lossPer = 100.0-per;
		double pf$ = profit$/losses$;
		double extra = balance+extraNeeded;
		double gainFactor = maxBalance*1.0/(extra);
		System.out.println(header+" "+" "+begin+" "+end+" "+nBars
				+" "+nATR
				+" "+PrintUtils.Print2dec(tpATR,false,1)
				+" "+PrintUtils.Print2dec(slATR,false,1)
				+" "+PrintUtils.Print2dec(pipsATR,false,1)
				+" "+expiration+" "+maxAllowed
				+" || "
				+PrintUtils.Print2Int(losses+wins,4)
				+" "+losses+" "+wins
				+" "+PrintUtils.Print2dec(per,false,3)
				+" "+maxOpens
				+" "+PrintUtils.Print2Int((int) totalPips,6)
				+" || "+PrintUtils.Print2dec2(actualBalance,true)
				+" "+PrintUtils.Print2(balance+extraNeeded)
				+" "+PrintUtils.Print2(pf$)
				+" "+PrintUtils.Print2dec2(maxBalance,true)
				+" "+PrintUtils.Print2dec2(profit$,true)
				+" "+PrintUtils.Print2dec2(losses$,true)
				+" "+PrintUtils.Print2(maxDD)
				+" || "
				+PrintUtils.Print2(pipsAvg/totalQuotes)
				+" "+PrintUtils.Print2(tpAvg/totalQuotes)
				+" "+PrintUtils.Print2(slAvg/totalQuotes)
				+" "+totalQuotes
				);
		return gainFactor;
	}

	public static void tradingHighLowOscillationsBars(String header,ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int begin,int end,
			int h1,int h2,
			int dayWeek1,int dayWeek2,double factor,
			int nBars,int sl,int tp,int pips,int expiration,
			int maxAllowed,
			double balance,double risk,double comm){
		
		
		double actualBalance = balance;
		double extraNeeded   = 0;
		double profit$        = 0;
		double losses$        = 0;
		double maxBalance = balance;
		double maxDD      = 0.0;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		if (begin<0) begin = 0;
		if (end>data.size()-2) end = data.size()-2;
		//if (end-begin<binSize) return;
		//System.out.println("size end: "+data.size()+" "+end);
		int wins   = 0;
		int losses = 0;
		int maxOpens = 0;
		PositionShort pos = null;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			//QuoteShort maxMin0 = TradingUtils.getMaxMinShort(data, i-nBars, i-1);
			//QuoteShort maxMin1 = TradingUtils.getMaxMinShort(data, i, i+nBars);
			QuoteShort maxMin0 = maxMins.get(i);
			
			if (h>=h1 && h<=h2){				
				if (maxMin0.getExtra()==-1){
					int entryValue = q.getLow5()+pips*10;
					//if (entryValue>=)
					int slValue    = entryValue+sl*10;
					int tpValue    = entryValue-tp*10;
					pos = new PositionShort();
					pos.setEntry(entryValue);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPositionType(PositionType.SHORT);
					pos.setPendingIndex(i);
					positions.add(pos);
				}else if (maxMin0.getExtra()==1){
					int entryValue = q.getHigh5()-pips*10;
					int slValue    = entryValue-sl*10;
					int tpValue    = entryValue+tp*10;
					pos = new PositionShort();
					pos.setEntry(entryValue);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPositionType(PositionType.LONG);
					pos.setPendingIndex(i);
					positions.add(pos);
				}
			}
			
			//positions update
			int opens = 0;
			int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
			//System.out.println("totalOpen maxAllowd: "+totalOpen+" "+maxAllowed);
			for (int s=0;s<positions.size();s++){
				PositionShort p = positions.get(s);
				if (p.getPositionStatus()==PositionStatus.PENDING){
					long diffExpiration = i-p.getPendingIndex();
					if (q1.getLow5()<=p.getEntry() && p.getEntry()<=q1.getHigh5() 
							&& diffExpiration<expiration
							&& totalOpen<maxAllowed){
						//margen requerido
						double minBalance = getMinBalanceRequiered(actualBalance,risk,maxAllowed,sl);
						if (actualBalance<minBalance){
							extraNeeded += minBalance-actualBalance;
							actualBalance = minBalance;
						}
						
						long microLots = calculateMicroLots(actualBalance,risk,maxAllowed,sl);
						
						p.setPositionStatus(PositionStatus.OPEN);
						p.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
						p.setOpenIndex(i+1);
						p.setMicroLots(microLots);
						totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
						if (totalOpen>maxOpens) maxOpens = opens;
					}
				}
				if (p.getPositionStatus()==PositionStatus.OPEN){
					opens++;
					boolean closed = false;
					double posPips = 0;
					if (p.getPositionType()==PositionType.LONG){
						if (q1.getLow5()<=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed  = true;
							posPips = p.getSl()-p.getEntry();
						}else if (q1.getHigh5()>=p.getTp() && (i)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed  = true;
							posPips = p.getTp()-p.getEntry();
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						if (q1.getHigh5()>=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed  = true;
							posPips = p.getEntry()-p.getSl();
						}else if (q1.getLow5()<=p.getTp() && (i)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed  = true;
							posPips = p.getEntry()-p.getTp();
						}
					}
					if (closed){
						double pipsEarned = (posPips-comm*10)/10;
						
						if (pipsEarned>=0){
							profit$+=pipsEarned*p.getMicroLots()*0.1;
							//System.out.println(pipsEarned+" "+p.getMicroLots());
						}else{
							losses$+=Math.abs(pipsEarned*p.getMicroLots()*0.1);
						}
						
						actualBalance +=pipsEarned*p.getMicroLots()*0.1;
						
						p.getCloseCal().setTimeInMillis(cal1.getTimeInMillis());
						//System.out.println("[CLOSE] "+p.toString2()+" || "+q1.toString());
						if (actualBalance>maxBalance) maxBalance = actualBalance;
						double dd = 100.0-actualBalance*100.0/maxBalance;
						if (dd>maxDD) maxDD = dd;
					}
				}
			}//positions						
		}
		int cases = losses+wins;
		double per = wins*100.0/cases;
		double lossPer = 100.0-per;
		double exp = (per*tp-(100.0-per)*sl);
		double pf = (wins*tp*1.0)/(losses*sl);
		double totalPips = cases*((exp/100.0)-comm);
		double pf$ = profit$/losses$;
		System.out.println(header+" "+h1+" "+begin+" "+end+" "+nBars+" "+tp+" "+sl+" "+pips+" "+expiration+" "+maxAllowed+" "+risk
				+" || "
				+PrintUtils.Print2Int(losses+wins,4)
				+" "+losses+" "+wins
				+" "+PrintUtils.Print2(per)
				+" "+maxOpens
				+" "+PrintUtils.Print2(exp/100.0)
				+" "+PrintUtils.Print2(pf)
				+" "+PrintUtils.Print2(totalPips)
				+" || "+PrintUtils.Print2dec2(actualBalance,true)
				+" "+PrintUtils.Print2(balance+extraNeeded)
				+" "+PrintUtils.Print2(pf$)
				+" "+PrintUtils.Print2dec2(maxBalance,true)
				+" "+PrintUtils.Print2dec2(profit$,true)
				+" "+PrintUtils.Print2dec2(losses$,true)
				+" "+PrintUtils.Print2(maxDD)
				);
	}
	
	public static void tradingHighLowOscillationsBarsATR(String header,ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int begin,int end,
			int h1,int h2,
			int dayWeek1,int dayWeek2,double factor,
			int nBars,
			int nATR,double slATR,double tpATR,double pipsATR,
			int expiration,
			int maxAllowed,
			double balance,double risk,double comm){
		
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		double actualBalance = balance;
		double extraNeeded   = 0;
		double profit$        = 0;
		double losses$        = 0;
		double maxBalance = balance;
		double maxDD      = 0.0;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		if (begin<0) begin = 0;
		if (end>data.size()-2) end = data.size()-2;
		//if (end-begin<binSize) return;
		//System.out.println("size end: "+data.size()+" "+end);
		int wins   = 0;
		int losses = 0;
		int maxOpens = 0;
		PositionShort pos = null;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int max = -999999;
		int min = 999999;
		double atr = 100;
		int totalDays = 0;
		long totalPips = 0;
		int pipsAvg = 0;
		int slAvg = 0;
		int tpAvg = 0;
		int totalQuotes=0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (day!=lastDay){
				if (lastDay>=0){
					int range = (max-min)/10;
					dailyRanges.add(range);
					if (dailyRanges.size()>0){
						atr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
						//System.out.println("max min atr y range "+max+" "+min+" "+atr/10+" "+range/10);
					}
				}
				max = -999999;
				min = 999999;
				lastDay = day;
				totalDays++;
			}
			
			int pips = (int) (atr*pipsATR);
			int sl   = (int) (atr*slATR);
			int tp   = (int) (atr*tpATR);
			pipsAvg+=pips;
			slAvg+=sl;
			tpAvg+=tp;
			totalQuotes++;
			//QuoteShort maxMin0 = TradingUtils.getMaxMinShort(data, i-nBars, i-1);
			//QuoteShort maxMin1 = TradingUtils.getMaxMinShort(data, i, i+nBars);
			QuoteShort maxMin0 = maxMins.get(i);
			
			if (h>=h1 && h<=h2){				
				if (maxMin0.getExtra()==-1){
					int entryValue = q.getLow5()+pips*10;
					//if (entryValue>=)
					int slValue    = entryValue+sl*10;
					int tpValue    = entryValue-tp*10;
					pos = new PositionShort();
					pos.setEntry(entryValue);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPositionType(PositionType.SHORT);
					pos.setPendingIndex(i);
					positions.add(pos);
				}else if (maxMin0.getExtra()==1){
					int entryValue = q.getHigh5()-pips*10;
					int slValue    = entryValue-sl*10;
					int tpValue    = entryValue+tp*10;
					pos = new PositionShort();
					pos.setEntry(entryValue);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionStatus(PositionStatus.PENDING);
					pos.setPositionType(PositionType.LONG);
					pos.setPendingIndex(i);
					positions.add(pos);
				}
			}
			
			//positions update
			int opens = 0;
			int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
			//System.out.println("totalOpen maxAllowd: "+totalOpen+" "+maxAllowed);
			for (int s=0;s<positions.size();s++){
				PositionShort p = positions.get(s);
				if (p.getPositionStatus()==PositionStatus.PENDING){
					long diffExpiration = i-p.getPendingIndex();
					if (q1.getLow5()<=p.getEntry() && p.getEntry()<=q1.getHigh5() 
							&& diffExpiration<expiration
							&& totalOpen<maxAllowed){
						//margen requerido
						double minBalance = getMinBalanceRequiered(actualBalance,risk,maxAllowed,sl);
						if (actualBalance<minBalance){
							extraNeeded += minBalance-actualBalance;
							actualBalance = minBalance;
						}
						
						long microLots = calculateMicroLots(actualBalance,risk,maxAllowed,sl);
						
						p.setPositionStatus(PositionStatus.OPEN);
						p.getOpenCal().setTimeInMillis(cal1.getTimeInMillis());
						p.setOpenIndex(i+1);
						p.setMicroLots(microLots);
						totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
						if (totalOpen>maxOpens) maxOpens = totalOpen;
					}
				}
				if (p.getPositionStatus()==PositionStatus.OPEN){
					opens++;
					boolean closed = false;
					double posPips = 0;
					if (p.getPositionType()==PositionType.LONG){
						if (q1.getLow5()<=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed  = true;
							posPips = p.getSl()-p.getEntry();
						}else if (q1.getHigh5()>=p.getTp() && (i)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed  = true;
							posPips = p.getTp()-p.getEntry();
						}
					}else if (p.getPositionType()==PositionType.SHORT){
						if (q1.getHigh5()>=p.getSl()){
							p.setPositionStatus(PositionStatus.CLOSE);
							losses++;
							closed  = true;
							posPips = p.getEntry()-p.getSl();
						}else if (q1.getLow5()<=p.getTp() && (i)>p.getOpenIndex()){
							p.setPositionStatus(PositionStatus.CLOSE);
							wins++;
							closed  = true;
							posPips = p.getEntry()-p.getTp();
						}
					}
					if (closed){
						double pipsEarned = (posPips-comm*10)/10;
						totalPips+=pipsEarned;
						if (pipsEarned>=0){
							profit$+=pipsEarned*p.getMicroLots()*0.1;
							//System.out.println(pipsEarned+" "+p.getMicroLots());
						}else{
							losses$+=Math.abs(pipsEarned*p.getMicroLots()*0.1);
						}
						
						actualBalance +=pipsEarned*p.getMicroLots()*0.1;
						
						p.getCloseCal().setTimeInMillis(cal1.getTimeInMillis());
						//System.out.println("[CLOSE] "+p.toString2()+" || "+q1.toString());
						if (actualBalance>maxBalance) maxBalance = actualBalance;
						double dd = 100.0-actualBalance*100.0/maxBalance;
						if (dd>maxDD) maxDD = dd;
					}
				}
			}//positions		
			
			if (q.getHigh5()>max) max = q.getHigh5();
			if (q.getLow5()<min) min = q.getLow5();
		}
		int cases = losses+wins;
		double per = wins*100.0/cases;
		double lossPer = 100.0-per;
		double pf$ = profit$/losses$;
		System.out.println(header+" "+h1+" "+begin+" "+end+" "+nBars
				+" "+nATR
				+" "+PrintUtils.Print2dec(tpATR,false,1)
				+" "+PrintUtils.Print2dec(slATR,false,1)
				+" "+PrintUtils.Print2dec(pipsATR,false,1)
				+" "+expiration+" "+maxAllowed+" "+risk
				+" || "
				+PrintUtils.Print2Int(losses+wins,4)
				+" "+losses+" "+wins
				+" "+PrintUtils.Print2dec(per,false,3)
				+" "+maxOpens
				+" "+PrintUtils.Print2Int((int) totalPips,6)
				+" || "+PrintUtils.Print2dec2(actualBalance,true)
				+" "+PrintUtils.Print2(balance+extraNeeded)
				+" "+PrintUtils.Print2(pf$)
				+" "+PrintUtils.Print2dec2(maxBalance,true)
				+" "+PrintUtils.Print2dec2(profit$,true)
				+" "+PrintUtils.Print2dec2(losses$,true)
				+" "+PrintUtils.Print2(maxDD)
				+" || "
				+PrintUtils.Print2(pipsAvg/totalQuotes)
				+" "+PrintUtils.Print2(tpAvg/totalQuotes)
				+" "+PrintUtils.Print2(slAvg/totalQuotes)
				+" "+totalQuotes
				);
	}
	
	public static void testHighLowOscillationsBars(String header,ArrayList<QuoteShort> data,
			int begin,int end,
			int h1,
			int dayWeek1,int dayWeek2,double factor,int nBars){
		
		if (begin<0) begin = 0;
		if (end>data.size()-1) end = data.size()-1;
		//if (end-begin<binSize) return;
		
		int cases = 0;
		int wins  = 0;
		
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			QuoteShort maxMin0 = TradingUtils.getMaxMinShort(data, i-nBars, i-1);
			QuoteShort maxMin1 = TradingUtils.getMaxMinShort(data, i, i+nBars);
			
			if (h==h1){
				if (q.getLow5()<maxMin0.getLow5()){
					cases++;
					if (maxMin1.getHigh5()>maxMin0.getHigh5()){
						wins++;
					}
				}else if (q.getHigh5()>maxMin0.getHigh5()){
					cases++;
					if (maxMin1.getLow5()<maxMin0.getLow5()){
						wins++;
					}
				}
			}
		}
		double per = wins*100.0/cases;
		System.out.println(header+" "+h1+" "+begin+" "+end+" "+nBars+" || "+PrintUtils.Print2Int(cases,4)+" "+PrintUtils.Print2(per));
	}
	
	
	public static void testHighLowOscillationsALL(String header,ArrayList<QuoteShort> data,
			int begin,int end,
			int h1,
			int dayWeek1,int dayWeek2,
			int binSize,double factor){
		
		ArrayList<Integer> counts= new ArrayList<Integer>();
		for (int i=0;i<=7;i++) counts.add(0);
		
		if (begin<0) begin = 0;
		if (end>data.size()-1) end = data.size()-1;
		//if (end-begin<binSize) return;
		
		int cases = 0;
		int wins  = 0;
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays=0;
		int min = 999999;
		int max = -999999;
		int lastHL = 0;
		boolean valid = false;
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		double atr= 1000.0;
		int range = (int) (atr*factor);
		boolean finished = false;
		String fingerPrint="";
		int count = 0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
			
			if (day!=lastDay){
				if (lastDay!=-1){
					dailyRanges.add(max-min);
					System.out.println(fingerPrint);
				}
				if (count>=1){
					int tot = counts.get(count-1);
					counts.set(count-1, tot+1);
					count = 0;
				}
				lastDay = day;
				lastHL = 0;
				valid = false;
				finished = false;
				if (dailyRanges.size()>0){
					atr = MathUtils.average(dailyRanges,totalDays-9,totalDays-1);
					range = (int) (atr*factor);
					//System.out.println("max min atr y range "+max+" "+min+" "+atr/10+" "+range/10);
				}
				min = 999999;
				max = -99999;
				totalDays++;
				fingerPrint=DateUtils.datePrint(cal)+" ";
			}
			
			int actualHL = 0;
			String hl = "";
			if (q.getHigh5()>max){
				max = q.getHigh5();
				actualHL = 1;
				hl="HIGH("+h+")";
			}
			if (q.getLow5()<min){
				min = q.getLow5();
				actualHL = -1;
				hl="LOW("+h+")";
			}
			double actualRange = max-min;
			//se hace un maximo/minimo
			//if (!finished){
			if (h>=h1 && actualRange>=range && actualHL!=0 && !valid){
				valid = true;//se activa
				lastHL = actualHL;
				cases++;
				fingerPrint+=hl+" ";
				count++;
			}
				
			if (valid && actualHL==-lastHL){
				wins++;
				finished = true;
				fingerPrint+=hl+" ";
				lastHL=actualHL;
				count++;
			}
			//}
		}
		//ultimo dia
		System.out.println(fingerPrint);
		
		double perOsc = wins*100.0/cases;
		String per = "";
		int tot = 0;
		for (int i=0;i<counts.size();i++){
			tot+=counts.get(i);
			//System.out.println(counts.get(i));
		}
		for (int i=1;i<=3;i++){
			if (counts.get(i-1)>0)
				per+=PrintUtils.Print2(counts.get(i)*100.0/counts.get(i-1))+" ";
			else per+="-"+" ";
		}
		
		System.out.println(header+" "+h1+" "+begin+" "+end+" "+PrintUtils.Print2(factor)+" || "+tot+" "+per);
	}
	
	public static void testHighLowOscillations(String header,ArrayList<QuoteShort> data,
			int begin,int end,
			int h1,
			int dayWeek1,int dayWeek2,
			int binSize,double factor){
		
		
		if (begin<0) begin = 0;
		if (end>data.size()-1) end = data.size()-1;
		if (end-begin<binSize) return;
		
		int cases = 0;
		int wins  = 0;
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays=0;
		int min = 999999;
		int max = -999999;
		int lastHL = 0;
		boolean valid = false;
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		double atr= 1000.0;
		int range = (int) (atr*factor);
		boolean finished = false;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
			
			if (day!=lastDay){
				if (lastDay!=-1){
					dailyRanges.add(max-min);
				}
				lastDay = day;
				lastHL = 0;
				valid = false;
				finished = false;
				if (dailyRanges.size()>0){
					atr = MathUtils.average(dailyRanges,totalDays-9,totalDays-1);
					range = (int) (atr*factor);
					//System.out.println("max min atr y range "+max+" "+min+" "+atr/10+" "+range/10);
				}
				min = 999999;
				max = -99999;
				totalDays++;
			}
			
			int actualHL = 0;
			if (q.getHigh5()>max){
				max = q.getHigh5();
				actualHL = 1;
			}
			if (q.getLow5()<min){
				min = q.getLow5();
				actualHL = -1;
			}
			double actualRange = max-min;
			//se hace un maximo/minimo
			if (!finished){
				if (h==h1 && actualRange>=range && actualHL!=0 && !valid){
					valid = true;//se activa
					lastHL = actualHL;
					cases++;
				}
				
				if (valid && actualHL==-lastHL){
					wins++;
					finished = true;
				}
			}
		}
		
		double perOsc = wins*100.0/cases;
				
		
		System.out.println(header+" "+h1+" "+begin+" "+end+" "+PrintUtils.Print2(factor)+" || "+cases+" "+PrintUtils.Print2(perOsc));
	}
	
	
	public static void countHighLowOscillations(String header,ArrayList<QuoteShort> data,
			int begin,int end,
			int h1,int h2,
			int dayWeek1,int dayWeek2,
			int binSize,double factor){
		
		
		if (begin<0) begin = 0;
		if (end>data.size()-1) end = data.size()-1;
		if (end-begin<binSize) return;
		
		ArrayList<Integer> osc = new ArrayList<Integer>();
		for (int i=0;i<=100;i++) osc.add(0);
		int total = 0;
		int totalDays = 0;
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int min = 999999;
		int max = -999999;
		int lastHL = 0;
		boolean valid = false;
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		double atr= 1000.0;
		int range = (int) (atr*factor);
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
			
			if (day!=lastDay){
				if (lastDay!=-1)
					dailyRanges.add(max-min);
				if (lastDay!=-1 && valid){
					int count = osc.get(total);
					osc.set(total, count+1);
				}
				
				lastDay = day;
				total = 0;
				lastHL = 0;
				totalDays++;
				valid = false;
				//MathUtils.
				if (dailyRanges.size()>0){
					atr = MathUtils.average(dailyRanges,totalDays-9,totalDays-1);
					range = (int) (atr*factor);
					//System.out.println("max min atr y range "+max+" "+min+" "+atr/10+" "+range/10);
				}
				min = 999999;
				max = -99999;
			}
			
			int actualHL = 0;
			if (q.getHigh5()>max){
				max = q.getHigh5();
				actualHL = 1;
			}
			if (q.getLow5()<min){
				min = q.getLow5();
				actualHL = -1;
			}
			double actualRange = max-min;
			//se hace un maximo/minimo
			if (actualHL!=0 && actualHL!=lastHL && actualRange>=range){									
				if (h>=h1 && h<=h2){	
					valid = true;									
				}
				if (valid){
					if (lastHL!=0 && h>=h1)
						total++;	
				}
				lastHL = actualHL;					
			}			
		}
		String totals="";
		/*for (int i=0;i<=7;i++){
			totals +=PrintUtils.Print2(osc.get(i)*100.0/totalDays)+" ";
		}*/
		int totali = 0;
		for (int i=0;i<=20;i++){
			totali += osc.get(i);
		}
				
		for (int i=0;i<=7;i++){
			totals +=PrintUtils.Print2dec(osc.get(i)*100.0/totali,false,2)+" ";
		}
		System.out.println(header+" "+h1+" "+h2+" "+begin+" "+end+" "+PrintUtils.Print2(factor)+" || "+totali+" "+totals);
	}


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.31.csv";
		//String path5m1   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.11.27.csv";
		//String path5m0   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.20.csv";
		String path5m1   = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.12.20.csv";
		String path5m2   = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.12.20.csv";
		String path5m3   = "c:\\fxdata\\USDCAD_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		String path5m4   = "c:\\fxdata\\GBPAUD_UTC_5 Mins_Bid_2006.03.22_2014.11.27.csv";
		String path5m5   = "c:\\fxdata\\EURGBP_UTC_5 Mins_Bid_2003.08.03_2014.11.27.csv";
		String path5m6   = "c:\\fxdata\\EURAUD_UTC_5 Mins_Bid_2005.10.07_2014.11.27.csv";		
		String path5m7   = "c:\\fxdata\\AUDNZD_UTC_5 Mins_Bid_2006.12.12_2014.11.27.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(path5m0);paths.add(path5m1);paths.add(path5m2);
		paths.add(path5m3);paths.add(path5m4);paths.add(path5m5);
		paths.add(path5m6);paths.add(path5m7);
		
		for (int i=0;i<=0;i++){			
			Sizeof.runGC ();
			String path5m = paths.get(i);
			ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 
	  		ArrayList<Quote> hourlyData 	= ConvertLib.convert(data5m, 12);
	  		//ArrayList<Quote> dailyData 	= ConvertLib.createDailyData(data5m);
	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			ArrayList<QuoteShort> hourlyDataS    = QuoteShort.convertQuoteArraytoQuoteShort(hourlyData);
			//ArrayList<QuoteShort> dailyDataS  = QuoteShort.convertQuoteArraytoQuoteShort(dailyData);
			
			ArrayList<QuoteShort> data = null;
			data = data5mS;
			//data = hourlyDataS;
			
			String header ="";
			if (path5m.contains("EURUSD")) header="EURUSD";
			if (path5m.contains("GBPUSD")) header="GBPUSD";
			if (path5m.contains("AUDUSD")) header="AUDUSD";
			if (path5m.contains("GBPAUD")) header="GBPAUD";
			if (path5m.contains("EURGBP")) header="EURGBP";
			if (path5m.contains("EURAUD")) header="EURAUD";
			if (path5m.contains("USDCAD")) header="USDCAD";
			if (path5m.contains("AUDNZD")) header="AUDNZD";
			
			//QuoteShort.saveToDisk(data5mS,"c:\\data5digits.csv");

			
			int begin = data.size()-400000;
			begin     = 1;
			int end   = data.size();
			
			begin   = 300000;
			//end   = 400000;
			
			int boxes = 1;
			int boxSpread = (end-begin)/boxes;
			int binSize = end-begin;
			//binSize = 800000;
			int h1 = 0;
			int h2 = 23;
			int dayWeek1 = Calendar.MONDAY+1;
			int dayWeek2 = Calendar.MONDAY+1;
			
			ArrayList<MaxMinRisk> maxMinRisks = new ArrayList<MaxMinRisk>();
			ArrayList<QuoteShort> maxMinsExt = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			System.out.println("calculados maxMinsAbsolutos: "+maxMinsExt.size());
			
			int total = 0;
			int totalLessZero = 0;
			int totalUp2 = 0;
			int totalUp5 = 0;
			int totalUp10 = 0;
			int totalUp20 = 0;
			int totalUp50 = 0;
			double comm = 1.4;
			dayWeek1 = Calendar.MONDAY+0;
			dayWeek2 = Calendar.MONDAY+4;
			for (int b = 0;b<boxes;b++){
				int begin1 = begin+b*boxSpread;
				int end1   = begin1+binSize;
				if (end1>data.size()){
					b=boxes-1;
					end1= data.size()-1;
					begin1 = end1-binSize;
				}
				for (int nBars=2000;nBars<=10000;nBars+=100){
					//MaxMinRisk.load(maxMinRisks,"5000 11.0"+","+String.valueOf(nBars)+" 8.0");
					for (double risk=8.0;risk<=8.0;risk+=0.25){
						header=" "+PrintUtils.Print2dec(risk,false,2);
						MaxMinRisk.load(maxMinRisks,String.valueOf(nBars)+" "+String.valueOf(risk));
						for (h1=0;h1<=0;h1++){
							h2=h1+0;
							//TestOscillations.testHighLowOscillationsBars(header,data,begin,end, h1,dayWeek1,dayWeek2,0.0,nBars);
							//for (int tp=14;tp<=14;tp+=1){
							for (int nATR=360;nATR<=360;nATR+=10){
								for (double tpATR=0.14;tpATR<=0.14;tpATR+=0.01){
									//for (int sl=(int) (tp*3.0);sl<=tp*3.0;sl+=tp*0.5){
									//for (double slATR=tpATR;slATR<=tpATR;slATR+=0.1){
									for (double slATR=0.29;slATR<=0.29;slATR+=0.1){
										//for (int pips=22;pips<=22;pips+=1){
										for (double pipsATR=0.21;pipsATR<=0.21;pipsATR+=0.01){	//0.14 0.28 0.2
											for (int expiration=800;expiration<=800;expiration+=10){
												for (int maxAllowed=7;maxAllowed<=7;maxAllowed++){
													for (double factorTP=10.0;factorTP<=10;factorTP+=0.1){
														//for (double risk=9.0;risk<=9.0;risk+=0.5){
															//TestOscillations.tradingHighLowOscillationsBars(header, data,maxMins,begin1, end1, 
															//TestOscillations.tradingHighLowOscillationsBarsV2(header, data,maxMins,begin, end,
																	//h1,h2, dayWeek1, dayWeek2,
																	//0.0, nBars, sl, tp,pips,expiration,maxAllowed,5000,risk,comm);
															//TestOscillations.tradingHighLowOscillationsBarsATR(header, data,maxMins,begin, end,
																	//h1,h2, dayWeek1, dayWeek2,
																	//0.0, nBars,nATR,slATR, tpATR,pipsATR,expiration,maxAllowed,1000,risk,comm);
															double pf = TestOscillations.tradingHighLowOscillationsBarsATRv3(header, data,maxMinsExt,begin1, end1,
																	"12 16 17 18 19 20 21 22 23", dayWeek1, dayWeek2,
																	//String.valueOf(h1), dayWeek1, dayWeek2,
																	0.0, nBars,nATR,slATR, tpATR,pipsATR,factorTP,
																	expiration,maxAllowed,1000,maxMinRisks,comm);
															total++;
															if (pf<1.5) totalLessZero++;
															if (pf>=2.0) totalUp2++;
															if (pf>=5.0) totalUp5++;
															if (pf>=10.0) totalUp10++;
															if (pf>=20.0) totalUp20++;
															if (pf>=50.0) totalUp50++;
														//}
													}
												}
											}
										}
									}//sl
								}//tp
							}//Natr
						}//h1
					}
				}//nbars
			}//boxes
			boxes = total;
			/*System.out.println("boxes <1.0 >2.0 >5.0 >10.0 >20.0 >50.0 "
					+boxes+" "
					+PrintUtils.Print2(totalLessZero*100.0/boxes)+" "
					+PrintUtils.Print2(totalUp2*100.0/boxes)+" "
					+PrintUtils.Print2(totalUp5*100.0/boxes)+" "
					+PrintUtils.Print2(totalUp10*100.0/boxes)+" "
					+PrintUtils.Print2(totalUp20*100.0/boxes)+" "
					+PrintUtils.Print2(totalUp50*100.0/boxes)+" "
					);*/
		}
	}
}
