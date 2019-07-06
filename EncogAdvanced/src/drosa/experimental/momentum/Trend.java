package drosa.experimental.momentum;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class Trend {

	QuoteShort qtoken = new QuoteShort();
	QuoteShort q0 = new QuoteShort();
	QuoteShort q1 = new QuoteShort();
	QuoteShort q2 = new QuoteShort();
	int index0 = -1;
	int index1 = -1;
	int index2 = -1;
	int type = 0;
	int tokenHour = -1;
	int tokenSize = -1;
	int tokenOpen = -1;

	public Trend(QuoteShort aq1,QuoteShort aq2,int aType){
		this.type = aType;
		this.q1.copy(aq1);
		this.q2.copy(aq2);
	}
	public Trend(QuoteShort aq1,QuoteShort aq2,QuoteShort aq0,QuoteShort aqtoken,int aIndex0,int aIndex1,int aIndex2,int aTokenHour,int aTokenSize,int aTokenOpen,int aType){
		this.type = aType;
		if (aqtoken!=null)
			this.qtoken.copy(aqtoken);
		if (aq0!=null) this.q0.copy(aq0);
		this.q1.copy(aq1);
		this.q2.copy(aq2);
		this.index0 = aIndex0;
		this.index1 = aIndex1;
		this.index2 = aIndex2;
		this.tokenHour = aTokenHour;
		this.tokenSize = aTokenSize;
		this.tokenOpen = aTokenOpen;
	}
	
	
	public int getTokenOpen() {
		return tokenOpen;
	}
	public void setTokenOpen(int tokenOpen) {
		this.tokenOpen = tokenOpen;
	}
	public int getTokenHour() {
		return tokenHour;
	}
	public void setTokenHour(int tokenHour) {
		this.tokenHour = tokenHour;
	}
	
	public int getTokenSize() {
		return tokenSize;
	}
	public void setTokenSize(int tokenSize) {
		this.tokenSize = tokenSize;
	}
	
	public QuoteShort getQ1() {
		return q1;
	}

	public void setQ1(QuoteShort q1) {
		this.q1 = q1;
	}

	public QuoteShort getQ2() {
		return q2;
	}

	public void setQ2(QuoteShort q2) {
		this.q2 = q2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getIndex0() {
		return index0;
	}
	public void setIndex0(int index0) {
		this.index0 = index0;
	}
	public int getIndex1() {
		return index1;
	}
	public void setIndex1(int index1) {
		this.index1 = index1;
	}
	public int getIndex2() {
		return index2;
	}
	public void setIndex2(int index2) {
		this.index2 = index2;
	}
	
	public static ArrayList<Trend> calculateTrends(ArrayList<QuoteShort> data,
			int begin,int end, int minSize,int thr,
			int tokenHour,
			boolean reset){
		
		ArrayList<Trend> trends = new ArrayList<Trend>();
		if (begin<1) begin = 1;
		if (end>data.size()-1) end = data.size()-1;
		
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		Calendar cal_1 = Calendar.getInstance();
		int actualLeg = 0;
		int index1=-1;
		int index2=-1;
		int index0=-1;
		int indexHigh = -1;
		int indexLow = -1;
		int actualHigh = -1;
		int actualLow = -1;
		int indexToken = -1;
		int actualTokenSize = -1;
		int actualTokenOpen = -1;
		QuoteShort q1 = null;
		QuoteShort q2 = null;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q_1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal_1, q_1);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			/*if (year>=2010 && month>=Calendar.FEBRUARY) break;
			
			if (cal_1.getTimeInMillis()>cal.getTimeInMillis()){
				System.out.println("[WARNING] INCONSISTENT DATE: "+q_1.toString()+" || "+q.toString());
			}*/
			
			if (reset){
				if (day!=lastDay){
					if (actualLeg!=0){
						QuoteShort q0 = null;
						if (index0>=0){ 
							q0 = data.get(index0);
						}
						QuoteShort qtoken = null;
						if (indexToken>=0)
							qtoken = data.get(indexToken);
						Trend t = new Trend(q1,q2,q0,qtoken,index0,index1,index2,tokenHour,actualTokenSize,actualTokenOpen,actualLeg);
						if (t.getSize()>=minSize) trends.add(t);
						//System.out.println("anadiendo trend: "+t.toString());
					}
					index0=0;
					index1=i;
					index2=i;
					indexToken = -1;
					actualTokenSize = -1;
					actualTokenOpen = -1;
					actualHigh = -1;
					actualLow = -1;
					actualLeg = 0;
					lastDay = day;
				}
			}
			
			if (index1==-1){
				index0 = 0;
				index1 = i;
				index2 = i;
			}
			
			q1 = data.get(index1);
			q2 = data.get(index2);
			double longDiff = (q2.getHigh5()-q1.getLow5())*0.1;
			double shortDiff = (q1.getHigh5()-q2.getLow5())*0.1;
			
			if (actualHigh==-1 || q.getHigh5()>=actualHigh){
				actualHigh = q.getHigh5();
				indexHigh = i;
			}
			if (actualLow==-1 || q.getLow5()<=actualLow){
				actualLow = q.getLow5();
				indexLow = i;
			}
			
			if (actualLeg==0){
				double longDiff0 = (q.getHigh5()-data.get(indexLow).getLow5())*0.1;
				double shortDiff0 = (data.get(indexHigh).getHigh5()-q.getLow5())*0.1;
				//System.out.println(longDiff0+" "+shortDiff0+" "+i+" "+index1);
				if (longDiff0>=minSize 
						&& indexLow!=i
						){
					index1 = indexLow;
					actualLeg = 1;
					index2 = i;
					//System.out.println(longDiff0+" "+actualLeg);
				}else if (shortDiff0>=minSize  
						&& indexHigh!=i
						){
					index1 = indexHigh;
					actualLeg = -1;
					index2 = i;
					//System.out.println(shortDiff0+" "+actualLeg);
				}
			}else if (actualLeg==1){
				int lowDiff = (int) ((q2.getHigh5()-q.getLow5())*0.1);
				if (h==tokenHour && actualTokenSize==-1){
					actualTokenSize = (int) ((q.getOpen5()-q1.getLow5())*0.1);
					actualTokenOpen = q.getOpen5();
					indexToken = i;
				}
				if (q.getHigh5()>=q2.getHigh5()){
					int actualSize = (int) ((q.getHigh5()-q1.getLow5())*0.1);
					if (index0<=0 && actualSize>=thr) index0 = i;
					index2 = i;
				}else if (lowDiff>=minSize){
					QuoteShort q0 = null;
					if (index0>=0){ 
						q0 = data.get(index0);
					}
					QuoteShort qtoken = null;
					if (indexToken>=0)
						qtoken = data.get(indexToken);
					Trend t = new Trend(q1,q2,q0,qtoken,index0,index1,index2,tokenHour,actualTokenSize,actualTokenOpen,actualLeg);
					trends.add(t);
					//System.out.println("anadiendo trend1: "+t.toString());
					actualLeg = -1;
					index0 = i;
					index1 = index2;
					index2 = i;
					actualTokenSize = -1;
					actualTokenOpen = -1;					
					if (lowDiff>=thr) index0=i;
					//System.out.println("[NEW SHORT TREND] index0: "+i);
				}
			}else if (actualLeg==-1){
				int highDiff = (int) ((q.getHigh5() -q2.getLow5())*0.1);
				if (h==tokenHour && actualTokenSize==-1){
					actualTokenSize = (int) ((q1.getHigh5()-q.getOpen5())*0.1);
					actualTokenOpen = q.getOpen5();
					indexToken = i;
				}
				if (q.getLow5()<=q2.getLow5()){
					int actualSize = (int) ((q1.getHigh5()-q.getLow5())*0.1);
					if (index0<=0 && actualSize>=thr) index0 = i;
					index2 = i;
				}else if (highDiff>=minSize){
					QuoteShort q0 = null;
					if (index0>=0){ 
						q0 = data.get(index0);
					}
					QuoteShort qtoken = null;
					if (indexToken>=0)
						qtoken = data.get(indexToken);
					Trend t = new Trend(q1,q2,q0,qtoken,index0,index1,index2,tokenHour,actualTokenSize,actualTokenOpen,actualLeg);
					trends.add(t);
					//System.out.println("anadiendo trend-1: "+t.toString());
					actualLeg = 1;
					index0 = -1;
					index1 = index2;
					index2 = i;
					actualTokenSize = -1;
					actualTokenOpen = -1;
					
					if (highDiff>=thr) index0=i;
					//System.out.println("[NEW LONG TREND] index0: "+i);
				}
			}	
		}	
		return trends;
	}
	
	public double getSize(){
		if (type==1) return (q2.getHigh5()-q1.getLow5())*0.1;
		if (type==-1) return (q1.getHigh5()-q2.getLow5())*0.1;
		
		return -1;
	}
	
	public String toString(){
		
		String tokenQ = "";
		if (qtoken!=null) tokenQ = qtoken.toString();
		
		return q1.toString()
				+"// "+q0.toString()
				+" // "+tokenQ
				+" // "+q2.toString()
				+" || "+type+" "+PrintUtils.Print2dec(this.getSize(),false)+" || "+tokenHour+" "+tokenSize;
	}
	public static void printSummary(String header, ArrayList<Trend> trends) {
		
		ArrayList<Double> sizes = new ArrayList<Double>();
		ArrayList<Integer> maxs = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) maxs.add(0);
		double avgSize = 0;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int actualMaxH = -1;
		double actualMax = -1;
		int totalDays = 0;
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			QuoteShort q1 = t.getQ1();
			QuoteShort.getCalendar(cal, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				if (lastDay!=-1){
					maxs.set(actualMaxH ,maxs.get(actualMaxH )+1);
					totalDays++;
				}
				actualMax = -1;
				actualMaxH = -1;
				lastDay = day;
			}
			
			if (t.getSize()>=actualMax){
				actualMax = t.getSize();
				actualMaxH = h;
			}
		}

		for (int i=0;i<=23;i++){
			double avg = maxs.get(i)*100.0/totalDays;
			System.out.println(i+" "+PrintUtils.Print2dec(avg, false));
		}
	}
	
	public static void printSummaryDaily(String header, ArrayList<Trend> trends,int h1,int h2) {
		
		ArrayList<Double> days = new ArrayList<Double>();
		double avgSize = 0;
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int actualMaxH = -1;
		double actualMax = -1;
		int totalDays = 0;
		int totalDay = 0;
		int only1 = 0;
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			QuoteShort q1 = t.getQ1();
			QuoteShort.getCalendar(cal, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				if (lastDay!=-1){
					totalDays++;
				}
				days.add((double) totalDay);
				if (totalDay==1) only1++;
				totalDay = 0;
				lastDay = day;
			}
			if (h>=h1 && h<=h2)
				totalDay++;
		}
		header +=" "+h1+" "+h2+" "+only1;
		MathUtils.summary_mean_sd(header,days);
	}
	
	public static void printSummaryDaily2Periods(String header, ArrayList<Trend> trends,int h1,int h2,int h3,int h4) {
		
		ArrayList<Integer> days = new ArrayList<Integer>();
		ArrayList<Integer> days2 = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays = 0;
		int totalDay = 0;
		int totalDay2 = 0;
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			QuoteShort q1 = t.getQ1();
			QuoteShort.getCalendar(cal, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				if (lastDay!=-1){
					totalDays++;
				}
				days.add(totalDay);
				days2.add(totalDay2);
				totalDay = 0;
				totalDay2 = 0;
				lastDay = day;
			}
			if (h>=h1 && h<=h2){
				totalDay++;
			}
			if (h>=h3 && h<=h4){
				totalDay2++;
			}
		}
		
		int total0=0;
		int total1=0;int count1=0;
		int total2=0;int count2=0;
		int total3=0;int count3=0;
		int total4=0;int count4=0;
		int total5=0;int count5=0;
		int total6=0;int count6=0;
		int total7=0;int count7=0;
		int total8=0;int count8=0;
		int total9=0;int count9=0;
		int total10=0;int count10=0;
		int total11=0;int count11=0;
		for (int i=0;i<days.size();i++){
			if (days.get(i)==1){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total1 += days2.get(i);
				count1++;
			}
			if (days.get(i)==2){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total2 += days2.get(i);
				count2++;
			}
			if (days.get(i)==3){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total3 += days2.get(i);
				count3++;
			}
			if (days.get(i)==4){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total4 += days2.get(i);
				count4++;
			}
			if (days.get(i)==5){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total5 += days2.get(i);
				count5++;
			}
			if (days.get(i)==6){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total6 += days2.get(i);
				count6++;
			}
			if (days.get(i)==7){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total7 += days2.get(i);
				count7++;
			}
			if (days.get(i)==8){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total8 += days2.get(i);
				count8++;
			}
			if (days.get(i)==9){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total9 += days2.get(i);
				count9++;
			}
			if (days.get(i)==10){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total10 += days2.get(i);
				count10++;
			}
			if (days.get(i)==11){
				//System.out.println(days.get(i)+" "+days2.get(i));
				total11 += days2.get(i);
				count11++;
			}
		}
		System.out.println(
				PrintUtils.Print2dec(total1*1.0/count1, false)
				+" "+PrintUtils.Print2dec(total2*1.0/count2, false)
				+" "+PrintUtils.Print2dec(total3*1.0/count3, false)
				+" "+PrintUtils.Print2dec(total4*1.0/count4, false)
				+" "+PrintUtils.Print2dec(total5*1.0/count5, false)
				+" "+PrintUtils.Print2dec(total6*1.0/count6, false)
				+" "+PrintUtils.Print2dec(total7*1.0/count7, false)
				+" "+PrintUtils.Print2dec(total8*1.0/count8, false)
				+" "+PrintUtils.Print2dec(total9*1.0/count9, false)
				+" "+PrintUtils.Print2dec(total10*1.0/count10, false)
				+" "+PrintUtils.Print2dec(total11*1.0/count11, false)
				);
	}
	
	public static void printSummaryDaily2Periodsv2(String header, ArrayList<Trend> trends,int h1,int h2,int offset,int h3,int h4) {
		
		int count1 = 0;
		int count2 = 0;
		ArrayList<Integer> days = new ArrayList<Integer>();
		ArrayList<Integer> days2 = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int totalDays = 0;
		int totalDay = 0;
		int totalDay2 = 0;
		boolean isValid = false;
		boolean counted = false;
		int counted1 = 0;
		int counted2 = 0;
		int hh = -1;
		Trend thh = null;
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			//System.out.println("[actualtrend] "+i+" "+t.toString());
			QuoteShort q1 = t.getQ1();
			QuoteShort.getCalendar(cal, q1);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				if (lastDay!=-1){
					totalDays++;
				}
				days.add(totalDay);
				days2.add(totalDay2);
				
				if (totalDay==1 && hh==h1+offset){
					counted1++;
					//System.out.println(totalDay+" || "+thh.toString());
					if (totalDay2>=1) counted2++;
					
				}
				
				totalDay = 0;
				totalDay2 = 0;
				isValid = false;
				counted = false;
				//counted1 = false;
				lastDay = day;
				
				//System.out.println("****NEW DAY****");
			}
			if (h>=h1 && h<=h2){
				totalDay++;
				if (totalDay==1){
					//System.out.println("[VALID] "+t.toString());
					thh=t;
					hh=h;
				}
				isValid = true;
			}
			if (isValid && h>=h3 && h<=h4){				
				if (totalDay>=1 && !counted){
					totalDay2=1;
					counted = true;
					//System.out.println("[REACHED] "+t.toString()+" || "+count1+" "+count2+" "+PrintUtils.Print2dec(count2*100.0/count1, false));
				}									
			}
		}
		
		ArrayList<Integer> totales = new ArrayList<Integer>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		for (int i=0;i<=10;i++){ totales.add(0);counts.add(0);}
		
		for (int i=0;i<days.size();i++){
			int freq = days.get(i);
			int freq2 = days2.get(i);
			
			if (freq>=10) freq  = 10;
			if (freq==1){
				for (int j=0;j<=0;j++){
					totales.set(j, totales.get(j)+freq2);
					counts.set(j, counts.get(j)+1);
				}
			}
			
			for (int j=1;j<=freq;j++){
				totales.set(j, totales.get(j)+freq2);
				counts.set(j, counts.get(j)+1);
			}
			/*if (freq==1){
				totales.set(1, totales.get(1)+freq2);
				counts.set(1, counts.get(1)+1);
			}*/
			/*if (freq>=1 && freq<20){
				totales.set(freq, totales.get(freq)+freq2);
				counts.set(freq, counts.get(freq)+1);
			}
			if (freq>=20){
				totales.set(20, totales.get(20)+freq2);
				counts.set(20, counts.get(20)+1);
			}*/
		}
		
		String freqs = "";
		for (int i=0;i<=10;i++){
			int total = totales.get(i);
			int count = counts.get(i);
			double per = total*100.0/count;
			double f1 = total*2.0/1000;
			double f2 = count*1.0/1000;
			freqs += PrintUtils.Print2dec(total*100.0/count, false,3)
					+" ("+PrintUtils.Print2Int(total,4)+" / "+PrintUtils.Print2Int(count,4)+" / "+PrintUtils.Print2dec(per*f2,false,4)+
					") ";
		}
		//System.out.println(counted1+" "+counted2+" "+PrintUtils.Print2dec(counted2*100.0/counted1, false));
		System.out.println(
				header
				+" "+PrintUtils.Print2Int(h1, 2)
				+" "+PrintUtils.Print2Int(h2, 2)
				+" "+PrintUtils.Print2Int(h3, 2)
				+" "+PrintUtils.Print2Int(h4, 2)
				+" "+freqs
				);
	}
	
	/*public static void printSummaryByDaily(String header, ArrayList<Trend> trends,int y1,int y2) {
		
		Calendar cal = Calendar.getInstance();
		ArrayList<Double> sizes = new ArrayList<Double>();
		double avgSize = 0;
		int total = 0;
		int lastDay = -1;
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			QuoteShort.getCalendar(cal, t.getQ1());
			int day = cal.get(Calendar.DAY_OF_YEAR);
			//int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				if (last)
			}
		}
		
		avgSize /= total;
		String header1 = header+" || "+PrintUtils.Print2dec(avgSize, false);
		//MathUtils.summary_mean_sd(header1, sizes);
		MathUtils.summary_complete(header1, sizes);
	}*/
	
	public static void printSummaryByHour(String header, ArrayList<Trend> trends,int y1,int y2,int h1,int h2) {
		
		Calendar cal = Calendar.getInstance();
		ArrayList<Double> sizes = new ArrayList<Double>();
		double avgSize = 0;
		int total = 0;
		int lastDay = -1;
		int totalDays = 0;
		for (int i=0;i<trends.size();i++){
			Trend t = trends.get(i);
			QuoteShort.getCalendar(cal, t.getQ1());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			if (y<y1 || y>y2) continue;
			int d = cal.get(Calendar.DAY_OF_YEAR);
			if (d!=lastDay){
				totalDays++;
				lastDay = d;
			}
			
			if (h1<=h && h<=h2){
				double size = trends.get(i).getSize();
				avgSize += trends.get(i).getSize();
				total++;
				sizes.add(size);
				int n = 10;
				double averageN = MathUtils.averageD(sizes, sizes.size()-n, sizes.size()-1);
				//System.out.println(t.toString());
				//if ((sizes.size()-n)>=0)
					//System.out.println(PrintUtils.Print2dec(averageN-200.0,true));
			}
		}
		
		avgSize /= total;
		String header1 = header+" || "+PrintUtils.Print2dec(avgSize, false)+" "+PrintUtils.Print2dec(total*1.0/totalDays, false);
		//MathUtils.summary_mean_sd(header1, sizes);
		//MathUtils.summary_complete(header1, sizes);
		MathUtils.summary_completeCustom(header1, sizes);
	}
	public int getHour0(Calendar cal) {
		QuoteShort.getCalendar(cal, this.q0);
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	public int getHour1(Calendar cal) {
		QuoteShort.getCalendar(cal, this.q1);
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	public int getHour2(Calendar cal) {
		QuoteShort.getCalendar(cal, this.q2);
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
}
