package dev;

import java.util.*;

public class Timing {
	public static Map<String, Long> startTimes = new HashMap<>();
	public static Map<String, Long> total = new HashMap<>();
	public static void start(String label) {
		startTimes.put(label, System.nanoTime());
	}
	public static void end(String label) {
		if (!startTimes.containsKey(label)) {
			total.put(label, (long) 0);
		} else {
			total.put(label, total.getOrDefault(label, (long) 0) + (System.nanoTime() - startTimes.get(label)));
		}
	}
	public static void printTotal(String label) {
		System.out.println(label + " took " + total.getOrDefault(label, (long) 0) / 1000000.0 + "ms");
	}
	public static void endAndPrint(String label) {
		end(label);
		printTotal(label);
	}
	public static void reset() {
		startTimes = new HashMap<>();
		total = new HashMap<>();
	}
}
