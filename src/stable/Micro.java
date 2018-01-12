package stable;

import java.util.*;
import bc.*;

public class Micro {

	private static Map<Robot, Tile> randomTargets = new HashMap<>();
	private static ArrayList<Tile> targets = new ArrayList<>();
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
	}
	
	public static void run() {
		startTurn();
		for (Robot r: GameInfoCache.allRangers) {
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
			if (target != null) {
				try {
					if (Game.canMove(r, Pathfinding.path(r.tile(), target))) {
						Game.moveRobot(r, Pathfinding.path(r.tile(), target));
					}
				} catch (Exception e) {
					
				}
			}
			if (Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy()).length > 0) {
				if (Game.isAttackReady(r) && Game.canAttack(r, Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy())[0])) {
					Game.attack(r, Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy())[0]);
					if (Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy()).length > 0) {
						newHelpRequests.add(Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy())[0]);
					}
				}
			}
		}
	}
}
