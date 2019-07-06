package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class LevelsTest {

	public static String completeNumber(String num){
        String com=num;
        
        if (com.length()==4)
            com+="0";
        if (com.length()==3)
            com+="00";
        if (com.length()==2)
            com+="000";
        if (com.length()==1)
            com+="0000";
        return com;
    }
	
	public static int getPipsDiff(double val1,double val2){
        String val1Str = PrintUtils.Print(val1);
        String val2Str = PrintUtils.Print(val2);
        val1Str = completeNumber(val1Str.substring(0, 1)+val1Str.substring(2, val1Str.length()));
        val2Str = completeNumber(val2Str.substring(0, 1)+val2Str.substring(2, val2Str.length()));
        
        //System.out.println("[getPipsDiff] "+val1Str+" "+val2Str);
        int diff =Integer.valueOf(val1Str)-Integer.valueOf(val2Str);
        return diff;
    }
	
	public static Quote calculateMaxRetrace(ArrayList<Quote> data, int begin,double refValue,int maxPipsNeg){
		Quote qRes = new Quote();
		qRes.setClose(0);
		int end= data.size()-1;	
		double maxNeg=-1;
		double maxPos=99999;
		double maxNegValue = refValue+maxPipsNeg*0.0001;		

		for (int i=begin;i<=end;i++){
			Quote q = data.get(i);
			if (q.getHigh()>maxNeg)
				maxNeg = q.getHigh();
			if (q.getLow()<maxPos)
				maxPos = q.getLow();
			qRes.setHigh(maxNeg);
			qRes.setLow(maxPos);
			if (maxNeg>maxNegValue){
				//System.out.println("[calculateMaxRetrace] refValue maxNegValue: "+PrintUtils.Print( refValue)+" "+PrintUtils.Print(maxNegValue)
				//		+" "+PrintUtils.Print((refValue-maxPos)/0.0001)+" "+PrintUtils.Print((refValue-maxNeg)/0.0001));
				qRes.setClose(-1);
				return qRes;
			}
			
		}
		//System.out.println("maxPips: "+maxPips);
		return qRes;
	}
	
	public static ArrayList<Integer> testLineRetracement(ArrayList<Quote> data, ArrayList<PhilDay> philDays,LineType testLevel,
			int pipsAwayL,int pipsAwayH,int pipsDiff,int pipsLimit){
		
		ArrayList<Integer> retracements = new ArrayList<Integer>();
		int totalBreachings=0;
		int totalWins=0;
		int totalLosses=0;
		for (int i=0;i<philDays.size()-1;i++){//Para cada día
			PhilDay pDay = philDays.get(i);
			PhilDay nextDay = philDays.get(i+1);
			ArrayList<PhilLine> lines = pDay.getLines();
			int index0 = pDay.getIndex();
			int index1 = nextDay.getIndex()-1;
			double LINE=-1;
			
			for (int j=0;j<lines.size();j++){
				if (lines.get(j).getLineType()==testLevel)
					LINE = lines.get(j).getValue();
			}
						
			boolean breached = false;
			double tradeRef=-1;
			
			Calendar cal = Calendar.getInstance();
			for (int j=index0;j<=index1;j++){//para cada quote de ese dia		
				Quote q = data.get(j);
				cal.setTime(q.getDate());
				int h = cal.get(Calendar.HOUR_OF_DAY);
				int dayWeek=cal.get(Calendar.DAY_OF_WEEK);				
			
				if (q.getOpen()<LINE			
						//&& q.getClose()<LINE		
						&& getPipsDiff(q.getHigh(),LINE)>=pipsAwayL 
						&& getPipsDiff(q.getHigh(),LINE)<=pipsAwayH 
						//&& q.getHigh()-q.getClose()<=0.0005
						//&& dayWeek>=dayL && dayWeek<=dayH
						//&& h>=hl && h<=hh
				){			
					tradeRef=q.getHigh()-0.0001*pipsDiff;
					/*System.out.println(DateUtils.datePrint(q.getDate())
							+" "+PrintUtils.Print(LINE)
							+" "+PrintUtils.Print(q.getHigh())
							+" "+PrintUtils.Print(tradeRef)
							+" "+getPipsDiff(q.getHigh(),LINE));
					*/
					//tradeRef = q.getClose();						
					//if (tradeRef>=LINE+0.0001*pipsAwayL && tradeRef<=LINE+0.0001*pipsAwayH){	
						totalBreachings++;
						//System.out.println("VALID "+totalBreachings);
						
						if (tradeRef>=q.getLow() && tradeRef>=LINE){
							Quote qBreach = calculateMaxRetrace(data,j+1,tradeRef,pipsLimit);
							int posPips =  getPipsDiff(tradeRef,qBreach.getLow());
							int negPips =  getPipsDiff (qBreach.getHigh(),tradeRef);
							totalBreachings++;
							if (negPips>posPips)
								totalWins++;
							else totalLosses++;
							retracements.add(posPips);
							//totalAvgPips+=posPips;
							//breached = true;
						}
					//}
					//break;
				}//breached
			}//for day						
		}//for philDay
		
		
		return retracements;
	}
	
	private static ArrayList<Quote> cleanData(ArrayList<Quote> dataS) {
		// TODO Auto-generated method stub
		ArrayList<Quote> data = new ArrayList<Quote>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<dataS.size();i++){
			Quote q = dataS.get(i);
			cal.setTime(q.getDate());
			if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
				continue;
			Quote qNew = new Quote();
			qNew.copy(q);
			data.add(q);
		}
		
		return data;
	}
	
	public static ArrayList<PhilDay> calculateLines(ArrayList<Quote> data,ArrayList<Quote> dailyData,
			ArrayList<Quote> weeklyData,ArrayList<Quote> monthlyData){
	 
	 ArrayList<PhilDay> philDays = new ArrayList<PhilDay>();
	 
	 Calendar qCal = Calendar.getInstance();
	 int beforeDay = -1;
	 //POINTS
	 double DO = -1;
	 double DP = -1;double DR1=-1;double DR2=-1;double DR3=-1;double DS1=-1;double DS2=-1;double DS3=-1;
	 double WP = -1;double WR1=-1;double WR2=-1;double WR3=-1;double WS1=-1;double WS2=-1;double WS3=-1;
	 double MP = -1;double MR1=-1;double MR2=-1;double MR3=-1;double MS1=-1;double MS2=-1;double MS3=-1;
	 double FIBR1=-1;double FIBS1=-1;double FIBR2=-1;double FIBS2=-1;double FIBR3=-1;double FIBS3=-1;
	 double FIBR4=-1;double FIBS4=-1;double FIBR5=-1;double FIBS5=-1;
	 double lastHighD = -1;double lastLowD = -1;double lastCloseD = -1;
	 double lastHighW = -1;double lastLowW = -1;double lastCloseW = -1;
	 double lastHighM = -1;double lastLowM = -1;double lastCloseM = -1;
	
	 int lastDay=-1;
	 int lastWeek=-1;
	 int lastMonth=-1;
	 for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			qCal.setTime(q.getDate());
			int actualDay   = qCal.get(Calendar.DAY_OF_YEAR);
			int actualMonth = qCal.get(Calendar.MONTH);
			int actualWeek  = qCal.get(Calendar.WEEK_OF_YEAR);
			int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
			
			if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
            	continue;
            }
			
			double range = -1;
			if (actualDay!=beforeDay){
				//int lastDay   =  DateUtils.findLastDateIndex(dailyData,q,1);
				//int lastWeek  =  DateUtils.findLastDateIndex(weeklyData,q,2);
				//int lastMonth =  DateUtils.findLastDateIndex(monthlyData,q,3);
				//double range = getAverageRange(dailyData,lastDay);
				ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
				if (lastDay!=-1){
					lastHighD = dailyData.get(lastDay).getHigh();
                    lastLowD = dailyData.get(lastDay).getLow();
                    lastCloseD = dailyData.get(lastDay).getClose();
                    DO = q.getOpen();
                    range = TestLines.getAverageRange(dailyData,lastDay);                                       					
					beforeDay = actualDay;					
					/*System.out.println("**NEW Day DO: "+DateUtils.datePrint(q.getDate())
							+" "+PrintUtils.Print(DO)
					);*/
					DP = ( lastHighD + lastLowD + lastCloseD ) / 3;
					DR1 = ( 2 * DP ) - lastLowD;
                    DS1 = ( 2 * DP ) - lastHighD;
                    DR2 = DP + ( lastHighD - lastLowD );
                    DS2 = DP - ( lastHighD - lastLowD );
                    DR3 = ( 2 * DP ) + ( lastHighD - ( 2 * lastLowD ) );
                    DS3 = ( 2 * DP ) - ( ( 2 * lastHighD ) - lastLowD );
                    lines.add(PhilLine.createLine(LineType.DO, DO));//0
                    lines.add(PhilLine.createLine(LineType.DP, DP));//1
                    lines.add(PhilLine.createLine(LineType.DR1, DR1));//2
                    lines.add(PhilLine.createLine(LineType.DS1, DS1));//3
                    lines.add(PhilLine.createLine(LineType.DR2, DR2));//4
                    lines.add(PhilLine.createLine(LineType.DS2, DS2));//5
                    lines.add(PhilLine.createLine(LineType.DR3, DR3));//6
                    lines.add(PhilLine.createLine(LineType.DS3, DS3));//7
				}
				if (lastWeek!=-1){
					lastHighW = dailyData.get(lastWeek).getHigh();
                    lastLowW = dailyData.get(lastWeek).getLow();
                    lastCloseW = dailyData.get(lastWeek).getClose();
                    WP = ( lastHighW + lastLowW + lastCloseW ) / 3;
                    WR1 = ( 2 * WP ) - lastLowW;
                    WS1 = ( 2 * WP ) - lastHighW;
                    WR2 = WP + ( lastHighW - lastLowW );
                    WS2 = WP - ( lastHighW - lastLowW );
                    WR3 = ( 2 * WP ) + ( lastHighW - ( 2 * lastLowW ) );
                    WS3 = ( 2 * WP ) - ( ( 2 * lastHighW ) - lastLowW );
                   
                    lines.add(PhilLine.createLine(LineType.WP, WP));//8
                    lines.add(PhilLine.createLine(LineType.WR1, WR1));//9
                    lines.add(PhilLine.createLine(LineType.WS1, WS1));//10
                    lines.add(PhilLine.createLine(LineType.WR2, WR2));//11
                    lines.add(PhilLine.createLine(LineType.WS2, WS2));//12
                    lines.add(PhilLine.createLine(LineType.WR3, WR3));//13
                    lines.add(PhilLine.createLine(LineType.WS3, WS3));//14
				}
				if (lastMonth!=-1){
					lastHighM = dailyData.get(lastMonth).getHigh();
                    lastLowM = dailyData.get(lastMonth).getLow();
                    lastCloseM = dailyData.get(lastMonth).getClose();
                    MP = ( lastHighM + lastLowM + lastCloseM ) / 3;
                    MR1 = ( 2 * MP ) - lastLowM;
                    MS1 = ( 2 * MP ) - lastHighM;
                    MR2 = MP + ( lastHighM - lastLowM );
                    MS2 = MP - ( lastHighM - lastLowM );
                    MR3 = ( 2 * MP ) + ( lastHighM - ( 2 * lastLowM ) );
                    MS3 = ( 2 * MP ) - ( ( 2 * lastHighM ) - lastLowM );
                                    
                    lines.add(PhilLine.createLine(LineType.MP, MP));//15
                    lines.add(PhilLine.createLine(LineType.MR1, MR1));//16
                    lines.add(PhilLine.createLine(LineType.MS1, MS1));//17
                    lines.add(PhilLine.createLine(LineType.MR2, MR2));//18
                    lines.add(PhilLine.createLine(LineType.MS2, MS2));//19
                    lines.add(PhilLine.createLine(LineType.MR3, MR3));//20
                    lines.add(PhilLine.createLine(LineType.MS3, MS3));//21
				}
				
				FIBR1 = DO + ( range * 0.382 );
	            FIBS1 = DO - ( range * 0.382 );
	            FIBR2 = DO + ( range * 0.618 );
	            FIBS2 = DO - ( range * 0.618 );
	            FIBR3 = DO + ( range * 0.764 );
	            FIBS3 = DO - ( range * 0.764 );
	            FIBR4 = DO + ( range * 1.000 );
	            FIBS4 = DO - ( range * 1.000 );
	            FIBR5 = DO + ( range * 1.382 );
	            FIBS5 = DO - ( range * 1.382 );
	            lines.add(PhilLine.createLine(LineType.FIBR1, FIBR1));//22
	            lines.add(PhilLine.createLine(LineType.FIBR2, FIBR2));//23          
                lines.add(PhilLine.createLine(LineType.FIBR3, FIBR3));//24
                lines.add(PhilLine.createLine(LineType.FIBR4, FIBR4));//25
                lines.add(PhilLine.createLine(LineType.FIBR5, FIBR5));//26
                lines.add(PhilLine.createLine(LineType.FIBS1, FIBS1));//27
	            lines.add(PhilLine.createLine(LineType.FIBS2, FIBS2));//28          
                lines.add(PhilLine.createLine(LineType.FIBS3, FIBS3));//29
                lines.add(PhilLine.createLine(LineType.FIBS4, FIBS4));//30
                lines.add(PhilLine.createLine(LineType.FIBS5, FIBS5));//31
				
				/*System.out.println("Testing LINE FIBS1 range "+breachedLine
						+" "+PrintUtils.Print(breachedLineValue)
						+" "+PrintUtils.Print(FIBS1)
						+" range "+PrintUtils.Print(range)
						);
				*/
                
                //add ney day
                Calendar dayCal = Calendar.getInstance();
                dayCal.setTime(q.getDate());
                PhilDay pDay = new PhilDay();
                pDay.setDay(dayCal);
                pDay.setIndex(i);
                pDay.setLines(lines);
                philDays.add(pDay);
                
				beforeDay=actualDay;
				lastDay++;
			}
	 } 
	 	return philDays;
	}
	
	public static int getTotalLess(ArrayList<Integer> array,double umbral,boolean less){
		int total=0;
		for (int i=0;i<array.size();i++){
			if (less && array.get(i)<=umbral){
				total++;
			}
			if (!less && array.get(i)>=umbral){
				total++;
			}
		}
		
		return total;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "resources/EURUSD_5 Mins_Bid_2009.01.01_2013.05.31.csv";
		
		ArrayList<Quote> dataS = null;
		ArrayList<Quote> dataI = null;
		if (fileName.equals("resources/eurusd5b.csv")){		
			dataS = DAO.retrieveData(fileName, DataProvider.PEPPERSTONE_FOREX);
		}else{
			dataI = DAO.retrieveData(fileName, DataProvider.DUKASCOPY_FOREX);
			dataS  =  TestLines.calculateCalendarAdjusted(dataI);
		}
		
		ArrayList<Quote> data = cleanData(dataS);
		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
		ArrayList<Quote> weeklyData = ConvertLib.createWeeklyData(data);
		ArrayList<Quote> monthlyData = ConvertLib.createMonthlyData(data);
		System.out.println("FILENAME: "+fileName);
		System.out.println("5min data: "+data.size());
		System.out.println("daily data: "+dailyData.size());
		System.out.println("weekly data: "+weeklyData.size());
		System.out.println("monthly data: "+monthlyData.size());
		ArrayList<PhilDay> philDays = calculateLines(data, dailyData, weeklyData, monthlyData);
		System.out.println("days: "+philDays.size());
		
		LineType testLevel = LineType.FIBR1;
	
		int pipsAwayL = 0;
		int pipsAwayH = 0;
		int pipsDiff = 3;
		int pipsLimit= 10;
		// TODO Auto-generated method stub
		for (int i=5;i<15;i++){
			pipsAwayL = i;
			pipsAwayH = pipsAwayL+5; 
			ArrayList<Integer> retracements =LevelsTest.testLineRetracement(data, philDays, testLevel, pipsAwayL, pipsAwayH, pipsDiff, pipsLimit);
			int total0 = getTotalLess(retracements,4,false);
			int total = getTotalLess(retracements,10,false);
			int total1 = getTotalLess(retracements,20,false);
			int total2 = getTotalLess(retracements,30,false);
			int total3 = getTotalLess(retracements,40,false);
			int total4 = getTotalLess(retracements,50,false);
			System.out.println(pipsAwayL+" "+pipsAwayH+" total less 4 10 20 30 40 50: "
					+" "+retracements.size()
					+" "+PrintUtils.Print(total0*100/retracements.size())+"%"
					+" "+PrintUtils.Print(total*100/retracements.size())+"%"
					+" "+PrintUtils.Print(total1*100/retracements.size())+"%"
					+" "+PrintUtils.Print(total2*100/retracements.size())+"%"
					+" "+PrintUtils.Print(total3*100/retracements.size())+"%"
					+" "+PrintUtils.Print(total4*100/retracements.size())+"%"
					);
		}
	}

}
