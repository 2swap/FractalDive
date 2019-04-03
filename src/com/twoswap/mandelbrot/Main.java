package com.twoswap.mandelbrot;

import java.awt.Canvas;

import com.twoswap.gui.GUI;
import com.twoswap.mandelbrot.extras.Gif;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final String TITLE = "Fractal Dive";

	private static  boolean running = false;
	private Thread thread;
	private GUI gui;

	public Main() {
		gui = new GUI();
	}

	public synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	@SuppressWarnings("unused")
	private synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void run() {
		requestFocus();
		while (running) tick();
	}

	private void tick() {
		if (Generator.record && Generator.time-1 == Generator.frames) {
			Gif.run();
			running = false;
			System.out.println("Final x: "+Generator.c.x+" Final y: "+Generator.c.y);
		}
		GUI.render();
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.start();
		//Generator.setupMagicPalette();
	}
}
