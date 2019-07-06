package drosa.strategies;

public class PairSpread {

	
	public static double getSpread(String pair){
		
		double spread = 4;
		//return spread;
		
		if (pair.equalsIgnoreCase("eurusd"))
			spread=1;
		if (pair.equalsIgnoreCase("gbpusd"))
			spread=2;
		if (pair.equalsIgnoreCase("usdjpy"))
			spread=1.5;
		if (pair.equalsIgnoreCase("usdchf"))
			spread=2;
		if (pair.equalsIgnoreCase("audusd"))
			spread=2.9;
		if (pair.equalsIgnoreCase("eurgbp"))
			spread=2.5;
		if (pair.equalsIgnoreCase("usdcad"))
			spread=3.4;
		if (pair.equalsIgnoreCase("nzdusd"))
			spread=3.6;
		if (pair.equalsIgnoreCase("eurjpy"))
			spread= 2;
		if (pair.equalsIgnoreCase("eurchf"))
			spread=3.1;
		if (pair.equalsIgnoreCase("gbpjpy"))
			spread=3;
		if (pair.equalsIgnoreCase("gbpchf"))
			spread=5.7;
		if (pair.equalsIgnoreCase("chfjpy"))
			return 3.6;
		if (pair.equalsIgnoreCase("cadchf"))
			spread= 5.2;
		if (pair.equalsIgnoreCase("euraud"))
			spread= 4.7;
		
		spread=4;
		return spread;
	}
}
