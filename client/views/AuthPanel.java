/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.views;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client.Client;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthPanel extends Parent implements ChangeablePanel {

	public Client client;

	private TextField userTextField;
	private PasswordField pwBox;
	private Label userName;
	private Label password;
	private Button sendBtn;
	private Label errorLabel;

	public AuthPanel(Socket sock) {
		
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

		// ////valider:

		this.sendBtn = new Button();
		sendBtn.setLayoutX(70);
		sendBtn.setLayoutY(120);
		sendBtn.setPrefHeight(10);
		sendBtn.setPrefWidth(100);
		sendBtn.setText("Valider");
		sendBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				client.getSession().setLogin(userTextField.getText());
				client.getSession().setMessage(pwBox.getText());
				pwBox.setText("");

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
					updateTextArea("Mauvais mot de passe / login");
					client.getSession().setConnected(0);
				}
			}
		});
		this.getChildren().add(sendBtn);
		
		errorLabel = new Label();

	}

	public void changeScene(final Parent panel, int x, int y) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Scene scene = new Scene(panel, x, y); //260 160
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
				AuthPanel clientPanel = (AuthPanel) client.getClientPanel();
				clientPanel.errorLabel.setText(msg);
			}
		});
	}
}
