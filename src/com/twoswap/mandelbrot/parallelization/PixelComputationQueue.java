package com.twoswap.mandelbrot.parallelization;

import java.util.concurrent.LinkedBlockingDeque;

import com.twoswap.mandelbrot.Generator;

public class PixelComputationQueue {
	public static LinkedBlockingDeque<Computation> queue;
	//private int threadcount;
	public static boolean run;
	public PixelComputationQueue(int threadcount) {
		//this.threadcount = threadcount;
		queue = new LinkedBlockingDeque<Computation>();
		run = false;
		for(int i = 0; i < threadcount; i++) {
			Thread thread = new Thread(){
				public void run(){
					while(true) {
						try {
							Computation c = PixelComputationQueue.queue.take();
							Generator.doPixel(c.x, c.y, Generator.pix);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			};
			thread.start();
		}
	}

	public void addToQueue(int x, int y) {
		queue.add(new Computation(x,y));
	}

	public void compute() {
		run = true;
		while(!queue.isEmpty()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		run = false;
	}
}