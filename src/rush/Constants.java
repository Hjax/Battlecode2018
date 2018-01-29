package rush;

import bc.Planet;

public class Constants {
	
	public static final Tile[] startingRobotLocation = Game.getInitialUnits();
	public static final Tile[] startingAlliesLocation;
	public static final Tile[] startingEnemiesLocation;

	static
	{
		if (Game.PLANET == Planet.Earth)
		{
			int allyIndex = 0;
			int enemyIndex = 0;
			startingAlliesLocation = new Tile[startingRobotLocation.length / 2];
			startingEnemiesLocation = new Tile[startingRobotLocation.length / 2];
			for (Tile worker:startingRobotLocation)
			{
				if (Game.hasUnitAtLocation(worker))
				{
					if (Game.senseUnitAtLocation(worker).team() == Game.TEAM)
					{
						startingAlliesLocation[allyIndex++] = worker;
					}
					else
					{
						startingEnemiesLocation[enemyIndex++] = worker;
					}
				}
				else
				{
					startingEnemiesLocation[enemyIndex++] = worker;
				}
			}
		}
		else
		{
			startingAlliesLocation = new Tile[0];
			startingEnemiesLocation = new Tile[0];
		}
	}

	public static final int INFINITY = 99999999;
	public static final int RUSHTHRESHOLD = 20;
	public static int RUSHDISTANCE = INFINITY;
	
	public static int WORKERLIMIT = 4;
	public static int WORKERLIMITWEIGHT = 3;
	public static int WORKERHARDCAP = 50;
	public static int WORKERREPLICATEDEPOSITWEIGHT = 10;
	public static final int FACTORYLIMIT = 9;
	public static final int FACTORYGOAL = 4;
	public static int FACTORYBUILDRANGE = 3;
	public static int FACTORYREPLICATEPRESSURE = 300;
	public static final int ROCKETBUILDRANGE = 50;
	public static final int FACTORYHALTROUND = 700;
	public static final int TIMELOW = 1000;
	
	public static final int AGGRESSIVEHARVESTTIMER = 50;
	public static final int HARVESTABORTROUND = 300;
	
	// if we are below this amount of time, skip turn
	public static final int CLOCKBUFFER = 600;
	public static final int MARSWORKERGOAL = 3;
	
	public static final int REPLICATECOST = 60;
	public static final int WORKERCOST = 50;
	public static final int RANGERCOST = 40;
	public static final int KNIGHTCOST = 40;
	public static final int MAGECOST = 40;
	public static final int HEALERCOST = 40;
	public static final int RANGERRANGE = 50;
	public static final int HEALERRANGE = 30;
	
	public static final int EMERGENCYCLOCKBUFFER = 200;
	
	
	public static final int QUADRANTSIZE = 2;
	public static final int QUADRANTROWSIZE = (int) Math.ceil(Game.WIDTH / (double) QUADRANTSIZE);
	public static final int QUADRANTCOLUMNSIZE = (int) Math.ceil(Game.HEIGHT / (double) QUADRANTSIZE);
	public static final int RANGERDAMAGE = 30;
	public static final int ROCKETBUILDLIMIT = 4;
	public static final long RANGERVISION = 70;
	
}
