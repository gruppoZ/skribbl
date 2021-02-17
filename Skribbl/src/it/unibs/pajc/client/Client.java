package it.unibs.pajc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {
		String serverName = "localhost";
		int port = 1234;
		
		try(
				Socket server = new Socket(serverName, port);
				PrintWriter out = new PrintWriter(server.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));		
		) {
			ServerProtocol serverProtocol = new ServerProtocol(server);
			serverProtocol.start();
			
			BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
			
			String request;
			//questo msg apparirà in chat con "benvenuto"
			System.out.println("Inserisci il tuo nome: ");
			
			while((request = consoleIn.readLine()) != null) {
				System.out.printf("\nRichiesta: %s", request);
				out.println(request);
				
				if("quit".equals(request))
					break;
			}
			
			
		} catch (IOException e) {
			System.out.printf("Errore nel client, %s", e);
			System.err.println(e);
		} 
	}
}