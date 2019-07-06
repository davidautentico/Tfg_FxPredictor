/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drosa.phil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import drosa.finances.Quote;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

/**
 *
 * @author drosa
 */
public class TMA {
    Calendar date;
    double upper;
    double middle;
    double lower;
	private int index;

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
            
    public double getUpper() {
        return upper;
    }

    public void setUpper(double upper) {
        this.upper = upper;
    }

    public double getMiddle() {
        return middle;
    }

    public void setMiddle(double middle) {
        this.middle = middle;
    }

    public double getLower() {
        return lower;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }
    
    public String toString(){
    	Calendar cal = Calendar.getInstance();
		cal.setTime(this.date.getTime());
		String dateStr = DateUtils.getDukasFormat(cal);
		
		String res = dateStr+" "+PrintUtils.Print(this.upper)+" "+PrintUtils.Print(this.lower);
		
		return res;
    }
    
    public static TMA decodeTMALine(String line){
    	TMA tma = new TMA();
    	Date date = DateUtils.getDukasDate(line.split(" ")[0].trim(),line.split(" ")[1].trim());
			
        double upper = Float.valueOf(line.split(" ")[2].trim());
        double lower= Float.valueOf(line.split(" ")[3].trim());
    	
        Calendar cal  = Calendar.getInstance();
        cal.setTime(date);
        
        tma.setDate(cal);
        tma.setUpper(upper);
        tma.setLower(lower);
        
    	return tma;
    }
    
    public static ArrayList<TMA> loadFromFile(String fileName){
    
    	 ArrayList<TMA> tmas = new ArrayList<TMA>();
    	 
    	 File file = new File(fileName);
    	 
    	 if (file.exists()){
    		 FileReader fr = null;
    		 BufferedReader br = null;
	
    		 try {
		    	// Apertura del fichero y creacion de BufferedReader para poder
		        // hacer una lectura comoda (disponer del metodo readLine()).
		        fr = new FileReader (file);
		        br = new BufferedReader(fr);
		        // Lectura del fichero
		        String line;
		        int i=0;
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		TMA tma = decodeTMALine(line); 
		        		tmas.add(tma);
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
    	 
    	 return tmas;
    }

	public void setIndex(int index) {
		// TODO Auto-generated method stub
		this.index = index;
	}

	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

	public static int find(ArrayList<TMA> tmaArray, int year, int month, int day,int hour,
			int min, int seg, int lastIndex) {
		// TODO Auto-generated method stub
		
		Calendar cal = Calendar.getInstance();
		for (int i=lastIndex;i<tmaArray.size();i++){
			TMA tma = tmaArray.get(i);
			
			cal.setTimeInMillis(tma.getDate().getTimeInMillis());
			
			int y    = cal.get(Calendar.YEAR);
			int mn 	 = cal.get(Calendar.MONTH);
			int d 	 = cal.get(Calendar.DAY_OF_YEAR);
			int h 	 = cal.get(Calendar.HOUR_OF_DAY);
			int m	 = cal.get(Calendar.MINUTE);
			int s	 = cal.get(Calendar.SECOND);
			
			boolean found = true;
			if (year>=0 && y!=year){
				found = false;
				continue;
			}
			if (d>=0 && d!=day){
				found = false;
				continue;
			}
			if (h>=0 && h!=hour){
				found = false;
				continue;
			}
			if (found){
				return i;
			}
		}
		return -1;
	}
    
}
