package it.unibs.pajc.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class PaintArea extends JPanel implements MouseMotionListener{

	/**
	 * Create the panel.
	 */
	private int currentX = -10, currentY = -10;
	private int oldX, oldY;
	
	public PaintArea() {
		setBackground(Color.WHITE);
		this.addMouseMotionListener(this);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g; 
		g2.setStroke(new BasicStroke((float)3));
		g2.setColor(Color.RED);
		
		g2.fillOval(currentX, currentY, 6, 6);
//		g.drawLine(0, 0, currentX, currentY);
		
		//IMPORTANTE
		//g2 punta allo stesso oggetto di g
		//=> ogni cambiamento di uno comporta un cambiamento nell'altro
		
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2.setColor(Color.RED);
//		
//		g2.drawLine(0, 0, currentX, currentY);
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		currentX = e.getX();
		currentY = e.getY();
		
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
	
}
