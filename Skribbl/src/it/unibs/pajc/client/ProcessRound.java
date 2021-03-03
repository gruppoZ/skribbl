package it.unibs.pajc.client;

public class ProcessRound implements ProcessMessageClient{

	@Override
	public void process(ClientView view, String msg) {
		int columIndex = msg.indexOf(",");
		String currentRound = msg.substring(0, columIndex);
		String totRound = msg.substring(columIndex + 1);
		
		view.setRound(currentRound, totRound);
	}

}
