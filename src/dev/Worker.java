package dev;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import bc.*;

public class Worker 
{
	private static HashSet<Robot> idleWorkers = new HashSet<Robot>();
	private static PriorityQueue<Tile> factoryGrid;
	private static Tile factoryGridCenter = Rocket.landingGridCenter;
	private static Random rng = new Random(5468);
	private static int rockets = 0;
	private static HashSet<Robot> replicatedWorkers = new HashSet<Robot>();
	
	
	private static class WorkerScoreTuple implements Comparator<WorkerScoreTuple>
	{
		public int score;
		public Robot worker;
		
		WorkerScoreTuple(int SCORE, Robot work)
		{
			score = SCORE;
			worker = work;
		}

		public int compare(WorkerScoreTuple o1, WorkerScoreTuple o2) 
		{
			//higher score is "less than" lower score for sorting purposes
			if (o1.score > o2.score)
			{
				return -1;
			}
			if (o1.score == o2.score)
			{
				return 0;
			}
			return 1;
		}
	}
	
	private static WorkerScoreTuple bestWorker()
	{
		int min = Constants.INFINITY;
		int max = -1;
		int pathDistance = -1;
		WorkerScoreTuple closeWorker = new WorkerScoreTuple(Constants.INFINITY, null);
		WorkerScoreTuple farWorker = new WorkerScoreTuple(-1, null);
		
		for (int ally = 0; ally < Constants.startingAlliesLocation.length; ally++)
		{
			for (int enemy = 0; enemy < Constants.startingEnemiesLocation.length; enemy++)
			{
				pathDistance = Pathfinding.pathLength(Constants.startingAlliesLocation[ally], Constants.startingEnemiesLocation[enemy]);
				if (pathDistance > max)
					{
						max = pathDistance;
					}
				if (pathDistance < min && pathDistance != -1)
				{
					min = pathDistance;
				}
			}
			if (max < closeWorker.score)
			{
				closeWorker.score = max;
				closeWorker.worker = Constants.startingAllies[ally];
			}
			if (min > farWorker.score)
			{
				farWorker.score = min;
				farWorker.worker = Constants.startingAllies[ally];
			}
		}
		if (closeWorker.score <= Constants.RUSHTHRESHOLD)
		{
			System.out.printf("rush distance is %d\tworker id is %d\n", closeWorker.score, closeWorker.worker.id());
			return closeWorker;
		}
		System.out.printf("rush distance is %d\tworker id is %d\n", farWorker.score, farWorker.worker.id());
		return farWorker;
	}
	
	private static class FactoryTileComparator implements Comparator<Tile>
	{
		public int compare(Tile arg0, Tile arg1) {
			int dist1 = Pathfinding.pathLength(arg0, factoryGridCenter);
			int dist2 = Pathfinding.pathLength(arg1, factoryGridCenter);
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
	
	private static void buildOrder()
	{
		if (Game.round() == 1)
		{
			WorkerScoreTuple bestWorker = bestWorker();
			Tile nearestEnemy = Constants.startingEnemiesLocation[0];

			for (Tile enemy:Constants.startingEnemiesLocation)
			{
				int distance = Pathfinding.pathLength(bestWorker.worker.tile(), enemy);
				if (distance != -1 && distance < Pathfinding.pathLength(bestWorker.worker.tile(), nearestEnemy))
				{
					nearestEnemy = enemy;
				}
			}
			Direction buildDir = Pathfinding.path(bestWorker.worker.tile(), nearestEnemy);
			System.out.printf("buildDir = %s", buildDir.name());
			if (buildDir == Direction.Center)
			{
				buildDir = bestWorker.worker.tile().directionTo(nearestEnemy);
			}
			if (bestWorker.score > Constants.RUSHTHRESHOLD)
			{
				buildDir = Utilities.oppositeDir(buildDir);
			}
			else
			{
				GlobalStrategy.rush = true;
				Constants.FACTORYBUILDRANGE = 4;
				Constants.FACTORYREPLICATEPRESSURE = 150;
				Constants.WORKERREPLICATEDEPOSITWEIGHT = 0;
				Constants.WORKERLIMITWEIGHT = 0;
			}
			buildDir = Utilities.findNearestOccupiableDir(bestWorker.worker.tile(), buildDir);
			System.out.printf("building in direction %s\n", buildDir.name());
			if (buildDir != Direction.Center)
			{
				Game.blueprint(bestWorker.worker, UnitType.Factory, buildDir);
				idleWorkers.remove(bestWorker.worker);
				factoryGridCenter = Utilities.offsetInDirection(bestWorker.worker.tile(), buildDir, 1);
				initializeBuildGrid();
			}
			else
			{
				while (buildDir == Direction.Center)
				{
					for (Robot worker:GameInfoCache.allyWorkers)
					{
						buildDir = Utilities.findOccupiableDir(worker.tile());
						if (buildDir == Direction.Center)
						{
							continue;
						}
						Game.blueprint(worker, UnitType.Factory, buildDir);
						idleWorkers.remove(worker);
						factoryGridCenter = Utilities.offsetInDirection(worker.tile(), buildDir, 1);
						initializeBuildGrid();
					}
					if (buildDir == Direction.Center)
					{
						Robot disintegratedWorker = GameInfoCache.allyWorkers.get(0);
						GameInfoCache.allyWorkers.remove(0);
						idleWorkers.remove(disintegratedWorker);
						Game.disintegrateUnit(disintegratedWorker);
					}
				}
			}
			
		}
	}
	
	private static void initializeBuildGrid()
	{
		factoryGrid = new PriorityQueue<Tile>(Game.HEIGHT * Game.WIDTH / 4, new FactoryTileComparator());
		int x = factoryGridCenter.getX() + 2;
		int y = factoryGridCenter.getY();
		Tile place;
		while (y < Game.HEIGHT)
		{
			while (x < Game.WIDTH)
			{
				place = Tile.getInstance(Game.planet(), x, y);
				if (Game.isPassableTerrainAt(place))
				{
					factoryGrid.add(place);
				}
				
				x += 2;
			}
			y += 2;
			x -= Game.WIDTH;
		}
		x = factoryGridCenter.getX() - 2;
		y = factoryGridCenter.getY();
		while (y >= 0)
		{
			while (x >= 0)
			{
				place = Tile.getInstance(Game.planet(), x, y);
					if (Game.isPassableTerrainAt(place))
					{
						factoryGrid.add(place);
					}
				x -= 2;
			}
			y -= 2;
			x += Game.WIDTH;
		}
	}
	
	
	private static int replicateScore(Robot worker)
	{
		int score = 0;
		int distance = -1;
		score += (Constants.WORKERLIMIT - GameInfoCache.allyWorkers.size()) * Constants.WORKERLIMITWEIGHT;
		if (Game.round() >= 750)
		{
			score += Constants.INFINITY;
		}
		for (Robot blueprint:GameInfoCache.currentBlueprints)
		{
			distance = Pathfinding.pathLength(worker.tile(), blueprint.tile());
			if (distance != -1)
			{
				score += Constants.FACTORYREPLICATEPRESSURE / distance;
			}
		}
		score += Constants.WORKERREPLICATEDEPOSITWEIGHT * GameInfoCache.karboniteDeposits.get(worker.tile().getX()/Constants.QUADRANTSIZE + worker.tile().getY()/Constants.QUADRANTSIZE * Constants.QUADRANTROWSIZE).size();
		for (Robot otherWorker:GameInfoCache.allyWorkers)
		{
			if (otherWorker == worker)
			{
				continue;
			}
			distance = Pathfinding.pathLength(otherWorker.tile(), worker.tile());
			if (distance != -1)
			{
				score -= 20/distance;
			}
			
		}
		return score;
		
	}
	
	
	private static void replicateWorkers()
	{
		PriorityQueue<WorkerScoreTuple> workerOrder = new PriorityQueue<WorkerScoreTuple>(GameInfoCache.allyWorkers.size()+1, new WorkerScoreTuple(0,null));
		for (Robot worker:GameInfoCache.allyWorkers)
		{
			if (worker.location().isOnMap())
			{
				workerOrder.add(new WorkerScoreTuple(replicateScore(worker), worker));
			}
		}
		while (Game.karbonite() > Constants.REPLICATECOST && workerOrder.peek() != null && workerOrder.peek().score > 0)
		{
			Direction replicateDir;
			Robot worker = workerOrder.poll().worker;
			if (GlobalStrategy.rush)
			{
				replicateDir = Utilities.findNearestOccupiableDir(worker.tile(), worker.tile().directionTo(factoryGridCenter));
			}
			else
			{
				replicateDir = Utilities.findNearestOccupiableDir(worker.tile(), Utilities.oppositeDir(worker.tile().directionTo(factoryGridCenter)));
			}
			
			if (Game.canReplicate(worker, replicateDir))
			{
				Game.replicate(worker, replicateDir);
				idleWorkers.remove(worker);
				if (Game.hasUnitAtLocation(Utilities.offsetInDirection(worker.tile(), replicateDir, 1)))
				{
					replicatedWorkers.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(worker.tile(), replicateDir, 1)));
				}
				
			}
		}
	}
	
	private static void giveWorkersOrders()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		workerLabel: for (Robot worker:idleWorkers)
		{
			Robot closestBlueprint = null;
			long mostHealth = -1;
			for (Robot structure:GameInfoCache.currentBlueprints)
			{
				int testDistance = Pathfinding.pathLength(structure.tile(), worker.tile());
				if ((structure.unitType() == UnitType.Factory && testDistance < Constants.FACTORYBUILDRANGE) || (structure.unitType() == UnitType.Rocket && testDistance < Constants.ROCKETBUILDRANGE))
				{
					if (structure.health() > mostHealth)
					{
						mostHealth = structure.health();
						closestBlueprint = structure;
						
					}
				}
			}
			if (closestBlueprint != null)
			{
				Direction moveDir = Pathfinding.path(worker.tile(), closestBlueprint.tile());
				if (Game.canMove(worker, moveDir))
				{
					Game.moveRobot(worker, moveDir);
					removeWorkers.add(worker);
				}
				if (Pathfinding.pathLength(worker.tile(), closestBlueprint.tile()) <= 1)
				{
					if (Game.canBuild(worker, closestBlueprint))
					{
						Game.build(worker, closestBlueprint);
						removeWorkers.add(worker);
					}
				}
				continue workerLabel;
			}
		}
		for (Robot worker: removeWorkers)
		{
			idleWorkers.remove(worker);
		}
	}
	
	private static void harvest()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		worker: for (Robot worker:idleWorkers)
		{
			Direction moveDir = Direction.Center;
			if (GameInfoCache.karboniteLocations.size() > 0)
			{
				moveDir = Pathfinding.karbonitePath(worker.tile());
			}
			else
			{
				continue;
			}
			
			if (Game.canMove(worker, moveDir))
			{
				Game.moveRobot(worker, moveDir);
				removeWorkers.add(worker);
			}
			int index = worker.tile().getX() + worker.tile().getY() * Game.WIDTH;
			if (GameInfoCache.karboniteDistance[index] <= 1)
			{
				Direction dir = worker.tile().directionTo(Tile.getInstance(Game.planet(), GameInfoCache.nearestKarbonite[index] % Game.WIDTH, GameInfoCache.nearestKarbonite[index] / Game.WIDTH));
				if (Game.canHarvest(worker, dir))
				{
					Game.harvest(worker, dir);
					removeWorkers.add(worker);
					continue worker;
				}
			}
		}
		for (Robot worker: removeWorkers)
		{
			idleWorkers.remove(worker);
		}
	}
	
	private static boolean shouldBlueprintFactory()
	{
		if (Game.PLANET == Planet.Mars)
		{
			return false;
		}
		if (factoryGrid.peek() == null)
		{
			return false;
		}
		if (GameInfoCache.allyFactories.size() < Constants.FACTORYGOAL && Game.karbonite() >= 100)
		{
			return true;
		}
		else if (Game.karbonite() >=(100 + 20 * (GameInfoCache.allyFactories.size())) && GameInfoCache.allyFactories.size() < Constants.FACTORYLIMIT)
		{
			return true;
		}
		return false;
	}
	
	private static void placeStructure(UnitType structure)
	{
		
		Tile placement = factoryGrid.peek();
		Robot closestWorker = null;
		int bestDistance = Constants.INFINITY;
		int currentDistance;
		for (Robot worker:idleWorkers)
		{
			currentDistance = Pathfinding.pathLength(worker.tile(), placement);
			if (currentDistance < bestDistance)
			{
				bestDistance = currentDistance;
				closestWorker = worker;
			}
		}
		if (closestWorker == null)
		{
			return;
		}
		if (bestDistance == 1)
		{
			if (Game.canBlueprint(closestWorker, structure, closestWorker.tile().directionTo(placement)))
			{
				Game.blueprint(closestWorker, structure, closestWorker.tile().directionTo(placement));
				factoryGrid.poll();
				idleWorkers.remove(closestWorker);
				if (structure == UnitType.Rocket)
				{
					rockets++;
				}
			}
		}
		else if (bestDistance == 0)
		{
			Direction moveDir = Utilities.findOccupiableDir(closestWorker.tile());
			if (moveDir != Direction.Center)
			{
				if (Game.canMove(closestWorker, moveDir))
				{
					Game.moveRobot(closestWorker, moveDir);
					idleWorkers.remove(closestWorker);
					if (Game.canBlueprint(closestWorker, structure, closestWorker.tile().directionTo(placement)))
					{
						Game.blueprint(closestWorker, structure, closestWorker.tile().directionTo(placement));
						factoryGrid.poll();
						if (structure == UnitType.Rocket)
						{
							rockets++;
						}
					}
					
				}
				
			}
		}
		else if (bestDistance > 1)
		{
			Direction moveDir = Pathfinding.path(closestWorker.tile(), placement);
			if (moveDir != Direction.Center)
			{
				if (Game.canMove(closestWorker, moveDir))
				{
					Game.moveRobot(closestWorker, moveDir);
					idleWorkers.remove(closestWorker);
					if (Pathfinding.pathLength(closestWorker.tile(), placement) == 1)
					{
						if (Game.canBlueprint(closestWorker, structure, closestWorker.tile().directionTo(placement)))
						{
							Game.blueprint(closestWorker, structure, closestWorker.tile().directionTo(placement));
							factoryGrid.poll();
							if (structure == UnitType.Rocket)
							{
								rockets++;
							}
						}
					}

				}
			}
			
		}
	}
	
	private static void tryBuildFactory()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		worker: for (Robot worker:idleWorkers)
		{
			for (Robot thing: Game.senseNearbyUnits(worker.tile(), 2))
			{
				if (Game.canBuild(worker, thing))
				{
					Game.build(worker, thing);
					removeWorkers.add(worker);
					continue worker;
				}
			}
		}
		for (Robot worker: removeWorkers)
		{
			idleWorkers.remove(worker);
		}
	}
	
	private static boolean shouldBuildRocket()
	{
		
		if (Game.PLANET == Planet.Mars)
		{
			return false;
		}
		
		if (Game.researchInfo().getLevel(UnitType.Rocket) > 0 && rockets == 0 && GameInfoCache.allyFactories.size() >= 2 && Game.karbonite() > 80)
		{
			return true;
		}
		
		if (Game.round >= 650)
		{
			return true;
		}
		
		if (GameInfoCache.allyFactories.size() >= Constants.FACTORYLIMIT && Game.karbonite() > 150 && Game.researchInfo().getLevel(UnitType.Rocket) > 0)
		{
			return true;
		}
		return false;
	}
	
	private static void moveRandomly()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		for (Robot worker:idleWorkers)
		{
			Direction randomDir = Utilities.findNearestOccupiableDir(worker.tile(), Direction.swigToEnum(rng.nextInt(8)));
			if (Game.canMove(worker, randomDir))
			{
				Game.moveRobot(worker, randomDir);
				removeWorkers.add(worker);
			}
		}
		for (Robot worker: removeWorkers)
		{
			idleWorkers.remove(worker);
		}
	}
	
	private static void loadRocket()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		worker: for (Robot worker:idleWorkers)
		{
			for (Robot thing: Game.senseNearbyUnits(worker.tile(), 2, UnitType.Rocket))
			{
				if (Game.canLoad(thing, worker))
				{
					Game.load(thing, worker);
					removeWorkers.add(worker);
					continue worker;
				}
			}
		}
		for (Robot worker: removeWorkers)
		{
			idleWorkers.remove(worker);
		}
	}
	
	public static void run() 
	{
		idleWorkers = new HashSet<Robot>(GameInfoCache.allyWorkers.size());
		for (Robot worker:GameInfoCache.allyWorkers)
		{
			if (!worker.location().isInGarrison())
			{
				idleWorkers.add(worker);
			}
			
		}
		while (idleWorkers.size() > 0)
		{
			if (Game.planet() == Planet.Earth)
			{
				buildOrder();
			}
			if (shouldBlueprintFactory())
			{
				placeStructure(UnitType.Factory);
			}
			if (shouldBuildRocket())
			{
				placeStructure(UnitType.Rocket);
			}
			if (Game.PLANET == Planet.Earth)
			{
				loadRocket();
			}
			replicateWorkers();
			
			if (Game.PLANET == Planet.Earth)
			{
				giveWorkersOrders();
				tryBuildFactory ();
			}
			harvest();
			moveRandomly();
			idleWorkers = new HashSet<Robot>();
			idleWorkers.addAll(replicatedWorkers);
			replicatedWorkers = new HashSet<Robot>();
		}
		
	}
	
	
	
	
}
