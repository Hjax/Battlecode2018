package sc2Micro;


import bc.*;


public class Player 
{
	public static long totalTime;
	public static long longest = 0;
    public static void main(String[] args) {
    	if (Game.planet() == Planet.Earth) {
        	Game.queueResearch(UnitType.Healer);  
        	Game.queueResearch(UnitType.Healer);
        	Game.queueResearch(UnitType.Healer);
        	Game.queueResearch(UnitType.Worker);
        	Game.queueResearch(UnitType.Ranger);
        	Game.queueResearch(UnitType.Rocket);
        	Game.queueResearch(UnitType.Rocket);
        	Game.queueResearch(UnitType.Ranger);
        	Game.queueResearch(UnitType.Rocket);
    	}
        while (true) 
        {
        	System.out.printf("\t\tremaining time is %d, longest %f, average %f\n", Game.gc.getTimeLeftMs(), longest / 1000000.0, (totalTime / Game.round()) / 1000000.0);
        	if (Game.gc.getTimeLeftMs() < Constants.CLOCKBUFFER) 
        	{
        		System.out.println("Low on time, ending turn");
        		Game.nextTurn();
        		continue;
        	}
        	if (Game.round() % 50 == 0) {
        		System.gc();
        	}
        	long start = System.nanoTime();
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

            start = System.nanoTime() - start;
            if (start > longest) longest = start;
            totalTime += start;
            Game.nextTurn();
        }
    }
}