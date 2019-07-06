package drosa.utils;

import java.util.ArrayList;

public class LinearRegression {
	
	ArrayList<Double> xSerie = new ArrayList<Double>();
	ArrayList<Double> ySerie = new ArrayList<Double>();
	private double correlation;
	private double stddev;
	private double slopeErr;
	private double slope;
	private double intercept;
	
	
	
	public ArrayList<Double> getxSerie() {
		return xSerie;
	}

	public void setxSerie(ArrayList<Double> xSerie) {
		this.xSerie = xSerie;
	}

	public ArrayList<Double> getySerie() {
		return ySerie;
	}

	public void setySerie(ArrayList<Double> ySerie) {
		this.ySerie = ySerie;
	}

	public double getCorrelation() {
		return correlation;
	}

	public void setCorrelation(double correlation) {
		this.correlation = correlation;
	}

	public double getStddev() {
		return stddev;
	}

	public void setStddev(double stddev) {
		this.stddev = stddev;
	}

	public double getSlopeErr() {
		return slopeErr;
	}

	public void setSlopeErr(double slopeErr) {
		this.slopeErr = slopeErr;
	}

	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public double getIntercept() {
		return intercept;
	}

	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}



	public void addPoint(double x,double y){
		xSerie.add(x);
		ySerie.add(y);
	}
	
	
	
	public void calculate(){
		
		double muX = mean(xSerie);   
		   
	    double muY = mean(ySerie); 
	    
		int n = xSerie.size();
		double SSxy = 0;   
	    double SSxx = 0;   
	    double SSyy = 0;   
	    double Sx = 0;   
	    double Sy = 0;   
	    double Sxy = 0;   
	    double SSy = 0;   
	    double SSx = 0;
	    
	    for (int i = 0; i < n; i++) {   
	      Sx = Sx + xSerie.get(i);   
	      Sy = Sy + ySerie.get(i);   
	      Sxy = Sxy + (xSerie.get(i) * ySerie.get(i));   
	      SSx = SSx + (xSerie.get(i) * xSerie.get(i));   
	      SSy = SSy + (ySerie.get(i) * ySerie.get(i));   
	      double subX = (xSerie.get(i) - muX);   
	      double subY = (ySerie.get(i) - muY);   
	      SSyy = SSyy + subY * subY;   
	      SSxy = SSxy + subX * subY;   
	      SSxx = SSxx + subX * subX;   
	    }   
	    
	    // slope   
	    double b = SSxy / SSxx;   
	    // intercept   
	    double a = muY - b * muX;   	   
	    // standard deviation of the points   
	    double stddevPoints = Math.sqrt( (SSyy - b * SSxy)/(n-2) );   
	   
	    // Error of the slope   
	    double bError = stddevPoints / Math.sqrt( SSxx );   
	   
	    double r2Numerator = (n * Sxy) - (Sx * Sy);   
	    double r2Denominator = ((n*SSx) - (Sx * Sx))*((n*SSy) - (Sy * Sy));   
	    double r2 = (r2Numerator * r2Numerator) / r2Denominator;   
	   
	    double signB = (b < 0) ? -1.0 : 1.0;   
	   
	    double r = signB * Math.sqrt( r2 ); 
	    
	    this.correlation = r;
	    this.stddev = stddevPoints;
	    this.slopeErr = bError;
	    this.slope = b;
	    this.intercept = a;
	}

	private double mean(ArrayList<Double> serie) {
		// TODO Auto-generated method stub
		
		double accum=0;
		for (int i=0;i<serie.size();i++){
			accum+=serie.get(i);
		}
		return accum/serie.size();
	}

	public void printInfo() {
		// TODO Auto-generated method stub
		System.out.println(
			"Slope: "+PrintUtils.Print(this.slope)
			+" Err: "+PrintUtils.Print(this.slopeErr)
			+" Corr: "+PrintUtils.Print(this.correlation)
			+" StdDev: "+PrintUtils.Print(this.stddev)
			);
		
		
	}
}
