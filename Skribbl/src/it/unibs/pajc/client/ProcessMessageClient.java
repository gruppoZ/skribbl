package it.unibs.pajc.client;

/**
 * Interfaccia che viene usata per i messaggi di sistema
 */
public interface ProcessMessageClient {
	void process(ClientView view, String msg);
}
