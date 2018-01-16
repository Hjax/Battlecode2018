package rocketBot;


import bc.*;


public class Player 
{
	static long workerTime = 0;
	static long microTime = 0;
    public static void main(String[] args) {
    	Game.queueResearch(UnitType.Worker);
    	Game.queueResearch(UnitType.Ranger);
    	Game.queueResearch(UnitType.Rocket);
    	Game.queueResearch(UnitType.Healer);
    	Game.queueResearch(UnitType.Healer);
    	Game.queueResearch(UnitType.Ranger);
    	Game.queueResearch(UnitType.Ranger);
    	Game.queueResearch(UnitType.Rocket);
    	Game.queueResearch(UnitType.Healer);
        while (true) 
        {
        	System.out.println("Current round: "+ Game.round());
        	Game.startTurn();
        	
        	Worker.run();
            System.out.printf("Total worker time is %d miliseconds\n", (workerTime)/1000000);
            Factory.run();
            Micro.run();
            Rocket.run();
            System.out.printf("\t\tremaining time is %d\n", Game.gc.getTimeLeftMs());
            
            Game.nextTurn();
        }
    }
}