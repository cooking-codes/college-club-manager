package com.collegeclub.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utility class for hashing and verifying passwords using SHA-256.
 */
public class PasswordUtil {

    /**
     * Hashes a plain-text password using SHA-256.
     * @param plainPassword plain-text string
     * @return hex-encoded SHA-256 hash string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies if a plain password matches either the stored hash or the stored plain text.
     * (Supports both seeded plain text and SHA-256 hashes).
     */
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) return false;
        // Direct match
        if (inputPassword.equals(storedPassword)) {
            return true;
        }
        // Hash match
        String hashedInput = hashPassword(inputPassword);
        return hashedInput != null && hashedInput.equalsIgnoreCase(storedPassword);
    }
}
