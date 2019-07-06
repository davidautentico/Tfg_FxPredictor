package drosa.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import drosa.finances.ForexQuote;
import drosa.finances.Quote;
import drosa.utils.DateUtils;

public class CSVtoMem {
	
	public static ArrayList<Quote> readCSV2(String fileName){
		
		ArrayList<Quote> data = new ArrayList<Quote> ();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
		
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String linea;
	        int i=0;
	        String ticker="";
	        String lastTicker="";
	      
	        Quote q = null;
	        while((linea=br.readLine())!=null){
	        	if (i>0){
	        		ticker = String.valueOf(linea.split(";")[0].trim());	 
	        		int session = Integer.valueOf(linea.split(";")[2].trim());	
	        		//double open= Double.valueOf(linea.split(",")[5].trim());
	        		double high= Double.valueOf(linea.split(";")[4].trim());
	        		double low= Double.valueOf(linea.split(";")[3].trim());
	        		double close= Double.valueOf(linea.split(";")[5].trim());
	        		Date date = decodeDate2(String.valueOf(linea.split(";")[1].trim())); 
	        		if (session==1){
	        			q = new Quote();
	        			q.setDate(date);
	        			q.setSymbol(ticker);
	        			//q.setOpen(open);
	        			q.setHigh(high);
	        			q.setLow(low);
	        			q.setClose(close);
	        		}else if (session==2){
	        			if (low<q.getLow()) q.setLow(low);
	        			if (high>q.getHigh()) q.setHigh(high);
	        			q.setClose(close);
	        			data.add(q);
	        		}
	        		//System.out.println("ticker date: "+ticker+" "+DateUtils.datePrint(date));
	        	}
	        	i++;
	        }    
	       // System.out.println("total ticker: "+datas.size());
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
		return data;
	}
	
	private static Date decodeDate2(String dateStr) {
		// TODO Auto-generated method stub
		Date date = new Date();
		
		int yearInt = Integer.valueOf(dateStr.substring(6, 8));
		String year="20"+dateStr.substring(6, 8);
		if (yearInt<=99 & yearInt>=50) year="19"+dateStr.substring(6, 8);
		String month = dateStr.substring(3, 5);
		String day = dateStr.substring(0, 2);
		String dat=year+'-'+month+'-'+day;
	
		String timeStr="00:00:00";
		
		//hora		
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//dfm.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
		try {
			//System.out.println("Antes de parse: "+dateStr+" "+timeStr);	
			date = dfm.parse(dat+" "+timeStr);
			//System.out.println("despues de parse: "+DateUtils.datePrint2(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		dfm=null;			
		return date;	
	}

	public static ArrayList<ArrayList<Quote>> readCSV(String fileName){
		ArrayList<ArrayList<Quote>> datas = new ArrayList<ArrayList<Quote>>();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
		
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String linea;
	        int i=0;
	        String ticker="";
	        String lastTicker="";
	        ArrayList<Quote> data =null;
	        while((linea=br.readLine())!=null){
	        	if (i>0){
	        		ticker = String.valueOf(linea.split(",")[0].trim());	   
	        		double open= Double.valueOf(linea.split(",")[5].trim());
	        		double high= Double.valueOf(linea.split(",")[6].trim());
	        		double low= Double.valueOf(linea.split(",")[7].trim());
	        		double close= Double.valueOf(linea.split(",")[8].trim());
	        		Date date = decodeDate(String.valueOf(linea.split(",")[3].trim())); 
	        		Quote q = new Quote();
	        		q.setDate(date);
	        		q.setSymbol(ticker);
	        		q.setOpen(open);
	        		q.setHigh(high);
	        		q.setLow(low);
	        		q.setClose(close);
	        		//System.out.println("ticker date: "+ticker+" "+DateUtils.datePrint(date));
	        		
	        		if (!ticker.equalsIgnoreCase(lastTicker)){
	        			if (data!=null){
	        				//System.out.println(data.get(0).getSymbol()+" "+data.size());
	        				datas.add(data);
	        			}
	        			data = new ArrayList<Quote>();
	        		}
	        		data.add(q);
	        		lastTicker=ticker;
	        	}
	        	i++;
	        }    
	       // System.out.println("total ticker: "+datas.size());
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
		return datas;
	}
	

	private static Date decodeDate(String dateStr) {
		// TODO Auto-generated method stub
		Date date = new Date();
		
		String year = dateStr.substring(0, 4);
		String month = dateStr.substring(4, 6);
		String day = dateStr.substring(6, 8);
		String dat=year+'-'+month+'-'+day;
		String timeStr="00:00:00";
		
		//hora		
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//dfm.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
		try {
			//System.out.println("Antes de parse: "+dateStr+" "+timeStr);	
			date = dfm.parse(dat+" "+timeStr);
			//System.out.println("despues de parse: "+DateUtils.datePrint2(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		dfm=null;			
		return date;	
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ArrayList<ArrayList<Quote>> datas =readCSV("C:\\MsCmdLineUtilsV1.8.1\\MsCmdLineUtilsV1.8.1\\ms2asc\\bin\\win32\\mideast.csv");
		ArrayList<Quote> data =readCSV2("C:\\MsCmdLineUtilsV1.8.1\\MsCmdLineUtilsV1.8.1\\ms2asc\\bin\\win32\\fiyatendXU100.csv");
		System.out.println(data.size());
		
	}

}
