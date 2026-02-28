package com.humanitarian.logistics.userinterface.sentimentAnalysis.analyseComplete;

import java.io.IOException;
import java.util.HashMap;
import com.humanitarian.logistics.model.TotalResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AnalyseCompleteController {

	// ========== FXML BINDINGS FOR YOUTUBE TAB ==========
	@FXML
	private PieChart ytChart;
	@FXML
	private TextArea ytText;

	// ========== FXML BINDINGS FOR GOOGLE CSE TAB ==========
	@FXML
	private PieChart cseChart;
	@FXML
	private TextArea cseText;

	// ========== FXML BINDINGS FOR NEWSAPI TAB ==========
	@FXML
	private PieChart newsChart;
	@FXML
	private TextArea newsText;

	@FXML
	private Button backBtn;

	private TotalResult ytResult;
	private TotalResult cseResult;
	private TotalResult nsApiResult;

	// Required empty constructor (JavaFX needs this)
	public AnalyseCompleteController() {
	}

	// Called by loader AFTER loading controller
	public void setData(HashMap<String, TotalResult> allResults) {
		this.ytResult = allResults.get("youtube");
		this.cseResult = allResults.get("googlecse");
		this.nsApiResult = allResults.get("newsapi");
		initialize();
	}

	public void initialize() {
		setupChartAndText(ytChart, ytText, ytResult);
		setupChartAndText(cseChart, cseText, cseResult);
		setupChartAndText(newsChart, newsText, nsApiResult);
	}

	// ===================== CHART SETUP =========================

	private void setupChartAndText(PieChart chart, TextArea textArea, TotalResult result) {

		if (result == null)
			return;

		textArea.setText(result.getString());

		// 1. Get raw values
		int positive = result.getTotalSentiment().get(0);
		int negative = result.getTotalSentiment().get(1);
		int neutral = result.getTotalSentiment().get(2);

		// 2. Calculate Total for percentage math
		double total = positive + negative + neutral;

		// 3. Create Label Strings with Percentages
		// Format: "Name (XX.X%)"
		String posLabel = "Positive";
		String negLabel = "Negative";
		String neuLabel = "Neutral";

		if (total > 0) {
			posLabel = String.format("Positive (%.1f%%)", (positive / total) * 100);
			negLabel = String.format("Negative (%.1f%%)", (negative / total) * 100);
			neuLabel = String.format("Neutral (%.1f%%)", (neutral / total) * 100);
		}

		// 4. Create Data with new Labels
		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
				new PieChart.Data(posLabel, positive),
				new PieChart.Data(negLabel, negative),
				new PieChart.Data(neuLabel, neutral));

		chart.setData(pieData);
		chart.setLegendVisible(true);
		chart.setLabelsVisible(true);

		// 5. Apply Colors
		colorSlice(pieData.get(0), "#4caf50"); // positive (Green)
		colorSlice(pieData.get(1), "#ff4d4d"); // negative (Red)
		colorSlice(pieData.get(2), "#ffbf00"); // neutral (Yellow/Orange)
	}

	private void colorSlice(PieChart.Data slice, String color) {
		// We use a listener because the Node might not be created immediately
		slice.nodeProperty().addListener((obs, oldNode, newNode) -> {
			if (newNode != null) {
				newNode.setStyle("-fx-pie-color: " + color + ";");
			}
		});

		// Also try setting it immediately in case the node already exists
		if (slice.getNode() != null) {
			slice.getNode().setStyle("-fx-pie-color: " + color + ";");
		}
	}

	// ===================== RETURN BUTTON =========================

	@FXML
	public void returnBtnPressed(ActionEvent e) throws IOException {

		Stage current = (Stage) backBtn.getScene().getWindow();
		current.close();

		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"/com/humanitarian/logistics/userinterface/sentimentAnalysis/modelInitialize/ModelInitialize.fxml"));
		Parent root = loader.load();

		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Initializing Sentiment Model...");
		stage.show();
	}
}