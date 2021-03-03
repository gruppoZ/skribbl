package it.unibs.pajc.server;

public class ProcessCommand implements ProcessMessage{

	public void process(Protocol sender, String msg) {
		if(msg.startsWith("deleteall"))
			sender.clearAll();
		
		if(msg.startsWith("stoptimer"))
			sender.stopTimer();
		
		if(msg.startsWith("startmatch"))
			sender.startMatch();
	}

}
