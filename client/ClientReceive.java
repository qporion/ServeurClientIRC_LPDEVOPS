package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

import auth.Session;
import client.views.ClientPanel;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import server.MainServer;

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
			Thread.currentThread().interrupt();
			return;
		}
		boolean connected = false;

		while (isActive) {
			Thread.yield();
			String message = null;
			isActive = !this.sock.isClosed();

			try {
				Session session = (Session) in.readObject();

				this.client.getSession().setPrivateId(-1);
				this.client.getSession().setAddUser(false);

				if (session.isConnected() == 1 && session.getMessage() != null) {
				if (session.getMessage().startsWith("%!file:")) {
					String fileName = session.getMessage().substring(
							session.getMessage().indexOf(":")+1
							);
					File file = new File(fileName);
					
					if (!file.exists()) {
						Files.write(file.toPath(), session.getFileReceived());
					} else {
						int cpt = 0;
						while ((file = new File(MainServer.UPLOAD_DIR + "\\" + (cpt++) + fileName)).exists())
							;

						Files.write(file.toPath(), session.getFileReceived());
					}
					
					session.setResponseMsg("Fichier "+file.getName()+" reçu ! -> "+file.getAbsolutePath());
				}
				}
				
				if (session.isConnected() == 1 && !connected) {
					ClientPanel newPanel = new ClientPanel();
					newPanel.client = this.client;
					this.client.getClientPanel().changeScene(newPanel, ClientPanel.X, ClientPanel.Y);
					this.client.setClientPanel(newPanel);
					connected = true;
				}

				message = "";
				this.client.getSession().getListeClients().clear();
				this.client.getSession().setListeClients(session.getListeClients());

				if (this.client.getSession().isConnected() != 1) {
					this.client.setSession(session);
					this.client.getSession().setSendMessage(false);
					message = this.client.getSession().getResponseMsg();
				} else {
					message = session.getLogin() + " >> " + session.getResponseMsg();
					this.client.getSession().setResponseMsg(session.getResponseMsg());
				}

				 

				if (this.client.getSession() != null) {
					if (session.getPrivateId() == -1) {
						this.client.getClientPanel().updateTextArea(message);
					} else {

						if (this.client.getClientPanel() instanceof ClientPanel) {
							ClientPanel client = (ClientPanel) this.client.getClientPanel();
							Tab tab = client.getTab(session.getPrivateId(), true);
							client.updateTextArea(message, session.getPrivateId());
						}
					}
				} else {
					isActive = false;
				}
			} catch (IOException | ClassNotFoundException e) {
				Thread.currentThread().interrupt();
				return;

			}
		}

		client.disconnectedServer();
	}

}
