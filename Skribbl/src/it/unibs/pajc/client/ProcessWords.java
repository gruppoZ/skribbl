package it.unibs.pajc.client;

public class ProcessWords implements ProcessMessageClient{

	@Override
	public void process(ClientSkribbl view, String msg) {
		msg = msg.substring(msg.indexOf(":") + 1);
		String[] words = msg.split(";");
		view.setWords(words);
		
	}

}
