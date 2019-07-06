package drosa.experimental.basicStrategies.strats2018;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.experimental.zznbrum.TrendClass;
import drosa.experimental.zznbrum.TrendInfo;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyTrendsFeb18 {
	
	public static void doTest(ArrayList<QuoteShort> data,
			int y1,int y2,
			int minSize,
			ArrayList<Double> trendsIndex,
			int debug
			){
		
		//ArrayList<Double> trendsIndex = new ArrayList<Double>();
		
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int index3 = 0;
		int lastDay = -1;
		int dayOpen = -1;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		int dayOrder = 0;
		int accOpen = 0;
		int countOpen = 0;
		
		int count20 = 0;
		int count40 = 0;
		int count60 = 0;
		int count80 = 0;
		int count100 = 0;
		int count120 = 0;
		int count140 = 0;
		int count160 = 0;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			//System.out.println(q.toString());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int year = cal.get(Calendar.YEAR);
			if (year<y1 || year>y2) continue;
			if (day!=lastDay){
				dayOpen = q.getOpen5();
				//System.out.println("[NEW DAY] "+DateUtils.datePrint(cal)+" | "+q.getOpen5());
				lastDay = day;
				dayOrder=0;
			}
			
			int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
			int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
			int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
			int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
			
			double actualTrendIndex = 0;
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
					
					//trendsIndex.add(actualSizeH1*1.0/minSize);
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
					
					//trendsIndex.add(-actualSizeL1*1.0/minSize);
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				}else{
					//trendsIndex.add(0.0);
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					int sizeClose = q.getClose5()-data.get(index1).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.setSizeClose(sizeClose);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					QuoteShort.getCalendar(cal3, data.get(index3));
					tsize.setMillisOpen(cal3.getTimeInMillis());
					tsize.setMode(1);
					trends.add(tsize);
					
					
					mode=-1;
					index1 = index2;
					index2 = i;
					index3 = i;//definición de trend
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
					if (dayOrder==0){
						int s = data.get(index1).getHigh5()-dayOpen;
						s = Math.abs(size);
						if (s>=1600) count160++;
						if (s>=1400) count140++;
						if (s>=1200) count120++;
						if (s>=1000) count100++;
						if (s>=800) count80++;
						if (s>=600) count60++;
						if (s>=400) count40++;
						if (s>=200) count20++;
						accOpen += data.get(index1).getHigh5()-dayOpen;
						countOpen++;
						if (debug==1)
							System.out.println("[TREND DOWN] "+DateUtils.datePrint(cal1)
								+" | "+q.toString()
								+" || "+(data.get(index1).getHigh5()-dayOpen)
								);
					}
					
					dayOrder++;
				}else if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
					
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
					//trendsIndex.add(actualSizeH1*1.0/minSize);
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
					int sizeClose = data.get(index1).getHigh5()-q.getClose5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(-size);
					tsize.setSizeClose(-sizeClose);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					QuoteShort.getCalendar(cal3, data.get(index3));
					tsize.setMillisOpen(cal3.getTimeInMillis());
					//tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					tsize.setMode(-1);
					trends.add(tsize);
					
					mode=1;
					index1 = index2;
					index2 = i;
					index3 = i;//definición de trend
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
					
					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
					if (dayOrder==0){
						int s = dayOpen-data.get(index1).getLow5();
						s = Math.abs(size);
						if (s>=1400) count160++;
						if (s>=1400) count140++;
						if (s>=1200) count120++;
						if (s>=1000) count100++;
						if (s>=800) count80++;
						if (s>=600) count60++;
						if (s>=400) count40++;
						if (s>=200) count20++;
						accOpen += (dayOpen-data.get(index1).getLow5());
						countOpen++;
						if (debug==1)
							System.out.println("[TREND UP] "+DateUtils.datePrint(cal1)
								+" | "+q.toString()
								+" || "+(dayOpen-data.get(index1).getLow5())
								);
					}
					dayOrder++;
				}else if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
					//trendsIndex.add(actualSizeL1*1.0/minSize);
				}
			}
			trendsIndex.add(actualTrendIndex);
		}

		System.out.println(
				y1+" "+y2
				+" || "
				+" "+countOpen
				+" "+PrintUtils.Print2dec(accOpen*0.1/countOpen, false)
				+" | "
				+" "+count20
				+" "+count40
				+" "+count60
				+" "+count80
				+" "+count100
				+" "+count120
				+" "+count140
				+" "+count160
				);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path0 ="C:\\fxdata\\";
		//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";
		
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2009.01.01_2018.12.27.csv";
		//String pathEURUSD = path0+"EURUSD_UTC_15 Secs_Bid_2010.12.31_2018.01.26.csv";
		
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
						
			//Tick.readFromDisk(ticks, path, 3);
			//System.out.println(ticks.size());
						
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
			TestLines.calculateCalendarAdjustedSinside(dataI);			
			dataS = TradingUtils.cleanWeekendDataS(dataI);   
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			
			
			ArrayList<TrendInfo> dataTrend = new ArrayList<TrendInfo>();
			ArrayList<Double> trendsIndex = new ArrayList<Double>();
			
			for (int y1=2009;y1<=2009;y1++){
				int y2 = y1+9;
				StudyTrendsFeb18.doTest(data,y1,y2, 200, trendsIndex,0);
			}
		}

	}

}

	