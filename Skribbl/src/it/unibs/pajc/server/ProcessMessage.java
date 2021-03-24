package it.unibs.pajc.server;

public interface ProcessMessage {
	static final String WORD = "?word:";	
	static final String START_TIMER = "<starttimer,";	
	static final String STOP_TIMER = "!stoptimer";
	static final String CHANGE_PAINTER = "!changepainter";
	static final String HIDE_WORDS = "!hidewords";
	
	void process(Protocol sender, String msg);
}
