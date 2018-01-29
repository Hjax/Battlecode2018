package rush;

import bc.UnitType;

public class GlobalStrategy {
	public static boolean rush = false;
	public static boolean rocketRush = false;
	public static int COMBATLIMIT = 50;
	public static void run() {
		if (GameInfoCache.enemyMages.size() > 0) 
		{
			rush = false;
			if (Game.researchInfo().getLevel(UnitType.Knight) < 3)
			{
				Game.resetResearch();
				Game.queueResearch(UnitType.Ranger);
	        	Game.queueResearch(UnitType.Healer);    	
	        	Game.queueResearch(UnitType.Healer);
	        	Game.queueResearch(UnitType.Healer);
	        	Game.queueResearch(UnitType.Rocket);
	        	Game.queueResearch(UnitType.Rocket);
	        	Game.queueResearch(UnitType.Ranger);
	        	Game.queueResearch(UnitType.Rocket);
			}
		}
		if (GameInfoCache.turnsSinceLastEnemy >= 100) {
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
