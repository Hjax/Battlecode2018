package first;

import bc.*;
import java.util.*;

public class Pathfinding {
	private static Map<Integer, Map<Integer, Integer>> cache;
	private static final int CONST1; 
	private static final int CONST2;
	private static final int CONST3;
	static {
		cache = new HashMap<>();
		// magic constants for serialize / deserialize
		CONST1 = (int) Math.pow(2, 20);
		CONST2 = (int) Math.pow(2, 10);
		CONST3 = (int) Math.pow(2, 6) - 1;
	}
	
	
	
	
	private static List<MapLocation> around(MapLocation target) {
		List<MapLocation> result = new ArrayList<>();
		for (Direction direction: game.directions) {
			if (game.onMap(target.add(direction), target.getPlanet()) && game.isPassableTerrainAt(target.add(direction))) {
				result.add(target.add(direction));
			}
		}
		return result;
	}

	private static int serialize(MapLocation loc) {
		return loc.getX() * CONST1 + loc.getY() * CONST2 + (game.planet() == Planet.Earth ? 1 : 0);
	}
	
	private static MapLocation deserialize(int loc) {
		return new MapLocation((loc & 1) == 1 ? Planet.Earth : Planet.Mars, (loc / CONST1) & CONST3, (loc / CONST2) & CONST3);
	}
	
	// methods for serialized maplocations
	
	private static void put(Collection<Integer> a, MapLocation b) {
		a.add(serialize(b));
	}
	
	private static boolean contains(Collection<Integer> a, MapLocation b) {
		return a.contains(serialize(b));
	}
	
	private static MapLocation pop(Queue<Integer> a) {
		return deserialize(a.poll());
	}
	
	private static void put(Map<Integer, Integer> a, MapLocation b, int c) {
		a.put(serialize(b), c);
	}
	
	private static int get(Map<Integer, Integer> a, MapLocation b) {
		return a.get(serialize(b));
	}
	
	private static void bfs(MapLocation dest) {

		 Map<Integer, Integer> current_map = new HashMap<>();
		 Queue<Integer> open = new LinkedList<>();
		 Set<Integer> closed = new HashSet<>();
		 
		 put(open, dest);
		 put(current_map, dest, 0);
		 while (open.size() > 0) {
			 MapLocation current = pop(open);
			 for (MapLocation loc: around(current)) {
				 if (!contains(open, loc) && !contains(closed, loc)) {
					 put(open, loc);
					 put(current_map, loc, get(current_map, current) + 1);
				 }
			 }
			 put(closed, current);
		 }
		 cache.put(serialize(dest), current_map);
	}
	
	public static Direction path(MapLocation source, MapLocation dest) {
		if (!contains(cache.keySet(), dest)) {
			long start = System.nanoTime();
			bfs(dest);
			System.out.println((System.nanoTime() - start) / 1000000.0);
		}
		if (!contains(cache.get(serialize(dest)).keySet(), source)) {
			return Direction.Center;
		}
		Direction best = Direction.Center;
		for (Direction direction: game.directions) {
			if (game.isPassableTerrainAt(source.add(direction))) {
				if (get(cache.get(serialize(dest)), source.add(best)) > get(cache.get(serialize(dest)), source.add(direction))) {
					best = direction;
				}
			}
		}
		return best;
	}
	
}
