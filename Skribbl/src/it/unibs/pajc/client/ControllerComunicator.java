package it.unibs.pajc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;

public class ControllerComunicator extends BaseModel {
	private String serverName = "localhost";
	private int port = 1234;
	private PrintWriter out;
	private Socket server;
	private String response;
	private Thread writer = new Thread(new Writer());
	
	public void start() {
		writer.start();
	}
	
	public void close() {
		if(out != null)
			out.close();
	}
	
	public void sendMsg(String msg) {
		out.println(msg);
	}
	
	public String updateChat() {
		StringBuffer sb = new StringBuffer();
		sb.append(response);
		sb.append("\n");
		
		return sb.toString();
	}
	
	private class Listener implements Runnable {

		public void run() {
			try(
					BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			) {
				while((response = in.readLine()) != null) {
					fireValuesChange(new ChangeEvent(this));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private class Writer implements Runnable {	
		@Override
		public void run() {
			try {
				server = new Socket(serverName, port);
				 out = new PrintWriter(server.getOutputStream(), true);
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
