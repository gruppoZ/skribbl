package MainDesign;

import java.awt.EventQueue;

import javax.swing.JFrame;

import it.unibs.pajc.client.Client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
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
	public Main() {
		initialize();
	}

	
	Chat chat;
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 690, 509);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		chat = new Chat();
		chat.setBounds(281, 0, 393, 459);
		frame.getContentPane().add(chat);
		chat.setLayout(null);
		
		JButton btnStartGame = new JButton("Start");
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startClient();
			}
		});
		btnStartGame.setBounds(76, 205, 89, 23);
		frame.getContentPane().add(btnStartGame);
		
		//chat.addActionListener(e -> this.startClient());
		
	}
	
	public void startClient() {
		Client client = new Client();
		client.start();
		client.setRequest(chat.getMsg()); 
	}	
}
