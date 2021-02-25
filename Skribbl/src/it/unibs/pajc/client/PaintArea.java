package it.unibs.pajc.client;

import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class PaintArea extends JPanel implements MouseMotionListener {
	public static final Color LINE_COLOR = Color.RED;
	 
	// Lines drawn, consists of a List of PolyLine instances
	private List<PolyLine> lines = new ArrayList<PolyLine>();
    private PolyLine currentLine;  // the current line (for capturing)
    private Graphics2D g2;
	/**
	 * Create the panel.
	 */
	public PaintArea() {
		setForeground(new Color(0, 0, 0));
		setBackground(new Color(255, 255, 240));
	    this.addMouseMotionListener(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D)g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke((float)2));
		
		g2.setColor(LINE_COLOR);
        for (PolyLine line: lines) {
           line.draw(g2);
        }
		
	}

	protected void changePaint(ActionEvent e) {
		System.out.println(e.getActionCommand());
		
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
