package drosa.auxiliar;

import java.util.ArrayList;

import java.util.List;

public class WeekPeriod extends TimePeriod{
	
	public WeekPeriod(){
		time = new ArrayList<TimeFrecuency>(7);
		DayofWeek [] dw = DayofWeek.values();
		for (int i=0;i<7;i++){
			DayFrecuency df = new DayFrecuency();					
			df.setDay(dw[i]);
			df.setFrecuency(0);
			
			time.add(df);
		}
	}			
}
