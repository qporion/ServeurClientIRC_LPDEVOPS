package client.views;

import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import client.Client;
import client.views.components.ButtonPerso;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class ClientPanel extends Parent implements ChangeablePanel {

	public final static int X = 600;
	public final static int Y = 500;
	
	TabPane tabPanel;

	public Client client;

	public ClientPanel() {
		tabPanel = new TabPane();

		Tab mainTab = createTab("Chat gÈnÈral", -1);
		mainTab.setClosable(false);
		tabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		tabPanel.getTabs().add(mainTab);
		this.getChildren().add(tabPanel);

	}

	public void updateTextArea(final String msg) {
		this.updateTextArea(msg, -1);
	}

	public void updateTextArea(final String msg, int privateId) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Label newLabel = new Label();
				newLabel.setText(msg);
				newLabel.setPrefWidth(400);

				TextFlow receivedText = (TextFlow) tabPanel.lookup("#receivedText_" + privateId);
				receivedText.getChildren().add(newLabel);

				VBox connected = (VBox) tabPanel.lookup("#connected_" + privateId);
				connected.getChildren().clear();
				client.getSession().getListeClients().forEach((id, login) -> {
					ButtonPerso btn = new ButtonPerso(login);
					btn.setPrefHeight(20);
					btn.setPrefWidth(100);

					TextArea textToSend = (TextArea) tabPanel.lookup("#textToSend_" + privateId);
					btn.setOnMouseClicked((MouseEvent e) -> {
						textToSend.setText("/chuchoter ‡ " + login + " ");
					});

					connected.getChildren().add(btn);
					connected.setMargin(btn, new Insets(0, 0, 3, 0));
				});

			}
		});

	}

	@Override
	public void changeScene(final ChangeablePanel panel, int x, int y) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				client.setClientPanel(panel);
				
				Parent parent = (Parent) panel;
				Scene scene = new Scene(parent, x, y); // 600, 500
				Stage appStage = (Stage) getScene().getWindow();
				appStage.setScene(scene);
				appStage.show();
			}
		});
	}

	private void sendMessage() {
		if (this.client.getSession().getMessage().startsWith("/chuchoter ‡ ")) {
			String msg = this.client.getSession().getMessage().substring("/chuchoter ‡ ".length());

			if (this.client.getSession().getMessage().length() <= "/chuchoter ‡ ".length() + 3) {
				this.client.getClientPanel().updateTextArea("Client >> Synthax : /chuchoter ‡ [login] [message]");
			} else {
				int idxLogin = msg.indexOf(" ");
				String login = msg.substring(0, idxLogin);

				if (idxLogin + 1 >= msg.length()) {
					this.client.getClientPanel().updateTextArea("Client >> Synthax : /chuchoter ‡ [login] [message]");
				} else {
					msg = msg.substring(idxLogin + 1);
					this.client.getSession().setMessage(msg);

					if (this.client.getSession().getListeClients().containsValue(login)) {
						this.client.getSession().getListeClients().forEach((key, value) -> {
							if (value.equals(login)) {
								this.client.getSession().setPrivateId(((Integer) key).intValue());
							}
						});

						try {
							client.out.writeObject(this.client.getSession());
							client.out.flush();
							client.out.reset();
						} catch (IOException e) {
							client.disconnectedServer();
						}

						this.client.getSession().setPrivateId(-1);

					} else {
						this.client.getClientPanel()
								.updateTextArea("Client >> Impossible de trouver l'utilisateur \"" + login + "\"");
					}
				}
			}

		} else {
			try {
				client.out.writeObject(this.client.getSession());
				client.out.flush();
				client.out.reset();
			} catch (IOException e) {
				client.disconnectedServer();
			}
		}
	}

	public Tab getTab(int id, boolean create) {

		if (id == -1) {
			return tabPanel.getTabs().get(0);
		}
		String login = client.getSession().getListeClients().get(new Integer(id));
		Tab selectedTab = null;

		tabPanel.getTabs().forEach(tab -> {
			if (tab.getText().equals(login)) {
				tabPanel.getSelectionModel().select(tab);
			}
		});

		selectedTab = tabPanel.getSelectionModel().getSelectedItem();

		if (selectedTab != null & !tabPanel.getTabs().get(0).equals(selectedTab))
			return selectedTab;

		if (create) {
			Tab newTab = createTab(login, id);
			Platform.runLater(() -> {
				tabPanel.getTabs().add(newTab);
				tabPanel.getSelectionModel().select(newTab);
			});
			try {
				//Obligatoire pour attendre le runlater au dessus
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return newTab;
		}

		return null;
	}

	private Tab createTab(String login, int id) {
		// zone de texte
		TextArea textToSend = new TextArea();
		// messages recu
		ScrollPane scrollReceivedText = new ScrollPane();
		TextFlow receivedText = new TextFlow();
		// bouton permettant d'envoyer du texte
		Button sendBtn = new ButtonPerso();
		// bouton permettant d'effacer la zone de saisie
		Button clearBtn = new ButtonPerso();
		// zone de texte recevant les noms des utilisateurs connect√©s
		VBox connected = new VBox();
		// Label connect√©s:
		Text textMembers = new Text();

		Tab mainTab = new Tab();
		mainTab.setText(login);
		Pane box = new Pane();

		// zone de texte
		textToSend.setLayoutX(50);
		textToSend.setLayoutY(350);
		textToSend.setPrefHeight(50);
		textToSend.setPrefWidth(400);
		textToSend.setId("textToSend_" + id);
		box.getChildren().add(textToSend);

		// messages recu
		receivedText.setLayoutX(50);
		receivedText.setLayoutY(50);
		receivedText.setPrefHeight(280);
		receivedText.setPrefWidth(400);
		receivedText.setId("receivedText_" + id);

		// avec le scroll
		scrollReceivedText.setLayoutX(50);
		scrollReceivedText.setLayoutY(50);
		scrollReceivedText.setPrefHeight(280);
		scrollReceivedText.setPrefWidth(400);

		scrollReceivedText.setContent(receivedText);
		scrollReceivedText.vvalueProperty().bind(receivedText.heightProperty());
		scrollReceivedText.setId("scrollReceivedText_" + id);
		box.getChildren().add(scrollReceivedText);

		// bouton permettant d'envoyer du texte
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
					client.getSession().setPrivateId(id);
					sendMessage();
					textToSend.setText("");
					
					if (client.getSession().getMessage().startsWith("/chuchoter ‡ ")) {
						String loginPriv = client.getSession().getListeClients().get(new Integer(client.getSession().getPrivateId()));
						Tab newTab = createTab(loginPriv, client.getSession().getPrivateId());
						Platform.runLater(() -> {
							tabPanel.getTabs().add(newTab);
							tabPanel.getSelectionModel().select(newTab);
						});
						
						TextFlow receivedText = (TextFlow) tabPanel.lookup("#receivedText_"+client.getSession().getPrivateId());
						receivedText.getChildren().add(newLabel);
					}
				}
			}
		});
		sendBtn.setId("sendBtn_" + id);
		box.getChildren().add(sendBtn);

		// bouton permettant d'effacer du texte
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
		clearBtn.setId("clearBtn_" + id);
		box.getChildren().add(clearBtn);

		// zone de texte recevant les noms des utilisateurs connect√©s
		connected.setLayoutX(470);
		connected.setLayoutY(50);
		connected.setPrefHeight(280);
		connected.setPrefWidth(100);
		connected.setId("connected_" + id);
		box.getChildren().add(connected);

		// Label connect√©s:
		textMembers.setLayoutX(470);
		textMembers.setLayoutY(50);
		textMembers.setId("textMembers_" + id);
		box.getChildren().add(textMembers);

		mainTab.setContent(box);
		return mainTab;
	}
}
