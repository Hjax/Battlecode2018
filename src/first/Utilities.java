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
	
	public static MapLocation offsetInDirection(MapLocation start, Direction dir, int offset)
	{
		switch (dir)
		{
		case East:
			return new MapLocation(start.getPlanet(), start.getX() + offset, start.getY());
		case West:
			return new MapLocation(start.getPlanet(), start.getX() - offset, start.getY());
		case North:
			return new MapLocation(start.getPlanet(), start.getX(), start.getY() + offset);
		case South:
			return new MapLocation(start.getPlanet(), start.getX(), start.getY() - offset);
		case Northeast:
			return new MapLocation(start.getPlanet(), start.getX() + offset, start.getY() + offset);
		case Northwest:
			return new MapLocation(start.getPlanet(), start.getX() - offset, start.getY() + offset);
		case Southeast:
			return new MapLocation(start.getPlanet(), start.getX() + offset, start.getY() - offset);
		case Southwest:
			return new MapLocation(start.getPlanet(), start.getX() - offset, start.getY() - offset);
		default:
			return start;
		}
	}
}
