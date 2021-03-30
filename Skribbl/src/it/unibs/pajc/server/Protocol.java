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

	private static final String ERROR_SEND_MSG = "Errore nel sendMsg ";
	private static final String MESSAGE_FORMAT = "[%s]: %s\n";
	private static final String ERROR_MESSAGE = "Errore durante i msg %s ";
	private static final String REQUEST_RECEIVED = "\nRichiesta ricevuta: %s [%s]";
	private static final String CLIENT_CONNECTED = "\nClient connesso: %s [%d] - Name: %s\n";
	protected static final String ERR_CLOSE_SOCKET = "Errore chiusura socket - durante la richiesta del nome";
	protected static final String ERR_CLOSE_OS = "Errore chiusura os - protocol";
	protected static final String ERR_CLIENT_EXIT = "Il client (%s) e' uscito\n";
	
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
	
	public void close() {
		clientList.remove(this);
		sendClientList();
		
		if(os != null) {
			try {
				sendMsgToAll(ProcessUtils.playerLeft(clientName));
				if(hasMatchStarted())
					match.removePlayer(this);
				os.close();
				
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
            
			System.out.printf(CLIENT_CONNECTED,
					clientSocket.getInetAddress(), clientSocket.getPort(),clientName);
			
			this.initialize();	
			
			String request;
			
			while((obj = is.readObject()) != null) {
				if(obj.getClass().equals(String.class)) {
					
					request = (String) obj;
					int indexOf = request.toString().indexOf(":");
					String messageType = request.toString().substring(0, indexOf + 1);
					ProcessMessage processor = commandMap.get(messageType);
					
					if(processor != null) {
						processor.process(this, request.substring(indexOf + 1));
					} else {
						String response = request;
						
						if(hasMatchStarted()) 
							fireValuesChange(new ChangeEvent(response));
						 else 
							sendMsgToAll(this, response);
						
							
					}
					
					System.out.printf(REQUEST_RECEIVED, request, clientName);
				}
				if(obj.getClass().equals(WhiteBoardLine.class)) {
					WhiteBoardLine line = (WhiteBoardLine) obj;
					this.addLine(line);
				} else
					System.out.println(obj);
			}
			
		} catch(SocketException e) {
			System.err.printf(ERR_CLIENT_EXIT, this.clientName);
		}
		catch (IOException e) {
			System.out.printf(ERROR_MESSAGE, e);
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendClientList() {
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
			String output = String.format(MESSAGE_FORMAT,sender.clientName, msg);
			
			this.os.writeObject(output);
			this.os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * Metodo per mandare msg "speciali"
	 */
	protected void sendMsg(Object msg) {
		try {
			if(os != null) {
				this.os.writeObject(msg);
				this.os.flush();
			}
		} catch (IOException e) {
			System.err.println(ERROR_SEND_MSG + clientName);
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
		whiteBoard.add(line);
	}
	
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
		
		//controlla che ci siano almeno 2 giocatori
		
		if(clientList.size() > 1) {
			match = new Match(clientList);
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
