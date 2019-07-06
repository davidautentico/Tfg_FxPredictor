package drosa.phil.levels;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.LineType;
import drosa.phil.PhilDay;
import drosa.phil.PriceTestResult;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestMaxPositiveInterLevel {

	public static void addPriceResults(ArrayList<PriceTestResult> allResults,ArrayList<PriceTestResult> res){
		for (int i=0;i<res.size();i++){
			PriceTestResult r = new PriceTestResult();
			r.copy(res.get(i));
			allResults.add(r);
		}
	}
	
	public static void printTestResults(String header, ArrayList<PriceTestResult> all){
		int wins=0;
		int losses=0;
		int count5=0;
		int count10=0;
		int count15=0;
		int count20=0;
		int count25=0;
		int count30=0;
		double pipsDiffAcc=0;
		for (int i=0;i<all.size();i++){
			PriceTestResult r = all.get(i);
			if (r.isWin()) wins++;
			else losses++;
			if (r.getMaxPositive()>=5) count5++;
			if (r.getMaxPositive()>=10) count10++;
			if (r.getMaxPositive()>=15) count15++;
			if (r.getMaxPositive()>=20) count20++;
			if (r.getMaxPositive()>=25) count25++;
			if (r.getMaxPositive()>=30) count30++;
			pipsDiffAcc+=r.getLastDiff();
		}
		int total = all.size();
		double lossPer = 100.0-wins*100.0/(wins+losses);
		double me = pipsDiffAcc*1.0/total;
		
		double win5per  = count5*100.0/(total);
		double loss5per = 100.0-win5per;
		double avgDiff  = pipsDiffAcc*1.0/ total;
		double me5 = win5per*5-lossPer*avgDiff;
		
		double win10per  = count10*100.0/(total);
		double loss10per = 100.0-win10per;
		double avgDiff10  = pipsDiffAcc*1.0/ total;
		double me10 = win10per*10-lossPer*avgDiff10;
		
		double win15per  = count15*100.0/(total);
		double loss15per = 100.0-win15per;
		double avgDiff15  = pipsDiffAcc*1.0/ total;
		double me15 = win15per*15-lossPer*avgDiff15;
		
		double win30per  = count30*100.0/(total);
		double loss30per = 100.0-win30per;
		double avgDiff30  = pipsDiffAcc*1.0/ total;
		double me30 = win30per*30-lossPer*avgDiff30;
		System.out.println(header+" total = "+total
				+" win%= "+PrintUtils.Print2dec(wins*100.0/(wins+losses), false)
				+" >=5=  "+PrintUtils.Print2dec(count5*100.0/(total), false)+"%"
				//+" ("+PrintUtils.Print2dec(me5/total, false)+")"
				+" >=10=  "+PrintUtils.Print2dec(count10*100.0/(total), false)+"%"
				//+" ("+PrintUtils.Print2dec(me10/total, false)+")"
				+" >=15=  "+PrintUtils.Print2dec(count15*100.0/(total), false)+"%"
				//+" ("+PrintUtils.Print2dec(me15/total, false)+")"
				+" >=20=  "+PrintUtils.Print2dec(count20*100.0/(total), false)+"%"
				+" >=25=  "+PrintUtils.Print2dec(count25*100.0/(total), false)+"%"
				+" >=30=  "+PrintUtils.Print2dec(count30*100.0/(total), false)+"%"
				//+" ("+PrintUtils.Print2dec(me30/total, false)+")"
				+" "+PrintUtils.Print2dec(me, false)
				);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		String path   = "c:\\fxdata";
		String file1H = path+"\\"+"EURUSD_Hourly_Bid_2003.05.04_2014.02.07.csv";
		String file5m = path+"\\"+"EURUSD_5 Mins_Bid_2003.05.04_2014.01.07.csv";
				
		ArrayList<Quote> dataI 		 = DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		 = TestLines.calculateCalendarAdjusted(dataI);
		ArrayList<Quote> data5m 	 = TradingUtils.cleanWeekendData(dataS);
		ArrayList<Quote> dailyData   = ConvertLib.createDailyData(data5m);
		ArrayList<Quote> weeklyData  = ConvertLib.createWeeklyData(data5m);
		ArrayList<Quote> monthlyData = ConvertLib.createMonthlyData(data5m);
		ArrayList<PhilDay> pDays     = TradingUtils.calculateLines(data5m, dailyData, weeklyData, monthlyData);
		//System.out.println("Initial data size y cleaned philDays: "+dataI.size()+" "+data5m.size()+" "+philDays.size());
		System.out.println("data5m: "+data5m.size());
	
		String symbol    	="EURUSD";
		int day1 			= Calendar.MONDAY+0;
		int day2 			= Calendar.MONDAY+4;
		int h1              = 16;
		int h2              = 19; 
		int bPips1          = 0;
		int bPips2          = 0;
		LineType line1      = LineType.FIBR1;
		LineType line2      = LineType.FIBR2;
		//LineType line1      = LineType.DR2;
		//LineType line2      = LineType.DR3;
		boolean modeUp      = true;
		
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		ArrayList<Quote>   data  = new ArrayList<Quote>();
		int year = 2010;
		Calendar from2 = Calendar.getInstance();
		Calendar to2 = Calendar.getInstance();
	
		ArrayList<PriceTestResult> allResults = new ArrayList<PriceTestResult>();
		for (year=2012;year<=2012;year++){
			for (bPips1=0;bPips1<=0;bPips1++){
				bPips2=bPips1;
				for (h1=0;h1<=17;h1++){
					h2 = h1+6;
					String header = "year= "+year+" h1= "+h1+" h2= "+h2+" bPips1= "+bPips1+" bips2= "+bPips2;
					from.set(2010, 0, 1);
					to.set(2014, 11, 13);
					allResults.clear();
					while (from.getTimeInMillis()<=to.getTimeInMillis()){
						int actualM = from.get(Calendar.MONTH);
						int actualY = from.get(Calendar.YEAR);						
						String fileData  = TradingUtils.getFileData(symbol,actualM,actualY);					
						from2.set(actualY, actualM, 1);
						to2.set(actualY, actualM, 31);							
						File file = new File(path+"\\"+fileData);
						if (file.exists()){											
							DAO.retrieveData(data,path+"\\"+fileData, DataProvider.DUKASCOPY_FOREX,0);
							ArrayList<PriceTestResult> res = StudyLevels.testInterlevel(data, pDays, from2, to2, day1, day2, h1, h2, line1, line2, bPips1, bPips2, modeUp);
							addPriceResults(allResults,res);
						}										
						from.add(Calendar.MONTH, 1);
					}//while
					printTestResults(header,allResults);
				}
			}
		}
	}

}
