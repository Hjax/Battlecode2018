package dev;

import bc.*;
import java.util.*;

public class Robot {
	private long round;
	private int id;
	private int gcId;
	private static int nextId = 0;
	private static Map<Integer, Robot> box;
	static {
		box = new HashMap<>();
	}
	public static Robot getInstance(Unit unit) {
		if (box.containsKey(unit.id())) {
			return box.get(unit.id());
		}
		Robot r = new Robot(unit);
		box.put(unit.id(), r);
		return r;
	}
	
	public Robot(Unit unit) {
		round = Game.round();
		this.gcId = gcId;
		this.id = nextId++;
	}
	
	long abilityCooldown()  {
		return Game.gc.unit(gcId).abilityCooldown();
	}
	long abilityHeat() {
		return Game.gc.unit(gcId).abilityHeat();
	}
	long abilityRange() {
		return Game.gc.unit(gcId).abilityRange();
	}
	long attackCooldown() {
		return Game.gc.unit(gcId).attackCooldown();
	}
	long attackHeat() {
		return Game.gc.unit(gcId).attackHeat();
	}
	long attackRange() {
		return Game.gc.unit(gcId).attackRange();
	}
	int damage() {
		return Game.gc.unit(gcId).damage();
	}
	boolean equals(Robot other) {
		return gcId == other.gcId;
	}
	long factoryMaxRoundsLeft() {
		return Game.gc.unit(gcId).factoryMaxRoundsLeft();
	}
	long factoryRoundsLeft()  {
		return Game.gc.unit(gcId).factoryRoundsLeft();
	}
	UnitType factoryUnitType() {
		return Game.gc.unit(gcId).factoryUnitType();
	}
	long healerSelfHealAmount() {
		return Game.gc.unit(gcId).healerSelfHealAmount();
	}
	long health() {
		return Game.gc.unit(gcId).health();
	}
	int id() {
		return gcId;
	}
	int predictableId() {
		return id;
	}
	short isAbilityUnlocked() {
		return Game.gc.unit(gcId).isAbilityUnlocked();
	}
	short isFactoryProducing() {
		return Game.gc.unit(gcId).isFactoryProducing();
	}
	long knightDefense() {
		return Game.gc.unit(gcId).knightDefense();
	}
	Location location() {
		return Game.gc.unit(gcId).location();
	}
	Tile tile() {
		return Tile.getInstance(Game.gc.unit(gcId).location().mapLocation());
	}
	long maxHealth() {
		return Game.gc.unit(gcId).maxHealth();
	}
	long movementCooldown() {
		return Game.gc.unit(gcId).movementCooldown();
	}
	long movementHeat() {
		return Game.gc.unit(gcId).movementHeat();
	}
	long rangerCannotAttackRange() {
		return Game.gc.unit(gcId).rangerCannotAttackRange();
	}
	long rangerCountdown() {
		return Game.gc.unit(gcId).rangerCountdown();
	}
	short rangerIsSniping() {
		return Game.gc.unit(gcId).rangerIsSniping();
	}
	long rangerMaxCountdown() {
		return Game.gc.unit(gcId).rangerMaxCountdown();
	}
	MapLocation rangerTargetLocation() {
		return Game.gc.unit(gcId).rangerTargetLocation();
	}
	long researchLevel() {
		return Game.gc.unit(gcId).researchLevel();
	}
	int rocketBlastDamage() {
		return Game.gc.unit(gcId).rocketBlastDamage();
	}
	short rocketIsUsed() {
		return Game.gc.unit(gcId).rocketIsUsed();
	}
	long rocketTravelTimeDecrease() {
		return Game.gc.unit(gcId).rocketTravelTimeDecrease();
	}
	Robot[] structureGarrison() {
		VecUnitID result = Game.gc.unit(gcId).structureGarrison();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(Game.gc.unit(result.get(i)));
		}
		return units;
	}
	short structureIsBuilt() {
		return Game.gc.unit(gcId).structureIsBuilt();
	}
	long structureMaxCapacity() {
		return Game.gc.unit(gcId).structureMaxCapacity();
	}
	Team team() {
		return Game.gc.unit(gcId).team();
	}
	UnitType unitType() {
		return Game.gc.unit(gcId).unitType();
	}
	long visionRange() {
		return Game.gc.unit(gcId).visionRange();
	}
	long workerBuildHealth() {
		return Game.gc.unit(gcId).workerBuildHealth();
	}
	long workerHarvestAmount() {
		return Game.gc.unit(gcId).workerBuildHealth();
	}
	short workerHasActed() {
		return Game.gc.unit(gcId).workerHasActed();
	}
	long workerRepairHealth() {
		return Game.gc.unit(gcId).workerRepairHealth();
	}
}
