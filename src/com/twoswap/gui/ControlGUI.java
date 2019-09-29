package com.twoswap.gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
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
	public JSlider zoomSpeed, zoomBar, moveSpeed, va, angleBar;
	public JCheckBox inv, burningShip;
	public JLabel xTag = new JLabel(), yTag = new JLabel(), zoomTag = new JLabel();
	public Panel leftPanel = new Panel();
	public Panel rightPanel = new Panel();
	
	public ControlGUI(int x, int y, int w, int h) {
		
		setBackground(Color.lightGray);
		setLayout(null);
		setBounds(x,y,w,h);

		leftPanel.setLayout(null);
		rightPanel.setLayout(null);
		
		leftPanel.setBounds(GUI.margins, GUI.margins, (w-3*GUI.margins)/2, h-2*GUI.margins);
		rightPanel.setBounds((GUI.margins+w)/2, GUI.margins, (w-3*GUI.margins)/2, h-2*GUI.margins);

		populateLeft();
		populateRight();
		
		add(leftPanel);
		add(rightPanel);
	}
	
	public void populateLeft() {

		zoomSpeed = new JSlider(0,100,50);
		zoomSpeed.setBounds(0,0,leftPanel.getWidth(),32);
		zoomSpeed.setPaintLabels(true);
		Hashtable<Integer, JLabel> zoomSpeedPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) zoomSpeedPosition.put(i, new JLabel(Math.round(Math.pow(1.2,i/50.-1)*100)/100.+""));
		zoomSpeed.setLabelTable(zoomSpeedPosition); 
		leftPanel.add(zoomSpeed);

		zoomBar = new JSlider(0,100000,8000);
		zoomBar.setBounds(0,32,leftPanel.getWidth(),32);
		zoomBar.setPaintLabels(true);
		Hashtable<Integer, JLabel> zoomBarPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100000; i+=25000) zoomBarPosition.put(i, new JLabel("e^"+(int)(i/2000.)));
		zoomBar.setLabelTable(zoomBarPosition); 
		leftPanel.add(zoomBar);

		
		
		va = new JSlider(0,100,50);
		va.setBounds(0,64+GUI.margins,leftPanel.getWidth(),32);
		va.setPaintLabels(true);
		Hashtable<Integer, JLabel> vaPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) vaPosition.put(i, new JLabel((i-50)/500.+""));
		va.setLabelTable(vaPosition); 
		leftPanel.add(va);

		angleBar = new JSlider(0,3600,0);
		angleBar.setBounds(0,96+GUI.margins,leftPanel.getWidth(),32);
		angleBar.setPaintLabels(true);
		Hashtable<Integer, JLabel> angleBarPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 3600; i+=900) angleBarPosition.put(i, new JLabel(i*6.28/3600+""));
		angleBar.setLabelTable(angleBarPosition); 
		leftPanel.add(angleBar);
		
		
		
		moveSpeed = new JSlider(0,100,0);
		moveSpeed.setBounds(0,128+2*GUI.margins,leftPanel.getWidth(),32);
		moveSpeed.setPaintLabels(true);
		Hashtable<Integer, JLabel> moveSpeedPosition = new Hashtable<Integer, JLabel>();
		for(int i = 0; i <= 100; i+=25) moveSpeedPosition.put(i, new JLabel(Math.round(Math.sqrt(i)/2)+""));
		moveSpeed.setLabelTable(moveSpeedPosition); 
		leftPanel.add(moveSpeed);
		
	}

	public void populateRight() {
		
		JLabel tag = new JLabel("control");
		tag.getFont().deriveFont(8.0f);
		tag.setBounds(0,0,rightPanel.getWidth(),16);
		rightPanel.add(tag);

		inv = new JCheckBox("Inversion");
		inv.setBounds(0,16,rightPanel.getWidth(),16);
		inv.setIconTextGap(16);
		rightPanel.add(inv);

		burningShip = new JCheckBox("Burning Ship");
		burningShip.setBounds(0,32,rightPanel.getWidth(),16);
		burningShip.setIconTextGap(16);
		rightPanel.add(burningShip);
		
		Button b = new Button("Reset");
		b.setBounds(0,48,rightPanel.getWidth(),20); // setting button position
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Controller.reset();
			}
		});
		rightPanel.add(b); // adding button into frame
		


		xTag.setFont(new Font(xTag.getFont().getName(), Font.PLAIN, 11));
		xTag.setBounds(0,128,rightPanel.getWidth(),16);
		rightPanel.add(xTag);

		yTag.setFont(new Font(yTag.getFont().getName(), Font.PLAIN, 11));
		yTag.setBounds(0,128+16,rightPanel.getWidth(),16);
		rightPanel.add(yTag);

		zoomTag.setFont(new Font(zoomTag.getFont().getName(), Font.PLAIN, 11));
		zoomTag.setBounds(0,128+32,rightPanel.getWidth(),16);
		rightPanel.add(zoomTag);
			
	}
	
	public void tick() {
		Controller.inversion = inv.isSelected();
		Controller.burningShip = burningShip.isSelected();
		
		Controller.zoomSpeed = Math.pow(1.2,zoomSpeed.getValue()/50.-1);
		Controller.zoom = Math.exp(zoomBar.getValue()/2000.) * Controller.zoomSpeed;
		zoomBar.setValue((int) (2000*Math.log(Controller.zoom)));
		
		Controller.speed = Math.sqrt(moveSpeed.getValue())/2;
		
		Controller.va = (va.getValue()-50)/500.;
		Controller.angle = (angleBar.getValue()*6.28/3600 + Controller.va+6.28)%6.28;
		angleBar.setValue((int) (Controller.angle/6.28*3600));

		xTag.setText("x: " + Controller.x);
		yTag.setText("y: " + Controller.y);
		zoomTag.setText("zoom: " + Controller.zoom);
	}
}
