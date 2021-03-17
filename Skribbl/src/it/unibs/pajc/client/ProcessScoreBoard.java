package it.unibs.pajc.client;

public class ProcessScoreBoard implements ProcessMessageClient{

	@Override
	public void process(ClientView view, String msg) {
		String messageType = msg.substring(0,1);
		//se @ togli e poi farai il view. diverso
		
		view.resetScoreBoard();
		String[] scoreBoard = msg.split("/");
		boolean isPainter = false;
		String name = null;
		String score = null;
		
		for (String s : scoreBoard) {
			isPainter = false;
			//^^^ se starta con sti tre è il painter e gli passo a setScoreBoear un boolean
			name = s.substring(0, s.indexOf(":"));
			if(name.startsWith("^^^")) {
				isPainter = true;
				name = name.substring(3);
			}
			score = s.substring(s.indexOf(":") + 1);
			view.setScoreBoard(name, score, isPainter);
		}
	}

}
