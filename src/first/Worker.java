package first;

import java.util.ArrayList;
import java.util.HashSet;

import bc.*;

public class Worker 
{
	private static HashSet<Robot> idleWorkers = new HashSet<Robot>();
	private static HashSet<Robot> currentBlueprints = new HashSet<Robot>();
	
	public static void startTurn()
	{
		for (Robot structure:currentBlueprints)
		{
			if (structure.structureIsBuilt() == 1)
			{
				currentBlueprints.remove(structure);
			}
		}
	}
	
	
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
				System.out.printf("\t\tAttempting to build Factory\n", bestWorker.worker.id());
				Game.blueprint(bestWorker.worker, UnitType.Factory, buildDir);
				idleWorkers.remove(bestWorker.worker);
				currentBlueprints.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(bestWorker.worker.tile(), buildDir, 1)));
				System.out.printf("\t\t factory ID is %d\n", currentBlueprints.iterator().next().id());
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
	
	private static void giveWorkersOrders()
	{
		workerLabel: for (Robot worker:GameInfoCache.allyWorkers)
		{
			for (Robot structure:currentBlueprints)
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
			Tile closest = null;
			int minDistance = Constants.INFINITY;
			int distance;
			for (Tile deposit:GameInfoCache.karboniteDeposits)
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
				if (Pathfinding.pathLength(worker.tile(), closest) <= 1)
				{
					if (Game.canHarvest(worker, Pathfinding.path(worker.tile(), closest)))
					{
						Game.harvest(worker, Pathfinding.path(worker.tile(), closest));
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
		giveWorkersOrders();
		if (shouldReplicate())
		{
			replicateWorkers();
		}
	}
	
	
	
	
}
