package it.unibs.pajc.client;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;

import it.unibs.pajc.client.panel.PnlBase;
import it.unibs.pajc.core.BaseModel;

public class ClientComunicator extends BaseModel {
	
	private static final String SERVER_NAME = "localhost";
	private static final int SERVER_PORT = 1234;
	
	private String serverName; 
	private int port;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Socket server;
	private Object response;
	private Writer writer;
	private Listener listener;
	private boolean isAvailable;
	
	
	public ClientComunicator() {
		this.serverName = SERVER_NAME;
		this.port = SERVER_PORT;
		isAvailable = false;
	}
	
	/**
	 * Fa' partire il Thread del Writer.
	 * Il Writer si occupera' di far partire il thread del Listener
	 */	
	public void start() {
		writer = new Writer();
		listener = new Listener();
		
		writer.addActionListener(e -> {
			fireActionListener(e);
		});
		listener.addActionListener(e -> {
			fireActionListener(e);
		});
		
		new Thread(writer).start();
	}
	
	protected boolean isAvailable() {
		return this.isAvailable;
	}
	
	/**
	 * Chiude la connessione socket
	 */
	public void close() {
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			System.err.println(ClientModel.ERR_CLOSE_SOCKET);
		}
	}
	
	/**
	 * 
	 * @param msg Inviato come generico oggetto al server
	 */
	public void sendMsg(Object msg) {
		try {
			out.writeObject(msg);
		} catch (IOException e) {
			System.err.println(ClientModel.ERR_SEND_MSG);
		}
	}
	
	/**
	 * Restituisce oggetti ricevuti dal Sever
	 * @return
	 */
	public synchronized Object update() {
		return response;
	}
	
	/**
	 * Thread: Inizializza la variabile in (ObjectInputStream)
	 * 		   Lancia un fireEvent nel caso di messaggi ricevuti dal server
	 */
	private class Listener extends PnlBase implements Runnable {
		
		public void run() {
			do {
				try{
					connect();
					while((response = in.readObject()) != null) {
						fireActionListener(new ActionEvent(ClientModel.LISTENER, 2, ClientModel.LISTENER));
					}
					
				} catch (IOException | ClassNotFoundException e) {
					System.err.printf(ClientModel.ERR_LISTENER);
					connect();
				} 
			} while(!isAvailable());
		}
		
		private void connect() {
			boolean success = false;
			do {
				try {
					in = new ObjectInputStream(server.getInputStream());
					success = true;
				} catch (Exception e) {
					success = false;
				} 
			} while(!success);
		}
	}
	
	/**
	 * Thread: Inizializza la connsessione con il server, inizializza quindi la variabile out (ObjectOutputStream)
	 * 		   Avvia il Thread Listener 
	 */
	private class Writer extends PnlBase implements Runnable {	
		@Override
		public void run() {
			do {
				try {
					server = new Socket(serverName, port);
					isAvailable = true;

					out = new ObjectOutputStream(server.getOutputStream());
					out.flush();
					
					fireActionListener(new ActionEvent(ClientModel.WRITER, 5, ClientModel.WRITER));
					
				} catch (UnknownHostException e) {
					isAvailable = false;
				} catch (IOException e) {
					isAvailable = false;
				}
			} while(!isAvailable);
			
			//Se la connessione e' avvenuta, avvia un thread per il listener
			
			if(isAvailable)
				new Thread(listener).start();
		}
	}
	
}