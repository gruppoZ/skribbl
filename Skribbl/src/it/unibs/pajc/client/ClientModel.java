package it.unibs.pajc.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
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

import it.unibs.pajc.core.BaseModel;
import it.unibs.pajc.core.ProcessUtils;

public class ClientModel extends BaseModel {
	
	protected static final String WRITER = "WRITER";
	protected static final String LISTENER = "LISTENER";
	
	protected static final String ERR_CLOSE_SOCKET = "Errore chiusura socket - comunocator";
	protected static final String ERR_SEND_MSG = "Errore invio Messaggio";
	protected static final String ERR_LISTENER = "Errore Listener";
	protected static final String ERR_COLOR_BY_NAME = "Errore getColorByName";
	
	private static final String FORMAT_DATE = "yyyy-MM-dd HH-mm-ss";
	
	protected static final String LOGO = "src/img/logo.gif";
	protected static final String BACKGROUND_GIF = "src/img/logo5.gif";
	public static final String CLOCK_GIF = "src/img/clock.gif";
	public static final String ICON_SEND = "src/img/right-arrow-20.png";	
	
	private static final String RUBBER = "src/img/rubber.gif";
	private static final String TRASHCAN = "src/img/trashcan.png";
	private static final String DIMENSION1 = "src/img/circle12.png";
	private static final String DIMENSION2 = "src/img/circle20.png";
	private static final String DIMENSION3 = "src/img/circle26.png";
	private static final String SAVE = "src/img/save.png";
	
	private static String[] icone = {
			RUBBER, TRASHCAN, DIMENSION1, DIMENSION2, DIMENSION3, SAVE
	};
	
	/**
	 * HashMap che associa a un messaggio di sistema (presente nella classe statica ProcessUtils) un'azione da intraprendere
	 */
	public static HashMap<String, ProcessMessageClient> commandMap;
	
	static {
		commandMap = new HashMap<String, ProcessMessageClient>();
		commandMap.put(ProcessUtils.COMMAND_KEY, new ProcessCommandClient());
		commandMap.put(ProcessUtils.ROUND_KEY, new ProcessRound());
		commandMap.put(ProcessUtils.WORDS_KEY, new ProcessWords());
		commandMap.put(ProcessUtils.SCOREBOARD_KEY, new ProcessScoreBoard());
		commandMap.put(ProcessUtils.MSGTYPE_KEY, new ProcessMessageType());
		commandMap.put(ProcessUtils.CLIENT_LIST_KEY, new ProcessClientList());
		commandMap.put(ProcessUtils.HINT_KEY, new ProcessHint());
		commandMap.put(ProcessUtils.START_TIMER_KEY, new ProcessTimer());
		commandMap.put(ProcessUtils.SELECTED_WORD_KEY, new ProcessSelectedWord());
	}
	
	private String nickname;
	private ClientComunicator comunicator;
	
	/**
	 * Costruttore del ClientModel. Crea un'istanza di ClientComunicator che si occupera' della connessione al server
	 */
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
	
	/**
	 * Metodo che passa al Comunicator un messaggio da inviare al server. Eventuali eccezioni sono gestite dal Comunicator
	 * @param msg
	 */
	public void sendMsg(Object msg) {
		if(msg.getClass().equals(String.class)) {
			if(String.valueOf(msg).strip().length() > 0) {
				comunicator.sendMsg(msg);
			}
		} else
			comunicator.sendMsg(msg);
	}
	
	public Object update() {
		return comunicator.update();	
	}
	
	public void close() {
		comunicator.close();
	}
	
	public ProcessMessageClient getProcess(String messageType) {
		return commandMap.get(messageType);
	}
	
	public static Set<String> getKeySet() {
		return commandMap.keySet();
	}
	
	public static Color getColorByName(String colore) {
	    try {
	        return (Color)Color.class.getField(colore.toUpperCase()).get(null);
	    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public static boolean isRubber(String icona) {
		return RUBBER.equalsIgnoreCase(icona);
	}
	public static boolean isTrash(String icona) {
		return TRASHCAN.equalsIgnoreCase(icona);
	}
	
	public static boolean isDimension1(String icona) {
		return DIMENSION1.equalsIgnoreCase(icona);
	}
	
	public static boolean isDimension2(String icona) {
		return DIMENSION2.equalsIgnoreCase(icona);
	}
	
	public static boolean isDimension3(String icona) {
		return DIMENSION3.equalsIgnoreCase(icona);
	}
	
	public static boolean isSave(String icon) {
		return SAVE.equalsIgnoreCase(icon);
	}
	
	public boolean isColor(String name) {
		boolean found = false;
		for (Colors colore : Colors.values()) {
			if(colore.getValue().equalsIgnoreCase(name))
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
	
	public ArrayList<Object> getStrumenti() {
		ArrayList<Object> strumenti = new ArrayList<Object>();
		
		for (Colors colore : Colors.values()) {
			strumenti.add(colore.getValue());
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
				
				SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE);  
			    Date date = new Date(); 
			    String url = "src/paints/PAINT_" + formatter.format(date) + ".png";
			    
				ImageIO.write(bimage , "PNG", new File(url));
				
				g2d.dispose();
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		});
		
		executor.shutdown();
	}
}
