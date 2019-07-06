package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.PriceTestResult;
import drosa.phil.TestLines;
import drosa.phil.strategy.StrategyResult;
import drosa.phil.strategy.TestStrategy;
import drosa.strategies.auxiliar.Position;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class SuperStrategy {
	
	
	
	private static String convertTradeSequence(String tradesStr) {
		// TODO Auto-generated method stub
		int wins=0;
		int losses=0;
		String seq="";
		for (int i=0; i<tradesStr.length();i++){
			char val = tradesStr.charAt(i);
			if (val=='W'){
				wins++;
				if (losses>0) seq+="-"+String.valueOf(losses)+",";
				losses=0;
			}
			if (val=='L'){
				losses++;
				if (wins>0) seq+=String.valueOf(wins)+",";
				wins=0;
			}
		}
		if (wins>0) seq+=String.valueOf(wins)+",";
		if (losses>0) seq+="-"+String.valueOf(losses)+",";
		if (seq.trim().length()>0){
			return seq.substring(0, seq.trim().length()-1);
		}
		return seq;
	}

	private static int calculateOpenPositions(
			ArrayList<PriceTestResult> resArray) {
		// TODO Auto-generated method stub
		ArrayList<Integer> totalOpens = new ArrayList<Integer>();
		for (int i=0;i<=100;i++) totalOpens.add(0);
		
		ArrayList<Calendar> dates    = new ArrayList<Calendar>();
		ArrayList<Integer> indexes   = new ArrayList<Integer>();
		ArrayList<Integer> openClose = new ArrayList<Integer>();
		
		
		for (int i=0;i<resArray.size();i++){
			PriceTestResult res = resArray.get(i);
			int index = insertDate(dates,res.getCal());//open
			if (index>openClose.size()-1){
				openClose.add(1);
				indexes.add(res.getIndex());
			}
			else{
				openClose.add(index, 1);
				indexes.add(index,res.getIndex());
			}
			//System.out.println("index open: "+index+" "+res.getIndex());
			index = insertDate(dates,res.getCloseTime());//cierre
			if (index>openClose.size()-1){
				openClose.add(0);
				indexes.add(res.getIndex());
			}
			else{
				openClose.add(index, 0);
				indexes.add(index,res.getIndex());
			}
			//System.out.println("index close: "+index+" "+DateUtils.datePrint(res.getCloseTime()));
		}
		
		int maxOpen = 0;
		int actual = 0;
		for (int i=0;i<openClose.size();i++){
			int val = openClose.get(i);
			int index = indexes.get(i);
			if (val==1){
				actual++;
				//System.out.println("OPEN "+index+" totalOpen= "+actual);
			}
			if (val==0){
				actual--;
				//System.out.println("CLOSE "+index+" totalOpen= "+actual);
			}
			//System.out.println(actual);
			if (actual>maxOpen){
				//System.out.println(actual);
				maxOpen = actual;
			}
			for (int j=0;j<=actual;j++){//mayores
				int opens = totalOpens.get(j);
				totalOpens.set(j, opens+1);
			}
		}
		String percs = "";
		for (int i=0;i<=20;i++){
			double per = totalOpens.get(i)*100.0/openClose.size();
			percs+=" "+i+"="+PrintUtils.Print2(per);
		}
		//System.out.println(percs);
		return maxOpen;
	}

	private static int insertDate(ArrayList<Calendar> dates, Calendar newCal) {
		// TODO Auto-generated method stub
		
		for (int i=0;i<dates.size();i++){
			Calendar cal = dates.get(i);
			if (newCal.getTimeInMillis()<cal.getTimeInMillis()){
				dates.add(i, newCal);
				return i;
			}
		}
		dates.add(newCal);
		return dates.size()-1;
	}
	
	private static StrategyResultEx calculateFinalBalance(String[] strs,
			ArrayList<Position> seqPosition, double actualBalance, double riskTrade, double tp,double sl, double comm,boolean debug) {
		// TODO Auto-generated method stub
		StrategyResultEx res = new StrategyResultEx();
		double finalBalance = actualBalance;
		double grossProfit = 0;
		double grossLoss = 0;
		for (int i=0 ;i<seqPosition.size();i++){
			Position pos = seqPosition.get(i);
			double slPips = pos.getStopLoss();
			double commPer = (comm*riskTrade)/slPips;
			double pipRisk = riskTrade/slPips;
			int pipsEarned = (int) pos.getPips();
			double amount = -(finalBalance*(riskTrade+commPer)/100.0);
			//if (debug)
				//System.out.println("Positions pips earned actualBalance: "+PrintUtils.Print2(pipsEarned));
			if (pipsEarned>=0){
				amount = finalBalance*(pipRisk * (pipsEarned-comm))/100.0;
				grossProfit += amount;
			}else{
				grossLoss -= amount;
			}
			if (debug)
			System.out.println("commPer slPips pipRisk pipsEarned= "
					+" "+PrintUtils.Print2(commPer)
					+" "+PrintUtils.Print2(slPips)
					+" "+PrintUtils.Print2(pipRisk)
					+" "+PrintUtils.Print2(pipsEarned)
					+" "+PrintUtils.Print2(finalBalance)
					+" "+PrintUtils.Print2(amount)
					);
			
			finalBalance += amount;
			//System.out.println("Positions pips earned actualBalance: "+i+" "+DateUtils.datePrint(pos.getOpenDate())+" "+PrintUtils.Print2(pipsEarned)+" "+PrintUtils.Print2(finalBalance));
		}
		
		res.setProfitFactor(grossProfit*1.0/grossLoss);
		res.setFinalCapital(finalBalance);
		
		/*if (tp>=2){
			double slComm = sl+comm;
			double tpComm = tp-comm;
			double pipRisk = riskTrade*1.0/slComm;
			double tpProfit = pipRisk*tpComm;
			finalBalance = actualBalance;
			for (int i=0;i<strs.length;i++){
		           //System.out.println(strs[i]);
				if (strs[i].trim()!=""){
		           double seqi = Integer.valueOf(strs[i].trim());				  		           
		           if (seqi>0){
		        	   finalBalance = finalBalance*Math.pow(1+tpProfit/100,seqi);
		           }else{
		        	   finalBalance = finalBalance*Math.pow(1-riskTrade/100,Math.abs(seqi));
		           }
				}
			}
		}else{			
			for (int i=0 ;i<seqPosition.size();i++){
				Position pos = seqPosition.get(i);
				double slPips = pos.getStopLoss();
				double commPer = (comm*riskTrade)/slPips;
				double pipRisk = riskTrade/slPips;
				int pipsEarned = (int) pos.getPips();
				double amount = -(finalBalance*(riskTrade+commPer)/100.0);
				if (debug)
					System.out.println("Positions pips earned actualBalance: "+PrintUtils.Print2(pipsEarned));
				if (pipsEarned>=0)
					amount = finalBalance*(pipRisk * (pipsEarned-comm))/100.0;
				if (debug)
				System.out.println("commPer slPips pipRisk pipsEarned= "
						+" "+PrintUtils.Print2(commPer)
						+" "+PrintUtils.Print2(slPips)
						+" "+PrintUtils.Print2(pipRisk)
						+" "+PrintUtils.Print2(pipsEarned)
						+" "+PrintUtils.Print2(finalBalance)
						+" "+PrintUtils.Print2(amount)
						);
				
				finalBalance += amount;
				//System.out.println("Positions pips earned actualBalance: "+i+" "+DateUtils.datePrint(pos.getOpenDate())+" "+PrintUtils.Print2(pipsEarned)+" "+PrintUtils.Print2(finalBalance));
			}
		}*/
		
		return res;
	}

	
	
	private static String getHoursEnabled(ArrayList<Integer> allowedHours,int inc) {
		// TODO Auto-generated method stub
		String hStr ="";
		for (int i=0;i<allowedHours.size();i+=inc){
			if (allowedHours.get(i)==1)
				hStr+=i+" ";
		}
		
		return hStr;
	}
	
	public static ArrayList<Quote> calculateMaxMinByDay(ArrayList<Quote> data){
		ArrayList<Quote> maxMin = new ArrayList<Quote>();
		
		Calendar cal = Calendar.getInstance();
		double minActual = 9999;
		double maxActual = 0;
		int lastDay = -1;
		Quote qNew = null;
		for (int i = 0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//inicializamos cuota
			qNew = new Quote();
			qNew.getDate().setTime(q.getDate().getTime());
			qNew.setIndex(i);
			qNew.setExtra(0);
			maxMin.add(qNew);//lo metemos
			if (day!=lastDay){ //cada dia reseteamos
				minActual = 9999;
				maxActual = 0;
				lastDay = day;
			}
			//vemos si es un máximo o mínimo actual
			if (q.getHigh()>maxActual){
				qNew.setExtra(1);
				maxActual = q.getHigh();
			}
			if (q.getLow()<minActual){
				qNew.setExtra(-1);
				minActual = q.getLow();
			}
			
		}
		
		return maxMin;
	}
	
	public static ArrayList<Quote> calculateMaxMinByBar(ArrayList<Quote> data,int lookBack){
		ArrayList<Quote> maxMin = new ArrayList<Quote>();
		
		Calendar cal = Calendar.getInstance();
		double minActual = 9999;
		double maxActual = 0;
		int lastDay = -1;
		Quote qNew = null;
		for (int i = 0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			Quote q0 = TradingUtils.getMaxMin(data,i-lookBack,i-1);
			
			//inicializamos cuota
			qNew = new Quote();
			qNew.getDate().setTime(q.getDate().getTime());
			qNew.setIndex(i);
			qNew.setExtra(0);
			maxMin.add(qNew);//lo metemos
			
			if (i>0)
			if (TradingUtils.getPipsDiff(q.getHigh(),q0.getHigh())>=1){
				qNew.setExtra(1);
			}else if (TradingUtils.getPipsDiff(q0.getLow(),q.getLow())>=1){
				qNew.setExtra(-1);
			}
		}
		
		return maxMin;
	}
	
	
	
	public static ArrayList<MaxMinResult> calculateMaxMinPerformance(ArrayList<Quote> data,ArrayList<Quote> maxMin,
			int begin, int end,
			double tp1,double tp2,double incTp,
			double sl1,double sl2,double incSl,
			boolean breakout
			){
		
		ArrayList<MaxMinResult> mmArray = new ArrayList<MaxMinResult>();
		PriceTestResult res = new PriceTestResult();
		int beginF = begin;
		if (begin<0) beginF = 0;
		int endF = end;
		if (end>maxMin.size()-2){
			endF = maxMin.size()-2;
		}
		int next = beginF+50000;
		for (int i=beginF;i<=endF;i++){
			Quote maxMinQ = maxMin.get(i);
			
			if (maxMinQ.getExtra() == 1){ //es un maximo
				for (double tp = tp1;tp<=tp2;tp+=incTp){
					for (double sl = sl1;sl<=sl2;sl+=incSl){
						if (i>=next){
						/*System.out.println("[calculateMaxMinPerformance] tp sl "
								+i+" "+endF+" "+PrintUtils.Print2dec(tp, false)+" "+PrintUtils.Print2dec(sl, false));*/
							next +=50000;
						}
						Quote first = data.get(i+1);
						double entryValue = first.getOpen();
						double slValue = entryValue+0.0001*sl;
						double tpValue = entryValue-0.0001*tp;
						int mode = 0;
						if (breakout){
							slValue = entryValue-0.0001*sl;
							tpValue = entryValue+0.0001*tp;
							mode = 1;
						}
						TradingUtils.testPriceMovement(res,data, i+1, endF, entryValue,slValue,tpValue,mode,false);
						MaxMinResult mm = new MaxMinResult();
						mm.getCal().setTimeInMillis(data.get(i).getDate().getTime());
						mm.getCloseTime().setTimeInMillis(res.getCloseTime().getTimeInMillis());
						
						mm.setMaxMin(maxMinQ.getExtra());
						mm.setSl(sl);
						mm.setTp(tp);
						mm.setWin(res.getWin());
						mmArray.add(mm);
						/*System.out.println("entry sl tp = "
								+" "+PrintUtils.Print4dec(entryValue)
								+" "+PrintUtils.Print4dec(slValue)
								+" "+PrintUtils.Print4dec(tpValue)
								+" "+mm.toString()
								);*/
						/*System.out.println(i
								+" open= "+DateUtils.datePrint(data.get(i).getDate())
								+" close= "+DateUtils.datePrint(data.get(i).getDate())
								+" "+maxMinQ.getExtra()
								+" "+PrintUtils.Print2(tp)
								+" "+PrintUtils.Print2(sl)
								+" "+res.isWin()
								);*/
						
					}
				}				
			}
			
			if (maxMinQ.getExtra() == -1){ //es un mínimo
				for (double tp = tp1;tp<=tp2;tp+=incTp){
					for (double sl = sl1;sl<=sl2;sl+=incSl){
						Quote first = data.get(i+1);
						double entryValue = first.getOpen();
						double slValue = entryValue-0.0001*sl;
						double tpValue = entryValue+0.0001*tp;
						int mode = 1;
						if (breakout){
							slValue = entryValue+0.0001*sl;
							tpValue = entryValue-0.0001*tp;
							mode = 0;
						}
						
						TradingUtils.testPriceMovement(res,data, i+1, endF, entryValue,slValue,tpValue,mode,false);
						MaxMinResult mm = new MaxMinResult();
						mm.getCal().setTimeInMillis(data.get(i).getDate().getTime());
						mm.getCloseTime().setTimeInMillis(res.getCloseTime().getTimeInMillis());
						mm.setMaxMin(maxMinQ.getExtra());
						mm.setSl(sl);
						mm.setTp(tp);
						mm.setWin(res.getWin());
						mmArray.add(mm);
						/*System.out.println("entry sl tp = "
								+" "+PrintUtils.Print4dec(entryValue)
								+" "+PrintUtils.Print4dec(slValue)
								+" "+PrintUtils.Print4dec(tpValue)
								+" "+mm.toString()
								);*/
						/*System.out.println(i
								+" open= "+DateUtils.datePrint(data.get(i).getDate())
								+" close= "+DateUtils.datePrint(data.get(i).getDate())
								+" "+maxMinQ.getExtra()
								+" "+PrintUtils.Print2(tp)
								+" "+PrintUtils.Print2(sl)
								+" "+res.isWin()
								);*/
					}
				}	
			}
		}
		return mmArray;
	}
	
	

	static int calculateConcurrent(ArrayList<MaxMinResult> mmArray,
			ArrayList<Integer> allowedHours,ArrayList<Integer> allowedMinutes,
			ArrayList<Integer> allowedMonths,
			double tp, double sl) {
		// TODO Auto-generated method stub
		int allowedH = 0;
		int allowedM = 0;
		int allowedMn = 0;
		ArrayList<PriceTestResult> resArray = new ArrayList<PriceTestResult>();
		for (int i=0;i<mmArray.size();i++){
			MaxMinResult mm = mmArray.get(i);
			double mmTP = mm.getTp();
			double mmSL = mm.getSl();
			if (allowedHours==null) allowedH=1;
			else allowedH = allowedHours.get(mm.getCal().get(Calendar.HOUR_OF_DAY));
			if (allowedMinutes==null) allowedM=1;
			else allowedM = allowedMinutes.get(mm.getCal().get(Calendar.MINUTE));
			if ( allowedMonths==null) allowedMn=1;
			else allowedMn = allowedMinutes.get(mm.getCal().get(Calendar.MONTH));
												
			if (allowedH==0 || allowedM==0 || allowedMn==0){ //si la hora no está permitida				
				continue;
			}
			
			if (Math.abs(mmTP-tp)<=0.001 && Math.abs(mmSL-sl)<=0.001){	
				PriceTestResult r = new PriceTestResult();
				r.getCal().setTimeInMillis(mm.getCal().getTimeInMillis());
				r.getCloseTime().setTimeInMillis(mm.getCloseTime().getTimeInMillis());
				r.setIndex(mm.getIndex());
				//System.out.println("añadiendo open y close: "
				//		+DateUtils.datePrint(mm.getCal())+" "+DateUtils.datePrint(mm.getCloseTime()));
				resArray.add(r);
			}
		}
		
		return SuperStrategy.calculateOpenPositions(resArray);
	}
		
	
	public static StrategyResultEx calculateStatsMin(ArrayList<MaxMinResult> mmArray,
			ArrayList<Integer> allowedHours,ArrayList<Integer> allowedMinutes,
			ArrayList<Integer> allowedDays,ArrayList<Integer> allowedMonths,
			double initialCapital,double tp,double sl,double comm,double maxRisk,
			boolean concurrent){
		StrategyResultEx strat = new StrategyResultEx();
		
		int tw = 0;
		int tl = 0;
		int ns = 0;
		Calendar lastClose = Calendar.getInstance();
		lastClose.set(1990,Calendar.JANUARY, 1, 0, 0);
		for (int i=0;i<mmArray.size();i++){
			MaxMinResult mm = mmArray.get(i);
			Calendar openCal = mm.getCal();
			Calendar closeCal = mm.getCloseTime();
			int isWin = mm.getWin();
			//check hour and sl and tp
			double mmTP = mm.getTp();
			double mmSL = mm.getSl();
			
			int dayWeek = mm.getCal().get(Calendar.DAY_OF_WEEK);			
			int allowedD = isDayAllowed(allowedDays,dayWeek);
			int allowedH = allowedHours.get(mm.getCal().get(Calendar.HOUR_OF_DAY));
			int allowedM = allowedMinutes.get(mm.getCal().get(Calendar.MINUTE));
			int allowedMn = allowedMonths.get(mm.getCal().get(Calendar.MONTH));
			//System.out.println(allowedD +" "+allowedH+" "+allowedM+" "+mm.getCal().get(Calendar.MINUTE));
			if (allowedH==0 || allowedM==0 ||allowedD==0 || allowedMn==0){ //si la hora no está permitida				
				continue;
			}
	
			if (Math.abs(mmTP-tp)<=0.001 && Math.abs(mmSL-sl)<=0.001){
				//System.out.println("new Open lastClose "+DateUtils.datePrint(openCal)+" "+DateUtils.datePrint(lastClose));
				if (!concurrent && openCal.getTimeInMillis()<lastClose.getTimeInMillis()){
					//System.out.println("avoided");
					continue;
				}
				//trade taken
				//System.out.println("TRADE "+mm.toString());
				if (isWin==1) tw++;
				else if (isWin==-1) tl++;
				else if (isWin==0) ns++;
				lastClose.setTimeInMillis(closeCal.getTimeInMillis());
				//System.out.println("new lastClose  "+DateUtils.datePrint(lastClose));
			}
		}
		
		double tpComm = tp-comm;
		double slComm = sl+comm;
		double winPer = tw*1.0/(tw+tl);
		double lossPer = 1.0-winPer;
		/*System.out.println(PrintUtils.Print2(winPer)
				+" "+PrintUtils.Print2(lossPer)+" "+PrintUtils.Print2(tpComm)
				+" "+PrintUtils.Print2(slComm)
				);*/
		double exp = tpComm*winPer-slComm*lossPer;
		double kelly = exp*100.0/tp;
		//System.out.println(PrintUtils.Print2(exp)+" "+PrintUtils.Print2(pipValue));
		
		strat.setTotalTrades(tw+tl);
		strat.setTotalWins(tw);
		strat.setTotalLosses(tl);
		strat.setTotalDontKnow(ns);
		strat.setPercentWin(tw*100.0/(tw+tl));
		strat.setProfitFactor((tw*tpComm)/(tl*slComm));
		//if (tl==0) strat.setProfitFactor(
		strat.setExpectancy(exp);
		strat.setKelly(exp*100.0/tp);
		
		if (exp*100.0<0){
			strat.setKelly(0);
			kelly = 0;
		}
		
		double pipValue = kelly / slComm;	
		strat.setTestRisk(kelly);
		if (kelly>maxRisk){
			pipValue = maxRisk /slComm;
			strat.setTestRisk(maxRisk);
		}
		double finalCapital = initialCapital*Math.pow(1+(pipValue*exp)/100,tw+tl);
		//System.out.println((pipValue*exp)/100);
		strat.setFinalCapital(finalCapital);
		strat.setTradeExpectancyTest(pipValue*exp);
		
		return strat;
	}
	
	private static int isDayAllowed(ArrayList<Integer> allowedDays,
			int dayWeek) {
		// TODO Auto-generated method stub
		
		if (dayWeek==Calendar.MONDAY && allowedDays.get(0)==1) return 1;
		if (dayWeek==Calendar.TUESDAY && allowedDays.get(1)==1) return 1;
		if (dayWeek==Calendar.WEDNESDAY && allowedDays.get(2)==1) return 1;
		if (dayWeek==Calendar.THURSDAY && allowedDays.get(3)==1) return 1;
		if (dayWeek==Calendar.FRIDAY && allowedDays.get(4)==1) return 1;
		
		return 0;
	}

	public static StrategyResultEx calculateStatsReal(ArrayList<MaxMinResult> mmArray,
			ArrayList<Integer> allowedHours,ArrayList<Integer> allowedMinutes,
			ArrayList<Integer> allowedDays,ArrayList<Integer> allowedMonths,
			double initialCapital,double tp,double sl,double comm,double maxRisk,
			boolean concurrent,boolean debug,boolean onlyBalanceDebug){
		StrategyResultEx strat = new StrategyResultEx();
		
		int tw = 0;
		int tl = 0;
		double tpComm = tp-comm;
		double slComm = sl+comm;
		double pipValue = maxRisk / slComm;	
		Calendar lastClose = Calendar.getInstance();
		lastClose.set(1990,Calendar.JANUARY, 1, 0, 0);
		double actualBalance = initialCapital;
		for (int i=0;i<mmArray.size();i++){
			MaxMinResult mm = mmArray.get(i);
			Calendar openCal = mm.getCal();
			Calendar closeCal = mm.getCloseTime();
			int isWin = mm.getWin();
			//check hour and sl and tp
			double mmTP = mm.getTp();
			double mmSL = mm.getSl();
			
			int dayWeek = mm.getCal().get(Calendar.DAY_OF_WEEK);			
			int allowedD = isDayAllowed(allowedDays,dayWeek);
			int allowedH = allowedHours.get(mm.getCal().get(Calendar.HOUR_OF_DAY));
			int allowedM = allowedMinutes.get(mm.getCal().get(Calendar.MINUTE));
			int allowedMn = allowedMinutes.get(mm.getCal().get(Calendar.MONTH));
			//System.out.println(allowedD +" "+allowedH+" "+allowedM);
			if (allowedH==0 || allowedM==0 || allowedD==0 || allowedMn==0){ //si la hora no está permitida				
				continue;
			}
			
			if (Math.abs(mmTP-tp)<=0.001 && Math.abs(mmSL-sl)<=0.001){
				//System.out.println("new Open lastClose "+DateUtils.datePrint(openCal)+" "+DateUtils.datePrint(lastClose));
				if (!concurrent && openCal.getTimeInMillis()<lastClose.getTimeInMillis()){
					//System.out.println("avoided");
					continue;
				}
				
				double actualRisk = actualBalance*maxRisk/100.0;
				pipValue = actualRisk / slComm; //$$per pip
				int numMicroLots = (int) (pipValue / 0.1);
				if (numMicroLots<1){
					if (debug)
					System.out.println("MICROLOTS < 1 ->> NECESARIO METER actulRisk pipValue "
							+PrintUtils.Print2(actualRisk)+" "+PrintUtils.Print2(pipValue));
				}
				//trade taken
				if (isWin==1){
					actualBalance+=(numMicroLots*tpComm*0.1);
					if (debug)
					System.out.println("WIN numMicroLots actualBalance : "+numMicroLots+" "+PrintUtils.Print2(actualBalance));
					tw++;
				}
				else if (isWin==-1){
					tl++;
					actualBalance-=(numMicroLots*slComm*0.1);
					if (debug)
					System.out.println("LOST numMicroLots actualBalance : "+numMicroLots+" "+PrintUtils.Print2(actualBalance));
				}
				lastClose.setTimeInMillis(closeCal.getTimeInMillis());
				//System.out.println("new lastClose  "+DateUtils.datePrint(lastClose));
				if (onlyBalanceDebug)
					System.out.println(PrintUtils.Print2(actualBalance,true));
			}
		}
		

		/*double winPer = tw*1.0/(tw+tl);
		double lossPer = 1.0-winPer;
		
		double exp = tpComm*winPer-slComm*lossPer;
		double kelly = exp*100.0/tp;
		
		strat.setTotalTrades(tw+tl);
		strat.setTotalWins(tw);
		strat.setTotalLosses(tl);
		strat.setPercentWin(tw*100.0/(tw+tl));
		strat.setProfitFactor((tw*tpComm)/(tl*slComm));
		strat.setExpectancy(exp);
		strat.setKelly(exp*100.0/tp);
		
		if (exp*100.0<0){
			strat.setKelly(0);
			kelly = 0;
		}
		
		double pipValue = kelly / slComm;	
		strat.setTestRisk(kelly);
		if (kelly>maxRisk){
			pipValue = maxRisk /slComm;
			strat.setTestRisk(maxRisk);
		}
		double finalCapital = initialCapital*Math.pow(1+(pipValue*exp)/100,tw+tl);
		//System.out.println((pipValue*exp)/100);*/
		//strat.setTradeExpectancyTest(pipValue*exp);
		strat.setFinalCapital(actualBalance);
		
		return strat;
	}
	

	public static void convertAllowedTime(ArrayList<Integer> allowedTime,
			String valueStr, int begin, int end,int def) {
		// TODO Auto-generated method stub
		for (int i =begin;i<=end;i+=1) allowedTime.set(i,def);
		int def2=0;
		if (def==0) def2=1;
		
		String[] values = valueStr.trim().split(" ");
		
		for (int i=0;i<values.length;i++){
			try{
				int v = Integer.valueOf(values[i]);
				if (v>=begin && v<=end)
					allowedTime.set(v, def2);
			}catch(Exception e){
				
			}
		}
		
	}
	
	public static void convertAllowedTime(ArrayList<Integer> allowedTime,
			String valueStr, int begin, int end,int value,int def) {
		// TODO Auto-generated method stub
		for (int i =begin;i<=end;i+=1) allowedTime.set(i,def);
		int def2=value;
		
		String[] values = valueStr.trim().split(" ");
		
		for (int i=0;i<values.length;i++){
			try{
				int v = Integer.valueOf(values[i]);
				if (v>=begin && v<=end)
					allowedTime.set(v, def2);
			}catch(Exception e){
				
			}
		}
		
	}
	
	public static void convertAllowedTime(ArrayList<Double> allowedTime,
			String valueStr, int begin, int end,double value,double def) {
		// TODO Auto-generated method stub
		for (int i =begin;i<=end;i+=1) allowedTime.set(i,def);
		double def2=value;
		
		String[] values = valueStr.trim().split(" ");
		
		for (int i=0;i<values.length;i++){
			try{
				int v = Integer.valueOf(values[i]);
				if (v>=begin && v<=end)
					allowedTime.set(v, def2);
			}catch(Exception e){
				
			}
		}
		
	}
	
		
	public static void optimization5m(ArrayList<Quote> data,ArrayList<Quote> maxMin,
			double tp1,double tp2,double tpInc,double sl1,double sl2,double slInc,
			int begin,int end,int lookBack,boolean breakout,boolean debug){
				
		double initialCapital = 200.0;
		double comm = 1.35;
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(1);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(0);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=5;i++) allowedDays.add(1);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedDays.add(1);
  		for (int m=0;m<=59;m+=5){ //para cada 5 min testeo
			convertAllowedTime(allowedMinutes,String.valueOf(m),0,59,0);
			double avgExpTest = 0;
			int total = 0;
			int totalPos = 0;
			int totalPeriods = 0;
			int totalTrades = 0;
			double avgPfs = 0;				

			ArrayList<MaxMinResult> mmArray = calculateMaxMinPerformance(data, maxMin,begin,end,tp1,tp2,tpInc,sl1,sl2,slInc,breakout);		  			
			//System.out.println("size mmArray: "+mmArray.size());
			for (double tp = tp1;tp<=tp2;tp+=tpInc){
				for (double sl = sl1;sl<=sl2;sl+=slInc){
					int maxConcurrent = calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					double maxRisk = 100.0/maxConcurrent;
					StrategyResultEx res0 = calculateStatsMin( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true);
					StrategyResultEx res1 = calculateStatsReal( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true,false,false);
	  				avgExpTest += res0.getTradeExpectancyTest();
	  				total++;
	  				if (res0.getTradeExpectancyTest()>0) totalPos++;
	  				totalPeriods++;
	  				avgPfs+=res0.getProfitFactor();
	  				totalTrades += res0.getTotalTrades();
	  				if (debug)
	  					System.out.println(""
	  							+" begin= "+DateUtils.datePrint(mmArray.get(0).getCal())
	  			  				//+" h= "+getHoursEnabled(allowedHours,1)
	  			  				+" min= "+getHoursEnabled(allowedMinutes,5)
	  			  				+" tp= "+tp
	  			  				+" sl= "+sl
	  			  				+" tt = "+res0.getTotalTrades()
	  			  				+" tdn = "+res0.getTotalDontKnow()
	  			  				+" maxC = "+maxConcurrent
	  			  				+" w% = "+PrintUtils.Print2dec(res0.getPercentWin(), false,3)  			  				
	  			  				+" exp = "+PrintUtils.Print2dec(res0.getExpectancy(), false,2)
	  			  				+" kellyC = "+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  			  				+" pf = "+PrintUtils.Print2dec(res0.getProfitFactor(), false,3)
	  			  				+" testRisk = "+PrintUtils.Print2dec(res0.getTestRisk(), false,3)
	  			  				+" tradeExpTest = "+PrintUtils.Print2dec(res0.getTradeExpectancyTest(), false,3)
	  			  				+" finalCapital = "+PrintUtils.Print2dec2(res0.getFinalCapital(), true)  
	  			  				+" finalCapital(semireal) = "+PrintUtils.Print2dec2(res1.getFinalCapital(), true)  
	  			  				);	
				}//sl
			}//tp
			/*System.out.println(" min= "+m
						+" totaltrades= "+totalTrades
						+" avgPfs= "+PrintUtils.Print2dec(avgPfs*1.0/total, false,3)
		  				+" avgExp= "+PrintUtils.Print2dec(avgExpTest*1.0/total, false,3)
		  				+" pos= "+PrintUtils.Print2dec(totalPos*1.0/totalPeriods,false,3)	
		  				);*/
		  				
		}//m		
	}
	
	public static StrategyResultEx optimization1h(String header,ArrayList<Quote> data,ArrayList<Quote> maxMin,int h1,int h2,
			double tp1,double tp2,double tpInc,double sl1,double sl2,double slInc,
			int begin,int end,int lookBack,int barLookBack,boolean breakout,int debug){
				
		double initialCapital = 200.0;
		double comm = 0;
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(1);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=5;i++) allowedDays.add(1);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedDays.add(1);
  		for (int h=h1;h<=h2;h+=1){ //para cada 5 min testeo
			convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
			//convertAllowedTime(allowedHours,"0 1 2 3 4 5 6 7 8 9",0,23,0);
			//convertAllowedTime(allowedHours,"0 4 5 6 7 8 9 23",0,23,0);
			//convertAllowedTime(allowedMinutes,"0",0,59,1);
			double avgExpTest = 0;
			int total = 0;
			int totalPos = 0;
			int totalPeriods = 0;
			int totalTrades = 0;
			double avgPfs = 0;				

			ArrayList<MaxMinResult> mmArray = calculateMaxMinPerformance(data, maxMin,begin,end,tp1,tp2,tpInc,sl1,sl2,slInc,breakout);		  			
			//System.out.println("size mmArray: "+mmArray.size());
			for (double tp = tp1;tp<=tp2;tp+=tpInc){
				for (double sl = sl1;sl<=sl2;sl+=slInc){
					int maxConcurrent = calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					double maxRisk = 100.0/maxConcurrent;
					StrategyResultEx res0 = calculateStatsMin( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true);
					StrategyResultEx res1 = calculateStatsReal( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true,false,false);
	  				avgExpTest += res0.getTradeExpectancyTest();
	  				total++;
	  				if (res0.getTradeExpectancyTest()>0) totalPos++;
	  				totalPeriods++;
	  				avgPfs+=res0.getProfitFactor();
	  				totalTrades += res0.getTotalTrades();
	  				if (debug==1)
	  					System.out.println(""
	  							+" header= "+header
	  							+" begin= "+DateUtils.datePrint(mmArray.get(0).getCal())
	  			  				+" h= "+getHoursEnabled(allowedHours,1)
	  			  				//+" min= "+getHoursEnabled(allowedMinutes,5)
	  			  				+" tp= "+tp
	  			  				+" sl= "+sl
	  			  				+" tt = "+res0.getTotalTrades()
	  			  				+" tdn = "+res0.getTotalDontKnow()
	  			  				+" maxC = "+maxConcurrent
	  			  				+" w% = "+PrintUtils.Print2dec(res0.getPercentWin(), false,3)  			  				
	  			  				+" exp = "+PrintUtils.Print2dec(res0.getExpectancy(), false,2)
	  			  				+" kellyC = "+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  			  				+" pf = "+PrintUtils.Print2dec(res0.getProfitFactor(), false,3)
	  			  				+" testRisk = "+PrintUtils.Print2dec(res0.getTestRisk(), false,3)
	  			  				+" tradeExpTest = "+PrintUtils.Print2dec(res0.getTradeExpectancyTest(), false,3)
	  			  				+" finalCapital = "+PrintUtils.Print2dec2(res0.getFinalCapital(), true)  
	  			  				+" finalCapital(semireal) = "+PrintUtils.Print2dec2(res1.getFinalCapital(), true)  
	  			  				);	
	  				if (debug==2){
	  					System.out.println(barLookBack+","+res0.getTotalTrades()+","+PrintUtils.Print2dec(res0.getExpectancy(), false,3));
	  				}
	  				if (debug==5)
	  					return res0;
				}//sl
			}//tp
		}//m	
  		return null;
	}
	
	public static StrategyResultEx optimization1hMonthFilter(String header,ArrayList<Quote> data,ArrayList<Quote> maxMin,int h1,int h2,
			int mn1,double tp1,double tp2,double tpInc,double sl1,double sl2,double slInc,
			int begin,int end,int lookBack,int barLookBack,boolean breakout,int debug){
				
		double initialCapital = 200.0;
		double comm = 1.4;
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(1);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=5;i++) allowedDays.add(1);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedMonths.add(1);
  		//convertAllowedTime(allowedMonths,String.valueOf(mn1),0,11,0);
  		for (int h=h1;h<=h2;h+=1){ //para cada 5 min testeo
			//convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
			//convertAllowedTime(allowedHours,"0 1 2 3 4 5 6 7 8 9",0,23,0);
			convertAllowedTime(allowedHours,"17",0,23,0);
			//convertAllowedTime(allowedMinutes,"0",0,59,1);
			double avgExpTest = 0;
			int total = 0;
			int totalPos = 0;
			int totalPeriods = 0;
			int totalTrades = 0;
			double avgPfs = 0;				

			ArrayList<MaxMinResult> mmArray = calculateMaxMinPerformance(data, maxMin,begin,end,tp1,tp2,tpInc,sl1,sl2,slInc,breakout);		  			
			//System.out.println("size mmArray: "+mmArray.size());
			for (double tp = tp1;tp<=tp2;tp+=tpInc){
				for (double sl = sl1;sl<=sl2;sl+=slInc){
					int maxConcurrent = calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					double maxRisk = 100.0/maxConcurrent;
					StrategyResultEx res0 = calculateStatsMin( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true);
					StrategyResultEx res1 = calculateStatsReal( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true,false,false);
	  				avgExpTest += res0.getTradeExpectancyTest();
	  				total++;
	  				if (res0.getTradeExpectancyTest()>0) totalPos++;
	  				totalPeriods++;
	  				avgPfs+=res0.getProfitFactor();
	  				totalTrades += res0.getTotalTrades();
	  				if (debug==1)
	  					System.out.println(""
	  							+" header= "+header
	  							+" begin= "+DateUtils.datePrint(mmArray.get(0).getCal())
	  			  				+" h= "+getHoursEnabled(allowedHours,1)
	  			  				//+" min= "+getHoursEnabled(allowedMinutes,5)
	  			  				+" tp= "+tp
	  			  				+" sl= "+sl
	  			  				+" tt = "+res0.getTotalTrades()
	  			  				+" tdn = "+res0.getTotalDontKnow()
	  			  				+" maxC = "+maxConcurrent
	  			  				+" w% = "+PrintUtils.Print2dec(res0.getPercentWin(), false,3)  			  				
	  			  				+" exp = "+PrintUtils.Print2dec(res0.getExpectancy(), false,2)
	  			  				+" kellyC = "+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  			  				+" pf = "+PrintUtils.Print2dec(res0.getProfitFactor(), false,3)
	  			  				+" testRisk = "+PrintUtils.Print2dec(res0.getTestRisk(), false,3)
	  			  				+" tradeExpTest = "+PrintUtils.Print2dec(res0.getTradeExpectancyTest(), false,3)
	  			  				+" finalCapital = "+PrintUtils.Print2dec2(res0.getFinalCapital(), true)  
	  			  				+" finalCapital(semireal) = "+PrintUtils.Print2dec2(res1.getFinalCapital(), true)  
	  			  				);	
	  				if (debug==2){
	  					System.out.println(barLookBack
	  							+","+res0.getTotalTrades()
	  							+","+maxConcurrent
	  							+","+PrintUtils.Print2dec2(res1.getFinalCapital(), true) 
	  							+","+PrintUtils.Print2dec(res0.getExpectancy(), false,3)
	  							);
	  				}
	  				if (debug==5)
	  					return res0;
				}//sl
			}//tp
		}//m	
  		return null;
	}
	
	public static void optimization1hDayFilter(String header,ArrayList<Quote> data,ArrayList<Quote> maxMin,int d,int h1,int h2,
			double tp1,double tp2,double tpInc,double sl1,double sl2,double slInc,
			int begin,int end,int lookBack,int barLookBack,boolean breakout,int debug){
				
		double initialCapital = 200.0;
		double comm = 1.35;
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(1);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=4;i++) allowedDays.add(1);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedDays.add(1);
  		convertAllowedTime(allowedDays,String.valueOf(d),0,4,0);
  		for (int h=h1;h<=h2;h+=1){ //para cada 5 min testeo
			convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
			//convertAllowedTime(allowedHours,"0 1 2 3 4 5 6 7 8 9",0,23,0);
			//convertAllowedTime(allowedHours,"4 5 6 7 8 9",0,23,0);
			//convertAllowedTime(allowedMinutes,"0",0,59,1);
			double avgExpTest = 0;
			int total = 0;
			int totalPos = 0;
			int totalPeriods = 0;
			int totalTrades = 0;
			double avgPfs = 0;				

			ArrayList<MaxMinResult> mmArray = calculateMaxMinPerformance(data, maxMin,begin,end,tp1,tp2,tpInc,sl1,sl2,slInc,breakout);		  			
			//System.out.println("size mmArray: "+mmArray.size());
			for (double tp = tp1;tp<=tp2;tp+=tpInc){
				for (double sl = sl1;sl<=sl2;sl+=slInc){
					int maxConcurrent = calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					double maxRisk = 100.0/maxConcurrent;
					StrategyResultEx res0 = calculateStatsMin( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true);
					StrategyResultEx res1 = calculateStatsReal( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true,false,false);
	  				avgExpTest += res0.getTradeExpectancyTest();
	  				total++;
	  				if (res0.getTradeExpectancyTest()>0) totalPos++;
	  				totalPeriods++;
	  				avgPfs+=res0.getProfitFactor();
	  				totalTrades += res0.getTotalTrades();
	  				if (debug==1)
	  					System.out.println(""
	  							+" header= "+header
	  							+" begin= "+DateUtils.datePrint(mmArray.get(0).getCal())
	  			  				+" h= "+getHoursEnabled(allowedHours,1)
	  			  				//+" min= "+getHoursEnabled(allowedMinutes,5)
	  			  				+" tp= "+tp
	  			  				+" sl= "+sl
	  			  				+" tt = "+res0.getTotalTrades()
	  			  				+" tdn = "+res0.getTotalDontKnow()
	  			  				+" maxC = "+maxConcurrent
	  			  				+" w% = "+PrintUtils.Print2dec(res0.getPercentWin(), false,3)  			  				
	  			  				+" exp = "+PrintUtils.Print2dec(res0.getExpectancy(), false,2)
	  			  				+" kellyC = "+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  			  				+" pf = "+PrintUtils.Print2dec(res0.getProfitFactor(), false,3)
	  			  				+" testRisk = "+PrintUtils.Print2dec(res0.getTestRisk(), false,3)
	  			  				+" tradeExpTest = "+PrintUtils.Print2dec(res0.getTradeExpectancyTest(), false,3)
	  			  				+" finalCapital = "+PrintUtils.Print2dec2(res0.getFinalCapital(), true)  
	  			  				+" finalCapital(semireal) = "+PrintUtils.Print2dec2(res1.getFinalCapital(), true)  
	  			  				);	
	  				if (debug==2){
	  					System.out.println(
	  							d
	  							+","+h
	  							+","+res0.getTotalTrades()
	  							+","+tp
	  							+","+sl
	  							+","+barLookBack
	  							+","+PrintUtils.Print2dec(res0.getExpectancy(), false,3)
	  							+","+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  							);
	  				}
				}//sl
			}//tp
			/*System.out.println(" min= "+m
						+" totaltrades= "+totalTrades
						+" avgPfs= "+PrintUtils.Print2dec(avgPfs*1.0/total, false,3)
		  				+" avgExp= "+PrintUtils.Print2dec(avgExpTest*1.0/total, false,3)
		  				+" pos= "+PrintUtils.Print2dec(totalPos*1.0/totalPeriods,false,3)	
		  				);*/
		  				
		}//m		
	}
	
	public static void optimization1hEach(String header,ArrayList<Quote> data,ArrayList<Quote> maxMin,int h1,int h2,
			double tp1,double tp2,double tpInc,double sl1,double sl2,double slInc,
			int begin,int end,int lookBack,int barLookBack,boolean breakout,int debug){
				
		double initialCapital = 200.0;
		double comm = 1.35;
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(1);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=5;i++) allowedDays.add(1);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedDays.add(1);
  		convertAllowedTime(allowedHours,"10 11",0,23,0);
  		for (int h=h1;h<=h2;h+=1){ //para cada 5 min testeo
			//convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
			
			double avgExpTest = 0;
			int total = 0;
			int totalPos = 0;
			int totalPeriods = 0;
			int totalTrades = 0;
			double avgPfs = 0;				

			ArrayList<MaxMinResult> mmArray = calculateMaxMinPerformance(data, maxMin,begin,end,tp1,tp2,tpInc,sl1,sl2,slInc,breakout);		  			
			//System.out.println("size mmArray: "+mmArray.size());
			for (double tp = tp1;tp<=tp2;tp+=tpInc){
				for (double sl = sl1;sl<=sl2;sl+=slInc){
					int maxConcurrent = calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					double maxRisk = 100.0/maxConcurrent;
					StrategyResultEx res0 = calculateStatsMin( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true);
					StrategyResultEx res1 = calculateStatsReal( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true,false,false);
	  				avgExpTest += res0.getTradeExpectancyTest();
	  				total++;
	  				if (res0.getTradeExpectancyTest()>0) totalPos++;
	  				totalPeriods++;
	  				avgPfs+=res0.getProfitFactor();
	  				totalTrades += res0.getTotalTrades();
	  				if (debug==1)
	  					System.out.println(""
	  							+" header= "+header
	  							+" begin= "+DateUtils.datePrint(mmArray.get(0).getCal())
	  			  				+" h= "+getHoursEnabled(allowedHours,1)
	  			  				//+" min= "+getHoursEnabled(allowedMinutes,5)
	  			  				+" tp= "+tp
	  			  				+" sl= "+sl
	  			  				+" tt = "+res0.getTotalTrades()
	  			  				+" tdn = "+res0.getTotalDontKnow()
	  			  				+" maxC = "+maxConcurrent
	  			  				+" w% = "+PrintUtils.Print2dec(res0.getPercentWin(), false,3)  			  				
	  			  				+" exp = "+PrintUtils.Print2dec(res0.getExpectancy(), false,2)
	  			  				+" kellyC = "+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  			  				+" pf = "+PrintUtils.Print2dec(res0.getProfitFactor(), false,3)
	  			  				+" testRisk = "+PrintUtils.Print2dec(res0.getTestRisk(), false,3)
	  			  				+" tradeExpTest = "+PrintUtils.Print2dec(res0.getTradeExpectancyTest(), false,3)
	  			  				+" finalCapital = "+PrintUtils.Print2dec2(res0.getFinalCapital(), true)  
	  			  				+" finalCapital(semireal) = "+PrintUtils.Print2dec2(res1.getFinalCapital(), true)  
	  			  				);	
	  				if (debug==2){
	  					System.out.println(h
	  							+","+res0.getTotalTrades()
	  							+","+tp
	  							+","+sl
	  							+","+barLookBack
	  							+","+PrintUtils.Print2dec(res0.getExpectancy(), false,3)
	  							+","+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  							);
	  				}
				}//sl
			}//tp
			/*System.out.println(" min= "+m
						+" totaltrades= "+totalTrades
						+" avgPfs= "+PrintUtils.Print2dec(avgPfs*1.0/total, false,3)
		  				+" avgExp= "+PrintUtils.Print2dec(avgExpTest*1.0/total, false,3)
		  				+" pos= "+PrintUtils.Print2dec(totalPos*1.0/totalPeriods,false,3)	
		  				);*/
		  				
		}//m		
	}
	
	public static void optimization1hEachDayFilter(String header,ArrayList<Quote> data,ArrayList<Quote> maxMin,
			int day,int h1,int h2,
			double tp1,double tp2,double tpInc,double sl1,double sl2,double slInc,
			int begin,int end,int lookBack,int barLookBack,boolean breakout,int debug){
				
		double initialCapital = 200.0;
		double comm = 1.35;
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(1);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=4;i++) allowedDays.add(0);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedDays.add(1);
  		convertAllowedTime(allowedDays,String.valueOf(day),0,4,0);
  		for (int h=h1;h<=h2;h+=1){ //para cada 5 min testeo
			convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
			
			double avgExpTest = 0;
			int total = 0;
			int totalPos = 0;
			int totalPeriods = 0;
			int totalTrades = 0;
			double avgPfs = 0;				

			ArrayList<MaxMinResult> mmArray = calculateMaxMinPerformance(data, maxMin,begin,end,tp1,tp2,tpInc,sl1,sl2,slInc,breakout);		  			
			//System.out.println("size mmArray: "+mmArray.size());
			for (double tp = tp1;tp<=tp2;tp+=tpInc){
				for (double sl = sl1;sl<=sl2;sl+=slInc){
					int maxConcurrent = calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					double maxRisk = 100.0/maxConcurrent;
					StrategyResultEx res0 = calculateStatsMin( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true);
					StrategyResultEx res1 = calculateStatsReal( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true,false,false);
	  				avgExpTest += res0.getTradeExpectancyTest();
	  				total++;
	  				if (res0.getTradeExpectancyTest()>0) totalPos++;
	  				totalPeriods++;
	  				avgPfs+=res0.getProfitFactor();
	  				totalTrades += res0.getTotalTrades();
	  				if (debug==1)
	  					System.out.println(""
	  							+" header= "+header
	  							+" begin= "+DateUtils.datePrint(mmArray.get(0).getCal())
	  			  				+" h= "+getHoursEnabled(allowedHours,1)
	  			  				//+" min= "+getHoursEnabled(allowedMinutes,5)
	  			  				+" tp= "+tp
	  			  				+" sl= "+sl
	  			  				+" tt = "+res0.getTotalTrades()
	  			  				+" tdn = "+res0.getTotalDontKnow()
	  			  				+" maxC = "+maxConcurrent
	  			  				+" w% = "+PrintUtils.Print2dec(res0.getPercentWin(), false,3)  			  				
	  			  				+" exp = "+PrintUtils.Print2dec(res0.getExpectancy(), false,2)
	  			  				+" kellyC = "+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  			  				+" pf = "+PrintUtils.Print2dec(res0.getProfitFactor(), false,3)
	  			  				+" testRisk = "+PrintUtils.Print2dec(res0.getTestRisk(), false,3)
	  			  				+" tradeExpTest = "+PrintUtils.Print2dec(res0.getTradeExpectancyTest(), false,3)
	  			  				+" finalCapital = "+PrintUtils.Print2dec2(res0.getFinalCapital(), true)  
	  			  				+" finalCapital(semireal) = "+PrintUtils.Print2dec2(res1.getFinalCapital(), true)  
	  			  				);	
	  				if (debug==2){
	  					System.out.println(barLookBack+","+PrintUtils.Print2dec(res0.getProfitFactor(), false,3));
	  				}
				}//sl
			}//tp
			/*System.out.println(" min= "+m
						+" totaltrades= "+totalTrades
						+" avgPfs= "+PrintUtils.Print2dec(avgPfs*1.0/total, false,3)
		  				+" avgExp= "+PrintUtils.Print2dec(avgExpTest*1.0/total, false,3)
		  				+" pos= "+PrintUtils.Print2dec(totalPos*1.0/totalPeriods,false,3)	
		  				);*/
		  				
		}//m		
	}

	public static void optimization5m1hfilter(String header,ArrayList<Quote> data,ArrayList<Quote> maxMin,
			int h,
			double tp1,double tp2,double tpInc,double sl1,double sl2,double slInc,
			int begin,int end,int lookBack,boolean breakout,int debug){
				
		double initialCapital = 200.0;
		double comm = 1.35;
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(0);
  		ArrayList<Integer> allowedDays = new ArrayList<Integer>();for (int i=0;i<=5;i++) allowedDays.add(1);
  		ArrayList<Integer> allowedMonths = new ArrayList<Integer>();for (int i=0;i<=11;i++) allowedDays.add(1);
  		//convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
  		convertAllowedTime(allowedHours,"",0,23,1);
  		for (int m=0;m<=59;m+=5){ //para cada 5 min testeo
			//convertAllowedTime(allowedHours,String.valueOf(h),0,23,0);
			//convertAllowedTime(allowedHours,"0 1 2 3 4 5 6 7 8 9",0,23,0);
			convertAllowedTime(allowedMinutes,String.valueOf(m),0,59,0);
			double avgExpTest = 0;
			int total = 0;
			int totalPos = 0;
			int totalPeriods = 0;
			int totalTrades = 0;
			double avgPfs = 0;				

			ArrayList<MaxMinResult> mmArray = calculateMaxMinPerformance(data, maxMin,begin,end,tp1,tp2,tpInc,sl1,sl2,slInc,breakout);		  			
			//System.out.println("size mmArray: "+mmArray.size());
			for (double tp = tp1;tp<=tp2;tp+=tpInc){
				for (double sl = sl1;sl<=sl2;sl+=slInc){
					int maxConcurrent = calculateConcurrent(mmArray,allowedHours,allowedMinutes,allowedMonths,tp,sl);
					double maxRisk = 100.0/maxConcurrent;
					StrategyResultEx res0 = calculateStatsMin( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true);
					StrategyResultEx res1 = calculateStatsReal( mmArray,allowedHours,allowedMinutes,allowedDays,allowedMonths,initialCapital,tp,sl,comm,maxRisk,true,false,false);
	  				avgExpTest += res0.getTradeExpectancyTest();
	  				total++;
	  				if (res0.getTradeExpectancyTest()>0) totalPos++;
	  				totalPeriods++;
	  				avgPfs+=res0.getProfitFactor();
	  				totalTrades += res0.getTotalTrades();
	  				if (debug==1)
	  					System.out.println(""
	  							+" header= "+header
	  							+" begin= "+DateUtils.datePrint(mmArray.get(0).getCal())
	  			  				+" h= "+getHoursEnabled(allowedHours,1)
	  			  				+" min= "+getHoursEnabled(allowedMinutes,5)
	  			  				+" tp= "+tp
	  			  				+" sl= "+sl
	  			  				+" tt = "+res0.getTotalTrades()
	  			  				+" tdn = "+res0.getTotalDontKnow()
	  			  				+" maxC = "+maxConcurrent
	  			  				+" w% = "+PrintUtils.Print2dec(res0.getPercentWin(), false,3)  			  				
	  			  				+" exp = "+PrintUtils.Print2dec(res0.getExpectancy(), false,2)
	  			  				+" kellyC = "+PrintUtils.Print2dec(res0.getKelly(), false,3)
	  			  				+" pf = "+PrintUtils.Print2dec(res0.getProfitFactor(), false,3)
	  			  				+" testRisk = "+PrintUtils.Print2dec(res0.getTestRisk(), false,3)
	  			  				+" tradeExpTest = "+PrintUtils.Print2dec(res0.getTradeExpectancyTest(), false,3)
	  			  				+" finalCapital = "+PrintUtils.Print2dec2(res0.getFinalCapital(), true)  
	  			  				+" finalCapital(semireal) = "+PrintUtils.Print2dec2(res1.getFinalCapital(), true)  
	  			  				);	
				}//sl
			}//tp
			/*System.out.println(" min= "+m
						+" totaltrades= "+totalTrades
						+" avgPfs= "+PrintUtils.Print2dec(avgPfs*1.0/total, false,3)
		  				+" avgExp= "+PrintUtils.Print2dec(avgExpTest*1.0/total, false,3)
		  				+" pos= "+PrintUtils.Print2dec(totalPos*1.0/totalPeriods,false,3)	
		  				);*/
		  				
		}//m		
	}
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.17.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.17.csv";
		//String path5m = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.09.17.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		//String path5m = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2005.01.01_2014.07.23.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		System.out.println("datas dataI: "+dataI.size()+" "+dataS.size());
  		ArrayList<Quote> dataGMT = TradingUtils.cleanWeekendData(dataI);
  		//TradingUtils.checkConsistency(data);
  		//ArrayList<Quote> hourlyData = ConvertLib.convert(data, 12);
  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(dataGMT);
  		//TradingUtils.checkConsistency(data);
  		//ArrayList<Quote> weeklyData = ConvertLib.createWeeklyData(dailyData);
  		//ArrayList<Quote> monthlyData = ConvertLib.createMonthlyData(dailyData);
  		
  		Calendar from = Calendar.getInstance();
  		Calendar to = Calendar.getInstance();
  		from.set(2004, 0,1);
  		to.set(2014, 11,1);
  		//double avg = TradingUtils.checkConsistencyHoles(dataGMT, from, to, 0, 5, 10);
  		//double avg = TradingUtils.checkConsistencyHoles(data, from, to, 0, 5, 10);
  		//System.out.println("avg : "+PrintUtils.Print2(avg));	
  		double tp1 = 10;
  		double tp2 = 10;
  		double sl1 = 10;
  		double sl2 = 10;
  		double tpInc = 1;
  		double slInc = 10;  		
  		int b = 400000;
  		int lookBack = 400000;
  		int barLookBack = 1;
  		boolean breakout = false;
  		int debug=2;
  		
  		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
  		ArrayList<Integer> allowedMinutes = new ArrayList<Integer>();for (int i=0;i<=59;i++) allowedMinutes.add(0);
  		//ArrayList<Quote> maxMin = calculateMaxMinByDay(dataGMT);
  		
  		
  		
  		
  		//int mn = Calendar.JANUARY;
  		for (int h1=0;h1<=0;h1++){
  			int h2 = h1+0;
	  		//for (barLookBack=12;barLookBack<=600;barLookBack+=120){
		  		//ArrayList<Quote> maxMin = calculateMaxMinByBar(dataGMT,barLookBack);
	  			//ArrayList<Quote> maxMin = calculateMaxMinByDay(data);
		  		//ArrayList<Quote> maxMin = calculateMaxMinByBar(data,barLookBack);
		  		//System.out.println("data maxmin "+dataGMT.size()+" "+data.size()+" "+maxMin.size());
		  		//int begin = maxMin.size()-1-b;
		  		//int end = maxMin.size()-1;
		  		//optimizacion para cada 5m de hora 00 05 10 15 20 25 30 35 40 45 50 55
		  		
		  		for (int d=0;d<=0;d++){
		  			for (h1=9;h1<=9;h1++){
		  	  			h2 = h1+0;
		  	  			for (int mn = Calendar.JANUARY+0;mn<=Calendar.JANUARY+11;mn++){
			  	  			for (b=800000;b>=800000;b-=50000){
			  	  				lookBack = 800000;
			  	  				double avg = 0;
			  	  				int total=0;
				  	  			for (barLookBack=12;barLookBack<=10000;barLookBack+=100){
				  	  			String header = "BLB= "+barLookBack;
				  	  				ArrayList<Quote> maxMin = calculateMaxMinByBar(data,barLookBack);
				  	  				int begin = maxMin.size()-1-b;
				  	  				int end = begin+lookBack;
							  		for (tp1=8;tp1<=8;tp1+=1){
							  			tp2=tp1;
							  			for (sl1=32;sl1<=32;sl1+=1){
							  				sl2=sl1;
							  				//optimization5m(dataGMT,maxMin,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,breakout,debug);//12 casos
							  				//optimization5m(data,maxMin,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,breakout,debug);//12 casos
							  				//optimization1hEach(header,data,maxMin,h1,h2,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,barLookBack,breakout,debug);//12 casos
							  				//optimization1hEachDayFilter(header,data,maxMin,d,h1,h2,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,barLookBack,breakout,debug);
							  				//StrategyResultEx res = optimization1h(header,data,maxMin,h1,h2,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,barLookBack,breakout,debug);
							  				StrategyResultEx res = optimization1hMonthFilter(header,data,maxMin,h1,h2,mn,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,barLookBack,breakout,debug);
							  				//optimization1hDayFilter(header,data,maxMin,d,h1,h2,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,barLookBack,breakout,debug);
							  				//optimization5m1hfilter(header,data,maxMin,h1,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,breakout,debug);//12 casos
							  				//System.out.println();
							  				if (res!=null){
							  					avg+=res.getExpectancy();
							  					total++;
							  				}
							  	  		}
							  		}
				  	  			}//barlookback
				  	  			if (debug==5)
				  	  				System.out.println(h1+","+b+","+PrintUtils.Print(avg*1.0/total));
			  	  			}//b
		  	  			}//mn
		  			}//h1
		  		}
		  		//System.out.println();
	  		//}
	  		
  		}
  		
  		//optimizacion de cada hora por dia 01 02 03 04 05 06 07 08...
  		//optimization1h(dataGMT,maxMin,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,debug);//24 casos
  		//optimizacion de cada 5m-hora
  		//optimization5m1h(dataGMT,maxMin,tp1,tp2,tpInc,sl1,sl2,slInc,begin,end,lookBack,debug);//288 casos
  		//optimizacion de cada dia L M X J V
  		//optimizacion sobre dataGMT
  		
	}
}
