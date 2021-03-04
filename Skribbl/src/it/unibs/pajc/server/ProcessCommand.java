package it.unibs.pajc.server;

public class ProcessCommand implements ProcessMessage{

	private static final String TRASHCAN = "trashcan";
	private static final String SELECTED_WORD = "selectedword";
	
	
	public void process(Protocol sender, String msg) {
		msg = msg.toLowerCase();
		int posStartMsg = msg.indexOf("@");
		posStartMsg += 1;
		msg = msg.substring(posStartMsg);
		if(msg.startsWith("quit"))
			sender.exit();
//		if(msg.startsWith("start"))
//			sender.active();
		if(msg.startsWith(TRASHCAN))
			sender.sendMsgToAll(sender, "@" + TRASHCAN);
		if(msg.startsWith("startgame"))
			sender.startGame();
		if(msg.startsWith(SELECTED_WORD)) {
			msg = msg.substring(msg.indexOf(":")+1);
			if(Protocol.getMatch() != null)
				Protocol.getMatch().setSelectedWord(msg);
		}		
	}
}
