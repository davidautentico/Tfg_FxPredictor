package drosa.random;

import java.util.Random;

public class RandomGenerator {
	
	public static int randomInt(Random random,int aStart, int aEnd){
		
		long range = (long)aEnd - (long)aStart + 1;
		    // compute a fraction of the range, 0 <= frac < range
		long fraction = (long)(range * random.nextDouble());
		int randomNumber =  (int)(fraction + aStart);    
		return randomNumber;
	}

	public static int randomInt(int aStart, int aEnd){
		Random random = new Random();
		long range = (long)aEnd - (long)aStart + 1;
		    // compute a fraction of the range, 0 <= frac < range
		long fraction = (long)(range * random.nextDouble());
		int randomNumber =  (int)(fraction + aStart);    
		return randomNumber;
	}
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(RandomGenerator.randomInt(4, 56));
		System.out.println(RandomGenerator.randomInt(1, 90));
	}

	public static double randomDouble(double min, double max) {
		// TODO Auto-generated method stub
		Random random = new Random();
		double num = random.nextDouble() * (max-min) + min;
		return num;
	}

}
