package com.humanitarian.logistics.userinterface.sentimentAnalysis.modelInitialize;

import java.io.IOException;
import java.util.HashMap;

import com.humanitarian.logistics.userinterface.sentimentAnalysis.Visobert;
import com.humanitarian.logistics.userinterface.sentimentAnalysis.startAnalysis.StartAnalysisController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class VisobertInitializeController {

	@FXML
	private Label statusLabel;
	@FXML
	private ProgressBar progressBar;

	private VisobertInitializeTask initializeTask = new VisobertInitializeTask();
	private Visobert sentimentModel;

	@FXML
	public void initialize() {

		progressBar.progressProperty().bind(initializeTask.progressProperty());
		statusLabel.textProperty().bind(initializeTask.messageProperty());

		initializeTask.setOnSucceeded(_ -> {
			try {
				sentimentModel = initializeTask.getValue();

				Stage currentStage = (Stage) statusLabel.getScene().getWindow();
				currentStage.close();

				FXMLLoader loader = new FXMLLoader(getClass().getResource(
						"/com/humanitarian/logistics/userinterface/sentimentAnalysis/startAnalysis/StartAnalysis.fxml"));

				loader.setControllerFactory(type -> {
					if (type == StartAnalysisController.class) {
						return new StartAnalysisController(this.sentimentModel, new HashMap<>());
					}
					try {
						return type.getDeclaredConstructor().newInstance();
					} catch (Exception except) {
						throw new RuntimeException(except);
					}
				});

				Parent root = loader.load();
				Stage stage = new Stage();

				Scene scene = new Scene(root);
				// String css =
				// this.getClass().getResource("/com/humanitarian/logistics/userInterface/inputBox/InputInterface.css").toExternalForm();
				// scene.getStylesheets().add(css);
				stage.setScene(scene);
				stage.setTitle("Humanitarian Logistics Sentiment Analysis Model");
				stage.centerOnScreen();
				stage.show();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		initializeTask.setOnFailed(_ -> {
			try {
				Stage currentStage = (Stage) statusLabel.getScene().getWindow();
				currentStage.close();

				FXMLLoader loader = new FXMLLoader(
						getClass().getResource("/com/humanitarian/logistics/userinterface/Error.fxml"));
				Parent root = loader.load();
				Stage stage = new Stage();

				Scene scene = new Scene(root);
				// String css =
				// this.getClass().getResource("/resources/InputInterface.css").toExternalForm();
				// scene.getStylesheets().add(css);
				stage.setScene(scene);
				stage.setTitle("Error");
				stage.centerOnScreen();
				stage.show();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		Thread t = new Thread(initializeTask);
		t.setDaemon(true);
		t.start();

	}

}
