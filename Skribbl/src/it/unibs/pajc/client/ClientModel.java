package it.unibs.pajc.client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
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
		comunicator.addChangeListener(e -> fireValuesChange(new ChangeEvent(this)));
	}
	
	
	
	public void close() {
		if(comunicator != null)
			comunicator.close();
	}
	
	public void sendMsg(String msg) {
		if(msg.strip().length() > 0)
			comunicator.sendMsg(msg);
	}
	
//	private void explodeEvent() {
//		fireValuesChange(new ChangeEvent(this));
//	}
	
	public String updateChat() {	
		return comunicator.updateChat();
	}
	
	public ControllerComunicator getComunicator() {
		return this.comunicator;
	}
	
	public List getStrumenti() {
		Icon gomma = new ImageIcon("src/img/5.gif");
		List<Object> strumenti = new ArrayList<Object>();
		strumenti.add(Color.RED);
		strumenti.add(Color.BLACK);
		strumenti.add(gomma);
		return strumenti;
	}
}
