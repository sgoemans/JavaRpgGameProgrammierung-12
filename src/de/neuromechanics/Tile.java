package de.neuromechanics;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

public class Tile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8217823681826381700L;
	private ArrayList<BufferedImage> animImages = new ArrayList<BufferedImage>();
	private int imgIdx = 0;
	private boolean solid;
	private RpgEvent rpgEvent;

	public Tile(BufferedImage image, boolean solid) {
		this.animImages.add(image);
		this.solid = solid;
	}
	
	private int slow;
	public BufferedImage getAnimImage() {
		if(animImages.size() > 1) {
			if(slow++ >= 7) {
				slow = 0;
				if(++imgIdx >= animImages.size()) {
					imgIdx = 0;
				}			
			}
			return animImages.get(imgIdx);
		} else {
			return animImages.get(0);
		}
	}
	public void addAnimImage(BufferedImage animImage) {
		this.animImages.add(animImage);
	}
}
