package com.example.atmsystem;

import java.io.*;
import java.util.*;

// --------- Main (UI Layer) ---------
public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            ATMService service = new ATMService(scanner);

            while (true) {
                System.out.println("\n--- ATM System ---");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Show Accounts");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter account number: ");
                        String number = scanner.nextLine();
                        System.out.print("Enter holder name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter initial balance: ");
                        double balance = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        System.out.print("Set 4-digit PIN: ");
                        String pin = scanner.nextLine();
                        service.createAccount(number, name, balance, pin);
                        break;
                    case 2:
                        System.out.print("Enter account number: ");
                        number = scanner.nextLine();
                        System.out.print("Enter deposit amount: ");
                        double dep = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Enter PIN: ");
                        String depositPin = scanner.nextLine();
                        service.deposit(number, dep, depositPin);
                        break;
                    case 3:
                        System.out.print("Enter account number: ");
                        number = scanner.nextLine();
                        System.out.print("Enter withdrawal amount: ");
                        double with = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Enter PIN: ");
                        String withdrawPin = scanner.nextLine();
                        if (!service.withdraw(number, with, withdrawPin)) {
                            System.out.println("Insufficient balance or authentication failed.");
                        }
                        break;
                    case 4:
                        service.displayAccounts();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

// --------- Model ---------
class Account {
    private String accountNumber;
    private String holderName;
    private double balance;
    private String pin;

    public Account(String accountNumber, String holderName, double balance, String pin) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.pin = pin;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }

    public void deposit(double amount) { balance += amount; }
    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public String toFileString() {
        return accountNumber + "," + holderName + "," + balance + "," + pin;
    }

    public String getPin() { return pin; }

    public static Account fromFileString(String line) {
        String[] parts = line.split(",");
        return new Account(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
    }
}

// --------- DAO ---------
class AccountDAO {
    private static final String FILE_NAME = "accounts.txt";

    public AccountDAO() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) file.createNewFile();
    }

    public List<Account> getAllAccounts() throws IOException {
        List<Account> accounts = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
        String line;
        while ((line = reader.readLine()) != null) {
            accounts.add(Account.fromFileString(line));
        }
        reader.close();
        return accounts;
    }

    public void saveAccounts(List<Account> accounts) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
        for (Account acc : accounts) {
            writer.write(acc.toFileString());
            writer.newLine();
        }
        writer.close();
    }
}

// --------- Service ---------
class ATMService {
    private Scanner scanner;
    private AccountDAO dao;
    private List<Account> accounts;

    public ATMService(Scanner scanner) throws IOException {
        this.scanner = scanner;
        dao = new AccountDAO();
        accounts = dao.getAllAccounts();
    }

    public void createAccount(String number, String name, double initialBalance, String pin) throws IOException {
        if (number == null || name == null || initialBalance < 0)
            throw new IllegalArgumentException("Invalid input");
        if (!pin.matches("\\d{4}"))
            throw new IllegalArgumentException("PIN must be 4 digits");

        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(number))
                throw new IllegalArgumentException("Account already exists");
        }

        accounts.add(new Account(number, name, initialBalance, pin));
        dao.saveAccounts(accounts);
    }

    public Account findAccount(String number) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(number)) return acc;
        }
        return null;
    }

    public void deposit(String number, double amount, String pin) throws IOException {
        Account acc = findAccount(number);
        if (acc == null) {
            System.out.println("Account not found");
            return;
        }
        if (!acc.getPin().equals(pin)) {
            System.out.println("Authentication failed.");
            return;
        }
        acc.deposit(amount);
        dao.saveAccounts(accounts);
    }

    public boolean withdraw(String number, double amount, String pin) throws IOException {
        Account acc = findAccount(number);
        if (acc == null) {
            System.out.println("Account not found");
            return false;
        }
        if (!acc.getPin().equals(pin)) {
            System.out.println("Authentication failed.");
            return false;
        }
        boolean success = acc.withdraw(amount);
        dao.saveAccounts(accounts);
        return success;
    }

    public void displayAccounts() {
        for (Account acc : accounts) {
            System.out.printf("%s | %s | %.2f\n", acc.getAccountNumber(), acc.getHolderName(), acc.getBalance());
        }
    }
}
