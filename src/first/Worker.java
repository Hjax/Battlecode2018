package first;

import bc.*;

public class Worker 
{
	
	private static class workerDistanceTuple
	{
		public int rushDistance;
		public Robot worker;
		
		workerDistanceTuple(int dist, Robot work)
		{
			rushDistance = dist;
			worker = work;
		}
	}
	
	private static workerDistanceTuple bestWorker()
	{
		int min = Constants.INFINITY;
		int max = -1;
		int pathDistance = -1;
		workerDistanceTuple closeWorker = new workerDistanceTuple(-1, null);
		workerDistanceTuple farWorker = new workerDistanceTuple(Constants.INFINITY, null);
		
		Robot[] startingRobots = Game.getInitialUnits();
		Robot[] startingAllies = new Robot[startingRobots.length/2];
		Robot[] startingEnemies = new Robot[startingAllies.length];
		
		for (Robot ally:startingAllies)
		{
			for (Robot enemy:startingEnemies)
			{
				pathDistance = Pathfinding.pathLength(ally.location().mapLocation(), enemy.location().mapLocation());
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
				closeWorker.rushDistance = max;
				closeWorker.worker = ally;
			}
			if (min > farWorker.rushDistance)
			{
				
				farWorker.rushDistance = min;
				farWorker.worker = ally;
			}
		}
		if (closeWorker.rushDistance < Constants.RUSHTHRESHOLD)
		{
			return closeWorker;
		}
		return farWorker;
	}
	
	
	public static void run() 
	{

	}
}
