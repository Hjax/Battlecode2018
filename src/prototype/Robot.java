package prototype;

import bc.*;

public class Robot {
	private int id;
	private int gcId;
	private static int nextId = 0;
	private static Robot[] box;
	static {
		box = new Robot[65536];
	}
	public static Robot getInstance(Unit unit) {
		if (box[unit.id()] != null) {
			return box[unit.id()];
		}
		Robot r = new Robot(unit);
		box[unit.id()] = r;
		return r;
	}
	
	public Robot(Unit unit) {
		this.gcId = unit.id();
		this.id = nextId++;
	}
	
	long abilityCooldown()  {
		Unit u = Game.gc.unit(gcId);
		long result = u.abilityCooldown();
		u.delete();
		return result;
	}
	long abilityHeat() {
		Unit u = Game.gc.unit(gcId);
		long result = u.abilityHeat();
		u.delete();
		return result;
	}
	long abilityRange() {
		Unit u = Game.gc.unit(gcId);
		long result = u.abilityRange();
		u.delete();
		return result;
	}
	long attackCooldown() {
		Unit u = Game.gc.unit(gcId);
		long result = u.attackCooldown();
		u.delete();
		return result;
	}
	long attackHeat() {
		Unit u = Game.gc.unit(gcId);
		long result = u.attackHeat();
		u.delete();
		return result;
	}
	long attackRange() {
		Unit u = Game.gc.unit(gcId);
		long result = u.attackRange();
		u.delete();
		return result;
	}
	int damage() {
		Unit u = Game.gc.unit(gcId);
		int result = u.damage();
		u.delete();
		return result;
	}
	boolean equals(Robot other) {
		return gcId == other.gcId;
	}
	long factoryMaxRoundsLeft() {
		Unit u = Game.gc.unit(gcId);
		long result = u.factoryMaxRoundsLeft();
		u.delete();
		return result;
	}
	long factoryRoundsLeft()  {
		Unit u = Game.gc.unit(gcId);
		long result = u.factoryRoundsLeft();
		u.delete();
		return result;
	}
	UnitType factoryUnitType() {
		Unit u = Game.gc.unit(gcId);
		UnitType result = u.factoryUnitType();
		u.delete();
		return result;
	}
	long healerSelfHealAmount() {
		Unit u = Game.gc.unit(gcId);
		long result = u.healerSelfHealAmount();
		u.delete();
		return result;
	}
	long health() {
		Unit u = Game.gc.unit(gcId);
		long result = u.health();
		u.delete();
		return result;
	}
	int id() {
		return gcId;
	}
	int predictableId() {
		return id;
	}
	short isAbilityUnlocked() {
		Unit u = Game.gc.unit(gcId);
		short result = u.isAbilityUnlocked();
		u.delete();
		return result;
	}
	short isFactoryProducing() {
		Unit u = Game.gc.unit(gcId);
		short result = u.isFactoryProducing();
		u.delete();
		return result;
	}
	long knightDefense() {
		Unit u = Game.gc.unit(gcId);
		long result = u.knightDefense();
		u.delete();
		return result;
	}
	Location location() {
		Unit u = Game.gc.unit(gcId);
		Location result = u.location();
		u.delete();
		return result;
	}
	Tile tile() {
		Unit u = Game.gc.unit(gcId);
		Tile result = Tile.getInstance(u.location().mapLocation());
		u.delete();
		return result;
	}
	long maxHealth() {
		Unit u = Game.gc.unit(gcId);
		long result = u.maxHealth();
		u.delete();
		return result;
	}
	long movementCooldown() {
		Unit u = Game.gc.unit(gcId);
		long result = u.movementCooldown();
		u.delete();
		return result;
	}
	long movementHeat() {
		Unit u = Game.gc.unit(gcId);
		long result = u.movementHeat();
		u.delete();
		return result;
	}
	long rangerCannotAttackRange() {
		Unit u = Game.gc.unit(gcId);
		long result = u.rangerCannotAttackRange();
		u.delete();
		return result;
	}
	long rangerCountdown() {
		Unit u = Game.gc.unit(gcId);
		long result = u.rangerCountdown();
		u.delete();
		return result;
	}
	short rangerIsSniping() {
		Unit u = Game.gc.unit(gcId);
		short result = u.rangerIsSniping();
		u.delete();
		return result;
	}
	long rangerMaxCountdown() {
		Unit u = Game.gc.unit(gcId);
		long result = u.rangerMaxCountdown();
		u.delete();
		return result;
	}
	Tile rangerTargetLocation() {
		Unit u = Game.gc.unit(gcId);
		Tile result = Tile.getInstance(u.rangerTargetLocation());
		u.delete();
		return result;
	}
	long researchLevel() {
		Unit u = Game.gc.unit(gcId);
		long result = u.researchLevel();
		u.delete();
		return result;
	}
	int rocketBlastDamage() {
		Unit u = Game.gc.unit(gcId);
		int result = u.rocketBlastDamage();
		u.delete();
		return result;
	}
	short rocketIsUsed() {
		Unit u = Game.gc.unit(gcId);
		short result = u.rocketIsUsed();
		u.delete();
		return result;
	}
	long rocketTravelTimeDecrease() {
		Unit u = Game.gc.unit(gcId);
		long result = u.rocketTravelTimeDecrease();
		u.delete();
		return result;
	}
	Robot[] structureGarrison() {
		VecUnitID result = Game.gc.unit(gcId).structureGarrison();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(Game.gc.unit(result.get(i)));
		}
		result.delete();
		return units;
	}
	short structureIsBuilt() {
		Unit u = Game.gc.unit(gcId);
		short result = u.structureIsBuilt();
		u.delete();
		return result;
	}
	long structureMaxCapacity() {
		Unit u = Game.gc.unit(gcId);
		long result = u.structureMaxCapacity();
		u.delete();
		return result;
	}
	Team team() {
		Unit u = Game.gc.unit(gcId);
		Team result = u.team();
		u.delete();
		return result;
	}
	UnitType unitType() {
		Unit u = Game.gc.unit(gcId);
		UnitType result = u.unitType();
		u.delete();
		return result;
	}
	long visionRange() {
		Unit u = Game.gc.unit(gcId);
		long result = u.visionRange();
		u.delete();
		return result;
	}
	long workerBuildHealth() {
		Unit u = Game.gc.unit(gcId);
		long result = u.workerBuildHealth();
		u.delete();
		return result;
	}
	long workerHarvestAmount() {
		Unit u = Game.gc.unit(gcId);
		long result = u.workerHarvestAmount();
		u.delete();
		return result;
	}
	short workerHasActed() {
		Unit u = Game.gc.unit(gcId);
		short result = u.workerHasActed();
		u.delete();
		return result;
	}
	long workerRepairHealth() {
		Unit u = Game.gc.unit(gcId);
		long result = u.workerRepairHealth();
		u.delete();
		return result;
	}
}
