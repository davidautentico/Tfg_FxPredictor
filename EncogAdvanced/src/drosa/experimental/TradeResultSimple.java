package drosa.experimental;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.Quote;
import drosa.utils.DateUtils;

public class TradeResultSimple {
	Calendar openCal      = Calendar.getInstance();
	Calendar buyCloseCal  = Calendar.getInstance();
	Calendar sellCloseCal = Calendar.getInstance();	
	byte tp = 0;
	byte sl = 0;
	byte buyResult = 0;
	byte sellResult = 0;
	
	public Calendar getOpenCal() {
		return openCal;
	}
	public void setOpenCal(Calendar openCal) {
		this.openCal = openCal;
	}
	public Calendar getBuyCloseCal() {
		return buyCloseCal;
	}
	public void setBuyCloseCal(Calendar buyCloseCal) {
		this.buyCloseCal = buyCloseCal;
	}
	public Calendar getSellCloseCal() {
		return sellCloseCal;
	}
	public void setSellCloseCal(Calendar sellCloseCal) {
		this.sellCloseCal = sellCloseCal;
	}
	public double getTp() {
		return tp;
	}
	public void setTp(byte tp) {
		this.tp = tp;
	}
	public double getSl() {
		return sl;
	}
	public void setSl(byte sl) {
		this.sl = sl;
	}
	public int getBuyResult() {
		return buyResult;
	}
	public void setBuyResult(byte buyResult) {
		this.buyResult = buyResult;
	}
	public int getSellResult() {
		return sellResult;
	}
	public void setSellResult(byte sellResult) {
		this.sellResult = sellResult;
	}
	
	public String toString(){
		
		String res = DateUtils.datePrint(this.openCal)
				+" "+DateUtils.datePrint(this.buyCloseCal)
				+" "+DateUtils.datePrint(this.sellCloseCal)
				+" "+tp
				+" "+sl
				+" "+this.buyResult
				+" "+this.sellResult;
		
		return res;		
	}
	
	public static void saveToDisk(String fileName,ArrayList<TradeResultSimple> data){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);

			  for (int i=0;i<data.size();i++){
				  TradeResultSimple res = data.get(i);
				  out.write(res.toString());
				  out.newLine();
			  }
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	private static TradeResultSimple decodeLine(String line){
				
		 Calendar openCal      = DateUtils.getDukasCalendar(line.split(" ")[0].trim(),line.split(" ")[1].trim());
		 Calendar buyCloseCal  = DateUtils.getDukasCalendar(line.split(" ")[2].trim(),line.split(" ")[3].trim());
		 Calendar sellCloseCal = DateUtils.getDukasCalendar(line.split(" ")[4].trim(),line.split(" ")[5].trim());			
         byte tp   = Byte.valueOf(line.split(" ")[6].trim().split("\\.")[0]);
         byte sl   = Byte.valueOf(line.split(" ")[7].trim().split("\\.")[0]);
         byte buyResult  = Byte.valueOf(line.split(" ")[8].trim());
         byte sellResult = Byte.valueOf(line.split(" ")[9].trim());
        
         TradeResultSimple trade = new TradeResultSimple();
         trade.setOpenCal(openCal);
         trade.setBuyCloseCal(buyCloseCal);
         trade.setSellCloseCal(sellCloseCal);
         trade.setSl(sl);
         trade.setTp(tp);
         trade.setBuyResult(buyResult);
         trade.setSellResult(sellResult);
		 //System.out.println("LINE: "+line+" TRADE: "+trade.toString());
		return trade;
	}
	
	public static ArrayList<TradeResultSimple> readFromDisk(String fileName){
		ArrayList<TradeResultSimple> data = new ArrayList<TradeResultSimple>();
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero

	        int i=0;
	        while((line=br.readLine())!=null){
	        	if (i>0){
	        		TradeResultSimple item = decodeLine(line);     	
	        		data.add(item);
	        	}
	        	i++;
	        	//if (i%20000==0)
	        		//System.out.println("i: "+i);
	        }    
	    }catch(Exception e){
	    	e.printStackTrace();
	    	 System.out.println("[error] "+line);
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
	public static Calendar getCalendarDay(
			ArrayList<TradeResultSimple> tradeResults, int actual, int days) {
		
		Calendar cal = Calendar.getInstance();
		int lastDay = -1;
		int count=-1;
		if (days>=0){
			for (int i=actual;i<tradeResults.size();i++){
				cal.setTimeInMillis(tradeResults.get(i).getOpenCal().getTimeInMillis());
				int d = cal.get(Calendar.DAY_OF_YEAR);
				if (d!=lastDay){
					count++;
					lastDay = d;
				}
				if (count>=Math.abs(days)) return cal;

			}
		}else{
			for (int i=actual;i>=0;i--){
				cal.setTimeInMillis(tradeResults.get(i).getOpenCal().getTimeInMillis());
				int d = cal.get(Calendar.DAY_OF_YEAR);
				if (d!=lastDay){
					count++;
					lastDay = d;
				}
				if (count>=Math.abs(days)) return cal;
			}
		}
		return null;
	}
		
}
