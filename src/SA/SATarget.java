package SA;

import java.util.*;

import bc.*;

public class SATarget {
	public static int iterations = 0;
	
	public static final int RANGER = UnitType.Ranger.ordinal();
	public static final int WORKER = UnitType.Worker.ordinal();
	public static final int HEALER = UnitType.Healer.ordinal();
	public static final int MAGE = UnitType.Mage.ordinal();
	public static final int KNIGHT = UnitType.Knight.ordinal();
	public static final int FACTORY = UnitType.Factory.ordinal();
	public static final int ROCKET = UnitType.Rocket.ordinal();
	
	public static final int KILLVALUE = 100;
	public static final int DAMAGEVALUE = 1;
	
	public static int mageDamage = 0;
	public static int rangerDamage = 0;
	
	public static int bestIteration = 0;
	
	public static final long MAXTIME = 5;
	public static long startTime = 0;
	
	public static int[][] aoe = new int[8192][9];
	public static int[] aoeLength = new int[8192];
	
	private static int[][] targets = new int[8192][0];
	private static int[] targetsLength = new int[8192];
	
	private static int[] hps = new int[8192];
	
	// targeting arrays, -1 means no targets, any other number is the predictable id of a unit to shoot
	private static int[] best = new int[8192];
	private static int[] current = new int[8192];
	
	private static double score = 0;
	private static double bestScore = -1 * Constants.INFINITY;
	
	// store the unit types of each predictable id, used for scoring
	private static int[] types = new int[8192];
	private static int[] damage = new int[8192];
	private static int[] shooters = new int[8192];
	
	public static void startTurn() {
		iterations = 0;
		hps = new int[8192];
		best = new int[8192];
		current = new int[8192];
		shooters = new int[Game.allyCombat.size()];
		targets = new int[8192][0];
		targetsLength = new int[8192];
		aoe = new int[8192][9];
		aoeLength = new int[8192];
		
		score = 0;
		bestScore = -1 * Constants.INFINITY;
		bestIteration = 0;
		
		int shootIndex = 0;
		
		Arrays.fill(best, -1);
		Arrays.fill(current, -1);
		
		mageDamage = Constants.attackDamage(UnitType.Mage);
		rangerDamage = Constants.attackDamage(UnitType.Ranger);
		
		for (Robot r: Game.allEnemies) {
			hps[r.predictableId()] = (int) r.health;
			types[r.predictableId()] = r.unitType().ordinal();
		}
		for (Robot r: Game.allyCombat) {
			if (r.unitType() == UnitType.Healer) continue;
			if (!r.onMap()) continue;
			if (r.attackHeat() >= 10) continue;
			boolean hasTarget = false;
			types[r.predictableId()] = r.unitType().ordinal();
			damage[r.predictableId()] = Constants.attackDamage(r.unitType());
			Robot[] myTargets = Game.senseNearbyUnits(r.tile(), Constants.attackRange(r.unitType()), Game.enemy());
			targets[r.predictableId()] = new int[256];
			for (Robot e: myTargets) {
				hasTarget = true;
				targets[r.predictableId()][targetsLength[r.predictableId()]++] = e.predictableId();
				if (hps[e.predictableId()] > 0) {
					if (r.unitType() == UnitType.Mage) {
						Robot[] adj = Game.senseNearbyUnits(e.tile(), 2);
						for (Robot f: adj) {
							aoe[e.predictableId()][aoeLength[e.predictableId()]++] = f.predictableId();
						}
					} else {
						current[r.predictableId()] = e.predictableId();
					}
				}
			}
			if (Micro.moves.containsKey(r) && Micro.moves.get(r) != Direction.Center) {
				hasTarget = true;
				myTargets = Game.senseNearbyUnits(r.tile().add(Micro.moves.get(r)), Constants.attackRange(r.unitType()), Game.enemy());
				outer: for (Robot e: myTargets) {
					for (int i = 0; i < targetsLength[r.predictableId()]; i++) {
						if (targets[r.predictableId()][i] == e.predictableId()) continue outer;
					}
					targets[r.predictableId()][targetsLength[r.predictableId()]++] = e.predictableId();
					targetsLength[r.predictableId()]++;
					if (hps[e.predictableId()] > 0) {
						if (r.unitType() == UnitType.Mage) {
							Robot[] adj = Game.senseNearbyUnits(e.tile(), 2);
							for (Robot f: adj) {
								aoe[e.predictableId()][aoeLength[e.predictableId()]++] = f.predictableId();
							}
						} else {
							current[r.predictableId()] = e.predictableId();
						}
					}
				}
			}
			if (hasTarget) {
				shooters[shootIndex++] = r.predictableId();
			}
		}
	}
	
	public static double damageValue(int type) {
		if (type == RANGER) {
			return 1.0;
		} else if (type == HEALER) {
			return 0.9;
		} else if (type == KNIGHT) {
			return 0.8;
		} else if (type == MAGE) {
			return 1.3;
		} else if (type == WORKER) {
			return 0.2;
		} else if (type == FACTORY) {
			return 0.5;
		} else if (type == ROCKET) {
			return 0.6;
		}
		return 0;
	}
	
	public static double killValue(int type) {
		if (type == RANGER) {
			return 1.0;
		} else if (type == HEALER) {
			return 1.2;
		} else if (type == KNIGHT) {
			return 0.8;
		} else if (type == MAGE) {
			return 1.3;
		} else if (type == WORKER) {
			return 0.01;
		} else if (type == FACTORY) {
			return 2.0;
		} else if (type == ROCKET) {
			return 0.6;
		}
		return 0;
	}
	
	public static void attack(int pid, int target) {
		boolean killed = false;
		if (types[pid] == MAGE) {
			for (int i = 0; i < aoeLength[target]; i++) {
				if (hps[aoe[target][i]] <= mageDamage) {
					killed = true;
				}
				hps[aoe[target][i]] -= mageDamage;
				// if we killed something or damaged something that wasnt already dead, update score
				
				if (killed) {
					score += killValue(types[aoe[target][i]]) * KILLVALUE;
				} else if (hps[aoe[target][i]] > 0) {
					score += damageValue(types[aoe[target][i]]) * DAMAGEVALUE * mageDamage;
				}
			}
		} else if (types[pid] == RANGER){
			if (hps[target] <= rangerDamage) {
				killed = true;
			}
			hps[target] -= rangerDamage;
			if (killed) {
				score += killValue(types[target]) * KILLVALUE;
			} else if (hps[target] > 0) {
				score += damageValue(types[target]) * DAMAGEVALUE * rangerDamage;
			}
		}
		current[pid] = target;
	}
	
	public static void reset(int pid) {
		boolean revived = false;
		if (current[pid] == -1) return;
		if (types[pid] == MAGE) {
			for (int i = 0; i < aoeLength[current[pid]]; i++) {
				hps[aoe[current[pid]][i]] += mageDamage;
                if (hps[aoe[current[pid]][i]] <= mageDamage) {
					revived = true;
				}
				if (revived) {
					score -= killValue(types[aoe[current[pid]][i]]) * KILLVALUE;
				} else if (hps[aoe[current[pid]][i]] > 0) {
					score -= damageValue(types[aoe[current[pid]][i]]) * DAMAGEVALUE * mageDamage;
				}
			}
		} else if (types[pid] == RANGER){
			hps[current[pid]] += rangerDamage;
			if (hps[current[pid]] <= rangerDamage) {
				revived = true;
			}
			if (revived) {
				score -= killValue(types[current[pid]]) * KILLVALUE;
			} else if (hps[current[pid]] > 0) {
				score -= damageValue(types[current[pid]]) * DAMAGEVALUE * rangerDamage;
			}
		}
		current[pid] = -1;
	}
	
	public static Robot getTarget(Robot r) {
		return Robot.getByPredictableId(best[r.predictableId()]);
	}
	
	public static void mutate() {
		double oldScore = score;
		int target = shooters[Game.rand.nextInt(shooters.length)];
		if (targetsLength[target] == 0) return; 
		int old = current[target];
		reset(target);
		attack(target, targets[target][Game.rand.nextInt(targetsLength[target])]);

		if (score < oldScore) {
			reset(target);
			attack(target, old);
			return;
		}
		if (score > bestScore) {
			bestIteration = iterations;
			best = current.clone();
			bestScore = score;
			System.out.println("Score improved to :" + bestScore);
		}
		
	}
	
	public static void run() {
		if (Game.allyCombat.size() == 0) return;
		startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime) < MAXTIME) {
			iterations++;
			mutate();
		}
		System.out.println("Ran " + iterations + " iterations in " + MAXTIME + " ms, best was found on iteration " + bestIteration);
	}
}
