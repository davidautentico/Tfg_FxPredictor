package drosa.phil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class PhilDayShort {
	Calendar day;
	int index;
	ArrayList<PhilLineShort> lines;
	
	
	
	public Calendar getDay() {
		return day;
	}

	public void setDay(Calendar day) {
		this.day = day;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ArrayList<PhilLineShort> getLines() {
		return lines;
	}

	public void setLines(ArrayList<PhilLineShort> lines) {
		this.lines = lines;
	}


	public String toString(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.day.getTime());
		String dateStr = DateUtils.getDukasFormat(cal);
		
		String res = dateStr;
		for (int i=0;i<lines.size();i++){
			PhilLineShort line = lines.get(i);
			res+=" "+line.getLineType().name()+":"+PrintUtils.Print(line.getValue());
		}
		
		return res; 
	}
	
	public static PhilDayShort decodeLine(String line){
			//System.out.println("line: "+line);
		
			PhilDayShort pDay = new PhilDayShort();
			
	    	ArrayList<PhilLineShort> lines = new ArrayList<PhilLineShort>();
	    	Date date = DateUtils.getDukasDate(line.split(" ")[0].trim(),line.split(" ")[1].trim());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
	    	int totalFields = line.split(" ").length;
	    	for (int i=2;i<totalFields;i++){
	    		PhilLineShort pLine = new PhilLineShort();
	    	
	    		String field = line.split(" ")[i].trim();
	    		//System.out.println(field);
	    		String lineStr = field.split(":")[0].trim();
	    		String valueStr = field.split(":")[1].trim();
	    		
	    		pLine.setLineType(LineType.valueOf(lineStr));
	    		pLine.setValue(Short.valueOf(valueStr));
	    		lines.add(pLine);
	    	}
	    	  
	    	pDay.setLines(lines);
	    	pDay.setDay(cal);
	    	
	    	return pDay;
	}
	    
	    public static ArrayList<PhilDayShort> loadFromFile(String fileName){
	    
	    	 ArrayList<PhilDayShort> pDays = new ArrayList<PhilDayShort>();
	    	 
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
			        	if (i>=0){
			        		PhilDayShort pDay = decodeLine(line); 
			        		pDays.add(pDay);
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
	    	 
	    	 return pDays;
	    }


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void loadFromFile(ArrayList<PhilDayShort> pDays, String fileName) {
		// TODO Auto-generated method stub
		
		if (pDays==null)
			pDays = new ArrayList<PhilDayShort>();
		pDays.clear();
   	 
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
		        	if (i>=0){
		        		PhilDayShort pDay = decodeLine(line); 
		        		pDays.add(pDay);
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
	}

}
