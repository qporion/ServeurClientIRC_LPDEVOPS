package server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import server.db.UsersRepository;

public class MainServer {

	public static void main(String[] args) throws IOException {
		UsersRepository db = UsersRepository.getInstance();
		db.init();
		
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