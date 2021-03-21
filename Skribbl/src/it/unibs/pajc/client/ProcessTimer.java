package it.unibs.pajc.client;

public class ProcessTimer implements ProcessMessageClient {

	@Override
	public void process(ClientView view, String msg) {
		int index = msg.indexOf(",");
		String seconds = msg.substring(index + 1);
		view.startTimer(seconds);
	}

}
