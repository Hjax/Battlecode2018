package first;

import bc.*;

public class Worker 
{
	
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
		WorkerDistanceTuple closeWorker = new WorkerDistanceTuple(-1, null);
		WorkerDistanceTuple farWorker = new WorkerDistanceTuple(Constants.INFINITY, null);
		
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
				closeWorker.rushDistance = max;
				closeWorker.worker = Constants.startingAllies[ally];
			}
			if (min > farWorker.rushDistance)
			{
				
				farWorker.rushDistance = min;
				farWorker.worker = Constants.startingAllies[ally];
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
		WorkerDistanceTuple bestWorker = bestWorker();
		WorkerDistanceTuple nearestEnemy = new WorkerDistanceTuple(Constants.INFINITY, null);

		
	}
}
