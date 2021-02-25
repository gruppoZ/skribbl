package it.unibs.pajc.client;

import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class PaintArea extends JPanel implements MouseMotionListener {
	public Color lineColor = Color.RED;
	 
	// Lines drawn, consists of a List of PolyLine instances
	private List<PolyLine> lines = new ArrayList<PolyLine>();
    private PolyLine currentLine;  // the current line (for capturing)
    private Graphics2D g2;
	/**
	 * Create the panel.
	 */
	public PaintArea() {
		setForeground(new Color(0, 0, 0));
		setBackground(Color.WHITE);
	    this.addMouseMotionListener(this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D)g;
		g2.setColor(lineColor);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke((float)2));
		
		
        for (PolyLine line: lines) {
           g2.setColor(line.getColore());
           line.draw(g2);
        }
		
	}

	protected void changePaint(ActionEvent e) {
		System.out.println("ActionCommand: " + e.getActionCommand());
		System.out.println("Source: " + e.getSource());
		
		if(ClientModel.isColor(e.getActionCommand()))
			lineColor = ClientModel.getColorByName(e.getActionCommand());
		if(ClientModel.isIcon(e.getActionCommand()) && ClientModel.isRubber(e.getActionCommand()))
			lineColor = Color.WHITE; //TODO da fare in Generale, per varie ICONE come cestino -> Cancella tutto: lines.clear()
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		currentLine.addPoint(e.getX(), e.getY());
        repaint();  // invoke paintComponent()
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Begin a new line
        currentLine = new PolyLine(lineColor);
        lines.add(currentLine);
        currentLine.addPoint(e.getX(), e.getY());
		
	}

}
