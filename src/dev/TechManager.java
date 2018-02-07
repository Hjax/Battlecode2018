package dev;

import bc.*;

public class TechManager {

	public static void run() {
		ResearchInfo info = Game.researchInfo();
		if (!info.hasNextInQueue()) {
			if (GlobalStrategy.rocketRush && info.getLevel(UnitType.Rocket) < 3) {
				Game.queueResearch(UnitType.Rocket);
				return;
			}
			if (info.getLevel(UnitType.Worker) == 0 && Game.karboniteLocations.size() >= 30) {
				Game.queueResearch(UnitType.Worker);
				return;
			}
			if (info.getLevel(UnitType.Ranger) == 0 && Game.allyRangers.size() > Game.allyMages.size()) {
				Game.queueResearch(UnitType.Ranger);
				return;
			}
			if (info.getLevel(UnitType.Mage) < 4 && ((GlobalStrategy.rush && info.getLevel(UnitType.Mage) == 0) || info.getLevel(UnitType.Healer) == 3)) {
				Game.queueResearch(UnitType.Mage);
				return;
			}
			if (info.getLevel(UnitType.Healer) < 3) {
				Game.queueResearch(UnitType.Healer);
				return;
			}
			if (info.getLevel(UnitType.Rocket) < 3) {
				Game.queueResearch(UnitType.Rocket);
				return;
			}
		}
	}
}
