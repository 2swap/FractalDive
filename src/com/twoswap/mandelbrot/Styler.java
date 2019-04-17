package com.twoswap.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.twoswap.mandelbrot.extras.Complex;

import net.jafama.FastMath;

public class Styler {

	public static double inhale = 0;//how fast the colors should go in(-) or out(+)
	public static String type = "rainbow";//the way it looks
	public static boolean iterationCount = true;

	//for image palettes
	public static int cycleWidth = 1, cycleHeight = 1;
	public static int[][] cycler;
	
	public static String[] styles = {"rainbow", "deepbow", "cycle"};//, "light"};//a list of styles for random selection
	
	//initialize with a magic palette image
	public static void initPalette(File f) {
		type = "magicpalette";
		BufferedImage img = null;
		try{
			img = ImageIO.read(f);
		}catch(IOException e){
			e.printStackTrace();
		}
		cycleWidth = img.getWidth();
		cycleHeight = img.getHeight();
		cycler = new int[cycleWidth][cycleHeight];
		for (int x = 0; x < cycleWidth; x++)
			for (int y = 0; y < cycleHeight; y++)
				cycler[x][y] = img.getRGB(x, y);
	}
	
	//get a color from a depth and time, depending on style
	public static int getColor(int depth, int time, int lastMinDepth, int lastMaxDepth, double rZ, double iZ) {
		int r = 0, g = 0, b = 0;
		boolean background = false;
		if (depth == -2) r = g = b = 255; //error
		if (depth >= 0) {// if it's not in the set
			r = g = b = 255;
			if (type.equals("rawhsv")) {
				double theta = Math.atan2(iZ, rZ)*2;
				double ro = 127 * FastMath.sin(theta + Math.PI * 2 / 3.) + 128;
				double go = 127 * FastMath.sin(theta + Math.PI * 0 / 3.) + 128;
				double bo = 127 * FastMath.sin(theta + Math.PI * 4 / 3.) + 128;

				double d = Math.sqrt(rZ*rZ+iZ*iZ);
				double scaler = d/4;
				r = (int) (ro*scaler);
				g = (int) (go*scaler);
				b = (int) (bo*scaler);
			}
			if (type.equals("sinhsv")) {
				return sinHSV(rZ,iZ);
			}
			if (type.equals("image")) {
				//TODO:Repeats an image, warped by the set
			}
			if (type.equals("longjump")) {
				double d = Math.sqrt(rZ*rZ+iZ*iZ) - 2;
				if(d < 0 || d > 4)System.out.println(d);
				r = (int) (d/4*255);
				g = (int) (d/4*255);
				b = (int) (d/4*255);
			}
			if (type.equals("contrast")) {
				r = g = b = depth%2*255;
			}
			if (type.equals("light")) {
				double num = ((double)depth - lastMinDepth)/(lastMaxDepth - lastMinDepth);
				r = g = b = (int) (Math.sqrt(num)*256);
			}
			if(type.equals("undark")) {
				double shader = Math.exp(-5*(depth-lastMinDepth)/(.01+lastMaxDepth-lastMinDepth));
				r=(int) (255-r*shader);
				g=(int) (255-g*shader);
				b=(int) (255-b*shader);
			}
			if(type.equals("dark")) {
				double shader = Math.exp(-5*(depth-lastMinDepth)/(.01+lastMaxDepth-lastMinDepth));
				r*=shader;
				g*=shader;
				b*=shader;
			}
			if (type.equals("cycle")) {
				double d = depth + inhale * time;
				while(d<0)d+=10;
				int col1 = cycler[(int) ((d/5)%cycleWidth)][0];
				int col2 = cycler[(int) ((d/5+1)%cycleWidth)][0];
				int col = colorLerp(col1,col2,1-(d%5)/5.);
				r = (col&0xff0000)>>16;
				g = (col&0xff00)>>8;
				b = col&0xff;
			}
			if (type.equals("magicpalette")) {
				int col = cycler[depth/5%cycleWidth][depth/5/cycleWidth%cycleHeight];
				background = (col&0xffffff) == 0xabcdef;
				r = (col&0xff0000)>>16;
				g = (col&0xff00)>>8;
				b = col&0xff;
			}
			if (type.equals("oldbow")) {
				r = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 0. / 3)) / 3) + 128);
				g = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 2. / 3)) / 3) + 128);
				b = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 4. / 3)) / 3) + 128);
			}
			if (type.equals("popbow")) {
				r = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (0 + inhale*time) / 3) + 128);
				g = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (1 + inhale*time) / 3) + 128);
				b = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (2 + inhale*time) / 3) + 128);
			}
			if (type.equals("deepbow")) {
				r = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (0 + inhale*time) / 3) + 128);
				g = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (1 + inhale*time) / 3) + 128);
				b = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (4 + inhale*time) / 3) + 128);
			}
			if (type.equals("rainbow") || background) {
				r = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (2 + inhale*time) / 3) + 128);
				g = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (0 + inhale*time) / 3) + 128);
				b = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (4 + inhale*time) / 3) + 128);
			}
		}
		return r*0x10000+g*0x100+b;
	}
	
	//generate a random color cycle to be looped through
	public static void inventCycle() {
		cycleHeight = 1;
		cycleWidth = (int) (Math.random()*3+3);
		cycler = new int[cycleWidth][1];
		for(int i = 0; i < cycleWidth; i++) cycler[i][0] = (int) (Math.random() * 0x1000000);
	}
	
	public static double square(double x) {
		return x*x;
	}
	
	public static int lerpInt(int a, int b, double w){
		return (int) (a*w+b*(w-1));
	}
	
	//linear interpolate between two colors
	public static int colorLerp(int a, int b, double w) {
		int r1 = (a&0xff0000)>>16;
		int g1 = (a&0xff00)>>8;
		int b1 = a&0xff;
		int r2 = (b&0xff0000)>>16;
		int g2 = (b&0xff00)>>8;
		int b2 = b&0xff;
		return (lerpInt(r1,r2,w)<<16)+(lerpInt(g1,g2,w)<<8)+lerpInt(b1,b2,w);
	}
	
	public static void randomize() {
		type = styles[(int)(Math.random()*styles.length)];// + (Math.random()<.5?"dark":"");
		inhale = (Math.random()*.8-.4);
		System.out.println("Style: " + type);
	}

	public static int rect2(Complex c) {
		return (int)((c.x/4+.5)*256) * 0x10000 + (int)((c.y/4+.5)*256) * 0x100;
	}
	
	public static int sinHSV(double rZ, double iZ) {
		double theta = Math.atan2(iZ, rZ)*2;
		double ro = 127 * FastMath.sin(theta + Math.PI * 2 / 3.) + 128;
		double go = 127 * FastMath.sin(theta + Math.PI * 0 / 3.) + 128;
		double bo = 127 * FastMath.sin(theta + Math.PI * 4 / 3.) + 128;

		double d = Math.sqrt(rZ*rZ+iZ*iZ);
		double scaler = Math.sin((d-.5)*Math.PI)/2+.5;
		return (int) (ro*scaler) * 0x10000 + (int) (go*scaler) * 0x100 + (int) (bo*scaler);
	}
}