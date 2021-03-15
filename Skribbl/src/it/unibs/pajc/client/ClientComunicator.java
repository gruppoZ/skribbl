package it.unibs.pajc.client;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;

public class ClientComunicator extends PnlBase{
	
	private String serverName = "localhost";
	private int port = 1234;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Socket server;
	private Object response;
	private Writer writer = new Writer();
	private Listener listener = new Listener();
	private boolean isAvaible = false;
	
	/**
	 * Fa' partire il Thread del Writer.
	 * Il Writer si occuperà di far partire il thread del Listener
	 */
	public void start() {
		writer.addActionListener(e -> {
			
			System.out.println("Comunicator Writer - " + e.getActionCommand());
			fireActionListener(e);
		});
		listener.addActionListener(e -> {
			
			System.out.println("Comunicator Listener - " + e.getActionCommand());
			fireActionListener(e);
		});
		
		new Thread(writer).start();
	}
	
	protected synchronized boolean isAvaible() {
		return this.isAvaible;
	}
	
	/**
	 * Chiude lo Stream in uscita
	 * @throws IOException
	 */
	public void close() throws IOException {
		if(out != null)
			out.close();
	}
	
	/**
	 * Invia un Oggetto al Server
	 * @param msg
	 * @throws IOException
	 */
	public void sendMsg(Object msg) throws IOException {
		out.writeObject(msg);
	}
	
	/**
	 * Restituisce oggetti ricevuti dal Sever
	 * @return
	 */
	public synchronized Object updateChat() {
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
//					addActionListener(e-> {
//						e = new ActionEvent(e.getSource(), e.getID(), "Listener");
//						fireActionListener(e);
//					});
					fireActionListener(new ActionEvent("Listener", 2, "Listener"));
					
				}
			}catch (IOException | ClassNotFoundException e) {
				System.out.printf("Errore Listener: %s", e);
				connect();
			}
			}while(!isAvaible());

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
			}while(!success);
		}
	}
	
	/**
	 * Thread: Inizializza la connsessione con il server, inizializza quindi la variabile out (ObjectOutputStream)
	 * 		   Fa partire il Thread Listener 
	 */
	private class Writer extends PnlBase implements Runnable {	
		@Override
		public void run() {
			do {
				try {
						server = new Socket(serverName, port);
						isAvaible = true;
					
//					addActionListener(e-> {
//						e = new ActionEvent(e.getSource(), e.getID(), "Writer");
//						fireActionListener(e);
//					});
				
					out = new ObjectOutputStream(server.getOutputStream());
					out.flush();
					
					fireActionListener(new ActionEvent("Writer", 5, "Writer"));
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					isAvaible = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					isAvaible = false;
				}
			} while(!isAvaible);
			//TODO:chiuderemo out, in e socket con un metodo apposito
			
			//start di un thread per il listener
			
			if(isAvaible)
				new Thread(listener).start();
		}
	}
	
}