package client;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import sun.security.krb5.internal.crypto.Aes256;

public class MainClient {

	public static void main(String[] args) {
		String message = "PasswordUti";

		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] encodedhash = digest.digest(
				message.getBytes(StandardCharsets.UTF_8));

		System.out.println(bytesToHex(encodedhash)+"      "+Arrays.toString(encodedhash));
		
		Connection con = null;
	      ResultSet résultats = null;
	      String requete = "";

	      // chargement du pilote

	      try {
	         Class.forName("oracle.jdbc.OracleDriver");
	      } catch (ClassNotFoundException e) {
	         e.printStackTrace();
	      }

	      //connection a la base de données


	      System.out.println("connexion a la base de données");
	      try {

	         con = DriverManager.getConnection("jdbc:oracle:thin:@iutdoua-oracle.univ-lyon1.fr:1521:orcl", "p1712620", "303176");
	      } catch (SQLException e) {
	         e.printStackTrace();
	      }

	      //insertion d'un enregistrement dans la table client 

	      System.out.println("creation enregistrement");
	      
	      
		System.exit(0);
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

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
}
