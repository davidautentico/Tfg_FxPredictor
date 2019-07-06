package drosa.utils;

import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;



public class PropertiesUtils {

	public String  readProperties( String p)
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
					if (((String)obj).equalsIgnoreCase(p)){
						return prop.getProperty(obj.toString());
					}
				}
			}else{
				System.out.println("Properties no encontrado");
			}
		} catch(IOException ioe) {
			System.out.println("[GetInfo] Error: "+ioe.getMessage());
		}
		
		return null;
	}
	
	

}
