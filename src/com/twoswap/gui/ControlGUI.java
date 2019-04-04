package com.twoswap.gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Controller;

public class ControlGUI extends Panel{
	private static final long serialVersionUID = 1L;
	JSlider zoomSpeed, moveSpeed;
	
	public ControlGUI(int x, int y, int w, int h) {
		setBackground(Color.GRAY);
		setLayout(null);
		setBounds(x,y,w,h);
		JLabel tag = new JLabel("control");
		tag.getFont().deriveFont(8.0f);
		tag.setBounds(2,2,64,16);
		add(tag);
		
		zoomSpeed = new JSlider(0,100,50);
		zoomSpeed.setBounds(GUI.margins,20,200-3*GUI.margins,32);
		// Set the labels to be painted on the slider
		zoomSpeed.setPaintLabels(true);
		// Add positions label in the slider
		Hashtable<Integer, JLabel> zoomSpeedPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) zoomSpeedPosition.put(i, new JLabel(Math.round(Math.pow(1.2,i/50.-1)*100)/100.+""));
		// Set the label to be drawn
		zoomSpeed.setLabelTable(zoomSpeedPosition); 
		add(zoomSpeed);
		
		moveSpeed = new JSlider(0,100,0);
		moveSpeed.setBounds(GUI.margins,60,200-3*GUI.margins,32);
		// Set the labels to be painted on the slider
		moveSpeed.setPaintLabels(true);
		// Add positions label in the slider
		Hashtable<Integer, JLabel> moveSpeedPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) moveSpeedPosition.put(i, new JLabel(Math.round(Math.sqrt(i))+""));
		// Set the label to be drawn
		moveSpeed.setLabelTable(moveSpeedPosition); 
		add(moveSpeed);
		
		Button b = new Button("randomize");
		b.setBounds(GUI.margins,100,80,30); // setting button position
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Controller.randomize();
			}
		});
		add(b); // adding button into frame
	}
	public void tick() {
		Controller.zoomSpeed = Math.pow(1.2,zoomSpeed.getValue()/50.-1);
		Controller.speed = Math.sqrt(moveSpeed.getValue());
	}
}
