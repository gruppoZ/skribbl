package it.unibs.pajc.client;

import javax.swing.JPanel;

public class ProcessCommand {
	
	private static final String TRASHCAN = "trashcan";
	private static final String STOPTIMER = "stoptimer";
	private static final String START_TIMER = "start_timer";
	//private static final String START_GAME = "startgame";  NON serve ?
	private static final String CHANGE_PAINTER = "changepainter";
	private static final String ROUND = "round";
	private static final String[] command = {TRASHCAN, START_TIMER, STOPTIMER, CHANGE_PAINTER, ROUND};
	
	
	public void process(String msg) {
		msg = getCommandFromString(msg);
		
		//if(msg.startsWith("TRASHCAN"))
			
	}
	public void process(ClientView view, String msg) {
		msg = getCommandFromString(msg);
		
		if(msg.startsWith(TRASHCAN))
			view.getPaintArea().cleanPaint();
		if(msg.startsWith(STOPTIMER))
			view.stopTimer();	
		if(msg.startsWith(START_TIMER))
			view.startTimer();	
		if(msg.startsWith(CHANGE_PAINTER)) {
			view.getPaintArea().setIsPainter();
			view.getPaintArea().cleanPaint();
			view.getPaintArea().useDefaultSizeStroke();;
		}
		if(msg.startsWith(ROUND)) {
			view.setRounds(msg.replaceAll(ROUND, ""));		
		}
			
	}
	
	
	protected boolean isCommand(String msg) {
		msg = getCommandFromString(msg);
		
		if(msg == null) {
			return false;
		}

		for (String cmd : command) {
			if(msg.startsWith(cmd))
				return true;
		}
		return false;	
	}
	
	/**
	 * Restituisce il comando contenuto nel msg
	 * @param msg
	 * @return
	 */
	protected String getCommandFromString(String msg) {
		msg = msg.toLowerCase();
		int posStartMsg = msg.indexOf("@");
		if(posStartMsg >= 0 ) {
			posStartMsg += 1;
			msg = msg.substring(posStartMsg);
			return msg;
		}
		 
		return null;
	}
	
	/**
	 * Restituisce la stringa filtrara, in modo da essere utilizzata per la Chat
	 * @param msg
	 * @return
	 */
	protected String getMsgChat(String msg) {
		String result;
		msg = msg.toLowerCase();
		int posStartMsg = msg.indexOf("@");
		result = msg.substring(0, posStartMsg) + msg.substring(posStartMsg+1);

		return result;
	}
	
}
