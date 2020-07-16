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

import com.twoswap.gui.GUI;
import com.twoswap.mandelbrot.extras.Complex;

public class Input implements KeyListener, FocusListener, MouseListener, MouseMotionListener, MouseWheelListener {
	public static int mouseX = 0, mouseY = 0, mouseButton = 0;
	public static boolean mouseInFrame = false;
	public static boolean[] key = new boolean[256];

	public Input() {
	}

	public static void update() {
		if(mouseInFrame) {
			boolean s1 = Controller.s1, s2 = Controller.s2, s3 = Controller.s3;
			Complex c = GUI.screenToMand(mouseX,mouseY);
			Generator.planeIteration(s1?c.x:Controller.rC, s1?c.y:Controller.iC, s2?c.x:Controller.rZ, s2?c.y:Controller.iZ, s3?c.x:Controller.rX, s3?c.y:Controller.iX, 100, true);
		}
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		mouseInFrame = true;
	}

	public void mouseExited(MouseEvent e) {
		mouseInFrame = false;
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
		if (keyCode > 0 && keyCode < key.length)
			key[keyCode] = true;
		System.out.println(keyCode);
		if (keyCode == 37 || keyCode == 65) Controller.x -= .01 * Generator.width / Controller.zoom;
		if (keyCode == 38 || keyCode == 87) Controller.y -= .01 * Generator.height / Controller.zoom;
		if (keyCode == 39 || keyCode == 68) Controller.x += .01 * Generator.width / Controller.zoom;
		if (keyCode == 40 || keyCode == 83) Controller.y += .01 * Generator.height / Controller.zoom;
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
		mouseInFrame = true;
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		Controller.zoom *= e.getWheelRotation() > 0?1/1.1:1.1;
		GUI.controllerPanel.zoomBar.setValue((int) (2000*Math.log(Controller.zoom)));
	}
}