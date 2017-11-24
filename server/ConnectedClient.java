package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import auth.Session;

public class ConnectedClient implements Runnable {

	private Server server;
	private Socket sock;
	private int id;
	public static int counterId = 0;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Session session; 
	
	public ConnectedClient (Server server, Socket sock) {
		this.id = ConnectedClient.counterId++;
		this.server = server;
		this.sock = sock;
        
        System.out.println("Nouvelle connexion, id = "+this.id);
	}

	@Override
	public void run(){
		
		while(this.sock.isConnected()) {
			
			try {
				this.in = new ObjectInputStream(this.sock.getInputStream()); 
				this.out = new ObjectOutputStream(this.sock.getOutputStream());
				
				this.session = (Session) in.readObject();
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
				
			if(session != null) {
				String message = this.session.getMessage();
				System.out.println("Message de recu de "+this.session.getLogin()+" : "+message);
				
				
				if (this.session.isConnected()) {
					if (this.session.getPrivateId() != -1)
						this.server.privateMessage(message, this.id, this.session.getPrivateId());
					else
						this.server.broadcastMessage(message, this.session);
				} else {
					UsersRepository db = UsersRepository.getInstance();
					
					session.setConnected(db.login(this.session.getLogin(), this.session.getMessage()));
					
					System.out.println(session.isConnected());
					session.setMessage("Vous etes auth");
					this.sendMessage(session);
					
					this.server.getClients().remove(this);
					this.server.getAuthClients().add(this);
				}
			} else {
				this.server.disconnectedClient(this);
				
			}
			
			
		}
	}
	

	public void sendMessage (Session session) {
		try {
			this.out.writeObject(session);
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeClient () throws IOException {
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
