package it.unibs.pajc.client.panel;

import java.awt.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibs.pajc.client.ClientModel;
import it.unibs.pajc.core.ProcessUtils;
import it.unibs.pajc.whiteboard.WhiteBoardLine;


public class PnlPaintArea extends JPanel implements MouseListener, MouseMotionListener{
	 
	private List<WhiteBoardLine> lines;
	private ClientModel model;
    private WhiteBoardLine currentLine;  // the current line (for capturing)
    private Graphics2D g2;
    private Color lineColor;
    private float sizeStroke;
    private boolean painter;
    
    private static final float DEFAULT_SIZESTROKE = 2;

    
	/**
	 * Create the panel.
	 */	
	public PnlPaintArea(ClientModel model) {
		setBackground(Color.WHITE);
		
		lines = Collections.synchronizedList(new ArrayList<WhiteBoardLine>());
		lineColor = Color.BLACK;
		sizeStroke = DEFAULT_SIZESTROKE;
		painter = false;
		
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		
		this.model  = model;
		
	}

	/**
	 * resetta il sizeStroke al valore di default
	 */
	protected void useDefaultSizeStroke() {
		sizeStroke = DEFAULT_SIZESTROKE;
	}
	
	/**
	 * Metodo chiamato dal repaint
	 * Utilizzato per ridisegnare le linee finora aggiunte 
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g2 = (Graphics2D) g; 
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(this.lines.size() > 0) {
			this.lines.forEach((line) -> {
				g2.setColor(line.getColor());
				g2.setStroke(new BasicStroke(line.getStrokeSize()));
				line.draw(g2);
			});
		}
	}
	
	/**
	 * quando il mouse si muove creo la line e gli aggiungo il primo punto della linea disegnata
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (painter) {
	        currentLine = new WhiteBoardLine(lineColor, sizeStroke);
	        this.lines.add(currentLine);
	        currentLine.addPoint(e.getX(), e.getY());
		}
	}
	
	/**
	 * finche' draggo il mouse continuo ad aggiungere punti alla line e la disegno a schermo (del disegnatore)
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
	
	/**
	 * inverto lo stato del painter  (se era falso diventa vero e viceversa)
	 */
	public void setPainter() {
		this.painter = !painter;
	}
	
	/**
	 * 
	 * @return lo stato del painter, se true allora sono il painter e posso disegnare
	 */
	public boolean isPainter() {
		return painter;
	}

	/**
	 * Viene aggiunta la line presa come parametro, infine viene fatto anche il repaint
	 * @param line
	 */
	public void updateWhiteBoard(WhiteBoardLine line) {
		if(line != null) {
			this.lines.add(line);
			repaint();
		}	
	}
	
	/**
	 * In base all'evento "e" preso come parametro, vengono eseguiti task diversi sul PaintArea
	 * @param e
	 */
	public void changePaint(ActionEvent e) {
		
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
		
		if(model.isIcon(e.getActionCommand()) && model.isSave(e.getActionCommand())) {
			model.savePaint(this);
			JOptionPane.showMessageDialog(null, "Hai salvato il disegno");
		}
			
		
		if(model.isIcon(e.getActionCommand()) && model.isTrash(e.getActionCommand())) {
			model.sendMsg("!deleteall");
			model.sendMsg(ProcessUtils.COMMAND_KEY + ProcessUtils.DELETE_ALL);
			clearAll();
		}
	}
	
	/**
	 * Viene resettata la view del PaintArea, eliminando tutte le lines
	 */
	public void clearAll() {
		if(this.lines != null && !this.lines.isEmpty()) {
			this.lines.clear();
			repaint();
		}	
	}
}
