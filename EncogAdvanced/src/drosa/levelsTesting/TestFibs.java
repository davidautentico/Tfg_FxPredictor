package drosa.levelsTesting;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.LineType;
import drosa.phil.PhilDay;
import drosa.phil.PhilLine;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestFibs {

	
	private static ArrayList<Integer> calculateMaxMin(ArrayList<Quote> data,double value,int h1, int h2
			,boolean allHours, boolean isHigh){
		ArrayList<Integer> res = new ArrayList<Integer>();
		//res.add(0);res.add(0);
		
		int max = 0;
		int min = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			//if (h<h1) continue;
			if (h>h2) break;
			int diffH = TradingUtils.getPipsDiff(q.getHigh(),value);
			int diffL = TradingUtils.getPipsDiff(value,q.getLow());
			
			if (isHigh && diffH>=0){
				//System.out.println("entrado");
				if (h>=h1 || allHours){
					for (int j=i+1;j<data.size();j++){
						Quote qj = data.get(j);
						diffH = TradingUtils.getPipsDiff(qj.getHigh(),value);
						diffL = TradingUtils.getPipsDiff(value,qj.getLow());
						
						if (diffH>=min) min = diffH;
						if (diffL>=max) max = diffL;
					}
				}
				break;
			}
			
			if (!isHigh && diffL>=0){
				if (h>=h1 || allHours){
					//System.out.println(" low ");
					for (int j=i+1;j<data.size();j++){
						Quote qj = data.get(j);
						diffH = TradingUtils.getPipsDiff(qj.getHigh(),value);
						diffL = TradingUtils.getPipsDiff(value,qj.getLow());
						
						if (diffH>=max) max = diffH;
						if (diffL>=min) min = diffL;
					}
				}
				break;
			}
			
		}
		//System.out.println(DateUtils.datePrint(cal)+" "+max+" "+min);
		res.add(max);
		res.add(min);
		
		return res;
	}
	
	public static void testFibs(String header, ArrayList<Quote> data,ArrayList<PhilDay> philDays,
			Calendar from,Calendar to,int dayL,int dayH,int h1,int h2,LineType testLevel,boolean allHours){
	
		boolean isHigh = true;
		int total = 0;
		int totalMax = 0;
		int totalMin = 0;
		int totalAbove = 0;
		ArrayList<Integer> maxPipsArr = new ArrayList<Integer>();
		ArrayList<Integer> minPipsArr = new ArrayList<Integer>();
		for (int i=0;i<1000;i++){
			maxPipsArr.add(0);
			minPipsArr.add(0);
		}
		for (int i=0;i<=philDays.size()-1;i++){
			//System.out.println("dia "+i);
			PhilDay pDay = philDays.get(i);
			Calendar actualDate = pDay.getDay();
			int dayWeek = actualDate.get(Calendar.DAY_OF_WEEK);
			int h = actualDate.get(Calendar.HOUR_OF_DAY);
			int actualDay = actualDate.get(Calendar.DAY_OF_YEAR);
			
		
			if (dayWeek<dayL || dayWeek>dayH) continue;
			
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() || actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			//obtenemos las lineas
			ArrayList<PhilLine> lines = pDay.getLines();
			double LINE=-1;
			double DO=-1;
			
			for (int j=0;j<lines.size();j++){
				if (lines.get(j).getLineType()==testLevel)
					LINE = lines.get(j).getValue();
				if (lines.get(j).getLineType()==LineType.DO)
					DO = lines.get(j).getValue();
			}
			
			//System.out.println("LINE DO "+PrintUtils.Print4dec(LINE)+" "+PrintUtils.Print4dec(DO));
			isHigh = true;
			if (LINE<DO) isHigh = false;
			
			ArrayList<Quote> dayData = TradingUtils.getDayData(data, actualDate);
			ArrayList<Integer> maxmin = calculateMaxMin(dayData,LINE,h1,h2,allHours,isHigh);
			if (maxmin.get(0)>0 || maxmin.get(1)>0){
				total++;
				totalMax+=maxmin.get(0);
				totalMin+=maxmin.get(1);
				int maxAcc = maxPipsArr.get(maxmin.get(0));
				int minAcc = maxPipsArr.get(maxmin.get(1));
				maxPipsArr.set(maxmin.get(0), maxAcc+1);
				maxPipsArr.set(maxmin.get(1), minAcc+1);
				if (maxmin.get(0)>maxmin.get(1)) totalAbove++;
			}
			
			//System.out.println(DateUtils.datePrint(actualDate)+" "+maxmin.get(0)+" "+maxmin.get(1));
		}	
		System.out.println("d1= "+dayL+" d2= "+dayH
				+" h1="+h1+" h2="+h2
				+" LineType= "+testLevel.name()
				+" "+header
				+" total= " +total
				+" factor= "+PrintUtils.Print2(totalMax*1.0/totalMin)
				+" above(%)= "+PrintUtils.Print2(totalAbove*100.0/total)+"%"
				+" maxPips= "+getMaxPips(maxPipsArr)
				);
	}
	
	
	
	private static String getMaxPips(ArrayList<Integer> maxPipsArr) {
		// TODO Auto-generated method stub
		String str = "";
		
		int total = 0;
		int total5 = 0;
		int total10 = 0;
		int total15 = 0;
		int total20 = 0;
		int total25 = 0;
		int total30 = 0;
		int total35 = 0;
		int total40 = 0;
		for (int i=0;i<maxPipsArr.size();i++){
			total+=maxPipsArr.get(i);
			if (i>=5) total5+=maxPipsArr.get(i);
			if (i>=10) total10+=maxPipsArr.get(i);
			if (i>=15) total15+=maxPipsArr.get(i);
			if (i>=20) total20+=maxPipsArr.get(i);
			if (i>=25) total25+=maxPipsArr.get(i);
			if (i>=30) total30+=maxPipsArr.get(i);
			if (i>=35) total35+=maxPipsArr.get(i);
			if (i>=40) total40+=maxPipsArr.get(i);
		}
		double per5 = total5*100.0/total;
		double per10 = total10*100.0/total;
		double per15 = total15*100.0/total;
		double per20 = total20*100.0/total;
		double per25 = total25*100.0/total;
		double per30 = total30*100.0/total;
		double per35 = total35*100.0/total;
		double per40 = total40*100.0/total;
		str=" >=5 "+PrintUtils.Print4dec(per5)
				+" >=10 "+PrintUtils.Print4dec(per10)
				+" >=15 "+PrintUtils.Print4dec(per15)
				+" >=20 "+PrintUtils.Print4dec(per20)
				+" >=25 "+PrintUtils.Print4dec(per25)
				+" >=30 "+PrintUtils.Print4dec(per30)
				+" >=35 "+PrintUtils.Print4dec(per35)
				+" >=40 "+PrintUtils.Print4dec(per40)
				;
		return str;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.17.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.25.csv";
		//String path5m = "c:\\fxdata\\AUDUSD_UTC_5 Mins_Bid_2003.08.03_2014.07.25.csv";
		//String path5m = "c:\\fxdata\\GBPUSD_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		//String path5m = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2005.01.01_2014.07.23.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
  		ArrayList<Quote> weeklyData = ConvertLib.createWeeklyData(dailyData);
  		ArrayList<Quote> monthlyData = ConvertLib.createMonthlyData(dailyData);
  		
		LineType lineType1 	= LineType.FIBR1;
		LineType lineType2 	= LineType.FIBR2;
		LineType lineType3 	= LineType.FIBR3;
		LineType lineType4 	= LineType.FIBR4;
		LineType lineType5 	= LineType.FIBR5;
		LineType lineType6 	= LineType.FIBS1;
		LineType lineType7 	= LineType.FIBS2;
		LineType lineType8 	= LineType.FIBS3;
		LineType lineType9 	= LineType.FIBS4;
		LineType lineType10 = LineType.FIBS5;
		LineType lineType11 = LineType.YH;
		LineType lineType12 = LineType.YL;
		LineType lineType13 = LineType.WH;
		LineType lineType14 = LineType.WL;
		LineType lineType15 = LineType.DOoffset;
		ArrayList<LineType> lines = new ArrayList<LineType>();
		ArrayList<LineType> lines2 = new ArrayList<LineType>();
		lines.add(lineType1);
		lines.add(lineType2);
		lines.add(lineType3);
		lines.add(lineType4);
		lines.add(lineType5);
		lines.add(lineType6);
		lines.add(lineType7);
		lines.add(lineType8);
		lines.add(lineType9);
		lines.add(lineType10);
		lines2.add(lineType11);
		lines2.add(lineType12);
		//lines2.add(lineType13);
		//lines2.add(lineType14);
		
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		from.set(2010, 0, 1);
		to.set(2011,  11, 31);
		int dayL = Calendar.MONDAY+3;
		int dayH = Calendar.MONDAY+3;
		int h1 = 8;
		int h2 = 10;
		int period = 5;
		double atrFactor = 0.3;
		ArrayList<PhilDay> days = TradingUtils.calculateFIBS(data, dailyData, 3, 100);
		ArrayList<PhilDay> days2 = TradingUtils.calculateLines(data, dailyData, weeklyData, monthlyData);
		ArrayList<PhilDay> atrDays = TradingUtils.calculateLinesAtr(dailyData,period,atrFactor);
		
		
		h1 = 12;
		h2 = 23;
		for (int y=2012;y<=2014;y++){
			from.set(y, 0, 1);
			to.set(y,  11, 31);
			//for (period=1;period<=100;period++){
			for (atrFactor=0.3;atrFactor<=0.3;atrFactor+=0.1){
				//atrDays = TradingUtils.calculateLinesAtr(dailyData,period,atrFactor);
				int offset = 0;
				//for (int offset=20;offset<=20;offset+=5){
					ArrayList<PhilDay> doPositives = TradingUtils.calculateLinesOffset(dailyData,offset,false);
					String header = "offset= "+offset;
					for (dayL=Calendar.MONDAY+0;dayL<=Calendar.MONDAY+0;dayL++){
						dayH = dayL+2;
						//fibs
						for(int i=0;i<1;i++){
							//for (h1=0;h1<=23;h1++){
								//h2=23;
							for (h1=15;h1<=15;h1++){
							 //h2=h1+50;
								h2=h1+8;
								//dayH = dayL+2;
								TestFibs.testFibs("",data, days, from, to,dayL,dayH,h1,h2,lines.get(3),true);
								TestFibs.testFibs("",data, days, from, to,dayL,dayH,h1,h2,lines.get(8),true);
								//TestFibs.testFibs(header,data, doPositives, from, to,dayL,dayH,h1,h2,lineType15,true);
							}
						}
						
						//otros
						for(int i=0;i<lines2.size();i++){
							for (h1=0;h1<=0;h1++){
								h2=h1+11;
								//dayH = dayL+2;
								//TestFibs.testFibs("",data, days2, from, to,dayL,dayH,h1,h2,lines2.get(i),true);
							}
						}
						
						//otros
						for(int i=0;i<1;i++){
							//for (h1=0;h1<=0;h1++){
							//h2=h1+11;
								//dayH = dayL+2;
								//TestFibs.testFibs(data, atrDays, from, to,dayL,dayH,h1,h2,LineType.ATR);
								//}
						}
					}
				//}//offset
			}
		}
		
		/*
		//fibs
		for(int i=0;i<2;i++){
			for (h1=0;h1<=0;h1++){
				h2=h1+11;
				for (dayL=Calendar.MONDAY+0;dayL<=Calendar.MONDAY+0;dayL++){
					dayH = dayL+2;
					TestFibs.testFibs(data, days, from, to,dayL,dayH,h1,h2,lines.get(i));
					TestFibs.testFibs(data, days, from, to,dayL,dayH,h1,h2,lines.get(i+5));
				}
			}
		}
		
		//otros
		for(int i=0;i<lines2.size();i++){
			for (h1=0;h1<=0;h1++){
				h2=h1+11;
				for (dayL=Calendar.MONDAY+0;dayL<=Calendar.MONDAY+0;dayL++){
					dayH = dayL+2;
					TestFibs.testFibs(data, days2, from, to,dayL,dayH,h1,h2,lines2.get(i));
				}
			}
		}*/
	}
}
