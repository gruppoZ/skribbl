package it.unibs.pajc.client.panel;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class PnlDatiPartita extends PnlBase {
	
	private JTextField txtRound;
	private JTextField txtPainter;
	private JTextField txtGuessWord;
	private JLabel lblTimer;

	private Timer timer;
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
		txtRound.setBackground(Color.WHITE);
		txtRound.setBounds(51, 140, 107, 31);
		txtRound.setEditable(false);
		add(txtRound);
		
		txtPainter = new JTextField();
		txtPainter.setBackground(Color.WHITE);
        txtPainter.setHorizontalAlignment(SwingConstants.CENTER);
        txtPainter.setFont(new Font("Stencil", Font.ITALIC, 12));
		txtPainter.setBounds(10, 182, 187, 31);
		txtPainter.setEditable(false);
		add(txtPainter);
		
		txtGuessWord = new JTextField();
		txtGuessWord.setText(DEFAULT_GUESS_WORD);
		txtGuessWord.setHorizontalAlignment(SwingConstants.CENTER);
		txtGuessWord.setFont(new Font("Stencil", Font.ITALIC, 12));
		txtGuessWord.setBackground(Color.WHITE);
		txtGuessWord.setBounds(10, 11, 187, 41);
		txtGuessWord.setEditable(false);
		add(txtGuessWord);
		

		lblTimer = new JLabel();
		lblTimer.setText("Timer\r\n");
		lblTimer.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimer.setFont(new Font("Arial Black", Font.ITALIC, 12));
		lblTimer.setBackground(Color.WHITE);
		lblTimer.setOpaque(true);
		lblTimer.setBounds(51, 63, 107, 56);
		add(lblTimer);
		
		initTimer();
	}
	
	/**
	 * Viene inizializzato il timer con il relativo task
	 */
	private void initTimer() {
		timer = new Timer(DELAY, new ActionListener() {		  
			  public void actionPerformed(ActionEvent e) {
				  if(seconds > 0) {
					  seconds -= 1;
					  lblTimer.setText(""+seconds);
				  }
				  if(seconds == 0)
					  fireValuesChange(new ChangeEvent(this));
				}	  
		});
	}
	
	
	/**
	 * Viene fatto partire il timer con il relativo task
	 * @param seconds
	 */
	public void startTimer(int seconds) {
		this.seconds = seconds;
		timer.start();
	}
	
	/**
	 * Viene fermato il timer
	 */
	public void stopTimer() {
		seconds = 0;
		lblTimer.setText(""+seconds);
		timer.stop();
		txtGuessWord.setText(DEFAULT_GUESS_WORD);
	}
	
	/**
	 * Restituisce l'oggetto JTextField 
	 * utilizzato per mostrare in che round si è durante il gioco
	 * RoundCorrente / RoundTotali
	 * @return
	 */
	public JTextField getTxtRound() {
		return this.txtRound;
	}
	
	/**
	 * Restituisce l'oggetto JTextField 
	 * utilizzato per mostrare all'utente il suo stato: PAINTER o GUESSER
	 * @return
	 */
	public JTextField getTxtPainter() {
		return this.txtPainter;
	}
	
	/**
	 * Restituisce l'oggetto JTextField 
	 * utilizzato per mostrare la parola che si bisogna indovinare (GUESSER) o disegnare (PAINTER)
	 * @return
	 */
	public JTextField getTxtGuessWord() {
		return this.txtGuessWord;
	}
	
	/**
	 * Restituisce l'oggetto JLabel
	 * utilizzato per mostrare il timer 
	 * @return
	 */
	public JLabel getlblTimer() {
		return this.lblTimer;
	}
	
}
