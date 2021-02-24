package it.unibs.pajc.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;

import it.unibs.pajc.core.BaseModel;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.DropMode;

public class ClientView {

	private JFrame frame;
	private JTextField txtWrite;

	private ClientModel model;
	private JTextArea txtChat;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientView window = new ClientView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * Controller App Client
	 */
	public ClientView() {
		model = new ClientModel();
		
		initialize();
		
		model.addChangeListener(e -> this.updateChat());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(335, 227, 89, 23);
		frame.getContentPane().add(btnSend);
		
		txtWrite = new JTextField();
		txtWrite.setBounds(235, 228, 86, 20);
		frame.getContentPane().add(txtWrite);
		txtWrite.setColumns(10);
		
		txtChat = new JTextArea();
		txtChat.setLineWrap(true);
		txtChat.setWrapStyleWord(true);
		txtChat.setEditable(false);
		txtChat.setBounds(224, 11, 177, 163);
		frame.getContentPane().add(txtChat);
		
		
		
		JScrollPane scrollBar = new JScrollPane(txtChat);
		scrollBar.setAutoscrolls(true);
		scrollBar.setBounds(268, 11, 160, 163);
		frame.getContentPane().add(scrollBar);
		
		setNickname();
		
		btnSend.addActionListener(e -> this.send());
		
	}
	
	private void send() {
		model.sendMsg(txtWrite.getText());
		txtWrite.setText("");
	}
	
	private void close() {
		model.close();
	}

	private void setNickname() {
		String nickname = JOptionPane.showInputDialog(frame,"What is your name?", null);
		model.sendMsg(nickname);
	}
	
	private void updateChat() {
		txtChat.append(model.updateChat());
		txtChat.setCaretPosition(txtChat.getDocument().getLength());
	}
	
}
