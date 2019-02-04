package com.Samsara.mandelbrot;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final String TITLE = "Fractal Dive";

	private boolean running = false;
	private Thread thread;
	private BufferedImage img;
	private int[] pixels;
	InputHandler input = new InputHandler();
	static JFrame frame = new JFrame();

	public Game() {
		Dimension size = new Dimension(Gen.width, Gen.height);
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
		addMouseWheelListener(input);
		addKeyListener(input);
		img = new BufferedImage(Gen.width, Gen.height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
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
		while (running) {
			tick();
			render();
		}
	}

	private void tick() {
		InputHandler.update();
		if (Gen.record && Gen.time-1 == Gen.frames) {
			Gif.run();
			running = false;
			System.out.println("Final x: "+Gen.ox+" Final y: "+Gen.oy);
		}
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		int[] pix = Gen.generate();
		for (int i = 0; i < Gen.width * Gen.height; i++)
			pixels[i] = pix[i];
		if (Gen.record && Gen.time > 1)
			Gen.savePic(pix, "giffer/img" + (Gen.time - 2) + ".png");
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, Gen.width + 10, Gen.height + 10, null);
		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		Game game = new Game();
		frame.add(game);
		frame.pack();
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		game.start();
	}
}
