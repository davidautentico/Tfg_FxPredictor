package drosa.trabajo.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CSVReader {
	
	
	public static String[] splitStringChArray(String str, StringBuilder sb) {
	    char[] strArray = str.toCharArray();
	    /*int count = 0;
	    for (char c : strArray) {
	        if (c == ';') {
	            count++;
	        }
	    }*/
	    int count = 6;
	    String[] splitArray = new String[count+1];
	    int i=0;
	    for (char c : strArray) {
	        if (c == ';') {
	            splitArray[i] = sb.toString();
	            sb.delete(0, sb.length());
	        } else {
	            sb.append(c);
	        }
	    }
	    return splitArray;
	}

	public static void main(String[] args) throws NumberFormatException, ParseException {

        String csvFile = "C:\\s2a2_cursoAv\\Docs\\rendimiento_diariox.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        //System.out.println(dateFormat.format(cal));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        
        
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        
        /*DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("0,#");
        format.setDecimalFormatSymbols(symbols);*/
        
        ArrayList<TApunte> apuntes = new ArrayList<TApunte>();
        try {
        	System.out.println("[started] "+dtf.format(LocalDateTime.now()));//dateFormat.format(cal));
        	int total = 0;
            br = new BufferedReader(new FileReader(csvFile));
            StringBuilder sb=new StringBuilder();
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //String[] apunte = line.split(";");
            	String[] apunte = splitStringChArray(line,sb);
            	apuntes.add(new TApunte(0,apunte[1],apunte[2],apunte[3],0,0));//9s
            	System.out.println(line+" || "+apuntes.get(apuntes.size()-1).toString());
            	
            	/*//apuntes.add(new TApunte(Long.valueOf(apunte[0]),apunte[1],apunte[2],apunte[3],format.parse(apunte[4]).floatValue(),format.parse(apunte[5]).floatValue()));
                //apuntes.add(new TApunte(Long.valueOf(apunte[0]),apunte[1],apunte[2],apunte[3],0,0));//17s                
                //apuntes.add(new TApunte(0,apunte[1],apunte[2],apunte[3],0,0));//17s
                //apuntes.add(new TApunte(0,"","","",0,0));//4s
                //apuntes.add(new TApunte(0,apunte[1],"","",0,0));//7s
                //apuntes.add(new TApunte(0,"",apunte[2],"",0,0));//7s
                apuntes.add(new TApunte(0,apunte[1],apunte[2],"",0,0));//12s*/
                total++;
            }
            System.out.println("[finished] "+dtf.format(LocalDateTime.now())+" || "+total+" "+apuntes.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

