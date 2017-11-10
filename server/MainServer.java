package server;

import java.io.IOException;

public class MainServer {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			printUsage();
		} else {
			Integer port = new Integer(args[0]);
			Server server = new Server(port);
		}
	}

	private static void printUsage() {
		System.out.println("java server.MainServer <port>");
		System.out.println("\t<port>: server's port");
	}
}