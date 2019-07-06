package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class LossesInARow {
	
	public static void testAdding(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,int h3,
			int thr,
			int tp,
			int sl,
			int mode,double comm,boolean debug){
		
		int maxLosses = 0;
		int actualLosses = 0;
		ArrayList<PositionCore> positions = new ArrayList<PositionCore>();
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		boolean isFailedDay = false;
		boolean isTradedDay = true;
		int totalFailedDays = 0;
		int totalWinDays = 0;
		int dayTrades = 0;
		for (int i=1; i<data.size();i++){
			QuoteShort q = data.get(i);
			
			QuoteShort.getCalendar(cal, q);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (day!=lastDay){
				if (isTradedDay){
					if (isFailedDay){
						totalFailedDays++;
						actualLosses++;
						if (actualLosses>=maxLosses) maxLosses = actualLosses;
					}else{
						totalWinDays++;
						actualLosses = 0;
					}
				}
				dayTrades = 0;
				isFailedDay = false;
				isTradedDay = false;
				lastDay = day;
			}
			
			if (h>=h1 && h<=h2 
					&& dayTrades<1
					){			
				int maxMin = maxMins.get(i-1);			
				//evaluar entrada de nuevas operaciones
				PositionCore pos = null;
				if (maxMin>=thr){
					pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setTp(q.getOpen5()-10*tp);
					pos.setSl(q.getOpen5()+10*sl);
					pos.setPositionType(PositionType.SHORT);
					positions.add(pos);
					dayTrades++;
				}else if (maxMin<=-thr){
					pos = new PositionCore();
					pos.setEntry(q.getOpen5());
					pos.setTp(q.getOpen5()+10*tp);
					pos.setSl(q.getOpen5()-10*sl);
					pos.setPositionType(PositionType.LONG);
					positions.add(pos);
					dayTrades++;
				}
			}
			
			
			//evalucion entradas actuales
			int j = 0;
			while (j<positions.size()){
				PositionCore p = positions.get(j);
				boolean closed = false;
				int diff = 0;
				
				if (p.getPositionType()==PositionType.LONG){
					if (q.getLow5()<=p.getSl()){
						diff = p.getSl()-p.getEntry();
						closed = true;
					}else if (q.getHigh5()>=p.getTp()){
						diff = p.getTp()-p.getEntry();
						closed = true;
					}else if (h>=h3){
						diff = q.getClose5()-p.getEntry();
						closed = true;
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					if (q.getHigh5()>=p.getSl()){
						diff = p.getEntry()-p.getSl();
						closed = true;
					}else if (q.getLow5()<=p.getTp()){
						diff = p.getEntry()-p.getTp();
						closed = true;
					}else if (h>=h3){
						diff = p.getEntry()-q.getClose5();
						closed = true;
					}
				}
				
				
				if (closed){
					isTradedDay = true;
					//stats
					if (diff>=0){
						wins++;
						winPips += diff;
						//System.out.println("[win] "+p.getPositionType()+" "+p.getEntry()+" "+p.getSl()+" "+p.getTp()+" || "+diff+" || "+q.toString());
					}else{
						isFailedDay = true;
						losses++;
						lostPips += -diff;
						//System.out.println("[loss] "+p.getPositionType()+" "+p.getEntry()+" "+p.getSl()+" "+p.getTp()+" || "+diff+" || "+q.toString());
					}
					positions.remove(j);
				}else{
					j++;
				}
			}			
		}
		
		int trades = wins+losses;
		double avg = (winPips-lostPips)*0.1/trades;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		int totalTradedDays = totalWinDays + totalFailedDays;
		double winDays = totalWinDays*100.0/(totalTradedDays);
		System.out.println(
				y1+" "+y2
				+" "+h1+" "+h2+" "+h3
				+" "+thr
				+" "+tp
				+" "+sl
				+" || "
				+trades+" "+wins+" "+losses
				+" "+PrintUtils.Print2dec(winPer, false)						
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" "+PrintUtils.Print2dec(winDays, false)+" ("+PrintUtils.Print2Int(totalFailedDays,4)+"/"+PrintUtils.Print2Int(totalTradedDays,4)+") "	
				+" "+maxLosses
				);
		
	}

	public static void main(String[] args) throws Exception {
String pathEURUSD = "C:\\fxdata\\eurusd_UTC_5 Mins_Bid_2003.05.04_2016.07.04.csv";
		
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
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX);									
			dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
			ArrayList<QuoteShort> data = null;
			data = data5m;
			
			
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			System.out.println("total data: "+data.size()+" "+maxMins.size());
			
			for (int y1=2003;y1<=2003;y1+=1){
				int y2 = y1+13;
				for (int h1=0;h1<=23;h1++){
					int h2=h1+0;
					for (int h3=99;h3<=99;h3++){
						for (int thr= 100;thr<= 100;thr+= 100){
							for (int nbars = 36;nbars<= 36;nbars+=1){
								for (int tp=5;tp<=5;tp++){
									for (int sl=200;sl<=200;sl+=10){									
										LossesInARow.testAdding(data, maxMins,y1,y2, h1, h2,h3, thr,tp, sl,-1,0.0,false);
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
