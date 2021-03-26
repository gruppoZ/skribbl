package it.unibs.pajc.client;

public class ProcessSelectedWord implements ProcessMessageClient{

	@Override
	public void process(ClientView view, String msg) {
		view.setSelectedWord(msg);
	}

}
