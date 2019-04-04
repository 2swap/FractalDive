package com.twoswap.mandelbrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.twoswap.mandelbrot.extras.Complex;

public class Generator {

	//dont touch stuff in this block
	public static int minDepth, maxDepth, lastMinDepth = 500000, lastMaxDepth = 0, time = 0, iterations = 0;//some stuff the program keeps track of
	
	//Settings for zooms- change away!
	public static int width = 512, height = 512; //screen size
	public static int frames = 800;//how long the gif should be
	public static boolean record = false; //Whether or not to save it as gif
	
	//generates one frame.
	public static int[] generate() {
		minDepth = 10000000;
		maxDepth = 0; //keep track of our min and max depths reached
		
		//initialize frame
		int[] pix = new int[width * height];
		
		//fill it with -1
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				pix[x+y*width] = -1;
		
		//brute force compute all points (x,y) for which x and y both even
		for (int x = 0; x < width; x+=2) for (int y = 0; y < height; y+=2)
				doPixel(x, y, pix);
		
		//fill in points that can be determined by neighbors
		optimizeFill(pix);
		
		//same as the last loop, but for x,y odd
		for (int x = 1; x < width; x+=2) for (int y = 1; y < height; y+=2)
			if(pix[x+y*width] == -1) doPixel(x, y, pix);
		
		//repeat neighbor fill
		optimizeFill(pix);
		
		//solve all remaining unknown points
		for (int x = 0; x < width; x++) for (int y = 0; y < height; y++)
			if(pix[x+y*width] == -1) doPixel(x, y, pix);
		
		
		
		if(++time%10==0)System.out.println(time + " " + Controller.searchDepth);
		lastMinDepth = minDepth;//update these for controller
		lastMaxDepth = maxDepth;
		if(Math.random() < .001) {
			//Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			savePic(pix, "images/"+System.currentTimeMillis()+".png");//save pictures now and then
		}
//		for(int dx = 0; dx < 2; dx++)for(int dy = 0; dy < 2; dy++) {
//			int xvalhere = width/2+dx+(int)(cpow.x*width/10.), yvalhere = height/2+dy-(int)(cpow.y*height/10.);
//			if(renderCPoint && xvalhere > 0 && xvalhere < width && yvalhere > 0 && yvalhere < height) pix[xvalhere+(yvalhere)*width] = 0xffffff;//center pixel white
//			pix[width/2+dx+(height/2+dy)*width] = 0xffffff;//center pixel white
//		}
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

	//the actual mandelbrot computation
	public static int computeDepth(double rC, double iC, double rZ, double iZ, double rX, double iX, int its, double[] outCoords) {
		double editI = iZ, editR = rZ, ei2 = editI * editI, er2 = editR * editR; //some intermediate temp values
		boolean diverged = false; // whether or not we have exited the radius 2 origin circle yet
		int divergeCount = 0; // iteration counter

		while (!diverged) {
			if (ei2+er2 > 4){diverged = true;break;} //out of circle
			divergeCount++;
			double newR, newI;
			if (iX != 0){
				Complex c = new Complex(editR, editI).pow(new Complex(rX,iX)); //arbitrary complex power
				newR = c.x;
				newI = c.y;
			} else if (rX == 2) {
				newR = er2 - ei2;
				newI = editI * editR; //square the complex
				newI += newI;
			} else if (rX == -1) {
				double div = 1 / (er2+ei2);
				newR = editR*div; //reciprocal it
				newI = -editI*div;
			} else if (rX == 3) {
				newR = editR * er2 - 3 * editR * ei2;
				newI = (er2 + er2 + er2 - ei2) * editI; //cube it
			} else {
				Complex c = new Complex(editR, editI).pow(new Complex(rX,iX)); //arbitrary real power
				newR = c.x;
				newI = c.y;
			}
			editI = newI + iC; //add c
			editR = newR + rC;
			ei2 = editI * editI; //update squares
			er2 = editR * editR;
			if (divergeCount > iterations) break;
		}
		if(!diverged) divergeCount = 0;
		//if (diverged && divergeCount >= c.searchDepth*.999)c.searchDepth++;
		if (diverged) minDepth = Math.min(minDepth, divergeCount);
		if (diverged) maxDepth = Math.max(maxDepth, divergeCount);
		outCoords[0] = editR-rC;
		outCoords[1] = editI-iC;
		return diverged?divergeCount:-1;
	}

	private static boolean doPixel(int x, int y, int[] pix) {
		double rotX = (x - width / 2) * Math.cos(Controller.angle) - (y - height / 2) * Math.sin(Controller.angle);
		double rotY = (x - width / 2) * Math.sin(Controller.angle) + (y - height / 2) * Math.cos(Controller.angle);
		double rPart = rotX / (Controller.zoom) + Controller.x;
		double iPart = rotY / (Controller.zoom) + Controller.y;// width, zoom is in terms of width. Stretched otherwise.
		
		//this is for last minute changes to pixel coords.
//	double pointAng = Math.atan2(rPart, iPart);
//	double pointDist = 1/Math.sqrt(rPart*rPart+iPart*iPart);//try changing this to 1/sqrt!
//	rPart = pointDist * Math.sin(pointAng);
//	iPart = pointDist * Math.cos(pointAng);
		
		double outCoords[] = new double[2];
		int s = Controller.set;
		int depth = computeDepth(s==0?rPart:Controller.rC, s==0?iPart:Controller.iC, s==1?rPart:Controller.rZ, s==1?iPart:Controller.iZ, s==2?rPart:Controller.rX, s==2?iPart:Controller.iX, Controller.searchDepth*iterations, outCoords);
		pix[x + y * width] = Styler.getColor(depth, time, lastMinDepth, lastMaxDepth, outCoords[0], outCoords[1]);
		return depth == -1;
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
		Styler.initPalette(new File("Harrison.png"));
		Controller.x = .25009989470767;
		Controller.y = 0.00000159156228;
		Controller.zoomSpeed = 1.03;
		Controller.zoom = 10000000000000d;
		Controller.searchDepth = 40000;
		Controller.va = 0.003;
		frames = 61;
	}

	public static BufferedImage getImageFromArray(int[] pixels) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, width, height, pixels, 0, width);
		return image;
	}
}