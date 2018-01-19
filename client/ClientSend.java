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
						//this.client.getClientPanel().updateTextArea("Mauvais mot de passe / login");
						this.client.getSession().setConnected(0);
					}
					this.client.getSession().setSendMessage(false);
				}
			}
			
			while (true) {
				Thread.yield();
				if (this.client.getSession().isSendMessage()) {
						out.writeObject(this.client.getSession());
						out.flush();
						out.reset();
						this.client.getSession().setSendMessage(false);
			
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
