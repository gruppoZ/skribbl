package it.unibs.pajc.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class PaintArea extends JPanel {

	/**
	 * Create the panel.
	 */
	private int currentX, currentY, oldX, oldY;
	private Graphics2D g2;
	
	public PaintArea() {
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				oldX = e.getX();
				oldY = e.getY();
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				currentX = e.getX();
				currentY = e.getY();
				
				if(g2 != null) {
					g2.drawLine(oldX, oldY, currentX, currentY);
					
					repaint();
					
					oldX = currentX;
					oldY = currentY;
				}
				
			}
		});
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//IMPORTANTE
		//g2 punta allo stesso oggetto di g
		//=> ogni cambiamento di uno comporta un cambiamento nell'altro
		g2 = (Graphics2D)g; 
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(Color.RED);
		
	}
	
}
