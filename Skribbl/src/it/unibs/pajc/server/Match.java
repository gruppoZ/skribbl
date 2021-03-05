package it.unibs.pajc.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;

public class Match implements Runnable{

	//creiamo dalla clientList un insieme di player che avranno:
	// Protocol + punteggio + painter
	
	/**
	 * TreeMap<Player, Integer>
	 * Player	Punti
	 * cl1		100
	 * cl2		300
	 */
	
	//TODO: no treemap si sortedmap
	private HashMap<Player, Integer> playerScoreBoard;
	private ArrayList<Player> playerList;
	
	//timer
	private int seconds;
	private Timer timer;
	private static final int DELAY = 1000; //millisecondi
	
	private static final int ROUNDS = 3;

	private ArrayList<Protocol> clientList;
	private int currentRound;
	/**
	 * @param clientList
	 */
	public Match(ArrayList<Protocol> clientList) {
		this.clientList = clientList;
		this.playerList = new ArrayList<Player>();
		this.playerScoreBoard = new HashMap<Player, Integer>();
	}
	
	@Override
	public void run() {
		//inizializzazione liste
		updatePlayerList();
		updatePlayerScoreBoard();
		
		startMatch();
	}
	
	/**
	 * check se il client è gia presente nella playerList se non lo è lo aggiungo
	 */
	private void updatePlayerList() {
		clientList.forEach(client -> {
			if(getPlayerByClient(client) == null) {
				playerList.add(new Player(client));
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
	
	private void updatePlayerScoreBoard() {
		playerList.forEach(player -> {
			System.out.println(playerScoreBoard.containsKey(player));
			playerScoreBoard.put(player, player.getScore());
		});
	}

	private void startMatch() {
		for (int i = 0; i < ROUNDS; i++) {
			currentRound = i + 1; //i miei round saranno 1 - 2 - 3
			startRound();
		}
	}
	
	private void startRound() {
		//creazione fake lista
		ArrayList<Protocol> copyList = (ArrayList<Protocol>) clientList.clone();
		
		//inizializzazione timer
		timer = new Timer(DELAY, e -> {
			if(seconds > 0) {
				  seconds -= 1;
			  }
			  if(seconds == 0)
				  stopTimer();
		});
		
		while(!copyList.isEmpty()) {
			int indexPainter = (int) (Math.random() * copyList.size());
			Protocol painter = copyList.get(indexPainter);
			painter.sendMsgToAll(painter.getClientName() + " e' il disegnatore!\nSta ancora scegliendo la parola...\n");
			painter.sendMsgToAll("/" + currentRound + "," + ROUNDS);
			
			sendScoreBoard(painter);
			//DA SPOSTARE
//			painter.sendLine(clientScoreBoard);
			//DA SPOSTARE
			
			//painter deve scegliere la parola
			painter.sendMsg("!changepainter");
			
			//start turn
			painter.sendMsgToAll("!starttimer");
			this.startTimer();
			
			//timer
			while(timer.isRunning()) {
				playerList.forEach((player) ->{
					int tem = (int) (Math.random() * 100);
					player.setScore(tem);
				});
				//verificare quando uno indovina le parole
				//chiameremo il metodo updatePlayerScoreBoard quando qualcuno indovina la parola
			}

			if(!timer.isRunning()) {
				painter.sendMsgToAll("!stoptimer");
				painter.clearAll();
				painter.sendMsg("!changepainter"); //TODO: cambiare in changepainterstatus
				//facendo il remove dalla copyList questo client non può più diventare un painter
				copyList.remove(painter);
			}
			
		}
	}
	
	protected void startTimer() {
		seconds = 10;
		timer.start();
	}
	
	protected void stopTimer() {
		timer.stop();
	}
	
	
	private void sendScoreBoard(Protocol sender) {
		
		sortScoreBoard(playerList);
//		Map<Player, Integer> sortedScoreBoard = sortScoreBoard(playerScoreBoard);
		// @nomeCLient:100/nomeCLient2:200
		StringBuffer sb = new StringBuffer();
		sb.append("@");
		
//		for (Player player : sortedScoreBoard.keySet()) {
//			sb.append(player.getProtocol().getClientName() + ":" + player.getScore() + "/");
//		}
		playerList.forEach((player) ->
				sb.append(player.getProtocol().getClientName() + ":" + player.getScore() + "/")
		);
		
//		playerScoreBoard.forEach((player, score) -> 
//			sb.append(player.getProtocol().getClientName() + ":" + player.getScore() + "/")
//		);
//		sender.sendMsgToAll(sb.toString());
		System.out.println(sb.toString());
	}
	
	private void sortScoreBoard(ArrayList<Player> unsortedScoreBoard) {
		Collections.sort(unsortedScoreBoard, new ScoreComparator());
		
	}
	
//	private Map<Player, Integer> sortScoreBoard(Map<Player, Integer> unsortedScoreBoard) {
//		
//		ScoreComparator cm = new ScoreComparator(unsortedScoreBoard);
//		TreeMap<Player, Integer> sortedScoreBoard = new TreeMap<Player, Integer>(cm);
//		
//		sortedScoreBoard.putAll(unsortedScoreBoard);
//		
//		return sortedScoreBoard;
//	}
	
	public void temp(Object response) {
		System.out.println("Sono nel match, il msg e': " + (String)response);
		System.out.println(Thread.currentThread().getName());
	}
	
	public void removePlayer(Protocol client) {
		Player player = getPlayerByClient(client);
		if(player != null) {
			playerList.remove(player);
			playerScoreBoard.remove(player);
			sendScoreBoard(client);
		}	
	}
	
	public void addPlayer(Protocol client) {
		updatePlayerList();
		updatePlayerScoreBoard();
		sendScoreBoard(client);
	}
	

}
