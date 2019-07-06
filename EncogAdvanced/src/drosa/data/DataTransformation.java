package drosa.data;

import java.util.ArrayList;


import drosa.data.indicators.TAIndicator;
import drosa.finances.Quote;
import drosa.utils.PrintUtils;

public class DataTransformation {
	
	private static double findMin(double list [][],int col){		
		int numPatterns= list.length;
		//System.out.println("[findMin] número de patrones: "+numPatterns+", número de columna: "+col);
		double xMin =list[0][col];		
		for (int i=1;i<numPatterns;i++){			
			if (list[i][col]<xMin)
				xMin = list[i][col];				
		}
		
		return xMin;
	}
	
	private static double findMax(double list [][],int col){		
		int numPatterns= list.length;
		double xMax =list[0][col];		
		for (int i=1;i<numPatterns;i++){			
			if (list[i][col]>xMax)
				xMax = list[i][col];				
		}
		
		return xMax;
	}
	
	public static void scaling(double[][] list,int type){		
		int numColumns = list[0].length;
		//System.out.println("[Scaling] número de columnas: "+numColumns);
		for (int i=0;i<numColumns;i++){
			double xMin = findMin(list,i);
			double xMax = findMax(list,i);
			
			//System.out.println("[Scaling] Mín y max de col "+i+" : "+xMin+","+xMax);
			for (int j=0;j<list.length;j++){
				list[j][i] = 0 +(1-0)*(list[j][i]-xMin)/(xMax-xMin);
			}
		}		
	}
	
	public static void scaling(double[][] list,int low,int high){		
		int numColumns = list[0].length;
		//System.out.println("[Scaling] número de columnas: "+numColumns);
		//System.out.println("[Scaling] primer elemento: "+list[0][0]);
		for (int i=0;i<numColumns;i++){
			double xMin = findMin(list,i);
			double xMax = findMax(list,i);			
			//System.out.println("[Scaling] Mín y max de col "+i+" : "+xMin+","+xMax);
			for (int j=0;j<list.length;j++){
				list[j][i] = low +(high-low)*(list[j][i]-xMin)/(xMax-xMin);
				//System.out.println("scaled "+j+","+i+" : "+list[j][i]);
			}
		}		
	}
	
	public static CSVData toCSVData(double[][] trainingSetIn,double[][] trainingSetOut){
		CSVData csvData = new CSVData();
		
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();
		
		for (int i=0;i<trainingSetIn[0].length;i++){
			names.add("INPUT "+(i+1));
		}
		
		if (trainingSetOut!=null)
		for (int i=0;i<trainingSetOut[0].length;i++){
			names.add("OUTPUT "+(i+1));
		}
		
		for (int i=0;i<trainingSetIn.length;i++){
			ArrayList<Double> rowValue= new ArrayList<Double>();
			for (int j=0;j<trainingSetIn[i].length;j++){
				rowValue.add(trainingSetIn[i][j]);
			}
			if (trainingSetOut!=null)
			rowValue.add(trainingSetOut[i][0]);
			values.add(rowValue);
		}
		
		csvData.setNames(names);
		csvData.setValues(values);
		
		return csvData;
	}
	
	public static CSVData rawData(ArrayList<Quote> data){
		CSVData csvData = new CSVData();
		
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();
		
		names.add("open");
		names.add("high");
		names.add("low");
		names.add("close");
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			ArrayList<Double> rowValue= new ArrayList<Double>();
			rowValue.add(q.getOpen());
			rowValue.add(q.getHigh());
			rowValue.add(q.getLow());
			rowValue.add(q.getClose());
			values.add(rowValue);
		}
		
		csvData.setNames(names);
		csvData.setValues(values);
		
		return csvData;
	}
	
	public static double[][] getOHL(ArrayList<Quote> data){
		double[][] ohl = new double [data.size()][3];
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			ohl[i][0]=q.getOpen();
			ohl[i][1]=q.getHigh();
			ohl[i][2]=q.getLow();
		}
		return ohl;
	}
	
	public static double[][] getField(ArrayList<Quote> data,QuoteFields field){
		double[][] ohl = new double [data.size()][1];
		
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			double value=0.0;
			
			if (field==QuoteFields.OPEN)
				value = q.getOpen();
			if (field==QuoteFields.HIGH)
				value = q.getHigh();
			if (field==QuoteFields.LOW)
				value = q.getLow();
			if (field==QuoteFields.CLOSE)
				value = q.getClose();
			
			ohl[i][0]=value;
		}
		return ohl;
	}
	
	

	
	public static double[][] switchData(double[][] data,int index){
		double[][] data2 = new double [data.length-index][data[0].length];
		
		int m=0;
		for (int i=index;i<data.length;i++){
			double [] d =data[i];
			for (int j=0;j<d.length;j++){
				data2[m][j]=d[j];
			}
			m++;
		}
		
		return data2;
	}

	public static double[][] getTrendInd(ArrayList<Quote> data,
			QuoteFields field, int n) {
		// TODO Auto-generated method stub
		double[][] t = new double [data.size()-n][1];
		int begin=1;
		if ((begin-n)<0) begin=n;
		int m=0;
		for (int i=begin;i<data.size();i++){
			Quote q = data.get(i);
			Quote q1 = data.get(i-n);
			double value=0.0;
			
			if (field==QuoteFields.OPEN){
				if (q.getOpen()>=q1.getOpen())
					value=1.0;
			}
			if (field==QuoteFields.HIGH)
				if (q.getHigh()>=q1.getHigh())
					value=1.0;
			if (field==QuoteFields.LOW)
				if (q.getLow()>=q1.getLow())
					value=1.0;
			if (field==QuoteFields.CLOSE)
				if (q.getClose()>=q1.getClose())
					value=1.0;
			
			t[m++][0]=value;
		}
		return t;
	}

	/**
	 * Combina los indicators
	 * @param inds
	 * @return
	 */
	public static double[][] combineIndicators(ArrayList<TAIndicator> inds) {
		// TODO Auto-generated method stub
		
		int dataSize = inds.get(0).getData().size();
		double[][] data = new double[dataSize][inds.size()];
		
		for (int i=0;i<dataSize;i++){
			//System.out.println(i);
			for (int j=0;j<inds.size();j++){
				TAIndicator ind = inds.get(j);
				double value = ind.getValue(i);
				data[i][j] = value;
				//System.out.println(i+" "+j+" "+data[i][j]);
			}
		}
		//System.out.println("dataSize inputs len y first: "+dataSize+" "+data[0][0]);
		return data;
	}

	public static void printData(double[][] data) {
		// TODO Auto-generated method stub
		for (int i=0;i<data.length;i++){
			double[] row = data[i];
			String rowStr=PrintUtils.Print(row[0])+" ";
			for (int j=1;j<row.length;j++){
				rowStr+=PrintUtils.Print(row[j])+" "; 
			}
			System.out.println(rowStr);
		}
	}

	public static void scaling(double[] list, int low, int high) {
		// TODO Auto-generated method stub
		double xMin = findMin(list);
		double xMax = findMax(list);
			
		for (int j=0;j<list.length;j++){
				list[j] = 0 +(1-0)*(list[j]-xMin)/(xMax-xMin);
		}	
	}

	private static double findMax(double[] list) {
		// TODO Auto-generated method stub
		int numPatterns= list.length;
		double xMax =list[0];		
		for (int i=1;i<numPatterns;i++){			
			if (list[i]>xMax)
				xMax = list[i];				
		}
		
		return xMax;
	}

	private static double findMin(double[] list) {
		// TODO Auto-generated method stub
		int numPatterns= list.length;
		//System.out.println("[findMin] número de patrones: "+numPatterns+", número de columna: "+col);
		double xMin =list[0];		
		for (int i=1;i<numPatterns;i++){			
			if (list[i]<xMin)
				xMin = list[i];				
		}
		
		return xMin;
	}

	public static void mult(double[] list, int n) {
		// TODO Auto-generated method stub
		for (int i=0;i<list.length;i++){
			list[i] = list[i]*n;
		}
	}

}
