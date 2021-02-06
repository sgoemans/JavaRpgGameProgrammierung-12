package de.neuromechanics.pathfinder.heuristics;

import java.io.Serializable;

import de.neuromechanics.pathfinder.IAStarHeuristic;
import de.neuromechanics.pathfinder.IMover;
import de.neuromechanics.pathfinder.ITileBasedMap;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 * 
 * @author Kevin Glass
 */
public class ClosestHeuristic implements IAStarHeuristic, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2788961513514934474L;

	/**
	 * @see IAStarHeuristic#getCost(ITileBasedMap, IMover, int, int, int, int)
	 */
	public float getCost(ITileBasedMap map, IMover mover, int x, int y, int tx, int ty) {		
		float dx = tx - x;
		float dy = ty - y;
		
		float result = (float) (Math.sqrt((dx*dx)+(dy*dy)));
		
		return result;
	}

}
