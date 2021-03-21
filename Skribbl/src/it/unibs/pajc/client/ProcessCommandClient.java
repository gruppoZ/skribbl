package it.unibs.pajc.client;

public class ProcessCommandClient implements ProcessMessageClient{
	
	public void process(ClientView view, String msg) {
		if(msg.startsWith("deleteall"))
			view.clearAll();
		
		if(msg.startsWith("starttimer")) {
			int index = msg.indexOf(",");
			String seconds = msg.substring(index + 1);
			view.startTimer(seconds);
		}
		if(msg.startsWith("hint")) {
			int index = msg.indexOf(":");
			String hint = msg.substring(index + 1);
			
			char[] result = new char[hint.length()*2];
			
			for(int i=0, j=0; i<hint.length()*2 && j<hint.length(); i+=2 , j++) {
				result[i] = hint.charAt(j);
				result[i+1] = ' ';
			}
			
			view.setWordWithHint(String.valueOf(result));			
		}
			
		if(msg.startsWith("stoptimer"))
			view.stopTimer();	
		if(msg.startsWith("changepainter"))
			view.setPainter();	
		if(msg.startsWith("hidewords"))
			view.hidePnlWords();	
		if(msg.startsWith("matchstarted"))
			view.matchStarted();	
		if(msg.startsWith("matchfinished"))
			view.matchFinished();
		if(msg.startsWith("matchcancelled"))
			view.matchCancelled();
		if(msg.startsWith("matchalreadyon"))
			view.matchAlreadyOn();
		
	}
	
}
