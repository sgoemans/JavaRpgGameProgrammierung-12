package de.neuromechanics;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;


public class Screen {
	/* Singleton pattern called Initialization-on-demand holder idiom
	 * see also http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static String title;
	private static int width, height;

	private static class Holder {
		static final Screen INSTANCE = new Screen();
	}
	public static Screen getInstance(String t, int w, int h) {
		title = t;
		width = w;
		height = h;
		return Holder.INSTANCE;
	}
	/* 
	 * End of Singleton code
	 */
	
	private JFrame frame;
	private Canvas canvas;

	private Screen() {
		this(title, width, height);
	}
	private Screen(String title, int width, int height){
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);
		frame.add(canvas);
		frame.pack();
	}

	public Canvas getCanvas(){
		return canvas;
	}

	public JFrame getFrame(){
		return frame;
	}
}