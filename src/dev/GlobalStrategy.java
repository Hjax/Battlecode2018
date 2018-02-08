package dev;

import bc.*;

public class GlobalStrategy {
	public static boolean rush = false;
	public static boolean rocketRush = false;
	public static int COMBATLIMIT = 50;
	public static double HEALERRATIO = 2;
	public static void run() {
		if (Game.turnsSinceLastEnemy >= 100) {
			COMBATLIMIT = 10;
			rocketRush = true;
		} else {
			COMBATLIMIT = 50;
			rocketRush = false;
		}
		ResearchInfo upgrades = Game.researchInfo();
		if (upgrades.getLevel(UnitType.Healer) == 3) {
			HEALERRATIO = 1.0;
		}
		if (Game.gc.getTimeLeftMs() < Constants.TIMELOW) {
			COMBATLIMIT = 20;
		}
	}
}
