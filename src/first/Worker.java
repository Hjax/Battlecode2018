package first;

import bc.*;

public class Worker 
{
	
	private static class workerDistanceTuple
	{
		public int rushDistance;
		public Unit worker;
		
		workerDistanceTuple(int dist, Unit work)
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
		
		Unit[] startingUnits = game.getInitialUnits();
		Unit[] startingAllies = new Unit[startingUnits.length/2];
		Unit[] startingEnemies = new Unit[startingAllies.length];
		
		for (Unit ally:startingAllies)
		{
			for (Unit enemy:startingEnemies)
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
				closeWorker = new workerDistanceTuple(max, ally);
			}
			if (min > farWorker.rushDistance)
			{
				farWorker = new workerDistanceTuple(min, ally);
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
