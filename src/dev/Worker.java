package dev;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import bc.*;

public class Worker 
{
	private static HashSet<Robot> moveableWorkers = new HashSet<Robot>();
	private static HashSet<Robot> actionableWorkers = new HashSet<Robot>();
	public static PriorityQueue<Tile> factoryGrid;
	private static Tile factoryGridCenter = Rocket.landingGridCenter;
	private static Random rng = new Random(5468);
	public static int rockets = 0;
	private static HashSet<Robot> replicatedWorkers = new HashSet<Robot>();
	private static int[] directions = {0, 1, 1 - Game.WIDTH, -1 * Game.WIDTH, -1 - Game.WIDTH, -1, Game.WIDTH - 1, Game.WIDTH, Game.WIDTH + 1};
	
	private static Direction intToDir(int dir)
	{
		if (dir == directions[0])
		{
			return Direction.Center;
		}
		else if (dir == directions[1])
		{
			return Direction.East;
		}
		else if (dir == directions[2])
		{
			return Direction.Southeast;
		}
		else if (dir == directions[3])
		{
			return Direction.South;
		}
		else if (dir == directions[4])
		{
			return Direction.Southwest;
		}
		else if (dir == directions[5])
		{
			return Direction.West;
		}
		else if (dir == directions[6])
		{
			return Direction.Northwest;
		}
		else if (dir == directions[7])
		{
			return Direction.North;
		}
		else
		{
			return Direction.Northeast;
		}
		
	}
	
	
	private static class WorkerScoreTuple implements Comparator<WorkerScoreTuple>
	{
		public int score;
		public Tile worker;
		
		WorkerScoreTuple(int SCORE, Tile work)
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
		
		all: for (int ally = 0; ally < Constants.startingAlliesLocation.length; ally++)
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
				else if (pathDistance == -1)
				{
					min = Constants.INFINITY;
					max = Constants.INFINITY;
					farWorker.worker = Constants.startingAlliesLocation[ally];
					break all;
				}
			}
			if (max < closeWorker.score)
			{
				closeWorker.score = max;
				closeWorker.worker = Constants.startingAlliesLocation[ally];
			}
			if (min > farWorker.score)
			{
				farWorker.score = min;
				farWorker.worker = Constants.startingAlliesLocation[ally];
			}
		}
		if (closeWorker.score <= Constants.RUSHTHRESHOLD)
		{
			System.out.printf("rush distance is %d\n", closeWorker.score);
			return closeWorker;
		}
		System.out.printf("rush distance is %d\n", farWorker.score);
		return farWorker;
	}
	
	private static class FactoryTileComparator implements Comparator<Tile>
	{
		public int compare(Tile arg0, Tile arg1) {
			int dist1 = Pathfinding.pathLength(arg0, factoryGridCenter);
			int dist2 = Pathfinding.pathLength(arg1, factoryGridCenter);
			if (dist1 < dist2 && dist1 != -1)
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
				int distance = Pathfinding.pathLength(bestWorker.worker, enemy);
				if (distance != -1 && distance < Pathfinding.pathLength(bestWorker.worker, nearestEnemy))
				{
					nearestEnemy = enemy;
				}
			}
			Direction buildDir = Pathfinding.path(bestWorker.worker, nearestEnemy);
			System.out.printf("buildDir = %s\n", buildDir.name());
			if (buildDir == Direction.Center)
			{
				buildDir = bestWorker.worker.directionTo(nearestEnemy);
			}
			Constants.RUSHDISTANCE = bestWorker.score;
			if (bestWorker.score > Constants.RUSHTHRESHOLD || bestWorker.score == -1)
			{
				buildDir = Utilities.oppositeDir(buildDir);
			}
			else
			{
				GlobalStrategy.rush = true;
				Constants.FACTORYBUILDRANGE = 4;
				Constants.FACTORYREPLICATEPRESSURE = 500;
				Constants.WORKERREPLICATEDEPOSITWEIGHT = 0;
				Constants.WORKERLIMIT = 0;
				factoryGridCenter = nearestEnemy;
				initializeBuildGrid();
				Game.resetResearch();
				if (GameInfoCache.karboniteDeposits.get(bestWorker.worker.getX()/Constants.QUADRANTSIZE + bestWorker.worker.getY()/Constants.QUADRANTSIZE * Constants.QUADRANTROWSIZE).size() > 15)
				{
					Game.queueResearch(UnitType.Worker);
				}
				Game.queueResearch(UnitType.Knight);
				Game.queueResearch(UnitType.Knight);
				Game.queueResearch(UnitType.Knight);
				Game.queueResearch(UnitType.Healer);
				Game.queueResearch(UnitType.Healer);
				Game.queueResearch(UnitType.Healer);
				Game.queueResearch(UnitType.Rocket);
				Game.queueResearch(UnitType.Rocket);
				Game.queueResearch(UnitType.Rocket);
				
				return;
			}
			
			buildDir = Utilities.findNearestOccupiableDir(bestWorker.worker, buildDir);
			System.out.printf("building in direction %s\n", buildDir.name());
			if (buildDir != Direction.Center)
			{
				factoryGridCenter = Utilities.offsetInDirection(bestWorker.worker, buildDir, 1);
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
						factoryGridCenter = Utilities.offsetInDirection(worker.tile(), buildDir, 1);
						initializeBuildGrid();
					}
					if (buildDir == Direction.Center)
					{
						Robot disintegratedWorker = GameInfoCache.allyWorkers.get(0);
						GameInfoCache.allyWorkers.remove(0);
						actionableWorkers.remove(disintegratedWorker);
						moveableWorkers.remove(disintegratedWorker);
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
		if (worker.abilityHeat() >= 10)
		{
			return -1;
		}
		score += (Constants.WORKERLIMIT - GameInfoCache.allyWorkers.size()) * Constants.WORKERLIMITWEIGHT;
		if (Game.round() >= 750 || Game.getTeamArray(Planet.Earth).get(0) == 1)
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
		score += Game.karbonite() / 25;
		score += Constants.WORKERREPLICATEDEPOSITWEIGHT * GameInfoCache.karboniteDeposits.get(worker.tile().getX()/Constants.QUADRANTSIZE + worker.tile().getY()/Constants.QUADRANTSIZE * Constants.QUADRANTROWSIZE).size();
		for (Robot otherWorker:GameInfoCache.allyWorkers)
		{
			if (otherWorker == worker || !otherWorker.location().isOnMap())
			{
				continue;
			}
			distance = Pathfinding.pathLength(otherWorker.tile(), worker.tile());
			if (distance != -1)
			{
				score -= 80/distance;
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
				workerOrder.add(new WorkerScoreTuple(replicateScore(worker), worker.tile()));
			}
		}
		while (Game.karbonite() > Constants.REPLICATECOST && workerOrder.peek() != null && workerOrder.peek().score > 0)
		{
			Direction replicateDir;
			Robot worker = Game.senseUnitAtLocation(workerOrder.poll().worker);
			if (GlobalStrategy.rush)
			{
				if (GameInfoCache.currentBlueprints.size() > 0)
				{
					replicateDir = Utilities.findNearestOccupiableDir(worker.tile(), worker.tile().directionTo(GameInfoCache.currentBlueprints.iterator().next().tile()));
				}
				else
				{
					replicateDir = Utilities.findNearestOccupiableDir(worker.tile(), worker.tile().directionTo(factoryGridCenter));
				}
				
			}
			else
			{
				replicateDir = Utilities.findNearestOccupiableDir(worker.tile(), Utilities.oppositeDir(worker.tile().directionTo(factoryGridCenter)));
			}
			
			if (Game.canReplicate(worker, replicateDir))
			{
				Game.replicate(worker, replicateDir);
				if (Game.hasUnitAtLocation(Utilities.offsetInDirection(worker.tile(), replicateDir, 1)))
				{
					replicatedWorkers.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(worker.tile(), replicateDir, 1)));
					GameInfoCache.allyWorkers.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(worker.tile(), replicateDir, 1)));
				}
				
			}
		}
	}
	
	private static void giveWorkersOrders()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		workerLabel: for (Robot worker:moveableWorkers)
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
						actionableWorkers.remove(worker);
					}
					removeWorkers.add(worker);
				}
				continue workerLabel;
			}
		}
		for (Robot worker: removeWorkers)
		{
			moveableWorkers.remove(worker);
		}
	}
	
	private static void harvest()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		if (GameInfoCache.karboniteLocations.size() == 0)
		{
			return;
		}
		worker: for (Robot worker:actionableWorkers)
		{
			int index = worker.tile().getX() + worker.tile().getY() * Game.WIDTH;
			if (Game.round() > Constants.HARVESTABORTROUND && !Game.canSenseLocation(Tile.getInstance(Game.PLANET, GameInfoCache.nearestKarbonite[index] % Game.WIDTH, GameInfoCache.nearestKarbonite[index] / Game.WIDTH)))
			{
				continue;
			}
			Direction moveDir = Direction.Center;
			moveDir = Pathfinding.karbonitePath(worker.tile());
			if (Game.canMove(worker, moveDir))
			{
				Game.moveRobot(worker, moveDir);
				moveableWorkers.remove(worker);
			}
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
			actionableWorkers.remove(worker);
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
		if (GlobalStrategy.rush && GameInfoCache.allyFactories.size() == 0)
		{
			return true;
		}
		if (Game.karbonite() > 2000)
		{
			return true;
		}
		if (GameInfoCache.currentBlueprints.size() > 1)
		{
			return false;
		}
		if (GameInfoCache.allyFactories.size() < Constants.FACTORYGOAL && Game.karbonite() >= 200)
		{
			return true;
		}
		else if (Game.karbonite() >=(320) && GameInfoCache.allyFactories.size() < Constants.FACTORYLIMIT)
		{
			return true;
		}
		else if (Game.karbonite() >= 1000)
		{
			return true;
		}
		return false;
	}
	
	private static int builderScore(Robot worker)
	{
		int score = 0;
		Robot[] nearbyWorkers = Game.senseNearbyUnits(worker.tile(), 8, UnitType.Worker, Game.TEAM);
		if (nearbyWorkers.length <= 9)
		{
			score =+ nearbyWorkers.length * 10;
		}
		else if (nearbyWorkers.length <= 12)
		{
			score += 60;
		}
		else
		{
			score -= 30;
		}
		score -= worker.abilityHeat() / nearbyWorkers.length;
		int passable = Utilities.passableSurroundings(worker.tile());
		score += 30 * passable;
		Robot[] nearbyEnemies = Game.senseCombatUnits(worker.tile(), Constants.RANGERRANGE + 15, Game.ENEMY);
		if (nearbyEnemies.length > 0)
		{
			score -= 100;
		}
		nearbyEnemies = Game.senseNearbyUnits(worker.tile(), Constants.RANGERRANGE + 50, UnitType.Factory, Game.ENEMY);
		if (nearbyEnemies.length > 0)
		{
			score -= 50;
		}
		if (passable < 4)
		{
			score -= 200;
		}
		return score;
	}
	
	private static void placeStructure(UnitType structure)
	{
		Robot closestWorker = null;
		int bestDistance = Constants.INFINITY;
		int currentDistance;
		Tile placement = null;
		
		if (GlobalStrategy.rush && GameInfoCache.allyFactories.size() == 0)
		{   //place at the opponent's base and use closest worker to it
			placement = Constants.startingEnemiesLocation[0];
			
			for (Robot worker:actionableWorkers)
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
			if (Game.hasUnitAtLocation(placement))
			{
				placement = Utilities.offsetInDirection(placement, Pathfinding.ghostPath(placement, closestWorker.tile()), 1);
				placement = Utilities.offsetInDirection(placement, Pathfinding.ghostPath(placement, closestWorker.tile()), 1);
			}
			if (Pathfinding.pathLength(closestWorker.tile(), placement) < Constants.RUSHDISTANCE / 2)
			{
				Tile bestPlacement = null;
				int bestScore = 0;
				Tile test = null;
				int testScore = 0;
				for (Direction dir:Game.moveDirections)
				{
					test = Utilities.offsetInDirection(closestWorker.tile(), dir, 1);
					if (!Game.onMap(test, Game.PLANET) || Game.isOccupiable(test) == 0)
					{
						continue;
					}
					testScore = Utilities.passableSurroundings(test);
					if (testScore > bestScore)
					{
						bestScore = testScore;
						bestPlacement = test;
					}
				}
				placement = bestPlacement;
				bestDistance = 1;
			}
			}
		else
		{
			PriorityQueue<WorkerScoreTuple> workerOrder = new PriorityQueue<WorkerScoreTuple>(GameInfoCache.allyWorkers.size()+1, new WorkerScoreTuple(0,null));
			for (Robot worker:actionableWorkers)
			{
				if (worker.location().isOnMap())
				{
					workerOrder.add(new WorkerScoreTuple(builderScore(worker), worker.tile()));
				}
			}
			while (placement == null)
			{
				closestWorker = Game.senseUnitAtLocation(workerOrder.peek().worker);
				if (closestWorker.movementHeat() < 10)
				{
					placement = workerOrder.peek().worker;
					bestDistance = 0;
				}
				else
				{
					Tile bestPlacement = null;
					int bestScore = 0;
					Tile test = null;
					int testScore = 0;
					for (Direction dir:Game.moveDirections)
					{
						test = Utilities.offsetInDirection(closestWorker.tile(), dir, 1);
						if (!Game.onMap(test, Game.PLANET) || Game.isOccupiable(test) == 0)
						{
							continue;
						}
						testScore = Utilities.passableSurroundings(test);
						if (testScore > bestScore)
						{
							bestScore = testScore;
							bestPlacement = test;
						}
					}
					if (bestScore > 2 && Utilities.passableSurroundings(closestWorker.tile()) > 2)
					{
						placement = bestPlacement;
						bestDistance = 1;
					}
				}
				workerOrder.poll();
			}
		}
		
		if (bestDistance == 1)
		{
			if (Game.canBlueprint(closestWorker, structure, closestWorker.tile().directionTo(placement)))
			{
				Game.blueprint(closestWorker, structure, closestWorker.tile().directionTo(placement));
				actionableWorkers.remove(closestWorker);
				
				
				if (structure == UnitType.Rocket)
				{
					rockets++;
				}
			}
			if (structure == UnitType.Rocket)
			{
				factoryGrid.poll();
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
					if (Game.canBlueprint(closestWorker, structure, closestWorker.tile().directionTo(placement)))
					{
						Game.blueprint(closestWorker, structure, closestWorker.tile().directionTo(placement));
						actionableWorkers.remove(closestWorker);
						if (structure == UnitType.Rocket)
						{
							factoryGrid.poll();
						}
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
					if (Pathfinding.pathLength(closestWorker.tile(), placement) == 1)
					{
						if (Game.canBlueprint(closestWorker, structure, closestWorker.tile().directionTo(placement)))
						{
							Game.blueprint(closestWorker, structure, closestWorker.tile().directionTo(placement));
							actionableWorkers.remove(closestWorker);
							factoryGrid.poll();
							if (structure == UnitType.Rocket)
							{
								rockets++;
							}
						}
					}

				}
			}
			else
			{
				factoryGrid.poll();
			}
			
		}
		moveableWorkers.remove(closestWorker);
		
		if (GlobalStrategy.rush && Game.karbonite() >= 100 && (Pathfinding.pathLength(closestWorker.tile(), placement) > 3 + Constants.RUSHDISTANCE / 2 || Game.karbonite() > 260))
		{
			Direction bestDir = Pathfinding.path(closestWorker.tile(), placement);
			if (Game.canReplicate(closestWorker, bestDir))
			{
				Game.replicate(closestWorker, bestDir);
				if (Game.hasUnitAtLocation(Utilities.offsetInDirection(closestWorker.tile(), bestDir, 1)))
				{
					replicatedWorkers.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(closestWorker.tile(), bestDir, 1)));
					GameInfoCache.allyWorkers.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(closestWorker.tile(), bestDir, 1)));
				}
			}
		}
	}
	
	private static void tryBuildFactory()
	{
		System.out.printf("we have %d actionable workers\n", actionableWorkers.size());
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		worker: for (Robot worker:actionableWorkers)
		{
			for (Robot thing: GameInfoCache.currentBlueprints)
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
			actionableWorkers.remove(worker);
		}
	}
	
	private static boolean shouldLoadRocket() 
	{
		if (GameInfoCache.allyCombat.size() == 0 || Rocket.launchedRockets == 0 || Game.getTeamArray(Planet.Mars).get(1) + Rocket.flyingWorkers < Constants.MARSWORKERGOAL)
		{
			return true;
		}
		return false;
	}
	
	private static boolean shouldBuildRocket()
	{
		
		if (Game.PLANET == Planet.Mars)
		{
			return false;
		}
		
		if (GameInfoCache.allyRockets.size() > Constants.ROCKETBUILDLIMIT && Game.round() < Constants.FACTORYHALTROUND) {
			return false;
		}
		
		if (Game.round >= 650)
		{
			return true;
		}
		
		if (GameInfoCache.allyFactories.size() >= Constants.FACTORYGOAL && Game.karbonite() > 150 && Game.researchInfo().getLevel(UnitType.Rocket) > 0)
		{
			return true;
		}
		return false;
	}
	
	private static void moveRandomly()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		for (Robot worker:moveableWorkers)
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
			moveableWorkers.remove(worker);
		}
	}
	
	private static void loadRocket()
	{
		HashSet<Robot> removeWorkers = new HashSet<Robot>();
		worker: for (Robot worker:moveableWorkers)
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
			moveableWorkers.remove(worker);
			actionableWorkers.remove(worker);
		}
	}
	
	private static void tryHarvest()
	{
		HashSet<Robot> removeWorkers = new HashSet<>();
		for (Robot worker:actionableWorkers)
		{
			int loc = worker.tile().getX() + worker.tile().getY() * Game.WIDTH;
			for (int dir: directions)
			{
				int test = loc + dir;
				if (GameInfoCache.karboniteLocations.contains(test))
				{
					if (Game.canHarvest(worker, intToDir(dir)))
					{
						Game.harvest(worker, intToDir(dir));
						removeWorkers.add(worker);
						break;
					}
				}
			}
		}
		for (Robot worker: removeWorkers)
		{
			actionableWorkers.remove(worker);
		}
	}
	
	private static void repair()
	{
		int count = 0;
		HashSet<Robot> removeWorkers = new HashSet<>();
		for (Robot factory: GameInfoCache.allyFactories)
		{
			if (factory.health() < factory.maxHealth() && factory.structureIsBuilt() == 1)
			{
				for (Robot worker:moveableWorkers)
				{
					if (Pathfinding.pathLength(worker.tile(), factory.tile()) <= Constants.FACTORYBUILDRANGE)
					{
						count++;
						Direction bestDir = Pathfinding.path(worker.tile(), factory.tile());
						if (Game.canMove(worker, bestDir))
						{
							Game.moveRobot(worker, bestDir);
							removeWorkers.add(worker);
						}
						if (Game.canRepair(worker, factory))
						{
							Game.repair(worker, factory);
							actionableWorkers.remove(worker);
						}
						if (count >= 7)
						{
							break;
						}
					}
				}
			}
		}
		
		for (Robot worker: removeWorkers)
		{
			moveableWorkers.remove(worker);
		}
	}
	
	private static void aggressivelyHarvest()
	{
		int currentDistance = 0;
		int bestDistance = Constants.INFINITY;
		Robot closestWorker = null;
		int targetIndex = GameInfoCache.nearestKarbonite[Constants.startingEnemiesLocation[0].getX() + Constants.startingEnemiesLocation[0].getY() * Game.WIDTH];
		Tile target = Tile.getInstance(Game.PLANET, targetIndex % Game.WIDTH, targetIndex / Game.WIDTH);
		for (Robot worker:moveableWorkers)
		{
			if (worker.abilityHeat() < 10)
			{
				currentDistance = Pathfinding.pathLength(worker.tile(), target);
				if (currentDistance < bestDistance)
				{
					bestDistance = currentDistance;
					closestWorker = worker;
				}
			}
		}
		if (closestWorker != null && bestDistance > 5)
		{
			Robot[] nearbyEnemies = Game.senseCombatUnits(closestWorker.tile(), Constants.RANGERRANGE, Game.ENEMY);
			if (nearbyEnemies.length == 0)
			{
				Direction bestDir = Pathfinding.path(closestWorker.tile(), target);
				if (Game.canMove(closestWorker, bestDir))
				{
					Game.moveRobot(closestWorker, bestDir);
					moveableWorkers.remove(closestWorker);
				}
				bestDir = Pathfinding.path(closestWorker.tile(), target);
				if (Game.canReplicate(closestWorker, bestDir))
				{
					Game.replicate(closestWorker, bestDir);
					if (Game.hasUnitAtLocation(Utilities.offsetInDirection(closestWorker.tile(), bestDir, 1)))
					{
						replicatedWorkers.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(closestWorker.tile(), bestDir, 1)));
						GameInfoCache.allyWorkers.add(Game.senseUnitAtLocation(Utilities.offsetInDirection(closestWorker.tile(), bestDir, 1)));
					}
				}
			}
			
		}
		
	}
	
	public static void run() 
	{
		moveableWorkers = new HashSet<Robot>(GameInfoCache.allyWorkers.size());
		actionableWorkers = new HashSet<Robot>(GameInfoCache.allyWorkers.size());
		for (Robot worker:GameInfoCache.allyWorkers)
		{
			if (!worker.location().isInGarrison())
			{
				actionableWorkers.add(worker);
				if (worker.movementHeat() < 10)
				{
					moveableWorkers.add(worker);
				}
			}
			
		}
		if (Game.planet() == Planet.Earth)
		{
			buildOrder();
		}
		while (actionableWorkers.size() > 0)
		{
			if (Game.round() <= Constants.AGGRESSIVEHARVESTTIMER && !GlobalStrategy.rush && GameInfoCache.karboniteLocations.size() > 30)
			{
				aggressivelyHarvest();
			}
			if (shouldBuildRocket())
			{
				placeStructure(UnitType.Rocket);
			}
			if (shouldBlueprintFactory())
			{
				placeStructure(UnitType.Factory);
			}
			repair();
			if (Game.PLANET == Planet.Earth)
			{
				if (shouldLoadRocket()) {
					loadRocket();
				}
			}
			if (Game.PLANET == Planet.Earth)
			{
				giveWorkersOrders();
				tryBuildFactory ();
			}
			harvest();
			tryHarvest();
			replicateWorkers();
			moveRandomly();
			actionableWorkers = new HashSet<Robot>();
			moveableWorkers = new HashSet<Robot>();
			actionableWorkers.addAll(replicatedWorkers);
			moveableWorkers.addAll(replicatedWorkers);
			replicatedWorkers = new HashSet<Robot>();
			
		}
		
	}
	
	
	
	
}
