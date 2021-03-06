package it.unibs.pajc.client;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import java.awt.event.*;

public class PnlTimer extends JPanel {

	/**
	 * Create the panel.
	 */

	private JLabel lblTimer;
	private static int seconds;
	private static final int DELAY = 1000; //millisecondi
	
	Timer timer = new Timer(DELAY, new ActionListener() {		  
	  public void actionPerformed(ActionEvent e) {
		  if(seconds > 0) {
			  seconds -= 1;
			  lblTimer.setText(""+seconds);
		  }
		  if(seconds == 0)
			  fireValuesChange(new ChangeEvent(this));
		}	  
	 });
	
	 public PnlTimer() {
		setLayout(null);
			
		lblTimer = new JLabel("Timer");
		lblTimer.setBounds(38, 23, 53, 14);
		add(lblTimer);
	}
	
	protected void startTimer() {
		seconds = 20;
		timer.start();
	}
	
	protected void stopTimer() {
		timer.stop();
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
		
}
