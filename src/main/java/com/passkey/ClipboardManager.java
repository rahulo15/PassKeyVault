package com.passkey;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ClipboardManager {

    public void copy(String text) {
        // 1. Get the system clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 2. Wrap the text in a transferable object
        StringSelection selection = new StringSelection(text);

        // 3. Set the clipboard contents
        clipboard.setContents(selection, null);

        System.out.println("✅ Password copied to clipboard!");
    }
}