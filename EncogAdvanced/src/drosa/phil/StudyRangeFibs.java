package drosa.phil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyRangeFibs {
	
	
	private static double breachingAnalysis2(ArrayList<Quote> breachings,String header,int bePips,int maxPips,
			boolean traceEnabled,boolean printLossDates,boolean print){
		int wins = 0;
		int losses = 0;
		int maeAcc=0;
		int totalLess=0;
		int bestStreak=0;
		int actualStreak=0;
		String streaks="";
		for (int i=0;i<breachings.size();i++){
			Quote q = breachings.get(i);
			int mae = (int)q.getHigh();
			if (q.getOpen()>100){
				if (traceEnabled)
					System.out.println("WIN maxAdv: "+(int)q.getHigh());
				wins++;
				maeAcc+=(int)q.getHigh();
				if (mae<maxPips){ 
					totalLess++;
					actualStreak++;
					streaks+="W";
				}else{
					if (actualStreak>bestStreak){
						bestStreak = actualStreak;
						//streaks+=" "+actualStreak;
					}
					streaks+="L";
					actualStreak=0;
					losses++;
					if (printLossDates)
						System.out.println(DateUtils.datePrint(q.getDate()));
				}
			}else if (q.getOpen()<-100){
				if (actualStreak>bestStreak){
					bestStreak = actualStreak;
					
				}
				streaks+="L";
				actualStreak=0;
				losses++;
				if (traceEnabled)
					System.out.println("LOSS maxAdv: "+(int)q.getHigh());
				
				if (printLossDates)
					System.out.println(DateUtils.datePrint(q.getDate()));
			}
		}
		if (actualStreak>bestStreak){
			bestStreak = actualStreak;
			//streaks+=" "+actualStreak;
		}
		//streaks+=" "+actualStreak;
		actualStreak=0;
		
		double winPer = wins*100.0/breachings.size();
		double avg    = maeAcc*1.0/wins;
		int totalData = breachings.size();
		double perLessMax = totalLess*100.0/breachings.size();
		double expMat = (bePips*totalLess-maxPips*losses)*1.0/(totalLess+losses);
		if (print){
			System.out.println(header+" "+
				PrintUtils.PrintInt(maxPips)
				+" "+PrintUtils.PrintInt(totalLess)
				+" "+PrintUtils.PrintInt(losses)
				+" "+PrintUtils.Print(perLessMax)+'%'
				+" "+PrintUtils.Print2(expMat)
				+" "+streaks				
				);
		}
		return expMat;
	}

	/**
	 * Calculate lines from 1H data
	 * @param symbol
	 * @param file1H
	 * @param path
	 */
	public static ArrayList<PhilDay> calculateFIBS(String file1H,int shortPeriod,int longPeriod){
		ArrayList<Quote> dataI 			= DAO.retrieveData(file1H, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 			= TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData 		= ConvertLib.createDailyData(data);

  		ArrayList<PhilDay> philDays 	= TradingUtils.calculateFIBS(data, dailyData,shortPeriod,longPeriod);
		//System.out.println("Initial data size y cleaned philDays: "+dataI.size()+" "+data.size()+" "+philDays.size());
		
		return philDays;
	}
	
	private static void addtoArray(ArrayList<Quote> allBreachings,
			ArrayList<Quote> dataPoints) {
		// TODO Auto-generated method stub
		for (int i=0;i<dataPoints.size();i++){
			Quote q = dataPoints.get(i);
			Quote qNew = new Quote();
			qNew.copy(q);
			allBreachings.add(qNew);
		}
	}
	
	
	
	public static BreachingStudy testMaxRetrace(ArrayList<Quote> data,ArrayList<PhilDay> philDays,
			Calendar from,Calendar to,int dL,int dH,int hl,int hh,LineType testLevel,
			int breachingPips, int targetPips, int dstDOl,int dstDOh){
		
		BreachingStudy breachingStudy = new BreachingStudy();
		ArrayList<Quote> breachings = new ArrayList<Quote>();
		ArrayList<Quote> continuations = new ArrayList<Quote>();
		//Para cada día testeamos
		for (int i=0;i<=philDays.size()-1;i++){
			//System.out.println("dia "+i);
			PhilDay pDay = philDays.get(i);
			Calendar actualDate = pDay.getDay();
			int dayW = actualDate.get(Calendar.DAY_OF_WEEK);
			if (dayW<dL || dayW>dH) continue;
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() || actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			//System.out.println("dia "+i+" "+DateUtils.datePrint(actualDate.getTime()));
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
			
			if (LINE<DO) continue;
			int linePipsDiff = TradingUtils.getPipsDiff(LINE, DO);
			/*System.out.println("pipsDiff: "+linePipsDiff
					+" "+PrintUtils.Print(LINE)
					+" "+PrintUtils.Print(DO));
		    */
			if (linePipsDiff<dstDOl || linePipsDiff>dstDOh) continue;
			//System.out.println("pasa el corte: "+dstDOl+" "+dstDOh+" "+linePipsDiff);
			//Obtenemos los datos de este PDay
			int index = TradingUtils.getDayIndex(data,pDay.getDay());
			ArrayList<Quote> newDayData = TradingUtils.getDayData(data,pDay.getDay());

			Calendar cal = Calendar.getInstance();
			boolean breached = false;

			for (int j=0;j<newDayData.size();j++){ //para cada dato de este dia
				Quote q = newDayData.get(j);
				cal.setTime(q.getDate());
				int h = cal.get(Calendar.HOUR_OF_DAY);
				if (h<hl || h>hh) continue;
		
				double beginValue = LINE+0.0001*breachingPips;
				double targetValue = beginValue-0.0001*targetPips;
				int pipsDiff = TradingUtils.getPipsDiff(q.getHigh(),beginValue);//pips por encima de LINE
				breached = false;

				if (pipsDiff==0)breached = true;
				
				if (breached					
						){//sólo la primera del día
					int lastQuoteIdx = data.size()-1;
					int begin = index+j+1;
										
					Quote qBreach  = TradingUtils.testPriceMovement(data,begin,lastQuoteIdx,beginValue,targetValue,true);
					qBreach.setDate(q.getDate());
					
					qBreach.setIndex(begin);
					breachings.add(qBreach);
					//System.out.println("breach add date: "+DateUtils.datePrint(q.getDate()));
					break;//salimos del for
				}//end breached
			}//end for daily
		}//end phil days
		breachingStudy.setBreachings(breachings);
		breachingStudy.setContinuations(continuations);
		return breachingStudy;
		
	}
	
	public static ArrayList<BreachingLevelResult> testRetracements(ArrayList<Quote> data,ArrayList<PhilDay> philDays,
			Calendar from,Calendar to,LineType testLevel,
			int breachingPips, int targetPips, int maxPips){
		
		ArrayList<BreachingLevelResult> results = new ArrayList<BreachingLevelResult>();
		ArrayList<Quote> breachings = new ArrayList<Quote>();
		boolean above = true;
		//Para cada día testeamos
		for (int i=0;i<=philDays.size()-1;i++){
			//System.out.println("dia "+i);
			PhilDay pDay = philDays.get(i);
			Calendar actualDate = pDay.getDay();

			if (actualDate.getTimeInMillis()<from.getTimeInMillis() || actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			//System.out.println("dia "+i+" "+DateUtils.datePrint(actualDate.getTime()));
			//obtenemos las lineas
			ArrayList<PhilLine> lines = pDay.getLines();
			double LINE=-1;
			double DO=-1;
			//BUSCAMOS LINE Y DO
			for (int j=0;j<lines.size();j++){
				if (lines.get(j).getLineType()==testLevel)
					LINE = lines.get(j).getValue();
				if (lines.get(j).getLineType()==LineType.DO)
					DO = lines.get(j).getValue();
			}
		    //
			if (LINE<DO){
				above = false;
			}
			
			
			//Obtenemos los datos de este PDay
			int index = TradingUtils.getDayIndex(data,pDay.getDay());
			ArrayList<Quote> newDayData = TradingUtils.getDayData(data,pDay.getDay());

			Calendar cal = Calendar.getInstance();
			boolean breached = false;
			
			for (int j=0;j<newDayData.size();j++){ //para cada dato de este dia
				Quote q = newDayData.get(j);
				cal.setTime(q.getDate());
				
				double beginValue  = LINE+0.0001*breachingPips;
				double targetValue = beginValue-0.0001*targetPips;
				double maxValue    = beginValue+0.0001*maxPips;
				int pipsDiff = TradingUtils.getPipsDiff(q.getHigh(),beginValue);//pips por encima de LINE
				if (!above){
					beginValue  	= LINE-0.0001*breachingPips;
					targetValue 	= beginValue+0.0001*targetPips;
					maxValue 		= beginValue-0.0001*maxPips;
					pipsDiff 	= TradingUtils.getPipsDiff(beginValue,q.getLow());//pips por encima de LINE
				}
				
				breached = false;
				if (pipsDiff>=0) breached = true;
				
				if (breached					
						){//sólo la primera del día
					int lastQuoteIdx = data.size()-1;
					int begin = index+j+1;
										
					Quote qBreach  = TradingUtils.checkPriceRetrace(data,begin,lastQuoteIdx,beginValue,targetValue,maxValue,true);
					qBreach.setDate(q.getDate());
					qBreach.setIndex(begin);
					
					//System.out.println("q time: "+DateUtils.datePrint(q.getDate()));
					
					BreachingLevelResult result = new BreachingLevelResult();
					Calendar dayCal = Calendar.getInstance();
					dayCal.setTime(q.getDate());
					result.setDay(dayCal);
					result.setPipsBreaching(breachingPips);
					result.setTargetPips(targetPips);
					result.setMaxPips(maxPips);
					result.setLine(testLevel);
					if (qBreach.getOpen()>0)
						result.setSuccess(1);
					else if (qBreach.getOpen()<-900)
						result.setSuccess(0);
					else result.setSuccess(-1);
					System.out.println(result.toString());
					results.add(result);					
					break;//salimos del for
				}				
			}//end for daily
			if (!breached){
				BreachingLevelResult result = new BreachingLevelResult();
				Calendar dayCal = Calendar.getInstance();
				dayCal.setTimeInMillis(actualDate.getTimeInMillis());
				result.setDay(dayCal);
				result.setPipsBreaching(breachingPips);
				result.setTargetPips(targetPips);
				result.setMaxPips(maxPips);
				result.setLine(testLevel);
				result.setSuccess(-1);
				System.out.println(result.toString());
				results.add(result);
			}		
		}//end phil days

		return results;	
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path   = "c:\\fxdata";
		String file1H = path+"\\"+"EURUSD_UTC_Hourly_Bid_2003.05.04_2014.06.11.csv";
		
		//System.out.println("pdays: "+pDays.size());
		
		LineType lineType1 	= LineType.FIBR1;
		LineType lineType2 	= LineType.FIBR2;
		LineType lineType3 	= LineType.FIBR3;
		LineType lineType4 	= LineType.FIBR4;
		LineType lineType5 	= LineType.FIBR5;
		ArrayList<LineType> lines = new ArrayList<LineType>();
		lines.add(lineType1);
		lines.add(lineType2);
		lines.add(lineType3);
		lines.add(lineType4);
		lines.add(lineType5);
		//String symbol="EURUSD";
		String symbol    	="EURUSD";
		int yearF      	 	= 2013;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2013;
		int monthL 			= Calendar.DECEMBER;
		int dL 				= Calendar.MONDAY+0;
		int dH 				= Calendar.MONDAY+2;
		int h1              = 0;
		int h2              = 23; 
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		ArrayList<Quote>   data  = new ArrayList<Quote>();
		ArrayList<Quote> allBreachings= new ArrayList<Quote>();
		ArrayList<PhilDay> pDays = StudyRangeFibs.calculateFIBS(file1H,3,100);
		System.out.println("FIBS calculados");
		for (h1=0;h1<=0;h1++){
			h2=h1+11;
			for (int i=0;i<=4;i++){
				LineType line = lines.get(i);
				System.out.println("year line: "+yearF+" "+line.name());
				for (int bePips=10;bePips<=10;bePips+=1){
				//for (int pipsBreaching=-5;pipsBreaching<=10;pipsBreaching+=1){
					for (int maxPips=80;maxPips<=80;maxPips+=1){
					//for (double factor=1;factor<=40.0;factor+=0.5){
						double avg=0;//avg por breachings
						int totalBreachs=9;
						for (int pipsBreaching=2;pipsBreaching<=2;pipsBreaching+=1){
							//int maxPips=(int) (bePips*factor);
							//maxPips = 5+bePips; 
							//ArrayList<PhilDay> pDays = StudyRangeFibs.calculateFIBS(file1H,3,100);
							allBreachings.clear();
							from.set(2009, 0, 1);
							to.set(2009, 11, 31);
								
							Calendar from2 = Calendar.getInstance();
							Calendar to2 = Calendar.getInstance();
							while (from.getTimeInMillis()<=to.getTimeInMillis()){
								int actualM = from.get(Calendar.MONTH);
								int actualY = from.get(Calendar.YEAR);
									
								String fileData  = TradingUtils.getFileData(symbol,actualM,actualY);
								//String fileLines = TradingUtils.getFileLines(symbol,actualM,actualY);
							
								from2.set(actualY, actualM, 1);
								to2.set(actualY, actualM, 31);
										
								File file = new File(path+"\\"+fileData);
								if (file.exists()){
									//System.out.println("filedata: "+path+"\\"+fileData);
									DAO.retrieveData(data,path+"\\"+fileData, DataProvider.DUKASCOPY_FOREX,0);
									BreachingStudy breachingStudy = StudyRangeFibs.testMaxRetrace(data, pDays, from2, to2, 
													dL,dH,h1, h2, line,pipsBreaching,bePips,0,2000);
									addtoArray(allBreachings,breachingStudy.breachings);
								}
								//addtoArray(allContinuations,breachingStudy.continuations);
									from.add(Calendar.MONTH, 1);
										//System.out.println("one month");
							}//while
							String header = String.valueOf(dL)+'-'+String.valueOf(dH)
											+" "+String.valueOf(h1)+'-'+String.valueOf(h2)
										    +" "+String.valueOf(pipsBreaching)
											+" "+String.valueOf(bePips)+" "+String.valueOf(maxPips)
											;
							avg+=breachingAnalysis2(allBreachings,header,bePips,maxPips,false,false,true);					
						}//for be
						//informe avg
						System.out.println("BE avg "+bePips+" "+PrintUtils.Print2dec(avg/totalBreachs, false));
					}//for maxpips
				}//for breaching	
			}//lines
		}//hours
	}
}
