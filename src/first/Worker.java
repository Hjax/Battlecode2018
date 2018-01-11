package first;

import java.util.HashSet;

import bc.*;

public class Worker 
{
	
	private static HashSet<Robot> currentBlueprints = new HashSet<Robot>();
	
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
			currentBlueprints.add(Game.senseNearbyUnits(Utilities.offsetInDirection(bestWorker.worker.mapLocation(), buildDir, 1), UnitType.Factory)[0]);
		}
	}
	
	
	
	public static void run() 
	{
		if (Game.planet() == Planet.Earth)
		{
			buildOrder();
		}
		
	}
}
