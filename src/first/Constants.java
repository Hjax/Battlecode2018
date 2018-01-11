package first;

import bc.*;

public class Constants {
	
	public static final Robot[] startingRobots = Game.getInitialUnits();
	public static final Robot[] startingAllies = new Robot[startingRobots.length/2];
	public static final Robot[] startingEnemies = new Robot[startingAllies.length];
	
	public static final MapLocation[] startingRobotLocation = new MapLocation[startingRobots.length];
	public static final MapLocation[] startingAlliesLocation = new MapLocation[startingAllies.length];
	public static final MapLocation[] startingEnemiesLocation = new MapLocation[startingEnemies.length];
	
	static
	{
		int allyIndex = 0;
		int enemyIndex = 0;
		for (Robot worker:startingRobots)
		{
			startingRobotLocation[allyIndex+enemyIndex] = worker.mapLocation();
			if (worker.team() == Game.team())
			{
				startingAlliesLocation[allyIndex] = worker.mapLocation();
				startingAllies[allyIndex++] = worker;
			}
			else
			{
				startingEnemiesLocation[enemyIndex] = worker.mapLocation();
				startingEnemies[enemyIndex++] = worker;
			}
		}
		
	}

	public static final int INFINITY = 99999999;
	public static final int RUSHTHRESHOLD = 10;
	public static final int WORKERLIMIT = 20;
	
}
