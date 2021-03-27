package it.unibs.pajc.client.panel;

import java.awt.*;
import javax.swing.*;

public class PnlChat extends PnlBase {

	private JButton btnSend;
	private JTextField txtMsg;
	private JTextPane txtChat;
	
	/**
	 * Create the panel.
	 */
	public PnlChat() {
		super();
		
		Icon iconSend = new ImageIcon("src/img/right-arrow-20.png");
		setLayout(null);
		btnSend = new JButton(iconSend);//e' creato due volte forse si potrebbe dichiarare solo da una parte
		btnSend.setBackground(Color.WHITE);
		btnSend.setBounds(220, 545, 33, 23);
		this.add(btnSend);
		
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
	
	public JButton getBtnSend() {
		return this.btnSend;
	}
	
	public JTextField getTxtMsg() {
		return this.txtMsg;
	}
	
	public JTextPane getTxtChat() {
		return this.txtChat;
	}
	
}
