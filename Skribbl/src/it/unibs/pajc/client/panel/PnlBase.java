package it.unibs.pajc.client.panel;	

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

public class PnlBase extends JPanel {

	private ArrayList<ActionListener> listenerList;
	private EventListenerList listenerChangeListenerList;
	
	/**
	 * Create the panel
	 */
	public PnlBase() {
		listenerList = new ArrayList<ActionListener>();
		listenerChangeListenerList = new EventListenerList();
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
	}
	
	/**
	 * Aggiunge un bottone con una stringa data dal parametro symbol
	 * PreferredSize di default = 50x50
	 * @param symbol
	 */
	public void addButton(Object symbol) {
		JButton btn = new JButton((String) symbol);
		btn.setPreferredSize(new Dimension(50, 50));
		this.add(btn);
		
		btn.addActionListener(e -> fireActionListener(e));
	}
	
	/**
	 * Aggiunge un actionListener alla lista delle listener
	 * @param l
	 */
	public void addActionListener(ActionListener l) {
		listenerList.add(l);
	}
	
	/**
	 * Rimuove l'actionListener l dalla lista delle listener se presente
	 * @param l
	 */
	public void removeActionListener(ActionListener l) {
		listenerList.remove(l);
	}
	
	/**
	 * Viene notificato l'actionPerformed a tutti gli ActionListener finora aggiunti
	 * @param e
	 */
	public void fireActionListener(ActionEvent e) {
		/**
		 * Non gli passo e ma gli passo un evento che voglio io
		 * per nascondere i bottoni all'esterno
		 * Sto isolando il mio sistema
		 */
		ActionEvent myEvent = new ActionEvent(this, 
				ActionEvent.ACTION_PERFORMED,
				e.getActionCommand(),
				e.getWhen(),
				e.getModifiers() //se quando premo con il mouse ho anche schiacciato ctrl questo e' un modifiers
		);
		
		
		for (ActionListener actionListener : listenerList) {
			actionListener.actionPerformed(myEvent);
		}
	}
	
	/**
	 * Aggiunge un changeListener alla lista delle listener
	 * @param l
	 */
	public void addChangeListener(ChangeListener l) {
		listenerChangeListenerList.add(ChangeListener.class ,l);
	}
	
	/**
	 * Rimuove il changeListener l dalla lista delle listener se presente
	 * @param l
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerChangeListenerList.remove(ChangeListener.class, l);
	}
	
	/**
	 * Viene notificato il stateChanged a tutti gli ChangeListener finora aggiunti
	 * @param e
	 */
	public void fireValuesChange(ChangeEvent e) {
		
		for (ChangeListener changeListener : listenerChangeListenerList.getListeners(ChangeListener.class)) {
			changeListener.stateChanged(e);
		}
	}
}
