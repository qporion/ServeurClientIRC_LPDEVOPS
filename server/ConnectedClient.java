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
        
        System.out.println("Nouvelle connexion, id = "+this.id+", ip = "+this.sock.getInetAddress().toString());
	}

	@Override
	public void run(){
		UsersRepository db = UsersRepository.getInstance();
		try {
			this.in = new ObjectInputStream(this.sock.getInputStream()); 
			this.out = new ObjectOutputStream(this.sock.getOutputStream());	
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		
		while(this.sock.isConnected()) {
			
			try {
				
				Session ses = (Session) in.readObject();
				this.session = ses;
				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Session de "+this.session.getLogin());
			if(this.session.getMessage() != null) {
				String message = this.session.getMessage();
				System.out.println("Message recu de "+this.session.getLogin()+" : "+message);				
				if (this.session.isAddUser()) {
					 if(db.newLogin(this.session.getLogin(), this.session.getMessage())) {
					session.setConnected(1);
					session.setResponseMsg("Bienvenue");
					this.sendMessage(session);
					
					this.server.getClients().remove(this);
					this.server.getAuthClients().add(this);
					 }
					 else {
						 session.setConnected(-1);
						 session.setResponseMsg("Login déjà utilisé");
						this.sendMessage(session);
					 }
				}
				else {
					if (this.session.isConnected() == 1) {
						if (this.session.getPrivateId() != -1)
							this.server.privateMessage(message, this.id, this.session.getPrivateId());
						else
							this.server.broadcastMessage(message, this.session);
					} else {					
						if(db.login(this.session.getLogin(), this.session.getMessage())) {
							session.setConnected(1);
							session.setResponseMsg("Vous etes auth");
							this.sendMessage(session);
							
							this.server.getClients().remove(this);
							this.server.getAuthClients().add(this);
						}
						else {
							session.setConnected(-1);
						}
					}
				}				
			} else {
				this.server.disconnectedClient(this);
			}
		}
	}
	

	public void sendMessage (Session session) {
		try {
			System.out.println(session.getResponseMsg());
			this.out.writeObject(session);
			this.out.flush();
			this.out.reset();
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
