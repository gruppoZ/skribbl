package it.unibs.pajc.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.JTextPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class ScoreboardView extends JFrame {

	private JPanel contentPane;
	private String nickname;
	private JTextArea txtClassifica;

	/**
	 * Create the frame.
	 */
	public ScoreboardView(String nickname) {
		this.nickname = nickname;
		
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtClassifica = new JTextArea();
		txtClassifica.setBounds(49, 10, 261, 191);
		txtClassifica.setEditable(false);
		
		contentPane.add(txtClassifica);
	}
	
	protected void addPlayer(String format, String name, String score, String position) {
		txtClassifica.append(String.format(format, position, name, score));
	}
	
//	protected void close() {
//		this.close();
//	}
//	protected void display() {
//		this.setVisible(true);
//	}
}
