package com.twoswap.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.twoswap.mandelbrot.Controller;
import com.twoswap.mandelbrot.Generator;
import com.twoswap.mandelbrot.Styler;

class DimCanvas extends Canvas{
	
	private static final long serialVersionUID = 1L;
	private int set = 0;
	private BufferedImage img;
	private int[] pixels;
	
	public DimCanvas(int plane) {
		this.set = plane;
		setBackground(Color.WHITE);
		setBounds(GUI.margins,GUI.margins,GUI.dimCanvasWidth,GUI.dimCanvasWidth);
		img = new BufferedImage(GUI.dimCanvasWidth,GUI.dimCanvasWidth, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
	}
	public void render(double pointR, double pointI) {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		for (int x = 0; x < GUI.dimCanvasWidth; x++)
			for (int y = 0; y < GUI.dimCanvasWidth; y++) {
				double r = x*4./GUI.dimCanvasWidth-2+pointR;
				double i = y*4./GUI.dimCanvasWidth-2+pointI;
				double[] d = new double[2];
				int out = 0;
				String prevType = Styler.type;
				Styler.type = "rainbow";
				int depth = Generator.computeDepth(set==0?r:Controller.rC, set==0?i:Controller.iC, set==1?r:Controller.rZ, set==1?i:Controller.iZ, set==2?r:Controller.rX, set==2?i:Controller.iX, 25, d);
				out = Styler.getColor(depth, 0, 0, 0, d[0],d[1]);
				Styler.type = prevType;
				pixels[x+y*GUI.dimCanvasWidth] = out;
			}
		for(int dx = 0; dx < 2; dx++) for(int dy = 0; dy < 2; dy++) pixels[GUI.dimCanvasWidth/2+dx+(GUI.dimCanvasWidth/2+dy)*GUI.dimCanvasWidth] = 0xff0000;
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, GUI.dimCanvasWidth, GUI.dimCanvasWidth, null);
		g.dispose();
		bs.show();
	}
	public void renderX() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.setFont(new Font("TimesRoman", Font.PLAIN, 10)); 
		g.clearRect(0, 0, GUI.dimCanvasWidth, GUI.dimCanvasWidth);
		g.drawString(DimPanel.names[set] + " set off", 8, 16);
		g.dispose();
		bs.show();
	}
}