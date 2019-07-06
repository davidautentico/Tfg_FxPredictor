package drosa.levelsTesting;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.DataCleaning;
import drosa.phil.PriceTestResult;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestDailyPoints {
	
	
	public static void testLowsHighs(ArrayList<Quote> data,ArrayList<Quote> dailyData,
			Calendar from,Calendar to,int dayL,int dayH,int h1,int h2,
			int breachingPips,int sl,int tp,int lag, CandlePoint candlePoint){
	
		int wins = 0;
		int losses = 0;
		double valueTest = -1;
		double DO = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=lag;i<dailyData.size();i++){
			Quote day1 = dailyData.get(i);
			Quote day0 = dailyData.get(i-lag);
			
			cal.setTime(day1.getDate());
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue ;
			if (cal.getTimeInMillis()>to.getTimeInMillis()){				
				break;
			}			
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			
			if (candlePoint == CandlePoint.OPEN)
				valueTest = day0.getOpen();
			if (candlePoint == CandlePoint.CLOSE)
				valueTest = day0.getClose();
			if (candlePoint == CandlePoint.HIGH)
				valueTest = day0.getHigh();
			if (candlePoint == CandlePoint.LOW)
				valueTest = day0.getLow();
			
			DO = day1.getOpen();
			
			boolean high = true;
			if (candlePoint.name().contains("HIGH")){
				if (DO>=valueTest) continue; 
			}
			if (candlePoint.name().contains("LOW")){
				if (DO<=valueTest) continue; 
				high = false;
			}
			
			System.out.println(PrintUtils.Print(day0));
			
			int index = TradingUtils.getDayIndex(data,cal);
			ArrayList<Quote> dayData = TradingUtils.getDayData(data, cal);
			
			Calendar c1 = Calendar.getInstance();
			for (int j=0;j<dayData.size();j++){
				Quote q = dayData.get(j);
				c1.setTime(q.getDate());
				int hj = c1.get(Calendar.HOUR_OF_DAY);
				
				if (hj>h2) break;
				
				int pipsDiff = -9999;
				double beginValue = -9999;
				double slValue = -9999;
				double tpValue = -9999;
				int mode = -1;
				if (high){
					pipsDiff = TradingUtils.getPipsDiff(q.getHigh(),valueTest);
					beginValue = valueTest+breachingPips*0.0001;
					tpValue = beginValue-tp*0.0001;
					slValue = beginValue+sl*0.0001;
					mode = 0;
					
				}else{
					pipsDiff = TradingUtils.getPipsDiff(valueTest,q.getLow());
					beginValue = valueTest-breachingPips*0.0001;
					tpValue = beginValue+tp*0.0001;
					slValue = beginValue-sl*0.0001;
					mode = 1;
				}
				
				if (pipsDiff>=breachingPips){
					System.out.println("valueTest beginValue slLoss tp= "
							+" "+PrintUtils.Print(valueTest)
							+" "+PrintUtils.Print(beginValue)
							+" "+PrintUtils.Print(slValue) +" "+PrintUtils.Print(tpValue)+" "+pipsDiff);
					System.out.println("day0 day1: "							
							+" "+PrintUtils.Print(day0)+" "+PrintUtils.Print(day1)
							);
					int end = data.size()-1;
					int begin = index+j+1;
					PriceTestResult res = TradingUtils.testPriceMovement(data, begin, end,beginValue,slValue,tpValue,mode);
					if (hj>=h1 && hj<=h2){ //entra en las stats
						double winPer = wins*100.0/(wins+losses);
						if (res.isWin())
							wins++;
						else losses++;
						System.out.println(res.isWin() +" "+PrintUtils.Print2(winPer));
					}
					break;//sólo el primero
				}				
			}
		}
		double winPer = wins*100.0/(wins+losses);
		double exp = (winPer*1.0*tp-(100.0-winPer)*1.0*sl)/100.0;
		System.out.println("level= "+candlePoint.name()
				+" lag = "+lag
				+" breachingPips= "+breachingPips
				+" total= "+(wins+losses)
				+" wins%= "+PrintUtils.Print2(winPer)+"%"
				+" exp= "+PrintUtils.Print2(exp));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.17.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		//String path5m = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2005.01.01_2014.07.23.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
  		
  		//DataCleaning.writeFile("c:\\fxdata\\EURUSD_PEPPER_UTC_5 Mins_Bid_2003.05.04_2014.07.17.csv",data);
  		
  		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		from.set(2012, 0, 1);
		to.set(2012,  11, 31);
		int dayL = Calendar.MONDAY+1;
		int dayH = Calendar.MONDAY+1;
		
  		int breachingPips = 0;
  		int sl = 10;
  		int tp = 30;
  		int h1 =  0;
  		int h2 =  7;
  		int lag = 1;
  		CandlePoint candlePoint2 = CandlePoint.LOW;
  		CandlePoint candlePoint1 = CandlePoint.HIGH;
  		
  		for (breachingPips=0;breachingPips<=0;breachingPips++)
  		for (lag=1;lag<=1;lag++)
  		for (dayL=Calendar.MONDAY;dayL<=Calendar.MONDAY+0;dayL++){
  			dayH = dayL+2;
  			TestDailyPoints.testLowsHighs(data, dailyData,from,to,dayL,dayH,h1,h2, breachingPips, sl, tp, lag, candlePoint1);
  			//TestDailyPoints.testLowsHighs(data, dailyData,from,to,dayL,dayH,h1,h2, breachingPips, sl, tp, lag, candlePoint2);
  		}
  		
  		
	}

}
