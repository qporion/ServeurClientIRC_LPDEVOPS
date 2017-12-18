package server;

import java.io.IOException;
import java.util.*;

import auth.Session;

public class Server {
	private int port;
	private List<ConnectedClient> clients = new ArrayList<>();
	private List<ConnectedClient> authClients = new ArrayList<>();
	private Session session;
	
	public Server (int port) throws IOException {
		this.port = port;
		this.session = new Session("Server", 1);
		Connection con = new Connection(this);
		Thread t = new Thread(con);
		t.start();
	}
	
	public void addClient (ConnectedClient client) {
		/*String msg = "Le client "+client.getSession().getLogin()+" vient de se connecter !";
		this.broadcastMessage(msg, -1);*/
		this.clients.add(client);
	}
	
	public void broadcastMessage (String message, Session session) {
		this.session.setLogin(session.getLogin());
		for(ConnectedClient client : this.authClients)  {
			if (session != client.getSession()) {
				this.session.setResponseMsg(message);
				client.sendMessage(this.session);	
			}
		}
	}
	
	public void privateMessage (String msg, int idSend, int idRec) {
		ConnectedClient send = null, rec = null;
		
		for(ConnectedClient client : this.authClients)  {
			if(client.getId() == idSend) {
				send = client;				
			}
			else if (client.getId() == idRec) {
				rec = client;
			}
		}
		
		if (rec == null && send != null) {
			//send.sendMessage("Impossible de trouver le destinataire");
		}
		else if (rec != null && send != null) {
			msg = "Message privé de "+send.getId()+" : "+msg;
			rec.getSession().setResponseMsg(msg);
			rec.sendMessage(rec.getSession());
		}
	}
	
	public void disconnectedClient (ConnectedClient client) {
		this.authClients.remove(client);
		/*String msg = "Le client "+client.getId()+" vient de se déconnecter !";
		this.broadcastMessage(msg, this.session);*/
	}
	
	public int getPort () {
		return this.port;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public List<ConnectedClient> getClients() {
		return clients;
	}

	public void setClients(List<ConnectedClient> clients) {
		this.clients = clients;
	}

	public List<ConnectedClient> getAuthClients() {
		return authClients;
	}

	public void setAuthClients(List<ConnectedClient> authClients) {
		this.authClients = authClients;
	}

}
