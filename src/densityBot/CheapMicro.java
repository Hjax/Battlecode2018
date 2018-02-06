package densityBot;

public class CheapMicro {
	public static void run() {
		for (Robot r: Game.allyCombat) {
			if (!r.isAttackReady()) continue;
			switch (r.unitType()) {
				case Healer:
					Robot[] allies = Game.senseNearbyUnits(r.tile(), Constants.attackRange(r.unitType()), Game.team());
					for (Robot e: allies) {
						if (e.health() < Constants.maxHealth(e.unitType())) {
							r.attack(e);
							break;
						}
					}
				default:
					Robot[] enemies = Game.senseNearbyUnits(r.tile(), Constants.attackRange(r.unitType()), Game.enemy());
					for (Robot e: enemies) {
						if (r.canAttack(e)) {
							r.attack(e);
							break;
						}
					}
			}
		}
	}
}
