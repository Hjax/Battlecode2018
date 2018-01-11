package first;

import java.util.ArrayList;
import java.util.HashSet;

import bc.*;

public class Worker 
{
	private static HashSet<Robot> idleWorkers = new HashSet<Robot>();
	private static ArrayList<Robot> currentBlueprints = new ArrayList<Robot>();
	
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
			MapLocation nearestEnemy = Constants.startingEnemiesLocation[0];

			for (MapLocation enemy:Constants.startingEnemiesLocation)
			{
				if (Pathfinding.pathLength(bestWorker.worker.mapLocation(), enemy) < Pathfinding.pathLength(bestWorker.worker.mapLocation(), nearestEnemy))
				{
					nearestEnemy = enemy;
				}
			}
			Direction buildDir = Pathfinding.path(bestWorker.worker.mapLocation(), nearestEnemy);;
			if (bestWorker.rushDistance > Constants.RUSHTHRESHOLD)
			{
				buildDir = Utilities.oppositeDir(buildDir);
			}
			System.out.printf("bestWorker id is %d\n", bestWorker.worker.id());
			Game.blueprint(bestWorker.worker, UnitType.Factory, buildDir);
			currentBlueprints.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(bestWorker.worker.mapLocation(), buildDir, 1)));
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
				Direction replicateDir = Utilities.findOccupiableDir(worker.mapLocation());
				if (Game.canReplicate(worker, replicateDir))
				{
					Game.replicate(worker, replicateDir);
				}
			}
			
		}
	}
	
	
	private static class WorkerTarget
	{
		public float score;
		public MapLocation location;
		public int targetType;
		/*target types:
		 * 0 = blueprint
		 * 1 = karbonite deposit
		 */
		
		WorkerTarget(float SCORE, MapLocation LOCATION, int TYPE)
		{
			score = SCORE;
			location = LOCATION;
			targetType = TYPE;
		}
		
		private void score(Robot blueprint)
		{
			score = 100;
			score += Game.karbonite() / 20f;
			score += blueprint.health()/blueprint.maxHealth() * 100f;
			for (Robot worker:idleWorkers)
			{
				score += 200/Pathfinding.pathLength(worker.mapLocation(), location);
			}
		}
		private void score(MapLocation deposit)
		{
			score = 20;
			score += 1000/Game.karbonite();
			for (Robot worker:idleWorkers)
			{
				score += 200/Pathfinding.pathLength(worker.mapLocation(), location);
			}
			for (Robot factory:GameInfoCache.allyFactories)
			{
				score += 200/Pathfinding.pathLength(factory.mapLocation(), location);
			}
			for (Robot factory:GameInfoCache.enemyFactories)
			{
				score -= 400/Pathfinding.pathLength(factory.mapLocation(), location);
			}
		}
	
		public void descore(Robot worker)
		{
			if (targetType == 0)
			{
				descoreBlueprint(worker, Game.senseUnitAtLocation(location));
			}
			else if (targetType == 1)
			{
				descoreDeposit(worker, location);
			}
		}
		private void descoreBlueprint(Robot worker, Robot blueprint)
		{
			score -= 200/Pathfinding.pathLength(worker.mapLocation(), location);
		}
	
		private void descoreDeposit(Robot worker, MapLocation deposit)
		{
			score -= 200/Pathfinding.pathLength(worker.mapLocation(), location);
		}
	}
	
	
	private static void handleWorkerTarget(Robot worker, WorkerTarget target)
	{
		if (worker.mapLocation().isAdjacentTo(target.location))
			{
				if (target.targetType == 0 && Game.canBuild(worker, Game.senseUnitAtLocation(target.location)))
				{
					Game.build(worker, Game.senseUnitAtLocation(target.location));
				}
				if (target.targetType == 1 && Game.canHarvest(worker, Pathfinding.path(worker.mapLocation(), target.location)))
				{
					Game.harvest(worker, Pathfinding.path(worker.mapLocation(), target.location));
				}
			}
		else
		{
			if (Game.canMove(worker, Pathfinding.path(worker.mapLocation(), target.location)))
			{
				Game.moveRobot(worker, Pathfinding.path(worker.mapLocation(), target.location));
			}
		}
	}
	
	private static void giveWorkersOrders()
	{
		
		WorkerTarget[] targets = new WorkerTarget[currentBlueprints.size() + GameInfoCache.karboniteDeposits.size()];
		int targetIndex = 0;
		for (Robot structure:currentBlueprints)
		{
			targets[targetIndex] = new WorkerTarget(0, structure.mapLocation(), 0);
			targets[targetIndex++].score(structure);
		}
		for (MapLocation deposit:GameInfoCache.karboniteDeposits)
		{
			targets[targetIndex] = new WorkerTarget(0, deposit, 0);
			targets[targetIndex++].score(deposit);
		}
		WorkerTarget max = new WorkerTarget(-1, null, -1);
		while (idleWorkers.size() > 0)
		{
			for (int count = 0; count < targets.length; count++)
			{
				if (targets[count].score > max.score)
				{
					max = targets[count];
				}
			}
			if (max.score < 0)
			{
				break;
			}
			
			Robot closestWorker = idleWorkers.iterator().next();
			for (Robot worker:idleWorkers)
			{
				if (Pathfinding.pathLength(worker.mapLocation(), max.location) < Pathfinding.pathLength(closestWorker.mapLocation(), max.location))
				{
					closestWorker = worker;
				}
			}
			handleWorkerTarget(closestWorker, max);
			idleWorkers.remove(closestWorker);
			for (WorkerTarget target:targets)
			{
				target.descore(closestWorker);
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
		if (shouldReplicate())
		{
			replicateWorkers();
		}
		
		giveWorkersOrders();
	}
	
	
	
	
}
