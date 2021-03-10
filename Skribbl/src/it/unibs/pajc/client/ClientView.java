package it.unibs.pajc.client;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
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
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JScrollBar;

public class ClientView {

	private JFrame frame;
	private JTextField txtWrite;

	private ClientModel model;
	//TODO: cambiare paintArea in pnlPaintArea
	private PnlPaintArea paintArea;
	private PnlTimer pnlTimer;
	private PnlStrumenti pnlStrumenti;
	private PnlWords pnlWords;
	
	private JTextPane txtCurrentRound;
	private JTextPane txtSeparetor;
	private JTextPane txtTotRound;
	private JTextPane txtPainter;
	private JTextPane txtScoreBoard;
	private JTextPane txtChat;
	private JScrollPane scrollPane;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientView window = new ClientView();
					window.frame.setVisible(true);
					window.frame.setLocationRelativeTo(null);
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
		model.addChangeListener(e -> this.update());
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1065, 722);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(935, 578, 89, 23);
		frame.getContentPane().add(btnSend);
		
		txtWrite = new JTextField();
		txtWrite.setBounds(782, 578, 143, 23);
		frame.getContentPane().add(txtWrite);
		txtWrite.setColumns(10);
//		JScrollPane scrollBar = new JScrollPane(txtChat);
//		scrollBar.setAutoscrolls(true);
//		scrollBar.setBounds(799, 11, 222, 373);
//		frame.getContentPane().add(scrollBar);
		
		//TODO: controllare se giusto o no passare model in paintarea
		paintArea = new PnlPaintArea(model);
		paintArea.setBounds(10, 71, 538, 545);
		frame.getContentPane().add(paintArea);
		paintArea.setLayout(null);
		
		pnlStrumenti = new PnlStrumenti(model.getStrumenti());
		pnlStrumenti.setBounds(10, 22, 538, 38);
		frame.getContentPane().add(pnlStrumenti);
		
		JButton btnStartGame = new JButton("Start Game");
		btnStartGame.setBounds(921, 612, 118, 60);
		frame.getContentPane().add(btnStartGame);
		
		pnlTimer = new PnlTimer();
		pnlTimer.setBounds(621, 22, 107, 68);
		pnlTimer.setBackground(Color.WHITE);
		frame.getContentPane().add(pnlTimer);
		
		txtCurrentRound = new JTextPane();
		txtCurrentRound.setEditable(false);
		txtCurrentRound.setBounds(594, 159, 32, 31);
		frame.getContentPane().add(txtCurrentRound);
		
		txtSeparetor = new JTextPane();
		txtSeparetor.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtSeparetor.setEditable(false);
		txtSeparetor.setText("/");
		txtSeparetor.setBounds(636, 159, 13, 23);
		frame.getContentPane().add(txtSeparetor);
		
		txtTotRound = new JTextPane();
		txtTotRound.setEditable(false);
		txtTotRound.setBounds(659, 159, 32, 31);
		frame.getContentPane().add(txtTotRound);
		
		txtPainter = new JTextPane();
		txtPainter.setEditable(false);
		txtPainter.setBounds(594, 246, 97, 20);
		frame.getContentPane().add(txtPainter);
		
		pnlWords = new PnlWords();
		pnlWords.setBounds(120, 627, 428, 45);
		frame.getContentPane().add(pnlWords);
		
		txtScoreBoard = new JTextPane();
		txtScoreBoard.setEditable(false);
		txtScoreBoard.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtScoreBoard.setBounds(594, 314, 134, 302);
		frame.getContentPane().add(txtScoreBoard);
		
		txtChat = new JTextPane();
		txtChat.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtChat.setBounds(822, 22, 217, 545);
		frame.getContentPane().add(txtChat);
		
		scrollPane = new JScrollPane(txtChat);
		scrollPane.setBounds(782, 33, 242, 521);
		frame.getContentPane().add(scrollPane);
		
		
		pnlTimer.addChangeListener(e -> this.stopTimer());
		
		setNickname();
		
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

	private void setNickname() {
		String nickname = JOptionPane.showInputDialog(frame,"What is your name?", null);
		model.sendMsg(nickname);
	}
	
	private void update() {
		Object response = model.update();
		
		if((response.getClass().equals(String.class)) && (response != null)) {
			String messageType = response.toString().substring(0,1);
			ProcessMessageClient processor = model.commandMap.get(messageType);
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
		pnlTimer.startTimer(Integer.valueOf(seconds));
		
		if(paintArea.isPainter()) {
			pnlStrumenti.setVisible(true);
			txtPainter.setText("Sei il painter");
			txtPainter.setBackground(Color.GREEN);
		} else {
			pnlStrumenti.setVisible(false);
			txtPainter.setText("Non sei il painter");
			txtPainter.setBackground(Color.RED);
		}
		
	}
	
	/**
	 * TODO: timer gestito dal server
	 * TODO: resettare i timer + la paintArea
	 */
	public void stopTimer() {
//		if(paintArea.isPainter())
//			model.sendMsg("!stoptimer");
		
		pnlTimer.stopTimer();
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
	
	protected void setRound(String currentRound, String totRound) {
		txtCurrentRound.setText(currentRound);
		txtTotRound.setText(totRound);
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
	
	protected void setScoreBoard(String name, String score, boolean isPainter) {
		Color c;
		if(isPainter)
			c = Color.RED;
		else
			c = Color.BLACK;
		
		appendToPane(txtScoreBoard, name + ":" + score + "\n", c);
		
	}
	
	protected void setTxtChat(String msg, Color c) {
		appendToPane(txtChat, msg, c);
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
}
