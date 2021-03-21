package it.unibs.pajc.client;

public class ProcessCommandClient implements ProcessMessageClient{
	
	public void process(ClientView view, String msg) {
		switch(msg) {
			case "deleteall":
				view.clearAll();
				break;
			case "stoptimer":
				view.stopTimer();
				break;
			case "changepainter":
				view.setPainter();
				break;
			case "hidewords":
				view.hidePnlWords();
				break;
			case "matchstarted":
				view.matchStarted();
				break;
			case "matchfinished":
				view.matchFinished();
				break;
			case "matchcancelled":
				view.matchCancelled();
				break;
			case "matchalreadyon":
				view.matchAlreadyOn();
				break;
			default:
				System.out.println("errore nello switch");
		}
	}
	
}
