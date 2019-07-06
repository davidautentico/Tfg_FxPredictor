package drosa.phil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.Quote;
import drosa.finances.QuoteBidAsk;
import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class DataCleaning {

	
	public static void writeFile(String fileName,ArrayList<Quote> data){
		try{
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				cal.setTime(q.getDate());
				String dateStr = DateUtils.getDukasFormat(cal);
				String OHLC = PrintUtils.getOHLC(q);					
				writer.println(dateStr + " "+OHLC);				
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeFileBidAsk(String fileName,ArrayList<QuoteBidAsk> data){
		try{
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				QuoteBidAsk q = data.get(i);				
				writer.println(q.toString());				
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeFile(String symbol,ArrayList<Quote> data,int month,int year,String path){
		try{
			String fileName = path+"\\"+symbol+"_"+DateUtils.getAlways2digits(month+1)+"_"+year+"_1s_data.csv";
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				cal.setTime(q.getDate());
				if (cal.get(Calendar.MONTH)==month
						&& cal.get(Calendar.YEAR)==year){
					String dateStr = DateUtils.getDukasFormat(cal);
					String OHLC = PrintUtils.getOHLC(q);					
					writer.println(dateStr + " "+OHLC);
				}				
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeFile(String symbol,ArrayList<Quote> data,int month,int year,String path,String suffix){
		try{
			String fileName = path+"\\"+symbol+"_"+DateUtils.getAlways2digits(month+1)+"_"+year+suffix;
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				cal.setTime(q.getDate());
				if (cal.get(Calendar.MONTH)==month
						&& cal.get(Calendar.YEAR)==year){
					String dateStr = DateUtils.getDukasFormat(cal);
					String OHLC = PrintUtils.getOHLC(q);					
					writer.println(dateStr + " "+OHLC);
				}				
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeFileShort(String symbol,ArrayList<QuoteShort> data,int month,int year,String path,String suffix){
		try{
			String fileName = path+"\\"+symbol+"_"+DateUtils.getAlways2digits(month+1)+"_"+year+suffix;
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				QuoteShort q = data.get(i);
				QuoteShort.getCalendar(cal, q);
				//if (cal.get(Calendar.MONTH)==month
						//&& cal.get(Calendar.YEAR)==year){
					//String dateStr = DateUtils.getDukasFormat(cal);
					//String OHLC = PrintUtils.getOHLC(q);					
					writer.println(q.toString());
				//}				
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeFileShort(ArrayList<QuoteShort> data,String fileName){
		try{
			//String fileName = path+"\\"+symbol+"_"+DateUtils.getAlways2digits(month+1)+"_"+year+suffix;
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				QuoteShort q = data.get(i);
				QuoteShort.getCalendar(cal, q);
				//if (cal.get(Calendar.MONTH)==month
						//&& cal.get(Calendar.YEAR)==year){
					//String dateStr = DateUtils.getDukasFormat(cal);
					//String OHLC = PrintUtils.getOHLC(q);					
					writer.println(q.toString());
				//}				
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeFileTick(String symbol,ArrayList<Tick> data,int month,int year,String path){
		try{
			String fileName = path+"\\"+symbol+"_"+DateUtils.getAlways2digits(month+1)+"_"+year+"_tick_data.csv";
			PrintWriter writer;		
			writer = new PrintWriter(fileName, "UTF-8");
			
			Calendar cal = Calendar.getInstance();
			for (int i=0;i<data.size();i++){
				Tick t = data.get(i);
				Tick.getCalendar(cal, t);
				if (cal.get(Calendar.MONTH)==month
						&& cal.get(Calendar.YEAR)==year){					
					writer.println(t.toString());
				}				
			}
			writer.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void  convertToPepperFolder(String symbol,String path,String destPath){
		File folder = new File(path);
	    File[] listOfFiles = folder.listFiles();

	    for (File file : listOfFiles) {
	    	if (file.isFile() && file.getName().contains("_10 Secs") && file.getName().contains(symbol)){                            
	    		System.out.println(file.getAbsolutePath()+" "+file.getName());
	    		String fileName = file.getAbsolutePath();
	    		ArrayList<Quote> dataI 	= DAO.retrieveData(fileName, DataProvider.DUKASCOPY_FOREX);
		  		ArrayList<Quote> dataS 	= TestLines.calculateCalendarAdjusted(dataI);
		  		ArrayList<Quote> data 	= TradingUtils.cleanWeekendData(dataS);
		  		System.out.println("Initial data size y cleaned: "+dataI.size()+" "+data.size());
		  		//System.out.println("First quote "
		  		//		+" "+DateUtils.datePrint(dataI.get(0).getDate())
		  		//		+" "+DateUtils.datePrint(data.get(400000).getDate()));
		  		Calendar cal = Calendar.getInstance();
		  		cal.setTime(data.get(400000).getDate());
		  		int month = cal.get(Calendar.MONTH);
		  		int year = cal.get(Calendar.YEAR);
	  		
		  		DataCleaning.writeFile(symbol,data, month, year,destPath);	  		
	      }
	    }
	}
	
	public static void  convertToPepperFolder(String symbol,String path,String destPath,String suffix){
		File folder = new File(path);
	    File[] listOfFiles = folder.listFiles();

	    for (File file : listOfFiles) {
	    	if (file.isFile() && file.getName().contains("_10 Secs") && file.getName().contains(symbol)){                            
	    		System.out.println(file.getAbsolutePath()+" "+file.getName());
	    		String fileName = file.getAbsolutePath();
	    		ArrayList<Quote> dataI 	= DAO.retrieveData(fileName, DataProvider.DUKASCOPY_FOREX);
		  		ArrayList<Quote> dataS 	= TestLines.calculateCalendarAdjusted(dataI);
		  		ArrayList<Quote> data 	= TradingUtils.cleanWeekendData(dataS);
		  		System.out.println("Initial data size y cleaned: "+dataI.size()+" "+data.size());
		  		//System.out.println("First quote "
		  		//		+" "+DateUtils.datePrint(dataI.get(0).getDate())
		  		//		+" "+DateUtils.datePrint(data.get(400000).getDate()));
		  		Calendar cal = Calendar.getInstance();
		  		cal.setTime(data.get(400000).getDate());
		  		int month = cal.get(Calendar.MONTH);
		  		int year = cal.get(Calendar.YEAR);
	  		
		  		DataCleaning.writeFile(symbol,data, month, year,destPath,suffix);	  		
	      }
	    }
	}
	
	public static void  convertToPepperFolderShort(String symbol,String path,String destPath,String suffix){
		File folder = new File(path);
	    File[] listOfFiles = folder.listFiles();

	    for (File file : listOfFiles) {
	    	if (file.isFile() && file.getName().contains("_10 Secs") && file.getName().contains(symbol)){                            
	    		System.out.println(file.getAbsolutePath()+" "+file.getName());
	    		String fileName = file.getAbsolutePath();
	    		ArrayList<Quote> dataI 		= DAO.retrieveData(fileName, DataProvider.DUKASCOPY_FOREX);
				ArrayList<Quote> dataS 		= TestLines.calculateCalendarAdjusted(dataI);
		  		ArrayList<Quote> data5m 	= TradingUtils.cleanWeekendData(dataS); 		  		
				ArrayList<QuoteShort> data  = QuoteShort.convertQuoteArraytoQuoteShort(data5m);
		  		System.out.println("Initial data size y cleaned: "+dataI.size()+" "+data.size());
		  		//System.out.println("First quote "
		  		//		+" "+DateUtils.datePrint(dataI.get(0).getDate())
		  		//		+" "+DateUtils.datePrint(data.get(400000).getDate()));
		  		Calendar cal = Calendar.getInstance();
		  		QuoteShort.getCalendar(cal, data.get(400000));
		  		int month = cal.get(Calendar.MONTH);
		  		int year = cal.get(Calendar.YEAR);
	  		
		  		DataCleaning.writeFileShort(symbol,data, month, year,destPath,suffix);	  		
	      }
	    }
	}
	
	public static void appendToFile(String fileName,String text){
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		    out.println(text);
		    out.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	public static void calculateTMAbyMonths(String symbol,String fileM,String path,String sufix){
		ArrayList<Quote> dataI = DAO.retrieveData(fileM, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		
  		double bandFactor = 2.9;
		int atrPeriod = 100;
		int halfLength = 56;
		ArrayList<TMA> tmas = IndicatorLib.calculateTMA_Array(data, 0,data.size()-1,bandFactor,halfLength,atrPeriod);
		System.out.println("Initial data size y cleaned tmas: "+dataI.size()+" "+data.size()+" "+tmas.size());
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<tmas.size();i++){
			TMA tma = tmas.get(i);
			cal.setTime(tma.getDate().getTime());
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			String fileName = path+"\\"+symbol+"_"+DateUtils.getAlways2digits(month+1)+"_"+year+sufix+".csv";
			String dateStr = DateUtils.getDukasFormat(cal);
			String values = PrintUtils.Print(tma.getUpper())+" "+PrintUtils.Print(tma.getLower());
			appendToFile(fileName,dateStr+" "+values);
			
		}
	}
	
	public static void calculateLinesbyMonths(String symbol,String file5M,String path){
		ArrayList<Quote> dataI = DAO.retrieveData(file5M, DataProvider.DUKASCOPY_FOREX);
  		ArrayList<Quote> dataS =  TestLines.calculateCalendarAdjusted(dataI);
  		ArrayList<Quote> data = TradingUtils.cleanWeekendData(dataS);
  		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
		ArrayList<Quote> weeklyData = ConvertLib.createWeeklyData(data);
		ArrayList<Quote> monthlyData = ConvertLib.createMonthlyData(data);
  		ArrayList<PhilDay> philDays = TradingUtils.calculateLines(data, dailyData, weeklyData, monthlyData);
		System.out.println("Initial data size y cleaned philDays: "+dataI.size()+" "+data.size()+" "+philDays.size());
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<philDays.size();i++){
			PhilDay pDay = philDays.get(i);
			cal.setTime(pDay.getDay().getTime());
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			String fileName = path+"\\"+symbol+"_"+DateUtils.getAlways2digits(month+1)+"_"+year+"_LINES.csv";
			appendToFile(fileName,pDay.toString());		
		}
	}
	
	private static String get2digits(int number){
		if (number<9)
			return "0"+number;
		return String.valueOf(number);
	}
	
	public static void createFilesDay(String symbol,String path,int year,int monthSearch, 
			String toSearch,int min,int type){
		String monthStr = get2digits(monthSearch);
		File folder = new File(path);
	    File[] listOfFiles = folder.listFiles();
	    
	    Calendar cal = Calendar.getInstance();
	    for (File file : listOfFiles) {
	    	if (file.isFile() && file.getName().contains(toSearch) && file.getName().contains(symbol)
	    			&& file.getName().contains("_"+String.valueOf(monthStr)+"_")
	    			&& file.getName().contains(String.valueOf(year))){   
	    		String fileName = file.getAbsolutePath();
	    		System.out.println("fileName base: "+fileName);
	    		if (type==0){//data
		    		ArrayList<Quote > dataSource = DAO.retrieveData(file.getAbsolutePath(),DataProvider.DUKASCOPY_FOREX,0);//1s
		    		System.out.println("size: "+dataSource.size());
		    				    	
		    		ArrayList<QuoteArray> quoteArrays = new ArrayList<QuoteArray>();
		    		for (int i=0;i<dataSource.size();i++){
		    			Quote q = dataSource.get(i);
		    			cal.setTime(q.getDate());
		    			int month = cal.get(Calendar.MONTH);
		    			
		    			String nameDay  = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
		    			String monthExt = "data_1_6";
		    			if (month>=Calendar.JULY) monthExt = "data_7_12";
		    			String fileToSave = path+"\\"+symbol+"_"+String.valueOf(year)+"_"+nameDay+"s_"+monthExt+".csv"; 
		    			boolean inserted = insertIntoQuoteArray(quoteArrays,fileToSave,q);		    			
		    		}
		    		saveQuoteArrays(quoteArrays);
	    		}else if (type==1){//tma
	    			ArrayList<TMA >tmas       = TMA.loadFromFile(fileName);//tmas 
	    			System.out.println("size: "+tmas.size());
	    			ArrayList<TmaArray> tmaArrays = new ArrayList<TmaArray>();
	    			for (int i=0;i<tmas.size();i++){
		    			TMA tma = tmas.get(i);
		    			cal.setTime(tma.getDate().getTime());
		    			int month = cal.get(Calendar.MONTH);
		    			String nameDay  = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
		    			String monthExt = min+"m_TMA_1_6";
		    			if (month>=Calendar.JULY) monthExt = min+"m_TMA_7_12";
		    			String fileToSave = path+"\\"+symbol+"_"+String.valueOf(year)+"_"+nameDay+"s_"+monthExt+".csv"; 
		    			//System.out.println("fileToSave: "+fileToSave);
		    			boolean inserted = insertIntoTMAArray(tmaArrays,fileToSave,tma);		    			
		    		}
	    			saveTmaArrays(tmaArrays);
	    		}else if (type==2){//lines
	    			ArrayList<PhilDay> philDays   = PhilDay.loadFromFile(fileName);//phildays
	    			System.out.println("size: "+philDays.size());
	    			ArrayList<LineArray> lineArrays = new ArrayList<LineArray>();
	    			for (int i=0;i<philDays.size();i++){
		    			PhilDay pDay = philDays.get(i);
		    			cal.setTime(pDay.getDay().getTime());
		    			int month = cal.get(Calendar.MONTH);
		    			String nameDay  = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
		    			String monthExt = "LINES_1_6";
		    			if (month>=Calendar.JULY) monthExt = "LINES_7_12";
		    			String fileToSave = path+"\\"+symbol+"_"+String.valueOf(year)+"_"+nameDay+"s_"+monthExt+".csv"; 
		    			boolean inserted = insertIntoLINEArray(lineArrays,fileToSave,pDay);
		    		}
	    			saveLineArrays(lineArrays);
	    		}
	    	}	
	    }//for
	 }
	    
	    
	
	private static void saveQuoteArrays(ArrayList<QuoteArray> quoteArrays) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<quoteArrays.size();i++){
			QuoteArray quoteArray = quoteArrays.get(i);
			ArrayList<Quote> quotes = quoteArray.getQuotes();
			String fileName  = quoteArray.getId();
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			    for (int j=0;j<quotes.size();j++){
			    	Quote q = quotes.get(j);
			    	cal.setTime(q.getDate());
			    	String dateStr = DateUtils.getDukasFormat(cal);
	    			String OHLC = PrintUtils.getOHLC(q);
			    	out.println(dateStr +" "+OHLC);
			    }			   			    			    
			    out.close();			    
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
	}
	
	
	
	private static void saveLineArrays(ArrayList<LineArray> lineArrays) {

		for (int i=0;i<lineArrays.size();i++){
			LineArray lineArray = lineArrays.get(i);
			ArrayList<PhilDay> pDays = lineArray.getpDays();
			String fileName  = lineArray.getId();
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			    for (int j=0;j<pDays.size();j++){
			    	PhilDay pDay = pDays.get(j);
			    	out.println(pDay.toString());
			    }			   			    			    
			    out.close();			    
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
	}
	
	private static void saveTmaArrays(ArrayList<TmaArray> tmaArrays) {

		for (int i=0;i<tmaArrays.size();i++){
			TmaArray tmaArray 	= tmaArrays.get(i);
			ArrayList<TMA> tmas = tmaArray.getTmas();
			String fileName  	= tmaArray.getId();
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			    for (int j=0;j<tmas.size();j++){
			    	TMA tma = tmas.get(j);
			    	out.println(tma.toString());
			    }			   			    			    
			    out.close();			    
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
	}

	private static boolean insertIntoQuoteArray(ArrayList<QuoteArray> quoteArrays,
			String fileToSave, Quote q) {
		// TODO Auto-generated method stub
		for (int i=0;i<quoteArrays.size();i++){
			QuoteArray quoteArray = quoteArrays.get(i);
			if (quoteArray.getId().equalsIgnoreCase(fileToSave)){
				
				quoteArray.addQuote(q);
				return true;
			}
		}	
		
		File newF = new File(fileToSave);
		if (newF.exists())
			newF.delete();
		QuoteArray quoteArray = new QuoteArray();
		quoteArray.setId(fileToSave);
		quoteArray.addQuote(q);
		quoteArrays.add(quoteArray);
		
		return false;
	}
	
	private static boolean insertIntoTMAArray(ArrayList<TmaArray> tmaArrays,
			String fileToSave, TMA tma) {
		// TODO Auto-generated method stub
		for (int i=0;i<tmaArrays.size();i++){
			TmaArray tmaArray = tmaArrays.get(i);
			if (tmaArray.getId().equalsIgnoreCase(fileToSave)){
				tmaArray.addTMA(tma);
				return true;
			}
		}	
		
		TmaArray tmaArray = new TmaArray();
		tmaArray.setId(fileToSave);
		tmaArray.addTMA(tma);
		tmaArrays.add(tmaArray);
		
		return false;
	}
	
	private static boolean insertIntoLINEArray(ArrayList<LineArray> lineArrays,
			String fileToSave, PhilDay pDay) {
		// TODO Auto-generated method stub
		for (int i=0;i<lineArrays.size();i++){
			LineArray lineArray = lineArrays.get(i);
			if (lineArray.getId().equalsIgnoreCase(fileToSave)){
				lineArray.addPhilDay(pDay);
				return true;
			}
		}	
		
		LineArray lineArray = new LineArray();
		lineArray.setId(fileToSave);
		lineArray.addPhilDay(pDay);
		lineArrays.add(lineArray);
		
		return false;
	}
	
	public static String intToStr(int num){
		if (num==0) return "00";
		if (num==1) return "01";
		if (num==2) return "02";
		if (num==3) return "03";
		if (num==4) return "04";
		if (num==5) return "05";
		if (num==6) return "06";
		if (num==7) return "07";
		if (num==8) return "08";
		if (num==9) return "09";
		if (num==10) return "10";
		if (num==11) return "11";
		if (num==12) return "12";
		
		return "";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path		=	"C:\\fxdata\\raw";
		String sufix5m	=	"_5m_TMA";//para 5
		String sufix15m	=	"_15m_TMA";//para 1
		String symbol 	=	"EURUSD";

		/**
		 * Módulo para crear ficheros por días
		 */
		/*for (int year = 2007;year<=2013;year++){
			//for (int dayWeek = Calendar.MONDAY;dayWeek<=Calendar.FRIDAY;dayWeek++)
			String toSearchData = "_data.csv";
			String minStr5  ="_5m_TMA.csv";
			String minStr15 ="_15m_TMA.csv";
			String lines="LINES.csv";
			for (int month=1;month<=12;month++){				
				//createFilesDay("EURUSD","c:\\fxdata",year,month,toSearchData,0,0);
				//createFilesDay("EURUSD","c:\\fxdata",year,month,minStr5,5,1);
				createFilesDay("EURUSD","c:\\fxdata",year,month,minStr15,15,1);
				//createFilesDay("EURUSD","c:\\fxdata",year,month,lines,0,2);
			}
		}*/
		
		
		if (args.length>0){
			symbol= args[0];
			if (args.length>=2)
				path= args[1];
		}
		System.out.println("symbol and path: "+symbol+" "+path);
		
		String file5m	=	symbol+"_UTC_5 Mins_Bid_2003.05.04_2014.07.23.csv";
		String file15m	=	symbol+"_UTC_15 Mins_Bid_2003.05.04_2014.05.01.csv";
		
		//calculateTMAbyMonths(symbol,path+"\\"+file5m,path,sufix5m);
		//calculateTMAbyMonths(symbol,path+"\\"+file15m,path,sufix15m);
		calculateLinesbyMonths(symbol,path+"\\"+file5m,path);
				
		/*convertToPepperFolder(symbol,path,path);
		
		path+="\\";
		for (int year=2007;year<=2013;year++){
			TrimDataFiles.trimFile(path+symbol+"_01_"+year+"_1s_data.csv",path+symbol+"_01_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_02_"+year+"_1s_data.csv",path+symbol+"_02_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_03_"+year+"_1s_data.csv",path+symbol+"_03_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_04_"+year+"_1s_data.csv",path+symbol+"_04_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_05_"+year+"_1s_data.csv",path+symbol+"_05_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_06_"+year+"_1s_data.csv",path+symbol+"_06_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_07_"+year+"_1s_data.csv",path+symbol+"_07_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_08_"+year+"_1s_data.csv",path+symbol+"_08_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_09_"+year+"_1s_data.csv",path+symbol+"_09_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_10_"+year+"_1s_data.csv",path+symbol+"_10_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_11_"+year+"_1s_data.csv",path+symbol+"_11_"+year+"_1s_data_trim.csv");
			TrimDataFiles.trimFile(path+symbol+"_12_"+year+"_1s_data.csv",path+symbol+"_12_"+year+"_1s_data_trim.csv");
		}*/
		
	
	}

	public static boolean testGaps(ArrayList<QuoteBidAsk> data,int min) {
		// TODO Auto-generated method stub
		Calendar calIter = Calendar.getInstance();
		calIter.setTimeInMillis(data.get(0).getCal().getTimeInMillis());
		Calendar calActual = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			calActual.setTimeInMillis(data.get(i).getCal().getTimeInMillis());
			int da = calActual.get(Calendar.DAY_OF_YEAR);
			int di = calIter.get(Calendar.DAY_OF_YEAR);
			int ha = calActual.get(Calendar.HOUR_OF_DAY);
			int ma = calActual.get(Calendar.MINUTE);
			int hi = calIter.get(Calendar.HOUR_OF_DAY);
			int mi = calIter.get(Calendar.MINUTE);
			if (da!=di  || ha!=hi || ma!=mi){
				System.out.println("Supuesto y obtenido: "+DateUtils.datePrint(calIter)+" "+DateUtils.datePrint(calActual));
				return true;
			}
			calIter.add(Calendar.MINUTE,min);
		}
		return false;
	}

	public static ArrayList<QuoteBidAsk> fillGaps(
			ArrayList<QuoteBidAsk> data, int min) {
		
		
		ArrayList<QuoteBidAsk> newData = new ArrayList<QuoteBidAsk>();
		// TODO Auto-generated method stub
		Calendar calIter = Calendar.getInstance();
		calIter.setTimeInMillis(data.get(0).getCal().getTimeInMillis());
		Calendar calTo = Calendar.getInstance();
		calTo.setTimeInMillis(data.get(data.size()-1).getCal().getTimeInMillis());
		
		Calendar calActual = Calendar.getInstance();
		int i = 0;
		while (calIter.getTimeInMillis()<calTo.getTimeInMillis()){
			calActual.setTimeInMillis(data.get(i).getCal().getTimeInMillis());
			int da = calActual.get(Calendar.DAY_OF_YEAR);
			int di = calIter.get(Calendar.DAY_OF_YEAR);
			int ha = calActual.get(Calendar.HOUR_OF_DAY);
			int ma = calActual.get(Calendar.MINUTE);
			int hi = calIter.get(Calendar.HOUR_OF_DAY);
			int mi = calIter.get(Calendar.MINUTE);
			//System.out.println("[FILLGAPS] actualQuote i "+i+" "+data.get(i).toString()+" "+DateUtils.datePrint(calIter));
			if (da!=di || ha!=hi || ma!=mi){
				//if (da>di) break;
				//System.out.println("[FILLGAPS] Supuesto y obtenido: "+DateUtils.datePrint(calIter)+" "+DateUtils.datePrint(calActual));
				//se copia el anterior con la nueva fecha
				QuoteBidAsk qNew = new QuoteBidAsk();
				qNew.copy(data.get(i-1));
				qNew.getCal().setTimeInMillis(calIter.getTimeInMillis());
				//newData.add(qNew);
				data.add(i,qNew); //metemos el nuevo
				//System.out.println("[FILLGAPS] Filled añadido: "+qNew.toString()+" "+DateUtils.datePrint(calIter));
			}
			else{
				//QuoteBidAsk qNew = new QuoteBidAsk();
				//qNew.copy(data.get(i));
				//newData.add(qNew);
				//System.out.println("[FILLGAPS] seguimos: "+data.get(i).toString());
			}
			i++;
			//System.out.println("size: "+data.size());
			calIter.add(Calendar.MINUTE,min);
		}
		return newData;
	}

}
