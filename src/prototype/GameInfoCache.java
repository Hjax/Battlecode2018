package prototype;

import java.util.*;

import bc.*;

public class GameInfoCache 
{
	public static ArrayList<HashSet<Tile>> karboniteDeposits = new ArrayList<HashSet<Tile>>(Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE);
	public static int[] karboniteDistance = new int[Game.WIDTH * Game.HEIGHT];
	public static int[] nearestKarbonite = new int[Game.WIDTH * Game.HEIGHT];
	public static HashSet<Integer> karboniteLocations = new HashSet<Integer>();
	
	public static int turnsSinceLastEnemy = 0;
	
	private static HashSet<Integer> queuedIndices = new HashSet<Integer>();
	private static LinkedList<Integer> karboniteQueue = new LinkedList<Integer>();
	public static Set<Tile> factoryCache = new HashSet<>();
	static
	{
		for (int x = 0; x < Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE; x++)
		{
			karboniteDeposits.add(new HashSet<Tile>());
		}
		int[] directions = {1, 1 - Constants.QUADRANTROWSIZE, -1 * Constants.QUADRANTROWSIZE, -1 - Constants.QUADRANTROWSIZE, -1, Constants.QUADRANTROWSIZE - 1, Constants.QUADRANTROWSIZE, Constants.QUADRANTROWSIZE + 1};
		Tile checkLocation;
		for (int x = 0; x < Game.startingMap(Game.planet()).getWidth(); x++)
		{
			for (int y = 0; y < Game.startingMap(Game.planet()).getHeight(); y++)
			{
				checkLocation = Tile.getInstance(Game.planet(), x, y);
				if (Game.initialKarboniteAt(checkLocation) > 0)
				{
					int loc = x/Constants.QUADRANTSIZE + y/Constants.QUADRANTSIZE * Constants.QUADRANTROWSIZE;
					karboniteDeposits.get(loc).add(checkLocation);
					for (int dir:directions)
					{
						int test = loc + dir;
						if ((Math.abs(test % Constants.QUADRANTROWSIZE - loc % Constants.QUADRANTROWSIZE) <= 1 && test >= 0 && test < Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE && Game.pathMap[test]))
						{
							karboniteDeposits.get(test).add(checkLocation);
						}
					}
				}
			}
		}
		initDijkstraMap();
	}
	
	private static void initDijkstraMap()
	{
		
		queuedIndices = new HashSet<Integer>();
		karboniteQueue = new LinkedList<Integer>();
		for (int x = 0; x < Game.WIDTH; x++)
		{
			for (int y = 0; y < Game.HEIGHT; y++)
			{
				int index = x + y * Game.WIDTH;
				if (Game.initialKarboniteAt(Tile.getInstance(Game.planet(), x, y)) > 0)
				{
					karboniteDistance[index] = 0;
					nearestKarbonite[index] = index;
					karboniteQueue.add(index);
					queuedIndices.add(index);
					karboniteLocations.add(index);
				}
				else
				{
					karboniteDistance[index] = Constants.INFINITY;
					nearestKarbonite[index] = -1;
				}
				
			}
		}
		updateDijkstraMap();
		
		

	}
	
	private static void updateDijkstraMap()
	{
		int[] directions = {1, 1 - Game.WIDTH, -1 * Game.WIDTH, -1 - Game.WIDTH, -1, Game.WIDTH - 1, Game.WIDTH, Game.WIDTH + 1};
		int infinityTimer = 0;
		while (karboniteQueue.size() > 0)
		{
			if (infinityTimer++ > Game.MAPSIZE)
			{
				System.out.printf("count to infinity\n");
				break;
			}
			int loc = karboniteQueue.poll();
			queuedIndices.remove(loc);
			for (int dir:directions)
			{
				int test = loc + dir;
				if ((Math.abs(test % Game.WIDTH - loc % Game.WIDTH) <= 1 && test >= 0 && test < Game.WIDTH * Game.HEIGHT && Game.pathMap[test]))
				{
					if (karboniteLocations.contains(nearestKarbonite[loc]) && !karboniteLocations.contains(nearestKarbonite[test]))
					{
						nearestKarbonite[test] = nearestKarbonite[loc];
						karboniteDistance[test] = karboniteDistance[loc] + 1;
						if (!queuedIndices.contains(test))
						{
							karboniteQueue.add(test);
							queuedIndices.add(test);
						}
						
					}
					else if (karboniteLocations.contains(nearestKarbonite[test]) && (!karboniteLocations.contains(nearestKarbonite[loc]) || karboniteDistance[test]+1 < karboniteDistance[loc]))
					{
						karboniteDistance[loc] = karboniteDistance[test] + 1;
						nearestKarbonite[loc] = nearestKarbonite[test];
						if (!queuedIndices.contains(loc))
						{
							karboniteQueue.add(loc);
							queuedIndices.add(loc);
						}
					}
				}
			}
		}
	}
	
	public static HashSet<Robot> currentBlueprints = new HashSet<Robot>();
	
	public static ArrayList<Robot> allyWorkers = new ArrayList<Robot>();
	public static ArrayList<Robot> allyKnights = new ArrayList<Robot>();
	public static ArrayList<Robot> allyRangers = new ArrayList<Robot>();
	public static ArrayList<Robot> allyMages = new ArrayList<Robot>();
	public static ArrayList<Robot> allyHealers = new ArrayList<Robot>();
	public static ArrayList<Robot> allyFactories = new ArrayList<Robot>();
	public static ArrayList<Robot> allyRockets = new ArrayList<Robot>();
	
	public static ArrayList<Robot> allyCombat = new ArrayList<Robot>();
	
	public static ArrayList<Robot> enemyWorkers = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyKnights = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyRangers = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyMages = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyHealers = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyFactories = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyRockets = new ArrayList<Robot>();
	
	public static ArrayList<Robot> allWorkers = new ArrayList<Robot>();
	public static ArrayList<Robot> allKnights = new ArrayList<Robot>();
	public static ArrayList<Robot> allRangers = new ArrayList<Robot>();
	public static ArrayList<Robot> allMages = new ArrayList<Robot>();
	public static ArrayList<Robot> allHealers = new ArrayList<Robot>();
	public static ArrayList<Robot> allFactories = new ArrayList<Robot>();
	public static ArrayList<Robot> allRockets = new ArrayList<Robot>();
	
	public static void cleanUpFactories() {
		// idk what senseUnitAtLocation returns, so im checking for null AND error
		List<Tile> toRemove = new ArrayList<>();
		for (Tile f: factoryCache) {
			try {
				if (Game.canSenseLocation(f) && Game.senseUnitAtLocation(f) == null) {
					toRemove.add(f);
				}
			} catch (Exception e) {
				toRemove.add(f);
			}
			
		}
		for (Tile f: toRemove) factoryCache.remove(f);
	}
	
	public static void updateCache()
	{
		cleanUpFactories();
		
		turnsSinceLastEnemy++;
		
		allyWorkers = new ArrayList<Robot>();
		allyKnights = new ArrayList<Robot>();
		allyRangers = new ArrayList<Robot>();
		allyMages = new ArrayList<Robot>();
		allyHealers = new ArrayList<Robot>();
		allyFactories = new ArrayList<Robot>();
		allyRockets = new ArrayList<Robot>();
		
		allyCombat = new ArrayList<Robot>();
		
		enemyWorkers = new ArrayList<Robot>();
		enemyKnights = new ArrayList<Robot>();
		enemyRangers = new ArrayList<Robot>();
		enemyMages = new ArrayList<Robot>();
		enemyHealers = new ArrayList<Robot>();
		enemyFactories = new ArrayList<Robot>();
		enemyRockets = new ArrayList<Robot>();
		
		allWorkers = new ArrayList<Robot>();
		allKnights = new ArrayList<Robot>();
		allRangers = new ArrayList<Robot>();
		allMages = new ArrayList<Robot>();
		allHealers = new ArrayList<Robot>();
		allFactories = new ArrayList<Robot>();
		allRockets = new ArrayList<Robot>();
		
		currentBlueprints = new HashSet<Robot>();
		
		updateType(UnitType.Worker, allyWorkers, enemyWorkers, allWorkers);
		updateType(UnitType.Knight, allyKnights, enemyKnights, allKnights);
		updateType(UnitType.Ranger, allyRangers, enemyRangers, allRangers);
		updateType(UnitType.Mage, allyMages, enemyMages, allMages);
		updateType(UnitType.Healer, allyHealers, enemyHealers, allHealers);
		updateType(UnitType.Factory, allyFactories, enemyFactories, allFactories);
		updateType(UnitType.Rocket, allyRockets, enemyRockets, allRockets);
		queuedIndices = new HashSet<Integer>();
		karboniteQueue = new LinkedList<Integer>();
		HashSet<Tile> depletedDeposits = new HashSet<Tile>();
		
		for (HashSet<Tile> quadrant:karboniteDeposits)
		{
			for (Tile deposit: quadrant)
			{
				if (Game.canSenseLocation(deposit))
				{
					if (Game.karboniteAt(deposit) == 0)
					{
						depletedDeposits.add(deposit);
					}
				}
			}
			for (Tile deposit: depletedDeposits)
			{
				quadrant.remove(deposit);
				
			}
		}
		for (Integer deposit:karboniteLocations)
		{
			Tile location = Tile.getInstance(Game.planet(), deposit % Game.WIDTH, deposit / Game.WIDTH);
			if (Game.canSenseLocation(location) && Game.karboniteAt(location) == 0)
			{
				karboniteQueue.add(deposit);
				queuedIndices.add(deposit);
			}
			
		}
		for (Integer deposit:queuedIndices)
		{
			karboniteLocations.remove(deposit);
		}
		if (queuedIndices.size() > 0)
		{
			for (int x = 0; x < Game.WIDTH * Game.HEIGHT; x++)
			{
				if (nearestKarbonite[x] != -1 && !karboniteLocations.contains(nearestKarbonite[x]))
				{
					karboniteQueue.add(x);
					queuedIndices.add(x);
				}
			}
		}
		
		AsteroidStrike asteroid = null;
		if (Game.ASTEROIDPATTERN.hasAsteroid(Game.round()))
		{
			asteroid = Game.ASTEROIDPATTERN.asteroid(Game.round());
			if (Game.PLANET == Planet.Mars)
			{
				int loc = asteroid.getLocation().getX() + asteroid.getLocation().getY() * Game.WIDTH;
				karboniteLocations.add(loc);
				karboniteDistance[loc] = 0;
				nearestKarbonite[loc] = loc;
				queuedIndices.add(loc);
				karboniteQueue.add(loc);
			}
		}
		
		if (karboniteLocations.size() > 0)
		{
			updateDijkstraMap();
		}
		
		if (Game.planet() == Planet.Earth && allyWorkers.size() + allyFactories.size() == 0)
		{
			Game.writeTeamArray(0, 1);
		}
		if (Game.planet() == Planet.Mars) {
			Game.writeTeamArray(1, allyWorkers.size());
		}
		
	}
	
	private static void updateType(UnitType type, ArrayList<Robot> allyCache, ArrayList<Robot> enemyCache, ArrayList<Robot> allCache)
	{
		for (Robot bot:Game.senseNearbyUnits(type))
			{
				allCache.add(bot);
				if (bot.team() == Game.TEAM && ( type == UnitType.Rocket || type == UnitType.Factory) && bot.structureIsBuilt() != 1)
				{
					currentBlueprints.add(bot);
				}
				if (bot.team() == Game.enemy() && (type == UnitType.Factory)) {
					factoryCache.add(bot.tile());
				}
				if (bot.team() == Game.enemy()) {
					turnsSinceLastEnemy = 0;
				}
				if (bot.team() == Game.team())
				{
					allyCache.add(bot);
					if (bot.unitType() != UnitType.Worker && bot.unitType() != UnitType.Factory && bot.unitType() != UnitType.Rocket) {
						allyCombat.add(bot);
					}
				}
				else
				{
					enemyCache.add(bot);
				}
				
			}
	}
	
}
