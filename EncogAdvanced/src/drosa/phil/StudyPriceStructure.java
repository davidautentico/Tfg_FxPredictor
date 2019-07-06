package drosa.phil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyPriceStructure {

	private static int studySegments(String header, ArrayList<SegmentInfo> segFiltered, boolean print) {
		// TODO Auto-generated method stub

		int totalDays=0;
		ArrayList<Integer> daysCount = new ArrayList<Integer>();
		for (int i=0;i<5;i++) daysCount.add(0);
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		for (int i=0;i<segFiltered.size();i++){
			SegmentInfo seg = segFiltered.get(i);
			cal.setTime(seg.getBeginDate().getTime());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			int index = dayWeek-Calendar.MONDAY;
			//System.out.println("index: "+index);
			int count = daysCount.get(index);
			daysCount.set(index, count+1);		
			if (day!=lastDay){
				totalDays++;
				lastDay=day;
			}
		}
		
		int total=0;
		String strCount ="";
		for (int i=0;i<daysCount.size();i++){
			int t = daysCount.get(i);
			strCount+=t+" ";
			total+=t;
		}
		if (print)
			System.out.println(header+" total diffDays days : "+ total+" || " + totalDays +" || "+strCount);
		return total;
	}
	
	public static void printSegment(ArrayList<Quote> data,int pos1,int pos2,int type){
		
		double val1 = data.get(pos1).getLow();
		double val2 = data.get(pos2).getHigh();
		
		if (type==0){
			val1 = data.get(pos1).getHigh();
			val2 = data.get(pos2).getLow();
		}
		
		int pipsDiff = Math.abs(TradingUtils.getPipsDiff(val1, val2));
		
		System.out.println("SEGMENT ->"+pipsDiff+"("+type+")");
	}
	
	public static ArrayList<SegmentInfo> studyDayDNA(ArrayList<Quote> data,Calendar from, Calendar to, 
			int day1, int day2, int h1, int h2, int pips){
	
		ArrayList<SegmentInfo> segments = new ArrayList<SegmentInfo>();
		//ArrayList<Integer> tramosPips = new ArrayList<Integer>();		

		int actualSegment = -1;
		int pos1     = 0; //posicion final del tramo actual
		int pos2     = 0; //posicion final del tramo actual
		Calendar beginDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();	
		int buyPips  = 0;
		int sellPips = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){//para cada quote del dia
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			if (cal.getTimeInMillis()<from.getTimeInMillis()){
				pos1++;
				pos2++;
				continue ;
			}
			if (cal.getTimeInMillis()>to.getTimeInMillis()){				
				break;
			}	
			
			if (actualSegment == -1){
				beginDate.setTimeInMillis(cal.getTimeInMillis());
				Quote q1 = data.get(pos1);
				/*System.out.println("pos1 quote: "+pos1+" "
							+PrintUtils.getOHLC(q1)
							+" "+q1.getLow()
							);*/
				buyPips  = TradingUtils.getPipsDiff(q.getHigh(),data.get(pos1).getLow());
				sellPips = TradingUtils.getPipsDiff(data.get(pos1).getHigh(),q.getLow());
				//System.out.println("buyPips sellPips: "+buyPips+" "+sellPips
				//		+" "+PrintUtils.Print(q.getHigh())+" "+PrintUtils.Print(q.getLow()));
				//return;
			}else if (actualSegment == 1){ //UP
				buyPips  = TradingUtils.getPipsDiff(q.getHigh(),data.get(pos1).getLow());
				sellPips  = TradingUtils.getPipsDiff(data.get(pos2).getHigh(),q.getLow());
			}else if (actualSegment == 0){ //DOWN
				sellPips	= TradingUtils.getPipsDiff(data.get(pos1).getHigh(),q.getLow());
				buyPips		= TradingUtils.getPipsDiff(q.getHigh(),data.get(pos2).getLow());
			}
			
			if (actualSegment==-1){// no trend
				if (buyPips>=pips){
					actualSegment	= 1;//UP
					pos2 = i;
					endDate.setTimeInMillis(cal.getTimeInMillis());
					//System.out.println("initial UP segment : "+pos2+" "+pos1);
				}
				if (sellPips>=pips){//estamos por encima del umbral down								
					actualSegment	= 0;//DOWN
					pos2 = i;
					endDate.setTimeInMillis(cal.getTimeInMillis());
					//System.out.println("initial SELL segment : "+pos2+" "+pos1);
				}
			}else if (actualSegment==1){ //UP 
				if (q.getHigh()>=data.get(pos2).getHigh()){ //actualizamos el high si es necesario
					pos2 = i;
					endDate.setTimeInMillis(cal.getTimeInMillis());
					//System.out.println("actualizacion high: "+data.get(pos2).getHigh());
				}
				if (sellPips>=pips){
					double val1 = data.get(pos1).getLow();
					double val2 = data.get(pos2).getHigh();
					int totalPips = Math.abs(TradingUtils.getPipsDiff(val1, val2));
					//tramosPips.add(totalPips);
					int index =  i;
					int day = beginDate.get(Calendar.DAY_OF_WEEK);
					int h   = beginDate.get(Calendar.HOUR_OF_DAY);
					if (day>=day1 && day<=day2 && h>=h1 && h<=h2){
						SegmentInfo seg = new SegmentInfo();
						seg.getBeginDate().setTimeInMillis(beginDate.getTimeInMillis());
						seg.getEndDate().setTimeInMillis(endDate.getTimeInMillis());
						seg.setBeginValue(val1);
						seg.setEndValue(val2);
						seg.setTotalPips(totalPips);
						seg.setUpDown(1);
						seg.setPos1(pos1);
						seg.setPos2(pos2);
						segments.add(seg);
					}
									
					//printSegment(data,pos1,pos2,actualSegment);
					actualSegment	= 0;//DOWN
					pos1 = pos2;
					pos2 = i;	
					//actualizamos fechas
					beginDate.setTimeInMillis(endDate.getTimeInMillis());
					endDate.setTimeInMillis(cal.getTimeInMillis());
				}
			}else if (actualSegment==0){ //DOWN 
				if (q.getLow()<=data.get(pos2).getLow()){ //actualizamos el low si es necesario
					pos2 = i;
					endDate.setTimeInMillis(cal.getTimeInMillis());
					//System.out.println("actualizacion low: "+data.get(pos2).getLow());
				}
				if (buyPips>=pips){
					double val1 = data.get(pos1).getHigh();
					double val2 = data.get(pos2).getLow();
					int totalPips = Math.abs(TradingUtils.getPipsDiff(val1, val2));
					//tramosPips.add(Math.abs(TradingUtils.getPipsDiff(val1, val2)));
					int day = beginDate.get(Calendar.DAY_OF_WEEK);
					int h   = beginDate.get(Calendar.HOUR_OF_DAY);
					if (day>=day1 && day<=day2 && h>=h1 && h<=h2){
						SegmentInfo seg = new SegmentInfo();
						seg.getBeginDate().setTimeInMillis(beginDate.getTimeInMillis());
						seg.getEndDate().setTimeInMillis(endDate.getTimeInMillis());;
						seg.setBeginValue(val1);
						seg.setEndValue(val2);
						seg.setTotalPips(totalPips);
						seg.setUpDown(0);
						seg.setPos1(pos1);
						seg.setPos2(pos2);
						segments.add(seg);
					}
					//printSegment(data,pos1,pos2,actualSegment);
					actualSegment	= 1;//UP
					pos1 = pos2; 					
					pos2 = i;	
					//actualizamos fechas
					beginDate.setTimeInMillis(endDate.getTimeInMillis());
					endDate.setTimeInMillis(cal.getTimeInMillis());
				}
			}									
		}
		return segments;
	}
	
	public static ArrayList<Integer> calculateFrecSegments(ArrayList<SegmentInfo> segments){
		
		ArrayList<Integer> frecuency= new ArrayList<Integer>();
		for (int i=0;i<400;i++) frecuency.add(0);
		
		for (int i=0;i<segments.size();i++){
			SegmentInfo seg = segments.get(i);
			int pips = seg.getTotalPips();
			int value = frecuency.get(pips);
			frecuency.set(pips,value+1);
		}
		return frecuency;
	}
	
	public static void printAllSegments(ArrayList<SegmentInfo> segments){
		ArrayList<Integer> frecs = calculateFrecSegments(segments);
		for (int i=0;i<frecs.size();i++){
			int frec = frecs.get(i);
			if (frec>0)
				System.out.println(i+","+frec);
		}
	}
	
	public static void printAllSegments1(ArrayList<SegmentInfo> segments){
		ArrayList<Integer> frecs = calculateFrecSegments(segments);
		for (int i=0;i<segments.size();i++){
				System.out.println(segments.get(i).getTotalPips());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path5m = "c:\\fxdata\\EURUSD_5 Mins_Bid_2003.05.04_2014.02.23.csv";
		String path1m = "c:\\fxdata\\EURUSD_1 Min_Bid_2009.01.01_2014.03.14.csv";
		
		ArrayList<Quote> dataI = DAO.retrieveData(path1m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data 	= TradingUtils.cleanWeekendData(dataS);
		
		int yearF      	 	= 2012;
		int monthF 			= Calendar.JANUARY;
		int yearL  			= 2012;
		int monthL 			= Calendar.DECEMBER;
		int dL 				= Calendar.MONDAY;
		int dH 				= Calendar.MONDAY;
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		Calendar from2 = Calendar.getInstance();
		Calendar to2 = Calendar.getInstance();
		int day1 = Calendar.MONDAY+0;
		int day2 = Calendar.MONDAY+4;
		int h1 = 0;
		int h2 = 23;
		int max = 45;
		
		int year = 2013;
		from.set(year, 0, 1);
		to.set(year,  11, 31);
		
		/*for (h1=0;h1<=23;h1++){
			h2=h1;
			ArrayList<SegmentInfo> segments = StudyPriceStructure.studyDayDNA(data,from,to,day1,day2,h1,h2,10);
			ArrayList<Integer> values = SegmentInfo.extractPipArray(segments);
			MathUtils.summary(DateUtils.datePrint(to)+" "+day1+" "+h1+" "+h2+" ", values);
		}*/
		ArrayList<SegmentInfo> segments = null;
		for (day1=Calendar.MONDAY;day1<=Calendar.MONDAY+0;day1++){
			day2=day1+4;
			segments = StudyPriceStructure.studyDayDNA(data,from,to,day1,day2,h1,h2,100);
			ArrayList<Integer> values = SegmentInfo.extractPipArray(segments);
			MathUtils.summary(DateUtils.datePrint(to)+" "+day1+" "+h1+" "+h2+" ", values);
		}
		
	
		SegmentInfo.printSegments("Segmenets", segments, max, true, true);
		
		
		/*int lastFilts = 1;
		for (int factor = 2;factor<=10;factor++){
			for (year=2013;year<=2013;year++){
				String header="year="+year;
				from.set(2011, 0, 1);
				to.set(2014,  11, 31);	
				for (int size=50;size<=50;size+=10){
					header="year=2013 factor="+factor+" size= "+size;
					ArrayList<SegmentInfo> segments = StudyPriceStructure.studyDayDNA(data,from,to,day1,day2,h1,h2,size);
					for (max=size*factor;max<=size*factor;max+=10){
						ArrayList<SegmentInfo> segFiltered = SegmentInfo.printSegments(header,segments,max,false,true);
						System.out.println(PrintUtils.Print2dec(lastFilts*1.0/segFiltered.size(), false));
						lastFilts = segFiltered.size();
					}
				}
			}
			
			System.out.println("******************************");
		}*/
		
		//for (int i=0;i<segments.size();i++){
		/*int lastMax = 0;
		int lastValue = 0;
		for (max=10;max<=100;max+=1){
			ArrayList<SegmentInfo> segs = SegmentInfo.printSegments(segments, max, false);
			double diffPer = max*100.0/lastMax-100.0;
			double diffValue = 100.0-segs.size()*100.0/lastValue;
			System.out.println("max= "+max+" total segs: "+segs.size()
					+" "+PrintUtils.Print2dec(diffPer,false)
					+" "+PrintUtils.Print2dec(diffValue,false)
					);
			lastMax=max;
			lastValue = segs.size();
		}
	*/
		//ArrayList<SegmentInfo> segments = StudyPriceStructure.studyDayDNA(data,from,to2,day1,day2,h1,h2,10);
		//System.out.println("total segments: "+segments.size());
		
		/*int totalWins =0;
		int totalLosses=0;
		int minDiff = 10;
		double beginValue = 0;
		double stopLoss = 0;
		double takeProfit = 0;
		int sl = 10;
		int tp = 2;
		int actualDiff = 0;
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			Quote q1 = data.get(i-1);
			SegmentInfo seg = SegmentInfo.findSegment(segments,i-1);
			if (seg==null) continue;
			int pos1 = seg.getPos1();
			int pos2 = seg.getPos2();
			Quote qInitial = data.get(pos1);
			int upDown = seg.getUpDown();
			if (upDown==1){
				actualDiff = TradingUtils.getPipsDiff(q1.getHigh(), qInitial.getLow());
				//System.out.println("actualDiff: "+actualDiff);
				if (actualDiff>=minDiff){
					beginValue = q.getOpen();
					stopLoss   = beginValue + 0.0001*sl;
					takeProfit = beginValue - 0.0001*tp;
					System.out.println("begin stopLoss takeProfit : "
							+PrintUtils.Print(beginValue)+" "
							+" "+PrintUtils.Print(stopLoss)+" "
							+" "+PrintUtils.Print(takeProfit)+" "
							);
					PriceTestResult res = TradingUtils.testPriceMovement(data,i,data.size()-1,beginValue,stopLoss,takeProfit,0);
					if (res.isWin()) totalWins++;
					else totalLosses++;
					System.out.println(" win%: "+PrintUtils.Print2dec(totalWins*100.0/(totalWins+totalLosses), false));
				}
			}
		}*/
		
		
		
		
		/*from.set(2013, 0, 1);
		to.set(2014,  1, 31);
		to2.setTimeInMillis(from.getTimeInMillis());
		while (to2.getTimeInMillis()<=to.getTimeInMillis()){
			from.setTimeInMillis(to2.getTimeInMillis());
			from.add(Calendar.MONTH, -8);
			
			for (day1=Calendar.MONDAY+0;day1<=Calendar.MONDAY+0;day1++){
				day2=day1;
				for (h1=0;h1<=23;h1++){
					h2=h1+0;
					ArrayList<SegmentInfo> segments = StudyPriceStructure.studyDayDNA(data,from,to2,day1,day2,h1,h2,10);
					ArrayList<Integer> values = SegmentInfo.extractPipArray(segments);
					MathUtils.summary(DateUtils.datePrint(to2)+" "+day1+" "+h1+" "+h2+" ", values);
				}
			}
			to2.add(Calendar.MONTH, 1);
		}*/
		//ArrayList<SegmentInfo> segFiltered = SegmentInfo.printSegments(segments,71,true);
		
		/*String header="";
		ArrayList<Integer> totals = new ArrayList<Integer>();
		
		for (h1=0;h1<=23;h1++){
			h2=h1;
			totals.clear();
			for (max=10;max<=45;max+=5){
				ArrayList<SegmentInfo> segments = StudyPriceStructure.studyDayDNA(data,from,to,day1,day2,h1,h2,10);
				ArrayList<SegmentInfo> segFiltered = SegmentInfo.printSegments(segments,max,false);
				header = "h1= "+h1+" h2="+h2+" max="+max;
				int total = studySegments(header,segFiltered,false);
				totals.add(total);
			}
			int total = totals.get(0);
			for (int i=5;i<6;i++){
				int t = totals.get(i);
				System.out.println(h1+"-"+h2+" "+PrintUtils.Print2dec(t*100.0/total,false)+"%");
			}
		}*/
		
		/*for (h2=13;h2<=13;h2++){
			h1 = 9;
			ArrayList<SegmentInfo> segments = StudyPriceStructure.studyDayDNA(data,from,to,day1,day2,0,h2,10);
			ArrayList<SegmentInfo> segFiltered = SegmentInfo.printSegments(segments,max,false);
			studySegments(segFiltered);
		}*/
		
		/*from.set(2013, 0, 1);
		to.set(2014,  11, 31);		
		for (h1=0;h1<=23;h1+=1){
			h2 = h1+4;
			ArrayList<SegmentInfo> segments = StudyPriceStructure.studyDayDNA(data,from,to,day1,day2,h1,h2,10);
			ArrayList<Integer> values = new ArrayList<Integer>();
			for (int i=0;i<segments.size();i++) values.add(segments.get(i).getTotalPips());
			String header=h1+" "+h2;
			MathUtils.summary(header,values);
		}*/
			//StudyPriceStructure.printAllSegments1(segments);
		/*for (h1=0;h1<=0;h1++){
			h2 = h1+23;
			for (int pips=10;pips<=10;pips+=1)
			for (int year=2005;year<=2013;year++){
				from.set(year, 0, 1);
				to.set(year,  11, 31);		
				ArrayList<SegmentInfo> segments = StudyPriceStructure.studyDayDNA(data,from,to,day1,day2,h1,h2,pips);
				ArrayList<Integer> tramos = SegmentInfo.extractPipArray(segments);
				
				double avg = MathUtils.average(tramos);		
				double dt  = Math.sqrt(MathUtils.variance(tramos));
				
				double acc=0;
				int below3=(int) (avg+3*dt);
				int below2=(int) (avg+2*dt);
				int below1=(int) (avg+1*dt);
				int below0=15;
				int totalBelow0=0;
				int totalBelow1=0;
				int totalBelow2=0;
				int totalBelow3=0;
				double avgMin=0;
				for (int i=1;i<segments.size();i++){
					SegmentInfo segment = segments.get(i);
					SegmentInfo segment1 = segments.get(i-1);
					acc+=segments.get(i).getTotalPips();
					
					long diffMilis = segment.getBeginDate().getTimeInMillis()-segment1.getBeginDate().getTimeInMillis();
					double diffMin = diffMilis*1.0/(1000*60);
					avgMin+=diffMin;
					//System.out.println("tramo: "+DateUtils.datePrint(segment.getBeginDate())
					//		+" "+tramos.get(i)+" "+PrintUtils.Print(acc*1.0/(i+1))
					//		+" "+PrintUtils.Print2dec(diffMin,false));
					if (segments.get(i).getTotalPips()<below1)
						totalBelow1++;	
					if (segments.get(i).getTotalPips()<below2){
						totalBelow2++;
					}
					if (segments.get(i).getTotalPips()<below3){
						totalBelow3++;
					}
					if (segments.get(i).getTotalPips()>=below0){
						
						totalBelow0++;
					}
				}
				
				System.out.println("year totals avg dt totalB below "+year
						+" "+h1+"-"+h2
						+" "+pips
						+" "+segments.size()
						+" "+PrintUtils.Print2dec(avg/pips,false)
						+" "+PrintUtils.Print2dec(avg, false)+" "+PrintUtils.Print2dec(dt, false)
						+" "+below0+" "+PrintUtils.Print2dec(totalBelow0*100.0/segments.size(), false)+"%"
						+" "+below1+" "+PrintUtils.Print2dec(totalBelow1*100.0/segments.size(), false)+"%"
						+" "+below2+" "+PrintUtils.Print2dec(totalBelow2*100.0/segments.size(), false)+"%"
						+" "+below3+" "+PrintUtils.Print2dec(totalBelow3*100.0/segments.size(), false)+"%"
						+" "+PrintUtils.Print2dec(avgMin/(segments.size()-1),false)
						);
			}
		}*/
		
		
	
		/*int pips = 30;
		for (int i=15;i<=300;i+=5){
			ArrayList<Integer> tramos = StudyPriceStructure.studyDayDNA(data,from,to,i);
			double avg = 0;
			for (int j=0;j<tramos.size();j++){
				avg +=tramos.get(j);
			}
			System.out.println(i+" "+tramos.size() 
					+" "+PrintUtils.Print2dec(avg*1.0/tramos.size(),false)
					+" "+PrintUtils.Print2dec(avg*1.0/(i*tramos.size()),false)
					);
		}*/
		System.out.println("programa terminado");
	}

	

}
