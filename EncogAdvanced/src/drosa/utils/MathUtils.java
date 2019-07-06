package drosa.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import drosa.finances.Quote;
import drosa.finances.QuoteShort;

public class MathUtils {
	
	public static double coeficienteR(ArrayList<Integer> x,ArrayList<Integer> y){
		double corr = 0.0;
		
		int sizex = x.size()-1;
		int sizey = y.size()-1;
		
		int size = sizex;
		if (sizey<=sizex)
			size = sizey;
		
		int sumx	= 0;
		int sumy	= 0;
		int sumxy	= 0;
		long sumx_2 	= 0;
		long sumy_2 	= 0;
		for (int i=0;i<=size;i++){
			int valorx = x.get(i);
			int valory = y.get(i);
			
			int xy = valorx*valory;
			
			sumx += valorx;
			sumy += valory;
			sumx_2 += valorx*valorx;
			sumy_2 += valory*valory;
			sumxy	+= xy; 
		}
		
		double avgx = sumx*1.0/(size+1);
		double avgy = sumy*1.0/(size+1);
		double covarianza = (sumxy/(size+1))-avgx*avgy;
		
		double desvx = Math.sqrt(sumx_2/(size+1)-avgx*avgx);
		double desvy = Math.sqrt(sumy_2/(size+1)-avgy*avgy);
		
		//System.out.println(covarianza+" "+desvx+" "+desvy+" "+avgy+" "+sumy_2);
		
		corr = covarianza/(desvx*desvy);
		
		return corr;
	}
	
	public static double Kurtosis(ArrayList<Double> xArray){
		double kurtosis =0.0;
		double n= xArray.size();
		double avg = MathUtils.average(xArray);
		
		double sum1=0.0;
		double sum2=0.0;
		for (int i=0;i<xArray.size();i++){
			double x=xArray.get(i);
			double diff = x-avg;
			sum1+=Math.pow(diff, 4);
			sum2+=Math.pow(diff, 2);
		}
		sum1=sum1/n;
		sum2=Math.pow(sum2/n,2);
		
		kurtosis = (sum1/sum2)-3;
		
		return kurtosis;
	}
	
	public static double covariance(double[] v1,double[] v2){
		double cov=99999.0;
		
		if (v1==null || v2==null) return 999999;
		
		int n1 = v1.length;
		int n2 = v2.length;
		//System.out.println("n1 n2 "+n1+" "+n2);
		double mean1 = 0.0;
		double mean2 = 0.0;
		double sum1=0.0;
		double sum2=0.0;
		for (int i=0;i<n1;i++){
			sum1+=v1[i];
			sum2+=v2[i];
			//System.out.println("sin acos "+v1[i]+" "+v2[i]);
		}
		mean1 = sum1/n1;
		mean2 = sum2/n1;
		//System.out.println("mean1 mean2 "+mean1+" "+mean2);
		double accum=0;
		for (int i=0;i<n1;i++){
			accum+=(v1[i]-mean1)*(v2[i]-mean2);
		}
		cov = accum/(n1-1);
				
		return cov;
	}
	
	public static double average(List<Double> values){
		
		double total=0;
		for (int i=0;i<values.size();i++){
			total+=values.get(i);
		}
		
		return total*1.0/values.size();
	}
	
	public static double average(ArrayList<Integer> values){
		
		double total=0;
		for (int i=0;i<values.size();i++){
			total+=values.get(i);
		}
		
		return total/values.size();
	}
	
	public static double average(ArrayList<Integer> values,boolean isAbs){
		
		double total=0;
		for (int i=0;i<values.size();i++){
			if (!isAbs){
				total+=values.get(i);
			}else{
				total+=Math.abs(values.get(i));
			}
		}
		
		return total/values.size();
	}
	
    public static double SSMA(ArrayList<Integer> values,int begin,int end){
		
		if (begin<0) begin = 0;
		if (end>values.size()-1) end = values.size()-1;
		double total=0;
		int count = 0;
		ArrayList<Integer> smas = new ArrayList<Integer>();
		for (int i=begin;i<=end;i++){
			total+=values.get(i);
			count++;
			
			smas.add((int) (total*1.0/count));
		}
		
		//double previousSum = total -values.get(end);
		//double previousAverage = previousAverage 		
		//int ssmaN = total - values.get((end)
		
		//SMMA =  (PREVIOUS SUM - PREVIOUS AVG + PRICE(END)) / PERIOD
		return total*1.0/count;
	}
	
	public static double average(ArrayList<Integer> values,int begin,int end){
		
		if (begin<0) begin = 0;
		if (end>values.size()-1) end = values.size()-1;
		double total=0;
		int count = 0;
		for (int i=begin;i<=end;i++){
			total+=values.get(i);
			count++;
		}
		
		return total*1.0/count;
	}
	
	public static double average(ArrayList<QuoteShort> values,int begin,int end,boolean isClose){
		
		if (begin<0) begin = 0;
		if (end>values.size()-1) end = values.size()-1;
		double total=0;
		int count = 0;
		for (int i=begin;i<=end;i++){
			if (isClose)
				total+=values.get(i).getClose5();
			else
				total+=values.get(i).getOpen5();
			count++;
		}
		
		return total*1.0/count;
	}
	
	public static double averageD(ArrayList<Double> values,int begin,int end){
		
		if (begin<0) begin = 0;
		if (end>values.size()-1) end = values.size()-1;
		double total=0;
		int count = 0;
		for (int i=begin;i<=end;i++){
			total+=values.get(i);
			count++;
		}
		
		return total*1.0/count;
	}
	
	public static double autocorr(List<Double> deltaP,List<Double> deltaPLag){
		double corr=0;
		
		double varx = variance(deltaP);
		double vary = variance(deltaPLag);
		
		corr=  covariance(deltaP,deltaPLag)/Math.sqrt(varx*vary);
		return corr;
	}
	
	public static double covariance(List<Double> deltaP,List<Double> deltaPLag){
		double avgx = average(deltaP);
		double avgy = average(deltaPLag);
		//System.out.println("mediax,mediay: "+avgx+","+avgy);
		double sum=0;
		
		for (int i=0;i<deltaP.size();i++){
			sum+= (deltaP.get(i))*(deltaPLag.get(i));
		}
		
		return (sum/deltaP.size())-avgx*avgy;
	}
	
	public static double variance(List<Double> deltaP){
		double avg = average(deltaP);
		
		double sum=0;
		for (int i=0;i<deltaP.size();i++){
			sum+= Math.pow(deltaP.get(i)-avg,2);
		}
		
		return sum/(deltaP.size()-1);
	}
	
	public static double varianceD(ArrayList<Double> deltaP){
		double avg = average(deltaP);
		
		double sum=0;
		for (int i=0;i<deltaP.size();i++){
			sum+= Math.pow(deltaP.get(i)-avg,2);
		}
		
		return sum/(deltaP.size()-1);
	}
	

	
	public static double variance(ArrayList<Integer> deltaP){
		double avg = average(deltaP);
		
		double sum=0;
		for (int i=0;i<deltaP.size();i++){
			sum+= Math.pow(deltaP.get(i)-avg,2);
		}
		
		return sum/(deltaP.size()-1);
	}
	
	public static double variance(ArrayList<Integer> deltaP,int begin,int end){
		
		if (begin<=0) begin = 0;
		if (end>=deltaP.size()-1) end = deltaP.size()-1;
		
		double avg = average(deltaP,begin,end);
		
		double sum=0;
		for (int i=begin;i<=end;i++){
			sum+= Math.pow(deltaP.get(i)-avg,2);
		}
		
		return sum/(deltaP.size()-1);
	}
	
	public static float Round(float Rval, int Rpl) {
		  float p = (float)Math.pow(10,Rpl);
		  Rval = Rval * p;
		  float tmp = Math.round(Rval);
		  return (float)tmp/p;
	}
	
	 /**
     * Return a random number within the specified range
     * 
     * @param lower range
     * @param upper range
     * @return a random number within the specified range
     */
    public static synchronized double getBoundedRandom(double lower, double upper) {
    	double range = upper - lower;
    	double result = _random.nextDouble() * range + lower;
    	return(result);
    }
    
    /**
     * Return a random double
     * 
     * @return random double
     */
    public static synchronized double getRandomDouble() {
    	return(_random.nextDouble());
    }
    
    /**
     * Convert a double array to a thresholded array
     * 
     * @param threshold value
     * @param candidatez raw values to threshold
     * @return 0, 1 or -1 (if failed threshold)
     */
    public static synchronized int[] thresholdArray(double threshold, double[] candidatez) {
    	double upper_threshold = 1.0 - threshold;
    	double lower_threshold = threshold;
	
    	int[] result = new int[candidatez.length];
	
    	for (int ii = 0; ii < candidatez.length; ii++) {
    		if (candidatez[ii] > upper_threshold) {
    			result[ii] = 1;
    		} else if (candidatez[ii] < lower_threshold) {
    			result[ii] = 0;
    		} else {
    			result[ii] = -1;
    		}
    	}
	
    	return(result);
    }
    
    /**
     * Duh.
     */
    private final static Random _random = new Random();

	public static double log2(double x) {
		// TODO Auto-generated method stub
		return Math.log(x)/Math.log(2);
	}

	public static double mean(double[] x) {
		// TODO Auto-generated method stub
		double accum=0.0;
		for (int i=0;i<x.length;i++){
			accum += x[i];
		}
		
		return accum/x.length;
	}
	

	public static void substract(double[] ySerie, double[] xSerie, double m) {
		// TODO Auto-generated method stub
		for (int i=0;i<xSerie.length;i++){
			ySerie[i] = xSerie[i]-m;
		}
	}

	public static void cummulativeSum(double[] zSerie, double[] ySerie) {
		// TODO Auto-generated method stub
		double accum=0.0;
		for (int i=0;i<ySerie.length;i++){
			accum += ySerie[i];
			zSerie[i]=accum;
		}
	}

	public static void summary(String header, ArrayList<Integer> values,int begin,int end) {
		// TODO Auto-generated method stub
		double mean = MathUtils.average(values,begin,end);
		double sd   = Math.sqrt(MathUtils.variance(values,begin,end));
		double sd2 = sd*2.0;
		double sd3 = sd*3.0;
		int total1=0;
		int total2=0;
		int total3=0;
		int max = -1;
		int min = -1;
		for (int i=begin;i<=end;i++){
			if (values.get(i)<=(mean+sd)) total1++;
			if (values.get(i)<=(mean+sd2)) total2++;
			if (values.get(i)<=(mean+sd3)) total3++;
			if (max==-1 || values.get(i)>=max) max = values.get(i);
			if (min==-1 || values.get(i)<=min) min = values.get(i);
		}
		int total = end-begin+1;
		double factor = mean*1.0/sd;
		System.out.println(header+" total= "+total+" mean="+PrintUtils.Print2dec(mean, false)
				+" sd="+PrintUtils.Print2dec(sd, false)
				+" mean/sd = "+PrintUtils.Print2dec(factor, false)
				+" max = "+max
				+" min = "+min
				+" <sd("+PrintUtils.Print2dec(mean+sd,false)+") "+PrintUtils.Print2dec(total1*100.0/total, false)+"%"
				+" <2sd("+PrintUtils.Print2dec(mean+2.0*sd,false)+") "+PrintUtils.Print2dec(total2*100.0/total, false)+"%"
				+" <3sd("+PrintUtils.Print2dec(mean+3.0*sd,false)+") "+PrintUtils.Print2dec(total3*100.0/total, false)+"%"
				);
	}
	
	public static void summary(String header, ArrayList<Integer> values) {
		// TODO Auto-generated method stub
		double mean = MathUtils.average(values);
		double sd   = Math.sqrt(MathUtils.variance(values));
		double sd2 = sd*2.0;
		double sd3 = sd*3.0;
		int total1=0;
		int total2=0;
		int total3=0;
		int max = -1;
		int min = -1;
		for (int i=0;i<=values.size()-1;i++){
			if (values.get(i)<=(mean+sd)) total1++;
			if (values.get(i)<=(mean+sd2)) total2++;
			if (values.get(i)<=(mean+sd3)) total3++;
			if (max==-1 || values.get(i)>=max) max = values.get(i);
			if (min==-1 || values.get(i)<=min) min = values.get(i);
		}
		
		double factor = mean*1.0/sd;
		System.out.println(header+" total= "+values.size()+" mean="+PrintUtils.Print2dec(mean, false)
				+" sd="+PrintUtils.Print2dec(sd, false)
				+" mean/sd = "+PrintUtils.Print2dec(factor, false)
				+" max = "+max
				+" min = "+min
				//+" <sd("+PrintUtils.Print2dec(mean+sd,false)+") "+PrintUtils.Print2dec(total1*100.0/values.size(), false)+"%"
				//+" <2sd("+PrintUtils.Print2dec(mean+2.0*sd,false)+") "+PrintUtils.Print2dec(total2*100.0/values.size(), false)+"%"
				//+" <3sd("+PrintUtils.Print2dec(mean+3.0*sd,false)+") "+PrintUtils.Print2dec(total3*100.0/values.size(), false)+"%"
				);
	}
	
	public static void summary_completeInt(String header, ArrayList<Integer> values) {
		// TODO Auto-generated method stub
		if (values.size()==0) return;
		double mean = MathUtils.average(values);
		double sd   = Math.sqrt(MathUtils.variance(values));
		double sd2 = sd*2.0;
		double sd3 = sd*3.0;
		int total_0=0;
		int total_1=0;
		int total_15=0;
		int total1=0;
		int total2=0;
		int total3=0;
		int totalPositives = 0;
		double best = values.get(0);
		double worst = values.get(0);
		for (int i=0;i<values.size();i++){
			if (values.get(i)<=(mean)) total_0++;
			if (values.get(i)<=(mean-sd)) total_1++;
			if (values.get(i)<=(mean-sd*0.5)) total_15++;
			if (values.get(i)<=(mean+sd)) total1++;
			if (values.get(i)<=(mean+sd2)) total2++;
			if (values.get(i)<=(mean+sd3)) total3++;
			if (values.get(i)>=0) totalPositives++;
			
			if (values.get(i)>best) best = values.get(i);
			if (values.get(i)<worst) worst = values.get(i);
		}
		
		double posPer = totalPositives*100.0/values.size();
		System.out.println(header+" total= "+values.size()
				+" totalPositive= "+PrintUtils.Print2(posPer)
				+" best= "+PrintUtils.Print2dec(best, false, 3)
				+" worst= "+PrintUtils.Print2dec(worst, false, 3)
				+" mean="+PrintUtils.Print2dec(mean, false, 3)
				+" sd="+PrintUtils.Print2dec(sd, false)
				//+" <mean-sd("+PrintUtils.Print2dec(mean-sd,false)+") "+PrintUtils.Print2dec(total_1*100.0/values.size(), false)+"%"
				//+" <mean-sd*0.5("+PrintUtils.Print2dec(mean-sd*0.5,false)+") "+PrintUtils.Print2dec(total_15*100.0/values.size(), false)+"%"
				+" <mean("+PrintUtils.Print2dec(mean,false)+") "+PrintUtils.Print2dec(total_0*100.0/values.size(), false)+"%"
				+" <sd("+PrintUtils.Print2dec(mean+sd,false)+") "+PrintUtils.Print2dec(total1*100.0/values.size(), false)+"%"
				+" <2sd("+PrintUtils.Print2dec(mean+2.0*sd,false)+") "+PrintUtils.Print2dec(total2*100.0/values.size(), false)+"%"
				+" <3sd("+PrintUtils.Print2dec(mean+3.0*sd,false)+") "+PrintUtils.Print2dec(total3*100.0/values.size(), false)+"%"
				);
	}
	
	public static void summary_complete(String header, ArrayList<Double> values) {
		// TODO Auto-generated method stub
		if (values.size()==0) return;
		double mean = MathUtils.average(values);
		double sd   = Math.sqrt(MathUtils.variance(values));
		double sd2 = sd*2.0;
		double sd3 = sd*3.0;
		int total_0=0;
		int total_1=0;
		int total_15=0;
		int total1=0;
		int total2=0;
		int total3=0;
		int totalPositives = 0;
		double best = values.get(0);
		double worst = values.get(0);
		for (int i=0;i<values.size();i++){
			if (values.get(i)<=(mean)) total_0++;
			if (values.get(i)<=(mean-sd)) total_1++;
			if (values.get(i)<=(mean-sd*0.5)) total_15++;
			if (values.get(i)<=(mean+sd)) total1++;
			if (values.get(i)<=(mean+sd2)) total2++;
			if (values.get(i)<=(mean+sd3)) total3++;
			if (values.get(i)>=0) totalPositives++;
			
			if (values.get(i)>best) best = values.get(i);
			if (values.get(i)<worst) worst = values.get(i);
		}
		
		double posPer = totalPositives*100.0/values.size();
		System.out.println(header+" total= "+values.size()
				+" totalPositive= "+PrintUtils.Print2(posPer)
				+" best= "+PrintUtils.Print2dec(best, false, 3)
				+" worst= "+PrintUtils.Print2dec(worst, false, 3)
				+" mean="+PrintUtils.Print2dec(mean, false, 3)
				+" sd="+PrintUtils.Print2dec(sd, false)
				+" <mean-sd("+PrintUtils.Print2dec(mean-sd,false)+") "+PrintUtils.Print2dec(total_1*100.0/values.size(), false)+"%"
				+" <mean-sd*0.5("+PrintUtils.Print2dec(mean-sd*0.5,false)+") "+PrintUtils.Print2dec(total_15*100.0/values.size(), false)+"%"
				+" <mean("+PrintUtils.Print2dec(mean,false)+") "+PrintUtils.Print2dec(total_0*100.0/values.size(), false)+"%"
				+" <sd("+PrintUtils.Print2dec(mean+sd,false)+") "+PrintUtils.Print2dec(total1*100.0/values.size(), false)+"%"
				+" <2sd("+PrintUtils.Print2dec(mean+2.0*sd,false)+") "+PrintUtils.Print2dec(total2*100.0/values.size(), false)+"%"
				+" <3sd("+PrintUtils.Print2dec(mean+3.0*sd,false)+") "+PrintUtils.Print2dec(total3*100.0/values.size(), false)+"%"
				);
	}
	
	public static void summary_completeCustom(String header, ArrayList<Double> values) {
		// TODO Auto-generated method stub
		if (values.size()==0) return;
		double mean = MathUtils.average(values);
		double sd   = Math.sqrt(MathUtils.variance(values));
		double sd2 = sd*2.0;
		double sd3 = sd*3.0;
		int total_0=0;
		int total_1=0;
		int total_15=0;
		int total1=0;
		int total2=0;
		int total3=0;
		int total4=0;
		int total5=0;
		int totalPositives = 0;
		double best = values.get(0);
		double worst = values.get(0);
		for (int i=0;i<values.size();i++){
			if (values.get(i)<=(30)) total_0++;
			if (values.get(i)<=(40)) total_1++;
			if (values.get(i)<=(50)) total_15++;
			if (values.get(i)<=(60)) total1++;
			if (values.get(i)<=(70)) total2++;
			if (values.get(i)<=(80)) total3++;
			if (values.get(i)<=(90)) total4++;
			if (values.get(i)<=(100)) total5++;
			if (values.get(i)>=0) totalPositives++;
			
			if (values.get(i)>best) best = values.get(i);
			if (values.get(i)<worst) worst = values.get(i);
		}
		
		double posPer = totalPositives*100.0/values.size();
		System.out.println(header+" total= "+values.size()
				+" totalPositive= "+PrintUtils.Print2(posPer)
				+" best= "+PrintUtils.Print2dec(best, false, 3)
				+" worst= "+PrintUtils.Print2dec(worst, false, 3)
				+" mean="+PrintUtils.Print2dec(mean, false, 3)
				+" sd="+PrintUtils.Print2dec(sd, false)
				+" <30("+PrintUtils.Print2dec(30,false)+") "+PrintUtils.Print2dec(total_0*100.0/values.size(), false)+"%"
				+" <40("+PrintUtils.Print2dec(40,false)+") "+PrintUtils.Print2dec(total_1*100.0/values.size(), false)+"%"
				+" <50("+PrintUtils.Print2dec(50,false)+") "+PrintUtils.Print2dec(total_15*100.0/values.size(), false)+"%"
				+" <60("+PrintUtils.Print2dec(60,false)+") "+PrintUtils.Print2dec(total1*100.0/values.size(), false)+"%"
				+" <70("+PrintUtils.Print2dec(70,false)+") "+PrintUtils.Print2dec(total2*100.0/values.size(), false)+"%"
				+" <80("+PrintUtils.Print2dec(80,false)+") "+PrintUtils.Print2dec(total3*100.0/values.size(), false)+"%"
				+" <90("+PrintUtils.Print2dec(90,false)+") "+PrintUtils.Print2dec(total4*100.0/values.size(), false)+"%"
				+" <100("+PrintUtils.Print2dec(100,false)+") "+PrintUtils.Print2dec(total5*100.0/values.size(), false)+"%"
				);
	}
	
	public static void summary_mean_sd(String header, ArrayList<Double> values) {
		// TODO Auto-generated method stub
		if (values.size()==0) return;
		double mean = MathUtils.average(values);
		double sd   = Math.sqrt(MathUtils.variance(values));
		double sd2 = sd*2.0;
		double sd3 = sd*3.0;
		int total1=0;
		int total2=0;
		int total3=0;
		int totalPositives = 0;
		double best = values.get(0);
		double worst = values.get(0);
		for (int i=0;i<values.size();i++){
			if (values.get(i)<=(mean+sd)) total1++;
			if (values.get(i)<=(mean+sd2)) total2++;
			if (values.get(i)<=(mean+sd3)) total3++;
			if (values.get(i)>=0) totalPositives++;
			
			if (values.get(i)>best) best = values.get(i);
			if (values.get(i)<worst) worst = values.get(i);
		}
		
		double posPer = totalPositives*100.0/values.size();
		System.out.println(header+" || total= "+values.size()
				+" totalPositive= "+PrintUtils.Print2(posPer)
				+" best= "+PrintUtils.Print2dec(best,false)
				+" worst= "+PrintUtils.Print2dec(worst,false)
				+" mean="+PrintUtils.Print2dec(mean, false)
				+" sd="+PrintUtils.Print2dec(sd, false)
				+" meanSdRatio="+PrintUtils.Print2dec(mean/sd, false)
				);
	}

	public static ArrayList<Double> calculateAtr(ArrayList<Quote> dailyData,
			int atrPeriod) {
		// TODO Auto-generated method stub
		ArrayList<Double> atr = new ArrayList<Double>();
		
		for (int i=0;i<dailyData.size();i++){
			double avg = calculateAvg(dailyData,i-atrPeriod,i-1);
			atr.add(avg);
		}
		
		return atr;
	}

	private static double calculateAvg(ArrayList<Quote> dailyData, int begin, int end) {
		// TODO Auto-generated method stub
		
		if (begin<=0) begin = 0;
		if (end>=dailyData.size()-1) end = dailyData.size()-1;
		
		double avg=0;
		int total=0;
		for (int i=begin;i<=end;i++){
			Quote q = dailyData.get(i);
			int range = TradingUtils.getPipsDiff(q.getHigh(), q.getLow());
			avg+=range;
			total++;
		}
		return avg*1.0/total;
	}

	public static void summary2(String header, ArrayList<Double> values) {
		// TODO Auto-generated method stub
		double mean = MathUtils.average(values);
		int count10=0;
		int count20=0;
		int count30=0;
		int count40=0;
		int count50=0;
		int count60=0;
		int count70=0;
		int count75=0;
		int count80=0;
		int count90=0;
		
		for (int i=0;i<values.size();i++){
			double val = values.get(i);
			if (val>=10) count10++;
			if (val>=20) count20++;
			if (val>=30) count30++;
			if (val>=40) count40++;
			if (val>=50) count50++;
			if (val>=60) count60++;
			if (val>=70) count70++;
			if (val>=75) count75++;
			if (val>=80) count80++;
			if (val>=90) count90++;
		}
		
		System.out.println(header+" mean="+PrintUtils.Print2dec(mean, false)
				+" >=10%= "+PrintUtils.Print2dec(count10*100.0/values.size(), false)
				+" >=20%= "+PrintUtils.Print2dec(count20*100.0/values.size(), false)
				+" >=30%= "+PrintUtils.Print2dec(count30*100.0/values.size(), false)
				+" >=40%= "+PrintUtils.Print2dec(count40*100.0/values.size(), false)
				+" >=50%= "+PrintUtils.Print2dec(count50*100.0/values.size(), false)
				+" >=60%= "+PrintUtils.Print2dec(count60*100.0/values.size(), false)
				+" >=70%= "+PrintUtils.Print2dec(count70*100.0/values.size(), false)
				+" >=80%= "+PrintUtils.Print2dec(count80*100.0/values.size(), false)
				+" >=90%= "+PrintUtils.Print2dec(count90*100.0/values.size(), false)
				);
	}

	public static String printArray(ArrayList<Double> values){
		
		String res = "";
		
		for (int i=0;i<values.size();i++){
			res+=" "+PrintUtils.Print2dec(values.get(i), false);
		}
		
		return res;
	}
	public static String PrintPercents(ArrayList<Integer> distances) {
		// TODO Auto-generated method stub
		int count5 = 0;
		int count10 = 0;
		int count15 = 0;
		int count20 = 0;
		int count25 = 0;
		int count30 = 0;
		int count35 = 0;
		int count40 = 0;
		int count45 = 0;
		int count50 = 0;
		for (int i=0;i<distances.size();i++){
			int dist = distances.get(i);
			
			if (dist<=5) count5++;
			if (dist<=10) count10++;
			if (dist<=15) count15++;
			if (dist<=20) count20++;
			if (dist<=25) count25++;
			if (dist<=30) count30++;
			if (dist<=35) count35++;
			if (dist<=40) count40++;
			if (dist<=45) count45++;
			if (dist<=50) count50++;
		}
		int total = distances.size();
		double per5 = count5*100.0/total;
		double per10 = count10*100.0/total;
		double per15 = count15*100.0/total;
		double per20 = count20*100.0/total;
		double per25 = count25*100.0/total;
		double per30 = count30*100.0/total;
		double per35 = count35*100.0/total;
		double per40 = count40*100.0/total;
		double per45 = count45*100.0/total;
		double per50 = count50*100.0/total;
		
		String res = PrintUtils.Print2(per5)+"%"
				+" "+PrintUtils.Print2(per10)+"%"
				+" "+PrintUtils.Print2(per15)+"%"
				+" "+PrintUtils.Print2(per20)+"%"
				+" "+PrintUtils.Print2(per25)+"%"
				+" "+PrintUtils.Print2(per30)+"%"
				+" "+PrintUtils.Print2(per35)+"%"
				+" "+PrintUtils.Print2(per40)+"%"
				+" "+PrintUtils.Print2(per45)+"%"
				+" "+PrintUtils.Print2(per50)+"%"
				;
		return res;
	}

	public static int sum(ArrayList<Integer> data, int begin, int end) {
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-1;
		int total = 0;
		for (int i=begin;i<=end;i++){
			total += data.get(i);
		}
		return total;
	}
	
	public static int countPositives(ArrayList<Integer> data, int begin, int end) {
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-1;
		int total = 0;
		for (int i=begin;i<=end;i++){
			if (data.get(i)>0){
				total++;
			}
		}
		return total;
	}

	public static double calcualteWinPer(ArrayList<Integer> results, int begin, int end) {
		// TODO Auto-generated method stub
		
		if (begin<=0) begin = 0;
		if (end>=results.size()-1) end = results.size()-1;
		
		int count = 0;
		int wins = 0;
		for (int i=begin; i<=end;i++){
			if (results.get(i)==1) wins++;
			count++;
		}
		
		
		if (count==0) return 0;
		
		return wins*100.0/count;
	}

	public static double calculateSlope(ArrayList<Integer> days, int nDays, int nPoints) {
		// TODO Auto-generated method stub
		
		ArrayList<Double> avgs = new ArrayList<Double>();
		for (int p=1;p<=nPoints;p++){
			double ma = MathUtils.average(days, days.size()-(nDays + p-1), days.size()-p);
			avgs.add(ma);
		}
		
		int count = 0;
		double accPendiente = 0;
		for (int i=0;i<avgs.size()-1;i++){
			double avg0 = avgs.get(i);
			double avg1 = avgs.get(i+1);
			accPendiente += avg0-avg1;
			count++;
		}
		
		
		return accPendiente/count;
	}
	
	public static int calculateSlope2(ArrayList<Integer> days, int nDays, int nPoints) {
		// TODO Auto-generated method stub
		
		ArrayList<Double> avgs = new ArrayList<Double>();
		for (int p=1;p<=nPoints;p++){
			double ma = MathUtils.average(days, days.size()-(nDays + p-1), days.size()-p);
			avgs.add(ma);
		}
		
		int count = 0;
		for (int i=0;i<avgs.size()-1;i++){
			double avg0 = avgs.get(i);
			double avg1 = avgs.get(i+1);
			if (avg0>=avg1) count++;
			else count--;
			//System.out.println(i+" "+avg0+" "+avg1+" "+count );
		}
		
		
		return count;
	}

	public static int getMaxMin(ArrayList<Integer> maxMins, int begin, int end, int mode) {
		// TODO Auto-generated method stub
				
		if (begin<=0) begin = 0;		
		if (end>=maxMins.size()-1) end = maxMins.size()-1;
		
		int maxMin = 9999;
		if (mode==1) maxMin = -9999;
		for (int i=begin;i<=end;i++){
			
			if (mode==1){
				if (maxMins.get(i)>=maxMin) maxMin = maxMins.get(i);
			}else{
				if (maxMins.get(i)<=maxMin) maxMin = maxMins.get(i); 
			}
		}
		
		return maxMin;
	}
	
	/*public static double calculateSlope(ArrayList<Integer> days, int nDays, int nPoints) {
		// TODO Auto-generated method stub
		
		ArrayList<Double> avgs = new ArrayList<Double>();
		for (int p=1;p<=nPoints;p++){
			double ma = MathUtils.average(days, days.size()-(nDays + p-1), days.size()-p);
			avgs.add(ma);
		}
		
		int count = 0;
		double accPendiente = 0;
		for (int i=0;i<avgs.size()-1;i++){
			double avg0 = avgs.get(i);
			double avg1 = avgs.get(i+1);
			accPendiente += avg0-avg1;
			count++;
		}
		
		
		return accPendiente/count;
	}*/
}
