package client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.*;
import javafx.stage.Stage;

class ClientPanel extends Parent implements ChangeablePanel {
	// zone de texte
	TextArea textToSend;
	// messages recu
	ScrollPane scrollReceivedText;
	TextFlow receivedText;
	// bouton permettant d'envoyer du texte
	Button sendBtn;
	// bouton permettant d'effacer la zone de saisie
	Button clearBtn;
	// zone de texte recevant les noms des utilisateurs connectés
	TextArea connected;
	// Label connectés:
	Text textMembers;

	Client client;

	public ClientPanel() {

		// zone de texte
		this.textToSend = new TextArea();
		textToSend.setLayoutX(50);
		textToSend.setLayoutY(350);
		textToSend.setPrefHeight(50);
		textToSend.setPrefWidth(400);
		this.getChildren().add(textToSend);

		// messages recu
		this.receivedText = new TextFlow();
		receivedText.setLayoutX(50);
		receivedText.setLayoutY(50);
		receivedText.setPrefHeight(280);
		receivedText.setPrefWidth(400);

		// avec le scroll
		this.scrollReceivedText = new ScrollPane();
		scrollReceivedText.setLayoutX(50);
		scrollReceivedText.setLayoutY(50);
		scrollReceivedText.setPrefHeight(280);
		scrollReceivedText.setPrefWidth(400);

		scrollReceivedText.setContent(receivedText);
		scrollReceivedText.vvalueProperty().bind(receivedText.heightProperty());

		this.getChildren().add(scrollReceivedText);

		// bouton permettant d'envoyer du texte
		this.sendBtn = new Button();
		sendBtn.setLayoutX(470);
		sendBtn.setLayoutY(350);
		sendBtn.setPrefHeight(20);
		sendBtn.setPrefWidth(100);
		sendBtn.setText("Envoyer");
		sendBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (client.getSession().getLogin().equals("")) {
					client.getSession().setLogin(textToSend.getText());
					textToSend.setText("");
				} else {
					Label newLabel = new Label();
					newLabel.setText(textToSend.getText());
					newLabel.setPrefWidth(400);
					receivedText.getChildren().add(newLabel);
					client.getSession().setMessage(textToSend.getText());
					client.getSession().setSendMessage(true);
					textToSend.setText("");

				}
			}
		});

		this.getChildren().add(sendBtn);

		// bouton permettant d'effacer du texte
		this.clearBtn = new Button();
		clearBtn.setLayoutX(470);
		clearBtn.setLayoutY(380);
		clearBtn.setPrefHeight(20);
		clearBtn.setPrefWidth(100);
		clearBtn.setText("Effacer");
		clearBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				textToSend.clear();
			}
		});

		this.getChildren().add(clearBtn);

		// zone de texte recevant les noms des utilisateurs connectés
		this.connected = new TextArea();
		connected.setLayoutX(470);
		connected.setLayoutY(50);
		connected.setPrefHeight(280);
		connected.setPrefWidth(100);
		connected.setEditable(false);
		this.getChildren().add(connected);

		// Label connectés:
		this.textMembers = new Text();
		textMembers.setLayoutX(470);
		textMembers.setLayoutY(50);

		this.getChildren().add(textMembers);
	}

	public void updateTextArea(final String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Label newLabel = new Label();
				newLabel.setText(msg);
				newLabel.setPrefWidth(400);
				ClientPanel clientPanel = (ClientPanel) client.getClientPanel();
				clientPanel.receivedText.getChildren().add(newLabel);
				
				clientPanel.connected.setText("");
				client.getSession().getListeClients().forEach((id, login) -> {
					System.out.println("Login : "+login);
					clientPanel.connected.setText(clientPanel.connected.getText()+login+"\n");
				});
				
				
			}
		});

	}

	@Override
	public void changeScene(final Parent panel) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Scene scene = new Scene(panel);
				Stage appStage = (Stage) getScene().getWindow();
				appStage.setScene(scene);
				appStage.show();
			}
		});
	}
}
