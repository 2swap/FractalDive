package com.twoswap.gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Controller;

public class ControlGUI extends Panel{
	private static final long serialVersionUID = 1L;
	public JSlider zoomSpeed, moveSpeed, va;
	public JCheckBox inv, burningShip;
	
	public ControlGUI(int x, int y, int w, int h) {
		setBackground(Color.lightGray);
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
		moveSpeed.setPaintLabels(true);
		Hashtable<Integer, JLabel> moveSpeedPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) moveSpeedPosition.put(i, new JLabel(Math.round(Math.sqrt(i))+""));
		moveSpeed.setLabelTable(moveSpeedPosition); 
		add(moveSpeed);
		
		va = new JSlider(0,100,50);
		va.setBounds(GUI.margins+200,20,200-3*GUI.margins,32);
		va.setPaintLabels(true);
		Hashtable<Integer, JLabel> vaPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) vaPosition.put(i, new JLabel((i-50)/1000.+""));
		va.setLabelTable(vaPosition); 
		add(va);


		inv = new JCheckBox("Inversion");
		inv.setBounds(GUI.margins,108+3*GUI.margins,128,16);
		inv.setIconTextGap(16);
		add(inv);

		burningShip = new JCheckBox("Burning Ship");
		burningShip.setBounds(GUI.margins,128+3*GUI.margins,128,16);
		burningShip.setIconTextGap(16);
		add(burningShip);
		
		
		Button b = new Button("Reset");
		b.setBounds(GUI.margins,96,64,20); // setting button position
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Controller.reset();
			}
		});
		add(b); // adding button into frame
	}
	public void tick() {
		Controller.inversion = inv.isSelected();
		Controller.burningShip = burningShip.isSelected();
		Controller.zoomSpeed = Math.pow(1.2,zoomSpeed.getValue()/50.-1);
		Controller.speed = Math.sqrt(moveSpeed.getValue());
		Controller.va = (va.getValue()-50)/1000.;
	}
}
