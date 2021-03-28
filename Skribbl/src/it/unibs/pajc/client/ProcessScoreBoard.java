package it.unibs.pajc.client;

import it.unibs.pajc.core.ProcessUtils;

public class ProcessScoreBoard implements ProcessMessageClient{

	@Override
	public void process(ClientView view, String msg) {
		
		int position = 1;
		
		boolean finishedMatch = false;
		if(msg.charAt(0) == '@') {
			finishedMatch = true;
			msg = msg.substring(msg.indexOf('@') + 1);
			view.initScoreboardView();
		}
		
		view.resetScoreBoard();
		String[] scoreBoard = msg.split("/");
		boolean isPainter = false;
		String name = null;
		String score = null;
		
		for (String s : scoreBoard) {
			isPainter = false;
			
			name = s.substring(0, s.indexOf(":"));
			if(name.startsWith(ProcessUtils.PAINTER_SUFFIX)) {
				isPainter = true;
				name = name.substring(3);
			}
			score = s.substring(s.indexOf(":") + 1);
			
			if(finishedMatch) {
				view.popupScoreboard(name, score, String.valueOf(position));
			} else {
				view.setScoreBoard(name, score, isPainter, String.valueOf(position));
			}
			position++;
		}
	}

}
