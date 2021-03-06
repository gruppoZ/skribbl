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

import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;

import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.server.ProcessCommand;
import it.unibs.pajc.server.ProcessMessage;
import it.unibs.pajc.whiteboard.WhiteBoard;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

public class ClientModel extends BaseModel{
	
	public static HashMap<String, ProcessMessageClient> commandMap;
	
	static {
		commandMap = new HashMap<String, ProcessMessageClient>();
		commandMap.put("!", new ProcessCommandClient());
		commandMap.put("/", new ProcessRound());
		commandMap.put("?", new ProcessWords());
	}
	
	private String serverName = "localhost";
	private int port = 1234;
	private Socket server;
	private PrintWriter out;
//	private BufferedReader in;
	private String response;
	
	private Object obj;
	private ObjectOutputStream os = null;
	
	public ClientModel() {
		try {
			this.server = new Socket(serverName, port);
//			this.out = new PrintWriter(server.getOutputStream(), true);
			
			this.os = new ObjectOutputStream(this.server.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO:chiuderemo out, in e socket con un metodo apposito
		
		//start di un thread per il listener
		new Thread(new Listener()).start();
		
	}
	
	public void sendMsg(String msg) {
//		out.println(msg);
		if(msg.strip().length() > 0) {
			try {
				os.writeObject(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void sendLine(WhiteBoardLine line) {
		try {
			os.writeObject(line);
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error writing object");
			e.printStackTrace();
		}
	}
	
	public Object update() {
		
//		if(obj.getClass().equals(String.class)) {
//			StringBuffer sb = new StringBuffer();
//			sb.append(obj.toString());
//			sb.append("\n");
//			
//			return sb.toString();
//		}
//		return obj.getClass().equals(WhiteBoardLine.class) ? obj : null;
		return obj;
		
	}
	
//	public WhiteBoardLine updateWhiteBoard() {
//		return obj.getClass().equals(WhiteBoardLine.class) ? (WhiteBoardLine)obj : null;
//	}
	
	public void close() {
		if(out != null)
			out.close();
//		try {
//			server.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private class Listener implements Runnable {
		@Override
		public void run() {
//			try(
////					BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
//					
//			) 
			try{
 				ObjectInputStream is = new ObjectInputStream(server.getInputStream());
				try {
					while((obj = is.readObject()) != null) {	
						fireValuesChange(new ChangeEvent(this));
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				while((response = in.readLine()) != null) {
//					fireValuesChange(new ChangeEvent(this));
//				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				close();
			}
		}

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
