package drosa.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import drosa.DAO.DAO;
import drosa.auxiliar.TestResult;
import drosa.data.DataProvider;
import drosa.finances.COTReport;
import drosa.finances.ForexQuote;
import drosa.finances.FutureCSIQuote;
import drosa.finances.FutureQuote;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.phil.TestLines;

public class FileUtils {

	public static void appendToFile(String file,String symbol,TestResult tr){
		
		try {
			
				BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				float percent = (float)(tr.getPositiveCases()*100)/tr.getTotalCases();
				out.write(symbol+":"+tr.getPositiveCases()+"/"+tr.getTotalCases()
							+" "+percent+"%\n");
				out.newLine();
				out.close();
			
		} catch (IOException e) {
			System.out.println("[appendToFile] Error: "+e.getMessage());
		}
	}
	
	
	public static List<COTReport> getCOTData(String fileName)
	{	
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    List<COTReport> symbolList = new ArrayList<COTReport>();

	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String linea;
	        int i=0;
	        while((linea=br.readLine())!=null){
	        	if (i>0){
	        		COTReport cot = COTReport.decodeCOTData(linea);	        	
	        		symbolList.add(cot);
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
	    return symbolList;
	}
	
	public static List<String> getFileSymbols(String fileName)
	{	
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    List<String> symbolList = new ArrayList<String>();

	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String linea;
	        while((linea=br.readLine())!=null){
	            //System.out.println(linea);
	            symbolList.add(linea.split(",")[0]);
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
	    return symbolList;
	}


	public static List<ForexQuote> getForexData(String fileName) {
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    List<ForexQuote>  data = new ArrayList<ForexQuote>();

	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String linea;
	        int i=0;
	        while((linea=br.readLine())!=null){
	        	if (i>0){
	        		 ForexQuote quote = ForexQuote.decodeForexData(linea);	        	
	        		 data.add(quote);
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


	public static List<FutureQuote> getFutureData(String string, TimeFrame tf) {
		// TODO Auto-generated method stub
		return null;
	}


	public static List<FutureCSIQuote> getFutureCSIData(String fileName) {
		// TODO Auto-generated method stub
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    List<FutureCSIQuote>  data = new ArrayList<FutureCSIQuote>();

	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String linea;
	        int i=0;
	        while((linea=br.readLine())!=null){
	        	if (i>0){
	        		 FutureCSIQuote quote = FutureCSIQuote.decodeFutureCSIData(linea);	        	
	        		 data.add(quote);
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


	public static ArrayList<QuoteShort> extractData(String fileName) {
		
		String path5m = fileName;
		//System.out.println(path5m);
		ArrayList<Quote> dataI 	= null;
		ArrayList<Quote> dataS  = null;
		if (path5m.contains("pepper")){
			dataI 		= DAO.retrieveData(path5m, DataProvider.PEPPERSTONE_FOREX);
			dataS 		= dataI;
			//provider="pepper";
		}else if (path5m.contains("forexdata")){
			dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX2);
			dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			//provider="forexdata";
		}else{
			dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
			dataS 		= TestLines.calculateCalendarAdjusted(dataI);
			//provider="dukasc";
		}					
		//ArrayList<Quote> dataI 		= DAO.retrieveData(path, DataProvider.DUKASCOPY_FOREX);
		//ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
	  	ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 	  		
		ArrayList<QuoteShort> data5mS       = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		//ArrayList<QuoteShort> dailyData = ConvertLib.createDailyDataShort(data5mS);
		ArrayList<QuoteShort> data = null;
		dataI.clear();
		dataS.clear();
		data5m.clear();
		data = data5mS;
		
		return data;
	}
}
