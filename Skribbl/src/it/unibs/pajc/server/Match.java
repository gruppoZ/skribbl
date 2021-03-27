package it.unibs.pajc.server;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


import javax.swing.Timer;

import java.util.concurrent.*;

import javax.swing.event.ChangeEvent;

import it.unibs.pajc.client.ProcessScoreBoard;
import it.unibs.pajc.core.BaseModel;

public class Match extends BaseModel implements Runnable {

	
	private final static String URI_FILE_WORD = "src/it/unibs/pajc/server/WORDS.dat";

	private static ArrayList<String> wordsFromFile;
	private Random random;
	private ArrayList<Player> playerList;
	private int seconds;
	private Timer timer;
	private static final int DELAY = 1000; //millisecondi
	private static final int DEFAULT_SECONDS = 20; 
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
	public boolean isRunning;
	
	private String tmp_wordHint;
	private int max_hint;
	private int tmp_countHint;
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
		this.isRunning = true;
		this.tmp_wordHint = null;
		this.tmp_countHint = 0;
		
		wordsFromFile = new ArrayList<>();
        readFile();
	}
	
	@Override
	public void run() {
		//inizializzazione liste
		updatePlayerList();
		
		startMatch();
		
		if(!clientList.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append(generateScoreBoard(ProcessMessage.FINAL_SCOREBOARD_KEY));
			Protocol.sendMsgToAll(sb.toString());
			Protocol.sendMsgToAll(ProcessMessage.command(ProcessMessage.MATCH_FINISHED));
		}
		isRunning = false;
	}
	
	/**
	 * Lettura del file con executor (Future). Viene utilizzato il metodo readFile di UtilsMatch
	 */
	private void readFile() {
		ExecutorService globalExecutor = Executors.newCachedThreadPool();;
		Future<ArrayList<String>> futureReadFile;
		
		Callable<ArrayList<String>> readFile = () -> {
        	return UtilsMatch.readFile();
        };
        
        futureReadFile = globalExecutor.submit(readFile);
        while(!futureReadFile.isDone()) {
			System.out.println("Lettura file WORDS.dat ancora in corso");
		}
        
		try {
			wordsFromFile = futureReadFile.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			globalExecutor.shutdown();
		}
	}

	/**
	 * Se sono usciti tutti => controllo da server e finisce/reset MATCH
	 */
	private void startMatch() {
		for (int i = 0; i < ROUNDS; i++) {
			if(clientList.size() > 1) {
				currentRound = i + 1; //i miei round saranno 1 - 2 - 3
				startRound();
			}
		}
	}
	
	private void startRound() {	
		//creazione fake lista
		copyList = (ArrayList<Player>) playerList.clone();
		
		timer = new Timer(DELAY, e -> {
			
			if(seconds > 0)
				seconds -= 1;

			if(seconds == 0 || turnEnded)
				stopTimer();
		});
		
		while(!copyList.isEmpty() && clientList.size() > 1) {
			selectPainter();
			Protocol.sendMsgToAll(ProcessMessage.sendRound(currentRound, ROUNDS));
			
			do {
				selectedWord = getSelectedWord();
			} while(selectedWord == null && !copyList.isEmpty() && clientList.size() > 1);
			
			if(copyList.isEmpty() || clientList.size() <= 1) {
				break;
			}
			
			painter.sendMsg(ProcessMessage.command(ProcessMessage.HIDE_WORDS));
			Protocol.sendMsgToAll(ProcessMessage.playerWaiting(painter.getClientName(), ProcessMessage.WORD_CHOOSEN));
			
			painter.sendMsg(ProcessMessage.command(ProcessMessage.CHANGE_PAINTER));
			painter.sendMsg(ProcessMessage.sendSelectedWord(selectedWord));
			//start turn
			max_hint = UtilsMatch.calculateMaxHint(selectedWord);
			Protocol.sendMsgToAll(ProcessMessage.HINT_KEY + String.valueOf(UtilsMatch.getInitWordForHint(selectedWord)));
			
			Runnable task = () -> {
				String result = getHint(selectedWord);
				if(result != null)
					Protocol.sendMsgToAll(ProcessMessage.sendHint(result));
			};
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			
			executor.scheduleAtFixedRate(task, DEFAULT_SECONDS/4, DEFAULT_SECONDS/4, TimeUnit.SECONDS);
			this.startTimer();		
			
			Protocol.sendMsgToAll(ProcessMessage.START_TIMER_KEY + seconds);
			
			//timer
			//aspetta che il timer finisca e "freeza" il turno
			while(timer.isRunning()) {
				if(clientList.size() <= 1) {
					stopTimer();
					close();
				}
			}
			//TODO:da togliere
			if(timer.isRunning())
				stopTimer();
			
			if(!timer.isRunning()) {
				executor.shutdown();
				Protocol.sendMsgToAll(ProcessMessage.notifySelectedWord(selectedWord));
				resetTurn();
			}
			
		}
	}

	/**
	 * invia l'evento al match in ascolto nel protocol che permette di chiudere questo thread
	 */
	private void close() {
		fireValuesChange(new ChangeEvent(this));
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
	
	/**
	 * serve a inizializzare il painter del turno corrente
	 * @return TRUE se painter != null
	 */
	public void selectPainter() {
		String words = getWordsToGuess();
		int indexPainter = random.nextInt(copyList.size());
		playerPainter = copyList.get(indexPainter);
		
		if(playerPainter != null) {
			painter = playerPainter.getProtocol();
			playerPainter.setPainter(true);
			Protocol.sendMsgToAll(generateScoreBoard(ProcessMessage.SCOREBOARD_KEY));
			
			Protocol.sendMsgToAll(ProcessMessage.playerWaiting(painter.getClientName(), ProcessMessage.WAIT_WORD));
			painter.sendMsg(words);
		}
	}
	
	private String getHint(String word) {
			
		int index;
		char[] result;
		
		if(tmp_countHint >= max_hint)
			return null;
		else {
			
			if(tmp_wordHint != null)
				result = tmp_wordHint.toCharArray();		
			else 
				result = UtilsMatch.getInitWordForHint(word);
				
			do {
				index = 1 + random.nextInt(word.length()-1);
			}while(result[index] != '_');
			
			result[index] = word.charAt(index);
			tmp_countHint++;
			tmp_wordHint = String.valueOf(result);
			
//			System.out.println("Parola: " + word);				
//			System.out.println("Hint: " + tmp_wordHint);
//			System.out.println("MAX - HINT: " + max_hint + " --> Current Count HINT: " + tmp_countHint);
			return tmp_wordHint;
		}
		
	}
	
	/**
	 * Reinizializza le variabili necessarie per il corretto funzionamento dei turni del gioco
	 */
	private void resetTurn() {
		tmp_wordHint = null;
		tmp_countHint = 0;
		Protocol.sendMsgToAll(ProcessMessage.command(ProcessMessage.STOP_TIMER));
		painter.clearAll();
		painter.sendMsg(ProcessMessage.command(ProcessMessage.CHANGE_PAINTER)); //TODO: cambiare in changepainterstatus
		playerPainter.setPainter(false);
		//facendo il remove dalla copyList questo client non puï¿½ piï¿½ diventare un painter
		copyList.remove(playerPainter);
		selectedWord = null;
		turnEnded = false;
		
		playerList.forEach((player) -> {
			player.setGuessed(false);
		});
		playersWhoGuessed = 0;
	}
	
	protected void startTimer() {
		seconds = DEFAULT_SECONDS;
		timer.start();
	}
	
	protected void stopTimer() {
		timer.stop();
	}
	
	
	private String generateScoreBoard(String scoreBoardType) {
		sortScoreBoard(playerList);
		
		StringBuffer sb = new StringBuffer();
		sb.append(scoreBoardType);
		playerList.forEach((player) -> {
			sb.append(ProcessMessage.sendScoreBoard(player.getName(), player.getScore(), player.isPainter()));
		});
		//@nome:punteggio/nome2:punteggio2/
		return sb.toString();
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
					
					Protocol.sendMsgToAll(generateScoreBoard(ProcessMessage.SCOREBOARD_KEY));
					Protocol.sendMsgToAll(ProcessMessage.playerGuessed(protocol.getClientName()));
				} else {
//					Protocol.sendMsgToAll(word);
					protocol.sendMsgToAll(protocol, word);
					//TODO: al posto che usare questo sendMsgToAll modificare il msg da inviare
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
	}
	
	/**
	 * Rimuove sia dalla playerList che dalla copyList il client inviato come parametro
	 * Se player.equals(painter) è true vuol dire che è uscito il painter e se !copyList.isEmpty() => 
	 * seleziono un nuovo player
	 * @param client
	 */
	public void removePlayer(Protocol client) {
		Player player = getPlayerByClient(client);
		if(player != null) {
			playerList.remove(player);
			copyList.remove(player);
			if(player.equals(painter) && !copyList.isEmpty())
				selectPainter(); //TODO: errore, il client che rimane solo si blocco
			Protocol.sendMsgToAll(generateScoreBoard(ProcessMessage.SCOREBOARD_KEY));
		}	
	}
	
	public void addPlayer(Protocol client) {
		updatePlayerList();
		client.sendMsg(ProcessMessage.sendRound(currentRound, ROUNDS));
		client.sendMsg(ProcessMessage.START_TIMER_KEY + seconds);
		Protocol.sendMsgToAll(generateScoreBoard(ProcessMessage.SCOREBOARD_KEY));
	}
	
	/*
	 * quando fai il set della parola selezionata la rimuovi dalla lista delle parole per evitare che esca un altra
	 * volta durante questa partita
	 */
	protected void setSelectedWord(String word) {
		this.selectedWord = word;
		wordsFromFile.remove(word);
		//Thread.start()
	}
	
	private String getSelectedWord() {
		return this.selectedWord;
	}
	
	
	private String getWordsToGuess() {
		int[] indexes = new int[3];
		
		for(int i = 0; i<indexes.length; i++) {
			indexes[i] = random.nextInt(wordsFromFile.size());
			for(int j = 0; j < i; j++) {
				while(indexes[i] == indexes[j])
					indexes[i] = random.nextInt(wordsFromFile.size());
			}
		}
		
		StringBuffer result = new StringBuffer();
		result.append(ProcessMessage.WORDS_KEY + ProcessMessage.WORD);
		for (int i : indexes) {
			result.append(wordsFromFile.get(i).trim() + ";");
		}
		return result.toString();
	}
	

}
