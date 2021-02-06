package de.neuromechanics.state;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashSet;

import javax.imageio.ImageIO;

import de.neuromechanics.Game;
import de.neuromechanics.KeyManager;
import de.neuromechanics.Player;
import de.neuromechanics.TileSet;
import de.neuromechanics.Utils;

public class MenuState extends State {
	private KeyManager keyManager;
	private GameState gameState;
	private int menuItem;
	private TileSet tileSet;
	private Game game;
	private	Font font;
	private File playerSerFile = new File("Player.ser");
	private BufferedImage menuItemFrame;

	public MenuState(Game game){
		super(game);
		this.game = game;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		HashSet hs = new HashSet(Arrays.asList(0));
		tileSet = new TileSet("/tiles/border4.png", 6 /*sizeX*/, 3 /*sizeY*/, 0 /*sizeZ*/, 0 /*border*/, hs);

		try {
			menuItemFrame = ImageIO.read(TileSet.class.getResource("/tiles/menuitemframe.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		keyManager = game.getKeyManager();
		//		font = new Font("Lucida Handwriting", Font.BOLD, 36);
		font = new Font("Matura MT Script Capitals", Font.BOLD, 40);
		//		font = new Font("SchoolHouse Cursive B", Font.BOLD, 48);
		//		font = new Font("Stencil", Font.BOLD, 48);
	}

	@Override
	public void render(Graphics g) {
		Canvas canvas = game.getCanvas();
		Color color = new Color(0x900000);
		canvas.setBackground(color);
		/* 
		 * First line of main menu screen
		 */
		renderMenu(g, 0 /* tilenum */, 0 /* x */, 0 /* y */);
		for(int x = 1; x <= Game.SCREEN_WIDTH / TileSet.TILEWIDTH - 2; x++) {
			renderMenu(g, 2, x, 0);			
		}
		renderMenu(g, 1, Game.SCREEN_WIDTH / TileSet.TILEWIDTH - 1, 0);
		/* 
		 * Line 2 to sizeY of main menu screen
		 */
		int y;
		for(y = 1; y <= Game.SCREEN_HEIGHT / TileSet.TILEHEIGHT - 2; y++) {
			renderMenu(g, 8, 0, y);
			int i;
			for(i = 1; i <= Game.SCREEN_WIDTH / TileSet.TILEWIDTH - 2; i++) {
				renderMenu(g, 12, i, y);			
			}
			renderMenu(g, 14, i, y);
		}
		/* 
		 * Last line of main menu screen
		 */
		renderMenu(g, 6 /* tilenum */, 0 /* x */, y /* y */);
		int x;
		for(x = 1; x <= Game.SCREEN_WIDTH / TileSet.TILEWIDTH - 2; x++) {
			renderMenu(g, 13, x, y);			
		}
		renderMenu(g, 7, x, y);

		g.setFont(font);
		color = new Color(0xEEEEEE);
		g.setColor(color);
		g.drawString("New Game", Game.SCREEN_WIDTH/2-120, 200);
		g.drawString("Resume", Game.SCREEN_WIDTH/2-120, 280);
		g.drawString("Quit", Game.SCREEN_WIDTH/2-120, 360);
		if(!playerSerFile.exists()) {
			color = new Color(0x600000);
			g.setColor(color);
			g.drawString("Resume", Game.SCREEN_WIDTH/2-120, 280);
		}
		g.drawImage(menuItemFrame, Game.SCREEN_WIDTH/2-190, 140 + menuItem * 80, 384, 96, null);
	}
	private void renderMenu(Graphics g, int tile, int x, int y) {
		tileSet.renderTile(g, tile, x * TileSet.TILEWIDTH, y * TileSet.TILEHEIGHT);
	}

	@Override
	public void update() {
		keyManager.update();
		Point p = keyManager.getInput();
		if(p.y == KeyEvent.VK_DOWN) {
			if(menuItem < 2) menuItem++;
		} else if(p.y == KeyEvent.VK_UP) {
			if(menuItem > 0) menuItem--;
		} else if(p.y == KeyEvent.VK_ENTER) {
			if(menuItem == 0) {
				if(playerSerFile.exists()) playerSerFile.delete();
				gameState = new GameState(game);
				State.setState(gameState);
			} else if(menuItem == 1) {
				if(playerSerFile.exists()) {
					gameState = new GameState(game);
					restoreState();
					State.setState(gameState);
				}
			} else {
				game.gameStop();
			}
		}
	}
	/*
	 * Restores the player's state in the game (position, health, speed, ...). 
	 */
	private void restoreState() {
		Player player;
		try {
			FileInputStream fis = new FileInputStream("player.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			player = (Player) ois.readObject();
			ois.close();
			gameState.setPlayer(player);
			player.setGameState(gameState);
			player.setSpriteSheet(gameState.initPlayerSprite());
			System.out.println("Player restored!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
