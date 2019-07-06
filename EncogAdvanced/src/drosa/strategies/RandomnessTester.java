package drosa.strategies;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.Quote;
import drosa.random.AutoCorrelationTest;
import drosa.random.NonParametricTest;
import drosa.strategies.scripts.PeriodConverter;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class RandomnessTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		SQLConnectionUtils sql1 = new SQLConnectionUtils();
		sql1.init("forexdata_forex2");
		SQLConnectionUtils sql2 = new SQLConnectionUtils();
		sql2.init("dukascopy_forex");
		int minutesBase=1;
		SQLConnectionUtils sqlData = null;
		//String provider="dukascopy";
		String provider="forexdata2";
	
		String futureInfoTable="info";
		if (provider.equalsIgnoreCase("dukascopy")){
			minutesBase=15;
			sqlData = sql2;
			futureInfoTable="info";
		}
		if (provider.equalsIgnoreCase("forexdata2")){
			minutesBase=15;
			sqlData = sql1;
			futureInfoTable="info";
		}
		String s[]={"eurusd"};
		
		String header="";
		int y1=2005;
		int y2=2001;
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		fromDate.set(y1, 0, 1);
		toDate.set(y2, 7,5);

		PeriodConverter pc = new PeriodConverter();
		ArrayList<Quote> data= null;
		ArrayList<Quote> dataTarget= null;
		//System.out.println("data: "+data.size());
		
		/*
		for (y1=2000;y1<=2012;y1++){
			int totalRandom=0;
			int totalPred=0;
			fromDate.set(y1, 0, 1);
			for (int i=0;i<=365;i++){
				toDate.setTimeInMillis(fromDate.getTimeInMillis());
				//toDate.add(Calendar.DAY_OF_YEAR, 1);				
				data= DAO.retrieveQuotes2(sqlData,s[0]+'_'+minutesBase+'m',s[0],fromDate,toDate,true);
				header=DateUtils.datePrint(fromDate.getTime());
				//System.out.println(data.size());
				if (data.size()>0){
					dataTarget=data;
					dataTarget = pc.convertMinutes(data,96);
					//dataTarget = PeriodConverter.createDailyData(dataTarget, 8);
					int res = NonParametricTest.doTest(header,dataTarget, 0.0001);
					if (res==1){
						totalRandom++;
					}else{
						totalPred++;
					}
				}
				fromDate.add(Calendar.DAY_OF_YEAR, 1);
			}
			double randPer = totalRandom*100.0/(totalRandom+totalPred);
			System.out.println(y1+" Random vs Predictable "
					+totalRandom+"("+PrintUtils.Print(randPer)+") "
					+totalPred+"("+PrintUtils.Print(100.0-randPer)+") ");
		}*/
		
		for (y1=1987;y1<=2012;y1++){
			int totalRandom=0;
			int totalPred=0;
			fromDate.set(y1, 0, 1);			
			toDate.set(y1, 11, 31);							
			data= DAO.retrieveQuotes2(sqlData,s[0]+'_'+minutesBase+'m',s[0],fromDate,toDate,true);
			dataTarget=data;
			//dataTarget = pc.convertMinutes(data,1);
			//dataTarget = PeriodConverter.createDailyData(dataTarget, 8);
			header=DateUtils.datePrint(fromDate.getTime());
			//System.out.println(data.size());
			/*if (data.size()>0){
				dataTarget=data;
				dataTarget = pc.convertMinutes(data,16);
				//dataTarget = PeriodConverter.createDailyData(dataTarget, 8);
				int res = NonParametricTest.doTest(header,dataTarget, 0.0001);
				if (res==1){
					totalRandom++;
				}else{
					totalPred++;
				}
			}*/				
			AutoCorrelationTest.doTest(header,dataTarget, 1,1);
		}
		
	}

}
