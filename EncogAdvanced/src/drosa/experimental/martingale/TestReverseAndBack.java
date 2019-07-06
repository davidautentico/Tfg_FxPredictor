package drosa.experimental.martingale;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.experimental.PositionShort;
import drosa.experimental.SuperStrategy;
import drosa.experimental.momentum.FirstStrikeDaily;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.FileUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestReverseAndBack {
	
	public static PositionShort generateSignal(ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			Calendar cal,int index,
			ArrayList<Integer> allowedHours,
			int tp,int sl,int thr,int offset,boolean counterTrade){
		
		
		PositionShort pos = null;

		QuoteShort q = data.get(index);
		QuoteShort q1 = data.get(index+1);
		QuoteShort.getCalendar(cal, q1);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		
		if (allowedHours.get(h)!=1) return null;
		
		PositionType posType = PositionType.NONE;
		int entry = -1;
		int slValue = -1;
		int tpValue = -1;
		int maxMin = maxMins.get(index).getExtra();
		int diffCO = (int) ((q.getClose5()-q.getOpen5())*0.1);
		if (maxMin>=thr
				//&& diffCO>=offset
				){
			entry = q1.getOpen5()+offset*10;
			if (counterTrade){
				slValue = entry+10*sl;
				tpValue = entry-10*tp;
				posType = PositionType.SHORT;
			}else{
				slValue = entry-10*sl;
				tpValue = entry+10*tp;
				posType = PositionType.LONG;
			}
		}else if (maxMin<=-thr
				//&& diffCO<=-offset
				){
			entry = q1.getOpen5()-offset*10;//se abrea con la siguiente
			if (counterTrade){
				slValue = entry-10*sl;
				tpValue = entry+10*tp;
				posType = PositionType.LONG;
			}else{
				slValue = entry+10*sl;
				tpValue = entry-10*tp;
				posType = PositionType.SHORT;
			}
		}
		if (entry!=-1){
			pos = new PositionShort();
			//pos.setPositionStatus(PositionStatus.OPEN);
			pos.setPositionStatus(PositionStatus.PENDING);
			pos.setPositionType(posType);
			pos.setEntry(entry);
			pos.setSl(slValue);
			pos.setTp(tpValue);
			pos.setPendingIndex(index+1);
			pos.getPendingCal().setTimeInMillis(cal.getTimeInMillis());
			pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
			//System.out.println("[PENDING] "+DateUtils.datePrint(cal)+" || "+pos.toString());
		}
		return pos;
	}
	
	public static PositionShort generateSignalDO(ArrayList<QuoteShort> data,ArrayList<QuoteShort> maxMins,
			Calendar cal,int index,
			int h1,int h2,
			int doValue,int sl,int thr){
		
		
		PositionShort pos = null;

		QuoteShort q = data.get(index);
		QuoteShort q1 = data.get(index+1);
		QuoteShort.getCalendar(cal, q1);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		if (h<h1 || h>h2) return null;
		
		PositionType posType = PositionType.NONE;
		int entry = -1;
		int slValue = -1;
		int tpValue = -1;
		int doPips = (int) (Math.abs(q1.getOpen5()-doValue)*0.1);
		int maxMin = maxMins.get(index).getExtra();
		if (maxMin>=thr && q1.getOpen5()>doValue){
			entry = q1.getOpen5();
			slValue = entry+10*doPips;
			tpValue = doValue;
			posType = PositionType.SHORT;
			
		}else if (maxMin<=-thr && q1.getOpen5()<doValue){
			entry = q1.getOpen5();//se abrea con la siguiente
			slValue = entry-10*doPips;
			tpValue = doValue;
			posType = PositionType.LONG;
		}
		if (entry!=-1){
			pos = new PositionShort();
			pos.setPositionStatus(PositionStatus.OPEN);
			pos.setPositionType(posType);
			pos.setEntry(entry);
			pos.setSl(slValue);
			pos.setTp(tpValue);
			pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
			//System.out.println("[OPEN] "+DateUtils.datePrint(cal));
		}
		return pos;
	}
	
	
	
	public static void testATR(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int begin,int end,
			String hours,
			double tpFactor,double slFactor,int be,
			int thr,
			double offsetFactor,
			int minCandleSize,
			int minPips,
			int maxExpiration,
			int nATR,
			double comm,
			boolean counterTrade,
			boolean debug){
		
		ArrayList<Integer> allowedHours = new ArrayList<Integer>();for (int i=0;i<=23;i++) allowedHours.add(0);
		SuperStrategy.convertAllowedTime(allowedHours,hours,0,23,1,0);
		
		int wins = 0;
		int losses = 0;
		double winPips = 0;
		double lostPips = 0;
		ArrayList<Double> tradePips = new ArrayList<Double>();
		ArrayList<Integer> dailyRanges = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		int i = begin;
		int lastDay = -1;
		int max = -1;
		int min = -1;
		int tp = -1;
		int sl = -1;
		int offset = -1;
		int totalDays=0;
		int totalPipsRisked=0;
		
		boolean firstPos = true;
		boolean canTrade = false;
		while (i<=end){
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q1);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (year<2000){
				i++;
				continue;
			}
			if (day!=lastDay){
				if (lastDay!=-1){
					double range = (max-min)*0.1;
					dailyRanges.add((int) range);
				}
				double rangeAtr = MathUtils.average(dailyRanges,totalDays-nATR,totalDays-1);
				if (rangeAtr<50) rangeAtr=50;
				tp = (int) (rangeAtr*tpFactor);
				sl = (int) (rangeAtr*slFactor);
				offset = (int) (rangeAtr*offsetFactor);
				double ratio = sl*1.0/tp;
				if (tp<=minPips){
					tp = minPips;
					sl = (int) (tp*ratio);
				}
				max = -1;
				min = -1;
				lastDay = day;
				totalDays++;
				//System.out.println(totalDays);
				canTrade = true;
			}
			
			if (max==-1 || q1.getHigh5()>=max) max = q1.getHigh5();
			if (min==-1 ||q1.getLow5()<=min) min = q1.getLow5();
			
			//generar posicion con la anterior, porque se abre con el open de i
			PositionShort pos = generateSignal(data,maxMins,cal,i-1,allowedHours,tp,sl,thr,offset,counterTrade);
			int diff = max-min;
			if (pos!=null  
					&& diff>=minCandleSize*10 
					&& tp>0 && sl>0
					&& canTrade
					){//generamos una posicion y actualizamos estadisticas
				//System.out.println(diff);
				//evaluar posicion
				String result = PositionShort.evaluatePosition(data,i,end,cal,pos,be,maxExpiration,comm,debug);
				int newIndex = Integer.valueOf(result.split(" ")[0]);
				String codOp = result.split(" ")[1];
				//actualizar stats
				if (newIndex>=0 
						&& !codOp.equalsIgnoreCase("NONE")
						&& !codOp.equalsIgnoreCase("EXP")
						){
					
					totalPipsRisked += Math.abs(pos.getEntry()-pos.getSl())*0.1;
					double pips = pos.getWinPips();
					if (pips>=0){
						wins++;
						winPips+=pips;
						/*System.out.println("WIN "+wins+" || "
								+PrintUtils.Print2dec(pips, false)
								+" "+PrintUtils.Print2dec(winPips, false)
								+" "+PrintUtils.Print2dec(winPips/wins, false)
								);*/
					}else{
						losses++;
						lostPips+=-pips;
						//System.out.println("LOSS "+pips);
					}
					tradePips.add(pips);
					//i = newIndex;//nos movemos a la nueva posicion//comentado evaluamos cada quote
					
					//canTrade = false;
					
					//para testeo
					if (firstPos){
						//System.out.println(pos.toString2()+" || "+q1.toString()+" || "+tp+" "+sl);
						firstPos = false;
					}
				}
			}
			
		
			//avanzamos
			i++;
		}
		int totalTrades = wins+losses;
		double winPer =wins*100.0/totalTrades;
		double winPer1 =wins*1.0/totalTrades;
		double pf = winPips/lostPips;
		double avgPipsRisked = totalPipsRisked*1.0/totalTrades;
		double avg = (winPips-lostPips)*1.0/totalTrades;
		double avgW = (winPips)*1.0/wins;
		double avgL = (lostPips)*1.0/losses;
		double wlRatio = avgW/avgL;
		double kelly = winPer1-((1-winPer1)/wlRatio);
		double totalPips =winPips-lostPips;
		double perTradeExp = avg*1.0/avgPipsRisked;
		
		String header = 
				hours
				+" "+PrintUtils.Print2dec(tpFactor,false)
				+" "+PrintUtils.Print2dec(slFactor,false)
				+" "+PrintUtils.Print2dec(offsetFactor,false)
				+" "+PrintUtils.Print2Int(minCandleSize,3)
				+" "+PrintUtils.Print2dec(avgPipsRisked,false)
				+" "+PrintUtils.Print2Int(thr,5)
				+" "+maxExpiration
				+" || "
				+PrintUtils.Print2Int(totalTrades,5)
				+" "+PrintUtils.Print2Int(wins,5)
				+" "+PrintUtils.Print2Int(losses,5)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false,3)
				+" "+PrintUtils.Print2dec(wlRatio, false,3)
				+" "+PrintUtils.Print2dec(avgW, false,3)
				+" "+PrintUtils.Print2dec(avgL, false,3)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "
				+" "+PrintUtils.Print3dec(perTradeExp, false)
				+" "+PrintUtils.Print3dec(kelly,false)
				+" "+PrintUtils.Print3dec(perTradeExp*kelly, false)
				+" "+PrintUtils.Print3dec(perTradeExp*kelly*totalTrades, false)
				;
		/*System.out.println(
				tp
				+" "+sl
				+" "+thr
				+" || "
				+totalTrades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);*/
		
		int boxSize = 50;
		int total = tradePips.size();
		int totalBoxes = total/boxSize+1;
		ArrayList<Double> avgs = new ArrayList<Double>();
		for (int j=0;j<totalBoxes;j++){
			int first = j*boxSize;
			int last = first+boxSize;
			if (last>tradePips.size()-1) last = tradePips.size();
			int effSize = last-first;
			if (effSize<boxSize*0.4) continue;
			double avgP = MathUtils.averageD(tradePips, first, last);
			//System.out.println(effSize+" "+avgP);
			avgs.add(avgP);
		}
		MathUtils.summary_mean_sd(header, avgs);
	}
	

	
	
	public static void testReverse(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int begin,int end,int tp,int sl,int thr,double comm){
		
		int totalReverses0 = 0;
		int totalReverses1 = 0;
		int totalReverses2 = 0;
		int totalReverses3 = 0;
		int totalReverses4 = 0;
		int totalReverses5 = 0;
		int totalReverses6 = 0;
		int totalReverses7 = 0;
		
		int wins = 0;
		int losses = 0;
		double winPips = 0;
		double lostPips = 0;
		int actualTrade = 0;
		int entry = -1;
		int slValue = -1;
		int tpValue = -1;
		Calendar cal = Calendar.getInstance();
		int actualReverses = 0;
		int i = begin;
		while (i<=end){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i+1);
			
			int maxMin = maxMins.get(i-1).getExtra();
			
			if (actualTrade==0){
				if (maxMin>=thr){
					entry = q.getOpen5();
					slValue = entry-10*sl;
					tpValue = entry+10*tp;
					actualTrade=1;
					actualReverses=0;
				}else if (maxMin<=-thr){
					entry = q.getOpen5();
					slValue = entry+10*sl;
					tpValue = entry-10*tp;
					actualTrade=-1;
					actualReverses=0;
				}
			}
			
			if (actualTrade!=0){
				int result = 0;
				if (actualTrade==1){
					if (q.getLow5()<=slValue){
						result =-1;
					}else if (q.getHigh5()>=tpValue){
						result = 1;
					}
				}else if (actualTrade==-1){
					if (q.getHigh5()>=slValue){
						result = -1;
					}else if (q.getLow5()<=tpValue){
						result = 1;
					}
				}
				if (result!=0){
					if (result==1){
						wins++;
						winPips+=tp;
						actualTrade=0;
						if (actualReverses==0) totalReverses0++;
						if (actualReverses==1) totalReverses1++;
						if (actualReverses==2) totalReverses2++;
						if (actualReverses==3) totalReverses3++;
						if (actualReverses==4) totalReverses4++;
						if (actualReverses==5) totalReverses5++;
						if (actualReverses==6) totalReverses6++;
						if (actualReverses==7) totalReverses7++;
						actualReverses=0;
					}else{
						losses++;
						lostPips+=sl;
						actualReverses++;
						//abrimos posicion en sentido contrario
						if (actualTrade==1){
							entry = q1.getOpen5();
							slValue = entry+10*sl;
							tpValue = entry-10*tp;
							actualTrade=-1;//reverse
						}else if (actualTrade==-1){
							entry = q1.getOpen5();
							slValue = entry-10*sl;
							tpValue = entry+10*tp;
							actualTrade=1;
						}
						//System.out.println("[OPEN REVERSE] "+actualTrade+" "+entry+" "+actualReverses);
					}
				}
			}
			//avanzamos
			i++;
		}
		int totalTrades = wins+losses;
		double winPer =wins*100.0/totalTrades;
		double pf = winPips/lostPips;
		double avg = (winPips-lostPips)*1.0/totalTrades;
		double avgW = (winPips)*1.0/wins;
		double avgL = (lostPips)*1.0/losses;
		System.out.println(
				tp
				+" "+sl
				+" "+thr
				+" || "
				+totalTrades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgW, false)
				+" "+PrintUtils.Print2dec(avgL, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "
				+totalReverses0
				+" "+totalReverses1
				+" "+totalReverses2
				+" "+totalReverses3
				+" "+totalReverses4
				+" "+totalReverses5
				+" "+totalReverses6
				+" "+totalReverses7
				);
	}

	public static void main(String[] args) throws Exception {
		ArrayList<ArrayList<QuoteShort>> datas = new ArrayList<ArrayList<QuoteShort>>();
		ArrayList<String> files = new ArrayList<String>();
		//String	path1 = "c:\\fxdata\\EURUSD_pepper_daily.csv";
		//String	path1 = "c:\\fxdata\\eurusd_forexdata_5min_1986_2012.csv";
		String	path1 = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.08.csv";
		String	path2 = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.12.31_2015.09.08.csv";
		String	path3 = "c:\\fxdata\\usdjpy_UTC_5 Mins_Bid_2003.12.31_2015.09.08.csv";
		String	path4 = "c:\\fxdata\\audusd_UTC_5 Mins_Bid_2003.12.31_2015.09.08.csv";
		String	path5 = "c:\\fxdata\\eurjpy_UTC_5 Mins_Bid_2003.12.31_2015.09.08.csv";
		String	path6 = "c:\\fxdata\\gbpjpy_UTC_5 Mins_Bid_2003.12.31_2015.09.08.csv";
		String	path7 = "c:\\fxdata\\audjpy_UTC_5 Mins_Bid_2003.12.31_2015.09.08.csv";
		files.add(path1);
		files.add(path2);
		files.add(path3);
		files.add(path4);
		files.add(path5);
		files.add(path6);
		files.add(path7);
	
		int limit = files.size()-1;
		limit = 0;
		//calculo de los arrays de datos
		for (int i=0;i<=limit;i++){
			Sizeof.runGC ();
			String fileName = files.get(i);
			ArrayList<QuoteShort> data = FileUtils.extractData(fileName);
			ArrayList<QuoteShort> maxMins = TradingUtils.calculateMaxMinByBarShortAbsolute(data);
			//System.out.println("total data: "+data.size());
			//for (int h1=0;h1<=0;h1++){
				//for (int h2=h1+9;h2<=h1+9;h2++)
				for (int thr=4000;thr<=10000;thr+=1000){
					//for (int tp=36;tp<=36;tp+=5){
					for (double tp=0.14;tp<=0.14;tp+=0.5){
						for (double sl=0.35;sl<=0.35;sl+=0.01){//14,35,20
						//for (int sl=20;sl<=20;sl+=1){
						//for (int sl=(int) (1.0*tp);sl<=1.0*tp;sl+=0.5*tp){
							for (double offset=-0.20;offset<=-0.20;offset+=0.01){
								for (int minPips=18;minPips<=18;minPips+=1){
									for (int maxExpiration=390;maxExpiration<=390;maxExpiration+=10){
										for (int h=0;h<=0;h++){
											String hours = PrintUtils.Print2Int(h,2);
											//hours="0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23";
											for (int atr=30;atr<=30;atr++){
												for (double comm=4.0;comm<=4.0;comm+=1.0){
													//TestReverseAndBack.testATR(data, maxMins, 1, data.size()-2,"0 1 2 3 4 5 6 7 8 9", tp, sl,99999, thr,offset,atr, comm,false);
													TestReverseAndBack.testATR(data, maxMins, 1, 100000,"16 17 18 19 20 21 22 23", tp, sl,99999,
													thr,offset,0,minPips,maxExpiration,atr,comm,false,false);
													
													TestReverseAndBack.testATR(data, maxMins, 100000, 200000,"16 17 18 19 20 21 22 23", tp, sl,99999,
													thr,offset,0,minPips,maxExpiration,atr,comm,false,false);
													
													TestReverseAndBack.testATR(data, maxMins, 200000, 300000,"16 17 18 19 20 21 22 23", tp, sl,99999,
															thr,offset,0,minPips,maxExpiration,atr,comm,false,false);
													
													TestReverseAndBack.testATR(data, maxMins, 400000, 500000,"16 17 18 19 20 21 22 23", tp, sl,99999,
															thr,offset,0,minPips,maxExpiration,atr,comm,false,false);
															
													TestReverseAndBack.testATR(data, maxMins, 500000, 600000,"16 17 18 19 20 21 22 23", tp, sl,99999,
															thr,offset,0,minPips,maxExpiration,atr,comm,false,false);
													
													TestReverseAndBack.testATR(data, maxMins, 600000, data.size()-2,"16 17 18 19 20 21 22 23", tp, sl,99999,
															thr,offset,0,minPips,maxExpiration,atr,comm,false,false);
													//System.out.println();
												}
											}
										}
									}
								}//candleSize
							}//offset
						}//sl
					}//tp
				}//thr
		}

	}

}
