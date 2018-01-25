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
        pathMap = new boolean[(int) (Game.WIDTH * Game.HEIGHT)];
        for (int x = 0; x < Game.WIDTH; x++) {
        	for (int y = 0; y < Game.HEIGHT; y++) {
            	if (STARTINGMAP.isPassableTerrainAt(new MapLocation(PLANET, x, y)) > 0) {
                	passable.add(Tile.getInstance(PLANET, x, y));
                	pathMap[x + Game.WIDTH * y] = true;
                } else {
                	pathMap[x + Game.WIDTH * y] = false;
                }
            }
        }
        
        for (int x = 0; x < Game.STARTINGMAPOTHER.getWidth(); x++) {
        	for (int y = 0; y < Game.STARTINGMAPOTHER.getHeight(); y++) {
            	if (STARTINGMAPOTHER.isPassableTerrainAt(new MapLocation(OTHERPLANET, x, y)) > 0) {
                	passableOther.add(Tile.getInstance(OTHERPLANET, x, y));
                }
            }
        }
    }
	public static void startTurn() 
	{
		GameInfoCache.updateCache();
	}

	public static VecMapLocation allLocationsWithin(Tile location, long radis_squared) {
		return gc.allLocationsWithin(location.location, radis_squared);
	}
	
	public static AsteroidPattern asteroidPattern() {
		return ASTEROIDPATTERN;
	}
	
	public static void attack(Robot robot, Robot target) {
		gc.attack(robot.id(), target.id());
		target.forceUpdate();
	}
	
	public static void beginSnipe(Robot ranger, Tile location) {
		gc.beginSnipe(ranger.id(), location.location);
		ranger.forceUpdate();
	}
	
	public static void blink(Robot mage, Tile location) {
		gc.blink(mage.id(), location.location);
		mage.forceUpdate();
	}
	
	public static void blueprint(Robot worker, UnitType structure, Direction direction) {
		gc.blueprint(worker.id(), structure, direction);
	}
	
	public static void build(Robot worker, Robot blueprint) {
		gc.build(worker.id(), blueprint.id());
		blueprint.forceUpdate();
	}
	
	public static boolean canAttack(Robot robot, Robot target) {
		return gc.canAttack(robot.id(), target.id());
	}
	
	public static boolean canBeginSnipe(Robot ranger, Tile location) {
		return gc.canBeginSnipe(ranger.id(), location.location);
	}
	
	public static boolean canBlink(Robot mage, Tile location) {
		return gc.canBlink(mage.id(), location.location);
	}
	
	public static boolean canBlueprint(Robot worker, UnitType structure, Direction direction) { 
		return gc.canBlueprint(worker.id(), structure, direction);
	}
	
	public static boolean canBuild(Robot worker, Robot structure) {
		return gc.canBuild(worker.id(), structure.id());
	}
	
	public static boolean canHarvest(Robot worker, Direction direction) {
		return gc.canHarvest(worker.id(), direction);
	}
	
	public static boolean canHeal(Robot healer, Robot target) {
		return gc.canHeal(healer.id(), target.id());
	}
	
	public static boolean canJavelin(Robot knight, Robot target) {
		return gc.canJavelin(knight.id(), target.id());
	}
	
	public static boolean canLaunchRocket(Robot rocket, Tile destination) {
		return gc.canLaunchRocket(rocket.id(), destination.location);
	}
	
	public static boolean canLoad(Robot structure, Robot robot) {
		return gc.canLoad(structure.id(), robot.id());
	}
	
	public static boolean canMove(Robot robot, Direction direction) {
		return isMoveReady(robot) && gc.canMove(robot.id(), direction);
	}
	
	public static boolean canOvercharge(Robot healer, Robot target) {
		return gc.canOvercharge(healer.id(), target.id());
	}
	
	public static boolean canProduceRobot(Robot factory, UnitType robotType) {
		return gc.canProduceRobot(factory.id(), robotType);
	}
	
	public static boolean canRepair(Robot worker, Robot structure) {
		return gc.canRepair(worker.id(), structure.id());
	}
	
	public static boolean canReplicate(Robot worker, Direction direction) {
		return gc.canReplicate(worker.id(), direction);
	}
	
	public static boolean canSenseLocation(Tile location) {
		return gc.canSenseLocation(location.location);
	}
	
	public static boolean canSenseUnit(Robot unit) {
		return gc.canSenseUnit(unit.id());
	}
	
	public static boolean canUnload(Robot structure, Direction direction) {
		return gc.canUnload(structure.id(), direction);
	}
	
	public static long currentDurationOfFlight() {
		return gc.currentDurationOfFlight();
	}
	
	public static void disintegrateUnit(Robot unit) {
		gc.disintegrateUnit(unit.id());
		unit.forceUpdate();
	}
	
	public static Veci32 getTeamArray(Planet planet) {
		return gc.getTeamArray(planet);
	}
	
	public static void harvest(Robot worker, Direction direction) {
		gc.harvest(worker.id(), direction);
	}
	
	public static boolean hasUnitAtLocation(Tile location) {
		return gc.hasUnitAtLocation(location.location);
	}
	
	public static void heal(Robot healer, Robot target) {
		gc.heal(healer.id(), target.id());
	}
	
	public static boolean isAttackReady(Robot unit) {
		return gc.isAttackReady(unit.id());
	}
	
	public static boolean isBeginSnipeReady(Robot ranger) {
		return gc.isBeginSnipeReady(ranger.id());
	}
	
	public static boolean isBlinkReady(Robot mage) {
		return gc.isBlinkReady(mage.id());
	}
	
	public static boolean isHealReady(Robot healer) {
		return gc.isHealReady(healer.id());
	}
	
	public static boolean isJavelinReady(Robot knight) {
		return gc.isJavelinReady(knight.id());
	}
	
	public static boolean isMoveReady(Robot unit) {
		return gc.isMoveReady(unit.id());
	}
	
	public static short isOccupiable(Tile location) {
		return gc.isOccupiable(location.location);
	}
	
	public static boolean isOver() {
		return gc.isOver();
	}
	
	public static boolean isOverchargeReady(Robot unit) {
		return gc.isOverchargeReady(unit.id());
	}
	
	public static void javelin(Robot knight, Robot target) {
		gc.javelin(knight.id(), target.id());
		knight.forceUpdate();
		target.forceUpdate();
	}
	
	public static long karbonite() {
		return gc.karbonite();
	}
	
	public static long karboniteAt(Tile location) {
		return gc.karboniteAt(location.location);
	}
	
	public static void launchRocket(Robot rocket, Tile location) {
		gc.launchRocket(rocket.id(), location.location);
		rocket.forceUpdate();
	}
	
	public static void load(Robot structure, Robot unit) {
		gc.load(structure.id(), unit.id());
		unit.forceUpdate();
		structure.forceUpdate();
	}
	
	public static void moveRobot(Robot robot, Direction direction) {
		gc.moveRobot(robot.id(), direction);
		robot.forceUpdate();
	}
	
	public static Robot[] myUnits() {
		VecUnit myUnits = gc.myUnits();
		Robot[] units = new Robot[(int) myUnits.size()];
		for (int i = 0; i < myUnits.size(); i++) {
		}
		return units;
	}
	
	public static void nextTurn() {
		round++;
		gc.nextTurn();
	}
	
	public static OrbitPattern orbitPattern() {
		return ORBITPATTERN;
	}
	
	public static void overcharge(Robot healer, Robot target) {
		gc.overcharge(healer.id(), target.id());
		healer.forceUpdate();
		target.forceUpdate();
	}
	
	public static Planet planet() {
		return PLANET;
	}
	
	public static void produceRobot(Robot factory, UnitType type) {
		gc.produceRobot(factory.id(), type);
		factory.forceUpdate();
	}
	
	public static short queueResearch(UnitType branch) {
		return gc.queueResearch(branch);
	}
	
	public static void repair(Robot worker, Robot structure) {
		gc.repair(worker.id(), structure.id());
		worker.forceUpdate();
	}
	
	public static void replicate(Robot worker, Direction direction) {
		gc.replicate(worker.id(), direction);
		worker.forceUpdate();
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
		return units;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius, Team team) {
		VecUnit result = gc.senseNearbyUnitsByTeam(location.location, radius, team);
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
		return units;
	}
	
	public static Robot[] senseNearbyUnits(Tile location, long radius, UnitType type) {
		VecUnit result = gc.senseNearbyUnitsByType(location.location, radius, type);
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
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
		return units.toArray(new Robot[0]);
	}
	
	public static Robot[] senseNearbyUnits(UnitType type, Team team) {
		return senseNearbyUnits(Tile.getInstance(Game.planet(), 0, 0), INFINITY, type, team);
	}
	
	public static Robot[] senseNearbyUnits(Team team) {
		return senseNearbyUnits(Tile.getInstance(Game.planet(), 0, 0), INFINITY, team);
	}
	
	public static Robot[] senseNearbyUnits(UnitType type) {
		return senseNearbyUnits(Tile.getInstance(Game.planet(), 0, 0), INFINITY, type);
	}
	
	public static Robot[] senseNearbyUnits() {
		return senseNearbyUnits(Tile.getInstance(Game.planet(), 0, 0), INFINITY);
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
		return units.toArray(new Robot[0]);
	}
	
	public static Robot[] senseCombatUnits(Team team) {
		return senseCombatUnits(Tile.getInstance(Game.planet(), 0, 0), INFINITY, team);
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
	
	public static Robot unit(int id) {
		return Robot.getInstance(gc.unit(id));
	}
	
	public static Robot[] units() {
		VecUnit result = gc.units();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
		return units;
	}
	
	public static Robot[] unitsInSpace() {
		VecUnit result = gc.unitsInSpace();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
		return units;
	}
	
	public static void unload(Robot structure, Direction direction) {
		Robot[] garrison = structure.structureGarrison();
		if (garrison.length > 0) {
			gc.unload(structure.id(), direction);
			garrison[0].forceUpdate();			
		}
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
	
	public static Robot[] getInitialUnits() {
		VecUnit result = startingMap(Planet.Earth).getInitial_units();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = Robot.getInstance(result.get(i));
		}
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

}


