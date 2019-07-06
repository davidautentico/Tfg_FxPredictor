package drosa.phil.levels;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.BreachingStudy;
import drosa.phil.LineType;
import drosa.phil.PhilDay;
import drosa.phil.PhilLine;
import drosa.phil.PriceTestResult;
import drosa.phil.StudyRangeFibs;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyLevels {
	
	
	
	/**
	 * Calcular el retrace entre niveles
	 */
	public static ArrayList<PriceTestResult> testInterlevel(ArrayList<Quote> data,ArrayList<PhilDay> pDays,Calendar from,Calendar to,
			int day1,int day2,int h1,int h2,LineType line1,LineType line2,int bPips1,int bPips2,boolean modeUp){
		
		ArrayList<PriceTestResult> results = new ArrayList<PriceTestResult>();
		for (int i=0;i<=pDays.size()-1;i++){ //para cada philday
			//System.out.println("dia "+i);
			PhilDay pDay = pDays.get(i);
			Calendar actualDate = pDay.getDay();
			int dayW = actualDate.get(Calendar.DAY_OF_WEEK);
			if (dayW<day1 || dayW>day2) continue;
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() || actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			//System.out.println("dia "+i+" "+DateUtils.datePrint(actualDate.getTime()));
			
			//obtenemos las lineas y valores de entrada y estop
			ArrayList<PhilLine> lines = pDay.getLines(); //niveles para este dia
			double line1value = 0;
			double line2value=0;
			for (int t = 0 ;t<lines.size();t++){
				PhilLine l = lines.get(t);
				if (l.getLineType()==line1) line1value= l.getValue();
				if (l.getLineType()==line2) line2value= l.getValue();
			}
			
			int mode = 0; //interesa que baje
			double beginValue  = line1value+0.0001*bPips1;
			double stopLoss    = line2value+0.0001*bPips2;
			int pipsDiff = TradingUtils.getPipsDiff(stopLoss, beginValue);
			if (!modeUp){
				mode = 1; //interesa que suba
				beginValue  = line1value-0.0001*bPips1;
				stopLoss	= line2value-0.0001*bPips2;	
				pipsDiff = TradingUtils.getPipsDiff(beginValue,stopLoss);
			}
			
			
			//lineas

			//Obtenemos los datos de este PDay
			int index = TradingUtils.getDayIndex(data,pDay.getDay());
			ArrayList<Quote> newDayData = TradingUtils.getDayData(data,pDay.getDay()); //datos del dia

			Calendar cal = Calendar.getInstance();
			boolean breached = false;

			for (int j=0;j<newDayData.size();j++){ //para cada dato de este dia
				Quote q   = newDayData.get(j);
				cal.setTime(q.getDate());
				int h = cal.get(Calendar.HOUR_OF_DAY);
				//if (h<h1 || h>h2) continue;
		
				int diff = TradingUtils.getPipsDiff(q.getHigh(), beginValue);
				if (!modeUp){
					diff = TradingUtils.getPipsDiff(beginValue,q.getLow());
				}
						
				if (diff>=0){
					breached = true;
				}
				
				if (breached && (h<h1 || h>h2)) break; //solo si es la primera de esa hora
							
				if (breached){//breached
					int begin = j+1;
					PriceTestResult res = TradingUtils.testPriceMovement(newDayData,begin,newDayData.size()-1,beginValue,stopLoss,-1,mode);
					res.setPipsDiff(pipsDiff);
					
					results.add(res);
					int maxPositive = res.getMaxPositive();
					int maxNegative = res.getMaxNegative();
					boolean win = res.isWin();
					//System.out.println(DateUtils.datePrint(cal)+" "+win+" "+maxNegative+" "+maxPositive);
					break;
				}//end breached*/
			}//end for daily
		}// end for
		return results;
	}
	
	private static LevelTouchResult getTouchedLine(ArrayList<PhilLine> lines,
			Quote q, Quote q1, int breachingPips) {
		
		LevelTouchResult res = new LevelTouchResult();
		res.setTouched(false);
		for (int i=0;i<lines.size();i++){
			PhilLine line = lines.get(i);
			if (line.getLineType()==LineType.DO) continue;
			double value = line.getValue();
			int low  = TradingUtils.getPipsDiff(value, q.getLow());
			int high = TradingUtils.getPipsDiff(q.getHigh(),value);
			int low1  = TradingUtils.getPipsDiff(value, q1.getLow());
			int high1 = TradingUtils.getPipsDiff(q1.getHigh(),value);
			
			boolean touched = false;
		    if (low>=breachingPips && low1<breachingPips
					&& (  //line.getLineType().name().contains("FIBS1")
							 line.getLineType().name().contains("FIBS4")
							//|| line.getLineType().name().contains("FIBS2")
							//line.getLineType().name().contains("FIBS3")
							//|| line.getLineType().name().contains("FIBS4")
							//|| line.getLineType().name().contains("FIBS5")
							//line.getLineType().name().contains("WS")) 
							)){
		    	res.setOriginalLevelValue(value);
				res.setLevel(line.getLineType());
				res.setTouched(true);
				res.setUp(true);
				res.setValue(value-0.0001*breachingPips);
				touched = true;
				//System.out.println("TOCADO INTERESA QUE SUBA NIVEL : "+line.getLineType().name());
			}
			if (high>=breachingPips && high1<breachingPips
					&& (    //line.getLineType().name().contains("FIBR2") //|| 
							line.getLineType().name().contains("FIBR4")
							//|| line.getLineType().name().contains("FIBR2")
							//line.getLineType().name().contains("FIBR3")
							//|| line.getLineType().name().contains("FIBR4")
							//|| line.getLineType().name().contains("FIBR5")
							//line.getLineType().name().contains("WR")) 
					)){
				res.setOriginalLevelValue(value);
				res.setLevel(line.getLineType());
				res.setTouched(true);
				res.setUp(false);
				res.setValue(value+0.0001*breachingPips);
				touched = true;
				//System.out.println("TOCADO INTERESA QUE BAJE NIVEL : "+line.getLineType().name());
			}
			if (touched){
				return res;
			}
		}
		return res;
	}

	public static void addToArry(ArrayList<LevelResult> array,ArrayList<LevelResult> toAdd){
		for (int i=0;i<toAdd.size();i++){
			LevelResult level = new LevelResult();
			level.copy(toAdd.get(i));
			array.add(level);
		}
	}
	
	public static void addToPriceResultArray(ArrayList<PriceTestResult> array,ArrayList<PriceTestResult> toAdd){
		for (int i=0;i<toAdd.size();i++){
			PriceTestResult res = new PriceTestResult();
			res.copy(toAdd.get(i));
			array.add(res);
		}
	}
	
	public static ArrayList<PriceTestResult> getResults(ArrayList<Quote> data,ArrayList<Quote> data5m, ArrayList<PhilDay> philDays,
			Calendar from,Calendar to,int dL,int dH,int hl,int hh,int minRange,LevelTestConfig levelConfig){
	
		ArrayList<PriceTestResult> results = new ArrayList<PriceTestResult>();
		
		int breachingPips = levelConfig.getBreachingPips();
		ArrayList<LineType> touchedLevels = new ArrayList<LineType>();
		int totalW = 0;
		int totalL = 0;
		int totalWW =0;
		int totalLL=0;
		int totalLW=0;
		//Para cada día testeamos
		boolean lastRes = true;
		for (int i=0;i<=philDays.size()-1;i++){
			//System.out.println("dia "+i);
			PhilDay pDay = philDays.get(i);
			Calendar actualDate = pDay.getDay();
			int dayW = actualDate.get(Calendar.DAY_OF_WEEK);
			if (dayW<dL || dayW>dH) continue;
			if (actualDate.getTimeInMillis()<from.getTimeInMillis() || actualDate.getTimeInMillis()>to.getTimeInMillis()) continue;
			//System.out.println("dia "+i+" "+DateUtils.datePrint(actualDate.getTime()));
			//obtenemos las lineas
			ArrayList<PhilLine> lines = pDay.getLines(); //niveles para este dia

			//Obtenemos los datos de este PDay
			int index = TradingUtils.getDayIndex(data,pDay.getDay());
			ArrayList<Quote> newDayData = TradingUtils.getDayData(data,pDay.getDay()); //datos del dia

			Calendar cal = Calendar.getInstance();
			boolean breached = false;
			
			touchedLevels.clear();
			double min = 99999;
			double max = 0;
			for (int j=1;j<newDayData.size();j++){ //para cada dato de este dia
				Quote q1   = newDayData.get(j-1);
				Quote q   = newDayData.get(j);
				
				if (q.getLow()<min) min = q.getLow();
				if (q.getHigh()>max) max = q.getHigh();
				
				int range = TradingUtils.getPipsDiff(max, min);
				
				if (range<minRange) continue;
				
				//System.out.println("q y q5m: "+DateUtils.datePrint(q.getDate())+" "+DateUtils.datePrint(q5m.getDate()));
				cal.setTime(q.getDate());
				int h = cal.get(Calendar.HOUR_OF_DAY);
				if (h<hl || h>hh) continue;
		
				LevelTouchResult lineTouched = getTouchedLine(lines,q,q1,breachingPips);//evaluar cada linea por si es tocada
				
				int mode = 1; //interesa que subaSSS
				double beginValue  = lineTouched.getValue();
				double targetValue = beginValue+0.0001*levelConfig.getBePips();
				double stopLoss    = beginValue-0.0001*levelConfig.getMaxPips();
				if (!lineTouched.isUp()){
					mode = 0; //interesa que baje
					targetValue = beginValue-0.0001*levelConfig.getBePips();
					stopLoss	= beginValue+0.0001*levelConfig.getMaxPips();					
				}else{
					
				}
				
				breached = lineTouched.isTouched();
							
				if (breached && !inTouchedGroup(touchedLevels,lineTouched.getLevel())					
						){//sólo la primera del día		
					int begin = index+j+1;
					PriceTestResult res = TradingUtils.testPriceMovement(data,begin,data.size()-1,beginValue,stopLoss,targetValue,mode);
					Calendar priceCal = Calendar.getInstance();
					priceCal.setTime(cal.getTime());
					res.setCal(priceCal);
					touchedLevels.add(lineTouched.getLevel());
					results.add(res);
					/*System.out.println(DateUtils.datePrint(cal)
							+" "+lineTouched.getLevel().name()
							+" vl= "+PrintUtils.Print4dec(lineTouched.getOriginalLevelValue())
							+" "+PrintUtils.Print4dec(beginValue)
							+" "+PrintUtils.Print4dec(stopLoss)
							+" "+PrintUtils.Print4dec(targetValue)
							+" "+res.isWin()
							);*/
					/*if (!res.isWin()){
						if (lastRes==false){
							totalLL++;							
						}
						//System.out.println("LL % "+PrintUtils.Print2dec(totalLL*100.0/totalL, false));
						lastRes = false;
						totalL++;
					}else{
						if (lastRes==false){
							totalLW++;							
						}
						//System.out.println("LW % "+PrintUtils.Print2dec(totalLW*100.0/totalW, false));
						lastRes = true;
						totalW++;
					}*/
					
					//System.out.println("DATE LEVEL RESULT: "+lineTouched.getLevel().name()+" "+DateUtils.datePrint(cal)+" "+res.isWin());					
				}//end breached*/
			}//end for daily
		}//end phil days
		
		
		return results;
	}
	
	

	private static Quote findQuote(ArrayList<Quote> data5m, Quote q) {
		// TODO Auto-generated method stub
		Calendar toFind = Calendar.getInstance();
		toFind.setTime(q.getDate());
		Calendar actual = Calendar.getInstance();
		//System.out.println("tofinde: "+DateUtils.datePrint(q.getDate()));
		for (int i=0;i<data5m.size();i++){
			Quote qi = data5m.get(i);
			actual.setTime(qi.getDate());
			//if (actual.getTimeInMillis()<toFind.getTimeInMillis()) continue;
			if (actual.get(Calendar.YEAR)==toFind.get(Calendar.YEAR)
				&& actual.get(Calendar.MONTH)==toFind.get(Calendar.MONTH)	
				&& actual.get(Calendar.DAY_OF_MONTH)==toFind.get(Calendar.DAY_OF_MONTH)
				&& actual.get(Calendar.HOUR_OF_DAY)==toFind.get(Calendar.HOUR_OF_DAY)
				){
				//System.out.println("coincide: "+DateUtils.datePrint(qi.getDate()));
				int minDiff = toFind.get(Calendar.MINUTE)-actual.get(Calendar.MINUTE);
				if (minDiff>=0 && minDiff<=4) return qi;
			}
		}
		return null;
	}

	private static boolean inTouchedGroup(ArrayList<LineType> touchedLevels,
			LineType level) {
		// TODO Auto-generated method stub
		for (int i=0;i<touchedLevels.size();i++){
			if (touchedLevels.get(i)==level) return true;
		}
		return false;
	}
	
	private static void winLossesStudy(String header, ArrayList<PriceTestResult> array){
		
		ArrayList<Integer> dayResult = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int lastDayRes = -1;
		for (int i=0;i<array.size();i++){
			PriceTestResult res = array.get(i);
			cal.setTime(res.getCal().getTime());
			int day = cal.get(Calendar.DAY_OF_YEAR);
			//System.out.println("day "+day);
			if (day!=lastDay){
				if (lastDay!=-1)
					dayResult.add(lastDayRes);
				lastDayRes = 1;
				lastDay = day;
			}
			if (!res.isWin()){
				lastDayRes = 0;
			}
		}
		//System.out.println("array: "+array.size()+" "+dayResult.size());
		
		int count3=0;
		int count2=0;
		int ll=0;
		int lll=0;
		for (int i=0;i<dayResult.size();i++){
			if (i>=1){
				if (dayResult.get(i-1)==0 && dayResult.get(i)==0){
					ll++;
				}
				count2++;
			}
			if (i>=2){
				if (dayResult.get(i-2)==0 && dayResult.get(i-1)==0 && dayResult.get(i)==0){
					lll++;
				}
				count3++;
			}
		}
		System.out.println(header+" "+PrintUtils.Print2dec(ll*100.0/count2++, false)+"%"
				+" "+PrintUtils.Print2dec(lll*100.0/count3++, false)+"%"
				);
	}
	
	private static void printStatsLevels(String header, int breachingPips, int bePips, int maxPips, ArrayList<PriceTestResult> array) {
		// TODO Auto-generated method stub
		int wins = 0;
		int losses = 0;
		int ll=0;
		int lw=0;
		int lll=0;
		int llll=0;
		int lllll=0;
		int count2=0;
		int count3=0;
		int count4=0;
		int count5=0;
		boolean lastRes = true;
		String winLossStr="";
		for (int i=0;i<array.size();i++){
			PriceTestResult res = array.get(i);
			if (res.isWin()){
				wins++;
				winLossStr+='W';
			}else{			
				losses++;
				winLossStr+='L';
			}
			if (i>=1){
				if (!array.get(i-1).isWin() ){
					if (!array.get(i).isWin()){
						ll++;
					}
				}
				count2++;
			}
			if (i>=2){
				if (!array.get(i-1).isWin() && !array.get(i-2).isWin()){
					if (!array.get(i).isWin()){
						lll++;
					}					
				}
				count3++;
			}
			if (i>=3){
				if (!array.get(i-1).isWin() && !array.get(i-2).isWin() && !array.get(i-3).isWin()){
					if (!array.get(i).isWin()){
						llll++;
					}					
				}
				count4++;
			}
			if (i>=4){
				if (!array.get(i-1).isWin() && !array.get(i-2).isWin() && !array.get(i-3).isWin()
						&& !array.get(i-4).isWin()){
					if (!array.get(i).isWin()){
						lllll++;
					}					
				}
				count5++;
			}
		}
		double winPer = wins*100.0/(wins+losses);
		double lossPer = 100.0-winPer;
		double me = winPer*bePips*1.0-lossPer*maxPips;
		System.out.println(header+" win %: "+(wins+losses)
				+" "+PrintUtils.Print2dec(wins*100.0/(wins+losses), false)+"%"
				+" "+losses
				+" "+PrintUtils.Print2dec(me/(100.0), false)
				+" "+PrintUtils.Print2dec(ll*100.0/count2++, false)+"%"
				+" "+PrintUtils.Print2dec(lll*100.0/count3++, false)+"%"
				+" "+PrintUtils.Print2dec(llll*100.0/count4++, false)+"%"
				+" "+PrintUtils.Print2dec(lllll*100.0/count5++, false)+"%"
				+" "+winLossStr
				);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				String path   = "c:\\fxdata";
				String file1H = path+"\\"+"EURUSD_Hourly_Bid_2003.05.04_2014.02.07.csv";
				String file5m = path+"\\"+"EURUSD_5 Mins_Bid_2003.05.04_2014.01.07.csv";
				
				ArrayList<Quote> dataI 			= DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
		  		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
		  		ArrayList<Quote> data5m 			= TradingUtils.cleanWeekendData(dataS);
		  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data5m);
				ArrayList<Quote> weeklyData = ConvertLib.createWeeklyData(data5m);
				ArrayList<Quote> monthlyData = ConvertLib.createMonthlyData(data5m);
		  		ArrayList<PhilDay> pDays = TradingUtils.calculateLines(data5m, dailyData, weeklyData, monthlyData);
				//System.out.println("Initial data size y cleaned philDays: "+dataI.size()+" "+data5m.size()+" "+philDays.size());
				
				System.out.println("data5m: "+data5m.size());
				

				//String symbol="EURUSD";
				String symbol    	="EURUSD";
				int yearF      	 	= 2013;
				int monthF 			= Calendar.JANUARY;
				int yearL  			= 2013;
				int monthL 			= Calendar.DECEMBER;
				int dL 				= Calendar.MONDAY+0;
				int dH 				= Calendar.MONDAY+4;
				int h1              = 0;
				int h2              = 11; 
				LevelTestConfig levelConfig = new LevelTestConfig();
				
				Calendar from = Calendar.getInstance();
				Calendar to   = Calendar.getInstance();
				ArrayList<Quote>   data  = new ArrayList<Quote>();
				ArrayList<PriceTestResult> resultsArray = new ArrayList<PriceTestResult>();
				//ArrayList<PhilDay> pDays = StudyRangeFibs.calculateFIBS(file1H,3,100);
				//ArrayList<PhilDay> pDays = StudyRangeFibs.calculateFIBS(file1H,3,100);
				System.out.println("FIBS calculados");
				int maxPips = 30;
				int bePips = 5;
				int breachingPips = 5;
				int year = 2013;
				Calendar from2 = Calendar.getInstance();
				Calendar to2 = Calendar.getInstance();
				for (maxPips=50;maxPips<=100;maxPips+=10){
					for (breachingPips=0;breachingPips<=0;breachingPips+=1){
						for (int minRange=0;minRange<=0;minRange+=10){
							for (h1= 0;h1<=0;h1+=1){
								h2 = h1+23;
								levelConfig.setBePips(bePips);
								levelConfig.setBreachingPips(breachingPips);
								levelConfig.setMaxPips(maxPips);
								resultsArray.clear();
								from.set(year, 0, 1);
								to.set(year, 11, 13);
								while (from.getTimeInMillis()<=to.getTimeInMillis()){
									int actualM = from.get(Calendar.MONTH);
									int actualY = from.get(Calendar.YEAR);
															
									String fileData  = TradingUtils.getFileData(symbol,actualM,actualY);
													
									from2.set(actualY, actualM, 1);
									to2.set(actualY, actualM, 31);
																
									File file = new File(path+"\\"+fileData);
									if (file.exists()){											
										DAO.retrieveData(data,path+"\\"+fileData, DataProvider.DUKASCOPY_FOREX,0);
										ArrayList<PriceTestResult> results = StudyLevels.getResults(data,data5m, pDays, from2, to2, dL, dH, h1, h2,minRange,levelConfig); 			
										StudyLevels.addToPriceResultArray(resultsArray, results);
									}										
									from.add(Calendar.MONTH, 1);
								}//while
								String header =
										" year= "+year										
										+" dl= "+dL+" dh= "+dH
										+" hl= "+h1+" hh= "+h2
										+" breachingPips= "+breachingPips
										+" bePips= "+bePips
										+" maxPips= "+maxPips+" minRange= "+minRange;
								printStatsLevels(header,breachingPips,bePips,maxPips,resultsArray);
								//winLossesStudy("", resultsArray);
							}
						}//minrange
					}
				}
	}
}
