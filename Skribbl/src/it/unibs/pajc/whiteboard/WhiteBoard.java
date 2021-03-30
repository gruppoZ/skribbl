package it.unibs.pajc.whiteboard;

import java.util.ArrayList;
import java.util.LinkedList;

import it.unibs.pajc.server.Protocol;

public class WhiteBoard {
	
	private ArrayList<WhiteBoardLine> lines;
	
	public WhiteBoard() {
		this.lines = new ArrayList<WhiteBoardLine>();
	}

	public synchronized void add(WhiteBoardLine line) { //add a line to the whiteboard
		lines.add(line);
		Protocol.sendMsgToAll(line);
	}
	
	public void clearAll() {
		lines.clear();
	}

	public ArrayList<WhiteBoardLine> getLines() {
		return lines;
	}
	
	
	
}
