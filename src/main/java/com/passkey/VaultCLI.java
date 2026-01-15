package com.passkey;

import java.util.Scanner;
import java.util.List;

public class VaultCLI {
    private Database db;
    private Security sec;
    private ClipboardManager clip;
    private Scanner scanner;

    // Constructor: We pass in our tools so the UI can use them
    public VaultCLI(Database db, Security sec, ClipboardManager clip) {
        this.db = db;
        this.sec = sec;
        this.clip = clip;
        this.scanner = new Scanner(System.in); // Connects to the keyboard
    }

    public void start() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine(); // Read user input as String

            switch (choice) {
                case "1":
                    handleSave();
                    break;
                case "2":
                    handleRetrieve();
                    break;
                case "3":
                    handleDelete();
                    break;
                case "4":
                    handleList();
                    break;
                case "5":
                    System.out.println("Goodbye! Stay safe.");
                    return; // Exits the method (and the loop)
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== 🔐 PassKey Vault ===");
        System.out.println("1. Save Password");
        System.out.println("2. Retrieve Password");
        System.out.println("3. Delete Password");
        System.out.println("4. List All Sites");
        System.out.println("5. Exit");
        System.out.print("Select an option: ");
    }

    // --- The Logic for Each Option ---

    private void handleSave() {
        try {
            System.out.print("Enter Site Name (e.g. facebook.com): ");
            String site = scanner.nextLine();

            System.out.print("Enter Username: ");
            String user = scanner.nextLine();

            System.out.print("Enter Password: ");
            String rawPass = scanner.nextLine();

            // Encrypt and Save
            String encryptedPass = sec.encrypt(rawPass);
            Account newAccount = new Account(site, user, encryptedPass);
            db.saveAccount(newAccount);

        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    private void handleRetrieve() {
        String siteQuery = null;
        try {
            System.out.print("Enter Site Name to search: ");
            siteQuery = scanner.nextLine();

            Account acc = db.getAccount(siteQuery);
            if (acc != null) {
                // Decrypt
                String plainPass = sec.decrypt(acc.getPassword());

                // Copy
                clip.copy(plainPass);

                System.out.println("✅ Found: " + acc.getUsername());
                System.out.println("✅ Password copied to clipboard!");
            }
            else {
                System.out.println("❌ Account not found.");
            }

        } catch (RuntimeException e) {
            handleRetrieveUsername(siteQuery);
        } catch (Exception e) {
            System.out.println("Error retrieving: " + e.getMessage());
        }
    }

    private void handleRetrieveUsername(String siteQuery) {
        try {
            System.out.print("Enter username to search: ");
            String usernameQuery = scanner.nextLine();

            Account acc = db.getAccountByUsername(siteQuery, usernameQuery);
            if (acc != null) {
                // Decrypt
                String plainPass = sec.decrypt(acc.getPassword());

                // Copy
                clip.copy(plainPass);

                System.out.println("✅ Found: " + acc.getUsername());
                System.out.println("✅ Password copied to clipboard!");
            }
            else {
                System.out.println("❌ Account not found.");
            }

        } catch (Exception e) {
            System.out.println("Error retrieving: " + e.getMessage());
        }
    }

    private void handleDelete() {
        try {
            handleList();
            System.out.print("Enter site name: ");
            String siteQuery = scanner.nextLine();
            System.out.print("Enter username: ");
            String usernameQuery = scanner.nextLine();
            db.deleteUsername(siteQuery, usernameQuery);
        } catch (Exception e) {
            System.out.println("Error deleting: " + e.getMessage());
        }
    }

    private void handleList() {
        List<Account> accounts = db.loadAccounts("");
        if (accounts.isEmpty()) {
            System.out.println("Vault is empty.");
        } else {
            System.out.println("\n--- Stored Accounts ---");
            for (Account acc : accounts) {
                // Only showing Site and User, keeping password hidden
                System.out.println("• " + acc.getSite() + " (" + acc.getUsername() + ")");
            }
        }
    }
}