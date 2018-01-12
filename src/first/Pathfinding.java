package first;

import bc.*;
import java.util.*;

public class Pathfinding {
	private static Map<Tile, Map<Tile, Integer>> cache;
	private static Map<Tile, Queue<Tile>> open;
	private static Map<Tile, Set<Tile>> closed;
	static {
		cache = new HashMap<>();
		open = new HashMap<>();
		closed = new HashMap<>();
	}
	
	private static void bfs(Tile source, Tile dest) {

		 if (!cache.containsKey(dest)) {
			 open.put(dest, new LinkedList<>());
			 closed.put(dest, new HashSet<>());
			 cache.put(dest, new HashMap<>());
			 
		 }
		 
		 open.get(dest).add(dest);
		 closed.get(dest).add(dest);
		 cache.get(dest).put(dest, 0);

		 while (open.get(dest).size() > 0) {
			Tile current = open.get(dest).poll();
			// for each direction 
			for (int i = 0; i < 8; i++) {
				Tile test = current.add(Game.moveDirections[i]);
				if (!closed.get(dest).contains(test) && Game.isPassableTerrainAt(test)) {
					open.get(dest).add(test);
					closed.get(dest).add(test);
					cache.get(dest).put(test, cache.get(dest).get(current) + 1);
				}
			}
			if (current == source) {
				return;
			}
		 }
	}
	
	public static Direction path(Tile source, Tile dest) {
		if (!(cache.containsKey(dest) && cache.get(dest).containsKey(source))) {
			bfs(source, dest);
		}
		if (!cache.get(dest).containsKey(source)) {
			return Direction.Center;
		}
		Direction best = Direction.Center;
		for (Direction direction: Game.directions) {
			if (Game.isPassableTerrainAt(source.add(direction)) && Game.isOccupiable(source.add(direction)) > 0) {
				if (best == Direction.Center && (cache.get(dest).get(source.add(best)) >= cache.get(dest).get(source.add(direction)))) {
					best = direction;
				}
				else if (cache.get(dest).get(source.add(best)) > cache.get(dest).get(source.add(direction))) {
					best = direction;
				}
			}
		}
		return best;
	}
	
	public static int pathLength(Tile source, Tile dest) {
		if (!(cache.containsKey(dest) && cache.get(dest).containsKey(source)) || !(cache.containsKey(source) && cache.get(source).containsKey(dest))) {
			bfs(source, dest);
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
