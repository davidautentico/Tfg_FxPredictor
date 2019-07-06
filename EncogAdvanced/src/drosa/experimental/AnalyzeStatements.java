package drosa.experimental;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;

public class AnalyzeStatements {

	public static void main(String[] args) {
		
		String fileName = "c:\\fxdata\\DetailedStatement.htm";

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
	        int type = 0;
	        int count = 0;
	        int acc = 0;
	        while((line=br.readLine())!=null){
	        	if (i>50){
	        		if (line.contains("My order")){			        	
			        	if (line.contains("sell")) type=-1;
			        	if (line.contains("buy")) type=1;
			        	
			        	if (line.contains("<td class=msdate nowrap>")){
			        		
			        		String[] values = line.split("<td class=msdate nowrap>");
			        		String dateTimeStr = values[1].substring(0, 19);
			        		String[] values2 = line.split("000;");
			        		
			        		String priceStr = values2[1].substring(2, 9).replace(".", "");
			        		String slStr = values2[2].substring(2, 9).replace(".", "");
			        		String tpStr = values2[3].substring(2, 9).replace(".", "");
			        		String closeStr = values2[4].substring(2, 9).replace(".", "");
			        		
			        		int diff = Integer.valueOf(tpStr)-Integer.valueOf(priceStr);
			        		if (type==-1){
			        			diff = Integer.valueOf(priceStr)-Integer.valueOf(tpStr);
			        		}
			        		
			        		count++;
			        		acc += diff;
			        		
			        		System.out.println(type+" "+dateTimeStr+" "+priceStr+" "+slStr+" "+tpStr+" "+closeStr
			        				+" ||| "+diff);
			        	}
	        		}
	        	}
	        	i++;
	        }  
	        
	        System.out.println(acc*1.0/count);
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

}
