package client;

import javafx.application.Application;
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
		Scene scene = new Scene(root, 250, 250);
		stage.setTitle("Mon chat");
		stage.setScene(scene);
		stage.show();

	}

	public static void main(String[] args) {
		client = new Client(args[0], new Integer(args[1]));
		Application.launch(Interface.class, args);
	}
}
