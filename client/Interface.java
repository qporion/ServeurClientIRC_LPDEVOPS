package client;

import client.views.AuthPanel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Interface extends Application {
	static Client client;

	@Override
	public void start(Stage stage) throws Exception {
		AuthPanel authPanel = new AuthPanel();
		authPanel.client = this.client;
		client.setClientPanel(authPanel);
		Group root = new Group();
		root.getChildren().add(authPanel);
		Scene scene = new Scene(root, AuthPanel.X, AuthPanel.Y);
		stage.setTitle("Mon ch@t");
		stage.setScene(scene);
		stage.show();

	}

	@Override
	public void stop() throws Exception 
	{
		client.disconnectedServer();
	    Platform.exit();
	    System.exit(0);
	}
	
	public static void main(String[] args) {
		client = new Client(args[0], new Integer(args[1]));
		Application.launch(Interface.class, args);
	}
}
