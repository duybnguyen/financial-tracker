package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to Financial Tracker");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        // creates new file if it doesn't already exist
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating file!");
            e.printStackTrace();
            return;
        }

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            // reads each line and splits it by "|" to get the individual parts of the transaction
            while((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length != 5) {
                    System.out.println("Skipping malformed transaction: " + line);
                    continue;
                }

                LocalDate date = LocalDate.parse(parts[0], DATE_FMT);
                LocalTime time = LocalTime.parse(parts[1], TIME_FMT);
                double amount = Double.parseDouble(parts[4]);

                Transaction transaction = new Transaction(date, time, parts[2], parts[3], amount);
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {
        // prompts user for all necessary fields
        System.out.print("\nEnter date and time (yyyy-MM-dd HH:mm:ss): ");
        String dateTimeInput = scanner.nextLine();

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeInput, DATETIME_FMT);

        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than 0!");
            return;
        }
        // creates a new transaction object
        Transaction transaction = new Transaction(date, time, description, vendor, amount);
        transactions.add(transaction);

        try {
            FileWriter fileWriter = new FileWriter(FILE_NAME, true);
            fileWriter.write("\n" + date + "|" + time + "|" + description + "|" + vendor + "|" + amount);
            fileWriter.close();
            System.out.println("Deposit added successfully\n");
        } catch (IOException e) {
            System.out.println("Error adding deposit. Please check your inputs.");
            e.printStackTrace();
        }

    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        // prompts user for all necessary fields
        System.out.print("\nEnter date and time (yyyy-MM-dd HH:mm:ss): ");
        String dateTimeInput = scanner.nextLine();

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeInput, DATETIME_FMT);

        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (amount <= 0) {
            System.out.println("Payment amount must be greater than 0!");
            return;
        }
        amount = -amount;
        // creates a new transaction object
        Transaction transaction = new Transaction(date, time, description, vendor, amount);
        transactions.add(transaction);

        try {
            FileWriter fileWriter = new FileWriter(FILE_NAME, true);
            fileWriter.write("\n" + date + "|" + time + "|" + description + "|" + vendor + "|" + amount);
            fileWriter.close();
            System.out.println("Payment added successfully!\n");
        } catch (IOException e) {
            System.out.println("Error adding payment. Please check your inputs.");
            e.printStackTrace();
        }
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {
        // loops through all transactions and print it
        System.out.println("\nShowing all transactions:");
        System.out.println("Date|Time|Description|Vendor|Amount");
        System.out.println("======================================================================");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        System.out.println("======================================================================\n");
    }

    private static void displayDeposits() {
        // loops through all transactions and print if > 0
        System.out.println("Showing all deposits:");
        System.out.println("Date|Time|Description|Vendor|Amount");
        System.out.println("======================================================================");
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                System.out.println(t);
            }
        }
        System.out.println("======================================================================\n");
    }

    private static void displayPayments() {
        // loops through all transactions and print if < 0
        System.out.println("\nShowing all payments:");
        System.out.println("Date|Time|Description|Vendor|Amount");
        System.out.println("======================================================================");
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) {
                System.out.println(t);
            }
        }
        System.out.println("======================================================================\n");
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            LocalDate today = LocalDate.now();
            switch (input) {
                case "1" -> filterTransactionsByDate(today.withDayOfMonth(1), today);
                case "2" -> {
                    LocalDate prevMonth = today.minusMonths(1);
                    LocalDate start = prevMonth.withDayOfMonth(1);
                    LocalDate end = prevMonth.withDayOfMonth(prevMonth.lengthOfMonth());
                    filterTransactionsByDate(start, end);
                }
                case "3" -> filterTransactionsByDate(today.withMonth(1).withDayOfMonth(31), today);
                case "4" -> {
                    LocalDate prevYear = today.minusYears(1);
                    LocalDate start = prevYear.withDayOfYear(1);
                    LocalDate end = prevYear.withDayOfYear(prevYear.lengthOfYear());
                    filterTransactionsByDate(start, end);
                }
                case "5" -> {
                    System.out.print("Enter vendor name: ");
                    String vendor = scanner.nextLine();
                    filterTransactionsByVendor(vendor);
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // loops through all transactions and print if it's the same or after the start date and before the end date
        System.out.println("\nShowing transactions from " + start + " - " + end + ": ");
        System.out.println("Date|Time|Description|Vendor|Amount");
        System.out.println("======================================================================");
        boolean found = false;
        for (Transaction t: transactions) {
            if ((t.getDate().isAfter(start) || t.getDate().isEqual(start)) &&
                    (t.getDate().isBefore(end) || t.getDate().isEqual(end))) {
                System.out.println(t);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No transaction found in this range.");
        }
        System.out.println("======================================================================\n");
    }

    private static void filterTransactionsByVendor(String vendor) {
        // loops through all transactions and print if vendor equals parameter ignoring case
        System.out.println("\nShowing all " + vendor + " transactions: ");
        System.out.println("Date|Time|Description|Vendor|Amount");
        System.out.println("======================================================================");
        for (Transaction t: transactions) {
            if (t.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println(t);
            }
        }
        System.out.println("======================================================================\n");
    }

    private static void customSearch(Scanner scanner) {

        System.out.println("\nCustom Transaction Search");
        System.out.println("======================================================================");
        System.out.print("Enter start date (yyyy-MM-dd) or leave blank: ");
        String startDateInput = scanner.nextLine();
        LocalDate startDate = null;
        if (!startDateInput.isEmpty()) {
            startDate = LocalDate.parse(startDateInput, DATE_FMT);
        }

        System.out.print("Enter end date (yyyy-MM-dd) or leave blank: ");
        String endDateInput = scanner.nextLine();
        LocalDate endDate = null;
        if (!endDateInput.isEmpty()) {
            endDate = LocalDate.parse(endDateInput, DATE_FMT);
        }

        System.out.print("Enter description to search or leave blank: ");
        String description = scanner.nextLine();

        System.out.print("Enter vendor to search or leave blank: ");
        String vendor = scanner.nextLine();

        System.out.print("Enter amount to search or leave blank: ");
        String amountInput = scanner.nextLine();
        Double amount = null;
        if (!amountInput.isEmpty()) {
            amount = Double.parseDouble(amountInput);
        }

        System.out.println("\nSearch Result(s):");
        boolean found = false;

        // loops through all transaction and skips current iteration if any requirement doesn't match
        for (Transaction t: transactions) {
            if (startDate != null && t.getDate().isBefore(startDate)) {
                continue;
            }
            if (endDate != null && t.getDate().isAfter(endDate)) {
                continue;
            }
            if (!description.isEmpty() && !t.getDescription().equalsIgnoreCase(description)) {
                continue;
            }
            if (!vendor.isEmpty() && !t.getVendor().equalsIgnoreCase(vendor)) {
                continue;
            }
            if (amount != null && t.getAmount() != amount) {
                continue;
            }

            System.out.println(t);
            found = true;
        }
        if (!found) {
            System.out.println("No transactions matched your requirements");
        }
        System.out.println("======================================================================\n");
    }
}
