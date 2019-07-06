package drosa.studies;

import java.util.GregorianCalendar;
import java.util.List;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.Quote;
import drosa.utils.PrintUtils;
import drosa.utils.TimeFrame;



public class CointegrationStudy {

	
	String bbdd;
	String table;
	String symbol1;
	String symbol2;
	GregorianCalendar fromDate;
	GregorianCalendar toDate;
	
	
	public CointegrationStudy(String bbdd, String table, String symbol1,
			String symbol2, GregorianCalendar fromDate, GregorianCalendar toDate) {
		// TODO Auto-generated constructor stub
		this.bbdd = bbdd;
		this.table = table;
		this.symbol1 = symbol1;
		this.symbol2 = symbol2;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public void run(){
		SQLConnectionUtils sqlHelper1 = new SQLConnectionUtils();
		SQLConnectionUtils sqlHelper2 = new SQLConnectionUtils();
		sqlHelper1.init(bbdd);
		sqlHelper2.init(bbdd);
		List<Quote> quotes1 = DAO.retrieveQuotes(sqlHelper1,table, symbol1, fromDate,toDate);
		List<Quote> quotes2 = DAO.retrieveQuotes(sqlHelper2,table, symbol2, fromDate, toDate);
		
		System.out.println("tamaño 1: "+quotes1.size());
		System.out.println("tamaño 2: "+quotes2.size());
		
		double sum = 0.0;
		for (int i=0;i<quotes1.size();i++){
			Quote q1 = quotes1.get(i);
			Quote q2 = quotes2.get(i);
			double adjClose1 = q1.getAdjClose();
			double adjClose2 = q2.getAdjClose();
			double dif = adjClose1-adjClose2;
			sum+=dif;
			System.out.println("san,bbva: " +PrintUtils.Print(adjClose1)
								+" / "+PrintUtils.Print(adjClose2)+"  "+PrintUtils.Print(adjClose1-adjClose2));
		}
		System.out.println("average: "+(PrintUtils.Print(sum/quotes1.size())));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Iniciando..");
		
		GregorianCalendar fromDate = (GregorianCalendar) GregorianCalendar.getInstance();
		GregorianCalendar toDate = (GregorianCalendar) GregorianCalendar.getInstance();
		fromDate.set(2010, 0,1);
		toDate.set(2012,11,31);
		
		CointegrationStudy con= new CointegrationStudy("finances","dailyquotes","SAN.MC","BBVA.MC",fromDate,toDate);
		con.run();
		
		System.out.println("Cointegration study finished");		
	}
}
