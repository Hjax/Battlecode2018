package dev;

import bc.*;
import java.util.*;

public class Game {
	public static GameController gc;
	public static Direction[] directions;
	public static Direction[] moveDirections;
	public static Set<Tile> passable = new HashSet<>();
	public static Set<Tile> passableOther = new HashSet<>();
	public static boolean[] pathMap;
	
	public static final Planet PLANET;
	public static final Planet OTHERPLANET;
	public static final int WIDTH;
	public static final int HEIGHT;
	public static final int MAPSIZE;
	public static final PlanetMap STARTINGMAP;
	public static final PlanetMap STARTINGMAPOTHER;
	public static final AsteroidPattern ASTEROIDPATTERN;
	public static final OrbitPattern ORBITPATTERN;
	public static final int INFINITY = 99999999;
	public static final Team TEAM;
	public static final Team ENEMY;
	public static int round = 1;
	public static Random rand = new Random();
	
	public static ArrayList<HashSet<Tile>> karboniteDeposits = new ArrayList<HashSet<Tile>>(Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE);
	
	static {
        gc = new GameController();
        TEAM = gc.team();
        if (TEAM == Team.Blue) {
        	ENEMY = Team.Red;
        } else {
        	ENEMY = Team.Blue;
        }
        directions = Direction.values();
        moveDirections = new Direction[8];
        int i = 0;
        for (Direction d: directions) {
        	if (!d.equals(Direction.Center)) {
        		moveDirections[i++] = d;
        	}
        }
        STARTINGMAP = gc.startingMap(gc.planet());
        PLANET = gc.planet();
        if (PLANET == Planet.Earth)
        {
        	OTHERPLANET = Planet.Mars;
        	STARTINGMAPOTHER = gc.startingMap(Planet.Mars);
        }
        else
        {
        	OTHERPLANET = Planet.Earth;
        	STARTINGMAPOTHER = gc.startingMap(Planet.Earth);
        }
        WIDTH = (int) STARTINGMAP.getWidth();
        HEIGHT = (int) STARTINGMAP.getHeight();
        MAPSIZE = WIDTH * HEIGHT;
        ASTEROIDPATTERN =  gc.asteroidPattern();
        ORBITPATTERN = gc.orbitPattern();
        pathMap = new boolean[(int) (WIDTH * HEIGHT)];
        for (int x = 0; x < WIDTH; x++) {
        	for (int y = 0; y < HEIGHT; y++) {
            	if (STARTINGMAP.isPassableTerrainAt(new MapLocation(PLANET, x, y)) > 0) {
                	passable.add(Tile.getInstance(PLANET, x, y));
                	pathMap[x + WIDTH * y] = true;
                } else {
                	pathMap[x + WIDTH * y] = false;
                }
            }
        }
        
        for (int x = 0; x < STARTINGMAPOTHER.getWidth(); x++) {
        	for (int y = 0; y < STARTINGMAPOTHER.getHeight(); y++) {
            	if (STARTINGMAPOTHER.isPassableTerrainAt(new MapLocation(OTHERPLANET, x, y)) > 0) {
                	passableOther.add(Tile.getInstance(OTHERPLANET, x, y));
                }
            }
        }
        
		for (int x = 0; x < Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE; x++)
		{
			karboniteDeposits.add(new HashSet<Tile>());
		}
		int[] directions = {1, 1 - Constants.QUADRANTROWSIZE, -1 * Constants.QUADRANTROWSIZE, -1 - Constants.QUADRANTROWSIZE, -1, Constants.QUADRANTROWSIZE - 1, Constants.QUADRANTROWSIZE, Constants.QUADRANTROWSIZE + 1};
		Tile checkLocation;
		for (int x = 0; x < startingMap(planet()).getWidth(); x++)
		{
			for (int y = 0; y < startingMap(planet()).getHeight(); y++)
			{
				checkLocation = Tile.getInstance(planet(), x, y);
				if (initialKarboniteAt(checkLocation) > 0)
				{
					int loc = x/Constants.QUADRANTSIZE + y/Constants.QUADRANTSIZE * Constants.QUADRANTROWSIZE;
					karboniteDeposits.get(loc).add(checkLocation);
					for (int dir:directions)
					{
						int test = loc + dir;
						if ((Math.abs(test % Constants.QUADRANTROWSIZE - loc % Constants.QUADRANTROWSIZE) <= 1 && test >= 0 && test < Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE && pathMap[test]))
						{
							karboniteDeposits.get(test).add(checkLocation);
						}
					}
				}
			}
		}
		initDijkstraMap();
    }
	
	public static Set<Tile> factoryCache = new HashSet<>();
	public static int[] karboniteDistance = new int[WIDTH * HEIGHT];
	static HashSet<Integer> queuedIndices = new HashSet<Integer>();
	static LinkedList<Integer> karboniteQueue = new LinkedList<Integer>();
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
	public static ArrayList<Robot> allRobots = new ArrayList<Robot>();
	public static ArrayList<Robot> allAllies = new ArrayList<Robot>();
	public static ArrayList<Robot> allEnemies = new ArrayList<Robot>();
	
	public static int turnsSinceLastEnemy = 0;
	public static HashSet<Integer> karboniteLocations = new HashSet<Integer>();
	public static int[] nearestKarbonite = new int[WIDTH * HEIGHT];
	
	public static void startTurn() 
	{
		Timing.reset();
		updateCache();
		Rocket.startTurn();
		Micro.startTurn();
		GlobalStrategy.run();
	}
	
	public static AsteroidPattern asteroidPattern() {
		return ASTEROIDPATTERN;
	}
	
	public static boolean canSenseLocation(Tile location) {
		return gc.canSenseLocation(location.location);
	}
	
	public static boolean canSenseUnit(Robot unit) {
		return gc.canSenseUnit(unit.id());
	}
	
	public static long currentDurationOfFlight() {
		return gc.currentDurationOfFlight();
	}
	
	public static Veci32 getTeamArray(Planet planet) {
		return gc.getTeamArray(planet);
	}
	
	public static boolean hasUnitAtLocation(Tile location) {
		return gc.hasUnitAtLocation(location.location);
	}
	
	public static short isOccupiable(Tile location) {
		return gc.isOccupiable(location.location);
	}
	
	public static boolean isOver() {
		return gc.isOver();
	}
	
	public static long karbonite() {
		return gc.karbonite();
	}
	
	public static long karboniteAt(Tile location) {
		return gc.karboniteAt(location.location);
	}
	
	public static void nextTurn() {
		round++;
		gc.nextTurn();
	}
	
	public static OrbitPattern orbitPattern() {
		return ORBITPATTERN;
	}
	
	public static Planet planet() {
		return PLANET;
	}
	
	public static short queueResearch(UnitType branch) {
		return gc.queueResearch(branch);
	}
	
	public static ResearchInfo researchInfo() {
		return gc.researchInfo();
	}
	
	public static short resetResearch() {
		return gc.resetResearch();
	}
	
	public static RocketLandingInfo rocketLandings() {
		return gc.rocketLandings();
	}
	
	public static long round() {
		return round;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius) {
		VecUnit result = gc.senseNearbyUnits(location.location, radius);
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
		free(result);
		return units;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius, Team team) {
		VecUnit result = gc.senseNearbyUnitsByTeam(location.location, radius, team);
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
		free(result);
		return units;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius, UnitType type) {
		VecUnit result = gc.senseNearbyUnitsByType(location.location, radius, type);
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
		free(result);
		return units;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius, UnitType type, Team team) {
		VecUnit result =  gc.senseNearbyUnitsByType(location.location, radius, type);
		List<Robot> units = new ArrayList<Robot>(); 
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).team().equals(team)) {
				units.add(Robot.getInstance(result.get(i)));
			}
		}
		free(result);
		return units.toArray(new Robot[0]);
	}
	
	public static Robot[] senseNearbyUnits(UnitType type, Team team) {
		return senseNearbyUnits(Tile.getInstance(planet(), 0, 0), INFINITY, type, team);
	}
	
	public static Robot[] senseNearbyUnits(Team team) {
		return senseNearbyUnits(Tile.getInstance(planet(), 0, 0), INFINITY, team);
	}
	
	public static Robot[] senseNearbyUnits(UnitType type) {
		return senseNearbyUnits(Tile.getInstance(planet(), 0, 0), INFINITY, type);
	}
	
	public static Robot[] senseNearbyUnits() {
		return senseNearbyUnits(Tile.getInstance(planet(), 0, 0), INFINITY);
	}
	
	public static Robot senseUnitAtLocation(Tile location) {
		return Robot.getInstance(gc.senseUnitAtLocation(location.location));
	}
	
	public static Robot[] senseCombatUnits(Tile location, long radius, Team team) {
		VecUnit result =  gc.senseNearbyUnitsByTeam(location.location, radius, team);
		List<Robot> units = new ArrayList<Robot>(); 
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).unitType() != UnitType.Worker && result.get(i).unitType() != UnitType.Factory && result.get(i).unitType() != UnitType.Rocket) {
				units.add(Robot.getInstance(result.get(i)));
			}
		}
		free(result);
		return units.toArray(new Robot[0]);
	}
	
	public static Robot[] senseCombatUnits(Team team) {
		return senseCombatUnits(Tile.getInstance(planet(), 0, 0), INFINITY, team);
	}
	
	public static PlanetMap startingMap(Planet planet) {
		if (planet == PLANET)
		{
			return STARTINGMAP;
		}
		return STARTINGMAPOTHER;
	}
	
	public static Team team() {
		return TEAM;
	}
	
	public static Team enemy() {
		return ENEMY;
	}
	
	public static boolean canAffordRobot(UnitType r) {
		return gc.karbonite() >= Constants.cost(r);
	}
	
	public static Robot unit(int id) {
		return Robot.getInstance(gc.unit(id));
	}
	
	public static Robot[] units() {
		VecUnit result = gc.units();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
		free(result);
		return units;
	}
	
	public static Robot[] unitsInSpace() {
		VecUnit result = gc.unitsInSpace();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
		free(result);
		return units;
	}
	
	
	public static Team winningTeam() {
		return gc.winningTeam();
	}
	
	public static void writeTeamArray(long index, int value) {
		gc.writeTeamArray(index, value);
	}
	
	public static boolean onMap(Tile loc, Planet p) {
		return startingMap(p).onMap(loc.location);
	}
	
	public static long initialKarboniteAt(Tile loc) {
		return startingMap(loc.location.getPlanet()).initialKarboniteAt(loc.location);
	}
	
	public static boolean isPassableTerrainAt(Tile loc) {
		if (loc.getPlanet() == PLANET)
		{
			return passable.contains(loc);
		}
		else
		{
			return passableOther.contains(loc);
		}
		
	}
	
	public static Tile[] getInitialUnits() {
		VecUnit result = startingMap(Planet.Earth).getInitial_units();
		Tile[] units = new Tile[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Tile.getInstance(result.get(i).location().mapLocation());
		}
		free(result);
		return units;
	}
	
	public static Tile getRandomLocation() {
		int x = 0;
		int y = 0;
		do {
			 x = rand.nextInt((int) startingMap(planet()).getWidth());
			 y = rand.nextInt((int) startingMap(planet()).getHeight());
		} while (!isPassableTerrainAt(Tile.getInstance(new MapLocation(planet(), x, y))));
		return Tile.getInstance(new MapLocation(planet(), x, y));
	}
	
	public static Tile getRandomLocation(Planet plnt) {
		int x = 0;
		int y = 0;
		do {
			 x = rand.nextInt((int) startingMap(plnt).getWidth());
			 y = rand.nextInt((int) startingMap(plnt).getHeight());
		} while (!isPassableTerrainAt(Tile.getInstance(new MapLocation(plnt, x, y))));
		return Tile.getInstance(new MapLocation(plnt, x, y));
	}
	
	public static void free(VecUnit u) {
		for (int i = 0; i < u.size(); i++) {
			u.get(i).delete();
		}
		u.delete();
	}

	

	static void updateDijkstraMap()
	{
		int[] directions = {1, 1 - WIDTH, -1 * WIDTH, -1 - WIDTH, -1, WIDTH - 1, WIDTH, WIDTH + 1};
		int infinityTimer = 0;
		while (karboniteQueue.size() > 0)
		{
			if (infinityTimer++ > MAPSIZE)
			{
				System.out.printf("count to infinity\n");
				break;
			}
			int loc = karboniteQueue.poll();
			queuedIndices.remove(loc);
			for (int dir:directions)
			{
				int test = loc + dir;
				if ((Math.abs(test % WIDTH - loc % WIDTH) <= 1 && test >= 0 && test < WIDTH * HEIGHT && pathMap[test]))
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

	static void initDijkstraMap()
	{
		
		queuedIndices = new HashSet<Integer>();
		karboniteQueue = new LinkedList<Integer>();
		for (int x = 0; x < WIDTH; x++)
		{
			for (int y = 0; y < HEIGHT; y++)
			{
				int index = x + y * WIDTH;
				if (initialKarboniteAt(Tile.getInstance(planet(), x, y)) > 0)
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

	public static void cleanUpFactories() {
		// idk what senseUnitAtLocation returns, so im checking for null AND error
		List<Tile> toRemove = new ArrayList<>();
		for (Tile f: factoryCache) {
			try {
				if (canSenseLocation(f) && senseUnitAtLocation(f) == null) {
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
		allRobots = new ArrayList<Robot>();
		allAllies = new ArrayList<Robot>();
		allEnemies = new ArrayList<Robot>();
		currentBlueprints = new HashSet<Robot>();
		
		updateUnitTypes();
		
	
		queuedIndices = new HashSet<Integer>();
		karboniteQueue = new LinkedList<Integer>();
		HashSet<Tile> depletedDeposits = new HashSet<Tile>();
		
		for (HashSet<Tile> quadrant:karboniteDeposits)
		{
			for (Tile deposit: quadrant)
			{
				if (canSenseLocation(deposit))
				{
					if (karboniteAt(deposit) == 0)
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
			Tile location = Tile.getInstance(planet(), deposit % WIDTH, deposit / WIDTH);
			if (canSenseLocation(location) && karboniteAt(location) == 0)
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
			for (int x = 0; x < WIDTH * HEIGHT; x++)
			{
				if (nearestKarbonite[x] != -1 && !karboniteLocations.contains(nearestKarbonite[x]))
				{
					karboniteQueue.add(x);
					queuedIndices.add(x);
				}
			}
		}
		
		AsteroidStrike asteroid = null;
		if (ASTEROIDPATTERN.hasAsteroid(round()))
		{
			asteroid = ASTEROIDPATTERN.asteroid(round());
			if (PLANET == Planet.Mars)
			{
				int loc = asteroid.getLocation().getX() + asteroid.getLocation().getY() * WIDTH;
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
		
		if (planet() == Planet.Earth && allyWorkers.size() + allyFactories.size() == 0)
		{
			writeTeamArray(0, 1);
		}
		if (planet() == Planet.Mars) {
			writeTeamArray(1, allyWorkers.size());
		}
		
	}

	static void updateUnitTypes()
	{
		
		VecUnit robots = gc.units();
		
		for (int i = 0; i < robots.size(); i++) {
			Robot bot = Robot.getInstance(robots.get(i));
			if (bot.team() == TEAM && ( bot.unitType() == UnitType.Rocket || bot.unitType() == UnitType.Factory) && !bot.structureIsBuilt())
			{
				currentBlueprints.add(bot);
			}
			if (bot.team() == enemy() && (bot.unitType() == UnitType.Factory)) {
				factoryCache.add(bot.tile());
			}
			allRobots.add(bot);
			if (bot.team() == enemy()) {
				turnsSinceLastEnemy = 0;
				allEnemies.add(bot);
			} else {
				allAllies.add(bot);
			}
			switch (bot.unitType()) {
				case Factory:
					if (bot.team() == team()) allyFactories.add(bot);
					else enemyFactories.add(bot);
					break;
				case Rocket:
					if (bot.team() == team()) allyRockets.add(bot);
					else enemyRockets.add(bot);
					break;
				case Ranger:
					if (bot.team() == team()) {
						allyRangers.add(bot);
						allyCombat.add(bot);
					}
					else enemyRangers.add(bot);
					break;
				case Knight:
					if (bot.team() == team()) {
						allyKnights.add(bot);
						allyCombat.add(bot);
					}
					else enemyKnights.add(bot);
					break;
				case Healer:
					if (bot.team() == team()) {
						allyHealers.add(bot);
						allyCombat.add(bot);
					}
					else enemyHealers.add(bot);
					break;
				case Mage:
					if (bot.team() == team()) {
						allyMages.add(bot);
						allyCombat.add(bot);
					}
					else enemyMages.add(bot);
					break;
				case Worker:
					if (bot.team() == team()) {
						allyWorkers.add(bot);
					}
					else enemyWorkers.add(bot);
					break;
			}
		}
		
		free(robots);
	}

}


