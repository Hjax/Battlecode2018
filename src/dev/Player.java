package dev;


import bc.*;


public class Player 
{
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
        	try {
            	if (Game.round() % 20 == 0) {
            		System.gc();
            	}
            	System.out.println("Current round: "+ Game.round());
            	Timing.start("StartTurn");
            	Game.startTurn();
            	Timing.end("StartTurn");
            	Timing.start("Worker");
            	Worker.run();
            	Timing.end("Worker");
            	Timing.start("Factory");
                Factory.run();
                Timing.end("Factory");
                Timing.start("Micro");
                Micro.run();
                Timing.end("Micro");
                Timing.start("Rocket");
                Rocket.run();
                Timing.end("Rocket");
                System.out.printf("\t\tremaining time is %d\n", Game.gc.getTimeLeftMs());
        	} catch (Exception e) {
        		
        	}
        	System.out.printf("ending turn\n");	
            Game.nextTurn();
        }
    }
}