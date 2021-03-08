package it.unibs.pajc.client;

import java.awt.Color;

public class ProcessMsg implements ProcessMessageClient{

	@Override
	public void process(ClientView view, String msg) {
		int columIndex = msg.indexOf("|");
		String msgType = msg.substring(0, columIndex);
		msg = msg.substring(columIndex + 1) + "\n";
		
		if(msgType.equals("left"))
			view.setTxtChat(msg, Color.RED);
		if(msgType.equals("join"))
			view.setTxtChat(msg, Color.GREEN);
		if(msgType.equals("guessed"))
			view.setTxtChat(msg, Color.GREEN);
		if(msgType.equals("waiting"))
			view.setTxtChat(msg, Color.LIGHT_GRAY);
		
		
		
		
		
	}

}
