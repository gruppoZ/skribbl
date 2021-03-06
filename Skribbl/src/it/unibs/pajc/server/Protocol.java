package it.unibs.pajc.server;

import java.io.*;
import java.net.*;
import java.util.*;
import it.unibs.pajc.server.Protocol;

public class Protocol implements Runnable{
	private static HashMap<String, ProcessMessage> commandMap;
	
	static {
		commandMap = new HashMap<String, ProcessMessage>();
		commandMap.put("@", new ProcessCommand());
	}
	
	private static ArrayList<Protocol> clientList = new ArrayList<Protocol>();
	
	private ObjectOutputStream out;
	private Socket clientSocket;
	private String clientName;
	private boolean active;
	private Object request;;
	private ObjectInputStream in;
	private boolean inGame = false;
	private Thread matchThread;
	private static Match match;
	
	public Protocol(Socket clientSocket, String clientName) {
		this.clientSocket = clientSocket;
		this.clientName = clientName;
		this.active = true;
		clientList.add(this);
	}
	
	protected String getClientName() {
		return this.clientName;
	}
	protected synchronized Protocol getProtocol() {
		return this;
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
	protected synchronized void exit() {
		this.active = false;
	}
	protected synchronized void active() {
		this.active = true;
	}
	@Override
	public void run() {		
		Thread writer = new Thread(new Writer());
		writer.start();
		
		System.out.printf("\nClient connesso: %s [%d] - Name: %s\n",
				clientSocket.getInetAddress(), clientSocket.getPort(),clientName);
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
	
	/**
	 * Invia un messaggio con "mittente" il client
	 * @param sender
	 * @param msg
	 */
	protected void sendMsg(Protocol sender, Object msg) {
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
	
	/**
	 * Invia un messaggio senza specificare il mittente
	 * @param sender
	 * @param msg
	 */
	protected void sendMsg(Object msg) {
		try {
			this.out.writeObject(msg);
			this.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Invia un messaggio a tutti, specificando il mittente
	 * @param sender
	 * @param msg
	 */
	protected void sendMsgToAll(Protocol sender, Object msg) {
		clientList.forEach((p) -> p.sendMsg(sender, msg));
	}
	
	/**
	 * Invia un messaggio a tutti senza specificare il mittente
	 * @param msg
	 */
	protected void sendMsgToAll(Object msg) {
		clientList.forEach((p) -> p.sendMsg(msg));
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
	
	protected boolean checkInGame() {
		if(matchThread != null) {
			try {
				if(matchThread.isAlive())
					inGame = true;
			} catch(Exception e) {
				inGame = false;
			}

		} else
			inGame = false;
		
		return inGame;
	}
	
	protected synchronized void startGame() {
		match = new Match(clientList);
		matchThread = new Thread(match);
		matchThread.start();
	}
	
	protected static Match getMatch() {
		return match;
	}
	
	private class Listener implements Runnable {
		public void run() {
			try{
				Protocol protocol = getProtocol();
				
				in = new ObjectInputStream(clientSocket.getInputStream());
				
				String clientNameRequest = (String) in.readObject();
				synchronized (clientNameRequest) {
					if(getClientByName(clientNameRequest) == null) {
						clientName = clientNameRequest;
					}
				}
				
				welcome();
				
				Object response;
				
				while((request = in.readObject()) != null) {
					System.out.printf("\nRichiesta ricevuta: %s [%s]", request, clientName);
					
					if(request.getClass().equals(String.class)) {
						String messageType = String.valueOf(request).substring(0,1);
						ProcessMessage processor = commandMap.get(messageType);

//							if(active) { //Utile se uso un comando di tipo: disattiva/attiva Client
//								response = request;
//								sendMsgToAll(protocol, response);
//							}
						
						if(processor != null)
							processor.process(protocol, String.valueOf(request).substring(1));
						
							
					} else {
						response = request;
						//TODO: analizzare parola e vedere se giusta
						sendMsgToAll(protocol, response);
					}
				
				}
				
			} catch (IOException | ClassNotFoundException e) {
				System.out.printf("\nErrore: %s", e);
			} finally {
				close();
			}

		}
	}
	
	
	private class Writer implements Runnable {	
		@Override
		public void run() {
			try {
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				out.flush();
			
			//start di un thread per il listener
			Listener listener = new Listener();
			new Thread(listener).start();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
