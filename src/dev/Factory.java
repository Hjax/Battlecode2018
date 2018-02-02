package dev;

import bc.UnitType;
import bc.*;


public class Factory {
	public static void run() {
		for (Robot r: Game.senseNearbyUnits(UnitType.Factory, Game.team())) {
			if (!r.isFactoryProducing()) {
				if (Game.round < Constants.FACTORYHALTROUND && Game.allyCombat.size() < GlobalStrategy.COMBATLIMIT) {
					if (Game.allyWorkers.size() == 0) {
						if (Game.canAffordRobot(UnitType.Worker)) {
							r.produceRobot(UnitType.Worker);
						} 
					}
					else if (Game.allyRangers.size() + Game.allyKnights.size() > (2 * (Game.allyHealers.size() + 1))) {
						if (Game.canAffordRobot(UnitType.Healer)) {
							r.produceRobot(UnitType.Healer);
						} 
					} 
					else if (GlobalStrategy.rush)
					{
						if (Game.canAffordRobot(UnitType.Knight)) {
							r.produceRobot(UnitType.Knight);
						} 
					} else if (Game.canAffordRobot(UnitType.Ranger)) {
						r.produceRobot(UnitType.Ranger);
					}
				}
			}
			if (r.structureGarrison().length > 0) {
				for (Direction d: Game.moveDirections) {
					if (r.canUnload(d)) {
						r.unload(d);
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
						if (blockingUnit.unitType() == UnitType.Worker && blockingUnit.team() == Game.TEAM && r.canLoad(blockingUnit))
						{
							r.load(blockingUnit);
							if (r.canUnload(d)) {
								r.unload(d);
								break;
							}
						}
						
					}
				}
			}
		}
	}
}
