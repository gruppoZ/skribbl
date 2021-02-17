package it.unibs.pajc.server;

import java.io.IOException;
import java.net.*;

public class Server {
	public static void main(String[] args) {
		int port = 1234;
		System.out.println("Server in avvio...");
		
		try(
			ServerSocket server = new ServerSocket(port);
		) {
			int id = 0;
			while(true) {
				Socket client = server.accept();
				Protocol clientProtocol = new Protocol(client, "CLI#" + id++);
				Thread clientThread = new Thread(clientProtocol);
				clientThread.start();
			}
		} catch (IOException e) {
			System.out.printf("\nErrore di comunicazione: %s", e);
		}
		
		System.out.println("Server termianto");
	}
}
