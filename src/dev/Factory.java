package dev;

import bc.UnitType;
import bc.*;


public class Factory {
	public static void run() {
		for (Robot r: Game.senseNearbyUnits(UnitType.Factory, Game.team())) {
			if (!r.isBuilt) continue;
			if (!r.isFactoryProducing()) {
				if (Game.allyWorkers.size() == 0) {
					if (Game.canAffordRobot(UnitType.Worker)) {
						r.produceRobot(UnitType.Worker);
					} 
				}
				else if (Game.round < Constants.FACTORYHALTROUND && Game.allyCombat.size() < GlobalStrategy.COMBATLIMIT) {
					if (Game.allyRangers.size() + Game.allyKnights.size() > (GlobalStrategy.HEALERRATIO * (Game.allyHealers.size() + 1))) {
						if (Game.canAffordRobot(UnitType.Healer)) {
							r.produceRobot(UnitType.Healer);
						} 
					} 
					else if (closestEnemyFactoryDistance(r) <= Constants.MAGERUSHDISTANCE || (closestEnemyFactoryDistance(r) <= Constants.KNIGHTDISTANCE && (Game.enemyKnights.size() > (Game.enemyRangers.size() + Game.enemyMages.size())))) {
						if (Game.canAffordRobot(UnitType.Mage)) {
							r.produceRobot(UnitType.Mage);
						} 
					} else if (Game.canAffordRobot(UnitType.Ranger)) {
						r.produceRobot(UnitType.Ranger);
					}
				}
			}
			
			if (r.structureGarrison().length > 0)
			{
				boolean unload = true;
				Robot best = null;
				Robot[] nearbyEnemies = Game.senseCombatUnits(r.tile(), 30, Game.ENEMY);
				System.out.printf("\t\t\tTHERE ARE %d enemies nearby\n", nearbyEnemies.length);
				if (Game.enemyFactories.size() > 0 && r.structureGarrison().length < 2 && nearbyEnemies.length == 0) 
				{
					for (Robot f: Game.enemyFactories) 
					{
						if (best == null || Pathfinding.pathLength(f.tile(), r.tile()) < Pathfinding.pathLength(best.tile(), r.tile())) 
						{
							best = f;
						}
					}
					if (best.structureIsBuilt())
					{
						if (r.tile().distanceSquaredTo(best.tile()) < 50)
						{
							unload = false;
						}
					}
				}
				if (unload)
				{
					for (Direction d: Game.moveDirections) {
						if (r.canUnload(d)) {
							Robot unloadedUnit = r.structureGarrison()[0];

							Game.allAllies.add(unloadedUnit);
							switch (unloadedUnit.unitType())
							{
								case Worker:
									Game.allyWorkers.add(unloadedUnit);
									break;
								case Ranger:
									Game.allyRangers.add(unloadedUnit);
									Game.allyCombat.add(unloadedUnit);
									break;
								case Mage:
									Game.allyMages.add(unloadedUnit);
									Game.allyCombat.add(unloadedUnit);
									break;
								case Knight:
									Game.allyKnights.add(unloadedUnit);
									Game.allyCombat.add(unloadedUnit);
									break;
								case Healer:
									Game.allyHealers.add(unloadedUnit);
									Game.allyCombat.add(unloadedUnit);
									break;
								default:
									break;
							}
							r.unload(d);
							unloadedUnit.update();
							if (r.structureGarrison().length == 0)
							{
								break;
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
										Robot unloadedUnit = r.structureGarrison()[0];

										Game.allAllies.add(unloadedUnit);
										
										switch (unloadedUnit.unitType())
										{
											case Worker:
												Game.allyWorkers.add(unloadedUnit);
												break;
											case Ranger:
												Game.allyRangers.add(unloadedUnit);
												Game.allyCombat.add(unloadedUnit);
												break;
											case Mage:
												Game.allyMages.add(unloadedUnit);
												Game.allyCombat.add(unloadedUnit);
												System.out.printf("\t\t\tspawned a mage\n");
												break;
											case Knight:
												Game.allyKnights.add(unloadedUnit);
												Game.allyCombat.add(unloadedUnit);
												break;
											case Healer:
												Game.allyHealers.add(unloadedUnit);
												Game.allyCombat.add(unloadedUnit);
												break;
											default:
												break;
										}
										r.unload(d);
										unloadedUnit.update();
										if (r.structureGarrison().length == 0)
										{
											break;
										}
										
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	public static int closestEnemyFactoryDistance(Robot r) {
		Robot best = null;
		if (Game.enemyFactories.size() == 0) {
			return Constants.INFINITY;
		}
		for (Robot f: Game.enemyFactories) {
			if (best == null || Pathfinding.pathLength(f.tile(), r.tile()) < Pathfinding.pathLength(best.tile(), r.tile())) {
				if (Pathfinding.pathLength(f.tile(), r.tile()) != -1) {
					best = f;
				}
			}
		}
		if (best == null) {
			return Constants.INFINITY;
		}
		return Pathfinding.pathLength(best.tile(), r.tile());
	}
}
