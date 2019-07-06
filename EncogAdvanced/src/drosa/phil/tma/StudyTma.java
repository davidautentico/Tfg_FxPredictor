package drosa.phil.tma;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.IndicatorLib;
import drosa.phil.PriceTestResult;
import drosa.phil.TMA;
import drosa.phil.TestLines;
import drosa.phil.TmaDiff;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyTma {
	
	private static int findDataIndex(ArrayList<Quote> data,TmaDiff tmaDiff,int index){
		
		Calendar actual = Calendar.getInstance();
		for (int i=index;i<data.size();i++){
			Quote q = data.get(i);
			actual.setTime(q.getDate());
			//System.out.println(">>>>>: "+DateUtils.datePrint(tmaDiff.getCal())+" "+DateUtils.datePrint(actual));
			if (DateUtils.isDateTimeEqual(actual, tmaDiff.getCal()))
					return i;
		}
		return -1;
	}
	
	private static void studyDiffsPrecise(String header,ArrayList<Quote> data, ArrayList<TmaDiff> tmaDiffs, int d1, int d2,
			int h1, int h2,int diff,int sl,int tp, int mode) {
		// TODO Auto-generated method stub
		int totalWins=0;
		int totalLosses=0;
		int index = 0;
		Calendar cal = Calendar.getInstance();
		Calendar calIndex = Calendar.getInstance();
		for (int i=0;i<tmaDiffs.size();i++){
			TmaDiff tmaDiff = tmaDiffs.get(i);
			cal.setTime(tmaDiff.getCal().getTime());
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int h   = cal.get(Calendar.HOUR_OF_DAY);
			if (day<d1 || day>d2 ) continue;
			if (h<h1 || h>h2) continue;
			
			boolean win = true;
			if (mode==1){
				//System.out.println("TMAdiff : "+DateUtils.datePrint(tmaDiff.getCal())+" "+tmaDiff.getDiffUp());
				if (tmaDiff.getDiffUp()==diff){					
					//System.out.println("TMAdiff : "+DateUtils.datePrint(tmaDiff.getCal())+" "+tmaDiff.getDiffUp()+" a buscar desde indice: "+index);
					calIndex.setTime(data.get(index).getDate());
					if (calIndex.getTimeInMillis()>tmaDiff.getCal().getTimeInMillis()) continue;
					int found = findDataIndex(data,tmaDiff,index);
					if (found>=0){
						index = found;
						
						//System.out.println("TMA A BUSCAR: "+DateUtils.datePrint(tmaDiff.getCal()));
						//System.out.println("quote found: "+DateUtils.datePrint(data.get(found).getDate()));
						double beginValue  = data.get(found).getHigh();
						double stopLoss   = beginValue+0.0001*sl;
						double takeProfit = beginValue-0.0001*tp;			
					   /*System.out.println(PrintUtils.Print4dec(beginValue)
							   +" "+PrintUtils.Print4dec(stopLoss)
							   +" "+PrintUtils.Print4dec(takeProfit)
							   );*/
						PriceTestResult res = TradingUtils.testPriceMovement(data, index+1, data.size()-1, beginValue, stopLoss, takeProfit,0);
						
						
						index = res.getIndex()+1;
						//System.out.println("index first then: "+found+" "+index);
						win = res.isWin();
						
						if (win){
							totalWins++;
						}else{
							totalLosses++;
						}
						//System.out.println("total=" +(totalWins+totalLosses)+" wins: "+PrintUtils.Print2(totalWins*100.0/(totalWins+totalLosses)));
					}
				}
			}else if (mode==0){
				if (tmaDiff.getDiffDown()==diff){
					
				}
			}
			
		}
		double winPer = totalWins*100.0/(totalWins+totalLosses);
		double lossPer = 100.0-winPer;
		int total = totalWins+totalLosses;
		double ME = (tp*winPer-sl*lossPer)/100;		
		System.out.println( header+" total="+(totalWins+totalLosses)
				+" WINS= "+PrintUtils.Print2(totalWins*100.0/(totalWins+totalLosses))
				+" ME= "+PrintUtils.Print2(ME)
				+" PROFIT= "+PrintUtils.Print2(ME*1.0*total)
				);		
	}
	
	private static void studyDiffs(String header,ArrayList<TmaDiff> tmaDiffs, int d1, int d2,
			int h1, int h2,int diff,int sl,int tp, int mode) {
		// TODO Auto-generated method stub
		int totalWins=0;
		int totalLosses=0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<tmaDiffs.size();i++){
			TmaDiff tmaDiff = tmaDiffs.get(i);
			cal.setTime(tmaDiff.getCal().getTime());
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int h   = cal.get(Calendar.HOUR_OF_DAY);
			if (day<d1 || day>d2 ) continue;
			if (h<h1 || h>h2) continue;
			
			boolean win = true;
			int tpD = diff;
			int slD = diff;
			if (mode==1){
				if (tmaDiff.getDiffUp()==diff){
					tpD = tmaDiff.getDiffUp()-tp;
					slD = tmaDiff.getDiffUp()+sl;
					//System.out.println(diff+" "+slD+" "+tpD);
					for (int j=i+1;j<tmaDiffs.size();j++){
						//System.out.println("tmas: "+tmaDiffs.get(j).getDiffUp());
						if (tmaDiffs.get(j).getDiffUp()>=slD){
							//i = j+1;
							win = false;
							break;
						}else if (tmaDiffs.get(j).getDiffUp()<=tpD){
							//i = j+1;
							win = true;
							break;
						} 
					}
					
					if (win){
						totalWins++;
					}else{
						totalLosses++;
					}
					//System.out.println("total=" +(totalWins+totalLosses)+" wins: "+PrintUtils.Print2(totalWins*100.0/(totalWins+totalLosses)));
				}
			}else if (mode==0){
				if (tmaDiff.getDiffDown()==diff){
					
				}
			}
			
		}
		double winPer = totalWins*100.0/(totalWins+totalLosses);
		double lossPer = 100.0-winPer;
		int total = totalWins+totalLosses;
		double ME = (tp*winPer-sl*lossPer)/100;		
		System.out.println( header+" total="+(totalWins+totalLosses)
				+" WINS= "+PrintUtils.Print2(totalWins*100.0/(totalWins+totalLosses))
				+" ME= "+PrintUtils.Print2(ME)
				+" PROFIT= "+PrintUtils.Print2(ME*1.0*total)
				);		
	}

	static ArrayList<TmaDiff> retrieveYearTmaDiff(String path,
			String symbol, int year) {
		// TODO Auto-generated method stub
		ArrayList<TmaDiff> all = new ArrayList<TmaDiff>();
		for (int m=0;m<=11;m++){
			String fileData  = TradingUtils.getFileTmaDiff("EURUSD",m,year);
			String fileName = path+"\\"+fileData;
			File file = new File(fileName);
			if (file.exists()){
				//System.out.println("load fileName: "+fileName);
				ArrayList<TmaDiff> tmas = DAO.retrieveTmaDiff(fileName);
				//System.out.println("tma1: "+DateUtils.datePrint(tmas.get(0).getCal()));
				for (int i=0;i<tmas.size();i++){
					all.add(tmas.get(i));
				}
			}
		}
		return all;
	}
	
	private static ArrayList<Quote> retrieveYear1secData(String path,
			String symbol, int year) {
		// TODO Auto-generated method stub
		ArrayList<Quote> all  = new ArrayList<Quote>();
		for (int m=0;m<=11;m++){
			String fileData  = TradingUtils.getFileData("EURUSD",m,year);					
			//System.out.println("tma1: "+DateUtils.datePrint(tmaDiffs.get(0).getCal()));
			File file = new File(path+"\\"+fileData);
			if (file.exists()){
				//System.out.println("filedata: "+path+"\\"+fileData);
				ArrayList<Quote> temp = DAO.retrieveData2(path+"\\"+fileData, DataProvider.DUKASCOPY_FOREX,0);
				for (int i=0;i<temp.size();i++){
					all.add(temp.get(i));
				}
				//System.out.println("total data: "+data1sec.size());				
				//studyDiffs(header,tmaDiffs,d1,d2,h1,h2,diff,sl,tp,mode);
			}//if
			
		}
		return all;
	}
	
	public static void writeFile(String symbol,String sufix,ArrayList<String> data,int month,int year,String path){
		try{
			String fileName = path+"\\"+symbol+"_"+DateUtils.getAlways2digits(month+1)+"_"+year+"_"+sufix+".csv";
			File file = new File(fileName);
			if (file.exists()) file.delete();
			
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				String s = data.get(i);
				writer.println(s);
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static TMA findTMA(ArrayList<TMA> tmas,Calendar calFind,int index){
		
		int hourFind = calFind.get(Calendar.HOUR_OF_DAY);
		int minFind  = calFind.get(Calendar.MINUTE);
		Calendar actual = Calendar.getInstance();
		for (int i=index;i<tmas.size();i++){
			TMA tma = tmas.get(i);
			actual.setTime(tma.getDate().getTime());
			int actualHour = actual.get(Calendar.HOUR_OF_DAY);
			int actualMinute = actual.get(Calendar.MINUTE);
			if (DateUtils.isSameDay(actual, calFind)){
				if (actualHour==hourFind){
					int diff = minFind-actualMinute;
					if (diff<5){
						tma.setIndex(i);
						return tma;
					}
				}				
			}
		}
		return null;
	}
	
	public static ArrayList<String> analisysTMA(ArrayList<Quote> data,ArrayList<TMA> tmas,Calendar from, Calendar to, 
			int d1,int d2,int h1,int h2){
		
		ArrayList<String> res = new ArrayList<String>();
		Calendar actual = Calendar.getInstance();
		int maxUpper = 0;
		int maxLower = 0;
		int lastDay=-1;
		int index = 0;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			actual.setTime(q.getDate());
			int h = actual.get(Calendar.HOUR_OF_DAY);
			int day = actual.get(Calendar.DAY_OF_WEEK);
			if (day!=lastDay){
				maxUpper = 0;
				maxLower = 0;
				lastDay = day;
			}
			//if (actual.getTimeInMillis()<from.getTimeInMillis()) continue;
			//if (actual.getTimeInMillis()>to.getTimeInMillis()) break;			
			//if (day<d1 || day>d2) continue;
			//if (h<h1 || h>h2) continue;
			
			TMA tma = findTMA(tmas,actual,index);
			if (tma!=null){
				index = tma.getIndex();
				int diffUpper = TradingUtils.getPipsDiff(q.getHigh(), tma.getUpper());
				int diffLower = TradingUtils.getPipsDiff(tma.getLower(), q.getLow());
				if (diffUpper>maxUpper){
					maxUpper = diffUpper;
					//System.out.println(DateUtils.datePrint(actual)+" "+DateUtils.datePrint(tma.getDate())
					//		+" "+maxUpper+" "+maxLower);
				}
				if (diffLower>maxLower){
					maxLower = diffLower;
					//System.out.println(DateUtils.datePrint(actual)+" "+maxUpper+" "+maxLower);
				}
				String resStr = DateUtils.datePrint(actual)+" "+diffUpper+" "+diffLower;
				//System.out.println(resStr);
				res.add(resStr);
			}
		}
		return res;
	}
	
	
	public static void testTMA(ArrayList<Quote> data,ArrayList<TMA> tmas,Calendar from, Calendar to, 
			int d1,int d2,
			int h1,int h2,int offset,int be,int maxPips){
		
		int totalWins = 0;
		int totalLosses = 0;
		Calendar actual = Calendar.getInstance();
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			Quote q1 = data.get(i-1);
			
			actual.setTime(q1.getDate());
			int h = actual.get(Calendar.HOUR_OF_DAY);
			int day = actual.get(Calendar.DAY_OF_WEEK);
			if (actual.getTimeInMillis()<from.getTimeInMillis()) continue;
			if (actual.getTimeInMillis()>to.getTimeInMillis()) break;
			
			if (day<d1 || day>d2) continue;
			if (h<h1 || h>h2) continue;
			
			TMA tma = tmas.get(i);
			
			double upper = tma.getUpper();
			double lower = tma.getLower();
			
			//int upOffset   = TradingUtils.getPipsDiff(q1.getClose(), upper);
			//int downOffset = TradingUtils.getPipsDiff(lower,q1.getClose());
			
			int upOffset   = TradingUtils.getPipsDiff(q1.getHigh(), upper);
			int downOffset = TradingUtils.getPipsDiff(lower,q1.getLow());
			
			double beginValue = q.getOpen();
			double stopLoss   = q.getOpen();
			double takeProfit = q.getOpen();
			int mode = -1;
			if (upOffset>=offset){// && q.getOpen()<upper){
				stopLoss   = q.getOpen()+0.0001*maxPips;
				takeProfit = q.getOpen()-0.0001*be;
				mode = 0;
			}else if (downOffset>=offset){// && q.getOpen()>lower){
				//stopLoss   = q.getOpen()-0.0001*maxPips;
				//takeProfit = q.getOpen()+0.0001*be;
				//mode = 1;
			}
			if (mode>=0){
				PriceTestResult res = TradingUtils.testPriceMovement(data,i,data.size()-1,beginValue,stopLoss,takeProfit,mode);
				//System.out.println("win: "+res.isWin());
				if (res.isWin())
					totalWins++;
				else totalLosses++;
			}
		}
		double winPer = totalWins*100.0/(totalWins+totalLosses);
		System.out.println("h1 h2 offset win losses "+h1+" "+h2+" "+offset+" "+totalWins+" "+totalLosses+" "+PrintUtils.Print2dec(winPer,false));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "c:\\fxdata";
		String file5m = path+"\\"+"EURUSD_5 Mins_Bid_2003.05.04_2014.01.07.csv";
		
		ArrayList<Quote> dataI 			= DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 			= TradingUtils.cleanWeekendData(dataS);
  		//data 		= ConvertLib.createDailyData(data);
  		Calendar from = Calendar.getInstance();
  		Calendar to = Calendar.getInstance();
  		int d1 = Calendar.MONDAY;
  		int d2 = Calendar.MONDAY+4;
  		int h1 = 0;
  		int h2 = 23;
  		int offset = 5;
  		int be = 10;
  		int maxPips = 20;
  		double bandFactor = 2.9;
		int atrPeriod = 100;
		int halfLength = 56;
		int year = 2012;
		
		String header = "EURUSD_tmaDiff";
		for(halfLength=56;halfLength<=56;halfLength+=5)
		for (atrPeriod = 100;atrPeriod <= 100; atrPeriod++){
			/*ArrayList<TMA> tma5m = IndicatorLib.calculateTMA_Array(data, 0,data.size()-1,bandFactor,halfLength,atrPeriod);
			//System.out.println("total data y tma 5m: "+data.size()+" "+tma5m.size());
			ArrayList<Quote> data1s = new ArrayList<Quote>();
			
			header = "EURUSD_tmaDiff";
			String suffix = "tma5mDiff";
			from.set(year, 0, 1);
			to.set(year, 11, 31);
				
			Calendar from2 = Calendar.getInstance();
			Calendar to2 = Calendar.getInstance();
			while (from.getTimeInMillis()<=to.getTimeInMillis()){
				int actualM = from.get(Calendar.MONTH);
				int actualY = from.get(Calendar.YEAR);
					
				String fileData  = TradingUtils.getFileData("EURUSD",actualM,actualY);
			
				from2.set(actualY, actualM, 1);
				to2.set(actualY, actualM, 31);
						
				File file = new File(path+"\\"+fileData);
				if (file.exists()){
					//System.out.println("filedata: "+path+"\\"+fileData);
					DAO.retrieveData(data1s,path+"\\"+fileData, DataProvider.DUKASCOPY_FOREX,0);	
					//System.out.println("total data: "+data1s.size());
					ArrayList<String> res = StudyTma.analisysTMA(data1s,tma5m,from,to,d1,d2,h1,h2);
					//System.out.println(" a escribir: "+res.size());
					StudyTma.writeFile("EURUSD",suffix, res,actualM, year, path);
				}
				from.add(Calendar.MONTH, 1);
			}
			data1s.clear();*/
			
			d1 = Calendar.MONDAY+0;
			d2 = Calendar.MONDAY+4;
			h1 = 0;
			h2 = 23;
			int diff = 0;
			int sl   = 10;
			int tp   = 5;
			int mode = 1;
			year = 2012;
			//year = 2013;
			ArrayList<TmaDiff> tmaDiffs = StudyTma.retrieveYearTmaDiff(path,"EURUSD",year);
			System.out.println("diffs: "+tmaDiffs.size());
			ArrayList<Quote> data1sec   = StudyTma.retrieveYear1secData(path,"EURUSD",year);			 
			System.out.println("data1sec: "+data1sec.size());
			for (h1=0;h1<=0;h1++){
				h2 = h1+7;
				for (diff=-4;diff<=20;diff++){					
					header = "year= "+year+"day1="+d1+" "+d2+" atrPeriod= "+atrPeriod+" halfLength="+halfLength+" diff= "+diff+" h1="+h1+" h2= "+h2;
					//studyDiffs(header,tmaDiffs,d1,d2,h1,h2,diff,sl,tp,mode);
					studyDiffsPrecise(header,data1sec,tmaDiffs,d1,d2,h1,h2,diff,sl,tp,mode);					
				}//diff
			}//h1
			//data1sec.clear();
			tmaDiffs.clear();
		}//atr
	}//main

	

}
