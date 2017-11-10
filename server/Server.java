package server;

import java.util.*;

public class Server {
	int port;
	List<ConnectedClient> clients = new ArrayList<>();
	
	public Server (int port) {
		this.port = port;
	}
	
	public void addClient (ConnectedClient client) {
		String msg = "Le client "+client.getId()+" vient de se connecter !";
		this.broadcastMessage(msg, -1);
		this.clients.add(client);
	}
	
	public void broadcastMessage (String message, int id) {
		for(ConnectedClient client : this.clients)  {
			
			switch (id) {
				case -1:
					message = "Server : "+message;
					client.sendMessage(message);
					break;
				default:
					if (client.getId() != id)
						message = "Message de "+id+" : "+message;
			}
			
		}
	}
	
	public void privateMessage (String msg, int idSend, int idRec) {
		ConnectedClient send = null, rec = null;
		
		for(ConnectedClient client : this.clients)  {
			if(client.getId() == idSend) {
				send = client;				
			}
			else if (client.getId() == idRec) {
				rec = client;
			}
		}
		
		if (rec == null && send != null)
			send.sendMessage("Impossible de trouver le destinataire");
		else if (rec != null && send != null) {
			msg = "Message privé de "+send.getId()+" : "+msg;
			rec.sendMessage(msg);
		}
	}
	
	public void disconnectedClient (ConnectedClient client) {
		this.clients.remove(client);
		String msg = "Le client "+client.getId()+" vient de se déconnecter !";
		this.broadcastMessage(msg, -1);
	}
	
	public int getPort () {
		return this.port;
	}

}
