package drosa.neuralNetwork;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.Quote;

public class NNPreprocessing {

	
	public static ArrayList<NNElement> preprocessing(ArrayList<Quote> data, double tp,double sl,int maxRetrace){
		
		ArrayList<NNElement> preprocessedData = new ArrayList<NNElement> ();
		
		Calendar cal = Calendar.getInstance();
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTimeInMillis(q.getDate().getTime());
			
			int begin = i-maxRetrace;
			if (begin<0) begin = 0;
			int end = i-1;
			int maxMin = 0;
			double max = -99;
			double min = 99;
			int mode = 0;
			int period = 0;
			int lastPeriod = 0;
			for (int j=end;j>=begin;j--){
				Quote qj = data.get(j);
				if (mode>=0 && qj.getHigh()<q.getHigh()){
					mode = 1;
					period++;
				}
				if (mode<=0 && qj.getLow()>q.getLow()){
					mode = -1;
					period++;
				}
				if (period<=lastPeriod) break; //salimos del bucle
				lastPeriod = period;
			}
			//formamos nnElement			
			if (mode==1){ //high		
				
			}else if (mode==-1){//low
				
			}
		}
		
		
		return preprocessedData;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
