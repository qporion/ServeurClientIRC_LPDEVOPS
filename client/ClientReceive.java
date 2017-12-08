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
				
				if (this.client.getSession().isConnected() != 1) {
				    this.client.setSession((Session) in.readObject());
				}
				else {
					this.client.getSession().setMessage(((Session) in.readObject()).getMessage());
				}
				message = this.client.getSession().getMessage();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();

			}

			if (this.client.getSession() != null) {
                            Label newLabel = new Label();
                            newLabel.setText(message);
                            newLabel.setPrefWidth(400);
                            this.client.getClientPanel().receivedText.getChildren().add( newLabel);
			} else {
				isActive = false;
			}
		}
		client.disconnectedServer();
	}

}
