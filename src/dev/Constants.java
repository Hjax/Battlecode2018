package dev;

import bc.*;

public class Constants {
	
	public static final Robot[] startingRobots = Game.getInitialUnits();
	public static final Robot[] startingAllies = new Robot[startingRobots.length/2];
	public static final Robot[] startingEnemies = new Robot[startingAllies.length];
	
	public static final Tile[] startingRobotLocation = new Tile[startingRobots.length];
	public static final Tile[] startingAlliesLocation = new Tile[startingAllies.length];
	public static final Tile[] startingEnemiesLocation = new Tile[startingEnemies.length];

	static
	{
		int allyIndex = 0;
		int enemyIndex = 0;
		for (Robot worker:startingRobots)
		{
			startingRobotLocation[allyIndex+enemyIndex] = worker.tile();
			if (worker.team() == Game.team())
			{
				startingAlliesLocation[allyIndex] = worker.tile();
				startingAllies[allyIndex++] = worker;
			}
			else
			{
				startingEnemiesLocation[enemyIndex] = worker.tile();
				startingEnemies[enemyIndex++] = worker;
			}
		}
		
	}

	public static final int INFINITY = 99999999;
	public static final int RUSHTHRESHOLD = 10;
	public static final int WORKERLIMIT = 20;
	public static final int BUILDRANGE = 5;
	
	public static final int WIDTH = (int) Game.startingMap(Game.planet()).getWidth();
	public static final int HEIGHT = (int) Game.startingMap(Game.planet()).getHeight();
	
	public static final int QUADRANTSIZE = 4;
	public static final int QUADRANTROWSIZE = (int) Math.ceil(WIDTH / (double) QUADRANTSIZE);
	public static final int QUADRANTCOLUMNSIZE = (int) Math.ceil(HEIGHT / (double) QUADRANTSIZE);
	
}
