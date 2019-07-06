package drosa.utils;

import java.io.*;

public class InsertDataFilter implements FilenameFilter {
	
	
	String pattern;
	
	public InsertDataFilter(String pattern) {
		
		this.pattern = pattern;
	}
	
	public boolean accept(File dir, String name) {
		return name.endsWith(pattern);
	}
}
