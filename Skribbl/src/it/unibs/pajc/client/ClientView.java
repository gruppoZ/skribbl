package it.unibs.pajc.client;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.DropMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JScrollBar;

public class ClientView {

	private JFrame frame;
	private JFrame frameLobby;

	private ClientModel model;
	
	//LOBBY
	private JTextPane txtClientList;
	private JButton btnStartGameLobby;
	private JButton btnSend;
	
	//TODO: cambiare paintArea in pnlPaintArea
	private PnlPaintArea paintArea;
	private PnlStrumenti pnlStrumenti;
	private PnlWords pnlWords;
	
	private JTextField txtWrite;
	private JTextPane txtScoreBoard;
	private JTextPane txtChat;
	private JScrollPane scrollPane;
	private ScoreboardView scoreboardView;
	private JButton btnStartGame;
	private PnlDatiPartita pnlDatiPartita;
	
	private String nickname;
	private JTextField txtStatusServer;
	/**
	 * Launch the application.
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
					JOptionPane.showMessageDialog(null, "Sei uscito");
					System.exit(0);
				}
				catch (Exception e) {
	//					e.printStackTrace();
					System.out.println("client view run");
				}
			}
		});
	}

	/**
	 * Create the application.
	 * Controller App Client
	 */
	public ClientView() {
		lobby(); //TODO disabilitare bottoni finchè non avviene effetivamente la connessione
		txtStatusServer.setText("Connessione al Server in corso...");
		
		model = new ClientModel();
		model.addActionListener(e -> this.checkEvent(e));
	}

	private void checkEvent(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase("Listener"))
			this.update();
		if(e.getActionCommand().equalsIgnoreCase("Writer")) {
			txtStatusServer.setText("Connessione al Server avvenuta...");
			btnStartGameLobby.setEnabled(true);
			btnSend.setEnabled(true);
			txtWrite.setEditable(true);
			getNickname();
			setNickname();
		}
			
	}
	
	protected void initScoreboardView() {
		// TODO Auto-generated method stub
		scoreboardView = new ScoreboardView(nickname);
		scoreboardView.setVisible(true);
	}

	private void lobby() {
		
		if(frame != null)
			frame.setVisible(false);
		//TODO:
		/**
		 * lista client lobby
		 * chat client lobby deve scrivere
		 * errore quando finisce il turno
		 * errore welcome
		 * quando finisce una partita far vedere la classifica
		 */
		frameLobby = new JFrame();
		frameLobby.setBounds(100, 100, 1065, 722);
		frameLobby.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameLobby.getContentPane().setLayout(null);
		
		txtClientList = new JTextPane();
		txtClientList.setEditable(false);
		txtClientList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtClientList.setBounds(594, 314, 134, 302);
		frameLobby.getContentPane().add(txtClientList);
		
		btnStartGameLobby = new JButton("Start Game");
		btnStartGameLobby.setBounds(921, 612, 118, 60);
		frameLobby.getContentPane().add(btnStartGameLobby);
		
		txtChat = new JTextPane();
		txtChat.setEditable(false);
		txtChat.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtChat.setBounds(822, 22, 217, 545);
		frameLobby.getContentPane().add(txtChat);
		
		scrollPane = new JScrollPane(txtChat);
		scrollPane.setBounds(782, 33, 242, 521);
		frameLobby.getContentPane().add(scrollPane);
		
		txtWrite = new JTextField();
		txtWrite.setBounds(782, 578, 143, 23);
		frameLobby.getContentPane().add(txtWrite);
		txtWrite.setColumns(10);
		
		btnSend = new JButton("Send");//e' creato due volte forse si potrebbe dichiarare solo da una parte
		btnSend.setBounds(935, 578, 89, 23);
		frameLobby.getContentPane().add(btnSend);
		
		txtStatusServer = new JTextField();
		txtStatusServer.setBounds(113, 128, 271, 60);
		frameLobby.getContentPane().add(txtStatusServer);
		txtStatusServer.setColumns(10);
		
		frameLobby.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				JOptionPane.showMessageDialog(null, "Sei uscito");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		//Verranno riattivati quando avverrà la connessione al Server
		btnStartGameLobby.setEnabled(false);
		btnSend.setEnabled(false);
		txtWrite.setEditable(false);
				
		txtWrite.addActionListener(e -> this.send());
		btnStartGameLobby.addActionListener(e -> this.startGame());
		btnSend.addActionListener(e -> this.send());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameLobby.setVisible(false);
		
		frame = new JFrame();
		frame.setBounds(100, 100, 1065, 722);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(935, 578, 89, 23);
		frame.getContentPane().add(btnSend);
		
		txtWrite = new JTextField();
		txtWrite.setBounds(782, 578, 143, 23);
		frame.getContentPane().add(txtWrite);
		txtWrite.setColumns(10);
		
		//TODO: controllare se giusto o no passare model in paintarea
		paintArea = new PnlPaintArea(model);
		paintArea.setBounds(10, 71, 538, 545);
		frame.getContentPane().add(paintArea);
		paintArea.setLayout(null);
		
		pnlStrumenti = new PnlStrumenti(model.getStrumenti());
		pnlStrumenti.setBounds(10, 22, 538, 38);
		frame.getContentPane().add(pnlStrumenti);
		
		btnStartGame = new JButton("Start Game");
		btnStartGame.setBounds(921, 612, 118, 60);
		frame.getContentPane().add(btnStartGame);
		
		pnlWords = new PnlWords();
		pnlWords.setBounds(120, 627, 428, 45);
		frame.getContentPane().add(pnlWords);
		
		txtScoreBoard = new JTextPane();
		txtScoreBoard.setEditable(false);
		txtScoreBoard.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtScoreBoard.setBounds(558, 257, 211, 301);
		frame.getContentPane().add(txtScoreBoard);
		
		txtChat = new JTextPane();
		txtChat.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtChat.setBounds(822, 22, 217, 545);
		frame.getContentPane().add(txtChat);
		
		scrollPane = new JScrollPane(txtChat);
		scrollPane.setBounds(782, 33, 242, 521);
		frame.getContentPane().add(scrollPane);
		
		pnlDatiPartita = new PnlDatiPartita();
		pnlDatiPartita.setBounds(558, 11, 211, 235);
		frame.getContentPane().add(pnlDatiPartita);
		
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				JOptionPane.showMessageDialog(null, "Sei uscito");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		pnlDatiPartita.addChangeListener(e -> this.stopTimer());
		
		//send msg
		btnSend.addActionListener(e -> this.send());
		txtWrite.addActionListener(e -> this.send());
		btnStartGame.addActionListener(e -> this.startGame());
		pnlStrumenti.addActionListener(e -> paintArea.changePaint(e));
		pnlWords.addActionListener(e -> this.sendSelectedWord(e.getActionCommand()));
		
	}
	
	private void send() {
		model.sendMsg(txtWrite.getText());
		txtWrite.setText("");
	}
	
	private void close() {
		model.close();
	}
	
	//TODO: non far inviare anche messaggi che iniziano con caratteri speciali
	protected void getNickname() {
		StringBuffer regexNickname = new StringBuffer();
		regexNickname.append("(?s).*[");
		ClientModel.getKeySet().forEach((key) -> {
			regexNickname.append(key);
		});
		regexNickname.append("].*");
		
		do {
			nickname = JOptionPane.showInputDialog(frame,"What is your name?", null);
			if(nickname == null)
				this.close();
			
		} while(nickname.matches(regexNickname.toString()) || nickname.trim().equals(""));
		
	}
	
	private void setNickname() {
		if(nickname != null) {
			model.setNickname(nickname.trim());
			model.sendMsg(nickname);
		}
	}
	private void update() {
		Object response = model.update();
		
		if((response.getClass().equals(String.class)) && (response != null)) {
			String messageType = response.toString().substring(0,1);
			ProcessMessageClient processor = model.getProcess(messageType);
			if(processor != null) {
				processor.process(this, response.toString().substring(1));
			} else {
//				txtChat.append(response.toString());
//				txtChat.setCaretPosition(txtChat.getDocument().getLength());
				appendToPane(txtChat, response.toString(), Color.BLACK);
			}
			
		}
		if((response.getClass().equals(WhiteBoardLine.class)) && (response != null)) {
			paintArea.updateWhiteBoard((WhiteBoardLine)response);
		}
		
		
	}
	
	public void startTimer(String seconds) {
		pnlDatiPartita.startTimer(Integer.valueOf(seconds));
		
		if(paintArea.isPainter()) {
			pnlStrumenti.setVisible(true);
			pnlDatiPartita.getTxtPainter().setText("Sei il painter");
			pnlDatiPartita.getTxtPainter().setBackground(Color.GREEN);
		} else {
			pnlStrumenti.setVisible(false);
			pnlDatiPartita.getTxtPainter().setText("Non sei il painter");
			pnlDatiPartita.getTxtPainter().setBackground(Color.RED);
		}
		
	}
	
	/**
	 * TODO: timer gestito dal server
	 * TODO: resettare i timer + la paintArea
	 */
	public void stopTimer() {
//		if(paintArea.isPainter())
//			model.sendMsg("!stoptimer");
		
		pnlDatiPartita.stopTimer();
	}
	
	protected void setWordWithHint(String word) {
		if(!paintArea.isPainter())	
			pnlDatiPartita.getTxtGuessWord().setText(word);
	}
	
	public void setPainter() {
		paintArea.setPainter();
	}
	
	//bisogna far si che slo il painter possa schiacciare il bucket ma tutti possano fare clearAll()
	public void clearAll() {
		paintArea.clearAll();	
	}
	
	private void startGame() {
		model.sendMsg("!startmatch");
	}
	
	protected void matchStarted() {
		if(frameLobby.isVisible()) {
			initialize();
			frame.repaint();
		}
			
		//TODO: rendere invisibile/forzare chiusura di ScoreboardView
//		if(scoreboardView != null)
//			scoreboardView.close();
		
		btnStartGame.setVisible(false);
	}
	
	protected void matchCancelled() {
		JOptionPane.showMessageDialog(null, "Sei da solo");
	}
	
	//TODO:quando finisce lo fai tornare nella lobby
	protected void matchFinished() {
		this.setRound("0", "0");
		txtChat.setText("");
		btnStartGame.setVisible(true);
		pnlDatiPartita.stopTimer();
	}
	
	protected void setRound(String currentRound, String totRound) {
		//txtRound.setText(currentRound + "/" + totRound);
		pnlDatiPartita.getTxtRound().setText(currentRound + "/" + totRound);
	}
	
	protected void sendSelectedWord(String word) {
		model.sendMsg("?" + word);
	}
	
	protected void setWords(String[] words) {
		pnlWords.setWords(words);
	}
	
	protected void hidePnlWords() {
		pnlWords.cancelBtn();
	}
	
	protected void resetScoreBoard() {
		txtScoreBoard.setText("");
	}
	
	protected void resetClientList() {
		txtClientList.setText("");
	}
	protected void setScoreBoard(String name, String score, boolean isPainter) {
		Color c;
		if(isPainter)
			c = Color.RED;
		else
			c = Color.BLACK;
		
		if(model.getNickname().equals(name))
			name += " (You)";
		
		appendToPane(txtScoreBoard, name + ":" + score + "\n", c);
	}
	
	protected void setTxtChat(String msg, Color c) {
		appendToPane(txtChat, msg, c);
	}
	
	protected void updateClientList(String name) {
		if(model.getNickname().equals(name))
			name += " (You)";
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

	protected void popupScoreboard(String name, String score) {
		//IDEA 1 : Istanza di una finestra che contiene la scoreboard + immagini per i diversi "posti" + grassetto per nome + "HAI VINTO"
		// ogni volta che viene chiamato questo metodo, popola la HashMap della ScoreboardView, che associa a un "nome" lo "score"
		scoreboardView.addPlayer(name, score);
	}
}
