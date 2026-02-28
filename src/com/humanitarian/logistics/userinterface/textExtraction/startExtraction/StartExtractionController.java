package com.humanitarian.logistics.userinterface.textExtraction.startExtraction;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.humanitarian.logistics.userinterface.textExtraction.extractComplete.ExtractCompleteController;
import com.humanitarian.logistics.userinterface.textExtraction.extracting.ExtractingController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StartExtractionController {

	@FXML
	private Button backBtn;

	@FXML
	private Button button;

	@FXML
	private TextField textField;

	// Supported data sources
	private final List<String> dataSourceList = List.of("youTube", "googlecse", "newsapi");

	@FXML
	public void startExtract(ActionEvent e) throws IOException {

		// Close current window
		Stage currentStage = (Stage) button.getScene().getWindow();
		currentStage.close();

		// Prepare keywords
		String[] keywords = textField.getText().split(",");
		for (int i = 0; i < keywords.length; i++)
			keywords[i] = keywords[i].strip();

		// Map of: DataSource → (Keyword → Count)
		Map<String, Map<String, Integer>> allResults = new HashMap<>();

		// Run extraction for each data source
		for (String dataType : dataSourceList) {

			Path dataPath = Paths.get("data", dataType + "_posts.json");

			ExtractingController extractor = new ExtractingController(dataPath, List.of(keywords));

			Map<String, Integer> result = extractor.extractProcedureReturn();
			allResults.put(dataType, result);
		}

		// Show summary window
		openSummaryWindow(allResults);
	}

	private void openSummaryWindow(Map<String, Map<String, Integer>> allResults) throws IOException {

		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(
						"/com/humanitarian/logistics/userinterface/textExtraction/extractComplete/ExtractComplete.fxml"));

		loader.setControllerFactory(type -> {
			if (type == ExtractCompleteController.class)
				return new ExtractCompleteController(allResults);
			try {
				return type.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		Parent root = loader.load();
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Extraction Summary");
		stage.centerOnScreen();
		stage.show();
	}

	@FXML
	public void returnBtnPressed(ActionEvent e) throws IOException {
		((Stage) backBtn.getScene().getWindow()).close();
	}
}
