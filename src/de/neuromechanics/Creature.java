package de.neuromechanics;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import de.neuromechanics.state.GameState;

public abstract class Creature extends AnimEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6511313147555966517L;
	protected int health;
	public int speed;
	protected int xMove, yMove;
	/**
	 * 
	 * @param name - The name of the creature/player
	 * @param spriteSheet - The creature or game character's spritesheet
	 * @param x - The x position on the canvas where to draw the initial image
	 * @param y - The x position on the canvas where to draw the initial image
	 * @param width
	 * @param height
	 * @param health
	 * @param speed
	 */
	public Creature(GameState gameState, String name, SpriteSheet spriteSheet, int x, int y, int width, int height, int health, int speed) {
		super(gameState, name, spriteSheet, x, y, width, height);
		this.health = health;
		this.speed = speed;
		xMove = 0;
		yMove = 0;
	}	

	int op = 1;
	int[][] touched;
	int oldX;
	int oldY;
	public void move(){
		if(xMove == 0 && yMove == 0) {
			/* If no directional key was pressed or mouse clicked, do the following:
			 * 1) pause animation so that no further slow counting takes place (slow stays 7)
			 * 2) Set slow to 7 so that the player immediately starts moving as soon as a directional key is pressed
			 * 3) Reset the pausing spritesheet image to the non-walking one  */
			pause = true;
			slow = 7;
			xPos = 1;
			return;
		}
		pause = false;

		/* if the pending move causes a collision, we need the old coordinates to revert the move */
		oldX = getEntityX();
		oldY = getEntityY();
		
		/* Do the move */
		setEntityX(getEntityX() + xMove * speed);
		setEntityY(getEntityY() + yMove * speed);
		
		/* Check for a collision on the tilemap */
		touched = ((GameState) gameState).getLevel().getTilesTouched(this);
		if(Utils.containsBlock(touched)) {
			setEntityX(oldX);
			setEntityY(oldY);
		}
		
		/* Check for a collision with network players */
//		for(Player p : team) {
//			if(p != null && Utils.overlaps(getEntityRectangle(), p.getEntityRectangle())) {
//				setEntityX(oldX);
//				setEntityY(oldY);
//				break;
//			}
//		}
		setCurrentImage(xMove, yMove);
	}

	int direction;
	private void setCurrentImage(int xMove, int yMove) {
		if(yMove == -1) {
			direction = 3;
		} else if(yMove == 1) {
			direction = 0;
		} else if(xMove == -1) {
			direction = 1;
		} else if(xMove == 1) {
			direction = 2;
		}
		yPos = direction;
	}

	public void setMove(Point p) {
		if(p.x == -99) {
			xMove = 0;
			yMove = 0;
		} else {
			xMove = p.x;
			yMove = p.y;
		}
	}
}