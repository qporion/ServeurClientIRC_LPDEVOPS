package client.views;

import javafx.scene.Parent;

public interface ChangeablePanel {

	public void changeScene(final ChangeablePanel panel, int x, int y);
	public void updateTextArea(final String msg);
	
}
