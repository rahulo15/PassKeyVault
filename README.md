# 🔐 PassKey Vault

A simple, offline password manager built with Java. It saves your passwords securely in a local database file, so you don't need to trust the cloud.

## Features
* **Secure:** Passwords are encrypted (AES) before being saved.
* **Private:** Data is stored locally on your computer in `passkey.db`.
* **Full Control:** You can Save, Retrieve, Update, and Delete your passwords.
* **Easy Copy:** Automatically copies your password to the clipboard when you retrieve it.

## How to Run
1.  Open the project in your IDE (IntelliJ, Eclipse, or VS Code).
2.  Locate `src/main/java/com/passkey/App.java`.
3.  Click the **Run** button (green play icon).
4.  Follow the menu options in the console.

## Usage
The app now supports full account management:

* **1. Save:** Add a new website, username, and password.
* **2. Retrieve:** Search for a website. The password is decrypted and copied to your clipboard.
* **3. List:** View all your saved accounts (passwords remain hidden).
* **4. Update:** Change the password for an existing site.
* **5. Delete:** Permanently remove an account from the vault.

## Requirements
* Java 17 or higher
* Maven
