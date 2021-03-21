package it.unibs.pajc.client;

public class ProcessHint implements ProcessMessageClient {

	@Override
	public void process(ClientView view, String msg) {
		String hint = msg;
		
		char[] result = new char[hint.length()*2];
		
		for(int i=0, j=0; i<hint.length()*2 && j<hint.length(); i+=2 , j++) {
			result[i] = hint.charAt(j);
			result[i+1] = ' ';
		}
		
		view.setWordWithHint(String.valueOf(result));	
	}

}
