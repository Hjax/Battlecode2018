package first;

import bc.*;

public class game {
	private static GameController gc;
	private static Direction[] directions;
	private static VecUnit allies;
	private static VecUnit enemies;
	public static final int INFINITY = 99999999;
	static {
        gc = new GameController();
        directions = Direction.values();
    }
	public static void startTurn() {
		allies = gc.myUnits();
		// TODO update when theres a way to find the enemy team
		enemies = gc.senseNearbyUnitsByTeam(new MapLocation(gc.planet(), 0, 0), INFINITY, gc.team());
	}

	public static VecMapLocation allLocationsWithin(MapLocation location, long radis_squared) {
		return gc.allLocationsWithin(location, radis_squared);
	}
	
	public static AsteroidPattern asteroidPattern() {
		return gc.asteroidPattern();
	}
	
	public static void attack(Unit robot, Unit target) {
		gc.attack(robot.id(), target.id());
	}
	
	public static void beginSnipe(Unit ranger, MapLocation location) {
		gc.beginSnipe(ranger.id(), location);
	}
	
	public static void blink(Unit mage, MapLocation location) {
		gc.blink(mage.id(), location);
	}
	
	public static void blueprint(Unit worker, UnitType structure, Direction direction) {
		gc.blueprint(worker.id(), structure, direction);
	}
	
	public static void build(Unit worker, Unit blueprint) {
		gc.build(worker.id(), blueprint.id());
	}
	
	public static boolean canAttack(Unit robot, Unit target) {
		return gc.canAttack(robot.id(), target.id());
	}
	
	public static boolean canBeginSnipe(Unit ranger, MapLocation location) {
		return gc.canBeginSnipe(ranger.id(), location);
	}
	
	public static boolean canBlink(Unit mage, MapLocation location) {
		return gc.canBlink(mage.id(), location);
	}
	
	public static boolean canBlueprint(Unit worker, UnitType structure, Direction direction) { 
		return gc.canBlueprint(worker.id(), structure, direction);
	}
	
	public static boolean canBuild(Unit worker, Unit structure) {
		return gc.canBuild(worker.id(), structure.id());
	}
	
	public static boolean canHarvest(Unit worker, Direction direction) {
		return gc.canHarvest(worker.id(), direction);
	}
	
	public static boolean canHeal(Unit healer, Unit target) {
		return gc.canHeal(healer.id(), target.id());
	}
	
	public static boolean canJavelin(Unit knight, Unit target) {
		return gc.canJavelin(knight.id(), target.id());
	}
	
	public static boolean canLaunchRocket(Unit rocket, MapLocation destination) {
		return gc.canLaunchRocket(rocket.id(), destination);
	}
	
	public static boolean canLoad(Unit structure, Unit robot) {
		return gc.canLoad(structure.id(), robot.id());
	}
	
	public static boolean canMove(Unit robot, Direction direction) {
		return gc.canMove(robot.id(), direction);
	}
	
	public static boolean canOvercharge(Unit healer, Unit target) {
		return gc.canOvercharge(healer.id(), target.id());
	}
	
	public static boolean canProduceRobot(Unit factory, UnitType robotType) {
		return gc.canProduceRobot(factory.id(), robotType);
	}
	
	public static boolean canRepair(Unit worker, Unit structure) {
		return gc.canRepair(worker.id(), structure.id());
	}
	
	public static boolean canReplicate(Unit worker, Direction direction) {
		return gc.canReplicate(worker.id(), direction);
	}
	
	public static boolean canSenseLocation(MapLocation location) {
		return gc.canSenseLocation(location);
	}
	
	public static boolean canSenseUnit(Unit unit) {
		return gc.canSenseUnit(unit.id());
	}
	
	public static boolean canUnload(Unit structure, Direction direction) {
		return gc.canUnload(structure.id(), direction);
	}
	
	public static long currentDurationOfFlight() {
		return gc.currentDurationOfFlight();
	}
	
	public static void disintegrateUnit(Unit unit) {
		gc.disintegrateUnit(unit.id());
	}
	
	public static Veci32 getTeamArray(Planet planet) {
		return gc.getTeamArray(planet);
	}
	
	public static void harvest(Unit worker, Direction direction) {
		gc.harvest(worker.id(), direction);
	}
	
	public static boolean hasUnitAtLocation(MapLocation location) {
		return gc.hasUnitAtLocation(location);
	}
	
	public static void heal(Unit healer, Unit target) {
		gc.heal(healer.id(), target.id());
	}
	
	public static boolean isAttackReady(Unit unit) {
		return gc.isAttackReady(unit.id());
	}
	
	public static boolean isBeginSnipeReady(Unit ranger) {
		return gc.isBeginSnipeReady(ranger.id());
	}
	
	public static boolean isBlinkReady(Unit mage) {
		return gc.isBlinkReady(mage.id());
	}
	
	public static boolean isHealReady(Unit healer) {
		return gc.isHealReady(healer.id());
	}
	
	public static boolean isJavelinReady(Unit knight) {
		return gc.isJavelinReady(knight.id());
	}
	
	public static boolean isMoveReady(Unit unit) {
		return gc.isMoveReady(unit.id());
	}
	
	public static short isOccupiable(MapLocation location) {
		return gc.isOccupiable(location);
	}
	
	public static boolean isOver() {
		return gc.isOver();
	}
	
	public static boolean isOverchargeReady(Unit unit) {
		return gc.isOverchargeReady(unit.id());
	}
	
	public static void javelin(Unit knight, Unit target) {
		gc.javelin(knight.id(), target.id());
	}
	
	public static long karbonite() {
		return gc.karbonite();
	}
	
	public static long karboniteAt(MapLocation location) {
		return gc.karboniteAt(location);
	}
	
	public static void launchRocket(Unit rocket, MapLocation location) {
		gc.launchRocket(rocket.id(), location);
	}
	
	public static void load(Unit structure, Unit unit) {
		gc.load(structure.id(), unit.id());
	}
	
	public static void moveRobot(Unit robot, Direction direction) {
		gc.moveRobot(robot.id(), direction);
	}
	
	public static VecUnit myUnits() {
		return gc.myUnits();
	}
	
	public static void nextTurn() {
		gc.nextTurn();
	}
	
	public static OrbitPattern orbitPattern() {
		return gc.orbitPattern();
	}
	
	public static void overcharge(Unit healer, Unit target) {
		gc.overcharge(healer.id(), target.id());
	}
	
	public static Planet planet() {
		return gc.planet();
	}
	
	public static void produceRobot(Unit factory, UnitType type) {
		gc.produceRobot(factory.id(), type);
	}
	
	public static short queueResearch(UnitType branch) {
		return gc.queueResearch(branch);
	}
	
	public static void repair(Unit worker, Unit structure) {
		gc.repair(worker.id(), structure.id());
	}
	
	public static void replicate(Unit worker, Direction direction) {
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
	
	public static VecUnit senseNearbyUnits(MapLocation location, long radius) {
		return gc.senseNearbyUnits(location, radius);
	}
	
	public static VecUnit senseNearbyUnitsByTeam(MapLocation location, long radius, Team team) {
		return gc.senseNearbyUnitsByTeam(location, radius, team);
	}
	
	public static VecUnit senseNearbyUnitsByType(MapLocation location, long radius, UnitType type) {
		return gc.senseNearbyUnitsByType(location, radius, type);
	}
	
	public static Unit senseUnitAtLocation(MapLocation location) {
		return gc.senseUnitAtLocation(location);
	}
	
	public static PlanetMap startingMap(Planet planet) {
		return gc.startingMap(planet);
	}
	
	public static Team team() {
		return gc.team();
	}
	
	public static Unit unit(int id) {
		return gc.unit(id);
	}
	
	public static VecUnit units() {
		return gc.units();
	}
	
	public static VecUnit unitsInSpace() {
		return gc.unitsInSpace();
	}
	
	public static void unload(Unit structure, Direction direction) {
		gc.unload(structure.id(), direction);
	}
	
	public static Team winningTeam() {
		return gc.winningTeam();
	}
	
	public static void writeTeamArray(long index, int value) {
		gc.writeTeamArray(index, value);
	}
	
}


