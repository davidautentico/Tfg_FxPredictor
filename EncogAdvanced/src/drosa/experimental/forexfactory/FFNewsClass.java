package drosa.experimental.forexfactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.phil.Currency;
import drosa.phil.NewsImpact;
import drosa.phil.NewsItem;
import drosa.utils.DateUtils;

public class FFNewsClass {
	
	int year = 0;
	int month = 0;
	int day = 0;
	
	String currency = "USD";
	int impact = 0;//0: bajo,1: medio,2:alto
	String decr="";
	
	
	
	public String getDecr() {
		return decr;
	}
	public void setDecr(String decr) {
		this.decr = decr;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public int getImpact() {
		return impact;
	}
	public void setImpact(int impact) {
		this.impact = impact;
	}
	
	public String toString() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH,month);
		cal.set(Calendar.YEAR,year);
		
		String str = DateUtils.datePrint(cal)
				+" "+currency+" "+decr+" "+impact
				;
		
		return str;
	}
	
	private static FFNewsClass  decodeNewsItem(String line){
		FFNewsClass  item = new FFNewsClass();
		
		String dateStr 	= line.split(",")[0].replaceAll("\"", "").trim();
		String timeStr	= line.split(",")[1].replaceAll("\"", "").trim();
		String currency	= line.split(",")[2].replaceAll("\"", "").trim();
		String impact	= line.split(",")[3].replaceAll("\"", "").trim();
		String descr	= line.split(",")[4].replaceAll("\"", "").trim();
		
		
		Integer year 	= Integer.valueOf(dateStr.substring(0,4));
		Integer month	= Integer.valueOf(dateStr.substring(5,7))-1;
		Integer day    = Integer.valueOf(dateStr.substring(8,10));
		Integer hour 	= Integer.valueOf(timeStr.substring(0,2));
        Integer min 	= Integer.valueOf(timeStr.substring(3,5));
        
        //System.out.println(dateStr+" "+day+" "+month+" "+year);
                
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, min);
        
        
        int impactInt = 0;
        
        if (impact.equalsIgnoreCase("H"))
        	impactInt = 3;
        if (impact.equalsIgnoreCase("M"))
        	impactInt = 2;
        if (impact.equalsIgnoreCase("L"))
        	impactInt = 1;
        
        item.setDay(day);
        item.setMonth(month);
        item.setYear(year);
        item.setDecr(descr);
        item.setImpact(impactInt);
        item.setCurrency(currency);
       
		return item;
	}
	
	
	public static void readNews(String path,ArrayList<FFNewsClass> news,int debug){
		//ArrayList<FFNewsClass > news = new ArrayList<NewsItem>();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;

	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (path);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String line;
	        int i=0;
	        while((line=br.readLine())!=null){
	        	if (i>=0){
	        		FFNewsClass  item = decodeNewsItem(line);     	
	        		 news.add(item);
	        		 if (debug==1)
	        		 System.out.println(item.toString());
	        	}
	        	i++;
	        }    
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	   }	
	}
	
	public static void getCalendar(FFNewsClass item,Calendar cal) {
		
		cal.set(Calendar.YEAR, item.getYear());
		cal.set(Calendar.MONTH, item.getMonth());
		cal.set(Calendar.DAY_OF_MONTH,item.getDay());
	}
	
	public static int getDayImpact(ArrayList<FFNewsClass> news,Calendar cal,int idx) {
		
		Calendar caln = Calendar.getInstance();
		int impact = 0;
		for (int i=idx;i<news.size();i++) {
			
			FFNewsClass item = news.get(i);
			
			FFNewsClass.getCalendar(item,caln);
		
			int itemDay = item.getYear()*360+item.getMonth()*30+item.getDay();
			int itemLook = cal.get(Calendar.YEAR)*360
					+cal.get(Calendar.MONTH)*30
					+cal.get(Calendar.DAY_OF_MONTH);
			
			if (itemDay==itemLook
					&& (item.getCurrency().equalsIgnoreCase("USD")
							|| item.getCurrency().equalsIgnoreCase("EUR"))
					) {
				impact += item.getImpact();
			}
		}
		
		return impact;
	}
	
	public static void main(String[] args) {
		
		String path = "C:\\Users\\David\\Documents\\fxdata\\news.csv" ;
		
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();
		
		FFNewsClass.readNews(path,news,0);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2017);
		cal.set(Calendar.MONTH,11);
		cal.set(Calendar.DAY_OF_MONTH, 14);
		
		int impact = FFNewsClass.getDayImpact(news, cal, 0);
		
		System.out.print(impact);

	}
	

}
