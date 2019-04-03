package com.twoswap.gui;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Panel;

class DimPanel extends Panel {
	private static final long serialVersionUID = 1L;
	public static char[] names = {'C','Z','X'};
	Checkbox check;
	boolean shouldRender;
	DimCanvas c;
	public DimPanel(int x, int y, int w, int h, int p) {
		shouldRender = p != 2;
		setBackground(Color.GRAY);
		setLayout(null);
		setBounds(x,y,w,h);
		
		c = new DimCanvas(p);
		c.setBackground(Color.WHITE);
		c.setBounds(GUI.margins,GUI.margins,GUI.dimCanvasWidth,GUI.dimCanvasWidth);
		add(c);
		
		check = new Checkbox("render "+names[p], shouldRender);
		add(check);
		check.setBounds(GUI.margins,2*GUI.margins+GUI.dimCanvasWidth,128,16);
	}
	public void render() {
		if(check.getState()) c.render();
		else c.renderX();
	}
}