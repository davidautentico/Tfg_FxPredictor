package drosa.auxiliar;

import java.util.ArrayList;

public class MonthPeriod2 extends TimePeriod {

	int bars;
	
	public MonthPeriod2(int bars) {
		time = new ArrayList<TimeFrecuency>(12*bars);
		MonthofYear [] dw = MonthofYear.values();
		for (int i=0;i<12;i++){
			for (int j=0;j<bars;j++){
				MonthFrecuency2 mf = new MonthFrecuency2();					
				mf.setMonth(dw[i]);
				mf.setBars(j);
				mf.setFrecuency(0);
				
				time.add(mf);
			}
		}
	}
	
	public TimeFrecuency findFrec(int month,int bars){
		
		for (int i=0;i<time.size();i++){
			MonthFrecuency2 mf = (MonthFrecuency2) time.get(i);
			if (mf.getMonth().ordinal()==month && mf.getBars()== bars)
				return mf;
		}
		return null;
	}
	
	

}
