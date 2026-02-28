package com.humanitarian.logistics.userinterface.textwindow;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TextWindowController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextArea textArea;

    public void setContent(String title, String content) {
        titleLabel.setText(title);
        textArea.setText(content);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
