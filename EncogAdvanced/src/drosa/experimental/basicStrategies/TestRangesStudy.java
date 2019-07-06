package drosa.experimental.basicStrategies;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.data.DataUtils;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.CoreStrategies.StrategyConfig;
import drosa.experimental.CoreStrategies.TestPriceBuffer;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestRangesStudy {
	
	public static double testTouches3(
			String aHeader,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int m1,int m2,
			int h1,int h2,
			int thr,
			int aDiff,
			int tp,
			int sl,			
			int debug
			) {
		
		Calendar cal = Calendar.getInstance();	
		Calendar calj = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();	
		QuoteShort qm = new QuoteShort();
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int dayOpen = 0;
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int high = -1;
		int low = -1;
		int totalDays = 0;
		double comm = 20;
		int wins = 0;
		int losses = 0;
		int actualLosses = 0;
		ArrayList<Double> rangesF = new ArrayList<Double>();
		ArrayList<Integer> touchesArr = new ArrayList<Integer>();
		ArrayList<Integer> lossesArr = new ArrayList<Integer>();
		int index = 0;
		int actualRacha = 0;
		int totaltrades = 0;
		int winPips = 0;
		int lostPips = 0;
		int maxLosses = 0;
		for (int i=10;i<data.size();i++) {
			QuoteShort q5 = data.get(i-5);
			QuoteShort q4 = data.get(i-4);
			QuoteShort q3 = data.get(i-3);
			QuoteShort q2 = data.get(i-2);
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (h>=h1 && h<=h2
					//&& min==0
					){
				
				dayOpen = q.getOpen5();
				int maxMin = maxMins.get(i-1);
				int entry = q.getOpen5();
				int tpValue = q.getOpen5() - tp;
				int slValue = q.getOpen5() + sl;
				int mode=0;
				if (true
						&& maxMin>=thr
						//&& q1.getClose5()<q1.getOpen5()-100
						&& q1.getHigh5()>q2.getHigh5()
						&& q2.getHigh5()>q3.getHigh5()
						&& q3.getHigh5()>q4.getHigh5()
						){
					
					int idx = TradingUtils.getMaxMinIndex(data, i-2-thr, i-2, true);
					int diff = i-2-idx;
					//System.out.println(diff);
					
					//if (diff==0){
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, entry, tpValue,slValue,false);
						mode = -1;
						totaltrades++;
					//}
				}else if (true
						&& maxMin<=-thr
						&& q1.getLow5()<q2.getLow5()
						&& q2.getLow5()<q3.getLow5()
						&& q3.getLow5()<q4.getLow5()
						//&& q1.getClose5()>q1.getOpen5()+100
						){
					
					int idx = TradingUtils.getMaxMinIndex(data, i-2-thr, i-2,false);
					int diff = i-2-idx;
					//System.out.println(diff);
					
					//if (diff==0){
						tpValue = q.getOpen5() + tp;
						slValue = q.getOpen5() - sl;
						TradingUtils.getMaxMinShortEntryTPSL(data, qm, calqm, i, data.size()-1, entry, tpValue,slValue,false);
						mode = 1;
						totaltrades++;
					//}
				}
				
				if (mode!=0){
					if (qm.getOpen5()==1){
						wins++;
						lossesArr.add(actualLosses);
						actualLosses = 0;
						winPips += tp;
					}else if (qm.getOpen5()==-1){
						losses++;
						actualLosses++;
						lostPips += sl;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}
				}			
			}
			
			
			if (high==-1 || q.getHigh5()>=high) {
				high = q.getHigh5();
			}
			
			if (low==-1 || q.getLow5()<=low) {
				low = q.getLow5();
			}
			
		}//data
		
		int total = 0;
		count1=0;
		count2=0;
		count3 = 0;
		int count0=0;
		int count00=0;
		int count4=0;
		double avg = MathUtils.average(lossesArr);
		double dt = Math.sqrt(MathUtils.variance(lossesArr));
		
		int thr3 = (int) (avg+5*dt);
		int thr00	= 3;
		int thr0	= 4;
		int thr1 	= 5;
		int thr2 	= 6;
		thr3 		= 7;
		int thr4 	= 8;
		
		
		
		for (int i=0;i<lossesArr.size();i++){
			int touches = lossesArr.get(i);
			if (touches>=thr00){
				count00++;
			}
			if (touches>=thr0){
				count0++;
			}
			if (touches>=thr1){
				count1++;
			}
			if (touches>=thr2){
				count2++;
			}
			if (touches>=thr3){
				count3++;
			}
			if (touches>=thr4){
				count4++;
			}
			total++;
		}
		
		double avgL = MathUtils.average(lossesArr);
		double dtL = Math.sqrt(MathUtils.variance(lossesArr));
		
		double perComm = (comm*100.0)/(tp);
				
		String header = y1+" "+y2+" "+h1+" "+thr+" "+tp+" "+sl;
				
		int sizeArr = lossesArr.size();
		
		double avgPerTrade = (winPips-lostPips)*0.1/(wins+losses);
		double pf = wins*tp*1.0/(losses*sl);
		
		header +=" || "
		+" "+lossesArr.size()
		+" "+PrintUtils.Print2dec(avgL, false)
		+" "+PrintUtils.Print2dec(dtL, false)
		+" || "+(wins+losses)
		+" "+PrintUtils.Print2dec(wins*100.0/(wins+losses), false)
		+" || "+PrintUtils.Print2dec(pf, false)
		+" "+PrintUtils.Print2dec(avgPerTrade, false)
		+" || "+maxLosses
		/*+" || "+lossesArr.size()
		+" "+PrintUtils.Print2dec(avg, false)
		+" "+PrintUtils.Print2dec(dt, false)
		+" "+count00+'('+thr00+')'
		+" "+count0+'('+thr0+')'
		+" "+count1+'('+thr1+')'
		+" "+count2+'('+thr2+')'
		+" "+count3+'('+thr3+')'
		+" "+count4+'('+thr4+')'
		+" || "
		+" "+PrintUtils.Print2dec(count00*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count0*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count1*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count2*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count3*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count4*100.0/sizeArr,false)
		+" || "+PrintUtils.Print2dec(perComm,false)*/
		;

		if (debug==0)
			System.out.println(aHeader+" | "+header);
		
		
		return pf;
	}
	
	public static void testTouches2(
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,
			int entryThr,
			int tpValue,
			int prevRange,
			int prevBars,
			int maxFails,
			int debug
			) {
		
		Calendar cal = Calendar.getInstance();	
		Calendar cal1 = Calendar.getInstance();
		Calendar calj = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();	
		QuoteShort qm = new QuoteShort();
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int dayOpen = 0;
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int high = -1;
		int low = -1;
		int totalDays = 0;
		double comm = 20;
		int wins = 0;
		int losses = 0;
		int actualLosses = 0;
		ArrayList<Double> rangesF = new ArrayList<Double>();
		ArrayList<Integer> touchesArr = new ArrayList<Integer>();
		ArrayList<Integer> lossesArr = new ArrayList<Integer>();
		int index = 0;
		int actualRacha = 0;
		int touches= 0;
		for (int i=prevBars;i<data.size();i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (h==0 && min==0) dayOpen = q.getOpen5();
			
			if (day!=lastDay
					&& h==h1
					) {		
				
				if (lastDay!=-1){
					if (debug==2)
						System.out.println("[DAY] "+DateUtils.datePrint(cal1)
							+" "+touches
								);
				}
				
				
				int range = TradingUtils.getRange(data, i-1, prevBars);
				
				
				dayOpen = q.getOpen5();
				
				if (range>=prevRange){
					int thrH = dayOpen + entryThr;
					int thrL = dayOpen - entryThr;
					int tpH = thrH+tpValue;
					int tpL = thrL-tpValue;
					//ahora bucle de calculo de touches
					int mode = 0;
					touches = 0;
					int completed = 0;
					if (debug==1)
					System.out.println("[NEWREF] "+DateUtils.datePrint(cal)
						+" "+dayOpen+" "+thrH+" "+thrL
							);
					for (int j=i;j<data.size();j++){
						QuoteShort qj = data.get(j);
						QuoteShort.getCalendar(calj, qj);
						if (mode==0){
							if (qj.getHigh5()>=thrH){
								mode=1;
								touches++;
								if (debug==1)
								System.out.println("[touch high] "+DateUtils.datePrint(calj)
								+" "+dayOpen+" "+thrH+" "+thrL
									);
								
							}else if (qj.getLow5()<=thrL){
								mode=-1;
								touches++;
								if (debug==1)
								System.out.println("[touch low] "+DateUtils.datePrint(calj)
								+" "+dayOpen+" "+thrH+" "+thrL
									);
							}
						}
						
						if (mode==1){
							if (qj.getHigh5()>=tpH){
								completed = 1;
								index = j;
								break;
							}
							if (qj.getLow5()<=thrL){
								if (debug==1)
								System.out.println("[touch low] "+DateUtils.datePrint(calj)
								+" "+dayOpen+" "+thrH+" "+thrL
									);
								touches++;
								mode=-1;
								if (qj.getLow5()<=tpL){
									completed = 1;
									index = j;
									break;
								}
							}
						}else if (mode==-1){
							if (qj.getLow5()<=tpL){
								completed = 1;
								index = j;
								break;
							}
							if (qj.getHigh5()>=thrH){
								if (debug==1)
								System.out.println("[touch high] "+DateUtils.datePrint(calj)
								+" "+dayOpen+" "+thrH+" "+thrL
									);
								mode=1;
								touches++;
								if (qj.getHigh5()>=tpH){
									completed = 1;
									index = j;
									break;
								}
							}
						}
						
						if (touches>maxFails){
							completed = -1;
							index = j;
							break;
						}
					}
					
					if (completed==1){
						wins++;
						touchesArr.add(touches);						
						actualLosses=0;
						if (debug==3)
						System.out.println("[WIN] "+DateUtils.datePrint(cal)						
							);
						//i = index;
					}else if (completed==-1){
						losses++;
						touchesArr.add(touches);
						actualLosses++;
						if (debug==3)
							System.out.println("[LOSS] "+DateUtils.datePrint(cal)
							+" || "+actualLosses
								);
						//i = index;
					}
					
				}//prevRange
				
				
				if (lastDay!=-1){
					totalDays++;
				}
				lastDay = day;
				high = -1;
				low = -1;
			}//day
			
			if (high==-1 || q.getHigh5()>=high) {
				high = q.getHigh5();
			}
			
			if (low==-1 || q.getLow5()<=low) {
				low = q.getLow5();
			}
			
		}//data
		
		int total = 0;
		count1=0;
		count2=0;
		count3 = 0;
		int count0=0;
		int count00=0;
		int count4=0;
		double avg = MathUtils.average(touchesArr);
		double dt = Math.sqrt(MathUtils.variance(touchesArr));
		
		int thr3 = (int) (avg+5*dt);
		int thr00	= 4;
		int thr0	= 5;
		int thr1 	= 6;
		int thr2 	= 7;
		thr3 		= 8;
		int thr4 	= 9;
		
		
		
		for (int i=0;i<touchesArr.size();i++){
			touches = touchesArr.get(i);
			if (touches>=thr00){
				count00++;
			}
			if (touches>=thr0){
				count0++;
			}
			if (touches>=thr1){
				count1++;
			}
			if (touches>=thr2){
				count2++;
			}
			if (touches>=thr3){
				count3++;
			}
			if (touches>=thr4){
				count4++;
			}
			total++;
		}
		
		double avgL = MathUtils.average(lossesArr);
		double dtL = Math.sqrt(MathUtils.variance(lossesArr));
		
		double perComm = (comm*100.0)/(tpValue);
				
		String header = y1+" "+y2+" "+h1+" "+entryThr+" "+tpValue
				+" || "
				+" "+PrintUtils.Print2dec(tpValue*1.0/(2*entryThr),false)
				;
				
		int sizeArr = lossesArr.size();
		header +=" || "
		//+" "+lossesArr.size()
		//+" "+PrintUtils.Print2dec(avgL, false)
		//+" "+PrintUtils.Print2dec(dtL, false)
		//+" "+PrintUtils.Print2dec(wins*100.0/(wins+losses), false)
		+" || "+touchesArr.size()
		+" "+PrintUtils.Print2dec(avg, false)
		+" "+PrintUtils.Print2dec(dt, false)
		+" "+count00+'('+thr00+')'
		+" "+count0+'('+thr0+')'
		+" "+count1+'('+thr1+')'
		+" "+count2+'('+thr2+')'
		+" "+count3+'('+thr3+')'
		+" "+count4+'('+thr4+')'
		+" || "
		+" "+PrintUtils.Print2dec(count00*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count0*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count1*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count2*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count3*100.0/sizeArr,false)
		+" "+PrintUtils.Print2dec(count4*100.0/sizeArr,false)
		+" || "+PrintUtils.Print2dec(perComm,false)
		;

		System.out.println(header);
	}
	
	public static void testTouches(
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,
			int entryThr,
			int factor,
			int prevRange,
			int prevBars,
			int debug
			) {
		
		Calendar cal = Calendar.getInstance();	
		Calendar calj = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();	
		QuoteShort qm = new QuoteShort();
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int dayOpen = 0;
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int high = -1;
		int low = -1;
		int totalDays = 0;
		double comm = 20;
		
		ArrayList<Double> rangesF = new ArrayList<Double>();
		ArrayList<Integer> touchesArr = new ArrayList<Integer>();
		int index = 0;
		for (int i=prevBars;i<data.size();i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			if (h==0 && min==0) dayOpen = q.getOpen5();
			
			if (day!=lastDay
					&& h==h1
					) {		
				
				
				int range = TradingUtils.getRange(data, i-1, prevBars);
				
				
				dayOpen = q.getOpen5();
				
				if (range>=prevRange){
					int thrH = dayOpen + entryThr;
					int thrL = dayOpen - entryThr;
					int tpH = thrH+entryThr*factor;
					int tpL = thrL-entryThr*factor;
					//ahora bucle de calculo de touches
					int mode = 0;
					int touches = 0;
					int completed = 0;
					if (debug==1)
					System.out.println("[NEWREF] "+DateUtils.datePrint(cal)
						+" "+dayOpen+" "+thrH+" "+thrL
							);
					for (int j=i;j<data.size();j++){
						QuoteShort qj = data.get(j);
						QuoteShort.getCalendar(calj, qj);
						if (mode==0){
							if (qj.getHigh5()>=thrH){
								mode=1;
								touches++;
								if (debug==1)
								System.out.println("[touch high] "+DateUtils.datePrint(calj)
								+" "+dayOpen+" "+thrH+" "+thrL
									);
								
							}else if (qj.getLow5()<=thrL){
								mode=-1;
								touches++;
								if (debug==1)
								System.out.println("[touch low] "+DateUtils.datePrint(calj)
								+" "+dayOpen+" "+thrH+" "+thrL
									);
							}
						}
						
						if (mode==1){
							if (qj.getHigh5()>=tpH){
								completed = 1;
								index = j;
								break;
							}
							if (qj.getLow5()<=thrL){
								if (debug==1)
								System.out.println("[touch low] "+DateUtils.datePrint(calj)
								+" "+dayOpen+" "+thrH+" "+thrL
									);
								touches++;
								mode=-1;
								if (qj.getLow5()<=tpL){
									completed = 1;
									index = j;
									break;
								}
							}
						}else if (mode==-1){
							if (qj.getLow5()<=tpL){
								completed = 1;
								index = j;
								break;
							}
							if (qj.getHigh5()>=thrH){
								if (debug==1)
								System.out.println("[touch high] "+DateUtils.datePrint(calj)
								+" "+dayOpen+" "+thrH+" "+thrL
									);
								mode=1;
								touches++;
								if (qj.getHigh5()>=tpH){
									completed = 1;
									index = j;
									break;
								}
							}
						}
					}
					if (completed==1){
						touchesArr.add(touches);
						if (debug==2
								&& touches>=20
								){
							System.out.println(DateUtils.datePrint(cal)+" stages= "+touches);
						}
						i=index;
					}
				}//prevRange
				
				
				if (lastDay!=-1){
					totalDays++;
				}
				lastDay = day;
				high = -1;
				low = -1;
			}//day
			
			if (high==-1 || q.getHigh5()>=high) {
				high = q.getHigh5();
			}
			
			if (low==-1 || q.getLow5()<=low) {
				low = q.getLow5();
			}
			
		}//data
		
		int total = 0;
		count1=0;
		count2=0;
		count3 = 0;
		int count0=0;
		int count00=0;
		int count4=0;
		double avg = MathUtils.average(touchesArr);
		double dt = Math.sqrt(MathUtils.variance(touchesArr));
		
		int thr3 = (int) (avg+5*dt);
		int thr00	= 10;
		int thr0	= 11;
		int thr1 	= 12;
		int thr2 	= 13;
		thr3 		= 14;
		int thr4 	= 15;
		
		
		
		for (int i=0;i<touchesArr.size();i++){
			int touches = touchesArr.get(i);
			if (touches>=thr00){
				count00++;
			}
			if (touches>=thr0){
				count0++;
			}
			if (touches>=thr1){
				count1++;
			}
			if (touches>=thr2){
				count2++;
			}
			if (touches>=thr3){
				count3++;
			}
			if (touches>=thr4){
				count4++;
			}
			total++;
		}
		
		double perComm = (comm*100.0)/(entryThr*factor);
				
		String header = y1+" "+y2+" "+h1+" "+entryThr+" "+factor;
						
		header +=" || "
		+" "+touchesArr.size()
		+" "+PrintUtils.Print2dec(avg, false)
		+" "+PrintUtils.Print2dec(dt, false)
		+" "+count00+'('+thr00+')'
		+" "+count0+'('+thr0+')'
		+" "+count1+'('+thr1+')'
		+" "+count2+'('+thr2+')'
		+" "+count3+'('+thr3+')'
		+" "+count4+'('+thr4+')'
		+" || "
		+" "+PrintUtils.Print2dec(count00*100.0/touchesArr.size(),false)
		+" "+PrintUtils.Print2dec(count0*100.0/touchesArr.size(),false)
		+" "+PrintUtils.Print2dec(count1*100.0/touchesArr.size(),false)
		+" "+PrintUtils.Print2dec(count2*100.0/touchesArr.size(),false)
		+" "+PrintUtils.Print2dec(count3*100.0/touchesArr.size(),false)
		+" "+PrintUtils.Print2dec(count4*100.0/touchesArr.size(),false)
		+" || "+PrintUtils.Print2dec(perComm,false)
		;

		System.out.println(header);
	}
	
	
	public static void test(
			ArrayList<QuoteShort> data,
			int y1,int y2,
			int m1,int m2,
			int h1,
			int nbarsFuture,
			int ndaysBack
			) {
		
		Calendar cal = Calendar.getInstance();	
		Calendar calqm = Calendar.getInstance();	
		QuoteShort qm = new QuoteShort();
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int dayOpen = 0;
		int lastDay = -1;
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int high = -1;
		int low = -1;
		int totalDays = 0;
		ArrayList<Double> rangesF = new ArrayList<Double>();
		for (int i=1;i<data.size()-nbarsFuture;i++) {
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int month = cal.get(Calendar.MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			
			if (y<y1 || y>y2) continue;
			if (month<m1 || month>m2) continue;
			
			
			
			if (day!=lastDay
					&& h==h1
					) {		

				//dayOpen = q.getOpen5();
				if (lastDay!=-1){
					ranges.add(high-low);
					
					double avg = MathUtils.average(ranges, ranges.size()-ndaysBack, ranges.size()-1);
					
					//vemos el rango en nbars futuras
					int range = TradingUtils.getRange(data, i, nbarsFuture);
					
					double f = range*1.0/avg;
					rangesF.add(f);
					if (f<=1.0) count1++;
					if (f<=2.0) count2++;
					if (f<=3.0) {
						//System.out.println(DateUtils.datePrint(cal)+" "+avg+" "+range);
						count3++;
					}
					totalDays++;
				}
				lastDay = day;
				high = -1;
				low = -1;
			}//day
			
			if (high==-1 || q.getHigh5()>=high) {
				high = q.getHigh5();
			}
			
			if (low==-1 || q.getLow5()<=low) {
				low = q.getLow5();
			}
			
		}//data
		
		String header = y1+" "+y2+" "+ndaysBack+" "+nbarsFuture;
		
		double avgf = MathUtils.average(rangesF);
		double dt = Math.sqrt(MathUtils.varianceD(rangesF));
		
		header +=" || "+PrintUtils.Print2dec(avgf, false)
		+" "+PrintUtils.Print2dec(dt, false)
		+" "+count1+" "+count2+" "+count3
		;
		
		MathUtils.summary_complete(header, rangesF);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
				String path0 ="C:\\fxdata\\";
				//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
				//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.09.28.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.10.23.csv";
				
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.12.04.csv";
				String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2008.12.31_2018.01.02.csv";
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2008.12.31_2018.01.02.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.12.csv";
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.08.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_30 Secs_Bid_2012.12.31_2017.12.11.csv";
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.12.04.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2003.05.04_2017.11.24.csv";
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.24.csv";
				//String pathEURUSD = path0+"eurusd_UTC_1 Min_Bid_2013.12.31_2017.11.22.csv";
				//String pathEURUSD = path0+"eurusd_UTC_5 Mins_Bid_2003.05.04_2017.11.16.csv";
				
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.12.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
				//String pathEURUSD = path0+"EURUSD_UTC_1 Min_Bid_2009.12.31_2017.10.25.csv";
				String pathNews = path0+"News.csv";
				
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(pathEURUSD);
				//paths.add(pathEURAUD);paths.add(pathNZDUSD);
				
				int total = 0;
				ArrayList<Double> pfs = new ArrayList<Double>();
				int limit = paths.size()-1;
				limit = 0;
				String provider ="";
				Sizeof.runGC ();
				ArrayList<QuoteShort> dataI 		= null;
				ArrayList<QuoteShort> dataS 		= null;
				ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
				//FFNewsClass.readNews(pathNews,news,0);
				for (int i = 0;i<=limit;i++){
					String path = paths.get(i);			
					dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
					//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
					TestLines.calculateCalendarAdjustedSinside(dataI);
					//TradingUtils.cleanWeekendDataSinside(dataI); 	
					dataS = TradingUtils.cleanWeekendDataS(dataI);  
					ArrayList<QuoteShort> data = null;
					ArrayList<QuoteShort> dataNoise = null;
					data = dataS;
				
					ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
					
					
					for (int m1=0;m1<=0;m1+=4){
						int m2 = m1+11;
						for (int h1=9;h1<=9;h1++){
							int h2 = h1+0;
							for (int entryThr=50;entryThr<=50;entryThr+=10){
								for (int tpValue = 200;tpValue<=200;tpValue+=10){
									for (int prevRange=0;prevRange<=0;prevRange+=100){
										for (int prevBars=120;prevBars<=120;prevBars+=1){
											for (int maxFails=1000;maxFails<=1000;maxFails++){
												for (int y1=2009;y1<=2009;y1++){
													int y2 = y1+8;
													TestRangesStudy.testTouches2(data, y1, y2, m1, m2, h1,
															entryThr, 
															tpValue,
															prevRange,
															prevBars,
															maxFails,
															0
															);
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

}