package it.unibs.pajc.server;

public interface ProcessMessage {
	static final String WORD = "?word:";	//da modificare
	static final String START_TIMER = "<starttimer,";	
	static final String STOP_TIMER = "!stoptimer";
	static final String CHANGE_PAINTER = "!changepainter";
	static final String HIDE_WORDS = "!hidewords";
	static final String MATCH_FINISHED = "!matchfinished";
	static final String MATCH_ALREADY_ON = "!matchalreadyon";
	static final String SCOREBOARD_KEY = "@";
	static final String GUESSED_KEY = "%guessed|";
	static final String WAITING_KEY = "%waiting|";
	static final String SYSTEM_KEY = "%system|";
	static final String JOIN_KEY = "%join|";
	
	static final String WAIT_WORD = "e' il disegnatore!\nSta ancora scegliendo la parola...";
	static final String WORD_CHOOSEN = "ha scelto, si gioca!";
	static final String CLIENT_GUESSED = "%s %s HA INDOVINATO LA PAROLA";
//	static final String CLIENT_WAITING = "%s%s %s";
	static final String CLIENT_JOINED = "e' entrato in partita";
	static final String SELECTED_WORD = "%sLa parola era: %s";
	
	static final String CLIENT_WELCOME = "Buongiorno, il tuo nome e': %s";
	static final String CLIENT_ALONE = "Buongiorno, il tuo nome e': %s \nNon ci sono altri utenti connessi...";
	static final String SEND_ROUND = "/%d,%d";
	static final String SEND_HINT = "*%s\n";
	static final String SEND_SCOREBOARD = "%s:%d/";
	
	public static String welcome(String name, boolean alone) {
		if(alone)
			return String.format(CLIENT_ALONE, name);
		else
			return String.format(CLIENT_WELCOME, name);
	}
	
	public static String sendRound(int currentRound, int rounds) {
		return String.format(SEND_ROUND, currentRound, rounds);
	}
	
	public static String playerGuessed(String name) {
		return String.format(CLIENT_GUESSED, GUESSED_KEY, name);
	}
	
	public static String playerWaiting(String name, String msg) {
		return WAITING_KEY + name + " " + msg;
	}
	
	public static String playerJoined(String name) {
		return JOIN_KEY + name + " " + CLIENT_JOINED;
	}
	
	public static String notifySelectedWord(String selectedWord) {
		return String.format(SELECTED_WORD, WAITING_KEY, selectedWord);
	}
	
	public static String sendHint(String result) {
		return String.format(SEND_HINT, result);
	}
	
	public static String sendScoreBoard(String name, int score, boolean isPainter) {
		if(isPainter)
			return "^^^" + String.format(SEND_SCOREBOARD, name, score);
		else
			return String.format(SEND_SCOREBOARD, name, score);
	}
	
	public static String sendSelectedWord(String selectedWord) {
		return ";" + selectedWord;
	}
	
	void process(Protocol sender, String msg);
}
