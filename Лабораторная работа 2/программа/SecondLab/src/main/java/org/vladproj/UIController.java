package org.vladproj;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UIController {

    @FXML
    private TextArea originalArea;

    @FXML
    private TextArea keyArea;

    @FXML
    private TextArea encryptedArea;

    @FXML
    private TextField seedField;

    @FXML
    private Label fileLabel;

    @FXML
    private Button encryptButton;

    private File selectedFile;

    @FXML
    public void initialize() {

        seedField.textProperty().addListener((obs, oldVal, newVal) -> {

            String filtered = newVal.replaceAll("[^01]", "");

            if (filtered.length() > 32)
                filtered = filtered.substring(0, 32);

            seedField.setText(filtered);

            encryptButton.setDisable(filtered.length() != 32);
        });
    }

    @FXML
    public void openFile() {

        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите файл");

        selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null)
            fileLabel.setText(selectedFile.getAbsolutePath());
    }

    @FXML
    public void encrypt() {

        if (selectedFile == null) {
            showError("Сначала выберите файл");
            return;
        }

        try {

            int seed = parseSeed();

            FileChooser fc = new FileChooser();
            fc.setTitle("Сохранить файл");

            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("All files", "*.*")
            );

            File output = fc.showSaveDialog(null);

            if (output == null)
                return;

            StringBuilder keyStream = new StringBuilder();

            byte[] original = CipherService.process(selectedFile, output, seed, keyStream);

            originalArea.setText(bytesToBinary(original));
            keyArea.setText(keyStream.toString());

            byte[] encrypted = Files.readAllBytes(output.toPath());

            encryptedArea.setText(bytesToBinary(encrypted));

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private int parseSeed() {

        String bits = seedField.getText();
        return (int) Long.parseLong(bits, 2);
    }

    private String bytesToBinary(byte[] data) {

        StringBuilder sb = new StringBuilder();

        for (byte b : data) {

            for (int i = 7; i >= 0; i--)
                sb.append((b >> i) & 1);

            sb.append(" ");
        }

        return sb.toString();
    }

    private void showError(String msg) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}