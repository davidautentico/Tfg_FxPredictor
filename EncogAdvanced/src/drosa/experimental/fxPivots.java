package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class fxPivots {

	 public static void doTradePullsClose(ArrayList<QuoteShort> data,
			 ArrayList<QuoteShort> maxMins,
			 int y1,int y2,
			 int h1,int h2,int thr,int nbars,
			 int tp,
			 int minPull,
			 int maxTrades,
			 int breakDirection
			 ){
			
		 	int winPips = 0;
		 	int lostPips = 0;
			int lastDay = -1;
			int max = -1;
			int min = -1;
			ArrayList<Double> diffs = new ArrayList<Double>();
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			int totalPips = 0;
			int totalAdverse = 0;
			int totalTrades = 0;
			int wins = 0;
			int tradeRef = 0;
			int tradeMode = 0;
			int dayTrades = 0;
			QuoteShort qm = new QuoteShort();
			for (int i=0;i<data.size()-1;i++){
				QuoteShort q = data.get(i);
				QuoteShort q1 = data.get(i+1);
				QuoteShort.getCalendar(cal, q);
				int year = cal.get(Calendar.YEAR);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				
				if (year<y1 || year>y2) continue;
				
				if (day!=lastDay){	
					tradeRef = -1;
					tradeMode = 0;
					max = -1;
					min = -1;
					dayTrades = 0;
					lastDay = day;
				}
				
				int maxMin = maxMins.get(i).getExtra();
				
				//actualizamos puntos de referencia
				if (h1<=h && h<=h2 && tradeMode<2){				
					if (maxMin>=thr){
						tradeRef = q.getHigh5();
						tradeMode=1;
						//System.out.println("maxMin "+maxMin+" "+thr+" "+q.toString());
					}else if (maxMin<=-thr){
						tradeRef = q.getLow5();
						tradeMode=-1;
					}
				}	
				
				//vemos si hay pull
				if (tradeMode==1){ //long
					int diff = tradeRef - q1.getOpen5();
					if (diff>=minPull){
						int valueTP = q1.getOpen5()+breakDirection*tp;
						//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
						TradingUtils.getMaxMinShortTP(data, qm, cal1, i+1, i+nbars,valueTP);
						int maxL =q1.getOpen5()-qm.getLow5();
						int diffH = qm.getClose5()-q1.getOpen5();
						if (breakDirection==-1){
							maxL = qm.getHigh5()-q1.getOpen5();
							diffH = q1.getOpen5()-qm.getClose5();
						}
						//int diffL = q1.getOpen5()-qm.getClose5();
						totalPips += (diffH);
						totalAdverse += maxL;
						int win = -1;
						if (diffH>=0){
							win = 1;
							wins++;
							winPips+=diffH;
						}else{
							lostPips+=-diffH;
						}
						totalTrades++;
						dayTrades++;
						if (dayTrades>=maxTrades) tradeMode=2;
						//System.out.println("[LONG] "+tradeRef+" "+q1.getOpen5()+" "+valueTP+" "+qm.getClose5()+" || "+diffH+" "+maxL+" || "+win);
					}				
				}else if (tradeMode==-1){//short
					int diff = q1.getOpen5()-tradeRef;
					if (diff>=minPull){
						int valueTP = q1.getOpen5()-breakDirection*tp;
						//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
						TradingUtils.getMaxMinShortTP(data, qm, cal1, i+1, i+nbars,valueTP);
						int maxH = qm.getHigh5()-q1.getOpen5();
						int diffL = q1.getOpen5()-qm.getClose5();
						if (breakDirection==-1){
							maxH = q1.getOpen5()-qm.getLow5();
							diffL = qm.getClose5()-q1.getOpen5();
						}
						totalPips += (diffL);
						totalAdverse += maxH;
						int win = -1;
						if (diffL>=0){
							win = 1;
							wins++;
							winPips += diffL;
						}else{
							lostPips += -diffL;
						}
						totalTrades++;
						dayTrades++;
						if (dayTrades>=maxTrades) tradeMode=2;
						
						//System.out.println("[SHORT] "+tradeRef+" "+q1.getOpen5()+" "+valueTP+" "+qm.getClose5()+" || "+diffL+" || "+win);
					}	
				}			
			}
			
			double avg = totalPips*1.0/totalTrades;
			double avgAdverse = totalAdverse*1.0/totalTrades;
			double winPer = wins*100.0/totalTrades;
			double pf = winPips*1.0/lostPips;
					
			System.out.println(
					h1+" "+h2
					+" "+thr
					+" "+nbars
					+" "+minPull
					+" "+tp
					+" || "
					+" "+totalTrades
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(avgAdverse, false)
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					);
		}
	 
	 public static void doTrade(ArrayList<QuoteShort> data,
			 ArrayList<QuoteShort> maxMins,
			 int y1,int y2,
			 int h1,int h2,int thr,int nbars,
			 int tp,int sl,int offset,
			 int maxTrades,
			 double comm,
			 int tickSize
			 ){
		 
		 	ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
			
		 	double winPips = 0;
		 	double lostPips = 0;
			int lastDay = -1;
			int max = -1;
			int min = -1;
			ArrayList<Double> diffs = new ArrayList<Double>();
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			int totalPips = 0;
			int totalAdverse = 0;
			int totalTrades = 0;
			int wins = 0;
			int losses = 0;
			int tradeRef = 0;
			int tradeMode = 0;
			int dayTrades = 0;
			int maxLosses = 0;
			int actualLosses = 0;
			double maxPips = 0;
			int ddPips = 0;
			double maxDD = 0;
			QuoteShort qm = new QuoteShort();
			for (int i=1;i<data.size()-1;i++){
				QuoteShort q_1 = data.get(i-1);
				QuoteShort q = data.get(i);
				QuoteShort q1 = data.get(i+1);
				QuoteShort.getCalendar(cal, q);
				int year = cal.get(Calendar.YEAR);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				
				if (year<y1 || year>y2) continue;
				
				if (day!=lastDay){	
					tradeRef = -1;
					tradeMode = 0;
					max = -1;
					min = -1;
					dayTrades = 0;
					lastDay = day;
				}
				
				int maxMin = maxMins.get(i).getExtra();
				//evaluacion nuevos trades
				if (h1<=h && h<=h2){
					int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
					if (totalOpen<maxTrades){
						int entry = -1;
						int tpValue = -1;
						int slValue = -1;
						PositionType positionType = PositionType.NONE;
						int diffOC = q.getOpen5()-q.getClose5();
						int diffHC = q.getHigh5()-q.getClose5();
						int diffCL = q.getClose5()-q.getLow5();
						if (maxMin>=thr
								//&& diffOC>=offset
								//&& diffHC>=offset
								){
							entry = q1.getOpen5();
							tpValue = entry - tickSize*tp;
							slValue = entry + tickSize*sl;
							positionType = PositionType.SHORT;
						}else if (maxMin<=-thr
								//&& diffOC<=-offset
								//&& diffCL>=offset
								){
							entry = q1.getOpen5();
							tpValue = entry + tickSize*tp;
							slValue = entry - tickSize*sl;
							positionType = PositionType.LONG;
						}
						
						if (entry!=-1){
							PositionShort pos = new PositionShort();
							pos.setEntry(entry);
							pos.setTp(tpValue);
							pos.setSl(slValue);
							pos.setPositionType(positionType);
							pos.setPositionStatus(PositionStatus.OPEN);
							positions.add(pos);
						}
					}
				}//nuevos trades
				
				//evaluacion posiciones
				
				int p = 0;
				while (p<positions.size()){
					PositionShort pos = positions.get(p);
					boolean closed = false;
					double points = 0;
					if (pos.getPositionStatus()==PositionStatus.OPEN){
						if (pos.getPositionType()==PositionType.LONG){
							if (q1.getLow5()<=pos.getSl()){
								points = -sl;
								closed = true;
							}else if (q1.getHigh5()>=pos.getTp()){
								points = tp;
								closed = true;
							}
						}else if (pos.getPositionType()==PositionType.SHORT){
							if (q1.getHigh5()>=pos.getSl()){
								points = -sl;
								closed = true;
							}else if (q1.getLow5()<=pos.getTp()){
								points = tp;
								closed = true;
							}
						}
					}
					
					if (closed){
						points = points-comm;//comision
						//points /=tickSize;
						if (points>=0){
							winPips += points;
							wins++;
							actualLosses = 0;
						}else{
							lostPips += -points;
							losses++;
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
						}
						
						double totalPoints = winPips-lostPips;
						if (totalPoints>=maxPips){
							maxPips = totalPoints;
						}else{
							double actualDD = maxPips-totalPoints;
							if (actualDD>=maxDD) maxDD = actualDD;
						}
						positions.remove(p);
					}else{
						p++;
					}
				}
				
			
			}//data
			
			totalTrades = wins+losses;
			double avg = (winPips-lostPips)*1.0/(totalTrades);
			//double avgAdverse = totalAdverse*1.0/totalTrades;
			double winPer = wins*100.0/totalTrades;
			double pf = winPips*1.0/lostPips;
			double balance = totalTrades*avg*5.0;
			double maxLoss$ = maxLosses*sl*5.0;
			System.out.println(
					h1+" "+h2
					+" "+thr
					+" "+nbars
					+" "+tp
					+" "+sl
					+" "+offset
					+" || "
					+" "+totalTrades+" "+wins+" "+losses
					+" "+PrintUtils.Print2dec(winPips, false)+" "+PrintUtils.Print2dec(lostPips, false)
					+" "+PrintUtils.Print2dec(avg, false)
					//+" "+PrintUtils.Print2dec(avgAdverse, false)
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					+" || "+maxLosses
					+" || "
					+" "+PrintUtils.Print2dec(balance, false)
					+" "+PrintUtils.Print2dec(maxLoss$, false)
					+" "+PrintUtils.Print2dec(balance/maxLoss$, false)
					+" || "
					//+" "+maxDD
					+" "+PrintUtils.Print2dec(maxDD*5.0, false)
					+" || "+PrintUtils.Print2dec(balance/(maxDD*5.0), false)
					);
		}
		
	 public static void doTradePullsClose(ArrayList<QuoteShort> data,
			 ArrayList<QuoteShort> maxMins,
			 int y1,int y2,
			 int h1,int h2,int thr,int nbars,
			 int tp,int sl,
			 int minPull,
			 int maxTrades,
			 int breakDirection
			 ){
			
		 	int winPips = 0;
		 	int lostPips = 0;
			int lastDay = -1;
			int max = -1;
			int min = -1;
			ArrayList<Double> diffs = new ArrayList<Double>();
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			int totalPips = 0;
			int totalAdverse = 0;
			int totalTrades = 0;
			int wins = 0;
			int tradeRef = 0;
			int tradeMode = 0;
			int dayTrades = 0;
			QuoteShort qm = new QuoteShort();
			for (int i=1;i<data.size()-1;i++){
				QuoteShort q_1 = data.get(i-1);
				QuoteShort q = data.get(i);
				QuoteShort q1 = data.get(i+1);
				QuoteShort.getCalendar(cal, q);
				int year = cal.get(Calendar.YEAR);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				
				if (year<y1 || year>y2) continue;
				
				if (day!=lastDay){	
				
					tradeRef = -1;
					tradeMode = 0;
					max = -1;
					min = -1;
					dayTrades = 0;
					lastDay = day;
				}
				
				int maxMin = maxMins.get(i).getExtra();
				
				//actualizamos puntos de referencia
				if (h1<=h && h<=h2 && tradeMode<2){				
					if (maxMin>=thr){
						tradeRef = q.getHigh5();
						tradeMode=1;
						//System.out.println("maxMin "+maxMin+" "+thr+" "+q.toString());
					}else if (maxMin<=-thr){
						tradeRef = q.getLow5();
						tradeMode=-1;
					}
				}	
				
				//vemos si hay pull
				if (tradeMode==1){ //long
					int diff = tradeRef - q1.getOpen5();
					if (diff>=minPull){
						int valueTP = q1.getOpen5()+breakDirection*tp;
						int valueSL = q1.getOpen5()-breakDirection*sl;
						//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
						TradingUtils.getMaxMinShortTPSL(data, qm, cal1, i+1, i+nbars,valueTP,valueSL);
						int maxL =q1.getOpen5()-qm.getLow5();
						int diffH = qm.getClose5()-q1.getOpen5();
						if (breakDirection==-1){
							maxL = qm.getHigh5()-q1.getOpen5();
							diffH = q1.getOpen5()-qm.getClose5();
						}
						//int diffL = q1.getOpen5()-qm.getClose5();
						totalPips += (diffH);
						totalAdverse += maxL;
						int win = -1;
						if (diffH>=0){
							win = 1;
							wins++;
							winPips+=diffH;
						}else{
							lostPips+=-diffH;
						}
						totalTrades++;
						dayTrades++;
						if (dayTrades>=maxTrades) tradeMode=2;
						//System.out.println("[LONG] "+tradeRef+" "+q1.getOpen5()+" "+valueTP+" "+qm.getClose5()+" || "+diffH+" "+maxL+" || "+win);
					}				
				}else if (tradeMode==-1){//short
					int diff = q1.getOpen5()-tradeRef;
					if (diff>=minPull){
						int valueTP = q1.getOpen5()-breakDirection*tp;
						int valueSL = q1.getOpen5()+breakDirection*sl;
						//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
						TradingUtils.getMaxMinShortTPSL(data, qm, cal1, i+1, i+nbars,valueTP,valueSL);
						int maxH = qm.getHigh5()-q1.getOpen5();
						int diffL = q1.getOpen5()-qm.getClose5();
						if (breakDirection==-1){
							maxH = q1.getOpen5()-qm.getLow5();
							diffL = qm.getClose5()-q1.getOpen5();
						}
						totalPips += (diffL);
						totalAdverse += maxH;
						int win = -1;
						if (diffL>=0){
							win = 1;
							wins++;
							winPips += diffL;
						}else{
							lostPips += -diffL;
						}
						totalTrades++;
						dayTrades++;
						if (dayTrades>=maxTrades) tradeMode=2;
						
						//System.out.println("[SHORT] "+tradeRef+" "+q1.getOpen5()+" "+valueTP+" "+qm.getClose5()+" || "+diffL+" || "+win);
					}	
				}			
			}
			
			double avg = totalPips*1.0/totalTrades;
			double avgAdverse = totalAdverse*1.0/totalTrades;
			double winPer = wins*100.0/totalTrades;
			double pf = winPips*1.0/lostPips;
					
			System.out.println(
					h1+" "+h2
					+" "+thr
					+" "+nbars
					+" "+minPull
					+" "+tp
					+" "+sl
					+" || "
					+" "+totalTrades
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(avgAdverse, false)
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					);
		}
	 
	 public static void doTradePullHL(ArrayList<QuoteShort> data,
			 ArrayList<QuoteShort> maxMins,
			 int y1,int y2,
			 int h1,int h2,int thr,int nbars,
			 int tp,int sl,
			 int minPull,
			 int maxTrades,
			 int breakDirection
			 ){
			
		 	int winPips = 0;
		 	int lostPips = 0;
			int lastDay = -1;
			int max = -1;
			int min = -1;
			int lastH = -1;
			int lastL = -1;
			ArrayList<Double> diffs = new ArrayList<Double>();
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			int totalPips = 0;
			int totalAdverse = 0;
			int totalTrades = 0;
			int wins = 0;
			int tradeRef = 0;
			int tradeMode = 0;
			int dayTrades = 0;
				
			QuoteShort qm = new QuoteShort();
			for (int i=1;i<data.size()-1;i++){
				QuoteShort q_1 = data.get(i-1);
				QuoteShort q = data.get(i);
				QuoteShort q1 = data.get(i+1);
				QuoteShort.getCalendar(cal, q);
				int year = cal.get(Calendar.YEAR);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				
				if (year<y1 || year>y2) continue;
				
				if (day!=lastDay){	
					tradeRef = -1;
					tradeMode = 0;
					lastH = max;
					lastL = min;
					max = -1;
					min = -1;
					dayTrades = 0;
					lastDay = day;
				}
				
				int maxMin = maxMins.get(i).getExtra();
				
				if (lastH!=-1 && q.getHigh5()>=lastH){
					int valueTP = q1.getOpen5()+breakDirection*tp;
					int valueSL = q1.getOpen5()-breakDirection*sl;
					//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
					TradingUtils.getMaxMinShortTPSL(data, qm, cal1, i+1, i+nbars,valueTP,valueSL);
					int maxL =q1.getOpen5()-qm.getLow5();
					int diffH = qm.getClose5()-q1.getOpen5();
					if (breakDirection==-1){
						maxL = qm.getHigh5()-q1.getOpen5();
						diffH = q1.getOpen5()-qm.getClose5();
					}
					//int diffL = q1.getOpen5()-qm.getClose5();
					totalPips += (diffH);
					totalAdverse += maxL;
					int win = -1;
					if (diffH>=0){
						win = 1;
						wins++;
						winPips+=diffH;
					}else{
						lostPips+=-diffH;
					}
					totalTrades++;
					dayTrades++;
					if (dayTrades>=maxTrades) tradeMode=2;
				}else if (lastL!=-1 && q.getLow5()<=lastL){
					int valueTP = q1.getOpen5()-breakDirection*tp;
					int valueSL = q1.getOpen5()+breakDirection*sl;
					//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
					TradingUtils.getMaxMinShortTPSL(data, qm, cal1, i+1, i+nbars,valueTP,valueSL);
					int maxH = qm.getHigh5()-q1.getOpen5();
					int diffL = q1.getOpen5()-qm.getClose5();
					if (breakDirection==-1){
						maxH = q1.getOpen5()-qm.getLow5();
						diffL = qm.getClose5()-q1.getOpen5();
					}
					totalPips += (diffL);
					totalAdverse += maxH;
					int win = -1;
					if (diffL>=0){
						win = 1;
						wins++;
						winPips += diffL;
					}else{
						lostPips += -diffL;
					}
					totalTrades++;
					dayTrades++;
					if (dayTrades>=maxTrades) tradeMode=2;
				}
				
				if (max==-1 || q.getHigh5()>=max){
					max = q.getHigh5();
				}
				
				if (min==-1 || q.getLow5()<=min){
					min = q.getLow5();
				}
				
			
			}
			
			double avg = totalPips*1.0/totalTrades;
			double avgAdverse = totalAdverse*1.0/totalTrades;
			double winPer = wins*100.0/totalTrades;
			double pf = winPips*1.0/lostPips;
					
			System.out.println(
					h1+" "+h2
					+" "+thr
					+" "+nbars
					+" "+minPull
					+" "+tp
					+" "+sl
					+" || "
					+" "+totalTrades
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(avgAdverse, false)
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					);
		}
	 
	 
	 public static void doTradePullsCloseES(ArrayList<QuoteShort> data,
			 ArrayList<QuoteShort> maxMins,
			 int y1,int y2,
			 int h1,int h2,int thr,int nbars,
			 int tp,int sl,
			 int minPull,
			 int maxTrades,
			 int breakDirection
			 ){
			
		 	int winPips = 0;
		 	int lostPips = 0;
			int lastDay = -1;
			int max = -1;
			int min = -1;
			ArrayList<Double> diffs = new ArrayList<Double>();
			Calendar cal = Calendar.getInstance();
			Calendar cal1 = Calendar.getInstance();
			int totalPips = 0;
			int totalAdverse = 0;
			int totalTrades = 0;
			int wins = 0;
			int tradeRef = 0;
			int tradeMode = 0;
			int dayTrades = 0;
			QuoteShort qm = new QuoteShort();
			for (int i=0;i<data.size()-1;i++){
				QuoteShort q = data.get(i);
				QuoteShort q1 = data.get(i+1);
				QuoteShort.getCalendar(cal, q);
				int year = cal.get(Calendar.YEAR);
				int day = cal.get(Calendar.DAY_OF_YEAR);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				
				if (year<y1 || year>y2) continue;
				
				if (day!=lastDay){	
					tradeRef = -1;
					tradeMode = 0;
					max = -1;
					min = -1;
					dayTrades = 0;
					lastDay = day;
				}
				
				int maxMin = maxMins.get(i).getExtra();
				
				//actualizamos puntos de referencia
				if (h1<=h && h<=h2 && tradeMode<2){				
					if (maxMin>=thr){
						tradeRef = q.getHigh5();
						tradeMode=1;
						//System.out.println("maxMin "+maxMin+" "+thr+" "+q.toString());
					}else if (maxMin<=-thr){
						tradeRef = q.getLow5();
						tradeMode=-1;
					}
				}	
				
				//vemos si hay pull
				if (tradeMode==1){ //long
					int diff = tradeRef - q1.getOpen5();
					if (diff>=minPull){
						int valueTP = q1.getOpen5()+breakDirection*tp*25;
						int valueSL = q1.getOpen5()-breakDirection*sl*25;
						//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
						TradingUtils.getMaxMinShortTPSL(data, qm, cal1, i+1, i+nbars,valueTP,valueSL);
						int maxL =q1.getOpen5()-qm.getLow5();
						int diffH = qm.getClose5()-q1.getOpen5();
						if (breakDirection==-1){
							maxL = qm.getHigh5()-q1.getOpen5();
							diffH = q1.getOpen5()-qm.getClose5();
						}
						//int diffL = q1.getOpen5()-qm.getClose5();
						totalPips += (diffH);
						totalAdverse += maxL;
						int win = -1;
						if (diffH>=0){
							win = 1;
							wins++;
							winPips+=diffH;
						}else{
							lostPips+=-diffH;
						}
						totalTrades++;
						dayTrades++;
						if (dayTrades>=maxTrades) tradeMode=2;
						//System.out.println("[LONG] "+tradeRef+" "+q1.getOpen5()+" "+valueTP+" "+qm.getClose5()+" || "+diffH+" "+maxL+" || "+win);
					}				
				}else if (tradeMode==-1){//short
					int diff = q1.getOpen5()-tradeRef;
					if (diff>=minPull){
						int valueTP = q1.getOpen5()-breakDirection*tp*25;
						int valueSL = q1.getOpen5()+breakDirection*sl*25;
						//TradingUtils.getMaxMinShort(data, qm, cal1, i+1, i+nbars);
						TradingUtils.getMaxMinShortTPSL(data, qm, cal1, i+1, i+nbars,valueTP,valueSL);
						int maxH = qm.getHigh5()-q1.getOpen5();
						int diffL = q1.getOpen5()-qm.getClose5();
						if (breakDirection==-1){
							maxH = q1.getOpen5()-qm.getLow5();
							diffL = qm.getClose5()-q1.getOpen5();
						}
						totalPips += (diffL);
						totalAdverse += maxH;
						int win = -1;
						if (diffL>=0){
							win = 1;
							wins++;
							winPips += diffL;
						}else{
							lostPips += -diffL;
						}
						totalTrades++;
						dayTrades++;
						if (dayTrades>=maxTrades) tradeMode=2;
						
						//System.out.println("[SHORT] "+tradeRef+" "+q1.getOpen5()+" "+valueTP+" "+qm.getClose5()+" || "+diffL+" || "+win);
					}	
				}			
			}
			
			double avg = (totalPips/25.0)/totalTrades;
			double avgAdverse = (totalAdverse/25.0)/totalTrades;
			double winPer = wins*100.0/totalTrades;
			double pf = winPips*1.0/lostPips;
					
			System.out.println(
					h1+" "+h2
					+" "+thr
					+" "+nbars
					+" "+minPull
					+" "+tp
					+" "+sl
					+" || "
					+" "+totalTrades
					+" "+PrintUtils.Print2dec(avg, false)
					+" "+PrintUtils.Print2dec(avgAdverse, false)
					+" "+PrintUtils.Print2dec(winPer, false)
					+" "+PrintUtils.Print2dec(pf, false)
					);
		}
	 
	 
	public static void main(String[] args) {
		
		String fileNameYM = "C:\\fxdata\\YM.txt";
		String fileNameES = "C:\\fxdata\\ES.txt";
		ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameYM, DataProvider.KIBOT);
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES, DataProvider.KIBOTES);
		ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
		
		System.out.println("Data: "+data.size());
		
		
		/*for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			System.out.println(q.toString()+" "+maxMins.get(i).getExtra());
		}*/
		double comm = 0.72;
		for (int y1=2009;y1<=2009;y1++){
			int y2 = y1+7;
			for (int h1=13;h1<=13;h1++){
				int h2 = h1+8;
				for (int thr=10000;thr<=10000;thr+=600){
					for (int nbars=0;nbars<=0;nbars+=100){
						for (int tp=1;tp<=80;tp++){
							for (int sl=2000;sl<=2000;sl+=1*tp){
								for (int offset=0;offset<=0;offset++){
									for (int maxTrades=30;maxTrades<=30;maxTrades++){
										//fxPivots.doTradePullsClose(data, maxMins, y1, y2, h1, h2, thr, nbars, tp, 0, maxTrades, -1);
										//fxPivots.doTradePullsClose(data, maxMins, y1, y2, h1, h2, thr, nbars,tp, sl, 0, maxTrades, -1);
										fxPivots.doTrade(data, maxMins, y1, y2, h1, h2, thr, nbars,tp, sl,offset, maxTrades,comm,1);
										//fxPivots.doTradePullHL(data, maxMins, y1, y2, h1, h2, thr, nbars,tp, sl, 0, maxTrades, -1);
										//fxPivots.doTradePullsCloseES(data, maxMins, y1, y2, h1, h2, thr, nbars,tp, sl, 0, maxTrades, -1);
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
