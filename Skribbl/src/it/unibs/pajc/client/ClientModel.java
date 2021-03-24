package it.unibs.pajc.client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.server.ProcessCommand;
import it.unibs.pajc.server.ProcessMessage;
import it.unibs.pajc.whiteboard.WhiteBoard;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

public class ClientModel extends PnlBase{
	
	public static HashMap<String, ProcessMessageClient> commandMap;
	
	static {
		commandMap = new HashMap<String, ProcessMessageClient>();
		commandMap.put("!", new ProcessCommandClient());
		commandMap.put("/", new ProcessRound());
		commandMap.put("?", new ProcessWords());
		commandMap.put("@", new ProcessScoreBoard());
		commandMap.put("%", new ProcessMsg());
		commandMap.put("+", new ProcessClientList());
		commandMap.put("*", new ProcessHint());
		commandMap.put("<", new ProcessTimer());
	}
	
	private String nickname;
	private ClientComunicator comunicator;
	
	public ClientModel() {
		comunicator = new ClientComunicator();
		comunicator.start();
		comunicator.addActionListener(e -> fireActionListener(e));
	}
	
	protected boolean isConnectionAvailable() {
		return comunicator.isAvailable();
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public void sendMsg(Object msg) {
//		out.println(msg);
		try {
			if(msg.getClass().equals(String.class)) {
				if(String.valueOf(msg).strip().length() > 0) {
					comunicator.sendMsg(msg);
				}
			} else
				comunicator.sendMsg(msg);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object update() {

		return comunicator.update();
		
	}
	
//	public WhiteBoardLine updateWhiteBoard() {
//		return obj.getClass().equals(WhiteBoardLine.class) ? (WhiteBoardLine)obj : null;
//	}
	
	public void close() {
		try {
			comunicator.close();
		} catch (IOException e) {
			System.out.println("Close del client model");
		}
	}
	
	public ProcessMessageClient getProcess(String messageType) {
		return commandMap.get(messageType);
	}
	
	public static Set<String> getKeySet() {
		return commandMap.keySet();
	}
	
	//gestione pnlStrumenti
	
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
	private static final String TRASHCAN = "src/img/trashcan.png";
	private static final String DIMENSION1 = "src/img/circle12.png";
	private static final String DIMENSION2 = "src/img/circle20.png";
	private static final String DIMENSION3 = "src/img/circle26.png";
	private static String[] icone = {
			RUBBER, TRASHCAN, DIMENSION1, DIMENSION2, DIMENSION3
	};
	
	public boolean isRubber(String icona) {
		return RUBBER.equalsIgnoreCase(icona);
	}
	public boolean isTrash(String icona) {
		return TRASHCAN.equalsIgnoreCase(icona);
	}
	
	protected boolean isDimension1(String icona) {
		return DIMENSION1.equalsIgnoreCase(icona);
	}
	
	protected boolean isDimension2(String icona) {
		return DIMENSION2.equalsIgnoreCase(icona);
	}
	
	protected boolean isDimension3(String icona) {
		return DIMENSION3.equalsIgnoreCase(icona);
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
	
	public ArrayList getStrumenti() {
		ArrayList<Object> strumenti = new ArrayList<Object>();
		
		for (String colore : colori) {
			strumenti.add(colore);
		}	
		for (String icone : icone) {
			strumenti.add(new ImageIcon(icone));
		}	
		
		return strumenti;
	}
	
	
	
}
