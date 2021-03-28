package it.unibs.pajc.client;

import java.awt.Color;

public class ProcessMessageType implements ProcessMessageClient{

	@Override
	public void process(ClientView view, String msg) {
		int index = msg.indexOf("|");
		String msgType = msg.substring(0, index);
		msg = msg.substring(index + 1) + "\n";
		
		if(msgType.equals("left"))
			view.setTxtChat(msg, Color.RED);
		if(msgType.equals("join"))
			view.setTxtChat(msg, Color.GREEN);
		if(msgType.equals("guessed"))
			view.setTxtChat(msg, Color.GREEN);
		if(msgType.equals("waiting"))
			view.setTxtChat(msg, Color.LIGHT_GRAY);
		if(msgType.equals("system"))
			view.setTxtChat(msg, Color.DARK_GRAY);

		
	}

}
