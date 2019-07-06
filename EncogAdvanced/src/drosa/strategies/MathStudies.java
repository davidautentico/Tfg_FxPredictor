package drosa.strategies;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.finances.Quote;
import drosa.utils.PrintUtils;

public class MathStudies {

	static double volatilityStudy(String market,double filter1,double filter2){
		SQLConnectionUtils sql = new SQLConnectionUtils();
		sql.init("visualchart");
	
		
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		fromDate.set(1900, 0, 1);
		toDate.set(2990, 11,31);	
		
		ArrayList<Quote> data =  DAO.retrieveQuotes2(sql,market+'_'+1440+'m',market,fromDate,toDate,true);
		//System.out.println("tamaño: "+data.size());
		boolean lastPos=false;
		double lastPer=0;
		
		long total=0;
		long ok=0;
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			Quote q_1=data.get(i-1);
			double diff = q.getClose()-q_1.getClose();
			double per=0;
			
			per=q.getClose()*100.0/q_1.getClose()-100.0;
			
			if (Math.abs(lastPer)>=filter1){
				total++;
				if (Math.abs(per)>=filter2){
					ok++;
				}
			}
			
			//System.out.println("last,per: "+PrintUtils.Print(lastPer)+" "+PrintUtils.Print(per));
			lastPer=per;
			//System.out.println("pp pn nn np: "+pp+" "+pn+" "+nn+" "+np);
		}
		//System.out.println("total volok: "+total+" "+PrintUtils.Print(ok*100.0/total));
		return ok*100.0/total;
	}
	
	static void simpleStudy(){
		SQLConnectionUtils sql = new SQLConnectionUtils();
		sql.init("visualchart");
	
		String s[][]={{"ndx"}}; //orange juice
		
		GregorianCalendar fromDate = new GregorianCalendar();
		GregorianCalendar toDate = new GregorianCalendar();
		fromDate.set(1900, 0, 1);
		toDate.set(2990, 11,31);	
		
		ArrayList<Quote> data =  DAO.retrieveQuotes2(sql,s[0][0]+'_'+1440+'m',s[0][0],fromDate,toDate,true);
		System.out.println("tamaño: "+data.size());
		boolean lastPos=false;
		double lastResult=0;
		long pp=0;
		long pn=0;
		long np=0;
		long nn=0;
		double filter=0.0;
		for (int i=1;i<data.size();i++){
			Quote q = data.get(i);
			Quote q_1=data.get(i-1);
			double diff = q.getClose()-q_1.getClose();
			double per=0.0;
			if (diff>=0){
				per=q.getClose()*100.0/q_1.getClose()-100.0;
				if (i>0 && Math.abs(per)>=filter){
					if (lastPos){
						pp++;
					}else{
						np++;
					}
				}
				lastPos=true;
			}else {
				per=q.getClose()*100.0/q_1.getClose()-100.0;
				if (i>0 && Math.abs(per)>filter){
					if (lastPos){
						pn++;
					}else{
						nn++;
					}
				}
				lastPos=false;
			}
			
			//System.out.println("last,per: "+PrintUtils.Print(lastResult)+" "+PrintUtils.Print(per));
			lastResult=per;
			//System.out.println("pp pn nn np: "+pp+" "+pn+" "+nn+" "+np);
		}
		double total=pp+nn;
		
		System.out.println("pp pn nn np: "+pp+" "+PrintUtils.Print(pp*100.0/(pp+pn))+"% "+pn+" "+
				+nn+" "+PrintUtils.Print(nn*100.0/(nn+np))+"% "+np);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//simpleStudy();
		double f2=1.5;
		String market="ibex";
		for (double f1=0;f1<=6;f1+=0.25){
			double per=volatilityStudy(market,f1,f2);
			System.out.println("f1 f2 "
					+PrintUtils.Print(f1)+" "+PrintUtils.Print(f2)+" "+PrintUtils.Print(per)+"%");
		}
		
	}

}
