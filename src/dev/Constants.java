package dev;

import bc.*;

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
			System.out.println(startingRobotLocation.length);
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
	
	public static final int KNIGHTDISTANCE = 12;
	public static final int MAGERUSHDISTANCE = 6;
	
	public static final int TARGETFACTORYTRESHOLD = 15;
	
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
	
	public static final int EMERGENCYCLOCKBUFFER = 200;
	public static final int RANGERMINRAGE = 10;
	
	
	public static final int QUADRANTSIZE = 2;
	public static final int QUADRANTROWSIZE = (int) Math.ceil(Game.WIDTH / (double) QUADRANTSIZE);
	public static final int QUADRANTCOLUMNSIZE = (int) Math.ceil(Game.HEIGHT / (double) QUADRANTSIZE);
	public static final int RANGERDAMAGE = 30;
	public static final int ROCKETBUILDLIMIT = 4;
	
	public static final int ROCKETMAXCAPACITY = 6;
	
	public static final int attackRange(UnitType u) {
		switch (u) {
			case Ranger:
				return 50;
			case Knight: 
				return 2;
			case Mage:
				return 30;
			case Healer:
				return 30;
			default:
				return 0;
		}
	}
	
	public static final int abilityRange(UnitType u) {
		switch (u) {
			case Ranger:
				return INFINITY;
			case Knight: 
				return 10;
			case Mage:
				return 8;
			case Healer:
				return 30;
			default:
				return 0;
		}
	}
	
	public static final int attackDamage(UnitType  u) {
		switch (u) {
			case Ranger:
				return 30;
			case Knight: 
				return 80;
			case Mage:
				return 60; // todo account for upgrades
			case Healer:
				return -10;
			default:
				return 0;
		}
	}
	
	public static final int visionRange(UnitType  u) {
		switch (u) {
			case Ranger:
				return 70; // todo account for upgrades
			case Knight: 
				return 50;
			case Mage:
				return 30;
			case Healer:
				return 50;
			default:
				return 50;
		}
	}
	
	public static final int movementCooldown(UnitType u) {
		switch (u) {
			case Ranger:
				return 30;// todo account for upgrades
			case Knight: 
				return 15;
			case Mage:
				return 20; 
			case Healer:
				return 25;
			case Worker:
				return 20;
			default:
				return 0;
		}
	}
	
	public static final int attackCooldown(UnitType u) {
		switch (u) {
			case Ranger:
				return 20;
			case Knight: 
				return 20;
			case Mage:
				return 20;
			case Healer:
				return 10;
			case Worker:
				return 100;
			default:
				return 0;
		}
	}
	
	public static final int maxHealth(UnitType u) {
		switch (u) {
			case Ranger:
				return 200;
			case Knight: 
				return 250;
			case Mage:
				return 80;
			case Healer:
				return 100;
			case Worker:
				return 100;
			case Factory:
				return 300;
			case Rocket:
				return 200;
			default:
				return 0;
		}
	}
	
	public static final int cost(UnitType u) {
		switch (u) {
			case Worker:
				return 50;
			case Factory:
				return 300;
			case Rocket:
				return 200;
			default:
				return 40;
		}
	}
	
	public static final int abilityCooldown(UnitType u) {
		switch (u) {
			case Worker:
				return 500;
			case Knight:
				return 100;
			case Healer:
				return 100;
			case Mage:
				return 250;
			case Ranger:
				return 200;
			default:
				return 0;
		}
	}
}
