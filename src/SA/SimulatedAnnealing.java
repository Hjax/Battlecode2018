package SA;

import java.util.*;
import bc.*;

public class SimulatedAnnealing {
	private final static class Move {
		// if we dont have a move, we use direction.center, if we dont have a target, we use the robot executing the move
		public final Direction move;
		public final Robot target;
		public final boolean moveFirst;
		public Move(Direction m, Robot t, boolean mf) {
			move = m;
			target = t;
			moveFirst = mf;
		}
	}
	private static long start_time;
	private static long max_time;
	private static Map<Robot, Map<Direction, List<Robot>>> attacks;
	private static Map<Robot, Move> state;
	private static Boolean[] occupied;
	static {
		occupied = new Boolean[Constants.WIDTH * Constants.HEIGHT];
	}
	public static Random rand = new Random();
	public static boolean takeMove(int score, int testScore) {
		if (testScore > score) return true;
		int delta = testScore - score;
		double chance = 1.0 / (1 + (Math.exp(delta / getTemperature())));
		return rand.nextFloat() < chance;
	}
	public static long getTemperature() {
		return max_time - (System.nanoTime() - start_time);
	}
	public static void anneal() {
		start_time = System.nanoTime();
	}
	public static void generateAttacks() {
		for (Robot r: Game.myUnits()) {
			// TODO add support for other unit types
			if (r.unitType() == UnitType.Ranger) {
				Map<Direction, List<Robot>> currentAttacks = new HashMap<>();
				for (Direction d: Game.directions) {
					if (d == Direction.Center || Game.isPassableTerrainAt(r.tile().add(d))) {
						currentAttacks.put(d, Arrays.asList(Game.senseCombatUnits(r.tile(), r.attackRange(), Game.team())));
					}
				}
			}
		}
	}
}
