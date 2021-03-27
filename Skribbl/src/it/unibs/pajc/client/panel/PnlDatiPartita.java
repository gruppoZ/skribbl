package it.unibs.pajc.client.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class PnlDatiPartita extends JPanel {
	
	private JTextField txtRound;
	private JLabel lblTimer;
	private JTextField txtPainter;
	private JTextField txtGuessWord;

	private int seconds;
	
	private static final String DEFAULT_GUESS_WORD = "Guess the Word";
	private static final int DELAY = 1000; //millisecondi
	
	/**
	 * Create the panel.
	 */
	public PnlDatiPartita() {
		setLayout(null);
		
		txtRound = new JTextField();
		txtRound.setText("/");
		txtRound.setHorizontalAlignment(SwingConstants.CENTER);
		txtRound.setFont(new Font("Stencil", Font.ITALIC, 12));
		txtRound.setEditable(false);
		txtRound.setBackground(Color.WHITE);
		txtRound.setBounds(51, 140, 107, 31);
		add(txtRound);
		
		lblTimer = new JLabel();
		lblTimer.setText("Timer\r\n");
		lblTimer.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimer.setFont(new Font("Arial Black", Font.ITALIC, 12));
		lblTimer.setBackground(Color.WHITE);
		lblTimer.setOpaque(true);
		lblTimer.setBounds(51, 63, 107, 56);
		add(lblTimer);
		
		txtPainter = new JTextField();
		txtPainter.setEditable(false);
		txtPainter.setBackground(Color.WHITE);
        txtPainter.setHorizontalAlignment(SwingConstants.CENTER);
        txtPainter.setFont(new Font("Stencil", Font.ITALIC, 12));
		txtPainter.setBounds(10, 182, 187, 31);
		add(txtPainter);
		
		txtGuessWord = new JTextField();
		txtGuessWord.setText(DEFAULT_GUESS_WORD);
		txtGuessWord.setHorizontalAlignment(SwingConstants.CENTER);
		txtGuessWord.setFont(new Font("Stencil", Font.ITALIC, 12));
		txtGuessWord.setEditable(false);
		txtGuessWord.setBackground(Color.WHITE);
		txtGuessWord.setBounds(10, 11, 187, 41);
		add(txtGuessWord);
		

	}
	
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
	
	public void startTimer(int seconds) {
		//TODO: arriva sempre 1 secondo in ritardo, colpa del passaggi che deve fare
		this.seconds = seconds;
		timer.start();
	}
	
	public void stopTimer() {
		//prima setta i secondi a 0 e lo stampa poi ferma il timer
		seconds = 0;
		lblTimer.setText(""+seconds);
		timer.stop();
		txtGuessWord.setText(DEFAULT_GUESS_WORD);
		
	}
	
	public JTextField getTxtRound() {
		return this.txtRound;
	}
	public JTextField getTxtPainter() {
		return this.txtPainter;
	}
	public JTextField getTxtGuessWord() {
		return this.txtGuessWord;
	}
	public JLabel getlblTimer() {
		return this.lblTimer;
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
