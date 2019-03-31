package com.twoswap.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.jafama.FastMath;

public class Styler {

	public double inhale = 0;//how fast the colors should go in(-) or out(+)
	public String type = "rainbow";//the way it looks

	//for image palettes
	public int cycleWidth = 1, cycleHeight = 1;
	public int[][] cycler;
	
	public static String[] styles = {"rainbow", "deepbow", "cycle"};//, "light"};//a list of styles for random selection

	public Styler(String type, double inhale) {
		this.inhale = inhale;
		this.type = type;
		inventCycle();
	}
	
	public Styler() {
		randomize();
		inventCycle();
	}
	
	//initialize with a magic palette image
	public Styler(File f) {
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
	public int getColor(int depth, int time, int lastMinDepth, int lastMaxDepth) {
		int r = 0, g = 0, b = 0;
		boolean background = false;
		if (depth == -2) r = g = b = 255; //error
		if (depth >= 0) {// if it's not in the set
			if (type.contains("inset")) {
				r = 255;
				g = 255;
				b = 255;
			}
			if (type.contains("contrast")) {
				r = depth%2*255;
				g = depth%2*255;
				b = depth%2*255;
			}
			if (type.contains("light")) {
				r = (int) (square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, 0)) / Math.PI * 512)))/256.);
				g = (int) (square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, 0)) / Math.PI * 512)))/256.);
				b = (int) (square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, 0)) / Math.PI * 512)))/256.);
			}
			if (type.contains("deepbow")) {
				r = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time)) / 3) + 128);
				g = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 2 / 3)) / 3) + 128);
				b = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 4 / 3)) / 3) + 128);
			}
			if (type.contains("cycle")) {
				double d = depth + inhale * time;
				while(d<0)d+=10;
				int col1 = cycler[(int) ((d/5)%cycleWidth)][0];
				int col2 = cycler[(int) ((d/5+1)%cycleWidth)][0];
				int col = colorLerp(col1,col2,1-(d%5)/5.);
				r = (col&0xff0000)>>16;
				g = (col&0xff00)>>8;
				b = col&0xff;
			}
			if (type.contains("magicpalette")) {
				int col = cycler[depth/5%cycleWidth][depth/5/cycleWidth%cycleHeight];
				background = (col&0xffffff) == 0xabcdef;
				r = (col&0xff0000)>>16;
				g = (col&0xff00)>>8;
				b = col&0xff;
			}
			if (type.contains("rainbow") || background) {
				r = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (2 + inhale*time) / 3) + 128);
				g = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (0 + inhale*time) / 3) + 128);
				b = (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (4 + inhale*time) / 3) + 128);
			}
			if(type.contains("dark")) {
				double shader = Math.exp(-5*(depth-lastMinDepth)/(.01+lastMaxDepth-lastMinDepth));
				r*=shader;
				g*=shader;
				b*=shader;
			}
		}
		return r*0x10000+g*0x100+b;
	}
	
	//generate a random color cycle to be looped through
	public void inventCycle() {
		cycleHeight = 1;
		cycleWidth = (int) (Math.random()*3+3);
		cycler = new int[cycleWidth][1];
		for(int i = 0; i < cycleWidth; i++) cycler[i][0] = (int) (Math.random() * 0x1000000);
	}
	
	public static double square(double x) {
		return x*x;
	}
	
	//linear interpolate between two colors
	public static int colorLerp(int a, int b, double w) {
		int r1 = (a&0xff0000)>>16;
		int g1 = (a&0xff00)>>8;
		int b1 = a&0xff;
		int r2 = (b&0xff0000)>>16;
		int g2 = (b&0xff00)>>8;
		int b2 = b&0xff;
		return ((int)(r1*w+r2*(1-w))<<16)+((int)(g1*w+g2*(1-w))<<8)+(int)(b1*w+b2*(1-w));
	}
	
	public void randomize() {
		type = styles[(int)(Math.random()*styles.length)];// + (Math.random()<.5?"dark":"");
		inhale = (Math.random()*.8-.4);
		System.out.println("Style: " + type);
	}
}