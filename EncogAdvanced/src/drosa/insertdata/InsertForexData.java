package drosa.insertdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.Quote;
import drosa.strategies.scripts.PeriodConverter;
import drosa.utils.DateUtils;

public class InsertForexData {

	static ArrayList<Quote> getDailyQuotes(SQLConnectionUtils sql,ArrayList<Quote> data ,String tableName,String symbol,int year1,int year2){
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		fromDate.set(year1, 0, 1, 00, 00,00);
		toDate.set(year2,11, 31, 23, 59,59);
		if (data==null || data.size()==0)
			data = DAO.retrieveQuotes2(sql,tableName,symbol, fromDate, toDate,true);
		
		ArrayList<Quote> days = new ArrayList<Quote>();
		
		int lastDay=-1;
		Quote last = new Quote();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			Calendar cal = Calendar.getInstance();
			cal.setTime(q.getDate());
			
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			if (actualDay!=lastDay){
				if (lastDay != -1){
					Quote qNew = new Quote();
					qNew.copy(last);
					days.add(qNew);
					//System.out.println("[getDailyQuotes] "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.Print(qNew.getHigh())+" "+PrintUtils.Print(qNew.getLow()));
				}
				//copio la cuota actual
				last.copy(q);
			}else{
				last.setClose(q.getClose());
				
				if (q.getLow()<last.getLow())
					last.setLow(q.getLow());
				if (q.getHigh()>last.getHigh())
					last.setHigh(q.getHigh());
			}
			lastDay = actualDay;
		}		
		if (lastDay != -1){
			Quote qNew = new Quote();
			qNew.copy(last);
			days.add(qNew);
			//System.out.println("[getDailyQuotes] "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.Print(qNew.getHigh())+" "+PrintUtils.Print(qNew.getLow()));
		}
		return days;
	}
	
	static ArrayList<Quote> getHourlyQuotes(SQLConnectionUtils sql,ArrayList<Quote> data,String tableName,String symbol,int year1,int year2){
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		fromDate.set(year1, 0, 1, 00, 00,00);
		toDate.set(year2,11, 31, 23, 59,59);
		if (data==null || data.size()==0)
			data = DAO.retrieveQuotes2(sql,tableName,symbol, fromDate, toDate,true);
				
		ArrayList<Quote> hours = new ArrayList<Quote>();
				
		int lastHour=-1;
		Quote last = new Quote();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			Calendar cal = Calendar.getInstance();
			cal.setTime(q.getDate());
			int actualDay = cal.get(Calendar.DAY_OF_WEEK);
			if (actualDay>=Calendar.MONDAY && actualDay<=Calendar.FRIDAY){
				int actualHour = cal.get(Calendar.HOUR_OF_DAY);
				if (actualHour!=lastHour){
					if (lastHour!= -1){
						Quote qNew = new Quote();
						qNew.copy(last);
						hours.add(qNew);
						//System.out.println("[getDailyQuotes] "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.Print(qNew.getHigh())+" "+PrintUtils.Print(qNew.getLow()));
					}
					//copio la cuota actual
					last.copy(q);
				}else{
					last.setClose(q.getClose());
					
					if (q.getLow()<last.getLow())
						last.setLow(q.getLow());
					if (q.getHigh()>last.getHigh())
						last.setHigh(q.getHigh());
				}
				lastHour = actualHour;
			}
		}		
		return hours;
	}
	
	private static void insertData(SQLConnectionUtils sql, String tableName,
			String symbol, ArrayList<Quote> data) {
		// TODO Auto-generated method stub
		//sqlHelper.init(symbol);
		Date lastDate = DateUtils.stringToDate2(DAO.getInsertDate(sql, tableName, "MAX"));
		System.out.println("LastDate: "+DateUtils.datePrint2(lastDate));
		for (int i=0;i< data.size();i++){
			if (data.get(i).getDate().after(lastDate)){
				DAO.storeForexData(sql,symbol, data.get(i),tableName);
			}
		}		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SQLConnectionUtils sql = new SQLConnectionUtils();
		sql.init("forexdata_forex");
		String[] symbols={"eurusd","gbpusd","usdchf","usdjpy"};
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		
		PeriodConverter pc = new PeriodConverter();
		for (int i=0;i<symbols.length;i++){
			System.out.println("Insertando : "+symbols[i]);
			for (int year=1987;year<=2012;year++){
				System.out.println("year: "+year);
				fromDate.set(year, 0, 1, 00, 00,00);
				toDate.set(year,11, 31, 23, 59,59);
				//ArrayList<Quote> data = getDailyQuotes(sql,null,symbols[i]+"1m",symbols[i],year,year);
				ArrayList<Quote> data = DAO.retrieveQuotes2(sql,symbols[i]+"1m",symbols[i], fromDate, toDate, true);
				ArrayList<Quote> target = pc.convert5mv2(data);
				insertData(sql,symbols[i]+"5m",symbols[i],target);
			}
		}

	}

	

}
