package dev;

import bc.UnitType;
import bc.*;

public class Factory {
	public static void run() {
		for (Robot r: Game.senseNearbyUnits(UnitType.Factory, Game.team())) {
			if (!(r.isFactoryProducing() > 0)) {
				if (Game.round < Constants.FACTORYHALTROUND && GameInfoCache.allyRangers.size() < Constants.RANGERLIMIT) {
					if (GameInfoCache.allyRangers.size() > (5 * (GameInfoCache.allyHealers.size() + 1))) {
						if (Game.canProduceRobot(r, UnitType.Healer)) {
							Game.produceRobot(r, UnitType.Healer);
						} 
					} else if (Game.canProduceRobot(r, UnitType.Ranger)) {
						Game.produceRobot(r, UnitType.Ranger);
					}
				}

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
