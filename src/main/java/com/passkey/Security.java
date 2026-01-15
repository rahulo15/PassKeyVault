package com.passkey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.Key;

public class Security {
    private static final String ALGORITHM = "AES";
    private Key secretKey;

    // Constructor: We initialize the class with a "Master Key"
    public Security(String masterKeyString) {
        // We use the Master Key to create a secret key spec for AES
        // Note: In a production app, we would hash this key.
        // For now, ensure masterKeyString is exactly 16 characters long for 128-bit AES.
        this.secretKey = new SecretKeySpec(masterKeyString.getBytes(), ALGORITHM);
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        // Convert raw bytes to a readable String using Base64
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}