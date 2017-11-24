package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
			Scanner sc = new Scanner(System.in);

			this.out = new ObjectOutputStream(this.sock.getOutputStream());

			while (this.client.getSession().isConnected() != 1) {
				
				if (this.client.getSession().isConnected() == 0) {
					
					synchronized (this.client) {
						System.out.print("Votre login >> ");
						this.client.getSession().setLogin(sc.nextLine());
		
						System.out.print("Votre password >> ");
						String msg = sc.nextLine();
		
						this.client.getSession().setMessage(msg);
						this.client.getSession().setMessage(this.client.getSession().encryptedMessage());
						out.writeObject(this.client.getSession());
						out.flush();
						
						this.client.getSession().setConnected(-2);
					}
				}
				else if (this.client.getSession().isConnected() == -1) {
					System.out.println("Mauvais login / mot de passe");
					this.client.getSession().setConnected(0);
				}
			}

			while (true) {
				System.out.print("Votre message >> ");
				String msg = sc.nextLine();
				this.client.getSession().setMessage(msg);
				out.writeObject(this.client.getSession());
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
