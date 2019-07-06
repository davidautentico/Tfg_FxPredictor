package drosa.experimental.ticksStudy;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class TestTicks {

	public static void decodeTickDNA(ArrayList<Tick> ticks,int h){
		long accP = 0;
		short accN = 0;
		int countP = 0;
		int countN = 0;
		int lastBid = -1;
		long totalticks = 0;
		long totalspread = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<ticks.size();i++){
			Tick tick = ticks.get(i);
			Tick.getCalendar(cal, tick);
			int offset = DateUtils.calculatePepperGMTOffset(cal);
			cal.add(Calendar.HOUR_OF_DAY, offset);
			int hr = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			if (hr==h
				&& ((hr==0 && min>=20) || hr!=0)	
					){
				if (i>0){
					int diff = tick.getBid()-lastBid;
					if (diff>0){
						countP++;
						accP +=diff;
					}
					if (diff<0){
						countN++;
						accN += diff;
					}
					totalspread += Math.abs(tick.getAsk()-tick.getBid());
					totalticks++;
					//System.out.println(tick.getAsk()+" "+tick.getBid());
				}
			}			
			lastBid = tick.getBid();
		}
		double factor     = Math.abs(countP*1.0/countN);
		double factorDiff = Math.abs(accP*1.0/accN);
		System.out.println(h+" "+PrintUtils.Print2(factor)+" "+PrintUtils.Print2(factorDiff)+" || "+PrintUtils.Print2(totalspread*0.1/totalticks));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "c:\\fxdata\\";
		
		String pathTick2016 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.12.31_2016.07.04.csv";
		String pathTick2015 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.12.31_2015.12.30.csv";
		String pathTick2014 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.12.31_2014.12.30.csv";
		String pathTick2013 = "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.12.31_2013.12.30.csv";
		//String pathTick2014 = "c:\\fxdata\\dukascopy tickdata\\EURUSD_UTC_Ticks_Bid_2013.12.31_2014.12.30.csv";
		//String pathTick2013 = "c:\\fxdata\\dukascopy tickdata\\EURUSD_UTC_Ticks_Bid_2012.12.31_2013.12.30.csv";
		//String pathTick2014 = "c:\\fxdata\\dukascopy tickdata\\EURUSD_UTC_Ticks_Bid_2014.07.01_2014.12.31.csv";
		String path1m = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2008.12.31_2014.12.30.csv";
		
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int year=2008;year<=2016;year++){
			String symbol = "usdjpy";
			String yearPath = "_UTC_Ticks_Bid_"+String.valueOf(year)+".csv";
			String pathcomplete = path+symbol+yearPath;
			
			ticks.clear();
			Tick.readFromDisk(ticks,pathcomplete, 2);
			System.out.println("ticks: "+ticks.size());
			for (int h=0;h<=1;h++){
				TestTicks.decodeTickDNA(ticks,h);
			}
		}
		
	}

}
