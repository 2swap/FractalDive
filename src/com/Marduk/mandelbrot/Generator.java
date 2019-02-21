package com.Marduk.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.Marduk.mandelbrot.extras.Complex;

public class Generator {

	//dont touch stuff in this block
	public static int searchDepth = 200, minDepth, maxDepth, lastMinDepth = 10000, lastMaxDepth = 0, time = 0;//some stuff the program keeps track of
	public static Styler s = new Styler("deepbow",-.2); //The look of the palette and such
	public static Controller c = new Controller(); //The thing that controls the motion of the zoom
	
	//Settings for zooms- change away!
	public static int width = 1024, height = 768; //screen size
	public static double pow = 2;//exponent of the iterated function
	public static int frames = 1000;//how long the gif should be
	public static boolean record = true; //Whether or not to save it as gif
	
	public static int[] generate() {
		minDepth = 100000;
		maxDepth = 0;
		int[] pix = new int[width * height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				doPixel(x, y, pix);
		if(++time%10==0)System.out.println(time + " " + searchDepth);
		lastMinDepth = minDepth;
		lastMaxDepth = maxDepth;
		if(Math.random() < .001) savePic(pix, "images/"+Math.random()+".png");
		pix[width/2+height/2*width] = 0xffffff;
		c.zoom(pix, width, height, time);
		return pix;
	}
	
	public static double serp(double a, double b, double spd) {
		return a+(b-a)*spd;
	}
	
	private static int computeDepth(double r, double i) {
		double editI = i;
		double editN = r;
		boolean diverged = false;
		int divergeCount = 0;

		while (!diverged) {
			if (editI * editI + editN * editN > 4) diverged = true;
			divergeCount++;
			double newN, newI;
			if (pow == 2) {
				if ((i * i + (r + 1) * (r + 1) < .25 * .25 || ((r - .25) * (r - .25) + i * i + .5 * (r - .25)) * ((r - .25) * (r - .25) + i * i + .5 * (r - .25)) < .25 * ((r - .25) * (r - .25) + i * i))) break;
				newN = editN * editN - editI * editI;
				newI = editI * editN * 2;
			} else if (pow == -1) {
				newN = editN / (editN * editN + editI * editI);
				newI = -editI / (editN * editN + editI * editI);
			} else if (pow == 3) {
				newN = editN * editN * editN - 3 * editN * editI * editI;
				newI = 3 * editN * editN * editI - editI * editI * editI;
			} else {
				Complex c = new Complex(editN, editI).pow(pow);
				newN = c.x;
				newI = c.y;
			}
			editI = newI + i;
			editN = newN + r;
			if (divergeCount > searchDepth) break;
		}
		if(!diverged) divergeCount = 0;
		if (diverged && divergeCount >= searchDepth - 5)searchDepth++;
		if (diverged) minDepth = Math.min(minDepth, divergeCount);
		if (diverged) maxDepth = Math.max(maxDepth, divergeCount);
		return diverged?divergeCount:-1;
	}

	private static boolean doPixel(int x, int y, int[] pix) {
		double nPart = (x - width / 2) / (c.zoom) + c.x;
		double iPart = (y - height / 2) / (c.zoom) + c.y;// width, zoom is in terms of width. Stretched otherwise.
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

	public static BufferedImage getImageFromArray(int[] pixels) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, width, height, pixels, 0, width);
		return image;
	}
}