package client.views.components;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Popup;

public class PopupHelp extends Popup {

	public PopupHelp() {
		super();
		setStylePopup(null);
	}
	
	public PopupHelp(TextArea text) {
		super();
		setStylePopup(text);
	}

	private void setStylePopup(TextArea text) {
		setX(680);
		setY(295);
		setWidth(150);
		setHeight(500);
		setAutoHide(true);

		VBox box = new VBox();
		box.setMinWidth(150);
		box.setMinHeight(500);
		box.setStyle("-fx-background-color:#FFF;" + "-fx-border-color: #4286f4;" + "-fx-border-radius: 2px;"
				+ "-fx-border-width: 1;" + "-fx-border-style: solid;");
		box.setPadding(new Insets(5, 5, 5, 5));
		
		Label label = new Label("Vous pouvez utiliser la balise <a>lien</a> afin de partager un lien.");
		label.setWrapText(true);
		label.setMaxWidth(150);
		
		box.getChildren().add(label);
		
		if (text != null) {
			Button btn = new ButtonPerso("<a>");
			btn.setOnAction((ActionEvent e) -> {
				text.setText(text.getText() + "<a></a>");
			});
			box.setMargin(btn, new Insets(5,40,5,5));
			box.getChildren().add(btn);
		}
		
		Line line = new Line(0,0, 140,0);
		box.setMargin(line, new Insets(10,5,10,5));
		box.getChildren().add(line);
		
		label = new Label("Vous pouvez utiliser les commandes :\n"
				+ " - !enableBot pour activer le bot.\n"
				+ " - !diableBot pour le désactiver.\n"
				+ " - !question pour demander une question.");
		
		box.getChildren().add(label);
		
		if (text != null) {
			Button btn = new ButtonPerso("enableBot");
			btn.setOnAction((ActionEvent e) -> {
				text.setText(text.getText() + "!enableBot");
			});
			box.setMargin(btn, new Insets(5,40,5,5));
			box.getChildren().add(btn);
			
			btn = new ButtonPerso("disableBot");
			btn.setOnAction((ActionEvent e) -> {
				text.setText(text.getText() + "!disableBot");
			});
			box.setMargin(btn, new Insets(5,40,5,5));
			box.getChildren().add(btn);
			
			btn = new ButtonPerso("question");
			btn.setOnAction((ActionEvent e) -> {
				text.setText(text.getText() + "!question");
			});
			box.setMargin(btn, new Insets(5,40,5,5));
			box.getChildren().add(btn);
		}
		
		Line line2 = new Line(0,0, 140,0);
		box.setMargin(line2, new Insets(10,5,10,5));
		box.getChildren().add(line2);
		getContent().add(box);
	}
}
