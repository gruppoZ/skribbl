package it.unibs.pajc.client.panel;

import javax.swing.*;
import java.awt.*;

public class PnlWords extends PnlBase {

	private String[] words;
	
	/**
	 * Create the panel.
	 */
	public PnlWords() {
		setBackground(Color.WHITE);
	}
	
	/**
	 * inizializza le parole che verranno mostrati come buttoni dal PnlWords
	 * @param words
	 */
	public void setWords(String[] words) {
		this.words = words;
		updateView();
	}
	
	/**
	 * Aggiorna la view, mostrando le parole nuove se presenti
	 */
	public void updateView() {
		this.removeAll();
		
		for(int i = 0; i < words.length; i++) {
			addButton(words[i]);
		}
		
		this.revalidate(); //forza la ridistribuzione dei componenti all'interno del container
		this.repaint();
	}
	
	/**
	 * Aggiunge un JButton con String word, settando manualmente actionCommand
	 * @param word
	 */
	private void addButton(String word) {
		JButton btn = new JButton(word); 
		btn.setActionCommand(word);
		btn.addActionListener(e -> fireActionListener(e));
		
		add(btn);
	}
	
	/**
	 * Elimina tutti i bottoni di PnlWords e quindi tutte le parole che erano presenti
	 * Viene fatto infine un repaint
	 */
	public void cancelBtn() {
		this.removeAll();
		this.repaint();
	}
}
