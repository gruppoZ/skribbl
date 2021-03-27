package it.unibs.pajc.client.panel;

import java.awt.*;
import javax.swing.*;

import it.unibs.pajc.client.ClientModel;

public class PnlChat extends PnlBase {

	private JButton btnSend;
	private JTextField txtMsg;
	private JTextPane txtChat;
	
	/**
	 * Create the panel.
	 */
	public PnlChat() {
		super();
		setLayout(null);
		
		Icon iconSend = new ImageIcon(ClientModel.ICON_SEND);
		
		btnSend = new JButton(iconSend);
		btnSend.setBackground(Color.WHITE);
		btnSend.setBounds(220, 545, 33, 23);
		add(btnSend);
		
		JScrollPane scrollPane = new JScrollPane((Component) null);
		scrollPane.setBounds(10, 13, 242, 521);
		add(scrollPane);
		
		txtChat = new JTextPane();
		txtChat.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtChat.setEditable(false);
		scrollPane.setViewportView(txtChat);
		
		txtMsg = new JTextField();
		txtMsg.setBackground(Color.WHITE);
		txtMsg.setEditable(false);
		txtMsg.setColumns(10);
		txtMsg.setBounds(10, 545, 200, 23);
		add(txtMsg);
	}
	
	/**
	 * Restituisce l'oggetto JButton 
	 * utilizzato per l'invio dei messaggi presenti in txtMsg
	 * @return
	 */
	public JButton getBtnSend() {
		return this.btnSend;
	}
	
	/**
	 * Restituisce l'oggetto JTextField 
	 * utilizzato per l'input dei messaggi che si vogliono inviare
	 * @return
	 */
	public JTextField getTxtMsg() {
		return this.txtMsg;
	}
	
	/**
	 * Restituisce l'oggetto JTextPane 
	 * utilizzato per l'output dei messaggi della Chat che si inviano e ricevono
	 * @return
	 */
	public JTextPane getTxtChat() {
		return this.txtChat;
	}
	
}
