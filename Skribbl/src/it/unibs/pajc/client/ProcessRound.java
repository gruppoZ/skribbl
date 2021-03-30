package it.unibs.pajc.client;

public class ProcessRound implements ProcessMessageClient{

	@Override
	public void process(ClientSkribbl view, String msg) {
		int index = msg.indexOf(",");
		String currentRound = msg.substring(0, index);
		String totRound = msg.substring(index + 1);
		
		view.setRound(currentRound, totRound);
	}

}
