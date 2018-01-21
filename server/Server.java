package server;

import java.io.IOException;
import java.util.*;

import auth.Session;
import bot.QuestionCapitaleBot;

public class Server {
	private int port;
	private List<ConnectedClient> clients = new ArrayList<>();
	private List<ConnectedClient> authClients = new ArrayList<>();
	private Session session, botSession;
	private ConnectedClient send = null, rec = null;
	private QuestionCapitaleBot bot;
	private boolean responseFound = false;

	public Server(int port) throws IOException {
		this.port = port;
		this.session = new Session("Server", 1);
		Connection con = new Connection(this);
		Thread t = new Thread(con);
		t.start();
	}

	public void addClient(ConnectedClient client) {
		this.clients.add(client);
	}

	public void notifyNewAuth(ConnectedClient client) {
		String msg = "[a]" + client.getSession().getLogin() + "[/a] à rejoint le salon !";
		this.session.setLogin("Serveur");
		this.broadcastMessage(msg, this.session);
	}

	public void broadcastMessage(String message, Session session) {
		this.session.setLogin(session.getLogin());
		this.session.setPrivateId(-1);

		this.session.getListeClients().clear();
		authClients
				.forEach(client -> this.session.getListeClients().put(client.getId(), client.getSession().getLogin()));

		for (ConnectedClient client : this.authClients) {
			if (session != client.getSession()) {
				this.session.setResponseMsg(message);
				client.sendMessage(this.session);
			}
		}
		
		if (bot != null) {
			if (verifyResponse(message) && !responseFound) {
				responseFound = true;
				broadcastMessage(session.getLogin()+" a gagné en "+bot.getTimeRemaining()+" s", botSession);
			}
			else if(!bot.isTimeRemaining() && !responseFound) {
				broadcastMessage("Fin du temps ! La réponse était : "+bot.getResponse(), botSession);
			}
		}
	}

	public void privateMessage(String msg, int idSend, int idRec) {
		send = null;
		rec = null;

		for (ConnectedClient client : this.authClients) {
			if (client.getId() == idSend) {
				send = client;
			} else if (client.getId() == idRec) {
				rec = client;
			}
		}

		if (rec != null) {
			rec.getSession().getListeClients().clear();
			authClients.forEach(
					client -> rec.getSession().getListeClients().put(client.getId(), client.getSession().getLogin()));
		}
		if (rec != null && send != null) {
			msg = send.getSession().getLogin() + " vous à chuchoté : " + msg;
			rec.getSession().setResponseMsg(msg);
			rec.getSession().setPrivateId(idSend);
			rec.sendMessage(rec.getSession());
		}
	}

	public void disconnectedClient(ConnectedClient client) {
		this.authClients.remove(client);
		String msg = client.getSession().getLogin() + " à quitté le salon !";
		this.session.setLogin("Serveur");
		this.broadcastMessage(msg, this.session);
	}

	public int getPort() {
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

	public boolean activateBot() {

		if (bot == null) {
			bot = new QuestionCapitaleBot();
			botSession = new Session("Bot", 1);
			botSession.setPrivateId(-1);
			bot.getRandomPays();
			return true;
		}
		return false;
	}

	public String newBotQuestion() {
		responseFound = false;
		return bot.getRandomPays();
	}
	
	public void disableBot() {
		this.bot = null;
		this.botSession = null;
	}
	public Session getBotSession() {
		return this.botSession;
	}
	
	private boolean verifyResponse(String msg) {
		if (msg.contains(bot.getResponse())) {
			return true;
		}
		return false;
	}
}
