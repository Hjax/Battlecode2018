package apiRemoval;

import java.util.Comparator;
import java.util.PriorityQueue;

import bc.Direction;
import bc.Planet;

public class Rocket 
{
	private static PriorityQueue<Tile> landingGrid;
	public static Tile landingGridCenter;
	private static int launchedRockets = 0;
	
	
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
	
	public static void run()
	{
		if (Game.PLANET == Planet.Mars)
		{
			unload();
		}
		if (Game.PLANET == Planet.Earth)
		{
			launch();
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
			if ((launchedRockets == 0 && rocket.structureGarrison().length > 1) || (rocket.structureGarrison().length == rocket.structureMaxCapacity() || Game.round == 749))
			{
				if (Game.canLaunchRocket(rocket, landingGrid.peek())) 
				{
					Game.launchRocket(rocket, landingGrid.poll());
				}
				
			}
		}
	}
}
