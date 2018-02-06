package SA;

import java.util.*;
import bc.*;

public class Movement {
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
	
	public static Map<Robot, Direction> moves = new HashMap<>(); 
	private static Set<Tile> taken = new HashSet<>();
	
	public static void startTurn() {
		// TODO purge help requests only every few rounds 
		helpRequests = newHelpRequests;
		newHelpRequests = new ArrayList<>();
		//System.out.println("Requests: " + helpRequests.size());
		//System.out.println("Random Targets: " + randomTargets.size());
	}
	
	public static boolean canMove(Robot r, Direction d) {
		return r.movementHeat() < 10 && Game.isOccupiable(r.tile().add(d)) > 0 && !taken.contains(r.tile().add(d)) ;
	}
	
	public static void move(Robot r, Direction d) {
		taken.remove(r.tile());
		taken.add(r.tile().add(d));
		moves.put(r, d);
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
				if (canMove(r, d)) {
					move(r, d);
				}
			} else if (enemies.length * Constants.RANGERDAMAGE >= r.health() || (Game.enemyKnights.size() > 0 && Game.senseNearbyUnits(r.tile(), Constants.attackRange(r.unitType()), UnitType.Knight, Game.enemy()).length > 0)){
				Direction d = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
				if (canMove(r, d)) {
					move(r, d);
				}
			}
		}
		else if (r.unitType() == UnitType.Healer) {
			Robot[] enemies = Game.senseCombatUnits(r.tile(), (long) Math.pow(Math.sqrt(Constants.attackRange(UnitType.Ranger)) + 1, 2), Game.enemy());
			if (enemies.length == 0 && target != null) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (canMove(r, d)) {
					move(r, d);
				}
			} else if (enemies.length != 0) {
				Direction d = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
				if (canMove(r, d)) {
					move(r, d);
				}
			}
		}
		else if (r.unitType() == UnitType.Knight) {
			Robot[] enemies = Game.senseNearbyUnits(r.tile(), Constants.visionRange(UnitType.Ranger), Game.enemy());
			if (enemies.length == 0 && target != null) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (canMove(r, d)) {
					move(r, d);
				}
			}
		}
		else if (r.unitType() == UnitType.Mage) {
			Robot[] enemies = Game.senseCombatUnits(r.tile(), Constants.attackRange(UnitType.Mage), Game.enemy());
			if (enemies.length == 0 && target != null) {
				Direction d = Pathfinding.path(r.tile(), target);
				if (canMove(r, d)) {
					move(r, d);
				}
			} else if (enemies.length != 0){
				Direction d = Utilities.findNearestOccupiableDir(r.tile(), Utilities.oppositeDir(getAverageEnemyDirection(r)));
				if (canMove(r, d)) {
					move(r, d);
				}
			}
		}
 	}
 	
	public static void run() {
		for (Robot r: Game.allyCombat) {
			if (r.inGarrison() || r.inSpace() || !r.onMap()) continue;
			micro(r);
		}
		SATarget.run();
		for (Robot r: Game.allyCombat) {
			try {
				if (SATarget.getTarget(r) != null ) {
					if (r.canAttack(SATarget.getTarget(r))) {
						r.attack(SATarget.getTarget(r));
						r.move(moves.get(r));
					} else {
						
						r.attack(SATarget.getTarget(r));
					}
				} else {
					r.move(moves.get(r));
				}
			} catch (Exception e) {
				System.out.println("Move error");
				e.printStackTrace();
			}
		}
	}
}
