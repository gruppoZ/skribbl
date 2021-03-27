package it.unibs.pajc.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import it.unibs.pajc.client.panel.PnlBase;
import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.server.ProcessCommand;
import it.unibs.pajc.server.ProcessMessage;
import it.unibs.pajc.whiteboard.WhiteBoard;
import it.unibs.pajc.whiteboard.WhiteBoardLine;

public class ClientModel extends PnlBase{
	
	protected static final String LOGO = "src/img/logo.gif";
	protected static final String BACKGROUND_GIF = "src/img/logo5.gif";
	public static final String ICON_SEND = "src/img/right-arrow-20.png";
	
	private static String[] colori = {
			"RED", "GREEN", "BLACK", "ORANGE", "PINK"
	};	
	
	private static final String RUBBER = "src/img/rubber.gif";
	private static final String TRASHCAN = "src/img/trashcan.png";
	private static final String DIMENSION1 = "src/img/circle12.png";
	private static final String DIMENSION2 = "src/img/circle20.png";
	private static final String DIMENSION3 = "src/img/circle26.png";
	private static final String SAVE = "src/img/save.png";
	
	private static String[] icone = {
			RUBBER, TRASHCAN, DIMENSION1, DIMENSION2, DIMENSION3, SAVE
	};
	
	public static HashMap<String, ProcessMessageClient> commandMap;
	
	static {
		commandMap = new HashMap<String, ProcessMessageClient>();
		commandMap.put(ProcessMessage.COMMAND_KEY, new ProcessCommandClient());
		commandMap.put(ProcessMessage.ROUND_KEY, new ProcessRound());
		commandMap.put(ProcessMessage.WORDS_KEY, new ProcessWords());
		commandMap.put(ProcessMessage.SCOREBOARD_KEY, new ProcessScoreBoard());
		commandMap.put(ProcessMessage.MSGTYPE_KEY, new ProcessMsg());
		commandMap.put(ProcessMessage.CLIENT_LIST_KEY, new ProcessClientList());
		commandMap.put(ProcessMessage.HINT_KEY, new ProcessHint());
		commandMap.put(ProcessMessage.START_TIMER_KEY, new ProcessTimer());
		commandMap.put(ProcessMessage.SELECTED_WORD_KEY, new ProcessSelectedWord());
//		commandMap.put("/", new ProcessRound());
//		commandMap.put("?", new ProcessWords());
//		commandMap.put("@", new ProcessScoreBoard());
//		commandMap.put("%", new ProcessMsg());
//		commandMap.put("+", new ProcessClientList());
//		commandMap.put("*", new ProcessHint());
//		commandMap.put("<", new ProcessTimer());
//		commandMap.put(";", new ProcessSelectedWord());
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
	
	public boolean isRubber(String icona) {
		return RUBBER.equalsIgnoreCase(icona);
	}
	public boolean isTrash(String icona) {
		return TRASHCAN.equalsIgnoreCase(icona);
	}
	
	public boolean isDimension1(String icona) {
		return DIMENSION1.equalsIgnoreCase(icona);
	}
	
	public boolean isDimension2(String icona) {
		return DIMENSION2.equalsIgnoreCase(icona);
	}
	
	public boolean isDimension3(String icona) {
		return DIMENSION3.equalsIgnoreCase(icona);
	}
	
	public boolean isSave(String icon) {
		return SAVE.equalsIgnoreCase(icon);
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
	
	/**
	 * Salva il contenuto del JPanel
	 * @param panel
	 */
	public void savePaint(JPanel panel) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		executor.submit(() -> {
			Graphics2D g2d;
			try {
				BufferedImage bimage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
				g2d = bimage.createGraphics();
				panel.print( g2d );			 
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");  
			    Date date = new Date(); 
			    String url = "src/paints/PAINT_" + formatter.format(date) + ".png";
			    
				ImageIO.write(bimage , "PNG", new File(url));
				
				g2d.dispose();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		});
		
		executor.shutdown();
	}
	
}
