package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import auth.Session;
import bot.QuestionCapitaleBot;
import server.db.UsersRepository;

public class ConnectedClient implements Runnable {

	private Server server;
	private Socket sock;
	private int id;
	public static int counterId = 0;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Session session;

	public ConnectedClient(Server server, Socket sock) {
		this.id = ConnectedClient.counterId++;
		this.server = server;
		this.sock = sock;

		System.out.println("Nouvelle connexion, id = " + this.id + ", ip = " + this.sock.getInetAddress().toString());
	}

	@Override
	public void run() {
		UsersRepository db = UsersRepository.getInstance();
		try {
			this.in = new ObjectInputStream(this.sock.getInputStream());
			this.out = new ObjectOutputStream(this.sock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			server.getClients().remove(this);
			Thread.currentThread().interrupt();
			return;
		}

		while (this.sock.isConnected()) {
			Thread.yield();
			try {

				Session ses = (Session) in.readObject();
				this.session = ses;

			} catch (ClassNotFoundException | IOException e) {
				this.server.disconnectedClient(this);

				if (session.isConnected() == 1) {
					server.getAuthClients().remove(this);
				} else {
					server.getClients().remove(this);
				}

				try {
					this.closeClient();
				} catch (IOException e1) {
				} finally {
					Thread.currentThread().interrupt();
				}
				return;
			}

			System.out.println("Session de " + this.session.getLogin());
			if (this.session.getMessage() != null) {
				String message = this.session.getMessage();

				System.out.println("Message recu de " + this.session.getLogin() + " : " + message);

				session.getListeClients().clear();
				this.server.getAuthClients().forEach(
						client -> session.getListeClients().put(client.getId(), client.getSession().getLogin()));

				if (this.session.isAddUser()) {
					if (db.newLogin(this.session.getLogin(), this.session.getMessage())) {
						session.setConnected(1);
						session.setResponseMsg("Bienvenue");
						session.getListeClients().put(this.getId(), this.session.getLogin());

						this.sendMessage(session);

						this.server.getClients().remove(this);
						this.server.getAuthClients().add(this);
						this.server.notifyNewAuth(this);
					} else {
						session.setConnected(-1);
						session.setResponseMsg("Login d�j� utilis�");
						this.sendMessage(session);
					}
				} else {
					if (this.session.isConnected() == 1) {
						if (this.session.getMessage().startsWith("!enableBot")) {
							if (this.server.activateBot()) {
								this.server.broadcastMessage("Bot activ�", this.server.getBotSession());
							} else {
								this.server.broadcastMessage("Impossible d'activ� le bot",
										this.server.getBotSession());
							}
						}

						if (this.session.getMessage().startsWith("!question")) {
							String pays = this.server.newBotQuestion();
							this.server.broadcastMessage("Quelle est la capitale de " + pays + " ?",
									this.server.getBotSession());
						}

						if (this.session.getMessage().startsWith("!disableBot")) {
							this.server.broadcastMessage("Bot d�sactiv�", this.server.getBotSession());
							this.server.disableBot();
						}
						
						if (this.session.getPrivateId() != -1)
							this.server.privateMessage(message, this.id, this.session.getPrivateId());
						else
							this.server.broadcastMessage(message, this.session);
					} else {
						boolean alreadyAuth = false;

						for (ConnectedClient client : server.getAuthClients()) {
							if (client.getSession().getLogin().equals(session.getLogin()))
								alreadyAuth = true;
						}

						if (alreadyAuth) {
							session.setConnected(-1);
							session.setResponseMsg("Vous d�j� authentifi�");
							this.sendMessage(session);
						} else {
							if (db.login(this.session.getLogin(), this.session.getMessage())) {
								session.setConnected(1);
								session.setResponseMsg("Vous etes auth");
								session.getListeClients().put(this.getId(), this.session.getLogin());

								this.sendMessage(session);

								this.server.getClients().remove(this);
								this.server.getAuthClients().add(this);
								this.server.notifyNewAuth(this);

							} else {
								session.setConnected(-1);
								session.setResponseMsg("Erreur de login/mdp");
								this.sendMessage(session);
							}
						}
					}
				}
			} else {
				if (session.isConnected() == 1) {
					this.server.disconnectedClient(this);
				} else {
					this.server.getClients().remove(this);
				}
			}
		}
	}

	public void sendMessage(Session session) {
		try {
			this.out.writeObject(session);
			this.out.flush();
			this.out.reset();
		} catch (IOException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			return;
		}
	}

	public void closeClient() throws IOException {
		this.in.close();
		this.out.close();
		this.sock.close();
	}

	public int getId() {
		return this.id;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
