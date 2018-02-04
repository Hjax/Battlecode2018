package dev;

import bc.*;

public class Robot {
	
	private int id;
	private int gcId;
	private static int nextId = 0;
	private static Robot[] box;
	
	// properties that we cache and reuse / update every turn
	public long health = 0;
	public Tile tile;
	public Tile rangerTarget;
	public boolean isInGarrison = false;
	public boolean isInSpace = false;
	public boolean onMap = true;
	public long abilityHeat = 0;
	public long attackHeat = 0;
	public long moveHeat = 0;
	public boolean canAct = true;
	public boolean isSniping = false;
	public long researchLevel = 0;
	public boolean isAbilityUnlocked = false;
	public boolean isBuilt = false;
	public boolean isFactoryProducing = false;
	public UnitType factoryUnitType;
	public long factoryRoundsLeft = 0;
	public Team team;
	public Planet p;
	public UnitType type;
	
	static {
		box = new Robot[65536];
	}
	public static Robot getInstance(Unit unit) {
		if (box[unit.id()] != null) {
			return box[unit.id()];
		}
		Robot r = new Robot(unit);
		r.update();
		box[unit.id()] = r;
		return r;
	}
	
	public Robot(Unit unit) {
		this.gcId = unit.id();
		this.id = nextId++;
	}
	
	public void update() {
		Unit u = Game.gc.unit(gcId);
		try {
			tile = Tile.getInstance(u.location().mapLocation());
		} catch (Exception e) {
		}
		type = u.unitType();
		health = u.health();
		team = u.team();
		
		if (type == UnitType.Worker) {
			canAct = u.workerHasActed() > 0;
		}
		if (type == UnitType.Ranger) {
			isSniping = u.rangerIsSniping() > 0;
		}
		
		researchLevel = u.researchLevel();
		if (type != UnitType.Rocket && type != UnitType.Factory) {
			isAbilityUnlocked = u.isAbilityUnlocked() > 0;
			moveHeat = u.movementHeat();
			abilityHeat = u.abilityHeat();
			attackHeat = u.attackHeat();
		}
		if (type == UnitType.Rocket || type == UnitType.Factory) {
			isBuilt = u.structureIsBuilt() > 0;
		}
		if (type == UnitType.Factory) {
			if (u.isFactoryProducing() > 0) {
				factoryRoundsLeft = u.factoryRoundsLeft();
				factoryUnitType = u.factoryUnitType();
			} else {
				factoryRoundsLeft = 0;
				factoryUnitType = null;
			}

			
			isFactoryProducing = u.isFactoryProducing() > 0;
		}
		
		try {
			rangerTarget = Tile.getInstance(u.rangerTargetLocation());
		} catch (Exception e) {
			
		}
		isInGarrison = u.location().isInGarrison();
		isInSpace = u.location().isInSpace();
		onMap = u.location().isOnMap();
		u.delete();
		
	}
	

	long abilityHeat() {
		return abilityHeat;
	}
	long attackHeat() {
		return attackHeat;
	}
	boolean equals(Robot other) {
		return gcId == other.gcId;
	}
	long factoryRoundsLeft()  {
		return factoryRoundsLeft;
	}
	UnitType factoryUnitType() {
		return factoryUnitType;
	}
	long health() {
		return health;
	}
	int id() {
		return gcId;
	}
	int predictableId() {
		return id;
	}
	boolean isAbilityUnlocked() {
		return isAbilityUnlocked;
	}
	boolean isFactoryProducing() {
		return isFactoryProducing;
	}
	Tile tile() {
		return tile;
	}
	long movementHeat() {
		return moveHeat;
	}
	boolean rangerIsSniping() {
		return isSniping;
	}
	Tile rangerTargetLocation() {
		return rangerTarget;
	}
	long researchLevel() {
		return researchLevel;
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
	boolean structureIsBuilt() {
		return isBuilt;
	}
	Team team() {
		return team;
	}
	UnitType unitType() {
		return type;
	}
	boolean workerHasActed() {
		return canAct;
	}
	public boolean onMap() {
		return onMap;
	}
	public boolean inGarrison() {
		return isInGarrison;
	}
	public boolean inSpace() {
		return isInSpace;
	}
	public void attack(Robot target) {
		target.health = Math.max(Math.min(Constants.maxHealth(target.unitType()), target.health - Constants.attackDamage(type)), 0);
		attackHeat += Constants.attackCooldown(type);
		if (type == UnitType.Healer) {
			Game.gc.heal(gcId, target.gcId);
		} else {
			Game.gc.attack(gcId, target.gcId);
		}
	}
	public void move(Direction d) {
		moveHeat += Constants.movementCooldown(type);
		tile = tile.add(d);
		Game.gc.moveRobot(gcId, d);
	}
	public boolean canUseAbililty(Direction d) {
		return Game.gc.canReplicate(gcId, d);
	}
	public boolean canUseAbility(Tile t) {
		switch (type) {
			case Ranger:
				return Game.gc.canBeginSnipe(gcId, t.location);
			case Mage:
				return Game.gc.canBlink(gcId, tile.location);
			default:
				return false;
		}
	}
	public boolean canUseAbililty(Robot r) {
		switch (type) {
			case Healer:
				return Game.gc.canOvercharge(gcId, r.gcId);
			case Knight:
				Game.gc.canJavelin(gcId, r.gcId);
			default:
				return false;
		}
	}
	public void useAbility(Direction d) {
		Game.gc.replicate(gcId, d);
		abilityHeat += Constants.abilityCooldown(type);
	}
	public void useAbility(Tile t) {
		switch (type) {
			case Ranger:
				Game.gc.beginSnipe(gcId, t.location);
				break;
			case Mage:
				Game.gc.blink(gcId, tile.location);
				break;
			default:
				break;
		}
		abilityHeat += Constants.abilityCooldown(type);
	}
	public void useAbililty(Robot r) {
		switch (type) {
			case Healer:
				Game.gc.overcharge(gcId, r.gcId);
				r.overcharge();
				break;
			case Knight:
				Game.gc.javelin(gcId, r.gcId);
				r.health = Math.max(Math.min(Constants.maxHealth(r.unitType()), r.health - Constants.attackDamage(type)), 0);
				break;
			default:
				break;
		}
		abilityHeat += Constants.abilityCooldown(type);
	}
	public void build(Robot s) {
		Game.gc.build(gcId, s.gcId);
		canAct = false;
	}
	public boolean canBuild(Robot s) {
		return Game.gc.canBuild(gcId, s.gcId);
	}
	public void repair(Robot s) {
		Game.gc.repair(gcId, s.gcId);
		canAct = false;
	}
	public boolean canRepair(Robot s) {
		return Game.gc.canRepair(gcId, s.gcId);
	}
	public void harvest(Direction d) {
		Game.gc.harvest(gcId, d);
		canAct = false;
	}
	public boolean canHarvest(Direction d) {
		return Game.gc.canHarvest(gcId, d);
	}
	public void blueprint(UnitType sType, Direction d) {
		Game.gc.blueprint(gcId, sType, d);
		canAct = false;
	}
	public boolean canBlueprint(UnitType sType, Direction d) {
		return Game.gc.canBlueprint(gcId, sType, d);
	}
	public boolean canAttack(Robot r) {
		if (type == UnitType.Ranger) {
			return attackHeat < 10 && r.tile().distanceSquaredTo(tile) < Constants.attackRange(type) && r.tile().distanceSquaredTo(tile) > Constants.RANGERMINRAGE && (r.health() > 0 || (r.team() == Game.team() && r.health < Constants.maxHealth(r.unitType())));
		}
		return attackHeat < 10 && r.tile().distanceSquaredTo(tile) < Constants.attackRange(type) && (r.health() > 0 || (r.team() == Game.team() && r.health < Constants.maxHealth(r.unitType())));
	}
	public void load(Robot r) {
		Game.gc.load(gcId, r.gcId);
	}
	public void unload(Direction d) {
		Game.gc.unload(gcId, d);
	}
	public boolean isAttackReady() {
		return attackHeat < 10;
	}
	public void produceRobot(UnitType r) {
		Game.gc.produceRobot(gcId, r);
	}
	public boolean canLoad(Robot r) {
		return Game.gc.canLoad(gcId, r.gcId);
	}
	public boolean canUnload(Direction d) {
		return Game.gc.canUnload(gcId, d);
	}
	public void launchRocket(Tile t) {
		Game.gc.launchRocket(gcId, t.location);
	}
	public boolean isAbilityReady() {
		return isAbilityUnlocked && abilityHeat < 10;
	}
	public boolean canMove(Direction d) {
		return moveHeat < 10 && Game.gc.canMove(gcId, d);
	}
	public void disintegrate() {
		Game.gc.disintegrateUnit(gcId);
	}
	public boolean canLaunchRocket(Tile t) {
		return Game.gc.canLaunchRocket(gcId, t.location);
	}
	public void overcharge() {
		moveHeat = 0;
		attackHeat = 0;
		abilityHeat = 0;
		canAct = true;
	}
}
