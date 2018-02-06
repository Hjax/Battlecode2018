package densityBot;

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
	
	public static int WORKERLIMIT = 4;
	public static int WORKERLIMITWEIGHT = 3;
	public static int WORKERHARDCAP = 50;
	public static int WORKERREPLICATEDEPOSITWEIGHT = 3;
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
	
	public static final double DENSITYSMOOTHER = 1.3;
	
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
	
	public static double powerSwitch(int power)
	{
		switch (power)
		{
		case 0:
			return 1.000000;
		case 1:
			return 1.450000;
		case 2:
			return 2.102500;
		case 3:
			return 3.048625;
		case 4:
			return 4.420506;
		case 5:
			return 6.409734;
		case 6:
			return 9.294114;
		case 7:
			return 13.476466;
		case 8:
			return 19.540876;
		case 9:
			return 28.334269;
		case 10:
			return 41.084691;
		case 11:
			return 59.572802;
		case 12:
			return 86.380562;
		case 13:
			return 125.251815;
		case 14:
			return 181.615132;
		case 15:
			return 263.341942;
		case 16:
			return 381.845816;
		case 17:
			return 553.676433;
		case 18:
			return 802.830827;
		case 19:
			return 1164.104699;
		case 20:
			return 1687.951814;
		case 21:
			return 2447.530131;
		case 22:
			return 3548.918689;
		case 23:
			return 5145.932100;
		case 24:
			return 7461.601544;
		case 25:
			return 10819.322239;
		case 26:
			return 15688.017247;
		case 27:
			return 22747.625008;
		case 28:
			return 32984.056262;
		case 29:
			return 47826.881579;
		case 30:
			return 69348.978290;
		case 31:
			return 100556.018521;
		case 32:
			return 145806.226855;
		case 33:
			return 211419.028939;
		case 34:
			return 306557.591962;
		case 35:
			return 444508.508345;
		case 36:
			return 644537.337101;
		case 37:
			return 934579.138796;
		case 38:
			return 1355139.751254;
		case 39:
			return 1964952.639318;
		case 40:
			return 2849181.327011;
		case 41:
			return 4131312.924166;
		case 42:
			return 5990403.740041;
		case 43:
			return 8686085.423060;
		case 44:
			return 12594823.863436;
		case 45:
			return 18262494.601983;
		case 46:
			return 26480617.172875;
		case 47:
			return 38396894.900669;
		case 48:
			return 55675497.605970;
		case 49:
			return 80729471.528656;
		case 50:
			return 117057733.716551;
		case 51:
			return 169733713.889000;
		case 52:
			return 246113885.139049;
		case 53:
			return 356865133.451622;
		case 54:
			return 517454443.504851;
		case 55:
			return 750308943.082035;
		case 56:
			return 1087947967.468950;
		case 57:
			return 1577524552.829978;
		case 58:
			return 2287410601.603467;
		case 59:
			return 3316745372.325028;
		case 60:
			return 4809280789.871290;
		case 61:
			return 6973457145.313371;
		case 62:
			return 10111512860.704388;
		case 63:
			return 14661693648.021360;
		case 64:
			return 21259455789.630974;
		case 65:
			return 30826210894.964909;
		case 66:
			return 44698005797.699120;
		case 67:
			return 64812108406.663719;
		case 68:
			return 93977557189.662384;
		case 69:
			return 136267457925.010452;
		case 70:
			return 197587813991.265167;
		case 71:
			return 286502330287.334473;
		case 72:
			return 415428378916.634949;
		case 73:
			return 602371149429.120728;
		case 74:
			return 873438166672.224976;
		case 75:
			return 1266485341674.726074;
		case 76:
			return 1836403745428.352783;
		case 77:
			return 2662785430871.111328;
		case 78:
			return 3861038874763.111816;
		case 79:
			return 5598506368406.511719;
		case 80:
			return 8117834234189.441406;
		case 81:
			return 11770859639574.689453;
		case 82:
			return 17067746477383.300781;
		case 83:
			return 24748232392205.785156;
		case 84:
			return 35884936968698.382812;
		case 85:
			return 52033158604612.656250;
		case 86:
			return 75448079976688.359375;
		case 87:
			return 109399715966198.109375;
		case 88:
			return 158629588150987.250000;
		case 89:
			return 230012902818931.500000;
		case 90:
			return 333518709087450.687500;
		case 91:
			return 483602128176803.437500;
		case 92:
			return 701223085856365.000000;
		case 93:
			return 1016773474491729.250000;
		case 94:
			return 1474321538013007.250000;
		case 95:
			return 2137766230118860.500000;
		case 96:
			return 3099761033672347.500000;
		case 97:
			return 4494653498824904.000000;
		case 98:
			return 6517247573296111.000000;
		case 99:
			return 9450008981279360.000000;
		default:
			return Constants.INFINITY;
		}
	}
	
}
