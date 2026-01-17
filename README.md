# 🔐 PassKey Vault

A secure, offline password manager built with Java. It saves your passwords locally in an encrypted database and can now be controlled remotely via a private Telegram Bot.

## Features
* **Secure Storage:** Passwords are encrypted (AES) and stored locally in `passkey.db`.
* **Remote Access:** Manage your vault from your phone using a Telegram Bot, so you don't need to be at your computer.
* **Strict Security:** The bot includes a "Bouncer" feature that blocks any user ID except yours.
* **Auto-Destruct:** For privacy, all command messages and bot responses automatically delete themselves after **15 seconds** to keep your chat history clean.

## How to Run
1.  **Configure:** Add your `BOT_TOKEN` and `OWNER_ID` to the application settings.
2.  **Build:** Open the project in your IDE (IntelliJ, Eclipse, or VS Code).
3.  **Run:** Locate `src/main/java/com/passkey/App.java` and click **Run**.
4.  **Connect:** Open your Telegram bot and start sending commands.

## Usage (Telegram Commands)
You can manage your vault using these text commands.
*Note: All inputs and outputs self-destruct after 15 seconds.*

* `/save <account> <username> <pass>`
    * *Example: `/save facebook.com myemail@gmail.com secret123`*
* `/get <account> <username>`
    * *Example: `/get facebook.com myemail@gmail.com`*
* `/update <account> <username> <pass>`
    * *Updates the password for an existing account.*
* `/delete <account> <username>`
    * *Permanently removes the account.*
* `/list`
    * *Shows all stored sites.*

## Requirements
* Java 17 or higher
* Maven
* A Telegram Bot Token (from @BotFather)
