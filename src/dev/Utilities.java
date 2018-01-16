package dev;

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
			Tile test = offsetInDirection(start, dir, 1);
			if (Game.isPassableTerrainAt(test) && Game.isOccupiable(test) == 1)
			{
				return dir;
			}
		}
		return Direction.Center;
	}
	public static Direction findNearestPassableDir(Tile start, Direction dir)
	{
		if (Game.isPassableTerrainAt(offsetInDirection(start, dir, 1)))
		{
			return dir;
		}
		Direction left = rotateCounterClockwise(dir);
		Direction right = rotateClockwise(dir);
		while (left != right)
		{
			if (Game.isPassableTerrainAt(offsetInDirection(start,left, 1)))
			{
				return left;
			}
			if (Game.isPassableTerrainAt(offsetInDirection(start,right, 1)))
			{
				return right;
			}
			left = rotateCounterClockwise(left);
			right = rotateClockwise(right);
		}
		if (Game.isPassableTerrainAt(offsetInDirection(start,left, 1)))
		{
			return left;
		}
		return Direction.Center;
	}
	
	public static Direction findNearestOccupiableDir(Tile start, Direction dir)
	{
		if (Game.isPassableTerrainAt(offsetInDirection(start, dir, 1)) && Game.isOccupiable(offsetInDirection(start, dir, 1)) > 0)
		{
			return dir;
		}
		Direction left = rotateCounterClockwise(dir);
		Direction right = rotateClockwise(dir);
		while (left != right)
		{
			if (Game.isPassableTerrainAt(offsetInDirection(start,left, 1)) && Game.isOccupiable(offsetInDirection(start, left, 1)) > 0)
			{
				return left;
			}
			if (Game.isPassableTerrainAt(offsetInDirection(start,right, 1)) && Game.isOccupiable(offsetInDirection(start, right, 1)) > 0)
			{
				return right;
			}
			left = rotateCounterClockwise(left);
			right = rotateClockwise(right);
		}
		if (Game.isPassableTerrainAt(offsetInDirection(start,left, 1)) && Game.isOccupiable(offsetInDirection(start, left, 1)) > 0)
		{
			return left;
		}
		return Direction.Center;
	}
	
	public static Direction rotateClockwise(Direction dir)
	{
		switch (dir)
		{
		case East:
			return Direction.Southeast;
		case West:
			return Direction.Northwest;
		case North:
			return Direction.Northeast;
		case South:
			return Direction.Southwest;
		case Northeast:
			return Direction.East;
		case Northwest:
			return Direction.North;
		case Southeast:
			return Direction.South;
		case Southwest:
			return Direction.West;
		default:
			return Direction.Center;
		}
	}
	public static Direction rotateCounterClockwise(Direction dir)
	{
		switch (dir)
		{
		case East:
			return Direction.Northeast;
		case West:
			return Direction.Southwest;
		case North:
			return Direction.Northwest;
		case South:
			return Direction.Southeast;
		case Northeast:
			return Direction.North;
		case Northwest:
			return Direction.West;
		case Southeast:
			return Direction.East;
		case Southwest:
			return Direction.South;
		default:
			return Direction.Center;
		}
	}
}
