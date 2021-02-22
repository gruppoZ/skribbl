package it.unibs.pajc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;

public class ClientModel extends BaseModel{
	private String serverName = "localhost";
	private int port = 1234;
	private Socket server;
	private PrintWriter out;
	private BufferedReader in;
	private String response;
	
	public ClientModel() {
		try {
			this.server = new Socket(serverName, port);
			this.out = new PrintWriter(server.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO:chiuderemo out, in e socket con un metodo apposito
		
		new Thread(new Listener()).start();
		
	}
	
	public void sendMsg(String msg) {
		if(msg.strip().length() > 0)
			out.println(msg);
	}
	
	public String updateChat() {
		StringBuffer sb = new StringBuffer();
		sb.append(response);
		sb.append("\n");
		
		return sb.toString();
	}
	
	private class Listener implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					response = in.readLine();
					fireValuesChange(new ChangeEvent(this));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

	}
	
	
	
}
