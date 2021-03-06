package it.unibs.pajc.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

public class Match implements Runnable {

	//creiamo dalla clientList un insieme di player che avranno:
	// Protocol + punteggio + painter
//	private ArrayList<Player> playerList;
	private List<Player> playerList = Collections.synchronizedList(new ArrayList<Player>());
	//timer
	private int seconds;
	private Timer timer;
	private static final int DELAY = 1000; //millisecondi
	
	private static final int ROUNDS = 3;

	private ArrayList<Protocol> clientList;
	private int currentRound;
	private String selectedWord;
	private boolean turnEnded;
	private ArrayList<Protocol> copyList;
	private Protocol painter;
	
	/**
	 * @param clientList
	 */
	public Match(ArrayList<Protocol> clientList) {
		this.clientList = clientList;
		this.playerList = new ArrayList<Player>();
		this.selectedWord = null;
		this.turnEnded = false;
		
	}
	
	@Override
	public void run() {
		//inizializzazione liste
		updatePlayerList();
		
		startMatch();
	}
	
	/**
	 * check se il client è gia presente nella playerList se non lo è lo aggiungo
	 */
	private void updatePlayerList() {
		clientList.forEach(client -> {
			if(getPlayerByClient(client) == null) {
				playerList.add(new Player(client));
				client.addChangeListener(e -> this.checkWord(client, String.valueOf(e.getSource())));
			}
		}
		);
	}
	
	private Player getPlayerByClient(Protocol client) {
		for (Player player : playerList) {
			if(player.getProtocol().getClientName().equals(client.getClientName()))
				return player;
		}
		return null;
	}

	private void startMatch() {
		for (int i = 0; i < ROUNDS; i++) {
			currentRound = i + 1; //i miei round saranno 1 - 2 - 3
			startRound();
		}
	}
	
	private void startRound() {
		//Lista di parole momentanea
		String words = "?words:gatto;cane;capra";
		
		//creazione fake lista
		copyList = (ArrayList<Protocol>) clientList.clone();
		
		//inizializzazione timer
		timer = new Timer(DELAY, e -> {
			if(seconds > 0) {
				  seconds -= 1;
			  }
			  if(seconds == 0 || turnEnded)
				  stopTimer();
		});
		
		while(!copyList.isEmpty()) {
			
			int indexPainter = (int) (Math.random() * copyList.size());
			painter = copyList.get(indexPainter);
			sendScoreBoard(painter);
			painter.sendMsgToAll(painter.getClientName() + " e' il disegnatore!\nSta ancora scegliendo la parola...\n");
			painter.sendMsgToAll("/" + currentRound + "," + ROUNDS);
			
			painter.sendMsg(words);
			do {
				selectedWord = getSelectedWord();
			} while(selectedWord == null);
			
			painter.sendMsg("!hidewords");
			painter.sendMsgToAll(painter.getClientName() + " ha scelto, si Gioca!\n");
			
			painter.sendMsg("!changepainter");
			
			//start turn
			painter.sendMsgToAll("!starttimer");
			this.startTimer();
			
			//timer
			/**
			 * all'entrata del while si setta turnEnded a TURE
			 * se tutti i player hanno indovinato la parola turnEnded = TRUE e esce dal while
			 * se anche solo un player non ha indovinato turnEnded = FALSE e contnua il while
			 */
			
			while(timer.isRunning()) {
				
			}
			
			if(!timer.isRunning()) {
				resetTurn();
			}
			
		}
	}
	
	private void resetTurn() {
		painter.sendMsgToAll("!stoptimer");
		painter.clearAll();
		painter.sendMsg("!changepainter"); //TODO: cambiare in changepainterstatus
		//facendo il remove dalla copyList questo client non può più diventare un painter
		copyList.remove(painter);
		selectedWord = null;
		
		playerList.forEach((player) -> {
			player.setGuessed(false);
		});
	}
	
	protected void startTimer() {
		seconds = 20;
		timer.start();
	}
	
	protected void stopTimer() {
		timer.stop();
	}
	
	
	private void sendScoreBoard(Protocol sender) {
		sortScoreBoard(playerList);
		
		StringBuffer sb = new StringBuffer();
		sb.append("@");
		playerList.forEach((player) ->
				sb.append(player.getProtocol().getClientName() + ":" + player.getScore() + "/")
		);
		System.out.println(sb.toString());
//		sender.sendMsgToAll(sb.toString());
	}
	
	private void sortScoreBoard(List<Player> unsortedScoreBoard) {
		Collections.sort(unsortedScoreBoard, new ScoreComparator());
	}
	
	public void checkWord(Protocol protocol, String word) {
		Player guesser = null;
		if(timer.isRunning()) {
			//parola indovinata:
			for (Player player : playerList) {
				if(player.equals(protocol) && !player.equals(painter)) {
					guesser = player;
				}
			}
			
			if(guesser != null && !guesser.hasGuessed()) {
				if(word.equalsIgnoreCase(selectedWord)) {
					guesser.updateScore(20);
					guesser.setGuessed(true);
					sendScoreBoard(painter);
					protocol.sendMsgToAll(protocol.getClientName() + " HA INDOVINATO LA PAROLA");
				} else {
					protocol.sendMsgToAll(protocol, word);
				}
			}
			
			turnEnded = true;
			for (Player player : playerList) {
					if(!player.equals(painter)) {
						turnEnded = turnEnded && player.hasGuessed();	
					}		
			}
				
		}
//		System.out.println("Sono nel match, il msg e': " + word);
//		System.out.println(Thread.currentThread().getName());
	}
	
	public void removePlayer(Protocol client) {
		Player player = getPlayerByClient(client);
		if(player != null) {
			playerList.remove(player);
			sendScoreBoard(client);
		}	
	}
	
	public void addPlayer(Protocol client) {
		updatePlayerList();
		sendScoreBoard(client);
	}
	
	protected void setSelectedWord(String word) {
		this.selectedWord = word;
	}
	
	private String getSelectedWord() {
		return this.selectedWord;
	}
	

}
