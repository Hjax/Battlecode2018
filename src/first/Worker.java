package first;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import bc.*;

public class Worker 
{
	private static HashSet<Robot> idleWorkers = new HashSet<Robot>();
	private static HashSet<Tile> factoryPlacements = new HashSet<Tile>(); 
	private static PriorityQueue<Tile> factoryGrid;
	private static Tile factoryGridCenter;
	
	
	private static class WorkerDistanceTuple
	{
		public int rushDistance;
		public Robot worker;
		
		WorkerDistanceTuple(int dist, Robot work)
		{
			rushDistance = dist;
			worker = work;
		}
	}
	
	private static WorkerDistanceTuple bestWorker()
	{
		int min = Constants.INFINITY;
		int max = -1;
		int pathDistance = -1;
		WorkerDistanceTuple closeWorker = new WorkerDistanceTuple(Constants.INFINITY, null);
		WorkerDistanceTuple farWorker = new WorkerDistanceTuple(-1, null);
		
		for (int ally = 0; ally < Constants.startingAlliesLocation.length; ally++)
		{
			for (int enemy = 0; enemy < Constants.startingEnemiesLocation.length; enemy++)
			{
				pathDistance = Pathfinding.pathLength(Constants.startingAlliesLocation[ally], Constants.startingEnemiesLocation[enemy]);
				if (pathDistance > max)
					{
						max = pathDistance;
					}
				if (pathDistance < min)
				{
					min = pathDistance;
				}
			}
			if (max < closeWorker.rushDistance)
			{
				System.out.printf("Updating closeWorker\n");
				closeWorker.rushDistance = max;
				closeWorker.worker = Constants.startingAllies[ally];
			}
			if (min > farWorker.rushDistance)
			{
				System.out.printf("Updating farWorker\n");
				farWorker.rushDistance = min;
				farWorker.worker = Constants.startingAllies[ally];
			}
		}
		if (closeWorker.rushDistance <= Constants.RUSHTHRESHOLD)
		{
			System.out.printf("rush distance is %d\tworker id is %d\n", closeWorker.rushDistance, closeWorker.worker.id());
			return closeWorker;
		}
		System.out.printf("rush distance is %d\tworker id is %d\n", farWorker.rushDistance, farWorker.worker.id());
		return farWorker;
	}
	
	private static class FactoryTileComparator implements Comparator<Tile>
	{
		public int compare(Tile arg0, Tile arg1) {
			int dist1 = Pathfinding.pathLength(arg0, factoryGridCenter);
			int dist2 = Pathfinding.pathLength(arg1, factoryGridCenter);
			if (dist1 < dist2)
			{
				return -1;
			}
			if (dist1 == dist2)
			{
				return 0;
			}
			return 1;
		}
		
	}
	
	private static void buildOrder()
	{
		if (Game.round() == 1)
		{
			WorkerDistanceTuple bestWorker = bestWorker();
			Tile nearestEnemy = Constants.startingEnemiesLocation[0];

			for (Tile enemy:Constants.startingEnemiesLocation)
			{
				if (Pathfinding.pathLength(bestWorker.worker.tile(), enemy) < Pathfinding.pathLength(bestWorker.worker.tile(), nearestEnemy))
				{
					nearestEnemy = enemy;
				}
			}
			Direction buildDir = Pathfinding.path(bestWorker.worker.tile(), nearestEnemy);;
			if (bestWorker.rushDistance > Constants.RUSHTHRESHOLD)
			{
				buildDir = Utilities.oppositeDir(buildDir);
			}
			buildDir = Utilities.findNearestPassableDir(bestWorker.worker.tile(), buildDir);
			if (buildDir != Direction.Center)
			{
				Game.blueprint(bestWorker.worker, UnitType.Factory, buildDir);
				idleWorkers.remove(bestWorker.worker);
				factoryGridCenter = Utilities.offsetInDirection(bestWorker.worker.tile(), buildDir, 1);
				
				factoryGrid = new PriorityQueue<Tile>(Constants.HEIGHT * Constants.WIDTH / 4, new FactoryTileComparator());
				int x = factoryGridCenter.getX() + 2;
				int y = factoryGridCenter.getY();
				Tile place;
				while (y < Constants.HEIGHT)
				{
					while (x < Constants.WIDTH)
					{
						place = Tile.getInstance(Game.planet(), x, y);
						if (Game.isPassableTerrainAt(place))
						{
							factoryGrid.add(place);
						}
						
						x += 2;
					}
					y += 2;
					x -= Constants.WIDTH;
				}
				x = factoryGridCenter.getX() - 2;
				y = factoryGridCenter.getY();
				while (y > Constants.HEIGHT)
				{
					while (x < Constants.WIDTH)
					{
						place = Tile.getInstance(Game.planet(), x, y);
							if (Game.isPassableTerrainAt(place))
							{
								factoryGrid.add(place);
							}
						x -= 2;
					}
					y -= 2;
					x += Constants.WIDTH;
				}
			}
			else
			{
				//TODO: HANDLE OUR BEST WORKER BEING TRAPPED
			}
			
		}
	}
	
	
	//TODO: think of reasonable conditions
	private static boolean shouldReplicate()
	{
		return GameInfoCache.allyWorkers.size() < Constants.WORKERLIMIT;
	}
	
	private static void replicateWorkers()
	{
		for (Robot worker:GameInfoCache.allyWorkers)
		{
			if (worker.location().isOnMap())
			{
				Direction replicateDir;
				replicateDir = Utilities.findOccupiableDir(worker.tile());
				if (Game.canReplicate(worker, replicateDir))
				{
					Game.replicate(worker, replicateDir);
				}
			}
		}
	}
	
	private static int findKarboniteQuadrant(int startingQuadrant)
	{
		Queue<Integer> open = new LinkedList<>();
		Set<Integer> closed = new HashSet<>();
		int[] directions = {1, 1 - Constants.QUADRANTROWSIZE, -1 * Constants.QUADRANTROWSIZE, -1 - Constants.QUADRANTROWSIZE, -1, Constants.QUADRANTROWSIZE - 1, Constants.QUADRANTROWSIZE, Constants.QUADRANTROWSIZE + 1};
	
		int size = Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE;
		 
		open.add(startingQuadrant);
		closed.add(startingQuadrant);
		while (open.size() > 0) {
			Integer current = open.poll();
			// for each direction 
			for (int i = 0; i < 8; i++) {
				Integer test = current + directions[i];
				if (!closed.contains(test) && !open.contains(test)) {
					if (Math.abs(test % Constants.QUADRANTROWSIZE - current % Constants.QUADRANTCOLUMNSIZE) <= 1 && test >= 0 && test < size) 
					{
						if (GameInfoCache.karboniteDeposits.get(test).size() > 0)
						{
							return test;
						}
						 open.add(test);
					}
				}
				closed.add(current);
			}
		}
		return -1;
	}
	
	private static void giveWorkersOrders()
	{
		workerLabel: for (Robot worker:idleWorkers)
		{
			for (Robot structure:GameInfoCache.currentBlueprints)
			{
				if (Pathfinding.pathLength(structure.tile(), worker.tile()) < Constants.BUILDRANGE)
				{
					Direction moveDir = Pathfinding.path(worker.tile(), structure.tile());
					if (Game.canMove(worker, moveDir))
					{
						Game.moveRobot(worker, moveDir);
					}
					if (Pathfinding.pathLength(structure.tile(), worker.tile()) <= 1)
					{
						if (Game.canBuild(worker, structure))
						{
							Game.build(worker, structure);
						}
					}
					continue workerLabel;
				}
			}
			if (Game.isMoveReady(worker))
			{
				Tile closest = null;
				int minDistance = Constants.INFINITY;
				int distance;
				int destinationQuadrant = findKarboniteQuadrant(worker.tile().getX()/Constants.QUADRANTSIZE + worker.tile().getY()/Constants.QUADRANTSIZE * Constants.QUADRANTROWSIZE);
				if (destinationQuadrant == -1)
				{
					continue;
				}
				for (Tile deposit:GameInfoCache.karboniteDeposits.get(destinationQuadrant))
				{
					distance = Pathfinding.pathLength(deposit, worker.tile());
					
					if (distance < minDistance)
					{
						minDistance = distance;
						closest = deposit;
					}
					
				}
				if (closest != null)
				{
					Direction moveDir = Pathfinding.path(worker.tile(), closest);
					if (Game.canMove(worker, moveDir))
					{
						Game.moveRobot(worker, moveDir);
					}
				}
			}	
			
			
		}
	}
	
	private static void harvest()
	{
		for (Robot worker:idleWorkers)
		{
			for (Direction dir: Game.directions)
			{
				if (Game.canHarvest(worker, dir))
				{
					Game.harvest(worker, dir);
				}
			}
		}
	}
	
	private static boolean shouldBuildFactory()
	{
		if (factoryGrid.peek() == null)
		{
			return false;
		}
		if (Game.karbonite() >=100 && GameInfoCache.allyFactories.size() < Constants.FACTORYLIMIT)
		{
			return true;
		}
		return false;
	}
	
	private static void placeFactory()
	{
		
		Tile placement = factoryGrid.peek();
		System.out.printf("\ttrying to place factory at (%d,%d)\n", placement.getX(), placement.getY());
		Robot closestWorker = null;
		int bestDistance = Constants.INFINITY;
		int currentDistance;
		for (Robot worker:idleWorkers)
		{
			currentDistance = Pathfinding.pathLength(worker.tile(), placement);
			if (currentDistance < bestDistance)
			{
				bestDistance = currentDistance;
				closestWorker = worker;
			}
		}
		if (closestWorker == null)
		{
			return;
		}
		if (bestDistance == 1)
		{
			System.out.printf("At (%d,%d) adjacent to placement\n", closestWorker.tile().getX(), closestWorker.tile().getY());
			if (Game.canBlueprint(closestWorker, UnitType.Factory, closestWorker.tile().directionTo(placement)))
			{
				System.out.printf("building factory\n");
				Game.blueprint(closestWorker, UnitType.Factory, closestWorker.tile().directionTo(placement));
				factoryGrid.poll();
				idleWorkers.remove(closestWorker);
			}
		}
		else if (bestDistance == 0)
		{
			System.out.printf("At (%d,%d) on top of placement\n", closestWorker.tile().getX(), closestWorker.tile().getY());
			Direction moveDir = Utilities.findOccupiableDir(closestWorker.tile());
			if (moveDir != Direction.Center)
			{
				if (Game.canMove(closestWorker, moveDir))
				{
					Game.moveRobot(closestWorker, moveDir);
					if (Game.canBlueprint(closestWorker, UnitType.Factory, closestWorker.tile().directionTo(placement)))
					{
						System.out.printf("building factory\n");
						Game.blueprint(closestWorker, UnitType.Factory, closestWorker.tile().directionTo(placement));
						factoryGrid.poll();
						idleWorkers.remove(closestWorker);
					}
					
				}
				
			}
		}
		else if (bestDistance > 1)
		{
			System.out.printf("At (%d,%d) away from placement\n", closestWorker.tile().getX(), closestWorker.tile().getY());
			Direction moveDir = Pathfinding.path(closestWorker.tile(), placement);
			if (moveDir != Direction.Center)
			{
				if (Game.canMove(closestWorker, moveDir))
				{
					Game.moveRobot(closestWorker, moveDir);
					if (Pathfinding.pathLength(closestWorker.tile(), placement) == 1)
					{
						if (Game.canBlueprint(closestWorker, UnitType.Factory, closestWorker.tile().directionTo(placement)))
						{
							System.out.printf("building factory\n");
							Game.blueprint(closestWorker, UnitType.Factory, closestWorker.tile().directionTo(placement));
							factoryGrid.poll();
							idleWorkers.remove(closestWorker);
						}
					}

				}
			}
			
		}
	}
	
	public static void run() 
	{
		idleWorkers = new HashSet<Robot>(GameInfoCache.allyWorkers.size());
		for (Robot worker:GameInfoCache.allyWorkers)
		{
			idleWorkers.add(worker);
		}
		if (Game.planet() == Planet.Earth)
		{
			buildOrder();
		}
		if (shouldBuildFactory())
		{
			placeFactory();
		}
		giveWorkersOrders();
		if (shouldReplicate())
		{
			replicateWorkers();
		}
		harvest();
	}
	
	
	
	
}
