package workerRush;

import java.util.*;

public class Timing {
	public static Map<String, Long> startTimes = new HashMap<>();
	public static void start(String label) {
		startTimes.put(label, System.nanoTime());
	}
	public static void end(String label) {
		System.out.println(label + " took " + (System.nanoTime() - startTimes.get(label)) / 1000000.0 + "ms");
	}
}
