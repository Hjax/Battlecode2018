package SA;

import bc.*;

import java.util.*;

public class Pathfinding {
	private static Map<Integer, Map<Integer, Integer>> cache;
	static {
		cache = new HashMap<>();
	}
	private static int[] directions = {1, 1 - Constants.WIDTH, -1 * Constants.WIDTH, -1 - Constants.WIDTH, -1, Constants.WIDTH - 1, Constants.WIDTH, Constants.WIDTH + 1};
	
	private static void bfs(Tile dest) {
		Map<Integer, Integer> current_map = new HashMap<>();
		Queue<Integer> open = new LinkedList<>();
		Set<Integer> closed = new HashSet<>();
		 
		Integer destination = dest.getX() + dest.getY() * Constants.WIDTH;
		int size = Constants.WIDTH * Constants.HEIGHT;
		 
		open.add(destination);
		closed.add(destination);
		current_map.put(destination, 0);
		while (open.size() > 0) {
			Integer current = open.poll();
			// for each direction 
			for (int i = 0; i < 8; i++) {
				Integer test = current + directions[i];
				if (!closed.contains(test) && !open.contains(test)) {
					if (Math.abs(test % Constants.WIDTH - current % Constants.WIDTH) <= 1 && test >= 0 && test < size && Game.pathMap[test]) {
						 open.add(test);
						 current_map.put(test, current_map.get(current) + 1);
					}
				}
				closed.add(current);
			}
		}
		cache.put(destination, current_map);
	}
	
	public static Direction path(Tile source, Tile dest) 
	{
		Integer sourceInt = source.getX() + source.getY() * Constants.WIDTH;
		Integer destInt = dest.getX() + dest.getY() * Constants.WIDTH;
		if (!cache.containsKey(destInt)) {
			bfs(dest);
		}
		if (!cache.get(destInt).containsKey(sourceInt)) {
			return Direction.Center;
		}
		int best = 0;
		for (int direction: directions) {
			int test = sourceInt + direction;
			if (Math.abs(test % Constants.WIDTH - sourceInt % Constants.WIDTH) <= 1 && test >= 0 && test < Constants.WIDTH * Constants.HEIGHT &&Game.pathMap[test] && Game.isOccupiable(Tile.getInstance(Game.planet(), (test) % Constants.WIDTH, (test)/Constants.WIDTH)) > 0) {
				if (best == 0 && (cache.get(destInt).get(sourceInt + best)) >= cache.get(destInt).get(test)) {
					best = direction;
				}
				else if (cache.get(destInt).get(sourceInt + best) > cache.get(destInt).get(test)) {
					best = direction;
				}
			}
		}
		//System.out.printf("Pathing from (%d,%d) to (%d,%d) using direction ", source.getX(), source.getY(), dest.getX(), dest.getY());
		
		if (best == 1)
		{
			//System.out.printf("East\n");
			return Direction.East;
		}
		else if (best == 1 - Constants.WIDTH)
		{
			//System.out.printf("Southeast\n");
			return Direction.Southeast;
		}
		else if (best == -1 * Constants.WIDTH)
		{
			//System.out.printf("South\n");
			return Direction.South;
		}
		else if (best == -1 - Constants.WIDTH)
		{
			//System.out.printf("Southwest\n");
			return Direction.Southwest;
		}
		else if (best == -1)
		{
			//System.out.printf("West\n");
			return Direction.West;
		}
		else if (best == Constants.WIDTH - 1)
		{
			//System.out.printf("Northwest\n");
			return Direction.Northwest;
		}
		else if (best == Constants.WIDTH)
		{
			//System.out.printf("North\n");
			return Direction.North;
		}
		else if (best == Constants.WIDTH+1)
		{
			//System.out.printf("Northeast\n");
			return Direction.Northeast;
		}
		else
		{
			//System.out.printf("Center\n");
			return Direction.Center;
		}
	}
	
	public static int pathLength(Tile source, Tile dest) {
		Integer sourceInt = source.getX() + source.getY() * Constants.WIDTH;
		Integer destInt = dest.getX() + dest.getY() * Constants.WIDTH;
		if (!((cache.containsKey(destInt) && cache.get(destInt).containsKey(sourceInt)) || (cache.containsKey(sourceInt) && cache.get(sourceInt).containsKey(destInt)))) {
			bfs(dest);
		}
		if (cache.containsKey(destInt)) {
			if (!cache.get(destInt).containsKey(sourceInt)) {
				return -1;
			}
			return cache.get(destInt).get(sourceInt);
		} else {
			if (!cache.get(sourceInt).containsKey(destInt)) {
				return -1;
			}
			return cache.get(sourceInt).get(destInt);
		}
	}
	
}
