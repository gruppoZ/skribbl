package it.unibs.pajc.client;

public class ProcessSelectedWord implements ProcessMessageClient{

	@Override
	public void process(ClientSkribbl view, String msg) {
		view.setSelectedWord(msg);
	}

}
