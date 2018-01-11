package first;

import bc.*;

public class Worker {
	public static void run(Unit unit) {
		if (game.senseNearbyUnits(unit.location().mapLocation(), UnitType.Factory).length < 1) {
        	for (Direction dir: game.directions) {
        		if (game.canBlueprint(unit, UnitType.Factory, dir)) {
        			game.blueprint(unit, UnitType.Factory, dir);
        		}
        	}
		}
		else if (game.senseNearbyUnits(unit.location().mapLocation(), UnitType.Worker, game.team()).length < 5) {
        	for (Direction dir: game.directions) {
        		if (game.canReplicate(unit, dir)) {
        			game.replicate(unit, dir);
        		}
        	}
    	}
	}
}
