package first;

import bc.*;
import java.util.*;

public class Pathfinding {
	private static Map<Tile, Map<Tile, Integer>> cache;
	static {
		cache = new HashMap<>();
	}
	
	private static void bfs(Tile dest) {

		 Map<Tile, Integer> current_map = new HashMap<>();
		 Queue<Tile> open = new LinkedList<>();
		 Set<Tile> closed = new HashSet<>();
		 
		 open.add(dest);
		 closed.add(dest);
		 current_map.put(dest, 0);
		 while (open.size() > 0) {
			Tile current = open.poll();
			// for each direction 
			for (int i = 0; i < 8; i++) {
				Tile test = current.add(Game.moveDirections[i]);
				if (!closed.contains(test) && Game.isPassableTerrainAt(test)) {
					open.add(test);
					closed.add(test);
					current_map.put(test, current_map.get(current) + 1);
				}
			}
		 }
		 cache.put(dest, current_map);
	}
	
	public static Direction path(Tile source, Tile dest) {
		if (cache.containsKey(dest)) {
			bfs(dest);
		}
		if (!cache.get(dest).containsKey(source)) {
			return Direction.Center;
		}
		Direction best = Direction.Center;
		for (Direction direction: Game.directions) {
			if (Game.isPassableTerrainAt(source.add(direction))) {
				if (cache.get(dest).get(source.add(best)) > cache.get(dest).get(source.add(direction))) {
					best = direction;
				}
			}
		}
		return best;
	}
	
	public static int pathLength(Tile source, Tile dest) {
		if (!cache.containsKey(dest) && !cache.containsKey(source)) {
			long start = System.nanoTime();
			bfs(dest);
			System.out.println((System.nanoTime() - start) / 1000000.0);
		}
		if (cache.containsKey(dest)) {
			if (!cache.get(dest).containsKey(source)) {
				return -1;
			}
			return cache.get(dest).get(source);
		} else {
			if (!cache.get(source).containsKey(dest)) {
				return -1;
			}
			return cache.get(source).get(dest);
		}
	}
	
}
