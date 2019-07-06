package drosa.phil;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestSaveLevelRetracements {

	
	public static void addtoArray(ArrayList<BreachingLevelResult> allResults,
			ArrayList<BreachingLevelResult> results) {
		// TODO Auto-generated method stub
		for (int i=0;i<results.size();i++){
			BreachingLevelResult res = results.get(i);
			BreachingLevelResult resNew = new BreachingLevelResult();
			//System.out.println("a copiar: "+res.toString());
			resNew.copy(res);
			allResults.add(resNew);
		}
	}
	
	private static void saveResults(LineType line,
			ArrayList<BreachingLevelResult> results) {
		// TODO Auto-generated method stub
		String fileName="c:\\fxdata\\EURUSD_"+line.name()+".csv";
		File file = new File(fileName);
		if (file.exists()){
			file.delete();
		}
		
		try{
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<results.size();i++){	
				BreachingLevelResult res = results.get(i);
				cal.setTime(res.getDay().getTime());
				String dateStr = DateUtils.getYMD(cal);
				int pipsB = res.getPipsBreaching();					
				int pipsT = res.getTargetPips();
				int pipsM = res.getMaxPips();
				int suc   = res.getSuccess();
				writer.println(dateStr+","+pipsB+","+pipsT+","+pipsM+","+suc);				
			}
			writer.close();
		}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path   = "c:\\fxdata";
		String file1H = path+"\\"+"EURUSD_Hourly_Bid_2005.01.01_2013.12.04.csv";

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
		ArrayList<LineType> lines = new ArrayList<LineType>();
		lines.add(lineType1);
		lines.add(lineType2);
		lines.add(lineType3);
		lines.add(lineType4);
		lines.add(lineType5);
		//String symbol="EURUSD";
		String symbol    	="EURUSD";
		int yearF      	 	= 2010;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2013;
		int monthL 			= Calendar.DECEMBER;
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		ArrayList<Quote>   data  = new ArrayList<Quote>();
		ArrayList<BreachingLevelResult> allResults = new ArrayList<BreachingLevelResult>();
		//ArrayList<PhilDay> pDays = StudyRangeFibs.calculateFIBS(file1H,2,100);
		System.out.println("FIBS calculados");
		for (int i=0;i<lines.size();i++){//para cada linea
		//for (int i=0;i<1;i++){//para cada linea
			LineType line = lines.get(i);
			allResults.clear();
			for (int pipsBreaching=0;pipsBreaching<=5;pipsBreaching+=1){
				for (int bePips=4;bePips<=4;bePips+=5){
					for (int maxPips=10;maxPips<=10;maxPips+=5){
						ArrayList<PhilDay> pDays = StudyRangeFibs.calculateFIBS(file1H,3,100);
						from.set(yearF, monthF, 1);
						to.set(yearL, monthL, 31);
						Calendar from2 = Calendar.getInstance();
						Calendar to2 = Calendar.getInstance();
						while (from.getTimeInMillis()<=to.getTimeInMillis()){
							int actualM = from.get(Calendar.MONTH);
							int actualY = from.get(Calendar.YEAR);
							String fileData  = TradingUtils.getFileData(symbol,actualM,actualY);
							from2.set(actualY, actualM, 1);
							to2.set(actualY, actualM, 31);
							File file = new File(path+"\\"+fileData);
							if (file.exists()){
								//System.out.println("filedata: "+path+"\\"+fileData);
								DAO.retrieveData(data,path+"\\"+fileData, DataProvider.DUKASCOPY_FOREX,0);
								ArrayList<BreachingLevelResult> results = StudyRangeFibs.testRetracements(data, pDays, 
										from2, to2,line,pipsBreaching,bePips,maxPips);
								TestSaveLevelRetracements.addtoArray(allResults,results);
							}
							//addtoArray(allContinuations,breachingStudy.continuations);
								from.add(Calendar.MONTH, 1);
									//System.out.println("one month");
						}//while
					}//for maxPips
				}//for bePips
			}//for pipsBreachings
			saveResults(line,allResults);			
		}
	}

	

}
