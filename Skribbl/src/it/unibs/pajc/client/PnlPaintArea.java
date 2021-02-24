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


public class PnlPaintArea extends JPanel implements MouseMotionListener{
	 
	// Lines drawn, consists of a List of PolyLine instances
	private List<PolyLine> lines = new ArrayList<PolyLine>();
    private PolyLine currentLine;  // the current line (for capturing)
    private Graphics2D g2;
    public boolean painter = false;
    
	/**
	 * Create the panel.
	 */
	
	public PnlPaintArea() {
		setBackground(Color.WHITE);
		this.addMouseMotionListener(this);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D)g; 
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
		if(painter) {
			currentLine.addPoint(e.getX(), e.getY());
	        repaint();
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (painter) {
	        currentLine = new PolyLine();
	        lines.add(currentLine);
	        currentLine.addPoint(e.getX(), e.getY());
		}
	}
	
	public void setPainter() {
		this.painter = !painter;
	}
	
	
}
