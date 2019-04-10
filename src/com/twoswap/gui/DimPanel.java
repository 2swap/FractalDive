package com.twoswap.gui;

import java.awt.Color;
import java.awt.Panel;
import java.awt.TextField;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Controller;

class DimPanel extends Panel {
	private static final long serialVersionUID = 1L;
	public static char[] names = {'C','Z','X'};
	JCheckBox check;
	boolean shouldRender;
	DimCanvas c;
	
	JSlider rPart,iPart;
	JLabel point;
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
		
		
		
		rPart = new JSlider(-80,80,0);
		rPart.setBounds(GUI.margins,GUI.margins*2+GUI.dimCanvasWidth,GUI.dimCanvasWidth,20);
		add(rPart);
		iPart = new JSlider(JSlider.VERTICAL,-80,80,0);
		iPart.setBounds(GUI.margins*2+GUI.dimCanvasWidth,GUI.margins,20,GUI.dimCanvasWidth);
		add(iPart);
		
		
		
		TextField eqn = new TextField();
		eqn.setBounds(20+GUI.dimCanvasWidth+GUI.margins*3,GUI.margins,w-4*GUI.margins-GUI.dimCanvasWidth-20,20);
		eqn.setText("Parametric example: '<5*t,4sin(t)>'");
		add(eqn);
		
		
		
		if(p == 2) rPart.setValue(-40);//exponent 2 by default
	}
	public void render() {
		point.setText(names[p] + " = " + -rPart.getValue()/20. +" + "+ -iPart.getValue()/20. + "i");

		if(p == 0) {
			Controller.rC = -rPart.getValue()/20.;
			Controller.iC = -iPart.getValue()/20.;
		}if(p == 1) {
			Controller.rZ = -rPart.getValue()/20.;
			Controller.iZ = -iPart.getValue()/20.;
		}if(p == 2) {
			Controller.rX = -rPart.getValue()/20.;
			Controller.iX = -iPart.getValue()/20.;
		}
		
		if(check.isSelected()) c.render(-rPart.getValue()/20., -iPart.getValue()/20.);
		else c.renderX();
	}
}