package SA;

import bc.*;


public class Player 
{
	static long workerTime = 0;
    public static void main(String[] args) {
        while (true) 
        {
        	System.out.println("Current round: "+ Game.round());
        	Game.startTurn();
        	
            
            if (Game.planet() == Planet.Earth) {
            	long time = System.nanoTime();
                Worker.run();
                workerTime += System.nanoTime() - time;
                //System.out.printf("Total worker time is %d miliseconds\n", (workerTime)/1000000);
                Factory.run();
                
                Micro.run();
                
            }
            
            Game.nextTurn();
            
        }
    }
}