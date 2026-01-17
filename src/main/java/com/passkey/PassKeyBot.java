package com.passkey;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class PassKeyBot extends TelegramLongPollingBot {

    // 🔒 SECURITY: Replace with your actual ID you got earlier
    private static final Dotenv dotenv = Dotenv.load();
    private static final long OWNER_ID = Long.parseLong(dotenv.get("OWNER_ID"));
    private static final String DB_URL = dotenv.get("DB_URL");
    private final String botUsername;
    private final Security sec;
    private final Database db;

    public PassKeyBot(String botToken, String botUsername, Security sec, Database db) {
        super(botToken);
        this.botUsername = botUsername;
        this.sec = sec;
        this.db = db;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return super.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        int userMsgId = update.getMessage().getMessageId(); // 🆔 Capture User's Message ID

        // 1. SECURITY: Block strangers
        if (OWNER_ID != 0L && chatId != OWNER_ID) {
            sendMsg(chatId, "⛔ Access Denied.");
            return;
        }

        // 2. Command Routing
        if (text.startsWith("/")) {
            if (text.startsWith("/save ")) {
                savePassword(chatId, text);
            } else if (text.startsWith("/get ")) {
                getPassword(chatId, text, userMsgId);
            } else if (text.startsWith("/delete ")) {
                deleteAccount(chatId, text);
            } else if (text.startsWith("/update ")) {
                updatePassword(chatId, text);
            } else if (text.equals("/list")) {
                listAccounts(chatId);
            }
            deleteAfterDelay(chatId, userMsgId);
        } else {
            Message botReply = sendMsg(chatId, "Commands:\n/save <account> <username> <pass>\n/get <account> <username>\n/delete <account> <username>\n/update <account> <username> <pass>\n/list");
            deleteAfterDelay(chatId, botReply.getMessageId());
        }
    }

    // --- FEATURE: SAVE ---
    private void savePassword(long chatId, String text) {
        String[] parts = text.split(" ", 4);
        if (parts.length < 4) {
            sendMsg(chatId, "❌ Usage: /save <Account> <UserName> <Password>");
            return;
        }

        String site = parts[1];
        String username = parts[2];
        String rawPass = parts[3];

        try {
            String encrypted = sec.encrypt(rawPass);
            Account newAccount = new Account(site, username, encrypted);
            Result res = db.saveAccount(newAccount);

            // Reply confirming save
            Message confirmMsg = sendMsg(chatId, res.getMessage());

            // Optional: Delete the confirmation after 5 seconds too, if you want zero trace
            deleteAfterDelay(chatId, confirmMsg.getMessageId());

        } catch (Exception e) {
            sendMsg(chatId, "❌ Error: " + e.getMessage());
        }
    }

    // --- FEATURE: GET (The Nuclear Option) ---
    private void getPassword(long chatId, String text, int userMsgId) {
        String[] parts = text.split(" ");
        if (parts.length < 3) {
            sendMsg(chatId, "❌ Usage: /get <Account> <UserName>");
            return;
        }
        String account = parts[1];
        String username = parts[2];

        try {
            Result<Account> acc = db.getAccountByUsername(account, username);

            if (acc.getData() != null) {
                String plainPass = sec.decrypt(acc.getData().getPassword());

                // ||text|| creates the Spoiler effect
                String responseText = "🔑 " + account + ":\n||" + plainPass + "||\n\n(🔥 Self-destructing in 15s)";

                // 1. Send the password
                Message botReply = sendMsg(chatId, responseText);

                // 2. Schedule DELETION of the bot's reply (The Password)
                deleteAfterDelay(chatId, botReply.getMessageId());

                // 3. Schedule DELETION of the user's request ("/get netflix")
                deleteAfterDelay(chatId, userMsgId);

            } else {
                sendMsg(chatId, "❌ Account not found.");
            }
        } catch (Exception e) {
            sendMsg(chatId, "❌ Error: " + e.getMessage());
        }
    }

    private void deleteAccount(long chatId, String text) {
        String[] parts = text.split(" ");
        if (parts.length < 3) {
            sendMsg(chatId, "❌ Usage: /delete <Account> <UserName>");
            return;
        }
        String account = parts[1];
        String username = parts[2];

        try {
            Result res = db.deleteUsername(account, username);

            if (res.isSuccess()) {
                Message botReply = sendMsg(chatId, "Account with site: " + account + " and username: " + username + " deleted successfully");
            } else {
                sendMsg(chatId, "❌ Account not found.");
            }
        } catch (Exception e) {
            sendMsg(chatId, "❌ Error: " + e.getMessage());
        }
    }

    private void updatePassword(long chatId, String text) {
        String[] parts = text.split(" ");
        if (parts.length < 4) {
            sendMsg(chatId, "❌ Usage: /update <Account> <UserName> <Pass>");
            return;
        }
        String account = parts[1];
        String username = parts[2];
        String rawpass = parts[3];
        try {
            String encryptPass = sec.encrypt(rawpass);
            Result res = db.updateUsername(account, username, encryptPass);

            if (res.isSuccess()) {
                Message botReply = sendMsg(chatId, "Password updated for site: " + account + " & username: " + username);
                deleteAfterDelay(chatId, botReply.getMessageId());
            } else {
                sendMsg(chatId, "❌ Account not found.");
            }
        } catch (Exception e) {
            sendMsg(chatId, "❌ Error: " + e.getMessage());
        }
    }

    // --- HELPER: LIST ---
    private void listAccounts(long chatId) {
        StringBuilder sb = new StringBuilder("📂 **Vault Accounts:**\n");
        try {
            Result<List<Account>> res = db.loadAccounts("");
            if (res.isSuccess()) {
                for (Account acc : res.getData()) {
                    sb.append("• ").append(acc.getSite()).append(" - ").append(acc.getUsername()).append("\n");
                }
            }
            Message botReply = sendMsg(chatId, sb.toString());
            deleteAfterDelay(chatId, botReply.getMessageId());
        } catch (Exception e) {
            sendMsg(chatId, "❌ Error: " + e.getMessage());
        }
    }

    // --- HELPER: AUTO-DESTRUCT ---
    private void deleteAfterDelay(long chatId, int messageId) {
        new Thread(() -> {
            try {
                //hardcoded 15 secs for everything
                Thread.sleep(15 * 1000L); // Wait X seconds

                DeleteMessage delete = new DeleteMessage();
                delete.setChatId(chatId);
                delete.setMessageId(messageId);
                execute(delete); // 💥 Boom, it's gone.

            } catch (Exception e) {
                // Fails silently if message is already deleted
                System.out.println("Delete after delay failed, please check immediately.");
            }
        }).start();
    }


    // Note: Changed return type to 'Message' so we can track what we sent
    private Message sendMsg(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setParseMode("Markdown");
        try {
            return execute(msg); // Returns the sent message object
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}