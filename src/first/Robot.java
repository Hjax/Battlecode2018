package first;

import bc.*;
import java.util.*;

public class Robot {
	Unit unit;
	long round;
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
		this.unit = unit;
	}
	
	public void update() {
		if (round != Game.round()) {
			round = Game.round();
			this.unit = Game.gc.unit(unit.id());
		}
	}
	long abilityCooldown()  {
		update();
		return unit.abilityCooldown();
	}
	long abilityHeat() {
		update();
		return unit.abilityCooldown();
	}
	long abilityRange() {
		update();
		return unit.abilityRange();
	}
	long attackCooldown() {
		update();
		return unit.attackCooldown();
	}
	long attackHeat() {
		update();
		return unit.attackHeat();
	}
	long attackRange() {
		update();
		return unit.attackRange();
	}
	int damage() {
		update();
		return unit.damage();
	}
	boolean equals(Robot other) {
		update();
		other.update();
		return unit.equals(other.unit);
	}
	long factoryMaxRoundsLeft() {
		update();
		return unit.factoryMaxRoundsLeft();
	}
	long factoryRoundsLeft()  {
		update();
		return unit.factoryRoundsLeft();
	}
	UnitType factoryUnitType() {
		update();
		return unit.factoryUnitType();
	}
	long healerSelfHealAmount() {
		update();
		return unit.healerSelfHealAmount();
	}
	long health() {
		update();
		return unit.health();
	}
	int id() {
		update();
		return unit.id();
	}
	short isAbilityUnlocked() {
		update();
		return unit.isAbilityUnlocked();
	}
	short isFactoryProducing() {
		update();
		return unit.isFactoryProducing();
	}
	long knightDefense() {
		update();
		return unit.knightDefense();
	}
	Location location() {
		update();
		return unit.location();
	}
	MapLocation mapLocation() {
		update();
		return unit.location().mapLocation();
	}
	long maxHealth() {
		update();
		return unit.maxHealth();
	}
	long movementCooldown() {
		update();
		return unit.movementCooldown();
	}
	long movementHeat() {
		update();
		return unit.movementHeat();
	}
	long rangerCannotAttackRange() {
		update();
		return unit.rangerCannotAttackRange();
	}
	long rangerCountdown() {
		update();
		return unit.rangerCountdown();
	}
	short rangerIsSniping() {
		update();
		return unit.rangerIsSniping();
	}
	long rangerMaxCountdown() {
		update();
		return unit.rangerMaxCountdown();
	}
	MapLocation rangerTargetLocation() {
		update();
		return unit.rangerTargetLocation();
	}
	long researchLevel() {
		update();
		return unit.researchLevel();
	}
	int rocketBlastDamage() {
		update();
		return unit.rocketBlastDamage();
	}
	short rocketIsUsed() {
		update();
		return unit.rocketIsUsed();
	}
	long rocketTravelTimeDecrease() {
		update();
		return unit.rocketTravelTimeDecrease();
	}
	Robot[] structureGarrison() {
		update();
		VecUnitID result = unit.structureGarrison();
		Robot[] units = new Robot[(int) result.size()];
		for (int i = 0; i < result.size(); i++) {
			units[i] = new Robot(Game.gc.unit(result.get(i)));
		}
		return units;
	}
	short structureIsBuilt() {
		update();
		return unit.structureIsBuilt();
	}
	long structureMaxCapacity() {
		update();
		return unit.structureMaxCapacity();
	}
	Team team() {
		update();
		return unit.team();
	}
	UnitType unitType() {
		update();
		return unit.unitType();
	}
	long visionRange() {
		update();
		return unit.visionRange();
	}
	long workerBuildHealth() {
		update();
		return unit.workerBuildHealth();
	}
	long workerHarvestAmount() {
		update();
		return unit.workerBuildHealth();
	}
	short workerHasActed() {
		update();
		return unit.workerHasActed();
	}
	long workerRepairHealth() {
		update();
		return unit.workerRepairHealth();
	}
}
