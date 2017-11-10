package server;

import java.io.*;
import java.net.Socket;

public class ConnectedClient implements Runnable {

	private Server server;
	private Socket sock;
	private int id;
	public static int counterId = 0;
	private BufferedReader in;
	private PrintWriter out;
	private String login; 
	
	public ConnectedClient (Server server, Socket sock) throws IOException {
		this.id = ConnectedClient.counterId++;
		this.server = server;
		this.sock = sock;
		this.in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
        this.out = new PrintWriter (this.sock.getOutputStream());
        
        System.out.println("Nouvelle connexion, id = "+this.id);
	}

	@Override
	public void run(){
		
		while(this.sock.isConnected()) {
			
			String message = null;
			try {
				message= in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(message != null) { // P%id% || B%
				String[] parts = message.split("%");
				
				switch (parts[0].charAt(0)) {
					case 'P':
						this.server.privateMessage(message, this.id, Integer.parseInt(parts[1]));
						break;
					case 'B':
						this.server.broadcastMessage(message, this.id);
						break;
				}
				
			} else {
				this.server.disconnectedClient(this);
			}
			
			
		}
	}
	
	public void sendMessage (String msg) {
		this.out.println(msg);
		this.out.flush();
	}
	
	public void closeClient () throws IOException {
		this.in.close();
		this.out.close();
		this.sock.close();
	}
	
	public int getId() {
		return this.id;
	}
}
