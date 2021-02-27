package it.unibs.pajc.client;

import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class PaintArea extends JPanel implements MouseMotionListener, MouseListener {
	public Color lineColor = Color.BLACK;
	 
	// Lines drawn, consists of a List of PolyLine instances
	private List<PolyLine> lines = new ArrayList<PolyLine>();
    private PolyLine currentLine;  // the current line (for capturing)
    private Graphics2D g2;
    private ClientModel model;
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
	protected synchronized void paintComponent(Graphics g) {
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
		
		if(model.isColor(e.getActionCommand()))
			lineColor = ClientModel.getColorByName(e.getActionCommand());
		if(model.isIcon(e.getActionCommand()) && model.isRubber(e.getActionCommand()))
			lineColor = Color.WHITE; 
		if(model.isIcon(e.getActionCommand()) && model.isTrash(e.getActionCommand())) {
			lines.clear();
			repaint();
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
        currentLine = new PolyLine(lineColor);
        lines.add(currentLine);
        currentLine.addPoint(e.getX(), e.getY());
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		fireValuesChange(new ChangeEvent(this));	
	}
	
	private ArrayList<ActionListener> listenerList = new ArrayList<ActionListener>();
	
	public void addActionListener(ActionListener l) {
		listenerList.add(l);
	}
	
	public void removeActionListener(ActionListener l) {
		listenerList.remove(l);
	}
	
	public void fireActionListener(ActionEvent e) {
		/**
		 * non gli passo e ma gli passo un evento che che voglio io
		 * per nascondere i bottoni all'esterno
		 * Sto: Isolando il mio sistema
		 */
		ActionEvent myEvent = new ActionEvent(this, 
				ActionEvent.ACTION_PERFORMED,
				e.getActionCommand(),
				e.getWhen(),
				e.getModifiers()//se quando premo con il mouse ho anche schiacciato ctrl questo è un modifiers
		);
		
		
		for (ActionListener actionListener : listenerList) {
			actionListener.actionPerformed(myEvent);
		}
	}

		protected EventListenerList listenerList2 = new EventListenerList();
		
		public void addChangeListener(ChangeListener l) {
			listenerList2.add(ChangeListener.class ,l);
		}
		
		public void removeChangeListener(ChangeListener l) {
			listenerList2.remove(ChangeListener.class, l);
		}
		
		public void fireValuesChange(ChangeEvent e) {
			
			for (ChangeListener changeListener : listenerList2.getListeners(ChangeListener.class)) {
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
