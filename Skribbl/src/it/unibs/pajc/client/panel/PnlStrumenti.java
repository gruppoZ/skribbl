package it.unibs.pajc.client.panel;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.util.ArrayList;
import java.util.List;

import it.unibs.pajc.client.ClientModel;

public class PnlStrumenti extends PnlBase {

	private static final int BTN_SIZE = 30;
	private ArrayList<JButton> btnColorList;
	private ArrayList<JButton> btnIconList;
	
	/**
	 * Create the panel.
	 */
	public PnlStrumenti(ArrayList<Object> tools) {
		super();
		btnColorList = new ArrayList<JButton>();
		btnIconList = new ArrayList<JButton>();
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
		
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
		    
		    public void mouseClicked(java.awt.event.MouseEvent e) {
		    	btnColorList.forEach((btn) -> {
		    		btn.setBorder(new LineBorder(UIManager.getColor("control")));
		    	});
		    	btn.setBorder(new LineBorder(Color.BLUE, 2));
		    }
		});
		
		if(btn.getActionCommand().equals("BLACK"))
			btn.setBorder(new LineBorder(Color.BLUE, 2));
		
		btnColorList.add(btn);
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
		
		
		btn.addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseEntered(java.awt.event.MouseEvent evt) {
				if(ClientModel.isTrash(btn.getActionCommand()) || ClientModel.isSave(btn.getActionCommand()))
					btn.setBorder(new LineBorder(Color.BLUE, 2));
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	if(ClientModel.isTrash(btn.getActionCommand()) || ClientModel.isSave(btn.getActionCommand()))
		    		btn.setBorder(new LineBorder(UIManager.getColor("control")));
		    }
		    
			public void mouseClicked(java.awt.event.MouseEvent e) {
				
				btnIconList.forEach((btn) -> {
		    		btn.setBorder(new LineBorder(UIManager.getColor("control")));
		    	});
				btn.setBorder(new LineBorder(Color.BLUE, 2));
		    }
		});
		
		btnIconList.add(btn);
		this.add(btn);
	}
}
