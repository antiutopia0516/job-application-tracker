package com.jobtracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static ArrayList<JobApplication> loadApplicationsFromFile(String filename) {
        ArrayList<JobApplication> applications = new ArrayList<>();
        File file = new File(filename);
    
        if (!file.exists()) {
            System.out.println("📝 No existing application file found. Starting fresh.");
            return applications;
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JobApplication app = JobApplication.fromCSV(line);
                    applications.add(app);
                } catch (Exception e) {
                    System.out.println("⚠️ Skipping invalid line: " + line);
                }
            }
            System.out.println("✅ Loaded " + applications.size() + " application(s) from file.");
        } catch (Exception e) {
            System.out.println("❌ Error reading from file: " + e.getMessage());
        }
    
        return applications;
    }

    public static void saveApplicationsToFile(ArrayList<JobApplication> applications, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (JobApplication app : applications) {
                writer.write(app.toCSV());
                writer.newLine();
            }
            System.out.println("\n✅ Applications saved to file: " + filename);
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

         // 从文件加载历史记录
        ArrayList<JobApplication> applications = loadApplicationsFromFile("applications.txt");

        System.out.println("Welcome to Job Application Tracker!");
        System.out.print("How many job applications do you want to enter? ");
        int count = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < count; i++) {
            JobApplication app;
            while (true) {
                System.out.println("\nApplication #" + (i + 1));

                System.out.print("Company: ");
                String company = scanner.nextLine().trim();

                System.out.print("Position: ");
                String position = scanner.nextLine().trim();

                System.out.print("Date Applied (yyyy-mm-dd): ");
                String dateInput = scanner.nextLine().trim();

                System.out.print("Status (e.g., Applied, Interviewing, Offer, Rejected): ");
                String status = scanner.nextLine().trim();

                try {
                    LocalDate dateApplied = LocalDate.parse(dateInput);
                    app = new JobApplication(company, position, dateApplied, status);

                    System.out.println("\nYou entered:");
                    System.out.println(app);
                    System.out.print("Is this correct? (Y/N): ");
                    String confirm = scanner.nextLine().trim().toUpperCase();

                    if (confirm.equals("Y")) {
                        break; // 信息确认，跳出 while 循环
                    } else {
                        System.out.println("Let's re-enter the information.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please re-enter the application.");
                }
            }

            applications.add(app);
        }

        System.out.println("\nMy Job Applications:");
        for (JobApplication app : applications) {
            System.out.println(app);
        }

        // 显示所有申请记录
        System.out.println("\nMy Job Applications:");
        for (int i = 0; i < applications.size(); i++) {
            System.out.println("[" + i + "] " + applications.get(i));
        }

        // 询问是否要修改状态
        System.out.print("\nDo you want to update the status of an application? (Y/N): ");
        String updateChoice = scanner.nextLine().trim().toUpperCase();

        while (updateChoice.equals("Y")) {
            System.out.print("Enter the index of the application to update: ");
            int index;
            try {
                index = Integer.parseInt(scanner.nextLine());
                if (index < 0 || index >= applications.size()) {
                    System.out.println("❌ Invalid index.");
                } else {
                    System.out.print("Enter new status (e.g., Applied, Interviewing, Offer, Rejected): ");
                    String newStatus = scanner.nextLine().trim();
                    applications.get(index).setStatus(newStatus);
                    System.out.println("✅ Status updated.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }

            // 是否继续修改？
            System.out.print("\nDo you want to update another one? (Y/N): ");
            updateChoice = scanner.nextLine().trim().toUpperCase();
        }

        System.out.print("\nDo you want to delete an application? (Y/N): ");
        String deleteChoice = scanner.nextLine().trim().toUpperCase();

        while (deleteChoice.equals("Y")) {
            System.out.print("Enter the index of the application to delete: ");
            int deleteIndex;
            try {
                deleteIndex = Integer.parseInt(scanner.nextLine());
                if (deleteIndex < 0 || deleteIndex >= applications.size()) {
                    System.out.println("❌ Invalid index.");
                } else {
                    System.out.println("You are about to delete:");
                    System.out.println("[" + deleteIndex + "] " + applications.get(deleteIndex));
                    System.out.print("Are you sure? (Y/N): ");
                    String confirmDelete = scanner.nextLine().trim().toUpperCase();

                    if (confirmDelete.equals("Y")) {
                        applications.remove(deleteIndex);
                        System.out.println("✅ Application deleted.");
                    } else {
                        System.out.println("❌ Deletion cancelled.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }

            System.out.print("\nDo you want to delete another one? (Y/N): ");
            deleteChoice = scanner.nextLine().trim().toUpperCase();
        }

        saveApplicationsToFile(applications, "applications.txt");

        scanner.close();

    }
}

