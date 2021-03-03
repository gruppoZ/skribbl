package it.unibs.pajc.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;

public class Match implements Runnable{

	//creiamo dalla clientList un insieme di player che avranno:
	// Protocol + punteggio + painter
	
	/**
	 * HashMap<Protocol, Integer>
	 * Protocol	Punti
	 * cl1		100
	 * cl2		300
	 */
	private TreeMap<Player, Integer> playerScoreBoard;
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
		this.playerScoreBoard = new TreeMap<Player, Integer>();
	}
	
	@Override
	public void run() {
		initializePlayerList();
		startMatch();
	}
	
	//anche qua sarà da fare il controllo se già presente
	//quindi class player deve avere un metodo per il controllo
	public void initializePlayerList() {
		for (Protocol client : clientList) {
			playerList.add(new Player(client));		
		}
	}
	
	public void updatePlayerScoreBoard() {
		//for(playerlist)
			//if player già presente in scoreboeard niente
			//else aggiungilo
		//? fare un controllo per i player rimossi:
			//1) lo controllo qua
			//2) me lo faccio dire dal protocol
	}

	private void startMatch() {
		//send oggetto hashmap dove i punti saranno 0
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
//				System.out.println("Sto runnando");
				//verificare quando uno indovina le parole
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
	
	public void temp(Object response) {
		System.out.println("Sono nel match, il msg e': " + (String)response);
		System.out.println(Thread.currentThread().getName());
	}
	// @nomeCLient:100/nomeCLient2:200
	
}
