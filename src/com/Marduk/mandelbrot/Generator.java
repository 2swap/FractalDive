package com.Marduk.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import javax.imageio.ImageIO;

import com.Marduk.mandelbrot.extras.Complex;

public class Generator {

	//dont touch stuff in this block
	public static int minDepth, maxDepth, lastMinDepth = 500000, lastMaxDepth = 0, time = 0;//some stuff the program keeps track of
	public static Styler s = new Styler("rainbow",.2); //The look of the palette and such
	public static Controller c = new Controller(); //The thing that controls the motion of the zoom
	
	//Settings for zooms- change away!
	public static int width = 192, height = 192; //screen size
	public static double pow = 5;//exponent of the iterated function
	public static int frames = 150;//how long the gif should be
	public static boolean record = true; //Whether or not to save it as gif
	
	public static int[] generate() {
		minDepth = 100000;
		maxDepth = 0;
		
		int[] pix = new int[width * height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				pix[x+y*width] = -1;
		
		for (int x = 0; x < width; x+=2)
			for (int y = 0; y < height; y+=2)
				doPixel(x, y, pix);
		
		optimizeFill(pix);
		
		for (int x = 1; x < width; x+=2)
			for (int y = 1; y < height; y+=2)
				if(pix[x+y*width] == -1)
					doPixel(x, y, pix);
		
		optimizeFill(pix);
		
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if(pix[x+y*width] == -1)
					doPixel(x, y, pix);
		
		
		
		if(++time%10==0)System.out.println(time + " " + c.searchDepth);
		lastMinDepth = minDepth;
		lastMaxDepth = maxDepth;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		if(Math.random() < .001) savePic(pix, "images/"+timestamp+".png");
		pix[width/2+height/2*width] = 0xffffff;
		c.zoom(pix, width, height, time);
		return pix;
	}
	
	private static void optimizeFill(int[] pix) {
		int edits = 1;
		while(edits > 0) {
			edits = 0;
			for (int x = 1; x < width-1; x++)
				for (int y = 1; y < height-1; y++) {
					int here = pix[x+y*width];
					if(here != -1)
						continue;

					int tl = pix[x-1+(y-1)*width];
					if(tl!=-1 && tl==pix[x+1+(y-1)*width] && tl==pix[x+1+(y+1)*width] && tl==pix[x-1+(y+1)*width]) {
						pix[x+y*width] = tl;
						edits++;
						continue;
					}

					int top = pix[x+(y-1)*width];
					if(top!=-1 && top==pix[x-1+y*width] && top==pix[x+(y+1)*width] && top==pix[x+1+y*width]) {
						pix[x+y*width] = top;
						edits++;
						continue;
					}
				}
		}
	}

	public static double serp(double a, double b, double spd) {
		return a+(b-a)*spd;
	}
	
	private static int computeDepth(double r, double i) {
		double editI = i, editR = r, ei2 = editI * editI, er2 = editR * editR;
		boolean diverged = false;
		int divergeCount = 0;

		while (!diverged) {
			if (ei2+er2 > 4) diverged = true;
			divergeCount++;
			double newR, newI;
			if (pow == 2) {
				newR = er2 - ei2;
				newI = editI * editR;
				newI += newI;
			} else if (pow == -1) {
				double div = 1 / (er2+ei2);
				newR = editR*div;
				newI = -editI*div;
			} else if (pow == 3) {
				newR = editR * er2 - 3 * editR * ei2;
				newI = (er2 + er2 + er2 - ei2) * editI;
			} else {
				Complex c = new Complex(editR, editI).pow(pow);
				newR = c.x;
				newI = c.y;
			}
			editI = newI + i;
			editR = newR + r;
			ei2 = editI * editI;
			er2 = editR * editR;
			if (divergeCount > c.searchDepth) break;
		}
		if(!diverged) divergeCount = 0;
		if (diverged && divergeCount >= c.searchDepth*.999)c.searchDepth+=100;
		if (diverged) minDepth = Math.min(minDepth, divergeCount);
		if (diverged) maxDepth = Math.max(maxDepth, divergeCount);
		return diverged?divergeCount:-1;
	}

	private static boolean doPixel(int x, int y, int[] pix) {
		double rotX = (x - width / 2) * Math.cos(c.angle) - (y - height / 2) * Math.sin(c.angle);
		double rotY = (x - width / 2) * Math.sin(c.angle) + (y - height / 2) * Math.cos(c.angle);
		double nPart = rotX / (c.zoom) + c.x;
		double iPart = rotY / (c.zoom) + c.y;// width, zoom is in terms of width. Stretched otherwise.
		int depth = computeDepth(nPart, iPart);
		pix[x + y * width] = s.getColor(depth, time, lastMinDepth, lastMaxDepth);
		return depth == -1;
	}

	public static void savePic(int[] pix, String fileName) {
		try {
			BufferedImage bi = getImageFromArray(pix);
			File outputfile = new File(fileName);
			ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setupMagicPalette() {
		s = new Styler(new File("Bop.png"));
		c = new Controller(.25009989470767,0.00000159156228,10000000000000d);
		c.searchDepth = 40000;
		c.zoomSpeed = 1.03;
		c.va = 0.003;
		frames = 61;
	}

	public static BufferedImage getImageFromArray(int[] pixels) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, width, height, pixels, 0, width);
		return image;
	}
}