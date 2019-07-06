package drosa.experimental.ticksStudy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class Tick {
	short year;
	byte month;
	byte day;
	byte hh;
	byte mm;
	byte ss;
	int  bid;
	int ask;
	int extra;
	short spread;//medido en miniPips 1=0.1 real
	
	public short getYear() {
		return year;
	}
	public void setYear(short year) {
		this.year = year;
	}
	public byte getMonth() {
		return month;
	}
	public void setMonth(byte month) {
		this.month = month;
	}
	public byte getDay() {
		return day;
	}
	public void setDay(byte day) {
		this.day = day;
	}
	public byte getHh() {
		return hh;
	}
	public void setHh(byte hh) {
		this.hh = hh;
	}
	public byte getMm() {
		return mm;
	}
	public void setMm(byte mm) {
		this.mm = mm;
	}
	public byte getSs() {
		return ss;
	}
	public void setSs(byte ss) {
		this.ss = ss;
	}
	public int getBid() {
		return bid;
	}
	public void setBid(int bid) {
		this.bid = bid;
	}
	public int getAsk() {
		return ask;
	}
	public void setAsk(int ask) {
		this.ask = ask;
	}
	
	public int getExtra() {
		return extra;
	}
	public void setExtra(int extra) {
		this.extra = extra;
	}
	public short getSpread() {
		return spread;
	}
	public void setSpread(short spread) {
		this.spread = spread;
	}
	
	public String toString(){
		String str = DateUtils.datePrint(year, month, day, hh, mm, ss)+" "+bid+" "+ask+" "+spread;
		return str;
	}
	
	public static void getCalendar(Calendar cal, Tick t) {
		// TODO Auto-generated method stub
		cal.set(t.getYear(), t.getMonth()-1, t.getDay(), t.getHh(), t.getMm(),t.getSs());
	}
	
	public void copy(Tick q) {
		// TODO Auto-generated method stub
		this.year = q.getYear();
		this.month = q.getMonth();
		this.day = q.getDay();
		this.hh = q.getHh();
		this.mm = q.getMm();
		this.ss = q.getSs();
		this.bid = q.getBid();
		this.ask = q.getAsk();
		this.extra = q.getExtra();
		this.spread = q.getSpread();
	}
	
	public void setCal(Calendar cal) {
		// TODO Auto-generated method stub
		this.year  = (short) cal.get(Calendar.YEAR);
		this.month = (byte) (cal.get(Calendar.MONTH)+1);
		this.day   = (byte) cal.get(Calendar.DAY_OF_MONTH);
		this.hh    = (byte) cal.get(Calendar.HOUR_OF_DAY);
		this.mm    = (byte) cal.get(Calendar.MINUTE);
		this.ss    = (byte) cal.get(Calendar.SECOND);
	}
	
	private static Tick decodeLine(String line){
		int bid = 0;
		int ask = 0;
		short spread = 0;
		
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
		String dateTimeStr = line.split(";")[0].trim();
		
		String timeStr = dateTimeStr.split(" ")[1].trim();
        String dateStr = dateTimeStr.split(" ")[0].trim();        
        year  = Short.valueOf(dateStr.split("\\.")[0].trim());
		month = Byte.valueOf(dateStr.split("\\.")[1].trim());
		day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
		bid      = (int) (Double.valueOf(line.split(";")[1].trim())*100000);
        ask      = (int) (Double.valueOf(line.split(";")[2].trim())*100000);
        spread   = (short) (Double.valueOf(line.split(";")[3].trim())*10);
		
       
        Tick t = new Tick();
        
        t.setBid(bid);
		t.setAsk(ask);
		t.setSpread(spread);	
		t.setYear(year);
		t.setMonth(month);
		t.setDay(day);
		t.setHh(hh);
		t.setMm(mm);
		t.setSs(ss);
		
		return t;
	}
	
	private static Tick decodeLinePepperPage(String line){
		int bid = 0;
		int ask = 0;
		short spread = 0;
		
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
		String dateTimeStr = line.split(",")[1].trim();
		
		String timeStr = dateTimeStr.split(" ")[1].trim();
        String dateStr = dateTimeStr.split(" ")[0].trim();        
        //year  = Short.valueOf(dateStr.split("\\.")[0].trim());
		//month = Byte.valueOf(dateStr.split("\\.")[1].trim());
		//day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
		year  = Short.valueOf(dateStr.substring(0,4));
		month = Byte.valueOf(dateStr.substring(4,6));
		day   = Byte.valueOf(dateStr.substring(6,8));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
		bid      = (int) (Double.valueOf(line.split(",")[2].trim())*100000);
        ask      = (int) (Double.valueOf(line.split(",")[3].trim())*100000);
        spread   = (short) (ask-bid);
		
       
        Tick t = new Tick();
        
        t.setBid(bid);
		t.setAsk(ask);
		t.setSpread(spread);	
		t.setYear(year);
		t.setMonth(month);
		t.setDay(day);
		t.setHh(hh);
		t.setMm(mm);
		t.setSs(ss);
		
		return t;
	}
	
	public static int findMinuteIndex(ArrayList<Tick> ticks,Calendar calFind,int index){
		
		Calendar cal = Calendar.getInstance();
		for (int i=index;i<ticks.size();i++){
			Tick t = ticks.get(i);
			Tick.getCalendar(cal, t);
			if (DateUtils.isDateTimeEqualMinute(cal, calFind))
				return i;
		}
		return -1;
	}
	
	public static int findMinuteIndexQS(ArrayList<QuoteShort> data,Calendar calFind,int index){
		
		Calendar cal = Calendar.getInstance();
		for (int i=index;i<data.size();i++){
			QuoteShort t = data.get(i);
			QuoteShort.getCalendar(cal, t);
			if (DateUtils.isDateTimeEqualMinute(cal, calFind))
				return i;
		}
		return -1;
	}
	
	private static Tick decodeLineDukas(String line){
		int bid = 0;
		int ask = 0;
		short spread = 0;
		
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
		
		String timeStr = line.split(" ")[1].trim();
        String dateStr = line.split(" ")[0].trim();       
        //year  = Short.valueOf(dateStr.split("\\.")[0].trim());
		//month = Byte.valueOf(dateStr.split("\\.")[1].trim());
		//day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
		year  = Short.valueOf(dateStr.substring(0,4));
		month = Byte.valueOf(dateStr.substring(5,7));
		day   = Byte.valueOf(dateStr.substring(8,10));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
        if (Double.valueOf(line.split(" ")[3].trim())<10.0){
			bid      = (int) (Double.valueOf(line.split(" ")[3].trim())*100000);
	        ask      = (int) (Double.valueOf(line.split(" ")[2].trim())*100000);
        }else{
        	bid      = (int) (Double.valueOf(line.split(" ")[3].trim())*1000);
	        ask      = (int) (Double.valueOf(line.split(" ")[2].trim())*1000);
        }
        spread   = (short) (ask-bid);
		
       
        Tick t = new Tick();
        
        t.setBid(bid);
		t.setAsk(ask);
		t.setSpread(spread);	
		t.setYear(year);
		t.setMonth(month);
		t.setDay(day);
		t.setHh(hh);
		t.setMm(mm);
		t.setSs(ss);
		
		return t;
	}
	
	private static Tick decodeLineDukas2(String line){
		int bid = 0;
		int ask = 0;
		short spread = 0;
		
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
		
		String timeStr = line.split(" ")[1].trim();
        String dateStr = line.split(" ")[0].trim();       
        //year  = Short.valueOf(dateStr.split("\\.")[0].trim());
		//month = Byte.valueOf(dateStr.split("\\.")[1].trim());
		//day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
		year  = Short.valueOf(dateStr.substring(6,10));
		month = Byte.valueOf(dateStr.substring(3,5));
		day   = Byte.valueOf(dateStr.substring(0,2));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
		bid      = Integer.valueOf(line.split(" ")[2]);
        ask      = Integer.valueOf(line.split(" ")[3]);
        spread   = (short) (ask-bid);
		
       
        Tick t = new Tick();
        
        t.setBid(bid);
		t.setAsk(ask);
		t.setSpread(spread);	
		t.setYear(year);
		t.setMonth(month);
		t.setDay(day);
		t.setHh(hh);
		t.setMm(mm);
		t.setSs(ss);
		
		return t;
	}
	
	private static Tick decodeLineDukas3(String line){
		int bid = 0;
		int ask = 0;
		short spread = 0;
		
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
		
		String timeStr = line.split(" ")[1].trim();
        String dateStr = line.split(" ")[0].trim();       
        //year  = Short.valueOf(dateStr.split("\\.")[0].trim());
		//month = Byte.valueOf(dateStr.split("\\.")[1].trim());
		//day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
		day  = Byte.valueOf(dateStr.substring(8,10));
		month = Byte.valueOf(dateStr.substring(5,7));
		year   = Short.valueOf(dateStr.substring(0,4));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
        
        String bidStr = line.split(" ")[2].replace(",", "");
        String askStr = line.split(" ")[3].replace(",", "");
       
     
        bidStr = QuoteShort.fill5(bidStr);
        askStr = QuoteShort.fill5(askStr);
        bid = Integer.valueOf(bidStr);
        ask =Integer.valueOf(askStr);
        
        spread   = (short) (ask-bid);
		
       
        Tick t = new Tick();
        
        t.setBid(bid);
		t.setAsk(ask);
		t.setSpread(spread);	
		t.setYear(year);
		t.setMonth(month);
		t.setDay(day);
		t.setHh(hh);
		t.setMm(mm);
		t.setSs(ss);
		
		return t;
	}
	
	private static Tick decodeLineDukas4(String line){
		int bid = 0;
		int ask = 0;
		short spread = 0;
		
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
		
		String timeStr = line.split(";")[0].split(" ")[1].trim();
        String dateStr = line.split(";")[0].split(" ")[0].trim();       
        //year  = Short.valueOf(dateStr.split("\\.")[0].trim());
		//month = Byte.valueOf(dateStr.split("\\.")[1].trim());
		//day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
		day  = Byte.valueOf(dateStr.substring(8,10));
		month = Byte.valueOf(dateStr.substring(5,7));
		year   = Short.valueOf(dateStr.substring(0,4));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
        
        String bidStr = line.split(";")[1].replace(",", "");
        String askStr = line.split(";")[2].replace(",", "");
       
     
        bidStr = QuoteShort.fill5(bidStr);
        askStr = QuoteShort.fill5(askStr);
        bid = Integer.valueOf(bidStr);
        ask =Integer.valueOf(askStr);
        
        spread   = (short) (ask-bid);
		
       
        Tick t = new Tick();
        
        t.setBid(bid);
		t.setAsk(ask);
		t.setSpread(spread);	
		t.setYear(year);
		t.setMonth(month);
		t.setDay(day);
		t.setHh(hh);
		t.setMm(mm);
		t.setSs(ss);
		
		return t;
	}
	
	private static void decodeLineDukas5(String line,Tick t){
		int bid = 0;
		int ask = 0;
		short spread = 0;
		
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
		
		String timeStr = line.split(";")[0].split(" ")[1].trim();
        String dateStr = line.split(";")[0].split(" ")[0].trim();       
        //year  = Short.valueOf(dateStr.split("\\.")[0].trim());
		//month = Byte.valueOf(dateStr.split("\\.")[1].trim());
		//day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
		day  = Byte.valueOf(dateStr.substring(8,10));
		month = Byte.valueOf(dateStr.substring(5,7));
		year   = Short.valueOf(dateStr.substring(0,4));
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
        String bidStr="";
        String askStr="";
        if (line.split(";")[1].contains(",")){ 
        	bidStr = line.split(";")[2].replace(",", "");
        	askStr = line.split(";")[1].replace(",", "");
        }else{
        	bidStr = line.split(";")[2].replace(".", "");
        	askStr = line.split(";")[1].replace(".", "");
        }
       
     
        bidStr = QuoteShort.fill5(bidStr);
        askStr = QuoteShort.fill5(askStr);
        bid = Integer.valueOf(bidStr);
        ask =Integer.valueOf(askStr);
        
        spread   = (short) (ask-bid);
		
       
        //Tick t = new Tick();
        
        t.setBid(bid);
		t.setAsk(ask);
		t.setSpread(spread);	
		t.setYear(year);
		t.setMonth(month);
		t.setDay(day);
		t.setHh(hh);
		t.setMm(mm);
		t.setSs(ss);
		
		//return t;
	}
	
	
	public static void readFromDisk(ArrayList<Tick> data,String fileName,int type){
		
		//ArrayList<Tick> data = new ArrayList<Tick>();
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    Tick item = null;
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero

	        int i=0;
	        while((line=br.readLine())!=null){
	        	if (i>=1){
	        		if (type==1)//mt4
	        			item = decodeLine(line);
	        		else if (type==0)//peper
	        			item = decodeLinePepperPage(line);
	        		else if (type==2)//dukas
	        			item = decodeLineDukas(line);
	        		else if (type==3)//dukas
	        			item = decodeLineDukas2(line);
	        		else if (type==4)//dukas
	        			item = decodeLineDukas3(line);
	        		
	        		
	        		//if (item.getMonth()<m1 || item.getMonth()>m2) break;
	        		data.add(item);
	        		//System.out.println(item.toString());
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
	   }	
	
public static void createBricks(ArrayList<QuoteShort> data,String fileName,int type,int size){
	
	File archivo = null;
    FileReader fr = null;
    BufferedReader br = null;
    String line="";
    Tick item = null;
    try {
    	// Apertura del fichero y creacion de BufferedReader para poder
        // hacer una lectura comoda (disponer del metodo readLine()).
        archivo = new File (fileName);
        fr = new FileReader (archivo);
        br = new BufferedReader(fr);

        // Lectura del fichero

        int actualHigh = -1;
        int actualLow = -1;
        int actualOpen = -1;
        int i=0;
        int lastBid=-1;
        Calendar lastOpenCal = Calendar.getInstance();
        while((line=br.readLine())!=null){
        	if (i>=1){
        		if (type==1)//mt4
        			item = decodeLine(line);
        		else if (type==0)//peper
        			item = decodeLinePepperPage(line);
        		else if (type==2)//dukas
        			item = decodeLineDukas(line);
        		else if (type==3)//dukas
        			item = decodeLineDukas2(line);
        		else if (type==4)//dukas
        			item = decodeLineDukas3(line);
        		else if (type==5)//dukas
        			item = decodeLineDukas4(line);
        		
        		
        		//if (item.getMonth()<m1 || item.getMonth()>m2) break;
        		int actualBid = item.getBid();
        		
        		if (actualBid!=lastBid){
        			boolean highUpdated = false;
        			boolean lowUpdated = false;
        			if (actualHigh==-1 || lastBid>actualHigh){
        				highUpdated = true;
        				actualHigh = lastBid;
        				if (actualOpen==-1) actualOpen = actualHigh;
        				//System.out.println("NEW BRICK HIGH "+actualHigh+" "+actualLow);
        				Tick.getCalendar(lastOpenCal, item);
        			}
        			if (actualLow==-1 || lastBid<actualLow){
        				lowUpdated = true;
        				actualLow = lastBid;
        				if (actualOpen==-1) actualOpen = actualLow;
        				//System.out.println("NEW BRICK LOW "+actualHigh+" "+actualLow);
        				Tick.getCalendar(lastOpenCal, item);
        			}
        			
        			int sizeBrickH = actualHigh-actualOpen;
        			int sizeBrickL = actualOpen-actualLow;
        			
        			if (sizeBrickH>=size){
        				while (sizeBrickH>=size){
    						QuoteShort qb = new QuoteShort();        					       
        					qb.setOpen5(actualOpen);
        					qb.setLow5(actualOpen);
        					qb.setHigh5(actualOpen+size);
        					qb.setClose5(actualOpen+size);
        					Calendar cal = Calendar.getInstance();
    	        			cal.setTimeInMillis(lastOpenCal.getTimeInMillis());
    	        			qb.setCal(cal);
        					data.add(qb);
        					//System.out.println("[NUEVO TICK]: "+qb.toString());
        					
        					//actualizamos valores del siguiente Brick
        					actualOpen = actualOpen+size;        					
        					actualLow = actualOpen; 
        					sizeBrickH = actualHigh-actualOpen;
        					Tick.getCalendar(lastOpenCal, item);
    					}
        			}else if (sizeBrickL>=size){
        				while (sizeBrickL>=size){
    						QuoteShort qb = new QuoteShort();        					       
        					qb.setOpen5(actualOpen);
        					qb.setHigh5(actualOpen);
        					qb.setLow5(actualOpen-size);
        					qb.setClose5(actualOpen-size);
        					Calendar cal = Calendar.getInstance();
    	        			cal.setTimeInMillis(lastOpenCal.getTimeInMillis());
    	        			qb.setCal(cal);
        					data.add(qb);
        					//System.out.println("[NUEVO TICK]: "+qb.toString());
        					
        					//actualizamos valores del siguiente Brick
        					actualOpen = actualOpen-size;
        					actualHigh = actualOpen;            					            					
        					sizeBrickL = actualOpen-actualLow;
        					Tick.getCalendar(lastOpenCal, item);
    					}        	      
        			}        		
        			lastBid = actualBid;
        		}
        		//System.out.println(item.toString());
        	}
        	i++;
        	//if (i%200000==0)
        		//System.out.println("i: "
        				//+PrintUtils.Print2dec(i*1.0/1000000, false));
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
}
	
public static void readFromDiskToQuoteShort(ArrayList<QuoteShort> data,String fileName,int type){
		
		//ArrayList<Tick> data = new ArrayList<Tick>();
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    Tick item = new Tick();
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero

	        int i=0;
	        int lastBid=-1;
	        int lastAsk = -1;
	        Calendar lastOpenCal = Calendar.getInstance();
	        Calendar cal = Calendar.getInstance();
	        while((line=br.readLine())!=null){
	        	if (i>=1){
	        		if (type==1)//mt4
	        			item = decodeLine(line);
	        		else if (type==0)//peper
	        			item = decodeLinePepperPage(line);
	        		else if (type==2)//dukas
	        			item = decodeLineDukas(line);
	        		else if (type==3)//dukas
	        			item = decodeLineDukas2(line);
	        		else if (type==4)//dukas
	        			item = decodeLineDukas3(line);
	        		else if (type==5)//dukas
	        			item = decodeLineDukas4(line);
	        		else if (type==6 || type==7)//dukas
	        			decodeLineDukas5(line,item);
	        			        		
	        		//if (item.getMonth()<m1 || item.getMonth()>m2) break;
	        		int actualBid = item.getBid();
	        		int actualAsk = item.getAsk();
	        		if (actualBid!=lastBid
	        				|| (actualAsk!=lastAsk && type==7)
	        				){
	        			if (lastBid!=-1){
		        			QuoteShort qb = new QuoteShort();
		        			qb.setOpen5(lastBid);
		        			qb.setHigh5(lastBid);
		        			qb.setLow5(lastBid);
		        			qb.setClose5(lastBid);	
		        			qb.setBid(lastBid);
		        			qb.setAsk(lastAsk);
		        			cal.setTimeInMillis(lastOpenCal.getTimeInMillis());
		        			qb.setCal(cal);
			        		data.add(qb);
	        			}
	        			item.getCalendar(lastOpenCal, item);
	        			lastBid = actualBid;
	        			lastAsk = actualAsk;
	        		}
	        		//System.out.println(item.toString());
	        	}
	        	i++;
	        	//if (i%200000==0)
	        		//System.out.println("i: "+PrintUtils.Print2dec(i*1.0/1000000, false));
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
	   }	
	    
	    public static void readFromDisk(ArrayList<Tick> data,String fileName,int type,int m1,int m2){
			
			//ArrayList<Tick> data = new ArrayList<Tick>();
			File archivo = null;
		    FileReader fr = null;
		    BufferedReader br = null;
		    String line="";
		    Tick item = null;
		    Tick itemLast = null;
		    try {
		    	// Apertura del fichero y creacion de BufferedReader para poder
		        // hacer una lectura comoda (disponer del metodo readLine()).
		        archivo = new File (fileName);
		        fr = new FileReader (archivo);
		        br = new BufferedReader(fr);

		        // Lectura del fichero

		        int i=0;
		        while((line=br.readLine())!=null){
		        	
		        	
		        	if (i>=1){
		        		
		        		if (type==1)//mt4
		        			item = decodeLine(line);
		        		else if (type==0)//peper
		        			item = decodeLinePepperPage(line);
		        		else if (type==2)//dukas
		        			item = decodeLineDukas(line);
		        		else if (type==3)//dukas
		        			item = decodeLineDukas2(line);
		        		else if (type==4)//dukas
		        			item = decodeLineDukas3(line);
		        		
		        		
		        		if (item.getMonth()>m2) break;
		        		if (item.getMonth()>=m1 && item.getMonth()<=m2){
		        			if (data.size()==0){
		        				data.add(item);
		        			}else{
			        			if (data.get(data.size()-1).getBid()!=item.getBid() 
			        					|| data.get(data.size()-1).getAsk()!=item.getAsk()){
			        			
			        				data.add(item);
			        			}
		        			}
		        		}
		        		//System.out.println(item.toString());
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
		
		//return data;
	}
}
