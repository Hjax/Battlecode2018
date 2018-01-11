package first;

import bc.*;

public class Utilities 
{
	public static Direction oppositeDir(Direction dir)
	{
		switch (dir)
		{
		case East:
			return Direction.West;
		case West:
			return Direction.East;
		case North:
			return Direction.South;
		case South:
			return Direction.North;
		case Northeast:
			return Direction.Southwest;
		case Northwest:
			return Direction.Southeast;
		case Southeast:
			return Direction.Northwest;
		case Southwest:
			return Direction.Northeast;
		default:
			return Direction.Center;
		}
	}
	
	public static Tile offsetInDirection(Tile start, Direction dir, int offset)
	{
		switch (dir)
		{
		case East:
			return Tile.getInstance(start.getPlanet(), start.getX() + offset, start.getY());
		case West:
			return Tile.getInstance(start.getPlanet(), start.getX() - offset, start.getY());
		case North:
			return Tile.getInstance(start.getPlanet(), start.getX(), start.getY() + offset);
		case South:
			return Tile.getInstance(start.getPlanet(), start.getX(), start.getY() - offset);
		case Northeast:
			return Tile.getInstance(start.getPlanet(), start.getX() + offset, start.getY() + offset);
		case Northwest:
			return Tile.getInstance(start.getPlanet(), start.getX() - offset, start.getY() + offset);
		case Southeast:
			return Tile.getInstance(start.getPlanet(), start.getX() + offset, start.getY() - offset);
		case Southwest:
			return Tile.getInstance(start.getPlanet(), start.getX() - offset, start.getY() - offset);
		default:
			return start;
		}
	}
	
	public static Direction findOccupiableDir(Tile start)
	{
		for (Direction dir: Game.directions)
		{
			if (Game.isOccupiable(offsetInDirection(start, dir, 1)) > 0)
			{
				return dir;
			}
		}
		return Direction.Center;
	}
}
