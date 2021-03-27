package it.unibs.pajc.client.panel;	

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PnlBase extends JPanel {

	/**
	 * Create the panel.
	 */
	public PnlBase() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
	}

	private ArrayList<ActionListener> listenerList = new ArrayList<ActionListener>();
	
	public void addActionListener(ActionListener l) {
		listenerList.add(l);
	}
	
	public void removeActionListener(ActionListener l) {
		listenerList.remove(l);
	}
	
	public void fireActionListener(ActionEvent e) {
		/**
		 * non gli passo e ma gli passo un evento che che voglio io
		 * per nascondere i bottoni all'esterno
		 * Sto: Isolando il mio sistema
		 */
		ActionEvent myEvent = new ActionEvent(this, 
				ActionEvent.ACTION_PERFORMED,
				e.getActionCommand(),
				e.getWhen(),
				e.getModifiers()//se quando premo con il mouse ho anche schiacciato ctrl questo è un modifiers
		);
		
		
		for (ActionListener actionListener : listenerList) {
			actionListener.actionPerformed(myEvent);
		}
	}
	
	public void addButton(Object symbol) {
		JButton btn = new JButton((String) symbol);
		btn.setPreferredSize(new Dimension(50, 50));
		this.add(btn);
		
		btn.addActionListener(e -> fireActionListener(e));
	}
}
