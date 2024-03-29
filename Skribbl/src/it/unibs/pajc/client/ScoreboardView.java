package it.unibs.pajc.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Color;

public class ScoreboardView extends JFrame {

	private JPanel contentPane;
	private JTextPane txtScoreBoard;
	private JTextField txtTitle;

	/**
	 * Classe che mostra la classifica finale della partita
	 */
	public ScoreboardView() {
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtScoreBoard = new JTextPane();
		txtScoreBoard.setBounds(146, 62, 278, 191);
		txtScoreBoard.setEditable(false);
		contentPane.add(txtScoreBoard);
		
		txtTitle = new JTextField();
		txtTitle.setBackground(Color.WHITE);
		txtTitle.setEditable(false);
		txtTitle.setForeground(Color.ORANGE);
		txtTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
		txtTitle.setText("Score Board");
		txtTitle.setBounds(10, 11, 414, 40);
		txtTitle.setHorizontalAlignment(JTextField.CENTER);
		contentPane.add(txtTitle);
		txtTitle.setColumns(10);

	}
	
	protected void addPlayer(String format, String name, String score, String position) {
		txtScoreBoard.setText(txtScoreBoard.getText() + String.format(format, position, name, score));
	}
}
