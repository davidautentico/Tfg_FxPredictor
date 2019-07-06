package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class DOWES {
	
	public static void testRebounds_classic(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			ArrayList<Integer> thrs,
			int y1,int y2,
			int minDesviation,
			int nBars,
			int tp,
			int sl,
			int maxTrades,
			int factor,		
			double risk,
			double comm,
			boolean debug
			){
	
		ArrayList<Integer> orderWins = new ArrayList<Integer> ();
		ArrayList<Integer> orderLosses = new ArrayList<Integer> ();
		for (int i=0;i<maxTrades;i++){
			orderWins.add(0);
			orderLosses.add(0);
		}
		double balanceInicial = 100000;
		double balanceActual = balanceInicial;
		double maxBalance = balanceActual;
		double minBalance = balanceActual;
		double maxBalanceDD = 0.0;
		double maxBalanceDDPer = 0.0;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		int bes = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		double minTicks = 0;
		double maxTicks = 0;
		double maxDD = 0;
		double accMaxAdv = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			//if (h<h1 || h>h2) continue;
			
			
						
			//2. *********EVALUACION NUEVAS ENTRADAS DE POSICIONES DE q
			//calculo el max y min de los ultimos thr	
			int maxMin = maxMins.get(i-1).getExtra();	
			
			double diffH = (q1.getHigh5()-q1.getClose5())*1.0/factor;
			double diffL = (q1.getClose5()-q1.getLow5())*1.0/factor;
			int thr = thrs.get(h);
			if (thr>0){
				if (
						maxMin>=thr 
						//maxMin<=thr
						//&& diffH<=5
						){		
					int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
					int value = q.getOpen5();
					int tpValue = value - tp*factor;
					int slValue = value + sl*factor;
					PositionType posType = PositionType.SHORT;
					if (totalOpen<maxTrades){
						PositionShort pos = new PositionShort();
						pos.setEntry(value);
						pos.setTp(tpValue);
						pos.setSl(slValue);
						pos.setPositionType(posType);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setOpenIndex(i);
						pos.setRisked$$(12.5*sl);
						pos.setPip$$(pos.getRisked$$()/sl);
						pos.setOrder(totalOpen);
						positions.add(pos);
						totalOpen++;
					}
				}else if (
						maxMin<=-thr
						//&& diffL<=5
						){
					int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
					int value = q.getOpen5();						
					int tpValue = value + tp*factor;
					int slValue = value - sl*factor;
					PositionType posType = PositionType.LONG;
					if (totalOpen<maxTrades){
						PositionShort pos = new PositionShort();
						pos.setEntry(value);
						pos.setTp(tpValue);
						pos.setSl(slValue);
						pos.setPositionType(posType);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.setOpenIndex(i);
						pos.setRisked$$(12.5*sl);
						pos.setPip$$(pos.getRisked$$()/sl);
						pos.setOrder(totalOpen);
						positions.add(pos);
						totalOpen++;
						if (debug){
							System.out.println(q1.toString()+" || "+maxMin+" || "+pos.toString());
						}
					}
				}
			}//H
			
			//evaluacion posiciones
			
			int p = 0;
			double actualTicks = winTicks-lostTicks;
			while (p<positions.size()){
				PositionShort pos = positions.get(p);
				
				boolean closed = false;
				double ticks = 0;
				double maxAdv = 0;
				if (pos.getPositionType() == PositionType.LONG){
					maxAdv = (pos.getEntry()-q.getLow5())*1.0/factor;
					if (maxAdv >pos.getMaxAdversion()) pos.setMaxAdversion(maxAdv);
					if (q.getLow5()<=pos.getSl()){
						ticks = (pos.getSl()-pos.getEntry())*1.0/factor;
						closed = true;
					}else if (q.getHigh5()>=pos.getTp()){
						ticks = (pos.getTp()-pos.getEntry())*1.0/factor;
						closed=true;
					}else {
						double ticksPos = (q.getHigh5()-pos.getEntry())*1.0/factor;
						double ticksNeg = (pos.getEntry()-q.getLow5())*1.0/factor;
						//if (ticksPos>=0.4*tp && q.getClose5()>=pos.getEntry()+2*factor)
							//pos.setSl(pos.getEntry());
						/*if (ticksNeg>=1.4*sl && q.getClose5()<=pos.getEntry()-2*factor)
							pos.setTp(pos.getEntry());
						
						ticks = (q.getClose5()-pos.getEntry())*1.0/factor;*/ 
					
						
						
						if (i==data.size()-1){
							closed = true;
							ticks = (q.getClose5()-pos.getEntry())*1.0/factor;
						}
					}
				}else if (pos.getPositionType() == PositionType.SHORT){
					maxAdv = (q.getHigh5()-pos.getEntry())*1.0/factor;
					if (maxAdv >pos.getMaxAdversion()) pos.setMaxAdversion(maxAdv);
					if (q.getHigh5()>=pos.getSl()){
						ticks = (pos.getEntry()-pos.getSl())*1.0/factor;
						closed = true;
					}else if (q.getLow5()<=pos.getTp()){
						ticks = (pos.getEntry()-pos.getTp())*1.0/factor;
						closed=true;
					}else{
						double ticksPos = (pos.getEntry()-q.getLow5())*1.0/factor;
						double ticksNeg = (q.getHigh5()-pos.getEntry())*1.0/factor;
						//if (ticksPos>=0.4*tp && q.getClose5()<=pos.getEntry()-2*factor){
						//pos.setSl(pos.getEntry());
						//}
						
						/*if (ticksNeg>=1.4*sl && q.getClose5()>=pos.getEntry()+2*factor)
							pos.setTp(pos.getEntry());
						
						ticks = (pos.getEntry()-q.getClose5())*1.0/factor;*/ 
						if (i==data.size()-1){
							closed = true;
							ticks = (pos.getEntry()-q.getClose5())*1.0/factor;
						}
					}
				}
				
				actualTicks += (ticks-comm);
				if (actualTicks>=maxTicks){ 
					maxTicks=actualTicks;
				}else{
					if (actualTicks<minTicks){
						minTicks = actualTicks;						
					}
					double currentDD = maxTicks-actualTicks;
					if (currentDD>=maxDD) maxDD = currentDD;
					/*System.out.println(
							PrintUtils.Print2dec(maxTicks, false)
							+" "+PrintUtils.Print2dec(minTicks, false)
							+" "+PrintUtils.Print2dec(actualTicks, false)
							+" "+PrintUtils.Print2dec(maxDD, false)
							+" || "+PrintUtils.Print2dec(ticks-comm, false)
							);*/
				}
				
				
				if (closed){
					ticks -= comm;
					//calculo de balance
					balanceActual += ticks*pos.getPip$$();
					/*System.out.println(PrintUtils.Print2dec(balanceActual, false)
							+" "+PrintUtils.Print2dec(ticks*pos.getPip$$(), false)
							);*/
					if (balanceActual<=minBalance){
						minBalance = balanceActual;
					}else if (balanceActual>=maxBalance){
						maxBalance = balanceActual;
					}
					double currentBalanceDD = maxBalance-balanceActual;
					double currentBalanceDDPer = currentBalanceDD*100.0/maxBalance;
					if (currentBalanceDDPer>=maxBalanceDDPer){
						maxBalanceDD = currentBalanceDD;
						maxBalanceDDPer = currentBalanceDDPer;
					}
					
					if (ticks>=0){
						winTicks += ticks;
						wins++;
						actualLosses = 0;
						accMaxAdv += pos.getMaxAdversion();
						orderWins.set(pos.getOrder(), orderWins.get(pos.getOrder())+1);
					}else{
						lostTicks += -ticks;
						losses++;
						actualLosses++;
						orderLosses.set(pos.getOrder(), orderLosses.get(pos.getOrder())+1);
						if (actualLosses>=maxLosses){
							maxLosses = actualLosses;
							/*System.out.println(
									actualLosses
									+" || "
									+" "+PrintUtils.Print2dec(maxTicks, false)
									+" "+PrintUtils.Print2dec(actualTicks, false)
									+" "+PrintUtils.Print2dec(maxDD, false)
									);*/
						}
					}
					positions.remove(p);
				}else{
					p++;
				}
			}//positions
					
		}//data
		
		
		int cases = wins+losses;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double pf = winTicks/lostTicks;
		double yield = avg*100.0/sl;
		double profit$ = 1000*Math.pow((1+0.01*yield/100),cases);//basado en riesgo 1%
		double profitPer = profit$*100.0/1000-100.0;
		//amount normalized to 30% max risk
		double riskFactor = maxDD/sl;
		double risk1 = 30.0/riskFactor;
		double maxRisk = 80.0/maxTrades;
		if (risk1>=maxRisk) maxRisk = risk1;
		double profit$adjusted = 1000*Math.pow((1+0.01*risk1*yield/100),cases);//basado en riesgo 1%
		double profit$$ = balanceActual-balanceInicial;
		double maxProfit$$ = maxBalance-balanceInicial;
		double avgMaxAdv = accMaxAdv*1.0/wins;
		System.out.println(
				" "
				+" "+thrs.get(0)
				+" "+tp
				+" "+sl
				+" "+maxTrades
				+" "+nBars
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgMaxAdv, false)
				//+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(yield, false)
				+" || "+PrintUtils.Print2dec2(profit$ , true)
				+" || "+maxLosses
				+" || "+PrintUtils.Print2dec(maxTicks, false)+" "+PrintUtils.Print2dec(minTicks, false)+" "+PrintUtils.Print2dec(maxDD, false)
				+" ||"+PrintUtils.Print2dec2(profitPer, true)+" "+PrintUtils.Print2dec(maxDD/sl, false)
				+" || "+PrintUtils.Print2dec(profitPer*sl/maxDD, false)
				+" ||"+PrintUtils.Print2dec2(profit$adjusted, true)+" "+PrintUtils.Print2dec(risk, false)
				+" ||"+PrintUtils.Print2dec2(profit$$, true)+" "+PrintUtils.Print2dec2(maxBalanceDD, true)
				+" ||"+PrintUtils.Print2dec2(maxProfit$$*1.0/maxBalanceDD, true)
				);
		
		/*for (int i=0;i<maxTrades;i++){			
			int w = orderWins.get(i);
			int l = orderLosses.get(i);
			if ((w+l)==0 )break;
			double wp = w*100.0/(w+l);
			double a = (w*tp-l*sl)/(w+l);
			double pfa = (w*tp*1.0)/(l*sl);
			System.out.println("pos "+i
					+" "+(w+l)
					+" "+PrintUtils.Print2dec(wp, false)
					+" "+PrintUtils.Print2dec(a, false)
					+" "+PrintUtils.Print2dec(pfa, false));
		}*/
	}
	public static void testRebounds_arrayComplete(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDesviation,
			int nBars,
			int tp,
			int sl,
			int maxTrades,
			int factor,			
			boolean debug
			){
	
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		//inicializacion
		LinkedHashMap<Integer,Integer> priceLastVisit = new LinkedHashMap<Integer,Integer>();
		for (int i=0;i<=300000;i+=factor){
			priceLastVisit.put(i, -1);
		}
		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		int bes = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			//if (year<y1 || year>y2) continue;
			//if (h<h1 || h>h2) continue;
			
			
						
			//2. *********EVALUACION NUEVAS ENTRADAS DE POSICIONES DE q
			//calculo el max y min de los ultimos thr			
			int maxMin = maxMins.get(i-1).getExtra();			
			//precios de high
			int highIdx = -1;
			int diffH = -1;
			int lowIdx = -1;
			int diffL = -1;
			int valueH = -1;
			int valueL = -1;
			double desvH = -1;
			double desvL = -1;
			if (h>=h1 && h<=h2){
				if (q.getHigh5()>q1.getHigh5() && maxMin>=thr){		
					int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
					for (int p=2;p<=2;p++){
						int value = q1.getHigh5()+p*factor;
						int timeStamp = priceLastVisit.get(value);
						diffH = i-timeStamp;
						if (diffH>=thr) {
							int tpValue = value - tp*factor;
							int slValue = value + sl*factor;
							PositionType posType = PositionType.SHORT;
							if (q.getHigh5()>=value){
								if (totalOpen<maxTrades){
									PositionShort pos = new PositionShort();
									pos.setEntry(value);
									pos.setTp(tpValue);
									pos.setSl(slValue);
									pos.setPositionType(posType);
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setOpenIndex(i);
									positions.add(pos);
									totalOpen++;
								}else{
									break;
								}
							}
						}
					}				
				}else if (q.getLow5()<q1.getLow5() && maxMin<=-thr){
					int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
					for (int p=2;p<=2;p++){
						int value = q1.getLow5()-p*factor;										
						int timeStamp = priceLastVisit.get(value);
						diffL = i-timeStamp;
						if (diffL>=thr){					
							int tpValue = value + tp*factor;
							int slValue = value - sl*factor;
							PositionType posType = PositionType.LONG;
							if (q.getLow5()<=value){
								if (totalOpen<maxTrades){
									PositionShort pos = new PositionShort();
									pos.setEntry(value);
									pos.setTp(tpValue);
									pos.setSl(slValue);
									pos.setPositionType(posType);
									pos.setPositionStatus(PositionStatus.OPEN);
									pos.setOpenIndex(i);
									positions.add(pos);
									totalOpen++;
								}else{
									break;
								}
							}
						}
					}		
				}
			}//H
			
			//evaluacion posiciones
			
			int p = 0;
			while (p<positions.size()){
				PositionShort pos = positions.get(p);
				boolean closed = false;
				double ticks = 0;
				if (pos.getPositionType() == PositionType.LONG){
					if (q.getLow5()<=pos.getSl()){
						ticks = (pos.getSl()-pos.getEntry())*1.0/factor;
						closed = true;
					}else if (q.getHigh5()>=pos.getTp() && i>pos.getOpenIndex()){
						ticks = (pos.getTp()-pos.getEntry())*1.0/factor;
						closed=true;
					}else {
						double ticksPos = (q.getHigh5()-pos.getEntry())*1.0/factor;
						double ticksNeg = (pos.getEntry()-q.getLow5())*1.0/factor;
						//if (ticksPos>=0.4*tp && q.getClose5()>=pos.getEntry()+2*factor)
							//pos.setSl(pos.getEntry());
						if (ticksNeg>=0.7*sl && q.getClose5()<=pos.getEntry()-2*factor)
							pos.setTp(pos.getEntry());
					}
				}else if (pos.getPositionType() == PositionType.SHORT){
					if (q.getHigh5()>=pos.getSl()){
						ticks = (pos.getEntry()-pos.getSl())*1.0/factor;
						closed = true;
					}else if (q.getLow5()<=pos.getTp() && i>pos.getOpenIndex()){
						ticks = (pos.getEntry()-pos.getTp())*1.0/factor;
						closed=true;
					}else{
						double ticksPos = (pos.getEntry()-q.getLow5())*1.0/factor;
						double ticksNeg = (q.getHigh5()-pos.getEntry())*1.0/factor;
						//if (ticksPos>=0.4*tp && q.getClose5()<=pos.getEntry()-2*factor){
						//pos.setSl(pos.getEntry());
						//}
						if (ticksNeg>=0.7*sl && q.getClose5()>=pos.getEntry()+2*factor)
							pos.setTp(pos.getEntry());
					}
				}
				
				if (closed){
					if (ticks>=0){
						winTicks += ticks;
						wins++;
					}else{
						lostTicks += -ticks;
						losses++;
					}
					positions.remove(p);
				}else{
					p++;
				}
			}//positions
					
		}//data
		
		int cases = wins+losses;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double avgAdverse = accAdverseTicks/cases;
		double pf = winTicks/lostTicks;
		System.out.println(minDesviation
				+" "+thr
				//+" "+nBars
				+" "+tp
				+" "+sl
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				//+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void testRebounds_array(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDesviation,
			int nBars,
			int tp,
			int sl,
			int factor,
			boolean debug
			){
	
		//inicializacion
		LinkedHashMap<Integer,Integer> priceLastVisit = new LinkedHashMap<Integer,Integer>();
		for (int i=0;i<=300000;i+=factor){
			priceLastVisit.put(i, -1);
		}
		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		int bes = 0;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			if (h<h1 || h>h2) continue;
			//calculo el max y min de los ultimos thr
			
			int maxMin = maxMins.get(i-1).getExtra();
			
			//precios de high
			int highIdx = -1;
			int diffH = -1;
			int lowIdx = -1;
			int diffL = -1;
			int valueH = -1;
			int valueL = -1;
			double desvH = -1;
			double desvL = -1;
			if (q.getHigh5()>q1.getHigh5() && maxMin>=thr){				
				int value = q1.getHigh5()+1*factor;
				if (q.getHigh5()>=value){
					int timeStamp = priceLastVisit.get(value);
					diffH = i-timeStamp;
					if (diffH>=thr){
						highIdx = timeStamp;
						valueH = value;
						TradingUtils.getMaxMinShort(data, qm, cal, highIdx+1, i-1);
						desvH = (value-qm.getLow5())*1.0/factor;
					}
				}
				
			}else if (q.getLow5()<q1.getLow5() && maxMin<=-thr){
				int value = q1.getLow5()-1*factor;
				if (q.getLow5()<=value){
					int timeStamp = priceLastVisit.get(value);
					diffL = i-timeStamp;
					if (diffL>=thr){ 
						lowIdx = timeStamp;
						valueL = value;
						TradingUtils.getMaxMinShort(data, qm, cal, lowIdx+1, i-1);
						desvL = (qm.getHigh5()-value)*1.0/factor;
					}
				}
			}

			
			
			double p = 0;
			double resF = 0;
			boolean trade = false;
			if (highIdx>=0 && diffH>=thr && desvH>=minDesviation){
				int ref = valueH;
				int diffValueH = (ref-q.getHigh5())/factor;
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, i+nBars);
				//if (q.getHigh5()>=qm.getHigh5()) qm.setHigh5(q.getHigh5());
				
				p = (ref-qm.getLow5())*1.0/factor;
				resF = (ref-qm.getClose5())*1.0/factor;
				accAdverseTicks += (qm.getHigh5()-ref)*1.0/factor;
				trade = true;
				if (debug)
					System.out.println("[SHORT] "+q.getHigh5()+" "+ref+" "+" "+diffValueH+" || "+" || "+q.toString()+" || "+qm.toString()+" || "+p+" "+resF);
			}else if (lowIdx>=0  && diffL>=thr && desvL>=minDesviation){
				int ref = valueL;
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, i+nBars);
				//if (q.getLow5()<=qm.getLow5()) qm.setLow5(q.getLow5());
				
				p = (qm.getHigh5()-ref)*1.0/factor;
				resF = (qm.getClose5()-ref)*1.0/factor;
				accAdverseTicks += (ref-qm.getLow5())*1.0/factor;
				trade = true;
			}
			
			if (trade){
				if (p>=tp){
					wins++;
					winTicks += tp;
				}else{
					if (resF>0){
						wins++;
						winTicks+=resF;
					}else if (resF<0){
						losses++;
						lostTicks+=-resF;
						
					}else if (resF==0){
						bes++;
					}
					//System.out.println(wins+" "+losses+" "+resF);
				}
			}
			
			//Actualizacion de ultima visita al precio
			for (int value = q.getLow5();value<=q.getHigh5();value+=25){
				priceLastVisit.put(value, i);
			}
		}//data
		
		int cases = wins+losses+bes;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double avgAdverse = accAdverseTicks/cases;
		double pf = winTicks/lostTicks;
		System.out.println(minDesviation
				+" "+thr
				+" "+nBars
				+" "+tp
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void testRebounds_simple(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDesviation,
			int nBars,
			int tp,
			int sl,
			int factor,
			boolean debug
			){
	

		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		int bes = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			if (h<h1 || h>h2) continue;
			//calculo el max y min de los ultimos thr
			
			int maxMin = maxMins.get(i).getExtra();
			
			int highIdx0 = -1;
			int lowIdx0 = -1;
			int diffH = -1;
			int diffL = -1;
			if (maxMin>=thr){
				for (int j=i-1;j>=0;j--){
					int m = maxMins.get(j).getExtra();
					if (m>=thr){
						highIdx0 = j;
						break;
					}
				}
				diffH = i-highIdx0;
			}else if (maxMin<=-thr){
				for (int j=i-1;j>=0;j--){
					int m = maxMins.get(j).getExtra();
					if (m<=-thr){
						lowIdx0  = j;
						break;
					}
				}
				diffL = i-lowIdx0;
			}
			
			
			
			double p = 0;
			double resF = 0;
			boolean trade = false;
			if (highIdx0>=0 && diffH>=thr){
				int ref = data.get(highIdx0).getHigh5();
				int diffValueH = (ref-q.getHigh5())/factor;
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, i+nBars);
				//if (q.getHigh5()>=qm.getHigh5()) qm.setHigh5(q.getHigh5());
				
				p = (ref-qm.getLow5())*1.0/factor;
				resF = (ref-qm.getClose5())*1.0/factor;
				accAdverseTicks += (qm.getHigh5()-ref)*1.0/factor;
				trade = true;
				if (debug)
					System.out.println("[SHORT] "+q.getHigh5()+" "+ref+" "+" "+diffValueH+" || "+" || "+q.toString()+" || "+qm.toString()+" || "+p+" "+resF);
			}else if (lowIdx0>=0  && diffL>=thr){
				int ref = data.get(lowIdx0).getLow5();
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, i+nBars);
				//if (q.getLow5()<=qm.getLow5()) qm.setLow5(q.getLow5());
				
				p = (qm.getHigh5()-ref)*1.0/factor;
				resF = (qm.getClose5()-ref)*1.0/factor;
				accAdverseTicks += (ref-qm.getLow5())*1.0/factor;
				trade = true;
			}
			
			if (trade){
				if (p>=tp){
					wins++;
					winTicks += tp;
				}else{
					if (resF>0){
						wins++;
						winTicks+=resF;
					}else if (resF<0){
						losses++;
						lostTicks+=-resF;
						
					}else if (resF==0){
						bes++;
					}
					//System.out.println(wins+" "+losses+" "+resF);
				}
			}
		}//data
		
		int cases = wins+losses+bes;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double avgAdverse = accAdverseTicks/cases;
		double pf = winTicks/lostTicks;
		System.out.println(minDesviation
				+" "+thr
				+" "+nBars
				+" "+tp
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void testRebounds_aboveMean(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDesviation,
			int nBars,
			int tp,
			int sl,
			int factor,
			boolean debug
			){
	

		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		int bes = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			if (h<h1 || h>h2) continue;
			//calculo el max y min de los ultimos thr
			
			int highDesvValue = q.getHigh5()+minDesviation*factor;
			int lowDesvValue = q.getLow5()-minDesviation*factor;
			
			int highIdx0 = TradingUtils.getFirstTouch(data, i+1, i+5*thr, q.getHigh5());
			int lowIdx0 = TradingUtils.getFirstTouch(data, i+1, i+5*thr, q.getLow5());
			int highIdx = TradingUtils.getFirstTouch(data, i+1, i+5*thr, highDesvValue);
			int lowIdx = TradingUtils.getFirstTouch(data, i+1, i+5*thr, lowDesvValue);
			
			int diffH0 = highIdx0-i;
			int diffL0 = lowIdx0-i;
									
			int diffH = highIdx-i;
			int diffL = lowIdx-i;
			
			double p = 0;
			double resF = 0;
			boolean trade = false;
			if (highIdx>=0 && highIdx0>=0 && diffH0>=thr && diffH>=thr && data.get(highIdx).getHigh5()>=highDesvValue){
				int ref = highDesvValue;
				int diffValueH = (ref-q.getHigh5())/factor;
				TradingUtils.getMaxMinShort(data, qm, cal, highIdx+1, highIdx+nBars);
				//if (q.getHigh5()>=qm.getHigh5()) qm.setHigh5(q.getHigh5());
				
				p = (ref-qm.getLow5())*1.0/factor;
				resF = (ref-qm.getClose5())*1.0/factor;
				accAdverseTicks += (qm.getHigh5()-ref)*1.0/factor;
				trade = true;
				if (debug)
					System.out.println("[SHORT] "+q.getHigh5()+" "+ref+" "+diffH+" "+diffValueH+" || "+data.get(highIdx).toString()+" || "+q.toString()+" || "+qm.toString()+" || "+p+" "+resF);
			}else if (lowIdx>=0 && lowIdx0>=0 && diffL0>=thr && diffL>=thr && data.get(lowIdx).getLow5()<=lowDesvValue){
				int ref = lowDesvValue;
				TradingUtils.getMaxMinShort(data, qm, cal, lowIdx+1, lowIdx+nBars);
				//if (q.getLow5()<=qm.getLow5()) qm.setLow5(q.getLow5());
				
				p = (qm.getHigh5()-ref)*1.0/factor;
				resF = (qm.getClose5()-ref)*1.0/factor;
				accAdverseTicks += (ref-qm.getLow5())*1.0/factor;
				trade = true;
			}
			
			if (trade){
				if (p>=tp){
					wins++;
					winTicks += tp;
				}else{
					if (resF>0){
						wins++;
						winTicks+=resF;
					}else if (resF<0){
						losses++;
						lostTicks+=-resF;
						
					}else if (resF==0){
						bes++;
					}
					//System.out.println(wins+" "+losses+" "+resF);
				}
			}
		}//data
		
		int cases = wins+losses+bes;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double avgAdverse = accAdverseTicks/cases;
		double pf = winTicks/lostTicks;
		System.out.println(minDesviation
				+" "+thr
				+" "+nBars
				+" "+tp
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void testRebounds_good(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDesviation,
			int nBars,
			int tp,
			int sl,
			int factor,
			boolean debug
			){
	

		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		int bes = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			if (h<h1 || h>h2) continue;
			//calculo el max y min de los ultimos thr
			
			int highIdx = TradingUtils.getFirstTouch(data, i+1, i+5*thr, q.getHigh5());
			int lowIdx = TradingUtils.getFirstTouch(data, i+1, i+5*thr, q.getLow5());
			
						
			int diffH = highIdx-i;
			int diffL = lowIdx-i;
			
			double p = 0;
			double resF = 0;
			boolean trade = false;
			if (highIdx>=0 && diffH>=thr && data.get(highIdx).getHigh5()>=q.getHigh5()){
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, highIdx-1);
				double desv = (q.getHigh5()-qm.getLow5())/factor;
				if (desv<minDesviation) continue;
				int ref = q.getHigh5();
				int diffValueH = q.getHigh5()-ref;
				TradingUtils.getMaxMinShort(data, qm, cal, highIdx+1, highIdx+nBars);
				//if (q.getHigh5()>=qm.getHigh5()) qm.setHigh5(q.getHigh5());
				
				p = (ref-qm.getLow5())*1.0/factor;
				resF = (ref-qm.getClose5())*1.0/factor;
				accAdverseTicks += (qm.getHigh5()-ref)*1.0/factor;
				trade = true;
				if (debug)
					System.out.println("[SHORT] "+ref+" "+diffH+" "+diffValueH+" || "+data.get(highIdx).toString()+" || "+q.toString()+" || "+qm.toString()+" || "+p+" "+resF);
			}else if (lowIdx>=0 && diffL>=thr && data.get(lowIdx).getLow5()<=q.getLow5()){
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, lowIdx-1);
				double desv = (qm.getHigh5()-q.getLow5())/factor;
				if (desv<minDesviation) continue;
				int ref = q.getLow5();
				TradingUtils.getMaxMinShort(data, qm, cal, lowIdx+1, lowIdx+nBars);
				//if (q.getLow5()<=qm.getLow5()) qm.setLow5(q.getLow5());
				
				p = (qm.getHigh5()-ref)*1.0/factor;
				resF = (qm.getClose5()-ref)*1.0/factor;
				accAdverseTicks += (ref-qm.getLow5())*1.0/factor;
				trade = true;
			}
			
			if (trade){
				if (p>=tp){
					wins++;
					winTicks += tp;
				}else{
					if (resF>0){
						wins++;
						winTicks+=resF;
					}else if (resF<0){
						losses++;
						lostTicks+=-resF;
						
					}else if (resF==0){
						bes++;
					}
					//System.out.println(wins+" "+losses+" "+resF);
				}
			}
		}//data
		
		int cases = wins+losses+bes;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double avgAdverse = accAdverseTicks/cases;
		double pf = winTicks/lostTicks;
		System.out.println(minDesviation
				+" "+thr
				+" "+nBars
				+" "+tp
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void testRebounds0(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDesviation,
			int nBars,
			int tp,
			int sl,
			int factor,
			boolean debug
			){
	

		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		int bes = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			if (h<h1 || h>h2) continue;
			//calculo el max y min de los ultimos thr
			TradingUtils.getMaxMinShort(data, qm, cal,i-thr, i-1);
			int highIdx = TradingUtils.getLastTouch(data, i-10*thr-1, i-thr-1,qm.getHigh5());
			int lowIdx  = TradingUtils.getLastTouch(data, i-10*thr-1, i-thr-1,qm.getLow5());
			
			
			int diffH = i-highIdx;
			int diffL = i-lowIdx;
			
			double p = 0;
			double resF = 0;
			boolean trade = false;
			if (highIdx>=0 && diffH>=thr && q.getHigh5()>=data.get(highIdx).getHigh5()){
				int ref = data.get(highIdx).getHigh5();
				int diffValueH = q.getHigh5()-ref;
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, i+nBars);
				if (q.getHigh5()>=qm.getHigh5()) qm.setHigh5(q.getHigh5());
				
				p = (ref-qm.getLow5())*1.0/factor;
				resF = (ref-qm.getClose5())*1.0/factor;
				accAdverseTicks += (qm.getHigh5()-ref)*1.0/factor;
				trade = true;
				if (debug)
					System.out.println("[SHORT] "+ref+" "+diffH+" "+diffValueH+" || "+data.get(highIdx).toString()+" || "+q.toString()+" || "+qm.toString()+" || "+p+" "+resF);
			}else if (lowIdx>=0 && diffL>=thr && q.getLow5()<=data.get(lowIdx).getLow5()){
				int ref = data.get(lowIdx).getLow5();
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, i+nBars);
				if (q.getLow5()<=qm.getLow5()) qm.setLow5(q.getLow5());
				
				p = (qm.getHigh5()-ref)*1.0/factor;
				resF = (qm.getClose5()-ref)*1.0/factor;
				accAdverseTicks += (ref-qm.getLow5())*1.0/factor;
				trade = true;
			}
			
			if (trade){
				if (p>=tp){
					wins++;
					winTicks += tp;
				}else{
					if (resF>0){
						wins++;
						winTicks+=resF;
					}else if (resF<0){
						losses++;
						lostTicks+=-resF;
						
					}else if (resF==0){
						bes++;
					}
					//System.out.println(wins+" "+losses+" "+resF);
				}
			}
		}//data
		
		int cases = wins+losses+bes;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double avgAdverse = accAdverseTicks/cases;
		double pf = winTicks/lostTicks;
		System.out.println(minDesviation
				+" "+thr
				+" "+nBars
				+" "+tp
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void testRebounds(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int minDesviation,
			int nBarsFuture,
			int tp,
			int sl,
			int factor,
			boolean debug
			){
	

		int casesD = 0;
		int casesNoTouched = 0;
		QuoteShort qm1 = new QuoteShort();
		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double accTicks = 0;
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			int indexH = TradingUtils.getFirstTouch(data, i+1, i+50*thr, q.getHigh5());
			
			if ((indexH-i)>=thr){//indexH ha superado el umbral necesario para valer 
				QuoteShort.getCalendar(cal, data.get(indexH));
				int h = cal.get(Calendar.HOUR_OF_DAY);
				if (h<h1 || h>h2) continue;
				TradingUtils.getMaxMinShort(data, qm1, cal, i+1, indexH);				
				int desv = q.getHigh5()-qm1.getLow5();
				double ticks = desv / factor;
				if (ticks>=minDesviation){					
					TradingUtils.getMaxMinShort(data, qm, cal, indexH+1, indexH+nBarsFuture);//calculo los maximos y minimos desde el indice hacia delaten			
					int po = q.getHigh5()-qm.getLow5();
					double t = po*1.0 / factor;
					accTicks += t;
					double ticksLost = (qm.getHigh5()-q.getHigh5())*1.0/factor;
					accAdverseTicks += ticksLost;
					double ticksr = 0;					
					if (t>=tp){
						wins++;
						winTicks += tp;
						ticksr = tp;
					}
					else {
						double ticksResult = (q.getHigh5()-qm.getClose5())*1.0/factor;
						if (ticksResult>=0){
							wins++;
							winTicks += ticksResult;							
						}else{
							lostTicks += -ticksResult;
							losses++;
						}
						ticksr = ticksResult;						
					}
					
					if (debug)
					System.out.println("[SHORT] "+(indexH-i)+" "+ticks
							+" "+q.getHigh5()
							+" || "+q.toString()
							+" || "+qm1.toString()
							+" || "+data.get(indexH).toString()
							+" || "+qm.toString()
							+" || "+t
							+" || "+ticksr
							);
				}
			}else{
				int indexL = TradingUtils.getFirstTouch(data, i+1, i+50*thr, q.getLow5());
				if ((indexL-i)>=thr){
					QuoteShort.getCalendar(cal, data.get(indexL));
					int h = cal.get(Calendar.HOUR_OF_DAY);
					if (h<h1 || h>h2) continue;
					TradingUtils.getMaxMinShort(data, qm1, cal, i+1, indexL);
					int desv = qm1.getLow5()-q.getLow5();
					double ticks = desv / factor;
					if (ticks>=minDesviation){
						TradingUtils.getMaxMinShort(data, qm, cal, indexH+1, indexH+nBarsFuture);			
						int po = qm.getHigh5()-q.getLow5();
						double t  = po*1.0 / factor;
						accTicks += t;	
						double ticksLost = (q.getLow5()-qm.getLow5())*1.0/factor;
						accAdverseTicks += ticksLost;
						double ticksr = 0;
						if (t>=tp){
							wins++;
							winTicks += tp;
						}
						else {
							double ticksResult = (qm.getClose5()-q.getLow5())*1.0/factor;
							if (ticksResult>=0){
								wins++;
								winTicks += ticksResult;							
							}else{
								lostTicks += -ticksResult;
								losses++;
							}
							ticksr = ticksResult;	
						}
					}
				}
			}//thr					
		}//data
		
		int cases = wins+losses;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double avgAdverse = accAdverseTicks/cases;
		double pf = winTicks/lostTicks;
		System.out.println(minDesviation
				+" "+thr
				+" "+nBarsFuture
				+" "+tp
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void testRebounds2(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int h1,int h2,
			int thr,
			int nBars,
			int tp,
			int factor,
			boolean debug
			){
	

		int casesD = 0;
		int casesNoTouched = 0;
		QuoteShort qm1 = new QuoteShort();
		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		double accTicks = 0;
		double winTicks = 0;
		double lostTicks = 0;
		double accAdverseTicks = 0;
		int wins = 0;
		int losses = 0;
		for (int i=0;i<data.size()-1;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int maxMin = maxMins.get(i).getExtra();
			
			if (maxMin<thr && maxMin>-thr) continue;
			
			//TradingUtils.getMaxMinShort(data, qm, cal, i-50*thr, i-1);
			
			//int diffH = i - qm.getHighIdx();
			//int diffL = i - qm.getLowIdx();
			
			if (maxMin>=thr
					//&& qm.getHighIdx()>=0 
					//&& diffH>=thr 
					//&& q.getHigh5()>=qm.getHigh5()
					){
				//int ref = qm.getHigh5();
				int ref = q1.getOpen5();
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, i+nBars);
				double ticksPositive = (ref-qm.getLow5())/factor;
				double ticksRes = (ref-qm.getClose5())/factor;
				
				if (ticksPositive>=tp){
					wins++;
					winTicks += tp;
				}else{
					if (ticksRes>=0){
						wins++;
						winTicks += ticksRes;
					}else{
						losses++;
						lostTicks += -ticksRes;
					}
				}
			}else if (
					maxMin<=-thr
					//&& qm.getLowIdx()>=0 
					//&& diffL>=thr 
					//&& q.getLow5()<=qm.getLow5()
					){
				//int ref = qm.getLow5();
				int ref = q1.getOpen5();
				TradingUtils.getMaxMinShort(data, qm, cal, i+1, i+nBars);
				double ticksPositive = (qm.getHigh5()-ref)/factor;
				double ticksRes = (qm.getClose5()-ref)/factor;
				
				if (ticksPositive>=tp){
					wins++;
					winTicks += tp;
				}else{
					if (ticksRes>=0){
						wins++;
						winTicks += ticksRes;
					}else{
						losses++;
						lostTicks += -ticksRes;
					}
				}
			}
					
		}//data
		
		int cases = wins+losses;
		double winPer = wins*100.0/cases;
		double avg = (winTicks-lostTicks)/cases;
		double avgAdverse = accAdverseTicks/cases;
		double pf = winTicks/lostTicks;
		System.out.println(
				thr
				+" "+nBars
				+" "+tp
				+" || "
				+" "+cases
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(winTicks/wins, false)
				+" "+PrintUtils.Print2dec(lostTicks/losses, false)
				+" || "+PrintUtils.Print2dec(avg, false)
				+" || "+PrintUtils.Print2dec(avgAdverse, false)
				+" || "+PrintUtils.Print2dec(pf, false)
				);
	}

	public static void main(String[] args) {
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2016.05.01.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2016.05.03.csv";
		String fileNameYM = "C:\\fxdata\\YM.txt";
		String fileNameES = "C:\\fxdata\\ES.txt";
		String fileNameES2010 = "C:\\fxdata\\futuros\\ticks\\ES_2009_2016_1min.txt";
		//ArrayList<QuoteShort> dataI  = DAO.retrieveDataShort(pathEURUSD, DataProvider.DUKASCOPY_FOREX);
		//ArrayList<QuoteShort> dataS = TestLines.calculateCalendarAdjustedShort(dataI);		
		//ArrayList<QuoteShort> data 	= TradingUtils.cleanWeekendDataShort(dataS); 	 
		
		
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameYM, DataProvider.KIBOT);
		ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES, DataProvider.KIBOTES);
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES2010, DataProvider.DAVE);
		
		
		ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
		
		
		System.out.println("Data: "+data.size());
		
		
		/*for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			System.out.println(q.toString()+" "+maxMins.get(i).getExtra());
		}*/
		
		
		ArrayList<Integer> thrs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) thrs.add(0);
		thrs.set(0,300);thrs.set(1,2500);thrs.set(2,2700);
		thrs.set(3,600);thrs.set(4,2200);thrs.set(5,550);
		thrs.set(6,550);thrs.set(7,500);thrs.set(8,550);
		thrs.set(9,900);
		double comm = 1.26;
		comm = 0.0;
		for (int y1=2015;y1<=2015;y1++){
			int y2 = y1+0;
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+23;
				if (h2>=23) h2 = 23;
				for (int i=0;i<=23;i++) thrs.set(i,0);
				for (int thr=1000;thr<=100000;thr+=1000){
					for (int i=h1;i<=h2;i++){
						thrs.set(i,thr);
					}
					for (int minDesviation=0;minDesviation<=0;minDesviation+=10){	
						for (int tp=5;tp<=5;tp+=1){
							for (int sl = 100;sl<=100;sl+=tp){
								//for (int sl=20;sl<=20;sl+=1){
									//for (int tp =(int) (1.0*sl);tp<=1*sl;tp+=1*sl){
								for (int nbars=0;nbars<=0;nbars++){
									for (int maxTrades=5000;maxTrades<=5000;maxTrades++){
										//para forex
										DOWES.testRebounds_classic(data, maxMins,thrs,y1,y2,minDesviation, nbars,tp,sl,maxTrades,25,1.0,comm,false);
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
