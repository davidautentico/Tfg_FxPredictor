package drosa.strategies.scripts;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.ForexQuote;
import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class PeriodConverter {

	int lastPos=0;
	private Quote newQuote(){
		Quote q = new Quote();
		
		q.setOpen(0.0);
		q.setClose(0.0);
		q.setHigh(-9999);
		q.setLow(99999999);
		q.setVolume(0);
		q.setAdjOpen(0.0);
		q.setAdjClose(0.0);
		q.setAdjHigh(-9999);
		q.setAdjLow(99999999);
		
		return q;
	}
	
	private void copyQuote(Quote or,Quote tg){
		tg.setDate(or.getDate());
		tg.setOpen(or.getOpen());
		tg.setClose(or.getClose());
		tg.setHigh(or.getHigh());
		tg.setLow(or.getLow());
		tg.setVolume(or.getVolume());
		tg.setAdjOpen(or.getAdjOpen());
		tg.setAdjClose(or.getAdjClose());
		tg.setAdjHigh(or.getAdjHigh());
		tg.setAdjLow(or.getAdjLow());
	}
	
	public static ArrayList<Quote> createWeeklyData(ArrayList<Quote> data) {
		// TODO Auto-generated method stub
		ArrayList<Quote> weeks = new ArrayList<Quote>(); 
		int lastWeek=-1;
		Quote lastWeekQ=new Quote();
		Quote actualWeekQ= new Quote();
		//System.out.println("tamaï¿½o data en calculate: "+data.size());
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			//System.out.println(DateUtils.datePrint(q.getDate())+" "+PrintUtils.getOHLC(q));
			Calendar cal = Calendar.getInstance();
			cal.setTime(q.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualWeek = cal.get(Calendar.WEEK_OF_YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			//no contamos los sabados ni domingos
			if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
				continue;
			}
			
			if (actualWeek!=lastWeek){
				if (lastWeek!=-1){
					Quote qNew = new Quote();
					qNew.copy(actualWeekQ);
					weeks.add(qNew);
					//System.out.println("anyadiendo dailyData: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
				}
				lastWeekQ.copy(actualWeekQ);
				actualWeekQ.copy(q);
				lastWeek=actualWeek;
			}
			
			//actualiamos actualDayQ
			actualWeekQ.setClose(q.getClose());
			if (q.getHigh()>actualWeekQ.getHigh())
				actualWeekQ.setHigh(q.getHigh());
			if (q.getLow()<actualWeekQ.getLow())
				actualWeekQ.setLow(q.getLow());
			
		}
		
		//último día
		Quote qNew = new Quote();
		qNew.copy(actualWeekQ);
		weeks.add(qNew);
		
		return weeks;
	}
	
	/**
	 * En principio sï¿½lo para dï¿½as
	 * @param sqlHelper
	 * @param tableName
	 * @param symbol
	 * @param dataSource
	 * @param multiplier
	 */
	public ArrayList<Quote> convert5m(ArrayList<Quote> dataSource){
		//System.out.println("[convert] stating...");
		ArrayList<Quote> dataTarget = new ArrayList<Quote>();
		Quote actual = newQuote();
		Quote last = newQuote();
		
		GregorianCalendar initialInterval = new GregorianCalendar(); //intervalo inicial barra
		GregorianCalendar finalInterval = new GregorianCalendar(); //intervalo final barra
		//valores iniciales
		initialInterval.setTimeInMillis(0);
		finalInterval.setTimeInMillis(0);
		
		GregorianCalendar actualDate = new GregorianCalendar();
		boolean first=true;
		for (int i=0;i<dataSource.size();i++){
			Quote q = dataSource.get(i);
			actualDate.setTime(dataSource.get(i).getDate());
			//actualDate supera el final del intervalo
			if (actualDate.getTimeInMillis()>finalInterval.getTimeInMillis()){
				//creamos nuevo intervalo
				initialInterval.setTime(dataSource.get(i).getDate());
				finalInterval.setTime(initialInterval.getTime());
				finalInterval.add(Calendar.MINUTE, 4);
				finalInterval.set(Calendar.SECOND, 59);
				
				if (!first){//la primera no vale
					Quote qnew = newQuote(); //creamos nueva cuota
					copyQuote(actual,qnew);//copiamos la actual a la nueva
					dataTarget.add(qnew); //aï¿½adimos al target
					//System.out.println("Dia "+DateUtils.datePrint(qnew.getDate())
					//		+" "+PrintUtils.getOHLC(qnew));
				}
				copyQuote(q,actual);//copiamos la cuota a la nueva cuota intervalo actual
				first=false;
			}else{
				actual.setClose(q.getClose());
				actual.setAdjClose(q.getAdjClose());
				if (q.getLow()<actual.getLow())
					actual.setLow(q.getLow());
				if (q.getHigh()>actual.getHigh())
					actual.setHigh(q.getHigh());
				if (q.getAdjLow()<actual.getAdjLow())
					actual.setAdjLow(q.getAdjLow());
				if (q.getAdjHigh()>actual.getAdjHigh())
					actual.setAdjHigh(q.getAdjHigh());
			}
			copyQuote(actual,last);
		}
		
		
		return dataTarget;
	}
	
	public static ArrayList<Quote> createDailyData(ArrayList<Quote> data) {
		// TODO Auto-generated method stub
		ArrayList<Quote> days = new ArrayList<Quote>(); 
		int lastDay=-1;
		Quote lastDayQ=new Quote();
		Quote actualDayQ= new Quote();
		//System.out.println("tamaï¿½o data en calculate: "+data.size());
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			//System.out.println(DateUtils.datePrint(q.getDate())+" "+PrintUtils.getOHLC(q));
			Calendar cal = Calendar.getInstance();
			cal.setTime(q.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			//no contamos los sabados ni domingos
			if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
				continue;
			}
			
			if (actualDay!=lastDay){
				if (lastDay!=-1){
					Quote qNew = new Quote();
					qNew.copy(actualDayQ);
					days.add(qNew);
					//System.out.println("anyadiendo dailyData: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
				}
				lastDayQ.copy(actualDayQ);
				actualDayQ.copy(q);
				lastDay=actualDay;
			}
			
			//actualiamos actualDayQ
			actualDayQ.setClose(q.getClose());
			if (q.getHigh()>actualDayQ.getHigh())
				actualDayQ.setHigh(q.getHigh());
			if (q.getLow()<actualDayQ.getLow())
				actualDayQ.setLow(q.getLow());
			
		}
		
		//último día
		Quote qNew = new Quote();
		qNew.copy(actualDayQ);
		days.add(qNew);
		
		return days;
	}
	
	public static ArrayList<Quote> createDailyData(ArrayList<Quote> data, int entryHour) {
		// TODO Auto-generated method stub
		ArrayList<Quote> days = new ArrayList<Quote>(); 
		int lastDay=-1;
		Quote lastDayQ=new Quote();
		Quote actualDayQ= new Quote();
		//System.out.println("tamaï¿½o data en calculate: "+data.size());
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			//System.out.println(DateUtils.datePrint(q.getDate())+" "+PrintUtils.getOHLC(q));
			Calendar cal = Calendar.getInstance();
			cal.setTime(q.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			//no contamos los sabados ni domingos
			if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
				continue;
			}
			
			if (actualDay!=lastDay && h==entryHour){
				if (lastDay!=-1){
					Quote qNew = new Quote();
					qNew.copy(actualDayQ);
					days.add(qNew);
					//System.out.println("anyadiendo dailyData: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
				}
				lastDayQ.copy(actualDayQ);
				actualDayQ.copy(q);
				lastDay=actualDay;
			}
			
			//actualiamos actualDayQ
			actualDayQ.setClose(q.getClose());
			if (q.getHigh()>actualDayQ.getHigh())
				actualDayQ.setHigh(q.getHigh());
			if (q.getLow()<actualDayQ.getLow())
				actualDayQ.setLow(q.getLow());
			
		}
		
		return days;
	}
	
	public ArrayList<Quote> convert5mv2(ArrayList<Quote> dataSource){
		//System.out.println("[convert] stating...");
		ArrayList<Quote> dataTarget = new ArrayList<Quote>();
		Quote actual = newQuote();
		Quote last = newQuote();
		
		GregorianCalendar initialInterval = new GregorianCalendar(); //intervalo inicial barra
		GregorianCalendar finalInterval = new GregorianCalendar(); //intervalo final barra
		//valores iniciales
		initialInterval.setTimeInMillis(0);
		finalInterval.setTimeInMillis(0);
		
		GregorianCalendar actualDate = new GregorianCalendar();
		boolean first=true;
		for (int i=0;i<dataSource.size();i++){
			Quote q = dataSource.get(i);
			actualDate.setTime(dataSource.get(i).getDate());
			//actualDate supera el final del intervalo
			if (actualDate.getTimeInMillis()>finalInterval.getTimeInMillis()){
				//creamos nuevo intervalo
				Calendar adjust = Calendar.getInstance();
				adjust.setTime(dataSource.get(i).getDate());
				int minute = adjust.get(Calendar.MINUTE);
				int newMinute = adjustMinute(minute);
				//System.out.println("new interval Minute: "+newMinute);
				initialInterval.setTime(dataSource.get(i).getDate());
				initialInterval.set(Calendar.MINUTE, newMinute);
				finalInterval.setTime(initialInterval.getTime());
				finalInterval.add(Calendar.MINUTE, 4);
				finalInterval.set(Calendar.SECOND, 59);
				
				if (!first){//la primera no vale
					Quote qnew = newQuote(); //creamos nueva cuota
					copyQuote(actual,qnew);//copiamos la actual a la nueva
					qnew.setDate(initialInterval.getTime());
					dataTarget.add(qnew); //aï¿½adimos al target
					//System.out.println("Dia "+DateUtils.datePrint(qnew.getDate())
					//		+" "+PrintUtils.getOHLC(qnew));
				}
				copyQuote(q,actual);//copiamos la cuota a la nueva cuota intervalo actual
				first=false;
			}else{
				actual.setClose(q.getClose());
				actual.setAdjClose(q.getAdjClose());
				if (q.getLow()<actual.getLow())
					actual.setLow(q.getLow());
				if (q.getHigh()>actual.getHigh())
					actual.setHigh(q.getHigh());
				if (q.getAdjLow()<actual.getAdjLow())
					actual.setAdjLow(q.getAdjLow());
				if (q.getAdjHigh()>actual.getAdjHigh())
					actual.setAdjHigh(q.getAdjHigh());
			}
			copyQuote(actual,last);
		}
		
		
		return dataTarget;
	}
	private int adjustMinute(int minute) {
		// TODO Auto-generated method stub
		int mod = minute%5;
		if ((mod>=0 && mod<5)){
			return minute-mod;
		}
		
		
		return minute;
	}

	/**
	 * En principio sï¿½lo para dï¿½as
	 * @param sqlHelper
	 * @param tableName
	 * @param symbol
	 * @param dataSource
	 * @param multiplier
	 */
	public ArrayList<Quote> convertDay(ArrayList<Quote> dataSource){
		//System.out.println("[convert] stating...");
		ArrayList<Quote> dataTarget = new ArrayList<Quote>();
		int lastDay = -1;
		int actualDay = -1;
		Quote actual = newQuote();
		Quote last = newQuote();
		for (int i=0;i<dataSource.size();i++){
			Quote q = dataSource.get(i);
			Calendar c = Calendar.getInstance();
			c.setTime(q.getDate());
			actualDay = c.get(Calendar.DAY_OF_MONTH);
			if (actualDay!=lastDay){
				//System.out.println("DIA "+DateUtils.datePrint(q.getDate()));
				if (lastDay!=-1){//se guarda last el dia si no es el primero
					//System.out.println("Dia "+DateUtils.datePrint(last.getDate())
					//		+" "+PrintUtils.getOHLC(last));
					Quote qnew = newQuote();
					//System.out.println("actualOpen: "
					//		+PrintUtils.Print(actual.getOpen()));
					copyQuote(actual,qnew);//copiamos la actual a la nueva
					dataTarget.add(qnew); //aï¿½adimos al target
					//System.out.println("Dia "+DateUtils.datePrint(qnew.getDate())
					//		+" "+PrintUtils.getOHLC(qnew));
				}
				copyQuote(q,actual);//copiamos la cuota a la nueva cuota intervalo actual
			}else{//actualizamos valores con esta barra
				//System.out.println("actualizamos actual actualOpen,qClose: "
				//		+PrintUtils.Print(actual.getOpen())+" "+PrintUtils.Print(q.getClose()));
				actual.setClose(q.getClose());
				actual.setAdjClose(q.getAdjClose());
				if (q.getLow()<actual.getLow())
					actual.setLow(q.getLow());
				if (q.getHigh()>actual.getHigh())
					actual.setHigh(q.getHigh());
				if (q.getAdjLow()<actual.getAdjLow())
					actual.setAdjLow(q.getAdjLow());
				if (q.getAdjHigh()>actual.getAdjHigh())
					actual.setAdjHigh(q.getAdjHigh());
			}
			copyQuote(actual,last);
			lastDay=actualDay;
		}
		
		if (lastDay!=-1){//se guarda last el dia si no es el primero
			//System.out.println("Dia "+DateUtils.datePrint(last.getDate())
			//		+" "+PrintUtils.getOHLC(last));
			
		}
		
		return dataTarget;
	}
	
	public void shiftHours(ArrayList<Quote> dataSource,int hours){
		
		for (int i=0;i<dataSource.size();i++){
			Quote q = dataSource.get(i);
			Calendar c = Calendar.getInstance();
			//System.out.println("original: "+DateUtils.datePrint(q.getDate())+" "+PrintUtils.Print(q.getClose()));
			c.setTime(q.getDate());
			c.add(Calendar.HOUR_OF_DAY, hours);
			q.setDate(c.getTime());
			//System.out.println("despues: "+DateUtils.datePrint(q.getDate())+" "+PrintUtils.Print(q.getClose()));
			//System.out.println("despues: "+DateUtils.datePrint(dataSource.get(i).getDate()));
		}
	}
	
	
	private double calculateErrors(GregorianCalendar fromDate,
			GregorianCalendar toDate,ArrayList<Quote> dataSource1,ArrayList<Quote> dataSource2,int type){
		
		GregorianCalendar iter = new GregorianCalendar();
		iter.setTime(fromDate.getTime());
		
		double error=0;
		int total=0;
		while (iter.getTimeInMillis()<=toDate.getTimeInMillis()){
			//System.out.println("DIA "+DateUtils.datePrint(iter));
			int pos1=getPosDate(dataSource1,iter);
			int pos2=getPosDate(dataSource2,iter);
			
			if (pos1!=-1 && pos2!=-1){
				Quote q1 = dataSource1.get(pos1);
				Quote q2 = dataSource2.get(pos2);
				if (type==1){
					error+=Math.abs(q1.getOpen()-q2.getOpen());
					//System.out.println("se agrega error de : "+PrintUtils.Print(Math.abs(q1.getOpen()-q2.getOpen())));
					total++;
				}
				if (type==2){
					error+=Math.abs(q1.getHigh()-q2.getHigh());
					total++;
				}
				if (type==3){
					error+=Math.abs(q1.getLow()-q2.getLow());
					total++;
				}
				if (type==4){
					error+=Math.abs(q1.getClose()-q2.getClose());
					total++;
				}
			}
			
			iter.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		//System.out.println("error y total: "+PrintUtils.Print(error)+" "+total);
		return error/total;
	}
	private int getPosDate(ArrayList<Quote> dataSource, GregorianCalendar iter) {
		// TODO Auto-generated method stub
		GregorianCalendar gc = new GregorianCalendar();
		for (int i=0;i<dataSource.size();i++){
			Date dt = dataSource.get(i).getDate();
			gc.setTime(dt);
			if (DateUtils.isDateEqual(gc, iter)){
				return i;
			}
		}
		
		return -1;
	}

	public void insertForexData(SQLConnectionUtils sqlHelper,String symbol,
			String tableName, ArrayList<Quote> data)
	{	
			//sqlHelper.init(symbol);
			Date lastDate = DateUtils.stringToDate2(DAO.getInsertDate(sqlHelper, tableName, "MAX"));
			System.out.println("LastDate: "+DateUtils.datePrint2(lastDate));
			for (int i=0;i< data.size();i++){
				if (data.get(i).getDate().after(lastDate)){
				DAO.storeForexData(sqlHelper,symbol, data.get(i),tableName);
				}
			}		
	}	
	

	public Quote quoteSearch(Date date, ArrayList<Quote> data) {
		// TODO Auto-generated method stub
		
		GregorianCalendar obj = new GregorianCalendar();
		obj.setTime(date);
		for (int i=lastPos;i<data.size();i++){
			lastPos=i;
			Quote q = data.get(i);
			GregorianCalendar newD = new GregorianCalendar();
			newD.setTime(q.getDate());
			//System.out.println("date: "+DateUtils.datePrint(q.getDate()));
			if (DateUtils.isDateEqual2(obj, newD)){
				return q;
			}
			
			if (newD.after(obj)){
				return null;
			}
		}
		return null;
	}
	
	public ArrayList<Quote> convertHours(ArrayList<Quote> dataSource, int hours) {
		// TODO Auto-generated method stub
		ArrayList<Quote> dataTarget = new ArrayList<Quote>();			
		Calendar cal = Calendar.getInstance();	
		//Primer
		Quote actual = dataSource.get(0); //inicial
		cal.setTime(actual.getDate());			
		int lastDay = cal.get(Calendar.DAY_OF_YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int hourCorrected = (int)(hour/hours) * hours;
		int lastHour=hourCorrected;
		cal.set(Calendar.HOUR_OF_DAY,hourCorrected);
		cal.set(Calendar.MINUTE,0);
		actual.setDate(cal.getTime());
		Quote qNew = new Quote();
		for (int i=1;i< dataSource.size();i++){
			Quote q = dataSource.get(i);
			cal.setTime(q.getDate());				
			int day = cal.get(Calendar.DAY_OF_YEAR);
			hour = cal.get(Calendar.HOUR_OF_DAY);			
			hourCorrected = (int)(hour/hours) * hours;
			cal.set(Calendar.HOUR_OF_DAY,hourCorrected);
			cal.set(Calendar.MINUTE,0);
			q.setDate(cal.getTime());
			
			if (day!=lastDay || hourCorrected!=lastHour){
				//cambio de minuto, insertamos el actual y cambiamos
				lastHour = hourCorrected;
				lastDay = day;	
				qNew = new Quote();
				qNew.copy(actual);
				dataTarget.add(qNew);
				//aÃ±adimos
				//System.out.println(DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
				
				//copiamos el de ahora
				actual.copy(q);
			}
						
			actual.setClose(q.getClose());
			if (q.getLow()<actual.getLow())
				actual.setLow(q.getLow());
			if (q.getHigh()>actual.getHigh())
				actual.setHigh(q.getHigh());
		}
		qNew = new Quote();
		qNew.copy(actual);
		dataTarget.add(qNew);
		
		return dataTarget;
	}
	
	public ArrayList<Quote> convertMinutes(ArrayList<Quote> dataSource, int minutes) {
		// TODO Auto-generated method stub
		ArrayList<Quote> dataTarget = new ArrayList<Quote>();			
		Calendar cal = Calendar.getInstance();	
		//Primer
		Quote actual = dataSource.get(0); //inicial
		cal.setTime(actual.getDate());	
		int lastHour = cal.get(Calendar.HOUR_OF_DAY);
		int lastDay = cal.get(Calendar.DAY_OF_YEAR);
		int min = cal.get(Calendar.MINUTE);
		int minCorrected = (int)(min/minutes) * minutes;
		cal.set(Calendar.MINUTE, minCorrected);
		int lastMinute = minCorrected;
		actual.setDate(cal.getTime());
		Quote qNew = new Quote();
		for (int i=1;i< dataSource.size();i++){
			Quote q = dataSource.get(i);
			cal.setTime(q.getDate());	
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			min = cal.get(Calendar.MINUTE);			
			minCorrected = (int)(min/minutes) * minutes;
			cal.set(Calendar.MINUTE, minCorrected);
			q.setDate(cal.getTime());
			
			if (day!=lastDay || minCorrected!=lastMinute){
				//cambio de minuto, insertamos el actual y cambiamos
				lastHour = h;
				lastDay = day;
				lastMinute=minCorrected;
				qNew = new Quote();
				qNew.copy(actual);
				dataTarget.add(qNew);
				//aÃ±adimos
				//System.out.println(DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
				
				//copiamos el de ahora
				actual.copy(q);
			}
						
			actual.setClose(q.getClose());
			if (q.getLow()<actual.getLow())
				actual.setLow(q.getLow());
			if (q.getHigh()>actual.getHigh())
				actual.setHigh(q.getHigh());
		}
		qNew = new Quote();
		qNew.copy(actual);
		dataTarget.add(qNew);
		
		return dataTarget;
	}

	public ArrayList<Quote> convert(ArrayList<Quote> dataSource, int fact) {
		// TODO Auto-generated method stub
		ArrayList<Quote> dataTarget = new ArrayList<Quote>();
		int count=0;
		Quote actual=null;
		Calendar cal = Calendar.getInstance();
		int lastDay=-1;
		for (int i=0;i< dataSource.size();i++){
			Quote q = dataSource.get(i);
			cal.setTime(q.getDate());
			int day = cal.get(Calendar.DAY_OF_YEAR);
			//copiamos la primera
			if (actual==null){
				actual = new Quote();
				actual.copy(q);
			}
			if (count==fact){
				//introducimos en array
				Quote qNew = new Quote();
				qNew.copy(actual);
				cal.setTime(qNew.getDate());
				int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
				if (dayWeek!=Calendar.SATURDAY && dayWeek!=Calendar.SUNDAY){
					dataTarget.add(qNew);
					//System.out.println(DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
				}
				//reseteamos cuenta
				count=0;
				actual.copy(q);
				lastDay=day;
			}
			
			actual.setClose(q.getClose());
			if (q.getLow()<actual.getLow())
				actual.setLow(q.getLow());
			if (q.getHigh()>actual.getHigh())
				actual.setHigh(q.getHigh());
			count++;
		}
		
		//introducimos en array
		Quote qNew = new Quote();
		qNew.copy(actual);
		cal.setTime(qNew.getDate());
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayWeek!=Calendar.SATURDAY && dayWeek!=Calendar.SUNDAY){
			dataTarget.add(qNew);
			//System.out.println(DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.getOHLC(qNew));
		}
		
		return dataTarget;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		//forexdata
		SQLConnectionUtils sqlHelper1 = new SQLConnectionUtils();
		//sqlHelper1.init("forexdata_forex");
		sqlHelper1.init("dukascopy_forex");
		
		String s[]={"usdchf","usdjpy","gbpusd","eurusd","gbpjpy"};
		//String s[]={"usdchf"};
		ArrayList<Quote> dataTarget = null;
		PeriodConverter pc = new PeriodConverter();
		for (int j=0;j<s.length;j++){
			for (int y=2004;y<=2012;y++){
				fromDate.set(y, 0, 1, 00, 00,00);
				toDate.set(y,11, 31, 23, 59,59);
				//System.out.println(args[0]+" "+args[1]+" "+y);
				System.out.println(s[j]);
				ArrayList<Quote> data1m= DAO.retrieveQuotes2(sqlHelper1,s[j]+"_5m",s[j],fromDate,toDate,true);
				//ArrayList<Quote> data1m= DAO.retrieveQuotes2(sqlHelper1,args[0]+"_5m",args[0],fromDate,toDate,true);
				
				dataTarget = pc.convertMinutes(data1m,15);
				/*if (Integer.valueOf(args[1])<60)
					dataTarget = pc.convertMinutes(data1m,Integer.valueOf(args[1])); 
				else
					dataTarget = pc.convertHours(data1m,Integer.valueOf(args[1])/60);
				*/
				Date lastDate = DateUtils.stringToDate2(DAO.getInsertDate(sqlHelper1, s[j]+"_15m", "MAX"));
				for (int i=0;i<dataTarget.size();i++){
					Quote q =dataTarget.get(i);
					//System.out.println(DateUtils.datePrint(lastDate));
					System.out.println(i+" "+DateUtils.datePrint(q.getDate())+" "+PrintUtils.getOHLC(q));
					if (q.getDate().after(lastDate)){
						System.out.println(DateUtils.datePrint(q.getDate())+" "+PrintUtils.getOHLC(q));
						//DAO.storeForexData(sqlHelper1,args[0], q,args[0]+'_'+args[1]+'m'); 
						DAO.storeForexData(sqlHelper1,s[j], q,s[j]+"_15m"); 
					}
				}
			}
		}				
	}

	


}
