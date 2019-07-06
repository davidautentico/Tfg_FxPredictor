package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class BounceNPeriods {
	
	public static void doBasic1(
			String header,
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,			
			int tp,
			int sl,
			int thr,
			int minPips,
			double comm,
			boolean debug
			){
	
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			if (y<y1 || y>y2) continue;
		
			if ((h>=h1 && h<=h2) 
					|| h==23
					){
				
				int maxMin = maxMins.get(i-1);
				boolean isTrade = false;
				int pips = 0;
				if (maxMin>=thr){
					TradingUtils.getMaxMinShort(data, qm, calqm, i-thr, i-1);
					int lowDiff = data.get(i-thr).getOpen5()-qm.getLow5();					
					if (lowDiff>=minPips*10){
						int valueTP = (int) (q.getOpen5()-10*tp);
						int valueSL = (int) (q.getOpen5()+10*sl); 
						TradingUtils.getMaxMinShortTPSLIndex(data, qm, cal, i, data.size()-1, valueTP, valueSL, false);
						pips = (int) (q.getOpen5()-qm.getClose5()-10*comm);
						isTrade = true;	
						
						if (debug){
							System.out.println("[SELL] "+DateUtils.datePrint(cal1)
									+" || "+q.getOpen5()+" "+valueTP+" "+valueSL
									+" || "+pips+" || "+DateUtils.datePrint(cal));
						}
					}					
				}else if (maxMin<=-thr){					
					TradingUtils.getMaxMinShort(data, qm, calqm, i-thr, i-1);
					int highDiff = qm.getHigh5()-data.get(i-thr).getOpen5();
					if (highDiff>=minPips*10){
						int valueTP = (int) (q.getOpen5()+10*tp);//+10*comm);
						int valueSL = (int) (q.getOpen5()-10*sl);//+10*comm);
						TradingUtils.getMaxMinShortTPSLIndex(data, qm, cal, i, data.size()-1, valueTP, valueSL, false);
						pips = (int) (qm.getClose5()-q.getOpen5()-10*comm);
						isTrade = true;
						
						if (debug){
							System.out.println("[BUY] "+DateUtils.datePrint(cal1)
									+" || "+q.getOpen5()+" "+valueTP+" "+valueSL
									+" || "+pips+" || "+DateUtils.datePrint(cal));
						}
					}
				}
				
				if (isTrade){						
					if (pips>=0){
						winPips += pips;
						wins++;
						//lossesArray.set(actualLosses, lossesArray.get(actualLosses)+1);
						//actualLosses=0;
						//i=index;
					}else if (pips<0){
						lostPips += -pips;
						losses++;
						//actualLosses++;
						//dayTraded = true;
						//i=index;
					}
				}
			}			
		}//for
		
		
		int trades = wins+losses;
		
		double winPer = 0;
		double avg = 0;
		double pf = 0;
		
		
		if (trades>0){
			winPer = wins*100.0/trades;
			avg = (winPips-lostPips)*0.1/(trades);
			pf = winPips*1.0/lostPips;
		}
		System.out.println(
				header
				+" || "
				+" "+trades
				+" "+wins
				+" "+losses
				+" "+PrintUtils.Print2dec(winPer,false)
				+" "+PrintUtils.Print2dec(avg,false)
				+" "+PrintUtils.Print2dec(pf,false)				
				);
	}

	public static void main(String[] args) throws Exception {
		String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.11.21.csv";
		
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
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			for (int y1=2004;y1<=2016;y1++){
				int y2 = y1+0;
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+2;
					for (int thr=500;thr<=500;thr+=100){
						for (int tp=10;tp<=10;tp+=1){
							for (int sl=50;sl<=50;sl+=10){
								for (int minPips=0;minPips<=0;minPips+=10){
									String header = y1+" "+y2+" "+h1+" "+h2+" "+thr+" "+minPips+" "+tp+" "+sl;
									BounceNPeriods.doBasic1(header, data, maxMins, y1, y2, h1, h2, tp, sl, thr, minPips, 0.0, false);
								}
							}
						}
					}
				}
			}
		}//

	}

}
