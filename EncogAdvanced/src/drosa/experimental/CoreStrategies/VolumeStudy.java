package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.finances.QuoteShort;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class VolumeStudy {
	
	public static void doStudyHours(ArrayList<QuoteShort> data){
		
		ArrayList<Integer> totalH = new ArrayList<Integer>();
		ArrayList<Long> totalV = new ArrayList<Long>();
		long total = 0;
		for (int i=0;i<=23;i++){
			totalH.add(0);
			totalV.add((long) 0);
		}
		
		int lastH = -1;
		int accH = 0;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (h!=lastH){
				totalV.set(h, totalV.get(h)+accH);
				totalH.set(h, totalH.get(h)+1);
				
				accH = 0;
				lastH = h;
			}
			
			accH += q.getVol();
			total += q.getVol();
		}
		
		for (int i=0;i<=23;i++){
			System.out.println(i
					//+" "+PrintUtils.Print2dec(totalV.get(i)*1.0/totalH.get(i), false)
					+" "+PrintUtils.Print2dec(totalV.get(i)*100.0/total, false)
					);
			
		}
	}

	public static void main(String[] args) {
		String fileNameYM = "C:\\fxdata\\YM.txt";
		String fileNameES = "C:\\fxdata\\ES.txt";; 	 
				
		ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameYM, DataProvider.KIBOT);
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES, DataProvider.KIBOTES);
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES2010, DataProvider.DAVE);		
		//ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		
		
		
		System.out.println("Data: "+data.size());
		
		VolumeStudy.doStudyHours(data);

	}

}
