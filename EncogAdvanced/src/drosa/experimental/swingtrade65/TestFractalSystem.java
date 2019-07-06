package drosa.experimental.swingtrade65;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestFractalSystem {
	
	
	public static void test(ArrayList<QuoteShort> data,
			ArrayList<Integer> fractals,
			int y1,int y2,int h1,int h2,int tp,int sl,int debug){
		
		Calendar cal = Calendar.getInstance();
		
		int lastUpFractal = -1;
		int lastDownFractal = -1;
		int mode = 0;
		int maxPositive = 0;
		int acc = 0;
		int count = 0;
		int entry = 0;
		int lastBreak = 0;
		for (int i=3;i<data.size();i++){
			
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);			
			int h = cal.get(Calendar.HOUR_OF_DAY);	
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			int fractal = fractals.get(i);
			
			if (debug==1){
				System.out.println(DateUtils.datePrint(cal)+" || "+lastUpFractal+" "+lastDownFractal);
			}
			
			if (mode==0){
				if (lastUpFractal!=-1
						&& q.getOpen5()<=lastUpFractal && q.getHigh5()>=lastUpFractal
						&& h>=h1 && h<=h2
						){
					entry = lastUpFractal;
					mode = 1;
					
					if (debug==2){
						System.out.println(DateUtils.datePrint(cal)+" || [BREAK UP] "+entry+" "+maxPositive);
					}
					lastBreak = lastUpFractal;
					maxPositive = 0;
				}
				if (lastDownFractal!=-1
						&& q.getOpen5()>=lastDownFractal && q.getLow5()<=lastDownFractal
						){
					entry = lastDownFractal;
					mode = -1;
					
					if (debug==2){
						System.out.println(DateUtils.datePrint(cal)+" || [BREAK DOWN] "+entry+" "+maxPositive);
					}
					lastBreak = lastDownFractal;
					maxPositive = 0;
				}
			}else if (mode==1){
				if (lastDownFractal!=-1
						&& q.getOpen5()>=lastDownFractal && q.getLow5()<=lastDownFractal
						){									
					acc += maxPositive;
					count++;
					if (debug==2){
						System.out.println(DateUtils.datePrint(cal)+" || [BREAK DOWN] "+(lastDownFractal-entry)+" "+maxPositive+" || "+count+" "+acc+" || "+q.toString());
					}
					maxPositive = 0;
					lastBreak = lastDownFractal;
					if (h>=h1 && h<=h2){
						entry = lastDownFractal;
						mode = -1;
					}else{
						mode = 0;
					}
				}else{
					if (lastUpFractal!=-1
							&& q.getOpen5()<=lastUpFractal && q.getHigh5()>=lastUpFractal && lastUpFractal!=lastBreak){
						if (debug==2){
							System.out.println(DateUtils.datePrint(cal)+" || [**BREAK UP**] "+lastUpFractal);
						}
						lastBreak = lastUpFractal;
					}					
				}
			}else if (mode==-1){
				if (lastUpFractal!=-1
						&& q.getOpen5()<=lastUpFractal && q.getHigh5()>=lastUpFractal
						){										
					acc += maxPositive;
					count++;
					if (debug==2){
						System.out.println(DateUtils.datePrint(cal)+" || [BREAK UP] "+(entry-lastUpFractal)+" "+maxPositive+" || "+count+" "+acc+" || "+q.toString());
					}
					maxPositive = 0;
					lastBreak = lastUpFractal;
					if (h>=h1 && h<=h2){
						entry = lastUpFractal;
						mode = 1;
					}else{
						mode = 0;
					}
				}else{
					if (lastDownFractal!=-1
							&& q.getOpen5()>=lastDownFractal && q.getLow5()<=lastDownFractal && lastDownFractal!=lastBreak
							){
						if (debug==2){
							System.out.println(DateUtils.datePrint(cal)+" || [**BREAK DOWN**] "+lastDownFractal);
						}
						lastBreak = lastDownFractal;
					}
				}
			}
			
			
			if (mode==1){
				int max = q.getHigh5()-entry;
				if (max>=maxPositive){
					maxPositive = max;
					//System.out.println("NUEVO MAXIMO "+maxPositive+" || "+entry);
				}
			}else if (mode==-1){
				int max = entry-q.getLow5();
				if (max>=maxPositive){
					maxPositive = max;
					//System.out.println("NUEVO MINIMO "+maxPositive);
				}
			}
			//check for UP fractal
			
			if (fractal==1){
				
				lastUpFractal = q.getHigh5();
				if (debug==2){
					//System.out.println("[UP FRACTAL] "+lastUpFractal+" / "+lastDownFractal+" || "+q.toString());	
				}
			}
			if (fractal==-1){
				lastDownFractal = q.getLow5();
				if (debug==2){
					//System.out.println("[DOWN FRACTAL] "+lastDownFractal+" / "+lastUpFractal+" || "+q.toString());	
				}
			}
		}
		
		double avg = acc*0.1/count;
		System.out.println(
				y1+" "+y2+" "+h1+" "+h2
				+" || "
				+" "+count+" "+PrintUtils.Print2dec(avg, false)
				);
	}
	

	public static void main(String[] args) throws Exception {
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.03.23.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2015_10_28_2015_01_04.csv";
		//String pathEURUSD = "C:\\fxdata\\EURUSD5_pepper_2013_08_29_2015_04_21.csv";
		String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_5 Mins_Bid_2003.05.04_2016.01.08.csv";
		//String pathEURJPY = "C:\\fxdata\\EURJPY_UTC_1 Min_Bid_2003.05.04_2015.12.17.csv";
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_1 Min_Bid_2008.12.31_2017.03.23.csv";
		//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2015.12.15.csv";		
		//String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_1 Min_Bid_2008.12.31_2015.12.08.csv";
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2017.03.23.csv";
		String pathGBPJPY = "C:\\fxdata\\GBPJPY_UTC_5 Mins_Bid_2003.05.04_2017.03.23.csv";
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_1 Min_Bid_2003.05.04_2017.03.20.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);paths.add(pathGBPUSD);
		paths.add(pathUSDJPY);paths.add(pathAUDUSD);
		paths.add(pathEURJPY);paths.add(pathGBPJPY);

		
		int total = 2;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 1;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<QuoteShort> dailyData 		= null;
		
		for (int i = 1;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			data = dataS;
			
			int hourBreak = 48;
			
						
			ArrayList<QuoteShort> dataH = ConvertLib.create4H(data);
			
			ArrayList<Integer> fractals = TradingUtils.calculateFractals(dataH,2);
			
			System.out.println(data.size()+" "+dataH.size()+" "+fractals.size());
			
		
			for (int y1=2017;y1<=2017;y1++){
				int y2 = y1+8;
				for (int h1=0;h1<=0;h1+=4){
					int h2 = h1+23;
					TestFractalSystem.test(dataH, fractals, y1, y2, h1, h2, 50, 50, 2);
				}
			}
		}

	}

}
