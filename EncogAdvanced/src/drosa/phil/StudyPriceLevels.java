package drosa.phil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyPriceLevels {

	private static void contsAnalysis(ArrayList<Quote> conts,int perInt,double per,int tp){
		int acc = 0;
		
		for (int i=0; i<conts.size();i++){
			Quote q = conts.get(i);
			String specStr = "WIN";
			if (q.getOpen()<-100)
				specStr = "LOSS";
			//System.out.println("[SPECIAL] "+specStr+" "+PrintUtils.Print(q.getHigh())
			//		+" "+PrintUtils.Print(q.getLow()));
			if (q.getLow()<=0){
				acc+=1;
			}else{
				acc+=q.getLow();
			}			
		}	
		double avg = acc*1.0/conts.size();
		double againstAmount = (100.0-per)*perInt;
		System.out.println(tp+" avg: "+PrintUtils.Print(acc*1.0/conts.size())
				+" "+PrintUtils.Print(avg*per-againstAmount));
	}
	
	private static double breachingAnalysis(ArrayList<Quote> breachings,String header,int bePips,boolean traceEnabled){
		int wins = 0;
		int losses = 0;
		int maeAcc=0;
		int less_5=0;
		int less_10=0;
		int less_15=0;
		int less_20=0;
		int less_30=0;
		int less_40=0;
		int less_50=0;
		int less_60=0;
		int less_70=0;
		int less_80=0;
		for (int i=0;i<breachings.size();i++){
			Quote q = breachings.get(i);
			int mae = (int)q.getHigh();
			if (q.getOpen()>100){
				if (traceEnabled)
					System.out.println("WIN maxAdv: "+(int)q.getHigh());
				wins++;
				maeAcc+=(int)q.getHigh();
				if (mae<5){ less_5++;less_10++;less_15++;less_20++;less_30++;less_40++;less_50++;less_60++;less_70++;less_80++;}
				else if (mae<10){ less_10++;less_15++;less_20++;less_30++;less_40++;less_50++;less_60++;less_70++;less_80++;}
				else if (mae<15){ less_15++;less_20++;less_30++;less_40++;less_50++;less_60++;less_70++;less_80++;}
				else if (mae<20){ less_20++;less_30++;less_40++;less_50++;less_60++;less_70++;less_80++;}
				else if (mae<30){ less_30++;less_40++;less_50++;less_60++;less_70++;less_80++;}
				else if (mae<40){ less_40++;less_50++;less_60++;less_70++;less_80++;}
				else if (mae<50){ less_50++;less_60++;less_70++;less_80++;}
				else if (mae<60){ less_60++;less_70++;less_80++;}
				else if (mae<70){ less_70++;less_80++;}
				else if (mae<80){ less_80++;}
			}else if (q.getOpen()<-100){	
				if (traceEnabled)
					System.out.println("LOSS maxAdv: "+(int)q.getHigh());
				losses++;
			}
		}
		double winPer = wins*100.0/breachings.size();
		double avg    = maeAcc*1.0/wins;
		int totalData = breachings.size();
		System.out.println("winPer avg "
				+" "+header
				+" "+PrintUtils.Print(winPer)
				+" "+PrintUtils.Print(avg)
				+" "+PrintUtils.Print(avg/Integer.valueOf(bePips))
				+" || "+PrintUtils.Print(less_5*100.0/totalData)
				+" "+PrintUtils.Print(less_10*100.0/totalData)
				+" "+PrintUtils.Print(less_15*100.0/totalData)
				+" "+PrintUtils.Print(less_20*100.0/totalData)
				+" "+PrintUtils.Print(less_30*100.0/totalData)
				+" "+PrintUtils.Print(less_40*100.0/totalData)
				+" "+PrintUtils.Print(less_50*100.0/totalData)
				+" "+PrintUtils.Print(less_60*100.0/totalData)
				+" "+PrintUtils.Print(less_70*100.0/totalData)
				+" "+PrintUtils.Print(less_80*100.0/totalData)
				);
		return less_80*100.0/totalData;		
	}
	
	private static void breachingAnalysis2(ArrayList<Quote> breachings,String header,int bePips,int maxPips,
			boolean traceEnabled,boolean printLossDates){
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
		double avgPips = (totalLess*bePips-losses*maxPips)*1.0/(totalLess+losses);
		System.out.println(
				/*"maxPips total winPer avg  totalLessMax"
				+" "+header
				+" "+maxPips
				+" "+breachings.size()
				+" "+PrintUtils.Print(winPer)
				+" "+PrintUtils.Print(avg)
				+" "+PrintUtils.Print(perLessMax)+'%'
				+" "+PrintUtils.Print(bePips*perLessMax-(100.0-perLessMax)*80.0)
				+" "+*/
				header
				+" "+PrintUtils.PrintInt(maxPips)
				+" "+PrintUtils.PrintInt(totalLess)
				+" "+PrintUtils.PrintInt(losses)
				+" "+PrintUtils.Print(avgPips)
				+" "+PrintUtils.Print(perLessMax)+'%'
				+" "+streaks
				
				);
	
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
			Calendar from,Calendar to,int hl,int hh,LineType testLevel,
			int breachingPips, int targetPips,int contTP){
		
		BreachingStudy breachingStudy = new BreachingStudy();
		ArrayList<Quote> breachings = new ArrayList<Quote>();
		ArrayList<Quote> continuations = new ArrayList<Quote>();
		//Para cada día testeamos
		for (int i=0;i<=philDays.size()-1;i++){
			//System.out.println("dia "+i);
			PhilDay pDay = philDays.get(i);
			Calendar actualDate = pDay.getDay();
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
			
			if (LINE<DO) continue;
			
			//Obtenemos los datos de este PDay
			int index = TradingUtils.getDayIndex(data,pDay.getDay());
			ArrayList<Quote> newDayData = TradingUtils.getDayData(data,pDay.getDay());

			Calendar cal = Calendar.getInstance();
			int dailyBreachings=0;
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
					int breachIndex = qBreach.getIndex();
					qBreach.setDate(q.getDate());
					Quote qSpecial = null;
					/*if (qBreach.getOpen()>100){
						
						double initialValue = data.get(breachIndex).getLow();
						double limitValue = beginValue-0.0001;
						qSpecial = TradingUtils.testPriceContinuation(data,breachIndex+1,lastQuoteIdx,initialValue,limitValue,contTP);
						continuations.add(qSpecial);
					}*/
					
					qBreach.setIndex(begin);
					breachings.add(qBreach);
					
					break;//salimos del for
				}//end breached
			}//end for daily
		}//end phil days
		breachingStudy.setBreachings(breachings);
		breachingStudy.setContinuations(continuations);
		return breachingStudy;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String forexPath = "C:\\fxdata";
		
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		int year = 2013;
		int pipsBreaching = 8;
		int bePips = 6;
		int maxAdversePips = 40;
		LineType lineType = LineType.FIBR4;
		//String symbol="EURUSD";
		String symbol="EURUSD";
		int yearF  = 2004;
		int monthF = Calendar.JANUARY;
		int yearL  = 2004;
		//int monthL = Calendar.AUGUST;
		int monthL = Calendar.DECEMBER;
		
		if (args.length>0){
			monthF = Integer.valueOf(args[0]);
			yearF  = Integer.valueOf(args[1]);
			monthL = Integer.valueOf(args[2]);
			yearL  = Integer.valueOf(args[3]);
		}
		
		ArrayList<LineType> lines = new ArrayList<LineType>();
		LineType l1 = LineType.FIBR1;lines.add(l1);
		LineType l2 = LineType.FIBR2;lines.add(l2);
		LineType l3 = LineType.FIBR3;lines.add(l3);
		LineType l4 = LineType.FIBR4;lines.add(l4);
		LineType l5 = LineType.FIBR5;lines.add(l5);
		LineType l6 = LineType.FIBS1;lines.add(l6);
		LineType l7 = LineType.FIBS2;lines.add(l7);
		LineType l8 = LineType.FIBS3;lines.add(l8);
		LineType l9 = LineType.FIBS4;lines.add(l9);
		LineType l10 = LineType.FIBS5;lines.add(l10);
		
		for (int l=0;l<lines.size();l++){
			lineType = lines.get(l);
			System.out.println(symbol+" "+lineType+" FROM TO "+ monthF+"-"+yearF+" to "+monthL+"-"+yearL);
			ArrayList<Quote>   data  = new ArrayList<Quote>();
			ArrayList<PhilDay> pDays = new ArrayList<PhilDay>();
			ArrayList<Quote> allBreachings= new ArrayList<Quote>();
			ArrayList<Quote> allContinuations= new ArrayList<Quote>();
			for (pipsBreaching=0;pipsBreaching<=0;pipsBreaching+=1){
				//int pipsBreachingH = pipsBreachingL+5;
				for (int maxPips=80;maxPips<=80;maxPips+=10)
				for (int contTP=5;contTP>=5;contTP-=5)
				//for (int hl=0;hl<=19;hl++)
				for (bePips=10;bePips>=10;bePips+=-1){
						//maxAdversePips =(int) (bePips*1);
						allBreachings.clear();
						from.set(yearF, monthF, 1);
						to.set(yearL, monthL, 31);
						Calendar from2 = Calendar.getInstance();
						Calendar to2 = Calendar.getInstance();
						while (from.getTimeInMillis()<=to.getTimeInMillis()){
							int actualM = from.get(Calendar.MONTH);
							int actualY = from.get(Calendar.YEAR);
						
							String fileData  = TradingUtils.getFileData(symbol,actualM,actualY);
							String fileLines = TradingUtils.getFileLines(symbol,actualM,actualY);
				
							from2.set(actualY, actualM, 1);
							to2.set(actualY, actualM, 31);
							
							File file = new File(forexPath+"\\"+fileData);
							if (file.exists()){
								DAO.retrieveData(data,forexPath+"\\"+fileData, DataProvider.DUKASCOPY_FOREX,0);
								PhilDay.loadFromFile(pDays,forexPath+"\\"+fileLines);
								
								//System.out.println("data: "+data.size());
								//System.out.println("pDays: "+pDays.size());
								BreachingStudy breachingStudy = StudyPriceLevels.testMaxRetrace(data, pDays, from2, to2, 0, 23, lineType,pipsBreaching,bePips,contTP);
								addtoArray(allBreachings,breachingStudy.breachings);
							}
							//addtoArray(allContinuations,breachingStudy.continuations);
							from.add(Calendar.MONTH, 1);
						}
						String header = PrintUtils.PrintInt(pipsBreaching)+" "+PrintUtils.PrintInt(bePips);
						breachingAnalysis2(allBreachings,header,bePips,maxPips,false,false);
						//contsAnalysis(allContinuations,80,per80,contTP);
				}	
			}
		}
	}

	

}
