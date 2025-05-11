package com.example.project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class TextEditor extends Application {
    private TextArea textArea = new TextArea(); // Поле редактирования текста
    private String currentFilePath = null; //Путь к текущему файлу
    private Connection connection; // Подключение к SQLite-базе

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        connectToDatabase(); // Подключение к базе данных
        createTables(); // Создание таблицы для хранения истории

        // Создаем главное окно
        primaryStage.setTitle("Текстовый редактор");

        // Меню
        MenuBar menuBar = createMenuBar(primaryStage);

        // Панель инструментов
        ToolBar toolBar = createToolBar(primaryStage);

        // Статусная строка
        Label statusBar = new Label("Готов");

        // Основной макет
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(textArea);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();

        // Меню Файл
        Menu fileMenu = new Menu("Файл");

        MenuItem newFileItem = new MenuItem("Новый");
        newFileItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newFileItem.setOnAction(e -> newFile());

        MenuItem openItem = new MenuItem("Открыть");
        openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openItem.setOnAction(e -> openFile(primaryStage));

        MenuItem saveItem = new MenuItem("Сохранить");
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveItem.setOnAction(e -> saveFile(primaryStage));

        MenuItem saveAsItem = new MenuItem("Сохранить как...");
        saveAsItem.setOnAction(e -> saveFileAs(primaryStage));

        MenuItem exitItem = new MenuItem("Выход");
        exitItem.setOnAction(e -> exitApp());

        fileMenu.getItems().addAll(newFileItem, openItem, saveItem, saveAsItem, new SeparatorMenuItem(), exitItem);

        // Меню Правка
        Menu editMenu = new Menu("Правка");

        MenuItem findItem = new MenuItem("Найти");
        findItem.setOnAction(e -> showFindWindow(primaryStage));

        MenuItem replaceItem = new MenuItem("Заменить");
        replaceItem.setOnAction(e -> showReplaceWindow(primaryStage));

        MenuItem historyItem = new MenuItem("История");
        historyItem.setOnAction(e -> showHistoryWindow(primaryStage));

        editMenu.getItems().addAll(findItem, replaceItem, new SeparatorMenuItem(), historyItem);

        menuBar.getMenus().addAll(fileMenu, editMenu);
        return menuBar;
    }

    private ToolBar createToolBar(Stage primaryStage) {
        ToolBar toolBar = new ToolBar();

        Button newBtn = new Button("Новый");
        newBtn.setOnAction(e -> newFile());

        Button openBtn = new Button("Открыть");
        openBtn.setOnAction(e -> openFile(primaryStage));

        Button saveBtn = new Button("Сохранить");
        saveBtn.setOnAction(e -> saveFile(primaryStage));

        toolBar.getItems().addAll(newBtn, openBtn, saveBtn);
        return toolBar;
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:text_editor.db");
        } catch (SQLException e) {
            showError("Ошибка подключения к БД", e.getMessage());
        }
    }

    private void createTables() {
        String createFilesTable = "CREATE TABLE IF NOT EXISTS files (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "filename TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createFilesTable);
        } catch (SQLException e) {
            showError("Ошибка создания таблицы", e.getMessage());
        }
    }

    private void newFile() {
        if (confirmUnsavedChanges()) {
            textArea.clear();
            currentFilePath = null;
        }
    }

    private void openFile(Stage primaryStage) {
        if (!confirmUnsavedChanges()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*"));

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            try {
                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                textArea.setText(content);
                currentFilePath = selectedFile.getAbsolutePath();
            } catch (IOException e) {
                showError("Ошибка открытия файла", e.getMessage());
            }
        }
    }

    private void saveFile(Stage primaryStage) {
        if (currentFilePath == null) {
            saveFileAs(primaryStage);
        } else {
            try {
                Files.write(Paths.get(currentFilePath), textArea.getText().getBytes());
                saveToDatabase(currentFilePath, textArea.getText());
            } catch (IOException e) {
                showError("Ошибка сохранения файла", e.getMessage());
            }
        }
    }

    private void saveFileAs(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*"));

        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            try {
                Files.write(selectedFile.toPath(), textArea.getText().getBytes());
                currentFilePath = selectedFile.getAbsolutePath();
                saveToDatabase(currentFilePath, textArea.getText());
            } catch (IOException e) {
                showError("Ошибка сохранения файла", e.getMessage());
            }
        }
    }

    private void saveToDatabase(String filename, String content) {
        String sql = "INSERT INTO files(filename, content) VALUES(?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, filename);
            ps.setString(2, content);
            ps.executeUpdate();
        } catch (SQLException e) {
            showError("Ошибка сохранения в БД", e.getMessage());
        }
    }

    private boolean confirmUnsavedChanges() {
        if (!textArea.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Несохраненные изменения");
            alert.setHeaderText("У вас есть несохраненные изменения");
            alert.setContentText("Вы хотите сохранить их перед продолжением?");

            ButtonType buttonYes = new ButtonType("Да");
            ButtonType buttonNo = new ButtonType("Нет");
            ButtonType buttonCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);

            return alert.showAndWait().orElse(buttonCancel) != buttonCancel;
        }
        return true;
    }

    private void exitApp() {
        if (confirmUnsavedChanges()) {
            System.exit(0);
        }
    }

    private void showFindWindow(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Найти");
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField searchField = new TextField();
        Button findBtn = new Button("Найти");
        findBtn.setOnAction(e -> {
            String text = textArea.getText();
            String searchText = searchField.getText();
            int index = text.indexOf(searchText);

            if (index != -1) {
                textArea.selectPositionCaret(index);
                textArea.extendSelection(index + searchText.length());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Не найдено");
                alert.setHeaderText(null);
                alert.setContentText("Текст не найден");
                alert.showAndWait();
            }
            dialog.close();
        });

        vbox.getChildren().addAll(new Label("Что найти:"), searchField, findBtn);
        Scene scene = new Scene(vbox, 300, 100);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showReplaceWindow(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Заменить");
        dialog.initOwner(primaryStage);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField searchField = new TextField();
        TextField replaceField = new TextField();
        Button replaceBtn = new Button("Заменить");

        replaceBtn.setOnAction(e -> {
            String currentText = textArea.getText();
            String searchText = searchField.getText();
            String replaceText = replaceField.getText();

            String newText = currentText.replace(searchText, replaceText);
            textArea.setText(newText);
            dialog.close();
        });

        vbox.getChildren().addAll(
                new Label("Что найти:"), searchField,
                new Label("На что заменить:"), replaceField,
                replaceBtn
        );

        Scene scene = new Scene(vbox, 300, 150);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showHistoryWindow(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.setTitle("История файлов");
        dialog.initOwner(primaryStage);

        ListView<String> historyList = new ListView<>();
        loadHistory(historyList);

        Button openBtn = new Button("Открыть");
        openBtn.setOnAction(e -> {
            String selected = historyList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String content = getHistoryContent(selected);
                if (content != null) {
                    textArea.setText(content);
                    currentFilePath = selected;
                }
                dialog.close();
            }
        });

        VBox vbox = new VBox(10, historyList, openBtn);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    private void loadHistory(ListView<String> historyList) {
        String sql = "SELECT filename FROM files GROUP BY filename ORDER BY MAX(last_modified) DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                historyList.getItems().add(rs.getString("filename"));
            }
        } catch (SQLException e) {
            showError("Ошибка загрузки истории", e.getMessage());
        }
    }

    private String getHistoryContent(String filename) {
        String sql = "SELECT content FROM files WHERE filename = ? ORDER BY last_modified DESC LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, filename);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("content");
            }
        } catch (SQLException e) {
            showError("Ошибка загрузки содержимого", e.getMessage());
        }
        return null;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}