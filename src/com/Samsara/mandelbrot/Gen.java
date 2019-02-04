package com.Samsara.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.jafama.FastMath;

public class Gen {

	//dont touch stuff in this block
	//fx = -0.750045367143, fy = -0.004786271734;
	//fx = -0.7925403632943916, fy = 0.16076317715583555
	//fx = 0.6569488539350342, fy = -0.41280661014731673
	//Final x: -0.6612648356278878 Final y: 0.4151437869617779
	public static int searchDepth = 200, minDepth, maxDepth, lastMinDepth = 10000, lastMaxDepth = 0;//some stuff the program keeps track of
	public static String[] styles = {"rainbow", "deepbow", "randomcycle", "purplegreen"};//a list of styles for random selection
	public static double rr = Math.random() * 6, rg = Math.random() * 6, rb = Math.random() * 6;//used for the random-y styles

	//Settings for zooms- change away!
	public static int width = 1280, height = 720, time = 0;//screen size and timer
	public static String style = "rainbow";//the way it looks
	public static double pow = 2;//exponent of the iterated function
	public static double inhale = -.2;//how fast the colors should go in(-) or out(+)

	public static int frames = 800;
	public static double speed = .02, zoomSpeed = 1.03;//speed of camera convergence to target, speed of zoom increase
	public static double fx = -1.315180982097868, fy = 0.073481649996795, ox = fx, oy = fy, zoom = 100, fzoom = 100000;//coordinates and zoom level
	public static boolean drift = false, randomizer = false;//drift looks for interesting spots, randomizer picks a random style configuration.
	public static double densityPreference = .52;

	public static boolean record = true;
	
	

	public static int[] generate() {
		if(randomizer && time == 0) randomize();
		minDepth = 100000;
		maxDepth = 0;
		zoom *= zoomSpeed;
		ox = serp(ox, fx, speed * (drift?5:1));
		oy = serp(oy, fy, speed * (drift?5:1));
		int[] pix = new int[width * height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				doPixel(x, y, pix);
		if(time++%10==0)System.out.println(time + " " + searchDepth);
		lastMinDepth = minDepth;
		lastMaxDepth = maxDepth;
		if(Math.random() < .001) savePic(pix, "images/"+Math.random()+".png");
		pix[width/2+height/2*width] = 0xffffff;
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
				if ((i * i + (r + 1) * (r + 1) < .25 * .25 || ((r - .25) * (r - .25) + i * i + .5 * (r - .25)) * ((r - .25) * (r - .25) + i * i + .5 * (r - .25)) < .25 * ((r - .25) * (r - .25) + i * i)))
					break;
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
		if(drift) {
			double lerp = (divergeCount-lastMinDepth)/(.01+lastMaxDepth-lastMinDepth);
			double weight = 1-square(lerp-densityPreference+Math.sin(time/60.)/20);
			if(!diverged) weight*=2;
			fx += (r-ox)*.00005*weight;
			fy += (i-oy)*.00005*weight;
		}
		if (diverged && divergeCount >= searchDepth - 5)searchDepth++;
		if (diverged) minDepth = Math.min(minDepth, divergeCount);
		if (diverged) maxDepth = Math.max(maxDepth, divergeCount);
		return diverged?divergeCount:-1;
	}

	private static boolean doPixel(int x, int y, int[] pix) {
		double nPart = (x - width / 2) / (zoom) + ox;
		double iPart = (y - height / 2) / (zoom) + oy;// width, zoom is in terms of width. Stretched otherwise.
		int depth = computeDepth(nPart, iPart);
		int r = 0, g = 0, b = 0;
		if (depth == -2) r = g = b = 255; //error
		if (depth >= 0) {// if it's not in the set
			double effects = 0;
			if (style.contains("inset")) {
				r+=255;
				g+=255;
				b+=255;
				effects++;
			}
			if (style.contains("light")) {
				r += (int) square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, rr)) / Math.PI * 512)))/256.;
				g += (int) square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, rg)) / Math.PI * 512)))/256.;
				b += (int) square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, rb)) / Math.PI * 512)))/256.;
				effects++;
			}
			if (style.contains("rainbow")) {
				r += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (2 + inhale*time) / 3) + 128);
				g += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (0 + inhale*time) / 3) + 128);
				b += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (4 + inhale*time) / 3) + 128);
				effects++;
			}
			if (style.contains("deepbow")) {
				r += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time)) / 3) + 128);
				g += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 2 / 3)) / 3) + 128);
				b += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 4 / 3)) / 3) + 128);
				effects++;
			}
			if (style.contains("randomcycle")) {
				r += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (rr + inhale*time) / 3) + 128);
				g += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (rg + inhale*time) / 3) + 128);
				b += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (rb + inhale*time) / 3) + 128);
				effects++;
			}
			if (style.contains("purplegreen")) {
				r += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (2 + inhale*time) / 10.) + 128);
				g += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (0 + inhale*time) / 10.) + 128);
				b += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (4 + inhale*time) / 10.) + 128);
				effects++;
			}
			r/=effects;
			g/=effects;
			b/=effects;
			if(style.contains("dark")) {
				double shader = 1/Math.exp(2*(depth-lastMinDepth)/(.01+lastMaxDepth-lastMinDepth));
				r*=shader;
				g*=shader;
				b*=shader;
			}
		}
		pix[x + y * width] = r * 0x10000 + g * 0x100 + b;
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
	
	public static double square(double x) {
		return x*x;
	}
	
	public static void randomize() {
		zoom = .1;
		style = styles[(int)(Math.random()*styles.length)];
		style+="dark";
		inhale = (Math.random()*.8-.4);
		double theta = 2*Math.PI*Math.random();
		fx = Math.cos(theta);
		fy = Math.sin(theta);
		System.out.println("Style: " + style);
	}
}