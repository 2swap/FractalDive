package com.twoswap.gui;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Generator;	
public class GUI{
	
	public static int margins = 8, dimCanvasWidth = 64;
	private static DimPanel[] dimensions = new DimPanel[3];
	public static JSlider its;
	public static JSlider zoomSpeed;
	
	static Canvas mainCanvas = new Canvas();
	private static BufferedImage img;
	private static int[] pixels;
	
	public GUI(){

		img = new BufferedImage(Generator.width, Generator.height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		
		//frame
		Frame f= new Frame("Fractal Dive");
		f.setLayout(null);
		f.setSize(50,50);//to set insets
		f.setVisible(true);
		f.setSize(f.getInsets().left+f.getInsets().right+1200, f.getInsets().top+f.getInsets().bottom+800);
		//f.setResizable(false);
		
		
		//right panel
		for(int i = 0; i < 3; i++) {
			dimensions[i] = new DimPanel(800+f.getInsets().left, f.getInsets().top+margins+200*i, 400, 200-margins, i);
			f.add(dimensions[i]);
		}


		//left panel
		Panel leftPanel = new Panel();
		leftPanel.setBackground(Color.GRAY);
		leftPanel.setLayout(null);
		leftPanel.setBounds(f.getInsets().left+margins, f.getInsets().top+margins, 800-2*margins, 800-2*margins);


		its = new JSlider(0,100,20);
		its.setBounds(margins,Generator.height+margins*2,256,32);
		// Set the labels to be painted on the slider
		its.setPaintLabels(true);
		// Add positions label in the slider
		Hashtable<Integer, JLabel> itsPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=20) itsPosition.put(i, new JLabel(Math.round(Math.exp(Math.sqrt(i)))+""));
		// Set the label to be drawn
		its.setLabelTable(itsPosition); 
		leftPanel.add(its);

		zoomSpeed = new JSlider(0,100,50);
		zoomSpeed.setBounds(margins,Generator.height+margins*3+32,256,32);
		// Set the labels to be painted on the slider
		zoomSpeed.setPaintLabels(true);
		// Add positions label in the slider
		Hashtable<Integer, JLabel> zoomSpeedPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=20) zoomSpeedPosition.put(i, new JLabel(Math.round(Math.pow(1.2,i/50.-1)*100)/100.+""));
		// Set the label to be drawn
		zoomSpeed.setLabelTable(zoomSpeedPosition); 
		leftPanel.add(zoomSpeed);
		
		
		mainCanvas.setBounds(margins,margins,Generator.width,Generator.height);
		leftPanel.add(mainCanvas);
		
		f.add(leftPanel);
		
		//Button b = new Button("click me");
		//b.setBounds(530,100,80,30); // setting button position
		//f.add(b); // adding button into frame
		
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
	}
	public static void render() {

		Generator.iterations = (int) Math.round(Math.exp(Math.sqrt(its.getValue())));

		Generator.c.zoomSpeed = Math.pow(1.2,zoomSpeed.getValue()/50.-1);
		
		BufferStrategy bs = mainCanvas.getBufferStrategy();
		if (bs == null) {
			mainCanvas.createBufferStrategy(3);
			return;
		}
		int[] pix = Generator.generate();
		for (int i = 0; i < Generator.width * Generator.height; i++)
			pixels[i] = pix[i];
		
		if (Generator.record && Generator.time > 1)
			Generator.savePic(pix, "giffer/img" + (Generator.time - 2) + ".png");
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, Generator.width, Generator.height, null);
		g.dispose();
		bs.show();
		for(DimPanel d : dimensions) d.render();
	}
}