package it.unibs.pajc.client;

import java.awt.Color;

import it.unibs.pajc.core.ProcessUtils;

public class ProcessMessageType implements ProcessMessageClient{

	@Override
	public void process(ClientView view, String msg) {
		int index = msg.indexOf(ProcessUtils.MSG_TYPE_SEPARATOR);
		String msgType = msg.substring(0, index);
		msg = msg.substring(index + 1) + "\n";
		
		if(msgType.equals(ProcessUtils.LEFT_KEY))
			view.setTxtChat(msg, Color.RED);
		if(msgType.equals(ProcessUtils.JOIN_KEY))
			view.setTxtChat(msg, Color.GREEN);
		if(msgType.equals(ProcessUtils.GUESSED_KEY))
			view.setTxtChat(msg, Color.GREEN);
		if(msgType.equals(ProcessUtils.WAITING_KEY))
			view.setTxtChat(msg, Color.GRAY);
		if(msgType.equals(ProcessUtils.SYSTEM_KEY))
			view.setTxtChat(msg, Color.CYAN);

		
	}

}
