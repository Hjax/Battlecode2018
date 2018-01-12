package workerCentric;

import bc.*;


public class Player 
{
    public static void main(String[] args) 
    {
    	if (Game.planet() == Planet.Earth) {
        	System.out.println(Pathfinding.pathLength(Tile.getInstance(Planet.Earth, 6, 2), Tile.getInstance(Planet.Earth, 6, 18)));
        	//System.out.println(Pathfinding.pathLength(new MapLocation(Planet.Earth, 6, 2), new MapLocation(Planet.Earth, 6, 2)));
        	System.out.println(Pathfinding.pathLength(Tile.getInstance(Planet.Earth, 6, 2), Tile.getInstance(Planet.Earth, 6, 3)));
    	}
        while (true) 
        {
        	Game.startTurn();
  
        	System.out.printf("From 0,0 to 3,3 is %d", Pathfinding.pathLength(Tile.getInstance(new MapLocation(Game.planet(), 0, 0)), Tile.getInstance(new MapLocation(Game.planet(), 3, 3))));
        	System.out.printf("From 17,4 to 17,17 is %d", Pathfinding.pathLength(Tile.getInstance(new MapLocation(Game.planet(), 17, 4)), Tile.getInstance(new MapLocation(Game.planet(), 17, 17))));
            System.out.println("Current round: "+ Game.round());

            //Worker.run();
            Game.nextTurn();
        }
    }
}