package com.Samsara.mandelbrot;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputHandler implements KeyListener, FocusListener, MouseListener, MouseMotionListener, MouseWheelListener {
	public static boolean forward, backward, left, right, space, esc, map, e;
	public static int mouseX = 0, mouseY = 0, mouseButton = 0, oldX = 0, oldY = 0;
	public static double turn = 0, turnUp = 0;
	public static boolean[] key = new boolean[256];

	public InputHandler() {

	}

	public static void update() {
		if (key[KeyEvent.VK_W])
			Gen.oy -= .1 / Gen.zoom;
		if (key[KeyEvent.VK_A])
			Gen.ox -= .1 / Gen.zoom;
		if (key[KeyEvent.VK_S])
			Gen.oy += .1 / Gen.zoom;
		if (key[KeyEvent.VK_D])
			Gen.ox += .1 / Gen.zoom;
		if (key[KeyEvent.VK_SPACE]) {
			Gen.width = 1024 * 1;
			Gen.height = 1024 * 1;
			Gen.savePic(Gen.generate(), "images/" + Gen.ox + "," + Gen.oy + "," + Gen.zoom + ".png");
		}
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		mouseButton = e.getButton();
	}

	public void mouseReleased(MouseEvent e) {
		mouseButton = 0;
	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode > 0 && keyCode < key.length) {
			key[keyCode] = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode > 0 && keyCode < key.length) {
			key[keyCode] = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		Gen.zoom *= Math.pow(.9, notches);
	}
}