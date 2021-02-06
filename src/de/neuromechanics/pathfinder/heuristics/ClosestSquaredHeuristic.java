package de.neuromechanics.pathfinder.heuristics;

import de.neuromechanics.pathfinder.IAStarHeuristic;
import de.neuromechanics.pathfinder.IMover;
import de.neuromechanics.pathfinder.ITileBasedMap;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile. In this case the sqrt is removed
 * and the distance squared is used instead
 * 
 * @author Kevin Glass
 */
public class ClosestSquaredHeuristic implements IAStarHeuristic {

	/**
	 * @see IAStarHeuristic#getCost(ITileBasedMap, IMover, int, int, int, int)
	 */
	public float getCost(ITileBasedMap map, IMover mover, int x, int y, int tx, int ty) {		
		float dx = tx - x;
		float dy = ty - y;
		
		return ((dx*dx)+(dy*dy));
	}

}
