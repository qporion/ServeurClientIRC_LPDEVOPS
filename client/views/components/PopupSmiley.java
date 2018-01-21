package client.views.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Popup;

public class PopupSmiley extends Popup {

	public PopupSmiley() {
		super();
		setStylePopup(null);
	}

	public PopupSmiley(TextArea text) {
		super();
		setStylePopup(text);
	}

	private void setStylePopup(TextArea text) {
		setX(580);
		setY(295);
		setWidth(250);
		setHeight(500);
		setAutoHide(true);

		File rep = new File(Smiley.SMILEY_REP);
		File[] emojis = rep.listFiles();

		ScrollPane scroll = new ScrollPane();
		scroll.setMaxHeight(500);
		scroll.setMaxWidth(250);
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		
		GridPane gridPane = new GridPane();
		gridPane.setStyle("-fx-background-color:#FFF;" + "-fx-border-color: #4286f4;" + "-fx-border-radius: 2px;"
				+ "-fx-border-width: 1;" + "-fx-border-style: solid;");
		gridPane.setPadding(new Insets(5, 15, 5, 5));
		gridPane.setMaxWidth(250);

		int cpt = 0;

		try {
			for (File png : emojis) {
				ImageView iv = new ImageView();
				iv.setImage(new Image(new FileInputStream(png)));
				iv.setFitWidth(20);
				iv.setPreserveRatio(true);
				iv.setSmooth(true);

				if (text != null) {
					Button btn = new Button();
					btn.setGraphic(iv);
					btn.setOnAction((ActionEvent e) -> {
						text.setText(text.getText() + "[s]"
								+ png.getName().substring(0, png.getName().indexOf(".")) + "[/s]");
					});
					gridPane.add(btn, cpt % 6, cpt / 6);
				} else {
					gridPane.add(iv, cpt % 6, cpt / 6);
				}
				cpt++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		scroll.setContent(gridPane);
		getContent().add(scroll);
	}
}
