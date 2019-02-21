package com.Marduk.mandelbrot;

import net.jafama.FastMath;

public class Styler {

	public double inhale = -.2;//how fast the colors should go in(-) or out(+)
	public String type = "rainbow";//the way it looks
	public static String[] styles = {"rainbow", "deepbow", "randomcycle", "purplegreen", "light"};//a list of styles for random selection
	public static double rr = Math.random() * 6, rg = Math.random() * 6, rb = Math.random() * 6;//used for the random-y styles

	public Styler(String type, double inhale) {
		this.inhale = inhale;
		this.type = type;
	}
	
	public Styler() {
		randomize();
	}
	
	public int getColor(int depth, int time, int lastMinDepth, int lastMaxDepth) {
		int r = 0, g = 0, b = 0;
		if (depth == -2) r = g = b = 255; //error
		if (depth >= 0) {// if it's not in the set
			double effects = 0;
			if (type.contains("inset")) {
				r+=255;
				g+=255;
				b+=255;
				effects++;
			}
			if (type.contains("light")) {
				r += (int) square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, rr)) / Math.PI * 512)))/256.;
				g += (int) square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, rg)) / Math.PI * 512)))/256.;
				b += (int) square(((FastMath.atan((depth - lastMinDepth + 1) / FastMath.pow(2, rb)) / Math.PI * 512)))/256.;
				effects++;
			}
			if (type.contains("rainbow")) {
				r += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (2 + inhale*time) / 3) + 128);
				g += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (0 + inhale*time) / 3) + 128);
				b += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (4 + inhale*time) / 3) + 128);
				effects++;
			}
			if (type.contains("deepbow")) {
				r += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time)) / 3) + 128);
				g += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 2 / 3)) / 3) + 128);
				b += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (3 + 1.5 * FastMath.sin(inhale*time + Math.PI * 4 / 3)) / 3) + 128);
				effects++;
			}
			if (type.contains("randomcycle")) {
				r += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (rr + inhale*time) / 3) + 128);
				g += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (rg + inhale*time) / 3) + 128);
				b += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (rb + inhale*time) / 3) + 128);
				effects++;
			}
			if (type.contains("purplegreen")) {
				r += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (2 + inhale*time) / 10.) + 128);
				g += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (0 + inhale*time) / 10.) + 128);
				b += (int) (127 * FastMath.sin(depth / 10. * Math.PI + Math.PI * (4 + inhale*time) / 10.) + 128);
				effects++;
			}
			r/=effects;
			g/=effects;
			b/=effects;
			if(type.contains("dark")) {
				double shader = 1/Math.exp(2*(depth-lastMinDepth)/(.01+lastMaxDepth-lastMinDepth));
				r*=shader;
				g*=shader;
				b*=shader;
			}
		}
		return r*0x10000+g*0x100+b;
	}
	
	public static double square(double x) {
		return x*x;
	}
	
	public void randomize() {
		type = styles[(int)(Math.random()*styles.length)] + (Math.random()<.1?"dark":"");
		inhale = (Math.random()*.8-.4);
		System.out.println("Style: " + type);
	}
}