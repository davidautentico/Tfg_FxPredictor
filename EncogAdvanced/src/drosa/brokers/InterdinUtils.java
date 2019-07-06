package drosa.brokers;

public class InterdinUtils {

	public static double calculateCommissions(double amount){
		double comm =0.0;
		
		double corretaje=0.1*amount/100;
		double iberclear=0.0026*amount/100;
		if (iberclear<=0.1){
			iberclear=0.1;
		}else if (iberclear>=3.5){
			iberclear=3.5;
		}
		double commFija=0.0;
		double commVar=0.0;
		if (amount<=300){
			commFija = 1.10;
		}else if (amount<=3000){
			commFija=2.45;
			commVar=0.0240*amount/100;
		}else if (amount<=35000){
			commFija=4.65;
			commVar=0.0120*amount/100;
		}else if (amount<=70000){
			commFija=6.40;
			commVar=0.0070*amount/100;
		}else if (amount<=140000){
			commFija=9.20;
			commVar=0.0030*amount/100;
		}else if (amount> 140000){
			commFija=13.40;
		}
		
		comm = corretaje+commFija+commVar+iberclear;
		
		
		return comm;
	}
	
	/**
	 * Suponiendo que tenemos una posicion larga y otra corta y se cancelan al mismo tiempo
	 * la diferencia es un 3.5%/365*ndias a pagar
	 * @param amount
	 * @param ndays
	 * @return
	 */
	public static double calculateMargenFinancieroPairTrading(double amount,int ndays){

		double costeDia=(amount/100)*(3.5/365);
		double margen = ndays*costeDia;
		return margen;
	}
}
