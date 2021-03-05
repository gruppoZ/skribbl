package it.unibs.pajc.server;

public class Player{
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

	public Protocol getProtocol() {
		return client;
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

//	@Override
//	public int compareTo(Player p) {
//		if(this.score > p.getScore())
//			return 1;
//		if(this.score == p.getScore() && this.client.getClientName().equals(p.getProtocol().getClientName()))
//			return 0;
//		return -1;
//	}

	
	
	
	
}
