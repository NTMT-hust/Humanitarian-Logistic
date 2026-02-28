package com.humanitarian.logistics.userinterface.collectData.intializeCollector;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.humanitarian.logistics.collector.*;
import com.humanitarian.logistics.config.*;
import com.humanitarian.logistics.dataStructure.InputData;
import com.humanitarian.logistics.model.SearchCriteria;
import com.humanitarian.logistics.model.SocialPost;
import com.humanitarian.logistics.userinterface.collectData.inputBox.InputBoxController;
import com.humanitarian.logistics.userinterface.collectData.searching.SearchingController;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InitializeController {

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Label statusLabel;

	@FXML
	private VBox rootPane;

	private AppConfig appConfig;
	private Collector<SearchCriteria, ?, List<SocialPost>> collector;
	private String collectorType;
	private String function;
	private Stage stage;

	private Node triggerEventSource; // FIX: store event source for later

	/**
	 * Entry point from previous screen
	 */
	public void initializeCollector(String collectorName, String function, Node eventSource) throws IOException {
		this.collectorType = collectorName;
		this.function = function;
		this.triggerEventSource = eventSource; // FIXED

		switch (collectorName) {
			case "Youtube":
				appConfig = new AppConfig("youtube");
				collector = new YouTubeCollector(new YouTubeConfig(appConfig));
				break;
			case "GoogleCSE":
				appConfig = new AppConfig("google.cse");
				collector = new GoogleCseCollector(new GoogleCseConfig(appConfig));
				break;
			case "NewsAPI":
				appConfig = new AppConfig("newsapi");
				collector = new NewsCollector(new NewsApiConfig(appConfig));
				break;
		}

		statusLabel.setText("Initializing " + collectorName + " Collector...");
		simulateInitialize();
	}

	/**
	 * Simulate progress bar animation and then execute appropriate action
	 */
	public void simulateInitialize() {

		boolean ok = collector.testConnection(); // FIX: test once

		KeyValue endValue = ok
				? new KeyValue(progressBar.progressProperty(), 1.0)
				: new KeyValue(progressBar.progressProperty(), 0.8);

		KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), endValue);
		Timeline timeline = new Timeline(keyFrame);

		timeline.setOnFinished(ae -> {

			// Close initializing popup
			Stage currentStage = (Stage) rootPane.getScene().getWindow();
			currentStage.close();

			if (ok) {
				if (function.equals("advanceSearch")) {
					openAdvanceSearchWindow();
				} else if (function.equals("Search")) {
					runDefaultSearch();
				}
			} else {
				showErrorWindow();
			}
		});

		timeline.play();
	}

	/**
	 * Open the advanced search input window
	 */
	private void openAdvanceSearchWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"/com/humanitarian/logistics/userinterface/collectData/inputBox/InputInterface.fxml"));

			loader.setControllerFactory(type -> {
				if (type == InputBoxController.class) {
					return new InputBoxController(collector, collectorType, function);
				}
				try {
					return type.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});

			Parent root = loader.load();
			Stage stage = new Stage();
			Scene scene = new Scene(root);

			stage.setScene(scene);
			stage.setTitle("Enter Search Criteria");
			stage.centerOnScreen();
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Perform a quick default search
	 */
	private void runDefaultSearch() {

		try {
			// Example default search
			InputData userInput = new InputData(
					"Bão",
					new String[0],
					LocalDateTime.now().minusDays(30),
					LocalDateTime.now(),
					50);

			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"/com/humanitarian/logistics/userinterface/collectData/searching/SearchingInterface.fxml"));

			loader.setControllerFactory(type -> {
				if (type == SearchingController.class) {
					return new SearchingController(collectorType, "");
				}
				try {
					return type.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});

			Parent root = loader.load();
			SearchingController controller = loader.getController();

			stage.setScene(new Scene(root));
			stage.setTitle("Searching Data...");
			stage.centerOnScreen();
			stage.show();

			controller.searchProcedure(userInput);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show error dialog
	 */
	private void showErrorWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/com/humanitarian/logistics/userinterface/Error.fxml"));

			Parent root = loader.load();
			Stage stage = new Stage();

			stage.setScene(new Scene(root));
			stage.setTitle("Connection Failed");
			stage.centerOnScreen();
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
