package drosa.random;

import java.util.ArrayList;

import drosa.finances.Quote;
import drosa.utils.PrintUtils;

public class NonParametricTest {
	
	
	private static double expectedRuns(int n1,int n2){
		
		return (2.0*n1*n2/(n1+n2))+1;
	}
	
	private static double standardDeviation(int n1,int n2){
		
		double num = 2.0*n1*n2*(2.0*n1*n2-n1-n2);
		double den = Math.pow(n1+n1,2.0)*(n1+n2-1);
		
		return Math.sqrt(num/den);
	}
	
	private static double ZScore(double u,double expRuns,double sd){
		
		return (Math.abs(u-expRuns)-0.5)/sd;
		
	}
	/**
	 * 
	 * @param header
	 * @param data
	 * @param minTick
	 * @return 1:random,0:non random
	 */
	public static int doTest(String header,ArrayList<Quote> data,double minTick){
		int n1=0;//positives
		int n2=0;//negatives
		double zscore=0.0;
		double u=0.0;
		double expRuns=0.0;
		double sd=0.0;
		
		//calculate u,n1,n2
		int lastState = -2;
		int actualState=-2;
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			Quote q_1 = data.get(i-1);
			double diff = q.getClose()-q_1.getClose();
			if (diff>=minTick){
				n1++;
				actualState=1;
			}else if (diff<=-minTick){
				n2++;
				actualState=-1;
			}else if (diff==0){
				actualState=0;
			}
			
			if (actualState!=0 && lastState!=actualState)
				u+=1.0;
			
			/*System.out.println(PrintUtils.Print(q_1.getClose())+
					" "+PrintUtils.Print(q.getClose())+
					" "+PrintUtils.Print(diff)+
					" "+PrintUtils.Print(u)
					+" "+n1+" "+n2
					);*/
			
			lastState = actualState;
		}
		
		//zscore calculate
		expRuns=expectedRuns(n1,n2);
		sd=standardDeviation(n1,n2);
		zscore = ZScore(u,expRuns,sd);
		
		String text = header+" "+"expected sd u n1 n2 zscore "+
				PrintUtils.Print(expRuns)+
				" "+PrintUtils.Print(sd)+
				" "+PrintUtils.Print(u)+
				" "+n1+" "+n2+" "+PrintUtils.Print(zscore);
		
		int res=0;
		if (zscore<1.645){
			text+=" "+'R';
			res=1;
		}else{//with 95% confidence
			text+=" "+'P';
			res = 0;
		}
		System.out.println(text);
		return res;
	}

}
