package de.neuromechanics;

import java.io.Serializable;

import de.neuromechanics.pathfinder.IMover;
import de.neuromechanics.pathfinder.ITileBasedMap;

public class PathFinderTileMap implements ITileBasedMap, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1432724026810288412L;
	private int[][] map;
	private int sizeX;
	private int sizeY;
	/** The entities on the game surface */
	private Entity[][] entities;
	/** Indicator if a given tile has been visited during the search */
	private boolean[][] visited;
	
	public PathFinderTileMap(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		map = new int[sizeX][sizeY];
		entities = new Entity[sizeX][sizeY];
		visited = new boolean[sizeX][sizeY];		
	}

	@Override
	public int getWidthInTiles() {
		return sizeX;
	}

	@Override
	public int getHeightInTiles() {
		return sizeY;
	}

	@Override
	public void pathFinderVisited(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean blocked(IMover mover, int x, int y) {
		// if there's an entity at the location, then it's blocked
//		if (getEntity(x,y) != null) {
//			return true;
//		}
		if(map[x][y] != 0) return true;
		return false;
	}

	@Override
	public void setBlocked(int x, int y, int type) {
		map[x][y] = type;
	}
	
	@Override
	public float getCost(IMover mover, int sx, int sy, int tx, int ty) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Clear the array marking which tiles have been visited by the path 
	 * finder.
	 */
	public void clearVisited() {
		for (int x = 0; x < getWidthInTiles(); x++) {
			for (int y = 0; y < getHeightInTiles(); y++) {
				visited[x][y] = false;
			}
		}
	}
	
	/**
	 * @see TileBasedMap#visited(int, int)
	 */
	public boolean visited(int x, int y) {
		return visited[x][y];
	}
	/**
	 * Get the unit at a given location
	 * 
	 * @param x The x coordinate of the tile to check for a unit
	 * @param y The y coordinate of the tile to check for a unit
	 * @return The ID of the unit at the given location or 0 if there is no unit 
	 */
	public Entity getEntity(int x, int y) {
//		return entities[x][y];
		return null;
	}
	
	/**
	 * Set the unit at the given location
	 * 
	 * @param x The x coordinate of the location where the unit should be set
	 * @param y The y coordinate of the location where the unit should be set
	 * @param unit The ID of the unit to be placed on the map, or 0 to clear the unit at the
	 * given location
	 */
	public void setEntity(int x, int y, Entity entity) {
		entities[x][y] = entity;
	}
}
