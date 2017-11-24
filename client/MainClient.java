package client;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import sun.security.krb5.internal.crypto.Aes256;

public class MainClient {

	public static void main(String[] args) {
		if (args.length != 2) {
			printUsage();
		} else {
			String address = args[0];
			Integer port = new Integer(args[1]);
			Client c = new Client(address, port);
		}

	}

	private static void printUsage() {
		System.out.println("java client.MainClient <address> <port>");
		System.out.println("\t<address>: server's ip address");
		System.out.println("\t<port>: server's port");
	}

}
