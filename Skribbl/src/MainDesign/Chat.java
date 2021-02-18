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
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class Chat extends JPanel {
	private JTextField textMsg;

	/**
	 * Create the panel.
	 */
	public Chat() {
		setLayout(null);
		
		JButton btnSend = new JButton(">");
		
		
		btnSend.setBounds(343, 227, 41, 29);
		add(btnSend);
		
		textMsg = new JTextField();
		textMsg.setBounds(10, 226, 332, 31);
		add(textMsg);
		textMsg.setColumns(10);
		
		JTextArea textChat = new JTextArea();
		textChat.setWrapStyleWord(true);
		textChat.setLineWrap(true);
		textChat.setEditable(false);
		textChat.setBounds(10, 11, 374, 205);
		add(textChat);
		
		/*
		 * Prendo il testo presente textMsg, inserisco in ArrayStringhe e visualizzo (es repaint) nel textChat
		 * nomeUtente: testo 			//nomeUtente usare font diverso (es. Grassetto) 
		 */
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() != null && e.getActionCommand().length()>0)
					setMsg(e.getActionCommand());
			}
		});
		
		
	}
	String msg = null;
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsg() {
		return msg;
	}
	
private ArrayList<ActionListener> listenerList = new ArrayList<ActionListener>();
	
	public void addActionListener(ActionListener l) {
		listenerList.add(l);
	}
	
	public void removeActionListener(ActionListener l) {
		listenerList.remove(l);
	}
	
	public void fireActionListener(ActionEvent e) {
		/**
		 * non gli passo e ma gli passo un evento che che voglio io
		 * per nascondere i bottoni all'esterno
		 * Sto: Isolando il mio sistema
		 */
		ActionEvent myEvent = new ActionEvent(this, 
				ActionEvent.ACTION_PERFORMED,
				e.getActionCommand(),
				e.getWhen(),
				e.getModifiers()//se quando premo con il mouse ho anche schiacciato ctrl questo è un modifiers
		);
		
		
		for (ActionListener actionListener : listenerList) {
			actionListener.actionPerformed(myEvent);
		}
	}
}
