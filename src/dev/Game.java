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
	
	private static ArrayList<DepositScoreTuple> enemyKarbonite = new ArrayList<>();
	
	private static class DepositScoreTuple implements Comparator<DepositScoreTuple>
	{
		public int score;
		public int deposit;
		
		DepositScoreTuple(int SCORE, int DEPOSIT)
		{
			score = SCORE;
			deposit = DEPOSIT;
		}

		public int compare(DepositScoreTuple o1, DepositScoreTuple o2) 
		{
			//higher score is "less than" lower score for sorting purposes
			if (o1.score < o2.score)
			{
				return -1;
			}
			if (o1.score == o2.score)
			{
				return 0;
			}
			return 1;
		}
	}
	
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
	
	public static double[] karboniteDensity;
	public static Tile contestedKarbonite = null;
	
	public static int[] startingKarbonite;
	public static HashSet<Integer> karboniteLocations = new HashSet<Integer>();
	
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
        
        karboniteDensity = new double[WIDTH * HEIGHT];
        startingKarbonite = new int[WIDTH * HEIGHT];
        
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
        
		Tile checkLocation;
		int contestedKarboniteScore = Constants.INFINITY;
		for (int x = 0; x < startingMap(planet()).getWidth(); x++)
		{
			for (int y = 0; y < startingMap(planet()).getHeight(); y++)
			{
				checkLocation = Tile.getInstance(planet(), x, y);
				int intTile = x + y * WIDTH;
				startingKarbonite[intTile] = (int) initialKarboniteAt(checkLocation);
				if (startingKarbonite[intTile] > 0)
				{
					karboniteLocations.add(intTile);
					int friendlyDistance = Constants.INFINITY;
					for (Tile start: Constants.startingAlliesLocation)
					{
						if (Pathfinding.pathLength(checkLocation, start) < friendlyDistance && Pathfinding.pathLength(checkLocation, start) > -1)
						{
							friendlyDistance = Pathfinding.pathLength(checkLocation, start);
						}
					}
					int enemyDistance = Constants.INFINITY;
					for (Tile start: Constants.startingEnemiesLocation)
					{
						if (Pathfinding.pathLength(checkLocation, start) < enemyDistance && Pathfinding.pathLength(checkLocation, start) > -1)
						{
							enemyDistance = Pathfinding.pathLength(checkLocation, start);
						}
					}
					enemyKarbonite.add(new DepositScoreTuple(enemyDistance, intTile));
					if (enemyDistance - friendlyDistance < contestedKarboniteScore && friendlyDistance != Constants.INFINITY && friendlyDistance <= enemyDistance)
					{
						contestedKarboniteScore = enemyDistance - friendlyDistance;
						contestedKarbonite = checkLocation;
					}
				}
			}
		}
		if (enemyKarbonite.size() > 0)
		{
			enemyKarbonite.sort(enemyKarbonite.get(0));
		}
		
		
		initDensityMap();
    }
	
	public static Set<Tile> factoryCache = new HashSet<>();
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
	public static ArrayList<Robot> enemyCombat = new ArrayList<Robot>();
	
	public static int turnsSinceLastEnemy = 0;

	
	
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
		Robot[] result = new Robot[1024];
		int total = 0;
		for (Robot r: allRobots) {
			if (!r.onMap()) continue;
			if (r.health() > 0 && r.tile().distanceSquaredTo(location) <= radius) {
				result[total++] = r;
			}
		}
		Robot[] units = new Robot[total];
		for (int i = 0; i < total; i++) {
			units[i] = result[i];
		}
		return units;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius, Team team) {
		Robot[] result = new Robot[1024];
		int total = 0;
		Robot[] current = senseNearbyUnits(team);
		for (Robot r: current) {
			if (!r.onMap()) continue;
			if (r.team() == team && r.health() > 0 && r.tile().distanceSquaredTo(location) <= radius) {
				result[total++] = r;
			}
		}
		Robot[] units = new Robot[total];
		for (int i = 0; i < total; i++) {
			units[i] = result[i];
		}
		return units;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius, UnitType type) {
		Robot[] result = new Robot[1024];
		int total = 0;
		Robot[] current = senseNearbyUnits(type);
		for (Robot r: current) {
			if (!r.onMap()) continue;
			if (r.unitType() == type && r.health() > 0 && r.tile().distanceSquaredTo(location) <= radius) {
				result[total++] = r;
			}
		}
		Robot[] units = new Robot[total];
		for (int i = 0; i < total; i++) {
			units[i] = result[i];
		}
		return units;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius, UnitType type, Team team) {
		Robot[] result = new Robot[1024];
		int total = 0;
		Robot[] current = senseNearbyUnits(type, team);
		for (Robot r: current) {
			if (!r.onMap()) continue;
			if (r.team() == team && r.unitType() == type && r.health() > 0 && r.tile().distanceSquaredTo(location) <= radius) {
				result[total++] = r;
			}
		}
		Robot[] units = new Robot[total];
		for (int i = 0; i < total; i++) {
			units[i] = result[i];
		}
		return units;
	}
	
	public static Robot[] senseNearbyUnits(UnitType type, Team team) {
		if (team == team()) {
			switch (type) {
			case Factory:
				return allyFactories.toArray(new Robot[allyFactories.size()]);
			case Healer:
				return allyHealers.toArray(new Robot[allyHealers.size()]);
			case Knight:
				return allyKnights.toArray(new Robot[allyKnights.size()]);
			case Mage:
				return allyMages.toArray(new Robot[allyMages.size()]);
			case Ranger:
				return allyRangers.toArray(new Robot[allyRangers.size()]);
			case Rocket:
				return allyRockets.toArray(new Robot[allyRockets.size()]);
			case Worker:
				return allyWorkers.toArray(new Robot[allyWorkers.size()]);
			}
		} else {
			switch (type) {
			case Factory:
				return enemyFactories.toArray(new Robot[enemyFactories.size()]);
			case Healer:
				return enemyHealers.toArray(new Robot[enemyHealers.size()]);
			case Knight:
				return enemyKnights.toArray(new Robot[enemyKnights.size()]);
			case Mage:
				return enemyMages.toArray(new Robot[enemyMages.size()]);
			case Ranger:
				return enemyRangers.toArray(new Robot[enemyRangers.size()]);
			case Rocket:
				return enemyRockets.toArray(new Robot[enemyRockets.size()]);
			case Worker:
				return enemyWorkers.toArray(new Robot[enemyWorkers.size()]);
			}
		}
		return null;
	}
	
	public static Robot[] senseNearbyUnits(Team team) {
		if (team == team()) {
			return allAllies.toArray(new Robot[allAllies.size()]);
		}
		return allEnemies.toArray(new Robot[allEnemies.size()]);
	}
	
	public static Robot[] senseNearbyUnits(UnitType type) {
		switch (type) {
		case Factory:
			return allFactories.toArray(new Robot[allFactories.size()]);
		case Healer:
			return allHealers.toArray(new Robot[allHealers.size()]);
		case Knight:
			return allKnights.toArray(new Robot[allKnights.size()]);
		case Mage:
			return allMages.toArray(new Robot[allMages.size()]);
		case Ranger:
			return allRangers.toArray(new Robot[allRangers.size()]);
		case Rocket:
			return allRockets.toArray(new Robot[allRockets.size()]);
		case Worker:
			return allWorkers.toArray(new Robot[allWorkers.size()]);
		}
		return null;
	}
	
	public static Robot[] senseNearbyUnits() {
		return allRobots.toArray(new Robot[allRobots.size()]);
	}
	
	public static Robot senseUnitAtLocation(Tile location) {
		return Robot.getInstance(gc.senseUnitAtLocation(location.location));
	}
	
	public static Robot[] senseCombatUnits(Tile location, long radius, Team team) {
		List<Robot> current;
		if (team == team()) {
			current = allyCombat;
		} else {
			current = enemyCombat;
		}
		Robot[] result = new Robot[1024];
		int total = 0;
		for (Robot r: current) {
			if (!r.onMap()) continue;
			if (r.team() == team && r.health() > 0 && r.tile().distanceSquaredTo(location) <= radius) {
				result[total++] = r;
			}
		}
		Robot[] units = new Robot[total];
		for (int i = 0; i < total; i++) {
			units[i] = result[i];
		}
		return units;
	}
	
	public static Robot[] senseCombatUnits(Team team) {
		if (team == team()) {
			return allyCombat.toArray(new Robot[allyCombat.size()]);
		} else {
			return enemyCombat.toArray(new Robot[enemyCombat.size()]);
		}
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
		Robot[] units = new Robot[allRobots.size()];
		for (int i = 0; i < allRobots.size(); i++) {
			units[i] = allRobots.get(i);
		}
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

	

	static void deleteDeposit(int deposit)
	{
		karboniteLocations.remove(deposit);
		for (int updateTile = 0; updateTile < MAPSIZE; updateTile++)
		{
			int distance = Pathfinding.pathLength(updateTile, deposit);
			if (distance != -1)
			{
				karboniteDensity[updateTile] -= startingKarbonite[deposit]/Constants.powerSwitch(distance);
			}
			
		}
	}
	
	static void createDeposit(int deposit)
	{
		karboniteLocations.remove(deposit);
		for (int updateTile = 0; updateTile < MAPSIZE; updateTile++)
		{
			int distance = Pathfinding.pathLength(updateTile, deposit);
			if (distance != -1)
			{
				karboniteDensity[updateTile] += startingKarbonite[deposit]/Constants.powerSwitch(distance);
			}
			
		}
	}

	static void initDensityMap()
	{
		
		for (int initDeposit: karboniteLocations)
		{
			for (int updateTile = 0; updateTile < MAPSIZE; updateTile++)
			{
				int distance = Pathfinding.pathLength(updateTile, initDeposit);
				if (distance != -1)
				{
					karboniteDensity[updateTile] += startingKarbonite[initDeposit]/Constants.powerSwitch(distance);
				}
			}
		}
		/*
		//print out the density map with nice formatting
		for (int y = HEIGHT - 1; y >=0; y--)
		{
			for (int x = 0; x < WIDTH; x++)
			{
				System.out.printf("%d", Math.round(karboniteDensity[x + y*WIDTH] * 100));
				if (karboniteDensity[x + y*WIDTH] < 100)
				{
					System.out.printf(" ");
				}
				if (karboniteDensity[x + y*WIDTH] < 1000)
				{
					System.out.printf(" ");
				}
				if (karboniteDensity[x + y*WIDTH] < 10)
				{
					System.out.printf(" ");
				}
				if (karboniteDensity[x + y*WIDTH] < 1)
				{
					System.out.printf(" ");
				}
				if (karboniteDensity[x + y*WIDTH] < 0.1)
				{
					System.out.printf(" ");
				}
				System.out.printf("|");
			}
			System.out.printf("\n");
		}
		*/
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
		enemyCombat = new ArrayList<Robot>();
		
		updateUnitTypes();
		
	
		HashSet<Integer> depositsToDelete = new HashSet<>();
		for (int deposit:karboniteLocations)
		{
			Tile location = Tile.getInstance(planet(), deposit % WIDTH, deposit / WIDTH);
			if (canSenseLocation(location) && karboniteAt(location) == 0)
			{
				depositsToDelete.add(location.getX() + location.getY() * WIDTH);
			}
		}
		for (int deposit:depositsToDelete)
		{
			if (contestedKarbonite != null && contestedKarbonite.getX() == deposit % WIDTH && contestedKarbonite.getY() == deposit / WIDTH)
			{
				contestedKarbonite = null;
			}
			deleteDeposit(deposit);
		}
		
		/*
		//print the density map
		if (round % 20 == 0)
		{
			for (int y = HEIGHT - 1; y >=0; y--)
			{
				for (int x = 0; x < WIDTH; x++)
				{
					System.out.printf("%d", Math.round(karboniteDensity[x + y*WIDTH] * 100));
					if (karboniteDensity[x + y*WIDTH] < 100)
					{
						System.out.printf(" ");
					}
					if (karboniteDensity[x + y*WIDTH] < 1000)
					{
						System.out.printf(" ");
					}
					if (karboniteDensity[x + y*WIDTH] < 10)
					{
						System.out.printf(" ");
					}
					if (karboniteDensity[x + y*WIDTH] < 1)
					{
						System.out.printf(" ");
					}
					if (karboniteDensity[x + y*WIDTH] < 0.1)
					{
						System.out.printf(" ");
					}
					System.out.printf("|");
				}
				System.out.printf("\n");
			}
		}
		*/
		
		
		//assume enemy mines out his deposits
		if (karboniteLocations.size() > 0 && PLANET == Planet.Earth)
		{
			while (!karboniteLocations.contains(new Integer(enemyKarbonite.get(0).deposit)))
			{
				enemyKarbonite.remove(0);
			}
			Tile location = Tile.getInstance(planet(), enemyKarbonite.get(0).deposit % WIDTH, enemyKarbonite.get(0).deposit / WIDTH);
			while(!canSenseLocation(location) && enemyKarbonite.get(0).score < (round() - 30) / 2)
			{
				deleteDeposit(enemyKarbonite.get(0).deposit);
				if (karboniteLocations.size() == 0)
				{
					break;
				}
				while (!karboniteLocations.contains(enemyKarbonite.get(0).deposit))
				{
					enemyKarbonite.remove(0);
				}
				location = Tile.getInstance(planet(), enemyKarbonite.get(0).deposit % WIDTH, enemyKarbonite.get(0).deposit / WIDTH);
			}
		}
		else
		{
			if (PLANET == Planet.Earth)
			{
				//print the density map
				if (round % 20 == 0)
				{
					for (int y = HEIGHT - 1; y >=0; y--)
					{
						for (int x = 0; x < WIDTH; x++)
						{
							System.out.printf("%d", Math.round(karboniteDensity[x + y*WIDTH] * 100));
							if (karboniteDensity[x + y*WIDTH] < 100)
							{
								System.out.printf(" ");
							}
							if (karboniteDensity[x + y*WIDTH] < 1000)
							{
								System.out.printf(" ");
							}
							if (karboniteDensity[x + y*WIDTH] < 10)
							{
								System.out.printf(" ");
							}
							if (karboniteDensity[x + y*WIDTH] < 1)
							{
								System.out.printf(" ");
							}
							if (karboniteDensity[x + y*WIDTH] < 0.1)
							{
								System.out.printf(" ");
							}
							System.out.printf("|");
						}
						System.out.printf("\n");
					}
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
				startingKarbonite[loc] = (int) asteroid.getKarbonite();
				createDeposit(loc);
			}
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
			bot.update();
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
					allFactories.add(bot);
					break;
				case Rocket:
					if (bot.team() == team()) allyRockets.add(bot);
					else enemyRockets.add(bot);
					allRockets.add(bot);
					break;
				case Ranger:
					if (bot.team() == team()) {
						allyRangers.add(bot);
						allyCombat.add(bot);
					}
					else {
						enemyRangers.add(bot);
						enemyCombat.add(bot);
					}
					allRangers.add(bot);
					break;
				case Knight:
					if (bot.team() == team()) {
						allyKnights.add(bot);
						allyCombat.add(bot);
					}
					else {
						enemyKnights.add(bot);
						enemyCombat.add(bot);
					}
					allKnights.add(bot);
					break;
				case Healer:
					if (bot.team() == team()) {
						allyHealers.add(bot);
						allyCombat.add(bot);
					}
					else {
						enemyHealers.add(bot);
						enemyCombat.add(bot);
					}
					allHealers.add(bot);
					break;
				case Mage:
					if (bot.team() == team()) {
						allyMages.add(bot);
						allyCombat.add(bot);
					}
					else {
						enemyMages.add(bot);
						enemyCombat.add(bot);
					}
					allMages.add(bot);
					break;
				case Worker:
					if (bot.team() == team()) {
						allyWorkers.add(bot);
					}
					else {
						enemyWorkers.add(bot);
					}
					allWorkers.add(bot);
					break;
			}
		}
		
		free(robots);
	}

}


