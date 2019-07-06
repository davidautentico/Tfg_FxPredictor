package drosa.data;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.Quote;
import drosa.utils.PrintUtils;

public class BBDDtoCSV {

	
	public static void toCSV(CSVData data,String fileName){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);
			  
			  ArrayList<String> names = data.getNames();
			  ArrayList<ArrayList<Double>> values = data.getValues();
			  String header = names.get(0);
			  
			  for (int i=1;i<names.size();i++){
				  String name = names.get(i);
				  header+=','+name;
			  }
			  System.out.println("header: "+header);
			  
			  out.write(header);
			  out.newLine();
			  for (int i=0;i<values.size();i++){
				  ArrayList<Double> rowValues = values.get(i);
				  String row = String.valueOf(PrintUtils.Print(rowValues.get(0)));
				  for (int j=1;j<rowValues.size();j++){
					  String rowValue = String.valueOf(PrintUtils.Print(rowValues.get(j)));
					  row+=','+rowValue;
				  }
				  System.out.println("row: "+row);
				  out.write(row);
				  out.newLine();
			  }
			  
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SQLConnectionUtils sql1 = new SQLConnectionUtils();
		sql1.init("forexdata_forex2");
		
		String s[]={"eurusd"};
		
		int minutesBase=1440;
		int y1=2007;
		int y2=2008;
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		fromDate.set(y1, 0, 1);
		toDate.set(y2, 11,31);

		ArrayList<Quote> data= DAO.retrieveQuotes2(sql1,s[0]+'_'+minutesBase+'m',s[0],fromDate,toDate,true);
		/*System.out.println("Total data: "+data.size());
		String dir = "C:\\Users\\david\\Documents\\trading\\datasets\\";
		
		String fileName="eurusd_"+minutesBase+"_"+y1+"_"+y2+".csv";
		System.out.println("filefullPath: "+dir+fileName);
		CSVData csvData = DataTransformation.rawData(data);
		System.out.println("csvdata: "+csvData.getNames().size()+" "+csvData.values.size());
		BBDDtoCSV.toCSV(csvData,dir+fileName);
		*/
		
		ArrayList<String> rows = new ArrayList<String>();
		for (int i=3;i<data.size()-1;i++){
			Quote q3= data.get(i-3);
			Quote q2= data.get(i-2);
			Quote q1= data.get(i-1);
			Quote q0= data.get(i-0);
			String row=String.valueOf(q3.getClose())+','+String.valueOf(q2.getClose())
					+','+String.valueOf(q1.getClose())+','+String.valueOf(q0.getClose());
			rows.add(row);
		}
		saveToCSV("c:\\inputs.csv",rows);
		rows.clear();
		
		String row = String.valueOf((data.get(4).getClose()-data.get(3).getClose())>0?1:0);
		for (int i=5;i<data.size();i++){
			Quote q0= data.get(i-0);
			Quote q1 = data.get(i-1);
			row+=','+String.valueOf((q1.getClose()-q0.getClose())>0?1:0);
			
		}
		rows.add(row);
		saveToCSV("c:\\outputs2.csv",rows);
	}



	private static void saveToCSV(String fileName, ArrayList<String> rows) {
		// TODO Auto-generated method stub
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);
			  
			  for (int i=0;i<rows.size();i++){
				  String row = rows.get(i);
				  System.out.println("row: "+row);
				  out.write(row);
				  out.newLine();
			  }
			  
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	

}
