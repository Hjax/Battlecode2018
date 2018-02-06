package densityBot;

import bc.UnitType;

public class GlobalStrategy {
	public static boolean rush = false;
	public static boolean rocketRush = false;
	public static int COMBATLIMIT = 50;
	public static void run() {
		if (Game.turnsSinceLastEnemy >= 100) {
			COMBATLIMIT = 20;
			rocketRush = true;
		} else {
			COMBATLIMIT = 50;
			rocketRush = false;
		}
		if (Game.gc.getTimeLeftMs() < Constants.TIMELOW) {
			COMBATLIMIT = 20;
		}
	}
}
