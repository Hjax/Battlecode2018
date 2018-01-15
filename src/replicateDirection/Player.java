package replicateDirection;


import bc.*;


public class Player 
{
	static long workerTime = 0;
	static long microTime = 0;
    public static void main(String[] args) {
    	Game.queueResearch(UnitType.Ranger);
    	Game.queueResearch(UnitType.Ranger);
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
                time = System.nanoTime();
                Micro.run();
                microTime += System.nanoTime() - time;
                System.out.printf("Total micro time is %d miliseconds\n", (microTime)/1000000);
                System.out.printf("Current micro time is %d miliseconds\n", (System.nanoTime() - time)/1000000);
                
            }
            
            Game.nextTurn();
        }
    }
}