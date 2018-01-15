package replicateDirection;

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
	public static int score(Robot r, Tile square, Tile target) {
		int score = 0;
		// todo account for unit types other than ranger
		// todo check if enemy threats is even helping
		Robot[] enemies = Game.senseNearbyUnits(square, r.attackRange(), UnitType.Ranger, Game.enemy());
		Robot[] tooClose = Game.senseNearbyUnits(square, 10, UnitType.Ranger, Game.enemy());
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
		
	public static void run() {
		startTurn();
		for (Robot r: GameInfoCache.allyRangers) {
			Tile target = null;
			if (helpRequests.size() + enemyFactories.size() > 0) {
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
					if (randomTargets.containsKey(r)) {
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
			if (Game.isMoveReady(r)) {
				Direction best = null;
				int bestScore = 0;
				for (Direction d: Game.directions) {
					if ((Game.isPassableTerrainAt(r.tile().add(d)) && Game.canMove(r, d)) || d == Direction.Center) {
						int current = score(r, r.tile().add(d), target);
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
			if (Game.isAttackReady(r)) {
				Robot[] combat = Game.senseCombatUnits(r.tile(), r.attackRange(), Game.enemy());
				if (combat.length > 0) {
					Robot best = null;
					for (Robot enemy: combat) {
						if (enemy.health() - damage[enemy.predictableId()] > 0) {
							if (best == null || ((enemy.health() - damage[enemy.predictableId()]) <  (best.health() - damage[best.predictableId()]))) {
								best = enemy;
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
							Game.attack(r, civilian[0]);
						}
					}
				}
			}
		}
	}
}
