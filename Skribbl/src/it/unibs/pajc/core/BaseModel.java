package it.unibs.pajc.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class BaseModel {

	private ArrayList<ActionListener> listenerList = new ArrayList<ActionListener>();
	protected EventListenerList changeListenerList = new EventListenerList();

	public void addChangeListener(ChangeListener l) {
		changeListenerList.add(ChangeListener.class ,l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		changeListenerList.remove(ChangeListener.class, l);
	}
	
	public void fireValuesChange(ChangeEvent e) {
		
		for (ChangeListener changeListener : changeListenerList.getListeners(ChangeListener.class)) {
			changeListener.stateChanged(e);
		}
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
}
