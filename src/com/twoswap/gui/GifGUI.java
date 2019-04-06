package com.twoswap.gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;

import com.twoswap.mandelbrot.Controller;
import com.twoswap.mandelbrot.Generator;
import com.twoswap.mandelbrot.extras.Gif;

public class GifGUI extends Panel{
	private static final long serialVersionUID = 1L;
	JSlider zoomSpeed, moveSpeed;
	JCheckBox inv;
	
	public GifGUI(int x, int y, int w, int h) {
		setBackground(Color.lightGray);
		setLayout(null);
		setBounds(x,y,w,h);
		JLabel tag = new JLabel("gif");
		tag.getFont().deriveFont(8.0f);
		tag.setBounds(2,2,64,16);
		add(tag);
		
		
		Button b = new Button("Record");
		b.setBounds(GUI.margins,100,64,20); // setting button position
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Generator.record) {
					b.setName("Record");
					Gif.run();
				}else {
					b.setName("Stop");
					Generator.time = 0;
					Generator.record = true;
				}
			}
		});
		add(b); // adding button into frame
	}
	public void tick() {
		Controller.inversion = inv.isSelected();
		Controller.zoomSpeed = Math.pow(1.2,zoomSpeed.getValue()/50.-1);
		Controller.speed = Math.sqrt(moveSpeed.getValue());
	}
}
