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
	private ControllerComunicator comunicator;
	
	public ClientModel() {
		start();	
	}
	
	/**
	 * Inizializza il Controller comunicator, stabliendo una connessione con il SERVER
	 */
	private void start() {
		comunicator = new ControllerComunicator();
		comunicator.start();
		comunicator.addChangeListener(e -> fireValuesChange(new ChangeEvent(this)));
	}
	
	/**
	 * chiude il Socket, chiude quindi la comunicazione con il SERVER
	 */
	protected void close() {
		if(comunicator != null)
			try {
				comunicator.close();
			} catch (IOException e) {
				System.out.println("CLIENT - Errore nella chiusura del Comunicator. \n ");
				e.printStackTrace();
			}
	}
	
	/**
	 * Permette di inviare un messaggio/Oggetto
	 * Nel caso di String viene controllato se è un COMANDO utilizzando il ProcessCommand
	 * @param msg
	 */
	protected void sendMsg(Object msg) {
		ProcessCommand command = new ProcessCommand();
		try {
		if(msg.getClass().equals(String.class)) {
			String result = (String) msg;
			if((result.strip().length() > 0)) {
				if(command.isCommand(result))
					comunicator.sendMsg("@" + result);
				else
					comunicator.sendMsg(result);
			}
		} else
			comunicator.sendMsg(msg);
		} catch (IOException e) {
			System.out.println("CLIENT - Errore nell'invio del Messaggio");
			e.printStackTrace();
		}
	}
	
	/**
	 * Permette all PaintArea di inviare il comando "Cancella tutte le Linee" agli altri Client
	 * dopo che si è premuto il bottone Cestino
	 */
	protected void sendTrashcan() {
		try {
			comunicator.sendMsg("@trashcan"); //TODO provare ad usare ProcessCommand
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Permette di inviare il NickName, scelto all'apertura dell'applicazione
	 * Utilizzato da ClientView
	 * @param msg
	 */
	protected void sendNickName(String msg) {
		try {
			if((msg.strip().length() > 0)) {
				comunicator.sendMsg(msg);
			}
		} catch (IOException e) {
			System.out.println("CLIENT - errore invio NICKNAME. ");
			e.printStackTrace();
		}
	}
	
	/**
	 * Restituisce eventuali messaggi (oggetti) ricevuti dal SERVER
	 * @return
	 */
	protected synchronized Object updateChat() {	
		Object response = comunicator.updateChat();
		
		if(response instanceof String) {
			StringBuffer sb = new StringBuffer();
			sb.append(String.valueOf(response));
			
			return sb.toString();
		}
		
		return response;
	}
	
	protected void startGame() {
		sendMsg("@startgame");
	}

	/**
	 * Permette di ottenere un Oggetto di tipo Color in base al nome del colore espresso come Stringa passata come parametro
	 * @param colore
	 * @return
	 */
	protected static Color getColorByName(String colore) {
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
	private static final String TRASHCAN = "src/img/trash.png";
	private static final String DIMENSION1 = "src/img/circle12.png";
	private static final String DIMENSION2 = "src/img/circle20.png";
	private static final String DIMENSION3 = "src/img/circle26.png";
	private static String[] icone = {
			RUBBER, TRASHCAN, DIMENSION1, DIMENSION2, DIMENSION3
	};
	
	/**
	 * Metodo utilizzato da PaintArea per verificare se si è premuti sul bottone Gomma
	 * @param icona
	 * @return
	 */
	protected boolean isRubber(String icona) {
		return RUBBER.equalsIgnoreCase(icona);
	}
	
	/**
	 * Metodo utilizzato da PaintArea per verificare se si è premuti sul bottone Cestino
	 * @param icona
	 * @return
	 */
	protected boolean isTrash(String icona) {
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
	
	/**
	 * Metodo utilizzato da PaintArea per verificare se si è premuti su un Bottone con un COLORE
	 * @param name
	 * @return
	 */
	protected boolean isColor(String name) {
		boolean found = false;
		for (String colore : colori) {
			if(colore.equalsIgnoreCase(name))
				return found = true;
		}	
		return found;
	}
	
	/**
	 * Metodo utilizzato da PaintArea per verificare se si è premuti su un Bottone con un'ICONA
	 * @param icon
	 * @return
	 */
	protected boolean isIcon(String icon) {
		boolean found = false;
		for (String icone : icone) {
			if(icone.equalsIgnoreCase(icon))
				return found = true;
		}	
		return found;
	}
	
	/**
	 * Metodo utilizzato da PaintArea, permette di ottenere la lista degli Oggetti per poter costruire visivamente
	 * il pannello degli Strumenti da Disegno
	 * @return
	 */
	protected List<Object> getStrumenti() {
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
