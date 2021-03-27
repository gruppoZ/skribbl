package it.unibs.pajc.client.panel;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;

public class PnlWords extends PnlBase {

	private String[] words;
	
	/**
	 * Create the panel.
	 */
	public PnlWords() {
		setBackground(Color.WHITE);

	}

	public void setWords(String[] words) {
		this.words = words;
		updateView();
	}
	
	public void updateView() {
		this.removeAll();
		for(int i = 0; i < words.length; i++) {
			addButton(words[i]);
		}
		
		this.revalidate(); //forza la ridistribuzione dei componenti all'interno del container
		this.repaint();
	}
	
	private void addButton(String word) {
		JButton btn = new JButton(word); 
		btn.setActionCommand(word);
		this.add(btn);
		btn.addActionListener(e -> fireActionListener(e));
	}
	
	/**
	 * Elimina tutti i bottoni di PnlWords e quindi tutte le parole che erano presenti
	 */
	public void cancelBtn() {
		this.removeAll();
		this.repaint();
	}
}
