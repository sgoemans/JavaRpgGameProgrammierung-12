package de.neuromechanics.network;

import de.neuromechanics.Creature;
import de.neuromechanics.SpriteSheet;
import de.neuromechanics.state.GameState;

public class NetworkPlayer extends Creature {
	long lastActiveInMillies;
	int id;
	
	public NetworkPlayer(GameState gameState, String name, SpriteSheet spriteSheet,
			int x, int y, int width, int height, int health, int speed) {
		
		super(gameState, name, spriteSheet, x, y, width, height, health, speed);
	}

	@Override
	public void update() {
		move();
		super.update();
	}

	public long getLastActiveInMillies() {
		return lastActiveInMillies;
	}

	public void setLastActiveInMillies(long lastActiveInMillies) {
		this.lastActiveInMillies = lastActiveInMillies;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
