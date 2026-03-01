package org.vladproj;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vladproj.alqorithm.StolbzoviyMethod;
import org.vladproj.alqorithm.Vizhener;
import org.vladproj.validator.KeyValidator;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Введите исходный текст");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPromptText("Результат");

        TextField keyField = new TextField();
        keyField.setPromptText("Ключ");

        ComboBox<String> methodBox = new ComboBox<>();
        methodBox.getItems().addAll("Виженер (RU)", "Столбцовый (EN)");
        methodBox.getSelectionModel().selectFirst();

        Button encodeButton = new Button("Зашифровать");
        Button decodeButton = new Button("Дешифровать");
        Button readFileButton = new Button("Прочитать из файла");
        Button saveFileButton = new Button("Сохранить в файл");

        KeyValidator validator = new KeyValidator();

        // --- КНОПКА ШИФРОВАНИЯ ---
        encodeButton.setOnAction(e -> {
            try {
                String original = inputArea.getText().toUpperCase(); // текст в верхний регистр
                String key = keyField.getText().toUpperCase();       // ключ в верхний регистр
                String method = methodBox.getValue();

                if (original.isEmpty() || key.isEmpty()) {
                    showAlert("Ошибка", "Текст и ключ не могут быть пустыми");
                    return;
                }

                List<Integer> spaces = getSpacePositions(original);
                String text = removeSpaces(original);
                String result;

                if (method.startsWith("Виженер")) {
                    key = validator.validateRu(key);
                    if (key.isEmpty()) {
                        showAlert("Ошибка", "Ключ не содержит допустимых русских букв");
                        return;
                    }
                    keyField.setText(key);
                    Vizhener v = new Vizhener(key);
                    result = v.encode(text);
                } else {
                    key = validator.validateEn(key);
                    if (key.isEmpty()) {
                        showAlert("Ошибка", "Ключ не содержит допустимых английских букв");
                        return;
                    }
                    keyField.setText(key);
                    int cols = key.length();
                    int rows = (int) Math.ceil((double) text.length() / cols);
                    Character[][] table = new Character[rows][cols];
                    StolbzoviyMethod s = new StolbzoviyMethod(table, key);
                    result = s.encode(text);
                }

                result = restoreSpaces(result, spaces);
                outputArea.setText(result);

            } catch (Exception ex) {
                showAlert("Ошибка", ex.getMessage());
            }
        });

        // --- КНОПКА ДЕШИФРОВАНИЯ ---
        decodeButton.setOnAction(e -> {
            try {
                String original = inputArea.getText().toUpperCase();
                String key = keyField.getText().toUpperCase();
                String method = methodBox.getValue();

                if (original.isEmpty() || key.isEmpty()) {
                    showAlert("Ошибка", "Текст и ключ не могут быть пустыми");
                    return;
                }

                List<Integer> spaces = getSpacePositions(original);
                String text = removeSpaces(original);
                String result;

                if (method.startsWith("Виженер")) {
                    key = validator.validateRu(key);
                    if (key.isEmpty()) {
                        showAlert("Ошибка", "Ключ не содержит допустимых русских букв");
                        return;
                    }
                    keyField.setText(key);
                    Vizhener v = new Vizhener(key);
                    result = v.decode(text);
                } else {
                    key = validator.validateEn(key);
                    if (key.isEmpty()) {
                        showAlert("Ошибка", "Ключ не содержит допустимых английских букв");
                        return;
                    }
                    keyField.setText(key);
                    int cols = key.length();
                    int rows = (int) Math.ceil((double) text.length() / cols);
                    Character[][] table = new Character[rows][cols];
                    StolbzoviyMethod s = new StolbzoviyMethod(table, key);
                    result = s.decode(text);  // важно именно decode!
                }

                result = restoreSpaces(result, spaces);
                outputArea.setText(result);

            } catch (Exception ex) {
                showAlert("Ошибка", ex.getMessage());
            }
        });

        readFileButton.setOnAction(e -> {
            try {
                javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
                chooser.setTitle("Выберите файл для чтения");
                chooser.getExtensionFilters().add(
                        new javafx.stage.FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
                );

                java.io.File file = chooser.showOpenDialog(primaryStage);
                if (file == null) return;

                String content = java.nio.file.Files.readString(file.toPath());
                inputArea.setText(content);

            } catch (Exception ex) {
                showAlert("Ошибка чтения файла", ex.getMessage());
            }
        });

        saveFileButton.setOnAction(e -> {
            try {
                String result = outputArea.getText();

                if (result.isEmpty()) {
                    showAlert("Ошибка", "Нет данных для сохранения");
                    return;
                }

                javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
                chooser.setTitle("Сохранить результат");
                chooser.getExtensionFilters().add(
                        new javafx.stage.FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
                );

                java.io.File file = chooser.showSaveDialog(primaryStage);
                if (file == null) return;

                java.nio.file.Files.writeString(file.toPath(), result);

            } catch (Exception ex) {
                showAlert("Ошибка сохранения файла", ex.getMessage());
            }
        });


        HBox topBox = new HBox(10,
                new Label("Ключ:"), keyField,
                new Label("Метод:"), methodBox,
                encodeButton, decodeButton, readFileButton, saveFileButton
        );
        topBox.setPadding(new Insets(10));

        VBox root = new VBox(10,
                topBox,
                new Label("Исходный текст:"),
                inputArea,
                new Label("Результат:"),
                outputArea
        );
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setTitle("Шифры: Виженер и Столбцовый");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<Integer> getSpacePositions(String text) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                positions.add(i);
            }
        }
        return positions;
    }

    private String removeSpaces(String text) {
        return text.replace(" ", "");
    }

    private String restoreSpaces(String text, java.util.List<Integer> positions) {
        StringBuilder sb = new StringBuilder(text);
        for (int pos : positions) {
            if (pos <= sb.length()) {
                sb.insert(pos, ' ');
            }
        }
        return sb.toString();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}