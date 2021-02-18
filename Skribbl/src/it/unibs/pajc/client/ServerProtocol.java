package it.unibs.pajc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerProtocol extends Thread{
	private Socket socket;
	private BufferedReader input;
	
	public ServerProtocol(Socket s) throws IOException {
		this.socket = s;
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void run() {
		
		try {
			while(true) {
				String response = input.readLine();
				this.setResponse(response); 
				System.out.println(response);
			}
		} catch (IOException e) {
			System.out.printf("Errore: %s", e);
		} finally {
			try {
				input.close();
			} catch (Exception e) {
				System.out.printf("Errore: %s", e);
			}
		}
	}
	
	String result = null;
	
	public void setResponse(String response) {
		this.result = response;
	}
	public String getResponse() {
		return this.result;
	}
	
}
