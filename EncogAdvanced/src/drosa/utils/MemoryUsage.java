package drosa.utils;

public class MemoryUsage {
	
	 static final Runtime runtime = Runtime.getRuntime ();
	
	 public static long  memoryUsed ()
	  {
	  return runtime.totalMemory () - runtime.freeMemory ();
	  }
	
}
