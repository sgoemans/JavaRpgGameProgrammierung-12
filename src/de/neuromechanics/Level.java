package de.neuromechanics;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import de.neuromechanics.pathfinder.ITileBasedMap;
import de.neuromechanics.state.GameState;

public class Level implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2604664208568396707L;
	private TileSet[] tileSet;
	private int sizeX, sizeY;
	private int[][][] tileMap;
	private String[][] tokens;
	private GameState gameState;
	private PathFinderTileMap pathTileMap;

	public Level(GameState gameState, String[] path, TileSet[] ts) {
		this.gameState = gameState;
		this.tileSet = ts;

		tokens = new String[ts.length][];
		// In tokens[] kommen alle Tilenummern aus den drei Level-Textdateien
		for(int i = 0; i < ts.length; i++) {
			String file = Utils.loadFileAsString(path[i]);
			tokens[i] = file.split("\\s+");			
		}
		// Die ersten zwei tokens sind die x und y Ausdehnung der Maps
		sizeX = Utils.parseInt(tokens[0][0]);
		sizeY = Utils.parseInt(tokens[0][1]);
		tileMap = new int[3][sizeX][sizeY];
		pathTileMap = new PathFinderTileMap(sizeX, sizeY);
		// Jetzt haben wir insgesamt drei Layer/Tilemaps, drei tokens Arrays, und drei Tilesets
		for(int z = 0; z < tileMap.length; z++) {
			// Offset für die x und y Ausdehnung Zahlen in der Textdatei
			int i = 2;
			// Tilemaps werden hier mit den Tilenummern bestückt
			for(int y = 0; y < sizeY; y++){
				for(int x = 0; x < sizeX; x++){
					int tileNum = Utils.parseInt(tokens[z][i++]);
					tileMap[z][x][y] = tileNum;
					if(ts[z].hs.contains(tileNum)) {
						// Tatsächlich wird für jedes Layer dieselbe pathTileMap mit blockenden Kacheln versehen.
						// Wenn in einem Layer eine Kachel blockt, ist es egal, ob sie auch in anderen Layern blockt.
						pathTileMap.setBlocked(x, y, tileNum);
					}
				}
			}
		}
	}
	/*
	 * Wir brauchen zwei Layer für den Hintergrund, da wir die zweite Ebene in der Regel mit Transparenz auf die erste legen wollen.
	 * So sparen wir uns z.B. einen Baum mit jedem einzelnen Terrainhintergrund (Gras, Sand, Steine,...) vorrätig halten zu müssen.
	 */
	public void render(Graphics g){
		r(g, tileMap[0], tileSet[0]);
		// Hier wird die zweite Ebene gerendered. 
		r(g, tileMap[1], tileSet[1]);
	}

	public void renderZ(Graphics g) {
		r(g, tileMap[2], tileSet[2]);
	}

	private void r(Graphics g, int[][] tm, TileSet ts) {
		int xStart = Math.max(0, gameState.getGameCamera().getxOffset() / TileSet.TILEWIDTH);
		int xEnd = Math.min(sizeX, (gameState.getGameCamera().getxOffset() + Game.SCREEN_WIDTH) / TileSet.TILEWIDTH + 1);
		int yStart = Math.max(0, gameState.getGameCamera().getyOffset() / TileSet.TILEHEIGHT);;
		int yEnd = Math.min(sizeY, (gameState.getGameCamera().getyOffset() + Game.SCREEN_HEIGHT) / TileSet.TILEHEIGHT + 1);
		for(int tileY = yStart; tileY < yEnd; tileY++){
			for(int tileX = xStart; tileX < xEnd; tileX++){
				if(tm[tileX][tileY] == -1) continue;
				ts.renderTile(g, tm[tileX][tileY], tileX * TileSet.TILEWIDTH - gameState.getGameCamera().getxOffset(),
						tileY * TileSet.TILEHEIGHT - gameState.getGameCamera().getyOffset());
				if (gameState.getPath() != null) {
					if (gameState.getPath().contains(tileX, tileY)) {
						g.setColor(Color.blue);
						g.fillRect(tileX * TileSet.TILEWIDTH - gameState.getGameCamera().getxOffset()+16,
								tileY * TileSet.TILEHEIGHT - gameState.getGameCamera().getyOffset()+16,16,16);
					}
				}	

			}
		}
	}
	public int[][] getTilesTouched(Creature player) {
		// tileset.length ist 3, da wir drei Tilesets haben. Die [2] sind die beiden Eckpunkte
		int[][] ret = new int[tileSet.length][2];
		// Die  x-Koordinate des unteren linken Eckpunkt des Player-Sprites
		int numX = (player.getEntityX() + Player.MARGIN_HORIZ) / player.getWidth();
		// Die y-Koordinate der beiden unteren Eckpunkte
		int numY = (player.getEntityY() + player.getHeight() - Player.MARGIN_VERT) / player.getHeight();
		// Für beide Eckpunkte ...
		for(int i = 0; i < 2; i++) {
			// ... und alle Layer die blockende Kacheln haben können (Layer 0 und 1)...
			for(int z = 0; z < tileMap.length; z++) {
				ret[z][i] = tileMap[z][numX][numY];
				if(tileSet[z].hs.contains(ret[z][i])) {
					ret[z][i] <<= 16;
				}
			}
			// Hier wird für den zweiten Durchlauf der zweite (rechte) Eckpunkt berechnet
			numX = (player.getEntityX() + player.getWidth() - Player.MARGIN_HORIZ) / player.getWidth();
		}
		return ret;
	}

	public ITileBasedMap getPathTileMap() {
		return pathTileMap;
	}
	public int getSizeX() {
		return sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
}
