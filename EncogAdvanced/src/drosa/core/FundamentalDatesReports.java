package drosa.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import drosa.DAO.DAO;
import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.auxiliar.MonthFrecuency;
import drosa.auxiliar.MonthFrecuency2;
import drosa.auxiliar.MonthPeriod;
import drosa.auxiliar.MonthPeriod2;
import drosa.auxiliar.TimeFrecuency;
import drosa.auxiliar.TimePeriod;
import drosa.auxiliar.WeekPeriod;
import drosa.finances.FundamentalTrendPoint;
import drosa.finances.MonthPoint;
import drosa.finances.Quote;
import drosa.finances.Trend;
import drosa.finances.TrendFilter;
import drosa.utils.DateUtils;
import drosa.utils.FundamentalType;
import drosa.utils.PrintUtils;
import drosa.utils.TimeFrame;
import drosa.utils.TrendType;

public class FundamentalDatesReports {

	private SQLConnectionUtils sql = null;
	
	List<Trend> trends = null;
	List<FundamentalTrendPoint> points = new ArrayList<FundamentalTrendPoint>();
	TrendFilter t1;
	TrendFilter t2;
	String symbol;
	
	public FundamentalDatesReports (){
				
		sql = new SQLConnectionUtils();
		sql.init();
		
		if (sql==null){
			System.out.println("Creator sql is null");
			System.exit(0);
		}
	}
	
	
	
	public List<Trend> getTrends() {
		return trends;
	}



	public void setTrends(List<Trend> trends) {
		this.trends = trends;
	}



	public List<FundamentalTrendPoint> getPoints() {
		return points;
	}
	public void setPoints(List<FundamentalTrendPoint> points) {
		this.points = points;
	}
	public TrendFilter getT1() {
		return t1;
	}
	public void setT1(TrendFilter t1) {
		this.t1 = t1;
	}
	public TrendFilter getT2() {
		return t2;
	}
	public void setT2(TrendFilter t2) {
		this.t2 = t2;
	}
					
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	private void addFundamentalPoint(FundamentalTrendPoint point){
		//System.out.println("Posible punto: "+DateUtils.datePrint(point.getDate())+","+point.getValue());
		for (int i=0;i<points.size();i++){
			FundamentalTrendPoint p = points.get(i);			
			if (point.getDate().before(p.getDate())){
				points.add(i, point);
				//System.out.println("Punto "+DateUtils.datePrint(point.getDate())+","+point.getValue());
				return;
			}else if (point.getDate().compareTo(p.getDate())==0){//si son iguales no se insertan
				return;
			}
		}	
		System.out.println("Punto "+DateUtils.datePrint(point.getDate())+","+point.getValue());
		//para el caso que aún no se haya insertado, se inserta al final
		points.add(point);
	}
	/**
	 * Para guardar a fichero el informe
	 * @param fileName
	 */
	private void saveToFile(String fileName){
		
	}
	
	private void calculatePoints(){
		points.clear();
		
		//calclulo de los puntos
		List<Trend> trends = DAO.getTrend(sql, symbol, t1,t2);
		
		System.out.println("Total trends: "+trends.size());
		for (int i=0;i<trends.size();i++){
			FundamentalTrendPoint point = new FundamentalTrendPoint();
			Trend t = trends.get(i);
			//System.out.println("Cogiendo tendencia i: "+i);
			if (t1.getType()==TrendType.DOWN 
				&& t2.getType()==TrendType.UP ){//t1 es bajista, t2 es alcista
				point.setDate(t.getPeriod().getDateLow());
				point.setType(FundamentalType.LOW);
				point.setValue(t.getPeriod().getLow());
				
				addFundamentalPoint(point);
			}else if (t1.getType()==TrendType.UP 
				&& t2.getType()==TrendType.DOWN ){//t1 es alcista, t2 es bajista
				point.setDate(t.getPeriod().getDateHigh());
				point.setType(FundamentalType.HIGH);
				point.setValue(t.getPeriod().getHigh());
				
				addFundamentalPoint(point);
			}
		}
		System.out.println("Total fundamental points: "+points.size());
		//
	}
	
	private void calculatePoints2(){
		points.clear();
		
		System.out.println("Total trends: "+trends.size());
		for (int i=0;i<trends.size();i++){
			FundamentalTrendPoint point = new FundamentalTrendPoint();
			Trend t = trends.get(i);
			//System.out.println("Cogiendo tendencia i: "+i);
			if (t1.getType()==TrendType.DOWN 
				&& t2.getType()==TrendType.UP ){//t1 es bajista, t2 es alcista
				point.setDate(t.getPeriod().getDateLow());
				point.setType(FundamentalType.LOW);
				point.setValue(t.getPeriod().getLow());
				point.setLowInterval(t.getPeriod().getTradingDaysBetween());
				point.setHighInterval(t.getPeriod().getTradingDaysBetween());
				//addFundamentalPoint(point);
				//System.out.println("low interval: "+t.getPeriod().getTradingDaysBetween());
				
				points.add(point);
			}else if (t1.getType()==TrendType.UP 
				&& t2.getType()==TrendType.DOWN ){//t1 es alcista, t2 es bajista
				point.setDate(t.getPeriod().getDateHigh());
				point.setType(FundamentalType.HIGH);
				point.setValue(t.getPeriod().getHigh());
				point.setLowInterval(t.getPeriod().getTradingDaysBetween());
				point.setHighInterval(t.getPeriod().getTradingDaysBetween());
				
				System.out.println("low interval: "+t.getPeriod().getTradingDaysBetween());
				//addFundamentalPoint(point);
				points.add(point);
			}
		}
		System.out.println("Total fundamental points: "+points.size());
		//
	}
	
	public void generateReport(String fileName,boolean saveToBBDD){
		
		//primero calculamos los puntos
		calculatePoints();
		
		//agrupamos fechas
		TimePeriod tp = null;		
		tp = new MonthPeriod();
		List<TimeFrecuency> dfList = tp.getTime();		
		//TimeFrecuency timef = null;
		System.out.println("total points added: "+points.size());
		for (int i=0;i<points.size();i++){
			FundamentalTrendPoint p = points.get(i);
			Date d = p.getDate();
			TimeFrecuency timef = null;
			timef = (MonthFrecuency) dfList.get(d.getMonth());
			timef.incFrecuency();

		}
		
		if (saveToBBDD){
			List<TimeFrecuency> dfl = tp.getTime();
			
			for (int i=0;i<dfl.size();i++){
				MonthFrecuency df = (MonthFrecuency) dfl.get(i);
				
				MonthPoint mp = new MonthPoint();
				mp.setMonth(df.getMonth().ordinal()+1);
				mp.setSymbol(symbol);
				mp.setT1(t1);
				mp.setTotal(df.getFrecuency());
				
				if (df.getFrecuency()>0)
					DAO.storeMonthPoint(sql,mp);
			}
		}
		
		//print Month Report
		//PrintUtils.Print((MonthPeriod)tp);
	}



	public void generateReport() {
		// TODO Auto-generated method stub
		//primero calculamos los puntos
				calculatePoints2();
				
				//agrupamos fechas
				MonthPeriod2 tp = null;		
				tp = new MonthPeriod2(30000);
				List<TimeFrecuency> dfList = tp.getTime();		
				//TimeFrecuency timef = null;
				System.out.println("total points added: "+points.size());
				for (int i=0;i<points.size();i++){
					FundamentalTrendPoint p = points.get(i);
					Date d = p.getDate();
					TimeFrecuency timef = null;
					timef = tp.findFrec(d.getMonth(), p.getLowInterval());
					timef.incFrecuency();
					//System.out.println("incrementando frecuencua(mes,bar,total): "+
					//		d.getMonth()+","+p.getLowInterval()+","+timef.getFrecuency());
				}
				
				
					List<TimeFrecuency> dfl = tp.getTime();
					
					for (int i=0;i<dfl.size();i++){
						MonthFrecuency2 df = (MonthFrecuency2) dfl.get(i);
						
						MonthPoint mp = new MonthPoint();
						mp.setMonth(df.getMonth().ordinal()+1);
						mp.setSymbol(symbol);
						mp.setT1(t1);
						t2.setLowBar(df.getBars());
						t2.setHighBar(df.getBars());
						mp.setT2(t2);
						mp.setTotal(df.getFrecuency());
						
						if (df.getFrecuency()>0)
							DAO.storeMonthPoint(sql,mp);
					}
				
				
				//print Month Report
				//PrintUtils.Print((MonthPeriod)tp);
	}
	
}
