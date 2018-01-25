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
	private static ArrayList<Robot> enemyFactories = new ArrayList<>();
	private static ArrayList<Robot> helpRequests = new ArrayList<>();
	private static ArrayList<Robot> newHelpRequests = new ArrayList<>();
	private static Map<Robot, Integer> scoreMap = new HashMap<Robot, Integer>();
	
	
	
 	public static void startTurn() {
		// TODO purge help requests only every few rounds 
		helpRequests = newHelpRequests;
		newHelpRequests = new ArrayList<>();
		ArrayList<Robot> oldEnemyFactories = enemyFactories;
		enemyFactories = new ArrayList<>();
		for (int i = 0; i < oldEnemyFactories.size(); i++) {
			if (oldEnemyFactories.get(i).health() > 0) {
				enemyFactories.add(oldEnemyFactories.get(i));
			}
		}
		scoreAllFights();
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
				newHelpRequests.add(best);
				Game.attack(r, best);
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
 	
 	public static void scoreFight(Robot r) {
 		int score = 0;
 		Queue<Robot> open = new LinkedList<>();
 		Set<Robot> closed = new HashSet<>();
 		open.add(r);
 		while (open.size() > 0) {
 			Robot current = open.poll();
 			closed.add(current);
 			Robot[] targets;
 			if (current.unitType() == UnitType.Healer) {
 				targets = Game.senseCombatUnits(current.tile(), (long) Math.pow(Math.sqrt(current.attackRange()) + 2, 2), current.team() == Game.team() ? Game.team() : Game.enemy());
 			} else {
 				targets = Game.senseCombatUnits(current.tile(), (long) Math.pow(Math.sqrt(current.attackRange()) + 2, 2), current.team() == Game.team() ? Game.enemy() : Game.team());
 			}
 			if (targets.length > 0) {
 				score += (current.team() == Game.team() ? 1 : -1) * Math.abs(current.damage()) * 2;
 				score += (current.team() == Game.team() ? 1 : -1) * current.health();
 			}
 			Robot[] sense = Game.senseCombatUnits(current.tile(), (long) Math.pow(Math.sqrt(Constants.RANGERRANGE) + 1, 2), Game.team());
 			for (Robot c: sense) {
 				if (!closed.contains(c) && !open.contains(c)) {
 					open.add(c);
 				}
 			}
 			sense = Game.senseCombatUnits(current.tile(), Constants.RANGERRANGE, Game.enemy());
 			for (Robot c: sense) {
 				if (!closed.contains(c) && !open.contains(c)) {
 					open.add(c);
 				}
 			}
 		}
 		for (Robot u: closed) {
 			scoreMap.put(u, score);
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
 	
 	public static void scoreAllFights() {
 		scoreMap = new HashMap<>();
 		for (Robot r: GameInfoCache.allyCombat) {
 			if (!scoreMap.containsKey(r)) {
 				scoreFight(r);
 			}
 		}
 	}
		
	public static void run() {
		for (Robot r: GameInfoCache.allyCombat) {
			if (!r.location().isOnMap() || r.location().isInGarrison() || r.location().isInSpace()) {
				continue;
			}

			Tile target = null;
			if (Rocket.assignments.containsKey(r)) {
				if (Game.canLoad(Rocket.assignments.get(r), r)) {
					Game.load(Rocket.assignments.get(r), r);
					continue;
				}
				target = Rocket.assignments.get(r).tile();
					
			}
			else if (helpRequests.size() + enemyFactories.size() > 0) {
				// TODO this is probably slow
				for (Robot help: helpRequests) {
					if (target == null || Pathfinding.pathLength(r.tile(), help.tile()) < Pathfinding.pathLength(r.tile(), target)) {
						target = help.tile();
					}
				}
				for (Robot fact: enemyFactories) {
					if (target == null || Pathfinding.pathLength(r.tile(), fact.tile()) < Pathfinding.pathLength(r.tile(), target)) {
						target = fact.tile();
					}
				}
			} else {
				if (targets.size() == 0) {
					if (randomTargets.containsKey(r) && r.tile().distanceSquaredTo(randomTargets.get(r)) > 2) {
						target = randomTargets.get(r);
					} else {
						randomTargets.put(r, Game.getRandomLocation());
						target = randomTargets.get(r);
					}
				} else {
					if (Pathfinding.pathLength(targets.get(0), r.tile()) <= 2) {
						targets.remove(0);
					} else {
						target = targets.get(0);
					}
				}
			}
			if (r.unitType() == UnitType.Ranger) {
				Robot[] enemies = Game.senseCombatUnits(r.tile(), (long) Math.pow(Math.sqrt(Constants.RANGERRANGE) + 1, 2), Game.enemy());
				if (enemies.length == 0) {
					Direction d = Pathfinding.path(r.tile(), target);
					if (Game.canMove(r, d)) {
						Game.moveRobot(r, d);
						shoot(r);
					}
				} else {
					if (scoreMap.get(r) > 400 && r.health() > 30) {
						Direction move = Utilities.findNearestOccupiableDir(r.tile(), getAverageEnemyDirection(r));
						if (Game.canMove(r, move)) {
							Game.moveRobot(r, move);
							shoot(r);
						}
					} else {
						Direction move = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
						if (Game.canMove(r, move)) {
							shoot(r);
							Game.moveRobot(r, move);
						}
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
					if (scoreMap.get(r) > 400) {
						Direction move = Utilities.findNearestOccupiableDir(r.tile(), getAverageAllyDirection(r));
						if (Game.canMove(r, move)) {
							Game.moveRobot(r, move);
						}
					} else {
						Direction move = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
						if (Game.canMove(r, move)) {
							heal(r);
							Game.moveRobot(r, move);
						}
					}
				}
				heal(r);
			}
		}
	}
}
