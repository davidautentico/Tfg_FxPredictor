package drosa.experimental.random;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.traderDale.TestLimits;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.TradingUtils;

public class TailTester {
	
	
	public static void doTest(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int h1,
			int thr,
			int minPips
			
			){
	
		Calendar cal = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		int lastDay = -1;
		int dmax = -1;
		int dmin = -1;
		int dayOpen = -1;
		int easyWins = 0;
		ArrayList<Integer> results = new ArrayList<Integer>(); 
		for (int i=1;i<data.size();i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			
			if (day!=lastDay && h==h1){
				
				if (dayOpen>=0){
					int diffH = dmax-dayOpen;
					int diffL = dayOpen-dmin;
					int diffC = q.getOpen5()-dayOpen;
					
					int res = 0;
					if (diffH>=10*minPips){
						if (diffL>=10*minPips){
							res = 3;//H+L
						}else{
							res = 1;//H
						}
					}else if (diffL>=10*minPips){
						res = 2;//L
					}
					results.add(res);
					
					if (results.size()==1 && res==3){
						easyWins++;
					}else if (results.size()>=1 && res==3 && res==results.get(results.size()-2)){
						easyWins++;
					}
					
					boolean isPremio = false;
					if (results.size()>=3 && (res==1 || res==2)){
						if (res==results.get(results.size()-2)
								&& res==results.get(results.size()-3) 
								){
							System.out.println(DateUtils.datePrint(cal)+" [L / H] PREMIO "
								+res
								+" || "+diffL+" / "+diffH
								+" || "+diffC
								);
							isPremio = true;
						}
					}
					
					if (!isPremio)
						System.out.println(DateUtils.datePrint(cal)+" [L / H] "
								+res
								+" || "+diffL+" / "+diffH
								+" || "+diffC
								);
				}
				
				dmax = -1;
				dmin = -1;
				dayOpen = q.getOpen5();
				lastDay = day;								
			}
			
			
			if (dmax==-1 || q.getHigh5()>=dmax){
				dmax = q.getHigh5();
			}
			if (dmin==-1 || q.getHigh5()<=dmin){
				dmin = q.getLow5();
			}
		}
		
		//streaks;
		int lastStreak = -1;
		int actualStreak = 0;
		ArrayList<Integer> streakList = new  ArrayList<Integer>();
		for (int i=0;i<=10;i++) streakList.add(0);
		for (int i=0;i<results.size();i++){
			int res = results.get(i);
			
			if (res==lastStreak){//si son iguales acumulo
				actualStreak++;
			}else{
				if (lastStreak==1 || lastStreak==2){
					if (actualStreak>=10) actualStreak=10;
					
					int count = streakList.get(actualStreak);
					streakList.set(actualStreak, count+1);
				}
				actualStreak = 1;
			}
			lastStreak = res;
		}
		
		String resStr ="";
		for (int i=1;i<=10;i++){
			resStr+=" "+String.valueOf(streakList.get(i));
		}
		System.out.println(
				h1+" "+minPips
				+" || "+results.size()
				+" || "+easyWins
				+" || "+resStr
				);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
String fileName   ="5 Mins_Bid_2003.05.04_2017.07.31.csv";
		
		String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_"+fileName;
		String pathGBPUSD = "C:\\fxdata\\GBPUSD_UTC_"+fileName;
		String pathAUDUSD = "C:\\fxdata\\AUDUSD_UTC_"+fileName;
		String pathUSDJPY = "C:\\fxdata\\USDJPY_UTC_"+fileName;
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		paths.add(pathGBPUSD);
		paths.add(pathAUDUSD);
		paths.add(pathUSDJPY);
		
		int limit = paths.size()-1;
		int initial = 0;
		limit       = 0;
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		for (int i=initial;i<=limit;i++){
			String path = paths.get(i);	
			String pairName = paths.get(i).split("\\\\")[2].substring(0, 6);
			if (path.contains("UTC")){
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX3);
			}else{
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);
			}
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  			
			ArrayList<QuoteShort> data = null;
			data = dataS;
			//System.out.println("total data: "+data.size());
			ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
			
			for (int h1=0;h1<=0;h1++){
				int h2 = h1+0;
				for (int thr=400;thr<=400;thr+=10){
					for (int lookback=100;lookback<=100;lookback++){
						for (int minPips=10;minPips<=10;minPips++){
							for (int nBars=60;nBars<=60;nBars+=12){
								TailTester.doTest(data, maxMins, h1, thr,minPips);
							}
						}
					}
				}
			}
			
		
		}
	}

}
