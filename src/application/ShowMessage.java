package application;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class ShowMessage extends Alert {
	{
		setHeaderText("");
		getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	}
	public ShowMessage(AlertType alType) {
		super(alType);
	}
	public ShowMessage(AlertType alType, String title, String content) {
		super(alType);
		setTitle(title);
		setContentText(content);
		showAndWait();
	}
}
