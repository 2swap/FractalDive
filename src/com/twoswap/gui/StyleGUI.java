package com.twoswap.gui;

import java.awt.Color;
import java.awt.Panel;
import java.awt.TextField;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Styler;

public class StyleGUI extends Panel{
	private static final long serialVersionUID = 1L;
	TextField tf;
	JSlider inhale;
	
	public StyleGUI(int x, int y, int w, int h) {
		setBackground(Color.lightGray);
		setLayout(null);
		setBounds(x,y,w,h);
		JLabel stylerTag = new JLabel("style");
		stylerTag.getFont().deriveFont(8.0f);
		stylerTag.setBounds(2,2,64,16);
		add(stylerTag);
		
		inhale = new JSlider(0,100,50);
		inhale.setBounds(GUI.margins,20,200-3*GUI.margins,32);
		// Set the labels to be painted on the slider
		inhale.setPaintLabels(true);
		// Add positions label in the slider
		Hashtable<Integer, JLabel> inhalePosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) inhalePosition.put(i, new JLabel(i/50.-1+""));
		// Set the label to be drawn
		inhale.setLabelTable(inhalePosition); 
		add(inhale);
		
		tf = new TextField("rainbow");
		tf.setBounds(GUI.margins,60,200-3*GUI.margins,32);
		add(tf);
	}
	public void tick() {
		Styler.type = tf.getText();
		Styler.inhale = inhale.getValue()/50.-1;
	}
}
