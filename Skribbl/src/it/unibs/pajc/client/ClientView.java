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
import javax.swing.JEditorPane;
import java.util.List;

public class ClientView {

	private JFrame frame;
	private JTextField txtWrite;
	private PaintArea paintArea;
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
	 */
	public ClientView() {
		model = new ClientModel();
		initialize();
		
		//model.getComunicator().addChangeListener(e -> this.updateChat());
		model.addChangeListener(e -> this.updateChat());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 700, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(585, 477, 89, 23);
		frame.getContentPane().add(btnSend);
		
		txtWrite = new JTextField();
		txtWrite.setBounds(396, 477, 177, 21);
		frame.getContentPane().add(txtWrite);
		txtWrite.setColumns(10);
		
		txtChat = new JTextArea();
		txtChat.setEditable(false);
		txtChat.setLineWrap(true);
		txtChat.setWrapStyleWord(true);
		txtChat.setBounds(110, 0, 177, 163);
		frame.getContentPane().add(txtChat);		
		
		JScrollPane scrollBar = new JScrollPane(txtChat);
		scrollBar.setAutoscrolls(true);
		scrollBar.setBounds(396, 11, 278, 449);
		frame.getContentPane().add(scrollBar);
		
		paintArea = new PaintArea(model);
		paintArea.setBounds(10, 76, 376, 424);
		frame.getContentPane().add(paintArea);
		
		PnlStrumenti pnlStrumenti = new PnlStrumenti(model.getStrumenti());
		pnlStrumenti.setBounds(10, 21, 376, 46);
		frame.getContentPane().add(pnlStrumenti);
		
		setNickname();
		
		//paintArea.addActionListener(e -> model.sendPaint(paintArea.getLines()));
		//paintArea.addChangeListener(e -> model.sendPaint(paintArea.getLines()));
		paintArea.addChangeListener(e -> model.sendMsg(paintArea.getCurrentLine()));
		pnlStrumenti.addActionListener(e -> paintArea.changePaint(e));
		btnSend.addActionListener(e -> this.send()); 
		txtWrite.addActionListener(e -> this.send());
	}
	
	private void send() {
		model.sendMsg(txtWrite.getText());
		txtWrite.setText("");
	}
	
	//Nel caso aggiungo un bottono "CHIUDI/ESCI"
	private void close() {
		model.close();
	}

	private void setNickname() {
		String nickname = JOptionPane.showInputDialog(frame,"What is your name?", null);
		model.sendMsg(nickname);
	}
	
	private synchronized void updateChat() {
		//txtChat.append(model.getComunicator().updateChat());
		Object result = model.updateChat();
		if(result != null && (result.getClass().equals(String.class))) {
			System.out.println("Result: " + result.getClass() + ".  ---- String: " + String.class);
			String msg = String.valueOf(result);
			System.out.println("length: " + msg.length());
			txtChat.append(msg);
			txtChat.setCaretPosition(txtChat.getDocument().getLength());
		}

		if(result != null && (result.getClass().equals(PolyLine.class))) {
			System.out.println("Result: " + result.getClass() + ".  ---- Polyline: " + PolyLine.class);
			paintArea.setLines((PolyLine) result);
		}
	}
}
