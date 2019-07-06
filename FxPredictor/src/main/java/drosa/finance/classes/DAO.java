package drosa.finance.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;



import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import drosa.finance.types.DataProvider;
import drosa.finance.utils.DateUtils;




public class DAO {
	
	private static int convert3(String[] values){
		int res = 0;
		if (values.length==2){
			res = Integer.valueOf(values[0])*1000+Integer.valueOf(QuoteShort.fill3(values[1])); 
		}else if (values.length==1){
			res = Integer.valueOf(values[0])*1000; 
		}
		
		return res;
	}
	
	private static QuoteShort decodeQuoteShort(String linea,DataProvider type){
		Date date = null;
		int open5 = 0;
		int high5 = 0;
		int low5 = 0;
		int close5 = 0;
		long vol= 0;
		long maxMin = 0;
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
	
		if (type==DataProvider.DUKASCOPY_FOREX){
			 //hora		
            date = DateUtils.getDukasDate(linea.split(" ")[0].trim(),linea.split(" ")[1].trim());
            
            String timeStr = linea.split(" ")[1].trim();
            String dateStr = linea.split(" ")[0].trim();
            
            year  = Short.valueOf(dateStr.split("\\.")[0].trim());
			month = Byte.valueOf(dateStr.split("\\.")[1].trim());
			day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            ss = Byte.valueOf(timeStr.substring(6,8));
            
            double open = Double.valueOf(linea.split(" ")[2]);
            //System.out.println("A CONVERTIR: "+linea);
            if (open<10.0){
	            String openStr = linea.split(" ")[2].replace(".", "");
	            String highStr = linea.split(" ")[3].replace(".", "");
	            String lowStr = linea.split(" ")[4].replace(".", "");
	            String closeStr = linea.split(" ")[5].replace(".", "");
	            
	            openStr = QuoteShort.fill5(openStr);
	            highStr = QuoteShort.fill5(highStr);
	            lowStr = QuoteShort.fill5(lowStr);
	            closeStr =QuoteShort.fill5(closeStr);
	           
	            open5	= Integer.valueOf(openStr);
	            high5 	= Integer.valueOf(highStr);
	            low5 	= Integer.valueOf(lowStr);
	            close5 	= Integer.valueOf(closeStr);
            }else{
            	//System.out.println("A CONVERTIR: "+linea);
            		String[] valuesO = linea.split(" ")[2].split("\\.");
            		String[] valuesH = linea.split(" ")[3].split("\\.");
            		String[] valuesL = linea.split(" ")[4].split("\\.");
            		String[] valuesC = linea.split(" ")[5].split("\\.");
            		
            		
            	open5 	= convert3(valuesO); 
            	high5 	= convert3(valuesH); 
            	low5	= convert3(valuesL); 
            	close5 	= convert3(valuesC); 
            }
      
            //System.out.println(linea.split(" ")[2].replace(".", "")+" "+open5);
		}else if (type==DataProvider.KIBOT){
	            
            String dateStr = linea.split(",")[0].trim();
            String timeStr = linea.split(",")[1].trim();
            year  = Short.valueOf(dateStr.substring(6,10));
			month = Byte.valueOf(dateStr.substring(0,2));
			day   = Byte.valueOf(dateStr.substring(3,5));
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            //ss = Byte.valueOf(timeStr.substring(6,8));
			
            String openStr = linea.split(",")[2].replace(".", "");
            String highStr = linea.split(",")[3].replace(".", "");
            String lowStr = linea.split(",")[4].replace(".", "");
            String closeStr = linea.split(",")[5].replace(".", "");
            String volStr = linea.split(",")[6].replace(".", "");
           
           
            open5	= Integer.valueOf(openStr);
            high5 	= Integer.valueOf(highStr);
            low5 	= Integer.valueOf(lowStr);
            close5 	= Integer.valueOf(closeStr);
            vol		= Long.valueOf(volStr);
		}else if (type==DataProvider.KIBOTES){
            String dateStr = linea.split(",")[0].trim();
            String timeStr = linea.split(",")[1].trim();
            year  = Short.valueOf(dateStr.substring(6,10));
			month = Byte.valueOf(dateStr.substring(0,2));
			day   = Byte.valueOf(dateStr.substring(3,5));
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            //ss = Byte.valueOf(timeStr.substring(6,8));
			
            String openStr = QuoteShort.fillES(linea.split(",")[2]);
            String highStr = QuoteShort.fillES(linea.split(",")[3]);
            String lowStr  = QuoteShort.fillES(linea.split(",")[4]);
            String closeStr = QuoteShort.fillES(linea.split(",")[5]);
            String volStr = linea.split(",")[6].replace(".", "");
           
           
            open5	= Integer.valueOf(openStr);
            high5 	= Integer.valueOf(highStr);
            low5 	= Integer.valueOf(lowStr);
            close5 	= Integer.valueOf(closeStr);
            vol		= Long.valueOf(volStr);
	}else if (type==DataProvider.DAVE){
		String dateStr="";
		String timeStr="";
		try{
        	dateStr = linea.split(" ")[0].trim();
			timeStr = linea.split(" ")[1].trim();
		}catch(Exception e){
			System.out.println("[ERROR] : "+linea+" "+dateStr+" "+timeStr);
		}
        year  = Short.valueOf(dateStr.substring(6,10));
		day = Byte.valueOf(dateStr.substring(0,2));
		month   = Byte.valueOf(dateStr.substring(3,5));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        //ss = Byte.valueOf(timeStr.substring(6,8));
		
        String openStr = linea.split(" ")[2];
        String highStr = linea.split(" ")[3];
        String lowStr  = linea.split(" ")[4];
        String closeStr = linea.split(" ")[5];
        //String volStr = linea.split(",")[6].replace(".", "");
       
       
        open5	= Integer.valueOf(openStr);
        high5 	= Integer.valueOf(highStr);
        low5 	= Integer.valueOf(lowStr);
        close5 	= Integer.valueOf(closeStr);
        //vol		= Long.valueOf(volStr);
	}else if (type==DataProvider.DAVEVOL){
		String dateStr="";
		String timeStr="";
		try{
        	dateStr = linea.split(" ")[0].trim();
			timeStr = linea.split(" ")[1].trim();
		}catch(Exception e){
			System.out.println("[ERROR] : "+linea+" "+dateStr+" "+timeStr);
		}
        year	= Short.valueOf(dateStr.substring(6,10));
		day 	= Byte.valueOf(dateStr.substring(0,2));
		month   = Byte.valueOf(dateStr.substring(3,5));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
		
        String openStr = linea.split(" ")[2];
        String highStr = linea.split(" ")[3];
        String lowStr  = linea.split(" ")[4];
        String closeStr = linea.split(" ")[5];
        String volStr = linea.split(" ")[6].replace(".", "");
       
       
        open5	= Integer.valueOf(openStr);
        high5 	= Integer.valueOf(highStr);
        low5 	= Integer.valueOf(lowStr);
        close5 	= Integer.valueOf(closeStr);
        vol		= Long.valueOf(volStr);
	}else if (type==DataProvider.DAVEVOLMAXMIN){
		String dateStr="";
		String timeStr="";
		try{
        	dateStr = linea.split(" ")[0].trim();
			timeStr = linea.split(" ")[1].trim();
		}catch(Exception e){
			System.out.println("[ERROR] : "+linea+" "+dateStr+" "+timeStr);
		}
        year	= Short.valueOf(dateStr.substring(6,10));
		day 	= Byte.valueOf(dateStr.substring(0,2));
		month   = Byte.valueOf(dateStr.substring(3,5));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
		
        String openStr = linea.split(" ")[2];
        String highStr = linea.split(" ")[3];
        String lowStr  = linea.split(" ")[4];
        String closeStr = linea.split(" ")[5];
        String volStr = linea.split(" ")[6].replace(".", "");
        String maxMinStr = linea.split(" ")[7];
        
        open5	= Integer.valueOf(openStr);
        high5 	= Integer.valueOf(highStr);
        low5 	= Integer.valueOf(lowStr);
        close5 	= Integer.valueOf(closeStr);
        vol		= Long.valueOf(volStr);
        maxMin  = Long.valueOf(maxMinStr);
	}else if (type==DataProvider.DUKASCOPY_FOREX3){
		 //hora		
        date = DateUtils.getDukasDate(linea.split(" ")[0].trim(),linea.split(" ")[1].trim());
        
        String timeStr = linea.split(" ")[1].trim();
        String dateStr = linea.split(" ")[0].trim();
        
        if (dateStr.split("\\.")[2].trim().length()==4){
        	year  = Short.valueOf(dateStr.split("\\.")[2].trim());
        	day   = Byte.valueOf(dateStr.split("\\.")[0].trim());
        }else{
        	year  = Short.valueOf(dateStr.split("\\.")[0].trim());
        	day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
        }
		month = Byte.valueOf(dateStr.split("\\.")[1].trim());
			
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
        double open = Double.valueOf(linea.split(" ")[2].replace(",", "."));            
		String delimiter = ".";
		if (linea.split(" ")[3].contains(",") || linea.split(" ")[2].contains(",") || linea.split(" ")[4].contains(",") || linea.split(" ")[5].contains(",")){
			delimiter = ",";
		}
        if (open<10.0){
            String openStr = linea.split(" ")[2].replace(delimiter, "");
            String highStr = linea.split(" ")[3].replace(delimiter, "");
            String lowStr = linea.split(" ")[4].replace(delimiter, "");
            String closeStr = linea.split(" ")[5].replace(delimiter, "");
            
            openStr = QuoteShort.fill5(openStr);
            highStr = QuoteShort.fill5(highStr);
            lowStr = QuoteShort.fill5(lowStr);
            closeStr =QuoteShort.fill5(closeStr);
           
            try{
	            open5	= Integer.valueOf(openStr);
	            high5 	= Integer.valueOf(highStr);
	            low5 	= Integer.valueOf(lowStr);
	            close5 	= Integer.valueOf(closeStr);
            }catch(Exception e){
            	//System.out.println(linea.split(" ")[2]+" "+openStr+". "+e.getMessage());
            }
        }else{
        	//System.out.println("A CONVERTIR: "+linea);
        		String[] valuesO = linea.split(" ")[2].split(delimiter);
        		String[] valuesH  = linea.split(" ")[3].split(delimiter);
        		String[] valuesL = linea.split(" ")[4].split(delimiter);
        		String[] valuesC = linea.split(" ")[5].split(delimiter);
        		if (delimiter=="."){
        			valuesO  = linea.split(" ")[2].split("\\.");
        			valuesH = linea.split(" ")[3].split("\\.");
        			valuesL  = linea.split(" ")[4].split("\\.");
        			valuesC = linea.split(" ")[5].split("\\.");
         		}
        		
        	open5 	= convert3(valuesO); 
        	high5 	= convert3(valuesH); 
        	low5	= convert3(valuesL); 
        	close5 	= convert3(valuesC); 
        }
  
        //System.out.println(linea.split(" ")[2].replace(".", "")+" "+open5);
	}else if (type==DataProvider.PEPPERSTONE_FOREX){
		 //hora		
        date = DateUtils.getPepperDate(linea.split(",")[0].trim(),linea.split(",")[1].trim());
        
        String timeStr = linea.split(",")[1].trim();
        String dateStr = linea.split(",")[0].trim();
        
        year  = Short.valueOf(dateStr.split("\\.")[0].trim());
		month = Byte.valueOf(dateStr.split("\\.")[1].trim());
		day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = 0;
        
        double open = Double.valueOf(linea.split(",")[2].replace(",", "."));            
		String delimiter = ".";
		if (linea.split(",")[3].contains(",") || linea.split(",")[2].contains(",") || linea.split(",")[4].contains(",") || linea.split(",")[5].contains(",")){
			delimiter = ",";
		}
        if (open<10.0){
            String openStr	= linea.split(",")[2].replace(delimiter, "");
            String highStr 	= linea.split(",")[3].replace(delimiter, "");
            String lowStr 	= linea.split(",")[4].replace(delimiter, "");
            String closeStr = linea.split(",")[5].replace(delimiter, "");
            
            openStr = QuoteShort.fill5(openStr);
            highStr = QuoteShort.fill5(highStr);
            lowStr = QuoteShort.fill5(lowStr);
            closeStr =QuoteShort.fill5(closeStr);
           
            try{
	            open5	= Integer.valueOf(openStr);
	            high5 	= Integer.valueOf(highStr);
	            low5 	= Integer.valueOf(lowStr);
	            close5 	= Integer.valueOf(closeStr);
            }catch(Exception e){
            	//System.out.println(linea.split(" ")[2]+" "+openStr+". "+e.getMessage());
            }
        }else{
        	//System.out.println("A CONVERTIR: "+linea);
        		String[] valuesO	= linea.split(",")[2].split(delimiter);
        		String[] valuesH  	= linea.split(",")[3].split(delimiter);
        		String[] valuesL 	= linea.split(",")[4].split(delimiter);
        		String[] valuesC 	= linea.split(",")[5].split(delimiter);
        		if (delimiter=="."){
        			valuesO  = linea.split(",")[2].split("\\.");
        			valuesH = linea.split(",")[3].split("\\.");
        			valuesL  = linea.split(",")[4].split("\\.");
        			valuesC = linea.split(",")[5].split("\\.");
         		}
        		
        	open5 	= convert3(valuesO); 
        	high5 	= convert3(valuesH); 
        	low5	= convert3(valuesL); 
        	close5 	= convert3(valuesC); 
        }		
	}else if (type==DataProvider.DUKASCOPY_FOREX4){
		 //hora		
		String dateTimeStr = linea.split(";")[0].trim(); 
		String timeStr = dateTimeStr.split(" ")[1].trim();
        String dateStr = dateTimeStr.split(" ")[0].trim();
        date = DateUtils.getDukasDate(dateStr,timeStr);
        
        
        
        if (dateStr.split("\\.")[2].trim().length()==4){
        	year  = Short.valueOf(dateStr.split("\\.")[2].trim());
        	day   = Byte.valueOf(dateStr.split("\\.")[0].trim());
        }else{
        	year  = Short.valueOf(dateStr.split("\\.")[0].trim());
        	day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
        }
		month = Byte.valueOf(dateStr.split("\\.")[1].trim());
			
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
        double open = Double.valueOf(linea.split(";")[1].replace(",", "."));            
		String delimiter = ".";
		if (linea.split(";")[1].contains(",") || linea.split(";")[2].contains(",") || linea.split(";")[3].contains(",") || linea.split(";")[4].contains(",")){
			delimiter = ",";
		}
        if (open<10.0){
            String openStr = linea.split(";")[1].replace(delimiter, "");
            String highStr = linea.split(";")[2].replace(delimiter, "");
            String lowStr = linea.split(";")[3].replace(delimiter, "");
            String closeStr = linea.split(";")[4].replace(delimiter, "");
            
            openStr = QuoteShort.fill5(openStr);
            highStr = QuoteShort.fill5(highStr);
            lowStr = QuoteShort.fill5(lowStr);
            closeStr =QuoteShort.fill5(closeStr);
           
            try{
	            open5	= Integer.valueOf(openStr);
	            high5 	= Integer.valueOf(highStr);
	            low5 	= Integer.valueOf(lowStr);
	            close5 	= Integer.valueOf(closeStr);
            }catch(Exception e){
            	//System.out.println(linea.split(" ")[2]+" "+openStr+". "+e.getMessage());
            }
        }else{
        	//System.out.println("A CONVERTIR: "+linea);
        		String[] valuesO = linea.split(";")[1].split(delimiter);
        		String[] valuesH  = linea.split(";")[2].split(delimiter);
        		String[] valuesL = linea.split(";")[3].split(delimiter);
        		String[] valuesC = linea.split(";")[4].split(delimiter);
        		if (delimiter=="."){
        			valuesO  = linea.split(";")[1].split("\\.");
        			valuesH = linea.split(";")[2].split("\\.");
        			valuesL  = linea.split(";")[3].split("\\.");
        			valuesC = linea.split(";")[4].split("\\.");
         		}
        		
        	open5 	= convert3(valuesO); 
        	high5 	= convert3(valuesH); 
        	low5	= convert3(valuesL); 
        	close5 	= convert3(valuesC); 
        }
  
        //System.out.println(linea.split(" ")[2].replace(".", "")+" "+open5);
	}
					
		QuoteShort fq = new QuoteShort();
					
		fq.setOpen5(open5);
		fq.setClose5(close5);
		fq.setHigh5(high5);
		fq.setLow5(low5);
		fq.setVol(vol);
		fq.setYear(year);
		fq.setMonth(month);
		fq.setDay(day);
		fq.setHh(hh);
		fq.setMm(mm);
		fq.setSs(ss);
		fq.setMaxMin(maxMin);
		
		//System.out.println(linea+ " || "+fq.toString());
		
		return fq;
	}

	public static ArrayList<QuoteShort> retrieveDataShort5m(String fileName,DataProvider providerType){
			ArrayList<QuoteShort> data = new  ArrayList<QuoteShort>();
			File archivo = null;
		    FileReader fr = null;
		    BufferedReader br = null;
		    Calendar cal = Calendar.getInstance();
		    try {
		    	// Apertura del fichero y creacion de BufferedReader para poder
		        // hacer una lectura comoda (disponer del metodo readLine()).
		        archivo = new File (fileName);
		        fr = new FileReader (archivo);
		        br = new BufferedReader(fr);

		        // Lectura del fichero
		        String line;
		        int i=0;
		        QuoteShort lastQ = null;
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		 QuoteShort q = decodeQuoteShort(line,providerType); 
		        		 QuoteShort.getCalendar(cal, q);
		        		 int s = cal.get(Calendar.SECOND); 
		        		 if (s%5==0){
		        			 //System.out.println("add1: "+q.toString()+" "+q.getOpen()+" "+q.getOpen5());
		        			 data.add(q);
		        		 }else if (lastQ==null || !QuoteShort.isSame(q,lastQ)){
		        			 //System.out.println("add2: "+q.toString()+" "+q.getOpen()+" "+q.getOpen5());
		        			 data.add(q);
		        		 }
		        		 //System.out.println(q.toString());
		        		 lastQ = q;
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
		    return data;
		}
		
		
	
}
