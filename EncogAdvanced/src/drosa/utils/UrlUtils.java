package drosa.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import drosa.finances.Quote;
import drosa.finances.SplitData;

public class UrlUtils {
	
	
	public static List<Quote> urlGet(String req){
				
		BufferedReader rd=null;
		List<String> data = new ArrayList<String>();
		
		try{
			URL url = new URL(req);
			
			URLConnection conn = (URLConnection) url.openConnection();
			
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line;
			
			while ((line = rd.readLine()) !=null){
				data.add(line);
				//response +=line;
			}
		}catch (Exception e){
			System.out.println("Web request failed");
			
		}finally{
			if (rd!=null){
				try{
					rd.close();
					
				}catch(IOException ex){
					System.out.println("Problem clossing reader");
				}
			}
		}
		
		List<Quote> quotes = new ArrayList<Quote>();
		//System.out.println("Data size: "+data.size());
		for (int i=1;i<data.size();i++){
			try{
				Quote q = new Quote();
				String[] array =data.get(i).split(",");
			
				SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd"); 
				java.util.Date date;
			
				date = sdf.parse(array[0]);
						
				q.setDate(date);
				q.setOpen(Float.valueOf(array[1]));
				q.setHigh(Float.valueOf(array[2]));
				q.setLow(Float.valueOf(array[3]));
				q.setClose(Float.valueOf(array[4]));
				q.setVolume(Long.valueOf(array[5]));
				q.setAdjClose(Float.valueOf(array[6]));
			
				quotes.add(q);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		//System.out.println("Size of quotes: "+quotes.size());
		return quotes;
	}
	
	public static List<SplitData> urlGetSplitData(String req){
		
		BufferedReader rd=null;
		List<String> data = new ArrayList<String>();
		
		try{
			URL url = new URL(req);
			
			URLConnection conn = (URLConnection) url.openConnection();
			
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line;
			
			while ((line = rd.readLine()) !=null){
				data.add(line);
				//response +=line;
			}
		}catch (Exception e){
			System.out.println("Web request failed");
			
		}finally{
			if (rd!=null){
				try{
					rd.close();
					
				}catch(IOException ex){
					System.out.println("Problem clossing reader");
				}
			}
		}
		
		List<SplitData> dataS = new ArrayList<SplitData>();
		System.out.println("Data size: "+data.size());
		for (int i=1;i<data.size();i++){
			try{
				//System.out.println("Data : "+data.get(i));
				
				SplitData s = new SplitData();
				String[] array =data.get(i).split(",");
				
				if (array[0].equalsIgnoreCase("DIVIDEND") ||
					array[0].equalsIgnoreCase("SPLIT")){
							
					SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd"); 
					java.util.Date date;
			
					date = sdf.parse(array[1].substring(0,5)+"-"+array[1].substring(5,7)
						+"-"+array[1].substring(7,9));
						
					s.setDate(date);
					if (array[0].equalsIgnoreCase("DIVIDEND")){
						s.setType(QuoteDataType.DIVIDEND);
						s.setValue(Float.valueOf(array[2]));
					}else{
						s.setType(QuoteDataType.SPLIT);
					
						float firstfactor = Float.valueOf(array[2].substring(0,array[2].indexOf(':')));
						float secondfactor =Float.valueOf(array[2].substring(array[2].indexOf(':')+1));
						float splitfactor = secondfactor/firstfactor;
						s.setValue(splitfactor);
						
						System.out.println(array[1].substring(0,5)+"-"+array[1].substring(5,7)
								+"-"+array[1].substring(7,9)+">>Split "+firstfactor+':'+secondfactor+"->"+splitfactor);
					}
			
					dataS.add(s);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		//System.out.println("Size of quotes: "+quotes.size());
		return dataS;
	}
	
	public static List<String> urlGet2(String req){
		
		BufferedReader rd=null;
		List<String> data = new ArrayList<String>();
		
		try{
			URL url = new URL(req);
			
			URLConnection conn = (URLConnection) url.openConnection();
			
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line;
			
			while ((line = rd.readLine()) !=null){
				data.add(line);
				//response +=line;
			}
		}catch (Exception e){
			System.out.println("Web request failed");
			
		}finally{
			if (rd!=null){
				try{
					rd.close();
					
				}catch(IOException ex){
					System.out.println("Problem clossing reader");
				}
			}
		}		
		return data;
	}

	public static List<Quote> urlGetEodData(String req,
			boolean eodAdjusted) {
		// TODO Auto-generated method stub
		BufferedReader rd=null;
		List<String> data = new ArrayList<String>();
		
		try{
			URL url = new URL(req);
			
			URLConnection conn = (URLConnection) url.openConnection();
			
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line;
			
			while ((line = rd.readLine()) !=null){
				data.add(line);
				//response +=line;
			}
		}catch (Exception e){
			System.out.println("Web request failed");
			
		}finally{
			if (rd!=null){
				try{
					rd.close();
					
				}catch(IOException ex){
					System.out.println("Problem clossing reader");
				}
			}
		}
		
		List<Quote> quotes = new ArrayList<Quote>();
		//System.out.println("Data size: "+data.size());
		for (int i=1;i<data.size();i++){
			try{
				Quote q = new Quote();
				String[] array =data.get(i).split(",");
			
				SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd"); 
				java.util.Date date;
			
				date = sdf.parse(array[0]);
				
				float factor =1;
				
				if (eodAdjusted){
					factor = Float.valueOf(array[6])/Float.valueOf(array[4]);
				}
				
				q.setDate(date);
				q.setOpen(factor*Float.valueOf(array[1]));
				q.setHigh(factor*Float.valueOf(array[2]));
				q.setLow(factor*Float.valueOf(array[3]));
				q.setClose(factor*Float.valueOf(array[4]));
				q.setVolume(Long.valueOf(array[5]));
				q.setAdjClose(Float.valueOf(array[6]));
			
				quotes.add(q);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		//System.out.println("Size of quotes: "+quotes.size());
		return quotes;
	}
	

}
