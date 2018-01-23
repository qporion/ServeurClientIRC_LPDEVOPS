package client.views.components;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import client.Client;
import client.views.ClientPanel;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class Smiley {

	public static final String SMILEY_REP = "assets\\emoji\\";
	private Client client;

	public Image getSmiley(String code) {
		InputStream fis = null;
		try {
			String current = new java.io.File(".").getCanonicalPath();
			fis = new FileInputStream(current + "\\" + SMILEY_REP + code + ".png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Image(fis);
	}

	public Node verifySmiley(String msg, Client client) {
		this.client = client;

		List<Node> l = parcoursMsg(msg);

		HBox box = new HBox();
		box.setMaxWidth(399);
		box.setMinWidth(399);

		for (Node n : l) {
			box.getChildren().add(n);
		}
		return box;
	}

	private ArrayList<Node> parcoursMsg(String msg) {
		ArrayList<Node> nodes = new ArrayList<>();

		Map<String, String> trigger_code = SmileyList.getMap();
		List<String> keys = new ArrayList<>();
		keys.addAll(trigger_code.keySet());

		for (int i = 0; i < keys.size(); i++) {
			if (msg.contains(keys.get(i))) {
				int idx = msg.indexOf(keys.get(i));

				ImageView iv = new ImageView();
				iv.setImage(getSmiley(trigger_code.get(keys.get(i))));
				iv.setFitWidth(20);
				iv.setPreserveRatio(true);
				iv.setSmooth(true);
				iv.setCache(true);

				nodes.addAll(parcoursMsg(msg.substring(0, idx)));
				nodes.add(iv);
				nodes.addAll(parcoursMsg(msg.substring(idx + keys.get(i).length())));
				break;
			}
		}

		if (nodes.size() == 0) {
			if (msg.contains("[f]") && msg.contains("[/f]")) {
				Button btnLabel = null;

				int idxStart = msg.indexOf("[f]");
				int idxEnd = msg.substring(idxStart).indexOf("[/f]");

				String url = msg.substring(idxStart + 3, idxStart + idxEnd);
				File file = new File(url);

				btnLabel = new ButtonLogin(url);
				btnLabel.setWrapText(true);
				btnLabel.setOnAction((ActionEvent e) -> {
					ClientPanel panel = (ClientPanel) this.client.getClientPanel();
					
					panel.sendFileRequest(url);
				});

				nodes.addAll(parcoursMsg(msg.substring(0, idxStart)));
				nodes.add(btnLabel);
				nodes.addAll(parcoursMsg(msg.substring(idxStart + idxEnd + 4)));
			} else if (msg.contains("[s]") && msg.contains("[/s]")) {
				int idxStart = msg.indexOf("[s]");
				int idxEnd = msg.substring(idxStart).indexOf("[/s]");

				ImageView iv = new ImageView();
				iv.setImage(getSmiley(msg.substring(idxStart + 3, idxStart + idxEnd)));
				iv.setFitWidth(20);
				iv.setPreserveRatio(true);
				iv.setSmooth(true);
				iv.setCache(true);

				nodes.addAll(parcoursMsg(msg.substring(0, idxStart)));
				nodes.add(iv);
				nodes.addAll(parcoursMsg(msg.substring(idxStart + idxEnd + 4)));
			} else if (msg.contains("<a>") && msg.contains("</a>")) {
				Button btnLabel = null;

				int idxStart = msg.indexOf("<a>");
				int idxEnd = msg.substring(idxStart).indexOf("</a>");

				String url = msg.substring(idxStart + 3, idxStart + idxEnd);

				btnLabel = new ButtonLogin(url);
				// btnLabel.setMaxHeight(25);
				btnLabel.setWrapText(true);
				btnLabel.setOnAction((ActionEvent e) -> {
					URI uri = URI.create(url);
					try {
						Desktop.getDesktop().browse(uri);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});

				nodes.addAll(parcoursMsg(msg.substring(0, idxStart)));
				nodes.add(btnLabel);
				nodes.addAll(parcoursMsg(msg.substring(idxStart + idxEnd + 4)));

			} else if (msg.contains("[a]") && msg.contains("[/a]")) {
				Button btnLabel = null;

				int idxStart = msg.indexOf("[a]");
				int idxEnd = msg.substring(idxStart).indexOf("[/a]");

				String login = msg.substring(idxStart + 3, idxStart + idxEnd);

				btnLabel = new ButtonLogin(login, client, (ClientPanel) client.getClientPanel());
				btnLabel.setWrapText(true);

				nodes.addAll(parcoursMsg(msg.substring(0, idxStart)));
				nodes.add(btnLabel);
				nodes.addAll(parcoursMsg(msg.substring(idxStart + idxEnd + 4)));

			} else {
				Label newLabel = new Label();
				newLabel.setText(msg);
				newLabel.setWrapText(true);

				nodes.add(newLabel);
			}
		}

		return nodes;
	}
}
