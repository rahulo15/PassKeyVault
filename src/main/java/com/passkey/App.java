package com.passkey;

public class App {
    public static void main(String[] args) {
        // 1. Setup Dependencies
        Database db = new Database();
        Security sec = new Security("1234567890123456");
        ClipboardManager clip = new ClipboardManager();

        // 2. Start the User Interface
        VaultCLI cli = new VaultCLI(db, sec, clip);

        try {
            cli.start();
        } finally {
            // 3. Cleanup on exit
            db.close();
        }
    }
}