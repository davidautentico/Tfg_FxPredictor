package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestBarReverses {
	
	public static void doTest(	String header,
			ArrayList<QuoteShort> data,
			int h1,int h2,
			int nbars,
			int nbars2,
			int minPips			
			){
		
		int diffacc = 0;
		int diffaccC = 0;
		int diffaccClose = 0;
		int lastDay = -1;
		int total = 0;
		int total5 = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		for (int i=nbars;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			//if (y<year1 || y>year2) continue;
			
			if (day!=lastDay){
				
				lastDay = day;
			}
			
			int diffH = q.getOpen5()-data.get(i-nbars).getLow5();
			int diffL = data.get(i-nbars).getHigh5()-q.getOpen5();
			
			int diff = 0;
			int diffc = 0;
			int diffClose = 0;
			boolean isTrade = false;
			if (h>=h1 && h<=h2 && min==0){
				if (diffH>=minPips*10){
					TradingUtils.getMaxMinShort(data, qm, calqm, i, i+nbars2);
					isTrade = true;
					diff = q.getOpen5()-qm.getLow5();
					diffc = qm.getHigh5()-q.getOpen5();
					diffClose = q.getOpen5()-qm.getClose5();
				}else if (diffL>=minPips*10){
					TradingUtils.getMaxMinShort(data, qm, calqm, i, i+nbars2);
					isTrade = true;
					diff = qm.getHigh5()-q.getOpen5();
					diffc = q.getOpen5()-qm.getLow5();
					diffClose = qm.getClose5()-q.getOpen5();
				}
			}
			
			if (isTrade){
				diffacc +=diff;
				diffaccC+=diffc;
				
					
				
				total++;
				if (diff>=50){
					total5++;
				}else{
					diffaccClose+=diffClose;
				}
			}			
		}
		
		int totalno50 = total-total5;
		double avg = diffacc*0.1/total;
		double avgC = diffaccC*0.1/total;
		double avgClose = diffaccClose*0.1/totalno50;
		double factor = avg/avgC;
		double per5 = total5*100.0/total;
		System.out.println(
				h1+" "+h2+" "+nbars+" "+minPips
				+" || "
				+" "+total+" "+total5+" "+(totalno50)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgC, false)
				+" "+PrintUtils.Print2dec(avgClose, false)
				+" "+PrintUtils.Print2dec(factor, false)
				+" "+PrintUtils.Print2dec(per5, false)
				);
	}

	public static void main(String[] args) throws Exception {

		String pathEURUSD = "C:\\fxdata\\eurUSD_UTC_5 Mins_Bid_2003.05.04_2016.10.04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2009.01.01_2016.09.20.csv";
		
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
			
			for (int h1=8;h1<=8;h1++){
				int h2=h1;
				for (int nbars=12*24;nbars<=12*24;nbars+=12){
					for (int minPips=0;minPips<=100;minPips+=10){
						TestBarReverses.doTest("", data, h1, h2, nbars,nbars, minPips);
					}
				}
			}
			
			
		}

	}

}
