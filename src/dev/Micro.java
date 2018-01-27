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
			score += 20;
			if (enemies[0].health() <= r.health()) {
				score += 30;
			}
		}
		score -= enemies.length * 2;
		score -= tooClose.length;

		if (target != null) {
			score -= Pathfinding.pathLength(square,  target);
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
			score -= Pathfinding.pathLength(square,  target);
		}
		return score;
	}
	
	public static int scoreKnights(Robot r, Tile square, Tile target) {
		int score = 0;
		Robot[] enemies = Game.senseNearbyUnits(square, 2, UnitType.Factory, Game.enemy());
		if (enemies.length > 0)
		{
			score += 1000;
		}
		enemies = Game.senseCombatUnits(square, Constants.RANGERRANGE, Game.enemy());
		if (enemies.length > 0) {
			for (Robot enemy: enemies) {
				score += 30/enemy.tile().distanceSquaredTo(square);
			}
		}
		
		enemies = Game.senseNearbyUnits(square, Constants.RANGERRANGE, UnitType.Worker, Game.enemy());
		if (enemies.length > 0) {
			for (Robot enemy: enemies) {
				score += 2/enemy.tile().distanceSquaredTo(square);
			}
		}
		

		if (target != null) {
			score -= Pathfinding.pathLength(square,  target);
		}
		return score;
	}
		
	public static void run() {
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

			long start = System.nanoTime();
			if (Game.isMoveReady(r)) {
				Robot[] enemies = Game.senseCombatUnits(r.tile(), (long) Math.pow(Math.sqrt(Constants.RANGERRANGE + 2), 2), Game.enemy());
				if (enemies.length == 0) {
					Direction move = Utilities.findNearestOccupiableDir(r.tile(), Pathfinding.path(r.tile(), target));
					if (Game.canMove(r, move)) {
						Game.moveRobot(r, move);
					}
				} else {
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
							case Knight:
								current = scoreKnights(r, r.tile().add(d), target);
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
			}
			time += System.nanoTime() - start;
			if (r.unitType() == UnitType.Healer && Game.isHealReady(r)) {
				Robot best = null;
				Robot[] healee = Game.senseCombatUnits(r.tile(), r.attackRange(), Game.TEAM);
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
			else if (Game.isAttackReady(r)) {
				Robot[] fact = Game.senseNearbyUnits(r.tile(), r.attackRange(), UnitType.Factory, Game.enemy());
				if (fact.length > 0) {
					Robot best = null;
					for (Robot enemy: fact) {
						if (enemy.location().isOnMap() && Game.canAttack(r, enemy)) {
							if (enemy.health() > 0) {
								if (best == null || (enemy.health() <  (best.health()))) {
									best = enemy;
								}
							}
						}
					}
					if (best != null && Game.canAttack(r, best)) {
						newHelpRequests.add(best.tile());
						Game.attack(r, best);
					}
				} else {
					Robot[] combat = Game.senseCombatUnits(r.tile(), r.attackRange(), Game.enemy());
					if (combat.length > 0) {
						Robot best = null;
						for (Robot enemy: combat) {
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
						}
					} else {
						Robot[] civilian = Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy());
						if (civilian.length > 0) {
							Robot best = null;
							for (Robot enemy: civilian) {
								if (enemy.location().isOnMap() && Game.canAttack(r, enemy)) {
									if (enemy.health() > 0) {
										if (best == null || (enemy.health() <  best.health())) {
											best = enemy;
										}
									}
								}
							}
							if (best != null && Game.canAttack(r, best)) {
								newHelpRequests.add(best.tile());
								Game.attack(r, best);
							}
						}
					}
				}
			}
			if (r.unitType() == UnitType.Knight && Game.researchInfo().getLevel(UnitType.Knight) >= 3 && Game.isJavelinReady(r)) {
				Robot[] fact = Game.senseNearbyUnits(r.tile(), r.abilityRange(), UnitType.Factory, Game.enemy());
				if (fact.length > 0) {
					Robot best = null;
					for (Robot enemy: fact) {
						if (enemy.location().isOnMap() && Game.canJavelin(r, enemy)) {
							if (enemy.health() > 0) {
								if (best == null || (enemy.health() <  (best.health()))) {
									best = enemy;
								}
							}
						}
					}
					if (best != null && Game.canJavelin(r, best)) {
						newHelpRequests.add(best.tile());
						Game.javelin(r, best);
					}
				} else {
					Robot[] combat = Game.senseCombatUnits(r.tile(), r.abilityRange(), Game.enemy());
					if (combat.length > 0) {
						Robot best = null;
						for (Robot enemy: combat) {
							if (enemy.location().isOnMap() && Game.canJavelin(r, enemy)) {
								if (enemy.health() > 0) {
									if (best == null || enemy.health() < best.health()) {
										best = enemy;
									}
								}
							}
						}
						if (best != null && Game.canJavelin(r, best)) {
							newHelpRequests.add(best.tile());
							Game.javelin(r, best);
						}
					} else {
						Robot[] civilian = Game.senseNearbyUnits(r.tile(), r.abilityRange(), Game.enemy());
						if (civilian.length > 0) {
							Robot best = null;
							for (Robot enemy: civilian) {
								if (enemy.location().isOnMap() && Game.canJavelin(r, enemy)) {
									if (enemy.health() > 0) {
										if (best == null || (enemy.health() <  best.health())) {
											best = enemy;
										}
									}
								}
							}
							if (best != null && Game.canJavelin(r, best)) {
								newHelpRequests.add(best.tile());
								Game.javelin(r, best);
							}
						}
					}
				}
			}
		}
		System.out.println("Finding target " + time / 1000000.0);
	}
}
