# 🔐 PassKey Vault

A simple, offline password manager built with Java. It saves your passwords securely in a local database file, so you don't need to trust the cloud.

## Features
* **Secure:** Passwords are encrypted before being saved.
* **Private:** Data is stored locally on your computer in `passkey.db`.
* **Easy Copy:** Automatically copies your password to the clipboard when you retrieve it.

## How to Run
1.  Open the project in your IDE (IntelliJ, Eclipse, or VS Code).
2.  Locate `src/main/java/com/passkey/App.java`.
3.  Click the **Run** button (green play icon).
4.  Follow the menu options in the console.

## Usage
* **Save:** Enter the website, username, and password.
* **Retrieve:** Search for a website (e.g., "google"). The password will be copied to your clipboard.
* **List:** View all saved accounts.

## Requirements
* Java 17 or higher
* Maven
