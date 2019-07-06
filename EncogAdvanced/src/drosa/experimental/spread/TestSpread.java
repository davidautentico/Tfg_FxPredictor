package drosa.experimental.spread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.finances.Quote;
import drosa.finances.Tick;
import drosa.utils.PrintUtils;

public class TestSpread {
	
	public static double readSpread(String fileName,int h1,int h2){
	
		long avgSpread = 0;
		int total = 0;
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;

	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        int mult4 = 10000;
			int mult5 = 100000;
			
			
	        // Lectura del fichero
	        String line;
	        int i=0;
	        while((line=br.readLine())!=null){
	        	if (i>0){
	        		Tick tick = DAO.decodeTickDukas(line);
	        		if (tick.getAsk()>10.0){
	    				mult4 = 100;
	    				mult5 = 1000;
	    			}
	        		Calendar cal = tick.getDate();
	        		int h = cal.get(Calendar.HOUR_OF_DAY);
	        		if (h1<=h && h<=h2){
	        			String[] values = line.split(" ");
	        			int askL = (int) (Double.valueOf(values[2])*mult5);
	        			int bidL = (int) (Double.valueOf(values[3])*mult5);
	        			int diff = (int) Math.abs(askL-bidL);
	        			avgSpread += diff;
	        			total++;
	        			/*System.out.println(line+" || "
	        					+" "+askL+" "+bidL+" "+diff
	        			+" "+PrintUtils.Print2dec(avgSpread*0.1/total, false));*/
	        		}
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
	    
	   return avgSpread*0.1/total;
	}

	public static void main(String[] args) {
		
		String currency="EURGBP";
		double avgSpread = 0;
		for (int h1=16;h1<=23;h1++){
			int h2 = h1;
			avgSpread = 0;
			for (int year=2006;year<= 2010;year++){
				String fileName = "c:\\fxdata\\"+currency+"_UTC_Ticks_Bid_"+(year-1)+".12.31_"+year+".01.30.csv";
				double spread = TestSpread.readSpread(fileName,h1,h2);
				avgSpread+=spread;
				//System.out.println(fileName+" "+PrintUtils.Print2(spread, false));
			}
			System.out.println(h1+" "+PrintUtils.Print2(avgSpread*1.0/12, false));
		}
	}

}
