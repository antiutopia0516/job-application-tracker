package com.jobtracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final ArrayList<JobApplication> dataList = new ArrayList<>();
    private final TableView<JobApplication> tableView = new TableView<>();
    private final String FILE_NAME = "applications.csv";

    @Override
    public void start(Stage primaryStage) {
        // Input fields
        TextField companyField = new TextField();
        TextField positionField = new TextField();
        ComboBox<String> statusBox = new ComboBox<>();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        statusBox.getItems().addAll("Applied", "Interviewing", "Offer", "Rejected");
        statusBox.setValue("Applied");

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete Selected");
        Button saveButton = new Button("Save to File");
        Label statusLabel = new Label();

        // Table columns
        TableColumn<JobApplication, String> companyCol = new TableColumn<>("Company");
        companyCol.setCellValueFactory(new PropertyValueFactory<>("company"));

        TableColumn<JobApplication, String> positionCol = new TableColumn<>("Position");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<JobApplication, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<JobApplication, LocalDate> dateCol = new TableColumn<>("Date Applied");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateApplied"));

        tableView.getColumns().addAll(companyCol, positionCol, statusCol, dateCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add application
        addButton.setOnAction(e -> {
            String company = companyField.getText();
            String position = positionField.getText();
            String status = statusBox.getValue();
            LocalDate selectedDate = datePicker.getValue();

            if (company.isEmpty() || position.isEmpty()) {
                statusLabel.setText("❌ Company and Position required");
                return;
            }

            JobApplication app = new JobApplication(company, position, selectedDate, status);
            dataList.add(app);
            tableView.getItems().add(app);
            statusLabel.setText("✅ Added: " + company + " - " + position);
            companyField.clear();
            positionField.clear();
            statusBox.setValue("Applied");
            datePicker.setValue(LocalDate.now());
        });

        // Delete selected
        deleteButton.setOnAction(e -> {
            JobApplication selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                tableView.getItems().remove(selected);
                dataList.remove(selected);
                statusLabel.setText("🗑️ Deleted: " + selected.getCompany());
            } else {
                statusLabel.setText("⚠️ No selection to delete");
            }
        });

        // Save to file
        saveButton.setOnAction(e -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (JobApplication app : tableView.getItems()) {
                    writer.write(app.toCSV());
                    writer.newLine();
                }
                statusLabel.setText("💾 Saved to " + FILE_NAME);
            } catch (IOException ex) {
                statusLabel.setText("❌ Error saving file: " + ex.getMessage());
            }
        });

        // Load existing data
        loadFromFile();

        // Layout
        VBox inputArea = new VBox(8, new Label("Company:"), companyField,
        new Label("Position:"), positionField,
        new Label("Status:"), statusBox,
        new Label("Date Applied:"), datePicker, addButton);

        HBox buttons = new HBox(10, deleteButton, saveButton);
        VBox root = new VBox(15, inputArea, new Label("Applications:"), tableView, buttons, statusLabel);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 650, 520);
        primaryStage.setTitle("Job Application Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JobApplication app = JobApplication.fromCSV(line);
                    dataList.add(app);
                    tableView.getItems().add(app);
                } catch (Exception e) {
                    System.out.println("⚠️ Invalid line skipped: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to load file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
