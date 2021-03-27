package it.unibs.pajc.core;

public class ProcessUtils {
	public static final String WORD = "word:";	//da modificare
	public static final String STOP_TIMER = "stoptimer";
	public static final String CHANGE_PAINTER = "changepainter";
	public static final String HIDE_WORDS = "hidewords";
	public static final String MATCH_STARTED = "matchstarted";
	public static final String MATCH_CANCELLED = "matchcancelled";
	public static final String MATCH_FINISHED = "matchfinished";
	public static final String MATCH_ALREADY_ON = "matchalreadyon";
	public static final String DELETE_ALL = "deleteall";
	//KEY
	public static final String COMMAND_KEY = "!command:";
	
	public static final String SCOREBOARD_KEY = "@scoreboard:";
	public static final String FINAL_SCOREBOARD_KEY = "@scoreboard:@";
	public static final String WORDS_KEY = "@words:";
	public static final String ROUND_KEY = "@round:";
	public static final String MSGTYPE_KEY = "@msgtype:";
	public static final String CLIENT_LIST_KEY = "@clientlist:";
	public static final String HINT_KEY = "@hint:";
	public static final String START_TIMER_KEY = "@starttimer:";	
	public static final String SELECTED_WORD_KEY = "@selectedword:";
	
	public static final String GUESSED_KEY = "guessed|";
	public static final String WAITING_KEY = "waiting|";
	public static final String SYSTEM_KEY = "system|";
	public static final String JOIN_KEY = "join|";
	public static final String LEFT_KEY = "left|";
	
	public static final String WAIT_WORD = "e' il disegnatore!\nSta ancora scegliendo la parola...";
	public static final String WORD_CHOOSEN = "ha scelto, si gioca!";
	static final String CLIENT_GUESSED = "%s%s%s HA INDOVINATO LA PAROLA";
//	static final String CLIENT_WAITING = "%s%s %s";
	static final String CLIENT_JOINED = "e' entrato in partita";
	static final String CLIENT_LEFT = "%s%s%s ha abbandonato la convesazione";
	static final String SELECTED_WORD = "%s%sLa parola era: %s";
	
	static final String CLIENT_WELCOME = "Buongiorno, il tuo nome e': %s";
	static final String CLIENT_ALONE = "Buongiorno, il tuo nome e': %s \nNon ci sono altri utenti connessi...";
	static final String SEND_ROUND = "%s%d,%d";
	static final String SEND_HINT = "%s%s\n";
	static final String SEND_SCOREBOARD = "%s:%d/";
	
	public static String command(String commandType) {
		return COMMAND_KEY + commandType;
	}
	
	public static String welcome(String name, boolean alone) {
		if(alone)
			return String.format(CLIENT_ALONE, name);
		else
			return String.format(CLIENT_WELCOME, name);
	}
	
	public static String sendRound(int currentRound, int rounds) {
		return String.format(SEND_ROUND, ROUND_KEY, currentRound, rounds);
	}
	
	public static String playerGuessed(String name) {
		return String.format(CLIENT_GUESSED, MSGTYPE_KEY, GUESSED_KEY, name);
	}
	
	public static String playerWaiting(String name, String msg) {
		return MSGTYPE_KEY + WAITING_KEY + name + " " + msg;
	}
	
	public static String playerJoined(String name) {
		return MSGTYPE_KEY + JOIN_KEY + name + " " + CLIENT_JOINED;
	}
	
	public static String playerLeft(String name) {
		return String.format(CLIENT_LEFT, MSGTYPE_KEY, LEFT_KEY, name);
	}
	
	public static String notifySelectedWord(String selectedWord) {
		return String.format(SELECTED_WORD, MSGTYPE_KEY, WAITING_KEY, selectedWord);
	}
	
	public static String sendHint(String result) {
		return String.format(SEND_HINT, HINT_KEY, result);
	}
	
	public static String sendScoreBoard(String name, int score, boolean isPainter) {
		if(isPainter)
			return "^^^" + String.format(SEND_SCOREBOARD, name, score);
		else
			return String.format(SEND_SCOREBOARD, name, score);
	}
	
	public static String sendSelectedWord(String selectedWord) {
		return SELECTED_WORD_KEY + selectedWord;
	}
}
