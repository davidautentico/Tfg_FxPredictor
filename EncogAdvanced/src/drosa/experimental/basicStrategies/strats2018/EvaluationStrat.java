package drosa.experimental.basicStrategies.strats2018;

public class EvaluationStrat {
	
	
	public static double getScore(double factorPDD,double maxDD,double tradingDaysPer){
		
		double score = 0.0;		
		double maxDDScore = 0.0;
		double tradingDaysPerScore = 0.0;
		double factorPDDScore = 0.0;
		
		/*if (maxDD>=90.0) maxDDScore = 0.0;
		else if (maxDD>=80.0) maxDDScore = 1.0;
		else if (maxDD>=70.0) maxDDScore = 2.0;
		else if (maxDD>=60.0) maxDDScore = 3.0;
		else if (maxDD>=50.0) maxDDScore = 4.0;
		else if (maxDD>=40.0) maxDDScore = 5.0;
		else if (maxDD>=30.0) maxDDScore = 6.0;
		else if (maxDD>=20.0) maxDDScore = 7.0;
		else if (maxDD>=15.0) maxDDScore = 8.0;
		else if (maxDD>=10.0) maxDDScore = 9.0;
		else if (maxDD<10.0) maxDDScore = 10.0;*/
		
		
		if (factorPDD>=4.0) factorPDDScore = 10.0;
		else if (factorPDD>=3.5)factorPDDScore= 9.0;
		else if (factorPDD>=3.0)factorPDDScore= 8.0;
		else if (factorPDD>=2.5)factorPDDScore= 7.5;
		else if (factorPDD>=2.0)factorPDDScore= 7.0;
		else if (factorPDD>=1.5)factorPDDScore= 6.5;
		else if (factorPDD>=1.0)factorPDDScore= 6.0;
		else if (factorPDD>=0.5)factorPDDScore= 5.5;
		else if (factorPDD>=0.0)factorPDDScore= 5.0;
		else if (factorPDD>=-0.5)factorPDDScore= 1.0;
		else if (factorPDD>=-1.0)factorPDDScore= 0.0;
		else if (factorPDD<-1.0)factorPDDScore= 0.0;
		
		if (tradingDaysPer>=40.0) tradingDaysPerScore = 10.0;
		else if (tradingDaysPerScore>=35.0)tradingDaysPerScore= 9.0;
		else if (tradingDaysPerScore>=30.0)tradingDaysPerScore= 8.5;
		else if (tradingDaysPerScore>=25.0)tradingDaysPerScore= 8.0;
		else if (tradingDaysPerScore>=20.0)tradingDaysPerScore= 7.0;
		else if (tradingDaysPerScore>=15.0)tradingDaysPerScore= 6.0;
		else if (tradingDaysPerScore>=10.0)tradingDaysPerScore= 5.0;
		else if (tradingDaysPerScore<10.0)tradingDaysPerScore= 4.0;
		
		score = (factorPDDScore*70.0 + tradingDaysPerScore*30.0)/100.0; 
		
		return score;
	}

}
