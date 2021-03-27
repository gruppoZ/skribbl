package it.unibs.pajc.server;

public interface ProcessMessage {

	void process(Protocol sender, String msg);
}
