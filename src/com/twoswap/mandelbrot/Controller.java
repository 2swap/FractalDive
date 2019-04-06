package com.twoswap.mandelbrot;

//Controls where the generator looks for
public class Controller {
	
	public static double x = 0, y = 0, zoom = 50, angle = 0;//coordinates and zoom level
	public static double speed = 10, zoomSpeed = 1, va = 0;//speed of camera convergence to target, speed of zoom
	public static int searchDepth = 50;
	public static boolean s1 = true, s2 = false, s3 = false;
	public static double rC = 0, iC = 0, rZ = 0, iZ = 0, rX = 2, iX = 0;
	public static double iDrift = 0, rDrift = 0;
	public static boolean inversion = false;
	
	//Known interesting spots
	//fx = -0.750045367143, fy = -0.004786271734;
	//fx = -0.7925403632943916, fy = 0.16076317715583555
	//fx = 0.6569488539350342, fy = -0.41280661014731673
	//Final x: -0.6612648356278878 Final y: 0.4151437869617779
	//fx = -1.315180982097868, fy = 0.073481649996795
	
	//called after every tick
	public static void zoom(int[] last, int w, int h, int t) {
		//double q = 4*(t/200.-.5);
		//rC = .25 * Math.cos(q) - 1;
		//iC = .25 * Math.sin(q); // left cir
        //double tx = .25 * Math.cos(runs / 512.0) - 1, ty = .25 * Math.sin(runs / 512.0); // left cir
        //double tx = .1 * Math.cos(runs / 10.0)-.125, ty = .1 * Math.sin(runs / 10.0) - .75; // top cir
        //double tx = .75 * Math.cos(runs / 10.0), ty = .75 * Math.sin(runs / 10.0); // main bulb
		//r0 = q*Math.cos(q);
		//i0 = q*Math.sin(q);//spiral
		//r0 = Math.cos(q);
		//i0 = Math.sin(q);//circle
		//r0 = q;
		//i0 = 0;
		angle += va;//rotate appropriately
		zoom *= zoomSpeed;//zoom in
		
		if(speed!=0) {
			//change coords towards a more interesting spot
			double avgx = 0, avgy = 0;
			int pixelsChecked = 0;
			for(int px = w/3; px < 2*w/3; px++) for(int py = h/3; py < 2*h/3; py++) { //iterate over the middle of the screen (not whole screen, that moves too fast)
				double sum = -6+Math.sin(t/80.)*1.5; //Set weight for what we're looking for
				for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++)//iterate over immediate vicinity
					if( !(dx == 0 && dy == 0) && colDist(last[px+py*w],last[px+dx+(py+dy)*w])) sum++;//if x+dx,y+dy =/= x,y then increment sum
				double rotX = (px - w / 2.) * Math.cos(angle) - (py - h / 2.) * Math.sin(angle);//un-rotate to figure out the actual coordinates in mandelbrot-space, not in screen-space
				double rotY = (px - w / 2.) * Math.sin(angle) + (py - h / 2.) * Math.cos(angle);
				//double dist = 1+Math.sqrt(square(px-w/2.)+square(py-h/2.));
				avgx += sum * Math.cbrt(rotX);//tweak our velocity based on interestingness of (x,y)
				avgy += sum * Math.cbrt(rotY);
				pixelsChecked++;
			}
			avgx /= pixelsChecked;//shouldn't be screensize dependent
			avgy /= pixelsChecked;
			rDrift = rDrift/2+speed*avgx/zoom;//move appropriately
			iDrift = iDrift/2+speed*avgy/zoom;
			x+=rDrift;
			y+=iDrift;
		}
	}
	
	public static double square(double x) {
		return x*x;
	}
	
	public static boolean colDist(int c1, int c2){
		int r1, g1, b1, r2, g2, b2;
		r1 = c1 / 0x10000 % 0x100;
		g1 = c1 / 0x100   % 0x100;
		b1 = c1 / 0x1     % 0x100;
		r2 = c2 / 0x10000 % 0x100;
		g2 = c2 / 0x100   % 0x100;
		b2 = c2 / 0x1     % 0x100;
		return (Math.abs(r1-r2)+Math.abs(b1-b2)+Math.abs(g1-g2)) > 10;
	}
	
	//linear interpolation
	public static double lerp(double a, double b, double w) {
		return a*w+b*(1-w);
	}
	
	//move to a random point on the unit circle
	public static void randomize() {
		zoom = Generator.width/8.;
		//va = Math.random()*.04-.02;
		double theta = 2*Math.PI*Math.random();
		x = Math.cos(theta)*1.2;
		y = Math.sin(theta)*1.2;
	}
}
