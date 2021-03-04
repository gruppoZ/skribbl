package it.unibs.pajc.client;

import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class PaintArea extends JPanel implements MouseMotionListener, MouseListener {
	
	private Color lineColor = Color.BLACK;
	private List<PolyLine> lines = new ArrayList<PolyLine>();
    private PolyLine currentLine;  
    private Graphics2D g2;
    private ClientModel model;
    private boolean isPainter = true;
    private static final float DEFAULT_SIZESTROKE = 2;
    private float sizeStroke = DEFAULT_SIZESTROKE;
	/**
	 * Create the panel.
	 */
	public PaintArea(ClientModel model) {
		setForeground(new Color(0, 0, 0));
		setBackground(Color.WHITE);
	    this.addMouseMotionListener(this);
	    this.addMouseListener(this);
	    this.model = model;
	}
	
	protected synchronized List<PolyLine> getLines() {
		return this.lines;
	}
	protected synchronized PolyLine getCurrentLine() {
		return this.currentLine;
	}
	protected synchronized void setLines(PolyLine newLines) {
		if(newLines != null) {
			lines.add(newLines);
			repaint();
		}	
	}
	
	protected synchronized boolean getIsPainter() {
		return this.isPainter;
	}
	protected synchronized void setIsPainter() {
		this.isPainter = !isPainter;
	}
	
	protected void useDefaultSizeStroke() {
		sizeStroke = DEFAULT_SIZESTROKE;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D)g;
		g2.setColor(lineColor);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);		
		
        for (PolyLine line: lines) {
           g2.setColor(line.getColore());
           g2.setStroke(new BasicStroke(line.getStrokeSize()));
           line.draw(g2);
        }
        
		
	}

	protected void changePaint(ActionEvent e) {
		System.out.println("ActionCommand: " + e.getActionCommand());
		System.out.println("Source: " + e.getSource());
	
		if(model.isColor(e.getActionCommand()))
			lineColor = ClientModel.getColorByName(e.getActionCommand());
		if(model.isIcon(e.getActionCommand()) && model.isRubber(e.getActionCommand()))
			lineColor = Color.WHITE; 
		if(model.isIcon(e.getActionCommand()) && model.isDimension1(e.getActionCommand()))
			sizeStroke = 2;
		if(model.isIcon(e.getActionCommand()) && model.isDimension2(e.getActionCommand()))
			sizeStroke = 4;
		if(model.isIcon(e.getActionCommand()) && model.isDimension3(e.getActionCommand()))
			sizeStroke = 6;
		if(model.isIcon(e.getActionCommand()) && model.isTrash(e.getActionCommand())) {
			cleanPaint();
			model.sendTrashcan();
			repaint();
		}	
	}
	
	protected synchronized void cleanPaint() {
		lines.clear();
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(isPainter) {
			currentLine.addPoint(e.getX(), e.getY());
		
			repaint();  // invoke paintComponent()
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(isPainter) {
			currentLine = new PolyLine(lineColor, sizeStroke);
        	lines.add(currentLine);
        	currentLine.addPoint(e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(isPainter)
			fireValuesChange(new ChangeEvent(this));	
	}

	protected EventListenerList listenerList = new EventListenerList();
		
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class ,l);
	}
		
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
		
	public void fireValuesChange(ChangeEvent e) {
		
		for (ChangeListener changeListener : listenerList.getListeners(ChangeListener.class)) {
			changeListener.stateChanged(e);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

}
