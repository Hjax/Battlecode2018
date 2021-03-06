package workerRush;

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
	}
	public static int scoreRangers(Robot r, Tile square, Tile target) {
		int score = 0;
		// todo account for unit types other than ranger
		// todo check if enemy threats is even helping

		Robot[] enemies = Game.senseCombatUnits(square, (long) Math.pow(Math.sqrt(r.attackRange()) + 1, 2), Game.enemy());
		Robot[] nearbyRangers = Game.senseNearbyUnits(square, 4, UnitType.Ranger, Game.team());
		Robot[] nearbyHealers = Game.senseNearbyUnits(square, Constants.HEALERRANGE, UnitType.Healer, Game.team());
		
		if (enemies.length * r.damage() >= r.health()) {
			score -= 200;
		}
		
		for (Robot e: enemies) {
			score -= 250/square.distanceSquaredTo(e.tile());
			if (square.distanceSquaredTo(e.tile()) >= (r.attackRange() - 12)) {
				score += 20;
			}
		}
		
		for (Robot a:nearbyRangers)
		{
			if (a != r)
			{
				score -= 5/square.distanceSquaredTo(a.tile());
			}
		}
		score += nearbyHealers.length * 3;
		
		if (r.attackHeat() >= 10) {
			score -= 5 * enemies.length;
		}

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
		Robot[] allyRangers = Game.senseNearbyUnits(square, r.attackRange(), UnitType.Ranger, Game.team());
		Robot[] allyHealers = Game.senseNearbyUnits(square, r.attackRange(), UnitType.Healer, Game.team());
		for (Robot a:allyRangers)
		{
			if (a != r)
			{
				score += 2/square.distanceSquaredTo(a.tile());
			}
			
		}
		
		if (enemies.length * r.damage() > r.health()) {
			score -= 100;
		}
		for (Robot a:allyHealers)
		{
			if (a != r)
			{
				score -= 2/square.distanceSquaredTo(a.tile());
			}
			
		};

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
		}
		System.out.println("Finding target " + time / 1000000.0);
	}
}
