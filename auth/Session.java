package auth;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Session implements Serializable{

	private static final long serialVersionUID = -6425471594092345976L;

	private String login, message;
	private int isConnected = 0;
	private int privateId = -1; 
	

	public Session (String login, int isConnected) {
		this.login = login;
		this.isConnected = isConnected;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int isConnected() {
		return isConnected;
	}

	public void setConnected(int isConnected) {
		this.isConnected = isConnected;
	}
	
	public int getPrivateId() {
		return privateId;
	}

	public void setPrivateId(int privateId) {
		this.privateId = privateId;
	}
	
	public String encryptedMessage() {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		byte[] encodedhash = digest.digest(message.getBytes(StandardCharsets.UTF_8));

		return bytesToHex(encodedhash);
	}
	
	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}
}
