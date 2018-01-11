package first;

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
  
        	
            System.out.println("Current round: "+ Game.round());

            Worker.run();
            Game.nextTurn();
        }
    }
}