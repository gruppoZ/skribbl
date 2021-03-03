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
	
	protected void updateOperators(List<Object> strumenti) {
		this.removeAll();
		for(int i = 0; i < strumenti.size(); i++) {
			if(strumenti.get(i) instanceof String)
				addButtonColor((String) strumenti.get(i));
			if(strumenti.get(i) instanceof Icon)
				addButtonIcon((Icon) strumenti.get(i));
		}
		
		this.revalidate(); //forza la ridistribuzione dei componenti all'interno del container
		this.repaint();
	}
	
	protected void addButtonColor(String colore) {
		JButton btn = new JButton(); 
		btn.setPreferredSize(new Dimension(30, 30));
		btn.setBackground(ClientModel.getColorByName(colore));

		btn.setActionCommand(colore);
		this.add(btn);
		btn.addActionListener(e -> fireActionListener(e));
	}
	
	protected void addButtonIcon(Icon icon) {
		JButton btn = new JButton(icon);
		btn.setPreferredSize(new Dimension(30, 30));
		btn.setBackground(Color.WHITE);
		
		btn.setActionCommand(icon.toString());
		this.add(btn);
		btn.addActionListener(e -> fireActionListener(e));
	}
}
