package first;

import bc.*;


public class Player {
    public static void main(String[] args) {
    	
        while (true) {
        	game.startTurn();
            System.out.println("Current round: "+ game.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = game.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);

                // Most methods on gc take unit IDs, instead of the unit objects themselves.
                if (game.isMoveReady(unit) && game.canMove(unit, Direction.Northwest)) {
                	Direction dir = Pathfinding.path(unit.location().mapLocation(), new MapLocation(Planet.Earth, 8, 8));
                	if (!(dir == Direction.Center)) {
                		game.moveRobot(unit, dir);
                	}
                    
                }
            }
            // Submit the actions we've done, and wait for our next turn.
            game.nextTurn();
        }
    }
}