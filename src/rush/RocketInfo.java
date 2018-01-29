package rush;
import bc.*;

public class RocketInfo {
	public long launchRound;
	public long landRound;
	public int workerCount = 0;
	public int combatCount = 0;
	public RocketInfo(Robot g) {
		launchRound = Game.round();
		for (Robot r: g.structureGarrison()) {
			if (r.unitType() == UnitType.Worker)
			{
				workerCount++;
			}
			else
			{
				combatCount++;
			}
		}
		landRound = Game.round() + Game.currentDurationOfFlight();
	}
	
}
