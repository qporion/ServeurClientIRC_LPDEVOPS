package client.views;

import java.io.IOException;

import client.Client;
import client.views.components.ButtonPerso;
import client.views.components.ErrorLabel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InscriptionPanel extends Parent implements ChangeablePanel {
	public final static int X = 250;
	public final static int Y = 310;
	
	TextField userTextField;
	PasswordField pwBox, pwBoxConfirm;
	Label userName;
	Label password;
	Label errorLabel;
	Button sendBtn;
	Client client;

	public InscriptionPanel() {

		this.errorLabel = new ErrorLabel();
		errorLabel.setLayoutX(70);
		errorLabel.setLayoutY(0);
		errorLabel.setPrefHeight(10);
		errorLabel.setPrefWidth(100);
		this.getChildren().add(errorLabel);

		// ////////////////user:
		this.userName = new Label("Pseudo :");
		userName.setLayoutX(70);
		userName.setLayoutY(10);
		userName.setPrefHeight(10);
		userName.setPrefWidth(50);
		this.getChildren().add(userName);

		this.userTextField = new TextField();
		userTextField.setLayoutX(70);
		userTextField.setLayoutY(30);
		userTextField.setPrefHeight(10);
		userTextField.setPrefWidth(100);
		this.getChildren().add(userTextField);

		// ///////password:
		this.password = new Label("mdp :");
		password.setLayoutX(70);
		password.setLayoutY(60);
		password.setPrefHeight(10);
		password.setPrefWidth(50);
		this.getChildren().add(password);

		this.pwBox = new PasswordField();

		pwBox.setLayoutX(70);
		pwBox.setLayoutY(80);
		pwBox.setPrefHeight(10);
		pwBox.setPrefWidth(100);
		this.getChildren().add(pwBox);

		password = new Label("Confirmation du mdp :");
		password.setLayoutX(70);
		password.setLayoutY(110);
		password.setPrefHeight(10);
		password.setPrefWidth(150);
		this.getChildren().add(password);

		this.pwBoxConfirm = new PasswordField();

		pwBoxConfirm.setLayoutX(70);
		pwBoxConfirm.setLayoutY(130);
		pwBoxConfirm.setPrefHeight(10);
		pwBoxConfirm.setPrefWidth(100);
		this.getChildren().add(pwBoxConfirm);

		// ////valider:

		this.sendBtn = new ButtonPerso();
		sendBtn.setLayoutX(70);
		sendBtn.setLayoutY(160);
		sendBtn.setPrefHeight(10);
		sendBtn.setPrefWidth(100);
		sendBtn.setText("Valider");
		sendBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (pwBox.getText().equals(pwBoxConfirm.getText())) {
					client.getSession().setLogin(userTextField.getText());
					client.getSession().setMessage(pwBox.getText());
					pwBox.setText("");
					pwBoxConfirm.setText("");
					client.getSession().setAddUser(true);

					if (client.getSession().isConnected() == 0) {
						client.getSession().setMessage(client.getSession().encryptedMessage());

						try {
							client.out.writeObject(client.getSession());
							client.out.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}

						client.getSession().setConnected(-2);
					} else if (client.getSession().isConnected() == -1) {
						client.getSession().setConnected(0);
					}
				} else {
					errorLabel.setText("La confirmation du mdp est différente du mdp");
				}
			}
		});
		this.getChildren().add(sendBtn);

		ButtonPerso btn = new ButtonPerso("Connection");
		btn.setLayoutX(70);
		btn.setLayoutY(220);
		btn.setPrefHeight(10);
		btn.setPrefWidth(100);
		btn.setOnAction((ActionEvent e) -> {
			AuthPanel auth = new AuthPanel();
			auth.client = client;
			changeScene(auth, AuthPanel.X, AuthPanel.Y);
		});
		this.getChildren().add(btn);
		
	}

	public void changeScene(final ChangeablePanel panel, int x, int y) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				client.setClientPanel(panel);
				
				Parent parent = (Parent) panel;
				Scene scene = new Scene(parent, x, y); // 250, 250
				Stage appStage = (Stage) getScene().getWindow();
				appStage.setScene(scene);
				appStage.show();
			}
		});
	}

	@Override
	public void updateTextArea(final String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				InscriptionPanel clientPanel = (InscriptionPanel) client.getClientPanel();
				clientPanel.errorLabel.setText(msg);
				;
			}
		});
	}
}
