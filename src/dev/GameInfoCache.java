package dev;

import java.util.ArrayList;
import java.util.HashSet;

import bc.*;

public class GameInfoCache 
{
	public static ArrayList<HashSet<Tile>> karboniteDeposits = new ArrayList<HashSet<Tile>>(Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE);
	
	static
	{
		long start = System.nanoTime();
		for (int x = 0; x < Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE; x++)
		{
			karboniteDeposits.add(new HashSet<Tile>());
		}
		int[] directions = {1, 1 - Constants.QUADRANTROWSIZE, -1 * Constants.QUADRANTROWSIZE, -1 - Constants.QUADRANTROWSIZE, -1, Constants.QUADRANTROWSIZE - 1, Constants.QUADRANTROWSIZE, Constants.QUADRANTROWSIZE + 1};
		Tile checkLocation;
		for (int x = 0; x < Game.startingMap(Game.planet()).getWidth(); x++)
		{
			for (int y = 0; y < Game.startingMap(Game.planet()).getHeight(); y++)
			{
				checkLocation = Tile.getInstance(Game.planet(), x, y);
				if (Game.initialKarboniteAt(checkLocation) > 0)
				{
					int loc = x/Constants.QUADRANTSIZE + y/Constants.QUADRANTSIZE * Constants.QUADRANTROWSIZE;
					karboniteDeposits.get(loc).add(checkLocation);
					for (int dir:directions)
					{
						int test = loc + dir;
						if ((Math.abs(test % Constants.QUADRANTROWSIZE - loc % Constants.QUADRANTROWSIZE) <= 1 && test >= 0 && test < Constants.QUADRANTROWSIZE * Constants.QUADRANTCOLUMNSIZE && Game.pathMap[test]))
						{
							karboniteDeposits.get(test).add(checkLocation);
						}
						
					}
				}
			}
		}
		System.out.println("GameInfoCache init took: " + ((System.nanoTime() - start) / 1000000.0) + " ms");
	}
	
	public static HashSet<Robot> currentBlueprints = new HashSet<Robot>();
	
	public static ArrayList<Robot> allyWorkers = new ArrayList<Robot>();
	public static ArrayList<Robot> allyKnights = new ArrayList<Robot>();
	public static ArrayList<Robot> allyRangers = new ArrayList<Robot>();
	public static ArrayList<Robot> allyMages = new ArrayList<Robot>();
	public static ArrayList<Robot> allyHealers = new ArrayList<Robot>();
	public static ArrayList<Robot> allyFactories = new ArrayList<Robot>();
	public static ArrayList<Robot> allyRockets = new ArrayList<Robot>();
	
	public static ArrayList<Robot> enemyWorkers = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyKnights = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyRangers = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyMages = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyHealers = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyFactories = new ArrayList<Robot>();
	public static ArrayList<Robot> enemyRockets = new ArrayList<Robot>();
	
	public static ArrayList<Robot> allWorkers = new ArrayList<Robot>();
	public static ArrayList<Robot> allKnights = new ArrayList<Robot>();
	public static ArrayList<Robot> allRangers = new ArrayList<Robot>();
	public static ArrayList<Robot> allMages = new ArrayList<Robot>();
	public static ArrayList<Robot> allHealers = new ArrayList<Robot>();
	public static ArrayList<Robot> allFactories = new ArrayList<Robot>();
	public static ArrayList<Robot> allRockets = new ArrayList<Robot>();
	
	public static void updateCache()
	{
		allyWorkers = new ArrayList<Robot>();
		allyKnights = new ArrayList<Robot>();
		allyRangers = new ArrayList<Robot>();
		allyMages = new ArrayList<Robot>();
		allyHealers = new ArrayList<Robot>();
		allyFactories = new ArrayList<Robot>();
		allyRockets = new ArrayList<Robot>();
		
		enemyWorkers = new ArrayList<Robot>();
		enemyKnights = new ArrayList<Robot>();
		enemyRangers = new ArrayList<Robot>();
		enemyMages = new ArrayList<Robot>();
		enemyHealers = new ArrayList<Robot>();
		enemyFactories = new ArrayList<Robot>();
		enemyRockets = new ArrayList<Robot>();
		
		allWorkers = new ArrayList<Robot>();
		allKnights = new ArrayList<Robot>();
		allRangers = new ArrayList<Robot>();
		allMages = new ArrayList<Robot>();
		allHealers = new ArrayList<Robot>();
		allFactories = new ArrayList<Robot>();
		allRockets = new ArrayList<Robot>();
		
		currentBlueprints = new HashSet<Robot>();
		
		updateType(UnitType.Worker, allyWorkers, enemyWorkers, allWorkers);
		updateType(UnitType.Knight, allyKnights, enemyKnights, allKnights);
		updateType(UnitType.Ranger, allyRangers, enemyRangers, allRangers);
		updateType(UnitType.Mage, allyMages, enemyMages, allMages);
		updateType(UnitType.Healer, allyHealers, enemyHealers, allHealers);
		updateType(UnitType.Factory, allyFactories, enemyFactories, allFactories);
		updateType(UnitType.Rocket, allyRockets, enemyRockets, allRockets);
		
		HashSet<Tile> depletedDeposits = new HashSet<Tile>();
		for (HashSet<Tile> quadrant:karboniteDeposits)
		{
			for (Tile deposit: quadrant)
			{
				if (Game.canSenseLocation(deposit))
				{
					if (Game.karboniteAt(deposit) == 0)
					{
						depletedDeposits.add(deposit);
					}
				}
			}
			for (Tile deposit: depletedDeposits)
			{
				quadrant.remove(deposit);
			}
		}
		
		
	}
	
	private static void updateType(UnitType type, ArrayList<Robot> allyCache, ArrayList<Robot> enemyCache, ArrayList<Robot> allCache)
	{
		for (Robot bot:Game.senseNearbyUnits(type))
			{
				allCache.add(bot);
				if (type == UnitType.Factory && bot.structureIsBuilt() != 1)
				{
					currentBlueprints.add(bot);
				}
				if (bot.team() == Game.team())
				{
					allyCache.add(bot);
				}
				else
				{
					enemyCache.add(bot);
				}
				
			}
	}
	
}
