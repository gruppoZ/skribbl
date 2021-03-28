package it.unibs.pajc.client;

import it.unibs.pajc.core.ProcessUtils;

public class ProcessCommandClient implements ProcessMessageClient{
	
	private static final String DEFAULT_MSG = "Errore nei messaggi di sistema";

	public void process(ClientView view, String msg) {
		switch(msg) {
			case ProcessUtils.DELETE_ALL:
				view.clearAll();
				break;
			case ProcessUtils.STOP_TIMER:
				view.stopTimer();
				break;
			case ProcessUtils.CHANGE_PAINTER:
				view.setPainter();
				break;
			case ProcessUtils.HIDE_WORDS:
				view.hidePnlWords();
				break;
			case ProcessUtils.MATCH_STARTED:
				view.matchStarted();
				break;
			case ProcessUtils.MATCH_FINISHED:
				view.matchFinished();
				break;
			case ProcessUtils.MATCH_CANCELLED:
				view.matchCancelled();
				break;
			case ProcessUtils.MATCH_ALREADY_ON:
				view.matchAlreadyOn();
				break;
			default:
				System.out.println(DEFAULT_MSG);
		}
	}
	
}
