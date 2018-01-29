package dev;

import bc.UnitType;
import bc.*;


public class Factory {
	public static void run() {
		for (Robot r: Game.senseNearbyUnits(UnitType.Factory, Game.team())) {
			if (!(r.isFactoryProducing() > 0)) {
				if (Game.round < Constants.FACTORYHALTROUND && GameInfoCache.allyCombat.size() < GlobalStrategy.COMBATLIMIT) {
					if (GameInfoCache.allyWorkers.size() == 0) {
						if (Game.canProduceRobot(r, UnitType.Worker)) {
							Game.produceRobot(r, UnitType.Worker);
						} 
					}
					else if (GameInfoCache.allyRangers.size() + GameInfoCache.allyKnights.size() > (2 * (GameInfoCache.allyHealers.size() + 1))) {
						if (Game.canProduceRobot(r, UnitType.Healer)) {
							Game.produceRobot(r, UnitType.Healer);
						} 
					} 
					else if (GlobalStrategy.rush)
					{
						if (Game.canProduceRobot(r, UnitType.Knight)) {
							Game.produceRobot(r, UnitType.Knight);
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
			if (r.structureGarrison().length > 0)
			{
				for (Direction d: Game.moveDirections) {
					Tile targetTile = Utilities.offsetInDirection(r.tile(), d, 1);
					if (Game.hasUnitAtLocation(targetTile))
					{
						Robot blockingUnit = Game.senseUnitAtLocation(targetTile);
						if (blockingUnit.unitType() == UnitType.Worker && blockingUnit.team() == Game.TEAM && Game.canLoad(r, blockingUnit))
						{
							Game.load(r, blockingUnit);
							if (Game.canUnload(r, d)) {
								Game.unload(r, d);
								break;
							}
						}
						
					}
				}
			}
		}
	}
}
