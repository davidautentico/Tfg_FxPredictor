package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class CoreMeanReversionStrategy {
	
	public static double getAvg(ArrayList<QuoteShort> data,int begin,int end){
		
		if (begin<0) begin = 0;
		int acc = 0;
		int total = 0;
		for (int i=0;i<=end;i++){
			acc += data.get(i).getHigh5()-data.get(i).getLow5();
			total++;
		}
		
		return acc*0.1/total;
	}
	
	public static void doTestPyramid(ArrayList<QuoteShort> data,
			ArrayList<QuoteShort> maxMins,
			int y1,int y2,
			int h1,int h2,
			int dayWeek1,int dayWeek2,
			int thr,int tp,int sl,int minNewOpen,
			int maxRange,
			boolean debug){
		
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		
		int maxLosses = 0;
		int actualLosses = 0;
		int totalDayWins = 0;
		int lastDayWins = 0;
		int totalDayLosses = 0;
		int lastDayLoss = -1;
		int lastDayWin = -1;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		double sizeWin = 0;
		double sizeLoss = 0;
		int lastDay = -1;
		int dayOpen = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		boolean canPrint = false;
		int lossIdx = -1;
		int max = -1;
		int min = -1;
		ranges.add(1000);
		int avgRange = 1000;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			int day = cal1.get(Calendar.DAY_OF_YEAR);
			int year = cal1.get(Calendar.YEAR);
			int dayWeek = cal1.get(Calendar.DAY_OF_WEEK);
			int minute = cal1.get(Calendar.MINUTE);
			
			int h = cal1.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){	
				if (max!=-1){
					int actualRange = max-min;
					ranges.add(actualRange);
				}
				max = -1;
				min = -1;
				avgRange = (int) MathUtils.average(ranges,ranges.size()-20,ranges.size()-1);
				dayOpen = q.getOpen5();
				lastDay = day;
			}
			
			if (q.getHigh5()>=max || max==-1){
				max = q.getHigh5();
			}
			if (q.getLow5()<=min || min==-1){
				min = q.getLow5();
			}
			
			//mirar para abrir operaciones
			boolean canOpen = (year>=y1 && year<=y2) && (h>=h1 && h<=h2) && (dayWeek>=dayWeek1 && dayWeek<=dayWeek2)
					&& avgRange<maxRange*10
					;
			//if (canOpen){
				int totalOpen		= PositionShort.countTotal(positions, PositionStatus.OPEN);
				int positionPips	= PositionShort.countTotalPips(positions, PositionStatus.OPEN,q.getOpen5()); //total pips de las operaciones abiertas
				
				if (totalOpen==1 
						&& positionPips<=-10*minNewOpen){ //ya hay una posicion y va en negativo
					int maxMin = maxMins.get(i-1).getExtra();		
					
					//abrimos nueva operacion
					int entry0		= positions.get(0).getEntry();
					int tp0			= positions.get(0).getTp();
					int sl0			= positions.get(0).getSl();
					PositionType posType = positions.get(0).getPositionType();
					if ((maxMin>=60 && posType==PositionType.SHORT)
							|| (maxMin<=-60 && posType==PositionType.LONG)){
						//creamos nueva posicion y agregamos
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setTp(tp0);
						pos.setSl(sl0);
						pos.setPositionType(posType);
						pos.setPositionStatus(PositionStatus.OPEN);
						positions.add(pos);
						if (debug)
							System.out.println("[ADDED NEW POSITION "+posType.name()+" ] "
									+DateUtils.datePrint(cal)+" "+positions.get(0).getEntry()+" "+q.getOpen5()+" || "+positionPips									
									);
					}
				}else if (canOpen 
						//&& totalOpen==0
						){
					int maxMin = maxMins.get(i-1).getExtra();		
					if (maxMin>=thr){
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setTp(q.getOpen5()-10*tp);
						pos.setSl(q.getOpen5()+10*sl);
						pos.setPositionType(PositionType.SHORT);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						positions.add(pos);
					}else if (maxMin<=-thr){
						PositionShort pos = new PositionShort();
						pos.setEntry(q.getOpen5());
						pos.setTp(q.getOpen5()+10*tp);
						pos.setSl(q.getOpen5()-10*sl);
						pos.setPositionType(PositionType.LONG);
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						positions.add(pos);
					}
				}							
			
		//}//canOpen
			
			//evalucaion de las posiciones
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				
				if (p.getPositionStatus()==PositionStatus.OPEN){
					boolean closed = false;
					int pips = 0;
					if (p.getPositionType()==PositionType.SHORT){
						if (q.getHigh5()>=p.getSl()){
							pips = -sl;
							closed = true;
						}else if (q.getLow5()<=p.getTp()){
							pips = tp;
							closed = true;
						}
					}else if (p.getPositionType()==PositionType.LONG){
						if (q.getLow5()<=p.getSl()){
							pips = -sl;
							closed = true;
						}else if (q.getHigh5()>=p.getTp()){
							pips = tp;
							closed = true;
						}
					}
					
					if (closed){
						if (pips>=0){
							wins++;
							winPips += pips;
							int dayWin = p.getOpenCal().get(Calendar.DAY_OF_YEAR);
							if (dayWin!=lastDayWin){
								totalDayWins++;
								lastDayWin = dayWin;
								actualLosses = 0;
								//System.out.println("[DAYWIN] "+DateUtils.datePrint(p.getOpenCal())+" "+totalDayWins+" "+totalDayLosses+" || "+wins+" "+losses);
							}
						}else{
							losses++;
							lostPips += -pips;
							int dayLoss = p.getOpenCal().get(Calendar.DAY_OF_YEAR);
							if (dayLoss!=lastDayLoss){
								totalDayLosses++;
								lastDayLoss = dayLoss;
								actualLosses++;
									if (actualLosses>maxLosses) maxLosses = actualLosses;
								//System.out.println("[DAYLOSS] "+DateUtils.datePrint(p.getOpenCal())+" "+totalDayWins+" "+totalDayLosses
								//		+" || "+wins+" "+losses+" || "+maxLosses);
							}
						}
						positions.remove(j);
					}else{
						j++;
					}
				}
			}
			
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (winPips-lostPips)*1.0/trades;
		double pf = winPips*1.0/lostPips;
		
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2				
				+" "+tp
				+" "+sl
				+" "+thr
				+" "+minNewOpen
				+" "+maxRange
				+" || "
				+" "+PrintUtils.Print2Int(trades,4)
				+" "+PrintUtils.Print2Int(wins,4)+" "+PrintUtils.Print2Int(losses,4)+" "+PrintUtils.Print2Int(totalDayLosses,4)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(sizeWin/wins, false)
				+" "+PrintUtils.Print2dec(sizeLoss/losses, false)
				+" || "+maxLosses
				);
		
	}
	
	public static void doTest(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int dayWeek1,int dayWeek2,
			int thr,int tp,int sl,int minSize,int minSize2,boolean debug){
		
		int dayLosses = 0;
		int lastDayLoss = -1;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		double sizeWin = 0;
		double sizeLoss = 0;
		int lastDay = -1;
		int dayOpen = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		boolean canPrint = false;
		int lossIdx = -1;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			int day = cal1.get(Calendar.DAY_OF_YEAR);
			int year = cal1.get(Calendar.YEAR);
			int dayWeek = cal1.get(Calendar.DAY_OF_WEEK);
			int min = cal1.get(Calendar.MINUTE);
			
			int h = cal1.get(Calendar.HOUR_OF_DAY);
			if (year<y1 || year>y2) continue;
			if (h<h1 || h>h2) continue;
			if (h==0 && min<=15) continue;
			if (dayWeek<dayWeek1 || dayWeek>dayWeek2) continue;
			
			if (day!=lastDay){
				
				dayOpen = q.getOpen5();
				lastDay = day;
			}
			
			
			int maxMin = maxMins.get(i-1);		
			if (maxMin>=thr){
				int size = q.getHigh5()-dayOpen;
				int diffH = q1.getHigh5()-q1.getClose5();
				//if (q1.getOpen5()<q1.getClose5()) continue;
				//if (diffH<=minSize2*10) continue;
				//if (size<minSize*10) continue;
				TradingUtils.getMaxMinShortTPSL(data, qm, cal, i, data.size()-1, q.getOpen5()-10*tp, q.getOpen5()+10*sl,false);
				int pips = q.getOpen5()-qm.getClose5();
				double avg = getAvg(data,i-5,i);
				if (pips>=0){
					wins++;
					winPips += pips;
					sizeWin += avg;
				}else{
					losses++;
					lostPips += -pips;
					sizeLoss += avg;
					lossIdx = i;
					if (day!=lastDayLoss){
						dayLosses++;
						lastDayLoss = day;
						if (debug)
							System.out.println("[LOSS SHORT] "+DateUtils.datePrint(cal1)+" || "+DateUtils.datePrint(cal));
					}
					canPrint = true;
				}
				if (canPrint){
					QuoteShort.getCalendar(cal, q);
					//System.out.println("[SHORT] "+DateUtils.datePrint(cal)+" "+q.getOpen5()+" "+pips);
				}
			}else if (maxMin<=-thr){
				int size = dayOpen-q.getLow5();
				int diffL = q1.getClose5()-q.getLow5();
				//if (q1.getOpen5()>q1.getClose5()) continue;
				//if (diffL<=minSize2*10) continue;
				//if (size<minSize*10) continue;
				TradingUtils.getMaxMinShortTPSL(data, qm, cal, i, data.size()-1, q.getOpen5()+10*tp, q.getOpen5()-10*sl,false);
				int pips = qm.getClose5()-q.getOpen5();
				double avg = getAvg(data,i-2,i-1);
				if (pips>=0){
					wins++;
					winPips += pips;
					sizeWin += avg;
				}else{
					losses++;
					lostPips += -pips;
					sizeLoss += avg;
					
					if (day!=lastDayLoss){
						dayLosses++;
						lastDayLoss = day;
						if (debug)
							System.out.println("[LOSS LONG] "+DateUtils.datePrint(cal1)+" || "+DateUtils.datePrint(cal));
					}
				}
			}
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double avg = (winPips-lostPips)*0.1/trades;
		double pf = winPips*1.0/lostPips;
		
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+thr
				+" "+tp
				+" "+sl
				+" "+minSize
				+" || "
				+" "+PrintUtils.Print2Int(trades,4)
				+" "+PrintUtils.Print2Int(wins,4)+" "+PrintUtils.Print2Int(losses,4)+" "+PrintUtils.Print2Int(dayLosses,4)
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+PrintUtils.Print2dec(sizeWin/wins, false)
				+" "+PrintUtils.Print2dec(sizeLoss/losses, false)
				);
		
	}

	
	public static void main(String[] args) throws Exception {
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.02.06.csv";
		
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
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;										
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
;
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			for (int y1=2009;y1<=2009;y1++){
				int y2 = y1+7;
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+9;				
					for (int thr=100;thr<=5000;thr+=100){
						for (int tp=15;tp<=15;tp++){
							for (int sl=3*tp;sl<=3*tp;sl+=tp){
								for (int minSize=400;minSize<=400;minSize+=5){
									for (int minSize2=0;minSize2<=0;minSize2+=1){
										for (int maxRange=2000;maxRange<=2000;maxRange+=5){
											for (int dayWeek1=Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
												int dayWeek2 = dayWeek1+4;
												CoreMeanReversionStrategy.doTest(data, maxMins,y1,y2, h1, h2,dayWeek1,dayWeek2, thr, tp, sl,minSize,minSize2,false);
												//CoreMeanReversionStrategy.doTestPyramid(data5mS, maxMins,y1,y2, h1, h2,dayWeek1,dayWeek2, thr, tp, sl,minSize,maxRange,false);
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
}
