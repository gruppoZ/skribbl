package it.unibs.pajc.client;

public class ProcessClientList implements ProcessMessageClient{
	@Override
	public void process(ClientView view, String msg) {
		view.resetClientList();
		String[] clientList = msg.split("/");
		
		for (String name : clientList) {
			view.updateClientList(name);
		}
	}
}
