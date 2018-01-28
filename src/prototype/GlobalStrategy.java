package prototype;

public class GlobalStrategy {
	public static boolean rush = false;
	public static boolean rocketRush = false;
	public static int COMBATLIMIT = 50;
	public static void run() {
		if (GameInfoCache.enemyMages.size() > 0) rush = false;
		if (GameInfoCache.turnsSinceLastEnemy >= 100) {
			System.out.println("We won earth");
			COMBATLIMIT = 20;
			rocketRush = true;
		} else {
			COMBATLIMIT = 50;
			rocketRush = false;
		}
	}
}
