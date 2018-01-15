package micro;

import bc.*;
import java.util.*;

public class Game {
	public static GameController gc;
	public static Direction[] directions;
	public static Direction[] moveDirections;
	public static Set<Tile> passable = new HashSet<>();
	public static boolean[] pathMap;
	
	public static final Planet PLANET;
	public static final int WIDTH;
	public static final int HEIGHT;
	public static final PlanetMap STARTINGMAP;
	public static final AsteroidPattern ASTEROIDPATTERN;
	public static final OrbitPattern ORBITPATTERN;
	public static final int INFINITY = 99999999;
	public static final Team TEAM;
	public static final Team ENEMY;
	public static int round = 1;
	
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
        WIDTH = (int) STARTINGMAP.getWidth();
        HEIGHT = (int) STARTINGMAP.getHeight();
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
	}
	
	public static void beginSnipe(Robot ranger, Tile location) {
		gc.beginSnipe(ranger.id(), location.location);
	}
	
	public static void blink(Robot mage, Tile location) {
		gc.blink(mage.id(), location.location);
	}
	
	public static void blueprint(Robot worker, UnitType structure, Direction direction) {
		gc.blueprint(worker.id(), structure, direction);
	}
	
	public static void build(Robot worker, Robot blueprint) {
		gc.build(worker.id(), blueprint.id());
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
	}
	
	public static long karbonite() {
		return gc.karbonite();
	}
	
	public static long karboniteAt(Tile location) {
		return gc.karboniteAt(location.location);
	}
	
	public static void launchRocket(Robot rocket, Tile location) {
		gc.launchRocket(rocket.id(), location.location);
	}
	
	public static void load(Robot structure, Robot unit) {
		gc.load(structure.id(), unit.id());
	}
	
	public static void moveRobot(Robot robot, Direction direction) {
		gc.moveRobot(robot.id(), direction);
	}
	
	public static Robot[] myUnits() {
		Robot[] units = new Robot[(int) gc.myUnits().size()];
		for (int i = 0; i < gc.myUnits().size(); i++) {
			units[i] = Robot.getInstance(gc.myUnits().get(i));
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
	}
	
	public static Planet planet() {
		return PLANET;
	}
	
	public static void produceRobot(Robot factory, UnitType type) {
		gc.produceRobot(factory.id(), type);
	}
	
	public static short queueResearch(UnitType branch) {
		return gc.queueResearch(branch);
	}
	
	public static void repair(Robot worker, Robot structure) {
		gc.repair(worker.id(), structure.id());
	}
	
	public static void replicate(Robot worker, Direction direction) {
		gc.replicate(worker.id(), direction);
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
		return gc.startingMap(planet);
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
		gc.unload(structure.id(), direction);
	}
	
	public static Team winningTeam() {
		return gc.winningTeam();
	}
	
	public static void writeTeamArray(long index, int value) {
		gc.writeTeamArray(index, value);
	}
	
	public static void attack(int robot, int target) {
		gc.attack(robot, target);
	}
	
	public static void beginSnipe(int ranger, Tile location) {
		gc.beginSnipe(ranger, location.location);
	}
	
	public static void blink(int mage, Tile location) {
		gc.blink(mage, location.location);
	}
	
	public static void blueprint(int worker, UnitType structure, Direction direction) {
		gc.blueprint(worker, structure, direction);
	}
	
	public static void build(int worker, int blueprint) {
		gc.build(worker, blueprint);
	}
	
	public static boolean canAttack(int robot, int target) {
		return gc.canAttack(robot, target);
	}
	
	public static boolean canBeginSnipe(int ranger, Tile location) {
		return gc.canBeginSnipe(ranger, location.location);
	}
	
	public static boolean canBlink(int mage, Tile location) {
		return gc.canBlink(mage, location.location);
	}
	
	public static boolean canBlueprint(int worker, UnitType structure, Direction direction) { 
		return gc.canBlueprint(worker, structure, direction);
	}
	
	public static boolean canBuild(int worker, int structure) {
		return gc.canBuild(worker, structure);
	}
	
	public static boolean canHarvest(int worker, Direction direction) {
		return gc.canHarvest(worker, direction);
	}
	
	public static boolean canHeal(int healer, int target) {
		return gc.canHeal(healer, target);
	}
	
	public static boolean canJavelin(int knight, int target) {
		return gc.canJavelin(knight, target);
	}
	
	public static boolean canLaunchRocket(int rocket, Tile destination) {
		return gc.canLaunchRocket(rocket, destination.location);
	}
	
	public static boolean canLoad(int structure, int robot) {
		return gc.canLoad(structure, robot);
	}
	
	public static boolean canMove(int robot, Direction direction) {
		return isMoveReady(robot) && gc.canMove(robot, direction);
	}
	
	public static boolean canOvercharge(int healer, int target) {
		return gc.canOvercharge(healer, target);
	}
	
	public static boolean canProduceRobot(int factory, UnitType robotType) {
		return gc.canProduceRobot(factory, robotType);
	}
	
	public static boolean canRepair(int worker, int structure) {
		return gc.canRepair(worker, structure);
	}
	
	public static boolean canReplicate(int worker, Direction direction) {
		return gc.canReplicate(worker, direction);
	}
	
	public static boolean canSenseUnit(int unit) {
		return gc.canSenseUnit(unit);
	}
	
	public static boolean canUnload(int structure, Direction direction) {
		return gc.canUnload(structure, direction);
	}
	
	public static void disintegrateUnit(int unit) {
		gc.disintegrateUnit(unit);
	}
	
	public static void harvest(int worker, Direction direction) {
		gc.harvest(worker, direction);
	}
	
	public static void heal(int healer, int target) {
		gc.heal(healer, target);
	}
	
	public static boolean isAttackReady(int unit) {
		return gc.isAttackReady(unit);
	}
	
	public static boolean isBeginSnipeReady(int ranger) {
		return gc.isBeginSnipeReady(ranger);
	}
	
	public static boolean isBlinkReady(int mage) {
		return gc.isBlinkReady(mage);
	}
	
	public static boolean isHealReady(int healer) {
		return gc.isHealReady(healer);
	}
	
	public static boolean isJavelinReady(int knight) {
		return gc.isJavelinReady(knight);
	}
	
	public static boolean isMoveReady(int unit) {
		return gc.isMoveReady(unit);
	}
	
	public static boolean isOverchargeReady(int unit) {
		return gc.isOverchargeReady(unit);
	}
	
	public static void javelin(int knight, int target) {
		gc.javelin(knight, target);
	}
	
	public static void launchRocket(int rocket, Tile location) {
		gc.launchRocket(rocket, location.location);
	}
	
	public static void load(int structure, int unit) {
		gc.load(structure, unit);
	}
	
	public static void moveRobot(int robot, Direction direction) {
		gc.moveRobot(robot, direction);
	}
	
	public static void overcharge(int healer, int target) {
		gc.overcharge(healer, target);
	}
	
	public static void produceRobot(int factory, UnitType type) {
		gc.produceRobot(factory, type);
	}
	
	public static void repair(int worker, int structure) {
		gc.repair(worker, structure);
	}
	
	public static void replicate(int worker, Direction direction) {
		gc.replicate(worker, direction);
	}
	
	public static void unload(int structure, Direction direction) {
		gc.unload(structure, direction);
	}
	
	public static boolean onMap(Tile loc, Planet p) {
		return startingMap(p).onMap(loc.location);
	}
	
	public static long initialKarboniteAt(Tile loc) {
		return startingMap(loc.location.getPlanet()).initialKarboniteAt(loc.location);
	}
	
	public static boolean isPassableTerrainAt(Tile loc) {
		return passable.contains(loc);
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
		Random rand = new Random();
		int x = 0;
		int y = 0;
		do {
			 x = rand.nextInt((int) startingMap(planet()).getWidth());
			 y = rand.nextInt((int) startingMap(planet()).getHeight());
		} while (!isPassableTerrainAt(Tile.getInstance(new MapLocation(planet(), x, y))));
		return Tile.getInstance(new MapLocation(planet(), x, y));
	}

}


