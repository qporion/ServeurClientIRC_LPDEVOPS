package client.views.components;

import java.util.Map;

import client.Client;
import client.views.ClientPanel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ButtonLogin extends Button{
	int id = 0;
	
	public ButtonLogin() {
		super();
		setStyleButton();
	}
	
	public ButtonLogin(String text) {
		super(text);
		setStyleButton();
	}
	
	public ButtonLogin(String login,Client client, ClientPanel panel) {
		super(login);
		setStyleButton();
		if (client.getSession().getListeClients().containsValue(login)) {
			id = 0;
			
			for (Map.Entry<Integer, String> data : client.getSession().getListeClients().entrySet()) {
				if (data.getValue().equals(login)) {
					id = data.getKey().intValue();
				}
			}
					
			setOnAction((ActionEvent e)-> {
				panel.getTab(id, true);
			});
		}
	}
	
	private void setStyleButton() {
		setStyle("-fx-background-color:#FFF;" + "-fx-border-color: #4286f4;" + "-fx-border-radius: 2px;"
				+ "-fx-border-width: 1;" + "-fx-border-style: solid;");
		
		setOnMouseEntered((MouseEvent e) -> {
			getStyleClass().removeAll();
			setStyle("-fx-background-color:#DDD;" + "-fx-border-color: #4286f4;"
					+ "-fx-border-radius: 2px;" + "-fx-border-width: 1;" + "-fx-border-style: solid;");
		});

		setOnMouseExited((MouseEvent e) -> {
			getStyleClass().removeAll();
			setStyle("-fx-background-color:#FFF;" + "-fx-border-color: #4286f4;"
					+ "-fx-border-radius: 2px;" + "-fx-border-width: 1;" + "-fx-border-style: solid;");
		});
	}
	
	
}
