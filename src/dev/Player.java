package dev;


import bc.*;


public class Player 
{
	public static long totalTime;
	public static long longest = 0;
	
    public static void main(String[] args) {
    	System.out.println("v1.2.0");
        while (true) 
        {
        	long start = System.nanoTime();
        	turnStart();

        	if (Game.gc.getTimeLeftMs() < Constants.CLOCKBUFFER) {
        		cheapTurn();
        	} else if (Game.gc.getTimeLeftMs() < Constants.EMERGENCYCLOCKBUFFER) {
        		instantTurn();
        	} else {
        		regularTurn();
        	}

            start = System.nanoTime() - start;
            if (start > longest) longest = start;
            totalTime += start;
            System.out.printf("\t\tremaining time is %d, longest %f, average %f\n", Game.gc.getTimeLeftMs(), longest / 1000000.0, (totalTime / Game.round()) / 1000000.0);
            Game.nextTurn();
        }
    }
    
    public static void turnStart() {
    	if (Game.round() % 50 == 0) {
    		System.gc();
    	}
    	try {
        	if (Game.round() % 20 == 0) {
        		System.gc();
        	}
        	System.out.println("Current round: "+ Game.round());
        	Timing.start("StartTurn");
        	Game.startTurn();
        	Timing.endAndPrint("StartTurn");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void regularTurn() {
    	try {
        	Timing.start("Tech");
        	TechManager.run();
        	Timing.endAndPrint("Tech");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
        	Timing.start("Worker");
        	Worker.run();
        	Timing.endAndPrint("Worker");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
        	Timing.start("Factory");
            Factory.run();
            Timing.endAndPrint("Factory");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
            Timing.start("Micro");
            Micro.run();
            Timing.endAndPrint("Micro");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
            Timing.start("Rocket");
            Rocket.run();
            Timing.endAndPrint("Rocket");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void cheapTurn() {
    	System.out.println("Running cheap turn");
    	try {
        	Timing.start("Tech");
        	TechManager.run();
        	Timing.endAndPrint("Tech");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
        	Timing.start("Factory");
            Factory.run();
            Timing.endAndPrint("Factory");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
            Timing.start("Micro");
            CheapMicro.run();
            Timing.endAndPrint("Micro");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
            Timing.start("Rocket");
            Rocket.run();
            Timing.endAndPrint("Rocket");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void instantTurn() {
    	System.out.println("Running instant turn");
    	try {
            Timing.start("Rocket");
            Rocket.run();
            Timing.endAndPrint("Rocket");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}