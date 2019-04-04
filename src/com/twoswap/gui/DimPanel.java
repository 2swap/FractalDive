package com.twoswap.gui;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Panel;

import javax.swing.JLabel;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Controller;

class DimPanel extends Panel {
	private static final long serialVersionUID = 1L;
	public static char[] names = {'C','Z','X'};
	Checkbox check;
	boolean shouldRender;
	DimCanvas c;
	
	JSlider rPart,iPart;
	JLabel point;
	int p;
	
	public DimPanel(int x, int y, int w, int h, int p) {
		this.p = p;
		shouldRender = p != 2;
		setBackground(Color.GRAY);
		setLayout(null);
		setBounds(x,y,w,h);
		
		c = new DimCanvas(p);
		c.setBackground(Color.WHITE);
		c.setBounds(GUI.margins,GUI.margins,GUI.dimCanvasWidth,GUI.dimCanvasWidth);
		add(c);
		
		check = new Checkbox("", shouldRender);
		check.setBounds(2*GUI.margins+GUI.dimCanvasWidth,2*GUI.margins+GUI.dimCanvasWidth,16,16);
		add(check);
		JLabel name = new JLabel("show " + names[p]);
		name.setBounds(2*GUI.margins+GUI.dimCanvasWidth,20+GUI.dimCanvasWidth,64,32);
		add(name);
		
		point = new JLabel("Location");
		point.setBounds(96+GUI.dimCanvasWidth,20+GUI.dimCanvasWidth,128,32);
		add(point);
		
		rPart = new JSlider(-40,40,0);
		rPart.setBounds(GUI.margins,GUI.margins*2+GUI.dimCanvasWidth,GUI.dimCanvasWidth,32);
		add(rPart);
		
		iPart = new JSlider(JSlider.VERTICAL,-40,40,0);
		iPart.setBounds(GUI.margins*2+GUI.dimCanvasWidth,GUI.margins,32,GUI.dimCanvasWidth);
		add(iPart);
		
		if(p == 2) rPart.setValue(-20);//exponent 2 by default
	}
	public void render() {
		point.setText(names[p] + " = " + -rPart.getValue()/10. +" + "+ -iPart.getValue()/10. + "i");

		if(p == 0) {
			Controller.rC = -rPart.getValue()/10.;
			Controller.iC = -iPart.getValue()/10.;
		}if(p == 1) {
			Controller.rZ = -rPart.getValue()/10.;
			Controller.iZ = -iPart.getValue()/10.;
		}if(p == 2) {
			Controller.rX = -rPart.getValue()/10.;
			Controller.iX = -iPart.getValue()/10.;
		}
		
		if(check.getState()) c.render(-rPart.getValue()/10., -iPart.getValue()/10.);
		else c.renderX();
	}
}