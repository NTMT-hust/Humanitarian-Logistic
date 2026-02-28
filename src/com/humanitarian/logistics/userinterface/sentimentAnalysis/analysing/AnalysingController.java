package com.humanitarian.logistics.userinterface.sentimentAnalysis.analysing;

import java.nio.file.Path;

import com.humanitarian.logistics.model.TotalResult;
import com.humanitarian.logistics.userinterface.sentimentAnalysis.Visobert;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class AnalysingController {

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Label statusLabel;

	private final Path dataPath;
	private final Visobert sentimentModel;
	private final String dataType;

	private Task<TotalResult> analyseTask;

	public AnalysingController(Path dataPath, Visobert sentimentModel, String dataType) {
		this.dataPath = dataPath;
		this.sentimentModel = sentimentModel;
		this.dataType = dataType;
	}

	public void startAnalysis(Runnable onFinish, java.util.function.Consumer<TotalResult> callback) {

		analyseTask = new AnalysingTask(dataPath, sentimentModel);

		// Bind UI BEFORE starting
		progressBar.progressProperty().bind(analyseTask.progressProperty());
		statusLabel.textProperty().bind(analyseTask.messageProperty());

		analyseTask.setOnSucceeded(e -> {
			TotalResult result = analyseTask.getValue();
			callback.accept(result);
			onFinish.run();
		});

		analyseTask.setOnFailed(e -> {
			analyseTask.getException().printStackTrace();
			onFinish.run();
		});

		new Thread(analyseTask).start();
	}
}
