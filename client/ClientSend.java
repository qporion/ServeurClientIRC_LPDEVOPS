package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.scene.control.Label;

public class ClientSend implements Runnable {
	public ObjectOutputStream out;
	private Socket sock;
	private Client client;

	public ClientSend(Client client, Socket sock) {
		this.client = client;
		this.sock = sock;
	}

	@Override
	public void run() {
		try {

			this.out = new ObjectOutputStream(this.sock.getOutputStream());
			while (this.client.getSession().isConnected() != 1) {
				Thread.yield();
				if (this.client.getSession().isSendMessage()) {
					if (this.client.getSession().isConnected() == 0) {
						this.client.getSession().setMessage(
								this.client.getSession().encryptedMessage());

						out.writeObject(this.client.getSession());
						out.flush();
						this.client.getSession().setConnected(-2);
					} else if (this.client.getSession().isConnected() == -1) {
						this.client.getClientPanel().updateTextArea("Mauvais mot de passe / login");
						this.client.getSession().setConnected(0);
					}
					this.client.getSession().setSendMessage(false);
				}
			}

			while (true) {
				Thread.yield();
				if (this.client.getSession().isSendMessage()) {
					if (this.client.getSession().getMessage()
							.startsWith("/chuchoter à ")) {
						String msg = this.client.getSession().getMessage()
								.substring("/chuchoter à ".length());

						if (this.client.getSession().getMessage().length() <= "/chuchoter à ".length()+3) {
							this.client
									.getClientPanel()
									.updateTextArea(
											"Client >> Synthax : /chuchoter à [login] [message]");
						} else {
							int idxLogin = msg.indexOf(" ");
							String login = msg.substring(0, idxLogin);

							if (idxLogin + 1 >= msg.length()) {
								this.client
										.getClientPanel()
										.updateTextArea(
												"Client >> Synthax : /chuchoter à [login] [message]");
							} else {
								msg = msg.substring(idxLogin + 1);
								this.client.getSession().setMessage(msg);

								if (this.client.getSession().getListeClients()
										.containsValue(login)) {
									this.client
											.getSession()
											.getListeClients()
											.forEach(
													(key, value) -> {
														if (value.equals(login)) {
															this.client
																	.getSession()
																	.setPrivateId(
																			((Integer) key)
																					.intValue());
														}
													});

									out.writeObject(this.client.getSession());
									out.flush();
									out.reset();
									
									this.client.getSession().setPrivateId(-1);

								} else {
									this.client.getClientPanel()
											.updateTextArea(
													"Client >> Impossible de trouver l'utilisateur \""
															+ login + "\"");
								}
							}
						}

					} else {
						out.writeObject(this.client.getSession());
						out.flush();
						out.reset();
					}

					this.client.getSession().setSendMessage(false);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
