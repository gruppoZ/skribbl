package it.unibs.pajc.client;

public class ProcessTimer implements ProcessMessageClient {

	@Override
	public void process(ClientView view, String msg) {
		view.startTimer(msg);
	}

}
