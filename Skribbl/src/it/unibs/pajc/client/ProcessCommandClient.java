package it.unibs.pajc.client;

public class ProcessCommandClient implements ProcessMessageClient{
	
	public void process(ClientView view, String msg) {
		if(msg.startsWith("deleteall"))
			view.clearAll();
		if(msg.startsWith("starttimer"))
			view.startTimer();
		if(msg.startsWith("stoptimer"))
			view.stopTimer();	
		if(msg.startsWith("changepainter"))
			view.setPainter();	
		if(msg.startsWith("hidewords"))
			view.hidePnlWords();	
		
	}
	
}
