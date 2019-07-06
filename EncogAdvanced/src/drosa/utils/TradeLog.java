package drosa.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import drosa.finances.Quote;

public class TradeLog {
	
	
	
	
	public static void writeToLog(String fileName, String msg){
		
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName,true);
			  BufferedWriter out = new BufferedWriter(fstream);

			  out.write(msg);
			  out.newLine();
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}

}
