package com.passkey;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Database {
    // This is the file path. "jdbc:sqlite:" tells Java which driver to use.
    private static final String URL = "jdbc:sqlite:passkey.db";
    private Connection connection;

    // Constructor: Opens the connection when you do 'new Database()'
    public Database() {
        try {
            // 1. Establish the connection
            this.connection = DriverManager.getConnection(URL);
            System.out.println("Connected to the database.");

            // 2. Initialize the table if it doesn't exist
            createTable();

        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    // Method to create the table structure
    private void createTable() {
        // SQL command to create a table named 'vault'
        String sql = "CREATE TABLE IF NOT EXISTS vault (" +
                "id TEXT PRIMARY KEY NOT NULL," +
                "site TEXT NOT NULL," +
                "username TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "UNIQUE(site, username));";

        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    // We need a way to close the connection when the app stops
    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
                System.out.println("Database closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getter to allow other parts of the app to use this connection
    public Connection getConnection() {
        return this.connection;
    }

    public void saveAccount(Account account) {
        String sql = "INSERT INTO vault(id, site, username, password) VALUES(?, ?, ?, ?)";

        // try-with-resources: automatically closes the statement when done
        try (java.sql.PreparedStatement stmt = this.connection.prepareStatement(sql)) {

            // We fill in the blanks (?) safely
            stmt.setString(1, account.getId());
            stmt.setString(2, account.getSite());
            stmt.setString(3, account.getUsername());
            stmt.setString(4, account.getPassword());

            stmt.executeUpdate(); // Execute the save
            System.out.println("Saved account for: " + account.getSite());

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Error: An account for " + account.getSite() +
                        " with username " + account.getUsername() + " already exists.");
            } else {
                System.out.println("Database Error: " + e.getMessage());
            }
        }
    }

    public void deleteUsername(String siteQuery, String username) {
        String sql = "DELETE FROM vault WHERE site = ? AND username = ?";

        // try-with-resources: automatically closes the statement when done
        try (java.sql.PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, siteQuery);
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate(); // Execute the save

            if (rowsAffected > 0) {
                System.out.println("Account deleted successfully.");
            } else {
                System.out.println("❌ No account found to delete.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }

    public List<Account> loadAccounts(String siteQuery) {
        List<Account> accounts = new ArrayList<>();
        String sql = "Select * from vault";

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                String id = rs.getString("id");
                String site = rs.getString("site");
                String username = rs.getString("username");
                String encryptedPass = rs.getString("password");

                Account acc = new Account(id, site, username, encryptedPass);

                if(site.equals(siteQuery)) System.out.println(acc.getUsername());
                else accounts.add(acc);
            }

        }  catch (SQLException e) {
            System.out.println("Error in loading Accounts: " + e.getMessage());
        }

        return accounts;
    }

    public Account getAccountByUsername(String siteQuery, String usernameQuery) {
        String sql = "SELECT * FROM vault WHERE site = ? AND username = ?";
        Account account = null;

        try (java.sql.PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, siteQuery);
            stmt.setString(2, usernameQuery);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                account = new Account(
                        rs.getString("id"),
                        rs.getString("site"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account by username: " + e.getMessage());
        }
        return account;
    }

    public Account getAccount(String siteQuery) {
        String checksql = "SELECT COUNT(*) FROM vault WHERE site = ?";
        try (java.sql.PreparedStatement stmt = this.connection.prepareStatement(checksql)) {
            stmt.setString(1, siteQuery);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) != 1) {
                System.out.println("Redirecting to fetchByUserName(too many acc for this site)...");
                System.out.println("Choose from below accounts...");
                loadAccounts(siteQuery);
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            System.out.println("Error counting number of accounts: " + e.getMessage());
        }
        String sql = "SELECT * FROM vault WHERE site = ?";
        Account account = null;

        try (java.sql.PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, siteQuery);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                account = new Account(
                        rs.getString("id"),
                        rs.getString("site"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account: " + e.getMessage());
        }
        return account;
    }
}