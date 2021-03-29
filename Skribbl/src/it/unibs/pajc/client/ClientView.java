package it.unibs.pajc.client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;

import it.unibs.pajc.client.panel.*;
import it.unibs.pajc.core.ProcessUtils;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class ClientView {
	
	private static final String USER_LIST = "User List";
	private static final String START_GAME = "Start Game";
	private static final String ATTEMPTING_CONNESSION = "Connessione al Server in corso...";
	private static final String SUCCESS_CONNECIION = "Connessione al Server avvenuta...";
	private static final String ASK_NAME = "What is your name?";
	private static final String ALONE_MSG = "Sei da solo";
	private static final String LEFT_GAME =  "Sei uscito";
	private static final String YOU_CLIENT_LIST = " (You)";
	private static final String STRING_EMPTY = "";
	private static final String REGEX_NICKNAME = "(?s).*[^a-zA-z0-9 ].*";
	
	public String TXT_ADD_PLAYER = "#%s %s:%s\n";
	
	private JFrame frame;
	private JFrame frameLobby;

	private ClientModel model;
	
	//LOBBY
	private JTextPane txtClientList;
	private JButton btnStartGameLobby;
	private JProgressBar pgsBar;
	
	private PnlPaintArea pnlPaintArea;
	private PnlStrumenti pnlStrumenti;
	private PnlWords pnlWords;
	
	private JTextPane txtScoreBoard;
	private ScoreboardView scoreboardView;
	private PnlDatiPartita pnlDatiPartita;
	private PnlChat pnlChat;
	
	private String nickname;
	
	/**
	 * Main della classe ClientView. Inizializza la FrameLobby e crea la ClientView.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientView window = new ClientView();
//					window.frame.setVisible(false);
					window.frameLobby.setVisible(true);
					window.frameLobby.setLocationRelativeTo(null);
				} catch (NullPointerException e) {
					JOptionPane.showMessageDialog(null, LEFT_GAME);
					System.exit(0);
				}
				catch (Exception e) {
	//					e.printStackTrace();
					System.out.println("Client view run");
				}
			}
		});
	}

	/**
	 * Costruttore della classe ClientView. Inizialmente visualizza la lobby e attende la connessione al server. Aggiunge actionlistener al model
	 * 
	 */
	public ClientView() {
		lobby();
		
		model = new ClientModel();
		model.addActionListener(e -> this.checkEvent(e));
	}
	
	/**
	 * Distingue fra gli eventi che provengono dal model, ergo il Listener (messaggi provenienti dal server) 
	 * o Writer (messaggi che il Client desidera inviare al server)
	 * @param e L'evento e che arriva dal model
	 */
	private void checkEvent(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase(ClientModel.LISTENER))
			this.update();
		if(e.getActionCommand().equalsIgnoreCase(ClientModel.WRITER)) {
			pgsBar.setIndeterminate(false);
			pgsBar.setString(SUCCESS_CONNECIION);
			pgsBar.setBackground(Color.GREEN);
			btnStartGameLobby.setEnabled(true);
			pnlChat.getBtnSend().setEnabled(true);
			pnlChat.getTxtMsg().setEditable(true);
			getNickname();
			setNickname();
		}
			
	}
	
	/**
	 * Inizializza la ScoreboardView - la classifica finale
	 */
	protected void initScoreboardView() {
		// TODO Auto-generated method stub
		scoreboardView = new ScoreboardView();
		scoreboardView.setVisible(true);
	}
	/**
	 * Metodo che mostra la Lobby prima dell'inizio della partita. Contiene una chat e un bottone "Start Game"
	 */
	private void lobby() {
		
		if(frame != null)
			frame.setVisible(false);
	
		frameLobby = new JFrame();
		frameLobby.setBounds(100, 100, 1065, 722);
		frameLobby.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameLobby.getContentPane().setLayout(null);
		frameLobby.setResizable(false);
		
		frameLobby.getContentPane().add(addPnlChat());
		enableChat();
		
		txtClientList = new JTextPane();
		txtClientList.setEditable(false);
		txtClientList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtClientList.setBounds(640, 230, 134, 375);
		frameLobby.getContentPane().add(txtClientList);
		
		btnStartGameLobby = new JButton(START_GAME);
		btnStartGameLobby.setBounds(655, 631, 119, 41);
		btnStartGameLobby.setBorderPainted(false);
		btnStartGameLobby.setForeground(new Color(0, 0, 0));
		btnStartGameLobby.setBorder(UIManager.getBorder("Button.border"));
		
		Icon iconLogo = new ImageIcon(ClientModel.LOGO);
		JLabel lblLogo = new JLabel(iconLogo);
		lblLogo.setBounds(10, 11, 626, 208);
		frameLobby.getContentPane().add(lblLogo);
		
		btnStartGameLobby.setFont(new Font("Tempus Sans ITC", Font.BOLD | Font.ITALIC, 14));
		btnStartGameLobby.setBackground(new Color(153, 255, 153));
		frameLobby.getContentPane().add(btnStartGameLobby);
		
		JLabel lblBackground = new JLabel(STRING_EMPTY);
		lblBackground.setBackground(Color.WHITE);
		lblBackground.setIcon(new ImageIcon(ClientModel.BACKGROUND_GIF));
		lblBackground.setBounds(63, 230, 564, 375);
		frameLobby.getContentPane().add(lblBackground);
		
		pgsBar = new JProgressBar();
		pgsBar.setIndeterminate(true);
		pgsBar.setBounds(63, 634, 242, 22);
		pgsBar.setStringPainted(true);
		pgsBar.setString(ATTEMPTING_CONNESSION);
		
		frameLobby.getContentPane().add(pgsBar);
		
		frameLobby.addWindowListener(new WindowListener() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				JOptionPane.showMessageDialog(null, LEFT_GAME);
			}
			
			@Override
			public void windowOpened(WindowEvent e){}
			@Override
			public void windowIconified(WindowEvent e){}			
			@Override
			public void windowDeiconified(WindowEvent e){}
			@Override
			public void windowDeactivated(WindowEvent e){}		
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});

		//Verranno riattivati quando avverra' la connessione al Server
		btnStartGameLobby.setEnabled(false);
		pnlChat.getBtnSend().setEnabled(false);
		pnlChat.getTxtMsg().setEditable(false);	
		
		JLabel lblUserList = new JLabel(USER_LIST);
		lblUserList.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserList.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblUserList.setBounds(640, 179, 134, 47);
		lblUserList.setBackground(Color.WHITE);
		lblUserList.setOpaque(true);
		
		frameLobby.getContentPane().add(lblUserList);
		this.addListenerChat();
		
		btnStartGameLobby.addActionListener(e -> this.startGame());
	}

	/**
	 * Metodo che inizializza i contenuti della View del Match
	 */
	private void initialize() {
		frameLobby.setVisible(false);

		frame = new JFrame();
		frame.setBounds(100, 100, 1057, 771);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		frame.getContentPane().add(addPnlChat());
		enableChat();
		
		pnlPaintArea = new PnlPaintArea(model);
		pnlPaintArea.setBounds(236, 132, 538, 555);
		frame.getContentPane().add(pnlPaintArea);
		pnlPaintArea.setLayout(null);
		
		pnlStrumenti = new PnlStrumenti(model.getStrumenti());
		pnlStrumenti.setBounds(236, 94, 538, 38);
		frame.getContentPane().add(pnlStrumenti);
		pnlStrumenti.setBackground(Color.WHITE);
		pnlStrumenti.setBorder(new LineBorder(Color.BLACK));

		pnlWords = new PnlWords();
		pnlWords.setBounds(236, 687, 538, 44);
		frame.getContentPane().add(pnlWords);
		pnlWords.setBorder(new LineBorder(Color.BLACK));
		
		txtScoreBoard = new JTextPane();
		txtScoreBoard.setEditable(false);
		txtScoreBoard.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtScoreBoard.setBounds(10, 167, 185, 301);
		frame.getContentPane().add(txtScoreBoard);
		
		pnlDatiPartita = new PnlDatiPartita();
		pnlDatiPartita.getTxtGuessWord().setFont(new Font("Arial Black", Font.ITALIC, 12));
		pnlDatiPartita.getTxtGuessWord().setLocation(364, 21);
		pnlDatiPartita.setBounds(10, 11, 754, 82);
		frame.getContentPane().add(pnlDatiPartita);
		
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				JOptionPane.showMessageDialog(null, LEFT_GAME);
			}
			
			@Override
			public void windowOpened(WindowEvent e){}
			@Override
			public void windowIconified(WindowEvent e){}			
			@Override
			public void windowDeiconified(WindowEvent e){}
			@Override
			public void windowDeactivated(WindowEvent e){}		
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		pnlDatiPartita.addChangeListener(e -> this.stopTimer());

		this.addListenerChat();
		pnlStrumenti.addActionListener(e -> pnlPaintArea.changePaint(e));
		pnlWords.addActionListener(e -> this.sendSelectedWord(e.getActionCommand()));
		
	}
	
	private void enableChat() {
		pnlChat.getTxtMsg().setEditable(true);
		pnlChat.getBtnSend().setEnabled(true);
	}
	private JPanel addPnlChat() {
		pnlChat = new PnlChat();
		pnlChat.getBtnSend().setLocation(220, 640);
		pnlChat.getTxtMsg().setLocation(10, 640);
		pnlChat.getTxtChat().setBounds(11, 14, 240, 560);
		pnlChat.setBounds(784, 11, 267, 673);
		return pnlChat;
	}
	
	private void addListenerChat() {
		pnlChat.getTxtMsg().addActionListener(e -> this.send());
		pnlChat.getBtnSend().addActionListener(e -> this.send());
	}
	
	private void send() {
		model.sendMsg(pnlChat.getTxtMsg().getText());
		pnlChat.getTxtMsg().setText(STRING_EMPTY);
	}
	
	/**
	 * Metodo che richiede il "nickname" all'utente. Utilizzo di regex per vietare nomi contenenti caratteri speciali (",=!? ecc..)
	 */
	protected void getNickname() {
		
		do {
			nickname = JOptionPane.showInputDialog(frame, ASK_NAME, null);
			if(nickname == null) {
				JOptionPane.showMessageDialog(null, LEFT_GAME);
				System.exit(0);	
			}
			
		} while(nickname.matches(REGEX_NICKNAME) || nickname.trim().equals(STRING_EMPTY));
		
	}
	
	private void setNickname() {
		if(nickname != null) {
			model.setNickname(nickname.trim());
			model.sendMsg(nickname);
		}
	}
	
	/**
	 * Metodo che differenzia gli Object in arrivo fra String (messaggi di sistema o messaggi fra client) e WhiteBoardLine 
	 */
	private void update() {	
		Object response = model.update();	
		
		if((response.getClass().equals(String.class)) && (response != null)) {
			
			int indexOf = response.toString().indexOf(":");
			String messageType = response.toString().substring(0, indexOf + 1);
			ProcessMessageClient processor = model.getProcess(messageType);
			
			if(processor != null) 
				processor.process(this, response.toString().substring(indexOf + 1));
			 else 
				appendToPane(pnlChat.getTxtChat(), response.toString(), Color.BLACK);			
		}
		
		if((response.getClass().equals(WhiteBoardLine.class)) && (response != null)) {
			pnlPaintArea.updateWhiteBoard((WhiteBoardLine)response);
		}	
	}
	
	/**
	 * Avvia il timer lato client per un determinato numero di secondi, che arriva come dato dal server
	 * @param seconds Durata del timer
	 */
	public void startTimer(String seconds) {
		pnlDatiPartita.startTimer(Integer.valueOf(seconds));
		managePnlStrumenti();
	}
	
	private void managePnlStrumenti() {
		if(pnlPaintArea.isPainter()) 
			pnlStrumenti.setVisible(true);
		 else 
			pnlStrumenti.setVisible(false);	
	}
	
	public void stopTimer() {
		pnlDatiPartita.stopTimer();
		managePnlStrumenti();
	}
	
	protected void setWordWithHint(String word) {
		if(!pnlPaintArea.isPainter())	
			pnlDatiPartita.getTxtGuessWord().setText(word);
	}
	
	protected void setSelectedWord(String word) {
		pnlDatiPartita.getTxtGuessWord().setText(word);
	}
	
	public void setPainter() {
		pnlPaintArea.setPainter();
	}
	
	public void clearAll() {
		pnlPaintArea.clearAll();
	}
	
	private void startGame() {
		try {
	        Clip clip = AudioSystem.getClip();
	        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("src/sounds/complete.wav"));
	        clip.open(inputStream);
	        clip.start(); 
	      } catch (Exception e) {
	        System.err.println(e.getMessage());
	      }
		model.sendMsg(ProcessUtils.COMMAND_KEY + ProcessUtils.MATCH_STARTED);
	}
	
	/**
	 * Viene chiamato se il client entra durante una partita in corso.
	 * Chiama initialize() e nasconde il bottone "StartGame"
	 */
	protected void matchStarted() {

		if(frameLobby.isVisible()) {
			initialize();
		}
		managePnlStrumenti();
		frame.repaint();
	}
	
	protected void matchCancelled() {
		JOptionPane.showMessageDialog(null, ALONE_MSG);
	}
	
	/**
	 * resetta i componenti di initialize() e fa tornare tutti alla lobby
	 */
	protected void matchFinished() {
		this.setRound("0", "0");
		pnlChat.getTxtChat().setText(STRING_EMPTY);
		pnlDatiPartita.stopTimer();
		txtScoreBoard.setText(STRING_EMPTY);
		hidePnlWords();
		if(pnlPaintArea.isPainter()) 
			pnlPaintArea.setPainter();
		
		frame.setVisible(false);
		frameLobby.setVisible(true);
	}
	
	protected void setRound(String currentRound, String totRound) {
		pnlDatiPartita.getTxtRound().setText(currentRound + "/" + totRound);
	}
	
	protected void sendSelectedWord(String word) {
		model.sendMsg(ProcessUtils.SERVER_WORD_KEY + word);
	}
	
	protected void setWords(String[] words) {
		pnlWords.setWords(words);
	}
	
	protected void hidePnlWords() {
		pnlWords.cancelBtn();
	}
	
	protected void resetScoreBoard() {
		txtScoreBoard.setText(STRING_EMPTY);
	}
	
	protected void resetClientList() {
		txtClientList.setText(STRING_EMPTY);
	}
	
	/**
	 * Popola la scoreboard con le informazioni relative ai player (score, colore) colorando il nome del painter di rosso
	 * @param name
	 * @param score
	 * @param isPainter
	 * @param position
	 */
	protected void setScoreBoard(String name, String score, boolean isPainter, String position) {
		Color c;
		if(isPainter)
			c = Color.RED;
		else
			c = Color.BLACK;
		
		if(model.getNickname().equals(name))
			name += YOU_CLIENT_LIST;
		

		appendToPane(txtScoreBoard, String.format(TXT_ADD_PLAYER, position, name, score), c);
	}
	
	protected void setTxtChat(String msg, Color c) {
		appendToPane(pnlChat.getTxtChat(), msg, c);
	}
	
	protected void updateClientList(String name) {
		if(model.getNickname().equals(name))
			name += YOU_CLIENT_LIST;
		appendToPane(txtClientList, name + "\n", Color.BLACK);
	}
	
	protected void matchAlreadyOn() {
		initialize();
		matchStarted();
	}
	
	private static void appendToPane(JTextPane tp, String txt, Color clr) {
		tp.setEditable(true);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, clr);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Serif");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(txt);
        tp.setEditable(false);
    }

	/**
	 * Aggiunge i player nella scoreboard di fine partita
	 * @param name
	 * @param score
	 * @param position
	 */
	protected void popupScoreboard(String name, String score, String position) {
		scoreboardView.addPlayer(TXT_ADD_PLAYER, name, score, position);
	}
}
