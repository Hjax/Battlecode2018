package dev;

import bc.*;

import java.util.*;

public class Pathfinding {
	private static int[][] cache;;
	private static Random rng = new Random(1486);
	static {
		cache = new int[Game.WIDTH*Game.HEIGHT][Game.WIDTH*Game.HEIGHT];
		for (int[] row: cache) {
			Arrays.fill(row, -1);
		}
	}
	private static int[] directions = {1, 1 - Game.WIDTH, -1 * Game.WIDTH, -1 - Game.WIDTH, -1, Game.WIDTH - 1, Game.WIDTH, Game.WIDTH + 1};
	
	private static void bfs(Tile dest) {

		Queue<Integer> open = new LinkedList<>();
		Set<Integer> closed = new HashSet<>();
		 
		Integer destination = dest.getX() + dest.getY() * Game.WIDTH;
		int size = Game.WIDTH * Game.HEIGHT;
		 
		open.add(destination);
		closed.add(destination);
		cache[destination][destination] = 0;
		while (open.size() > 0) {
			Integer current = open.poll();
			// for each direction 
			for (int i = 0; i < 8; i++) {
				Integer test = current + directions[i];
				if (!closed.contains(test) && !open.contains(test)) {
					if (Math.abs(test % Game.WIDTH - current % Game.WIDTH) <= 1 && test >= 0 && test < size && Game.pathMap[test]) {
						 open.add(test);
						 cache[destination][test] = cache[destination][current] + 1;
					}
				}
				closed.add(current);
			}
		}
	}
	
	public static Direction path(Tile source, Tile dest) 
	{
		Integer sourceInt = source.getX() + source.getY() * Game.WIDTH;
		Integer destInt = dest.getX() + dest.getY() * Game.WIDTH;
		if (cache[destInt][destInt] == -1) {
			bfs(dest);
		}
		if (cache[destInt][sourceInt] == -1) {
			return Direction.Center;
		}
		int best = 0;
		for (int direction: directions) {
			int test = sourceInt + direction;
			
			if (Math.abs(test % Game.WIDTH - sourceInt % Game.WIDTH) <= 1 && test >= 0 && test < Game.WIDTH * Game.HEIGHT &&Game.pathMap[test] && Game.isOccupiable(Tile.getInstance(Game.planet(), (test) % Game.WIDTH, (test)/Game.WIDTH)) > 0) {
				if (best == 0 && (cache[destInt][sourceInt + best]) >= cache[destInt][test]) {
					best = direction;
				}
				else if (cache[destInt][sourceInt + best] > cache[destInt][test]) {
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
		else if (best == 1 - Game.WIDTH)
		{
			//System.out.printf("Southeast\n");
			return Direction.Southeast;
		}
		else if (best == -1 * Game.WIDTH)
		{
			//System.out.printf("South\n");
			return Direction.South;
		}
		else if (best == -1 - Game.WIDTH)
		{
			//System.out.printf("Southwest\n");
			return Direction.Southwest;
		}
		else if (best == -1)
		{
			//System.out.printf("West\n");
			return Direction.West;
		}
		else if (best == Game.WIDTH - 1)
		{
			//System.out.printf("Northwest\n");
			return Direction.Northwest;
		}
		else if (best == Game.WIDTH)
		{
			//System.out.printf("North\n");
			return Direction.North;
		}
		else if (best == Game.WIDTH+1)
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
	
	public static Direction karbonitePath(Tile source) 
	{
		Integer sourceInt = source.getX() + source.getY() * Game.WIDTH;
		if (Game.nearestKarbonite[sourceInt] <= 0)
		{
			return Direction.Center;
		}
		int best = 0;
		int dirStart = rng.nextInt(7);
		int direction = dirStart;
		do
		{
			int test = sourceInt + directions[direction];
			if (Math.abs(test % Game.WIDTH - sourceInt % Game.WIDTH) <= 1 && test >= 0 && test < Game.WIDTH * Game.HEIGHT &&Game.pathMap[test] && Game.isOccupiable(Tile.getInstance(Game.planet(), (test) % Game.WIDTH, (test)/Game.WIDTH)) > 0) {
				if (best == 0 && (Game.karboniteDistance[sourceInt + best]) >= Game.karboniteDistance[test]) {
					best = directions[direction];
				}
				else if (Game.karboniteDistance[sourceInt + best] > Game.karboniteDistance[test]) {
					best = directions[direction];
				}
			}
			if (direction == 7)
			{
				direction = -1;
			}
		}while(++direction != dirStart);
		//System.out.printf("Pathing from (%d,%d) to (%d,%d) using direction ", source.getX(), source.getY(), dest.getX(), dest.getY());
		
		if (best == 1)
		{
			//System.out.printf("East\n");
			return Direction.East;
		}
		else if (best == 1 - Game.WIDTH)
		{
			//System.out.printf("Southeast\n");
			return Direction.Southeast;
		}
		else if (best == -1 * Game.WIDTH)
		{
			//System.out.printf("South\n");
			return Direction.South;
		}
		else if (best == -1 - Game.WIDTH)
		{
			//System.out.printf("Southwest\n");
			return Direction.Southwest;
		}
		else if (best == -1)
		{
			//System.out.printf("West\n");
			return Direction.West;
		}
		else if (best == Game.WIDTH - 1)
		{
			//System.out.printf("Northwest\n");
			return Direction.Northwest;
		}
		else if (best == Game.WIDTH)
		{
			//System.out.printf("North\n");
			return Direction.North;
		}
		else if (best == Game.WIDTH+1)
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
	
	
	public static Direction ghostPath(Tile source, Tile dest) 
	{
		Integer sourceInt = source.getX() + source.getY() * Game.WIDTH;
		Integer destInt = dest.getX() + dest.getY() * Game.WIDTH;
		if (cache[destInt][destInt] == -1) {
			bfs(dest);
		}
		if (cache[destInt][sourceInt] == -1) {
			return Direction.Center;
		}
		int dirStart = rng.nextInt(7);
		int direction = dirStart;
		int best = 0;
		do
		{
			int test = sourceInt + direction;
			
			if (Math.abs(test % Game.WIDTH - sourceInt % Game.WIDTH) <= 1 && test >= 0 && test < Game.WIDTH * Game.HEIGHT &&Game.pathMap[test] && Game.isPassableTerrainAt(Tile.getInstance(Game.planet(), (test) % Game.WIDTH, (test)/Game.WIDTH))) {
				if (best == 0 && (cache[destInt][sourceInt + best]) >= cache[destInt][test]) {
					best = direction;
				}
				else if (cache[destInt][sourceInt + best] > cache[destInt][test]) {
					best = direction;
				}
			}
			if (direction == 7)
			{
				direction = -1;
			}
		} while(++direction != dirStart);
		//System.out.printf("Pathing from (%d,%d) to (%d,%d) using direction ", source.getX(), source.getY(), dest.getX(), dest.getY());
		
		if (best == 1)
		{
			//System.out.printf("East\n");
			return Direction.East;
		}
		else if (best == 1 - Game.WIDTH)
		{
			//System.out.printf("Southeast\n");
			return Direction.Southeast;
		}
		else if (best == -1 * Game.WIDTH)
		{
			//System.out.printf("South\n");
			return Direction.South;
		}
		else if (best == -1 - Game.WIDTH)
		{
			//System.out.printf("Southwest\n");
			return Direction.Southwest;
		}
		else if (best == -1)
		{
			//System.out.printf("West\n");
			return Direction.West;
		}
		else if (best == Game.WIDTH - 1)
		{
			//System.out.printf("Northwest\n");
			return Direction.Northwest;
		}
		else if (best == Game.WIDTH)
		{
			//System.out.printf("North\n");
			return Direction.North;
		}
		else if (best == Game.WIDTH+1)
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
		Integer sourceInt = source.getX() + source.getY() * Game.WIDTH;
		Integer destInt = dest.getX() + dest.getY() * Game.WIDTH;
		if (!(cache[destInt][destInt] != -1 || cache[sourceInt][sourceInt] != -1)) {
		//if (!((cache.containsKey(destInt) && cache.get(destInt).containsKey(sourceInt)) || (cache.containsKey(sourceInt) && cache.get(sourceInt).containsKey(destInt)))) {
			bfs(dest);
		}
		if (cache[destInt][destInt] != -1) {
			if (cache[destInt][sourceInt] == -1) {
				return -1;
			}
			return cache[destInt][sourceInt];
		} else {
			if (cache[sourceInt][destInt] == -1) {
				return -1;
			}
			return cache[sourceInt][destInt];
		}
	}
	
}
