package it.unibs.pajc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.server.ProcessMessage;
import it.unibs.pajc.server.Protocol;
import it.unibs.pajc.whiteboard.WhiteBoard;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

public class Protocol extends BaseModel implements Runnable{

	//TODO: controllare che non ci siano conflitti all'interno di match
	private static Match match;
	
	private static WhiteBoard whiteBoard = new WhiteBoard();;
	
	private static HashMap<String, ProcessMessage> commandMap;
	
	static {
		commandMap = new HashMap<String, ProcessMessage>();
		commandMap.put("!", new ProcessCommand());
		commandMap.put("?", new ProcessWord());
	}
	
	private static ArrayList<Protocol> clientList = new ArrayList<Protocol>();
	//private static ArrayList<Protocol> fakeClientList = new ArrayList<Protocol>();
	
	private ObjectInputStream is;			// Input stream
    private ObjectOutputStream os;			// Output stream
    private Object obj;
    
	private PrintWriter out;
	private Socket clientSocket;
	private String clientName;
	//TODO: per ora active è inutile
	private boolean active;
	private boolean painter;
	private boolean isStopped;
	
	public Protocol(Socket clientSocket, String clientName) {
		this.clientSocket = clientSocket;
		this.clientName = clientName;
		this.active = true;
		this.painter = false;
		this.isStopped = false;
		clientList.add(this);
	}
	
	public void close() {
		clientList.remove(this);
		if(os != null) {
			try {
				sendMsgToAll("%left|" + this.clientName + " ha abbandonato la conversazione");
				if(hasMatchStarted())
					match.removePlayer(this);
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("sono nel protocol");
//				e.printStackTrace();
			}
		}
		
		
	}

	@Override
	public void run() {
		try{
			is = new ObjectInputStream(clientSocket.getInputStream());
            os = new ObjectOutputStream(clientSocket.getOutputStream());
            
			System.out.printf("\nClient connesso: %s [%d] - Name: %s\n",
					clientSocket.getInetAddress(), clientSocket.getPort(),clientName);
			
			this.initialize();	
			
			String request;
			
			while((obj = is.readObject()) != null) {
				if(obj.getClass().equals(String.class)) {
					request = (String) obj;
					//hashmap
					String messageType = request.substring(0,1);
					ProcessMessage processor = commandMap.get(messageType);
					if(processor != null) {
						processor.process(this, request.substring(1));
					} else {
						String response = request;
//						sendMsgToAll(this, response);
						if(hasMatchStarted()) {
							//TODO: controllare bene il synchronized
							fireValuesChange(new ChangeEvent(response));
//							synchronized (this) {
//								match.checkWord(response);
//							}
							
						} else {
							sendMsgToAll(this, response);
						}
							
					}
					
					System.out.printf("\nRichiesta ricevuta: %s [%s]", request, clientName);
				} else {
					WhiteBoardLine line = (WhiteBoardLine) obj;
					this.addLine(line);
				}
			}
			
		} catch(SocketException e) {
			System.out.println("errore socket");
		}
		catch (IOException e) {
			System.out.printf("Errore durante i msg %s ", e);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
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
	
//	private void sendMsg(Protocol sender, String msg) {
//		this.out.printf("[%s]: %s\r\n", sender.clientName, msg);
//		this.out.flush();
//	}
	
	private void initialize() {
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String clientNameRequest;
		try {
			clientNameRequest = (String) is.readObject();
			synchronized (clientNameRequest) {
				if(getClientByName(clientNameRequest) == null) {
					clientName = clientNameRequest;
				}
			}	
			
			this.welcome();
			sendMsgToAll("%join|" + this.clientName + " è entrato in partita");
			
			if(hasMatchStarted()) {
				this.sendMsg("!matchalreadyon");
				match.addPlayer(this);
				
				ArrayList<WhiteBoardLine> lines = whiteBoard.getLines();
				if(!lines.isEmpty()) {
					lines.forEach((line) ->
						this.sendMsg(line)
					);
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getClientName() {
		return clientName;
	}
	
	private boolean hasMatchStarted() {
		return match != null ? true : false;
	}
	
	/*
	 * metodo per mandare msg normalmente
	 */
	protected void sendMsg(Protocol sender, String msg) {
		try {
			String output = String.format("[%s]: %s\n",sender.clientName, msg);

			this.os.writeObject(output);
			this.os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*
	 * metodo per mandare msg "speciali"
	 */
	protected void sendMsg(Object msg) {
		try {
			this.os.writeObject(msg);
			this.os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*
	 * metodo per mandare msg normalmente
	 */
	protected void sendMsgToAll(Protocol sender, String msg) {
		clientList.forEach((p) -> p.sendMsg(sender, msg));
	}
	/*
	 * metodo per mandare msg "speciali"
	 */
	public void sendMsgToAll(Object msg) {
		clientList.forEach((p) -> p.sendMsg(msg));
	}
	
	//non worka
	private void welcome() {
		if(clientList.size() > 1) {
			sendMsg(this, "Buongiorno, il tuo nome e': " + clientName);
		} else
			sendMsg(this, "Buongiorno, il tuo nome e': " + clientName + "\nNon ci sono altri utenti connessi...");
	}
	
	
	//da cambiare i private
	public synchronized void addLine(WhiteBoardLine line) {
		whiteBoard.add(line, this);
	}
	
	//prima era WhiteBoardLine line
//	public void sendLine(Object obj) {
//		try {
//			os.writeObject(obj);
//			os.flush();
//		} catch (IOException e) {
//			System.err.println("Error writing shape to client");
//		}
//	}
	
	public void clearAll() {
		whiteBoard.clearAll();
		sendMsgToAll("!deleteall");
	}
	
	public void startMatch() {
		//TODO: gestire meglio il thread
		//TODO: gestire il fatto che più client possano schiacciare il bottone
		match = new Match(clientList);
		Thread threadMatch = new Thread(match);
		threadMatch.start();
		
		sendMsgToAll("!matchstarted");
	}
//	
//	public Match getMatch() {
//		return match;
//	}
	
	public void setSelectedWord(String word) {
		match.setSelectedWord(word);
	}
	
	public void stopTimer() {
		isStopped = true;
	}

	public boolean isStopped() {
		return isStopped;
	}
	
	
}
