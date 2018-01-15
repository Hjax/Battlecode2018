package factoryCentric;

import bc.UnitType;
import bc.*;

public class Factory {
	public static void run() {
		for (Robot r: Game.senseNearbyUnits(UnitType.Factory, Game.team())) {
			if (!(r.isFactoryProducing() > 0) && Game.canProduceRobot(r, UnitType.Ranger)) {
				Game.produceRobot(r, UnitType.Ranger);
			}
			if (r.structureGarrison().length > 0) {
				for (Direction d: Game.moveDirections) {
					if (Game.canUnload(r, d)) {
						Game.unload(r, d);
					}
				}
			}
		}
	}
}
