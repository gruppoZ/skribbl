package it.unibs.pajc.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class PaintArea extends JPanel implements MouseMotionListener{
	 
	// Lines drawn, consists of a List of PolyLine instances
	private List<PolyLine> lines = new ArrayList<PolyLine>();
    private PolyLine currentLine;  // the current line (for capturing)
    
	/**
	 * Create the panel.
	 */
	
	public PaintArea() {
		setBackground(Color.WHITE);
		this.addMouseMotionListener(this);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g; 
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//set color and size 
		g2.setStroke(new BasicStroke((float)2));
		g2.setColor(Color.RED);
		
		for (PolyLine line: lines) {
	           line.draw(g2);
	        }
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		currentLine.addPoint(e.getX(), e.getY());
        repaint();  // invoke paintComponent()
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		// Begin a new line
        currentLine = new PolyLine();
        lines.add(currentLine);
        currentLine.addPoint(e.getX(), e.getY());
	}
	
	
}
