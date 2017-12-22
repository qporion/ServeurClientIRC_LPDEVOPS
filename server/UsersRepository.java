package server;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsersRepository {
	
	private Connection con = null;
	private Statement stmt = null;
	
	private UsersRepository() {}

	private static UsersRepository INSTANCE = null;

	public static UsersRepository getInstance() {
		if (INSTANCE == null) {
			synchronized (UsersRepository.class) {
				if (INSTANCE == null) {
					INSTANCE = new UsersRepository();
				}
			}
		}
		return INSTANCE;
	}
	
	public void init() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			
			con = DriverManager.getConnection("jdbc:oracle:thin:@iutdoua-oracle.univ-lyon1.fr:1521:orcl","p1712620", "303176");
			stmt = con.createStatement();
	
			DatabaseMetaData dbm = con.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "IRC_USERS", null);
			
			if (!tables.next()) {
				stmt.executeQuery("CREATE TABLE IRC_USERS ( id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY, login VARCHAR2(50) NOT NULL, password VARCHAR2(255), CONSTRAINT pk_irc_users PRIMARY KEY (id));");
			}			
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean login (String login, String pwd) {
		ResultSet result;
		try {
			String query = "SELECT * FROM IRC_USERS where login = '"+login+"' and password = '"+pwd+"'";
			result = stmt.executeQuery(query);
			
			int cpt = 0;
			while (result.next())
				cpt++;
			
			return cpt == 1;
					
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean newLogin (String login, String pwd) {
		String query = "INSERT INTO IRC_USERS(login, password) VALUES ('"+login+"','"+pwd+"')";
		try {
			stmt.executeQuery(query);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public void close() {
		try {
			con.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
