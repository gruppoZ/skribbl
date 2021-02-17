package it.unibs.pajc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import it.unibs.pajc.server.ProcessMessage;
import it.unibs.pajc.server.Protocol;

public class Protocol implements Runnable{
	private static HashMap<String, ProcessMessage> commandMap;
	
	static {
		commandMap = new HashMap<String, ProcessMessage>();
	}
	
	private static ArrayList<Protocol> clientList = new ArrayList<Protocol>();
	
	private PrintWriter out;
	private Socket clientSocket;
	private String clientName;
	private boolean active;
	
	public Protocol(Socket clientSocket, String clientName) {
		this.clientSocket = clientSocket;
		this.clientName = clientName;
		this.active = active;
		clientList.add(this);
	}
	
	public void close() {
		if(out != null)
			out.close();
		
		clientList.remove(this);
		
		sendMsgToAll(this, " ha abbandonato la conversazione");
	}

	@Override
	public void run() {
		try(
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
		) {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			System.out.printf("Client connesso: %s [%d] - Name: %s\n",
					clientSocket.getInetAddress(), clientSocket.getPort(),clientName);
			sendMsg(this, "Inserisci il tuo nome: ");
			String clientNameRequest = in.readLine();
			synchronized (clientNameRequest) {
				if(getClientByName(clientNameRequest) == null) {
					clientName = clientNameRequest;
				}
			}
			
			sendMsg(this, "Buongiorno, il tuo nome e': " + clientName);
			
			//protocollo di comunicazione
			String request;
			while(((request = in.readLine()) != null) && active) {
				System.out.printf("\nRichiesta ricevuta: %s [%s]", request, clientName);
				
				String response = request;
				//TODO: analizzare parola e vedere se giusta
				sendMsgToAll(this, response);
			}
		} catch (IOException e) {
			System.out.printf("Errore durante i msg %s ", e);
		} finally {
			this.close();
		}
		
		
	}
	
	public Protocol getClientByName(String clientName) {
		for (Protocol p : clientList) {
			if(p.clientName.equals(clientName))
				return p;
		}
		return null;
	}
	
//	public void sendMsgTo(Protocol sender, Protocol dest, String msg) {
//		if(dest != null)
//			dest.sendMsg(sender, msg + "***");
//	}
	
	private void sendMsg(Protocol sender, String msg) {
		this.out.printf("[%s]: %s\r\n", sender.clientName, msg);
		this.out.flush();
	}
	
	private void sendMsgToAll(Protocol sender, String msg) {
		clientList.forEach((p) -> p.sendMsg(sender, msg));
	}
	

}
