package first;

import bc.*;


public class Player 
{
    public static void main(String[] args) 
    {
    	
        while (true) 
        {
        	game.startTurn();
            System.out.println("Current round: "+ game.round());

            Worker.run();
            game.nextTurn();
        }
    }
}