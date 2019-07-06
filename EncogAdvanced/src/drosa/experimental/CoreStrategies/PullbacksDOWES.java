package drosa.experimental.CoreStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.DOWES;
import drosa.finances.QuoteShort;
import drosa.utils.TradingUtils;

public class PullbacksDOWES {

	public static void main(String[] args) {
		String fileNameYM = "C:\\fxdata\\YM.txt";
		String fileNameES = "C:\\fxdata\\ES.txt";; 	 
				
		ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameYM, DataProvider.KIBOT);
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES, DataProvider.KIBOTES);
		//ArrayList<QuoteShort> data = DAO.retrieveDataDOW(fileNameES2010, DataProvider.DAVE);		
		ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
		
		
		
		System.out.println("Data: "+data.size());
		
	
		//SimpleContinuation
		for (int y1=2009;y1<=2016;y1+=1){
			int y2 = y1+0;
			for (int h1=11;h1<=11;h1++){
				int h2=h1+12;
				for (int thr1=5000;thr1<=5000;thr1+=1000){
					for (int thr2=240;thr2<=240;thr2+=60){
						for (int nbars = 9999;nbars<=9999;nbars+=60){
						//for (int nbars = 12;nbars<=48*12;nbars+=12){
							for (int minPips=20;minPips<=20;minPips++){
								//DailyBreak.doTrade(data, y1, y2, h1, h2, tp);
								//DailyBreak.doTrade2(data, y1, y2, h1, h2, tp);
								for (int dayWeek1=Calendar.MONDAY+0;dayWeek1<=Calendar.MONDAY+0;dayWeek1++){
									for (int tp=10;tp<=10;tp+=1){
										for (int sl=4*tp;sl<=6*tp;sl+=1*tp){
											double comm = 0.0;
											//Pullbacks.doTrade(data,maxMins, y1, y2, h1, h2,dayWeek1, thr1,thr2, nbars,tp,sl,comm,25);//ES
											Pullbacks.doTrade(data,maxMins, y1, y2, h1, h2,dayWeek1, thr1,thr2, nbars,tp,sl,comm,1);//YM
										}
									}										
								}
							}
						}//nbars
					}//thr2
				}
			}
			System.out.println("");
		}//year

	}

}
