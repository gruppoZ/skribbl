package it.unibs.pajc.server;

import java.io.*;
import java.util.*;

public class UtilsMatch {

	private final static String URI_FILE_WORD = "src/it/unibs/pajc/server/WORDS.dat";
	
	/**
	 * Legge da file e ritorna tramite ArrayList<String> la collezzione di parole da indovinare
	 * @return
	 */
	protected static ArrayList<String> readFile() {
		ArrayList<String> words = new ArrayList<>();
		File fileName = new File(URI_FILE_WORD);

        try(
                BufferedReader in = new BufferedReader(new FileReader(fileName))
            ) {

            String line;
            while((line = in.readLine()) != null) {
                words.add(line);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return words;
	}
	
	/**
	 * Resituisce la parola da indovinare, solo con la prima e l'ultima lettera visibile, le altre vengono nascoste con il
	 * carattere '_'
	 * @param word
	 * @return
	 */
	protected static char[] getInitWordForHint(String word) {
		char[] result = new char[word.length()];
		
		for(int i=0; i<result.length; i++) {
			result[i] = '_';
		}
		
		result[0] = word.charAt(0);
		result[result.length-1] = word.charAt(word.length()-1);
		
		return result;
	}
	
	/**
	 * In basse all'elenco delle parole in words, ne vengono scelte casualmente 3
	 * @param words
	 * @return
	 */
	protected static String getWordToGuess(ArrayList<String> words) {
		Random random = new Random();
		int[] indexes = new int[3];
		
		for(int i = 0; i<indexes.length; i++) {
			indexes[i] = random.nextInt(words.size());
			for(int j = 0; j < i; j++) {
				while(indexes[i] == indexes[j])
					indexes[i] = random.nextInt(words.size());
			}
		}
		
		StringBuffer result = new StringBuffer();
		result.append(ProcessMessage.WORD);
		for (int i : indexes) {
			result.append(words.get(i).trim() + ";");
		}
		
		return result.toString();
	}
	
	/**
	 * In base alla lunghezza della word, viene deciso quanti suggerimenti max da inviare ai client
	 * @param word
	 * @return max suggerrimenti
	 */
	protected static int calculateMaxHint(String word) {
		int max = 1;
		if(word.length() >= 9)
			max = 4;
		if(word.length() > 7 && word.length() < 9)
			max = 3;
		if(word.length() > 5 && word.length() <= 7)
			max = 2;
	
		return max;
	}
}
