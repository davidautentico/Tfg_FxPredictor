package drosa.experimental.zznbrum;

import java.util.ArrayList;
import java.util.Random;

public class SimulateModel {
	
	public static void generatePrices(int priceStart,ArrayList<Integer> freqs,
			ArrayList<Integer> sizes, boolean debug,
			ArrayList<Integer> prices
			){
		
		int count = 0;
		for (int i=0;i<freqs.size();i++){
			count += freqs.get(i);
		}
		
		Random rand = new Random();
		
		ArrayList<Integer> totalList = new ArrayList<Integer>();
		for (int i=1;i<=11;i++){
			int f = freqs.get(i-1);
			int s = sizes.get(i-1);
			
			for (int j=1;j<=f;j++){
				totalList.add(s);
			}
		}
		
		int i = 0;
		int mode = 1;
		if (debug)
			System.out.println(priceStart);
		prices.add(priceStart);
		while (totalList.size()>0){
			int  n = rand.nextInt(totalList.size()); //entre 0 y 11
			int value = totalList.get(n);

			priceStart += mode*value;
			prices.add(priceStart);
			mode = -mode;//cambiamos orientacion
			totalList.remove(n);	
			if (debug)
				System.out.println(priceStart+" || "+n+" "+value*mode+" "+totalList.size());		
		}
		
		
	}

	
	public static void doTrade(ArrayList<Integer> prices) {
		// TODO Auto-generated method stub
		
		int sellsCount = 0;
		int buysCount = 0;
		
		int lastPrice = prices.get(0);
		int lastEntries = 0;
		int lastCurrentPrice = 0;
		int mode = 0;
		System.out.println(lastPrice);
		for (int i=1;i<prices.size()-2;i++){
			int price = prices.get(i);
			
			int diff = price-lastPrice;
			int entries = diff/200;
			String entriesStr = "";
			if (diff>0){
				mode = 1;
				for (int e=1;e<=entries;e++){
					entriesStr+= " "+(lastPrice+e*200);
					lastCurrentPrice =lastPrice+e*200;
				}
				sellsCount+=entries;
			}else{
				mode = -1;
				for (int e=1;e<=-entries;e++){
					entriesStr+= " "+(lastPrice-e*200);
					lastCurrentPrice =lastPrice-e*200;
				}
				buysCount+=entries;
			}
			
			int diffFuture = lastCurrentPrice-prices.get(i+1);
			if (mode==-1)
				diffFuture = prices.get(i+1)-lastCurrentPrice;
			if (Math.abs(entries)>=9 
					//&& diffFuture<=200
					){
				
				System.out.println(lastPrice+" "+price+" "+prices.get(i+1)
						+" ||  "+entries
						+" || "+lastCurrentPrice+" "+diffFuture
						);
			}
			
			/*if (Math.abs(entries)>=10){
				System.out.println(lastPrice+" "+price
						+" || "+diff+" "+lastEntries+" "+entries
						+" ||| "+entriesStr
						+" ||| "+buysCount+" "+sellsCount
						);
			}*/
			
			lastEntries = entries;
			lastPrice = price;
		}
	}
	
	
	public static void main(String[] args) {
		
		ArrayList<Integer> freqs = new ArrayList<Integer>();
		ArrayList<Integer> sizes = new ArrayList<Integer>();
		
		freqs.add(6260);freqs.add(2335);freqs.add(848);
		freqs.add(314);freqs.add(114);freqs.add(45);
		freqs.add(12);freqs.add(10);freqs.add(4);
		freqs.add(1);freqs.add(1);
		for (int i=1;i<=11;i++){
			sizes.add(10+20*i);
		}
		
		int priceStart = 10000;
		
		ArrayList<Integer> prices = new ArrayList<Integer>();
		SimulateModel.generatePrices(priceStart,freqs,sizes,true,prices);
		
		SimulateModel.doTrade(prices);

	}

	

}
