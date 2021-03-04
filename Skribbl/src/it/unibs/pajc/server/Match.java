package it.unibs.pajc.server;

import java.util.*;
import javax.swing.Timer;

public class Match implements Runnable {

	//creiamo dalla clientList un insieme di player che avranno:
		// Protocol + punteggio + painter
		
		/**
		 * HashMap<Protocol, Integer>
		 * Protocol	Punti
		 * cl1		100
		 * cl2		300
		 */
		HashMap<Protocol, Integer> clientScoreBoard;
		
		//timer
		private int seconds;
		private Timer timer;
		private static final int DELAY = 1000; //millisecondi
		
		private static final int ROUNDS = 3;

		private String selectedWord = null;
		
		private ArrayList<Protocol> clientList;
		private int currentRound;
		/**
		 * @param clientList
		 */
		public Match(ArrayList<Protocol> clientList) {
			this.clientList = clientList;
			this.clientScoreBoard = new HashMap<Protocol, Integer>();
		}
		
		@Override
		public void run() {
			initializeScoreBoard();
			startMatch();
		}
		
		public void initializeScoreBoard() {
			for (Protocol client : clientList) {
				clientScoreBoard.put(client, 0);
			}
		}

		private void startMatch() {
			//send oggetto hashmap dove i punti saranno 0
			clientList.get(0).sendMsgToAll("@changepainter");//da correggere
			for (int i = 0; i < ROUNDS; i++) {
				currentRound = i + 1; //i miei round saranno 1 - 2 - 3
				startRound();
			}
		}
		
		protected void setSelectedWord(String word) {
			this.selectedWord = word;
		}
		protected String getSelectedWord() {
			return this.selectedWord;
		}
		
		private void startRound() {
			
			String words = "@words:gatto;cane;capra";
			
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
				painter.sendMsgToAll(painter, "@round" + currentRound + "/" + ROUNDS);
				
				painter.sendMsg("@changepainter");
				painter.sendMsg(painter, words);
				//DA SPOSTARE
//					painter.sendLine(clientScoreBoard);
				//DA SPOSTARE
				
				//painter deve scegliere la parola
				
				do {
					selectedWord = getSelectedWord();
				} while(selectedWord == null);
			
				painter.sendMsgToAll(painter.getClientName() + " e' il disegnatore!\nHa scelto, si Gioca!\n");
				//start turn
				painter.sendMsgToAll("@start_timer");
				this.startTimer();
				
				//timer
				while(timer.isRunning()) {
//						System.out.println("Sto runnando");
					//verificare quando uno indovina le parole
				}

				if(!timer.isRunning()) {
					painter.sendMsgToAll("@stoptimer");
					painter.sendMsg("@trashcan"); //painter.clearAll(); //pulire il paint
					painter.sendMsg("@changepainter");
					painter.sendMsg("@nowords");
					//facendo il remove dalla copyList questo client non può più diventare un painter
					copyList.remove(painter);
				}
				
				selectedWord = null;
				
			}
		}
		
		protected void startTimer() {
			seconds = 10;
			timer.start();
		}
		
		protected void stopTimer() {
			timer.stop();
		}
		
		// @nomeCLient:100/nomeCLient2:200

}
