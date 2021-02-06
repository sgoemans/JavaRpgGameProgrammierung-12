package de.neuromechanics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

import javax.imageio.ImageIO;

public class TileSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4924349121534274904L;

	public static final int TILEWIDTH = 64, TILEHEIGHT = 64;

	private Tile[] tiles;

	@SuppressWarnings("rawtypes")
	public HashSet hs;

	public TileSet(String path, int sizeX, int sizeY, int sizeZ, int border, @SuppressWarnings("rawtypes") HashSet hs) {
		this.hs = hs;
		tiles = new Tile[sizeX * sizeY];
		BufferedImage tileSet;
		try {
			tileSet = ImageIO.read(TileSet.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		int i = 0;
		for(int y = 0; y < sizeY; y++) {
			for(int x = 0; x < sizeX; x++) {
				tiles[i] = new Tile(tileSet.getSubimage(x * (TILEWIDTH + border), y * (TILEHEIGHT + border), TILEWIDTH, TILEHEIGHT), hs.contains(i));
				i++;

				// Variante 1
				//
				//				BufferedImage img = tiles[i-1].getAnimImage();
				//				for(int xx = 0; xx < img.getWidth(); xx++) {
				//					for(int yy = 0; yy < img.getHeight(); yy++) {
				//						Color cc = new Color(img.getRGB(xx, yy), true);
				//						float[] compArray = new float[4];
				//						compArray = cc.getComponents(null);
				//						compArray[0] /= 2;
				//						compArray[1] /= 2;
				//						compArray[2] /= 2;
				//						Color cy = new Color(compArray[0], compArray[1], compArray[2], compArray[3]);
				//						img.setRGB(xx, yy, cy.getRGB());
				//					}
				//				}

				// Variante 2
				//
				//				BufferedImage img = tiles[i-1].getAnimImage();
				//				int red;
				//				int green;
				//				int blue;	
				//				int alpha;
				//				int color;
				//				int newColor;
				//				for(int xx = 0; xx < img.getWidth(); xx++) {
				//					for(int yy = 0; yy < img.getHeight(); yy++) {
				//						color = img.getRGB(xx,yy);
				//						red = (color & 0x00FF0000)>>17;
				//						green = (color & 0x0000FF00)>>9;
				//						blue = (color & 0x000000FF)>>1;
				//						alpha = color & 0xFF000000;
				//						newColor = (alpha + (red<<16) + (green<<8) + blue);
				//						img.setRGB(xx, yy, newColor);
				//						//img.setRGB(xx, yy, newColor);
				//					}
				//				}
			}
			for(int z = 1; z < sizeZ; z++) {
				tiles[i-1].addAnimImage(tileSet.getSubimage((sizeX - 1 + z) * (TILEWIDTH + border), y * (TILEHEIGHT + border), TILEWIDTH, TILEHEIGHT));
			}
		}
	}

	public void renderTile(Graphics g, int tileNum, int x, int y){
		g.drawImage(tiles[tileNum].getAnimImage(), x, y, TILEWIDTH, TILEHEIGHT, null);
	}
}