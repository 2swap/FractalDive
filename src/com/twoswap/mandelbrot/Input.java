package com.twoswap.mandelbrot;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Input implements KeyListener, FocusListener, MouseListener, MouseMotionListener, MouseWheelListener {
	public static int mouseX = 0, mouseY = 0, mouseButton = 0;
	public static boolean[] key = new boolean[256];

	public static void update() {
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
//                if (keyCode == 37)
//                    screen.cx -= screen.zoom * Game.width / 100;
//                if (keyCode == 38)
//                    screen.cy -= screen.zoom * Game.height / 100;
//                if (keyCode == 39)
//                    screen.cx += screen.zoom * Game.width / 100;
//                if (keyCode == 40)
//                    screen.cy += screen.zoom * Game.height / 100;
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
//            screen.zoom(e.getWheelRotation() > 0);
	}
}