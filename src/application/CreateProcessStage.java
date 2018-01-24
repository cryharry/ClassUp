package application;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CreateProcessStage {
	ProgressBar progress = new ProgressBar();
	Label message = new Label();
	Stage progressStage = new Stage();
	StackPane pane = new StackPane();
	VBox box = new VBox();
	public void showStage() {
		pane.setPrefSize(200, 50);
		progress.setProgress(0);
		progressStage.initStyle(StageStyle.UTILITY);
		box.getChildren().add(message);
		box.getChildren().add(progress);
		pane.getChildren().add(box);
		progressStage.setScene(new Scene(pane));
		progressStage.show();
	}
	public void hideStage() {
		progressStage.hide();
	}
	public ProgressBar bindProperty(Task<Void> task) {
		progress.progressProperty().unbind();
		progress.progressProperty().bind(task.progressProperty());
		message.textProperty().unbind();
		message.textProperty().bind(task.messageProperty());
		return this.progress;
	}
}
