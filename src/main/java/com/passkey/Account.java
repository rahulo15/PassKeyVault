package com.passkey;

import java.util.UUID;

public class Account {
    private String id;
    private String site;
    private String username;
    private String password; // This will hold the ENCRYPTED password

    // Constructor
    public Account(String site, String username, String password) {
        this.id = UUID.randomUUID().toString();
        this.site = site;
        this.username = username;
        this.password = password;
    }

    // Constructor for loading an EXISTING account from DB (Preserves original UUID)
    public Account(String id, String site, String username, String password) {
        this.id = id;
        this.site = site;
        this.username = username;
        this.password = password;
    }

    // Getters (We use these to read the values safely)
    public String getId() {
        return id;
    }

    public String getSite() {
        return site;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Good practice: Override toString() so we can print the object easily
    @Override
    public String toString() {
        return "ID: " + id.substring(0, 8) + " | Site: " + site + " | User: " + username;
    }
}