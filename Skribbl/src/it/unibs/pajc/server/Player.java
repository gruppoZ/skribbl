package it.unibs.pajc.server;

public class Player {
	private Protocol client;
	private int score;
	private boolean painter;
	
	/**
	 * @param client
	 * @param score
	 * @param painter
	 */
	public Player(Protocol client) {
		this.client = client;
		this.score = 0;
		this.painter = false;
	}

	public Protocol getClient() {
		return client;
	}

	public int getScore() {
		return score;
	}

	public boolean isPainter() {
		return painter;
	}
	
	
	
	
}
