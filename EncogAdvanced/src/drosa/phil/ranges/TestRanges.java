package drosa.phil.ranges;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.Currency;
import drosa.phil.IndicatorLib;
import drosa.phil.NewsItem;
import drosa.phil.PriceTestResult;
import drosa.phil.Range;
import drosa.phil.TMA;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestRanges {
	
	public static ArrayList<ExtremePoint> calculateDailyExtremes(ArrayList<Quote> data,ArrayList<Quote> dailyData){
		ArrayList<ExtremePoint> extremePoints = new ArrayList<ExtremePoint>();
		
		int lastDay = -1;
		double maxValue = 0;
		double minValue = 99999;
		int maxIndex = -1;
		int minIndex = -1;
		Calendar actualCal = Calendar.getInstance();
		Calendar actualMin = Calendar.getInstance();
		Calendar actualMax = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			actualCal.setTime(q.getDate());
			int day = actualCal.get(Calendar.DAY_OF_YEAR);
			
			
			if (day!=lastDay){
				if (lastDay!=-1){					
					int dayIndex = TradingUtils.getDayIndex(dailyData,actualMin); 		
					ExtremePoint point1 = new ExtremePoint();
					point1.setFirst(true);
					point1.setIndexDay(dayIndex);				
					
					ExtremePoint point2 = new ExtremePoint();
					point2.setFirst(false);				
					point2.setIndexDay(dayIndex);
					
					if (minIndex<maxIndex){
						point1.setValue(minValue);
						point1.setIndexData(minIndex);
						point1.setMaximum(false);
						point1.getCal().setTimeInMillis(actualMin.getTimeInMillis());
						
						point2.setValue(maxValue);
						point2.setIndexData(maxIndex);
						point2.setMaximum(true);
						point2.getCal().setTimeInMillis(actualMax.getTimeInMillis());
						
						extremePoints.add(point1);
						extremePoints.add(point2);
					}else if (minIndex>maxIndex){
						point2.setValue(minValue);
						point2.setIndexData(minIndex);
						point2.setMaximum(false);
						point2.getCal().setTimeInMillis(actualMin.getTimeInMillis());
						
						point1.setValue(maxValue);
						point1.setIndexData(maxIndex);
						point1.setMaximum(true);
						point1.getCal().setTimeInMillis(actualMax.getTimeInMillis());
						
						extremePoints.add(point1);
						extremePoints.add(point2);
					}
				}
				minValue = 99999;
				maxValue = 0;
				minIndex = -1;
				maxIndex = -1;
				lastDay = day;
			}//end lastDay
			
			if (q.getHigh()>maxValue){
				maxValue = q.getHigh();
				maxIndex = i;
				actualMax.setTimeInMillis(actualCal.getTimeInMillis());
				//System.out.println("maximo: "+DateUtils.datePrint(actualMax));
			}
			if (q.getLow()<minValue){
				minValue = q.getLow();
				minIndex = i;
				actualMin.setTimeInMillis(actualCal.getTimeInMillis());
			}
		}
		
		return extremePoints;
	}
	
	private static Quote getExtremes(ArrayList<Quote> data, int begin, int end,double value,boolean high,boolean canOver) {
		// TODO Auto-generated method stub
		Quote maxmin = new Quote();
		
		double max = 0;
		double min =9999;
		if (end>=data.size()-1) end = data.size()-1;
		for (int i=begin;i<=end;i++){
			Quote q = data.get(i);
			//System.out.println(DateUtils.datePrint(q.getDate())+" "+PrintUtils.getOHLC(q));
			
			if (!canOver){
				if (high && q.getHigh()>value) break; //no puede superar el maximo anterior
				if (!high && q.getLow()<value) break; //no puede superar el minimo anterior
			}
			
			if (q.getHigh()>max) max = q.getHigh();
			if (q.getLow()<min){
				min = q.getLow();
				//System.out.println("min reached: "+PrintUtils.Print4dec(min));
			}
		}
		
		maxmin.setHigh(max);
		maxmin.setLow(min);
		//System.out.println("max: "+PrintUtils.Print4dec(max)+" +min: "+PrintUtils.Print4dec(min));
		return maxmin;
	}
	
	public static ArrayList<Overlap> calculateOverlap(String header, ArrayList<Quote> data,ArrayList<Quote> dailyData,ArrayList<ExtremePoint> extremes,
			Calendar from,
			Calendar to,
			int day1,
			int day2,
			int h1,
			int h2,
			int barsOffset,int mode){
		
		ArrayList<Overlap> overlaps = new ArrayList<Overlap>();
		Calendar actualCal = Calendar.getInstance();
		for (int i=0;i<extremes.size();i++){
			ExtremePoint e = extremes.get(i);
			
			actualCal.setTimeInMillis(e.getCal().getTimeInMillis());
			int dayWeek = actualCal.get(Calendar.DAY_OF_WEEK);
			int h = actualCal.get(Calendar.HOUR_OF_DAY);
			if (h<h1 ||  h>h2) continue;
			if (actualCal.getTimeInMillis()<from.getTimeInMillis()) continue;
			if (actualCal.getTimeInMillis()>to.getTimeInMillis()) break;
			if (dayWeek<day1 || dayWeek>day2) continue;
			
			if ((mode==1 || mode==2) && e.isMaximum() && !e.isFirst()){
				int indexData      = e.getIndexData();
				int indexDay       = e.getIndexDay();
				Quote dayQuote     = dailyData.get(indexDay); 
				int begin = indexData;
				int end = begin+barsOffset;
				Quote maxMin = getExtremes(data,begin,end,dayQuote.getHigh(),true,true);
				maxMin.setHigh(dayQuote.getHigh());
				//System.out.println("UP "+DateUtils.datePrint(dayQuote.getDate())+" "+PrintUtils.getOHLC(dayQuote));
				double percent = calculatePerOverlap(dayQuote,maxMin,true);
				if (percent<=0){
					percent = 0;
				}
				Overlap o = new Overlap();
				o.getCal().setTimeInMillis(actualCal.getTimeInMillis());
				o.setValue(percent);
				overlaps.add(o);
					/*System.out.println(e.toString()
							+" high low= "+PrintUtils.Print4dec(dayQuote.getHigh())+" "+PrintUtils.Print4dec(dayQuote.getLow())
							+" maxMin= "+PrintUtils.Print4dec(maxMin.getHigh())+" "+PrintUtils.Print4dec(maxMin.getLow())
							+" per= "+PrintUtils.Print2dec(percent,false)
							);*/
				
			}
			if ((mode==0 || mode==2) && !e.isMaximum() && !e.isFirst()){
				int indexData      = e.getIndexData();
				int indexDay       = e.getIndexDay();
				Quote dayQuote     = dailyData.get(indexDay); 
				int begin = indexData;
				int end = begin+barsOffset;
				Quote maxMin = getExtremes(data,begin,end,dayQuote.getLow(),false,true);
				maxMin.setLow(dayQuote.getLow());
				//System.out.println("DOWN "+DateUtils.datePrint(dayQuote.getDate())+" "+PrintUtils.getOHLC(dayQuote));
				double percent = calculatePerOverlap(dayQuote,maxMin,false);
				if (percent<=0){
					percent = 0;
				}
				Overlap o = new Overlap();
				o.getCal().setTimeInMillis(actualCal.getTimeInMillis());
				o.setValue(percent);
				overlaps.add(o);
				/*System.out.println(e.toString()
							+" high low= "+PrintUtils.Print4dec(dayQuote.getHigh())+" "+PrintUtils.Print4dec(dayQuote.getLow())
							+" maxMin= "+PrintUtils.Print4dec(maxMin.getHigh())+" "+PrintUtils.Print4dec(maxMin.getLow())
							+" per= "+PrintUtils.Print2dec(percent,false)
							);*/
				
			}
		}
		//MathUtils.summary2(header+" offset= "+barsOffset, overlaps);
		return overlaps;
	}
	
	

	private static double calculatePerOverlap(Quote dayQuote, Quote maxMin,boolean max) {
		// TODO Auto-generated method stub
		int yRange = TradingUtils.getPipsDiff(dayQuote.getHigh(), dayQuote.getLow());
		int tRange = 0;
		
		if (max){
			double min = dayQuote.getLow();
			if (maxMin.getLow()>min) min = maxMin.getLow();		
			tRange = TradingUtils.getPipsDiff(dayQuote.getHigh(), min);			
		}
		else{
			double max2 = dayQuote.getHigh();
			if (maxMin.getHigh()<max2) max2 = maxMin.getHigh();		
			tRange = TradingUtils.getPipsDiff(max2,dayQuote.getLow());	
		}
		
		return tRange*100.0/yRange;
	}

	public static double testBuySellRangeDOoffset(String header,ArrayList<Quote> data,ArrayList<Quote> dailyData,
			ArrayList<Double> dailyAtr, 
			ArrayList<TMA> tma5m,
			ArrayList<NewsItem> news,
			ArrayList<String> blockList,
			Calendar from,Calendar to,
			int day1,int day2,
			int h1,int h2,
			int tmaLimit,
			double offset1, 
			double offset2,
			double stopLoss, int takeProfit, boolean upMode){
		
		int wins  =0;
		int losses=0;
		int lastDay = -1;
		int totalLostPips=0;
		Calendar cal = Calendar.getInstance();
		ArrayList<Calendar> failDays = new ArrayList<Calendar>(); 
		ArrayList<Currency> currs = new ArrayList<Currency>();
		currs.add(Currency.EUR);
		currs.add(Currency.USD);
		ArrayList<NewsItem> todayNews = null;
		double entryValue1 = -1;
		double entryValue2 = -1;
		double DO= -1;
		int actualStopLoss=0;
		int totalDays=0;
		int dayIndex = 0;
		int lastDayIndex = -1;
		boolean highNews = false;
		double max = 0;
		double min=9999;
		boolean maxTouched = false;
		boolean minTouched = false;
		for (int i=1;i<data.size();i++){
			Quote q1 = data.get(i-1);
			Quote q = data.get(i);
			TMA tma_1 = tma5m.get(i-1);
			cal.setTime(q1.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue;
			if (cal.getTimeInMillis()>to.getTimeInMillis()) break;
			if (h<h1 || h>h2) continue;
			
			if (dayWeek<day1 || dayWeek>day2) continue;
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				max=0;
				min=99999;
				maxTouched = false;
				minTouched = false;
				entryValue1=-1;
				entryValue2=-1;
				highNews = false;
				dayIndex = TradingUtils.getDayIndex(dailyData, cal);
				//todayNews = NewsItem.findAllNewsItem(news, currs, cal);
				//highNews = NewsItem.existsTypeNew(todayNews, "Non-Farm Employment Change");
				//highNews = NewsItem.existsTypeNew(todayNews, blockList);
				if (dayIndex>=0){
					Quote dayQuote = dailyData.get(dayIndex);
					double atr = dailyAtr.get(dayIndex);
					actualStopLoss = (int) (atr*stopLoss);
					//System.out.println("actualstoploss: "+actualStopLoss);
					DO = dayQuote.getOpen();
					//int range = TradingUtils.getPipsDiff(dayQuote.getHigh(), dayQuote.getLow());
					if (upMode){
						if (offset1<1.0){
							entryValue1 = DO+0.0001*atr*offset1;
							entryValue2 = DO+0.0001*atr*offset2;
						}
						else{
							entryValue1 = DO+0.0001*offset1;
							entryValue1 = DO+0.0001*offset2;
						}
					}else{
						if (offset1<1.0){
							entryValue1 = DO-0.0001*atr*offset1;
							entryValue2 = DO-0.0001*atr*offset2;
						}
						else{
							entryValue1 = DO-0.0001*offset1;
							entryValue2 = DO-0.0001*offset2;
						}
					}
					
					/*System.out.println("low high pipsDiff entry "+PrintUtils.Print(dayQuote.getHigh())
							+" "+PrintUtils.Print(dayQuote.getLow())+" "+pipsDiff
							+" "+PrintUtils.Print(entryValue)
							);*/
				}
				lastDay = day;
			}
			
			//System.out.println("ver highs");
			//analisis highs-lows
			if (q1.getHigh()>max){
				max= q1.getHigh();
				maxTouched=true;
				//System.out.println("maxTouched: "+PrintUtils.Print4dec(q1.getHigh()));
			}else{
				//System.out.println("NO maxTouched:  "+PrintUtils.getOHLC(q1));
				maxTouched=false;
			}
			if (q1.getLow()<min){
				min= q1.getLow();
				minTouched=true;
			}else minTouched=false;
			//
			
			if (entryValue1!=-1){
				//if (highNews) continue;
				if (upMode && (q1.getHigh()>=entryValue1 && q1.getHigh()<=entryValue2)){
					int tma5diff = TradingUtils.getPipsDiff(tma_1.getUpper(),q1.getHigh());
					//if (tma5diff<=tmaLimit) continue;
					//if (q1.getHigh()<tma_1.getUpper()) continue;
					
					//System.out.println("llegado");
					//if (maxTouched) continue;
					
					//System.out.println("entrado");
					double beginValue = q.getOpen();
					double stop   = beginValue+0.0001*actualStopLoss;
					double profit = beginValue-0.0001*takeProfit;
					PriceTestResult res = TradingUtils.testPriceMovement(data, i, data.size()-1, beginValue, stop, profit, 0);
					/*System.out.println(DateUtils.datePrint(cal)
							+" "+PrintUtils.Print4dec(beginValue)
							+" "+PrintUtils.Print4dec(stop)
							+" "+PrintUtils.Print4dec(takeProfit)
							);*/
					if (dayIndex!=lastDayIndex){
						totalDays++;
						lastDayIndex=dayIndex;
					}
					if (res.isWin()) wins++;
					else{
						losses++;
						totalLostPips+=actualStopLoss;
						if (!alreadyFailDay(failDays,cal)){
							Calendar c = Calendar.getInstance();
							c.setTimeInMillis(cal.getTimeInMillis());
							failDays.add(c);
							//System.out.println("fail: "+DateUtils.datePrint(c));
						}
					}
					//System.out.println("total: "+(wins+losses));
				}
				
				if (!upMode && (q1.getLow()<=entryValue1 && q1.getLow()>=entryValue2) ){
					//System.out.println("entrado");
					
					//if (!minTouched) continue;
					
					double beginValue = q.getOpen();
					double stop   = beginValue-0.0001*actualStopLoss;
					double profit = beginValue+0.0001*takeProfit;
					PriceTestResult res = TradingUtils.testPriceMovement(data, i, data.size()-1, beginValue, stop, profit, 1);
					/*System.out.println(DateUtils.datePrint(cal)
							+" "+PrintUtils.Print4dec(beginValue)
							+" "+PrintUtils.Print4dec(stop)
							+" "+PrintUtils.Print4dec(takeProfit)
							);*/
					if (dayIndex!=lastDayIndex){
						totalDays++;
						lastDayIndex=dayIndex;
					}
					if (res.isWin()) wins++;
					else{
						losses++;
						totalLostPips+=actualStopLoss;
						if (!alreadyFailDay(failDays,cal)){
							Calendar c = Calendar.getInstance();
							c.setTimeInMillis(cal.getTimeInMillis());
							failDays.add(c);
						}
					}
				}//low
			}//if entry
			
			
		}//for
		int total = wins+losses;
		double winPer  = wins*100.0/total;
		double lossPer = 100.0-winPer;
		double me = (wins*takeProfit-totalLostPips)*1.0/total;
		System.out.println(header
				+" total= "+total
				+" losses= "+losses
				+" totalLossDays = "+failDays.size()+"/"+totalDays+" ("+PrintUtils.Print2dec(failDays.size()*100.0/totalDays,false)+")"
				+" wins %: "+PrintUtils.Print2dec(wins*100.0/total,false)
				+" me= "+PrintUtils.Print2dec(me,false)
				+" profit= "+PrintUtils.Print2dec(me*total,false)
				);
		return me;
	}
	
	
	public static void testBuySellRange(String header,ArrayList<Quote> data,ArrayList<Quote> dailyData,Calendar from,Calendar to,
			double entryPer,int stopLoss,int takeProfit,int aboveDO, boolean upMode){
		
		int wins  =0;
		int losses=0;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		double entryValue = -1;
		double DOvalue = -1;
		ArrayList<Calendar> failDays = new ArrayList<Calendar>();
		for (int i=1;i<data.size();i++){
			Quote q1 = data.get(i-1);
			Quote q = data.get(i);
			cal.setTime(q1.getDate());
			if (cal.getTimeInMillis()<from.getTimeInMillis()) continue;
			if (cal.getTimeInMillis()>to.getTimeInMillis()) break;
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				entryValue=-1;
				int index = TradingUtils.getDayIndex(dailyData, cal);
				if (index>=0){
					Quote dayQuote = dailyData.get(index);
					int range = TradingUtils.getPipsDiff(dayQuote.getHigh(), dayQuote.getLow());
					double pipsDiff = range*entryPer;
					if (upMode)
						entryValue = dayQuote.getHigh()-0.0001*pipsDiff;
					else
						entryValue = dayQuote.getLow()+0.0001*pipsDiff;
					DOvalue = dayQuote.getOpen();
					/*System.out.println("low high pipsDiff entry "+PrintUtils.Print(dayQuote.getHigh())
							+" "+PrintUtils.Print(dayQuote.getLow())+" "+pipsDiff
							+" "+PrintUtils.Print(entryValue)
							);*/
				}
				lastDay = day;
			}
			
			if (entryValue!=-1){
				int DOCond = 0;
				if (upMode && aboveDO==1 && q1.getHigh()<DOvalue){//?¿ empeora?
					DOCond = -1;
				}
				if (upMode && q1.getHigh()>=entryValue && DOCond>=0){
					
					//System.out.println("entrado");
					double beginValue = q.getOpen();
					double stop   = beginValue+0.0001*stopLoss;
					double profit = beginValue-0.0001*takeProfit;
					PriceTestResult res = TradingUtils.testPriceMovement(data, i, data.size()-1, beginValue, stop, profit, 0);
					
					if (res.isWin()) wins++;
					else{
						losses++;
						if (!alreadyFailDay(failDays,cal)){
							
						}
					}
				}
			}
		}
		int total = wins+losses;
		double winPer  = wins*100.0/total;
		double lossPer = 100.0-winPer;
		double me = (winPer * takeProfit-lossPer*stopLoss)/100.0;
		System.out.println(header
				+" wins %: "+PrintUtils.Print2dec(wins*100.0/total,false)
				+" me= "+PrintUtils.Print2dec(me,false)
				);
	}
	
	
	private static boolean alreadyFailDay(ArrayList<Calendar> failDays,
			Calendar cal) {
		// TODO Auto-generated method stub
		
		Calendar actual = Calendar.getInstance();
		for (int i=0;i<failDays.size();i++){
			actual.setTimeInMillis(failDays.get(i).getTimeInMillis());
			if (DateUtils.isSameDay(actual, cal))
				return true;
		}
		return false;
	}


	public static void main(String[] args) {
		String path   = "c:\\fxdata";
		String file1H = path+"\\"+"EURUSD_Hourly_Bid_2003.05.04_2014.02.07.csv";
		String file5m = path+"\\"+"EURUSD_5 Mins_Bid_2003.05.04_2014.01.07.csv";
				
		ArrayList<Quote> dataI 		 = DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		 = TestLines.calculateCalendarAdjusted(dataI);
		ArrayList<Quote> data5m 	 = TradingUtils.cleanWeekendData(dataS);
		ArrayList<Quote> dailyData   = ConvertLib.createDailyData(data5m);
		ArrayList<String> blockList  = new ArrayList<String>(); 
		
		double bandFactor = 2.9;
		int atrPeriod = 100;
		int halfLength = 56;
		double entryPer = 0.5;
		double stopLoss    = 300;
		int takeProfit  = 4;
		boolean upMode = false;
		int aboveDO = 1;		
		double offset1=0;
		double offset2=0;
		int day1 = Calendar.MONDAY+0;
		int day2 = Calendar.MONDAY+2;
		int h1 = 0;
		int h2 = 23;		
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		
		
		ArrayList<TMA> tma5m = IndicatorLib.calculateTMA_Array(data5m, 0,data5m.size()-1,bandFactor,halfLength,atrPeriod);
		ArrayList<Double> dailyAtr = MathUtils.calculateAtr(dailyData,atrPeriod);
		ArrayList<NewsItem>	news  = DAO.loadNews(path+"\\news.txt");
		System.out.println("data5m atr tma news : "+data5m.size()+" "+dailyAtr.size()+" "+tma5m.size()+" "+news.size());
		System.out.println("upMode: "+upMode);
		blockList.add("Fed Chairman");
		blockList.add("ECB");
		blockList.add("Non-Farm Employment Change");
		
		//for (int year=2005;year<=2013;year++){
			//from.set(year, 0, 1);
			//to.set(year, 11, 31);	
			//for (stopLoss=0.1;stopLoss<=3.0;stopLoss+=0.1){
			for (offset1=0.1;offset1<=0.9;offset1+=0.1){
				offset2=999999;
				for (stopLoss=1.1;stopLoss<=1.1;stopLoss+=0.1){
				//for (offset=0.8;offset<=0.8;offset+=0.1){
					double accMe=0;
					int total=0;
					for (takeProfit=2;takeProfit<=30;takeProfit+=5){
						for (int year=2013;year<=2013;year++){
							from.set(year, 0, 1);
							to.set(year, 11, 31);	
							for (int tmaLimit=20;tmaLimit<=20;tmaLimit++){
								String header = "yr= "+year
										+" off1= "+PrintUtils.Print2dec(offset1,false)
										+" off2= "+PrintUtils.Print2dec(offset2,false)
										+" SL= "+PrintUtils.Print2dec(stopLoss,false)
										+" TP= "+takeProfit;			
								double me = TestRanges.testBuySellRangeDOoffset(header,data5m, dailyData,dailyAtr, tma5m,news,blockList, from, to,day1,day2,h1,h2,tmaLimit, 
										offset1,offset2, stopLoss, takeProfit, upMode);
								accMe+=me;
								total++;
							}
						}
						/*System.out.println("offset= "+PrintUtils.Print2dec(offset1,false)
								+" stop= "+PrintUtils.Print2dec(stopLoss,false)
								+" "+PrintUtils.Print2dec(accMe*1.0/total,false)
								);*/
					}
				}
			//}
		}
	}
}
