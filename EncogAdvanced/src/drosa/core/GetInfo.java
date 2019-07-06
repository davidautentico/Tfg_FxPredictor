package drosa.core;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.IntradayQuote;
import drosa.finances.Quote;
import drosa.finances.Stock;
import drosa.finances.Volume;
import drosa.utils.DateUtils;
import drosa.utils.FileUtils;
import drosa.utils.StockUtils;
import drosa.utils.TimeFrame;

public class GetInfo {

	String symbolPath ="";
	SQLConnectionUtils sqlHelper = null;
	boolean firstInserted=false;
	
	
	public GetInfo(){
		sqlHelper = new SQLConnectionUtils();
		sqlHelper.init();
		firstInserted=false;
	}
	
	private void readProperties()
	{		
		Properties prop = new Properties();
		InputStream is = null;

		try {
			InputStream in = this.getClass().getResourceAsStream("/drosa/properties/config.properties");
			if (in!=null){
				prop.load(in);
			
				for (Enumeration e = prop.keys(); e.hasMoreElements() ; ) {
					// Obtenemos el objeto
					Object obj = e.nextElement();
					//System.out.println(obj + ": " + prop.getProperty(obj.toString()));
					if (((String)obj).equalsIgnoreCase("symbolPath")){
						symbolPath = prop.getProperty(obj.toString());
					}
				}
			}else{
				System.out.println("Properties no encontrado");
			}
		} catch(IOException ioe) {
			System.out.println("[GetInfo] Error: "+ioe.getMessage());
		}
	}
		
	private void insertSymbolListQuotes(List<String> symbolList) throws Exception{
		List<Quote> data = null;
		Stock s = new Stock();
		for (int i=0;i<symbolList.size();i++){
			System.out.println("Symbol to insert: "+symbolList.get(i));
			String symbol = symbolList.get(i);
			
			//DAILY			
			GregorianCalendar fromDate  = DateUtils.stringToDateFormat(DAO.getLastInsertDate(sqlHelper,symbol, TimeFrame.DAILY));
			GregorianCalendar todayDate = DateUtils.getTodayDate();
			
			System.out.println("[DAILY]From Date and TodayDate : "+DateUtils.datePrint(fromDate)+","+DateUtils.datePrint(todayDate));
			fromDate.add(GregorianCalendar.DAY_OF_MONTH,1);
			data = StockUtils.getEodCSVData(symbol,fromDate,todayDate,TimeFrame.DAILY);	
			
			//System.out.println("[insertSymbolListQuotes]Datos obtenidos: "+data.size());
			//data = StockUtils.getEodCSVData(symbol,fromDate,todayDate,TimeFrame.DAILY);	
			s.setSymbol(symbol);
			s.setQuotes(data);
			s.setTf(TimeFrame.DAILY);
			DAO.storeStock(sqlHelper,s);
			
			//WEEKLY
			fromDate  = DateUtils.stringToDateFormat(DAO.getLastInsertDate(sqlHelper,symbol, TimeFrame.WEEKLY));
			todayDate = DateUtils.getTodayDate();
			
			System.out.println("[WEEKLY]From Date and TodayDate : "+DateUtils.datePrint(fromDate)+","+DateUtils.datePrint(todayDate));
			
			data = StockUtils.getEodCSVData(symbol,fromDate,todayDate,TimeFrame.WEEKLY);	
			s.setSymbol(symbol);
			s.setQuotes(data);
			s.setTf(TimeFrame.WEEKLY);
			DAO.storeStock(sqlHelper,s);
			
			//MONTHLY
			//fromDate.add(GregorianCalendar.MONTH,1);
			fromDate  = DateUtils.stringToDateFormat(DAO.getLastInsertDate(sqlHelper,symbol, TimeFrame.MONTHLY));
			todayDate = DateUtils.getTodayDate();
			
			System.out.println("[MONTLY]From Date and TodayDate : "+DateUtils.datePrint(fromDate)+","+DateUtils.datePrint(todayDate));
			
			data =  StockUtils.getEodCSVData(symbol,fromDate,todayDate,TimeFrame.MONTHLY);
			s.setSymbol(symbol);
			s.setQuotes(data);
			s.setTf(TimeFrame.MONTHLY);
			DAO.storeStock(sqlHelper,s);	
		}
	}
	
	
	private void insertAdjSymbolListQuotes(List<String> symbolList) throws Exception{
		List<Quote> data = null;
		Stock s = new Stock();
		for (int i=0;i<symbolList.size();i++){
			System.out.println("Symbol to insert: "+symbolList.get(i));
			String symbol = symbolList.get(i);
			
			//DAILY			
			GregorianCalendar fromDate  = DateUtils.stringToDateFormat(DAO.getLastInsertDate(sqlHelper,"adj_dailyquotes",symbol));
			GregorianCalendar todayDate = DateUtils.getTodayDate();
			
			System.out.println("[DAILY]From Date and TodayDate : "+DateUtils.datePrint(fromDate)+","+DateUtils.datePrint(todayDate));
			fromDate.add(GregorianCalendar.DAY_OF_MONTH,1);
			data = StockUtils.getEodCSVData(symbol,fromDate,todayDate,true);	
			
			//System.out.println("[insertSymbolListQuotes]Datos obtenidos: "+data.size());
			//data = StockUtils.getEodCSVData(symbol,fromDate,todayDate,TimeFrame.DAILY);	
			s.setSymbol(symbol);
			s.setQuotes(data);
			s.setTf(TimeFrame.DAILY);
			DAO.storeAdjustedStock(sqlHelper,s);						
		}
	}
		
	private void insertSymbolListIntraday2(List<Volume> volumeStocks, List<String> symbolList) throws Exception{	
		System.out.println("[insertSymbolListIntraday2]entrado1");
		IntradayQuote s = new IntradayQuote();
		List<String>data = StockUtils.getIntradayData(symbolList);
		
		for (int i=0;i<data.size();i++){
			String [] values = data.get(i).split(",");
			s.setSymbol(values[0].replaceAll("\"",""));
			s.setPrice(Float.valueOf(values[1]));
			s.setVolume(Long.valueOf(values[4]));
			System.out.println("Mirando volumen de: "+s.getSymbol());
			Volume v = findVolume(volumeStocks,s.getSymbol());
			if (s.getVolume()-v.getValue()>0){						
				s.setContracts(s.getVolume()-v.getValue());
				v.setValue(s.getVolume());
				System.out.println("Se inserta con contracts: "+values[0].replaceAll("\"","")+","+v.getSymbol()+","+s.getContracts());
				DAO.storeIntradayQuote(sqlHelper,s);
			}
		}											
	}
		
	private Volume findVolume(List<Volume> volumeStocks,String symbol) {
		// TODO Auto-generated method stub
		for (int i=0;i<volumeStocks.size();i++){
			if (volumeStocks.get(i).getSymbol().equalsIgnoreCase(symbol)){
				return volumeStocks.get(i);				
			}
		}
		return null;
	}

	public void startProcess(){
		readProperties();
		
		System.out.println("symbolPath2: "+symbolPath);
		
		File dir = new File(symbolPath);		
		
		sqlHelper.init();
		
		File[] files = dir.listFiles();		
		for (int i=0;i<files.length;i++){						;
			System.out.println("Fichero encontrado: "+files[i].getAbsolutePath());
			List<String> symbolList =FileUtils.getFileSymbols(files[i].getAbsolutePath());
			try {
				insertSymbolListQuotes(symbolList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	public void startProcess(String symbol) throws Exception{
		
		List<String> symbolList = new ArrayList<String>();
		symbolList.add(symbol);
		
		insertSymbolListQuotes(symbolList);
	}
	
	public void startIntradayProcess(){
		readProperties();
		
		System.out.println("symbolPath: "+symbolPath);
		
		final File dir = new File(symbolPath);		
		
		sqlHelper.init();
		
		File[] files = dir.listFiles();
		final List<String> symbolList =FileUtils.getFileSymbols(files[0].getAbsolutePath());
		final List<Volume> volumeStocks = new ArrayList<Volume>();
		
		for (int i=0;i<symbolList.size();i++){
			Volume v = new Volume();
			v.setSymbol(symbolList.get(i));
			v.setValue(0);
			volumeStocks.add(v);
		}
		
		
		TimerTask timerTask = new TimerTask()
	     {
	         public void run() 
	         {	        	
	        	 try {	        		 							
						insertSymbolListIntraday2(volumeStocks,symbolList);
						firstInserted=true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
	             // Aquí el código que queremos ejecutar.
					/*	File[] files = dir.listFiles();		
					for (int i=0;i<files.length;i++){						;
						System.out.println("Fichero encontrado: "+files[i].getAbsolutePath());
						List<String> symbolList =FileUtils.getFileSymbols(files[i].getAbsolutePath());
						try {
							//insertSymbolListIntraday(symbolList);
							insertSymbolListIntraday2(symbolList);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}*/		
	         }
	     }; 
	     
	     // Aquí se pone en marcha el timer cada segundo.
	     Timer timer = new Timer();
	     // Dentro de 0 milisegundos avísame cada 1000 milisegundos
	     timer.scheduleAtFixedRate(timerTask, 0, 15000); 
		
	}
	
	
	public void startAdjustedProcess() {
		// TODO Auto-generated method stub
		readProperties();
		
		System.out.println("symbolPath: "+symbolPath);
		
		File dir = new File(symbolPath);		
		
		sqlHelper.init();
		
		File[] files = dir.listFiles();		
		for (int i=0;i<files.length;i++){						;
			System.out.println("Fichero encontrado: "+files[i].getAbsolutePath());
			List<String> symbolList =FileUtils.getFileSymbols(files[i].getAbsolutePath());
			try {
				insertAdjSymbolListQuotes(symbolList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GetInfo info = new GetInfo();
			
		//info.startProcess();
		
		info.startIntradayProcess();
		
		System.out.println("Actualizacion terminada");
		
	}

	

}
