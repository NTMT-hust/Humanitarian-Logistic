package com.humanitarian.logistics.userinterface.textExtraction.extracting;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class ExtractingController {

	@FXML
	private Label statusLabel;
	@FXML
	private ProgressBar progressBar;

	private final TextExtractTask extractTask;

	public ExtractingController(Path dataPath, List<String> keywords) {
		this.extractTask = new TextExtractTask(dataPath, keywords);
	}

	/**
	 * Synchronous extraction for StartExtractionController
	 * (returns results immediately)
	 */
	public Map<String, Integer> extractProcedureReturn() {
		try {
			return extractTask.call(); // run synchronously
		} catch (Exception e) {
			System.err.println("Extraction failed: " + e.getMessage());
			e.printStackTrace();
			return Map.of(); // avoid crashing UI
		}
	}

	@FXML
	public void initialize() {
		if (progressBar == null)
			return; // If extract window is not used

		progressBar.progressProperty().bind(extractTask.progressProperty());
		statusLabel.textProperty().bind(extractTask.messageProperty());

		extractTask.setOnSucceeded(_ -> closeWindow());
		extractTask.setOnFailed(_ -> closeWindow());
	}

	private void closeWindow() {
		Stage stage = (Stage) statusLabel.getScene().getWindow();
		stage.close();
	}
}
