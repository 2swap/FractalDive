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
import com.twoswap.mandelbrot.Input;
import com.twoswap.mandelbrot.extras.Complex;

import net.jafama.FastMath;

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
	public static Input in = new Input();
	public static Panel leftPanel = new Panel();
	
	public GUI(){
		
		mainCanvas.addMouseListener(in);
		mainCanvas.addMouseMotionListener(in);
		mainCanvas.addMouseWheelListener(in);
		mainCanvas.addKeyListener(in);
		
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
		leftPanel.setBackground(Color.lightGray);
		leftPanel.setLayout(null);
		leftPanel.setBounds(f.getInsets().left+margins, f.getInsets().top+margins, 800-2*margins, 600-margins);
		addCZXButtons();
		
		
		addConsole();


		addIterationsSlider();
		

		
		
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

	private void addCZXButtons() {
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
	}

	public static void addConsole() {
		consoleArea = new JTextArea();
		consoleArea.setBounds(margins,margins*2+Generator.height, Generator.width, 600-Generator.height-4*margins);
		consoleArea.setEditable(false);
		clog("Console Loaded");
		leftPanel.add(consoleArea);
	}
	
	private void addIterationsSlider() {
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
	}

	public static void clog(String str) {
		if(str.equals(lastStr)) return;
		lastStr = str;
		consoleArea.append(str+"\n");
	}
	
	public static void render() {
		
		Input.update();
		
		drawPoint(0,0,0xff0000, 0);
		
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
		for (int i = 0; i < Generator.width * Generator.height; i++) if(pixels[i] == -1) pixels[i] = pix[i];
		
		if (Generator.record && Generator.time > 1)
			Generator.savePic(pix, "giffer/img" + (Generator.time - 2) + ".png");
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, Generator.width, Generator.height, null);
		g.dispose();
		bs.show();
		for(DimPanel d : dimensions) d.render();
		
		for (int i = 0; i < Generator.width * Generator.height; i++) pixels[i] = -1;
	}
	
	public static boolean drawPoint(double x, double y, int col, int width) { // Takes point in mandelbrot-space and draws on screen

		//double rotX = (x - Generator.width / 2) * FastMath.cos(Controller.angle) - (y - Generator.height / 2) * FastMath.sin(Controller.angle);
		//double rotY = (x - Generator.width / 2) * FastMath.sin(Controller.angle) + (y - Generator.height / 2) * FastMath.cos(Controller.angle);
		//TODO doesn't work with rotation... nasty linear algebra

		if(Controller.inversion) {
			double pointAng = FastMath.atan2(x, y);
			double pointDist = 1/FastMath.sqrt(x*x+y*y);//try changing this to 1/sqrt!
			x = pointDist * FastMath.sin(pointAng);
			y = pointDist * FastMath.cos(pointAng);
		}
		
		double rotX = (x - Controller.x) * Controller.zoom + Generator.width / 2;
		double rotY = (y - Controller.y) * Controller.zoom + Generator.height / 2;
		int sx = (int) Math.round(rotX);
		int sy = (int) Math.round(rotY);
		
		if(sx>=width && sx < Generator.width - width && sy>=width && sy < Generator.height - width)
			for(int dx = sx - width; dx <= sx + width; dx++) for(int dy = sy - width; dy <= sy + width; dy++) pixels[dx+dy*Generator.width] = col;
		else return false;
		return true;
	}
	
	public static void drawLine(Complex c1, Complex c2, int col) { // Takes points in mandelbrot-space and draws line on screen
		
		double rotX1 = (c1.x - Controller.x) * Controller.zoom + Generator.width / 2;
		double rotY1 = (c1.y - Controller.y) * Controller.zoom + Generator.height / 2;
		int sx1 = (int) Math.round(rotX1);
		int sy1 = (int) Math.round(rotY1);
		
		double rotX2 = (c2.x - Controller.x) * Controller.zoom + Generator.width / 2;
		double rotY2 = (c2.y - Controller.y) * Controller.zoom + Generator.height / 2;
		int sx2 = (int) Math.round(rotX2);
		int sy2 = (int) Math.round(rotY2);
		
		double dist = Math.sqrt((sx1-sx2)*(sx1-sx2)+(sy1-sy2)*(sy1-sy2));
		
		for(int d = 0; d < dist; d++)
			drawPoint(intlerp(sx1,sx2, dist/d), intlerp(sy1,sy2, dist/d), col, 1);
	}
	
	public static Complex screenToMand(int x, int y) { // Takes point in mandelbrot-space and draws on screen

		double rotX = (x - Generator.width / 2) * FastMath.cos(Controller.angle) - (y - Generator.height / 2) * FastMath.sin(Controller.angle);
		double rotY = (x - Generator.width / 2) * FastMath.sin(Controller.angle) + (y - Generator.height / 2) * FastMath.cos(Controller.angle);
		
		double mx = rotX/Controller.zoom + Controller.x;
		double my = rotY/Controller.zoom + Controller.y;
		
		if(Controller.inversion) {
			double pointAng = FastMath.atan2(mx, my);
			double pointDist = 1/FastMath.sqrt(mx*mx+my*my);//try changing this to 1/sqrt!
			mx = pointDist * FastMath.sin(pointAng);
			my = pointDist * FastMath.cos(pointAng);
		}
		
		return new Complex(mx,my);
	}
	
	public static int intlerp(int a, int b, double w) {
		return (int) Math.round(a*w+b*(1-w));
	}
	
	public static int itsBarToNumber(int x) {
		return (int) (x + Math.exp(Math.sqrt(1.5*x)-3));
	}
}