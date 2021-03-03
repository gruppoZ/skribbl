package it.unibs.pajc.server;

public class ProcessCommand implements ProcessMessage{

	private static final String TRASHCAN = "TRASHCAN";
	
	public void process(Protocol sender, String msg) {
		msg = msg.toLowerCase();
		int posStartMsg = msg.indexOf("]");
		posStartMsg += 1;
		msg = msg.substring(posStartMsg);
		if(msg.startsWith("quit"))
			sender.exit();
//		if(msg.startsWith("start"))
//			sender.active();
		if(msg.startsWith("TRASHCAN"))
			sender.sendMsg(sender, "@" + TRASHCAN);
		if(msg.startsWith("startgame"))
			sender.startGame();
	}

}
