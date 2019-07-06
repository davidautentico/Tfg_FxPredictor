package drosa.experimental.transientprices;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestTransient {

	
	public static int countConsecutive(ArrayList<ArrayList<Integer>> prices,int numCon,int val){
		
		int total = 0;
		for (int i=0;i<prices.size();i++){
			ArrayList<Integer> p = prices.get(i);
			//System.out.println(p.size());
			if (p!=null && p.size()>0){
				int con = 0;
				for (int j=0;j<p.size();j++){
					if (p.get(j)==val){
						con++;
						if (con>=numCon) total++;
					}else con = 0;
				}
			}
		}
		return total;
	}
	
public static int countConsecutive(ArrayList<ArrayList<Integer>> prices,ArrayList<Integer> structure){
		
		int total = 0;
		for (int i=0;i<prices.size();i++){
			ArrayList<Integer> p = prices.get(i);
			//System.out.println(p.size());
			if (p!=null && p.size()>0){
				for (int j=0;j<p.size();j++){
					boolean full = true;
					for (int s=0;s<structure.size();s++){
						if ((j+s)>=p.size() || p.get(j+s)!=structure.get(s)){
							full=false;
							break;
						}
					}
					if (full) total++;
				}
			}
		}
		return total;
	}

	public static void testTransientCandles(ArrayList<QuoteShort> data,int h1,int h2,int h,int k){
	
		Calendar cal = Calendar.getInstance();

		int totalCandleTrans = 0;
		int totalCandleTransK = 0;
		int totalCandles = 0;
		int totalCandlesK = 0;
		for (int i=h+1;i+1+h<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			if (hour<h1 || hour>h2) continue;
			
			QuoteShort qL = TradingUtils.getMaxMinShort(data, i-1-h, i-1);
			QuoteShort qR = TradingUtils.getMaxMinShort(data, i+1, i+1+h);
			
			boolean candleTrans = false;
			int kCount = 0;
			int kCountL = 0;
			for (int val=q.getLow();val<=q.getHigh();val++){
				boolean trans = true;
				if (qL.getLow()<=val && val<=qL.getHigh()){
					trans=false;
					kCountL++;
				}
				if (qR.getLow()<=val && val<=qR.getHigh()){
					trans=false;
				}
				if (trans){
					candleTrans = true;
					kCount++;
				}else{
					//
				};
			}
			if (candleTrans) totalCandleTrans++;
			if (kCountL>=k){
				totalCandlesK++;
				if (kCount>=k){
					totalCandleTransK++;
				}
			}
			totalCandles++;			
		}
		double perTransient  = totalCandleTrans*100.0 / totalCandles;
		double perTransientK = totalCandleTransK*100.0 / totalCandlesK;
		
		System.out.println(h1 +" "+h2+" "+h+" "+k
				+" "+totalCandles
				+" "+PrintUtils.Print2(perTransient)
				+" "+PrintUtils.Print2(perTransientK)
				);
	}
	
	public static void testTransient(ArrayList<QuoteShort> data,int h1,int h2,int h){
		
		ArrayList<ArrayList<Integer>> prices =  new ArrayList<ArrayList<Integer>>();
		for (int i=0;i<40000;i++){
			ArrayList<Integer> p = new ArrayList<Integer>();
			prices.add(p);
		}
			
		Calendar cal = Calendar.getInstance();
		
		int totalPrices = 0;
		int totalTransients = 0;
		int totalCandleTrans = 0;
		int totalCandles = 0;
		for (int i=h+1;i+1+h<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			if (hour<h1 || hour>h2) continue;
			
			QuoteShort qL = TradingUtils.getMaxMinShort(data, i-1-h, i-1);
			QuoteShort qR = TradingUtils.getMaxMinShort(data, i+1, i+1+h);
			
			boolean candleTrans = false;
			for (int val=q.getLow();val<=q.getHigh();val++){
				boolean trans = true;
				if (qL.getLow()<=val && val<=qL.getHigh()){
					trans=false;
				}
				if (qR.getLow()<=val && val<=qR.getHigh()){
					trans=false;
				}
				if (trans){
					prices.get(val).add(1);
					totalTransients++;
					candleTrans = true;
				}else{
					prices.get(val).add(0);
				}
				totalPrices++;
			}
			if (candleTrans) totalCandleTrans++;
			totalCandles++;
		}
		ArrayList<Integer> s11111 = new ArrayList<Integer>(); s11111.add(1);s11111.add(1);s11111.add(1);s11111.add(1);s11111.add(1);
		ArrayList<Integer> s1111 = new ArrayList<Integer>(); s1111.add(1);s1111.add(1);s1111.add(1);s1111.add(1);
		ArrayList<Integer> s111 = new ArrayList<Integer>(); s111.add(1);s111.add(1);s111.add(1);
		ArrayList<Integer> s11 = new ArrayList<Integer>(); s11.add(1);s11.add(1);
		ArrayList<Integer> s0000 = new ArrayList<Integer>(); s0000.add(0);s0000.add(0);s0000.add(0);s0000.add(0);
		ArrayList<Integer> s000 = new ArrayList<Integer>(); s000.add(0);s000.add(0);s000.add(0);
		ArrayList<Integer> s00 = new ArrayList<Integer>(); s00.add(0);s00.add(0);
		ArrayList<Integer> s00001 = new ArrayList<Integer>(); s00001.add(0);s00001.add(0);s00001.add(0);s00001.add(0);s00001.add(1);
		ArrayList<Integer> s0001 = new ArrayList<Integer>(); s0001.add(0);s0001.add(0);s0001.add(0);s0001.add(1);
		ArrayList<Integer> s001 = new ArrayList<Integer>(); s001.add(0);s001.add(0);s001.add(1);
		ArrayList<Integer> s01 = new ArrayList<Integer>(); s01.add(0);s01.add(1);
		ArrayList<Integer> s0 = new ArrayList<Integer>(); s0.add(0);
		ArrayList<Integer> s1 = new ArrayList<Integer>(); s1.add(1);
		int totals0  =  countConsecutive(prices,s0);
		int totals00  =  countConsecutive(prices,s00);
		int totals000  =  countConsecutive(prices,s000);
		int totals0000  =  countConsecutive(prices,s0000);
		int totals1  =  countConsecutive(prices,s1);
		int totals1111 =  countConsecutive(prices,s1111);
		int totals11111 =  countConsecutive(prices,s11111);
		int totals111 =  countConsecutive(prices,s111);
		int totals11 =  countConsecutive(prices,s11);
		int totals01 =  countConsecutive(prices,s01);
		int totals001 =  countConsecutive(prices,s001);
		int totals0001 =  countConsecutive(prices,s0001);
		int totals00001 =  countConsecutive(prices,s00001);
		int total2 =  countConsecutive(prices,2,1);
		int total3 =  countConsecutive(prices,3,1);
		int total4 =  countConsecutive(prices,4,1);
		
		double per0 = totals0*100.0/totalPrices;
		double per1 = totals1*100.0/totalPrices;
		double per01 = totals01*100.0/totals0;
		double per001 = totals001*100.0/totals00;
		double per0001 = totals0001*100.0/totals000;
		double per00001 = totals00001*100.0/totals0000;
		double per11 = totals11*100.0/totals1;
		double per111 = totals111*100.0/totals11;
		double per1111 = totals1111*100.0/totals111;
		double per11111 = totals11111*100.0/totals1111;
		
		double perTrans = totalTransients*100.0/totalPrices;
		double perTrans2 = total2*100.0/totalTransients;
		double perTrans3 = total3*100.0/total2;
		double perTrans4 = total4*100.0/total3;
		double totalBarsTrans = totalCandleTrans*100.0/totalCandles;
		
		/*System.out.println(
				" "+h
				+" "+totalPrices
				+" "+totals0+" ("+PrintUtils.Print2(per0)+")"
				+" "+totals01+" ("+PrintUtils.Print2(per01)+")"
				+" "+totals001+" ("+PrintUtils.Print2(per001)+")"
				+" "+totals0001+" ("+PrintUtils.Print2(per0001)+")"
				+" "+totals00001+" ("+PrintUtils.Print2(per00001)+")"
				);*/
		
		System.out.println(
				" "+h
				+" "+totalCandles
				+" "+ totalCandleTrans+" ("+PrintUtils.Print2(totalBarsTrans)+")"
				+" || "+totalPrices
				//+" "+totals0+" ("+PrintUtils.Print2(per0)+")"
				+" "+totals1+" ("+PrintUtils.Print2(per1)+")"
				+" "+totals11+" ("+PrintUtils.Print2(per11)+")"
				+" "+totals111+" ("+PrintUtils.Print2(per111)+")"
				+" "+totals1111+" ("+PrintUtils.Print2(per1111)+")"
				+" "+totals11111+" ("+PrintUtils.Print2(per11111)+")"
				);
		/*System.out.println(h+" "+h1+" "+h2
				+" "+totalCandles
				+" "+totalCandleTrans
				+" "+PrintUtils.Print2(totalBarsTrans)
				+"     "+totalPrices
				+" "+totalTransients
				+" "+PrintUtils.Print2(perTrans)
				+"     "+total2 
				+" "+totals11
				+" "+totals01
				+" "+PrintUtils.Print2(perTrans2)
				+"     "+total3
				+" "+PrintUtils.Print2(perTrans3)
				+"     "+total4
				+" "+PrintUtils.Print2(perTrans4)
				);*/
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2003.05.04_2014.09.30.csv";
		String path5m   = "c:\\fxdata\\EURUSD_UTC_5 Mins_Bid_2006.12.31_2015.07.28.csv";
		//String path5m   = "c:\\fxdata\\EURUSD_UTC_1 Min_Bid_2003.05.04_2014.12.30.csv";
		
		Sizeof.runGC ();
		//ArrayList<Quote> data1I 	= DAO.retrieveData(path1m, DataProvider.DUKASCOPY_FOREX);
		//ArrayList<Quote> data1S 		= TestLines.calculateCalendarAdjusted(data1I);
		//ArrayList<Quote> data1m 	= TradingUtils.cleanWeekendData(data1S);
		
		ArrayList<Quote> dataI 		= DAO.retrieveData(path5m, DataProvider.DUKASCOPY_FOREX);
		ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 
  		
  		//ArrayList<Quote> data10m  = ConvertLib.convert(data5m,2);
  		//ArrayList<Quote> data15m  = ConvertLib.convert(data5m,3);
  		//ArrayList<Quote> data20m  = ConvertLib.convert(data5m,4);
  		//ArrayList<Quote> data4h = ConvertLib.convert(data5m,48);
  		
  		//ArrayList<QuoteShort> data1mS  = QuoteShort.convertQuoteArraytoQuoteShort(data1m);
		ArrayList<QuoteShort> data5mS  = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		//ArrayList<QuoteShort> data15mS  = QuoteShort.convertQuoteArraytoQuoteShort(data15m);
		//ArrayList<QuoteShort> data4hS  = QuoteShort.convertQuoteArraytoQuoteShort(data4h);
		
		for (int h1=0;h1<=0;h1++){
			int h2 = h1+23;
			for (int h=1;h<=50;h++){
				for (int k=1;k<=1;k++){
					TestTransient.testTransient(data5mS,h1,h2, h);
					//TestTransient.testTransient(data5mS,h1,h2, h);
					//TestTransient.testTransient(data5mS,h1,h2, h);
					//TestTransient.testTransient(data15mS,h1,h2, h);
					//TestTransient.testTransientCandles(data5mS,h1,h2, h,k);
				}
			}
		}
	}

}
