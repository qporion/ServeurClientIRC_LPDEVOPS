package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;

import auth.Session;
import javafx.scene.control.Label;

public class ClientReceive implements Runnable {
	public Client client;
	private Socket sock;
	public ObjectInputStream in;

	public ClientReceive(Client client, Socket sock) {
		this.client = client;
		this.sock = sock;
	}

	@Override
	public void run() {
		boolean isActive = true;
		try {
			this.in = new ObjectInputStream(this.sock.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (isActive) {
			String message = null;
			
			try {
				Session session = (Session) in.readObject();
                                System.out.println("BONJOUR");
				session.getListeClients().forEach(client->System.out.println(client));
                                System.out.flush();
				if (this.client.getSession().isConnected() != 1) {
						this.client.setSession(session);
					    this.client.getSession().setSendMessage(false);
				}
				else {
					this.client.getSession().setResponseMsg(session.getResponseMsg());
				}
				message = session.getLogin()+" >> "+this.client.getSession().getResponseMsg();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();

			}

			if (this.client.getSession() != null) {
				this.client.getClientPanel().updateTextArea(message);
			} else {
				isActive = false;
			}
		}
                
		client.disconnectedServer();
	}

}
