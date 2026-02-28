package com.humanitarian.logistics.userinterface.collectData.collectorSelectMenu;

import java.io.IOException;
import java.util.List;

import com.humanitarian.logistics.userinterface.collectData.intializeCollector.InitializeController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class CollectorSelectionController {

	List<String> collectorList = List.of("Youtube", "GoogleCSE", "NewsAPI");
	private String selection;

	@FXML
	private MenuButton menuSelection;

	@FXML
	private Button advanceSearchBtn;

	private Stage stage;
	private Scene scene;
	private Parent root;

	@FXML
	public void initialize() {

		menuSelection.getItems().clear();

		for (String option : collectorList) {
			MenuItem item = new MenuItem(option);

			item.setOnAction(_ -> {
				menuSelection.setText(option);
				this.selection = option;
			});

			menuSelection.getItems().add(item);
		}
	}

	@FXML
	public void advanceSearch(ActionEvent e) throws IOException {

		if (selection == null) {
			menuSelection.setText("Select collector first!");
			return;
		}

		String function = "advanceSearch";

		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/com/humanitarian/logistics/userinterface/collectData/intializeCollector/InitializeCollector.fxml"));

		root = loader.load();
		stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

		InitializeController init = loader.getController();
		init.setStage(stage);
		init.initializeCollector(this.selection, function, (Node) e.getSource()); // FIXED

		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Initializing Collector...");
		stage.centerOnScreen();
		stage.show();
	}

	@FXML
	public void cancel(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/com/humanitarian/logistics/userinterface/collectData/cancelling/CancellingInterface.fxml"));
		root = loader.load();
		stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);

		stage.centerOnScreen();
		stage.show();
	}

}
