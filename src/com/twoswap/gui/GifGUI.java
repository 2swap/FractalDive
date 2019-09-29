package com.twoswap.gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import com.twoswap.mandelbrot.Generator;
import com.twoswap.mandelbrot.extras.Gif;
import com.twoswap.mandelbrot.extras.Mp4;
import com.twoswap.mandelbrot.extras.Xugg;

public class GifGUI extends Panel{
	private static final long serialVersionUID = 1L;
	
	public GifGUI(int x, int y, int w, int h) {
		
		setBackground(Color.lightGray);
		setLayout(null);
		setBounds(x,y,w,h);
		
		JLabel tag = new JLabel("gif");
		tag.getFont().deriveFont(8.0f);
		tag.setBounds(2,2,64,16);
		add(tag);
		
		Button g = new Button("Gif");
		g.setBounds(GUI.margins,100,64,20); // setting button position
		g.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Generator.record) {
					GUI.clog("Gif Recording Terminated");
					Gif.run();
				} else {
					GUI.clog("Gif Recording In Progress");
					Generator.time = 0;
					Generator.record = true;
				}
			}
		});
		add(g); // adding button into frame
		
		Button m = new Button("Mp4");
		m.setBounds(GUI.margins,50,64,20); // setting button position
		m.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Generator.record) {
					GUI.clog("MP4 Recording Terminated");
					Mp4.run();
				} else {
					GUI.clog("MP4 Recording In Progress");
					Generator.time = 0;
					Generator.record = true;
				}
			}
		});
		add(m); // adding button into frame
		
		Button u = new Button("Xuggler");
		u.setBounds(GUI.margins,150,64,20); // setting button position
		u.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Generator.record) {
					GUI.clog("Xuggler Recording Terminated");
					Xugg.run();
				} else {
					GUI.clog("Xuggler Recording In Progress");
					Generator.time = 0;
					Generator.record = true;
				}
			}
		});
		add(u); // adding button into frame
	}
	public void tick() {
	}
}
