package drosa.data;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestData {

	public static void main(String[] args) {
		//String eurusdPepper = "C:\\fxdata\\EURUSD5_pepper_2016_03_10_2017_01_03.csv";
		String eurusdPepper = "C:\\fxdata\\EURUSD5_pepper_2013_08_29_2017_01_03_GAPS.csv";
		String eurusdDukas = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.01.03.csv";

		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		dataI 		= DAO.retrieveDataShort5m(eurusdDukas, DataProvider.DUKASCOPY_FOREX3);									
		dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
		ArrayList<QuoteShort> data5m 	= TradingUtils.cleanWeekendDataS(dataS); 	
		ArrayList<QuoteShort> data = null;
		ArrayList<QuoteShort> dataNoise = null;
		data = data5m;
		
		ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		
		ArrayList<QuoteShort> dataIp 		= null;
		dataIp 		= DAO.retrieveDataShort5m(eurusdPepper, DataProvider.PEPPERSTONE_FOREX);	
		ArrayList<QuoteShort> datap 	= TradingUtils.cleanWeekendDataS(dataIp); 	
		
		System.out.println(data.size()+" "+datap.size());
		
		Calendar cal = Calendar.getInstance();
		Calendar calp = Calendar.getInstance();
		int y1 = 2017;
		
		for (int h1=0;h1<=0;h1++){
			int h2 = 23;
			for (int thr=0;thr<=0;thr+=100){
				int index = 0;
				int totalFounds = 0;
				int highDiffs = 0;
				int lowDiffs = 0;
				int totalLess10 = 0;
				int totalLess20 = 0;
				int totalLess30 = 0;
				int totalLess40 = 0;
				for (int i=0;i<datap.size();i++){
					QuoteShort qp = datap.get(i);
					QuoteShort.getCalendar(calp, qp);
					
					int yp = calp.get(Calendar.YEAR);
					int dayp = calp.get(Calendar.DAY_OF_YEAR);
					int hp = calp.get(Calendar.HOUR_OF_DAY);
					int minp = calp.get(Calendar.MINUTE);
					int dayWeek = calp.get(Calendar.DAY_OF_WEEK);
					
					//System.out.println(qp.toString());
					boolean found = false;			
					for (int j=index;j<data.size() && !found;j++){
						QuoteShort q = data.get(j);
						QuoteShort.getCalendar(cal, q);
						int y 	= cal.get(Calendar.YEAR);
						int day = cal.get(Calendar.DAY_OF_YEAR);
						int h 	= cal.get(Calendar.HOUR_OF_DAY);
						int min = cal.get(Calendar.MINUTE);
						int maxMin = maxMins.get(j);
						
						if (yp==y && dayp==day && hp==h && minp==min){
							found = true;					
							index=j;
							
							if (h>=h1 && h<=h2 
									//&& y1==2017
									&& maxMin>=thr || maxMin<=-thr
									){
								totalFounds++;
								highDiffs += Math.abs(q.getHigh5()-qp.getHigh5());
								lowDiffs += Math.abs(q.getLow5()-qp.getLow5());
								
								int highDiff = Math.abs(q.getHigh5()-qp.getHigh5());
								int lowDiff = Math.abs(q.getLow5()-qp.getLow5());
								
								if (highDiff<=10 && lowDiff<=10){
									//System.out.println(q.toString() +" || "+qp.toString());
									totalLess10++;
								}
								if (highDiff<=20 && lowDiff<=20){
									//System.out.println(q.toString() +" || "+qp.toString());
									totalLess20++;
								}
								if (highDiff<=30 && lowDiff<=30){
									//System.out.println(q.toString() +" || "+qp.toString());
									totalLess30++;
								}
								if (highDiff<=40 && lowDiff<=40){
									//System.out.println(q.toString() +" || "+qp.toString());
									totalLess40++;
								}
								if (highDiff>40 || lowDiff>=40){
									System.out.println(q.toString() +" || "+qp.toString());
									//totalLess40++;
								}
							}
							/*System.out.println(totalFounds
									+" "+PrintUtils.Print2dec(highDiffs*0.1/totalFounds, false)
									+" "+PrintUtils.Print2dec(lowDiffs*0.1/totalFounds, false)
									);*/
						}
					}			
				}
				System.out.println(
						h1+" "+h2+" "+thr
						+" ||| "+totalFounds
						+" "+PrintUtils.Print2dec(highDiffs*0.1/totalFounds, false)
						+" "+PrintUtils.Print2dec(lowDiffs*0.1/totalFounds, false)
						+" || "+PrintUtils.Print2dec(totalLess10*100.0/totalFounds, false)
						+" "+PrintUtils.Print2dec(totalLess20*100.0/totalFounds, false)
						+" "+PrintUtils.Print2dec(totalLess30*100.0/totalFounds, false)
						+" "+PrintUtils.Print2dec(totalLess40*100.0/totalFounds, false)
						);
			}//thr
		}//h1
	}

}
