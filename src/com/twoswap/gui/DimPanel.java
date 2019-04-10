package com.twoswap.gui;

import java.awt.Color;
import java.awt.Panel;
import java.awt.TextField;
import java.text.DecimalFormat;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Controller;
import com.twoswap.mandelbrot.Generator;
import com.twoswap.mandelbrot.extras.Calculator;

class DimPanel extends Panel {
	private static final long serialVersionUID = 1L;
	public static char[] names = {'C','Z','X'};
	public static String[] examples = {"<cos(x)/4-1,sin(x)/4>","<cos(x),sin(x)>","<2cos(x),0>"};
	JCheckBox check;
	boolean shouldRender;
	DimCanvas c;
	
	JSlider rPart,iPart, pSpeed;
	JLabel point;
	TextField eqn;
	int p;
	
	public DimPanel(int x, int y, int w, int h, int p) {
		this.p = p;
		shouldRender = p != 2;
		setBackground(Color.lightGray);
		setLayout(null);
		setBounds(x,y,w,h);
		
		c = new DimCanvas(p);
		c.setBackground(Color.WHITE);
		c.setBounds(GUI.margins,GUI.margins,GUI.dimCanvasWidth,GUI.dimCanvasWidth);
		add(c);
		
		check = new JCheckBox("show " + names[p], shouldRender);
		check.setBounds(GUI.margins,20+3*GUI.margins+GUI.dimCanvasWidth,128,16);
		check.setIconTextGap(16);
		add(check);
		point = new JLabel("Location");
		point.setBounds(GUI.margins,20+16+3*GUI.margins+GUI.dimCanvasWidth,128,16);
		add(point);
		
		
		
		pSpeed = new JSlider(-50,50,0);
		pSpeed.setBounds(20+GUI.dimCanvasWidth+GUI.margins*3,20+2*GUI.margins,w-4*GUI.margins-GUI.dimCanvasWidth-20,20);
		add(pSpeed);
		
		
		
		rPart = new JSlider(-80,80,0);
		rPart.setBounds(GUI.margins,GUI.margins*2+GUI.dimCanvasWidth,GUI.dimCanvasWidth,20);
		add(rPart);
		iPart = new JSlider(JSlider.VERTICAL,-80,80,0);
		iPart.setBounds(GUI.margins*2+GUI.dimCanvasWidth,GUI.margins,20,GUI.dimCanvasWidth);
		add(iPart);
		
		
		
		eqn = new TextField();
		eqn.setBounds(20+GUI.dimCanvasWidth+GUI.margins*3,GUI.margins,w-4*GUI.margins-GUI.dimCanvasWidth-20,20);
		eqn.setText("Example: '"+examples[p]+"'");
		add(eqn);
		
		
		
		if(p == 2) rPart.setValue(-40);//exponent 2 by default
	}
	public void render() {
		
		double s = pSpeed.getValue()/100.;
		double q = Generator.time*(s*s*s);
		String fn = eqn.getText().replaceAll("x", ""+q);
		boolean b = fn.startsWith("<") && fn.endsWith(">") && fn.contains(",");
		
		double r=0,i=0;
		
		if(b) {
			r = Double.parseDouble(Calculator.calc(fn.substring(1).split(",")[0]));
			i = Double.parseDouble(Calculator.calc(fn.split(",")[1].split(">")[0]));
			rPart.setValue((int) (-20*r));
			iPart.setValue((int) (-20*i));
		} else {
			r = -rPart.getValue()/20.;
			i = -iPart.getValue()/20.;
		}
		
		DecimalFormat df = new DecimalFormat("#.00");
		point.setText(names[p] + " = " + df.format(r) + " + " + df.format(i) + "i");
		
		if(p == 0) {
			Controller.rC = r;
			Controller.iC = i;
		}else if(p == 1) {
			Controller.rZ = r;
			Controller.iZ = i;
		}else if(p == 2) {
			Controller.rX = r;
			Controller.iX = i;
		}
		
		
		if(check.isSelected()) c.render(-rPart.getValue()/20., -iPart.getValue()/20.);
		else c.renderX();
	}
}