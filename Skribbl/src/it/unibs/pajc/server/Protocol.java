package it.unibs.pajc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.core.ProcessUtils;
import it.unibs.pajc.server.Protocol;
import it.unibs.pajc.whiteboard.WhiteBoard;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

public class Protocol extends BaseModel implements Runnable{

	protected static final String ERR_CLOSE_SOCKET = "Errore chiusura socket - durante la richiesta del nome";
	protected static final String ERR_CLOSE_OS = "Errore chiusura os - protocol";
	protected static final String ERR_CLIENT_EXIT = "errore socket - il client e' uscito";
	
	private static Match match;
	private static WhiteBoard whiteBoard = new WhiteBoard();
	private static HashMap<String, ProcessMessage> commandMap;
	
	static {
		commandMap = new HashMap<String, ProcessMessage>();
		commandMap.put(ProcessUtils.COMMAND_KEY, new ProcessCommand());
		commandMap.put(ProcessUtils.SERVER_WORD_KEY, new ProcessWord());
	}
	
	private static ArrayList<Protocol> clientList = new ArrayList<Protocol>();
	
	private ObjectInputStream is;			// Input stream
    private ObjectOutputStream os;			// Output stream
    private Object obj;
	private Socket clientSocket;
	private String clientName;
	private boolean isStopped;
	
	public Protocol(Socket clientSocket, String clientName) {
		this.clientSocket = clientSocket;
		this.clientName = clientName;
		this.isStopped = false;
		clientList.add(this);
	}
	
	//TODO: errore se ci sono sia sendClientList - sendMsgToAll(ProcessUtils.playerLeft(clientName));
	public void close() {
		clientList.remove(this);
		sendClientList();
		
		if(os != null) {
			try {
				sendMsgToAll(ProcessUtils.playerLeft(clientName));
				if(hasMatchStarted())
					match.removePlayer(this);
				os.close();
//				os = null; //aggiundo 29/03
			} catch (IOException e) {
				System.err.println(ERR_CLOSE_OS);
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
					int indexOf = request.toString().indexOf(":");
					String messageType = request.toString().substring(0, indexOf + 1);
					//String messageType = request.substring(0,1);
					ProcessMessage processor = commandMap.get(messageType);
					if(processor != null) {
						processor.process(this, request.substring(indexOf + 1));
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
				}
				if(obj.getClass().equals(WhiteBoardLine.class)) {
					WhiteBoardLine line = (WhiteBoardLine) obj;
					this.addLine(line);
				} else
					System.out.println(obj);
			}
			
		} catch(SocketException e) {
			System.err.println(ERR_CLIENT_EXIT);
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
	
	private void initialize() {
		String clientNameRequest;
		try {
			clientNameRequest = (String) is.readObject();
			synchronized (clientNameRequest) {
				if(getClientByName(clientNameRequest) == null) {
					clientName = clientNameRequest;
				}
			}	
			
			this.welcome();
			sendMsgToAll(ProcessUtils.playerJoined(this.clientName));
			
			this.sendClientList();
			
			if(hasMatchStarted()) {
				this.sendMsg(ProcessUtils.command(ProcessUtils.MATCH_ALREADY_ON));
				match.addPlayer(this);
				
				ArrayList<WhiteBoardLine> lines = whiteBoard.getLines();
				if(!lines.isEmpty()) {
					lines.forEach((line) ->
						this.sendMsg(line)
					);
				}
			}
		} catch(SocketException e) {
			System.err.println(ERR_CLOSE_SOCKET);
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
	
	private void sendClientList() {
		//ogni volta che un client entra invio a tutti la client list
		StringBuffer list = new StringBuffer();
		list.append(ProcessUtils.CLIENT_LIST_KEY);
		clientList.forEach((client) -> {
			list.append(client.getNickname() + "/");
		});
		sendMsgToAll(list.toString());
	}
	
	public String getNickname() {
		return clientName;
	}
	
	private boolean hasMatchStarted() {
		if(match == null) {
			return false;
		} else {
			if(match.isRunning)
				return true;
			else
				return false;
		}
	}
	
	/*
	 * Metodo per mandare msg normalmente
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
	 * mMetodo per mandare msg "speciali"
	 */
	protected void sendMsg(Object msg) {
		try {
			if(os != null) {
				this.os.writeObject(msg);
				this.os.flush();
			}
		} catch (IOException e) {
			System.err.println("Errore nel sendMsg " + clientName);
		}
		
	}
	/*
	 * Metodo per mandare msg normalmente
	 */
	protected void sendMsgToAll(Protocol sender, String msg) {
		clientList.forEach((p) -> p.sendMsg(sender, msg));
	}
	/*
	 * Metodo per mandare msg "speciali"
	 */
	public static void sendMsgToAll(Object msg) {
		clientList.forEach((p) -> p.sendMsg(msg));
	}
	
	private void welcome() {
		sendMsg(this, ProcessUtils.welcome(clientName, clientList.size() <= 1));
	}

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
		sendMsgToAll(ProcessUtils.command(ProcessUtils.DELETE_ALL));
		whiteBoard.clearAll();
	}
	
	public void setSelectedWord(String word) {
		match.setSelectedWord(word);
	}
	
	public void stopTimer() {
		isStopped = true;
	}

	public boolean isStopped() {
		return isStopped;
	}
	
	public void startMatch() {
		//TODO: gestire meglio il thread
		
		//controlla che ci siano almeno 2 giocatori
		if(clientList.size() > 1) {
			match = new Match(clientList);
//			executor = Executors.newCachedThreadPool();
			Thread threadMatch = new Thread(match);
			match.addChangeListener(e -> {
				threadMatch.interrupt();			
			});
	
			threadMatch.start();
	
			sendMsgToAll(ProcessUtils.command(ProcessUtils.MATCH_STARTED));
		} else {
			sendMsgToAll(ProcessUtils.command(ProcessUtils.MATCH_CANCELLED));
		}
		
	}
}
