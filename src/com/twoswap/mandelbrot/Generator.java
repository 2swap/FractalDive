package com.twoswap.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.twoswap.gui.GUI;
import com.twoswap.mandelbrot.extras.Complex;
import com.twoswap.mandelbrot.parallelization.PixelComputationQueue;

import net.jafama.FastMath;

public class Generator {

	//dont touch stuff in this block
	public static int minDepth, maxDepth, lastMinDepth = 500000, lastMaxDepth = 0, time = 0, iterations = 0;//some stuff the program keeps track of
	
	//Settings for zooms- change away!
	public static int width = 1280, height = 720; //screen size
	public static boolean record = false; //Whether or not to save it as gif
	public static double imageRate = .0001;
	
	//initialize frame
	public static int[] pix = new int[width * height];
	static PixelComputationQueue queue = new PixelComputationQueue(4);

	//generates one frame.
	public static int[] generate() {
		
		//A quirk of the mandelbrot set, NOT a bug. Result of negative or imaginary exponents when piping pixel values into C but not Z (and not X)
		if(Controller.s1 && !Controller.s2 && !Controller.s3 && (Controller.rX < 0 || Controller.iX != 0)) GUI.clog("Unexpected black screen? You probably mean to check the Z button.");
		
		minDepth = 10000000;
		maxDepth = 0; //keep track of our min and max depths reached
		
		//fill it with -1
		Arrays.fill(pix, -1);
		
		if(!Styler.inside) {
			//brute force compute all points (x,y) for which x and y both even
			for (int x = 0; x < width; x+=2) for (int y = 0; y < height; y+=2)
				queue.addToQueue(x, y);
			queue.compute();
			//fill in points that can be determined by neighbors
			optimizeFill(pix);
				
			//same as the last loop, but for x,y odd
			for (int x = 1; x < width; x+=2) for (int y = 1; y < height; y+=2)
				if(pix[x+y*width] == -1) queue.addToQueue(x, y);
			queue.compute();
			//repeat neighbor fill
			optimizeFill(pix);
			
			//solve all remaining unknown points
			for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
				if(pix[x+y*width] == -1) queue.addToQueue(x, y);
			queue.compute();
		} else {
			for (int x = 0; x < width; x++) for (int y = 0; y < height; y++) doPixel(x,y);
		}
		
		
		if(++time%10==0)System.out.println(time + " " + Controller.searchDepth);
		lastMinDepth = minDepth;//update these for controller
		lastMaxDepth = maxDepth;
		
		if(Styler.inside) for (int x = 0; x < width; x++) for (int y = 0; y < height; y++) pix[x+y*width] = (int) (16*Math.log(pix[x+y*width]));
		
		if(FastMath.random() < imageRate) {
			//Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			savePic(pix, "images/"+System.currentTimeMillis()+".png");//save pictures now and then
		}
		
		Controller.zoom(pix, width, height, time);//tick controller
		return pix;
	}
	
	//fill in pixels which can be determined by neighbors' values
	private static void optimizeFill(int[] pix) {
		int edits = 1;//keep an eye on how many pixels we change
		while(edits > 0) {//if we changed nothing last iteration, terminate
			edits = 0;//start new iteration
			
			for (int x = 1; x < width-1; x++) for (int y = 1; y < height-1; y++) {//iterate over screen
				int here = pix[x+y*width];
				if(here != -1) continue;//already solved

				
				/* if the s's are all the same, this sets vs to s.
				 * svs
				 * vvv
				 * svs
				 */
				int tl = pix[x-1+(y-1)*width];
				if(tl!=-1 && tl==pix[x+1+(y-1)*width] && tl==pix[x+1+(y+1)*width] && tl==pix[x-1+(y+1)*width]) {
					pix[x+y*width] = pix[x+1+y*width] = pix[x+(y+1)*width] = pix[x-1+y*width] = pix[x+(y-1)*width] = tl;
					edits++;
					continue;
				}
				
				
				/* if the s's are all the same, this sets v to s.
				 * *s*
				 * svs
				 * *s*
				 */
				int top = pix[x+(y-1)*width];
				if(top!=-1 && top==pix[x-1+y*width] && top==pix[x+(y+1)*width] && top==pix[x+1+y*width]) {
					pix[x+y*width] = top;
					edits++;
					continue;
				}
			}
		}
	}

	public static Complex planeIteration(double rC, double iC, double rZ, double iZ, double rX, double iX, int d, boolean draw) {
		Complex c = new Complex(rZ, iZ), lc = c; //TODO use pow(iX and rX), but there's a bug i think
		for(int i = 0; i < d; i++) {
			c = c.multiply(c).add(new Complex(rC,iC));
			if(draw) {
				if(GUI.stylerPanel.point.isSelected())GUI.drawPoint(c.x,c.y,0xff0000,1);
				if(lc.x != 0 && GUI.stylerPanel.line.isSelected()) GUI.drawLine(lc,c,0xffff00);
				lc = c;
			}
			if(Controller.burningShip) {
				c.x = Math.abs(c.x);
				c.y = Math.abs(c.y);
			}
		}
		return c;
	}
	
	//the actual mandelbrot computation
	//TODO: Parallelize
	public static int computeDepth(double rC, double iC, double rZ, double iZ, double rX, double iX, double[] outCoords) {
		double editI = iZ, editR = rZ, ei2 = editI * editI, er2 = editR * editR; //some intermediate temp values
		boolean diverged = false; // whether or not we have exited the radius 2 origin circle yet
		int divergeCount = 0; // iteration counter

		while (!diverged) {
			int scrX = (int) ((editR-Controller.x)*Controller.zoom+width/2.);
			int scrY = (int) ((editI-Controller.y)*Controller.zoom+height/2.);
			if(Styler.inside && scrX >=0 && scrX < width && scrY >=0 && scrY < height) pix[scrX+scrY*width]+=1;
			if (ei2+er2 > 10000){diverged = true;break;} //out of arbitrarily large circle
			divergeCount++;
			double newR, newI;
			
			if (iX != 0) { //arbitrary complex power
				Complex c = new Complex(editR, editI);
				c = c.pow(new Complex(rX,iX));
				newR = c.x;
				newI = c.y;
			}
			
			else if (rX == 2) { //square the complex
				newR = er2 - ei2;
				newI = editI * editR;
				newI += newI;
			}
			
			else if (rX == -1) { //reciprocal it
				double div = 1 / (er2+ei2);
				newR = editR*div;
				newI = -editI*div;
			}
			
			else if (rX == 3) { //cube it
				newR = editR * er2 - 3 * editR * ei2;
				newI = (er2 + er2 + er2 - ei2) * editI;
			}
			
			else if (rX == (int)rX && rX >= 0) { //Non-negative integer powers
				Complex c = new Complex(1,0);
				Complex z = new Complex(editR,editI);
				for(int i = 0; i < rX; i++)
					c = c.multiply(z);
				newR = c.x;
				newI = c.y;
			}
			
			else if (rX == (int)rX) { //Negative integer powers
				double div = 1 / (er2+ei2);
				editR = editR*div;
				editI = -editI*div;
				Complex c = new Complex(1,0);
				Complex z = new Complex(editR,editI);
				for(int i = 0; i > rX; i--)
					c = c.multiply(z);
				newR = c.x;
				newI = c.y;
			}
			
			else { //real power
				Complex c = new Complex(editR, editI);
				c = c.pow(rX);
				newR = c.x;
				newI = c.y;
			}
			
			editI = newI + iC; //add c
			editR = newR + rC;
			if(Controller.burningShip) {
				editI = FastMath.abs(editI);
				editR = FastMath.abs(editR);
			}
			ei2 = editI * editI; //update squares
			er2 = editR * editR;
			if (divergeCount > iterations) break;
		}
		if(!diverged) divergeCount = 0;
		//if (diverged && divergeCount >= c.searchDepth*.999)c.searchDepth++;
		if (diverged) minDepth = FastMath.min(minDepth, divergeCount);
		if (diverged) maxDepth = FastMath.max(maxDepth, divergeCount);
		outCoords[0] = editR;
		outCoords[1] = editI;
		return diverged?divergeCount:-1;
	}

	public static void doPixel(int x, int y) {
		pix[x+y*width] = 0;
		
		double rotX = (x - width / 2) * FastMath.cos(Controller.angle) - (y - height / 2) * FastMath.sin(Controller.angle);
		double rotY = (x - width / 2) * FastMath.sin(Controller.angle) + (y - height / 2) * FastMath.cos(Controller.angle);
		double rPart = rotX / Controller.zoom + Controller.x;
		double iPart = rotY / Controller.zoom + Controller.y;// width, zoom is in terms of width. Stretched otherwise.
		
		//this is for last minute changes to pixel coords.
		if(Controller.inversion) {
			double pointAng = FastMath.atan2(rPart, iPart);
			double pointDist = 1/FastMath.sqrt(rPart*rPart+iPart*iPart);//try changing this to 1/sqrt!
			rPart = pointDist * FastMath.sin(pointAng);
			iPart = pointDist * FastMath.cos(pointAng);
		}

		boolean s1 = Controller.s1, s2 = Controller.s2, s3 = Controller.s3;
		
		int depthHere = 0;
		if(Styler.iterationCount || Styler.inside) {
			double outCoords[] = new double[2];
			double cr0 = s1?rPart:Controller.rC, ci0 = s1?iPart:Controller.iC;
			depthHere = computeDepth(cr0, ci0, s2?rPart:Controller.rZ, s2?iPart:Controller.iZ, s3?rPart:Controller.rX, s3?iPart:Controller.iX, outCoords);
			if(!Styler.inside) pix[x + y * width] = depthHere == -1 ? Styler.inside(cr0,outCoords[0],ci0,outCoords[1]) : Styler.getColor(depthHere, time, lastMinDepth, lastMaxDepth, outCoords[0], outCoords[1]);
		} else {
			Complex c = planeIteration(s1?rPart:Controller.rC, s1?iPart:Controller.iC, s2?rPart:Controller.rZ, s2?iPart:Controller.iZ, s3?rPart:Controller.rX, s3?iPart:Controller.iX, iterations, false);
			pix[x+y*width] = Styler.sinHSV(c.x,c.y);
		}
	}

	//saves pix as an image
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
		Styler.initPalette(new File("2swap.png"));
		Controller.x = .25009989470767;
		Controller.y = 0.00000159156228;
		Controller.zoomSpeed = 1.03;
		Controller.zoom = 10000000000000d;
		Controller.searchDepth = 40000;
		Controller.va = 0.003;
		//frames = 61;
	}

	public static BufferedImage getImageFromArray(int[] pixels) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, width, height, pixels, 0, width);
		return image;
	}
}