package it.unibs.pajc.server;

import it.unibs.pajc.core.ProcessUtils;

public class ProcessCommand implements ProcessMessage{

	public void process(Protocol sender, String msg) {
		if(msg.startsWith(ProcessUtils.DELETE_ALL))
			sender.clearAll();
		
		if(msg.startsWith(ProcessUtils.STOP_TIMER))
			sender.stopTimer();
		
		if(msg.startsWith(ProcessUtils.MATCH_STARTED))
			sender.startMatch();
	}

}
