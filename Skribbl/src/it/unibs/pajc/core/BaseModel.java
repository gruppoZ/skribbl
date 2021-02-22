package it.unibs.pajc.core;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class BaseModel{

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
}
