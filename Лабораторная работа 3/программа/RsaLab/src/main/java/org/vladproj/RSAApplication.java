package org.vladproj;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class RSAApplication extends Application {

    private TextArea outputArea;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Криптосистема RSA");

        // UI Элементы для шифрования
        TextField pField = new TextField(); pField.setPromptText("p (простое)");
        TextField qField = new TextField(); qField.setPromptText("q (простое)");
        TextField dField = new TextField(); dField.setPromptText("Kc (закрытый ключ d)");
        Button encryptButton = new Button("Зашифровать файл");

        // UI Элементы для расшифрования
        TextField rFieldDecrypt = new TextField(); rFieldDecrypt.setPromptText("Модуль r");
        TextField dFieldDecrypt = new TextField(); dFieldDecrypt.setPromptText("Kc (закрытый ключ d)");
        Button decryptButton = new Button("Расшифровать файл");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(200);

        // Панель шифрования
        VBox encryptBox = new VBox(10, new Label("ШИФРОВАНИЕ"), pField, qField, dField, encryptButton);
        encryptBox.setPadding(new Insets(10));
        encryptBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");

        // Панель расшифрования
        VBox decryptBox = new VBox(10, new Label("РАСШИФРОВАНИЕ"), rFieldDecrypt, dFieldDecrypt, decryptButton);
        decryptBox.setPadding(new Insets(10));
        decryptBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5;");

        HBox topBox = new HBox(20, encryptBox, decryptBox);
        topBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, topBox, new Label("Вывод (в 10-й системе счисления):"), outputArea);
        root.setPadding(new Insets(20));

        // Обработчики кнопок
        encryptButton.setOnAction(e -> handleEncryption(primaryStage, pField.getText(), qField.getText(), dField.getText()));
        decryptButton.setOnAction(e -> handleDecryption(primaryStage, rFieldDecrypt.getText(), dFieldDecrypt.getText()));

        Scene scene = new Scene(root, 600, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- ОБРАБОТКА ШИФРОВАНИЯ ---
    private void handleEncryption(Stage stage, String pStr, String qStr, String dStr) {
        try {
            long p = Long.parseLong(pStr);
            long q = Long.parseLong(qStr);
            long d = Long.parseLong(dStr);

            // Проверки
            if (!isPrime(p) || !isPrime(q)) throw new Exception("p и q должны быть простыми числами.");

            long r = p * q;
            if (r <= 255 || r > 65535) {
                throw new Exception("Модуль r = p*q должен быть > 255 и <= 65535 (чтобы 1 байт < r <= 2 байта). Сейчас r = " + r);
            }

            long phi = (p - 1) * (q - 1);
            if (d <= 1 || d >= phi) throw new Exception("Kc (d) должен быть > 1 и < Ф(r) (" + phi + ").");

            // Расширенный алгоритм Евклида для проверки взаимной простоты и поиска открытого ключа e
            long[] euclidResult = extendedEuclid(phi, d);
            long gcd = euclidResult[2];
            if (gcd != 1) throw new Exception("Kc (d) и Ф(r) должны быть взаимно простыми! НОД = " + gcd);

            // Вычисляем открытый ключ e (коэффициент y алгоритма Евклида)
            long eKey = euclidResult[1];
            if (eKey < 0) eKey += phi; // Если ключ отрицательный, добавляем модуль Ф(r)

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл для шифрования");
            File inputFile = fileChooser.showOpenDialog(stage);
            if (inputFile == null) return;

            File outputFile = new File(inputFile.getParent(), "encrypted_" + inputFile.getName());

            encryptFile(inputFile, outputFile, eKey, r);

            outputArea.appendText("\n[УСПЕШНО] Файл зашифрован: " + outputFile.getName() +
                    "\nВычисленный открытый ключ (e): " + eKey +
                    "\nМодуль (r): " + r + "\n");

        } catch (NumberFormatException ex) {
            showAlert("Ошибка ввода", "Параметры p, q и Kc должны быть целыми числами.");
        } catch (Exception ex) {
            showAlert("Ошибка", ex.getMessage());
        }
    }

    // --- ОБРАБОТКА РАСШИФРОВАНИЯ ---
    private void handleDecryption(Stage stage, String rStr, String dStr) {
        try {
            long r = Long.parseLong(rStr);
            long d = Long.parseLong(dStr);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите зашифрованный файл");
            File inputFile = fileChooser.showOpenDialog(stage);
            if (inputFile == null) return;

            File outputFile = new File(inputFile.getParent(), "decrypted_" + inputFile.getName().replace("encrypted_", ""));

            decryptFile(inputFile, outputFile, d, r);
            outputArea.appendText("\n[УСПЕШНО] Файл расшифрован: " + outputFile.getName() + "\n");

        } catch (NumberFormatException ex) {
            showAlert("Ошибка ввода", "Параметры r и Kc должны быть целыми числами.");
        } catch (Exception ex) {
            showAlert("Ошибка", ex.getMessage());
        }
    }

    // --- АЛГОРИТМЫ ФАЙЛОВОГО ВВОДА/ВЫВОДА ---

    private void encryptFile(File input, File output, long e, long r) throws IOException {
        StringBuilder outputText = new StringBuilder("Зашифрованные блоки (в 10-й системе): ");
        try (FileInputStream fis = new FileInputStream(input);
             DataOutputStream dos = new DataOutputStream(new FileOutputStream(output))) {

            int m;
            int counter = 0;
            // Читаем побайтово
            while ((m = fis.read()) != -1) {
                // Шифруем
                long c = fastExp(m, e, r);
                // Пишем 16-битный блок (2 байта)
                dos.writeShort((short) c);

                // Добавляем в UI (ограничиваем вывод, чтобы не повесить интерфейс на больших файлах)
                if (counter < 200) {
                    outputText.append(c).append(" ");
                } else if (counter == 200) {
                    outputText.append("... (показана часть)");
                }
                counter++;
            }
        }
        outputArea.setText(outputText.toString());
    }

    private void decryptFile(File input, File output, long d, long r) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(input));
             FileOutputStream fos = new FileOutputStream(output)) {

            // Читаем по 2 байта, пока не достигнем конца файла
            while (dis.available() > 0) {
                // Читаем short и преобразуем в беззнаковое число
                int c = dis.readShort() & 0xFFFF;
                long m = fastExp(c, d, r);
                fos.write((int) m);
            }
        }
    }

    // --- МАТЕМАТИЧЕСКИЕ АЛГОРИТМЫ RSA ---

    // 1. Быстрое возведение в степень по модулю
    private long fastExp(long a, long z, long n) {
        long a1 = a;
        long z1 = z;
        long x = 1;
        while (z1 != 0) {
            while (z1 % 2 == 0) {
                z1 = z1 / 2;
                a1 = (a1 * a1) % n;
            }
            z1 = z1 - 1;
            x = (x * a1) % n;
        }
        return x;
    }

    // 2. Расширенный алгоритм Евклида (возвращает {x1, y1, НОД})
    private long[] extendedEuclid(long a, long b) {
        long d0 = a, d1 = b;
        long x0 = 1, x1 = 0;
        long y0 = 0, y1 = 1;

        while (d1 > 1) {
            long q = d0 / d1;
            long d2 = d0 % d1;
            long x2 = x0 - q * x1;
            long y2 = y0 - q * y1;

            d0 = d1; d1 = d2;
            x0 = x1; x1 = x2;
            y0 = y1; y1 = y2;
        }

        // Если b изначально делило a
        if (d1 == 0) return new long[]{x0, y0, d0};

        return new long[]{x1, y1, d1};
    }

    // 3. Проверка на простоту (упрощенная для наших ограничений)
    private boolean isPrime(long n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    // Вспомогательный метод для ошибок
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}