package client.views.components;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ButtonPerso extends Button{

	public ButtonPerso () {
		super();
		setStyleButton();
	}
	
	public ButtonPerso (String text) {
		super(text);
		setStyleButton();
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
