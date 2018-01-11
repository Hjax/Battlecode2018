package first;

import bc.*;


public class Player {
    public static void main(String[] args) {
    	
        while (true) {
        	game.startTurn();
            System.out.println("Current round: "+ game.round());

            for (Unit unit: game.myUnits()) {	
                switch (unit.unitType()) {
                	case Factory:
                		break;
                	case Healer:
                		break;
                	case Knight:
                		break;
                	case Mage:
                		break;
                	case Ranger:
                		break;
                	case Rocket:
                		break;
                	case Worker:
                		Worker.run(unit);
                		break;
                	default:
                		break;

                }
            }
            game.nextTurn();
        }
    }
}