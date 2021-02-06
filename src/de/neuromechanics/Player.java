package de.neuromechanics;

import java.io.Serializable;

import de.neuromechanics.pathfinder.IMover;
import de.neuromechanics.state.GameState;

public class Player extends Creature implements IMover, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -120744420124488402L;
	public static final int PLAYER_DEFAULT_WIDTH = 64;
	public static final int PLAYER_DEFAULT_HEIGHT = 64;
	public static final int DEFAULT_HEALTH = 100;
	public static final int DEFAULT_SPEED = 2;
	public static final int MARGIN_HORIZ = 28;
	public static final int MARGIN_VERT = 2;

	public Player(GameState gameState, String name, int x, int y, int width, int height, int health, int speed, SpriteSheet spriteSheet, boolean isPlayer) {
		super(gameState, name, spriteSheet, x, y, width, height, health, speed);
	}

	@Override
	public void update() {
		move();
		super.update();
		gameState.getGameCamera().centerOnEntity(this);
	}
}
