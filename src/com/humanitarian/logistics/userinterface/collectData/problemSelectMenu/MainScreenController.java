package com.humanitarian.logistics.userinterface.collectData.problemSelectMenu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanitarian.logistics.userinterface.textwindow.TextWindowController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainScreenController {

	@FXML
	private MenuItem button1, button2, buttonCollectData;

	@FXML
	private Button deleteButton;

	@FXML
	private Button previewBtn;

	@FXML
	private AnchorPane youtubePane;

	@FXML
	private AnchorPane newsApiPane;

	@FXML
	private AnchorPane googleCsePane;

	@FXML
	private TabPane mainTabPane;

	@FXML
	private AnchorPane filePane;

	@FXML
	public void initialize() {
		loadJsonIntoPane("data/youtube_posts.json", youtubePane);
		loadJsonIntoPane("data/googlecse_posts.json", googleCsePane);
		loadJsonIntoPane("data/newsapi_posts.json", newsApiPane);
	}

	private Stage getStageFromEvent(ActionEvent e) {
		Object src = e.getSource();

		if (src instanceof MenuItem item) {
			return (Stage) item.getParentPopup().getOwnerWindow();
		} else if (src instanceof Node node) {
			return (Stage) node.getScene().getWindow();
		}
		return null;
	}

	@FXML
	public void collectData(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/com/humanitarian/logistics/userinterface/collectData/collectorSelectMenu/CollectorSelectionMenu.fxml"));
		Parent root = loader.load();

		Stage popup = new Stage();
		popup.setTitle("Select Collector");
		popup.setScene(new Scene(root));
		popup.setMaximized(false);
		popup.initModality(Modality.WINDOW_MODAL);
		popup.initOwner(getStageFromEvent(e)); // <-- show on top of the current window
		popup.show();
	}

	@FXML
	public void sentimentAnalysis(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/com/humanitarian/logistics/userinterface/sentimentAnalysis/modelInitialize/ModelInitialize.fxml"));
		Parent root = loader.load();

		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Initializing Sentiment Model...");
		stage.show();
	}

	@FXML
	public void textExtraction(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/com/humanitarian/logistics/userinterface/textExtraction/startExtraction/StartExtraction.fxml"));
		Parent root = loader.load();

		Stage stage = new Stage();
		stage.setMaximized(false);
		stage.setScene(new Scene(root));

		stage.setTitle("Initializing Text Extraction...");
		stage.show();
	}

	@FXML
	private void onDeleteClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select JSON File to Delete");

		// Optional: limit to JSON files
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json"));

		// Set initial directory if needed
		File initialDir = new File("data");
		if (initialDir.exists()) {
			fileChooser.setInitialDirectory(initialDir);
		}

		Stage stage = (Stage) filePane.getScene().getWindow(); // rootPane is any node in your scene
		File selectedFile = fileChooser.showOpenDialog(stage);

		if (selectedFile == null) {
			return; // User cancelled
		}

		// Confirm deletion
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete File");
		alert.setHeaderText("Delete: " + selectedFile.getName());
		alert.setContentText("This action cannot be undone.");

		alert.showAndWait().ifPresent(result -> {
			if (result == ButtonType.OK) {
				if (selectedFile.delete()) {
					Alert success = new Alert(Alert.AlertType.INFORMATION);
					success.setHeaderText(null);
					success.setContentText("File deleted successfully!");
					success.show();
				} else {
					Alert failure = new Alert(Alert.AlertType.ERROR);
					failure.setHeaderText("Failed to delete file");
					failure.setContentText("Could not delete: " + selectedFile.getName());
					failure.show();
				}
			}
		});
	}

	@FXML
	private void chooseFileAndOpenPreviewTab() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose JSON File");
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("JSON Files", "*.json"));

		// Load default data folder from system properties
		String dataDirPath = System.getProperty("app.data.dir", "./data");
		File dataDir = new File(dataDirPath);
		if (dataDir.exists() && dataDir.isDirectory()) {
			fileChooser.setInitialDirectory(dataDir);
		}

		// Show file chooser
		Window window = previewBtn.getScene().getWindow();
		File file = fileChooser.showOpenDialog(window);

		if (file == null)
			return; // user canceled

		// Read JSON content
		String jsonContent;
		try {
			jsonContent = Files.readString(file.toPath());
		} catch (IOException e) {
			jsonContent = "Error loading file: " + e.getMessage();
		}

		// Create new tab
		Tab previewTab = new Tab();

		// Header: filename + close button
		Label title = new Label(file.getName());
		Button closeBtn = new Button("x");
		closeBtn.setStyle(
				"-fx-background-color: transparent;" +
						"-fx-text-fill: black;" +
						"-fx-font-weight: bold;" +
						"-fx-padding: 0 5 0 5;");
		closeBtn.setOnAction(e -> mainTabPane.getTabs().remove(previewTab));

		HBox header = new HBox(title, closeBtn);
		header.setSpacing(5);
		previewTab.setGraphic(header);

		// Tab content
		AnchorPane content = new AnchorPane();

		TextArea textArea = new TextArea(jsonContent);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		AnchorPane.setTopAnchor(textArea, 0.0);
		AnchorPane.setBottomAnchor(textArea, 0.0);
		AnchorPane.setLeftAnchor(textArea, 0.0);
		AnchorPane.setRightAnchor(textArea, 0.0);

		content.getChildren().add(textArea);
		previewTab.setContent(content);

		// Add and switch to tab
		mainTabPane.getTabs().add(previewTab);
		mainTabPane.getSelectionModel().select(previewTab);
	}

	// -----------------------------------------
	// JSON loader — displays dynamic content in anchor panes
	// -----------------------------------------

	private void loadJsonIntoPane(String fileName, AnchorPane targetPane) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			String dataDir = System.getProperty("app.data.dir", "");
			Path jsonPath = Paths.get(dataDir, fileName);

			if (!Files.exists(jsonPath)) {
				System.err.println("❌ JSON file not found: " + jsonPath.toAbsolutePath());
				return;
			}

			InputStream is = Files.newInputStream(jsonPath);
			JsonNode root = mapper.readTree(is);

			if (!root.isArray() || root.size() == 0) {
				System.err.println("❌ JSON array empty!");
				return;
			}

			// -----------------------------
			// PICK 1 RANDOM POST
			// -----------------------------
			JsonNode item = root.get((int) (Math.random() * root.size()));
			JsonNode metadata = item.path("metadata");

			// -------- SAFE READS (no crashes) --------
			String title = metadata.path("title").asText(
					metadata.path("video_title").asText(
							metadata.path("headline").asText("No title")));

			String snippet = metadata.path("snippet").asText(
					metadata.path("video description").asText(
							metadata.path("description").asText("No description")));

			String authorText = item.path("author").asText("");

			String imgUrl = metadata.path("image_url").asText(
					metadata.path("thumbnail").asText(""));

			// -----------------------------
			// BUILD UI CARD
			// -----------------------------
			VBox card = new VBox(8);
			card.setPadding(new Insets(15));
			card.setStyle("""
					-fx-background-color: white;
					-fx-padding: 12;
					-fx-border-color: #DDD;
					-fx-border-radius: 6;
					-fx-background-radius: 6;
					""");

			Label titleLabel = new Label(title);
			titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

			Label snippetLabel = new Label(snippet);
			snippetLabel.setWrapText(true);

			Label author = new Label("Source: " + authorText);
			author.setStyle("-fx-text-fill: #444;");

			HBox imageRow = new HBox(10);

			if (!imgUrl.isEmpty()) {
				try {
					ImageView imgView = new ImageView(new Image(imgUrl, true));
					imgView.setFitWidth(150);
					imgView.setPreserveRatio(true);
					imageRow.getChildren().add(imgView);
				} catch (Exception ignore) {
				}
			}

			imageRow.getChildren().add(snippetLabel);

			card.getChildren().addAll(titleLabel, imageRow, author);

			// -----------------------------
			// DISPLAY IN ANCHOR PANE
			// -----------------------------
			AnchorPane.setTopAnchor(card, 0.0);
			AnchorPane.setLeftAnchor(card, 0.0);
			AnchorPane.setRightAnchor(card, 0.0);
			AnchorPane.setBottomAnchor(card, 0.0);

			targetPane.getChildren().setAll(card);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void showAppInfor() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/com/humanitarian/logistics/userinterface/textwindow/TextWindow.fxml"));

			Parent root = loader.load();
			TextWindowController controller = loader.getController();

			controller.setContent(
					"Application Information",
					"App Name: ReliefInsight\n" + //
							"Project Title: Social Media Data Application for Enhancing Humanitarian Logistics Efficiency.\n"
							+ //
							"\n" + //
							"1. Overview\n" + //
							"ReliefInsight is a Java Desktop application designed to assist humanitarian organizations in managing natural disaster responses (e.g., Typhoon Yagi). By collecting and analyzing real-time data from social media platforms (X, Facebook, TikTok), the app converts unstructured public feedback into actionable insights for optimizing relief resource allocation.\n"
							+ //
							"\n" + //
							"2. Core Functions (The 4 Problems)\n" + //
							"The application processes data to solve four key analytical problems:\n" + //
							"\n" + //
							"Sentiment Tracking: Monitors public sentiment trends (positive/negative) over time to gauge emotional shifts during the crisis.\n"
							+ //
							"\n" + //
							"Damage Classification: Categorizes posts into specific damage types (e.g., human casualties, housing damage, infrastructure failure) to identify critical impact areas.\n"
							+ //
							"\n" + //
							"Satisfaction Analysis: Measures public satisfaction vs. dissatisfaction regarding general relief efforts.\n"
							+ //
							"\n" + //
							"Item-Specific Analytics: Tracks sentiment regarding specific relief items (cash, food, medicine, shelter) to identify supply gaps or distribution successes.\n"
							+ //
							"\n" + //
							"3. Technical Architecture & OOP Design\n" + //
							"The system is built with a focus on flexibility and extensibility using Object-Oriented Programming (OOP) principles:\n"
							+ //
							"\n" + //
							"Modular Data Collection: Uses Factory/Interface patterns to allow easy addition of new data sources (e.g., adding Zalo without changing core code).\n"
							+ //
							"\n" + //
							"Dynamic Configuration: Keywords, damage categories, and relief items are configurable (external JSON/XML), allowing updates without recompilation.\n"
							+ //
							"\n" + //
							"Hybrid AI Model:\n" + //
							"\n" + //
							"The core logic is Java-based.\n" + //
							"\n" + //
							"The Analytical Model (NLP) can be written in Python and integrated via a RESTful API or strictly defined JSON Interface.\n"
							+ //
							"\n" + //
							"Uses the Adapter Pattern to switch seamlessly between a Python model and a native Java model (e.g., DL4J).\n"
							+ //
							"\n" + //
							"Pipeline Architecture: Features a customizable preprocessing pipeline (tokenization, spam filtering) designed with the Decorator Pattern.\n"
							+ //
							"\n" + //
							"4. Goal\n" + //
							"To provide a highly adaptable tool that helps logistics coordinators prioritize aid distribution based on real-time public feedback and damage reports.");

			Stage stage = new Stage();
			stage.setTitle("About App");
			stage.setScene(new Scene(root));
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}