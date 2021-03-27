package it.unibs.pajc.client.panel;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import it.unibs.pajc.client.ClientModel;

public class PnlStrumenti extends PnlBase {

	private static final int BTN_SIZE = 30;
	
	/**
	 * Create the panel.
	 */
	public PnlStrumenti(ArrayList<Object> tools) {
		super();
		updateTools(tools);
	}
	
	/**
	 * Viene creato il pannello dei tools necessari per poter disegnare
	 * @param tools
	 */
	private void updateTools(List<Object> tools) {
		this.removeAll();
		
		for(int i = 0; i < tools.size(); i++) {
			if(tools.get(i) instanceof String)
				addButtonColor((String) tools.get(i));
			if(tools.get(i) instanceof Icon)
				addButtonIcon((Icon) tools.get(i));
		}
		
		this.revalidate(); //forza la ridistribuzione dei componenti all'interno del container
		this.repaint();
	}
	
	/**
	 * Viene aggiunto un JButton colorato in base al parametro passato
	 * @param colore
	 */
	private void addButtonColor(String colore) {
		JButton btn = new JButton(); 
		btn.setPreferredSize(new Dimension(BTN_SIZE, BTN_SIZE));
		btn.setBackground(ClientModel.getColorByName(colore));

		btn.setActionCommand(colore);
		btn.addActionListener(e -> fireActionListener(e));
		
		this.add(btn);
	}
	
	/**
	 * Viene aggiunto un JButton con un'icona presa come parametro
	 * @param icon
	 */
	private void addButtonIcon(Icon icon) {
		JButton btn = new JButton(icon);
		btn.setPreferredSize(new Dimension(BTN_SIZE, BTN_SIZE));
		btn.setBackground(Color.WHITE);
		
		btn.setActionCommand(icon.toString());
		btn.addActionListener(e -> fireActionListener(e));
		
		this.add(btn);
	}
}
