package workerRush;

public class Constants {
	
	public static final Tile[] startingRobotLocation = Game.getInitialUnits();
	public static final Tile[] startingAlliesLocation;
	public static final Tile[] startingEnemiesLocation;

	static
	{
		int allyIndex = 0;
		int enemyIndex = 0;
		for (Tile worker:startingRobotLocation)
		{
			if (Game.hasUnitAtLocation(worker))
			{
				allyIndex++;
			}
			else
			{
				enemyIndex++;
			}
		}
		startingAlliesLocation = new Tile[allyIndex];
		startingEnemiesLocation = new Tile[enemyIndex];
		allyIndex = 0;
		enemyIndex = 0;
		for (Tile worker:startingRobotLocation)
		{
			if (Game.hasUnitAtLocation(worker))
			{
				startingAlliesLocation[allyIndex++] = worker;
			}
			else
			{
				startingEnemiesLocation[enemyIndex++] = worker;
			}
		}
	}

	public static final int INFINITY = 99999999;
	public static final int RUSHTHRESHOLD = 25;
	public static final int WORKERLIMIT = 4;
	public static int WORKERLIMITWEIGHT = 5;
	public static int WORKERREPLICATEDEPOSITWEIGHT = 20;
	public static final int FACTORYLIMIT = 9;
	public static final int FACTORYGOAL = 4;
	public static int FACTORYBUILDRANGE = 3;
	public static int FACTORYREPLICATEPRESSURE = 120;
	public static final int ROCKETBUILDRANGE = 50;
	public static final int COMBATLIMIT = 50;
	public static final int FACTORYHALTROUND = 700; 
	// if we are below this amount of time, skip turn
	public static final int CLOCKBUFFER = 200;
	public static final int MARSWORKERGOAL = 3;
	
	public static final int REPLICATECOST = 60;
	public static final int WORKERCOST = 50;
	public static final int RANGERCOST = 40;
	public static final int KNIGHTCOST = 40;
	public static final int MAGECOST = 40;
	public static final int HEALERCOST = 40;
	public static final int RANGERRANGE = 50;
	public static final int HEALERRANGE = 30;
	
	
	public static final int QUADRANTSIZE = 4;
	public static final int QUADRANTROWSIZE = (int) Math.ceil(Game.WIDTH / (double) QUADRANTSIZE);
	public static final int QUADRANTCOLUMNSIZE = (int) Math.ceil(Game.HEIGHT / (double) QUADRANTSIZE);
	public static final int RANGERDAMAGE = 30;
	public static final int ROCKETBUILDLIMIT = 4;
	
}
