package it.unibs.pajc.whiteboard;

import java.util.ArrayList;
import java.util.LinkedList;

import it.unibs.pajc.server.Protocol;

public class WhiteBoard {
	
	private LinkedList<WhiteBoardLine> lines;
	private ArrayList<Protocol> clientList;
	
	public WhiteBoard(ArrayList<Protocol> clientList) {
		this.lines = new LinkedList<WhiteBoardLine>();
		this.clientList = clientList;
	}
	
	//togliere synchronized
	public synchronized void add(WhiteBoardLine line, Protocol protocol) { //add a line to the whiteboard
		lines.add(line);
		for(Protocol p: clientList) {
			p.sendLine(line);
		}
	}
	
	public void clearAll() {
		lines.clear();
	}
	
}
