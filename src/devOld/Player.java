package devOld;


import bc.*;


public class Player 
{
	public static long totalTime;
	public static long longest = 0;
    public static void main(String[] args) {
    	if (Game.planet() == Planet.Earth) {
        	Game.queueResearch(UnitType.Worker);
        	Game.queueResearch(UnitType.Ranger);
        	Game.queueResearch(UnitType.Healer);    	
        	Game.queueResearch(UnitType.Rocket);
        	Game.queueResearch(UnitType.Healer);
        	Game.queueResearch(UnitType.Ranger);
        	Game.queueResearch(UnitType.Rocket);
        	Game.queueResearch(UnitType.Healer);
        	Game.queueResearch(UnitType.Rocket);
    	}
        while (true) 
        {
        	if (Game.gc.getTimeLeftMs() < Constants.CLOCKBUFFER) 
        	{
        		System.out.println("Low on time, ending turn");
        		Game.nextTurn();
        		continue;
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
        	}
        	try {
                Timing.start("Rocket");
                Rocket.run();
                Timing.end("Rocket");
        	} catch (Exception e) {
        		System.out.println("An error has occured");
        		e.printStackTrace();
        	}

            start = System.nanoTime() - start;
            if (start > longest) longest = start;
            totalTime += start;
            System.out.printf("\t\tremaining time is %d, longest %f, average %f\n", Game.gc.getTimeLeftMs(), longest / 1000000.0, (totalTime / Game.round()) / 1000000.0);
            Game.nextTurn();
        }
    }
}