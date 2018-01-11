package MechanicsTestPlayer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import bc.*;


public class Player {
    public static void main(String[] args) {
    	
    	int rocket = -1;
    	int worker = -1;
    	if (game.planet() == Planet.Earth)
    	{
    		worker = game.myUnits().get(0).id();
    	}

        while (true) {
        	try
        	{
        		game.startTurn();
                System.out.println("Current round: "+ game.round());
                game.queueResearch(UnitType.Rocket);
                if (game.planet() == Planet.Mars)
                {
                	game.nextTurn();
                	continue;
                }
                
                if (game.round() < 102)
                {
                	game.nextTurn();
                	continue;
                }
                if (game.round() == 102)
                {
                	game.blueprint(worker, UnitType.Rocket, Direction.North);
                	rocket = game.senseNearbyUnitsByType(game.unit(worker).location().mapLocation(), 2, UnitType.Rocket).get(0).id();
                	game.nextTurn();
                }
                
                if (game.unit(rocket).health() < 200)
                {
                	game.build(worker, rocket);
                }
                
                if (game.unit(rocket).health() > 196)
                {
                	if (game.canLoad(rocket, worker))
                	{
                		game.load(rocket, worker);
                		game.nextTurn();
                	}
                }
                if (game.unit(rocket).structureGarrison().size() > 0 && game.round() == 300)
                {
                	System.out.println(game.unit(rocket).rocketIsUsed());
                	game.launchRocket(rocket, new MapLocation(Planet.Mars, 0, 0));
                }
                
                
                
                
                // Submit the actions we've done, and wait for our next turn.
                game.nextTurn();
        	}
        	catch (Exception e)
        	{
        		e.printStackTrace();
        		game.nextTurn();
        	}
        	
        }
    }
}