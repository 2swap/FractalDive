package com.twoswap.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import com.twoswap.mandelbrot.Controller;
import com.twoswap.mandelbrot.Generator;	
public class GUI{
	
	public static int margins = 4, dimCanvasWidth = 128;
	private static DimPanel[] dimensions = new DimPanel[3];
	public static JSlider its;
	public static StyleGUI stylerPanel;
	public static ControlGUI controllerPanel;
	public static GifGUI gifPanel;
	public static JTextArea consoleArea;
	public static String lastStr = "";
	public static JLabel itsLabel;
	
	static Canvas mainCanvas = new Canvas();
	private static BufferedImage img;
	public static int[] pixels;
	
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
			dimensions[i] = new DimPanel(800+f.getInsets().left, f.getInsets().top+margins+200*i, 400-margins, 200-margins, i);
			f.add(dimensions[i]);
		}


		//left panel
		Panel leftPanel = new Panel();
		leftPanel.setBackground(Color.lightGray);
		leftPanel.setLayout(null);
		leftPanel.setBounds(f.getInsets().left+margins, f.getInsets().top+margins, 800-2*margins, 600-margins);
		JCheckBox radioC = new JCheckBox("C");
		JCheckBox radioZ = new JCheckBox("Z");
		JCheckBox radioX = new JCheckBox("X");
		radioC.setBounds(Generator.width+margins*2+0*(margins+64), margins, 64, 16);
		radioZ.setBounds(Generator.width+margins*2+1*(margins+64), margins, 64, 16);
		radioX.setBounds(Generator.width+margins*2+2*(margins+64), margins, 64, 16);
		radioC.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Controller.s1 = radioC.isSelected();
			}
		});
		radioZ.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Controller.s2 = radioZ.isSelected();
			}
		});
		radioX.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Controller.s3 = radioX.isSelected();
			}
		});
		radioC.setSelected(true);
		radioZ.setSelected(false);
		leftPanel.add(radioC);
		leftPanel.add(radioZ);
		leftPanel.add(radioX);
		
		
		//Console
		consoleArea = new JTextArea();
		consoleArea.setBounds(margins,margins*2+Generator.height, Generator.width, 600-Generator.height-4*margins);
		consoleArea.setEditable(false);
		clog("Console Loaded");
		leftPanel.add(consoleArea);


		its = new JSlider(0,100,20);
		its.setBounds(2*margins+Generator.height,margins*2+16,256,32);
		// Set the labels to be painted on the slider
		its.setPaintLabels(true);
		// Add positions label in the slider
		Hashtable<Integer, JLabel> itsPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=20) itsPosition.put(i, new JLabel(itsBarToNumber(i)+""));
		// Set the label to be drawn
		its.setLabelTable(itsPosition); 
		leftPanel.add(its);
		
		itsLabel = new JLabel();
		itsLabel.setBounds(2*margins+Generator.height,margins*2+64,256,32);
		leftPanel.add(itsLabel);

		
		
		mainCanvas.setBounds(margins,margins,Generator.width,Generator.height);
		leftPanel.add(mainCanvas);
		
		f.add(leftPanel);
		
		


		//styler panel
		stylerPanel = new StyleGUI(800+f.getInsets().left, f.getInsets().top+margins+600, 400-margins, 200-2*margins);
		f.add(stylerPanel);
		
		
		//controller panel
		controllerPanel = new ControlGUI(400+f.getInsets().left, f.getInsets().top+margins+600, 400-margins, 200-2*margins);
		f.add(controllerPanel);
		
		
		//controller panel
		gifPanel = new GifGUI(f.getInsets().left + margins, f.getInsets().top+margins+600, 400-2*margins, 200-2*margins);
		f.add(gifPanel);
		
		
		
		
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
	}
	
	public static void clog(String str) {
		if(str.equals(lastStr)) return;
		lastStr = str;
		consoleArea.append(str+"\n");
	}
	
	public static void render() {

		Generator.iterations = itsBarToNumber(its.getValue());
		itsLabel.setText(""+Generator.iterations);

		stylerPanel.tick();
		controllerPanel.tick();
		
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
	
	public static int itsBarToNumber(int x) {
		return (int) (x + Math.exp(Math.sqrt(x)-3));
	}
}