package drosa.experimental.ticksStudy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.data.DataUtils;
import drosa.data.TickQuote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TickVolumeTest {
	
	
	public static void doGetVolume1sec(
			String fileNameTicks,
			HashMap<Integer,Short> vol1sec){
		
		ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileNameTicks,1);
		//ArrayList<String> dataVolumeS = new ArrayList<String>(); 
		
		
		System.out.println("Size: "+data.size());
	
		int actualSecond = -1;
		int lastSecond = -1;
		
		//ANIO.MES.DIA HH:MM:SS
		short acc = 0;
		for (int i=1;i<data.size();i++){
			TickQuote t1 = data.get(i-1);
			TickQuote t = data.get(i);
			actualSecond = DataUtils.getDateInSeconds(t1.getYear(), t1.getMonth(), t1.getDay(), t1.getHh(),t1.getMm(),t1.getSs());
			
			//System.out.println(t.toString());
			if (actualSecond!=lastSecond){
				if (lastSecond!=-1){
					String valueStr = 
							DateUtils.datePrint(t1.getYear(), t1.getMonth(), t1.getDay(), t1.getHh(),t1.getMm(),t1.getSs())
							+" "+acc;
					//dataVolumeS.add(valueStr);
					//System.out.println(actualSecond+" || "+valueStr);
					vol1sec.put(lastSecond, acc);
				}
				acc = 0;
				lastSecond = actualSecond;
			}
			
			acc++;			
		}
		
		data.clear();
		
		System.out.println("Size volumes: "+vol1sec.size());
	}
	
	
	private static void save1secVols(String fileName1SecOut, ArrayList<QuoteShort> data,
			HashMap<Integer, Short> vol1sec) {
		// TODO Auto-generated method stub
		
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(fileName1SecOut));
		
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				QuoteShort q = data.get(i);
				QuoteShort.getCalendar(cal, q);
				int year = cal.get(cal.YEAR);
				int month = cal.get(Calendar.MONTH);
				int day = cal.get(Calendar.DAY_OF_MONTH);
				int hh = cal.get(Calendar.HOUR_OF_DAY);
				int mm = cal.get(Calendar.MINUTE);
				int ss = cal.get(Calendar.SECOND);
				int qsec = DataUtils.getDateInSeconds((short)year, (byte)(month+1), (byte)day, (byte)hh, (byte)mm, (byte)ss);
				
				int vol = 0;
				if (vol1sec.containsKey(qsec)){
					vol = vol1sec.get(qsec); 
					/*(System.out.println(DateUtils.datePrint(cal)
							+" || "+q.toString()+" "+vol1sec.get(qsec)
							);*/
				}
				String valueStr = q.toString()+" "+vol;
				pw.write(valueStr+'\n');
				
			}
			pw.close();
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void save1secMaxMins(String fileName1SecOutMaxMin, ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(fileName1SecOutMaxMin));
		
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				QuoteShort q = data.get(i);
				
				String valueStr = q.toStringExt()+" "+maxMins.get(i);
				pw.write(valueStr+'\n');			
			}
			pw.close();
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void testVolumes(ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,
			int h1,int h2,
			int thr,
			int bars
			) {
		
		int totalPips = 0;
		int trades = 0;
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		for (int i=1;i<data.size()-bars;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY); 
			//System.out.println(q.toStringExt());
			
			if (y>=y1 && y<=y1 
					&& h>=h1 && h<=h2){
				int mode = 0;
				int maxMin = maxMins.get(i-1);
				if (maxMin>=thr){					
					mode = -1;
				}else if (maxMin<=-thr){					
					mode = 1;
				}
				
				if (mode!=0){
					TradingUtils.getMaxMinShort(data, qm, calm, i, i+bars);
					int diffH = qm.getHigh5()-q.getOpen5();
					int diffL = q.getOpen5()-qm.getLow5();
					
					if (mode==1){
						int diff = diffH-diffL;
						if (diff>=0){
							wins++;
						}
						trades++;
						totalPips += diff;
					}else if (mode==-1){
						int diff = diffL-diffH;
						if (diff>=0){
							wins++;
						}
						trades++;
						totalPips += diff;
					}
				}
			}
		}
		
		double avg = totalPips*0.1/trades;
		System.out.println(
				thr+" "+bars
				+" || "
				+" "+trades
				+" "+wins
				+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
	private static void testVolumes2(ArrayList<QuoteShort> data,
			//ArrayList<Integer> maxMins,
			int h1,int h2,
			int thr,
			int bars
			) {
		
		int totalPips = 0;
		int trades = 0;
		int wins = 0;
		int losses = 0;
		QuoteShort qm = new QuoteShort();
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		for (int i=1;i<data.size()-bars;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY); 
			//System.out.println(q.toStringExt());
			
			if (h>=h1 && h<=h2){
				int mode = 0;
				long maxMin = data.get(i-1).getMaxMin();
				if (maxMin>=thr){					
					mode = -1;
				}else if (maxMin<=-thr){					
					mode = 1;
				}
				
				if (mode!=0){
					TradingUtils.getMaxMinShort(data, qm, calm, i, i+bars);
					int diffH = qm.getHigh5()-q.getOpen5();
					int diffL = q.getOpen5()-qm.getLow5();
					
					if (mode==1){
						int diff = diffH-diffL;
						if (diff>=0){
							wins++;
						}
						trades++;
						totalPips += diff;
					}else if (mode==-1){
						int diff = diffL-diffH;
						if (diff>=0){
							wins++;
						}
						trades++;
						totalPips += diff;
					}
				}
			}
		}
		
		double avg = totalPips*0.1/trades;
		System.out.println(
				thr+" "+bars
				+" || "
				+" "+trades
				+" "+wins
				+" "+PrintUtils.Print2dec(wins*100.0/trades, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
	public static void main(String[] args) throws Exception {
		
		String fileNameTicks	= "c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.12.31_2017.06.05.csv";
		String fileName1Sec 	= "c:\\fxdata\\EURUSD_UTC_1 Sec_Bid_2016.12.31_2017.06.05.csv"; 
		String fileName1SecOut 	= "c:\\fxdata\\EURUSD_UTC_1 SecVol_Bid_2016.12.31_2017.06.05.csv"; 
		String fileName1SecOutMaxMin 	= "c:\\fxdata\\EURUSD_UTC_1 SecVolMaxMin_Bid_2016.12.31_2017.06.05.csv";
		String fileName5min	= "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2017.05.31.csv";
		
		
		
		/*ArrayList<QuoteShort> data =  DAO.retrieveDataShort( fileName1SecOutMaxMin, DataProvider.DAVEVOLMAXMIN);		
		for (int h1=0;h1<=23;h1++){
			int h2 = h1;
			for (int thr = 10000;thr<=10000;thr+=600){
				for (int bars=7200;bars<=7200;bars++){
					TickVolumeTest.testVolumes2(data,h1,h2,thr,bars);
				}
			}
		}*/
		
		//TickVolumeTest.save1secMaxMins(fileName1SecOutMaxMin,data,maxMins);
		
		ArrayList<QuoteShort> data = null;
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(fileName5min);		
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
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			data = TradingUtils.cleanWeekendDataS(dataI);  
		}
		ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		System.out.println("Size vol: "+data.size());
		
		for (int y1=2004;y1<=2017;y1++){
			int y2 = y1;
			for (int thr = 500;thr<=500;thr+=10){
				for (int bars=60;bars<=60;bars++){
					TickVolumeTest.testVolumes(data,maxMins,y1,y2,1,1,thr,bars);
				}
			}
		}
		
		/**MODULO PARA CREAR EL FICHERO DE VOLUMEN PARA 1 SEC**/
		/*ArrayList<QuoteShort> data1s = new ArrayList<QuoteShort>();	
		ArrayList<QuoteShort> data 			= null;
		HashMap<Integer,Short> vol1sec = new HashMap<Integer,Short>();	
		
		TickVolumeTest.doGetVolume1sec(fileNameTicks,vol1sec);
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(fileName1Sec);		
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
			dataI 		= DAO.retrieveDataShort(path, DataProvider.DUKASCOPY_FOREX3);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			data = TradingUtils.cleanWeekendDataS(dataI);  
		}
		
		System.out.println("Size 1sec: "+data.size());
		
		TickVolumeTest.save1secVols(fileName1SecOut,data,vol1sec);*/
		
	}


	


	




	

}
