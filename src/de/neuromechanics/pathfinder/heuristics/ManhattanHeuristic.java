package de.neuromechanics.pathfinder.heuristics;

import de.neuromechanics.pathfinder.IAStarHeuristic;
import de.neuromechanics.pathfinder.IMover;
import de.neuromechanics.pathfinder.ITileBasedMap;

/**
 * A heuristic that drives the search based on the Manhattan distance
 * between the current location and the target
 * 
 * @author Kevin Glass
 */
public class ManhattanHeuristic implements IAStarHeuristic {
	/** The minimum movement cost from any one square to the next */
	private int minimumCost;
	
	/**
	 * Create a new heuristic 
	 * 
	 * @param minimumCost The minimum movement cost from any one square to the next
	 */
	public ManhattanHeuristic(int minimumCost) {
		this.minimumCost = minimumCost;
	}
	
	/**
	 * @see IAStarHeuristic#getCost(ITileBasedMap, IMover, int, int, int, int)
	 */
	public float getCost(ITileBasedMap map, IMover mover, int x, int y, int tx,
			int ty) {
		return minimumCost * (Math.abs(x-tx) + Math.abs(y-ty));
	}

}
