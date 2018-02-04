package dev;

import java.util.*;
import bc.*;

public class Micro {
	// TODO shoot before moving when it makes sense
	private static Map<Robot, Tile> randomTargets = new HashMap<>();
	private static ArrayList<Tile> targets = new ArrayList<>();
	static {
		for (Tile loc:Constants.startingEnemiesLocation) {
			targets.add(loc);
		}
	}
	private static ArrayList<Tile> helpRequests = new ArrayList<>();
	private static ArrayList<Tile> newHelpRequests = new ArrayList<>();
	
	public static void startTurn() {
		// TODO purge help requests only every few rounds 
		helpRequests = newHelpRequests;
		newHelpRequests = new ArrayList<>();
		//System.out.println("Requests: " + helpRequests.size());
		//System.out.println("Random Targets: " + randomTargets.size());
	}
	
	public static void overchargeTarget(Robot r) {
		if (!r.isAbilityReady()) return;
		Robot[] chargee = Game.senseCombatUnits(r.tile(), Constants.abilityRange(r.unitType()), Game.team());
		for (Robot t: chargee) {
			if (t.unitType() == UnitType.Healer) continue;
			if (Game.senseNearbyUnits(t.tile(), Constants.attackRange(r.unitType()), Game.enemy()).length > 0) {
				r.useAbililty(t);
				micro(t);
				return;
			}
		}
		chargee = Game.senseNearbyUnits(r.tile(), Constants.abilityRange(r.unitType()), UnitType.Knight, Game.team());
		for (Robot t: chargee) {
			r.useAbililty(t);
			micro(t);
			return;
		}
		
	}

 	public static void heal(Robot r) {
 		if (!r.isAttackReady()) return;
 		Robot best = null;
		Robot[] healee = Game.senseCombatUnits(r.tile(), Constants.attackRange(r.unitType()), Game.team());
		for (Robot ally: healee) {
			if (ally.health() < Constants.maxHealth(ally.unitType())) {
				if (best == null || (ally.health() < best.health())) {
					best = ally;
				}
			}
		}
		if (best != null && r.canAttack(best)) {
			r.attack(best);
		}
 	}

 	public static void target(Robot r) {
 		if (r.attackHeat() >= 10) return;
		Robot[] targets = Game.senseNearbyUnits(r.tile(), Constants.attackRange(r.unitType()), Game.enemy());
		if (targets.length > 0) {
			Robot best = null;
			for (Robot enemy: targets) {
				if (enemy.onMap() && r.canAttack(enemy)) {
					if (enemy.health() > 0) {
						if (best == null || ((enemy.health() < best.health() && scoreRanger(enemy.unitType()) >= scoreRanger(best.unitType())) || scoreRanger(enemy.unitType()) > scoreRanger(best.unitType()))) {
							best = enemy;
						}
					}
				}
			}
			if (best != null) {
				newHelpRequests.add(best.tile());
				r.attack(best);
				if (r.unitType() == UnitType.Knight && r.isAbilityReady()) {
					r.useAbililty(best);
				}
			}
		}
 	}
 	
 	public static int scoreRanger(UnitType r) {
 		switch (r) {
 			case Factory:
 				return 10;
 			case Rocket:
 				return 9;
 			case Mage:
 				return 8;
 			case Healer:
 				return 7;
 			case Ranger:
 				return 6;
 			case Knight:
 				return 5;
 			case Worker:
 				return 4;
 			default:
 				return 3;
 		}
 	}
 	
 	public static int scoreKnight(UnitType r) {
 		switch (r) {
 			case Factory:
 				return 9;
 			case Knight:
 				return 10;
 			case Ranger:
 				return 8;
 			case Mage:
 				return 7;
 			case Rocket:
 				return 6;
 			case Worker:
 				return 5;
 			case Healer:
 				return 4;
 			default:
 				return 3;
 		}
 	}
 	
 	public static void knightTarget(Robot r) {
 		if (r.movementHeat() >= 10) return;
 		Robot[] nearby = Game.senseNearbyUnits(r.tile(), Constants.visionRange(UnitType.Ranger), Game.enemy());
 		Robot best = null;
 		for (Robot enemy: nearby) {
			if (enemy.health() > 0) {
				if (enemy.onMap() && (best == null || (Pathfinding.pathLength(enemy.tile(), r.tile()) < Pathfinding.pathLength(best.tile(), r.tile()) && scoreKnight(enemy.unitType()) >= scoreKnight(best.unitType())) || scoreKnight(enemy.unitType()) > scoreKnight(best.unitType()))) {
					best = enemy;
				}
			}
		}
 		if (best != null) {
 			Direction m = Pathfinding.path(r.tile(), best.tile());
 			if (m != null && r.canMove(m)) {
 				r.move(m);
 			}
 		}
 	}
 	
 	public static Direction getAverageEnemyDirection(Robot r) {
 		int x = 0, y = 0;
 		Robot[] nearby = Game.senseCombatUnits(r.tile(), Constants.visionRange(UnitType.Ranger), Game.enemy());
 		for (Robot n: nearby) {
 			x += n.tile().getX();
 			y += n.tile().getY();
 		}
 		return r.tile().directionTo(Tile.getInstance(Game.planet(), x / nearby.length, y / nearby.length));
 	}
 	
 	public static Direction getAverageAllyDirection(Robot r) {
 		int x = 0, y = 0;
 		Robot[] nearby = Game.senseCombatUnits(r.tile(), Constants.visionRange(UnitType.Ranger), Game.team());
 		for (Robot n: nearby) {
 			x += n.tile().getX();
 			y += n.tile().getY();
 		}
 		return r.tile().directionTo(Tile.getInstance(Game.planet(), x / nearby.length, y / nearby.length));
 	}
		
 	public static void micro(Robot r) {
 		if (!r.onMap()|| r.inSpace() || r.inGarrison()) {
			return;
		}
		Tile target = null;
		if (Rocket.assignments.containsKey(r)) {
			if (Rocket.assignments.get(r).canLoad(r)) {
				Rocket.assignments.get(r).load(r);
				return;
			}
			target = Rocket.assignments.get(r).tile();
				
		}
		else if (helpRequests.size() > 0) {
			// TODO this is probably slow
			for (Tile help: helpRequests) {
				if (target == null || Pathfinding.pathLength(r.tile(), help) < Pathfinding.pathLength(r.tile(), target)) {
					target = help;
				}
			}
		} else {
			if (targets.size() == 0 && Game.factoryCache.size() == 0) {
				if (randomTargets.containsKey(r) && r.tile().distanceSquaredTo(randomTargets.get(r)) > 2) {
					target = randomTargets.get(r);
				} else {
					randomTargets.put(r, Game.getRandomLocation());
					target = randomTargets.get(r);
				}
			} else {
				if (Game.factoryCache.size() > 0) {
					Tile best = null;
					for (Tile f: Game.factoryCache) {
						if (best == null || Pathfinding.pathLength(r.tile(), best) > Pathfinding.pathLength(r.tile(), f)) {
							best = f;
						}
					}
					target = best;
				}
				else if (Pathfinding.pathLength(targets.get(0), r.tile()) <= 2) {
					targets.remove(0);
				} else {
					target = targets.get(0);
				}
			}
		}
		if (r.unitType() == UnitType.Ranger) {
			Robot[] enemies = Game.senseCombatUnits(r.tile(), Constants.attackRange(UnitType.Ranger), Game.enemy());

			if (enemies.length == 0 && target != null) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (r.canMove(d)) {
					r.move(d);
					target(r);
				}
			} else if (enemies.length * Constants.RANGERDAMAGE >= r.health() || (Game.enemyKnights.size() > 0 && Game.senseNearbyUnits(r.tile(), Constants.attackRange(r.unitType()), UnitType.Knight, Game.enemy()).length > 0)){
				Direction d = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
				target(r);
				if (r.canMove(d)) {
					r.move(d);
				}
			}
			target(r);
		}
		else if (r.unitType() == UnitType.Healer) {
			Robot[] enemies = Game.senseCombatUnits(r.tile(), (long) Math.pow(Math.sqrt(Constants.attackRange(UnitType.Ranger)) + 1, 2), Game.enemy());
			if (enemies.length == 0 && target != null) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (r.canMove(d)) {
					r.move(d);
				}
			} else {
				Direction d = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
				if (r.canMove(d)) {
					heal(r);
					r.move(d);
				}
			}
			overchargeTarget(r);
			heal(r);
		}
		else if (r.unitType() == UnitType.Knight) {
			Robot[] enemies = Game.senseNearbyUnits(r.tile(), Constants.visionRange(UnitType.Ranger), Game.enemy());
			
			if (enemies.length == 0 && target != null) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (r.canMove(d)) {
					r.move(d);
				}
			} else {
				knightTarget(r);
			}
			target(r);
		}
 	}
 	
	public static void run() {
		for (Robot r: Game.allyCombat) {
			if (r.inGarrison() || r.inSpace() || !r.onMap()) continue;
			if (r.unitType() == UnitType.Healer) continue;
			micro(r);
		}
		for (Robot r: Game.allyHealers) {
			micro(r);
		}
	}
}
