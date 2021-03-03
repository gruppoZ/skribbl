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
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ClientView {

	private JFrame frame;
	private JTextField txtWrite;
	private PaintArea paintArea;
	private PnlStrumenti pnlStrumenti;
	private ClientModel model;
	private JTextArea txtChat;
	private PnlTimer pnlTimer;
	private JButton btnStartGame;
	private JTextField txtRounds;
	private JTextField txtPainter;
	private String namePlayer;
	
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
		
		model.addChangeListener(e -> this.updateView());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setEnabled(false);
		frame.setBounds(100, 100, 1023, 694);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(894, 569, 89, 23);
		frame.getContentPane().add(btnSend);
		
		txtWrite = new JTextField();
		txtWrite.setBounds(707, 570, 177, 21);
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
		scrollBar.setBounds(705, 17, 278, 541);
		frame.getContentPane().add(scrollBar);
		
		paintArea = new PaintArea(model);
		paintArea.setBounds(10, 76, 447, 457);
		frame.getContentPane().add(paintArea);
		
		pnlStrumenti = new PnlStrumenti(model.getStrumenti());
		pnlStrumenti.setBounds(10, 21, 447, 44);
		frame.getContentPane().add(pnlStrumenti);
		
		pnlTimer = new PnlTimer();
		pnlTimer.setBounds(520, 21, 121, 44);
		frame.getContentPane().add(pnlTimer);
		
		btnStartGame = new JButton("Start!");
		btnStartGame.setBorderPainted(false);
		btnStartGame.setForeground(new Color(0, 0, 0));
		btnStartGame.setBorder(UIManager.getBorder("Button.border"));
		
		btnStartGame.setFont(new Font("Tempus Sans ITC", Font.BOLD | Font.ITALIC, 14));
		btnStartGame.setBackground(new Color(153, 255, 153));
		btnStartGame.setBounds(894, 621, 89, 23);
		frame.getContentPane().add(btnStartGame);
		
		JLabel lblRounds = new JLabel("Rounds");
		lblRounds.setHorizontalAlignment(SwingConstants.CENTER);
		lblRounds.setBounds(551, 130, 46, 14);
		frame.getContentPane().add(lblRounds);
		
		txtRounds = new JTextField();
		txtRounds.setHorizontalAlignment(SwingConstants.CENTER);
		txtRounds.setBackground(Color.WHITE);
		txtRounds.setEditable(false);
		txtRounds.setBounds(534, 150, 86, 20);
		frame.getContentPane().add(txtRounds);
		txtRounds.setColumns(10);
		
		txtRounds.setText("/");
		
		txtPainter = new JTextField();
		txtPainter.setHorizontalAlignment(SwingConstants.CENTER);
		txtPainter.setEditable(false);
		txtPainter.setColumns(10);
		txtPainter.setBackground(Color.WHITE);
		txtPainter.setBounds(486, 247, 187, 23);
		frame.getContentPane().add(txtPainter);
		
		setNickname();
		
		paintArea.addChangeListener(e -> model.sendMsg(paintArea.getCurrentLine()));
		pnlStrumenti.addActionListener(e -> paintArea.changePaint(e));
		btnSend.addActionListener(e -> this.send()); 
		txtWrite.addActionListener(e -> this.send());
		btnStartGame.addActionListener(e -> model.startGame());
	}
	
	protected PaintArea getPaintArea() {
		return this.paintArea;
	}
	
	/**
	 * Richiamato dal fireEvent di txtWrite o del btnSend. Invia quindi il messaggio al server mediante l'uso del model
	 */
	private void send() {
		model.sendMsg(txtWrite.getText());
		txtWrite.setText("");
	}
	
	//TODO Nel caso aggiungo un bottono "CHIUDI/ESCI"
	private void close() {
		model.close();
	}

	/**
	 * Inizializza il Nome del Player, comunicandolo al server mediante l'uso del Model
	 */
	private void setNickname() {
		String nickname = JOptionPane.showInputDialog(frame,"What is your name?", null);
		namePlayer = nickname;
		txtPainter.setText(nickname);
		model.sendNickName(nickname);
	}
	
	/**
	 * Richiamata nel caso di un fireEvent nel Model
	 * <p>Verifica i messaggi ricevuti dal server attraverso la ControllerCommand, e decide se aggiornare la chat, il paintArea o delegare
	 * le attività al ControllerCommand </p>
	 */
	private synchronized void updateView() {
		Object result = model.updateChat();
		ProcessCommand command;
		if(result != null && (result.getClass().equals(String.class))) {
			System.out.println("Result: " + result.getClass() + ".  ---- String: " + String.class);
			String msg = String.valueOf(result);
			
			command = new ProcessCommand();
			if(!command.isCommand(msg)) {
				System.out.println("length: " + msg.length());
				//txtChat.append(command.getMsgChat(msg));  
				txtChat.append(msg);
				txtChat.setCaretPosition(txtChat.getDocument().getLength());
			} else
				command.process(this, msg);
		}

		if(result != null && (result.getClass().equals(PolyLine.class))) {
			System.out.println("Result: " + result.getClass() + ".  ---- Polyline: " + PolyLine.class);
			paintArea.setLines((PolyLine) result);
		}
	}
	
	/**
	 * Resetta e Inizia il TImer
	 */
	protected void startTimer() {
		pnlTimer.startTimer();
		//TODO fare una migliore grafica
	
		if(paintArea.getIsPainter()) {
			pnlStrumenti.setVisible(true);
			txtPainter.setText(namePlayer + ": Sei il painter");
			txtPainter.setBackground(Color.GREEN);
		} else {
			pnlStrumenti.setVisible(false);
			txtPainter.setText(namePlayer + ": Non sei il painter");
			txtPainter.setBackground(Color.RED);
		}
	}
	
	/**
	 * Ferma il timer
	 */
	protected void stopTimer() {
		pnlTimer.stopTimer();
	}
	
	protected void setRounds(String rounds) {
		txtRounds.setText(rounds);
	}
}
