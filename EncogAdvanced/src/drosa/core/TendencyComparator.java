package drosa.core;

import drosa.auxiliar.ComparationResult;
import drosa.finances.PeriodInformation;
import drosa.finances.Trend;
import drosa.utils.MathUtils;

public class TendencyComparator {
	
		public void TendencyComparator(){
			
		}
		
		public ComparationResult compare(Trend t1,Trend t2){
			ComparationResult cr = new ComparationResult();
			
			PeriodInformation pi1 = t1.getPeriod();
			PeriodInformation pi2 = t2.getPeriod();
			
			if (pi1.getTrendType()==pi2.getTrendType()){
				cr.setTypeCompatible(true);
			}else{
				cr.setTypeCompatible(false);
			}
			
			float speedDiff = MathUtils.Round(Math.abs(pi1.getSpeedAbsolute()-pi2.getSpeedAbsolute()),4);
			cr.setAbsoluteSpeedDiff(speedDiff);
			
			int barsDiff = Math.abs(pi1.getTradingDaysBetween()-pi2.getTradingDaysBetween());
			cr.setAbsoluteSpeedDiff(barsDiff);
						
			//calculate similarity 0:max similarity
			float sim = barsDiff+speedDiff;
			cr.setSimilarity(sim);
			//
			
			return cr;
		}

}
