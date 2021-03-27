package it.unibs.pajc.server;

public interface ProcessMessage {
	static final String WORD = "word:";	//da modificare
	static final String STOP_TIMER = "stoptimer";
	static final String CHANGE_PAINTER = "changepainter";
	static final String HIDE_WORDS = "hidewords";
	static final String MATCH_STARTED = "matchstarted";
	static final String MATCH_CANCELLED = "matchcancelled";
	static final String MATCH_FINISHED = "matchfinished";
	static final String MATCH_ALREADY_ON = "matchalreadyon";
	static final String DELETE_ALL = "deleteall";
	//KEY
	static final String COMMAND_KEY = "!command:";
	
	static final String SCOREBOARD_KEY = "@scoreboard:";
	static final String FINAL_SCOREBOARD_KEY = "@scoreboard:@";
	static final String WORDS_KEY = "@words:";
	static final String ROUND_KEY = "@round:";
	static final String MSGTYPE_KEY = "@msgtype:";
	static final String CLIENT_LIST_KEY = "@clientlist:";
	static final String HINT_KEY = "@hint:";
	static final String START_TIMER_KEY = "@starttimer:";	
	static final String SELECTED_WORD_KEY = "@selectedword:";
	
	static final String GUESSED_KEY = "guessed|";
	static final String WAITING_KEY = "waiting|";
	static final String SYSTEM_KEY = "system|";
	static final String JOIN_KEY = "join|";
	static final String LEFT_KEY = "left|";
	
	static final String WAIT_WORD = "e' il disegnatore!\nSta ancora scegliendo la parola...";
	static final String WORD_CHOOSEN = "ha scelto, si gioca!";
	static final String CLIENT_GUESSED = "%s%s %s HA INDOVINATO LA PAROLA";
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
	
	void process(Protocol sender, String msg);
}
