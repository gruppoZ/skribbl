package it.unibs.pajc.client;

import java.io.*;
import java.net.*;
import javax.swing.event.ChangeEvent;
import it.unibs.pajc.core.BaseModel;

public class ControllerComunicator extends BaseModel {
	private String serverName = "localhost";
	private int port = 1234;
	//private PrintWriter out;
	private ObjectOutputStream out;
	private Socket server;
	private Object response;
	private Thread writer = new Thread(new Writer());
	
	/**
	 * Fa' partire il Thread del Writer.
	 * Il Writer si occuperà di far partire il thread del Listener
	 */
	public void start() {
		writer.start();
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
	private class Listener implements Runnable {

		public void run() {
			try(
					//BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
					ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			) {
				while((response = in.readObject()) != null) {
					fireValuesChange(new ChangeEvent(this));
				}
			}catch (IOException | ClassNotFoundException e) {
				System.out.printf("Errore: %s", e);
			}

		}
	}
	
	/**
	 * Thread: Inizializza la connsessione con il server, inizializza quindi la variabile out (ObjectOutputStream)
	 * 		   Fa partire il Thread Listener 
	 */
	private class Writer implements Runnable {	
		@Override
		public void run() {
			try {
				do {
					server = new Socket(serverName, port);
				} while(server == null);
				
				out = new ObjectOutputStream(server.getOutputStream());
				out.flush();
				 //out = new PrintWriter(server.getOutputStream(), true);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//TODO:chiuderemo out, in e socket con un metodo apposito
			
			//start di un thread per il listener
			Listener listener = new Listener();
			new Thread(listener).start();
		}
	}
	
}
