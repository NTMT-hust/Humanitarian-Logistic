package com.humanitarian.logistics.userinterface.sentimentAnalysis.startAnalysis;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.humanitarian.logistics.model.TotalResult;
import com.humanitarian.logistics.userinterface.sentimentAnalysis.Visobert;
import com.humanitarian.logistics.userinterface.sentimentAnalysis.analyseComplete.AnalyseCompleteController;
import com.humanitarian.logistics.userinterface.sentimentAnalysis.analysing.AnalysingController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;

public class StartAnalysisController {

	private final List<String> dataSourceList = List.of("youtube", "googlecse", "newsapi");

	@FXML
	private Button backBtn;

	private final Visobert sentimentModel;
	private HashMap<String, TotalResult> analHashMap;

	public StartAnalysisController(Visobert sentimentModel, HashMap<String, TotalResult> analHashMap) {
		this.sentimentModel = sentimentModel;
		this.analHashMap = analHashMap;
	}

	@FXML
	public void initialize() {
	}

	@FXML
	public void startAnalysis(ActionEvent e) throws IOException {
		((Stage) backBtn.getScene().getWindow()).close();
		runNextAnalysis(0, analHashMap);
	}

	private void runNextAnalysis(int index, HashMap<String, TotalResult> analHashMap) {

		if (index >= dataSourceList.size()) {
			showSummary(analHashMap);
			return;
		}

		String dataType = dataSourceList.get(index);
		Path dataPath = Paths.get("data", dataType + "_posts.json");

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"/com/humanitarian/logistics/userinterface/sentimentAnalysis/analysing/Analysing.fxml"));

			loader.setControllerFactory(type -> {
				if (type == AnalysingController.class) {
					return new AnalysingController(dataPath, sentimentModel, dataType);
				}
				try {
					return type.getDeclaredConstructor().newInstance();
				} catch (Exception except) {
					throw new RuntimeException(except);
				}
			});

			Parent root = loader.load();
			AnalysingController controller = loader.getController();

			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Analyzing " + dataType + "...");
			stage.centerOnScreen();
			stage.show();

			// Start async analysis
			controller.startAnalysis(
					() -> stage.close(), // close this analysing window when done
					result -> {
						analHashMap.put(dataType, result); // store result
						runNextAnalysis(index + 1, analHashMap); // start next dataset
					});

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void showSummary(HashMap<String, TotalResult> analHashMap) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"/com/humanitarian/logistics/userinterface/sentimentAnalysis/analyseComplete/test.fxml"));
			Stage currentStage = (Stage) backBtn.getScene().getWindow();
			currentStage.close();

			loader.setControllerFactory(type -> {
				if (type == AnalyseCompleteController.class) {
					return new AnalyseCompleteController();
				}
				try {
					return type.getDeclaredConstructor().newInstance();
				} catch (Exception except) {
					throw new RuntimeException(except);
				}
			});

			Parent root = loader.load();
			AnalyseCompleteController controller = loader.getController();
			controller.setData(analHashMap);

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Analyzing Result");
			stage.centerOnScreen();
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void returnBtnPressed(ActionEvent e) {
		((Stage) backBtn.getScene().getWindow()).close();
	}
}
