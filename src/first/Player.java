package first;

import bc.*;


public class Player 
{
    public static void main(String[] args) {
        while (true) 
        {
        	Game.startTurn();
  
            System.out.println("Current round: "+ Game.round());
            if (Game.planet() == Planet.Earth) {
                long start = System.nanoTime();
                Worker.run();
                Factory.run();
                Micro.run();
                System.out.println("Time: " + (System.nanoTime() - start) / 1000000.0);
            }
            
            Game.nextTurn();
        }
    }
}