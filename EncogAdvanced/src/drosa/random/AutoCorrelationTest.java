package drosa.random;

import java.util.ArrayList;
import drosa.finances.Quote;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class AutoCorrelationTest {
	
		
	public static void doTest(String header, ArrayList<Quote> data,int beginLag,int maxLag){
	
		ArrayList<Double> changes = new ArrayList<Double>();
		ArrayList<Double> deltaP = new ArrayList<Double>();
		ArrayList<Double> deltaPLag = new ArrayList<Double>();
		double autocorr=0;
		int sigTotal=0;
		int pointsTotal=0;
		
		//calculate canges
		for (int i=0;i<data.size()-1;i++){	//for each bar
			Quote q = data.get(i);
			Quote q_1 = data.get(i+1);			
			double change = 100.0-(q_1.getClose()*100.0/q.getClose());
			changes.add(change);		
			//System.out.println("change: "+change);
		}
		int nbars = changes.size();
		double sigValue = (double) (2.0/Math.sqrt(nbars));
		double maxAuto=0;
		double sigLag=-9999;
		for (int t=beginLag;t<=maxLag;t++){ //for each lag
			deltaP.clear();
			deltaPLag.clear();
			for (int j=changes.size()-1;j>=t;j--){
				deltaP.add(changes.get(j));
				deltaPLag.add(changes.get(j-t));
			}
			double value1 = MathUtils.covariance(deltaP,deltaPLag); 
			double value2 = MathUtils.variance(deltaP);
			double value3 = MathUtils.variance(deltaPLag);
			
			//System.out.println("covarianza: "+value1);
			//System.out.println("varianza1: "+value2);
			//System.out.println("varianza2: "+value3);
	
			if (value2 > 0)
				autocorr = MathUtils.autocorr(deltaP,deltaPLag);
				
			
	
			if (Math.abs(autocorr)>Math.abs(maxAuto)){
				maxAuto = Math.abs(autocorr);
				sigLag=t;
			}					
			
			
			if (Math.abs(maxAuto)>sigValue){
				//System.out.println("es significativo con lag: "+sigLag+" umbrales +-"+sigValue);
				sigTotal++;
			}
		}
		String text = header;
		header+=" sigValue autocorr res "
				+PrintUtils.Print(sigValue)
				+" "+PrintUtils.Print(maxAuto);
		//System.out.println("maxauto: "+maxAuto);
		if (Math.abs(maxAuto)>sigValue){
			//System.out.println("es significativo con lag: "+sigLag+" umbrales +-"+sigValue);
			header+=" P";
		}else{
			header+=" R";
		}
		System.out.println(header);
	}
}
