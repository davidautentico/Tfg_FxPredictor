package drosa.phil;

import java.util.ArrayList;

import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class RangeDynamics {
	
	
	/**
	 * Aplicado únicamente a 1 día
	 * @param data
	 * @param umbral
	 */
	public static double lastTimeDOinDay(ArrayList<Quote> data,int umbral){
		
		Calendar actualDate = Calendar.getInstance();
		Calendar doDate     = Calendar.getInstance();
		double DO = data.get(0).getOpen();
		doDate.setTime(data.get(0).getDate());
		boolean hDOEnabled = false;
		boolean lDOEnabled = false;
		double timeSpread = 999999;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			actualDate.setTime(q.getDate());
			
			int hDO = TradingUtils.getPipsDiff(q.getHigh(), DO);
			int lDO = TradingUtils.getPipsDiff(DO,q.getLow());
			
			if (hDO>umbral){
				hDOEnabled = true;
			}
			if (lDO>umbral){
				lDOEnabled = true;				
			}
			
			if (hDOEnabled && lDOEnabled){
				return (actualDate.getTimeInMillis()-doDate.getTimeInMillis())/60000;
			}
		}
		return timeSpread;
	}
	
	public static double survivorSpreadRate(ArrayList<Double> spreads,int umbral){
		
		int count = 0;
		int survivors = 0;
		for (int i=0;i<spreads.size();i++){
			double spread = spreads.get(i);
			if (spread>=umbral){
				//System.out.println(umbral+" spread: "+spread);
				if (spread<900000){
					survivors++;
				}
				count++;
			}
		}
		if (count ==0) return -1;
		return survivors*100.0/count;
	}
	
	public static void studyTimetoDO(ArrayList<Quote> data,Calendar from,Calendar to,int umbral){
		
		Calendar actualDate = Calendar.getInstance();
		int actualDay = -1;
		ArrayList<Quote> dayQuotes = new ArrayList<Quote>();
		int count = 0;
		double avg =0;
		int success=0;
		ArrayList<Double> spreads = new ArrayList<Double>();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);			
			actualDate.setTime(q.getDate());
			int day = actualDate.get(Calendar.DAY_OF_YEAR);
			if (actualDate.getTimeInMillis()<from.getTimeInMillis()) continue;
			if (actualDate.getTimeInMillis()>to.getTimeInMillis()){
				break;
			}
			
			if (day!=actualDay){
				if (dayQuotes.size()>0){
					//llamar a la funcion
					double timeSpread = lastTimeDOinDay(dayQuotes,umbral);
					//System.out.println("Elapsed: "+PrintUtils.Print(timeSpread));
					if (timeSpread<900000){
						avg+=timeSpread;
						success++;						
					}
					spreads.add(timeSpread);
					count++;
				}
				dayQuotes.clear();
				actualDay = day;
			}
			dayQuotes.add(q);
		}
		System.out.println("umbral total success avg: "
				+" "+umbral
				+" "+count
				+" "+PrintUtils.Print4dec(success*100.0/count)+"%"
				+" "+PrintUtils.Print4dec(avg/success)
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,60))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,120))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,180))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,240))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,300))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,360))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,420))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,480))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,540))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,600)) //10h
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,660))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,720))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,780))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,840)) //14h
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,900))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,960))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,1020))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,1080))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,1140))
				+" "+PrintUtils.Print4dec(survivorSpreadRate(spreads,1200))
				);
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m = "c:\\fxdata\\EURUSD_5 Mins_Bid_2006.01.01_2013.11.13.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 			= TradingUtils.cleanWeekendData(dataS);
		
		int yearF      	 	= 2013;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2013;
		int monthL 			= Calendar.DECEMBER;
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		
		for (int year=2006;year<=2006;year++){
			from.set(year, Calendar.JANUARY, 1);
			to.set(year+7, Calendar.DECEMBER, 31);
			for (int i=1;i<=50;i++)
				RangeDynamics.studyTimetoDO(data, from, to, i);
		}
		
	}

}
