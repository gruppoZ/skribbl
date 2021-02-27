package it.unibs.pajc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	//private PrintWriter out;
	private ObjectOutputStream out;
	private Socket clientSocket;
	private String clientName;
	private boolean active;
	private Object request;;
	private ObjectInputStream in;
	public Protocol(Socket clientSocket, String clientName) {
		this.clientSocket = clientSocket;
		this.clientName = clientName;
		this.active = true;
		clientList.add(this);
	}
	
	public void close() {
		if(out != null)
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		clientList.remove(this);
		
		sendMsgToAll(this, " ha abbandonato la conversazione");
	}

	@Override
	public void run() {
//		try(
//				//BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//				
//		)
		try {
			this.in = new ObjectInputStream(clientSocket.getInputStream());
			//out = new PrintWriter(clientSocket.getOutputStream(), true);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			System.out.printf("\nClient connesso: %s [%d] - Name: %s\n",
					clientSocket.getInetAddress(), clientSocket.getPort(),clientName);
			
//			sendMsg(this, "Inserisci il tuo nome: ");
			String clientNameRequest = (String) in.readObject();
			synchronized (clientNameRequest) {
				if(getClientByName(clientNameRequest) == null) {
					clientName = clientNameRequest;
				}
			}
			
			
			welcome();
			
			//protocollo di comunicazione
			
			//while(((request = in.readObject()) != null) && active) {
			while((request = in.readObject()) != null) {
				if(request != null) {
					System.out.printf("\nRichiesta ricevuta: %s [%s]", request, clientName);
					
					Object response = request;
					//TODO: analizzare parola e vedere se giusta
					sendMsgToAll(this, response);
				}
				
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.printf("\nErrore durante i msg %s ", e);
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
	
	private void sendMsg(Protocol sender, Object msg) {
		if(msg instanceof String)
			msg = "[" + sender.clientName + "]: " + msg + "\r\n";
		try {
			this.out.writeObject(msg);
			this.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void sendMsgToAll(Protocol sender, Object msg) {
		clientList.forEach((p) -> p.sendMsg(sender, msg));
	}
	
	//non worka
	private synchronized void welcome() {
		String msg = "Buongiorno, il tuo nome e': ".concat(clientName);
		
		if(clientList.size() > 1)
			this.sendMsg(this, msg);
		if(clientList.size() == 1) {
			msg += "\nNon ci sono altri utenti connessi...";
			this.sendMsg(this, msg);
		}
	}
	

}
