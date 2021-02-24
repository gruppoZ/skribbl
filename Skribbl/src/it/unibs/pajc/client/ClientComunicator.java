package it.unibs.pajc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;

public class ClientComunicator extends BaseModel{
	
	private Socket socket;
	private BufferedReader input;
	private String response;
	
	public ClientComunicator(Socket s) {
		this.socket = s;
		try {
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		try {
			while((response = input.readLine()) != null) {
				fireValuesChange(new ChangeEvent(this));
			}
		}catch (IOException e) {
			System.out.printf("Errore: %s", e);
		} finally {
			try {
				input.close();
			} catch (Exception e) {
				System.out.printf("Errore: %s", e);
			}
		}
	}
	
}
