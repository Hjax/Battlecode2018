package dev;

import bc.*;
import java.util.*;

public class Tile {
	MapLocation location;
	private static Map<String, Tile> box;
	static {
		box = new HashMap<>();
	}
	public static Tile getInstance(MapLocation loc) {
		if (box.containsKey(loc.toString())) {
			return box.get(loc.toString());
		}
		Tile r = new Tile(loc);
		box.put(loc.toString(), r);
		return r;
	}
	public static Tile getInstance(Planet p, int x, int y) {
		return getInstance(new MapLocation(p, x, y));
	}
	
	public Tile(MapLocation location) {
		this.location = location;
	}
	
	Tile add(Direction direction) {
		return getInstance(location.add(direction));
	}
	Tile addMultiple(Direction direction, int multiple) {
		return getInstance(location.addMultiple(direction, multiple));
	}
	Direction directionTo(Tile o) {
		return location.directionTo(o.location);
	}
	long distanceSquaredTo(Tile o) {
		return location.distanceSquaredTo(o.location);
	}
	boolean	equals(Tile o) {
		return location.equals(o.location);
	}
	Planet getPlanet() {
		return location.getPlanet();
	}
	int	getX() {
		return location.getX();
	}
	int	getY() {
		return location.getY();
	}
	boolean	isAdjacentTo(Tile o) {
		return location.isAdjacentTo(o.location);
	}
	boolean	isWithinRange(long range, Tile o) {
		return location.isWithinRange(range, o.location);
	}
	Tile translate(int dx, int dy) {
		return Tile.getInstance(location.translate(dx, dy));
	}
}
