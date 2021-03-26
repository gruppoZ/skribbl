package it.unibs.pajc.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import it.unibs.pajc.whiteboard.WhiteBoardLine;


public class PnlPaintArea extends JPanel implements MouseListener, MouseMotionListener{
	 
	// Lines drawn, consists of a List of PolyLine instances
	
	//usiamo la synchronizedList perch� se no da problemi nel for del paintComponent()
	private List<WhiteBoardLine> lines = Collections.synchronizedList(new ArrayList<WhiteBoardLine>());
    private WhiteBoardLine currentLine;  // the current line (for capturing)
    private Graphics2D g2;
    
    //TODO: vedere bene dove inizializzare gli att
    private Color lineColor = Color.BLACK;
//    private float strokeSize = 2;
    
    private static final float DEFAULT_SIZESTROKE = 2;
    private float sizeStroke = DEFAULT_SIZESTROKE;
    
    private boolean painter = false;
    
    
    private ClientModel model;
    
    
	/**
	 * Create the panel.
	 */
	
	public PnlPaintArea(ClientModel model) {
		setBackground(Color.WHITE);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		
		this.model  = model;
		
//		if(model.isLine)
//			model.addChangeListener(e -> this.updateWhiteBoard());
		
	}
	
//	public void setModel(ClientModel model) {
//		this.model = model;
//	}
	
	protected void useDefaultSizeStroke() {
		sizeStroke = DEFAULT_SIZESTROKE;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D)g; 
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//set color and size 
//		g2.setStroke(lineStroke);
		
		//TODO: vedere se va senza syncrho
//		synchronized (lines) {
//			for (WhiteBoardLine line: lines) {
//				g2.setColor(line.getColor());
//				g2.setStroke(new BasicStroke(line.getStrokeSize()));
//				line.draw(g2);
//			}
//		}
		getLines().forEach((line) -> {
			g2.setColor(line.getColor());
			g2.setStroke(new BasicStroke(line.getStrokeSize()));
			line.draw(g2);
		});
		
		
	}

	private synchronized List<WhiteBoardLine> getLines() {
		return this.lines;
	}
	
	/**
	 * quando il mouse si muove creo la line e gli aggiungo il primo punto della linea disegnata
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (painter) {
	        currentLine = new WhiteBoardLine(lineColor, sizeStroke);
	        getLines().add(currentLine);
	        currentLine.addPoint(e.getX(), e.getY());
		}
	}
	
	/**
	 * finch� draggo il mouse continuo ad aggiungere punti alla line e la disegno a schermo (del disegnatore)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if(painter) {
			currentLine.addPoint(e.getX(), e.getY());
			repaint();
		}
		
	}
	
	/**
	 * quando rilascio il mouse invio al server la line con tutti i punti
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if(painter)
			model.sendMsg(currentLine);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	
	public void setPainter() {
		this.painter = !painter;
	}
	
	public boolean isPainter() {
		return painter;
	}

	public void updateWhiteBoard(WhiteBoardLine line) {
		if(line != null) {
			getLines().add(line);
			repaint();
		}	
	}
	
	protected void changePaint(ActionEvent e) {
		
		if(model.isColor(e.getActionCommand()))
			lineColor = ClientModel.getColorByName(e.getActionCommand());
			
		if(model.isIcon(e.getActionCommand()) && model.isRubber(e.getActionCommand()))
			lineColor = Color.WHITE;
			//TODO: fare un draw oval sul mouse quando hai la gomma
		
		if(model.isIcon(e.getActionCommand()) && model.isDimension1(e.getActionCommand()))
			sizeStroke = 2;
		
		if(model.isIcon(e.getActionCommand()) && model.isDimension2(e.getActionCommand()))
			sizeStroke = 4;
		
		if(model.isIcon(e.getActionCommand()) && model.isDimension3(e.getActionCommand()))
			sizeStroke = 6;
		
		if(model.isIcon(e.getActionCommand()) && model.isSave(e.getActionCommand()))
			model.savePaint(this);
		
		if(model.isIcon(e.getActionCommand()) && model.isTrash(e.getActionCommand())) {
			model.sendMsg("!deleteall");
			clearAll();
		}
	}
	
	public void clearAll() {
		if(getLines() != null && !getLines().isEmpty()) {
			getLines().clear();
			repaint();
		}
		
	}
	
	
}
