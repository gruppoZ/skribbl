package it.unibs.pajc.server;

public class Player{
	private Protocol client;
	private int score;
	private boolean painter;
	private boolean guessed;
	/**
	 * @param client
	 * @param score
	 * @param painter
	 */
	public Player(Protocol client) {
		this.client = client;
		this.score = 0;
		this.painter = false;
		this.guessed = false;
	}

	public Protocol getProtocol() {
		return client;
	}

	public boolean hasGuessed() {
		return guessed;
	}

	public void setGuessed(boolean guessed) {
		this.guessed = guessed;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isPainter() {
		return painter;
	}
	
	public void setPainter(boolean painter) {
		this.painter = painter;
	}

	public void updateScore(int points) {
		this.score += points;
	}
	
	public String getName() {
		return this.client.getNickname();
	}

	public boolean equals(Protocol protocol) {
		return this.client.getNickname().equals(protocol.getNickname());
	}
	
	
	
	
	
}
