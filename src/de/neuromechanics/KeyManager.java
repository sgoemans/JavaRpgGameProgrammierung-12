package de.neuromechanics;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {

	private boolean[] keys;
	public boolean up, down, left, right, escape, enter, curUp, curDown;

	public KeyManager(){
		keys = new boolean[256];
	}

	public void update(){
		up = keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_D];
		escape = keys[KeyEvent.VK_ESCAPE];
		enter = keys[KeyEvent.VK_ENTER];
		curUp = keys[KeyEvent.VK_UP];
		curDown = keys[KeyEvent.VK_DOWN];
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	public Point getInput(){
		int xMove = 0;
		int yMove = 0;
		if(up)
			yMove = -1;
		if(down)
			yMove = 1;
		if(left)
			xMove = -1;
		if(right)
			xMove = 1;
		if(!(up || down || left || right)) {
			xMove = -99;
			if(escape) {
				yMove = KeyEvent.VK_ESCAPE;
				keys[KeyEvent.VK_ESCAPE] = false;
			} else if(enter) {
				yMove = KeyEvent.VK_ENTER;
				keys[KeyEvent.VK_ENTER] = false;
			} else if(curUp) {
				yMove = KeyEvent.VK_UP;
				keys[KeyEvent.VK_UP] = false;
			} else if(curDown) {
				yMove = KeyEvent.VK_DOWN;
				keys[KeyEvent.VK_DOWN] = false;
			}
		}
		return new Point(xMove, yMove);
	}
}
