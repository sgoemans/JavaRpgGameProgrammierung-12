package de.neuromechanics;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public abstract class Entity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9054257792812672683L;
	protected String name;
	private int entityX;
	private int entityY;
	private int width;
	private int height;
	protected transient BufferedImage image;

	public Entity(String name, BufferedImage image, int x, int y, int width, int height) {
		this(name, x, y, width, height);
		this.image = image;
	}
	public Entity(String name, int x, int y, int width, int height) {
		this.name = name;
		this.setEntityX(x);
		this.setEntityY(y);
		this.setWidth(width);
		this.setHeight(height);
	}

	protected abstract void update();
	
	protected void render(Graphics g) {
		g.drawImage(image, getEntityX(), getEntityY(), null);
	}
	/**
	 * @return the entityX
	 */
	public int getEntityX() {
		return entityX;
	}
	/**
	 * @param entityX the entityX to set
	 */
	public void setEntityX(int entityX) {
		this.entityX = entityX;
	}
	/**
	 * @return the entityY
	 */
	public int getEntityY() {
		return entityY;
	}
	/**
	 * @param entityY the entityY to set
	 */
	public void setEntityY(int entityY) {
		this.entityY = entityY;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @param image sets the image of the entity
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public Rectangle getEntityRectangle() {
		return new Rectangle(entityX+12, entityY, width-24, height);
	}
}