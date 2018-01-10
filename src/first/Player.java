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
                    game.moveRobot(unit, Direction.Northwest);
                }
            }
            // Submit the actions we've done, and wait for our next turn.
            game.nextTurn();
        }
    }
}