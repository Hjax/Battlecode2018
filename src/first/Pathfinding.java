package first;

import bc.*;
import java.util.*;

public class Pathfinding {
	private static Map<MapLocation, Map<MapLocation, Integer>> cache;
	static {
		cache = new HashMap<>();
	}
	
	private static List<MapLocation> around(MapLocation target) {
		List<MapLocation> result = new ArrayList<>();
		for (Direction direction: game.directions) {
			if (game.isPassableTerrainAt(target.add(direction))) {
				result.add(target.add(direction));
			}
		}
		return result;
	}
	
	private static void bfs(MapLocation dest) {
		 Map<MapLocation, Integer> current_map = new HashMap<>();
		 Queue<MapLocation> open = new LinkedList<>();
		 Set<MapLocation> closed = new HashSet<>();
		 open.add(dest);
		 while (open.size() > 0) {
			 MapLocation current = open.poll();
			 for (MapLocation loc: around(current)) {
				 if (closed.contains(loc)) {
					 current_map.put(loc, Math.min(current_map.get(current) + 1, current_map.get(loc)));
				 } else {
					 current_map.put(loc, current_map.get(loc));
				 }
				 if (!open.contains(loc)) {
					 open.add(loc);
				 }
			 }
			 closed.add(current);
		 }
		 cache.put(dest, current_map);
	}
	
	public static Direction path(MapLocation source, MapLocation dest) {
		if (!cache.containsKey(dest)) {
			bfs(dest);
		}
		if (!cache.get(dest).containsKey(source)) {
			return Direction.Center;
		}
		Direction best = Direction.Center;
		for (Direction direction: game.directions) {
			if (game.isPassableTerrainAt(source.add(direction))) {
				if (cache.get(dest).get(source.add(best)) > cache.get(dest).get(source.add(direction))) {
					best = direction;
				}
			}
		}
		return best;
	}
	
}
