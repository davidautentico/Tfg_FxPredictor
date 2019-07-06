package drosa.experimental.ticksStudy;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.Quote;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestPepperstoneTicks {

	public static double avgSpread(ArrayList<Tick> ticks,int h1,int h2,int min1,int min2, boolean debug){
		Calendar cal = Calendar.getInstance();
		int avg = 0;
		int total = 0;
		for (int i=0;i<ticks.size();i++){
			Tick t = ticks.get(i);
			Tick.getCalendar(cal, t);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			if (h<h1 || h>h2) continue;
			if (min<min1 || min>min2) continue;
			avg += t.getSpread();
			total++;
			if (t.getSpread()>=10.0){
				//System.out.println(t.toString());
			}
		}
		if (debug)
			System.out.println(h1+" "+h2+" "+min1+" "+min2+" "+PrintUtils.Print2(avg*1.0/total));
		if (total>0)
		return avg*1.0/total;
		
		return -1;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String tickFolder = "C:\\fxdata\\ticks\\pepper recorded\\";
		String tickPageFolder = "C:\\fxdata\\tickdata pepper page\\";
		
		/*ArrayList<Tick> ticks = Tick.readFromDisk(tickFolder+"EURUSD_2014.11.03.csv",1);		
		for (int h=0;h<=23;h++){
			avgSpread(ticks,h,h);
		}*/
		
		int min1 = 0;
		int min2 = 0;
		for (int h=0;h<=23;h++){
			for (min1=0;min1<=59;min1++){
				min2 = min1;
				int total=0;
				double avg=0;
				for (int year=2014;year<=2014;year++){
					int totaly=0;
					int avgy=0;
					for (int m=1;m<=12;m++){
						for (int d=1;d<=31;d++){
							//String fileName = tickPageFolder+"EURUSD-"+year+"-"+decodeMonth(m)+".csv";
							int type=1;
							String fileName = tickFolder+"EURUSD_"+year+"."+decodeNumber(m)+"."+decodeNumber(d)+".csv";
							File f = new File(fileName);
							if (!f.exists()){
								//System.out.println(fileName+" no existe");
								continue;
							}
							Sizeof.runGC ();
							ArrayList<Tick> ticks2      = Tick.readFromDisk(fileName,type);
							ArrayList<Tick> tickClean = null;
							if (type==1) tickClean = ticks2;
							else{
								ArrayList<Tick> ticksS 		= TestLines.calculateCalendarAdjustedT(ticks2);
						  		tickClean 	= TradingUtils.cleanWeekendDataT(ticksS);
							}
					  		
							double spread = avgSpread(tickClean,h,h,min1,min2,false);
							if (spread>=0){
								//System.out.println("calculado "+year+" "+m+" "+d+" "+spread);
								avg+=spread;
								total++;
								avgy+=spread;
								totaly++;
							}
						}
					}
					//System.out.println(h+" "+PrintUtils.Print2(avgy*1.0/totaly));
				}
				System.out.println(h+" "+min1+" "+PrintUtils.Print2(avg*0.1/total));
			}
		}
		
	}



	private static String decodeNumber(int m) {
		// TODO Auto-generated method stub
		String num = String.valueOf(m+100);
		return num.substring(1);
	}

}
