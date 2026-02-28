package com.humanitarian.logistics.userinterface.textExtraction.extractComplete;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ExtractCompleteController implements Initializable {

    @FXML
    private BarChart<String, Number> youtubeChart;
    @FXML
    private BarChart<String, Number> googleCseChart;
    @FXML
    private BarChart<String, Number> newsApiChart;

    private final Map<String, Map<String, Integer>> allResults;
    @FXML
    private Button backBtn;

    // Constructor used in StartExtractionController
    public ExtractCompleteController(Map<String, Map<String, Integer>> allResults) {
        this.allResults = allResults;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Wait for FXML injection, then load charts
        loadAllCharts();
    }

    private void loadAllCharts() {
        loadSingleChart(youtubeChart, allResults.get("youTube"));
        loadSingleChart(googleCseChart, allResults.get("googlecse"));
        loadSingleChart(newsApiChart, allResults.get("newsapi"));
    }

    private void loadSingleChart(BarChart<String, Number> chart, Map<String, Integer> data) {
        if (chart == null || data == null || data.isEmpty())
            return;

        chart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Keyword Frequency");

        data.forEach((keyword, count) -> {
            series.getData().add(new XYChart.Data<>(keyword, count));
        });

        chart.getData().add(series);
    }

    @FXML
    private void returnToMenu() throws IOException {
        // Load previous window
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/humanitarian/logistics/userinterface/textExtraction/startExtraction/StartExtraction.fxml"));

        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setMaximized(false);
        stage.setTitle("Main Menu");
        stage.show();

        // Close current window
        Stage current = (Stage) backBtn.getScene().getWindow();
        current.close();
    }

}
