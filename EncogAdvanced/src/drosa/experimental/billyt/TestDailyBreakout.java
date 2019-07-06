package drosa.experimental.billyt;

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

public class TestDailyBreakout {
	
	
	public static void testBreakReverse(ArrayList<QuoteShort> data,int tp,int sl,int maxAttempts,boolean modeContinuation){
		
		int max = -1;
		int min = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			
			if (max==-1 || q.getHigh5()>=max) max=-1;
			if (min==-1 || q.getLow5()<=min) min=-1;
		}
	}
	
	
	/**
	 * Usando intraday data
	 * @param data
	 * @param begin
	 * @param end
	 * @param minPips
	 */
	public static void testMonthlyBreakMin(ArrayList<QuoteShort> data,int begin,int end,int h1,int h2,int minPips,int offset){
		
		ArrayList<Integer> hwins = new ArrayList<Integer>();
		ArrayList<Integer> hlosses = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			hwins.add(0);
			hlosses.add(0);
		}
		
		if (begin<1) begin = 1;
		if (end>data.size()-1) end = data.size()-1;
		double accWinPips = 0;
		double accLostPips = 0;
		double totalPips = 0;
		int total = 0;	
		int wins = 0;
		int maxLosses = 0;
		int lastMonth = -1;
		int max = -1;
		int min = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int pointLong = -1;
		int pointShort = -1;
		int isShort = 0;//0: no activada, -1:activado 1:win
		int isLong = 0;
		int hLActivated = -1;
		int hSActivated = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (month!=lastMonth){	
				//System.out.println("NUEVO DIA: "+DateUtils.datePrint(cal)+" "+q.toString());
				if (lastMonth!=-1){
					double pips = 0;
					if (isLong!=0 && hLActivated>=h1 && hLActivated<=h2){
						total++;
						if (isLong==1){
							pips = minPips;
							wins++;
						}
						else{
							pips = (q1.getClose5()-pointLong)*0.1;							
							//accLostPips+= (q1.getClose5()-pointLong)*0.1;
						}
					}
					if (isShort!=0 && hSActivated>=h1 && hSActivated<=h2){
						total++;
						if (isShort==1){
							pips = minPips;
							wins++;
						}
						else{							
							pips = (pointShort-q1.getClose5())*0.1;
							
							//accLostPips+= (pointShort-q1.getClose5())*0.1;
						}
					}			
					
					totalPips+=pips;
					if (pips>=0) accWinPips+=pips;
					else accLostPips+=Math.abs(pips);
					
					lastHigh = max;
					lastLow = min;
					pointLong = lastHigh+offset*10;
					pointShort = lastLow-offset*10;
					
					if (pointLong<q.getOpen5()) pointLong = lastHigh;
					if (pointShort>q.getOpen5()) pointShort = lastLow;
					//System.out.println(DateUtils.datePrint(cal1)+" "+lastHigh+" "+lastLow+" "+total+" "+wins+" "+isLong+" "+isShort+" "+pips+" "+PrintUtils.Print2(accLostPips)+" "+q1.getClose5());
				}			
				isShort = 0;
				isLong = 0;
				min = 999999;
				max = -999999;
				lastMonth = month;
				hLActivated = -1;
				hSActivated = -1;
			}
			
			if (lastHigh!=-1 && lastLow!=-1){
				
				int diffH = q.getHigh5()-pointLong;
				int diffL = pointShort-q.getLow5();
				if (isLong!=1){//no ganado
					if (diffH>=minPips*10) isLong = 1; //win
					else if (diffH>=0) isLong = -1;
					if (hLActivated==-1 && isLong!=0) hLActivated = h;
				}
				if (isShort!=1){// no ganado
					if (diffL>=minPips*10) isShort=1; //win
					else if (diffL>=0) isShort= -1;
					if (hSActivated==-1 && isShort!=0) hSActivated = h;
				}
			}
									
			if (q.getHigh5()>max){
				max = q.getHigh5();
				//System.out.println("[MAX] "+DateUtils.datePrint(cal)+" "+max);
			}
			if (q.getLow5()<min){
				min = q.getLow5();
				//System.out.println("[MIN] "+DateUtils.datePrint(cal)+" "+min);
			}
		}
		
		//double totalPips = accWinPips-Math.abs(accLostPips);
				double avgPips = totalPips*1.0/total;
				int losses = total-wins;
				double pf = Math.abs(accWinPips*1.0/accLostPips);
				double winPer = wins*100.0/total;
				System.out.println(
						"MONTH"
						+" "+minPips
						+" "+offset
						+" "+h1+" "+h2
						+" || "+total+" "+wins+" "+losses
						//+" "+PrintUtils.Print2(accWinPips)+" "+PrintUtils.Print2(accLostPips)+" "+PrintUtils.Print2(totalPips)
						+" "+PrintUtils.Print2(avgPips)
						+" "+PrintUtils.Print2(pf)
						//+" "+totalIB
						+" "+PrintUtils.Print2(winPer)
						+" "+maxLosses
						//+" "+PrintUtils.Print2(winIBPer)
						);
	}
	
	/**
	 * Usando intraday data
	 * @param data
	 * @param begin
	 * @param end
	 * @param minPips
	 */
	public static void testWeeklyBreakMin(ArrayList<QuoteShort> data,int begin,int end,int h1,int h2,int minPips,int offset){
		
		ArrayList<Integer> hwins = new ArrayList<Integer>();
		ArrayList<Integer> hlosses = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			hwins.add(0);
			hlosses.add(0);
		}
		
		if (begin<1) begin = 1;
		if (end>data.size()-1) end = data.size()-1;
		double accWinPips = 0;
		double accLostPips = 0;
		double totalPips = 0;
		int total = 0;	
		int wins = 0;
		int maxLosses = 0;
		int lastWeek = -1;
		int max = -1;
		int min = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int pointLong = -1;
		int pointShort = -1;
		int isShort = 0;//0: no activada, -1:activado 1:win
		int isLong = 0;
		int hLActivated = -1;
		int hSActivated = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (week!=lastWeek){	
				//System.out.println("NUEVO DIA: "+DateUtils.datePrint(cal)+" "+q.toString());
				if (lastWeek!=-1){
					double pipsL = 0;
					double pipsS = 0;
					if (isLong!=0 && hLActivated>=h1 && hLActivated<=h2){
						total++;
						if (isLong==1){
							pipsL = minPips;
							wins++;
						}
						else{
							pipsL = (q1.getClose5()-pointLong)*0.1;							
							//accLostPips+= (q1.getClose5()-pointLong)*0.1;
						}
					}
					if (isShort!=0 && hSActivated>=h1 && hSActivated<=h2){
						total++;
						if (isShort==1){
							pipsS = minPips;
							wins++;
						}
						else{							
							pipsS = (pointShort-q1.getClose5())*0.1;
							
							//accLostPips+= (pointShort-q1.getClose5())*0.1;
						}
					}			
					
					totalPips+=pipsL;
					if (pipsL>=0){
						accWinPips+=pipsL;
					}
					else{
						accLostPips+=Math.abs(pipsL);
					}
					totalPips+=pipsS;
					if (pipsS>=0){
						accWinPips+=pipsS;
					}
					else{
						accLostPips+=Math.abs(pipsS);
					}
					
					lastHigh = max;
					lastLow = min;
					pointLong = lastHigh+offset*10;
					pointShort = lastLow-offset*10;
					
					if (pointLong<q.getOpen5()) pointLong = lastHigh;
					if (pointShort>q.getOpen5()) pointShort = lastLow;
					//System.out.println(DateUtils.datePrint(cal1)+" "+lastHigh+" "+lastLow+" "+total+" "+wins+" "+isLong+" "+isShort+" "+pips+" "+PrintUtils.Print2(accLostPips)+" "+q1.getClose5());
				}			
				isShort = 0;
				isLong = 0;
				min = 999999;
				max = -999999;
				lastWeek = week;
				hLActivated = -1;
				hSActivated = -1;
			}
			
			if (lastHigh!=-1 && lastLow!=-1){
				
				int diffH = q.getHigh5()-pointLong;
				int diffL = pointShort-q.getLow5();
				if (isLong!=1){//no ganado
					if (diffH>=minPips*10) isLong = 1; //win
					else if (diffH>=0) isLong = -1;
					if (hLActivated==-1 && isLong!=0) hLActivated = h;
				}
				if (isShort!=1){// no ganado
					if (diffL>=minPips*10) isShort=1; //win
					else if (diffL>=0) isShort= -1;
					if (hSActivated==-1 && isShort!=0) hSActivated = h;
				}
			}
									
			if (q.getHigh5()>max){
				max = q.getHigh5();
				//System.out.println("[MAX] "+DateUtils.datePrint(cal)+" "+max);
			}
			if (q.getLow5()<min){
				min = q.getLow5();
				//System.out.println("[MIN] "+DateUtils.datePrint(cal)+" "+min);
			}
		}
		
		//double totalPips = accWinPips-Math.abs(accLostPips);
		double avgPips = totalPips*1.0/total;
		int losses = total-wins;
		double pf = Math.abs(accWinPips*1.0/accLostPips);
		double winPer = wins*100.0/total;
		System.out.println(
				"WEEKLY"
				+" "+minPips
				+" "+offset
				+" "+h1+" "+h2
				+" || "+total+" "+wins+" "+losses
				//+" "+PrintUtils.Print2(accWinPips)+" "+PrintUtils.Print2(accLostPips)+" "+PrintUtils.Print2(totalPips)
				+" "+PrintUtils.Print2(avgPips)
				+" "+PrintUtils.Print2(pf)
				//+" "+totalIB
				+" "+PrintUtils.Print2(winPer)
				+" "+maxLosses
				//+" "+PrintUtils.Print2(winIBPer)
				);
	}
	
	/**
	 * Usando intraday data
	 * @param data
	 * @param begin
	 * @param end
	 * @param minPips
	 */
	public static void testDailyBreakMin(ArrayList<QuoteShort> data,int begin,int end,int h1,int h2,int minPips,int offset){
		
		ArrayList<Integer> hwins = new ArrayList<Integer>();
		ArrayList<Integer> hlosses = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			hwins.add(0);
			hlosses.add(0);
		}
		
		if (begin<1) begin = 1;
		if (end>data.size()-1) end = data.size()-1;
		double accWinPips = 0;
		double accLostPips = 0;
		double totalPips = 0;
		int total = 0;	
		int wins = 0;
		int maxLosses = 0;
		int lastDay = -1;
		int max = -1;
		int min = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int pointLong = -1;
		int pointShort = -1;
		int isShort = 0;//0: no activada, -1:activado 1:win
		int isLong = 0;
		int hLActivated = -1;
		int hSActivated = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){	
				//System.out.println("NUEVO DIA: "+DateUtils.datePrint(cal)+" "+q.toString());
				if (lastDay!=-1){
					double pipsL = 0;
					double pipsS = 0;
					if (isLong!=0 && hLActivated>=h1 && hLActivated<=h2){
						total++;
						if (isLong==1){
							pipsL = minPips;
							wins++;
						}
						else{
							pipsL = (q1.getClose5()-pointLong)*0.1;							
							//accLostPips+= (q1.getClose5()-pointLong)*0.1;
						}
					}
					if (isShort!=0 && hSActivated>=h1 && hSActivated<=h2){
						total++;
						if (isShort==1){
							pipsS = minPips;
							wins++;
						}
						else{							
							pipsS = (pointShort-q1.getClose5())*0.1;
							
							//accLostPips+= (pointShort-q1.getClose5())*0.1;
						}
					}			
					
					totalPips+=pipsL;
					if (pipsL>=0) accWinPips+=pipsL;
					else accLostPips+=Math.abs(pipsL);
					
					totalPips+=pipsS;
					if (pipsS>=0) accWinPips+=pipsS;
					else accLostPips+=Math.abs(pipsS);
					
					lastHigh = max;
					lastLow = min;
					pointLong = lastHigh+offset*10;
					pointShort = lastLow-offset*10;
					
					if (pointLong<q.getOpen5()) pointLong = lastHigh;
					if (pointShort>q.getOpen5()) pointShort = lastLow;
					/*System.out.println(DateUtils.datePrint(cal1)+" "+lastHigh+" "+lastLow
							+" "+total+" "+wins+" "+isLong+" "+isShort
							+" "+PrintUtils.Print2(pipsL+pipsS)+" "+PrintUtils.Print2(totalPips)
							+" "+hLActivated+" "+hSActivated
							);*/
				}			
				isShort = 0;
				isLong = 0;
				min = 999999;
				max = -999999;
				lastDay = day;
				hLActivated = -1;
				hSActivated = -1;
			}
			
			if (lastHigh!=-1 && lastLow!=-1){
				
				int diffH = q.getHigh5()-pointLong;
				int diffL = pointShort-q.getLow5();
				if (isLong!=1){//no ganado
					if (diffH>=minPips*10) isLong = 1; //win
					else if (diffH>=0) isLong = -1;
					if (hLActivated==-1 && isLong!=0) hLActivated = h;
				}
				if (isShort!=1){// no ganado
					if (diffL>=minPips*10) isShort=1; //win
					else if (diffL>=0) isShort= -1;
					if (hSActivated==-1 && isShort!=0) hSActivated = h;
				}
			}
									
			if (q.getHigh5()>max){
				max = q.getHigh5();
				//System.out.println("[MAX] "+DateUtils.datePrint(cal)+" "+max);
			}
			if (q.getLow5()<min){
				min = q.getLow5();
				//System.out.println("[MIN] "+DateUtils.datePrint(cal)+" "+min);
			}
		}
		
		//double totalPips = accWinPips-Math.abs(accLostPips);
		double avgPips = totalPips*1.0/total;
		int losses = total-wins;
		double pf = Math.abs(accWinPips*1.0/accLostPips);
		double winPer = wins*100.0/total;
		System.out.println(
				"DAILY "
				+" "+minPips
				+" "+offset
				+" "+h1+" "+h2
				+" || "+total+" "+wins+" "+losses
				//+" "+PrintUtils.Print2(accWinPips)+" "+PrintUtils.Print2(accLostPips)+" "+PrintUtils.Print2(totalPips)
				+" "+PrintUtils.Print2(avgPips)
				+" "+PrintUtils.Print2(pf)
				//+" "+totalIB
				+" "+PrintUtils.Print2(winPer)
				+" "+maxLosses
				//+" "+PrintUtils.Print2(winIBPer)
				);
	}
	
	public static void testDailyBreak(ArrayList<QuoteShort> days,int begin,int end,int minPips){
		
		if (begin<2) begin = 2;
		if (end>days.size()-1) end = days.size()-1;
		
		int accLostPips = 0;
		int total = 0;
		int totalIB = 0;
		int totalIBTouched = 0;
		int wins = 0;
		int winsIB = 0;
		int actualLosses = 0;
		int maxLosses = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort day2 = days.get(i-2);
			QuoteShort day1 = days.get(i-1);
			QuoteShort day0 = days.get(i);
			QuoteShort.getCalendar(cal, day0);
			boolean insideBar = false;
			
			//last
			int diffH1 = day1.getHigh5()-day2.getHigh5();
			int diffL1 = day2.getLow5()-day1.getLow5();
			//actual
			int diffH = day0.getHigh5()-day1.getHigh5();
			int diffL = day1.getLow5()-day0.getLow5();
			
						
			if (diffL1<=0 && diffH1<=0){
				insideBar=true;
				totalIB++;
			}
			
			//System.out.println(day1.toString()+" "+insideBar);
			
			if (diffH>=0){
				total++;
				if (insideBar) totalIBTouched++; 
				if (diffH>=minPips*10){
					wins++;
					if (insideBar) winsIB++;
					if (actualLosses>maxLosses) maxLosses++;
					actualLosses = 0;
				}else{
					double res = day0.getClose5()-day1.getHigh5();
					accLostPips+=Math.abs(res)*0.1;
					actualLosses++;
					//System.out.println(DateUtils.datePrint(cal)+" "+PrintUtils.Print2(Math.abs(res)*0.1));
				}
			}
			if (diffL>=0){
				total++;
				if (insideBar) totalIBTouched++;
				if (diffL>=minPips*10){
					wins++;
					if (insideBar) winsIB++;
					if (actualLosses>maxLosses) maxLosses++;
					actualLosses = 0;
				}else{
					double res = day1.getLow5()-day0.getClose5();
					accLostPips+=Math.abs(res)*0.1;
					actualLosses++;
					//System.out.println(DateUtils.datePrint(cal)+" "+PrintUtils.Print2(Math.abs(res)*0.1)+" "+day0.getClose5());
				}
			}			
		}
		
		int accWinPips = wins*minPips;
		int totalPips = accWinPips-accLostPips;
		double avgPips = totalPips*1.0/total;
		int losses = total-wins;
		double pf = accWinPips*1.0/accLostPips;
		double winPer = wins*100.0/total;
		double winIBPer = winsIB*100.0/totalIBTouched;
		System.out.println(minPips
				+" || "+total+" "+wins+" "+losses+" ("+accLostPips+") "
				+" "+PrintUtils.Print2(avgPips)
				+" "+PrintUtils.Print2(pf)
				//+" "+totalIB
				+" "+PrintUtils.Print2(winPer)
				+" "+maxLosses
				//+" "+PrintUtils.Print2(winIBPer)
				);
	}
	
	
	public static void testBreak(ArrayList<QuoteShort> data,int minPips,
			int h1,int h2,
			int tp,int sl,boolean breakMode){
		
		//stats
		int wins = 0;
		int losses = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<PositionShort> closedPositions = new ArrayList<PositionShort>();
		int max = 0;
		int min = 999999;
		int lastMax = 0;
		int lastMin = 999999;
		int lastDay = -1;
		int order = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				if (lastDay!=-1){
					lastMax = max;
					lastMin = min;
					max = 0;
					min = 999999;
				}
				lastDay = day;
				order = 0;
			}
			
			int diffHigh = q.getHigh5()-lastMax;
			int diffLow = lastMin-q.getLow5();
			int entry = -1;
			int slValue = -1;
			int tpValue = -1;
			PositionType positionType = PositionType.NONE;
			if (diffHigh>=minPips*10 
					&& q.getOpen5()<=lastMax 
					//&& PositionShort.countTotal(positions, PositionStatus.OPEN)==0
					){
				entry = q.getHigh5();
				slValue = entry-sl*10;
				tpValue = entry+tp*10;
				positionType = PositionType.LONG;
				
				if (!breakMode){
					slValue = entry+sl*10;
					tpValue = entry-tp*10;
					positionType = PositionType.SHORT;
				}
				
				order = Math.abs(order)+1;
			}else if (diffLow>=minPips*10 
					&& q.getOpen5()>=lastMin
					//&& PositionShort.countTotal(positions, PositionStatus.OPEN)==0
					){
				entry = q.getLow5();
				slValue = entry+sl*10;
				tpValue = entry-tp*10;
				positionType = PositionType.SHORT;
				
				if (!breakMode){
					slValue = entry-sl*10;
					tpValue = entry+tp*10;
					positionType = PositionType.LONG;
				}
				order = -(Math.abs(order)+1); //los low van en -
			}

			if (entry!=-1){
				PositionShort pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(slValue);
				pos.setTp(tpValue);
				pos.setPositionType(positionType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
				pos.setOpenIndex(i);
				pos.setDayOrder(order);
				positions.add(pos);
			}
			
			int s = 0;
			//if (positions.size()>0) System.out.println(q1.toString());
			while (s<positions.size()){
				PositionShort p = positions.get(s);
				boolean removed = false;
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					boolean closed = false;
					double posPips = 0;
					if (p.getPositionType()==PositionType.LONG){
						if (q.getHigh5()>=p.getTp()){//funciona porque vamos en esa direccion
							p.setPositionStatus(PositionStatus.CLOSE);
							closed  = true;
							posPips = p.getTp()-p.getEntry();
						}else if (q.getLow5()<=p.getSl() 
								&& (i)>p.getOpenIndex()
								){//optimista
							p.setPositionStatus(PositionStatus.CLOSE);
							closed  = true;
							posPips = p.getSl()-p.getEntry();
						}						
					}else if (p.getPositionType()==PositionType.SHORT){
						if (q.getLow5()<=p.getTp()){//funciona porque vamos en esa direccion
							p.setPositionStatus(PositionStatus.CLOSE);
							closed  = true;
							posPips = p.getEntry()-p.getTp();
						}else if (q.getHigh5()>=p.getSl() 
								&& (i)>p.getOpenIndex()
								){//optimista
							p.setPositionStatus(PositionStatus.CLOSE);
							closed  = true;
							posPips = p.getEntry()-p.getSl();
						}							
					}
																				
					if (closed){
						double pipsEarned = posPips*1.0/10;
						p.setWinPips(pipsEarned);
						//System.out.println("pipsearned before comission after comm: "+posPips+" "+pipsEarned);
						if (pipsEarned>=0){
							wins++;
							p.setWin(1);
							
						}else{
							losses++;
							p.setWin(-1);
						}
						/*System.out.println("[CLOSED] "
								+" "+DateUtils.datePrint(p.getOpenCal())
								+" "+DateUtils.datePrint(cal)
								+" "+lastMax+" "+lastMin
								+" "+p.getPositionType()+" "+p.getWin()
								);*/
											
						PositionShort closedPos = new PositionShort();
						closedPos.copy(p);
						closedPositions.add(closedPos);
						
						positions.remove(s);//borramos y no avanzamos
						removed = true;
					}else{//notClosed
						//s++;//avanzamos
					}
				}
				
				if (!removed){
					s++;
				}
			}//positions
			
			if (max<q.getHigh5()) max = q.getHigh5();
			if (min>q.getLow5()) min = q.getLow5();
		}
		//
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double lossPer = 100.0-winPer;
		double avgPips = (tp*winPer-sl*lossPer)/100.0;
		System.out.println(
				minPips
				+" "+tp+" "+sl
				+" || "
				+total
				+" "+PrintUtils.Print2dec(winPer, false, 2)
				+" "+PrintUtils.Print2dec(avgPips, false, 2)
				);
		
		for (int o=1;o<=1;o++){
			//studyPositions(closedPositions,h1,h2,tp,sl,o);
		}
	}
	
	public static void testBreak2(ArrayList<QuoteShort> data,int minPips,
			int h1,int h2,
			int tp,int sl,boolean breakMode){
		
		//stats
		int wins = 0;
		int losses = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<PositionShort> closedPositions = new ArrayList<PositionShort>();
		int max = 0;
		int min = 999999;
		int lastMax = 0;
		int lastMin = 999999;
		int lastDay = -1;
		int order = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				if (lastDay!=-1){
					lastMax = max;
					lastMin = min;
					max = 0;
					min = 999999;
				}
				lastDay = day;
				order = 0;
			}
			
			int maxThr = lastMax+minPips*10;
			int minThr = lastMin-minPips*10;

			int entry = -1;
			int slValue = -1;
			int tpValue = -1;
			PositionType positionType = PositionType.NONE;
			if (q.getOpen5()>=maxThr
					//&& q.getOpen5()<=lastMax 
					//&& PositionShort.countTotal(positions, PositionStatus.OPEN)==0
					){
				entry = q.getOpen5();
				slValue = entry-sl*10;
				tpValue = entry+tp*10;
				positionType = PositionType.LONG;
				
				if (!breakMode){
					slValue = entry+sl*10;
					tpValue = entry-tp*10;
					positionType = PositionType.SHORT;
				}
				
				order = Math.abs(order)+1;
			}else if (q.getOpen5()<=minThr
					//&& PositionShort.countTotal(positions, PositionStatus.OPEN)==0
					){
				entry = q.getOpen5();
				slValue = entry+sl*10;
				tpValue = entry-tp*10;
				positionType = PositionType.SHORT;
				
				if (!breakMode){
					slValue = entry-sl*10;
					tpValue = entry+tp*10;
					positionType = PositionType.LONG;
				}
				order = -(Math.abs(order)+1); //los low van en -
			}

			if (entry!=-1){
				PositionShort pos = new PositionShort();
				pos.setEntry(entry);
				pos.setSl(slValue);
				pos.setTp(tpValue);
				pos.setPositionType(positionType);
				pos.setPositionStatus(PositionStatus.OPEN);
				pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
				pos.setOpenIndex(i);
				pos.setDayOrder(order);
				positions.add(pos);
			}
			
			int s = 0;
			//if (positions.size()>0) System.out.println(q1.toString());
			while (s<positions.size()){
				PositionShort p = positions.get(s);
				boolean removed = false;
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					boolean closed = false;
					double posPips = 0;
					if (p.getPositionType()==PositionType.LONG){
						if (q.getHigh5()>=p.getTp()){//funciona porque vamos en esa direccion
							p.setPositionStatus(PositionStatus.CLOSE);
							closed  = true;
							posPips = p.getTp()-p.getEntry();
						}else if (q.getLow5()<=p.getSl() 
								//&& (i)>p.getOpenIndex()
								){//optimista
							p.setPositionStatus(PositionStatus.CLOSE);
							closed  = true;
							posPips = p.getSl()-p.getEntry();
						}						
					}else if (p.getPositionType()==PositionType.SHORT){
						if (q.getLow5()<=p.getTp()){//funciona porque vamos en esa direccion
							p.setPositionStatus(PositionStatus.CLOSE);
							closed  = true;
							posPips = p.getEntry()-p.getTp();
						}else if (q.getHigh5()>=p.getSl() 
								//&& (i)>p.getOpenIndex()
								){//optimista
							p.setPositionStatus(PositionStatus.CLOSE);
							closed  = true;
							posPips = p.getEntry()-p.getSl();
						}							
					}
																				
					if (closed){
						double pipsEarned = posPips*1.0/10;
						p.setWinPips(pipsEarned);
						//System.out.println("pipsearned before comission after comm: "+posPips+" "+pipsEarned);
						if (pipsEarned>=0){
							wins++;
							p.setWin(1);
							
						}else{
							losses++;
							p.setWin(-1);
						}
						/*System.out.println("[CLOSED] "
								+" "+DateUtils.datePrint(p.getOpenCal())
								+" "+DateUtils.datePrint(cal)
								+" "+lastMax+" "+lastMin
								+" "+p.getPositionType()+" "+p.getWin()
								);*/
											
						PositionShort closedPos = new PositionShort();
						closedPos.copy(p);
						closedPositions.add(closedPos);
						
						positions.remove(s);//borramos y no avanzamos
						removed = true;
					}else{//notClosed
						//s++;//avanzamos
					}
				}
				
				if (!removed){
					s++;
				}
			}//positions
			
			if (max<q.getHigh5()) max = q.getHigh5();
			if (min>q.getLow5()) min = q.getLow5();
		}
		//
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double lossPer = 100.0-winPer;
		double avgPips = (tp*winPer-sl*lossPer)/100.0;
		/*System.out.println(
				minPips
				+" "+tp+" "+sl
				+" || "
				+total
				+" "+PrintUtils.Print2dec(winPer, false, 2)
				+" "+PrintUtils.Print2dec(avgPips, false, 2)
				);*/
		
		for (int o=1;o<=30;o++){
			studyPositions(closedPositions,h1,h2,tp,sl,o);
		}
	}

	private static void studyPositions(
			ArrayList<PositionShort> positions,int h1,int h2,int tp,int sl, int order) {
		// TODO Auto-generated method stub
		int wins = 0;
		int losses = 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			int h = p.getOpenCal().get(Calendar.HOUR_OF_DAY);
			if (p.getPositionStatus()==PositionStatus.CLOSE){
				if (Math.abs(p.getDayOrder())==order && h1<=h && h<=h2){
					if (p.getWin()==1) wins++;
					if (p.getWin()==-1) losses++;
				}
			}
		}
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double lossPer = 100.0-winPer;
		double avgPips = (winPer*tp-lossPer*sl)*1.0/100.0;
		System.out.println(
				order
				+" "+tp
				+" "+sl
				+" || "+total
				+" "+PrintUtils.Print2dec(winPer, false, 2)
				+" "+PrintUtils.Print2dec(avgPips, false, 2)
				);
	}

	private static void testDailyData(ArrayList<QuoteShort> dailyData,int minPips) {
		// TODO Auto-generated method stub
		
		int touches = 0;
		int wins = 0;
		int total = 0;
		int dayTouched = 0;
		for (int i=1;i<dailyData.size();i++){
			QuoteShort q_1 = dailyData.get(i-1);
			QuoteShort q = dailyData.get(i);
			total++;
			int diffH = q.getHigh5()-q_1.getHigh5();
			int diffL = q_1.getLow5()-q.getLow5();
			boolean isTouched = false;
			if (diffH>=0){
				touches++;
				if (diffH>=minPips*10){
					wins++;
				}
				isTouched = true;
			}
			if (diffL>=0){
				touches++;
				if (diffL>=minPips*10){
					wins++;
				}
				isTouched = true;
			}
			
			if (isTouched){
				dayTouched++;
			}else{
				//System.out.println(q_1.toString()+" || "+q.toString());
			}
		}
		
		double dayTouchedPer = dayTouched*100.0/total;
		double winPer = wins*100.0/touches;
		System.out.println(
				total+" "+dayTouched+" "+(total-dayTouched)
				+" "+minPips
				+" || "
				+PrintUtils.Print2dec(dayTouchedPer, false, 2)
				+" "+PrintUtils.Print2dec(winPer, false, 2)
				);
		
	}
	
	private static void testDailyDataH(ArrayList<QuoteShort> hData,int minPips) {
		// TODO Auto-generated method stub
		ArrayList<Integer> hTouches = new ArrayList<Integer>();
		ArrayList<Integer> hWins = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			hTouches.add(0);
			hWins.add(0);
		}
		
		int touches2 = 0;
		int wins0=0;
		int touches = 0;
		int wins = 0;
		int total = 0;
		int dayTouched = 0;
		
		int lastDay = -1;
		int lastMax = -1;
		int lastMin = -1;
		int max = -1;
		int min = -1;
		int lowHTouched = -1;
		int highHTouched = -1;
		boolean highTouched = false;
		boolean lowTouched = false;
		boolean highWin = false;
		boolean lowWin = false;
		boolean isTouched = false;
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<hData.size();i++){
			QuoteShort q = hData.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			//System.out.println(DateUtils.datePrint(cal)+" "+h);
			
			if (day!=lastDay){
				if (lastDay!=-1){
					lastMax = max;
					lastMin = min;
					if (isTouched){
						dayTouched++;
					}
					if (highTouched && lowTouched){
						touches2++;
						if (!highWin && !lowWin) wins0++;
					}
				}
				max = -1;
				min = -1;
				lastDay = day;
				total++;
				highTouched = false;
				lowTouched = false;
				highWin = false;
				lowWin = false;
				isTouched = false;
			}
			
			if (lastMax!=-1 && lastMin!=-1){
				int diffH = q.getHigh5()-lastMax;
				int diffL = lastMin-q.getLow5();
				if (diffH>=0 && !highTouched){
					touches++;
					highTouched = true;
					isTouched = true;
					highHTouched = h;
					hTouches.set(h, hTouches.get(h)+1);
					//System.out.println("high touched: "+h+" "+hTouches.get(h));
				}
				if (diffL>=0 && !lowTouched){
					touches++;
					lowTouched = true;
					isTouched = true;
					lowHTouched = h;
					hTouches.set(h, hTouches.get(h)+1);
					//System.out.println("low touched: "+h+" "+hTouches.get(h));
				}
				if (!highWin && diffH>=minPips*10){
					highWin = true;
					wins++;
					hWins.set(highHTouched, hWins.get(highHTouched)+1);
					//System.out.println("high win: "+highHTouched+" "+hWins.get(highHTouched));
				}
				if (!lowWin && diffL>=minPips*10){
					lowWin = true;
					hWins.set(lowHTouched, hWins.get(lowHTouched)+1);
					wins++;
					//System.out.println("low win: "+lowHTouched+" "+hWins.get(lowHTouched));
				}
			}
			
			if (max==-1 || q.getHigh5()>max){
				max = q.getHigh5();
			}
			if (min==-1 || q.getLow5()<min){
				min = q.getLow5();
			}
		}
		
		double dayTouchedPer = dayTouched*100.0/total;
		double winPer = wins*100.0/touches;
		
		double win0Per = wins0*100.0/touches2;
		System.out.println(
				total+" "+dayTouched+" "+(total-dayTouched)
				+" "+minPips
				+" || "
				+PrintUtils.Print2dec(dayTouchedPer, false, 2)
				+" "+PrintUtils.Print2dec(winPer, false, 2)
				+" || "
				+" "+touches2
				+" "+wins0
				+" "+PrintUtils.Print2dec(win0Per, false, 2)
				);
		
		/*for (int i=0;i<=23;i++){
			int totalh = hTouches.get(i);
			int totalw = hWins.get(i);
			double winPerH = totalw*100.0/totalh;
			System.out.println(i+" || "+totalh+" "+totalw+" "+PrintUtils.Print2dec(winPerH, false, 2));
		}*/
	}
	
	public static double calculatePips(int number,int tp,int sl,boolean isTp){
		
		return number*tp;
	}
	
	public static void testBreakDetail(ArrayList<QuoteShort> data,int begin,int end,
			int aYear1,int aYear2,
			int correctedPips,
			int maxTouches,
			int tp,int sl,boolean debug){
		
		int totalTrades = 0;
		double winPips = 0;
		double lostPips = 0;
		ArrayList<Integer> totalDayTouches = new ArrayList<Integer>();
		for (int i=0;i<=50;i++) totalDayTouches.add(0);
		int touches = 0;
		int wins = 0;
		boolean isSpecial = false;
		int totalSpecial = 0;
		int totalSpecialWins = 0;
		int lastMax = -1;
		int lastMin = -1;
		int max = -1;
		int min = -1;
		int lastDay = -1;
		int openIndex = 0;
		PositionType positionType = PositionType.NONE;
		int entry = -1;
		int slValue = -1;
		int tpValue = -1;
		int dayTradesTpTotal = 0;
		int dailyTouches = 0;
		int dayTradesSl = 0;
		int dayTradesTp = 0;
		double actualPipValue = 1;
		boolean isTradeOpen = false;
		boolean isDayFinished = false;
		boolean isTpDay = false;
		boolean isDayTouched = false;
		double dailyLosses= 0;
		int total = 0;
		int tpDays = 0;//termina en tp
		boolean specialTrade = false;
		int actualWins = 0;
		int actualLosses = 0;
		int maxWins = 0;
		int maxLosses = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int year = cal.get(Calendar.YEAR);
			
			if (year<aYear1 || year>aYear2) continue;
			
			//determinar si estamos en nuevo dia
			if (day!=lastDay){
				if (lastDay!=-1){
					if (isTradeOpen){
						int gainPips = q.getOpen5()-entry;//caso buy
						if (positionType==PositionType.SHORT){
							gainPips = entry-q.getOpen5();
						}
						if (gainPips>=0){
							winPips += actualPipValue*Math.abs(gainPips*0.1);
							actualWins++;
							if (actualWins>=maxWins) maxWins = actualWins;
							actualLosses = 0;
							//System.out.println("WIN: "+gainPips);
						}
						else{
							lostPips += actualPipValue*Math.abs(gainPips*0.1);
							actualLosses++;
							if (actualLosses>=maxLosses) maxLosses = actualLosses;
							actualWins = 0;
							//System.out.println("LOST: "+gainPips);
						}
						if (debug)
							System.out.println("[CLOSE END DAY] "+positionType
									+" "+gainPips*0.1
									+" "+winPips
									+" "+lostPips
									);
					}
					//totalPips = 
					if (isTpDay && dailyTouches<=maxTouches){
						tpDays++;
						//if (dailyTouches==1) winPips+=tp;
						//winPips+=tp;
					}
					
					if (isDayTouched){
						if (!isTpDay){
							//double pips = calculatePips(dayTradesSl,tp,sl,false);
							//lostPips+=pips;
							if (debug)
							System.out.println("[FAILED DAY] touches sl lostPips totalWin totalLost: "
									+DateUtils.datePrint(cal)
									+" "+dailyTouches
									+" "+dayTradesSl
									+" "+dailyLosses
									+" "+winPips
									+" "+lostPips
									);
						}
						total++;
						dayTradesTpTotal += dailyTouches;
						totalDayTouches.set(dailyTouches, totalDayTouches.get(dailyTouches)+1);
					}
					lastMax = max;
					lastMin = min;
				}
				//dailywins
				dailyLosses = 0;
				actualPipValue = 1;
				dailyTouches = 0;
				dayTradesTp = 0;
				dayTradesSl = 0;
				//
				isDayTouched = false;
				isTradeOpen = false;
				isDayFinished = false;
				isTpDay = false;
				max = -1;
				min = -1;
				lastDay = day;
				
				if (debug)
					System.out.println("****[NEW DAY] "+DateUtils.datePrint(cal));
			}
			//determinar nueva posicion
			if (!isDayFinished 
					&& dailyTouches<maxTouches
					&& !isTradeOpen && lastMax!=-1 && lastMin!=-1){
				int diffH = q.getHigh5()-(lastMax+correctedPips);
				int diffL = (lastMin-correctedPips)-q.getLow5();
				if (diffH>=0 && q.getOpen5()<lastMax){
					isTradeOpen = true;
					isDayTouched = true;
					openIndex = i;
					positionType = PositionType.LONG;
					entry = lastMax+correctedPips;
					slValue = entry-sl*10;
					tpValue = entry+tp*10;
					if (debug)
						System.out.println("[OPEN] BUY "+lastMax+" "+i);
					touches++;
					dailyTouches++;
					totalTrades++;
					isSpecial = false;
				}
				
				if (diffL>=0 && q.getOpen5()>lastMin){
					isTradeOpen = true;
					isDayTouched = true;
					openIndex = i;
					positionType = PositionType.SHORT;
					entry = lastMin-correctedPips;
					slValue = entry+sl*10;
					tpValue = entry-tp*10;
					if (debug)
						System.out.println("[OPEN] SELL "+lastMin+" "+i);
					touches++;
					dailyTouches++;
					totalTrades++;
					isSpecial = false;
				}
			}
			//determinar estado de la posicion actual
			if (isTradeOpen){
				int win = 0;
				if (positionType==PositionType.LONG){
					if (q.getHigh5()>=tpValue){
						isTradeOpen = false;
						isDayFinished = true;
						isTpDay = true;
						dayTradesTp++;
						wins++;
						winPips +=actualPipValue*tp;
						if (isSpecial) totalSpecialWins++;
						win=1;
						if (debug)
							System.out.println("[CLOSE TP] BUY "+tpValue+" "+i
									+" || "+actualPipValue
									+" "+PrintUtils.Print2dec(actualPipValue*tp,false)
									+" "+PrintUtils.Print2dec(dailyLosses,false)
									+" || "+winPips
									+" "+lostPips
									+" || "+PrintUtils.Print2dec(winPips-lostPips,false)
									);
						//System.out.println("WIN "+tp*10);
					}else if (i>openIndex && q.getLow5()<=slValue){
						dayTradesSl++;
						isTradeOpen = false;
						lostPips +=actualPipValue*sl; 
						dailyLosses += actualPipValue*sl;
						//cambio del pipValue
						actualPipValue = dailyLosses*1.0/tp;
						win=-1;
						if (debug)
							System.out.println("[CLOSE SL] BUY "+slValue+" "+i+" || "+actualPipValue);
						
						//System.out.println("LOSS "+(-sl*10));
						//prueba abrimos nuevo desde aqui
						if (dailyTouches<maxTouches){
							isTradeOpen = true;
							openIndex = i;
							
							/*positionType = PositionType.LONG;
							entry = q.getClose5();
							slValue = entry-sl*10;
							tpValue = entry+tp*10;*/
							
							positionType = PositionType.SHORT;
							entry = q.getClose5();
							slValue = entry+sl*10;
							tpValue = entry-tp*10;
							
							totalSpecial++;
							isSpecial = true;
							totalTrades++;
							dailyTouches++;
							if (debug)
								System.out.println("[OPEN SPECIAL] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+i);
						}
						//
						
					}
				}
				if (positionType==PositionType.SHORT){
					if (q.getLow5()<=tpValue){
						isTradeOpen = false;
						isDayFinished = true;
						isTpDay = true;
						dayTradesTp++;
						wins++;
						winPips +=actualPipValue*tp;
						if (isSpecial) totalSpecialWins++;
						win=1;
						if (debug)
							System.out.println("[CLOSE TP] SELL "+tpValue+" "+i
									+" || "+actualPipValue
									+" "+PrintUtils.Print2dec(actualPipValue*tp,false)
									+" "+PrintUtils.Print2dec(dailyLosses,false)
									+" || "+winPips
									+" "+lostPips
									+" || "+PrintUtils.Print2dec(winPips-lostPips,false)
									);
						//System.out.println("WIN "+tp*10);
					}else if (i>openIndex && q.getHigh5()>=slValue){
						
						dayTradesSl++;
						isTradeOpen = false;
						lostPips +=actualPipValue*sl;
						dailyLosses += actualPipValue*sl;
						//cambio del pipValue
						actualPipValue = dailyLosses*1.0/tp;
						win=-1;
						if (debug)
							System.out.println("[CLOSE SL] SELL "+slValue+" "+i+" || "+actualPipValue);
						
						//System.out.println("LOSS "+(-sl*10));
						//ABRIMOS SELL especial
						if (dailyTouches<maxTouches){
							isTradeOpen = true;
							openIndex = i;
							
							/*positionType = PositionType.SHORT;
							entry = q.getClose5();
							slValue = entry+sl*10;
							tpValue = entry-tp*10;*/
							
							positionType = PositionType.LONG;
							entry = q.getClose5();
							slValue = entry-sl*10;
							tpValue = entry+tp*10;
							
							
							totalSpecial++;
							isSpecial = true;
							totalTrades++;
							dailyTouches++;
							if (debug)
								System.out.println("[OPEN SPECIAL] "+positionType+" "+entry+" "+tpValue+" "+slValue+" || "+i);
						}
						//
					}//else
				}//short
				if (win==1){
					actualWins++;
					if (actualWins>=maxWins) maxWins = actualWins;
					actualLosses = 0;
				}else if (win==-1){
					actualLosses++;
					if (actualLosses>=maxLosses) maxLosses = actualLosses;
					actualWins = 0;
				}
			}
			//determinar actual max && min
			if (max==-1 || q.getHigh5()>max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<min) min = q.getLow5();
		}
		
		String touchesStr="";
		for (int i=1;i<=5;i++){
			touchesStr+=" "+totalDayTouches.get(i);
		}
		
		double winPer = wins*100.0/totalTrades++;;
		double lossPer = 100.0-winPer;
		double avgPips = (winPer*tp-lossPer*sl)/100.0;
		double tpPer = tpDays*100.0/total;
		double avgPipsReal = (winPips-lostPips)*1.0/totalTrades++;;
		double winSpecialPer = totalSpecialWins*100.0/totalSpecial;
		System.out.println(
				tp+" "+sl+" || "
				+totalTrades
				+" "+maxWins+" "+maxLosses
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(winPips, false)
				+" "+PrintUtils.Print2(lostPips, false)
				+" "+PrintUtils.Print2(avgPipsReal, false)
				+" || "
				+totalSpecial
				+" "+PrintUtils.Print2(winSpecialPer, false)
				);
	}
	
	public static void testBreakDetailLT(ArrayList<QuoteShort> data,int begin,int end,
			int correctedPips,
			int maxTouches,
			int tp,int sl,boolean debug){
		double winPips = 0;
		double lostPips = 0;
		ArrayList<Integer> totalDayTouches = new ArrayList<Integer>();
		for (int i=0;i<=50;i++) totalDayTouches.add(0);
		int touches = 0;
		int wins = 0;
		
		int lastMax = -1;
		int lastMin = -1;
		int max = -1;
		int min = -1;
		int lastDay = -1;
		int openIndex = 0;
		PositionType positionType = PositionType.NONE;
		int entry = -1;
		int slValue = -1;
		int tpValue = -1;
		int dayTradesTpTotal = 0;
		int dailyTouches = 0;
		int dayTradesSl = 0;
		int dayTradesTp = 0;
		double actualPipValue = 1;
		boolean isTradeOpen = false;
		boolean isDayFinished = false;
		boolean isTpDay = false;
		boolean isDayTouched = false;
		double dailyLosses= 0;
		int total = 0;
		int tpDays = 0;//termina en tp
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int year = cal.get(Calendar.YEAR);
			
			if (year<2009) continue;
			
			//determinar si estamos en nuevo dia
			if (day!=lastDay){
				if (lastDay!=-1){
					lastMax = max;
					lastMin = min;
				}
				isDayFinished = false;
				dailyTouches = 0;
				isTpDay = false;
				max = -1;
				min = -1;
				lastDay = day;
				if (debug)
					System.out.println("****[NEW DAY] "+DateUtils.datePrint(cal));
			}
			//determinar nueva posicion
			if (!isDayFinished 
					//&& dailyTouches<maxTouches
					&& !isTradeOpen 
					&& lastMax!=-1 && lastMin!=-1){
				int diffH = q.getHigh5()-(lastMax+correctedPips);
				int diffL = (lastMin-correctedPips)-q.getLow5();
				
				if (diffH>=0 && q.getOpen5()<lastMax){
					isTradeOpen = true;
					isDayTouched = true;
					openIndex = i;
					positionType = PositionType.LONG;
					entry = lastMax+correctedPips;
					slValue = entry-sl*10;
					tpValue = entry+tp*10;
					if (debug)
						System.out.println("[OPEN] BUY "+lastMax+" "+i);
					touches++;
					dailyTouches++;
					isDayFinished = false;
				}
				
				if (diffL>=0 && q.getOpen5()>lastMin){
					isTradeOpen = true;
					isDayTouched = true;
					openIndex = i;
					positionType = PositionType.SHORT;
					entry = lastMin-correctedPips;
					slValue = entry+sl*10;
					tpValue = entry-tp*10;
					if (debug)
						System.out.println("[OPEN] SELL "+lastMin+" "+i);
					touches++;
					dailyTouches++;
					isDayFinished = false;
				}
			}
			//determinar estado de la posicion actual
			if (isTradeOpen){
				if (positionType==PositionType.LONG){
					if (q.getHigh5()>=tpValue){
						isTradeOpen = false;
						isDayFinished = true;
						isTpDay = true;
						dayTradesTp++;
						wins++;
						winPips +=1*tp;
						if (debug)
							System.out.println("[CLOSE TP] BUY "+tpValue+" "+i
									+" || "+actualPipValue
									+" "+PrintUtils.Print2dec(actualPipValue*tp,false)
									+" "+PrintUtils.Print2dec(dailyLosses,false)
									+" || "+winPips
									+" "+lostPips
									+" || "+PrintUtils.Print2dec(winPips-lostPips,false)
									);
					}else if (i>openIndex && q.getLow5()<=slValue){
						dayTradesSl++;
						isTradeOpen = false;
						lostPips +=1*sl; 
						dailyLosses += 1*sl;
						//cambio del pipValue
						actualPipValue = dailyLosses*1.0/tp;
						if (debug)
							System.out.println("[CLOSE SL] BUY "+slValue+" "+i+" || "+actualPipValue);
					}
				}
				if (positionType==PositionType.SHORT){
					if (q.getLow5()<=tpValue){
						isTradeOpen = false;
						isDayFinished = true;
						isTpDay = true;
						dayTradesTp++;
						wins++;
						winPips +=1*tp;
						if (debug)
							System.out.println("[CLOSE TP] SELL "+tpValue+" "+i
									+" || "+actualPipValue
									+" "+PrintUtils.Print2dec(actualPipValue*tp,false)
									+" "+PrintUtils.Print2dec(dailyLosses,false)
									+" || "+winPips
									+" "+lostPips
									+" || "+PrintUtils.Print2dec(winPips-lostPips,false)
									);
					}else if (i>openIndex && q.getHigh5()>=slValue){
						
						dayTradesSl++;
						isTradeOpen = false;
						lostPips +=1*sl;
						dailyLosses += 1*sl;
						//cambio del pipValue
						actualPipValue = dailyLosses*1.0/tp;
						if (debug)
							System.out.println("[CLOSE SL] SELL "+slValue+" "+i+" || "+actualPipValue);
					}
				}
			}
			
			//determinar actual max && min
			if (max==-1 || q.getHigh5()>max) max = q.getHigh5();
			if (min==-1 || q.getLow5()<min) min = q.getLow5();
		}
		
		double winPer = wins*100.0/touches;
		double lossPer = 100.0-winPer;
		double avgPips = (winPer*tp-lossPer*sl)/100.0;
		System.out.println(
				tp+" "+sl+" || "
				+touches
				+" "+PrintUtils.Print2(winPer, false)
				+" "+PrintUtils.Print2(avgPips, false)
				+" || "
				);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2015.03.12.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2010.01.01_2015.08.15.csv";
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_Hourly_Bid_2003.12.31_2015.08.16.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2004.01.01_2015.08.15.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_5 Mins_Bid_2003.12.31_2015.09.16.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathEURJPY);
		paths.add(pathGBPUSD);paths.add(pathGBPJPY);
		paths.add(pathUSDJPY);
		
		int limit = 2;
		for (int i = 2;i<=limit;i++){
			String path = paths.get(i);			
			ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
			ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
		  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
			ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
			ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
			ArrayList<QuoteShort> data = null;
			dataI.clear();
			dataS.clear();
			data5m.clear();
			data = data5mS;
			//System.out.println("data: "+data.size());
			int begin = 4000000;
			int end = data.size();
			int tp = 5;
			int sl = 20;
			int offset = 0;
			int limitPerDay = 1;
			boolean breakMode = false;
			
			begin = 0;
			end = data.size()-1;
			
			for (sl=30;sl<=30;sl+=10*tp)
				for (tp=1*sl;tp<=1*sl;tp+=1*sl)
				//for (tp=5;tp<=100;tp+=1)
				//for (sl=2*tp;sl<=2*tp;sl+=1*tp)
					for (int maxTouches=1;maxTouches<=20;maxTouches++)
						for (int correctedPips=0;correctedPips<=0;correctedPips+=10){
							for (int year1=2004;year1<=2004;year1++){
								int year2=2015;
								TestDailyBreakout.testBreakDetail(data, begin, end,year1,year2,correctedPips,maxTouches, tp, sl,false);
							}
						}
			
			/*for (int minPips=5;minPips<=5;minPips++){
				//testDailyData(dailyData,minPips);
				testDailyDataH(data,minPips);
			}*/
			
			/*for (int h1=0;h1<=0;h1++){
				int h2=h1+9;
				for (int minPips=0;minPips<=0;minPips++){
					for (tp=20;tp<=20;tp+=5)
						for (sl=(int) (1.0*tp);sl<=1.0*tp;sl+=1*tp)
							//for (sl=50;sl<=50;sl+=5)
								//for (tp=1*sl;tp<=100*sl;tp+=5*sl)
								TestDailyBreakout.testBreak2(data, minPips,h1,h2, tp, sl,false);
						//}
					//}
				}
			}*/
		}
	}


	

}
