package first;

import bc.*;
import java.util.*;

public class Game {
	public static GameController gc;
	public static Direction[] directions;
	public static final int INFINITY = 99999999;
	static {
        gc = new GameController();
        directions = Direction.values();
    }
	public static void startTurn() {
	}

	public static VecMapLocation allLocationsWithin(MapLocation location, long radis_squared) {
		return gc.allLocationsWithin(location, radis_squared);
	}
	
	public static AsteroidPattern asteroidPattern() {
		return gc.asteroidPattern();
	}
	
	public static void attack(Robot robot, Robot target) {
		gc.attack(robot.id(), target.id());
	}
	
	public static void beginSnipe(Robot ranger, MapLocation location) {
		gc.beginSnipe(ranger.id(), location);
	}
	
	public static void blink(Robot mage, MapLocation location) {
		gc.blink(mage.id(), location);
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
	
	public static boolean canBeginSnipe(Robot ranger, MapLocation location) {
		return gc.canBeginSnipe(ranger.id(), location);
	}
	
	public static boolean canBlink(Robot mage, MapLocation location) {
		return gc.canBlink(mage.id(), location);
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
	
	public static boolean canLaunchRocket(Robot rocket, MapLocation destination) {
		return gc.canLaunchRocket(rocket.id(), destination);
	}
	
	public static boolean canLoad(Robot structure, Robot robot) {
		return gc.canLoad(structure.id(), robot.id());
	}
	
	public static boolean canMove(Robot robot, Direction direction) {
		return gc.canMove(robot.id(), direction);
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
	
	public static boolean canSenseLocation(MapLocation location) {
		return gc.canSenseLocation(location);
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
	
	public static boolean hasUnitAtLocation(MapLocation location) {
		return gc.hasUnitAtLocation(location);
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
	
	public static short isOccupiable(MapLocation location) {
		return gc.isOccupiable(location);
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
	
	public static long karboniteAt(MapLocation location) {
		return gc.karboniteAt(location);
	}
	
	public static void launchRocket(Robot rocket, MapLocation location) {
		gc.launchRocket(rocket.id(), location);
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
			units[i] = new Robot(gc.myUnits().get(i));
		}
		return units;
	}
	
	public static void nextTurn() {
		gc.nextTurn();
	}
	
	public static OrbitPattern orbitPattern() {
		return gc.orbitPattern();
	}
	
	public static void overcharge(Robot healer, Robot target) {
		gc.overcharge(healer.id(), target.id());
	}
	
	public static Planet planet() {
		return gc.planet();
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
		return gc.round();
	}
	
	public static Robot[] senseNearbyUnits(MapLocation location, long radius) {
		VecUnit result = gc.senseNearbyUnits(location, radius);
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(result.get(i));
		}
		return units;
	}
	
	public static Robot[] senseNearbyUnits(MapLocation location, long radius, Team team) {
		VecUnit result = gc.senseNearbyUnitsByTeam(location, radius, team);
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(result.get(i));
		}
		return units;
	}
	
	public static Robot[] senseNearbyUnits(MapLocation location, long radius, UnitType type) {
		VecUnit result = gc.senseNearbyUnitsByType(location, radius, type);
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(result.get(i));
		}
		return units;
	}
	
	public static Robot[] senseNearbyUnits(MapLocation location, long radius, UnitType type, Team team) {
		VecUnit result = gc.myUnits();
		List<Robot> units = new ArrayList<Robot>(); 
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).unitType().equals(type) && result.get(i).team().equals(team)) {
				units.add(new Robot(result.get(i)));
			}
		}
		return units.toArray(new Robot[0]);
	}
	
	public static Robot[] senseNearbyUnits(MapLocation location, UnitType type, Team team) {
		return senseNearbyUnits(location, INFINITY, type, team);
	}
	
	public static Robot[] senseNearbyUnits(MapLocation location, Team team) {
		return senseNearbyUnits(location, INFINITY, team);
	}
	
	public static Robot[] senseNearbyUnits(MapLocation location, UnitType type) {
		return senseNearbyUnits(location, INFINITY, type);
	}
	
	public static Robot[] senseNearbyUnits(MapLocation location) {
		return senseNearbyUnits(location, INFINITY);
	}
	
	public static Robot senseUnitAtLocation(MapLocation location) {
		return new Robot(gc.senseUnitAtLocation(location));
	}
	
	public static PlanetMap startingMap(Planet planet) {
		return gc.startingMap(planet);
	}
	
	public static Team team() {
		return gc.team();
	}
	
	public static Robot unit(int id) {
		return new Robot(gc.unit(id));
	}
	
	public static Robot[] units() {
		VecUnit result = gc.units();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(result.get(i));
		}
		return units;
	}
	
	public static Robot[] unitsInSpace() {
		VecUnit result = gc.unitsInSpace();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(result.get(i));
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
	
	public static void beginSnipe(int ranger, MapLocation location) {
		gc.beginSnipe(ranger, location);
	}
	
	public static void blink(int mage, MapLocation location) {
		gc.blink(mage, location);
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
	
	public static boolean canBeginSnipe(int ranger, MapLocation location) {
		return gc.canBeginSnipe(ranger, location);
	}
	
	public static boolean canBlink(int mage, MapLocation location) {
		return gc.canBlink(mage, location);
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
	
	public static boolean canLaunchRocket(int rocket, MapLocation destination) {
		return gc.canLaunchRocket(rocket, destination);
	}
	
	public static boolean canLoad(int structure, int robot) {
		return gc.canLoad(structure, robot);
	}
	
	public static boolean canMove(int robot, Direction direction) {
		return gc.canMove(robot, direction);
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
	
	public static void launchRocket(int rocket, MapLocation location) {
		gc.launchRocket(rocket, location);
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
	
	public static boolean onMap(MapLocation loc, Planet p) {
		return startingMap(p).onMap(loc);
	}
	
	public static long initialKarboniteAt(MapLocation loc) {
		return startingMap(loc.getPlanet()).initialKarboniteAt(loc);
	}
	
	public static boolean isPassableTerrainAt(MapLocation loc) {
		return startingMap(loc.getPlanet()).isPassableTerrainAt(loc) > 0;
	}
	
	public static Robot[] getInitialUnits() {
		VecUnit result = startingMap(Planet.Earth).getInitial_units();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(result.get(i));
		}
		return units;
	}
}


