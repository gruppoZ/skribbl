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
	ControllerComunicator comunicator;
	
	public ClientModel() {
		start();	
	}
	
	private void start() {
		comunicator = new ControllerComunicator();
		comunicator.start();
		comunicator.addChangeListener(e -> this.explodeEvent());
	}
	
	
	
	public void close() {
		if(comunicator != null)
			comunicator.close();
	}
	
	public void sendMsg(String msg) {
		if(msg.strip().length() > 0)
			comunicator.sendMsg(msg);
	}
	
	private void explodeEvent() {
		fireValuesChange(new ChangeEvent(this));
	}
	
	public String updateChat() {	
		return comunicator.updateChat();
	}
	
	public ControllerComunicator getComunicator() {
		return this.comunicator;
	}
}
