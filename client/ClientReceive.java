package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;

import auth.Session;

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
				this.client.setSession((Session) in.readObject());
				message = this.client.getSession().getMessage();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();

			}

			if (this.client.getSession() != null) {
				System.out.println("\nMessage recu de " + this.client.getSession().getLogin()
						+ " : " + message);
			} else {
				isActive = false;
			}
		}
		client.disconnectedServer();
	}

}
