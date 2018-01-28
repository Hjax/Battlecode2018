package placement;

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
		if (!Game.isOverchargeReady(r) || r.abilityHeat() >= 10) return;
		Robot[] chargee = Game.senseCombatUnits(r.tile(), r.abilityRange(), Game.team());
		for (Robot t: chargee) {
			if (Game.senseNearbyUnits(t.tile(), (long) Math.pow(Math.sqrt(t.attackRange()) + 1, 2), Game.enemy()).length > 0) {
				if (Game.canOvercharge(r, t)) {
					Game.overcharge(r, t);
					micro(t);
					return;
				}
			}
		}
		
	}

 	public static void heal(Robot r) {
 		if (r.attackHeat() >= 10) return;
 		Robot best = null;
		Robot[] healee = Game.senseCombatUnits(r.tile(), r.attackRange(), Game.team());
		for (Robot ally: healee) {
			if (ally.health() < ally.maxHealth()) {
				if (best == null || (ally.health() < best.health())) {
					best = ally;
				}
			}
		}
		if (best != null && Game.canHeal(r, best)) {
			Game.heal(r, best);
		}
 	}

 	public static void target(Robot r, UnitType u) {
 		if (r.attackHeat() >= 10) return;
		Robot[] targets = Game.senseNearbyUnits(r.tile(), r.attackRange(), u, Game.enemy());
		if (targets.length > 0) {
			Robot best = null;
			for (Robot enemy: targets) {
				if (enemy.location().isOnMap() && Game.canAttack(r, enemy)) {
					if (enemy.health() > 0) {
						if (best == null || enemy.health() < best.health()) {
							best = enemy;
						}
					}
				}
			}
			if (best != null && Game.canAttack(r, best)) {
				newHelpRequests.add(best.tile());
				Game.attack(r, best);
				if (Game.canJavelin(r, best)) {
					Game.javelin(r, best);
				}
			}
		}
 	}
 	
 	public static void shoot(Robot r) {
 		target(r, UnitType.Factory);
 		target(r, UnitType.Rocket);
 		target(r, UnitType.Mage);
 		target(r, UnitType.Healer);
 		target(r, UnitType.Ranger);
 		target(r, UnitType.Knight);
 		target(r, UnitType.Worker);
 	}
 	
 	public static void knightMicro(Robot r) {
 		knightTarget(r, UnitType.Factory);
 		knightTarget(r, UnitType.Knight);
 		knightTarget(r, UnitType.Ranger);
 		knightTarget(r, UnitType.Mage);
 		knightTarget(r, UnitType.Rocket);
 		knightTarget(r, UnitType.Worker);
 		knightTarget(r, UnitType.Healer);
 	}
 	
 	public static void knightTarget(Robot r, UnitType u) {
 		if (r.movementHeat() >= 10) return;
 		Robot[] nearby = Game.senseNearbyUnits(r.tile(), Constants.RANGERVISION, u, Game.enemy());
 		Robot best = null;
 		for (Robot enemy: nearby) {
			if (enemy.health() > 0) {
				if (enemy.location().isOnMap() && (best == null || Pathfinding.pathLength(enemy.tile(), r.tile()) < Pathfinding.pathLength(best.tile(), r.tile()))) {
					best = enemy;
				}
			}
		}
 		if (best != null) {
 			Direction m = Pathfinding.path(r.tile(), best.tile());
 			if (m != null && Game.canMove(r, m)) {
 				Game.moveRobot(r, m);
 			}
 		}
 	}
 	
 	public static Direction getAverageEnemyDirection(Robot r) {
 		int x = 0, y = 0;
 		Robot[] nearby = Game.senseCombatUnits(r.tile(), Constants.RANGERVISION, Game.enemy());
 		for (Robot n: nearby) {
 			x += n.tile().getX();
 			y += n.tile().getY();
 		}
 		return r.tile().directionTo(Tile.getInstance(Game.planet(), x / nearby.length, y / nearby.length));
 	}
 	
 	public static Direction getAverageAllyDirection(Robot r) {
 		int x = 0, y = 0;
 		Robot[] nearby = Game.senseCombatUnits(r.tile(), Constants.RANGERVISION, Game.team());
 		for (Robot n: nearby) {
 			x += n.tile().getX();
 			y += n.tile().getY();
 		}
 		return r.tile().directionTo(Tile.getInstance(Game.planet(), x / nearby.length, y / nearby.length));
 	}
		
 	public static void micro(Robot r) {
 		if (!r.location().isOnMap() || r.location().isInGarrison() || r.location().isInSpace()) {
			return;
		}
		Tile target = null;
		if (Rocket.assignments.containsKey(r)) {
			if (Game.canLoad(Rocket.assignments.get(r), r)) {
				Game.load(Rocket.assignments.get(r), r);
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
			if (targets.size() == 0 && GameInfoCache.factoryCache.size() == 0) {
				if (randomTargets.containsKey(r) && r.tile().distanceSquaredTo(randomTargets.get(r)) > 2) {
					target = randomTargets.get(r);
				} else {
					randomTargets.put(r, Game.getRandomLocation());
					target = randomTargets.get(r);
				}
			} else {
				if (GameInfoCache.factoryCache.size() > 0) {
					Tile best = null;
					for (Tile f: GameInfoCache.factoryCache) {
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
			Robot[] enemies = Game.senseCombatUnits(r.tile(), Constants.RANGERRANGE, Game.enemy());
			if (enemies.length == 0) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (Game.canMove(r, d)) {
					Game.moveRobot(r, d);
					shoot(r);
				}
			} else if (enemies.length * Constants.RANGERDAMAGE >= r.health()){
				Direction move = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
				shoot(r);
				if (Game.canMove(r, move)) {
					Game.moveRobot(r, move);
				}
			}
			shoot(r);
		}
		else if (r.unitType() == UnitType.Healer) {
			Robot[] enemies = Game.senseCombatUnits(r.tile(), (long) Math.pow(Math.sqrt(Constants.RANGERRANGE) + 1, 2), Game.enemy());
			if (enemies.length == 0) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (Game.canMove(r, d)) {
					Game.moveRobot(r, d);
				}
			} else {
				Direction move = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
				if (Game.canMove(r, move)) {
					heal(r);
					Game.moveRobot(r, move);
				}
			}
			overchargeTarget(r);
			heal(r);
		}
		else if (r.unitType() == UnitType.Knight) {
			Robot[] enemies = Game.senseNearbyUnits(r.tile(), Constants.RANGERVISION, Game.enemy());
			if (enemies.length == 0) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (Game.canMove(r, d)) {
					Game.moveRobot(r, d);
				}
			} else {
				knightMicro(r);
			}
			shoot(r);
		}
 	}
 	
	public static void run() {
		for (Robot r: GameInfoCache.allyCombat) {
			micro(r);
		}
	}
}
