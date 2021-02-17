package MainDesign;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Chat extends JPanel {
	private JTextField textMsg;

	/**
	 * Create the panel.
	 */
	public Chat() {
		setLayout(null);
		
		JButton btnSend = new JButton(">");
		
		
		btnSend.setBounds(409, 261, 41, 29);
		add(btnSend);
		
		textMsg = new JTextField();
		textMsg.setBounds(10, 258, 399, 35);
		add(textMsg);
		textMsg.setColumns(10);
		
		JTextArea textChat = new JTextArea();
		textChat.setBounds(10, 11, 430, 236);
		add(textChat);
		
		/*
		 * Prendo il testo presente textMsg, inserisco in ArrayStringhe e visualizzo (es repaint) nel textChat
		 * nomeUtente: testo 			//nomeUtente usare font diverso (es. Grassetto) 
		 */
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

	}
}
