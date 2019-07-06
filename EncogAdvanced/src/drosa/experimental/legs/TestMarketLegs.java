package drosa.experimental.legs;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.zznbrum.TestTrends;
import drosa.experimental.zznbrum.TrendClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMarketLegs {
	
	
public static ArrayList<TrendClass> getTrends(ArrayList<QuoteShort> data,
		int y1,int y2,
		int minSize){
		
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int lastDay = -1;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (y<y1 | y>y2) continue;
			
			if (day!=lastDay){
				
				lastDay = day;
			}
			
			int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
			int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
			int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
			int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=-1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
				}else if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
				}else if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
				}
			}
		}
		
		return trends;
		
	}

public static ArrayList<TrendClass> getTrends2(ArrayList<QuoteShort> data,
		int y1,int y2,
		int minSize
		
		){
		
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int lastDay = -1;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		int totalDays = 0;
		int legsDay = 0;
		ArrayList<Integer> legsDayArr = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					legsDayArr.add(legsDay);
					legsDay=0;
				}
				
				lastDay = day;
			}
			
			int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
			int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
			int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
			int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=-1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					legsDay++;
				}else if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=1;
					index1 = index2;
					index2 = i;
					
					legsDay++;
					QuoteShort.getCalendar(cal1, data.get(index1));
				}else if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
				}
			}
		}
		
		double avgSize = 0;
		int down2 = 0;
		int down3 = 0;
		int down4 = 0;
		int down5 = 0;
		int down6 = 0;
		int down7 = 0;
		int down8 = 0;
		int down9 = 0;
		int down10 = 0;
		int down2d = 0;
		int down3d = 0;
		int down4d = 0;
		int down5d = 0;
		int down6d = 0;
		int down7d = 0;
		int down8d = 0;
		int down9d = 0;
		int down10d = 0;
		int lastDay2 = -1;
		int lastDay3 = -1; 
		int lastDay4 = -1; 
		int lastDay5 = -1; 
		int lastDay6 = -1; 
		int lastDay7 = -1; 
		int lastDay8 = -1; 
		int lastDay9 = -1; 
		int lastDay10 = -1; 
		for (int i=0;i<trends.size();i++){
			avgSize += trends.get(i).getSize();
			int day = trends.get(i).getCal().get(Calendar.DAY_OF_YEAR);
			if (trends.get(i).getSize()<=2*minSize){				
				down2++;				
			}else{
				if (day!=lastDay2){
					lastDay2 = day;
					down2d++;
				}
			}
			
			if (trends.get(i).getSize()<=3*minSize){
				down3++;				
			}else{
				if (day!=lastDay3){
					lastDay3 = day;
					down3d++;
				}
			}
			
			if (trends.get(i).getSize()<=4*minSize){
				down4++;				
			}else{
				if (day!=lastDay4){
					lastDay4 = day;
					down4d++;
				}
			}
			
			if (trends.get(i).getSize()<=5*minSize){
				down5++;				
			}
			else{
				if (day!=lastDay5){
					lastDay5 = day;
					down5d++;
				}
			}
			if (trends.get(i).getSize()<=6*minSize){
				down6++;				
			}else{
				if (day!=lastDay6){
					lastDay6 = day;
					down6d++;
				}
			}
			
			if (trends.get(i).getSize()<=7*minSize){
				down7++;				
			}else{
				//System.out.println(trends.get(i).toString());
				if (day!=lastDay7){
					lastDay7 = day;
					down7d++;					
				}
			}
			
			if (trends.get(i).getSize()<=8*minSize){
				down8++;				
			}else{
				//System.out.println(trends.get(i).toString());
				if (day!=lastDay8){
					lastDay8 = day;
					down8d++;					
				}
			}
			
			if (trends.get(i).getSize()<=9*minSize){
				down9++;				
			}else{
				//System.out.println(trends.get(i).toString());
				if (day!=lastDay9){
					lastDay9 = day;
					down9d++;					
				}
			}
			
			if (trends.get(i).getSize()<=10*minSize){
				down10++;				
			}else{
				//System.out.println(trends.get(i).toString());
				if (day!=lastDay10){
					lastDay10 = day;
					down10d++;					
				}
			}
			
		}
		
		avgSize /=trends.size();
		
		double avg = trends.size()*1.0 /legsDayArr.size();
		String msg =y1+" "+y2
				+" "+minSize
				+" || "				
				+" "+trends.size()
				+" "+legsDayArr.size()
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(avgSize/minSize, false)
				+" || "
				+PrintUtils.Print2dec(down2*100.0/trends.size(), false)
				+" [ "+down2d+" ]"
				+" "+PrintUtils.Print2dec(down3*100.0/trends.size(), false)
				+" [ "+down3d+" ]"
				+" "+PrintUtils.Print2dec(down4*100.0/trends.size(), false)
				+" [ "+down4d+" ]"
				+" "+PrintUtils.Print2dec(down5*100.0/trends.size(), false)
				+" [ "+down5d+" ]"
				+" "+PrintUtils.Print2dec(down6*100.0/trends.size(), false)
				+" [ "+down6d+" ]"
				+" "+PrintUtils.Print2dec(down7*100.0/trends.size(), false)
				+" [ "+down7d+" ]"
				+" "+PrintUtils.Print2dec(down8*100.0/trends.size(), false)
				+" [ "+down8d+" ]"
				+" "+PrintUtils.Print2dec(down9*100.0/trends.size(), false)
				+" [ "+down9d+" ]"
				+" "+PrintUtils.Print2dec(down10*100.0/trends.size(), false)
				+" [ "+down10d+" ]"
				+" || "
				; 
		MathUtils.summary(msg, legsDayArr);
		/*System.out.println(
				""
				+" "+y1+" "+y2
				+" "+minSize
				+" || "				
				+" "+trends.size()
				+" "+legsDayArr.size()
				+" "+PrintUtils.Print2dec(avg, false)
				
				);*/
		
		return trends;
		
	}
	
	
	public void test(ArrayList<QuoteShort> data,
			int y1,int y2,int m1,int m2,
			int atrBars,double coeff){
		
		
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int totalPips = 0;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int mode = 0;
		int lastDay = -1;
		int dailyPips = 0;
		int dayTrades = 0;
		int actualPOI = 0;
		int countd=0;
		int countdb=0;
		boolean isHigh = false;
		boolean isLow = false;
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q1);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (y<y1 || y>y2) continue;
			if (m<m1 || m>m2) continue;
			
			if (day!=lastDay){	
				
				lastDay = day;
			}
			
			
			
		}
	}
	
	
	private static void studyTrends(String header, ArrayList<QuoteShort> data,int y1,int y2, int sizeTrend) {
		
		//obtenog todas las legs de sizetrend o mas
		ArrayList<TrendClass> trends = TestMarketLegs.getTrends(data, y1, y2,  sizeTrend);
		
		int totalLegs = 0;
		int totalDays = 0;
		ArrayList<Integer> legsDayArr = new ArrayList<Integer>();
		int lastDay = -1;
		int count = 0;
		for (int i=0;i<trends.size();i++){
			TrendClass t = trends.get(i);
			Calendar c = t.getCal();
			int day = c.get(Calendar.DAY_OF_YEAR);
			
			
			if (day!=lastDay){				
				if (lastDay!=-1){
					legsDayArr.add(count);	
					totalDays++;
				}
				count = 0;
				lastDay = day;
			}
			
			int tsize = t.getSize();
			count++;			
		}
		
		double avg = trends.size()*1.0 /totalDays;
		System.out.println(
				header
				+" "+y1+" "+y2
				+" || "
				+" "+trends.size()
				+" "+totalDays
				+" "+PrintUtils.Print2dec(avg, false)
				
				);
		
	}

	public static void main(String[] args) throws Exception {
		String pathEUR ="C:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2017.08.29.csv";
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEUR);
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
			if (path.contains("UTC")){
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);
			}else{
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);
			}
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			int sizeTrend = 400;
			
			for (sizeTrend=100;sizeTrend<=2000;sizeTrend+=100){
				for (double maxFactor1=1.0;maxFactor1<=1.0;maxFactor1+=1.0){
					double maxFactor2 = maxFactor1+0.99;
					//TestMarketLegs.studyTrends("", data,2003,2017, sizeTrend);
					//obtenog todas las legs de sizetrend o mas
					for (int y1=2013;y1<=2013;y1++){
						int y2 = y1+4;
						ArrayList<TrendClass> trends = TestMarketLegs.getTrends2(data,y1,y2, sizeTrend);
					}
				}				
			}	
			
		}

	}


	

}
