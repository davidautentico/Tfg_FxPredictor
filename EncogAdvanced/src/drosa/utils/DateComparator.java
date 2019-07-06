package drosa.utils;

import java.util.Comparator;
import java.util.Date;

import drosa.finances.QuoteValue;

public class DateComparator implements Comparator<QuoteValue> {
	public int compare(QuoteValue v1, QuoteValue v2){		
	        return v1.getDate().compareTo(v2.getDate());				 
	}
	
}
