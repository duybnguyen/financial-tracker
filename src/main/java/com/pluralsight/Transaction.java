package com.pluralsight;
import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    LocalDate date;
    LocalTime time;
    String description;
    String vendor;
    double amount;

    public Transaction(double amount, LocalDate date, String description, LocalTime time, String vendor) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.time = time;
        this.vendor = vendor;
    }
}
