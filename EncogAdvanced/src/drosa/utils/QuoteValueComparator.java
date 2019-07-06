package drosa.utils;

import java.util.Comparator;

import drosa.finances.QuoteValue;

public class QuoteValueComparator implements Comparator<QuoteValue> {
	public int compare(QuoteValue v1, QuoteValue v2){
		
			if (v1.getValue()>v2.getValue()) return 1;
			if (v1.getValue()<v2.getValue()) return -1;
			return 0;       			 
	}
	
}
