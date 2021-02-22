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
	private ServerProtocol serverProtocol;
	
	public ClientModel() {
		try {
			this.server = new Socket(serverName, port);
			this.out = new PrintWriter(server.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			
//			serverProtocol = new ServerProtocol(server);
//			serverProtocol.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//chiuderemo out, in e socket con un metodo apposito
		
		
	}
	
	public void sendMsg(String msg) {
		out.println(msg);
		fireValuesChange(new ChangeEvent(this));
	}
	
	public String updateChat() {
		return "ciao";
//		while(true) {
//			String response;
//			try {
//				response = in.readLine();
//				return response;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			}
//			
//		}
	}
	
	
	
}
