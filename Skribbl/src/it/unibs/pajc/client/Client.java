package it.unibs.pajc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread{
		String serverName = "localhost";
		int port = 1234;
		
		String request = null;
		boolean anyMsg = false;
		public void run() {
			try(
					Socket server = new Socket(serverName, port);
					PrintWriter out = new PrintWriter(server.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));		
			) {
				ServerProtocol serverProtocol = new ServerProtocol(server);
				serverProtocol.start();
				
				
				//Invece che leggere dalla console, dovrò ricevere i messaggi da inviare al server da MainDesign-Main
				//MainDesign-Main per esempio ottiene il testo da inoltrare, da MainDesign-Chat
				BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
				
				//questo msg apparirà in chat con "benvenuto"
				System.out.println("Inserisci il tuo nome: ");
				
				while(!anyMsg) {
					while(getRequest() != null) {	//while(request != null)
						System.out.printf("\nRichiesta: %s", request);
						out.println(request);
						
						if("quit".equals(request))
							break;
						
						request = null;
					}
					
				}
				
				
				
			} catch (IOException e) {
				System.out.printf("Errore nel client, %s", e);
				System.err.println(e);
			}
		}
		
		public String getRequest() {
			return this.request;
		}
		public void setRequest(String request) {
			this.request = request;
		}
		

}
