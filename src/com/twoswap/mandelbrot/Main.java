package com.twoswap.mandelbrot;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.twoswap.mandelbrot.extras.Gif;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final String TITLE = "Fractal Dive";

	private boolean running = false;
	private Thread thread;
	private BufferedImage img;
	private int[] pixels;
	static JFrame frame = new JFrame();

	public Main() {
		Dimension size = new Dimension(Generator.width, Generator.height);
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
		img = new BufferedImage(Generator.width, Generator.height, BufferedImage.TYPE_INT_RGB);
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
		if (Generator.record && Generator.time-1 == Generator.frames) {
			Gif.run();
			running = false;
			System.out.println("Final x: "+Generator.c.x+" Final y: "+Generator.c.y);
		}
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		int[] pix = Generator.generate();
		for (int i = 0; i < Generator.width * Generator.height; i++)
			pixels[i] = pix[i];
		if (Generator.record && Generator.time > 1)
			Generator.savePic(pix, "giffer/img" + (Generator.time - 2) + ".png");
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, Generator.width + 10, Generator.height + 10, null);
		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		Main main = new Main();
		frame.add(main);
		frame.pack();
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		main.start();
	}
}
