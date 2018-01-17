package prototype;


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
    		System.out.printf("\t\tremaining time before turn starts is %d\n", Game.gc.getTimeLeftMs());
        	try {
            	if (Game.round() % 20 == 0) {
            		System.gc();
            	}
            	System.out.println("Current round: "+ Game.round());
            	Timing.start("StartTurn");
            	Game.startTurn();
            	Timing.end("StartTurn");
        	} catch (Exception e) {
        	}
        	try {
            	Timing.start("Worker");
            	Worker.run();
            	Timing.end("Worker");
        	} catch (Exception e) {
        	}
        	try {
            	Timing.start("Factory");
                Factory.run();
                Timing.end("Factory");
        	} catch (Exception e) {
        	}
        	try {
                Timing.start("Micro");
                Micro.run();
                Timing.end("Micro");
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	try {
                Timing.start("Rocket");
                Rocket.run();
                Timing.end("Rocket");
        	} catch (Exception e) {
        	}
        	System.out.printf("\t\tending turn\n");
            Game.nextTurn();
        }
    }
}