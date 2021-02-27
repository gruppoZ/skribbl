package it.unibs.pajc.client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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
			try {
				comunicator.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void sendMsg(Object msg) {
		try {
		if(msg.getClass().equals(String.class)) {
			String result = (String) msg;
			if((result.strip().length() > 0)) {
				comunicator.sendMsg(result);
			}
		} else
			comunicator.sendMsg(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	public synchronized void sendPaint(List<PolyLine> lines) {
//		try {
//			for (PolyLine polyLine : lines) {
//				comunicator.sendMsg(polyLine);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	
	public synchronized Object updateChat() {	
		return comunicator.updateChat();
	}
	
	public ControllerComunicator getComunicator() {
		return this.comunicator;
	}
	

	public static Color getColorByName(String colore) {
	    try {
	        return (Color)Color.class.getField(colore.toUpperCase()).get(null);
	    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	
	private static String[] colori = {
			"RED", "GREEN", "BLACK", "ORANGE", "PINK"
	};	
	
	private static final String RUBBER = "src/img/rubber.gif";
	private static final String TRASH = "src/img/trash.png";
	private static String[] icone = {
			RUBBER, TRASH
	};
	
	public boolean isRubber(String icona) {
		return RUBBER.equalsIgnoreCase(icona);
	}
	public boolean isTrash(String icona) {
		return TRASH.equalsIgnoreCase(icona);
	}
	
	public boolean isColor(String name) {
		boolean found = false;
		for (String colore : colori) {
			if(colore.equalsIgnoreCase(name))
				return found = true;
		}	
		return found;
	}
	
	public boolean isIcon(String icon) {
		boolean found = false;
		for (String icone : icone) {
			if(icone.equalsIgnoreCase(icon))
				return found = true;
		}	
		return found;
	}
	
	public List getStrumenti() {
		List<Object> strumenti = new ArrayList<Object>();
		
		for (String colore : colori) {
			strumenti.add(colore);
		}	
		for (String icone : icone) {
			strumenti.add(new ImageIcon(icone));
		}	
		
		return strumenti;
	}
}
