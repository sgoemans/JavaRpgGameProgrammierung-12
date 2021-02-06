package de.neuromechanics.state;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import de.neuromechanics.Camera;
import de.neuromechanics.Game;
import de.neuromechanics.KeyManager;
import de.neuromechanics.Level;
import de.neuromechanics.PathFinderTileMap;
import de.neuromechanics.Player;
import de.neuromechanics.SpriteSheet;
import de.neuromechanics.TileSet;
import de.neuromechanics.network.Broadcast;
import de.neuromechanics.network.INetworkPlayer;
import de.neuromechanics.network.NetworkPlayer;
import de.neuromechanics.network.Receiver;
import de.neuromechanics.network.Sender;
import de.neuromechanics.pathfinder.AStarPathFinder;
import de.neuromechanics.pathfinder.IPathFinder;
import de.neuromechanics.pathfinder.Path;

public class GameState extends State implements Serializable, INetworkPlayer {
	private static final long serialVersionUID = -5205085269605782656L;
	private Player player;
	private Level level;
	private Camera gameCamera;
//	private AnimEntity fire; 
	private PathFinderTileMap map;
	/** The path finder we'll use to search our map */
	private IPathFinder finder;
	private Path path;
	private KeyManager keyManager;
	private Broadcast t_bcast;
	private Sender t_tx;
	private Receiver t_recv;
	private ConcurrentHashMap<InetAddress, NetworkPlayer> hm_clients;
	private Thread t1, t2, t3;
	private SpriteSheet playerSprite;

	public GameState(Game game) {
		super(game);
		/*
		 * Network play functionality
		 */
		t_bcast = new Broadcast();
		t1 = new Thread(t_bcast);
		t1.start();
		hm_clients = new ConcurrentHashMap<InetAddress, NetworkPlayer>(32 /*Initial capacity*/, 0.75f /*load factor*/, 16 /*threads*/);
		t_recv = new Receiver(this, hm_clients);
		t2 = new Thread(t_recv);
		t2.start();
		t_tx = new Sender(hm_clients);
		t3 = new Thread(t_tx);
		t3.start();
		
		initLevel();
		playerSprite = initPlayerSprite();
		player = new Player(this, "Player", 320, 320, Player.PLAYER_DEFAULT_WIDTH, Player.PLAYER_DEFAULT_HEIGHT, 
				Player.DEFAULT_HEALTH, Player.DEFAULT_SPEED, playerSprite, true);
		
		//		SpriteSheet fireSprite = new SpriteSheet("/sprites/fire_big.png", 3, 1, 64, 128);
		
		gameCamera = new Camera(level.getSizeX(), level.getSizeY());

		//		fire = new AnimEntity(this, "Fire", fireSprite, 280, 280, 64, 192);

		map = (PathFinderTileMap) level.getPathTileMap();
		finder = new AStarPathFinder(map, 500 /*maxSearchDistance*/, false /*diagonal*/);

		game.screen.getCanvas().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handleMousePressed(e.getX(), e.getY());
			}
		});
//		game.screen.getCanvas().addMouseMotionListener(new MouseMotionListener() {
//			public void mouseDragged(MouseEvent e) {
//			}
//			public void mouseMoved(MouseEvent e) {
//				handleMouseMoved(e.getX(), e.getY());
//			}
//		});
		keyManager = game.getKeyManager();
	}
	/**
	 * The Receiver class instanciates networkPlayer objects. To increase cohesian, the GameState class
	 * implements the INetworkPlayer interface. Therefore, regardless which class provides the createNetworkPlayer
	 * method, the Receiver's constructor is only given the object which implements this interface.
	 */
	@Override
	public NetworkPlayer createNetworkPlayer(int hashCode, long timeInMillies) {
		NetworkPlayer np = new NetworkPlayer(this, "NetworkPlayer", playerSprite, 390, 320,
				Player.PLAYER_DEFAULT_WIDTH, Player.PLAYER_DEFAULT_HEIGHT, 
				Player.DEFAULT_HEALTH, Player.DEFAULT_SPEED );
		return np;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initLevel() {
		TileSet[] tileSet = new TileSet[3];
		// Boden Tileset für Layer mit blockenden Kacheln, der hinter der Spielfigur gerenderd wird
		HashSet hs = new HashSet(Arrays.asList(115, 116, 117, 127, 129, 139, 140, 141));
		tileSet[0] = new TileSet("/tiles/rpg.png", 12 /*sizeX*/, 12/*sizeY*/, 0 /*sizeZ*/, 3 /*border*/, hs);

		// Zweites Tileset fï¿½r Layer mit blockenden Kacheln, der ï¿½ber der Spielfigur gerendered wird
		hs = new HashSet(Arrays.asList(33, 138, 139, 145, 146, 170, 171));
		tileSet[1] = new TileSet("/tiles/tilec.png", 17 /*sizeX*/, 16 /*sizeY*/, 3 /*sizeZ*/, 0 /*border*/, hs);

		// Transparent Z / foreground layer tileset, no blocking tiles
		tileSet[2] = new TileSet("/tiles/tilec.png", 17 /*sizeX*/, 16 /*sizeY*/, 3 /*sizeZ*/, 0 /*border*/, hs);

		String[] paths = new String[3];
		paths[0] = "/level/level2.txt";
		paths[1] = "/level/level2a.txt";
		paths[2] = "/level/level2b.txt";
		level = new Level(this, paths, tileSet);
	}

	public SpriteSheet initPlayerSprite() {
		return new SpriteSheet("/sprites/player.png", 3, 4, 64, 64);
	}

	@Override
	public void update() {
		keyManager.update();
		/* pathUpdate returns either the next movement p from a mouseclick or from a directional key */
		Point p = pathUpdate();
		/* 
		 * p.x == -99 if no directional key was pressed but a command key like ESC. 
		 * ESC switches from gameState to menuState. 
		 */
		if(p.y == KeyEvent.VK_ESCAPE) {
			saveState();
			stopThreads();
			State.setState(game.getMenuState());
			return;
		}
		player.setMove(p);
		player.update();

		/* Send the player's xMove, yMove and entityX, entityY values to the other players on the network */
		getSender().setData(";" + p.x + ";" + p.y + ";" + Integer.toString(player.getEntityX()) + ";" + player.getEntityY());
		/* Move all registered network characters around this playground */
		for(NetworkPlayer np : hm_clients.values()) {
			/* Their movement data have been set in the Receiver class */
			np.update();
		}
		//		fire.update();
	}
	@Override
	public void render(Graphics g) {
		level.render(g);
		for(NetworkPlayer np : hm_clients.values()) {
			np.render(g);
		}
		player.render(g);
		//		fire.render(g);
		level.renderZ(g);
	}

	public Camera getGameCamera(){
		return gameCamera;
	}

	public void stopThreads() {
		t_bcast.setRunning(false);
		t_recv.stop();
		t_recv.setRunning(false);
		t_tx.setRunning(false);
	}

	/**
	 * Diese Funktion prüft, ob der Pathfinder Algorithmus, der mit der Maus angestoï¿½en wurde,
	 * einen Pfad zurückgegeben hat. In diesem Fall muss die Spielerfigur diesen Pfad Kachel
	 * für Kachel abgehen. Beim Erreichen einer Kachel wird dieser Eintrag im Pfad gelï¿½scht.
	 * @return Point - Die x/y Bewegung, um die die Spielfigur in Richtung der nächsten Kachel
	 * vorrücken soll.
	 */
	private Point pathUpdate() {
		if(path != null && path.getLength() != 0) {
			int pixX = player.getEntityX();
			int mapX = path.getX(0) * TileSet.TILEWIDTH;
			int pixY = player.getEntityY();
			int mapY = path.getY(0) * TileSet.TILEHEIGHT;
			if((pixX >= mapX - player.speed && pixX <= mapX + player.speed) && 
					(pixY >= mapY - player.speed && pixY <= mapY + player.speed)) {
				path.removeStep(0);
			} else {
				int xMove = 0;
				if(pixX > mapX) xMove = -1;
				else if(pixX < mapX) xMove = 1;
				int yMove = 0;
				if(pixY > mapY) yMove = -1;
				else if(pixY < mapY) yMove = 1;
				return new Point(xMove, yMove);
			}
		}
		return game.getKeyManager().getInput();
	}
	/**
	 * Handle the mouse being pressed. If the mouse is over a unit select it. Otherwise we move
	 * the selected unit to the new target (assuming there was a path found)
	 * 
	 * @param x The x coordinate of the mouse cursor on the screen
	 * @param y The y coordinate of the mouse cursor on the screen
	 */
	//	private void handleMousePressed(int x, int y) {
	//		x -= 50;
	//		y -= 50;
	//		x /= 16;
	//		y /= 16;
	//		
	//		if ((x < 0) || (y < 0) || (x >= map.getWidthInTiles()) || (y >= map.getHeightInTiles())) {
	//			return;
	//		}
	//		
	//		if (map.getEntity(x, y) != null) {
	//			selectedx = x;
	//			selectedy = y;
	//			lastFindX = - 1;
	//		} else {
	//			if (selectedx != -1) {
	//				map.clearVisited();
	//				path = finder.findPath(new UnitMover(map.getEntity(selectedx, selectedy)), 
	//						   			   selectedx, selectedy, x, y);
	//				
	//				if (path != null) {
	//					path = null;
	//					Entity entity = map.getEntity(selectedx, selectedy);
	//					map.setEntity(selectedx, selectedy, null);
	//					map.setEntity(x, y, entity);
	//					selectedx = x;
	//					selectedy = y;
	//					lastFindX = - 1;
	//				}
	//			}
	//		}		
	//	}

	private void saveState() {
		Player player = ((GameState) State.getState()).getPlayer();
		try {
			FileOutputStream fos = new FileOutputStream("player.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(player);
			oos.close();
			System.out.println("Player saved!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void handleMousePressed(int x, int y) {
		// map.clearVisited();
		x += gameCamera.getxOffset();
		y += gameCamera.getyOffset();
		path = finder.findPath(player, (player.getEntityX()+2) / TileSet.TILEWIDTH, (player.getEntityY()+2) / TileSet.TILEHEIGHT, x / TileSet.TILEWIDTH, y / TileSet.TILEHEIGHT);
	}

	public Path getPath() {
		return path;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;		
	}

	public Level getLevel() {
		return level;
	}

	public ConcurrentHashMap<InetAddress, NetworkPlayer> getHm_clients() {
		return hm_clients;
	}

	public Sender getSender() {
		return t_tx;
	}
}
