package de.neuromechanics;

import java.awt.Graphics;
import java.io.Serializable;

import de.neuromechanics.state.GameState;

public class AnimEntity extends Entity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2387346757748401445L;
	protected transient SpriteSheet spriteSheet;
	protected transient GameState gameState;
	protected boolean pause;

	public AnimEntity(GameState game, String name, SpriteSheet sheet, int x, int y, int width, int height) {
		super(name, x, y, width, height);
		this.spriteSheet = sheet;
		this.gameState = game;
	}

	protected int slow = 0;
	protected int xPos = 0;
	protected int yPos = 0;
	private int op = 1;

	@Override
	public  void update() {
		if(spriteSheet != null) {
			if(!pause) {
				if(slow++ >= 7) {
					slow = 0;
					if(op == -1 && xPos <= 0) {
						op = 1;
					} else if(op == 1 && xPos >= 2) {
						op = -1;
					}
					xPos = (xPos + op);
				}
			}
			image = spriteSheet.getSpriteElement(xPos, yPos);
		}			
	}
	@Override
	public void render(Graphics g) {
		//			g.drawImage(image, entityX, entityY, null);
		g.drawImage(image, getEntityX() - gameState.getGameCamera().getxOffset(), getEntityY() - gameState.getGameCamera().getyOffset(), null);
	}
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	public void setSpriteSheet(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}
}