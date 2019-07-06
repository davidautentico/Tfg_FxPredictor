package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class Compounding {
	
	public static void doTest(ArrayList<QuoteShort> data, 			
			ArrayList<Integer> maxMins,
			int h1,int h2,
			int blockSize,
			int aRange,
			int thr,
			int debug){
		
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalDays = 0;
		int ref = -1;
		int upLimit = -1;
		int downLimit = -1;
		int upPP = 0;
		int downPP = 0;
		int buyTarget = -1;
		int sellTarget = -1;
		int actualMode = 0;
		int upTouches = 0;
		int downTouches = 0;
		int buys = 0;
		int sells = 0;
		int accDesv = 0;
		int high = -1;
		int low = -1;
		int lastHigh = -1;
		int lastLow = -1;
		int slActual = -1;
		int priceActual = -1;
		int mode = 0;
		int nivelActual = 0;
		int range = 800;
		ArrayList<Integer> nivelesAlcanzados = new ArrayList<Integer>();
		for (int i=1;i<data.size()-1;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i+1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (day!=lastDay){
				
				range = high-low;
				
				lastHigh = high;
				lastLow = low;
				high = -1;
				low = -1;
				lastDay = day;
				
				if (mode==3) mode=0;
			}
			
			int actualRange = high-low;
			int maxMin = maxMins.get(i-1);
			if (mode==0 && lastHigh!=-1
					&& maxMin>=thr
					//&& actualRange>=aRange
					//&& range>=aRange
					&& h>=h1 && h<=h2
					){
				if (true
						//q.getOpen5()<=lastHigh 
						&& q.getHigh5()>=lastHigh){
					mode = 1;
					int price = q.getClose5();
					slActual = price -blockSize;
					priceActual = price;
					nivelActual = 0;
					mode=1;
				}else if (true
						//q.getOpen5()>=lastLow 
						&& q.getLow5()<=lastLow){
					mode = 1;
					int price = q.getClose5();
					slActual = price+blockSize;
					priceActual = price;
					nivelActual = 0;
					mode = -1;
				} 
			}else if (mode==1){
				if (q.getLow5()<=slActual){
					nivelesAlcanzados.add(nivelActual);
					mode = 0;
					nivelActual=0;
				}else{
					for (int j=20;j>=1;j--){
						int level = priceActual+j*blockSize;
						if (q.getHigh5()>=level){
							priceActual = level;
							slActual = priceActual-blockSize;
							nivelActual += j;
							break;
						}
					}
				}
			}else if (mode==-1){
				if (q.getHigh5()>=slActual){
					nivelesAlcanzados.add(nivelActual);
					mode = 0;
					nivelActual=0;
				}else{
					for (int j=20;j>=1;j--){
						int level = priceActual-j*blockSize;
						if (q.getLow5()<=level){
							priceActual = level;
							slActual = priceActual+blockSize;
							nivelActual += j;
							break;
						}
					}
				}
			}
			
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
			}
		}
		
		int totalNivels = nivelesAlcanzados.size();
		String str ="";
		int c0=0;
		int c1=0;
		int c2=0;
		int c3=0;
		int c4=0;
		int c5=0;
		int c6=0;
		int c7=0;
		int c8=0;
		int c9=0;
		int v1 = 20;
		int v2 = 60;
		int v3 = 140;
		int v4 = 300;
		int v5 = 620;
		int v6 = 1080;
		int v7 = 2180;
		int v8 = 4380;
		int v9 = 8780;
		for (int i=0;i<nivelesAlcanzados.size();i++){
			int level = nivelesAlcanzados.get(i);
			if (level>=9) c9++;
			if (level>=8) c8++;
			if (level>=7) c7++;
			if (level>=6) c6++;
			if (level>=5) c5++;
			if (level>=4) c4++;
			if (level>=3) c3++;
			if (level>=2) c2++;
			if (level>=1) c1++;
			if (level>=0) c0++;
		}
		
		System.out.println(
				h1+" "+h2				
				+" || "+totalNivels
				+" "
				+" "+c0
				//+" "+c1
				+" "+PrintUtils.Print2dec(c1*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v1*(c1*100.0/c0)/(20*(100.0-(c1*100.0/c0))), false)+"] "
				//+" "+c2
				+" "+PrintUtils.Print2dec(c2*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v2*(c2*100.0/c0)/(20*(100.0-(c2*100.0/c0))), false)+"] "
				//+" "+c3
				+" "+PrintUtils.Print2dec(c3*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v3*(c3*100.0/c0)/(20*(100.0-(c3*100.0/c0))), false)+"] "
				//+" "+c4
				+" "+PrintUtils.Print2dec(c4*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v4*(c4*100.0/c0)/(20*(100.0-(c4*100.0/c0))), false)+"] "
				//+" "+c5
				+" "+PrintUtils.Print2dec(c5*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v5*(c5*100.0/c0)/(20*(100.0-(c5*100.0/c0))), false)+"] "
				//+" "+c6
				+" "+PrintUtils.Print2dec(c6*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v6*(c6*100.0/c0)/(20*(100.0-(c6*100.0/c0))), false)+"] "
				+" "+PrintUtils.Print2dec(c7*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v7*(c7*100.0/c0)/(20*(100.0-(c7*100.0/c0))), false)+"] "
				+" "+PrintUtils.Print2dec(c8*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v8*(c8*100.0/c0)/(20*(100.0-(c8*100.0/c0))), false)+"] "
				+" "+PrintUtils.Print2dec(c9*100.0/c0, false)
				+" ["+PrintUtils.Print2dec(v9*(c9*100.0/c0)/(20*(100.0-(c9*100.0/c0))), false)+"] "
				);
	}

	public static void main(String[] args) {
String path0 ="C:\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2018.12.10_(1).csv";
		
		//String pathEURUSD = path0+"EURUSD_30 Secs_Bid_2009.01.01_2018.12.12.csv";
		
		String pathEURUSD_bricks5 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_50_bricks.csv";
		String pathEURUSD_bricks10 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_100_bricks.csv";
		String pathEURUSD_bricks20 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_200_bricks.csv";
		String pathEURUSD_bricks40 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_400_bricks.csv";
		String pathEURUSD_bricks60 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_600_bricks.csv";
		String pathEURUSD_bricks80 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_800_bricks.csv";
		String pathEURUSD_bricks100 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_1000_bricks.csv";
		String pathEURUSD_bricks150 = path0+"EURUSD_Ticks_2010.01.01_2018.12.31.csv_1500_bricks.csv";
		//String pathEURUSD = path0+"EURUSD_Ticks_2018.10.04_2018.10.04.csv_50_bricks.csv";
		
		String pathNews = path0+"News.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		try {
			Sizeof.runGC ();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		//FFNewsClass.readNews(pathNews,news,0);
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);	
			
			dataI 		= new ArrayList<QuoteShort>();
			
			//Tick.readFromDiskToQuoteShort(dataI, path, 5);
			
			/*for (int sizeBrick=50;sizeBrick<=50;sizeBrick+=50){
				dataI.clear();
				
				Tick.createBricks(dataI, path, 5,sizeBrick);	
				
				TestLines.calculateCalendarAdjustedSinside(dataI);			
				dataS = TradingUtils.cleanWeekendDataS(dataI);  
				ArrayList<QuoteShort> data = null;
				data = dataS;			
				System.out.println("Total size: "+data.size());	
				
				QuoteShort.saveToDisk(data, pathEURUSD+'_'+sizeBrick+"_bricks.csv");
				System.out.println("Total size: "+data.size());	
			}*/
			
															
			//ArrayList<QuoteShort> data = QuoteShort.readFromDisk(pathEURUSD_bricks5);
			
			/*ArrayList<QuoteShort> data2 = QuoteShort.createBricksFromBricks(data, 1000);
			QuoteShort.saveToDisk(data2,pathEURUSD_bricks100);
			
			data2 = QuoteShort.createBricksFromBricks(data, 1500);
			QuoteShort.saveToDisk(data2,pathEURUSD_bricks150);*/
			System.out.println("path: "+path);
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = dataS;
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			Calendar cal = Calendar.getInstance();
			
			for (int aRange=0;aRange<=0;aRange+=100){
				for (int h1=0;h1<=0;h1++){
					int h2 = h1+9;
					for (int blockSize=600;blockSize<=600;blockSize+=100){
						for (int thr=0;thr<=1000;thr+=10){
							Compounding.doTest(data,maxMins, h1, h2, blockSize,aRange,thr, 0);
						}
					}
				}
			}
			
		}

	}

}
