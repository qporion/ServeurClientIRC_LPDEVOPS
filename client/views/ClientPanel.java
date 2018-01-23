package client.views;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import client.Client;
import client.views.components.ButtonLogin;
import client.views.components.ButtonPerso;
import client.views.components.HelpButton;
import client.views.components.PopupHelp;
import client.views.components.PopupSmiley;
import client.views.components.Smiley;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class ClientPanel extends Parent implements ChangeablePanel {

	public final static int X = 600;
	public final static int Y = 500;

	private Smiley smiley = new Smiley();
	TabPane tabPanel;

	public Client client;

	public ClientPanel() {
		tabPanel = new TabPane();

		Tab mainTab = createTab("Chat général", -1);
		mainTab.setClosable(false);
		tabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		tabPanel.getTabs().add(mainTab);
		this.getChildren().add(tabPanel);

	}

	public void updateTextArea(final String msg) {
		this.updateTextArea(msg, -1);
	}

	public void updateTextArea(final String msg, int privateId) {
		ClientPanel thisPanel = this;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Label newLabel = new Label();
				Button btnLabel = null;

				VBox receivedText = (VBox) tabPanel.lookup("#receivedText_" + privateId);

				receivedText.getChildren().addAll(smiley.verifySmiley(msg, client));

				VBox connected = (VBox) tabPanel.lookup("#connected_" + privateId);
				connected.getChildren().clear();
				client.getSession().getListeClients().forEach((id, login) -> {
					ButtonPerso btn = new ButtonPerso(login);
					btn.setPrefHeight(20);
					btn.setPrefWidth(100);

					TextArea textToSend = (TextArea) tabPanel.lookup("#textToSend_" + privateId);
					btn.setOnMouseClicked((MouseEvent e) -> {
						textToSend.setText("/chuchoter � " + login + " ");
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
		if (this.client.getSession().getMessage().startsWith("/chuchoter à ")) {
			String msg = this.client.getSession().getMessage().substring("/chuchoter à ".length());

			if (this.client.getSession().getMessage().length() <= "/chuchoter à ".length() + 3) {
				this.client.getClientPanel().updateTextArea("Client >> Synthax : /chuchoter à [login] [message]");
			} else {
				int idxLogin = msg.indexOf(" ");
				String login = msg.substring(0, idxLogin);

				if (idxLogin + 1 >= msg.length()) {
					this.client.getClientPanel().updateTextArea("Client >> Synthax : /chuchoter à [login] [message]");
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

	public void sendFileRequest(String name) {
		this.client.getSession().setPrivateId(-1);
		this.client.getSession().setMessage("%!fileRequest:" + name);
		try {
			this.client.out.writeObject(this.client.getSession());
			this.client.out.flush();
			this.client.out.reset();
		} catch (IOException e) {
			e.printStackTrace();
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
				// Obligatoire pour attendre le runlater au dessus
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
		VBox receivedText = new VBox();
		// bouton permettant d'envoyer du texte
		Button sendBtn = new ButtonPerso();
		// bouton permettant d'effacer la zone de saisie
		Button clearBtn = new ButtonPerso();
		// zone de texte recevant les noms des utilisateurs connectés
		VBox connected = new VBox();
		// Label connectés:
		Text textMembers = new Text();

		ClientPanel thisPanel = this;
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
					String msg = textToSend.getText();
					Button btnLabel = null;
					Label newLabel = new Label();

					Smiley smiley = new Smiley();
					receivedText.getChildren().addAll(smiley.verifySmiley(msg, client));

					client.getSession().setMessage(msg);
					client.getSession().setPrivateId(id);
					sendMessage();

					textToSend.setText("");
					client.getSession().setFiles(new HashMap<String, byte[]>());

					if (client.getSession().getMessage().startsWith("/chuchoter à ")) {
						String loginPriv = client.getSession().getListeClients()
								.get(new Integer(client.getSession().getPrivateId()));
						Tab newTab = createTab(loginPriv, client.getSession().getPrivateId());
						Platform.runLater(() -> {
							tabPanel.getTabs().add(newTab);
							tabPanel.getSelectionModel().select(newTab);
						});

						TextFlow receivedText = (TextFlow) tabPanel
								.lookup("#receivedText_" + client.getSession().getPrivateId());
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
				client.getSession().setFiles(new HashMap<String, byte[]>());
			}
		});
		clearBtn.setId("clearBtn_" + id);
		box.getChildren().add(clearBtn);

		// zone de texte recevant les noms des utilisateurs connectés
		connected.setLayoutX(470);
		connected.setLayoutY(50);
		connected.setPrefHeight(280);
		connected.setPrefWidth(100);
		connected.setId("connected_" + id);
		box.getChildren().add(connected);

		// Label connectés:
		textMembers.setLayoutX(470);
		textMembers.setLayoutY(50);
		textMembers.setId("textMembers_" + id);
		box.getChildren().add(textMembers);

		Button helpBtn = new HelpButton("?");

		helpBtn.setLayoutX(50);
		helpBtn.setLayoutY(420);
		helpBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Popup popup = new PopupHelp(textToSend);
				popup.show(getScene().getWindow());
			}
		});
		helpBtn.setId("helpBtn_" + id);
		box.getChildren().add(helpBtn);

		Button smileyBtn = new HelpButton(":)");

		smileyBtn.setLayoutX(80);
		smileyBtn.setLayoutY(420);
		smileyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Popup popup = new PopupSmiley(textToSend);
				popup.show(getScene().getWindow());
			}
		});
		smileyBtn.setId("smileyBtn_" + id);
		box.getChildren().add(smileyBtn);

		Label lblUpload = new Label("Partagez vos fichiers !");
		lblUpload.setPadding(new Insets(5, 5, 5, 5));

		ImageView iv = new ImageView();
		try {
			iv.setImage(new Image(new FileInputStream(Smiley.SMILEY_REP + "\\1f4d2.png")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		iv.setFitWidth(20);
		iv.setPreserveRatio(true);
		iv.setSmooth(true);

		Button btnUpload = new ButtonPerso();
		btnUpload.setLayoutX(152);
		btnUpload.setLayoutY(0);
		btnUpload.setGraphic(iv);
		btnUpload.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				List<File> listFile = fileChooser.showOpenMultipleDialog(null);

				if (listFile != null) {
					lblUpload.setText("");
					for (File file : listFile) {
						try {
							client.getSession().addFile(file.getName(), Files.readAllBytes(file.toPath()));
							lblUpload.setText(lblUpload.getText() + ", " + file.getName());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		Pane pane = new Pane();
		pane.setLayoutX(110);
		pane.setLayoutY(420);
		pane.setMinWidth(180);
		pane.setMinHeight(28);
		pane.setStyle("-fx-background-color:#fff;" + "-fx-border-color: #4286f4;" + "-fx-border-radius: 2px;"
				+ "-fx-border-width: 1;" + "-fx-border-style: solid;");
		pane.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			}
		});

		pane.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasFiles()) {
					success = true;
					lblUpload.setText("");
					for (File file : db.getFiles()) {
						try {
							client.getSession().addFile(file.getName(), Files.readAllBytes(file.toPath()));
							lblUpload.setText(lblUpload.getText() + ", " + file.getName());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				event.setDropCompleted(success);
				event.consume();
			}
		});
		pane.getChildren().add(lblUpload);
		pane.getChildren().add(btnUpload);
		box.getChildren().add(pane);

		mainTab.setContent(box);
		return mainTab;
	}
}
