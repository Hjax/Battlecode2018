package prototype;

public class CheapMicro {
	public static void run() {
		for (Robot r: GameInfoCache.allyCombat) {
			if (!Game.isAttackReady(r) || r.attackHeat() >= 10) continue;
			switch (r.unitType()) {
				case Healer:
					Robot[] allies = Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.team());
					for (Robot e: allies) {
						if (e.health() < e.maxHealth() && Game.canHeal(r, e)) {
							Game.heal(r, e);
							break;
						}
					}
				default:
					Robot[] enemies = Game.senseNearbyUnits(r.tile(), r.attackRange(), Game.enemy());
					for (Robot e: enemies) {
						if (Game.canAttack(r, e)) {
							Game.attack(r, e);
							break;
						}
					}
			}
		}
	}
}
