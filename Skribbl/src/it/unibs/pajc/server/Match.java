package it.unibs.pajc.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

public class Match implements Runnable {

	
	//creiamo dalla clientList un insieme di player che avranno:
	// Protocol + punteggio + painter
//	private ArrayList<Player> playerList;
//	private List<Player> playerList = Collections.synchronizedList(new ArrayList<Player>());
	
	private static final String[] LIST_WORDS= {"gatto", "cane", "capra", "fiore", "muro", "viso", "collo", "naso", "mano", "dito",
			"ruota", "casa", "porta", "vaso"};
	private static ArrayList<String> words;
	private Random random;
	private ArrayList<Player> playerList;
	//timer

	private int seconds;
	private Timer timer;
	private static final int DELAY = 1000; //millisecondi
	private static final int COEFF_GUESS = 5;
	private static final int COEFF_PAINTER_MIN = 5;
	private static final int COEFF_PAINTER_MAX = 15;
	private static final int ROUNDS = 3;

	private int playersWhoGuessed;
	private ArrayList<Protocol> clientList;
	private int currentRound;
	private String selectedWord;
	private boolean turnEnded;
	private ArrayList<Player> copyList;
	
	private Protocol painter;
	private Player playerPainter;
	
	/**
	 * @param clientList
	 */
	public Match(ArrayList<Protocol> clientList) {
		this.clientList = clientList;
		this.playerList = new ArrayList<Player>();
		this.selectedWord = null;
		this.turnEnded = false;
		this.random = new Random();
		this.playersWhoGuessed = 0;
		
		words = new ArrayList<>();
        readFile();
	}
	
	@Override
	public void run() {
		//inizializzazione liste
		updatePlayerList();
		
		startMatch();
		
		for (Protocol protocol : clientList) {
			if(protocol != null)
				protocol.sendMsgToAll("!matchfinished");
			break;
		}
	}
	
	private void readFile() {
		File fileName = new File("src/it/unibs/pajc/server/WORDS.dat");

        try(
                BufferedReader in = new BufferedReader(new FileReader(fileName))
            ) {

            String line;
            while((line = in.readLine()) != null) {
                words.add(line);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	/**
	 * check se il client e' gia presente nella playerList se non lo e' lo aggiungo
	 */
	private void updatePlayerList() {
		clientList.forEach(client -> {
			if(getPlayerByClient(client) == null) {
				playerList.add(new Player(client));
				client.addChangeListener(e -> this.checkWord(client, String.valueOf(e.getSource())));
			}
		});
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
		//creazione fake lista
//		copyList = (ArrayList<Protocol>) clientList.clone();
		copyList = (ArrayList<Player>) playerList.clone();
		
		timer = new Timer(DELAY, e -> {
			
			if(seconds > 0)
				seconds -= 1;

			if(seconds == 0 || turnEnded)
				stopTimer();
		});
		
		while(!copyList.isEmpty()) {
			String words = getWordsToGuess();
			int indexPainter = random.nextInt(copyList.size());
//			int indexPainter = (int) (Math.random() * copyList.size());
			playerPainter = copyList.get(indexPainter);
			painter = playerPainter.getProtocol();
			playerPainter.setPainter(true);
			sendScoreBoard(painter);
			
			painter.sendMsgToAll("%waiting|" + painter.getClientName() + " e' il disegnatore!\nSta ancora scegliendo la parola...");
			painter.sendMsgToAll("/" + currentRound + "," + ROUNDS);
			
			painter.sendMsg(words);
			do {
				selectedWord = getSelectedWord();
			} while(selectedWord == null);
			
			painter.sendMsg("!hidewords");
			painter.sendMsgToAll("%waiting|" + painter.getClientName() + " ha scelto, si Gioca!");
			
			painter.sendMsg("!changepainter");
			//start turn
			this.startTimer();
			painter.sendMsgToAll("!starttimer," + seconds);
			
			//timer
			//aspetta che il timer finisca e "freeza" il turno
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
		playerPainter.setPainter(false);
		//facendo il remove dalla copyList questo client non pu� pi� diventare un painter
		copyList.remove(playerPainter);
		selectedWord = null;
		turnEnded = false;
		
		playerList.forEach((player) -> {
			player.setGuessed(false);
		});
		playersWhoGuessed = 0;
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
		playerList.forEach((player) -> {
			if(player.isPainter())
				sb.append("^^^" + player.getProtocol().getClientName() + ":" + player.getScore() + "/");
			else
				sb.append(player.getProtocol().getClientName() + ":" + player.getScore() + "/");
		});
//		System.out.println(sb.toString());
//		System.out.println(sender.getClientName());
		//TODO: se un player entra a partita iniziata non riceve la scoreboard
//		try {
//			TimeUnit.MILLISECONDS.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		sender.sendMsgToAll(sb.toString());
		//@nome:punteggio/nome2:punteggio2/
	}
	
	private void sortScoreBoard(ArrayList<Player> unsortedScoreBoard) {
		Collections.sort(unsortedScoreBoard, new ScoreComparator());
		Collections.reverse(unsortedScoreBoard);
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
			
			/**
			 * guesser si fa qua
			 * 
			 * fine di ogni turno facciamo un metodo
			 * contatore che viene incrementato qua
			 * in base al contatore 
			 */
			if(guesser != null && !guesser.hasGuessed()) {
				if(word.equalsIgnoreCase(selectedWord)) {
					
					guesser.updateScore(seconds * COEFF_GUESS);
					playersWhoGuessed++;
					guesser.setGuessed(true);
					
					if(playersWhoGuessed <= (playerList.size() - 1)/2) 
						playerPainter.updateScore(COEFF_PAINTER_MAX);
					else
						playerPainter.updateScore(COEFF_PAINTER_MIN);
					
					sendScoreBoard(painter);
					protocol.sendMsgToAll("%guessed|" +protocol.getClientName() + " HA INDOVINATO LA PAROLA");
				} else {
					protocol.sendMsgToAll(protocol, word);
				}
			}
			
			/**
			 * all'entrata del while si setta turnEnded a TURE
			 * se tutti i player hanno indovinato la parola turnEnded = TRUE e esce dal while
			 * se anche solo un player non ha indovinato turnEnded = FALSE e contnua il while
			 */
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
		client.sendMsg("/" + currentRound + "," + ROUNDS);
		client.sendMsg("!starttimer," + seconds);
		sendScoreBoard(client);
	}
	
	protected void setSelectedWord(String word) {
		this.selectedWord = word;
	}
	
	private String getSelectedWord() {
		return this.selectedWord;
	}
	
	
	private String getWordsToGuess() {
		int[] indexes = new int[3];
		
		for(int i = 0; i<indexes.length; i++) {
			indexes[i] = random.nextInt(words.size());
			for(int j = 0; j < i; j++) {
				while(indexes[i] == indexes[j])
					indexes[i] = random.nextInt(words.size());
			}
		}
		
		StringBuffer result = new StringBuffer();
		result.append("?word:");
		for (int i : indexes) {
			result.append(words.get(i) + ";");
		}
		return result.toString();
	}
	

}
