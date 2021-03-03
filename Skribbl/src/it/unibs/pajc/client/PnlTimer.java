package it.unibs.pajc.client;

import javax.swing.*;
import javax.swing.Timer;
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
		}	  
	 });
	
	 public PnlTimer() {
		setLayout(null);
		lblTimer = new JLabel("Timer");
		lblTimer.setBounds(38, 23, 53, 14);
		add(lblTimer);
	}
	
	protected synchronized void startTimer() {
		seconds = 10;
		timer.start();
	}
	
	protected synchronized void stopTimer() {
		timer.stop();
	}
		
}
