package com.Marduk.mandelbrot;

import com.Marduk.mandelbrot.extras.Complex;

//Controls where the generator looks for
public class Controller {
	
	double x, y, zoom = 50, angle = 0;//coordinates and zoom level
	public boolean drift; //looks for interesting spots
	public double speed = 1, zoomSpeed = 1.05, va = 0;//speed of camera convergence to target, speed of zoom
	public int searchDepth = 50;
	
	//Known interesting spots
	//fx = -0.750045367143, fy = -0.004786271734;
	//fx = -0.7925403632943916, fy = 0.16076317715583555
	//fx = 0.6569488539350342, fy = -0.41280661014731673
	//Final x: -0.6612648356278878 Final y: 0.4151437869617779
	//fx = -1.315180982097868, fy = 0.073481649996795
	
	//initialize controller with no starting position, pick a random one and drift to make an interesting zoom
	public Controller() {
		randomize();
		drift = true;
	}
	
	//initialize at a set zoom location
	public Controller(double x, double y, double zoomSpeed) {
		this.zoomSpeed = zoomSpeed;
		this.x = x;
		this.y = y;
		drift = false;
	}
	
	//same as above, but initializes zoom too
	public Controller(double x, double y, double zoomSpeed, double z) {
		this.zoomSpeed = zoomSpeed;
		this.x = x;
		this.y = y;
		zoom = z;
		drift = false;
	}
	
	//called after every tick
	public void zoom(int[] last, int w, int h, int t) {
		//double q = t*.03;
		//Generator.cpow = new Complex(q,q/5.*Math.sin(q*5));
		//System.out.println(Generator.cpow);
		angle += va;//rotate appropriately
		zoom *= zoomSpeed;//zoom in
		
		if(drift) {
			//change coords towards a more interesting spot
			double avgx = 0, avgy = 0;
			for(int x = 1*w/8; x <= 7*w/8; x++) for(int y = 1*h/8; y <= 7*h/8; y++) { //iterate over the middle of the screen (not whole screen, that moves too fast)
				double sum = -5.5+Math.sin(t/80.)*2; //Too confusing for a one-line comment, ask alex if you wanna know what this does
				for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++)//iterate over immediate vicinity
					if( !(x == 0 && y == 0) && last[x+y*w]!=last[x+dx+(y+dy)*w]) sum++;//if x+dx,y+dy =/= x,y then increment sum
				double rotX = (x - w / 2) * Math.cos(angle) - (y - h / 2) * Math.sin(angle);//un-rotate to figure out the actual coordinates in mandelbrot-space, not in screen-space
				double rotY = (x - w / 2) * Math.sin(angle) + (y - h / 2) * Math.cos(angle);
				double dist = 1+Math.sqrt(square(x-w/2)+square(y-h/2));
				avgx += square(sum) * Math.cbrt(rotX) * 8 / dist;//tweak our velocity based on interestingness of (x,y)
				avgy += square(sum) * Math.cbrt(rotY) * 8 / dist;
			}
			avgx /= w*h;//shouldn't be screensize dependent
			avgy /= w*h;
			x += speed*lerp(-1/zoom,1/zoom,avgx);//move appropriately
			y += speed*lerp(-1/zoom,1/zoom,avgy);
		}
	}
	
	public static double square(double x) {
		return x*x;
	}
	
	//linear interpolation
	public static double lerp(double a, double b, double w) {
		return a*w+b*(1-w);
	}
	
	//move to a random point on the unit circle
	public void randomize() {
		zoom = 50;
		va = Math.random()*.04-.02;
		double theta = 2*Math.PI*Math.random();
		x = Math.cos(theta);
		y = Math.sin(theta);
	}
}
