package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.PositionShort;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class CoreContinuation {
	
	public static void doOptimizaContinuation(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int h1,int h2,int thr,
			int nbars,
			int sl,int comm,
			int mode,
			int y1,int y2,int nParts,
			boolean showPeriods){
	
		int years = y2-y1;
		
		int total = 0;
		double avgs = 0;
		double winPips = 0;
		double lostPips = 0;
		for (int year1=y1;year1<=y2-nParts;year1++){
			int year2 = year1+nParts;
			
			CoreStats stats = CoreContinuation.doTestSimpleContinuationPositions(data, maxMins,year1,year2, h1, h2, thr, nbars,sl,comm,mode,showPeriods);
			avgs+=stats.getAvg()*stats.getTotalTrades();
			winPips+=stats.getTotalWinPips();
			lostPips+=stats.getTotalLostPips();
			total+=stats.getTotalTrades();
		}
		
		System.out.println(
				h1+" "+h2
				+" "+thr
				+" "+nbars
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(avgs/total, false)
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false)
				);
	}
	
	public static void doTestSimpleContinuationPullBackTPSL(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr1,int thr2,
			int bbars,int nbars,
			int tp,int sl,
			boolean debug){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calj = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size()-nbars;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int maxMin = maxMins.get(i-1);			
			
			if (y>=y1 && y<=y2
					&& h>=h1 && h<=h2
					){
				int type = 0;
				int valueTP = -1;
				int valueSL = -1;
				if (maxMin>=thr2){
					int begin = i-1-bbars;
					if (begin<=0) begin = 0;
					for (int j=begin;j<=i-1;j++){
						QuoteShort.getCalendar(calj, data.get(j));
						int hj = calj.get(Calendar.HOUR_OF_DAY);
						if (hj>=h1 && hj<=h2){
							int maxMin0 = maxMins.get(j);	
							if (maxMin0<=-thr1){
								if (debug)
								System.out.println("[min located] "
										+DateUtils.datePrint(cal)+" "+DateUtils.datePrint(calj)
										+" "+j+" "+maxMin0+" "+data.get(j).getLow5()+" || "+(i-1)+" "+maxMin+" "+data.get(i-1).getHigh5());
								type = -1;
								valueTP = q.getOpen5()-tp*10;
								valueSL = q.getOpen5()+sl*10;
								break;
							}
						}
					}
				}else if (maxMin<=-thr2){
					int begin = i-1-bbars;
					if (begin<=0) begin = 0;
					for (int j=begin;j<=i-1;j++){
						QuoteShort.getCalendar(calj, data.get(j));
						int hj = calj.get(Calendar.HOUR_OF_DAY);
						if (hj>=h1 && hj<=h2){
							int maxMin0 = maxMins.get(j);	
							if (maxMin0>=thr1){
								type = 1;
								valueTP = q.getOpen5()+tp*10;
								valueSL = q.getOpen5()-sl*10;
								break;
							}
						}
					}
				}
				
				if (type!=0){	
					TradingUtils.getMaxMinShortTPSL(data, qm, calj, i, data.size(), valueTP, valueSL, debug);
					int diff = 0;
					if (qm.getOpen5()==1){
						diff = tp*10;
					}else if (qm.getOpen5()==-1){
						diff = -sl*10;
					}
					//int diff = type*data.get(i+nbars).getClose5()+(-type)*q.getOpen5();					
					if (diff>=0){
						winPips += diff;
						wins++;
					}else{
						lostPips += -diff;
						losses++;
					}
				}
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = winPips*1.0/lostPips;
		System.out.println(
				h1+" "+h2
				+" "+thr1+" "+thr2
				+" "+bbars
				+" "+nbars
				+" "+tp+" "+sl
				+" || "
				+" "+total
				+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}
	
	public static void doTestSimpleContinuationPullBack(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr1,int thr2,int bbars,int nbars,boolean debug){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calj = Calendar.getInstance();
		for (int i=1;i<data.size()-nbars;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			int maxMin = maxMins.get(i-1);			
			
			if (y>=y1 && y<=y2 && h>=h1 && h<=h2){
				int type = 0;
				if (maxMin>=thr2){
					int begin = i-1-bbars;
					if (begin<=0) begin = 0;
					for (int j=begin;j<=i-1;j++){
						QuoteShort.getCalendar(calj, data.get(j));
						int hj = calj.get(Calendar.HOUR_OF_DAY);
						if (hj>=h1 && hj<=h2){
							int maxMin0 = maxMins.get(j);	
							if (maxMin0<=-thr1){
								if (debug)
								System.out.println("[min located] "
										+DateUtils.datePrint(cal)+" "+DateUtils.datePrint(calj)
										+" "+j+" "+maxMin0+" "+data.get(j).getLow5()+" || "+(i-1)+" "+maxMin+" "+data.get(i-1).getHigh5());
								type = -1;
								break;
							}
						}
					}
				}else if (maxMin<=-thr2){
					int begin = i-1-bbars;
					if (begin<=0) begin = 0;
					for (int j=begin;j<=i-1;j++){
						QuoteShort.getCalendar(calj, data.get(j));
						int hj = calj.get(Calendar.HOUR_OF_DAY);
						if (hj>=h1 && hj<=h2){
							int maxMin0 = maxMins.get(j);	
							if (maxMin0>=thr1){
								type = 1;
								break;
							}
						}
					}
				}
				
				if (type!=0){				
					int diff = type*data.get(i+nbars).getClose5()+(-type)*q.getOpen5();					
					if (diff>=0){
						winPips += diff;
						wins++;
					}else{
						lostPips += -diff;
						losses++;
					}
				}
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = winPips*1.0/lostPips;
		System.out.println(
				h1+" "+h2
				+" "+thr1+" "+thr2
				+" "+bbars
				+" "+nbars
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}
	
	public static void doTestSimpleContinuationOpt(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins){

		CoreStats stats = new CoreStats();
		for (int h1=4;h1<=9;h1++){
			int h2 = h1+0;
			for (int thr= 100;thr<= 1000;thr+= 100){
				for (int nbars = 12;nbars<= 48;nbars+=12){
					for (int sl=30;sl<=50;sl+=10){
						double avgPf = 0;
						int totalWins = 0;
						int total = 0;
						stats.reset();
						for (int y1=2003;y1<=2016;y1+=1){
							int y2 = y1;
							double pf = CoreContinuation.doTestSimpleContinuation(data, maxMins,stats,y1,y2, h1, h2, thr, nbars,sl,-1,2.0,false);
							avgPf += pf;
							if (pf>=1.0){
								totalWins++;
							}
							total++;
						}
						if (stats.getTotalTrades()<300) continue;
						if (totalWins>=8)
						System.out.println(
								h1+" "+h2+" "+thr+" "+nbars+" "+sl
								+" || "+totalWins
								+" "+stats.getTotalTrades()
								+" "+PrintUtils.Print2dec(stats.getTotalWinPips()*1.0/stats.getTotalLostPips(), false)
								);
					}
				}
			}
		}
		
		
	}
	
	public static double doTestSimpleContinuation(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			CoreStats stats,
			int y1,int y2,
			int h1,int h2,int thr,int nbars,
			int sl,
			int mode,double comm,boolean debug){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int accMaxAdverse = 0;
		int accMaxProfit = 0;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size()-nbars;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			int y = cal1.get(Calendar.YEAR);
			int h = cal1.get(Calendar.HOUR_OF_DAY);
			int min = cal1.get(Calendar.MINUTE);
			
			int maxMin = maxMins.get(i-1);
			
			if (y>=y1 && y<=y2 
					&& h>=h1 && h<=h2
					&& ((h==0 && min>=20) || h!=0)
					){
				int type = 0;
				if (maxMin>=thr){
					type = 1*mode;
				}else if (maxMin<=-thr){
					type = -1*mode;
				}
				
				if (type!=0){				
					TradingUtils.getMaxMinShort(data, qm, cal, i, i+nbars);
					int maxProfit = 0;
					int maxAdverse = 0;
					if (type==1){//long
						maxProfit = qm.getHigh5()-q.getOpen5();
						maxAdverse = q.getOpen5() - qm.getLow5();												
					}else if (type==-1){
						maxProfit = q.getOpen5() - qm.getLow5();
						maxAdverse = qm.getHigh5()-q.getOpen5();
					}
					
					accMaxProfit += maxProfit;
					accMaxAdverse += maxAdverse;
					
					int diff = type*data.get(i+nbars).getClose5()+(-type)*q.getOpen5();	
					
					if (maxAdverse>=sl*10){
						diff = -sl*10;
					}
					
					diff -=comm*10;
					//experimental
					if (maxAdverse>=150){
						//diff = -150;
					}					
					//experimental
					if (maxProfit>=150){
						//diff = 150;
					}
					//if (debug)
						//System.out.println("[TRADE "+type+" ] "+DateUtils.datePrint(cal)+" "+diff);
					if (diff>=0){
						winPips += diff;
						wins++;
						actualLosses = 0;
					}else{						
						lostPips += -diff;
						losses++;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}
				}
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double pf = winPips*1.0/lostPips;
		double avgMaxProfit = accMaxProfit*0.1/total;
		double avgMaxAdverse = accMaxAdverse*0.1/total;
		double profitMeasure = total*pf;
		double RRtrade = avg*100.0/sl;
		double RRtotal = total*RRtrade;
		
		stats.setTotalTrades(stats.getTotalTrades()+total);
		stats.setTotalWinPips(stats.getTotalWinPips()+winPips);
		stats.setTotalLostPips(stats.getTotalLostPips()+lostPips);
		
		//if (avg>=1.0 
				//&& winPer>=56.0 
			//	&& pf>=1.1)
		if (debug)
		System.out.println(
				h1+" "+h2
				+" "+thr
				+" "+nbars
				+" "+sl
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+maxLosses
				+" || "+PrintUtils.Print2dec(avgMaxProfit, false)
				+" "+PrintUtils.Print2dec(avgMaxAdverse, false)
				+" || "+PrintUtils.Print2dec(RRtrade, false)
				+" "+PrintUtils.Print2dec(RRtotal, false)
				);
		
		return pf;
		
	}
	
	public static CoreStats doTestSimpleContinuationPositions(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int tp,
			int sl,
			int comm,
			int mode,
			int offset,
			boolean debug
			){
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>(); 
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int maxLosses = 0;
		int actualLosses = 0;
		int maxPositions = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calq = Calendar.getInstance();
		int b = 1;
		for (int i=b;i<data.size();i++){
			QuoteShort q1 = data.get(i-b);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q1);
			QuoteShort.getCalendar(calq, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			int maxMin = maxMins.get(i-b);
			
			boolean canTrade = (y>=y1 && y<=y2) && ((h>=h1 && h<=h2) && ((h!=0) || (h==0 && min>=10)));
			
			if (canTrade){
				int entry = -1;
				int slValue = -1;
				int tpValue = -1;
				PositionType posType = PositionType.NONE;
				if (maxMin>=thr
						//&& q.getOpen5()>q1.getOpen5()+offset*10
						){
					entry = q.getOpen5();
					tpValue = entry+10*tp;
					slValue = entry-10*sl;
					posType = PositionType.LONG;
					if (mode==-1){
						posType = PositionType.SHORT;
						slValue = entry+10*sl;
						tpValue = entry-10*tp;
					}
				}else if (maxMin<=-thr
						//&& q.getOpen5()<q1.getOpen5()-offset*10
						){
					entry = q.getOpen5();
					tpValue = entry-10*tp;
					slValue = entry+10*sl;					
					posType = PositionType.SHORT;
					if (mode==-1){
						posType = PositionType.LONG;
						slValue = entry-10*sl;
						tpValue = entry+10*tp;
					}
				}
				
				if (entry!=-1){
					PositionShort pos = new PositionShort();
					pos.setEntry(entry);
					pos.setSl(slValue);
					pos.setTp(tpValue);
					pos.setPositionType(posType);
					pos.setPositionStatus(PositionStatus.OPEN);
					pos.setExpiredTime(i+999999);
					pos.getOpenCal().setTimeInMillis(calq.getTimeInMillis());
					positions.add(pos);
					//System.out.println("[new pos]: "+pos.toString2());
					if (positions.size()>=maxPositions) maxPositions = positions.size();
				}
			}
			
			int j = 0;
			while (j<positions.size()){
				PositionShort p = positions.get(j);
				boolean closed = false;
				int diff = 0;
				/*if (i>=p.getExpiredTime()){
					diff = q.getClose5()-p.getEntry();
					if (p.getPositionType() == PositionType.SHORT)
						diff = p.getEntry()-q.getClose5();
					closed = true;
				//}else{*/
					int difftp = q.getHigh5()-p.getTp();
					int diffsl = p.getSl()-q.getLow5();
					if (p.getPositionType() == PositionType.SHORT){
						difftp = p.getTp()-q.getLow5();
						diffsl = q.getHigh5()-p.getSl();						
					}
					if (diffsl>=0){
						diff = -sl*10;
						closed = true;
					}else if (difftp>=0){
						diff = tp*10;
						closed = true;
					} 					
				//}
				
				if (closed){
					diff -=comm*10;
					if (diff>=0){
						winPips += diff;
						wins++;
						actualLosses = 0;
					}else{						
						lostPips += -diff;
						losses++;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}
					positions.remove(j);
				}else{
					j++;
				}
			}
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avg = (winPips-lostPips)*0.1/total;
		double avgWin = winPips*0.1/wins;
		double avgLoss = lostPips*0.1/losses;
		double pf = winPips*1.0/lostPips;
		if (debug)
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2
				+" "+thr
				+" "+tp
				+" "+sl
				+" || "
				+" "+total
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgWin, false)
				+" ("+PrintUtils.Print2dec(avgLoss, false)+")"
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "+maxLosses
				+" || "+maxPositions
				);
		
		CoreStats stats = new CoreStats();
		stats.setTotalTrades(total);
		stats.setAvg(avg);
		stats.setPf(pf);
		stats.setTotalWinPips(winPips);
		stats.setTotalLostPips(lostPips);
		return stats;
		
	}

	public static void main(String[] args) throws Exception {
		
		//horas viables para USDJPY 0,1,2,23
		
		String pathEURUSD = "C:\\fxdata\\gbpusd_UTC_5 Mins_Bid_2003.05.04_2016.08.03.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			CoreContinuation.doTestSimpleContinuationOpt(data, maxMins);
			
			//SimpleContinuation
			/*for (int y1=2003;y1<=2003;y1+=1){
				int y2 = y1+13;
				for (int h1=9;h1<=9;h1++){
					int h2=h1+0;
					for (int thr= 400;thr<= 400;thr+= 50){
						for (int nbars = 36;nbars<= 36;nbars+=1){
							for (int sl=35;sl<=35;sl+=5)
								CoreContinuation.doTestSimpleContinuation(data, maxMins,y1,y2, h1, h2, thr, nbars,sl,-1,2.0,false);
							//for (int sl=5;sl<=60;sl+=5){
							
						}
					}
				}
			}*/
			/*for (int tp=10;tp<=50;tp+=1){
			//for (int tp=sl;tp<=sl;tp+=10){
			for (int sl=3*tp;sl<=3*tp;sl+=5){
				for (int offset=0;offset<=0;offset++){
					CoreContinuation.doTestSimpleContinuationPositions(data, maxMins,y1,y2, h1, h2, thr,tp,sl,0,-1,offset,true);
				}
		
			}
		}*/
			/*for (int h1=9;h1<=9;h1++){
				int h2=h1+0;
				for (int thr=1;thr<=1000;thr+=1){
					for (int nbars = 24;nbars<=24;nbars+=12){
						//CoreContinuation.doTestSimpleContinuation(data, maxMins,y1,y2, h1, h2, thr, nbars,-1);
						for (int sl=200;sl<=200;sl+=10){
							doOptimizaContinuation(data, maxMins,h1, h2, thr, nbars,sl,2,-1,2009,2016,0,false);
						}
					}
				}
			}*/
			
			
		
		}//limit

	}

}
