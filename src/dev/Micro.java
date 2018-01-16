package dev;

import java.util.*;
import bc.*;

public class Micro {

	private static Map<Robot, Tile> randomTargets = new HashMap<>();
	private static ArrayList<Tile> targets = new ArrayList<>();
	private static int[] damage = new int[5000];
	static {
		for (Tile loc:Constants.startingEnemiesLocation) {
			targets.add(loc);
		}
	}
	private static ArrayList<Robot> enemyFactories = new ArrayList<>();
	private static ArrayList<Robot> helpRequests = new ArrayList<>();
	private static ArrayList<Robot> newHelpRequests = new ArrayList<>();
	
	private static void startTurn() {
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
		damage = new int[5000];
	}
	public static int scoreRangers(Robot r, Tile square, Tile target) {
		int score = 0;
		// todo account for unit types other than ranger
		// todo check if enemy threats is even helping
		Robot[] enemies = Game.senseCombatUnits(square, r.attackRange(), Game.enemy());
		Robot[] tooClose = Game.senseCombatUnits(square, 10, Game.enemy());
		if (enemies.length * r.damage() > r.health()) {
			score -= 100;
		}
		if (enemies.length == 1) {
			score += 40;
			if (enemies[0].health() < r.health()) {
				score += 60;
			}
		}
		score -= enemies.length * 2;
		score -= tooClose.length;

		if (target != null) {
			score -= Pathfinding.pathLength(square,  target) * 10;
		}
		return score;
	}
	
	public static int scoreHealers(Robot r, Tile square, Tile target) {
		int score = 0;
		Robot[] enemies = Game.senseCombatUnits(square, Constants.RANGERRANGE, Game.enemy());
		if (enemies.length > 0) {
			int enemy_score = 0;
			for (Robot enemy: enemies) {
				enemy_score += enemy.tile().distanceSquaredTo(square);
			}
			enemy_score = enemy_score / enemies.length;
			score -= enemy_score;
		}
		Robot[] allies = Game.senseCombatUnits(square, r.attackRange(), Game.team());
		if (enemies.length * r.damage() > r.health()) {
			score -= 100;
		}
		score += allies.length;

		if (target != null) {
			score -= Pathfinding.pathLength(square,  target) * 10;
		}
		return score;
	}
		
	public static void run() {
		startTurn();
		long time = 0;
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
					if (randomTargets.containsKey(r) && r.tile().distanceSquaredTo(randomTargets.get(r)) > 0) {
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

			long start = System.nanoTime();
			if (Game.isMoveReady(r)) {
				Direction best = null;
				int bestScore = 0;
				for (Direction d: Game.directions) {
					if ((Game.isPassableTerrainAt(r.tile().add(d)) && Game.canMove(r, d)) || d == Direction.Center) {
						int current = -1 * Constants.INFINITY;
						switch (r.unitType()) {
							case Ranger:
								current = scoreRangers(r, r.tile().add(d), target);
								break;
							case Healer:
								current = scoreHealers(r, r.tile().add(d), target);
								break;
							default:
								break;
						}
						
						if (best == null || current > bestScore) {
							bestScore = current;
							best = d;
						}
					}
				}
				if (best != null && Game.canMove(r, best)) {
					Game.moveRobot(r, best);
				}
			}
			time += System.nanoTime() - start;
			if (r.unitType() == UnitType.Healer && Game.isHealReady(r)) {
				Robot best = null;
				Robot[] healee = Game.senseCombatUnits(r.tile(), r.attackRange(), Game.TEAM);
				for (Robot ally: healee) {
					if (ally.health() < ally.maxHealth()) {
						if (best == null || (ally.health() - damage[ally.predictableId()] < best.health())) {
							best = ally;
						}
					}
				}
				if (best != null && Game.canHeal(r, best)) {
					Game.heal(r, best);
					damage[best.predictableId()] += r.damage();
				}
			}
			else if (Game.isAttackReady(r)) {
				Robot[] combat = Game.senseCombatUnits(r.tile(), r.attackRange(), Game.enemy());
				if (combat.length > 0) {
					Robot best = null;
					for (Robot enemy: combat) {
						if (enemy.location().isOnMap() && Game.canAttack(r, enemy)) {
							if (enemy.health() - damage[enemy.predictableId()] > 0) {
								if (best == null || ((enemy.health() - damage[enemy.predictableId()]) <  (best.health() - damage[best.predictableId()]))) {
									best = enemy;
								}
							}
						}
					}
					if (best != null && Game.canAttack(r, best)) {
						newHelpRequests.add(Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy())[0]);
						damage[best.predictableId()] += r.damage();
						Game.attack(r, best);
					}
				} else {
					Robot[] civilian = Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy());
					if (civilian.length > 0) {
						if (Game.canAttack(r, civilian[0])) {
							newHelpRequests.add(Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy())[0]);
							Game.attack(r, civilian[0]);
						}
					}
				}
			}
		}
		System.out.println("Finding target " + time / 1000000.0);
	}
}
