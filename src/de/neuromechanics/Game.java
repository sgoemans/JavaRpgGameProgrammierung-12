package de.neuromechanics;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;

import de.neuromechanics.state.GameState;
import de.neuromechanics.state.MenuState;
import de.neuromechanics.state.State;

public class Game implements Runnable, WindowListener {
	public static final int FPS = 60;
	public static final long maxLoopTime = 1000 / FPS;
	public static final int SCREEN_WIDTH = 1280;
	public static final int SCREEN_HEIGHT = 960;

	public Screen screen;
//	private AnimEntity fire;
	private Graphics g;
	private BufferStrategy bs;
	private KeyManager keyManager;
	private Canvas canvas;
	private State menuState;

	public static void main(String[] arg) {
		Game game = new Game();
		new Thread(game).start();
	}


	boolean running = true;
	@Override
	public void run() {
		long timestamp;
		long oldTimestamp;

		keyManager = new KeyManager();
		screen = Screen.getInstance("Game", SCREEN_WIDTH, SCREEN_HEIGHT);
		screen.getFrame().addWindowListener(this);
		screen.getFrame().addKeyListener(keyManager);
		canvas = screen.getCanvas();
		menuState = new MenuState(this);
		State.setState(menuState);
		canvas.createBufferStrategy(3);
		bs = canvas.getBufferStrategy();

		while(running) {
			oldTimestamp = System.currentTimeMillis();
			/*
			 * UPDATE
			 */
			update();
			/*
			 * 
			 */
			timestamp = System.currentTimeMillis();
			if(timestamp-oldTimestamp > maxLoopTime) {
				//				System.out.println("Wir sind zu spät!");
				//				continue;
			}
			/*
			 * RENDER
			 */
			render();
			/*
			 * 
			 */
			timestamp = System.currentTimeMillis();
			//			System.out.println(maxLoopTime + " : " + (timestamp-oldTimestamp));
			if(timestamp-oldTimestamp <= maxLoopTime) {
				try {
					Thread.sleep(maxLoopTime - (timestamp-oldTimestamp) );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Stopped!");
		System.exit(0);
	}

	void update() {
		State.getState().update();
	}

	void render() {
		/* This loop repeats the rendering if the drawing buffer was lost */
		do {
			/* The inner loop ensures that the contents of the drawing buffer
	           are consistent in case the underlying surface was recreated */
			do {
				g = bs.getDrawGraphics();
				g.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
				if(State.getState() != null)
					State.getState().render(g);
				bs.show();
				g.dispose();
			} while(bs.contentsRestored());
		} while(bs.contentsLost());
	}

	public MenuState getMenuState(){
		return (MenuState) menuState;
	}

	public KeyManager getKeyManager(){
		return keyManager;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	boolean isPaused = false;
	@Override
	public void windowActivated(WindowEvent arg0) {
		gameResume();		
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		screen.getFrame().dispose();	
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		gamePause();
		gameStop();
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		gamePause();		
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		gameResume();		
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		gamePause();	
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub		
	}

	private void gamePause() {
		isPaused = true;
	}
	private void gameResume() {
		isPaused = false;
	}
	public void gameStop() {
		running = false;
	}
}
