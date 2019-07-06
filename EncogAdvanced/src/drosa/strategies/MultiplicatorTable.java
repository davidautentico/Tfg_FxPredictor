package drosa.strategies;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;

public class MultiplicatorTable {
	
	public static int getMult(String symbol) {
		// TODO Auto-generated method stub
		if (symbol.contains("JPY") || symbol.contains("jpy"))
			return 100;
		if (symbol.equalsIgnoreCase("ECZ"))
			return 10000;
		if (symbol.equalsIgnoreCase("CZ"))
			return 100;
		if (symbol.equalsIgnoreCase("CCZ"))
			return 1;
		if (symbol.equalsIgnoreCase("BOZ"))
			return 100;
		if (symbol.equalsIgnoreCase("USDJPY"))
				return 100;
		if (symbol.equalsIgnoreCase("AUDJPY"))
			return 100;
		if (symbol.equalsIgnoreCase("GBPJPY"))
				return 100;
		if (symbol.equalsIgnoreCase("EURJPY"))
			return 100;
		if (symbol.equalsIgnoreCase("compx"))
			return 100;
		if (symbol.equalsIgnoreCase("dxy"))
			return 100;
		if (symbol.equalsIgnoreCase("idx"))
			return 100;
		if (symbol.equalsIgnoreCase("inx"))
			return 100;
		if (symbol.equalsIgnoreCase("indu"))
			return 100;
		if (symbol.equalsIgnoreCase("ndx"))
			return 100;
		if (symbol.equalsIgnoreCase("oex"))
			return 100;
		if (symbol.equalsIgnoreCase("rua"))
			return 100;
		if (symbol.equalsIgnoreCase("rui"))
			return 100;
		if (symbol.equalsIgnoreCase("rut"))
			return 100;
		if (symbol.equalsIgnoreCase("sox"))
			return 100;
		if (symbol.equalsIgnoreCase("tsyy"))
			return 100;
		if (symbol.equalsIgnoreCase("vix"))
			return 100;
		if (symbol.equalsIgnoreCase("bo"))
			return 100;
		if (symbol.equalsIgnoreCase("c"))
			return 10;
		if (symbol.equalsIgnoreCase("cc"))
			return 1;
		if (symbol.equalsIgnoreCase("cl"))
			return 100;
		if (symbol.equalsIgnoreCase("ct"))
			return 100;
		if (symbol.equalsIgnoreCase("dj"))
			return 1;
		if (symbol.equalsIgnoreCase("dx"))
			return 1000;
		if (symbol.equalsIgnoreCase("es"))
			return 100;
		if (symbol.equalsIgnoreCase("fc"))
			return 100;
		if (symbol.equalsIgnoreCase("ff"))
			return 100;
		if (symbol.equalsIgnoreCase("gc"))
			return 10;
		if (symbol.equalsIgnoreCase("kc"))
			return 100;
		 if (symbol.equalsIgnoreCase("^dax"))
				return 100;
		 if (symbol.equalsIgnoreCase("^dji"))
				return 1;
		 if (symbol.equalsIgnoreCase("^djt"))
				return 100;
		 if (symbol.equalsIgnoreCase("^dju"))
				return 100;
		 if (symbol.equalsIgnoreCase("^ftse"))
				return 100;
		 if (symbol.equalsIgnoreCase("^ibex"))
				return 1;
		 
		return 10000;
	}




	public static SymbolInfo getSymbolInfo(SQLConnectionUtils sql,String tableName,String symbol) {
		// TODO Auto-generated method stub
		
		String bbdd1=tableName;
		SymbolInfo sInfo= DAO.getSymbolInfo2(sql, bbdd1, symbol);
		if (sInfo.getSymbol().equalsIgnoreCase(symbol)){
			return sInfo;
		}
		return null;
	}

}
