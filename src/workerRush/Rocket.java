package workerRush;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import bc.Direction;
import bc.Planet;

public class Rocket 
{
	private static PriorityQueue<Tile> landingGrid;
	public static Map<Robot, Robot> assignments = new HashMap<>();
	public static Map<Robot, Integer> assignmentCount = new HashMap<>();
	public static Tile landingGridCenter;
	public static int launchedRockets = 0;
	public static ArrayList<RocketInfo> radar = new ArrayList<>();
	public static int flyingWorkers = 0;
	
	
	private static class LandingTileComparator implements Comparator<Tile>
	{
		public int compare(Tile arg0, Tile arg1) {
			int dist1 = Pathfinding.pathLength(arg0, landingGridCenter);
			int dist2 = Pathfinding.pathLength(arg1, landingGridCenter);
			if (dist1 < dist2)
			{
				return -1;
			}
			if (dist1 == dist2)
			{
				return 0;
			}
			return 1;
		}
		
	}
	
	static
	{
		landingGridCenter = Game.getRandomLocation(Planet.Mars);
		
		landingGrid = new PriorityQueue<Tile>(Game.HEIGHT * Game.WIDTH / 4, new LandingTileComparator());
		int x = landingGridCenter.getX();
		int y = landingGridCenter.getY();
		Tile place;
		while (y < Game.startingMap(Planet.Mars).getHeight())
		{
			while (x < Game.startingMap(Planet.Mars).getWidth())
			{
				place = Tile.getInstance(Planet.Mars, x, y);
				if (Game.startingMap(Planet.Mars).isPassableTerrainAt(place.location) == 1)
				{
					landingGrid.add(place);
				}
				
				x += 2;
			}
			y += 2;
			x -= Game.startingMap(Planet.Mars).getWidth();
		}
		x = landingGridCenter.getX() - 2;
		y = landingGridCenter.getY();
		while (y >= 0)
		{
			while (x >= 0)
			{
				place = Tile.getInstance(Planet.Mars, x, y);
					if (Game.startingMap(Planet.Mars).isPassableTerrainAt(place.location) == 1)
					{
						landingGrid.add(place);
					}
				x -= 2;
			}
			y -= 2;
			x += Game.startingMap(Planet.Mars).getWidth();
		}
	}
	
	public static void startTurn()
	{
		flyingWorkers = 0;
		for (RocketInfo rocket: radar)
		{
			if (Game.round() >= rocket.landRound + 50) //50 turns for communication delay
			{
				radar.remove(rocket);
			}
			else
			{
				flyingWorkers += rocket.workerCount;
			}
		}
	}
	
	public static void run()
	{
		if (Game.PLANET == Planet.Mars)
		{
			unload();
		}
		if (Game.PLANET == Planet.Earth)
		{
			cleanUpAssignments();
			assignToRockets();
			launch();
		}
	}
	
	private static void cleanUpAssignments() {
		Set<Robot> toRemove = new HashSet<>();
		for (Robot r: assignments.keySet()) {
			try {
				if (!assignments.get(r).location().isOnMap() || assignments.get(r).health() <= 0) {
					toRemove.add(r);
					assignmentCount.put(r, assignmentCount.getOrDefault(r, 0) - 1);
				}
			} catch (Exception e) {
				toRemove.add(r);
				assignmentCount.put(r, assignmentCount.getOrDefault(r, 0) - 1);
			}
		}
		for (Robot r: toRemove) {
			assignments.remove(r);
		}
	}
	
	private static void cleanUpRocket(Robot rocket) {
		radar.add(new RocketInfo(rocket));
		Worker.factoryGrid.add(rocket.tile());
		Set<Robot> toRemove = new HashSet<>();
		for (Robot r: assignments.keySet()) {
			if (assignments.get(r) == rocket) {
				toRemove.add(r);
			}
		}
		for (Robot r: toRemove) {
			assignments.remove(r);
		}
	}
	
  	
	private static void assignToRockets() {
		for (Robot rocket: GameInfoCache.allyRockets)  {
			if (!(rocket.structureIsBuilt() == 1)) {
				continue;
			}
			// not sure if this check is needed, but it cant hurt
			if (!rocket.location().isOnMap() || !rocket.location().isOnPlanet(Game.planet())) {
				continue;
			}
			while (assignmentCount.getOrDefault(rocket, 0) < rocket.structureMaxCapacity()) {
				Robot best = null;
				for (Robot r: GameInfoCache.allyCombat) {
					if (!assignments.containsKey(r) && (best == null || Pathfinding.pathLength(best.tile(), rocket.tile()) > Pathfinding.pathLength(r.tile(), rocket.tile()))) {
						best = r;
					}
				}
				if (best == null) {
					break;
				}
				assignments.put(best, rocket);
				assignmentCount.put(rocket, assignmentCount.getOrDefault(rocket, 0) + 1);
			}
		}
	}
	
	private static void unload()
	{
		for (Robot rocket: GameInfoCache.allyRockets) 
		{
			if (rocket.structureGarrison().length > 0) 
			{
				for (Direction dir: Game.moveDirections) 
				{
					if (Game.canUnload(rocket, dir)) 
					{
						Game.unload(rocket, dir);
					}
				}
			}
		}
	}
	
	private static void launch()
	{
		for (Robot rocket: GameInfoCache.allyRockets)
		{
			System.out.printf("rocket has %d units loaded\n", rocket.structureGarrison().length);
			if ((launchedRockets == 0 && rocket.structureGarrison().length > 1) || 
					(rocket.structureGarrison().length == rocket.structureMaxCapacity() || Game.round == 749) || 
					Game.senseCombatUnits(rocket.tile(), Constants.RANGERRANGE, Game.enemy()).length * Constants.RANGERDAMAGE >= rocket.health())
			{
				if (Game.canLaunchRocket(rocket, landingGrid.peek())) 
				{
					cleanUpRocket(rocket);
					Game.launchRocket(rocket, landingGrid.poll());
					launchedRockets++;
				}
				
			}
		}
	}
}
