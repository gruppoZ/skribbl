package it.unibs.pajc.client;

public class ProcessScoreBoard implements ProcessMessageClient{

	@Override
	public void process(ClientView view, String msg) {
		
		boolean finishedMatch = false;
		if(msg.charAt(0) == '@') {
			finishedMatch = true;
			msg = msg.substring(msg.indexOf('@') + 1);
			view.initScoreboardView();
		}
		//se @ togli e poi farai il view. diverso
		
		view.resetScoreBoard();
		String[] scoreBoard = msg.split("/");
		boolean isPainter = false;
		String name = null;
		String score = null;
		
		for (String s : scoreBoard) {
			isPainter = false;
			//^^^ se starta con sti tre � il painter e gli passo a setScoreBoear un boolean
			name = s.substring(0, s.indexOf(":"));
			if(name.startsWith("^^^")) {
				isPainter = true;
				name = name.substring(3);
			}
			score = s.substring(s.indexOf(":") + 1);
			
			if(finishedMatch) {
				view.popupScoreboard(name, score);
			} else {
				view.setScoreBoard(name, score, isPainter);
			}
		}
		
//		if(finishedMatch)
//			view.displayScoreBoard();
	}

}
