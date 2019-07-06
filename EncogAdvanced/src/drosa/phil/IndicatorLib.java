/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drosa.phil;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.utils.PrintUtils;

/**
 *
 * @author drosa
 */
public class IndicatorLib {
    
    
    public static ArrayList<Quote> calculateATR(ArrayList<Quote> data, int n) {
		
        ArrayList<Quote> atr = new ArrayList<Quote>();
		
	for (int i=0;i<data.size();i++){
            Date d = data.get(i).getDate();
            double value = getATR(data,i-n,i);
            Quote q = new Quote();
            q.setDate(d);
            q.setClose(value);
            //System.out.println("[calculateMA] "+DateUtils.datePrint(q.getDate())+" "+PrintUtils.Print(q.getClose()));
            atr.add(q);
        }		
	return atr;
    }

    public static double getAtrShort(ArrayList<QuoteShort> data, int begin, int end) {
                    // TODO Auto-generated method stub
	double max = -99999;
	if (begin<0)
		begin=0;
	if (end>data.size()-1)
		end = data.size()-1;
		int value1=-99;
		int value2=-99;
		int value3=-99;
		int total=0;
		int avg=0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = null;
			value1 = q.getHigh5()-q.getLow5();
			if (i>0){
				q1 = data.get(i-1);
				value2=Math.abs(q.getHigh5()-q1.getClose5());
				value3=Math.abs(q1.getClose5()-q.getLow5());
			}
			max = value1;
			if (value2>max)
				max=value2;
			if (value3>max)
				max=value3;
			avg+=max;
			total++;
		}
		return (double)avg*1.0/total;
    }
    
    public static double getATR(ArrayList<Quote> data, int begin, int end) {
        // TODO Auto-generated method stub
double max = -99999;
if (begin<0)
begin=0;
if (end>data.size()-1)
end = data.size()-1;
double value1=-99;
double value2=-99;
double value3=-99;
int total=0;
double avg=0.0;
for (int i=begin;i<=end;i++){
Quote q = data.get(i);
Quote q1 = null;
value1 = q.getHigh()-q.getLow();
if (i>0){
	q1 = data.get(i-1);
	value2=Math.abs(q.getHigh()-q1.getClose());
	value3=Math.abs(q1.getClose()-q.getLow());
}
max = value1;
if (value2>max)
	max=value2;
if (value3>max)
	max=value3;
avg+=max;
total++;
}
return (double)avg/total;
}
    
    public static double calculateTMA_Last(ArrayList<Quote> data, int end){
        
    	
        int halfLength = 56;
        
        double sumW = halfLength + 1;
        double sum  = sumW * data.get(end).getClose();
        
        for (int i = end,j=halfLength;j>=1 && i>=0;i--,j--){
            sumW += j;
            sum  += j * data.get(i).getClose();
        }
        
        System.out.println("[calculateTMA_Last] end "+end);
        
        return sum/sumW;
    }
    
    public static double calculateTMA_Last(ArrayList<Quote> data, int end,int halfLength){

        double sumW = halfLength + 1;
        double sum  = sumW * data.get(end).getClose();
        
        for (int i = end,j=halfLength;j>=1 && i>=0;i--,j--){
            sumW += j;
            sum  += j * data.get(i).getClose();
        }
        
        //System.out.println("[calculateTMA_Last] end "+end);
        
        return sum/sumW;
    }
    
    public static double calculateTMA_LastShort(ArrayList<QuoteShort> data, int end,int halfLength){

        double sumW = halfLength + 1;
        double sum  = sumW * data.get(end).getClose5();
        
        for (int i = end,j=halfLength;j>=1 && i>=0;i--,j--){
            sumW += j;
            sum  += j * data.get(i).getClose5();
        }
        //System.out.println("[calculateTMA_Last] end "+end);
        return sum/sumW;
    }
    
    public static ArrayList<TMA> calculateTMA_Array(ArrayList<Quote> data,int begin,int end){
        ArrayList<TMA> tma = new ArrayList<TMA>();
        
        
        for (int i=begin;i<=end;i++){
            int beginR = i-100;            
            if (beginR<0) beginR = 0;
            
            //valores para posicion i
            double tma_value = 0;
            double range = 0;
            if ((i-1)>=0){
                range = IndicatorLib.getATR(data,beginR, i-1); //ATR            
                tma_value = calculateTMA_Last(data,i-1);                                                   
            }
            System.out.println("range tma_value: "+PrintUtils.Print(range)+" "+PrintUtils.Print(tma_value));
            TMA tmaPoint = new TMA(); 
            tmaPoint.setUpper(tma_value+2.9*range);
            tmaPoint.setMiddle(tma_value);
            tmaPoint.setLower(tma_value-2.9*range);
            Calendar cal = Calendar.getInstance();
            cal.setTime(data.get(i).getDate());
            tmaPoint.setDate(cal);
            tma.add(tmaPoint);
            
            /*System.out.println("Añadiendo punto TMA rangos de tiempo: "
                    +" "+DateUtils.datePrint(cal.getTime())
                    +" "+PrintUtils.Print(range)
                    +" "+PrintUtils.Print(tma_value)                    
                    );
            */
        }
        return tma;
    }
    
    public static ArrayList<TMA> calculateTMA_Array(ArrayList<Quote> data,int begin,int end,double bandFactor,int halfLength,
    				int atrPeriod){
        ArrayList<TMA> tma = new ArrayList<TMA>();
        
        
        for (int i=begin;i<=end;i++){
            int beginR = i-atrPeriod;            
            if (beginR<0) beginR = 0;
            
            //valores para posicion i
            double tma_value = 0;
            double range = 0;
            if ((i-1)>=0){
                range = IndicatorLib.getATR(data,beginR, i-1); //ATR            
                tma_value = calculateTMA_Last(data,i-1,halfLength);                                                   
            }
            //System.out.println("range tma_value: "+PrintUtils.Print(range)+" "+PrintUtils.Print(tma_value));
            TMA tmaPoint = new TMA(); 
            tmaPoint.setUpper(tma_value+bandFactor*range);
            tmaPoint.setMiddle(tma_value);
            tmaPoint.setLower(tma_value-bandFactor*range);
            Calendar cal = Calendar.getInstance();
            cal.setTime(data.get(i).getDate());
            tmaPoint.setDate(cal);
            tma.add(tmaPoint);
            
            /*System.out.println("Añadiendo punto TMA rangos de tiempo: "
                    +" "+DateUtils.datePrint(cal.getTime())
                    +" "+PrintUtils.Print(range)
                    +" "+PrintUtils.Print(tma_value)                    
                    );
            */
        }
        return tma;
    }
    
    public static ArrayList<TmaShort> calculateTMA_ArrayShort(ArrayList<QuoteShort> data,int begin,int end,double bandFactor,int halfLength,
			int atrPeriod){
		ArrayList<TmaShort> tma = new ArrayList<TmaShort>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
		    int beginR = i-atrPeriod;            
		    if (beginR<0) beginR = 0;
		    
		    //valores para posicion i
		    double tma_value = 0;
		    double range = 0;
		    if ((i-1)>=0){
		        range     = IndicatorLib.getAtrShort(data,beginR, i-1); //ATR            
		        tma_value = calculateTMA_LastShort(data,i-1,halfLength);                                                   
		    }
		    //System.out.println("range tma_value: "+PrintUtils.Print(range)+" "+PrintUtils.Print(tma_value));
		    TmaShort tmaPoint = new TmaShort(); 
		    tmaPoint.setUpper((int) (tma_value+bandFactor*range));
		    tmaPoint.setMiddle((int) tma_value);
		    tmaPoint.setLower((int) (tma_value-bandFactor*range));
		    
		    QuoteShort.getCalendar(cal, data.get(i));
		    tmaPoint.setCal(cal);
		   //tmaPoint.setDate(cal);
		    tma.add(tmaPoint);
		}
		return tma;
    }
    
    
    public static void main(String[] args) {
    	
    	ArrayList<Quote> data = new ArrayList<Quote>();
    	Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2012);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
    	for (int i=0;i<=200;i++){
    		Quote q = new Quote();
    		q.setOpen(i+1);
    		q.setHigh(i+3);
    		q.setLow(i+2);
    		q.setClose(i+4);
    		Calendar cal2 = Calendar.getInstance();
    		cal2.setTime(cal.getTime());
    		cal2.add(Calendar.DAY_OF_YEAR, 1);
    		q.setDate(cal.getTime());
    		data.add(q);
    	}
    	
    	ArrayList<TMA> tma = IndicatorLib.calculateTMA_Array(data, 0,200);
    	for (int i=0;i<tma.size();i++){
    		System.out.println(i
    				+" "+PrintUtils.Print(tma.get(i).getUpper())
    				+" "+PrintUtils.Print(tma.get(i).getLower())
    				);
    	}
    }
}
