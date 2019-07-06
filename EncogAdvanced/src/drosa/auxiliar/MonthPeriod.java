package drosa.auxiliar;

import java.util.ArrayList;
import drosa.auxiliar.MonthofYear;

public class MonthPeriod extends TimePeriod {
	
	public MonthPeriod(){
		time = new ArrayList<TimeFrecuency>(12);
		MonthofYear [] dw = MonthofYear.values();
		for (int i=0;i<12;i++){
			MonthFrecuency mf = new MonthFrecuency();					
			mf.setMonth(dw[i]);
			mf.setFrecuency(0);
			
			time.add(mf);
		}
	}	
}
