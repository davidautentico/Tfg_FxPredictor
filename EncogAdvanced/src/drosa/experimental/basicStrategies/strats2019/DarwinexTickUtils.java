package drosa.experimental.basicStrategies.strats2019;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class DarwinexTickUtils {
	
	public static void readTickFile(String fileName,ArrayList<QuoteShort> data){
		
	}
	
	public static void readTick(String masterFolder,ArrayList<QuoteShort> data,String symbol){
		
		File folder = new File(masterFolder);
		File[] listOfFiles = folder.listFiles();
		HashMap<Long,QuoteShort> map = new HashMap<Long,QuoteShort>();
		Calendar cal = Calendar.getInstance();
		for (int year=2018;year<=2019;year++){
			for (int m=1;m<=12;m++){
				for (int d=1;d<=31;d++){
					for (int h=0;h<=23;h++){
						String mStr = DateUtils.getAlways2digits(m);
						String dStr = DateUtils.getAlways2digits(d);
						String hStr = DateUtils.getAlways2digits(h);
						String fileAsk = masterFolder+"\\"+symbol+"_ASK_"+year+"-"+mStr+"-"+dStr+"_"+hStr+".log";
						String fileBid = masterFolder+"\\"+symbol+"_BID_"+year+"-"+mStr+"-"+dStr+"_"+hStr+".log";
						
						File fileA = new File(fileAsk);
						File fileB = new File(fileBid);
						
						if (fileA.exists() && fileB.exists()){
							System.out.println("Existe: "+fileAsk+" "+fileBid);
							FileReader fr = null;
							BufferedReader br = null;
							
							try {
								fr = new FileReader (fileAsk);
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						    br = new BufferedReader(fr);
							String line;
						    int i=0;
						    QuoteShort lastQ = null;
						    try {
								while((line=br.readLine())!=null){
									if (i>=0){
										String[] values = line.split(",");
										long ts = Long.valueOf(values[0]);
										String valueStr = values[1].replace(".", "");
										cal.setTimeInMillis(ts);
										short yyyy = (short) cal.get(Calendar.YEAR);
										byte mm = (byte) cal.get(Calendar.MONTH);
										byte dd = (byte) cal.get(Calendar.DAY_OF_MONTH);
										byte hh = (byte) cal.get(Calendar.HOUR_OF_DAY);
										byte min = (byte) cal.get(Calendar.MINUTE);
										byte ss = (byte) cal.get(Calendar.SECOND);
										
										//System.out.println(ts+" "+valueStr);
										
										if (!map.containsKey(ts)){
											QuoteShort q = new QuoteShort();
											map.put(ts, q);
											//data.add(q);
										}
										QuoteShort q = map.get(ts);
										q.setYear(yyyy);
										q.setMonth((byte) (mm+1));
										q.setDay(dd);
										q.setHh(hh);
										q.setMm(min);
										q.setSs(ss);
										q.setAsk(Integer.valueOf(valueStr));
										
								    }  
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						    
						    //BID
						    try {
								fr = new FileReader (fileBid);
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						    br = new BufferedReader(fr);
						    i=0;
						    try {
								while((line=br.readLine())!=null){
									if (i>=0){
										String[] values = line.split(",");
										long ts = Long.valueOf(values[0]);
										String valueStr = values[1].replace(".", "");
										cal.setTimeInMillis(ts);
										short yyyy = (short) cal.get(Calendar.YEAR);
										byte mm = (byte) cal.get(Calendar.MONTH);
										byte dd = (byte) cal.get(Calendar.DAY_OF_MONTH);
										byte hh = (byte) cal.get(Calendar.HOUR_OF_DAY);
										byte min = (byte) cal.get(Calendar.MINUTE);
										byte ss = (byte) cal.get(Calendar.SECOND);
										
										//System.out.println(ts+" "+valueStr);
										
										if (!map.containsKey(ts)){
											QuoteShort q = new QuoteShort();
											map.put(ts, q);
											//data.add(q);
										}
										QuoteShort q = map.get(ts);
										q.setYear(yyyy);
										q.setMonth((byte) (mm+1));
										q.setDay(dd);
										q.setHh(hh);
										q.setMm(min);
										q.setSs(ss);
										q.setBid(Integer.valueOf(valueStr));
										
										//System.out.println("[NEW TICK] "+q.toStringTick());
										
								    }  
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							//System.out.println("NO Existe!: "+fileAsk+" "+fileBid);
						}
					}
				}
			}			
		}

		List sortedKeys=new ArrayList(map.keySet());
		Collections.sort(sortedKeys);
		
		//ajustamos -1s
		int lastValidAsk = -1;
		int lastValidBid = -1;
		int accSpread = 0;
		for (int i=0;i<sortedKeys.size();i++){
			long keyl = (long) sortedKeys.get(i);
			QuoteShort q = map.get(keyl);
			
			if (q.getAsk()==-1) q.setAsk(lastValidAsk);
			if (q.getBid()==-1) q.setBid(lastValidBid);
			
			if (q.getAsk()>0) lastValidAsk = q.getAsk();
			if (q.getBid()>0) lastValidBid = q.getBid();
			
			accSpread += q.getAsk()-q.getBid();
			data.add(q);
		}
		System.out.println("total ticks: "+data.size()+" "+PrintUtils.Print2dec(accSpread*1.0/data.size(), false));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String masterFolder = "C:\\fxdata\\darwinex\\gdaxi";
		String symbol = "GDAXIm";
		String outputFileName = masterFolder+"\\GDAXIm.csv";
		ArrayList<QuoteShort> data = new ArrayList<QuoteShort>();
		
		DarwinexTickUtils.readTick(masterFolder, data, symbol);
		
		QuoteShort.saveToDiskTick(data, outputFileName);
	}

}
