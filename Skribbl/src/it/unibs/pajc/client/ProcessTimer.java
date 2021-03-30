package it.unibs.pajc.client;

public class ProcessTimer implements ProcessMessageClient {

	@Override
	public void process(ClientSkribbl view, String msg) {
		view.startTimer(msg);
	}

}
