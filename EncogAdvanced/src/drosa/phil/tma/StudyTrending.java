package drosa.phil.tma;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.phil.IndicatorLib;
import drosa.phil.TMA;
import drosa.phil.TestLines;
import drosa.phil.TmaDiff;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyTrending {

	public static ArrayList<Integer> countBounces(ArrayList<Quote> data,ArrayList<TMA> tmas,
			Calendar from, Calendar to, int day1,int day2,int h1,int h2,int minDiff){
		
		ArrayList<Integer> bouncesArr = new ArrayList<Integer>();
		Calendar actualCal = Calendar.getInstance();
		int lastDay = -1;
		int countDays =  0;
		int totalBounces = 0;
		int dayBounces = 0;
		int lastBounce=-1;
		for (int i=0;i<data.size();i++){
			TMA tma = tmas.get(i);
			Quote q = data.get(i);
			actualCal.setTime(tma.getDate().getTime());
			if (from.getTimeInMillis()>actualCal.getTimeInMillis()) continue;
			if (to.getTimeInMillis()<actualCal.getTimeInMillis()) break;
			
			int day = actualCal.get(Calendar.DAY_OF_YEAR);
			int h = actualCal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = actualCal.get(Calendar.DAY_OF_WEEK);
			if (dayWeek<day1 || dayWeek>day2 ) continue;
			if (h<h1 || h>h2) continue;
			if (day!=lastDay){
				if (lastDay!=-1){
					countDays++;
					totalBounces+=dayBounces;
					bouncesArr.add(dayBounces);
					//System.out.println("dayBounces : "+dayBounces);
				}
				lastDay = day;
				dayBounces = 0;
				lastBounce=-1;
			}
			
			int diffUp   = TradingUtils.getPipsDiff(q.getHigh(), tma.getUpper());
			int diffDown = TradingUtils.getPipsDiff(tma.getLower(),q.getLow());
			boolean upBounce = (diffUp>=minDiff) ? true:false;
			boolean downBounce = (diffDown>=minDiff) ? true:false;
			
			//if (upBounce && downBounce) continue;
			
			if ((lastBounce==-1 || lastBounce==0) && upBounce==true){
				dayBounces++;
				lastBounce = 1;
				//System.out.println("upBounce");
			}else{
				
				if ((lastBounce==-1 || lastBounce==1) && downBounce==true){
					dayBounces++;
					lastBounce = 0;
				//System.out.println("downBounce");
				}
			}
		}
		//System.out.println("total days total bounces avg: "+countDays+" "+totalBounces
		//		+" "+PrintUtils.Print2dec(totalBounces*1.0/countDays, false));
		
		return bouncesArr;
	}
	
	public static ArrayList<DayBounces> countDayBounces(ArrayList<Quote> data,ArrayList<TMA> tmas,
			Calendar from, Calendar to, int day1,int day2,int h1,int h2,int minDiff){
		
		ArrayList<DayBounces> bouncesArr = new ArrayList<DayBounces>();
		Calendar actualCal = Calendar.getInstance();
		int lastDay = -1;
		int countDays =  0;
		int lastBounce=-1;
		double lastTmaValue=0;
		int maxPips=0;
		Calendar lastCal =  null;
		DayBounces dayBounces = null;
		
		for (int i=0;i<data.size();i++){
			TMA tma = tmas.get(i);
			Quote q = data.get(i);
			actualCal.setTime(tma.getDate().getTime());
			if (from.getTimeInMillis()>actualCal.getTimeInMillis()) continue;
			if (to.getTimeInMillis()<actualCal.getTimeInMillis()) break;
			
			int day = actualCal.get(Calendar.DAY_OF_YEAR);
			int h = actualCal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = actualCal.get(Calendar.DAY_OF_WEEK);
			if (dayWeek<day1 || dayWeek>day2 ) continue;
			if (h<h1 || h>h2) continue;
			if (day!=lastDay){
				if (lastDay!=-1){
					if (lastBounce==1){
						Bounce bounce = new Bounce();
						bounce.setMaxPips(maxPips);
						bounce.setUpBounce(true);
						bounce.setCal(lastCal);
						dayBounces.getBounces().add(bounce);
						//System.out.println("adding up bounce: "+maxPips);
					};	
					if (lastBounce==0){
						Bounce bounce = new Bounce();
						bounce.setMaxPips(maxPips);
						bounce.setUpBounce(false);
						bounce.setCal(lastCal);
						dayBounces.getBounces().add(bounce);
						//System.out.println("adding down bounce: "+maxPips);
					};		
					bouncesArr.add(dayBounces);
					//System.out.println("dayBaounces: "+DateUtils.datePrint(dayBounces.getCal())+" "+dayBounces.getBounces().size());
				}
				//nuevo dayBounces
				dayBounces = new DayBounces();
				dayBounces.getCal().setTimeInMillis(actualCal.getTimeInMillis());				
				lastDay = day;
				lastBounce=-1;
			}
			
			int diffUp   = TradingUtils.getPipsDiff(q.getHigh(), tma.getUpper());
			int diffDown = TradingUtils.getPipsDiff(tma.getLower(),q.getLow());
			boolean upBounce = (diffUp>=minDiff) ? true:false;
			boolean downBounce = (diffDown>=minDiff) ? true:false;
			
			//if (upBounce && downBounce) continue;
			
			/*System.out.println("cal diffUp diffDown "+DateUtils.datePrint(q.getDate())
					+" "+diffUp+" "+PrintUtils.Print4dec(tma.getUpper())
					+" "+diffDown+" "+PrintUtils.Print4dec(tma.getLower())
					+" "+upBounce+" "+downBounce
					+" "+lastBounce
					);*/
			if ((lastBounce==-1 || lastBounce==0) && upBounce==true){
				if (lastBounce==0){
					Bounce bounce = new Bounce();
					bounce.setMaxPips(maxPips);
					bounce.setUpBounce(false);
					bounce.setCal(lastCal);
					dayBounces.getBounces().add(bounce);
					//System.out.println("adding down bounce: "+maxPips);
				};				
				maxPips=0;
				lastBounce   = 1;				
				lastTmaValue = tma.getUpper();
				lastCal = Calendar.getInstance();
				lastCal.setTimeInMillis(actualCal.getTimeInMillis());
				//System.out.println("upBounce");
			}else if ((lastBounce==-1 || lastBounce==1) && downBounce==true){
				if (lastBounce==1){
					Bounce bounce = new Bounce();
					bounce.setMaxPips(maxPips);
					bounce.setUpBounce(true);
					bounce.setCal(lastCal);
					dayBounces.getBounces().add(bounce);
					//System.out.println("adding up bounce: "+maxPips);
				};	
				maxPips=0;
				lastBounce = 0;	
				lastTmaValue = tma.getLower();				
				lastCal = Calendar.getInstance();
				lastCal.setTimeInMillis(actualCal.getTimeInMillis());
			}
		
			//actualizacion maximos
			if (lastBounce==1){
				if (diffUp>=maxPips) maxPips =diffUp;
			}else if (lastBounce==0){
				if (diffDown>=maxPips) maxPips =diffDown;
			}
		}
		
		return bouncesArr;
	}
	
	public static void countBounces(ArrayList<TmaDiff> tmaDiffs,int day1,int day2,int h1,int h2){
		
		Calendar actualCal = Calendar.getInstance();
		int lastDay = -1;
		int countDays =  0;
		int totalBounces = 0;
		int dayBounces = 0;
		int lastBounce=-1;
		for (int i=0;i<tmaDiffs.size();i++){
			TmaDiff tmaDiff = tmaDiffs.get(i);
			actualCal.setTime(tmaDiff.getCal().getTime());
			
			int day = actualCal.get(Calendar.DAY_OF_YEAR);
			int h = actualCal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = actualCal.get(Calendar.DAY_OF_WEEK);
			if (dayWeek<day1 || dayWeek>day2 ) continue;
			if (h<h1 || h>h2) continue;
			if (day!=lastDay){
				if (lastDay!=-1){
					countDays++;
					totalBounces+=dayBounces;
					//System.out.println("dayBounces : "+dayBounces);
				}
				lastDay = day;
				dayBounces = 0;
				lastBounce=-1;
			}
			
			boolean upBounce = (tmaDiff.getDiffUp()>=0) ? true:false;
			boolean downBounce = (tmaDiff.getDiffDown()>=0) ? true:false;
			
			if ((lastBounce==-1 || lastBounce==0) && upBounce==true){
				dayBounces++;
				lastBounce = 1;
				//System.out.println("upBounce");
			}
			if ((lastBounce==-1 || lastBounce==1) && downBounce==true){
				dayBounces++;
				lastBounce = 0;
				//System.out.println("downBounce");
			}								
		}
		//System.out.println("total days total bounces avg: "+countDays+" "+totalBounces
		//		+" "+PrintUtils.Print2dec(totalBounces*1.0/countDays, false));
	}
	
	public static void dayBouncesStats(String header,
			ArrayList<DayBounces> bounces,boolean details) {
		// TODO Auto-generated method stub
		int totalBounces = 0;
		int totalDays = bounces.size();
		int total12=0;
		int total23=0;
		int total34=0;
		int total45=0;
		int total56=0;
		double avg12 = 0;
		double avg23 = 0;
		double avg34 = 0;
		double avg45 = 0;
		double avg56 = 0;
		ArrayList<Integer> b12 = new ArrayList<Integer>();
		ArrayList<Integer> b23 = new ArrayList<Integer>();
		ArrayList<Integer> b34 = new ArrayList<Integer>();
		ArrayList<Integer> b45 = new ArrayList<Integer>();
		ArrayList<Integer> b56 = new ArrayList<Integer>();
		ArrayList<Integer> bday= new ArrayList<Integer>();
		for (int i=0;i<bounces.size();i++){
			DayBounces dayBounce = bounces.get(i);
			ArrayList<Bounce> bouncesArr  = dayBounce.getBounces();
			totalBounces += bouncesArr.size();
			bday.add(bouncesArr.size());
			for (int j=0;j<bouncesArr.size();j++){
				Bounce b = bouncesArr.get(j);
				if (j==1){
					total12++;
					avg12+=bouncesArr.get(0).getMaxPips();
					b12.add(bouncesArr.get(0).getMaxPips());
				}
				if (j==2){
					total23++;
					avg23+=bouncesArr.get(1).getMaxPips();
					b23.add(bouncesArr.get(1).getMaxPips());
				}
				if (j==3){
					total34++;
					avg34+=bouncesArr.get(2).getMaxPips();
					b34.add(bouncesArr.get(2).getMaxPips());
				}
				if (j==4){
					total45++;
					avg45+=bouncesArr.get(3).getMaxPips();
					b45.add(bouncesArr.get(3).getMaxPips());
				}
				if (j==5){
					total56++;
					avg56+=bouncesArr.get(4).getMaxPips();
					b56.add(bouncesArr.get(4).getMaxPips());
				}
			}
		}
		bouncesStats("GLOBALRESULTS "+header,bday);
		if (details){
			double avg12Per = avg12*1.0/total12;
			System.out.println(header+" totalDays= "+totalDays+" totalBounces= "+total12
					+" "+PrintUtils.Print2dec(total12*100.0/totalDays, false)+'%'
					+" avg12= "+PrintUtils.Print2dec(avg12Per, false)
					);
			bouncesStats("",b12);
			double avg23Per = avg23*1.0/total23;
			System.out.println(header+" totalDays= "+totalDays+" totalBounces= "+total23
					+" "+PrintUtils.Print2dec(total23*100.0/totalDays, false)+'%'
					+" avg23= "+PrintUtils.Print2dec(avg23Per, false)
					);
			bouncesStats("",b23);
			double avg34Per = avg34*1.0/total34;
			System.out.println(header+" totalDays= "+totalDays+" totalBounces= "+total34
					+" "+PrintUtils.Print2dec(total34*100.0/totalDays, false)+'%'
					+" avg34= "+PrintUtils.Print2dec(avg34Per, false)
					);
			bouncesStats("",b34);
			double avg45Per = avg45*1.0/total45;
			System.out.println(header+" totalDays= "+totalDays+" totalBounces= "+total45
					+" "+PrintUtils.Print2dec(total45*100.0/totalDays, false)+'%'
					+" avg45= "+PrintUtils.Print2dec(avg45Per, false)
					);
			bouncesStats("",b45);
			double avg56Per = avg56*1.0/total56;
			System.out.println(header+" totalDays= "+totalDays+" totalBounces= "+total56
					+" "+PrintUtils.Print2dec(total56*100.0/totalDays, false)+'%'
					+" avg56= "+PrintUtils.Print2dec(avg56Per, false)
					);
			bouncesStats("",b56);
		}
	}
	
	public static void bouncesStats(String header, ArrayList<Integer> bounces){
		int count0=0;
		int count1=0;
		int count2=0;
		int count3=0;
		int count4=0;
		int count5=0;
		int count6=0;
		int count7=0;
		int count8=0;
		int count9=0;
		int count10=0;
		int count11=0;
		int count12=0;
		int count13=0;
		int count14=0;
		int count15=0;
		for (int i=0;i<bounces.size();i++){
			int b = bounces.get(i);
			if (b==0) count0++;
			if (b==1) count1++;
			if (b==2) count2++;
			if (b==3) count3++;
			if (b==4) count4++;
			if (b==5) count5++;
			if (b==6) count6++;
			if (b==7) count7++;
			if (b==8) count8++;
			if (b==9) count9++;
			if (b==10) count10++;
			if (b==11) count11++;
			if (b==12) count12++;
			if (b==13) count13++;
			if (b==14) count14++;
			if (b>=15) count15++;
		}
		System.out.println(header+" bounces (0 1 2 3 4 5 6 7 >8) = "
				+" "+PrintUtils.Print2dec(count0*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count1*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count2*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count3*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count4*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count5*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count6*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count7*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count8*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count9*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count10*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count11*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count12*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count13*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count14*100.0/bounces.size(),false)
				+" "+PrintUtils.Print2dec(count15*100.0/bounces.size(),false)
				);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "c:\\fxdata";
		String file5m = path+"\\"+"EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.03.20.csv";
		
		ArrayList<Quote> dataI 			= DAO.retrieveData(file5m, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS 			=  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 			= TradingUtils.cleanWeekendData(dataS);
  			
		double bandFactor = 2.9;
		int atrPeriod = 100;
		int halfLength = 56;
		
		int year = 2011;
		int day1 = Calendar.MONDAY+1;
		int day2 = Calendar.MONDAY+1;
		int h1 = 0;
		int h2 = 0;
		
		ArrayList<TMA> tma5m = IndicatorLib.calculateTMA_Array(data, 0,data.size()-1,bandFactor,halfLength,atrPeriod);
		
		Calendar from = Calendar.getInstance();
  		Calendar to = Calendar.getInstance();
  		for (year=2014;year<=2014;year++){	
  			from.set(2014, 0, 1);
  			to.set(2014, 11, 31);
  			for (h2= 8;h2<=8;h2+=1){
  				h1 = 3;  				
  				for (int minDiff=0;minDiff<=0;minDiff++){  
  					String header = "h1= "+h1+" h2= "+h2+" minDiff= "+minDiff;
  					//ArrayList<Integer> bounces =StudyTrending.countBounces(data,tma5m,from,to, day1, day2, h1, h2,minDiff);
  					ArrayList<DayBounces> bounces =StudyTrending.countDayBounces(data,tma5m,from,to, day1, day2, h1, h2,minDiff);
  					int totalBounce = countBounces(bounces);
  						
  					dayBouncesStats(header,bounces,true);
  					//bouncesStats(header,bounces);
  				}  				
  			}
  		}
  		
  		/*int minDiff=0;
  		h1 = 8;
  		h2 = 11;
  		int h3 = 12;
  		int h4 = 23;
  		for (year=2013;year<=2013;year++){	
  			from.set(2012, 0, 1);
  			to.set(2014, 11, 31);
  			String header = "h1= "+h1+" h2= "+h2+" minDiff= "+0;
  			ArrayList<DayBounces> bounces =StudyTrending.countDayBounces(data,tma5m,from,to, day1, day2, h1, h2,minDiff);
  			int totalBounces = countBounces(bounces);
  			ArrayList<DayBounces> bounces2 =StudyTrending.countDayBounces(data,tma5m,from,to, day1, day2, h3, h4,minDiff);
  			int totalBounces2 = countBounces(bounces2);
  			for (int i=0;i<bounces.size();i++){
  				DayBounces dayBounce = bounces.get(i);
  				DayBounces dayBounce2 = bounces2.get(i);
  				System.out.println(dayBounce.getBounces().size()+" "+dayBounce2.getBounces().size());
  			}  			
  		}*/
  	}

	private static int countBounces(ArrayList<DayBounces> bounces) {
		// TODO Auto-generated method stub
		int totalBounces = 0;
		for (int i=0;i<bounces.size();i++){
			DayBounces dayBounce = bounces.get(i);
			ArrayList<Bounce> bouncesArr  = dayBounce.getBounces();
			totalBounces += bouncesArr.size();
		}
		return totalBounces;
	}

	

}
