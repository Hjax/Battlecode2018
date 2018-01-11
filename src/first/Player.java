package first;

import bc.*;


public class Player 
{
    public static void main(String[] args) 
    {
    	
        while (true) 
        {
        	Game.startTurn();
            System.out.println("Current round: "+ Game.round());

            Worker.run();
            Game.nextTurn();
        }
    }
}