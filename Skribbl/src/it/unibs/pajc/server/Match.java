package it.unibs.pajc.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;
import java.util.concurrent.*;
import javax.swing.event.ChangeEvent;
import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.core.ProcessUtils;

/**
 * Classe che rappresenta una Partita. Gestisce tutte le dinamiche di una partita come: scelta del Disegnatore, 
 * assegnazione punti, rimozione Player dalla partita, ecc..
 *
 */
public class Match extends BaseModel implements Runnable {
	
	private static final String READING_FILE = "Lettura file WORDS.dat ancora in corso";
	private static final int DELAY = 1000; //millisecondi
	private static final int DEFAULT_SECONDS = 20; 
	private static final int COEFF_GUESS = 5;
	private static final int COEFF_PAINTER_MIN = 5;
	private static final int COEFF_PAINTER_MAX = 15;
	private static final int ROUNDS = 3;
	
	private static ArrayList<String> wordsFromFile;
	private Random random;
	private List<Player> playerList;
	private int seconds;
	private Timer timer;

	private int playersWhoGuessed;
	private ArrayList<Protocol> clientList;
	private int currentRound;
	private String selectedWord;
	private boolean turnEnded;
	private List<Player> copyList;
	
	private Protocol painter;
	private Player playerPainter;
	public boolean isRunning;
	
	private String tmp_wordHint;
	private int max_hint;
	private int tmp_countHint;
	
	/**
	 * Costruttore della classe Match. Richiede una clientList per poter creare la partita
	 * @param clientList
	 */
	public Match(ArrayList<Protocol> clientList) {
		this.clientList = clientList;
		this.playerList = Collections.synchronizedList(new ArrayList<Player>());
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
		//Inizializzazione liste
		updatePlayerList();
		
		startMatch();
		
		if(!clientList.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append(generateScoreBoard(ProcessUtils.FINAL_SCOREBOARD_KEY));
			Protocol.sendMsgToAll(sb.toString());
			Protocol.sendMsgToAll(ProcessUtils.command(ProcessUtils.MATCH_FINISHED));
		}
		isRunning = false;
	}
	
	/**
	 * Lettura del file con executor (Future). Viene utilizzato il metodo readFile di UtilsMatch
	 */
	private void readFile() {
		ExecutorService executor = Executors.newCachedThreadPool();;
		Future<ArrayList<String>> futureReadFile;
		
		Callable<ArrayList<String>> readFile = () -> {
        	return UtilsMatch.readFile();
        };
        
        futureReadFile = executor.submit(readFile);
        while(!futureReadFile.isDone()) {
			System.out.println(READING_FILE);
		}
        
		try {
			wordsFromFile = futureReadFile.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}

	/**
	 * Se tutti i player sono usciti dalla partita => controllo da server e la partita viene interrotta
	 */
	private void startMatch() {
		for (int i = 0; i < ROUNDS; i++) {
			if(clientList.size() > 1) {
				currentRound = i + 1;
				startRound();
			}
		}
	}
	
	private void startRound() {	
		copyList = clonePlayerList();
		timer = new Timer(DELAY, e -> {
			if(seconds > 0)
				seconds -= 1;

			if(seconds == 0 || turnEnded)
				stopTimer();
		});
		
		while(!copyList.isEmpty() && clientList.size() > 1) {
			selectPainter();
			Protocol.sendMsgToAll(ProcessUtils.sendRound(currentRound, ROUNDS));
			
			do {
				selectedWord = getSelectedWord();
			} while(selectedWord == null && !copyList.isEmpty() && clientList.size() > 1);
			
			if(copyList.isEmpty() || clientList.size() <= 1) {
				break;
			}
			
			painter.sendMsg(ProcessUtils.command(ProcessUtils.HIDE_WORDS));
			Protocol.sendMsgToAll(ProcessUtils.playerWaiting(painter.getNickname(), ProcessUtils.WORD_CHOOSEN));
			
			painter.sendMsg(ProcessUtils.command(ProcessUtils.CHANGE_PAINTER));
			painter.sendMsg(ProcessUtils.sendSelectedWord(selectedWord));
			
			//Start turn
			max_hint = UtilsMatch.calculateMaxHint(selectedWord);
			Protocol.sendMsgToAll(ProcessUtils.HINT_KEY + String.valueOf(UtilsMatch.getInitWordForHint(selectedWord)));
			
			Runnable task = () -> {
				String result = getHint(selectedWord);
				if(result != null)
					Protocol.sendMsgToAll(ProcessUtils.sendHint(result));
			};
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			
			executor.scheduleAtFixedRate(task, DEFAULT_SECONDS/4, DEFAULT_SECONDS/4, TimeUnit.SECONDS);
			this.startTimer();		
			
			Protocol.sendMsgToAll(ProcessUtils.START_TIMER_KEY + seconds);

			while(timer.isRunning()) {
				if(clientList.size() <= 1) {
					stopTimer();
					close();
				}
			}

			if(timer.isRunning())
				stopTimer();
			
			if(!timer.isRunning()) {
				executor.shutdown();
				Protocol.sendMsgToAll(ProcessUtils.notifySelectedWord(selectedWord));
				resetTurn();
			}
			
		}
	}
	
	/**
	 * Permette di clonare la lista synchronized
	 */
	private synchronized List<Player> clonePlayerList(){
	    return new ArrayList<Player>(playerList);        
	}

	/**
	 * Invia l'evento al match in ascolto nel protocol che permette di chiudere questo thread
	 */
	private void close() {
		fireValuesChange(new ChangeEvent(this));
	}
	
	/**
	 * Check se il client e' gia presente nella playerList. Se non lo e', esso viene aggiunto
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
			if(player.getProtocol().getNickname().equals(client.getNickname()))
				return player;
		}
		return null;
	}
	
	/**
	 * Inizializza il painter del turno corrente
	 * @return TRUE se painter != null
	 */
	public void selectPainter() {
		if(!copyList.isEmpty()) {
			String words = getWordsToGuess();
			int indexPainter = random.nextInt(copyList.size());
			playerPainter = copyList.get(indexPainter);
			
			if(playerPainter != null && clientList.size() > 1) {
				painter = playerPainter.getProtocol();
				playerPainter.setPainter(true);
				Protocol.sendMsgToAll(generateScoreBoard(ProcessUtils.SCOREBOARD_KEY));
				
				Protocol.sendMsgToAll(ProcessUtils.playerWaiting(painter.getNickname(), ProcessUtils.WAIT_WORD));
				painter.sendMsg(words);
			}
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
			
			return tmp_wordHint;
		}	
	}
	
	/**
	 * Reinizializza le variabili necessarie per il corretto funzionamento dei turni del gioco
	 */
	private void resetTurn() {
		tmp_wordHint = null;
		tmp_countHint = 0;
		painter.clearAll();
		painter.sendMsg(ProcessUtils.command(ProcessUtils.CHANGE_PAINTER));
		Protocol.sendMsgToAll(ProcessUtils.command(ProcessUtils.STOP_TIMER));
		playerPainter.setPainter(false);
		
		//Facendo il remove dalla copyList questo client non puo' piu' diventare un painter
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
	
	
	private synchronized String generateScoreBoard(String scoreBoardType) {
		sortScoreBoard(playerList);
		
		StringBuffer sb = new StringBuffer();
		sb.append(scoreBoardType);
		playerList.forEach((player) -> {
			sb.append(ProcessUtils.sendScoreBoard(player.getName(), player.getScore(), player.isPainter()));
		});
		//Esempio Stringa inviata:  @nome1:punteggio1/nome2:punteggio2/
		return sb.toString();
	}
	
	private void sortScoreBoard(List<Player> unsortedScoreBoard) {
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
			
			 // Assegnazione punti una volta che una parola viene indovinata

			if(guesser != null && !guesser.hasGuessed()) {
				if(word.equalsIgnoreCase(selectedWord)) {
					
					guesser.updateScore(seconds * COEFF_GUESS);
					playersWhoGuessed++;
					guesser.setGuessed(true);
					
					if(playersWhoGuessed <= (playerList.size() - 1)/2) 
						playerPainter.updateScore(COEFF_PAINTER_MAX);
					else
						playerPainter.updateScore(COEFF_PAINTER_MIN);
					
					Protocol.sendMsgToAll(generateScoreBoard(ProcessUtils.SCOREBOARD_KEY));
					Protocol.sendMsgToAll(ProcessUtils.playerGuessed(protocol.getNickname()));
				} else {
					protocol.sendMsgToAll(protocol, word);
				}
			}
			
			/*
			 * All'entrata del while si setta turnEnded a TRUE
			 * Se tutti i player hanno indovinato la parola turnEnded = TRUE e esce dal while
			 * Se anche solo un player non ha indovinato turnEnded = FALSE e contnua il while
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
	 * Se player.equals(painter) e' true vuol dire che e' uscito il painter e se !copyList.isEmpty() => 
	 * seleziono un nuovo player
	 * @param client
	 */
	public void removePlayer(Protocol client) {	
		Player player = getPlayerByClient(client);
		if(player != null && !copyList.isEmpty()) {
			playerList.remove(player);
			copyList.remove(player);
			if(player.equals(painter) )
				selectPainter();
			if(clientList.size() > 1)
				Protocol.sendMsgToAll(generateScoreBoard(ProcessUtils.SCOREBOARD_KEY));
		}	
	}
	
	public void addPlayer(Protocol client) {
		updatePlayerList();
		client.sendMsg(ProcessUtils.sendRound(currentRound, ROUNDS));
		client.sendMsg(ProcessUtils.START_TIMER_KEY + seconds);
		Protocol.sendMsgToAll(generateScoreBoard(ProcessUtils.SCOREBOARD_KEY));
	}
	
	/*
	 * Quando fai il set della parola selezionata la rimuovi dalla lista delle parole per evitare che esca un altra
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
	
	/**
	 * Costruisce un array contenente 3 parole prelevate casualmente dalla lista di parole a disposizione del Match 
	 * (provenienti dal file WORDS.dat) 
	 * @return
	 */
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
		result.append(ProcessUtils.WORDS_KEY + ProcessUtils.WORD);
		for (int i : indexes) {
			result.append(wordsFromFile.get(i).trim() + ";");
		}
		return result.toString();
	}
	

}
