package com.twoswap.gui;

import java.awt.Color;
import java.awt.Panel;
import java.awt.TextField;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Styler;

public class StyleGUI extends Panel{
	private static final long serialVersionUID = 1L;
	TextField tf,tfi;
	JSlider inhale;
	JRadioButton r1,r2, r3;
	public JCheckBox line,point;
	
	public StyleGUI(int x, int y, int w, int h) {
		setBackground(Color.lightGray);
		setLayout(null);
		setBounds(x,y,w,h);
		JLabel stylerTag = new JLabel("style");
		stylerTag.getFont().deriveFont(8.0f);
		stylerTag.setBounds(2,2,64,16);
		add(stylerTag);
		
		inhale = new JSlider(0,100,50);
		inhale.setBounds(GUI.margins,16+GUI.margins,196,32);
		// Set the labels to be painted on the slider
		inhale.setPaintLabels(true);
		// Add positions label in the slider
		Hashtable<Integer, JLabel> inhalePosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) inhalePosition.put(i, new JLabel(i/50.-1+""));
		// Set the label to be drawn
		inhale.setLabelTable(inhalePosition); 
		add(inhale);
		
		tf = new TextField("rainbow");
		tf.setBounds(GUI.margins,16+32+2*GUI.margins,196,16);
		add(tf);
		
		tfi= new TextField("none");
		tfi.setBounds(GUI.margins*2+196,64+2*GUI.margins,188,16);
		add(tfi);
		
		r1 = new JRadioButton("Corona");
		r2 = new JRadioButton("Result");
		r3 = new JRadioButton("Popularity");
		r1.setBounds(196+2*GUI.margins,GUI.margins+16,188,16);
		r2.setBounds(196+2*GUI.margins,GUI.margins+32,188,16);
		r3.setBounds(196+2*GUI.margins,GUI.margins+48,188,16);
		r1.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(r1);bg.add(r2);bg.add(r3);
		add(r1);add(r2);add(r3);

		line = new JCheckBox("Line");
		line.setBounds(0,100,64,16);
		add(line);
		
		point = new JCheckBox("Point");
		point.setBounds(0,116,64,16);
		add(point);
	}
	public void tick() {
		Styler.iterationCount = r1.isSelected();
		Styler.inside = r3.isSelected();
		Styler.type = tf.getText();
		Styler.insideType = tfi.getText();
		Styler.inhale += inhale.getValue()/50.-1;
	}
}
