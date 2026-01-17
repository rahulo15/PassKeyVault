package com.passkey;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import io.github.cdimascio.dotenv.Dotenv;

public class App {
    // Define your keys here for clarity
    private static final Dotenv dotenv = Dotenv.load();
    private static final String BOT_TOKEN = dotenv.get("BOT_TOKEN");
    private static final String BOT_NAME = dotenv.get("BOT_NAME");
    private static final String MASTER_STRING = dotenv.get("MASTER_STRING");

    public static void main(String[] args) {
        // 1. Setup Dependencies
        Database db = new Database();
        Security sec = new Security(MASTER_STRING);
        ClipboardManager clip = new ClipboardManager();

        // 2. Start the User Interface
        VaultCLI cli = new VaultCLI(db, sec, clip);

        try {
            initTelegramBot(sec, db);
            cli.start();
        } finally {
            // 3. Cleanup on exit
            db.close();
        }
    }

    private static void initTelegramBot(Security sec, Database db) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new PassKeyBot(BOT_TOKEN, BOT_NAME, sec, db));

            System.out.println("✅ PassKeyVault Bot is Online!");
        } catch (Exception e) {
            System.out.println("Unfortunately something went wrong, bot is offline.");
            e.printStackTrace();
        }
    }
}