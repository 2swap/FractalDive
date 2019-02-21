package com.Marduk.mandelbrot;

//Controls where the generator looks for
public class Controller {
	
	double x, y, zoom = 100;//coordinates and zoom level
	public static boolean drift; //looks for interesting spots
	public static double speed = 1, zoomSpeed = 1.03;//speed of camera convergence to target, speed of zoom
	
	//Known interesting spots
	//fx = -0.750045367143, fy = -0.004786271734;
	//fx = -0.7925403632943916, fy = 0.16076317715583555
	//fx = 0.6569488539350342, fy = -0.41280661014731673
	//Final x: -0.6612648356278878 Final y: 0.4151437869617779
	//fx = -1.315180982097868, fy = 0.073481649996795
	
	public Controller() {
		randomize();
		drift = true;
	}
	
	public Controller(double x, double y) {
		this.x = x;
		this.y = y;
		drift = false;
	}
	
	public void zoom(int[] last, int w, int h, int t) {
		zoom *= zoomSpeed;
		if(drift) {
			double avgx = 0, avgy = 0;
			for(int x = 0; x < w; x++) for(int y = 0; y < h; y++) {
				double sum = -5.5+Math.sin(t/80.)*2;
				if(x != 0 && y != 0 && x != w-1 && y != h-1)
					for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++)
						if( !(x == 0 && y == 0) && last[x+y*w]!=last[x+dx+(y+dy)*w]) sum++;
				avgx += square(sum) * Math.cbrt(x-w/2);
				avgy += square(sum) * Math.cbrt(y-h/2);
			}
			avgx /= w*h;
			avgy /= w*h;
			x += speed*lerp(-1/zoom,1/zoom,avgx);
			y += speed*lerp(-1/zoom,1/zoom,avgy);
		}
	}
	
	public static double square(double x) {
		return x*x;
	}
	
	public static double lerp(double a, double b, double w) {
		return a*w+b*(1-w);
	}
	
	public void randomize() {
		double theta = 2*Math.PI*Math.random();
		x = Math.cos(theta);
		y = Math.sin(theta);
	}
}
