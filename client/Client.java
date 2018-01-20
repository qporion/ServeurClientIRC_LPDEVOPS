package client;

import java.net.*;
import java.io.*;

import auth.Session;
import client.views.ChangeablePanel;

public class Client {
	public String address;
	public int port;
	public Socket socket;
	public Session session;
	public ObjectOutputStream out;
	
	private ChangeablePanel clientPanel;
	private Thread rec;

	public Client(String address, int port) {
		this.address = address;
		this.port = port;
		this.session = new Session("", 0);

		try {
			this.socket = new Socket(address, port);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());

			rec = new Thread(new ClientReceive(this, this.socket));

			rec.start();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

	public void disconnectedServer() {
		this.rec.interrupt();
		try {
			this.out.close();
			this.socket.close();
		} catch (IOException e) {
		}
		finally {
			this.clientPanel.updateTextArea("Impossible de joindre le serveur, veuillez redémarrer votre client");
		}
	}

	public Session getSession() {
		return this.session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public ChangeablePanel getClientPanel() {
		return clientPanel;
	}

	public void setClientPanel(ChangeablePanel clientPanel) {
		this.clientPanel = clientPanel;
	}

}
