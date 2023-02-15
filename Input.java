package base.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;

import base.Game;
import math.Vec;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public boolean[] keys, justUnPressed, justPressed, cantPress;
	
	public int mouseX, mouseY;
	public Vec cursor;
	public int scroll;
	public final Press
		lMouse, rMouse, mMouse,
		upArrow, downArrow, leftArrow, rightArrow,
		w, a, s, d,
		h,
		enter,
		esc,
		ctrl,
		tab,
		up, down, left, right,
		selectTool,
		select,
		deselect,
		brushTool,
		paint,
		eraserTool,
		erase,
		resetCam,
		deleteLayout,
		toggleConsole, consoleSubmit,
		save
	;
	public final Scroll
		consoleScroll
	;
	
	boolean newScroll;
	
	public class Press {
		
		private int[] keyCodes;
		
		public Press(int... keyCodes) {
			this.keyCodes = keyCodes;
			for (int i = 0; i < this.keyCodes.length; i++)
				this.keyCodes[i] += 3;
		}
		
		public boolean hold() {
			for (int keyCode : keyCodes)
				if (!keys[keyCode])
					return false;
			return true;
		}
		
		public boolean click() {
			for (int keyCode : keyCodes) {
				if (justPressed[keyCode])
					return hold();
			}
			return false;
		}
		
		public boolean unclick() {
			boolean justUnClicked = false;
			int holdNum = 0;
			for (int keyCode : keyCodes) {
				if (keys[keyCode])
					holdNum++;
				if (justUnPressed[keyCode]) {
					justUnClicked = true;
					holdNum++;
				}
			}
			return holdNum == keyCodes.length && justUnClicked;
		}
		
		public String toString() {
			return "keyCodes: " + Arrays.toString(keyCodes);
		}

	}
	
	public class Scroll {
		
		Press up, down;
		
		public Scroll() {}
		
		public Scroll(Press up, Press down) {
			this.up = up;
			this.down = down;
		}
		
		public double scrollAmt() {
			if (up == null)
				return scroll;
			return up == down ? 0 : (up.hold() ? 1 : -1);
		}
		
	}
	
	public Input() {
		keys = new boolean[256];
		justUnPressed = new boolean[keys.length];
		justPressed = new boolean[keys.length];
		cantPress = new boolean[keys.length];
		
		cursor = new Vec();
		
		lMouse = new Press(MouseEvent.BUTTON1 - 4);
		rMouse = new Press(MouseEvent.BUTTON3 - 4);
		mMouse = new Press(MouseEvent.BUTTON2 - 4);
		
		upArrow = new Press(KeyEvent.VK_UP);
		downArrow = new Press(KeyEvent.VK_DOWN);
		leftArrow = new Press(KeyEvent.VK_LEFT);
		rightArrow = new Press(KeyEvent.VK_RIGHT);
		
		w = new Press(KeyEvent.VK_W);
		a = new Press(KeyEvent.VK_A);
		s = new Press(KeyEvent.VK_S);
		d = new Press(KeyEvent.VK_D);
		
		h = new Press(KeyEvent.VK_H);
		
		enter = new Press(KeyEvent.VK_ENTER);
		
		ctrl = new Press(KeyEvent.VK_CONTROL);
		
		esc = new Press(KeyEvent.VK_ESCAPE);
		
		tab = new Press(KeyEvent.VK_TAB);
		
		up = new Press(KeyEvent.VK_W);
		left = new Press(KeyEvent.VK_A);
		down = new Press(KeyEvent.VK_S);
		right = new Press(KeyEvent.VK_D);
		
		selectTool = new Press(KeyEvent.VK_ALT, KeyEvent.VK_S);
		select = new Press(MouseEvent.BUTTON1 - 4);
		deselect = new Press(MouseEvent.BUTTON3 - 4);
		
		brushTool = new Press(KeyEvent.VK_CONTROL, KeyEvent.VK_B);
		paint = new Press(MouseEvent.BUTTON1 - 4);

		eraserTool = new Press(KeyEvent.VK_CONTROL, KeyEvent.VK_E);
		erase = new Press(MouseEvent.BUTTON1 - 4);
		
		resetCam = new Press(KeyEvent.VK_C);
		
		deleteLayout = new Press(KeyEvent.VK_BACK_SPACE);
		
		toggleConsole = new Press(KeyEvent.VK_CONTROL, KeyEvent.VK_H);
		consoleSubmit = new Press(KeyEvent.VK_CONTROL, KeyEvent.VK_ENTER);
		consoleScroll = new Scroll();
		
		save = new Press(KeyEvent.VK_CONTROL, KeyEvent.VK_S);
	}
	
	public void resetKeys() {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
			justUnPressed[i] = false;
			justPressed[i] = false;
			cantPress[i] = false;
		}
		tick();
	}
	
	public void tick() {
		if (!Game.get().gamePanel.isFocusOwner())
			System.out.println("no longer focus owner");
		cursor.setX(mouseX);
		cursor.setY(mouseY);
		
		for (int i = 0; i < keys.length; i++) {
			if (cantPress[i] && !keys[i]) {
				cantPress[i] = false;
				justUnPressed[i] = true;
			} else if (justPressed[i]) {
				cantPress[i] = true;
				justPressed[i] = false;
			} else if (justUnPressed[i]) {
				justUnPressed[i] = false;
			}
			if (!cantPress[i] && keys[i])
				justPressed[i] = true;
		}
		if (!newScroll)
			scroll = 0;
		if (scroll != 0)
			newScroll = false;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() + 3 < 0 || e.getKeyCode() + 3 >= keys.length)
			return;
		keys[e.getKeyCode() + 3] = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() + 3 < 0 || e.getKeyCode() + 3 >= keys.length)
			return;
		keys[e.getKeyCode() + 3] = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scroll = e.getWheelRotation();
		newScroll = true;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = Game.get().getHeight() - e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = Game.get().getHeight() - e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() + 3 < 0 || e.getButton() + 3 >= keys.length)
			return;
		keys[e.getButton() - 1] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() + 3 < 0 || e.getButton() + 3 >= keys.length)
			return;
		keys[e.getButton() - 1] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
}
