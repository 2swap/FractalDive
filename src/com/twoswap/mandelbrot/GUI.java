package com.twoswap.mandelbrot;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;	
public class GUI{
	public GUI(){
		Frame f= new Frame("Canvas Example");
		f.setLayout(null);
		f.setSize(50,50);//to set insets
		f.setVisible(true);
		f.setSize(f.getInsets().left+f.getInsets().right+1200, f.getInsets().top+f.getInsets().bottom+800);

		Panel leftPanel = new Panel();
		leftPanel.setBackground(Color.GRAY);
		leftPanel.setLayout(null);
		leftPanel.setBounds(f.getInsets().left+16, f.getInsets().top+16, 800-32, 800-32);
		
		DimPanel[] dimensions = new DimPanel[3];
		
		for(int i = 0; i < 3; i++) {
			dimensions[i] = new DimPanel(800+f.getInsets().left+16, f.getInsets().top+16+200*i, 400-32, 200-32);
			f.add(dimensions[i]);
		}
		
		f.add(leftPanel);
		
		Button b = new Button("click me");
		b.setBounds(530,100,80,30); // setting button position
		f.add(b); // adding button into frame
		
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
	}
	public static void main(String args[]){
		new GUI();
	}
}
class DimPanel extends Panel {
	DimCanvas c;
	public DimPanel(int x, int y, int w, int h) {
		setBackground(Color.GRAY);
		setLayout(null);
		setBounds(x,y,w,h);
		
		c = new DimCanvas();
		c.setBackground(Color.WHITE);
		c.setBounds(16,16,128,128);
	}
	public void paint(Graphics g){
		g.setColor(Color.red);
		g.fillOval(75, 75, 150, 75);
	}
}
class DimCanvas extends Canvas{
	int plane = 0;
	public DimCanvas() {
		setBackground(Color.WHITE);
		setBounds(16,16,128,128);
		plane = type;
	}
	public void paint(Graphics g){
		g.setColor(Color.red);
		g.fillOval(75, 75, 150, 75);
	}
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		int[] pix = Generator.generate();
		for (int i = 0; i < Generator.width * Generator.height; i++)
			pixels[i] = pix[i];
		if (Generator.record && Generator.time > 1)
			Generator.savePic(pix, "giffer/img" + (Generator.time - 2) + ".png");
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, Generator.width + 10, Generator.height + 10, null);
		g.dispose();
		bs.show();
	}
}