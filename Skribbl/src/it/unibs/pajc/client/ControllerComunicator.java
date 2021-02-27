package it.unibs.pajc.client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
	
	public void start() {
		writer.start();
	}
	
	public void close() throws IOException {
		if(out != null)
			out.close();
	}
	
//	public void sendMsg(String msg) {
//		out.println(msg);
//	}
	public void sendMsg(Object msg) throws IOException {
		out.writeObject(msg);
	}
	
	public synchronized Object updateChat() {
		if(response instanceof String) {
			StringBuffer sb = new StringBuffer();
			sb.append(String.valueOf(response));
			
			return sb.toString();
		}
		
		return response;
	}
	
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
	
	
	private class Writer implements Runnable {	
		@Override
		public void run() {
			try {
				server = new Socket(serverName, port);
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
