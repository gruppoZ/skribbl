package it.unibs.pajc.client;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PnlStrumenti extends PnlBase {

	/**
	 * Create the panel.
	 */
	public PnlStrumenti(List<Object> strumenti) {
		super();
		updateOperators(strumenti);
	}
	
	public void updateOperators(List<Object> strumenti) {
		this.removeAll();
		for(int i = 0; i < strumenti.size(); i++) {
			if(strumenti.get(i) instanceof Color)
				addButtonColor((Color) strumenti.get(i));
			if(strumenti.get(i) instanceof Icon)
				addButtonIcon((Icon) strumenti.get(i));
		}
		
		this.revalidate();//forza la ridistribuzione dei componenti all'interno del container
		this.repaint();
	}
	
	public void addButtonColor(Color colore) {
		JButton btn = new JButton();
		btn.setPreferredSize(new Dimension(30, 30));
		btn.setBackground(colore);
		
		this.add(btn);
		btn.addActionListener(e -> fireActionListener(e));
	}
	public void addButtonIcon(Icon icon) {
		JButton btn = new JButton(icon);
		btn.setPreferredSize(new Dimension(30, 30));
		btn.setBackground(Color.WHITE);
		
		this.add(btn);
		btn.addActionListener(e -> fireActionListener(e));
	}
}
