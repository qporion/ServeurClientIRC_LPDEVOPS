package client.views.components;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class HelpButton extends Button {

	public HelpButton () {
		super();
		setStyleButton();
	}
	
	public HelpButton (String text) {
		super(text);
		setStyleButton();
	}
	
	private void setStyleButton() {
		setStyle("-fx-background-color:#4286f4;" + "-fx-border-color: #000;" + "-fx-border-radius: 2px;"
				+ "-fx-border-width: 1;" + "-fx-border-style: solid;");
		setTextFill(Color.WHITE);
		
		setOnMouseEntered((MouseEvent e) -> {
			getStyleClass().removeAll();
			setStyle("-fx-background-color:#FFF;" + "-fx-border-color: #4286f4;"
					+ "-fx-border-radius: 2px;" + "-fx-border-width: 1;" + "-fx-border-style: solid;");
			setTextFill(Color.BLACK);
		});

		setOnMouseExited((MouseEvent e) -> {
			getStyleClass().removeAll();
			setStyle("-fx-background-color:#4286f4;" + "-fx-border-color: #000;"
					+ "-fx-border-radius: 2px;" + "-fx-border-width: 1;" + "-fx-border-style: solid;");
			setTextFill(Color.WHITE);
		});
	}
}
