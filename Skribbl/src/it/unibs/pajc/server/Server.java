package it.unibs.pajc.server;

import java.io.IOException;
import java.net.*;

public class Server {
	
	private static final String ERR_COMMUNICATION = "\nErrore di comunicazione: %s";
	private static final String SERVER_START = "Server in avvio...";
	private static final String SERVER_CLOSE = "Server termianto";
	private static final String CLIENT_PREFIX = "CLI#";

	public static void main(String[] args) {
		int port = 1234;
		
		System.out.println(SERVER_START);
		
		try(
			ServerSocket server = new ServerSocket(port);
		) {
			int id = 0;
			while(true) {
				Socket client = server.accept();
				Protocol clientProtocol = new Protocol(client, CLIENT_PREFIX + id++);
				Thread clientThread = new Thread(clientProtocol);
				clientThread.start();
			}
		} catch (IOException e) {
			System.err.printf(ERR_COMMUNICATION, e);
		}
		
		System.out.println(SERVER_CLOSE);
	}
}
