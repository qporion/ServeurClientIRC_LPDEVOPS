package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection implements Runnable{

	private Server server;
	private ServerSocket sSocket;
	
	public Connection (Server server) throws IOException {
		this.server = server;
		this.sSocket = new ServerSocket(server.getPort());
	}
	
	@Override
	public void run() {
		for(;;) {
			Socket sClient = new Socket();
			try {
				sClient = this.sSocket.accept();
				ConnectedClient newClient = new ConnectedClient(server, sClient);
				this.server.addClient(newClient);				
				
				Thread threadNewClient = new Thread(newClient);
				threadNewClient.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}

}
