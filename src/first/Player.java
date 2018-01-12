package first;

import bc.*;


public class Player 
{
    public static void main(String[] args) {
        while (true) 
        {
        	long time = System.nanoTime();
        	System.out.println("Current round: "+ Game.round());
        	Game.startTurn();
        	
            
            if (Game.planet() == Planet.Earth) {
            	
                Worker.run();
                
                Factory.run();
                
                Micro.run();
                System.out.printf("Turn took %d miliseconds\n", (System.nanoTime() - time)/1000000);
            }
            
            Game.nextTurn();
            
        }
    }
}