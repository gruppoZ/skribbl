package it.unibs.pajc.server;

public class ProcessWord implements ProcessMessage{

	@Override
	public void process(Protocol sender, String word) {
		sender.setSelectedWord(word);
	}

}
